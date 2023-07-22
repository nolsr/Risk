package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class DarkLog extends JTextArea {

    public DarkLog() {
        super();
        this.setForeground(Color.WHITE);
        this.setFont(this.getFont().deriveFont(15.0f));
        this.setEditable(false);
        this.setOpaque(false);
    }
}
