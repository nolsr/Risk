package de.hsbremen.risk.common.exceptions;

public class UnplacedArmiesException extends Exception {
    public UnplacedArmiesException() {
        super(String.format("You have not placed all of your armies"));
    }
}
