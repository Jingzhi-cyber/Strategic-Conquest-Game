package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

public class ResultTest {
    @Test
    public void testBasicFunc() {
        Result r = new Result();
        r.addWinner(0);
        r.addLoser(1);
        r.addLoser(2);
        HashSet<Integer> expectedWinners = new HashSet<>();
        expectedWinners.add(0);
        HashSet<Integer> expectedLosers = new HashSet<>();
        expectedLosers.add(1);
        expectedLosers.add(2);
        assertEquals(expectedWinners, r.getWinners());
        assertEquals(expectedLosers, r.getLosers());
    }
}
