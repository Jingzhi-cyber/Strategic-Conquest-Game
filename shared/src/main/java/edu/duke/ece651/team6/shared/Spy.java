package edu.duke.ece651.team6.shared;

import java.io.Serializable;

public class Spy implements Serializable, Cloneable {
    private int ownerId;

    public Spy(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    @Override
    public Object clone() {
        return new Spy(this.ownerId);
    }

    @Override
    public String toString() {
        return "{name: Spy ownerId: " + this.ownerId + "}";
    }
}
