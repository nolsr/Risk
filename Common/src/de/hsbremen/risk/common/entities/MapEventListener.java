package de.hsbremen.risk.common.entities;


import java.io.IOException;

public interface MapEventListener {
    void onCountryClicked(Integer countryId) throws IOException;
}
