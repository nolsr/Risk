package de.hsbremen.risk.common.exceptions;

public class GameEndedException extends Exception{
    public GameEndedException() {
        super("The Game has ended.");
    }
}
