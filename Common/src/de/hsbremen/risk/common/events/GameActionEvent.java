package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;

public class GameActionEvent extends GameEvent {
    @Serial
    private static final long serialVersionUID = 1177860608466305601L;

    public enum GameControlEventType { ATTACK, DEFEND, MOVE, DRAW, DISTRIBUTE };

    private GameControlEventType type;

    public GameActionEvent(Player player, GameControlEventType type) {
        super(player);

        this.type = type;
    }

    public GameControlEventType getType() {
        return type;
    }
}
