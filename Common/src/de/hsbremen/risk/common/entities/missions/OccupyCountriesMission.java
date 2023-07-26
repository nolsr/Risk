package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class OccupyCountriesMission extends Mission implements Serializable {
    @Serial
    private static final long serialVersionUID = 8034919529414135908L;

    int amountOfCountriesToOccupy;

    /**
     * Sets how many countries a player should occupy.
     *
     * @param playerCount Integer amount of player inside the game.
     */
    public OccupyCountriesMission(int playerCount) {
        this.amountOfCountriesToOccupy = 36 - (playerCount * 4);
    }

    /**
     * Checks if the Player assigned to that mission has the required amount of countries occupied.
     *
     * @param player Player that has the mission assigned.
     * @param countries ArrayList of all Countries.
     * @param players ArrayList of all Players.
     *
     * @return true if the conditions have been met.
     */
    @Override
    public boolean isCompleted(Player player, ArrayList<Country> countries,
                               ArrayList<Player> players) {
        int occupiedCountries = 0;
        for (Country country : countries) {
            if (country.getOccupiedBy().equals(player.getUsername())) {
                occupiedCountries++;
            }
        }
        return occupiedCountries >= this.amountOfCountriesToOccupy;
    }

    /**
     * Return a String of how many countries the Player should occupy.
     *
     * @return String with the missions instructions.
     */
    @Override
    public String getPrintMissionString() {
        return "Occupy " + this.amountOfCountriesToOccupy + " countries";
    }
}
