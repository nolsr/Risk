package de.hsbremen.risk.common.exceptions;

public class NotEnoughPlayersException extends Exception {
    public NotEnoughPlayersException() {
        super("There need to be at least 3 players to start the game");
    }
}
