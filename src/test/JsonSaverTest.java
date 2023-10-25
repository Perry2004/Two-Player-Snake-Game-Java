import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Game;
import persistence.JsonSaver;

public class JsonSaverTest {
    Game game;

    @BeforeEach
    void setUp() {
        game = new Game(10, 10);
    }

    @Test
    public void testSave() {
        JsonSaver.saveGame("data/testSnakeSave.json", game.getSnake1());

        // read in the file
        try (Reader reader = new FileReader("data/testSnakeSave.json")) {
            // read in the file
            StringBuilder sb = new StringBuilder();
            while (reader.ready()) {
                sb.append((char) reader.read());
            }
            String fileContents = sb.toString();
            assertEquals(fileContents, game.getSnake1().toJson().toString(4));
        } catch (IOException e) {
            fail("IOException should not have been thrown");
        }
    }

    @Test
    public void testSaveWithException() {
        boolean res = JsonSaver.saveGame("data/illegal:\"\"\0path.json", game);
        assertFalse(res);
        JsonSaver jsonSaver = new JsonSaver();
        assertNotNull(jsonSaver);
    }

}
