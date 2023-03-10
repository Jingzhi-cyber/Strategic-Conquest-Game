package edu.duke.ece651.team6.client;

import java.io.IOException;

public interface Player {
    /**
     * Initialize the Player
     * Postcondition: if return true, the connection to the Master is established
     * @return true if success
     */
    public boolean init() throws IOException, ClassNotFoundException;

    /**
     * At the beginning of the game, Player needs to place units to the territories
     */
    public void placeUnit();

    /**
     * Player's one turn of game includes:
     * 1. Receive playerinfo from master
     * 2. Make several actions, create a commit
     * 3. Send commit to master, if the master says the commit is invalid, repeat 2
     */
    public void playOneTurn();
}
