package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

public class RiscMaster implements Master {
    private final Server server;
    private Socket clientSocket;
    private GameMap gameMap;

    public RiscMaster() throws IOException {
        this.server = new Server(12345);
    }

    public void setGameMap(GameMap gm) {
        this.gameMap = gm;
    }

    // TODO: player should be a Player class
    // TODO: this should be private
    /**
     * Create GameMap info according to a specific player
     * @param player
     * @return player's territory1 - territory1's neighbors' names
     *         player's territory2 - territory2's neighbors' names
     */
    public PlayerMapInfo createPlayerMapInfo(String player) {
        HashMap<Territory, HashSet<String>> info = new HashMap<Territory, HashSet<String>>();
        Set<Territory> territories = gameMap.getTerritorySet();
        for (Territory t : territories) {
            if (t.getOwner().equals(player)) {
                HashSet<String> neighbor = new HashSet<String>();
                for (Territory n : gameMap.getNeighborSet(t.getName())) {
                    neighbor.add(n.getName());
                }
                info.put(t, neighbor);
            }
        }
        PlayerMapInfo playerMapInfo = new PlayerMapInfo(info);
        return playerMapInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean init() throws IOException {
        clientSocket = server.accept();
        return true;
        // throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playOneTurn() throws IOException {
        server.sendObject(clientSocket, createPlayerMapInfo("Player1"));
        // throw new UnsupportedOperationException("Unimplemented method 'playOneTurn'");
    }
    
    public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {
        HashMap<Territory, HashSet<Territory>> adjList = new HashMap<Territory, HashSet<Territory>>();
        Territory t1 = new Territory("Hogwarts", "Player1", 8);
        Territory t2 = new Territory("Narnia", "Player2", 3);
        Territory t3 = new Territory("Midkemia", "Player3", 1);
        HashSet<Territory> n1 = new HashSet<Territory>();
        n1.add(t2);
        n1.add(t3);
        adjList.put(t1, n1);
        HashSet<Territory> n2 = new HashSet<Territory>();
        n2.add(t1);
        adjList.put(t2, n2);
        HashSet<Territory> n3 = new HashSet<Territory>();
        n3.add(t1);
        adjList.put(t3, n3);
        GameMap gm = new GameMap(adjList);
        RiscMaster rm = new RiscMaster();
        rm.setGameMap(gm);
        rm.init();
        rm.playOneTurn();
    }
}
