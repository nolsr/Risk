package de.hsbremen.risk.common.entities.cards;

public class WildCard extends Card
{
    public WildCard()
    {
        super();
    }
    public WildCard(int id)
    {
        super(id);
    }

    private final String kind = "Wild-Card";
    private final String unit = "Wild";
    @Override
    public String getUnit() {
        return this.unit;
    }
    @Override
    public String getKind()
    {
        return kind;
    }

    @Override
    public String toString() {
        String s = "\nType: " + kind + "\nId: "+getId();
        return s;
    }
}
