import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Direction;
import model.Position;
import model.Snake;

public class SnakeTest {

    private Snake snake;

    @BeforeEach
    public void setUp() {
        snake = new Snake(1, 1, "a");
    }

    @Test
    public void testMove() {
        snake.move();
        snake.move();
        snake.setDirection(Direction.DOWN);
        snake.move();

        Position expectPosition = new Position(3, 2);
        assertEquals(expectPosition, snake.getHead());
    }

    @Test
    public void testChangeDirection() {
        snake.move();
        snake.setDirection(Direction.DOWN);
        snake.move();
        snake.setDirection(Direction.LEFT);
        snake.move();
        snake.setDirection(Direction.UP);
        snake.move();
        Position expectPosition = new Position(1, 1);
        assertEquals(expectPosition, snake.getHead());
    }

    @Test
    public void testGrow() {
        snake.move();
        snake.move();
        snake.grow();
        snake.setDirection(Direction.DOWN);
        snake.move();

        Position expectHead = new Position(3, 2);
        List<Position> expectBody = new ArrayList<>();
        expectBody.add(new Position(3, 1));
        assertEquals(expectHead, snake.getHead());
        assertEquals(expectBody, snake.getBody());
    }

    @Test
    public void testCollideWithSelf() {
        snake.move();
        snake.move();
        snake.grow();
        snake.setDirection(Direction.DOWN);
        snake.move();
        snake.grow();
        snake.setDirection(Direction.LEFT);
        snake.move();
        snake.grow();
        snake.setDirection(Direction.UP);
        snake.move();
        snake.grow();
        snake.setDirection(Direction.RIGHT);
        snake.move();
        assertTrue(snake.hasCollidedWithSelf());
    }

    @Test
    public void testHasCollided() {
        Position pos = new Position(1, 1);
        assertTrue(snake.hasCollided(pos));
        snake.grow();
        snake.move();
        snake.grow();
        snake.move();
        assertTrue(snake.hasCollided(new Position(2, 1)));
    }

    @Test
    public void testEatApple() {
        snake.eatApple();
        assertEquals(1, snake.getNumApplesEaten());
    }

    @Test
    public void testDirection() {
        snake.setDirection(Direction.DOWN);
        assertEquals(Direction.DOWN, snake.getDirection());
    }
}