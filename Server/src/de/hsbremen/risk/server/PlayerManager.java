package de.hsbremen.risk.server;

import de.hsbremen.risk.common.entities.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerManager {

    private ArrayList<Player> playerList = new ArrayList<>();

    private DefaultListModel<String> model = new DefaultListModel<>();

    public Player getPlayer(String username) {
        for (Player player : playerList) {
            if (player.getUsername().equalsIgnoreCase(username))  {
                return player;
            }
        }
        return null;
    }
    public DefaultListModel<String> removePlayerFromModel(String name) {
        for (int i = 0; i < model.size(); i++) {
            if (name.equals(model.get(i))) {
                model.remove(i);
            }
        }
        return model;
    }

    public DefaultListModel<String> addPlayerToModel(String name) {
        model.addElement(name);
        return model;
    }

    public DefaultListModel<String> getModel() {
        return model;
    }
    public boolean createPlayer(String username) {
        if (getPlayer(username) == null) {
            playerList.add(new Player(username, 0));
            return true;
        }
        return false;
    }

    public void removePlayer(String username) {
        playerList.remove(getPlayer(username));
    }

    public void removeAllPlayers() {
        playerList.clear();
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void shufflePlayerList() {
        Collections.shuffle(playerList);
    }

    public Player getNextPlayer(Player currentPlayer) {
        int nextPlayerIndex = playerList.indexOf(currentPlayer) + 1;
        if (nextPlayerIndex >= playerList.size()) {
            nextPlayerIndex = 0;
        }
        return playerList.get(nextPlayerIndex);
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }
}
