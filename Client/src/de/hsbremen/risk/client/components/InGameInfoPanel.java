package de.hsbremen.risk.client.components;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.entities.Turn;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InGameInfoPanel extends JPanel {

    private final JLabel playerName;
    private final JLabel countryName = new WhiteLabel("");

    private final JLabel countryContinent = new WhiteLabel("");;
    private final JLabel countryUnits = new WhiteLabel("");;
    private final JLabel countryOccupiedBy = new WhiteLabel("");;

    public InGameInfoPanel(ArrayList<Player> players, Player player) {
        super();

        playerName = new WhiteLabel(player.getUsername());
        DarkList<String> playerList = new DarkList<>();
        DefaultListModel<String> playerListModel = new DefaultListModel<>();
        playerList.setModel(playerListModel);
        playerList.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(90, 90, 90)));
        playerList.setFixedCellWidth(200);
        playerList.setCellRenderer(new ColorfulListCellRenderer());
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setSelectionBackground(playerList.getBackground());
        playerList.setSelectionForeground(playerList.getBackground());

        for (Player p : players) {
            playerListModel.addElement(p.getUsername());
        }

        this.setPreferredSize(new Dimension(250, 1920));
        this.setBackground(new Color(27, 31, 36));
        this.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(90, 90, 90)));
        this.setLayout(new FlowLayout());

        countryName.setPreferredSize(new Dimension(200, 25));
        countryContinent.setPreferredSize(new Dimension(200, 25));
        countryUnits.setPreferredSize(new Dimension(200, 25));
        countryOccupiedBy.setPreferredSize(new Dimension(200, 25));

        this.add(playerName);
        this.add(playerList);
        this.add(countryName);
        this.add(countryContinent);
        this.add(countryUnits);
        this.add(countryOccupiedBy);
    }

    public void updateInfoPanel(Country country) {
        countryName.setText(country.getName());
        countryContinent.setText("Continent: " + country.getContinent());
        countryUnits.setText("Amount of units placed: " + country.getArmies());
        countryOccupiedBy.setText("Occupied by: " + country.getOccupiedBy());
    }

    public String showCurrentPlayerTurn(Turn turn, JLabel label) {
        return "";
    }

    private class ColorfulListCellRenderer extends DefaultListCellRenderer {

        private final Color[] colors = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.PINK, Color.CYAN};

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (index >= 0 && index < colors.length) {
                renderer.setForeground(colors[index]);
            }

            return renderer;
        }
    }
}
