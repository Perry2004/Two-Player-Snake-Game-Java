package model;

import java.util.ArrayList;

import model.achievements.Achievement;
import model.achievements.StatisticalAchievement;

public class AchievementCollection {
    private final ArrayList<Achievement> achievements;

    public AchievementCollection(Snake snake1, Snake snake2) {
        this.achievements = new ArrayList<>();
        setUpStats(snake1, snake2);
    }

    public void setUpStats(Snake snake1, Snake snake2) {
        setUpStats(snake1);

        setUpStats(snake2);
    }

    private void setUpStats(Snake snake2) {
        achievements.add(new StatisticalAchievement("Key Stroke", "Number of keys pressed", snake2, 0));
        achievements.add(new StatisticalAchievement("Step Upwards", "Number of steps going upwards", snake2, 0));
        achievements.add(new StatisticalAchievement("Step Downwards", "Number of steps going downwards", snake2, 0));
        achievements.add(new StatisticalAchievement("Step Leftwards", "Number of steps going leftwards", snake2, 0));
        achievements.add(new StatisticalAchievement("Step Rightwards", "Number of steps going rightwards", snake2, 0));
        achievements.add(new StatisticalAchievement("Total Rounds", "Number of rounds played", snake2, 0));
        achievements.add(new StatisticalAchievement("Apples Eaten", "Number of apples eaten", snake2, 0));
    }

    public boolean addAchievement(Achievement achievement) {
        if (!this.achievements.contains(achievement)) {
            this.achievements.add(achievement);
            return true;
        }
        return false;
    }

    public ArrayList<Achievement> getAchievements() {
        return this.achievements;
    }

    public Achievement getAchievement(String title) {
        for (Achievement achievement : this.achievements) {
            if (achievement.getTitle().equals(title)) {
                return achievement;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Achievement achievement : this.achievements) {
            result.append(achievement.toString()).append("\n");
        }
        return result.toString();
    }
}
