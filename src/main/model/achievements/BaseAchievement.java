package model.achievements;

import model.Snake;

public class BaseAchievement implements Achievement {
    private final String title;
    private final String description;
    private final Snake snake;

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
}