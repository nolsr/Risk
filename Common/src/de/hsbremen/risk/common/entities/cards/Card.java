package de.hsbremen.risk.common.entities.cards;

public abstract class Card {
    private static int nextId = 0;
    private final int id;

    public Card()
    {
        this.id = nextId++;
    }

    public Card(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
    public abstract String getUnit();

    public abstract String getKind();

}
