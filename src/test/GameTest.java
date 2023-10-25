import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Direction;

import model.Game;
import model.Position;
import model.Snake;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game(5, 6);
    }

    @Test
    public void testSetUp() {
        int expectMaxX = 5;
        int expectMaxY = 6;
        int expectScore = 0;

        assertEquals(expectMaxX, game.getMaxX());
        assertEquals(expectMaxY, game.getMaxY());
        assertEquals(expectScore, game.getScore1());
        assertEquals(expectScore, game.getScore2());
    }

    @Test
    public void testIsOutOfBounds() {
        assertTrue(game.isOutOfBounds(new Position(-1, -2)));
        assertTrue(game.isOutOfBounds(new Position(6, 7)));
        assertFalse(game.isOutOfBounds(new Position(0, 0)));
        assertTrue(game.isOutOfBounds(new Position(3, 7)));
        assertTrue(game.isOutOfBounds(new Position(7, 2)));
        assertFalse(game.isOutOfBounds(new Position(2, 2)));
    }

    @Test
    public void testIsValidPosition() {
        assertFalse(game.isValidPosition(new Position(1, 1)));
        assertFalse(game.isValidPosition(new Position(100, 100)));
    }

    @Test
    public void testTick() {
        game.tick();
        Position expectSnake1Pos = new Position(2, 1);
        Set<Position> food = game.getFood();

        assertEquals(expectSnake1Pos, game.getSnake1().getHead());
        // assert that the food set is not empty
        assertFalse(food.isEmpty());
        game.endGame();
        assertTrue(game.isEnded());
    }

    @Test
    public void testTickOutOfBound() {
        assertTrue(game.isOutOfBounds(new Position(-1, 2)));
        assertTrue(game.isOutOfBounds(new Position(1, -1)));
        assertTrue(game.isOutOfBounds(new Position(6, 7)));
        assertFalse(game.isOutOfBounds(new Position(0, 0)));
        assertTrue(game.isOutOfBounds(new Position(3, 7)));
        assertTrue(game.isOutOfBounds(new Position(7, 2)));
        assertFalse(game.isOutOfBounds(new Position(2, 2)));
    }

    @Test
    public void testEatFood() {
        // test that the snake grows when it eats food by moving the snakes through
        // every position in the game
        // and checking that the snake grows when it eats food
        for (int x = 0; x < game.getMaxX(); x++) {
            for (int y = 0; y < game.getMaxY(); y++) {
                Position pos = new Position(x, y);
                if (!game.isValidPosition(pos)) {
                    continue;
                }
                game.getSnake1().setDirection(Direction.RIGHT);
                while (!game.isEnded()) {
                    game.tick();
                    if (game.getFood().contains(game.getSnake1().getHead())) {
                        int oldLength = game.getScore1();
                        game.getSnake1().grow();
                        assertEquals(oldLength + 1, game.getScore1());
                    }
                }
                game = new Game(5, 6);
            }
        }

        // test for the same thing but with the other snake
        for (int x = 0; x < game.getMaxX(); x++) {
            for (int y = 0; y < game.getMaxY(); y++) {
                Position pos = new Position(x, y);
                if (!game.isValidPosition(pos)) {
                    continue;
                }
                game.getSnake2().setDirection(Direction.RIGHT);
                while (!game.isEnded()) {
                    game.tick();
                    if (game.getFood().contains(game.getSnake2().getHead())) {
                        int oldLength = game.getScore2();
                        game.getSnake2().grow();
                        assertEquals(oldLength + 1, game.getScore2());
                    }
                }
                game = new Game(5, 6);
            }
        }
    }

    @Test
    public void testSpawnNewFood() {
        game.spawnNewFood();
        Set<Position> food = game.getFood();
        assertFalse(food.isEmpty());
    }

    @Test
    public void testSpawnFoodAtInvalid() {
        // spawn enough times to make sure that the food is spawned at an invalid
        // position
        for (int i = 0; i < 27; i++) {
            game.spawnNewFood();
        }
        Set<Position> food = game.getFood();
        assertFalse(food.isEmpty());
    }

    @Test
    public void testNoEatCount() {
        game.setNoEatCount1(10);
        assertEquals(10, game.getNoEatCount1());
        game.setNoEatCount2(100000);
        assertEquals(100000, game.getNoEatCount2());
    }

    @Test
    public void testChooseDifficulty() {
        Game.setTicksPerSecond(10);
        assertEquals(10, Game.getTicksPerSecond());
    }

    @Test
    public void testGetAchievementCollection() {
        game.setNoEatCount1(100000);
        game.setNoEatCount2(5000);
        game.setScore1(100);
        game.setScore2(200);
        game.updateAchievement();
        assertEquals(18, game.getAchievements().getAchievements().size());
    }

    @Test
    public void testCollideOrOutOfBound() {
        // case 1: snake1 collide with self but snake2 is fine
        // first grow the snake to long enough, then go a circle and collide with self
        game = new Game(10, 10);
        Snake snake1 = game.getSnake1();
        Snake snake2 = game.getSnake2();
        snake1.setDirection(Direction.RIGHT);
        for (int i = 0; i < 7; i++) {
            snake1.grow();
            switch (snake2.getDirection()) {
                case UP:
                    snake2.setDirection(Direction.DOWN);
                    break;
                case DOWN:
                    snake2.setDirection(Direction.UP);
                    break;
                case LEFT:
                    snake2.setDirection(Direction.RIGHT);
                    break;
                case RIGHT:
                    snake2.setDirection(Direction.LEFT);
                    break;
                default:
                    break;
            }
            game.tick();
        }
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        game.tick();
        snake1.setDirection(Direction.LEFT);
        snake2.setDirection(Direction.DOWN);
        game.tick();
        snake1.setDirection(Direction.UP);
        snake2.setDirection(Direction.UP);
        game.tick();
        
        assertTrue(game.isEnded());

        // case 2: snake2 collide with self
        game = new Game(10, 10);
        snake1 = game.getSnake1();
        snake2 = game.getSnake2();
        snake2.setDirection(Direction.RIGHT);
        for (int i = 0; i < 7; i++) {
            snake2.grow();
            switch (snake1.getDirection()) {
                case UP:
                    snake1.setDirection(Direction.DOWN);
                    break;
                case DOWN:
                    snake1.setDirection(Direction.UP);
                    break;
                case LEFT:
                    snake1.setDirection(Direction.RIGHT);
                    break;
                case RIGHT:
                    snake1.setDirection(Direction.LEFT);
                    break;
                default:
                    break;
            }
            game.tick();
        }
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        game.tick();
        snake1.setDirection(Direction.UP);
        snake2.setDirection(Direction.LEFT);
        game.tick();
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.DOWN);
        game.tick();
        assertTrue(game.isEnded());

        // case 3: snake1 out of bound
        game = new Game(10, 10);
        snake1 = game.getSnake1();
        snake2 = game.getSnake2();
        snake1.setDirection(Direction.RIGHT);
        for (int i = 0; i < 10; i++) {
            switch (snake2.getDirection()) {
                case UP:
                    snake2.setDirection(Direction.DOWN);
                    break;
                case DOWN:
                    snake2.setDirection(Direction.UP);
                    break;
                case LEFT:
                    snake2.setDirection(Direction.RIGHT);
                    break;
                case RIGHT:
                    snake2.setDirection(Direction.LEFT);
                    break;
                default:
                    break;
            }
            game.tick();
        }
        assertTrue(game.isEnded());

        // case 4: snake2 out of bound
        game = new Game(10, 10);
        snake1 = game.getSnake1();
        snake2 = game.getSnake2();
        snake2.setDirection(Direction.RIGHT);
        for (int i = 0; i < 10; i++) {
            switch (snake1.getDirection()) {
                case UP:
                    snake1.setDirection(Direction.DOWN);
                    break;
                case DOWN:
                    snake1.setDirection(Direction.UP);
                    break;
                case LEFT:
                    snake1.setDirection(Direction.RIGHT);
                    break;
                case RIGHT:
                    snake1.setDirection(Direction.LEFT);
                    break;
                default:
                    break;
            }
            game.tick();
        }

        // case 5: snake1 collide with snake2
        game = new Game(10, 10);
        snake1 = game.getSnake1();
        snake2 = game.getSnake2();
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        for (int i = 0; i < 4; i++) {
            game.tick();
        }
        assertTrue(game.isEnded());

        // case 6: snake 1 collide with snake2, but snake2 does not collide with snake1
        game = new Game(10, 10);
        snake1 = game.getSnake1();
        snake2 = game.getSnake2();
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        game.tick();

        snake1.setDirection(Direction.RIGHT);
        snake2.setDirection(Direction.RIGHT);
        for (int i = 0; i < 7; i++) {
            snake1.grow();
            game.tick();
        }
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        game.tick();
        snake1.setDirection(Direction.LEFT);
        for (int i = 0; i < 5; i++) {
            game.tick();
        }
        assertTrue(game.isEnded());

        // case 7: snake 2 collide with snake1, but snake1 does not collide with snake2
        game = new Game(10, 10);
        snake1 = game.getSnake1();
        snake2 = game.getSnake2();
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        game.tick();

        snake1.setDirection(Direction.RIGHT);
        snake2.setDirection(Direction.RIGHT);
        for (int i = 0; i < 7; i++) {
            snake2.grow();
            game.tick();
        }
        snake1.setDirection(Direction.DOWN);
        snake2.setDirection(Direction.UP);
        game.tick();
        snake2.setDirection(Direction.LEFT);
        for (int i = 0; i < 5; i++) {
            game.tick();
        }
        assertTrue(game.isEnded());
    }

    @Test
    public void testFoodToJson() {
        Position foodPos = game.getFood().iterator().next();
        String expected = "{\"food\":" + foodPos.toJson().toString() + "}";
        assertEquals(expected, game.foodToJson().toString());
    }

    @Test
    public void testGameToJson() {
        game.setTicksPerSecond(5);
        JSONObject json = new JSONObject();
        json.put("snake1", game.getSnake1().toJson());
        json.put("snake2", game.getSnake2().toJson());
        json.put("food", game.foodToJson());
        json.put("score1", 0);
        json.put("score2", 0);
        json.put("noEatCount1", 0);
        json.put("noEatCount2", 0);
        json.put("achievements", game.getAchievements().toJson());
        json.put("TICKS_PER_SECOND", 5);
        assertEquals(json.toString(), game.toJson().toString());
    }

}