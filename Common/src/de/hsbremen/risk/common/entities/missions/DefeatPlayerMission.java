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

    @Override
    public String getPrintMissionString() {
        return "Defeat: " + this.playerToDefeat.getUsername();
    }
}
