package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class DoNotOccupyCountryException extends Exception {

    public DoNotOccupyCountryException(Country country) {
        super(String.format("You are not the occupant of %s", country.getName()));
    }
}
