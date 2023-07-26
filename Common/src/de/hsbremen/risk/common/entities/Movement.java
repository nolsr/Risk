package de.hsbremen.risk.common.entities;

import java.io.Serial;
import java.io.Serializable;

public class Movement implements Serializable {
    @Serial
    private static final long serialVersionUID = 206943351872184514L;

    private int originCountry;
    private int targetCountry;

    /**
     * Initialises the origin country and target country with -1.
     */
    public Movement() {
        this.originCountry = -1;
        this.targetCountry = -1;
    }

    /**
     * Resets the origin country id and target country id with -1.
     */
    public void reset() {
        this.originCountry = -1;
        this.targetCountry = -1;
    }

    /**
     * If an origin country hasn't already been set it will return false, otherwise
     * it will return true.
     *
     * @return false if origin country hasn't been set yet.
     */
    public boolean hasOriginCountry() {
        return this.originCountry != -1;
    }

    /**
     * Get the id of the origin country.
     *
     * @return Integer id of the origin country.
     */
    public int getOriginCountry() {
        return originCountry;
    }

    /**
     * Set the origin countries id.
     *
     * @param originCountry Integer id of the origin country
     */
    public void setOriginCountry(int originCountry) {
        this.originCountry = originCountry;
    }

    /**
     * Get the id of the target country.
     *
     * @return Integer id of the target country.
     */
    public int getTargetCountry() {
        return targetCountry;
    }

    /**
     * Set the target countries id.
     *
     * @param targetCountry Integer id of the target country.
     */
    public void setTargetCountry(int targetCountry) {
        this.targetCountry = targetCountry;
    }
}
