package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;

public class Turn implements Serializable {
    @Serial
    private static final long serialVersionUID = -6464158444415807231L;

    public enum Phase {
        REINFORCEMENT_PHASE,
        LIBERATION_PHASE, // aka Attack Phase, Optional
        MOVEMENT_PHASE, // Optional
        DRAWING_PHASE, // If liberated one or more countries
        GAME_ENDED;

        /**
         * Turns the Phase into a String.
         *
         * @return the String representation of the Phase.
         */
        @Override
        public String toString() {
            return switch (this.ordinal()) {
                case 0 -> "Reinforcement Phase";
                case 1 -> "Liberation Phase";
                case 2 -> "Movement Phase";
                case 3 -> "Drawing Phase";
                case 4 -> "Game Ended";
                default -> "";
            };
        }

        /**
         * Turn the String representation of the Phase back to a Phase.
         *
         * @param phaseString String representation of the Phase.
         * @return the Phase the String represented
         */
        public static Phase getPhaseFromString(String phaseString) {
            return switch (phaseString) {
                case "Reinforcement Phase" -> REINFORCEMENT_PHASE;
                case "Liberation Phase" -> LIBERATION_PHASE;
                case "Movement Phase" -> MOVEMENT_PHASE;
                case "Drawing Phase" -> DRAWING_PHASE;
                default -> GAME_ENDED;
            };
        }
    }

    private Player player;
    private Phase phase;

    /**
     * Initialise a Turn with the Turns Player in the REINFORCEMENT_PHASE.
     *
     * @param player the rounds beginning Player.
     */
    public Turn(Player player) {
        this.player = player;
        this.phase = Phase.REINFORCEMENT_PHASE;
    }

    /**
     * Switch to the next Phase
     *
     */
    public void nextPhase() {
        switch (phase) {
            case REINFORCEMENT_PHASE -> this.phase = Phase.LIBERATION_PHASE;
            case LIBERATION_PHASE -> this.phase = Phase.MOVEMENT_PHASE;
            case MOVEMENT_PHASE -> this.phase = Phase.DRAWING_PHASE;
        }
    }

    /**
     * Set the Phase to GAME_ENDED.
     */
    public void setGameEnded() {
        this.phase = Phase.GAME_ENDED;
    }

    /**
     * Get the Turns current Player.
     *
     * @return the current turns Player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the Current Phase.
     * @return Current Phase.
     */
    public Phase getPhase() {
        return this.phase;
    }

    /**
     * Set the Turns Phase.
     *
     * @param phase Phase the Turn should change to.
     */
    public void setPhase(Phase phase){
        this.phase = phase;
    }

    /**
     * Set the Turns current Player.
     *
     * @param player Player that should be set to the current turn
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
}
