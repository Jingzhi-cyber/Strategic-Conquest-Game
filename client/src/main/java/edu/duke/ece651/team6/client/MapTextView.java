package edu.duke.ece651.team6.client;

import java.util.*;

import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

public class MapTextView extends MapView {

    /**
     * {@inheritDoc}
     * @param playerMapInfo
     */
    public MapTextView(PlayerMapInfo playerMapInfo) {
        super(playerMapInfo);
    }

    /**
     * {@inheritDoc}
     */
    public String display() {
        StringBuilder ans = new StringBuilder("-------------\n");
        Set<Territory> territories = playerMapInfo.getTerritories();
        for (Territory t : territories) {
            ans.append(t.getNumUnits() + " units in " + t.getName() + " ");
            HashSet<String> neighbors = playerMapInfo.getTerritoryNeighbors(t);
            ans.append("(next to: ");
            for (String neighbor : neighbors) {
                ans.append(neighbor + " ");
            }
            ans.append(")\n");
        }
        return ans.toString();
    }

}
