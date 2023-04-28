package edu.duke.ece651.team6.shared;

import java.util.Random;

public class Card {
  private final String name;
  private final int code;
  private final String description;

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

  private final static String[] texts = {
      "You can launch a team of paratroopers at a \"remote\" territory, limited by the costs associated with the attack.",
      "You can directly invade and occupy a territory of your choice.\nUpon successfully utilizing this skill, all units present in the targeted territory are eliminated, and the ownership of the territory is modified.",
      "You can modify the attack/defense costs associated with a territory.",
      "You can enhance the defense capabilities of a specific territory, so that no enemy can occupy this territory for one turn",
      "You can remove the fog of war from a chosen territory for a single turn.",
      "You can make a territory hidden from seeing by enemies even when an enemy has a spy on it"
  };

  private static double[] cumulativeProbability = null;

  public Card(String name, int code, String description) {
    this.name = name;
    this.code = code;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
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
        return new Card(cards[i], i, texts[i]);
      }
    }
    return new Card("No card here!", -1, "You bad luck! HHHHH!\nPool you. Better luck next time.");
  }
}
