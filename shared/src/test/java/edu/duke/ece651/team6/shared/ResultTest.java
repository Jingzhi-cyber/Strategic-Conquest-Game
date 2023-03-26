package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class ResultTest {
    @Test
    public void testBasicFunc() {
        Result r = new Result();
        r.addWinner(0);
        r.addLoser(1);
        r.addLoser(2);
        Set<Integer> expectedWinners = new HashSet<>();
        expectedWinners.add(0);
        Set<Integer> expectedLosers = new HashSet<>();
        expectedLosers.add(1);
        expectedLosers.add(2);
        assertEquals(expectedWinners, r.getWinners());
        assertEquals(expectedLosers, r.getLosers());
    }
}
