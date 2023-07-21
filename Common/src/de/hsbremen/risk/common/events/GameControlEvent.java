package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Turn;

import java.io.Serial;

public class GameControlEvent extends GameEvent {
    @Serial
    private static final long serialVersionUID = -5597502502428648233L;

    public enum GameControlEventType { GAME_STARTED, NEXT_PHASE, GAME_OVER }

    private final GameControlEventType type;
    private final Turn turn;

    public GameControlEvent(Turn turn, GameControlEventType type) {
        super(turn.getPlayer());

        this.turn = turn;
        this.type = type;
    }

    public GameControlEventType getType() {
        return type;
    }

    public Turn getTurn() {
        return turn;
    }
}
