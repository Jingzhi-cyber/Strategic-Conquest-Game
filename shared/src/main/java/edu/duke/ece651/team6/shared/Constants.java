package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.Map;

public class Constants {
  public static final String EXIT = "Exit";

  public static final String GAME_OVER = "Game Over";

  public static final int UNITS_PER_PLAYER = 12;

  public static final String RESOURCE_FOOD = "food";
  public static final String RESOURCE_TECH = "technology";

  public static final Map<Integer, Integer> researchCosts = new HashMap<Integer, Integer>() {{
    put(1, 2);
    put(2, 3);
    put(3, 4);
    put(4, 5);
    put(5, 6);
}};
}
