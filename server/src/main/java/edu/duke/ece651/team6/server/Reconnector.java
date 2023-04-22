package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Reconnector {

    private final GameReactor gr;
    private final Map<String, Set<String>> userDisconnectedGames;
    private final Map<String, Map<Socket, String>> gameSockets;
    private final Map<String, Integer> gameNum;

    public Reconnector(GameReactor gr, AccountManager am) {
        this.gr = gr;
        userDisconnectedGames = new HashMap<>();
        gameSockets = new HashMap<>();
        gameNum = new HashMap<>();
        am.userGames.forEach((key, value) -> {
            userDisconnectedGames.put(key, new HashSet<>());
            value.forEach(gameUUID -> userDisconnectedGames.get(key).add(gameUUID));
        });
        am.games.forEach((key, value) -> {
            gameSockets.put(key, new HashMap<>());
            gameNum.put(key, value.size());
        });
    }

    public boolean joinGame(String username, Socket socket) {
        if (!userDisconnectedGames.containsKey(username) || userDisconnectedGames.get(username).size() == 0) {
            return false;
        }
        try {
            socket.getOutputStream().write(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String gameUUID = userDisconnectedGames.get(username).stream().findFirst().get();
        userDisconnectedGames.get(username).remove(gameUUID);
        gameSockets.get(gameUUID).put(socket, username);
        if (gameSockets.get(gameUUID).size() == gameNum.get(gameUUID)) {
            gr.returnToGame(gameUUID, gameSockets.get(gameUUID));
            gameSockets.remove(gameUUID);
        }
        return true;
    }

}
