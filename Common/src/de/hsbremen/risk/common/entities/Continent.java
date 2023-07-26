package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Continent implements Serializable {
    @Serial
    private static final long serialVersionUID = 6433989674991593868L;

    private final String name;
    private final ArrayList<Country> countryWithin;
    private String ownedBy;

    /**
     * Create a continent with its name and the countries that are inside of it.
     *
     * @param name String of the continents name.
     * @param countryWithin ArrayList of all countries that are inside of this continent.
     */
    public Continent(String name, ArrayList<Country> countryWithin)
    {
        this.name = name;
        this.ownedBy = "";
        this.countryWithin = countryWithin;
    }

    /**
     * Get the player name that owns the continent or an empty String if no Player has that continent occupied.
     *
     * @return The name of the player that owns the continent, if this continent has not been occupied it
     * will return an empty String.
     */
    public String getOwnedBy()
    {
        return ownedBy;
    }

    /**
     * Sets the occupant of this continent with the players name.
     *
     * @param ownedBy String name of the Player that occupies this continent.
     */
    public void setOwnedBy(String ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    /**
     * Get the String name of that continent.
     *
     * @return name of the continent.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the ArrayList of all countries within the continent.
     *
     * @return ArrayList of all countries within this continent.
     */
    public ArrayList<Country> getCountriesWithin()
    {
        return countryWithin;
    }

}
