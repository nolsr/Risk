package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class DarkList<S> extends JList<S> {

    /**
     * DarkList Constructor to set the JList matching the game theme
     */
    public DarkList() {
        super();
        this.setBackground(new Color(18, 20, 24) );
        this.setForeground(Color.WHITE);
        this.setOpaque(false);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) this.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        this.setFont(this.getFont().deriveFont(22.0f));
    }
}
