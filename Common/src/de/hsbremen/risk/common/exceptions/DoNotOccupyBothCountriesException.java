package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class DoNotOccupyBothCountriesException extends MovementException {
        public DoNotOccupyBothCountriesException(Country originCountry, Country targetCountry) {
            super(originCountry, targetCountry,
                    String.format("You do not own %s and %s.", originCountry.getName(), targetCountry.getName()));
        }
}
