package edu.duke.ece651.team6.shared;

public class Territory implements java.io.Serializable {
    private final String name;
    // FIXME: owner should be a Player class
    private String owner;
    private int units;

    /**
     * Constructs a territory with default name, that does not have owner and any units
     */
    public Territory() {
        this.name = "Default Territory";
        this.owner = null;
        this.units = 0;
    }

    /**
     * Constructs a territory that has a specified name, with owner and some units
     * @param name the name of territory
     * @param owner the owner of territory
     * @param units the number of units on the territory
     */
    public Territory(String name, String owner, int units) {
        this.name = name;
        if (units < 0) {
            throw new IllegalArgumentException("Territory's unit must be non-negative but is " + units);
        }
        this.owner = owner;
        this.units = units;
    }

    /**
     * Get the territory's name
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the territory's owner
     * @return the owner
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Get the territory's units
     * @return the number of units
     */
    public int getUnits() {
        return this.units;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            Territory t = (Territory) o;
            return t.name.equals(name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(name: " + name + ", owner: " + owner + ", units: " + units + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
