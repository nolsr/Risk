package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AttackResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 528324944731843347L;

    private final ArrayList<Integer> attackingRolls;
    private final ArrayList<Integer> defendingRolls;
    private int winningAttackingDice;
    private int winningDefendingDice;
    private boolean hasBeenResolved;
    private boolean attackerWon;

    /**
     * Initializes an AttackResult object.
     *
     * @param attackingRolls Amount of dice used by the attacker.
     * @param defendingRolls Amount of dice used by the defendant.
     */
    public AttackResult(int attackingRolls, int defendingRolls) {
        this.attackingRolls = new ArrayList<>();
        this.defendingRolls = new ArrayList<>();
        this.winningAttackingDice = 0;
        this.winningDefendingDice = 0;
        this.hasBeenResolved = false;

        // Rolling results
        for (int i = 0; i < attackingRolls; i++) {
            this.attackingRolls.add(new Random().nextInt(6) + 1);
        }
        for (int i = 0; i < defendingRolls; i++) {
            this.defendingRolls.add(new Random().nextInt(6) + 1);
        }
        this.attackingRolls.sort(Collections.reverseOrder());
        this.defendingRolls.sort(Collections.reverseOrder());

        this.evaluateDices();
    }

    /**
     * Evaluates the dice rolls and sets the result
     */
    private void evaluateDices() {
        int minSize = Math.min(attackingRolls.size(), defendingRolls.size());

        for (int i = 0; i < minSize; i++) {
            if (attackingRolls.get(i) > defendingRolls.get(i)) {
                winningAttackingDice++;
            } else if (attackingRolls.get(i) <= defendingRolls.get(i)) {
                winningDefendingDice++;
            }
        }
        if (attackingRolls.size() > minSize) {
            int remainingAttackerDice = attackingRolls.size() - minSize;
            winningAttackingDice += remainingAttackerDice;
        } else if (defendingRolls.size() > minSize) {
            int remainingDefenderDice = defendingRolls.size() - minSize;
            winningDefendingDice += remainingDefenderDice;
        }
    }

    /**
     * Sets whether or not the attack has been resolved or has to continue.
     *
     * @param hasBeenResolved Boolean value if the fight has been resolved.
     */
    public void setHasBeenResolved(boolean hasBeenResolved) {
        this.hasBeenResolved = hasBeenResolved;
    }

    /**
     * Retrieves if the attack has been resolved.
     *
     * @return A Boolean value representing whether or not the attack has been resolved.
     */
    public boolean hasBeenResolved() {
        return hasBeenResolved;
    }

    /**
     * Retrieves the amount of winning dice of the attacker.
     *
     * @return An Integer of the amount of winning dice of the attacker.
     */
    public int getWinningAttackingDice() {
        return winningAttackingDice;
    }

    /**
     * Retrieves the amount of winning dice of the defendant.
     *
     * @return An Integer of the amount of winning dice of the defendant.
     */
    public int getWinningDefendingDice() {
        return winningDefendingDice;
    }

    /**
     * Retrieves the dice results of the attacking rolls.
     *
     * @return An ArrayList containing the Integers of the attackers dice results.
     */
    public ArrayList<Integer> getAttackingRolls() {
        return attackingRolls;
    }


    /**
     * Retrieves the dice results of the defending rolls.
     *
     * @return An ArrayList containing the Integers of the defendants dice results.
     */
    public ArrayList<Integer> getDefendingRolls() {
        return defendingRolls;
    }

    /**
     * Retrieves whether or not the attack was successful.
     *
     * @return A Boolean value representing whether or not the attack was successful.
     */
    public boolean hasAttackerWon() {
        return attackerWon;
    }

    /**
     * Sets whether or not the attack was successful.
     *
     * @param attackerWon Boolean representing whether or not the attack was successful.
     */
    public void setAttackerWon(boolean attackerWon) {
        this.attackerWon = attackerWon;
    }
}
