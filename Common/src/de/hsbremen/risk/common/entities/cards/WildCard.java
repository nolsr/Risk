package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public class WildCard extends Card implements Serializable {
    @Serial
    private static final long serialVersionUID = -3202430453623944592L;

    /**
     * Calls constructor from card.
     * Therefore, setting its id automatically.
     */
    public WildCard()
    {
        super();
    }

    /**
     * Manually set the WildCards id.
     *
     * @param id the card should receive.
     */
    public WildCard(int id)
    {
        super(id);
    }

    private final String kind = "Wild-Card";
    private final String unit = "Wild";

    /**
     * Get the cards Unit.
     *
     * @return the cards Unit.
     */
    @Override
    public String getUnit() {
        return this.unit;
    }

    /**
     * Gets the Cards kind.
     *
     * @return the cards kind.
     */
    @Override
    public String getKind()
    {
        return kind;
    }

    /**
     * Returns the cards kind and id as string.
     *
     * @return kind and id.
     */
    @Override
    public String toString() {
        String s = "\nType: " + kind + "\nId: "+getId();
        return s;
    }
}
