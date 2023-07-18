package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class ArmiesAlreadyMovedException extends MovementException {
    public ArmiesAlreadyMovedException(Country originCountry, Country targetCountry) {
        super(originCountry, targetCountry,
                String.format("Armies of %s have already moved this turn.", originCountry.getName()));
    }
}
