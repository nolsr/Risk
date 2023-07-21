package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.DarkList;
import de.hsbremen.risk.client.components.LightButton;
import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;

import javax.swing.*;
import java.awt.*;

public class RiskLobby extends JPanel implements GameEventListener {
    private final JButton startGameButton = new LightButton("Start Game");

    private final JButton exitButton = new LightButton("Back to Main-Menu");

    private final DarkList playerJList = new DarkList();

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

    public DarkList getPlayerJList() {
        return playerJList;
    }

    public JButton getStartGameButton() {
        return startGameButton;
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