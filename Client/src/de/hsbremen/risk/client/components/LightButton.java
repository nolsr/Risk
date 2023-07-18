package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class LightButton extends JButton {
    public LightButton(String text) {
        super(text);
        this.setBackground(new Color(27, 31, 36));
        this.setForeground(Color.WHITE);
    }
}
