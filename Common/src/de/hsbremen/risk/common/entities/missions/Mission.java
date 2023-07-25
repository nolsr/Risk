package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Mission implements Serializable {
     @Serial
     private static final long serialVersionUID = 5894325215668056581L;

     public abstract boolean isCompleted(Player player, ArrayList<Country> countries, ArrayList<Player> players);

     public abstract String getPrintMissionString();
}
