package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class OrderTest {
    @Test
    public void testGetName() {
        Order order = new Order("name");
        assertEquals("name", order.getName());
    }
}
