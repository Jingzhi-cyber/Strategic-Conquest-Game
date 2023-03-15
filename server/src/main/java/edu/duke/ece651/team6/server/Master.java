package edu.duke.ece651.team6.server;

import java.io.IOException;

public interface Master {
    /**
     * Initialize the game (generate Map, connect to players, etc.)
     * Postcondition: if return true, all game data is initialized
     * @return true if initialization is successful, otherwise false
     */
    public boolean init() throws IOException;

    /**
     * Play one turn of the game, including sending players the current status,
     * receiving commits from players, check if the commits are valid, execute the
     * commits, and update the status
     * @return true if the game ends
     */
    public boolean playOneTurn() throws IOException;
}
