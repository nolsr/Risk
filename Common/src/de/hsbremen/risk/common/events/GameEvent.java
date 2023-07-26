package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;

public abstract class GameEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = -4381683792211858794L;

    private Player player;

    /**
     * A Player is being passed to the GameEvent.
     *
     * @param player Player passed to the GameEvent.
     */
    public GameEvent(Player player) {
        super();
        this.player = player;
    }

    /**
     * Get the Player that has been passed to the GameEvent.
     *
     * @return Player passed to the GameEvent.
     */
    public Player getPlayer() {
        return player;
    }
}
