package de.hsbremen.risk.common.entities;

public class Turn {

    public enum Phase {
        REINFORCEMENT_PHASE,
        LIBERATION_PHASE, // aka Attack Phase, Optional
        MOVEMENT_PHASE, // Optional
        DRAWING_PHASE, // If liberated one or more countries
        GAME_ENDED;

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

    public Turn(Player player) {
        this.player = player;
        this.phase = Phase.REINFORCEMENT_PHASE;
    }

    public void nextPhase() {
        switch (phase) {
            case REINFORCEMENT_PHASE -> this.phase = Phase.LIBERATION_PHASE;
            case LIBERATION_PHASE -> this.phase = Phase.MOVEMENT_PHASE;
            case MOVEMENT_PHASE -> this.phase = Phase.DRAWING_PHASE;
        }
    }

    public void setGameEnded() {
        this.phase = Phase.GAME_ENDED;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public void setPhase(Phase phase){
        this.phase = phase;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
