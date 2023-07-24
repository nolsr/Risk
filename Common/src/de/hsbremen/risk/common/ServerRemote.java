package de.hsbremen.risk.common;

import de.hsbremen.risk.common.entities.*;
import de.hsbremen.risk.common.exceptions.*;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerRemote extends Remote {

    void addGameEventListener(GameEventListener listener) throws RemoteException;

    void removeGameEventListener(GameEventListener listener) throws RemoteException;

    void addPlayer(Player player) throws RemoteException;

    Turn getCurrentTurn() throws RemoteException;

    void nextTurn() throws RemoteException, UnplacedArmiesException, GameEndedException;

    void startGame() throws RemoteException, NotEnoughPlayersException;

    boolean loadGame(String file) throws IOException, RemoteException, LoadGameWrongPlayerException;

    Country getCountry(int countryId) throws RemoteException;

    void removePlayer(Player player) throws RemoteException;

    ArrayList<Player> getPlayerList() throws RemoteException;

    void moveForces(int originCountryId, int targetCountryId, int amount) throws MovementException, RemoteException;

    Attack getCurrentAttack() throws RemoteException;

    void startAttack(Attack attack) throws DoNotOccupyCountryException, OccupyTargetCountry, NoArmiesLeftException, RemoteException;

    void removeAttackingForcesFromOriginCountry() throws RemoteException;

    AttackResult attack(int attackingDiceCount, int defendingDiceCount) throws RemoteException;

    void defendAttack(int defendingDice) throws RemoteException, NotEnoughArmiesException, IllegalDefendingDiceException;

    void distributeArmy(int countryId, int amount) throws DoNotOccupyCountryException, NotEnoughArmiesException, RemoteException;

    void saveGame(String file) throws IOException, RemoteException;

    void playerDrawsCard() throws RemoteException, NotEntitledToDrawCardException;

    void tradeCards(int[] cardIds) throws InvalidCardCombinationException, RemoteException;

    DefaultListModel<String> updatePlayerModel() throws RemoteException;

    Player getPlayer(String username) throws RemoteException;
}
