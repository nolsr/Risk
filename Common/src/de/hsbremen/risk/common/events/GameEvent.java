package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.io.Serializable;

public abstract class GameEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = -4381683792211858794L;

    private Player player;

    public GameEvent(Player player) {
        super();
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
