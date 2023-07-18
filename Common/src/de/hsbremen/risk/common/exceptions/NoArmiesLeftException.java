package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class NoArmiesLeftException extends MovementException {
    public NoArmiesLeftException(Country originCountry, Country targetCountry) {
        super(originCountry, targetCountry,
                String.format("You need to leave at least one army in %s.", originCountry.getName()));
    }
}
