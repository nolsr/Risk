package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Country;

public class OccupyTargetCountry extends Exception {
    public OccupyTargetCountry(Country country) {
        super(String.format("%s is your own country", country.getName()));
    }
}
