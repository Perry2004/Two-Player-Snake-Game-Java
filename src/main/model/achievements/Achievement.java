package model.achievements;

import model.Snake;

/**
 * This interface represents an achievement that can be earned by the player.
 */
public interface Achievement {

    /**
     * EFFECTS: returns true if the achievement is earned, false otherwise
     * @param achievement the achievement to compare to
     * @return true if the achievement is earned, false otherwise
     */
    boolean equals(Achievement achievement);

    /**
     * EFFECTS: returns the title of the achievement
     * @return the title of the achievement
     */
    String getTitle();

    /**
     * EFFECTS: returns the description of the achievement
     * @return the description of the achievement
     */
    String getDescription();

    /**
     * EFFECTS: returns the snake that the achievement is associated with
     * @return the snake that the achievement is associated with
     */
    Snake getSnake();

    /**
     * EFFECTS: returns the string representation of the achievement
     * @return the string representation of the achievement
     */
    String toString();

    /**
     * EFFECTS: returns the value of the achievement
     * @return the value of the achievement
     */
    double getValue();

    /**
     * MODIFIES: this
     * EFFECTS: updates the value of the achievement
     * @param value the value to update the achievement with
     */
    void updateValue(double value);
}
