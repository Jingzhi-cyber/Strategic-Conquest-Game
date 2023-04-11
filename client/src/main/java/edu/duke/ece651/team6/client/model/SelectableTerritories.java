package edu.duke.ece651.team6.client.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.duke.ece651.team6.shared.Territory;

public class SelectableTerritories {
  private final Set<Territory> selectableTerritories;

  public SelectableTerritories(Set<Territory> territories, Territory source, String orderType) {
    selectableTerritories = new HashSet<>();
    if (orderType.equals("Move")) {
      for (Territory t : territories) {
        if (t.equals(source)) {
          continue;
        }
        if (t.getOwnerId() == source.getOwnerId() && isAdjacent(source, t)) {
          selectableTerritories.add(t);
        }
      }
    } else if (orderType.equals("Attack")) {
      for (Territory t : territories) {
        if (t.equals(source)) {
          continue;
        }
        if (t.getOwnerId() != source.getOwnerId() && isAdjacent(source, t)) {
          selectableTerritories.add(t);
        }
      }
    }
  }
  
  private boolean isAdjacent(Territory t1, Territory t2) {
    // Check if t2 is adjacent to t1
    // Implement this method based on the adjacencies information of the territories
    return false;
  }

  public Set<Territory> getSelectableTerritories() {
    return selectableTerritories;
  }
}
