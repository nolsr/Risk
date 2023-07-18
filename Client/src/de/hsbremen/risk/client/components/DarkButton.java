package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class DarkButton extends JButton {

    public DarkButton(String text) {
        super(text);
        this.setBackground(new Color(18, 20, 24));
        this.setForeground(Color.WHITE);
        this.setFocusPainted(false);
    }

    public DarkButton(Icon icon) {
        super(icon);
        this.setBackground(new Color(18, 20, 24));
        this.setBorder(null);
    }
}
