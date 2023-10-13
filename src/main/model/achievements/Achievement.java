package model.achievements;

import model.Snake;

// The class represents the achievements that the user can get
public interface Achievement {
    boolean equals(Achievement achievement);

    String getTitle();

    String getDescription();

    Snake getSnake();

    String toString();

    double getValue();

    void updateValue(double value);
}
