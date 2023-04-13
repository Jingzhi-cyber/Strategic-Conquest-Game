package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConstantsTest {
    @Test
    public void testResearchCosts() {
        assertEquals(2, Constants.researchCosts.get(1));
    }
}
