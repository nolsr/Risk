package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public abstract class Card implements Serializable {
    @Serial
    private static final long serialVersionUID = -5927412479572066299L;

    private static int nextId = 0;
    private final int id;

    /**
     * Automatically sets the id for each card.
     */
    public Card()
    {
        this.id = nextId++;
    }

    /**
     * For Loading cards to set their id manually.
     *
     * @param id integer id the card should receive.
     */
    public Card(int id)
    {
        this.id = id;
    }

    /**
     * Gets the cards id number.
     *
     * @return the cards id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the cards Unit.
     *
     * @return the cards Unit.
     */
    public abstract String getUnit();

    /**
     * Gets the cards kind.
     *
     * @return the card kind.
     */
    public abstract String getKind();

}
