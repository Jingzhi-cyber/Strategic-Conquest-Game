package edu.duke.ece651.team6.server;

import java.io.IOException;

public interface Master {
    /**
     * Initialize network connection to all players
     */
    public void init() throws IOException;

    /**
     * Play one turn of the game, including sending players the current status,
     * receiving commits from players, check if the commits are valid, execute the
     * commits, and update the status
     * @return true if the game ends
     */
    public boolean playOneTurn() throws IOException;
}
