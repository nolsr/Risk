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

    public enum GameActionEventType {ATTACK, ATTACK_RESULT, MOVE, DRAW, DISTRIBUTE, TRADE}

    private Attack attack;
    private AttackResult result;
    private final GameActionEventType type;
    private final ArrayList<Player> players;
    private final ArrayList<Country> countries;

    /**
     * Initialise and set the values for the GameActionEvent with the given parameters.
     *
     * @param player Player that is passed to the GameEvent.
     * @param type Which type of GameActionEventType.
     * @param players ArrayList of all Players.
     * @param countries ArrayList of all Countries.
     */
    public GameActionEvent(Player player, GameActionEventType type,
                           ArrayList<Player> players, ArrayList<Country> countries) {
        super(player);

        this.type = type;
        this.players = players;
        this.countries = countries;
    }

    /**
     * Initialise and set the values for the GameActionEvent with the given parameters.
     *
     * @param player Player that is passed to the GameEvent.
     * @param type Which type of GameActionEventType.
     * @param players ArrayList of all Players.
     * @param countries ArrayList of all Countries.
     * @param attack current Attack.
     */
    public GameActionEvent(Player player, GameActionEventType type,
                           ArrayList<Player> players, ArrayList<Country> countries,
                           Attack attack) {
        super(player);

        this.type = type;
        this.players = players;
        this.countries = countries;
        this.attack = attack;
    }

    /**
     * Initialise and set the values for the GameActionEvent with the given parameters.
     *
     * @param player Player that is passed to the GameEvent.
     * @param type Which type of GameActionEventType.
     * @param players ArrayList of all Players.
     * @param countries ArrayList of all Countries.
     * @param attack current Attack.
     * @param result current AttackResult.
     */
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

    /**
     * Get the GameActionEventType.
     *
     * @return the set GameActionEventType.
     */
    public GameActionEventType getType() {
        return type;
    }

    /**
     * Get the ArrayList of all Players.
     *
     * @return ArrayList of all Players.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Get the ArrayList of all Countries.
     *
     * @return ArrayList of all Countries.
     */
    public ArrayList<Country> getCountries() {
        return countries;
    }

    /**
     * Get the current Attack.
     *
     * @return current Attack.
     */
    public Attack getAttack() {
        return attack;
    }

    /**
     * get the current AttackResult.
     *
     * @return current AttackResult.
     */
    public AttackResult getAttackResult() {
        return result;
    }
}
