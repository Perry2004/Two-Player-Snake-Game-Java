package model.achievements;

import org.json.JSONObject;

import model.Snake;
import persistence.JSONizable;

/**
 * The abstract class for achievements that each achievement inherits from.
 */
public class BaseAchievement implements Achievement, JSONizable {
    private final String title;
    private final String description;
    private final Snake snake;

    /**
     * REQUIRES: title and description are non-empty strings, snake is a valid snake
     * EFFECTS: constructs a new achievement with the given title, description, and
     * snake
     * 
     * @param title       the title of the achievement
     * @param description the description of the achievement
     * @param snake       the snake that the achievement is associated with
     */
    public BaseAchievement(String title, String description, Snake snake) {
        this.title = title;
        this.description = description;
        this.snake = snake;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Snake getSnake() {
        return snake;
    }

    @Override
    public boolean equals(Achievement achievement) {
        return this.title.equals(achievement.getTitle())
                && this.description.equals(achievement.getDescription()) && this.snake == achievement.getSnake();
    }

    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public void updateValue(double value) {
    }

    /**
     * EFFECTS: returns the JSON representation of this achievement
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);
        json.put("snake", snake.toJson());
        return json;
    }
}