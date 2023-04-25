package edu.duke.ece651.team6.shared;

import java.util.Random;

public class Card {
    private final String name;
    private final int code;

    private final static String[] cards = {
            "SanBing",
            "Nuclear weapon",
            "Defense infrastructure",
            "Free from attack",
            "Eliminate fog",
            "Gap generator"
    };

    private final static double[] probability = {
            0.2,
            0.01,
            0.1,
            0.2,
            0.1,
            0.1
    };

    private static double[] cumulativeProbability = null;

    public Card(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static Card chouOneCard() {
        if (cumulativeProbability == null) {
            cumulativeProbability = new double[probability.length];
            cumulativeProbability[0] = probability[0];
            for (int i = 1; i < probability.length; i++) {
                cumulativeProbability[i] = cumulativeProbability[i - 1] + probability[i];
            }
        }
        Random random = new Random();
        double p = random.nextDouble();
        for (int i = 0; i < cumulativeProbability.length; i++) {
            if (p < cumulativeProbability[i]) {
                return new Card(cards[i], i);
            }
        }
        return new Card("You bad luck! No card here! HAHAHAHAHA", -1);
    }
}
