package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class MovementBetweenSameCountriesException extends MovementException {
    public MovementBetweenSameCountriesException(Country originCountry, Country targetCountry) {
        super(originCountry, targetCountry, "Origin country and target country can not be the same.");
    }
}
