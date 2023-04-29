package edu.duke.ece651.team6.server;

import edu.duke.ece651.team6.shared.SocketKey;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketManager {

    private static SocketManager instance = null;
    private final Map<SocketKey, Socket> sockets;

    private final Map<SocketKey, String> socketToUsername;

    private SocketManager() {
        sockets = new HashMap<>();
        socketToUsername = new HashMap<>();
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public SocketKey add(Socket socket, String username) {
        SocketKey key = new SocketKey();
        sockets.put(key, socket);
        socketToUsername.put(key, username);
        return key;
    }

    public Socket get(SocketKey key) {
        return sockets.get(key);
    }

    public String getUsername(SocketKey key) {
        return socketToUsername.get(key);
    }

}
