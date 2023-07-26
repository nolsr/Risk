package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Mission implements Serializable {
     @Serial
     private static final long serialVersionUID = 5894325215668056581L;

     /**
      * Should return a boolean value of true if a player has completed his or her mission.
      *
      * @param player Player that has the mission assigned.
      * @param countries ArrayList of all Countries.
      * @param players ArrayList of all Players.
      *
      * @return True only if the player completed the Mission.
      */
     public abstract boolean isCompleted(Player player, ArrayList<Country> countries, ArrayList<Player> players);

     /**
      * Return the String of the kind of mission the player has to fulfill.
      *
      * @return String with the missions instructions.
      */
     public abstract String getPrintMissionString();
}
