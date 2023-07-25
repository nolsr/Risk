package de.hsbremen.risk.common.exceptions;

public class IllegalDefendingDiceException extends Exception {
    public IllegalDefendingDiceException() {
        super("You can only defend with one or two dice!");
    }
}
