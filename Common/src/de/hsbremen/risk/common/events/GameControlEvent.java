package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Turn;

import java.io.Serial;
import java.util.ArrayList;

public class GameControlEvent extends GameEvent {
    @Serial
    private static final long serialVersionUID = -5597502502428648233L;

    public enum GameControlEventType { GAME_STARTED, NEXT_PHASE, GAME_OVER}

    private final GameControlEventType type;
    private final Turn turn;
    private final ArrayList<Country> countries;

    public GameControlEvent(Turn turn, GameControlEventType type, ArrayList<Country> countries) {
        super(turn.getPlayer());

        this.turn = turn;
        this.type = type;
        this.countries = countries;
    }

    public GameControlEventType getType() {
        return type;
    }

    public Turn getTurn() {
        return turn;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }
}
