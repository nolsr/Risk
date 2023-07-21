package de.hsbremen.risk.client.components;

import de.hsbremen.risk.common.entities.Turn;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class InGameControlPanel extends JPanel {
    private DarkButton btnNextPhase;
    private final DarkButton btnAction;
    private final DarkButton btnSave;
    private DarkButton btnCardStack;
    private final MissionDisplay missionDisplay;
    private final PhaseInformation phaseInformation;
    private DarkButton btnTradeCards;



    BufferedImage bufferedCardStack;

    public InGameControlPanel(Turn turn) {
        super();
        try {
            this.bufferedCardStack = ImageIO.read(new File("assets/CardStack.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setPreferredSize(new Dimension(1080, 150));
        this.setBackground(new Color(27, 31, 36));
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(90, 90, 90)));
        this.setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setLayout(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        this.add(left, BorderLayout.WEST);
        JPanel right = new JPanel();
        right.setLayout(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.add(right, BorderLayout.EAST);
        this.btnTradeCards = new DarkButton("Trade Cards");
        this.btnTradeCards.setPreferredSize(new Dimension(150, 100));
        this.btnSave = new DarkButton("Save Game");
        this.btnSave.setPreferredSize(new Dimension(150, 100));
        this.btnNextPhase = new DarkButton("Next Phase");
        this.btnNextPhase.setPreferredSize(new Dimension(150, 100));
        this.btnAction = new DarkButton("Action");
        this.btnAction.setPreferredSize(new Dimension(150, 100));
        Image scaledImage = bufferedCardStack.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        this.btnCardStack = new DarkButton(new ImageIcon(scaledImage));
        this.btnCardStack.setOpaque(false);
        this.missionDisplay = new MissionDisplay(turn.getPlayer().getMissionString());
        this.phaseInformation = new PhaseInformation(turn);

        left.add(btnCardStack);
        left.add(missionDisplay);
        left.add(phaseInformation);
        right.add(btnSave);
        right.add(btnTradeCards);
        this.btnTradeCards.setVisible(false);
        right.add(btnAction);
        right.add(btnNextPhase);

        this.setNewPhaseContent(turn);
    }

    public void setNewPhaseContent(Turn turn) {
        this.missionDisplay.updateMissionText(turn.getPlayer().getMissionString());
        this.phaseInformation.updatePhaseInformation(turn);
        switch (turn.getPhase()) {
            case REINFORCEMENT_PHASE -> {
                this.btnTradeCards.setVisible(true);
                this.btnAction.setText("Place Units");
            }
            case LIBERATION_PHASE -> {
                this.btnTradeCards.setVisible(false);
                this.btnAction.setText("Attack");
            }
            case MOVEMENT_PHASE -> this.btnAction.setText("Move Units");
            case DRAWING_PHASE -> this.btnAction.setText("Draw Card");
        }
    }

    public void enableControls() {
        this.btnAction.setEnabled(true);
        this.btnTradeCards.setEnabled(true);
        this.btnNextPhase.setEnabled(true);
    }

    public void disableControls() {
        this.btnAction.setEnabled(false);
        this.btnTradeCards.setEnabled(false);
        this.btnNextPhase.setEnabled(false);
    }

    public DarkButton getBtnSave() {
        return btnSave;
    }

    public DarkButton getBtnNextPhase() {
        return btnNextPhase;
    }

    public DarkButton getBtnAction() {
        return btnAction;
    }

    public DarkButton getBtnTradeCards() {
        return btnTradeCards;
    }

    public DarkButton getBtnCardStack() {
        return btnCardStack;
    }
}
