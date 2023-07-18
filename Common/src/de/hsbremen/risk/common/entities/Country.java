package de.hsbremen.risk.common.entities;

public class Country
{
    private final int adjacencyId;
    private final String name;
    private int armies;
    private final String continent;
    private String occupiedBy;
    private boolean unitsMoved;

    public Country(int adjacencyId, String name, String shortname, String continent)
    {
        this.adjacencyId = adjacencyId;
        this.name = name;
        this.continent = continent;
        this.occupiedBy = "";
        this.armies = 0;
        this.unitsMoved = false;
    }

    public boolean getUnitsMoved() {
        return this.unitsMoved;
    }

    public void setUnitsMoved(boolean value) {
        this.unitsMoved = value;
    }

    public int getAdjacencyId() {
        return adjacencyId;
    }

    public String getName() {
        return name;
    }

    public int getArmies() {
        return armies;
    }

    public void setArmies(int armies) {
        this.armies = armies;
    }

    public String getContinent() {
        return continent;
    }

    public String getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(String occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    public void decreaseArmy(int amount)
    {
        this.armies = this.armies - amount;
    }
    public void increaseArmy(int amount)
    {
        this.armies = this.armies + amount;
    }
}
