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

    public OccupyAndReinforceMission(int playerCount) {
        this.amountOfCountriesToOccupy = 36 - (playerCount * 5);
        this.amountOfArmiesToBeInCountry = playerCount > 4 ? 3 : 2;
    }

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

    @Override
    public String getPrintMissionString() {
        return "Occupy " + this.amountOfCountriesToOccupy + " countries and have at least " +
                this.amountOfArmiesToBeInCountry + " armies inside each country";
    }
}
