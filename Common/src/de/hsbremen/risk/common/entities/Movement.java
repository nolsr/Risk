package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;

public class Movement implements Serializable {
    @Serial
    private static final long serialVersionUID = 206943351872184514L;

    private int originCountry;
    private int targetCountry;

    public Movement() {
        this.originCountry = -1;
        this.targetCountry = -1;
    }

    public void reset() {
        this.originCountry = -1;
        this.targetCountry = -1;
    }

    public boolean hasOriginCountry() {
        return this.originCountry != -1;
    }

    public int getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(int originCountry) {
        this.originCountry = originCountry;
    }

    public int getTargetCountry() {
        return targetCountry;
    }

    public void setTargetCountry(int targetCountry) {
        this.targetCountry = targetCountry;
    }
}
