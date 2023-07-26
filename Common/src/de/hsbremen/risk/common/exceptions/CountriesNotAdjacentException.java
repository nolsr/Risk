package de.hsbremen.risk.common.exceptions;

public class CountriesNotAdjacentException extends Exception {
    public CountriesNotAdjacentException() {
        super("The countries are not adjacent to one another");
    }
}
