package persistence;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import model.Direction;
import model.Game;
import model.Position;
import model.Snake;
import model.achievements.GeneralAchievement;
import model.achievements.StatisticalAchievement;

/**
 * The class that is used to load Jsonizable objects from a file
 */
public class JsonLoader {
    /**
     * REQUIRES: path is a valid path to a JSON file;
     * Game, snake1, snake2 are valid objects
     * MODIFIES: game, snake1, snake2
     * EFFECTS: loads the game from the given JSON file and updates the given
     * objects
     * 
     * @param path   the path to the JSON file
     * @param game   the game to update
     * @param snake1 the first snake to update
     * @param snake2 the second snake to update
     */
    public static boolean loadGame(String path, Game game, Snake snake1, Snake snake2) {
        StringBuilder jsonString = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
            return false;
        }
        updateGame(game, snake1, snake2, new JSONObject(jsonString.toString()));
        return true;
    }

    /**
     * REQUIRES: json is a valid JSON object; game, snake1, snake2 are valid and not
     * null
     * MODIFIES: game, snake1, snake2
     * EFFECTS: updates the given objects with the data from the given JSON object
     * 
     * @param game the game to update
     * @param snake1 the first snake to update
     * @param snake2 the second snake to update
     * @param json the JSON object to update from
     */
    public static boolean updateGame(Game game, Snake snake1, Snake snake2, JSONObject json) {
        // update game board
        int noEatCount1 = json.getInt("noEatCount1");
        int noEatCount2 = json.getInt("noEatCount2");
        int score1 = json.getInt("score1");
        int score2 = json.getInt("score2");
        int ticksPerSecond = json.getInt("TICKS_PER_SECOND");

        game.setNoEatCount1(noEatCount1);
        game.setNoEatCount2(noEatCount2);
        game.setScore1(score1);
        game.setScore2(score2);
        Game.setTicksPerSecond(ticksPerSecond);

        updateSnake(snake1, json);
        updateSnake(snake2, json);

        // update food
        JSONObject foodJson = json.getJSONObject("food").getJSONObject("food");
        game.getFood().add(new Position(foodJson.getInt("posX"), foodJson.getInt("posY")));

        // update achievements
        return !updateAchievements(game, snake1, snake2, json);
    }

    private static boolean updateAchievements(Game game, Snake snake1, Snake snake2, JSONObject json) {
        for (Object jsonObject : json.getJSONObject("achievements").getJSONArray("achievements")) {
            JSONObject savedAchievement = (JSONObject) jsonObject;
            String title = savedAchievement.getString("title");
            String description = savedAchievement.getString("description");
            String snakeName = savedAchievement.getString("snake");
            Snake snake = snakeName.equals(snake1.getName()) ? snake1 : snake2;
            try {
                // if it is a statistical achievement
                StatisticalAchievement gameAchievement = (StatisticalAchievement) game.getAchievements()
                        .getAchievement(title, snake);
                gameAchievement.setValue(savedAchievement.getDouble("value"));
            } catch (Exception e) {
                game.getAchievements().addAchievement(new GeneralAchievement(title, description, snake));
                return true;
            }

        }
        return false;
    }

    /**
     * REQUIRES: game, snake are valid and not null; json is a valid JSON object
     * MODIFIES: game, snake
     * EFFECTS: updates the given snake with the data from the given JSON object
     * 
     * @param snake the snake to update
     * @param json  the JSON object to update from
     */
    private static void updateSnake(Snake snake, JSONObject json) {
        // update snakes
        String snakeName = snake.getName();
        if (snakeName.equals("one")) {
            snakeName = "snake1";
        } else {
            snakeName = "snake2";
        }
        JSONObject snakeJson = json.getJSONObject(snakeName);

        snake.setHead(new Position(snakeJson.getJSONObject("head").getInt("posX"),
                snakeJson.getJSONObject("head").getInt("posY")));
        snake.setDirection(Direction.valueOf(snakeJson.getJSONObject("direction").getString("name")));

        // update snake body
        List<Position> body = snake.getBody();
        for (Object bodyPart : snakeJson.getJSONArray("body")) {
            body.add(new Position(((JSONObject) bodyPart).getInt("posX"), ((JSONObject) bodyPart).getInt("posY")));
        }

    }
}
