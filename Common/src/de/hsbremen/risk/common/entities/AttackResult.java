package de.hsbremen.risk.common.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AttackResult {
    private ArrayList<Integer> attackingRolls;
    private ArrayList<Integer> defendingRolls;
    private int winningAttackingDice;
    private int winningDefendingDice;
    private boolean hasBeenResolved;
    private boolean attackerWon;

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

    private void evaluateDices() {
        int minSize = Math.min(attackingRolls.size(), defendingRolls.size());

        for (int i = 0; i < minSize; i++) {
            if (attackingRolls.get(i) > defendingRolls.get(i)) {
                winningAttackingDice++;
            } else if (attackingRolls.get(i) < defendingRolls.get(i)) {
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

    public void setHasBeenResolved(boolean hasBeenResolved) {
        this.hasBeenResolved = hasBeenResolved;
    }

    public boolean hasBeenResolved() {
        return hasBeenResolved;
    }

    public int getWinningAttackingDice() {
        return winningAttackingDice;
    }

    public int getWinningDefendingDice() {
        return winningDefendingDice;
    }

    public ArrayList<Integer> getAttackingRolls() {
        return attackingRolls;
    }

    public ArrayList<Integer> getDefendingRolls() {
        return defendingRolls;
    }

    public boolean hasAttackerWon() {
        return attackerWon;
    }

    public void setAttackerWon(boolean attackerWon) {
        this.attackerWon = attackerWon;
    }
}
