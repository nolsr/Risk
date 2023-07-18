package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class MovementException extends Exception {
    private final Country originCountry;
    private final Country targetCountry;

    public MovementException(Country originCountry, Country targetCountry, String message) {
        super(message);

        this.originCountry = originCountry;
        this.targetCountry = targetCountry;
    }

    public Country getOriginCountry() {
        return originCountry;
    }

    public Country getTargetCountry() {
        return targetCountry;
    }
}
