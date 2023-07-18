package de.hsbremen.risk.client.components;

import de.hsbremen.risk.common.entities.Turn;

import javax.swing.*;
import java.awt.*;

public class PhaseInformation extends JPanel {
    private final JLabel title;
    private final JTextArea informationText;
    private Turn currentTurn;

    public PhaseInformation(Turn turn) {
        super();
        this.currentTurn = turn;
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(200, 100));
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));


        this.title = new JLabel("");
        this.title.setForeground(Color.WHITE);
        this.title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        this.informationText = new JTextArea("");
        this.informationText.setForeground(Color.WHITE);
        this.informationText.setBackground(new Color(27, 31, 36));
        this.informationText.setLineWrap(true);
        this.informationText.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(90, 90, 90)));
        this.informationText.setEditable(false);

        updatePhaseInformation(turn);

        this.add(this.title, BorderLayout.NORTH);
        this.add(this.informationText, BorderLayout.CENTER);
    }

    public void updateTitle(Turn.Phase phase) {
        this.title.setText(phase.toString());
    }

    public void updatePhaseInformation(Turn turn) {
        updateTitle(turn.getPhase());
        this.currentTurn = turn;
        switch(turn.getPhase()) {
            case REINFORCEMENT_PHASE -> this.informationText.setText("You have " + turn.getPlayer().getArmies() + " units left to place.");
            case LIBERATION_PHASE -> this.informationText.setText("Liberate a country occupied by somebody else.");
            case MOVEMENT_PHASE -> this.informationText.setText("Move units between your countries.");
            case DRAWING_PHASE -> this.informationText.setText("If you liberated at least one country this turn, you can draw a card.");
        }
    }
}
