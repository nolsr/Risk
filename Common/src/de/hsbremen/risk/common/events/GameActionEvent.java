package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Attack;
import de.hsbremen.risk.common.entities.AttackResult;
import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.io.Serial;
import java.util.ArrayList;

public class GameActionEvent extends GameEvent {
    @Serial
    private static final long serialVersionUID = 1177860608466305601L;

    public enum GameActionEventType {ATTACK, ATTACK_RESULT, MOVE, DRAW, DISTRIBUTE}

    private Attack attack;
    private AttackResult result;
    private final GameActionEventType type;
    private final ArrayList<Player> players;
    private final ArrayList<Country> countries;

    public GameActionEvent(Player player, GameActionEventType type,
                           ArrayList<Player> players, ArrayList<Country> countries) {
        super(player);

        this.type = type;
        this.players = players;
        this.countries = countries;
    }

    public GameActionEvent(Player player, GameActionEventType type,
                           ArrayList<Player> players, ArrayList<Country> countries,
                           Attack attack) {
        super(player);

        this.type = type;
        this.players = players;
        this.countries = countries;
        this.attack = attack;
    }


    public GameActionEvent(Player player, GameActionEventType type,
                           ArrayList<Player> players, ArrayList<Country> countries,
                           Attack attack, AttackResult result) {
        super(player);

        this.type = type;
        this.players = players;
        this.countries = countries;
        this.attack = attack;
        this.result = result;
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

    public Attack getAttack() {
        return attack;
    }

    public AttackResult getAttackResult() {
        return result;
    }
}
