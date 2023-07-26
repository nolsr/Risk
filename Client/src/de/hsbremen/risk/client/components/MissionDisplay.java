package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class MissionDisplay extends JPanel {
    /**
     * A display of the players respective mission.
     *
     * @param missionString Text of the mission to be accomplished.
     */
    public MissionDisplay(String missionString) {
        super();
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(200, 100));
        this.setLayout(new BorderLayout());
        JLabel title = new JLabel("Mission");
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.add(title, BorderLayout.NORTH);
        JTextArea missionText = new JTextArea(missionString);
        missionText.setForeground(Color.WHITE);
        missionText.setBackground(new Color(27, 31, 36));
        missionText.setLineWrap(true);
        missionText.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(90, 90, 90)));
        missionText.setEditable(false);
        this.add(missionText, BorderLayout.CENTER);
    }
}
