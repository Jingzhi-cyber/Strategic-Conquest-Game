package edu.duke.ece651.team6.client;

import java.util.Map;
import java.util.Set;

import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

/**
 * Display the map in the text format
 */
public class MapTextView {

  final GlobalMapInfo globalMapInfo;

  /**
   * Constructs {@link MapTextView} with 1 param
   * 
   * @param globalMapInfo contains all global information that is necessary to
   *                      know on the client side
   */
  public MapTextView(GlobalMapInfo globalMapInfo) {
    this.globalMapInfo = globalMapInfo;
  }

  /**
   * Display the map by returning the text format of map in a string
   * 
   * @return {@link String}
   */
  public String display() {
    Set<Integer> playerIds = globalMapInfo.getPlayers();
    StringBuilder ans = new StringBuilder();
    for (int playerId : playerIds) {
      PlayerMapInfo playerMapInfo = globalMapInfo.getPlayerMapInfo(playerId);
      ans.append("Player" + playerMapInfo.getPlayerId() + ":\n");
      if (playerMapInfo.getTerritories().isEmpty()) {
        ans.append("-------------\n" + "This player does not own any territory\n\n");
      } else {
        ans.append("-------------\n");
        Set<Territory> territories = playerMapInfo.getTerritories();
        for (Territory t : territories) {
          ans.append("In " + t.getName() + ": \n");
          Map<Territory, Integer> neighbors = playerMapInfo.getTerritoryNeighbors(t);
          ans.append("next to: \n");
          for (Territory neighbor : neighbors.keySet()) {
            ans.append(neighbor.getName() + " " + "Distance: " + neighbors.get(neighbor) + "\n");
          }
          ans.append("\n");
          for (int level = 0; level < t.getNumLevels(); level++) {
            ans.append("level " + level + ": " + t.getUnitsNumByLevel(level) + " units\n");
          }
        }
      }
    }
    return ans.toString();
  }
}
