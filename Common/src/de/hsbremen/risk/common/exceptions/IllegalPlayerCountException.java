package de.hsbremen.risk.common.exceptions;

public class IllegalPlayerCountException extends Exception {
    public IllegalPlayerCountException() {
        super("There need to be between 3 and 6 players to start the game");
    }
}
