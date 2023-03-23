package edu.duke.ece651.team6.shared;
import java.net.Socket;

public class PlayerProfile implements java.io.Serializable {
    private final int id;
    private String name;
    private transient Socket socket;

    /**
     * Construct PlayerProfile with an unique id
     * @param id
     */
    public PlayerProfile(int id) {
        this.id = id;
        this.name = null;
        this.socket = null;
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
    public void setSocket(Socket socket) {
        this.socket = socket;
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
    public Socket getSocket() {
        return socket;
    }
}
