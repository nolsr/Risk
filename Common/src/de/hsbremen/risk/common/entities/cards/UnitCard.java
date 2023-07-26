package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public class UnitCard extends Card implements Serializable {
    @Serial
    private static final long serialVersionUID = 2217378977030364185L;

    private final String kind = "Unit-Card";
    private final String unit;
    private final String country;

    /**
     * Create a UnitCard with its unit and country.
     * The id is set automatically via the Cards constructor.
     *
     * @param unit String of its unit either infantry, artillery or cavalry.
     * @param country String of the country the card represents.
     */
    public UnitCard(String unit, String country)
    {
        super();
        this.unit = unit;
        this.country = country;
    }

    /**
     * Create a UnitCard with its unit and country.
     * The id has to be set manually.
     *
     * @param unit String of its unit either infantry, artillery or cavalry.
     * @param country String of the country the card represents.
     * @param id Integer of the card's id.
     */
    public UnitCard(String unit, String country, int id)
    {
        super(id);
        this.unit = unit;
        this.country = country;
    }

    /**
     * Gets the unit the card has.
     *
     * @return the unit of the card.
     */
    @Override
    public String getUnit() {
        return this.unit;
    }

    /**
     * Gets the Country the card represents.
     *
     * @return country the card represents.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Gets the kind of the card.
     *
     * @return the kind of the card.
     */
    @Override
    public String getKind() {
        return kind;
    }

    /**
     * Returns the cards kind, its id, its unit and the country it represents as string.
     *
     * @return kind, id, unit and country as a String.
     */
    @Override
    public String toString() {
        String s = "\nType: " + kind + "\nId: "+getId()+"\nForce: " + unit + "\nCountry: " + country;
        return s;
    }
}
