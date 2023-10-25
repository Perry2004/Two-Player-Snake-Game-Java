package model.achievements;

import model.Snake;

/**
 * GeneralAchievement is an achievement that can be earned by the player when
 * certain conditions are met.
 * They do not have a value associated with them.
 */
public class GeneralAchievement extends BaseAchievement {

    /**
     * EFFECTS: constructs a general achievement with the given title, description,
     * and snake
     * 
     * @param title       the title of the achievement
     * @param description the description of the achievement
     * @param snake       the snake that the achievement is associated with
     */
    public GeneralAchievement(String title, String description, Snake snake) {
        super(title, description, snake);
    }

    @Override
    public String toString() {
        return "Achievement: " + getTitle() + " - " + getDescription();
    }
}