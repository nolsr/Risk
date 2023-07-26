package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;

public class Attack implements Serializable {
    @Serial
    private static final long serialVersionUID = 8659647285194270403L;

    private final Player attackingPlayer;
    private Player defendingPlayer;
    private int originCountry;
    private int targetCountry;
    private int amount;

    /**
     * Initializes a new attack.
     *
     * @param attackingPlayer Player object of the player attacking.
     */
    public Attack(Player attackingPlayer) {
        this.attackingPlayer = attackingPlayer;
        this.originCountry = -1;
        this.targetCountry = -1;
        this.amount = -1;
    }

    /**
     * Resets the attack object to its initial state.
     */
    public void reset() {
        this.originCountry = -1;
        this.targetCountry = -1;
        this.amount = -1;
    }

    /**
     * Checks if the attack has an origin country set.
     *
     * @return A Boolean value whether the attack has an origin country or not.
     */
    public boolean hasOriginCountry() {
        return this.originCountry != -1;
    }

    /**
     * Retrieves the ID of the origin country of the attack.
     *
     * @return An Integer of the ID of the attacks origin country.
     */
    public int getOriginCountry() {
        return originCountry;
    }

    /**
     * Sets the origin country of the attack.
     *
     * @param originCountry ID of the origin country that should be set for the attack.
     */
    public void setOriginCountry(int originCountry) {
        this.originCountry = originCountry;
    }

    /**
     * Retrieves the ID of the target country of the attack.
     *
     * @return An Integer of the ID of the attacks target country.
     */
    public int getTargetCountry() {
        return targetCountry;
    }

    /**
     * Sets the target country of the attack.
     *
     * @param targetCountry ID of the target country that should be set for the attack.
     */
    public void setTargetCountry(int targetCountry) {
        this.targetCountry = targetCountry;
    }

    /**
     * Retrieves the amount of units used by the attacker for the attack.
     *
     * @return An Integer of the amount of units used for the attack.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of units used by the attacker for the attack.
     *
     * @param amount Integer representing the amount of units that shall be used.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Retrieves the player occupying the target country of the attack.
     *
     * @return Player object of the occupant of the attacks target country.
     */
    public Player getDefendingPlayer() {
        return defendingPlayer;
    }

    /**
     * Sets the player defending the attack.
     *
     * @param defendingPlayer Player object of the defending player.
     */
    public void setDefendingPlayer(Player defendingPlayer) {
        this.defendingPlayer = defendingPlayer;
    }

    /**
     * Retrieves the player occupying the origin country of the attack.
     *
     * @return Player object of the attacking player.
     */
    public Player getAttackingPlayer() {
        return attackingPlayer;
    }
}
