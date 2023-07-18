package de.hsbremen.risk.client.components;

import javax.swing.*;
import java.awt.*;

public class MissionDisplay extends JPanel {
    private final JTextArea missionText;
    public MissionDisplay(String missionString) {
        super();
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(200, 100));
        this.setLayout(new BorderLayout());
        JLabel title = new JLabel("Mission");
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.add(title, BorderLayout.NORTH);
        this.missionText = new JTextArea(missionString);
        this.missionText.setForeground(Color.WHITE);
        this.missionText.setBackground(new Color(27, 31, 36));
        this.missionText.setLineWrap(true);
        this.missionText.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(90, 90, 90)));
        this.missionText.setEditable(false);
        this.add(this.missionText, BorderLayout.CENTER);
    }

    public void updateMissionText(String missionString) {
        this.missionText.setText(missionString);
    }
}
