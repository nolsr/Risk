package de.hsbremen.risk.client.components;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.MapEventListener;
import de.hsbremen.risk.common.entities.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RiskMap extends JPanel {
    BufferedImage bufferedMap;
    BufferedImage bufferedMapGreyscale;
    private static final double ASPECT_RATIO = 16.0 / 9.0;

    private JPanel countryPanel;

    private double scaling;
    private double offsetX;
    private double offsetY;

    private ArrayList<Player> players;
    private ArrayList<Country> countries;

    public RiskMap(ArrayList<Player> players, ArrayList<Country> countries) {
        super();
        this.setOpaque(false);

        try {
            this.bufferedMap = ImageIO.read(new File("assets/MapColored.png"));
            this.bufferedMapGreyscale = ImageIO.read(new File("assets/RiskMapGreyScale.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.players = players;
        this.countries = countries;

        setLayout(null);
        this.countryPanel = new JPanel();
        this.countryPanel.setLayout(null);
        this.countryPanel.setOpaque(false);
        this.countryPanel.setBounds(0, 0, getWidth(), getHeight());
        add(this.countryPanel);
    }

    public void addCountryClickedListener(MapEventListener listener) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                double x = e.getX() - getOffsetX();
                double y = e.getY() - getOffsetY();
                int unscaledX = (int) Math.floor(x / getScaling());
                double unscaledY = (int) Math.floor(y / getScaling());
                if (unscaledX < 0 || unscaledX >= 1920 || unscaledY < 0 || unscaledY >= 1080) {
                    return;
                }

                int translatedX = (int) Math.floor((e.getX() - getOffsetX()) / getScaling());
                int translatedY = (int) Math.floor((e.getY() - getOffsetY()) / getScaling());

                int pixel = getBufferedMapGreyscale().getRGB(translatedX, translatedY);
                int clickedCountryIndex = ((pixel >> 16) & 0xFF) - 100;
                if (clickedCountryIndex != 155) {
                    try {
                        listener.onCountryClicked(clickedCountryIndex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getScaling() {
        return scaling;
    }

    public BufferedImage getBufferedMapGreyscale() {
        return bufferedMapGreyscale;
    }

    public void updateCountryInfo(ArrayList<Player> players, ArrayList<Country> countries) {
        this.players = players;
        this.countries = countries;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int calculatedWidth = (int) (height * ASPECT_RATIO);
        int calculatedHeight = (int) (width / ASPECT_RATIO);
        int x = 0;
        int y = 0;

        if (calculatedWidth > width) {
            calculatedWidth = width;
            y = (height - (int) (calculatedWidth / ASPECT_RATIO)) / 2;
        } else {
            calculatedHeight = height;
            x = (width - (int) (calculatedHeight * ASPECT_RATIO)) / 2;
        }
        this.scaling = height == calculatedHeight ? (double) calculatedWidth / 1920 : (double) calculatedHeight / 1080;

        this.offsetX = x;
        this.offsetY = y;

        this.countryPanel.setBounds(0, 0, width, height);
        paintCountries(x, y);

        Image scaledImage = bufferedMap.getScaledInstance(calculatedWidth, calculatedHeight, Image.SCALE_SMOOTH);
        g.drawImage(scaledImage, x, y, null);
    }

    private void paintCountries(int offsetX, int offsetY) {
        this.countryPanel.removeAll();

        // North America
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 135, 162, this.scaling, getArmies(0), getCountryColor(0)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 310, 160, this.scaling, getArmies(1), getCountryColor(1)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 689, 122, this.scaling, getArmies(2), getCountryColor(2)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 297, 250, this.scaling, getArmies(3), getCountryColor(3)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 425, 260, this.scaling, getArmies(4), getCountryColor(4)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 543, 268, this.scaling, getArmies(5), getCountryColor(5)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 302, 365, this.scaling, getArmies(6), getCountryColor(6)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 440, 402, this.scaling, getArmies(7), getCountryColor(7)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 322, 510, this.scaling, getArmies(8), getCountryColor(8)));

        // South America
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 441, 605, this.scaling, getArmies(9), getCountryColor(9)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 608, 715, this.scaling, getArmies(10), getCountryColor(10)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 481, 751, this.scaling, getArmies(11), getCountryColor(11)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 482, 906, this.scaling, getArmies(12), getCountryColor(12)));

        // Europe
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 829, 211, this.scaling, getArmies(13), getCountryColor(13)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 975, 204, this.scaling, getArmies(14), getCountryColor(14)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 808, 342, this.scaling, getArmies(15), getCountryColor(15)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 965, 360, this.scaling, getArmies(16), getCountryColor(16)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1150, 289, this.scaling, getArmies(17), getCountryColor(17)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 820, 509, this.scaling, getArmies(18), getCountryColor(18)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 978, 458, this.scaling, getArmies(19), getCountryColor(19)));

        // Africa
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 900, 674, this.scaling, getArmies(20), getCountryColor(20)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1042, 618, this.scaling, getArmies(21), getCountryColor(21)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1047, 811, this.scaling, getArmies(22), getCountryColor(22)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1117, 712, this.scaling, getArmies(23), getCountryColor(23)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1065, 955, this.scaling, getArmies(24), getCountryColor(24)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1225, 959, this.scaling, getArmies(25), getCountryColor(25)));

        // Asia
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1576, 137, this.scaling, getArmies(26), getCountryColor(26)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1334, 255, this.scaling, getArmies(27), getCountryColor(27)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1445, 199, this.scaling, getArmies(28), getCountryColor(28)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1554, 270, this.scaling, getArmies(29), getCountryColor(29)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1723, 145, this.scaling, getArmies(30), getCountryColor(30)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1307, 407, this.scaling, getArmies(31), getCountryColor(31)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1522, 482, this.scaling, getArmies(32), getCountryColor(32)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1572, 369, this.scaling, getArmies(33), getCountryColor(33)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1774, 389, this.scaling, getArmies(34), getCountryColor(34)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1183, 549, this.scaling, getArmies(35), getCountryColor(35)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1406, 561, this.scaling, getArmies(36), getCountryColor(36)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1564, 608, this.scaling, getArmies(37), getCountryColor(37)));

        // Australia
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1576, 792, this.scaling, getArmies(38), getCountryColor(38)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1743, 751, this.scaling, getArmies(39), getCountryColor(39)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1656, 952, this.scaling, getArmies(40), getCountryColor(40)));
        this.countryPanel.add(new MapCountryCircle(offsetX, offsetY, 1831, 955, this.scaling, getArmies(41), getCountryColor(41)));
    }

    private int getArmies(int countryId) {
        return this.countries.get(countryId).getArmies();
    }

    private Color getCountryColor(int countryId) {
        String occupant = this.countries.get(countryId).getOccupiedBy();
        int playerId = 6;
        for (int i = 0; i < players.size(); i++) {
            if (this.players.get(i).getUsername().equals(occupant)) playerId = i;
        }
        Color c = Color.WHITE;
        switch (playerId) {
            case 0 -> c = Color.RED;
            case 1 -> c = Color.GREEN;
            case 2 -> c = Color.YELLOW;
            case 3 -> c = Color.BLUE;
            case 4 -> c = Color.PINK;
            case 5 -> c = Color.CYAN;
        }
        return c;
    }
}
