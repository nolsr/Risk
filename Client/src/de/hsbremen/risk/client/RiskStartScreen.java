package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.LightButton;
import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Raphael Tam-Dao
 */

public class RiskStartScreen extends JPanel implements GameEventListener {

    private JButton newGameButton = new LightButton("Enter Lobby");
    private JButton quitGameButton = new LightButton("Quit Game");

    public RiskStartScreen() {
        super();
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(newGameButton, BorderLayout.CENTER);
        add(quitGameButton);
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }

    public JButton getQuitGameButton() {
        return quitGameButton;
    }

    public void handleGameEvent(GameEvent event) {

    }
}
