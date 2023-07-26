package de.hsbremen.risk.client.components;

import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.entities.Turn;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class InGameControlPanel extends JPanel {
    private final DarkButton btnNextPhase;
    private final DarkButton btnAction;
    private final DarkButton btnSave;
    private final DarkButton btnCardStack;
    private final MissionDisplay missionDisplay;
    private final PhaseInformation phaseInformation;
    private final DarkButton btnTradeCards;

    BufferedImage bufferedCardStack;

    /**
     * InGameControlPanel constructor displays the player's missions, cards and action.
     * Additionally, the panel will be formatted to fit our game theme.
     *
     * @param turn object of the current turn
     * @param player player Object of the client
     */
    public InGameControlPanel(Turn turn, Player player) {
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
        this.missionDisplay = new MissionDisplay(player.getMissionString());
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

    /**
     * Setting and displaying the action buttons depending on the phase.
     *
     * @param turn object of the current turn to check phase
     */
    public void setNewPhaseContent(Turn turn) {
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

    /**
     * Setting buttons to be clickable.
     */
    public void enableControls() {
        this.btnAction.setEnabled(true);
        this.btnTradeCards.setEnabled(true);
        this.btnNextPhase.setEnabled(true);
        this.phaseInformation.setVisible(true);
    }

    /**
     * Disables the buttons clickable attribute.
     */
    public void disableControls() {
        this.btnAction.setEnabled(false);
        this.btnTradeCards.setEnabled(false);
        this.btnNextPhase.setEnabled(false);
        this.phaseInformation.setVisible(false);
    }

    /**
     * Retrieves the save button.
     *
     * @return returns the DarkButton btnSave.
     */
    public DarkButton getBtnSave() {
        return btnSave;
    }

    /**
     * Retrieves the next phase button.
     *
     * @return returns the DarkButton btnNextPhase.
     */
    public DarkButton getBtnNextPhase() {
        return btnNextPhase;
    }

    /**
     * Retrieves the action button.
     *
     * @return returns the DarkButton btnAction.
     */
    public DarkButton getBtnAction() {
        return btnAction;
    }

    /**
     * Retrieves the trade cards button.
     *
     * @return returns the DarkButton btnTradeCards.
     */
    public DarkButton getBtnTradeCards() {
        return btnTradeCards;
    }

    /**
     * Retrieves the trade card stack button .
     *
     * @return returns the DarkButton btnCardStack.
     */
    public DarkButton getBtnCardStack() {
        return btnCardStack;
    }
}
