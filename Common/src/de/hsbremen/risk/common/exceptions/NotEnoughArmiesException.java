package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

public class NotEnoughArmiesException extends Exception {

    public NotEnoughArmiesException() {
        super(String.format("You do not have enough armies"));
    }
}
