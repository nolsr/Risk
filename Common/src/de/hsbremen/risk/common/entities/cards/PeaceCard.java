package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public class PeaceCard extends Card implements Serializable {
    @Serial
    private static final long serialVersionUID = 755798574227836547L;

    /**
     * Calls constructor from card.
     * Therefore, setting its id automatically.
     */
    public PeaceCard() {
        super();
    }

    /**
     * Manually set the PeaceCards id.
     *
     * @param id the card should receive.
     */
    public PeaceCard(int id) {
        super(id);
    }

    private final String kind = "Peace-Card";

    /**
     * Get the cards Unit.
     *
     * @return the cards Unit.
     */
    @Override
    public String getUnit() {
        return "Peace";
    }

    /**
     * Gets the Cards kind.
     *
     * @return the cards kind.
     */
    @Override
    public String getKind() {
        return kind;
    }

    /**
     * Returns the cards kind and id as String.
     *
     * @return kind and id.
     */
    @Override
    public String toString() {
        return "\nType: " + kind + "\nId: " + getId();
    }
}
