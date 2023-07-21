package de.hsbremen.risk.common.entities.cards;

import java.io.Serial;
import java.io.Serializable;

public class WildCard extends Card implements Serializable {
    @Serial
    private static final long serialVersionUID = -3202430453623944592L;

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
