package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class GameBasicSettingTest {

  public GameBasicSetting constructGameBasicSetting() {
    Set<Territory> territories = new HashSet<Territory>() {
      {
        add(new Territory("A", 1));
        add(new Territory("B", 1));
      }
    };
    GameBasicSetting setting = new GameBasicSetting(1, 3, territories, 10);
    return setting;
  }

  @Test
  public void test_decreaseUnitsBy() {
    GameBasicSetting setting = constructGameBasicSetting();
    assertEquals(1, setting.getPlayerId());
    assertEquals(3, setting.getNumPlayers());
    assertEquals(new HashSet<Territory>() {
      {
        add(new Territory("A", 1));
        add(new Territory("B", 1));
      }
    }, setting.getAssignedTerritories());
    assertEquals(10, setting.getRemainingNumUnits());

    setting.decreaseUnitsBy(6);
    assertEquals(4, setting.getRemainingNumUnits());

    assertThrows(IllegalArgumentException.class, () -> setting.decreaseUnitsBy(5));
  }

  @Test
  public void test_initializeUnitPlacement() {
    GameBasicSetting setting = constructGameBasicSetting();
    Set<Territory> territories = setting.getAssignedTerritories();
    Map<Territory, Integer> unitsPlacement = new HashMap<Territory, Integer>();

    // assertThrows(InternalError.class, () ->
    // setting.initializeUnitPlacement(unitsPlacement));

    for (Territory t : territories) {
      unitsPlacement.put(t, 10);
    }

    setting.initializeUnitPlacement(unitsPlacement);
    assertEquals(unitsPlacement, setting.getUnitPlacement());
  }
}
