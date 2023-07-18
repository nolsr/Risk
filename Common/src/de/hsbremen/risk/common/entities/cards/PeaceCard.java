package de.hsbremen.risk.common.entities.cards;

public class PeaceCard extends Card
{
    public PeaceCard()
    {
        super();
    }
    public PeaceCard(int id)
    {
        super(id);
    }
    private final String kind = "Peace-Card";
    private final String unit = "Peace";
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
