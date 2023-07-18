package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.util.ArrayList;

public class OccupyCountriesMission extends Mission {
    int amountOfCountriesToOccupy;
    public OccupyCountriesMission(int playerCount) {
        this.amountOfCountriesToOccupy = 36 - (playerCount * 4);
    }

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

    @Override
    public String getPrintMissionString() {
        return "Occupy " + this.amountOfCountriesToOccupy + " countries";
    }
}
