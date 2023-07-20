package de.hsbremen.risk.server;

import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.exceptions.*;
import de.hsbremen.risk.common.entities.*;
import de.hsbremen.risk.common.entities.missions.*;
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

public class RiskServer extends UnicastRemoteObject implements ServerRemote {

    private final PlayerManager playerManager;
    private final WorldManager worldManager;
    private final CardManager cardManager;

    private final FilePersistenceManager filePersistenceManager;
    // GameManager
    private Turn currentTurn;
    private Attack attack;

    public RiskServer() throws RemoteException {
        filePersistenceManager = new FilePersistenceManager();
        playerManager = new PlayerManager();
        worldManager = new WorldManager();
        cardManager = new CardManager();
    }

    public Turn getCurrentTurn() {
        return this.currentTurn;
    }

    public ArrayList<Player> getWinner() {
        int mostCountriesOccupied = 0;
        ArrayList<Player> leadingPlayer = null;
        if(isMissionCompleted(this.currentTurn.getPlayer()))
        {
            leadingPlayer.add(this.currentTurn.getPlayer());
            return leadingPlayer;
        }
        for (Player player: getPlayerList())
        {
            if(worldManager.getAmountOfCountriesOwnedBy(player.getUsername()) > mostCountriesOccupied)
            {
                mostCountriesOccupied = worldManager.getAmountOfCountriesOwnedBy(player.getUsername());
                leadingPlayer = null;
                leadingPlayer.add(player);
            }
            else if(worldManager.getAmountOfCountriesOwnedBy(player.getUsername()) == mostCountriesOccupied)
            {
                leadingPlayer.add(player);
            }
        }
        return leadingPlayer;
    }

    public void nextTurn() throws UnplacedArmiesException, GameEndedException {
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
            return;
        }
        this.currentTurn.nextPhase();
        if (isMissionCompleted(this.currentTurn.getPlayer()) || playerHasPeaceCard(this.currentTurn.getPlayer())) {
            this.currentTurn.setGameEnded();
        }
    }

    public ArrayList<Integer> getNeighbourCountries(int countryId) {
        return worldManager.getNeighbourCountries(countryId);
    }

    public void startGame() {
        playerManager.shufflePlayerList();
        worldManager.assignCountriesToPlayers(getPlayerList());
        cardManager.insertPeaceCard(getPlayerList().size());
        assignMissions(true, null);

        Player firstTurnPlayer = getFirstTurnPlayer();
        this.currentTurn = new Turn(firstTurnPlayer);
        for (Continent continent : worldManager.getContinentList())
            for (Player player : getPlayerList()) {
                checkIfPlayerOwnsContinentAndSet(continent.getName(), player.getUsername());
            }
        getReinforcementUnits(firstTurnPlayer);
    }

    public boolean loadGame(String file) throws IOException {
        try {
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
            return true;
        } catch (NullPointerException ignored) {

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
                }
                else {
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
    public int getAmountOfCountriesOwnedBy(Player player)
    {
        return worldManager.getAmountOfCountriesOwnedBy(player.getUsername());
    }

    public void addPlayer(String username){
        playerManager.createPlayer(username);
    }

    public Country getCountry(int countryId) {
        return worldManager.getCountry(countryId);
    }

    public ArrayList<Country> getCountries() {
        return worldManager.getCountries();
    }


    public void removePlayer(String username) {
        playerManager.removePlayer(username);
    }

    public void removeAllPlayers() {
        playerManager.removeAllPlayers();
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

    public void moveForces(int originCountryId, int targetCountryId, int amount) throws MovementException {
        worldManager.moveForces(originCountryId, targetCountryId, amount, currentTurn.getPlayer());
    }

    public boolean isMissionCompleted(Player player) {
        return player.hasCompletedMission(worldManager.getCountries(), playerManager.getPlayerList());
    }

    public boolean isNeighbour(int liberatingCountryId, int defendingCountryId)
    {
        for (int neighbourCountryId : getNeighbourCountries(liberatingCountryId))
        {
            if(neighbourCountryId == defendingCountryId)
            {
                return true;
            }
        }
        return false;
    }

    public Attack getCurrentAttack()
    {
        return this.attack;
    }
    public boolean isPlayerOccupantOfGivenCountry(Player player, int countryId)
    {
        return worldManager.isPlayerOccupantOfGivenCountry(player, countryId);
    }

    public boolean isAttackLegal(Attack attack) throws DoNotOccupyCountryException, OccupyTargetCountry, NoArmiesLeftException {
        if (!getCountry(attack.getOriginCountry()).getOccupiedBy().equals(this.currentTurn.getPlayer().getUsername())) {
            throw new DoNotOccupyCountryException(getCountry(attack.getOriginCountry()));
        }
        if (getCountry(attack.getTargetCountry()).getOccupiedBy().equals(this.currentTurn.getPlayer().getUsername())) {
            throw new OccupyTargetCountry(getCountry(attack.getTargetCountry()));
        }
        if (attack.getAmount() > getCountry(attack.getOriginCountry()).getArmies() - 1) {
            throw new NoArmiesLeftException(getCountry(attack.getOriginCountry()), getCountry(attack.getTargetCountry()));
        }
        this.attack = attack;
        return true;
    }

    public void removeAttackingForcesFromOriginCountry() {
        this.getCountry(this.attack.getOriginCountry()).decreaseArmy(this.attack.getAmount());
    }

    public AttackResult attack(int attackingDiceCount, int defendingDiceCount) {
        AttackResult result = new AttackResult(attackingDiceCount, defendingDiceCount);

        // Setting lost armies
        if (result.getWinningDefendingDice() == defendingDiceCount) {
            this.attack.setAmount(0);
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

    public void distributeArmy(int countryId, int amount) throws DoNotOccupyCountryException, NotEnoughArmiesException {
        if (!this.isPlayerOccupantOfGivenCountry(this.getCurrentTurn().getPlayer(), countryId)) {
            throw new DoNotOccupyCountryException(this.getCountry(countryId));
        }
        if (this.currentTurn.getPlayer().getArmies() < amount) {
            throw new NotEnoughArmiesException();
        }
        worldManager.distributeArmy(this.currentTurn.getPlayer(), countryId, amount);
    }

    public void saveGame(String file) throws IOException {
        filePersistenceManager.writeGameIntoFile(getPlayerList(), worldManager.getContinentList(), getCurrentTurn(), cardManager.getCardList(), cardManager, file);
    }

    public boolean playerHasPeaceCard(Player player)
    {
        for (Card card: player.getCards()) {
            if(card.getKind().equals("Peace-Card"))
            {
                return true;
            }
        }
        return false;
    }

    public void playerDrawsCard(Player drawingPlayer)
    {
        drawingPlayer.insertCardToHand(cardManager.drawCard());
    }

    public void tradeCards(int[] cardIds) throws InvalidCardCombinationException {
        int extraUnits = cardManager.tradeCards(cardIds);
        if(extraUnits == 0)
        {
            throw new InvalidCardCombinationException(this.currentTurn.getPlayer());
        }
        Card card1 = cardManager.getCardById(cardIds[0]);
        Card card2 = cardManager.getCardById(cardIds[1]);
        Card card3 = cardManager.getCardById(cardIds[2]);
        System.out.println(this.currentTurn.getPlayer().getUsername());
        this.currentTurn.getPlayer().removeCards(card1, card2, card3);
        this.currentTurn.getPlayer().increaseArmies(extraUnits);
    }

    private JSONObject loadFile(String datei) throws IOException {
        return filePersistenceManager.loadFile(datei);
    }

    public DefaultListModel<String> addPlayerToModel(String name) {
        return playerManager.addPlayerToModel(name);
    }

    public DefaultListModel<String> removePlayerFromModel(String name) {
        return playerManager.removePlayerFromModel(name);
    }

    public DefaultListModel<String> getModel() {
        return playerManager.getModel();
    }

    public Player getPlayer(String username) {
       return playerManager.getPlayer(username);
    }


    public static void main(String[] args) {
        String serviceName = "Risk Server";

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
