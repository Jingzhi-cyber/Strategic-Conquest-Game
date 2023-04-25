package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.SocketKey;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {

    @Test
    public void testManager() {
        AccountManager am = new AccountManager();
        assertTrue(am.register("hello", "hello"));
        assertFalse(am.register("hello", "hello"));
        assertTrue(am.login("hello", "hello"));
        assertFalse(am.login("a", "hello"));
        am.addToGame("game1", "a");
        am.addToGame("game1", "b");
        assertTrue(am.gameExists("game1"));
        assertFalse(am.gameExists("game2"));
        am.gameOver("game1");
        assertFalse(am.gameExists("game1"));
    }
    
}
