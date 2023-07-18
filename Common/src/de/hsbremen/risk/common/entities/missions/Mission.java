package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.util.ArrayList;

public abstract class Mission {
     public abstract boolean isCompleted(Player player, ArrayList<Country> countries, ArrayList<Player> players);

     public abstract String getPrintMissionString();
}
