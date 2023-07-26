package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class DarkButton extends JButton {

    /**
     * DarkButton Constructor to set the JButton matching the game theme
     * @param text text which will be displayed on the button
     */
    public DarkButton(String text) {
        super(text);
        this.setBackground(new Color(18, 20, 24));
        this.setForeground(Color.WHITE);
        this.setFocusPainted(false);
    }

    /**
     * Set DarkButton values
     * @param icon icon which will be displayed on the button
     */
    public DarkButton(Icon icon) {
        super(icon);
        this.setBackground(new Color(18, 20, 24));
        this.setBorder(null);
    }
}
