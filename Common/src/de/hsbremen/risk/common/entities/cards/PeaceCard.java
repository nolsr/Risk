package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public class PeaceCard extends Card implements Serializable {
    @Serial
    private static final long serialVersionUID = 755798574227836547L;

    public PeaceCard() {
        super();
    }

    public PeaceCard(int id) {
        super(id);
    }

    private final String kind = "Peace-Card";

    @Override
    public String getUnit() {
        return "Peace";
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return "\nType: " + kind + "\nId: " + getId();
    }
}
