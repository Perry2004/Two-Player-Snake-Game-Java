package model.achievements;

import model.Snake;

public class StatisticalAchievement extends BaseAchievement {
    private double value;

    public StatisticalAchievement(String title, String description, Snake snake, double value) {
        super(title, description, snake);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void updateValue(double value) {
        this.value += value;
    }

    public String toString() {
        return this.getTitle() + "\n\t" + this.getDescription() + " => " + this.getValue() + "\n\tBelongs to: "
                + this.getSnake().getName();
    }

}
