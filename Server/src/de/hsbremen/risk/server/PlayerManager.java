package de.hsbremen.risk.server;

import de.hsbremen.risk.common.entities.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerManager {

    private ArrayList<Player> playerList = new ArrayList<>();

    private final DefaultListModel<String> model = new DefaultListModel<>();

    /**
     * Retrieves the player object of a player by his username.
     *
     * @param username Username of the desired player.
     * @return A Player object of the desired player.
     */
    public Player getPlayer(String username) {
        for (Player player : playerList) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Updates the player list model.
     *
     * @return A DefaultListModel object of the player list model.
     */
    public DefaultListModel<String> updatePlayerModel() {
        model.removeAllElements();
        playerList.forEach(p -> model.addElement(p.getUsername()));
        return model;
    }

    /**
     * Adds a player to the player list.
     *
     * @param player Player object to be added.
     */
    public void addPlayer(Player player) {
        if (getPlayer(player.getUsername()) == null) {
            playerList.add(player);
        }
    }

    /**
     * Removes a player from the player list.
     *
     * @param player Player object to be removed.
     */
    public void removePlayer(Player player) {
        playerList.remove(getPlayer(player.getUsername()));
    }

    /**
     * Retrieves the player list.
     *
     * @return An ArrayList of the players.
     */
    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Shuffles the player list.
     */
    public void shufflePlayerList() {
        Collections.shuffle(playerList);
    }

    /**
     * Gets the player at the next index of the player list.
     *
     * @param currentPlayer Player object of the player whose turn it is.
     * @return A Player object of the player whose turn is next.
     */
    public Player getNextPlayer(Player currentPlayer) {
        int nextPlayerIndex = playerList.indexOf(currentPlayer) + 1;
        if (nextPlayerIndex >= playerList.size()) {
            nextPlayerIndex = 0;
        }
        return playerList.get(nextPlayerIndex);
    }

    /**
     * Sets the player list. Is used when loading a save file.
     *
     * @param playerList ArrayList of the players to be loaded in.
     */
    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }
}
