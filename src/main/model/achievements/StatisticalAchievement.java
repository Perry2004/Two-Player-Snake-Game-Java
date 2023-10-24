package model.achievements;

import org.json.JSONObject;

import model.Snake;

/**
 * StatisticalAchievement is a type of achievement that has a value associated.
 * The values are updated when certain actions are performed.
 */
public class StatisticalAchievement extends BaseAchievement {
    private double value;

    /**
     * REQUIRES: title and description are non-empty strings, snake is a valid snake
     * 
     * @param title       the title of the achievement
     * @param description the description of the achievement
     * @param snake       the snake that the achievement is associated with
     * @param value       the value of the achievement
     */
    public StatisticalAchievement(String title, String description, Snake snake, double value) {
        super(title, description, snake);
        this.value = value;
    }

    /**
     * EFFECTS: returns true if the achievement is earned, false otherwise
     * 
     * @return the value of the achievement
     */
    public double getValue() {
        return value;
    }

    /**
     * EFFECTS: updates the value by adding the given value to the current value
     */
    public void updateValue(double value) {
        this.value += value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString() {
        return this.getTitle() + "\n\t" + this.getDescription() + " => " + this.getValue() + "\n\tBelongs to: "
                + this.getSnake().getName();
    }

    /**
     * EFFECTS: returns the JSON representation of this achievement
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("title", this.getTitle());
        json.put("description", this.getDescription());
        json.put("snake", this.getSnake().getName());
        json.put("value", this.getValue());
        return json;
    }

}
