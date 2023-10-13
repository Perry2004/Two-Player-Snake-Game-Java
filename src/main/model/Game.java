package model;

import model.achievements.GeneralAchievement;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents the SnakeGame as a whole
 */
public class Game {
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

    public Game(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;

        this.snake1 = new Snake(1, 1, "one");
        this.snake2 = new Snake(1, maxY - 1, "two");

        this.achievements = new AchievementCollection(snake1, snake2);

        food.add(generateRandomPosition());
    }

    public static int getTicksPerSecond() {
        return TICKS_PER_SECOND;
    }

    public static void setTicksPerSecond(int ticksPerSecond) {
        TICKS_PER_SECOND = ticksPerSecond;
    }

    /**
     * Progresses the game state, moving the snake, and handling
     * food
     */
    public void tick() {
        updateAchievement();
        snake1.move();
        snake2.move();

        if (snake1.hasCollidedWithSelf() || isOutOfBounds(snake1.getHead()) || snake2.hasCollidedWithSelf()
                || isOutOfBounds(snake2.getHead()) || snake1.hasCollided(snake2.getHead())
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
     * Spawns a new food item into a valid
     * position in the game
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
     * Returns whether a given position is
     * out of the game frame
     */
    public boolean isOutOfBounds(Position pos) {
        return pos.getPosX() < 0 || pos.getPosY() < 0 || pos.getPosX() > maxX || pos.getPosY() > maxY;
    }

    /**
     * Returns whether a given position is in bounds
     * and not already occupied
     */
    public boolean isValidPosition(Position pos) {
        return !isOutOfBounds(pos) && !food.contains(pos) && !snake1.hasCollided(pos) && !snake2.hasCollided(pos);
    }

    /**
     * Checks for food that the snake has eaten,
     * grows the snake and increases score if food is found
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
            this.achievements.getAchievement("Apples Eaten")
                    .updateValue(achievements.getAchievement("Apples Eaten").getValue() + 1);
        } else if (snake2.hasCollided(eatenFood)) {
            score2++;
            snake2.grow();
            noEatCount2 = 0;
            noEatCount1++;
            achievements.getAchievement("Apples Eaten")
                    .updateValue(achievements.getAchievement("Apples Eaten").getValue() + 1);
        } else {
            noEatCount1++;
            noEatCount2++;
        }
        food.remove(eatenFood);
    }

    /**
     * Generates a random position.
     * Guaranteed to be in bounds but not necessarily valid
     */
    private Position generateRandomPosition() {
        return new Position(
                ThreadLocalRandom.current().nextInt(maxX),
                ThreadLocalRandom.current().nextInt(maxY));
    }

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

    public Snake getSnake1() {
        return snake1;
    }

    public Snake getSnake2() {
        return snake2;
    }

    public Set<Position> getFood() {
        return food;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public AchievementCollection getAchievements() {
        return achievements;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public boolean isEnded() {
        return ended;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getNoEatCount1() {
        return noEatCount1;
    }

    public void setNoEatCount1(int noEatCount1) {
        this.noEatCount1 = noEatCount1;
    }

    public int getNoEatCount2() {
        return noEatCount2;
    }

    public void setNoEatCount2(int noEatCount2) {
        this.noEatCount2 = noEatCount2;
    }

}
