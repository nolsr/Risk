package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.DarkList;
import de.hsbremen.risk.client.components.LightButton;
import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;

import javax.swing.*;
import java.awt.*;

public class RiskLobby extends JPanel implements GameEventListener {
    private final LightButton startGameButton = new LightButton("Start Game");
    private final LightButton loadGameButton = new LightButton("Load Game");

    private final LightButton exitButton = new LightButton("Back to Main-Menu");

    private final DarkList<String> playerJList = new DarkList<>();

    public RiskLobby() {
        super();
        setOpaque(false);
        this.setLayout(new BorderLayout());
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(startGameButton, BorderLayout.CENTER);

        this.add(controlPanel, BorderLayout.NORTH);
        this.add(playerJList, BorderLayout.CENTER);
        this.add(exitButton, BorderLayout.SOUTH);
    }

    public void updatePlayerList(DefaultListModel<String> players) {
        playerJList.setModel(players);
    }

    public JButton getStartGameButton() {
        return startGameButton;
    }

    public JButton getLoadGameButton() {
        return loadGameButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public void handleGameEvent(GameEvent event) {
        if (!(event instanceof GameLobbyEvent)) {
            return;
        }
        System.out.println(((GameLobbyEvent) event).getType());
    }
}