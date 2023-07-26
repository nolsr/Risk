package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;

public class DarkLog extends JTextArea {

    /**
     * DarkLog Constructor to set the JTextArea matching the game theme.
     */
    public DarkLog() {
        super();
        this.setForeground(Color.WHITE);
        this.setFont(this.getFont().deriveFont(18.0f));
        this.setEditable(false);
        this.setOpaque(false);
    }
}
