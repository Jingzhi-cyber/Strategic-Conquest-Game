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
import edu.duke.ece651.team6.shared.SocketKey;

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
        AccountManager m = AccountManager.getInstance();
        while (true) {
            Socket socket = serverSocket.accept();
            ObjectInputStream oos = new ObjectInputStream(socket.getInputStream());
            String info = (String) oos.readObject();
            System.out.println(info);
            String[] args = info.split(" ");
            String username = args[0];
            int numPlayer = 0;
            if (args.length > 1) {
                numPlayer = Integer.valueOf(args[1]);
            }
            System.out.println(numPlayer);
            SocketKey key = null;
            if (numPlayer == 0) {
                if (!m.update(username, socket)) {
                    socket.close();
                }
            } else {
                key = m.add(username, socket);
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
    private class GameStarter implements Runnable {

        private List<SocketKey> clientSockets;

        public GameStarter(List<SocketKey> clientSockets) {
            this.clientSockets = clientSockets;
        }

        @Override
        public void run() {
            SampleMap sampleMap = new SampleMap();
            GameMap gameMap = new GameMap(sampleMap.getAdjList());
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
