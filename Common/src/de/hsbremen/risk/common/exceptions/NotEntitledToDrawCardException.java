package de.hsbremen.risk.common.exceptions;

/**
 * @author Raphael Tam-Dao
 */

public class NotEntitledToDrawCardException extends Exception{
    public NotEntitledToDrawCardException() {
        super("You're not entitled to draw a card.");
    }

}
