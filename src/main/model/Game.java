package model;

import model.achievements.GeneralAchievement;
import persistence.Jsonizable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;

/**
 * The class that contains the game state
 */
public class Game implements Jsonizable {
    private static int TICKS_PER_SECOND;
    private final Snake snake1;
    private final Snake snake2;
    private final Set<Position> food = new HashSet<>();
    private final int maxX;
    private final int maxY;
    private final AchievementCollection achievements;
    private int score1 = 0;
    private int score2 = 0;
    private boolean ended = false;
    private int noEatCount1 = 0;
    private int noEatCount2 = 0;

    /**
     * REQUIRE: maxX and maxY are positive integers
     * EFFECTS: constructs a new game with the given dimensions
     * 
     * @param maxX the maximum width
     * @param maxY the maximum height
     */
    public Game(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;

        this.snake1 = new Snake(1, 1, "one");
        this.snake2 = new Snake(1, maxY - 1, "two");

        this.achievements = new AchievementCollection(snake1, snake2);

        food.add(generateRandomPosition());
    }

    /**
     * EFFECTS: returns the number of ticks per second (difficulty)
     * 
     * @return the number of ticks per second
     */
    public static int getTicksPerSecond() {
        return TICKS_PER_SECOND;
    }

    /**
     * REQUIRE: ticksPerSecond is a positive integer
     * EFFECTS: sets the number of ticks per second (difficulty)
     * 
     * @param ticksPerSecond the number of ticks per second
     */
    public static void setTicksPerSecond(int ticksPerSecond) {
        TICKS_PER_SECOND = ticksPerSecond;
    }

    /**
     * MODIFIES: this
     * EFFECTS: moves the snakes, handles food and checks for collisions
     */
    public void tick() {
        updateAchievement();
        snake1.move();
        snake2.move();

        if (snake1.hasCollidedWithSelf()
                || isOutOfBounds(snake1.getHead())
                || snake2.hasCollidedWithSelf()
                || isOutOfBounds(snake2.getHead())
                || snake1.hasCollided(snake2.getHead())
                || snake2.hasCollided(snake1.getHead())) {

            ended = true;
            return;
        }

        handleFood();

        if (food.isEmpty()) {
            spawnNewFood();
        }
    }

    /**
     * MODIFIES: this
     * EFFECTS: spawns a new food at a random position that is not occupied
     */
    public void spawnNewFood() {
        Position pos = generateRandomPosition();

        while (!isValidPosition(pos)) {
            // check if the apple attempts to spawn on the snake (achievement)
            // check snake 1
            if (snake1.hasCollided(pos)) {
                achievements.addAchievement(new GeneralAchievement("The Chosen One",
                        "The apple attempts to spawn inside a snake's body", snake1));
            } else if (snake2.hasCollided(pos)) {
                achievements.addAchievement(new GeneralAchievement("The Chosen One",
                        "The apple attempts to spawn inside a snake's body", snake2));
            }

            pos = generateRandomPosition();
        }

        food.add(pos);
    }

    /**
     * REQUIRE: pos != null
     * EFFECTS: returns whether a given position is out of bounds
     * 
     * @param pos the position
     * @return whether a given position is out of bounds
     */
    public boolean isOutOfBounds(Position pos) {
        return pos.getPosX() < 0 || pos.getPosY() < 0 || pos.getPosX() > maxX || pos.getPosY() > maxY;
    }

    /**
     * REQUIRE: pos != null
     * EFFECTS: returns whether a given position is valid (not out of bounds, not
     * occupied by food or snakes)
     * 
     * @param pos the position
     * @return whether a given position is valid
     */
    public boolean isValidPosition(Position pos) {
        return !isOutOfBounds(pos) && !food.contains(pos) && !snake1.hasCollided(pos) && !snake2.hasCollided(pos);
    }

    /**
     * MODIFIES: this
     * EFFECTS: checks if the snakes eat the food and handles it accordingly
     */
    private void handleFood() {
        Position eatenFood = food.stream().filter(snake1::hasCollided).findFirst().orElse(null);
        if (eatenFood == null) {
            eatenFood = food.stream().filter(snake2::hasCollided).findFirst().orElse(null);
        }

        if (snake1.hasCollided(eatenFood)) {
            score1++;
            snake1.grow();
            noEatCount1 = 0;
            noEatCount1++;
            this.achievements.getAchievement("Apples Eaten", snake1)
                    .updateValue(achievements.getAchievement("Apples Eaten", snake1).getValue() + 1);
        } else if (snake2.hasCollided(eatenFood)) {
            score2++;
            snake2.grow();
            noEatCount2 = 0;
            noEatCount1++;
            achievements.getAchievement("Apples Eaten", snake2)
                    .updateValue(achievements.getAchievement("Apples Eaten", snake2).getValue() + 1);
        } else {
            noEatCount1++;
            noEatCount2++;
        }
        food.remove(eatenFood);
    }

    /**
     * EFFECTS: generates a random position within the bounds of the game
     * 
     * @return a random position within the bounds of the game
     */
    private Position generateRandomPosition() {
        return new Position(
                ThreadLocalRandom.current().nextInt(maxX),
                ThreadLocalRandom.current().nextInt(maxY));
    }

    /**
     * MODIFIES: this
     * EFFECTS: check whether the general achievements are fulfilled and adds them
     * to the collection if they are
     */
    public void updateAchievement() {
        if (noEatCount1 >= 1000) {
            achievements.addAchievement(new GeneralAchievement("The Survivor",
                    "Survive for 1000 rounds without eating any apples or colliding", snake1));
        }
        if (noEatCount2 >= 1000) {
            achievements.addAchievement(new GeneralAchievement("The Survivor",
                    "Survive for 1000 rounds without eating any apples or colliding", snake2));
        }
        if (score1 >= 100) {
            achievements.addAchievement(new GeneralAchievement("The Eater", "Eat 100 apples in total", snake1));
        }
        if (score2 >= 100) {
            achievements.addAchievement(new GeneralAchievement("The Eater", "Eat 100 apples in total", snake2));
        }
    }

    /**
     * EFFECTS: returns the first snake
     * 
     * @return the first snake
     */
    public Snake getSnake1() {
        return snake1;
    }

    /**
     * EFFECTS: returns the second snake
     * 
     * @return the second snake
     */
    public Snake getSnake2() {
        return snake2;
    }

    /**
     * EFFECTS: returns the food
     * 
     * @return the food
     */
    public Set<Position> getFood() {
        return food;
    }

    /**
     * EFFECTS: returns the score of the first snake
     * 
     * @return the score of the first snake
     */
    public int getScore1() {
        return score1;
    }

    /**
     * MODIFIES: this
     * EFFECTS: sets the score of the first snake
     * 
     * @param score1 the score of the first snake
     */
    public void setScore1(int score1) {
        this.score1 = score1;
    }

    /**
     * EFFECTS: returns the achievements
     * 
     * @return the achievements
     */
    public AchievementCollection getAchievements() {
        return achievements;
    }

    /**
     * EFFECTS: returns the score of the second snake
     * 
     * @return the score of the second snake
     */
    public int getScore2() {
        return score2;
    }

    /**
     * MODIFIES: this
     * EFFECTS: sets the score of the second snake
     * 
     * @param score2 the score of the second snake
     */
    public void setScore2(int score2) {
        this.score2 = score2;
    }

    /**
     * EFFECTS: returns whether the game has ended
     * 
     * @return whether the game has ended
     */
    public boolean isEnded() {
        return ended;
    }

    public void endGame() {
        ended = true;
    }

    /**
     * EFFECTS: returns the maximum width
     * 
     * @return the maximum width
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * EFFECTS: returns the maximum height
     * 
     * @return the maximum height
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * EFFECTS: returns the number of rounds without eating any apples or colliding
     * for the first snake
     * 
     * @return the number of rounds without eating any apples or colliding for the
     *         first snake
     */
    public int getNoEatCount1() {
        return noEatCount1;
    }

    /**
     * MODIFIES: this
     * EFFECTS: sets the number of rounds without eating any apples or colliding for
     * the first snake
     * 
     * @param noEatCount1 the number of rounds without eating any apples or
     *                    colliding for the first snake
     */
    public void setNoEatCount1(int noEatCount1) {
        this.noEatCount1 = noEatCount1;
    }

    /**
     * EFFECTS: returns the number of rounds without eating any apples or colliding
     * for the second snake
     * 
     * @return the number of rounds without eating any apples or colliding for the
     *         second snake
     */
    public int getNoEatCount2() {
        return noEatCount2;
    }

    /**
     * MODIFIES: this
     * EFFECTS: sets the number of rounds without eating any apples or colliding for
     * the second snake
     * 
     * @param noEatCount2 the number of rounds without eating any apples or
     *                    colliding for the second snake
     */
    public void setNoEatCount2(int noEatCount2) {
        this.noEatCount2 = noEatCount2;
    }

    /**
     * EFFECTS: returns the JSON representation of the food
     * 
     * @return the JSON representation of the food
     */
    public JSONObject foodToJson() {
        JSONObject json = new JSONObject();
        for (Position pos : food) {
            json.put("food", pos.toJson());
        }
        return json;
    }

    /**
     * EFFECTS: returns the JSON representation of the game
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("snake1", snake1.toJson());
        json.put("snake2", snake2.toJson());
        json.put("food", foodToJson());
        json.put("score1", score1);
        json.put("score2", score2);
        json.put("noEatCount1", noEatCount1);
        json.put("noEatCount2", noEatCount2);
        json.put("achievements", achievements.toJson());
        json.put("TICKS_PER_SECOND", TICKS_PER_SECOND);
        return json;
    }

}
