package edu.duke.ece651.team6.client;
import edu.duke.ece651.team6.shared.GlobalMapInfo;


public abstract class MapView {
    protected final GlobalMapInfo globalMapInfo;

    /**
     * Construct MapView with a PlayerMapInfo
     * @param globalMapInfo
     */
    public MapView(GlobalMapInfo globalMapInfo) {
        this.globalMapInfo = globalMapInfo;
    }

    /**
     * Display the Player Map
     * @return the display
     */
    public abstract String display();
}
