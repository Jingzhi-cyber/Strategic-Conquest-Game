package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.duke.ece651.team6.server.MongoHero;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.SocketKey;

/**
 * This class is used to manage multiple games. It will group the incoming connections based on 
 * player numbers. For example, if three connections of a three-players game come in, a new game 
 * will be started.
 */
public class GameReactor {

    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final Map<Integer, List<String>> players;
    private final Map<Integer, List<Socket>> sockets;
    private final AccountManager accountManager;
    private final MongoHero mongoHero;
    private final SocketManager socketManager = SocketManager.getInstance();
    private final Reconnector reconnector;

    GameReactor(int port, int num) throws IOException {
        this.serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(num);
        players = new HashMap<>();
        players.put(2, new ArrayList<>());
        players.put(3, new ArrayList<>());
        players.put(4, new ArrayList<>());
        sockets = new HashMap<>();
        sockets.put(2, new ArrayList<>());
        sockets.put(3, new ArrayList<>());
        sockets.put(4, new ArrayList<>());
        mongoHero = MongoHero.getInstance();
        Object obj = mongoHero.getObject("accountManager");
        if (obj != null) {
            System.out.println("accountManager found");
            accountManager = (AccountManager) obj;
        } else {
            System.out.println("new accountManager");
            accountManager = new AccountManager();
        }
        reconnector = new Reconnector(this, accountManager);
    }

    /**
     * Start waiting for incoming connections.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            Socket socket = serverSocket.accept();
            ObjectInputStream oos = new ObjectInputStream(socket.getInputStream());
            String info = (String) oos.readObject();
            System.out.println(info);
            String[] args = info.split(" ");
            if (args.length != 3) {
                socket.close();
                continue;
            }
            String username = args[0];
            String password = args[1];
            int numPlayer = Integer.parseInt(args[2]);
            // Register
            if (numPlayer == -2) {
                if (accountManager.register(username, password)) {
                    socket.getOutputStream().write(0);
                    System.out.println("registered!");
                    mongoHero.storeObject(accountManager, "accountManager");
                }
                socket.close();
                continue;
            }
            // Login
            if (!accountManager.login(username, password)) {
                socket.close();
                System.out.println("login failed!");
                continue;
            } else {
                if (numPlayer == -1) {
                    socket.getOutputStream().write(0);
                    socket.close();
                    continue;
                }
            }
            // Return to a game.
            if (numPlayer == 0) {
                if (!reconnector.joinGame(username, socket)) {
                    System.out.println("return failed!");
                    socket.close();
                } else {
                    System.out.println("returned");
                }
            // Join a new game.
            } else {
                players.get(numPlayer).add(username);
                sockets.get(numPlayer).add(socket);
                if (players.get(numPlayer).size() == numPlayer) {
                    newGame(numPlayer);
                }
            }   
        }
    }

    private void newGame(int numPlayer) {
        String uuid;
        do {
            UUID u = UUID.randomUUID();
            uuid = u.toString();
        } while (accountManager.gameExists(uuid));
        String uuidStr = uuid;
        players.get(numPlayer).forEach(username -> accountManager.addToGame(uuidStr, username));
        mongoHero.storeObject(accountManager, "accountManager");
        List<SocketKey> socketKeys = new ArrayList<>();
        for (int i = 0; i < numPlayer; i++) {
            socketKeys.add(socketManager.add(sockets.get(numPlayer).get(i), players.get(numPlayer).get(i)));
        }
        MapGenerator mapGenerator = new MapGenerator(24);
        GameMap gameMap = new GameMap(mapGenerator.getDistanceMap(), false);
        Server server = new Server(socketKeys);
        socketKeys.forEach(key -> {
            try {
                server.sendObject(key, "Connected to the server!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        RiscMaster riscMaster = new RiscMaster(server, numPlayer, gameMap);
        System.out.println("new game id: " + uuidStr);
        threadPool.submit(new GameStarter(riscMaster, uuidStr));
        players.get(numPlayer).clear();
        sockets.get(numPlayer).clear();
    }

    public void returnToGame(String gameUUID, Map<Socket, String> sockets) {
        List<SocketKey> socketKeys = new ArrayList<>();
        sockets.forEach((socket, username) -> socketKeys.add(socketManager.add(socket, username)));
        System.out.println("try to fetch game: " + gameUUID);
        RiscMaster riscMaster = (RiscMaster) mongoHero.getObject(gameUUID);
        Server server = new Server(socketKeys);
        riscMaster.setServer(server);
        socketKeys.forEach(key -> {
            try {
                if (riscMaster.status.equals("INIT")) {
                    server.sendObject(key, "Congratulations! Returned to a game!");
                } else {
                    server.sendObject(key, "Returned to a game!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        threadPool.submit(new GameStarter(riscMaster, gameUUID));
    }

    public void close() throws IOException {
        serverSocket.close();
    }

    /**
     * Start a game.
     */
    protected class GameStarter implements Runnable {

        private final RiscMaster riscMaster;
        private final String gameUUID;

        public GameStarter(RiscMaster riscMaster, String gameUUID) {
            this.riscMaster = riscMaster;
            this.gameUUID = gameUUID;
        }

        @Override
        public void run() {
            try {
                mongoHero.storeObject(riscMaster, gameUUID);
                riscMaster.init();
                if (riscMaster.status.equals("INIT")) {
                    riscMaster.setUpGameBasicSettings();
                    riscMaster.status = "BEGIN";
                    mongoHero.storeObject(riscMaster, gameUUID);
                }
                while (true) {
                    if (riscMaster.playOneTurn()) {
                        break;
                    }
                    mongoHero.storeObject(riscMaster, gameUUID);
                }
                riscMaster.finish();
                accountManager.gameOver(gameUUID);
                mongoHero.storeObject(accountManager, "accountManager");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
