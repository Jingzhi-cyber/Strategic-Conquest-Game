package edu.duke.ece651.team6.shared;

import java.util.HashSet;

public class Result implements java.io.Serializable {
    HashSet<Integer> winners;
    HashSet<Integer> losers;

    public Result() {
        winners = new HashSet<Integer>();
        losers = new HashSet<Integer>();
    }

    /**
     * Add a winner
     * @param playerId
     */
    public void addWinner(int playerId) {
        winners.add(playerId);
    }

    /**
     * Add a loser
     * @param playerId
     */
    public void addLoser(int playerId) {
        losers.add(playerId);
    }

    /**
     * Get Set of winners
     * @return winners
     */
    public HashSet<Integer> getWinners() {
        return this.winners;
    }

    /**
     * Get Set of losers
     * @return losers
     */
    public HashSet<Integer> getLosers() {
        return this.losers;
    }
}
