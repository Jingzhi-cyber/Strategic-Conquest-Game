package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.SocketKey;

public class AccountManagerTest {

    @Test
    public void testManager() {
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
        assertFalse(m.update("a", s1));
        try {
            s2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(m.update("a", s1));
        assertSame(s1, m.getSocket(key2));
    }
    
}