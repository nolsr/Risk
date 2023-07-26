package de.hsbremen.risk.common.entities;

        import java.io.Serial;
        import java.io.Serializable;

public class Country implements Serializable {
    @Serial
    private static final long serialVersionUID = -1590791042578738044L;

    private final int adjacencyId;
    private final String name;
    private int armies;
    private final String continent;
    private String occupiedBy;
    private boolean unitsMoved;

    /**
     * Create a country with its adjacencyId, name and the continent it is part of.
     *
     * @param adjacencyId Integer of its adjacencyId.
     * @param name String name of the country.
     * @param continent String of the continent it is part of.
     */
    public Country(int adjacencyId, String name, String continent)
    {
        this.adjacencyId = adjacencyId;
        this.name = name;
        this.continent = continent;
        this.occupiedBy = "";
        this.armies = 0;
        this.unitsMoved = false;
    }

    /**
     * Returns the boolean value that indicates the units inside of this country have been moved.
     *
     * @return boolean value if the units have been moved.
     */
    public boolean getUnitsMoved() {
        return this.unitsMoved;
    }

    /**
     * Set unitMoved to true if units have been moved inside a turn.
     * Set it back to false if a turn has ended.
     *
     * @param value Boolean true if units have been moved in a players turn, set it back to false if the turn
     *              has ended.
     */
    public void setUnitsMoved(boolean value) {
        this.unitsMoved = value;
    }

    /**
     * Get the adjacencyId of the country.
     *
     * @return Integer of the adjacencyId.
     */
    public int getAdjacencyId() {
        return adjacencyId;
    }

    /**
     * Get the String name of the country.
     *
     * @return String of the name the country has.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets number of armies the country currently has inside of it.
     *
     * @return Integer of the amount of armies inside the country.
     */
    public int getArmies() {
        return armies;
    }

    /**
     * Set the amount of armies inside the country.
     *
     * @param armies Integer amount to be set inside the country.
     */
    public void setArmies(int armies) {
        this.armies = armies;
    }

    /**
     * Get the String name of the continent the country is part of.
     *
     * @return continent String name.
     */
    public String getContinent() {
        return continent;
    }

    /**
     * Get the String name of the player that occupies this country.
     *
     * @return name of the player occupying this country.
     */
    public String getOccupiedBy() {
        return occupiedBy;
    }

    /**
     * Set the String name of the player that took over this country.
     *
     * @param occupiedBy String name of the player that took over that country.
     */
    public void setOccupiedBy(String occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    /**
     * Decrease the countries armies by the Integer amount.
     *
     * @param amount Integer amount the army should be decreased by.
     */
    public void decreaseArmy(int amount)
    {
        this.armies = this.armies - amount;
    }
}

