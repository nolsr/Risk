package de.hsbremen.risk.client;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.util.ArrayList;

public class RiskMapPrinter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private ArrayList<Country> countries;
    private ArrayList<Player> players;

    public void printMap(ArrayList<Player> players, ArrayList<Country> countries) {
        this.countries = countries;
        this.players = players;

        System.out.print("\n\nPlayers: \t\t\n");
        for (int i = 0; i < players.size(); i++) {
            printInColor(players.get(i).getUsername() + "     ", generateColorFromIndex(i));
        }
        System.out.println("\n\nNorth America\t\t\t\tSouth America\t\tEurope" +
                "\t\t\t\t\t\tAfrica\t\t\t\t\t\tAsia\t\t\t\t\tAustralia");
        printCountry(0); tabs(4); printCountry(9); tabs(1);  printCountry(13); tabs(3);
        printCountry(20); tabs(2); printCountry(26); tabs(2); printCountry(38, true);
        printCountry(1); tabs(1); printCountry(10); tabs(2); printCountry(14); tabs(2);
        printCountry(21); tabs(4); printCountry(27); tabs(3); printCountry(39, true);
        printCountry(2); tabs(3); printCountry(11); tabs(2); printCountry(15); tabs(2);
        printCountry(22); tabs(4); printCountry(28); tabs(2); printCountry(40, true);
        printCountry(3); tabs(4); printCountry(12); tabs(1); printCountry(16); tabs(1);
        printCountry(23); tabs(2); printCountry(29); tabs(2); printCountry(41, true);
        printCountry(4); tabs(9); printCountry(17); tabs(3); printCountry(24);
        tabs(2); printCountry(30, true);
        printCountry(5); tabs(9); printCountry(18); tabs(2); printCountry(25);
        tabs(3); printCountry(31, true);
        printCountry(6); tabs(8); printCountry(19); tabs(8); printCountry(32, true);
        printCountry(7); tabs(22); printCountry(33, true);
        printCountry(8); tabs(21); printCountry(34, true);
        tabs(26); printCountry(35, true);
        tabs(26); printCountry(36, true);
        tabs(26); printCountry(37, true);

        System.out.println();
    }

    private void tabs(int amount) {
        System.out.print("\t".repeat(Math.max(0, amount)));
    }

    private void printCountry(int id) {
        printCountry(id, false);
    }
    private void printCountry(int id, boolean lineBreak) {
        Country c = this.countries.get(id);
        printInColor("[" + id + "] " + c.getName() + " (" + c.getArmies() + ")", generateColorFromIndex(getOccupantIndex(id)), lineBreak);
    }

    private int getOccupantIndex(int countryId) {
        String occupant = this.countries.get(countryId).getOccupiedBy();
        for (int i = 0; i < players.size(); i++) {
            if (this.players.get(i).getUsername().equals(occupant)) return i;
        }
        return 6;
    }

    private String generateColorFromIndex(int index) {
        String color = "";
        switch (index) {
            case 0 -> color = "red";
            case 1 -> color = "green";
            case 2 -> color = "yellow";
            case 3 -> color = "blue";
            case 4 -> color = "purple";
            case 5 -> color = "cyan";
        }
        return color;
    }

    private void printInColor(String message, String color) {
        printInColor(message, color, false);
    }
    private void printInColor(String message, String color, boolean lineBreak) {
        String ansiColor = "";
        switch (color) {
            case "red" -> ansiColor = ANSI_RED;
            case "green" -> ansiColor = ANSI_GREEN;
            case "yellow" -> ansiColor = ANSI_YELLOW;
            case "blue" -> ansiColor = ANSI_BLUE;
            case "purple" -> ansiColor = ANSI_PURPLE;
            case "cyan" -> ansiColor = ANSI_CYAN;
        }
        System.out.print(ansiColor + message + ANSI_RESET + (lineBreak ? "\n" : ""));
    }
}
