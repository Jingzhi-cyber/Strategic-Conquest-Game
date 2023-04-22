package edu.duke.ece651.team6.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReconnectorTest {

    @Test
    void testMain() throws IOException {
        GameReactor gr = new GameReactor(6677, 5);
        new Thread(() -> {
            try {
                gr.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
        AccountManager am = new AccountManager();
        am.addToGame("game1", "a");
        am.addToGame("game1", "b");
        am.addToGame("game2", "c");
        am.addToGame("game2", "d");
        Reconnector r = new Reconnector(gr, am);
        assertThrows(RuntimeException.class, () -> r.joinGame("a", new Socket()));
        r.joinGame("a", new Socket("localhost", 6677));
        assertThrows(Exception.class, () -> r.joinGame("b", new Socket("localhost", 6677)));
    }
}
