package model;

import java.util.ArrayList;

import org.json.JSONObject;

import model.achievements.Achievement;
import model.achievements.StatisticalAchievement;
import persistence.Jsonizable;

/**
 * AchievementCollection is a collection of achievements that can be earned by
 * the player.
 * It provides methods to add unique achievements and get achievements.
 */
public class AchievementCollection implements Jsonizable {
    private final ArrayList<Achievement> achievements;

    /**
     * REQUIRE: snake is a valid snake
     * EFFECTS: constructs a new achievement collection, initiates the statistical
     * achievements
     * 
     * @param snake1 the first snake
     * @param snake2 the second snake
     */
    public AchievementCollection(Snake snake1, Snake snake2) {
        this.achievements = new ArrayList<>();
        setUpStats(snake1, snake2);
    }

    /**
     * REQUIRE: snake1 != null && snake2 != null
     * MODIFIES: this, EventLog
     * EFFECTS: sets up the statistical achievements for the given snakes.
     * Initializes the values to 0.
     * Logs the event.
     * 
     * @param snake1 the first snake
     * @param snake2 the second snake
     */
    public void setUpStats(Snake snake1, Snake snake2) {
        setUpStats(snake1);
        EventLog.getInstance().logEvent(new Event("Initialized statistical achievements for " + snake1.getName()));
        setUpStats(snake2);
        EventLog.getInstance().logEvent(new Event("Initialized statistical achievements for " + snake2.getName()));
    }

    /**
     * REQUIRE: snake != null
     * EFFECTS: sets up the statistical achievements for the given snake.
     * Initializes the values to 0.
     * 
     * @param snake the snake
     */
    private void setUpStats(Snake snake) {
        achievements.add(new StatisticalAchievement("Key Stroke", "Number of keys pressed", snake, 0));
        achievements.add(new StatisticalAchievement("Step Upwards", "Number of steps going upwards", snake, 0));
        achievements.add(new StatisticalAchievement("Step Downwards", "Number of steps going downwards", snake, 0));
        achievements.add(new StatisticalAchievement("Step Leftwards", "Number of steps going leftwards", snake, 0));
        achievements.add(new StatisticalAchievement("Step Rightwards", "Number of steps going rightwards", snake, 0));
        achievements.add(new StatisticalAchievement("Total Rounds", "Number of rounds played", snake, 0));
        achievements.add(new StatisticalAchievement("Apples Eaten", "Number of apples eaten", snake, 0));
    }

    /**
     * REQUIRES: achievement != null
     * EFFECTS: adds the given achievement to the collection if it is not already in
     * the collection
     * 
     * @param achievement the achievement to be added
     * @return true if the achievement was added, false otherwise
     */
    public boolean addAchievement(Achievement achievement) {
        if (!this.achievements.contains(achievement)) {
            this.achievements.add(achievement);
            EventLog.getInstance().logEvent(new Event("Added achievement " + achievement.getTitle()));
            return true;
        }
        EventLog.getInstance().logEvent(new Event("Failed to add achievement " + achievement.getTitle()));
        return false;
    }

    /**
     * EFFECTS: returns the achievements in the collection
     * 
     * @return the achievements in the collection
     */
    public ArrayList<Achievement> getAchievements() {
        return this.achievements;
    }

    /**
     * REQUIRES: title != null && snake != null
     * EFFECTS: returns the achievement with the given title and snake, null if it
     * does not exist
     * 
     * @param title the title of the achievement
     * @param snake the snake of the achievement
     * @return the achievement with the given title and snake, null if it does not
     *         exist
     */
    public Achievement getAchievement(String title, Snake snake) {
        for (Achievement achievement : this.achievements) {
            if (achievement.getTitle().equals(title) && achievement.getSnake().equals(snake)) {
                return achievement;
            }
        }
        return null;
    }

    /**
     * EFFECTS: returns the string representation of the achievement collection
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Achievement achievement : this.achievements) {
            result.append(achievement.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * EFFECTS: returns the JSON representation of the achievement collection that
     * contains the JSON representation of each achievement
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        for (Achievement achievement : this.achievements) {
            json.append("achievements", achievement.toJson());
        }
        return json;
    }
}
