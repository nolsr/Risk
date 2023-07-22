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
    public OccupyTwoContinentsMission(ArrayList<Continent> continents, boolean newGame) {
        ArrayList<Continent> shuffledContinents = new ArrayList<>(continents);
        if (newGame) {
            Collections.shuffle(shuffledContinents);
        }
        this.continentOne = shuffledContinents.get(0);
        this.continentTwo = shuffledContinents.get(1);
    }

    @Override
    public boolean isCompleted(Player player, ArrayList<Country> countries,
                               ArrayList<Player> players) {
        return this.continentOne.getOwnedBy().equals(player.getUsername()) &&
                this.continentTwo.getOwnedBy().equals(player.getUsername());
    }

    @Override
    public String getPrintMissionString() {
        return "Occupy the continents: " + this.continentOne.getName() + " and " + this.continentTwo.getName();
    }
}
