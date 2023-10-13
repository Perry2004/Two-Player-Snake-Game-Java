package model.achievements;

import model.Snake;

public class GeneralAchievement extends BaseAchievement {
    public GeneralAchievement(String title, String description, Snake snake) {
        super(title, description, snake);
    }

    @Override
    public String toString() {
        return "Achievement: " + getTitle() + " - " + getDescription();
    }
}