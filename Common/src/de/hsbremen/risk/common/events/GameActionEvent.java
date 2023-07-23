package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.util.ArrayList;

public class GameActionEvent extends GameEvent {
    @Serial
    private static final long serialVersionUID = 1177860608466305601L;

    public enum GameActionEventType { ATTACK, DEFEND, MOVE, DRAW, DISTRIBUTE }

    private final GameActionEventType type;
    private final ArrayList<Player> players;
    private final ArrayList<Country> countries;

    public GameActionEvent(Player player, GameActionEventType type, ArrayList<Player> players, ArrayList<Country> countries) {
        super(player);

        this.type = type;
        this.players = players;
        this.countries = countries;
    }

    public GameActionEventType getType() {
        return type;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }
}
