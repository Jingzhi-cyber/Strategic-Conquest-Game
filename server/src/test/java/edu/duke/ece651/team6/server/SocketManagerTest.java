package edu.duke.ece651.team6.server;

import edu.duke.ece651.team6.shared.SocketKey;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketManagerTest {
    @Test
    void testMain() {
        SocketManager socketManager = SocketManager.getInstance();
        socketManager = SocketManager.getInstance();
        Socket socket = new Socket();
        SocketKey key = socketManager.add(socket, "test");
        assertSame(socketManager.get(key), socket);
        assertEquals(socketManager.getUsername(key), "test");
    }

}