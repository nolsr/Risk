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

    /**
     * Initialise and set the all values for the GameControlEvent with the given parameters.
     *
     * @param turn The current Turn.
     * @param type Which type of GameControlEventType.
     * @param countries ArrayList of all Countries
     */
    public GameControlEvent(Turn turn, GameControlEventType type, ArrayList<Country> countries) {
        super(turn.getPlayer());

        this.turn = turn;
        this.type = type;
        this.countries = countries;
    }

    /**
     * Get the GameControlEventType.
     *
     * @return the set GameControlEventType.
     */
    public GameControlEventType getType() {
        return type;
    }

    /**
     * Get the current Turn.
     *
     * @return current Turn.
     */
    public Turn getTurn() {
        return turn;
    }

    /**
     * Get the ArrayList of all Countries.
     *
     * @return ArrayList of all Countries.
     */
    public ArrayList<Country> getCountries() {
        return countries;
    }
}
