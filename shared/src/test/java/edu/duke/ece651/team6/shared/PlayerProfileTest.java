package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class PlayerProfileTest {
    @Test
    public void testBasicFunc() {
        PlayerProfile pp = new PlayerProfile(0, "test");
        pp.setName("test");
        pp.setSocket(new SocketKey());
        pp.getSocket();
        assertEquals(0, pp.getId());
        assertEquals("test", pp.getName());
    }
}
