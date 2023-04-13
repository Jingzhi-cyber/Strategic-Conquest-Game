package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.SocketKey;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {

    @Test
    public void testManager() throws IOException {
        AccountManager m = AccountManager.getInstance();
        Socket s1 = new Socket();
        SocketKey key1 = m.add("a", s1);
        Socket s2 = new Socket();
        SocketKey key2 = m.add("a", s2);
        Socket s3 = new Socket();
        m.add("a", s3);
        assertSame(s1, m.getSocket(key1));
        assertSame(s2, m.getSocket(key2));
        m.remove(key1);
        m.remove(key1);
        assertTrue(m.update("a", s1));
        try {
            s2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(m.update("a", s1));
        //assertNotSame(s1, m.getSocket(key2));
        assertTrue(m.register("hello", "hello"));
        assertFalse(m.register("hello", "hello"));
        assertFalse(m.login("user", "hello"));
        assertFalse(m.login("hello", "user"));
        assertTrue(m.login("hello", "hello"));
    }
    
}
