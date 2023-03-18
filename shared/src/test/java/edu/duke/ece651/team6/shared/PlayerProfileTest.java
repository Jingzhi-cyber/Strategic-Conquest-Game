package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.Socket;


public class PlayerProfileTest {
    @Test
    public void testBasicFunc() {
        PlayerProfile pp = new PlayerProfile(0);
        pp.setName("test");
        pp.setSocket(new Socket());
        pp.getSocket();
        assertEquals(0, pp.getId());
        assertEquals("test", pp.getName());
    }
}
