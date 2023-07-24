package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public abstract class Card implements Serializable {
    @Serial
    private static final long serialVersionUID = -5927412479572066299L;

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
