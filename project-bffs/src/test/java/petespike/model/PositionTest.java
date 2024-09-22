package petespike.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class PositionTest {
    @Test
    public void testEquals() {
        Position p1 = new Position(1, 2);
        Position p2 = new Position(1, 2);
        Position p3 = new Position(2, 1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }
}
