package de.hsbremen.risk.common.entities;

public class Movement {
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
