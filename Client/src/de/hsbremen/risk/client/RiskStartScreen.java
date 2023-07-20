package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.LightButton;

import javax.swing.*;
import java.awt.*;

/**
 * @author Raphael Tam-Dao
 */

public class RiskStartScreen extends JPanel {

    private JButton newGameButton = new LightButton("Enter Lobby");
    private JButton loadGameButton = new LightButton("Load Game");
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

    public JButton getLoadGameButton() {
        return loadGameButton;
    }

    public JButton getQuitGameButton() {
        return quitGameButton;
    }
}
