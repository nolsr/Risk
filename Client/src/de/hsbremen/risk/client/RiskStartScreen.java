package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.LightButton;

import javax.swing.*;
import java.awt.*;

public class RiskStartScreen extends JPanel {

    private LightButton newGameButton = new LightButton("Enter Lobby");
    private LightButton quitGameButton = new LightButton("Quit Game");

    /**
     * All UI Elements are applied and set
     */
    public RiskStartScreen() {
        super();
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(newGameButton, BorderLayout.CENTER);
        add(quitGameButton);
    }

    /**
     * Retrieves the new game button.
     *
     * @return the LightButton newGameButton.
     */
    public JButton getNewGameButton() {
        return newGameButton;
    }

    /**
     * Retrieves the quit game button.
     *
     * @return the LightButton quitGameButton.
     */
    public JButton getQuitGameButton() {
        return quitGameButton;
    }
}
