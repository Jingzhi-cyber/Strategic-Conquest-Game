package edu.duke.ece651.team6.shared;

import java.util.Random;

public class RoundFight extends WarZone {

    public RoundFight(Territory territory) {
        super(territory);
    }

    @Override
    public void startWar() {
        if (armies.size() == 0) {
            throw new RuntimeException("Cannot start a war with no army!");
        }
        while (armies.size() > 1) {
            Army army1 = armies.pollFirst();
            Army army2 = armies.peekFirst();
            Random random = new Random();
            int d1 = random.nextInt(20);
            int d2 = random.nextInt(20);
            if (d1 > d2) {
                army2.loseOneTurn();
            } else if (d2 > d1) {
                army1.loseOneTurn();
            } else {
                if (army1.getOwnerId() == territory.getOwnerId()) {
                    army2.loseOneTurn();
                } else if (army2.getOwnerId() == territory.getOwnerId()) {
                    army1.loseOneTurn();
                }
            }
            if (!army1.hasLost()) {
                armies.addLast(army1);
            }
            if (army2.hasLost()) {
                armies.pollFirst();
            }
        }
        territory.absorbArmy(armies.pollFirst());
    }
    
}
