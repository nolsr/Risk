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

    private final PlayerManager playerManager;
    private final WorldManager worldManager;
    private final CardManager cardManager;

    private final FilePersistenceManager filePersistenceManager;
    // GameManager
    private Turn currentTurn;
    private Attack attack;
    private List<GameEventListener> listeners;

    private int defendingDice;

    public RiskServer() throws RemoteException {
        filePersistenceManager = new FilePersistenceManager();
        playerManager = new PlayerManager();
        worldManager = new WorldManager();
        cardManager = new CardManager();
        listeners = new Vector<>();
        // allPlayers = new Vector<>();
    }


    public Turn getCurrentTurn() {
        return this.currentTurn;
    }

    public ArrayList<Player> getWinner() {
        int mostCountriesOccupied = 0;
        ArrayList<Player> leadingPlayer = null;
        if (isMissionCompleted(this.currentTurn.getPlayer())) {
            leadingPlayer.add(this.currentTurn.getPlayer());
            return leadingPlayer;
        }
        for (Player player : getPlayerList()) {
            if (worldManager.getAmountOfCountriesOwnedBy(player.getUsername()) > mostCountriesOccupied) {
                mostCountriesOccupied = worldManager.getAmountOfCountriesOwnedBy(player.getUsername());
                leadingPlayer = null;
                leadingPlayer.add(player);
            } else if (worldManager.getAmountOfCountriesOwnedBy(player.getUsername()) == mostCountriesOccupied) {
                leadingPlayer.add(player);
            }
        }
        return leadingPlayer;
    }

    public void nextTurn() throws UnplacedArmiesException, GameEndedException, RemoteException {
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
        if (isMissionCompleted(this.currentTurn.getPlayer()) || playerHasPeaceCard(this.currentTurn.getPlayer())) {
            this.currentTurn.setGameEnded();
        }

        notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.NEXT_PHASE, getCountries()));
    }

    public ArrayList<Integer> getNeighbourCountries(int countryId) {
        return worldManager.getNeighbourCountries(countryId);
    }

    public void startGame() throws RemoteException, NotEnoughPlayersException {
        if (!this.isLegalPlayerCount()) {
            throw (new NotEnoughPlayersException());
        }
        playerManager.shufflePlayerList();
        worldManager.assignCountriesToPlayers(getPlayerList());
        cardManager.insertPeaceCard(getPlayerList().size());
        assignMissions(true, null);

        Player firstTurnPlayer = getFirstTurnPlayer();
        this.currentTurn = new Turn(firstTurnPlayer);
        System.out.println("Star game turn: " + this.currentTurn);
        for (Continent continent : worldManager.getContinentList())
            for (Player player : getPlayerList()) {
                checkIfPlayerOwnsContinentAndSet(continent.getName(), player.getUsername());
            }
        getReinforcementUnits(firstTurnPlayer);
        this.notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.GAME_STARTED, getCountries()));
    }

    public boolean loadGame(String file) throws IOException, RemoteException, LoadGameWrongPlayerException{
        try {
            if (!checkLoadGamePossible(file)) {
                throw new LoadGameWrongPlayerException(file);
            }
            filePersistenceManager.retrieveContinentData(loadFile(file), 0, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(loadFile(file), 1, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(loadFile(file), 2, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(loadFile(file), 3, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(loadFile(file), 4, worldManager.getContinentList());
            filePersistenceManager.retrieveContinentData(loadFile(file), 5, worldManager.getContinentList());
            playerManager.setPlayerList(filePersistenceManager.retrievePlayerData(loadFile(file)));
            Player loadedPlayerTurn = playerManager.getPlayer(filePersistenceManager.retrieveTurnPlayer(loadFile(file)));
            this.currentTurn = new Turn(loadedPlayerTurn);
            this.currentTurn.setPhase(filePersistenceManager.retrieveTurnPhase(loadFile(file)));
            filePersistenceManager.retrieveCardManagerInfo(loadFile(file), cardManager);
            cardManager.updateCardManager(filePersistenceManager.retrieveCardsData(loadFile(file)));

            assignMissions(false, file); // Wildcards noch nicht
            this.notifyListeners(new GameControlEvent(this.currentTurn, GameControlEvent.GameControlEventType.GAME_STARTED, getCountries()));
            return true;
        } catch (NullPointerException ignored) {

        } catch (NotEntitledToDrawCardException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkLoadGamePossible(String file) throws IOException, NotEntitledToDrawCardException {
        ArrayList<Player> temp = filePersistenceManager.retrievePlayerData(loadFile(file));
        int counter = 0;
        if (getPlayerList().size() == temp.size()) {
            for (Player playerListplayer : getPlayerList()) {
                for (Player jsonFilePlayer : temp) {
                    if (playerListplayer.getUsername().equals(jsonFilePlayer.getUsername())) {
                        counter++;
                        System.out.println("Counter: " + counter);
                    }
                }
            }
            return counter == temp.size();
        }
      return false;
    }


    public void checkIfPlayerOwnsContinentAndSet(String continentName, String occupant) {
        worldManager.checkIfPlayerOwnsContinentAndSet(continentName, occupant);
    }

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
                    mission = new OccupyTwoContinentsMission(worldManager.getContinentList(), newGame);
                } else {
                    try {
                        mission = new OccupyTwoContinentsMission(filePersistenceManager.retrieveContinentMission(loadFile(file), player, worldManager.getContinentList()), newGame);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (player.getRandomNumber() > 0.25) {
                mission = new OccupyAndReinforceMission(getPlayerList().size());
            } else {
                if (newGame) {
                    mission = new DefeatPlayerMission(player, playerManager.getPlayerList(), newGame, null);
                } else {
                    try {
                        mission = new DefeatPlayerMission(player, playerManager.getPlayerList(), newGame, filePersistenceManager.retrieveDefeatPlayerMission(loadFile(file), player, getPlayerList()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            player.setMission(mission);
        });
    }

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

    public int getAmountOfCountriesOwnedBy(Player player) {
        return worldManager.getAmountOfCountriesOwnedBy(player.getUsername());
    }

    public void addPlayer(Player player) throws RemoteException {
        playerManager.createPlayer(player);
        notifyListeners(new GameLobbyEvent(player, PLAYER_ENTERED));
    }

    public Country getCountry(int countryId) {
        return worldManager.getCountry(countryId);
    }

    public ArrayList<Country> getCountries() {
        return worldManager.getCountries();
    }

    public void removePlayer(Player player) throws RemoteException {
        playerManager.removePlayer(player);
        notifyListeners(new GameLobbyEvent(player, PLAYER_LEFT));
    }

    public ArrayList<Player> getPlayerList() {
        return playerManager.getPlayerList();
    }

    public Player getNextPlayer() {
        return playerManager.getNextPlayer(currentTurn.getPlayer());
    }

    public boolean isLegalPlayerCount() {
        return playerManager.getPlayerList().size() >= 2 && playerManager.getPlayerList().size() <= 6;
    }

    public void moveForces(int originCountryId, int targetCountryId, int amount) throws MovementException, RemoteException {
        worldManager.moveForces(originCountryId, targetCountryId, amount, currentTurn.getPlayer());
        notifyListeners(
                new GameActionEvent(currentTurn.getPlayer(),
                        GameActionEvent.GameActionEventType.MOVE,
                        getPlayerList(),
                        getCountries()));
    }

    public boolean isMissionCompleted(Player player) {
        return player.hasCompletedMission(worldManager.getCountries(), playerManager.getPlayerList());
    }

    public boolean isNeighbour(int liberatingCountryId, int defendingCountryId) {
        for (int neighbourCountryId : getNeighbourCountries(liberatingCountryId)) {
            if (neighbourCountryId == defendingCountryId) {
                return true;
            }
        }
        return false;
    }

    public Attack getCurrentAttack() {
        return this.attack;
    }

    public boolean isPlayerOccupantOfGivenCountry(Player player, int countryId) {
        return worldManager.isPlayerOccupantOfGivenCountry(player, countryId);
    }

    public void startAttack(Attack attack) throws DoNotOccupyCountryException, OccupyTargetCountry, NoArmiesLeftException, RemoteException {
        if (!getCountry(attack.getOriginCountry()).getOccupiedBy().equals(this.currentTurn.getPlayer().getUsername())) {
            throw new DoNotOccupyCountryException(getCountry(attack.getOriginCountry()));
        }
        if (getCountry(attack.getTargetCountry()).getOccupiedBy().equals(this.currentTurn.getPlayer().getUsername())) {
            throw new OccupyTargetCountry(getCountry(attack.getTargetCountry()));
        }
        if (attack.getAmount() > getCountry(attack.getOriginCountry()).getArmies() - 1) {
            throw new NoArmiesLeftException(getCountry(attack.getOriginCountry()), getCountry(attack.getTargetCountry()));
        }
        attack.setDefendingPlayer(playerManager.getPlayer(getCountries().get(attack.getTargetCountry()).getOccupiedBy()));
        this.attack = attack;
        this.removeAttackingForcesFromOriginCountry();
        notifyListeners(new GameActionEvent(
                this.currentTurn.getPlayer(),
                GameActionEvent.GameActionEventType.ATTACK,
                getPlayerList(),
                getCountries(),
                attack));
    }

    public void defendAttack(int defendingDice) throws RemoteException, NotEnoughArmiesException, IllegalDefendingDiceException {
        if (defendingDice > getCountry(this.attack.getTargetCountry()).getArmies()) {
            throw new NotEnoughArmiesException();
        } else if (defendingDice < 1 || defendingDice > 2) {
            throw new IllegalDefendingDiceException();
        }
        openLiberationCycle(defendingDice);
    }

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

    public void removeAttackingForcesFromOriginCountry() throws RemoteException {
        this.getCountry(this.attack.getOriginCountry()).decreaseArmy(this.attack.getAmount());
    }

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

    public void getReinforcementUnits(Player player) {
        worldManager.getReinforcementUnits(player);
    }

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

    public void saveGame(String file) throws IOException {
        filePersistenceManager.writeGameIntoFile(getPlayerList(), worldManager.getContinentList(), getCurrentTurn(), cardManager.getCardList(), cardManager, file);
    }

    public void playerDrawsCard() throws RemoteException, NotEntitledToDrawCardException {
        this.currentTurn.getPlayer().insertCardToHand(cardManager.drawCard());
        this.currentTurn.getPlayer().setEntitledToDraw(false);
        this.notifyListeners(new GameActionEvent(this.currentTurn.getPlayer(), GameActionEvent.GameActionEventType.DRAW, getPlayerList(), getCountries()));
    }

    public boolean playerHasPeaceCard(Player player) {
        for (Card card : player.getCards()) {
            if (card.getKind().equals("Peace-Card")) {
                return true;
            }
        }
        return false;
    }

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

    private JSONObject loadFile(String datei) throws IOException {
        return filePersistenceManager.loadFile(datei);
    }

    public DefaultListModel<String> updatePlayerModel() throws RemoteException {
        return playerManager.updatePlayerModel();
    }

    public Player getPlayer(String username) {
        return playerManager.getPlayer(username);
    }

    private void notifyListeners(GameEvent event) throws RemoteException {
        for (GameEventListener listener : listeners) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener.handleGameEvent(event);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    public void addGameEventListener(GameEventListener listener) throws RemoteException {
        listeners.add(listener);
    }

    public void removeGameEventListener(GameEventListener listener) throws RemoteException {
        listeners.remove(listener);
    }

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
