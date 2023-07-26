package de.hsbremen.risk.common.exceptions;


public class NotEntitledToDrawCardException extends Exception{
    public NotEntitledToDrawCardException() {
        super("You're not entitled to draw a card.");
    }

}
