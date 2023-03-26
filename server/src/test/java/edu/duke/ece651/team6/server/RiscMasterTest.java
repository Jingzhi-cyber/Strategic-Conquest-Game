package edu.duke.ece651.team6.server;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.SimpleMap;
import edu.duke.ece651.team6.shared.Territory;


public class RiscMasterTest {
    private final Server server = mock(Server.class);
    private GameMap gameMap;
    private RiscMaster riscMaster;

    @BeforeEach
    public void setUp() throws IOException, ClassNotFoundException {
        // Testing: Use SimpleMap
        SimpleMap simpleMap = new SimpleMap();
        gameMap = new GameMap(simpleMap.getAdjList());
        assertThrows(IllegalArgumentException.class, ()->new RiscMaster(server, 0, gameMap));
        this.riscMaster = new RiscMaster(server, 2, gameMap);
        List<Socket> clientSockets = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            clientSockets.add(new Socket());
        }
        when(this.server.getClientSockets()).thenReturn(clientSockets);
    }

    @Test
    public void testInit() throws IOException {
        assertDoesNotThrow(()->riscMaster.init());
    }

    @Test
    public void testSetUpGameBasicSettings() throws ClassNotFoundException, IOException {
        GameBasicSetting gameBasicSetting = new GameBasicSetting(0, 2, new HashSet<Territory>(), 2);
        Territory t = gameMap.getTerritoryByName("Narnia");
        Map<Territory, Integer> map = new HashMap<>();
        map.put(t, 1);
        gameBasicSetting.initializeUnitPlacement(map);
        when(this.server.recvObject(any(Socket.class))).thenReturn(gameBasicSetting);
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.setUpGameBasicSettings());
        when(this.server.recvObject(any(Socket.class))).thenReturn(null);
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.setUpGameBasicSettings());
    }

    @Test
    public void testPlayOneTurn() throws ClassNotFoundException, IOException {
        riscMaster.init();
        Commit commit = mock(Commit.class);
        when(this.server.recvObject(any(Socket.class))).thenReturn(commit);
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testFinish() throws IOException {
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.finish());
    }

    @Test
    public void testOriginalCtor() throws IOException {
        SimpleMap simpleMap = new SimpleMap();
        GameMap gameMap = new GameMap(simpleMap.getAdjList());
        assertDoesNotThrow(()->new RiscMaster(12345, 2, gameMap));
        assertThrows(IllegalArgumentException.class, ()->new RiscMaster(65536, 2, gameMap));
        assertThrows(IllegalArgumentException.class, ()->new RiscMaster(12345, 0, gameMap));
        assertThrows(IllegalArgumentException.class, ()->new RiscMaster(54321, 4, gameMap));
    }

    @Test
    public void testInvalidCommit() throws ClassNotFoundException, IOException {
        riscMaster.init();
        Commit commit = mock(Commit.class);
        when(this.server.recvObject(any(Socket.class))).thenReturn(commit);
        doThrow(new IllegalArgumentException()).when(commit).checkAll(any(GameMap.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testCheckResult() throws ClassNotFoundException, IOException {
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        riscMaster.init();
        Commit commit = mock(Commit.class);
        when(this.server.recvObject(any(Socket.class))).thenReturn(commit, commit, commit, commit, false, commit, true);
        doThrow(new IOException()).when(server).closeClientSocket(any(Socket.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        Territory t = gameMap.getTerritoryByName("Narnia");
        t.setOwnerId(2); // To simulate someone loses but not ending the game
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        t.setOwnerId(1);
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testSendObjectError() throws ClassNotFoundException, IOException {
        riscMaster.init();
        // Commit commit = mock(Commit.class);
        doThrow(new IOException()).when(server).sendObject(any(Socket.class), any(Object.class));
        doThrow(new IOException()).when(server).closeClientSocket(any(Socket.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testRecvObjectError() throws ClassNotFoundException, IOException {
        riscMaster.init();
        Commit commit = mock(Commit.class);
        doThrow(new ClassNotFoundException()).when(server).recvObject(any(Socket.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        Territory t = gameMap.getTerritoryByName("Narnia");
        t.setOwnerId(2); // To simulate someone loses but not ending the game
        when(this.server.recvObject(any(Socket.class))).thenReturn(commit, commit, true);
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        doThrow(new IOException()).when(server).recvObject(any(Socket.class));
        doThrow(new IOException()).when(server).closeClientSocket(any(Socket.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }
}
