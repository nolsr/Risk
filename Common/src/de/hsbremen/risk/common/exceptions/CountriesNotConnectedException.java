package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class CountriesNotConnectedException extends MovementException {

    public CountriesNotConnectedException(Country originCountry, Country targetCountry) {
        super(originCountry, targetCountry,
                String.format("There is no connection occupied by you between the %s and %s.",
                        originCountry.getName(), targetCountry.getName()));
    }
}
