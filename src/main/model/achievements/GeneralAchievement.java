package model.achievements;

import model.Snake;

/**
 * GeneralAchievement is an achievement that can be earned by the player when certain conditions are met.
 * They do not have a value associated with them.
 */
public class GeneralAchievement extends BaseAchievement {
    public GeneralAchievement(String title, String description, Snake snake) {
        super(title, description, snake);
    }

    @Override
    public String toString() {
        return "Achievement: " + getTitle() + " - " + getDescription();
    }
}