package de.hsbremen.risk.client.components;

import de.hsbremen.risk.common.entities.Turn;

import javax.swing.*;
import java.awt.*;

public class CurrentTurnPanel extends JPanel {
    private Turn currentTurn;
    private WhiteLabel label;

    public CurrentTurnPanel(Turn turn) {
        this.currentTurn = turn;
        this.label = new WhiteLabel(this.currentTurn.getPlayer().getUsername() + " - " + this.currentTurn.getPhase());
        this.label.setForeground(Color.WHITE);
        this.setOpaque(false);
        this.add(this.label);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void updateTurnDisplay(Turn turn) {
        this.currentTurn = turn;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.label.setText(this.currentTurn.getPlayer().getUsername() + " - " + this.currentTurn.getPhase());
    }
}
