package edu.duke.ece651.team6.server;

import java.io.Serializable;
import java.util.*;

/**
 * This class is a singelton. This class can manage all the user with their sockets. 
 * All the send or read operations related to socket should get socket from this class.
 * When adding a new socket into this class, a SocketKey will be returned, which can be 
 * used to get Socket from this class.
 */
public class AccountManager implements Serializable {

    // key = gameUUID, value = names of users in a game
    protected final Map<String, Set<String>> games;
    // key = username, value = password
    private final Map<String, String> password;
    // key = username, value = gameUUIDs belong to the user
    protected final Map<String, Set<String>> userGames;

    public AccountManager() {
        password = new HashMap<>();
        games = new HashMap<>();
        userGames = new HashMap<>();
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

    public void gameOver(String gameUUID) {
        games.get(gameUUID).forEach(username -> userGames.get(username).remove(gameUUID));
        games.remove(gameUUID);
    }

    public boolean gameExists(String gameUUID) {
        return games.containsKey(gameUUID);
    }

    public void addToGame(String gameUUID, String username) {
        games.putIfAbsent(gameUUID, new HashSet<>());
        games.get(gameUUID).add(username);
        userGames.putIfAbsent(username, new HashSet<>());
        userGames.get(username).add(gameUUID);
    }
    
}
