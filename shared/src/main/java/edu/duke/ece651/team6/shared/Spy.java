package edu.duke.ece651.team6.shared;

import java.io.Serializable;

public class Spy implements Serializable, Cloneable {
    private int ownerId;
    private boolean canMove;

    public Spy(int ownerId) {
        this.ownerId = ownerId;
        this.canMove = true;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public boolean canMove() {
        return this.canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    @Override
    public Object clone() {
        Spy spy = new Spy(this.ownerId);
        spy.setCanMove(this.canMove);
        return spy;
    }

    @Override
    public String toString() {
        return "{name: Spy ownerId: " + this.ownerId + "}";
    }
}
