package edu.duke.ece651.team6.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.duke.ece651.team6.shared.SocketKey;

/**
 * This class is a singelton. This class can manage all the user with their sockets. 
 * All the send or read operations related to socket should get socket from this class.
 * When adding a new socket into this class, a SocketKey will be returned, which can be 
 * used to get Socket from this class.
 */
public class AccountManager {

    private static volatile AccountManager instance;

    private final Map<SocketKey, Socket> sockets;
    private final Map<SocketKey, String> keyToUser;
    private final Map<String, Set<SocketKey>> userList;

    private final Map<String, String> password;

    private AccountManager() {
        sockets = new HashMap<>();
        keyToUser = new HashMap<>();
        userList = new HashMap<>();
        password = new HashMap<>();
    }

    public static AccountManager getInstance() {
        if (instance == null) {
            synchronized (AccountManager.class) {
                if (instance == null) {
                    instance = new AccountManager();
                }
            }
        }
        return instance;
    }

    public Socket getSocket(SocketKey key) {
        return sockets.get(key);
    }

    /**
     * Add a new Socket, bind it to a specific user.
     * @param username
     * @param socket
     * @return  A SocketKey, which can be used to identify the socket.
     */
    public SocketKey add(String username, Socket socket) {
        SocketKey key = new SocketKey();
        if (!userList.containsKey(username)) {
            userList.put(username, new HashSet<>());
        }
        userList.get(username).add(key);
        sockets.put(key, socket);
        keyToUser.put(key, username);
        return key;
    }

    /**
     * Remove a socket.
     * @param key
     */
    public void remove(SocketKey key) {
        if (!sockets.containsKey(key)) {
            return;
        }
        sockets.remove(key);
        String username = keyToUser.get(key);
        userList.get(username).remove(key);
        keyToUser.remove(key);
    }

    /**
     * Update a socket, can only succeed if an existing socket is closed.
     * @param username
     * @param socket
     * @return true if succeed.
     */
    public boolean update(String username, Socket socket) {
        SocketKey keyToChange = null;
        if (!userList.containsKey(username)) {
            return false;
        }
        for (SocketKey key : userList.get(username)) {
            if (sockets.get(key).isClosed()) {
                keyToChange = key;
                break;
            }
        }
        if (keyToChange == null) {
            return false;
        }
        sockets.put(keyToChange, socket);
        return true;
    }

    public boolean register(String username, String password) {
        if (this.password.containsKey(username)) {
            return false;
        }
        this.password.put(username, password);
        return true;
    }

    public boolean login(String username, String password) {
        if (!this.password.containsKey(username)) {
            return false;
        }
        String actualPassword = this.password.get(username);
        return actualPassword.equals(password);
    }
    
}
