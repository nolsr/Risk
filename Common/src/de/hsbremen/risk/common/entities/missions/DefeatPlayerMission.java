package de.hsbremen.risk.common.entities.missions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class DefeatPlayerMission extends Mission implements Serializable {
    @Serial
    private static final long serialVersionUID = -3875751244870118905L;

    private final Player playerToDefeat;

    /**
     * Randomly sets the Players target he or she has to defeat.
     *
     * @param missionsPlayer Player the mission is going tp be assigned to.
     * @param playerList ArrayList of all Players.
     * @param newGame Boolean True if it's not a loaded game.
     * @param targetPlayer Player that shall be defeated, only effective if it is a loaded game.
     */
    public DefeatPlayerMission(Player missionsPlayer, ArrayList<Player> playerList, boolean newGame, Player targetPlayer) {
        ArrayList<Player> shuffledPlayers = new ArrayList<>(playerList);
        if (newGame) {
            Collections.shuffle(shuffledPlayers);
            this.playerToDefeat = shuffledPlayers.get(0).getUsername().equals(missionsPlayer.getUsername()) ?
                    shuffledPlayers.get(1) : shuffledPlayers.get(0);
        } else {
            this.playerToDefeat = targetPlayer;
        }
    }

    /**
     * Check if the target Player has been defeated and returns true if so.
     *
     * @param player Player that has the mission assigned.
     * @param countries ArrayList of all Countries.
     * @param players ArrayList of all Players.
     *
     * @return true if the target Player doesn't own any countries, otherwise false.
     */
    @Override
    public boolean isCompleted(Player player, ArrayList<Country> countries,
                               ArrayList<Player> players) {
        for (Country country : countries) {
            if (country.getOccupiedBy().equals(this.playerToDefeat.getUsername())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the instructions of whom to defeat as a String.
     *
     * @return String with the missions instructions.
     */
    @Override
    public String getPrintMissionString() {
        return "Defeat: " + this.playerToDefeat.getUsername();
    }
}
