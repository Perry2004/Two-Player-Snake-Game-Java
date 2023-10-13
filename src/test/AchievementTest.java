import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.AchievementCollection;
import model.Snake;
import model.achievements.Achievement;
import model.achievements.GeneralAchievement;
import model.achievements.StatisticalAchievement;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementTest {
    private AchievementCollection ac;
    private Snake snake1;
    private Achievement a1;
    private Achievement a2;

    @BeforeEach
    public void setUp() {
        snake1 = new Snake(1, 1, "one");
        Snake snake2 = new Snake(1, 2, "two");
        ac = new AchievementCollection(snake1, snake2);
        a1 = new GeneralAchievement("a1", "a1", snake1);
        a2 = new StatisticalAchievement("a2", "a2", snake1, 1);
    }

    @Test
    public void testAddDuplicateAchievement() {
        ac.addAchievement(a1);
        assertFalse(ac.addAchievement(a1));
    }

    @Test
    public void testGetNoExistingAchievement() {
        ac.addAchievement(a1);
        assertEquals(a1, ac.getAchievement("a1", snake1));
        assertNull(ac.getAchievement("a2", snake1));
    }

    @Test
    public void testToString() {
        ac.addAchievement(a1);
        ac.addAchievement(a2);

        StringBuilder expectAcStr = new StringBuilder();
        for (Achievement a : ac.getAchievements()) {
            expectAcStr.append(a.toString()).append("\n");
        }

        assertEquals(expectAcStr.toString(), ac.toString());
    }

    @Test
    public void testEquals() {
        a1 = new GeneralAchievement("a1", "a1", snake1);
        a2 = new StatisticalAchievement("a2", "a2", snake1, 1);
        Achievement a3 = new GeneralAchievement("a1", "a1", snake1);
        Achievement a4 = new GeneralAchievement("a1", "different", snake1);
        Achievement a5 = new GeneralAchievement("a1", "a1", new Snake(0, 0, "different"));
        assertTrue(a1.equals(a3));
        assertFalse(a1.equals(a2));
        assertFalse(a1.equals(a4));
        assertFalse(a1.equals(a5));
    }

    @Test
    public void testGetValue() {
        a1 = new GeneralAchievement("a1", "a1", snake1);
        a2 = new StatisticalAchievement("a2", "a2", snake1, 1);
        assertEquals(0, a1.getValue());
        assertEquals(1, a2.getValue());
    }

    @Test
    public void testUpdateValue() {
        a1 = new GeneralAchievement("a1", "a1", snake1);
        a2 = new StatisticalAchievement("a2", "a2", snake1, 1);
        a1.updateValue(1);
        a2.updateValue(1);
        assertEquals(0, a1.getValue());
        assertEquals(2, a2.getValue());
    }
}
