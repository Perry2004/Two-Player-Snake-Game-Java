import model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {
    private Position position;

    @BeforeEach
    public void setUp() {
        position = new Position(3, 4);
    }

    @Test
    public void testGetX() {
        assertEquals(3, position.getPosX());
    }

    @Test
    public void testGetY() {
        assertEquals(4, position.getPosY());
    }

    @Test
    public void testEquals() {
        Position samePosition = new Position(3, 4);
        Position differentPosition = new Position(1, 2);

        assertEquals(position, samePosition);
        assertEquals(position, position);
        assertNotEquals(position, differentPosition);
        assertNotEquals(null, position);
    }

    @Test
    public void testHashCode() {
        Position samePosition = new Position(3, 4);
        Position differentPosition = new Position(1, 2);

        assertEquals(position.hashCode(), samePosition.hashCode());
        assertNotEquals(position.hashCode(), differentPosition.hashCode());
    }
}