package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.DarkList;
import de.hsbremen.risk.client.components.DarkLog;
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
    private final DarkLog lobbylog = new DarkLog();
    private JScrollPane scrollPane = new JScrollPane(lobbylog, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
        scrollPane.add(lobbylog);

        this.add(controlPanel, BorderLayout.NORTH);
        this.add(playerJList, BorderLayout.CENTER);
        this.add(exitButton, BorderLayout.SOUTH);
        //this.add(scrollPane, BorderLayout.EAST);

        this.add(lobbylog, BorderLayout.EAST);
    }

    public void updatePlayerList(DefaultListModel<String> players) {
        playerJList.setModel(players);
    }

    public void updateLobbyLog(String log) {
        lobbylog.append(log);
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