package de.hsbremen.risk.common.entities.cards;

public class UnitCard extends Card {

    private final String kind = "Unit-Card";
    private final String unit;
    private final String country;

    public UnitCard(String unit, String country)
    {
        super();
        this.unit = unit;
        this.country = country;
    }
    public UnitCard(String unit, String country, int id)
    {
        super(id);
        this.unit = unit;
        this.country = country;
    }
    @Override
    public String getUnit() {
        return this.unit;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String toString() {
        String s = "\nType: " + kind + "\nId: "+getId()+"\nForce: " + unit + "\nCountry: " + country;
        return s;
    }
}
