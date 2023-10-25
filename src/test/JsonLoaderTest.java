import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.util.Scanner;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Game;
import model.Snake;
import persistence.JsonLoader;

public class JsonLoaderTest {
    String jsonStr;

    @BeforeEach
    void setUp() {
        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File("data/testSnakeSave.json"));
            jsonString.append(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        } finally {
            jsonStr = jsonString.toString();
        }
    }

    @Test
    void testLoad() {
        Game game = new Game(10, 10);
        Snake snake1 = game.getSnake1();
        Snake snake2 = game.getSnake2();
        JsonLoader.loadGame("data/testGameSave.json", game, snake1, snake2);

        assertEquals(0, game.getNoEatCount1());
        assertEquals(0, game.getNoEatCount2());
        assertEquals(14, game.getAchievements().getAchievements().size());
        assertEquals(0, game.getScore1());
        assertEquals(0, game.getScore2());
        assertEquals(0, Game.getTicksPerSecond());
        assertEquals(1, snake1.getHead().getPosX());
        assertEquals(1, snake1.getHead().getPosY());
        assertEquals(2, snake1.getBody().size());
        assertEquals(1, snake2.getHead().getPosX());
        assertEquals(9, snake2.getHead().getPosY());
        assertEquals(0, snake2.getBody().size());
    }

    @Test
    void testInvalidLoad() {
        Game game = new Game(10, 10);
        Snake snake1 = game.getSnake1();
        Snake snake2 = game.getSnake2();
        assertFalse(JsonLoader.loadGame("data/invalid::://\\GameSave.json", game, snake1, snake2));
    }

    @Test
    void testLoadGeneralAchievement() {
        // read from data/testGeneralAchievementSave.json
        JSONObject json;
        try (Scanner scanner = new Scanner(new File("data/testGeneralAchievementSave.json"))) {
            StringBuilder jsonString = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
            json = new JSONObject(jsonString.toString());
        } catch (Exception e) {
            return;
        }

        Game game = new Game(10, 10);
        Snake snake1 = game.getSnake1();
        Snake snake2 = game.getSnake2();

        assertFalse(JsonLoader.updateGame(game, snake1, snake2, json));

    }

}
