package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Continent;
import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class OccupyTwoContinentsMission extends Mission implements Serializable {
    @Serial
    private static final long serialVersionUID = 4095882400744507829L;

    Continent continentOne;
    Continent continentTwo;

    /**
     * Randomly set two continents the Player assigned to this mission should occupy.
     *
     * @param continents ArrayList of all continents.
     * @param newGame Boolean value of true if this hasn't been called from a loaded game.
     */
    public OccupyTwoContinentsMission(ArrayList<Continent> continents, boolean newGame) {
        ArrayList<Continent> shuffledContinents = new ArrayList<>(continents);
        if (newGame) {
            Collections.shuffle(shuffledContinents);
        }
        this.continentOne = shuffledContinents.get(0);
        this.continentTwo = shuffledContinents.get(1);
    }

    /**
     * Checks if the Player has occupied both assigned continents.
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
        return this.continentOne.getOwnedBy().equals(player.getUsername()) &&
                this.continentTwo.getOwnedBy().equals(player.getUsername());
    }

    /**
     * Returns a String of both continents the assigned Player should occupy.
     *
     * @return String with the missions instructions.
     */
    @Override
    public String getPrintMissionString() {
        return "Occupy the continents: " + this.continentOne.getName() + " and " + this.continentTwo.getName();
    }
}
