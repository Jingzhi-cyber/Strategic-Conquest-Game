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

import edu.duke.ece651.team6.shared.*;


public class RiscMasterTest {
    private final Server server = mock(Server.class);
    private GameMap gameMap;
    private RiscMaster riscMaster;

    @BeforeEach
    public void setUp() throws IOException, ClassNotFoundException {
        // Testing: Use SimpleMap
        SimpleMap simpleMap = new SimpleMap();
        AccountManager m = AccountManager.getInstance();
        gameMap = new GameMap(simpleMap.getAdjList());
        assertThrows(IllegalArgumentException.class, ()->new RiscMaster(server, 0, gameMap));
        this.riscMaster = new RiscMaster(server, 2, gameMap);
        List<SocketKey> clientSockets = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            clientSockets.add(m.add("test", new Socket()));
        }
        when(this.server.getClientSockets()).thenReturn(clientSockets);
    }

    @Test
    public void testInit() throws IOException {
        assertDoesNotThrow(()->riscMaster.init());
    }

    @Test
    public void testSetUpGameBasicSettings() throws ClassNotFoundException, IOException {
        GameBasicSetting gameBasicSetting = new GameBasicSetting(0, 2, null, new HashSet<Territory>(), 2);
        Territory t = gameMap.getTerritoryByName("Narnia");
        Map<Territory, Integer> map = new HashMap<>();
        map.put(t, 1);
        gameBasicSetting.initializeUnitPlacement(map);
        when(this.server.recvObject(any(SocketKey.class))).thenReturn(gameBasicSetting);
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.setUpGameBasicSettings());
        when(this.server.recvObject(any(SocketKey.class))).thenReturn(null);
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.setUpGameBasicSettings());
    }

    @Test
    public void testPlayOneTurn() throws ClassNotFoundException, IOException {
        riscMaster.init();
        Commit commit = mock(Commit.class);
        when(this.server.recvObject(any(SocketKey.class))).thenReturn(commit);
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testFinish() throws IOException {
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.finish());
    }

    @Test
    public void testInvalidCommit() throws ClassNotFoundException, IOException {
        riscMaster.init();
        Commit commit = mock(Commit.class);
        when(this.server.recvObject(any(SocketKey.class))).thenReturn(commit);
        doThrow(new IllegalArgumentException()).when(commit).checkAll(any(GameMap.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testCheckResult() throws ClassNotFoundException, IOException {
        riscMaster.init();
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        riscMaster.init();
        Commit commit = mock(Commit.class);
        when(this.server.recvObject(any(SocketKey.class))).thenReturn(commit, commit, commit, commit, false, commit, true);
        doThrow(new IOException()).when(server).closeClientSocket(any(SocketKey.class));
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
        doThrow(new IOException()).when(server).sendObject(any(SocketKey.class), any(Object.class));
        doThrow(new IOException()).when(server).closeClientSocket(any(SocketKey.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }

    @Test
    public void testRecvObjectError() throws ClassNotFoundException, IOException {
        riscMaster.init();
        Commit commit = mock(Commit.class);
        doThrow(new ClassNotFoundException()).when(server).recvObject(any(SocketKey.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        Territory t = gameMap.getTerritoryByName("Narnia");
        t.setOwnerId(2); // To simulate someone loses but not ending the game
        when(this.server.recvObject(any(SocketKey.class))).thenReturn(commit, commit, true);
        assertDoesNotThrow(()->riscMaster.playOneTurn());
        doThrow(new IOException()).when(server).recvObject(any(SocketKey.class));
        doThrow(new IOException()).when(server).closeClientSocket(any(SocketKey.class));
        assertDoesNotThrow(()->riscMaster.playOneTurn());
    }
}
