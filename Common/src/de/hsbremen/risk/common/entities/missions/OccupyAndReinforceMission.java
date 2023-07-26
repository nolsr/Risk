package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class OccupyAndReinforceMission extends Mission implements Serializable {
    @Serial
    private static final long serialVersionUID = -1352681092385597724L;

    int amountOfCountriesToOccupy;
    int amountOfArmiesToBeInCountry;

    /**
     * Sets how many countries a player should occupy and how many forces should be in them.
     * to finish this Mission.
     *
     * @param playerCount Integer amount of player inside the game.
     */
    public OccupyAndReinforceMission(int playerCount) {
        this.amountOfCountriesToOccupy = 36 - (playerCount * 5);
        this.amountOfArmiesToBeInCountry = playerCount > 4 ? 3 : 2;
    }

    /**
     * Checks if the Player assigned to that mission has the required amount of countries occupied.
     * and if these countries have the required amount of armies inside of them.
     *
     * @param player Player that has the mission assigned.
     * @param countries ArrayList of all Countries.
     * @param players ArrayList of all Players.
     *
     * @return true if the conditions have been met.
     */
    @Override
    public boolean isCompleted(Player player, ArrayList<Country> countries, ArrayList<Player> players) {
        int occupiedCountries = 0;
        for (Country country : countries) {
            if (country.getOccupiedBy().equals(player.getUsername()) && country.getArmies() >= this.amountOfArmiesToBeInCountry) {
                occupiedCountries++;
            }
        }
        return occupiedCountries >= this.amountOfCountriesToOccupy;
    }

    /**
     * Return a String of how many countries to occupy and how many armies should be inside of them.
     *
     * @return String with the missions instructions.
     */
    @Override
    public String getPrintMissionString() {
        return "Occupy " + this.amountOfCountriesToOccupy + " countries and have at least " +
                this.amountOfArmiesToBeInCountry + " armies inside each country";
    }
}
