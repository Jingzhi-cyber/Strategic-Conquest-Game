package edu.duke.ece651.team6.shared;

import java.io.Serializable;

/**
 * This class represents an Unit, which has level, bonus and name. There are 7 different levels, from 0-6.
 */
public class Unit implements Serializable {
    private int level;
    private int bouns;
    private String name;

    public Unit(int level, int bouns, String name) {
        this.level = level;
        this.bouns = bouns;
        this.name = name;
    }

    public Unit(String name) {
        this(0, 0, name);
    }

    public int bouns() {
        return bouns;
    }

    public int level() {
        return level;
    }

    protected void upgrade(int level, int bouns, String name) {
        this.level = level;
        this.bouns = bouns;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{name: " + name + ", level: " + level + ", bonus: " + bouns + "}";
    }

}
