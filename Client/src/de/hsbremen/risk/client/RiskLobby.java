package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.DarkList;
import de.hsbremen.risk.client.components.DarkLog;
import de.hsbremen.risk.client.components.LightButton;

import javax.swing.*;
import java.awt.*;

public class RiskLobby extends JPanel  {
    private final LightButton startGameButton = new LightButton("Start Game");
    private final LightButton loadGameButton = new LightButton("Load Game");
    private final LightButton exitButton = new LightButton("Back to Main-Menu");
    private final DarkList<String> playerJList = new DarkList<>();
    private final DarkLog lobbylog = new DarkLog();

    /**
     * All UI Elements are applied and set
     */
    public RiskLobby() {
        super();
        setOpaque(false);
        this.setLayout(new BorderLayout());
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(startGameButton, BorderLayout.CENTER);
        controlPanel.add(loadGameButton, BorderLayout.CENTER);
        lobbylog.setColumns(1);

        this.add(controlPanel, BorderLayout.NORTH);
        this.add(playerJList, BorderLayout.CENTER);
        this.add(exitButton, BorderLayout.SOUTH);

        this.add(lobbylog, BorderLayout.EAST);
    }

    /**
     * Updates the PlayerJList which displays the current players.
     *
     * @param players DefaultListModel consisting of all the clients players.
     */
    public void updatePlayerList(DefaultListModel<String> players) {
        playerJList.setModel(players);
    }

    /**
     * Updates the lobbylog which displays the lobby activity, meaning joining
     * and leaving the lobby by clients.
     *
     * @param log Player activity in the lobby, meaning leaving or joining the lobby.
     */
    public void updateLobbyLog(String log) {
        lobbylog.append(log);
    }

    /**
     * Retrieves the start game button.
     *
     * @return returns the LightButton startGameButton.
     */
    public JButton getStartGameButton() {
        return startGameButton;
    }

    /**
     * Retrieves the load game button.
     *
     * @return returns the LightButton loadGameButton.
     */
    public JButton getLoadGameButton() {
        return loadGameButton;
    }

    /**
     * Retrieves the exit button.
     *
     * @return returns the LightButton exitButton.
     */
    public JButton getExitButton() {
        return exitButton;
    }
}