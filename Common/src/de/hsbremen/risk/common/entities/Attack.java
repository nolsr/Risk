package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;

public class Attack implements Serializable {
    @Serial
    private static final long serialVersionUID = 8659647285194270403L;

    private Player attackingPlayer;
    private Player defendingPlayer;
    private int originCountry;
    private int targetCountry;
    private int amount;
    private boolean isRematch;

    public Attack(Player attackingPlayer) {
        this.attackingPlayer = attackingPlayer;
        this.originCountry = -1;
        this.targetCountry = -1;
        this.amount = -1;
        this.isRematch = false;
    }

    public void reset() {
        this.originCountry = -1;
        this.targetCountry = -1;
        this.amount = -1;
        this.isRematch = false;
    }

    public boolean hasOriginCountry() {
        return this.originCountry != -1;
    }

    public int getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(int originCountry) {
        this.originCountry = originCountry;
    }

    public int getTargetCountry() {
        return targetCountry;
    }

    public void setTargetCountry(int targetCountry) {
        this.targetCountry = targetCountry;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Player getDefendingPlayer() {
        return defendingPlayer;
    }

    public void setDefendingPlayer(Player defendingPlayer) {
        this.defendingPlayer = defendingPlayer;
    }

    public Player getAttackingPlayer() {
        return attackingPlayer;
    }
}
