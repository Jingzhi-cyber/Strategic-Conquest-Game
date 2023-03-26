package edu.duke.ece651.team6.shared;

import java.util.Random;

/**
 * A subclass of WarZone, in version 1, this is how game works that each attacker fight a round until one army left.
 */
public class RoundFight extends WarZone {

    public RoundFight(Territory territory) {
        super(territory);
    }

    @Override
    public void startWar() {
        if (armies.size() == 0) {
            throw new RuntimeException("Cannot start a war with no army!");
        }
        for (Army army : armies) {
            army.sort();
        }
        while (armies.size() > 1) {
            Army army1 = armies.pollFirst();
            Army army2 = armies.peekFirst();
            Unit strong = army1.pollLast();
            Unit weak = army2.pollFirst();
            Random random = new Random();
            int d1 = random.nextInt(20) + strong.bouns();
            int d2 = random.nextInt(20) + weak.bouns();
            if (d1 > d2) {
                weak = null;
            } else if (d2 > d1) {
                strong = null;
            } else {
                if (army1.getOwnerId() == territory.getOwnerId()) {
                    weak = null;
                } else if (army2.getOwnerId() == territory.getOwnerId()) {
                    strong = null;
                }
            }
            army1.addLast(strong);
            army2.addFirst(weak);
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
