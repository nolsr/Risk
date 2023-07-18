package de.hsbremen.risk.common.entities;

import java.util.ArrayList;

public class Continent
{
    private final String name;
    private final ArrayList<Country> countryWithin;
    private String ownedBy;

    public Continent(String name, ArrayList<Country> countryWithin)
    {
        this.name = name;
        this.ownedBy = "";
        this.countryWithin = countryWithin;
    }

    public String getOwnedBy()
    {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<Country> getCountriesWithin()
    {
        return countryWithin;
    }

}
