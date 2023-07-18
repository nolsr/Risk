package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class WhiteLabel extends JLabel {
    public WhiteLabel(String label) {
        super(label);
        this.setForeground(Color.WHITE);
        this.setFont(getFont().deriveFont(16.0f));
    }
}
