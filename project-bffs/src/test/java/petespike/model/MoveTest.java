package petespike.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class MoveTest {
    @Test
    public void testEquals() {
        Move m1 = new Move(new Position(1, 2), Direction.UP);
        Move m2 = new Move(new Position(1, 2), Direction.UP);
        Move m3 = new Move(new Position(2, 1), Direction.DOWN);
        Move m4 = new Move(new Position(1, 2), Direction.DOWN);

        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
        assertNotEquals(m1, m4);
        assertNotEquals(m3, m4);
        assertNotEquals(m1, m4);
    }

}
