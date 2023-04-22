package edu.duke.ece651.team6.shared;

public class PlayerProfile implements java.io.Serializable {
    private final int id;
    private String name;
    private transient SocketKey socketKey;

    /**
     * Construct PlayerProfile with an unique id
     * @param id
     */
    public PlayerProfile(int id, String name) {
        this.id = id;
        this.name = name;
        this.socketKey = null;
    }

    /**
     * Set name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set socket
     * @param socket
     */
    public void setSocket(SocketKey key) {
        this.socketKey = key;
    }

    /**
     * Get id
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Get name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get socket
     * @return
     */
    public SocketKey getSocket() {
        return socketKey;
    }
}
