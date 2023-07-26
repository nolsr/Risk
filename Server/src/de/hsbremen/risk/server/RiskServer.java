package de.hsbremen.risk.server;

import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.entities.*;
import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.missions.*;
import de.hsbremen.risk.common.events.GameActionEvent;
import de.hsbremen.risk.common.events.GameControlEvent;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;
import de.hsbremen.risk.common.exceptions.*;
import de.hsbremen.risk.server.persistence.FilePersistenceManager;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.Serial;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static de.hsbremen.risk.common.events.GameLobbyEvent.GameLobbyEventType.PLAYER_ENTERED;
import static de.hsbremen.risk.common.events.GameLobbyEvent.GameLobbyEventType.PLAYER_LEFT;

public class RiskServer extends UnicastRemoteObject implements ServerRemote {

    @Serial
    private static final long serialVersionUID = 5270082596267157282L;
    private final PlayerManager playerManager;
    private final WorldManager worldManager;
    private final CardManager cardManager;

    private final FilePersistenceManager filePersistenceManager;
    // GameManager
    private Turn currentTurn;
    private Attack attack;
    private final List<GameEventListener> listeners;

    public RiskServer() throws RemoteException {
        filePersistenceManager = new FilePersistenceManager();
        playerManager = new PlayerManager();
        worldManager = new WorldManager();
        cardManager = new CardManager();
        listeners = new Vector<>();
        // allPlayers = new Vector<>();
    }


    /**
     * Retrieves the current turn information.
     *
     * @return The Turn object of the current game Turn.
     */
    public Turn getCurrentTurn() {
        return this.currentTurn;
    }

    /**
     * Calculates and sets the next phase or turn based on the current phase and player.
     *
     * @throws UnplacedArmiesException When the player tries to exit the reinforcement phase without distributing all of his forces.
     * @throws GameEndedException      When a player tries to enter the next phase but the game has ended.
     * @throws RemoteException         When having trouble communicating with the client.
     */
    public void nextTurn() throws UnplacedArmiesException, GameEndedException, RemoteException {
        if (isMissionCompleted(this.currentTurn.getPlayer())) {
            this.currentTurn.setGameEnded();
            this.notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.GAME_OVER, getCountries()));
        }
        if (playerHasPeaceCard(this.currentTurn.getPlayer())) {
            this.currentTurn.setGameEnded();
            this.notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.GAME_OVER, getCountries(), true));
        }
        if (this.currentTurn.getPhase() == Turn.Phase.GAME_ENDED) {
            throw new GameEndedException();
        }
        if (this.currentTurn.getPhase() == Turn.Phase.REINFORCEMENT_PHASE &&
                this.currentTurn.getPlayer().getArmies() > 0) {
            throw new UnplacedArmiesException();
        }
        if (this.currentTurn.getPhase() == Turn.Phase.DRAWING_PHASE) {
            this.currentTurn = new Turn(getNextPlayer());
            getReinforcementUnits(this.currentTurn.getPlayer());
            worldManager.resetUnitsMoved();
            notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.NEXT_PHASE, getCountries()));
            return;
        }
        this.currentTurn.nextPhase();

        notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.NEXT_PHASE, getCountries()));
    }

    /**
     * Starts the game.
     *
     * @throws IllegalPlayerCountException When not enough or too many players are in the lobby.
     * @throws RemoteException             When having trouble communicating with the client.
     */
    public void startGame() throws RemoteException, IllegalPlayerCountException {
        if (!this.isLegalPlayerCount()) {
            throw (new IllegalPlayerCountException());
        }
        playerManager.shufflePlayerList();
        worldManager.assignCountriesToPlayers(getPlayerList());
        cardManager.insertPeaceCard(getPlayerList().size());
        assignMissions(true, null);

        Player firstTurnPlayer = getFirstTurnPlayer();
        this.currentTurn = new Turn(firstTurnPlayer);
        System.out.println("Start game turn: " + this.currentTurn);
        for (Continent continent : worldManager.getContinentList())
            for (Player player : getPlayerList()) {
                checkIfPlayerOwnsContinentAndSet(continent.getName(), player.getUsername());
            }
        getReinforcementUnits(firstTurnPlayer);
        this.notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.GAME_STARTED, getCountries()));
    }

    /**
     * Loads and starts a saved game state.
     *
     * @param file Filename of the save that shall be loaded.
     * @throws IOException                  When there is a problem reading the file.
     * @throws LoadGameWrongPlayerException When there are not the same players in the lobby as in the saved game.
     */
    public void loadGame(String file) throws IOException, LoadGameWrongPlayerException {
        try {
            if (!checkLoadGamePossible(file)) {
                throw new LoadGameWrongPlayerException(file);
            }
            JSONObject json = loadFile(file);
            filePersistenceManager.retrieveContinentData(json, 0, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(json, 1, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(json, 2, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(json, 3, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(json, 4, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(json, 5, worldManager.getContinentList());
            playerManager.setPlayerList(filePersistenceManager.retrievePlayerData(json));
            Player loadedPlayerTurn = playerManager.getPlayer(filePersistenceManager.retrieveTurnPlayer(json));
            this.currentTurn = new Turn(loadedPlayerTurn);
            this.currentTurn.setPhase(filePersistenceManager.retrieveTurnPhase(json));
            filePersistenceManager.retrieveCardManagerInfo(json, cardManager);
            cardManager.updateCardManager(filePersistenceManager.retrieveCardsData(json));
            assignMissions(false, file);
            this.notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.GAME_STARTED, getCountries()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a save file can be loaded or not.
     *
     * @param file Filename of the save that shall be loaded.
     * @return A Boolean value whether the game load is possible or not.
     * @throws IOException When there is a problem reading the file.
     */
    public boolean checkLoadGamePossible(String file) throws IOException {
        ArrayList<Player> temp = filePersistenceManager.retrievePlayerData(loadFile(file));
        int counter = 0;
        if (getPlayerList().size() == temp.size()) {
            for (Player playerListPlayer : getPlayerList()) {
                for (Player jsonFilePlayer : temp) {
                    if (playerListPlayer.getUsername().equals(jsonFilePlayer.getUsername())) {
                        counter++;
                    }
                }
            }
            return counter == temp.size();
        }
        return false;
    }

    /**
     * Checks if a player occupies every country of a continent and sets the continent to occupied if so.
     *
     * @param continentName Name of the continent to be checked.
     * @param occupant      Name of the player to be checked for.
     */
    public void checkIfPlayerOwnsContinentAndSet(String continentName, String occupant) {
        worldManager.checkIfPlayerOwnsContinentAndSet(continentName, occupant);
    }

    /**
     * Assigns random Missions to the player or loads the mission from the saved game back into the player object.
     *
     * @param newGame If the missions should be created or loaded.
     * @param file    Filename of the saved game.
     */
    private void assignMissions(boolean newGame, String file) {
        playerManager.getPlayerList().forEach(player -> {
            Mission mission;
            if (newGame) {
                double randomNumber = Math.random();
                player.setRandomNumber(randomNumber);
            }
            if (player.getRandomNumber() > 0.75) {
                mission = new OccupyCountriesMission(getPlayerList().size());
            } else if (player.getRandomNumber() > 0.5) {
                if (newGame) {
                    mission = new OccupyTwoContinentsMission(worldManager.getContinentList(), true);
                } else {
                    try {
                        mission = new OccupyTwoContinentsMission(filePersistenceManager.retrieveContinentMission(
                                loadFile(file), player, worldManager.getContinentList()), false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (player.getRandomNumber() > 0.25) {
                mission = new OccupyAndReinforceMission(getPlayerList().size());
            } else {
                if (newGame) {
                    mission = new DefeatPlayerMission(player, playerManager.getPlayerList(), true, null);
                } else {
                    try {
                        mission = new DefeatPlayerMission(player, playerManager.getPlayerList(), false, filePersistenceManager.retrieveDefeatPlayerMission(loadFile(file), player, getPlayerList()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            player.setMission(mission);
        });
    }

    /**
     * Calculates whoever gets to be first at the beginning of the game.
     *
     * @return A player object of the first turn player.
     */
    private Player getFirstTurnPlayer() {
        int lowestCountriesCount = 43;
        Player firstTurnPlayer = null;
        for (Player player : playerManager.getPlayerList()) {
            int playersCountriesCount = worldManager.getAmountOfCountriesOwnedBy(player.getUsername());
            if (lowestCountriesCount >= playersCountriesCount) {
                lowestCountriesCount = playersCountriesCount;
                firstTurnPlayer = player;
            }
        }
        return firstTurnPlayer;
    }

    /**
     * Adds a player to the game.
     *
     * @param player Player to be added to the game.
     * @throws RemoteException When having trouble communicating with the client.
     */
    public void addPlayer(Player player) throws RemoteException {
        playerManager.addPlayer(player);
        notifyListeners(new GameLobbyEvent(player, PLAYER_ENTERED));
    }

    /**
     * Retrieves a country object from the country list by its ID.
     *
     * @param countryId ID of the desired country.
     * @return An object of the desired country.
     */
    public Country getCountry(int countryId) {
        return worldManager.getCountry(countryId);
    }

    /**
     * Retrieves the country list from the world manager
     *
     * @return An ArrayList of all existing countries and their current state.
     */
    public ArrayList<Country> getCountries() {
        return worldManager.getCountries();
    }

    /**
     * Removes a player from the game.
     *
     * @param player Player object of the player to be removed from the game.
     * @throws RemoteException When having trouble communicating with the client.
     */
    public void removePlayer(Player player) throws RemoteException {
        playerManager.removePlayer(player);
        notifyListeners(new GameLobbyEvent(player, PLAYER_LEFT));
    }

    /**
     * Retrieves the list of players currently in the game.
     *
     * @return An ArrayList of the player objects currently in the game.
     */
    public ArrayList<Player> getPlayerList() {
        return playerManager.getPlayerList();
    }

    /**
     * Retrieves the player information of next turns player.
     *
     * @return The player object of the next turns player.
     */
    public Player getNextPlayer() {
        return playerManager.getNextPlayer(currentTurn.getPlayer());
    }

    /**
     * Checks whether there is a legal amount of players currently in the lobby.
     *
     * @return A Boolean whether there is a legal amount of players in the lobby.
     */
    public boolean isLegalPlayerCount() {
        return playerManager.getPlayerList().size() >= 3 && playerManager.getPlayerList().size() <= 6;
    }

    /**
     * Moves forces from one country to another given they are occupied by the same player.
     *
     * @param originCountryId ID of the country units shall be moved from.
     * @param targetCountryId ID of the country units shall be moved to.
     * @param amount          Amount of units to be moved.
     * @throws MovementException When the requirements for a movement are not met.
     * @throws RemoteException   When having trouble communicating with the client.
     */
    public void moveForces(int originCountryId, int targetCountryId, int amount) throws MovementException, RemoteException {
        worldManager.moveForces(originCountryId, targetCountryId, amount, currentTurn.getPlayer());
        notifyListeners(
                new GameActionEvent(currentTurn.getPlayer(),
                        GameActionEvent.GameActionEventType.MOVE,
                        getPlayerList(),
                        getCountries()));
    }

    /**
     * Checks if the mission of a player has been fulfilled.
     *
     * @param player Player whose mission shall be checked.
     * @return A Boolean value whether or not the mission has been fulfilled.
     */
    public boolean isMissionCompleted(Player player) {
        return player.hasCompletedMission(worldManager.getCountries(), playerManager.getPlayerList());
    }

    /**
     * Gets the information of the ongoing attack.
     *
     * @return An Attack object of the ongoing attack.
     */
    public Attack getCurrentAttack() {
        return this.attack;
    }

    /**
     * Checks if a specified player is the occupant of a specified country.
     *
     * @param player    Player object of the player who should be the occupant.
     * @param countryId ID of the country that should be checked.
     * @return A Boolean value representing whether or not the player is the occupant of the specified country.
     */
    public boolean isPlayerOccupantOfGivenCountry(Player player, int countryId) {
        return worldManager.isPlayerOccupantOfGivenCountry(player, countryId);
    }

    /**
     * Starts the attack sequence and notifies the occupant of the attacked country to defend it.
     *
     * @param attack Attack object containing all of the attack information.
     * @throws DoNotOccupyCountryException   When the player does not own the origin country of the attack.
     * @throws OccupyTargetCountry           When the player occupies the target country themselves.
     * @throws NoArmiesLeftException         When the player did not leave at least one army in the origin country.
     * @throws RemoteException               When having trouble communicating with the client.
     * @throws CountriesNotAdjacentException When the countries are not adjacent to one another.
     */
    public void startAttack(Attack attack) throws DoNotOccupyCountryException, OccupyTargetCountry, NoArmiesLeftException, RemoteException, CountriesNotAdjacentException {
        if (!getCountry(attack.getOriginCountry()).getOccupiedBy().equals(this.currentTurn.getPlayer().getUsername())) {
            throw new DoNotOccupyCountryException(getCountry(attack.getOriginCountry()));
        }
        if (getCountry(attack.getTargetCountry()).getOccupiedBy().equals(this.currentTurn.getPlayer().getUsername())) {
            throw new OccupyTargetCountry(getCountry(attack.getTargetCountry()));
        }
        if (attack.getAmount() > getCountry(attack.getOriginCountry()).getArmies() - 1) {
            throw new NoArmiesLeftException(getCountry(attack.getOriginCountry()), getCountry(attack.getTargetCountry()));
        }
        if (!worldManager.areAdjacent(attack.getOriginCountry(), attack.getTargetCountry())) {
            throw new CountriesNotAdjacentException();
        }
        attack.setDefendingPlayer(playerManager.getPlayer(getCountries().get(attack.getTargetCountry()).getOccupiedBy()));
        this.attack = attack;
        this.removeAttackingForcesFromOriginCountry();
        this.worldManager.getCountry(attack.getTargetCountry()).setUnitsMoved(true);
        notifyListeners(new GameActionEvent(
                this.currentTurn.getPlayer(),
                GameActionEvent.GameActionEventType.ATTACK,
                getPlayerList(),
                getCountries(),
                attack));
    }

    /**
     * Sets the defense information about the ongoing attack.
     *
     * @param defendingDice Amount of dice the attacked player defends with.
     * @throws RemoteException               When having trouble communicating with the client.
     * @throws NotEnoughArmiesException      When the defending player tries to defend with more units than there are in the country.
     * @throws IllegalDefendingDiceException When the defending player tries to defend with an illegal amount of dice.
     */
    public void defendAttack(int defendingDice) throws RemoteException, NotEnoughArmiesException, IllegalDefendingDiceException {
        if (defendingDice > getCountry(this.attack.getTargetCountry()).getArmies()) {
            throw new NotEnoughArmiesException();
        } else if (defendingDice < 1 || defendingDice > 2) {
            throw new IllegalDefendingDiceException();
        }
        openLiberationCycle(defendingDice);
    }

    /**
     * Starts the calculation of the ongoing attack.
     *
     * @param defendingDice Amount of dice the attacked player defends with.
     * @throws RemoteException When having trouble communicating with the client.
     */
    public void openLiberationCycle(int defendingDice) throws RemoteException {
        AttackResult result;
        int attackingDice = this.attack.getAmount();
        attackingDice = Math.min(attackingDice, 3);
        result = attack(attackingDice, defendingDice);
        notifyListeners(new GameActionEvent(
                this.currentTurn.getPlayer(),
                GameActionEvent.GameActionEventType.ATTACK_RESULT,
                getPlayerList(),
                getCountries(),
                this.attack,
                result
        ));
    }

    /**
     * Removes the amount of units of the ongoing attack from the origin country.
     *
     * @throws RemoteException When having trouble communicating with the client.
     */
    public void removeAttackingForcesFromOriginCountry() throws RemoteException {
        this.getCountry(this.attack.getOriginCountry()).decreaseArmy(this.attack.getAmount());
    }

    /**
     * Calculates the result of the ongoing attack.
     *
     * @param attackingDiceCount Amount of dice attacked with.
     * @param defendingDiceCount Amount of dice defended with.
     * @return An AttackResult object containing the results of the calculation.
     */
    public AttackResult attack(int attackingDiceCount, int defendingDiceCount) {
        AttackResult result = new AttackResult(attackingDiceCount, defendingDiceCount);

        // Setting lost armies
        if (result.getWinningDefendingDice() == defendingDiceCount) {
            this.attack.setAmount(this.attack.getAmount() - attackingDiceCount);
        } else {
            this.getCountry(this.attack.getTargetCountry()).decreaseArmy(result.getWinningAttackingDice());
            this.attack.setAmount(this.attack.getAmount() - result.getWinningDefendingDice());
        }

        if (this.attack.getAmount() <= 0) {
            // Defender won
            result.setAttackerWon(false);
            result.setHasBeenResolved(true);
            return result;
        } else if (this.getCountry(this.attack.getTargetCountry()).getArmies() <= 0) {
            // Attacker won
            result.setAttackerWon(true);
            this.getCountry(this.attack.getTargetCountry()).setOccupiedBy(
                    this.getCountry(this.attack.getOriginCountry()).getOccupiedBy());
            this.getCountry(this.attack.getTargetCountry()).setArmies(this.attack.getAmount());
            this.currentTurn.getPlayer().setEntitledToDraw(true);
            checkIfPlayerOwnsContinentAndSet(getCountry(this.attack.getTargetCountry()).getContinent(),
                    this.currentTurn.getPlayer().getUsername());
            result.setHasBeenResolved(true);
            return result;
        }

        return result;
    }

    /**
     * Retrieves the amount of reinforcements a player has to distribute in his distribution phase.
     *
     * @param player Player object of the player the reinforcement count should be calculated for.
     */
    public void getReinforcementUnits(Player player) {
        worldManager.getReinforcementUnits(player);
    }

    /**
     * Distributes an amount of units to a country.
     *
     * @param countryId ID of the country to distribute units to.
     * @param amount    Amount of units to distribute.
     * @throws DoNotOccupyCountryException When the player does not occupy the target country.
     * @throws NotEnoughArmiesException    When the player does not have enough reinforcements to distribute.
     * @throws RemoteException             When having trouble communicating with the client.
     */
    public void distributeArmy(int countryId, int amount) throws DoNotOccupyCountryException, NotEnoughArmiesException, RemoteException {
        if (!this.isPlayerOccupantOfGivenCountry(this.getCurrentTurn().getPlayer(), countryId)) {
            throw new DoNotOccupyCountryException(this.getCountry(countryId));
        }
        if (this.currentTurn.getPlayer().getArmies() < amount) {
            throw new NotEnoughArmiesException();
        }
        worldManager.distributeArmy(this.currentTurn.getPlayer(), countryId, amount);
        this.notifyListeners(new GameActionEvent(this.currentTurn.getPlayer(),
                GameActionEvent.GameActionEventType.DISTRIBUTE,
                getPlayerList(),
                getCountries()));
    }

    /**
     * Saves the current game state to a file.
     *
     * @param file Filename the saved state shall be stored as.
     * @throws IOException When having trouble writing the save file.
     */
    public void saveGame(String file) throws IOException {
        filePersistenceManager.writeGameIntoFile(getPlayerList(), worldManager.getContinentList(), getCurrentTurn(), cardManager, file);
    }

    /**
     * Draws a card from the card stack and adds it to the current turns players hand.
     *
     * @throws RemoteException                When having trouble communicating with the client.
     * @throws NotEntitledToDrawCardException When the player has not liberated at least one country this turn.
     */
    public void playerDrawsCard() throws RemoteException, NotEntitledToDrawCardException {
        if (!this.currentTurn.getPlayer().getEntitledToDraw()) {
            throw new NotEntitledToDrawCardException();
        }
        Card c = cardManager.drawCard();
        this.currentTurn.getPlayer().insertCardToHand(c);
        this.currentTurn.getPlayer().setEntitledToDraw(false);
        this.notifyListeners(new GameActionEvent(this.currentTurn.getPlayer(), GameActionEvent.GameActionEventType.DRAW, getPlayerList(), getCountries()));
    }

    /**
     * Checks if a player owns a peace card.
     *
     * @param player Player object of the player to be checked.
     * @return A Boolean value whether or not the player owns a peace card.
     */
    public boolean playerHasPeaceCard(Player player) {
        for (Card card : player.getCards()) {
            if (card.getKind().equals("Peace-Card")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Trades in cards for an increase of reinforcements.
     *
     * @param cardIds Array of the card IDs to be traded in.
     * @throws InvalidCardCombinationException When the cards that should be traded are an illegal combination.
     * @throws RemoteException                 When having trouble communicating with the client.
     */
    public void tradeCards(int[] cardIds) throws InvalidCardCombinationException, RemoteException {
        int extraUnits = cardManager.tradeCards(cardIds);
        if (extraUnits == 0) {
            throw new InvalidCardCombinationException(this.currentTurn.getPlayer());
        }
        Card card1 = cardManager.getCardById(cardIds[0]);
        Card card2 = cardManager.getCardById(cardIds[1]);
        Card card3 = cardManager.getCardById(cardIds[2]);
        this.currentTurn.getPlayer().removeCards(card1, card2, card3);
        this.currentTurn.getPlayer().increaseArmies(extraUnits);
        this.notifyListeners(new GameActionEvent(this.currentTurn.getPlayer(), GameActionEvent.GameActionEventType.TRADE, getPlayerList(), getCountries()));
    }

    /**
     * Loads a file.
     *
     * @param file Filename of the file to be loaded.
     * @return JSONObject of the contents of the file.
     * @throws IOException When having trouble reading or finding the file.
     */
    private JSONObject loadFile(String file) throws IOException {
        return filePersistenceManager.loadFile(file);
    }

    /**
     * Updates the player model for the player list
     *
     * @return A DefaultListModel object of the player list model.
     * @throws RemoteException When having trouble communicating with the client.
     */
    public DefaultListModel<String> updatePlayerModel() throws RemoteException {
        return playerManager.updatePlayerModel();
    }

    /**
     * Retrieves a player object by username.
     *
     * @param username Username of the desired player.
     * @return A Player object of the desired player.
     */
    public Player getPlayer(String username) {
        return playerManager.getPlayer(username);
    }

    /**
     * Notifies all connected clients about game updates
     *
     * @param event Event to broadcast to all clients
     * @throws RemoteException When having trouble communicating with the client.
     */
    private void notifyListeners(GameEvent event) throws RemoteException {
        for (GameEventListener listener : listeners) {
            Thread t = new Thread(() -> {
                try {
                    listener.handleGameEvent(event);
                } catch (RemoteException | NotEntitledToDrawCardException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    /**
     * Adds a game client as a listener to server updates.
     *
     * @param listener Game client as a listener to be added.
     * @throws RemoteException When having trouble communicating with the client.
     */
    public void addGameEventListener(GameEventListener listener) throws RemoteException {
        listeners.add(listener);
    }

    /**
     * Removes a game client from the listener list of server updates.
     *
     * @param listener Game client as a listener to be removed from the list.
     * @throws RemoteException When having trouble communicating with the client.
     */
    public void removeGameEventListener(GameEventListener listener) throws RemoteException {
        listeners.remove(listener);
    }

    /**
     * Entry point of the server application.
     */
    public static void main(String[] args) {
        String serviceName = "RiskServer";

        Registry registry;
        ServerRemote server = null;

        try {
            server = new RiskServer();

            registry = LocateRegistry.getRegistry();
            registry.rebind(serviceName, server);
            System.out.println("Local registry-object found.");
            System.out.println("Game-Server running...");
        } catch (ConnectException ce) {
            System.out.println("No registry found.");
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Registry created.");
                registry.rebind(serviceName, server);
                System.out.println("Game-Server running...");
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (RemoteException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        }
    }
}
