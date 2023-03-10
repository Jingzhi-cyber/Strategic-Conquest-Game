package edu.duke.ece651.team6.client;
import edu.duke.ece651.team6.shared.PlayerMapInfo;

public abstract class MapView {
    protected PlayerMapInfo playerMapInfo;

    /**
     * Construct MapView with a PlayerMapInfo
     * @param playerMapInfo
     */
    public MapView(PlayerMapInfo playerMapInfo) {
        this.playerMapInfo = playerMapInfo;
    }

    /**
     * Display the Player Map
     * @return the display
     */
    public abstract String display();
}
