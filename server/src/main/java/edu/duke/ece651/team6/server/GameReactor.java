package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.SampleMap;
import edu.duke.ece651.team6.shared.SimpleMap;
import edu.duke.ece651.team6.shared.SocketKey;
import edu.duke.ece651.team6.shared.Territory;

/**
 * This class is used to manage multiple games. It will group the incoming connections based on 
 * player numbers. For example, if three connections of a three-players game come in, a new game 
 * will be started.
 */
public class GameReactor {

    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    Map<Integer, List<SocketKey>> playerManager;

    GameReactor(int port, int num) throws IOException {
        this.serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(num);
        playerManager = new HashMap<>();
        playerManager.put(2, new ArrayList<>());
        playerManager.put(3, new ArrayList<>());
        playerManager.put(4, new ArrayList<>());
    }

    /**
     * Start waiting for incoming connections.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void run() throws IOException, ClassNotFoundException {
        AccountManager accountManager = AccountManager.getInstance();
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
            System.out.println(numPlayer);
            // Register
            if (numPlayer == -2) {
                if (accountManager.register(username, password)) {
                    socket.getOutputStream().write(0);
                }
                socket.close();
                continue;
            }
            // Login
            if (!accountManager.login(username, password)) {
                socket.close();
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
                if (!accountManager.update(username, socket)) {
                    socket.close();
                } else {
                    socket.getOutputStream().write(0);
                }
            // Join a new game.
            } else {
                SocketKey key = accountManager.add(username, socket);
                playerManager.get(numPlayer).add(key);
                if (playerManager.get(numPlayer).size() == numPlayer) {
                    GameStarter gs = new GameStarter(new ArrayList<>(playerManager.get(numPlayer)));
                    playerManager.get(numPlayer).clear();
                    threadPool.execute(gs);
                }
            }   
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }

    /**
     * Start a game.
     */
    protected class GameStarter implements Runnable {

        private final List<SocketKey> clientSockets;

        public GameStarter(List<SocketKey> clientSockets) {
            this.clientSockets = clientSockets;
        }

        @Override
        public void run() {
            // SampleMap sampleMap = new SampleMap();
            // GameMap gameMap = new GameMap(sampleMap.getAdjList());
            // SimpleMap simpleMap = new SimpleMap();
            // GameMap gameMap = new GameMap(simpleMap.getAdjList());
            MapGenerator mapGenerator = new MapGenerator(24);
            GameMap gameMap = new GameMap(mapGenerator.getDistanceMap(), false);
            Server server = new Server(clientSockets);
            try {
                for (SocketKey key : clientSockets) {
                    server.sendObject(key, "Connected to the server!");
                }
                RiscMaster riscMaster = new RiscMaster(server, clientSockets.size(), gameMap);
                riscMaster.init();
                riscMaster.setUpGameBasicSettings();
                while (true) {
                    if (riscMaster.playOneTurn()) {
                        break;
                    }
                }
                riscMaster.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
    }


}
