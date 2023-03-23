package edu.duke.ece651.team6.client;

import java.util.HashSet;
import java.util.Set;

import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

/**
 * Display the map in the text format
 */
public class MapTextView extends MapView {

  /**
   * Constructs {@link MapTextView} with 1 param
   * 
   * @param globalMapInfo contains all global information that is necessary to
   *                      know on the client side
   */
  public MapTextView(GlobalMapInfo globalMapInfo) {
    super(globalMapInfo);
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
          ans.append(t.getNumUnits() + " units in " + t.getName() + " ");
          HashSet<String> neighbors = playerMapInfo.getTerritoryNeighbors(t);
          ans.append("(next to: ");
          for (String neighbor : neighbors) {
            ans.append(neighbor + " ");
          }
          ans.deleteCharAt(ans.length() - 1);
          ans.append(")\n\n");
        }
      }
    }
    return ans.toString();
  }
}
