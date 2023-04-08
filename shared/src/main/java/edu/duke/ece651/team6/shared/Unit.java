package edu.duke.ece651.team6.shared;

import java.io.Serializable;

/**
 * This class represents an Unit, which has level, bonus and name. There are 7 different levels, from 0-6.
 */
public class Unit implements Serializable, Cloneable {
    private int level;
    private int bonus;
    private String name;

    public Unit(int level, int bonus, String name) {
        this.level = level;
        this.bonus = bonus;
        this.name = name;
    }

    public Unit(String name) {
        this(0, 0, name);
    }

    @Override
    public Object clone() {
        try {
            Unit unit = (Unit) super.clone();
            unit.level = this.level;
            unit.bonus = this.bonus;
            unit.name = this.name;
            return unit;
        } catch (CloneNotSupportedException e) {
            return new Unit(this.level, this.bonus, this.name);
        }
    }

    public int bonus() {
        return bonus;
    }

    public int level() {
        return level;
    }

    protected void upgrade(int level, int bonus, String name) {
        this.level = level;
        this.bonus = bonus;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{name: " + name + ", level: " + level + ", bonus: " + bonus + "}";
    }

}
