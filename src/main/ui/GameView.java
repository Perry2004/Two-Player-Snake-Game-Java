package ui;

import model.Direction;
import model.Game;
import model.Position;
import model.Snake;
import model.achievements.Achievement;
import model.achievements.GeneralAchievement;
import persistence.JsonLoader;
import persistence.JsonSaver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GameView implements ActionListener, KeyListener {
    public static final int ROWS = 30;
    public static final int COLUMNS = 50;
    public static final int GRID_SIZE = 10;
    private final Game game;
    JDialog selectDifficultyWindow;
    JDialog loadWindow;
    JButton yesLoadButton;
    JButton noLoadButton;
    JFrame gameWindow;
    JPanel gamePanel;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem saveMenuItem;
    JPanel scorePanel;
    JTextPane achievementsPanel;
    JScrollPane achievementScrollPane;
    JComboBox<String> achievementFilterComboBox;
    JPanel difficultyPanel;
    JComboBox<String> difficultyComboBox;
    Timer gameTimer;
    ActionListener gameLoop = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            tick();
            // update round achievements
            game.getAchievements().getAchievement("Total Rounds", game.getSnake1()).updateValue(1);
            game.getAchievements().getAchievement("Total Rounds", game.getSnake2()).updateValue(1);
            // update step for different directions
            updateStepAchievement(game.getSnake1());
            updateStepAchievement(game.getSnake2());
            checkSpeedyAchievement();
        }
    };
    private boolean isPaused = false;

    public GameView() {
        game = new Game(COLUMNS, ROWS);
    }

    static void updateStep(Snake snake, Game game) {
        switch (snake.getDirection()) {
            case UP:
                game.getAchievements().getAchievement("Step Upwards", snake).updateValue(1);
                break;
            case DOWN:
                game.getAchievements().getAchievement("Step Downwards", snake).updateValue(1);
                break;
            case LEFT:
                game.getAchievements().getAchievement("Step Leftwards", snake).updateValue(1);
                break;
            case RIGHT:
                game.getAchievements().getAchievement("Step Rightwards", snake).updateValue(1);
                break;
            case PAUSE:
                break;

        }
    }

    private static void getStatisticalAchievements(ArrayList<Achievement> allAchievements,
            ArrayList<Achievement> selectedAchievements) {
        for (Achievement achievement : allAchievements) {
            if (achievement.getClass().getSimpleName().equals("StatisticalAchievement")) {
                selectedAchievements.add(achievement);
            }
        }
    }

    private static void getGeneralAchievements(ArrayList<Achievement> allAchievements,
            ArrayList<Achievement> selectedAchievements) {
        for (Achievement achievement : allAchievements) {
            if (achievement.getClass().getSimpleName().equals("GeneralAchievement")) {
                selectedAchievements.add(achievement);
            }
        }
    }

    public void beginTicks() {
        gameTimer = new Timer(1000 / Game.getTicksPerSecond(), gameLoop);
        gameTimer.start();
    }

    public void tick() {
        game.tick();
        if (game.isEnded()) {
            setUpEndWindow();
        }
        render();
        gameWindow.revalidate();
        gameWindow.repaint();
    }

    public void render() {
        // clear the board
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            JPanel grid = (JPanel) gamePanel.getComponent(i);
            grid.setBackground(Color.WHITE);
        }
        drawScore();
        drawSnake();
        drawFood();
    }

    public void drawScore() {
        scorePanel.removeAll();
        scorePanel.add(new JLabel("Score 1: \t" + game.getScore1()));
        scorePanel.add(new JLabel("Score 2: \t" + game.getScore2()));
        scorePanel.revalidate();
        scorePanel.repaint();
    }

    public void drawSnake() {
        Snake snake1 = game.getSnake1();
        setColorByPosition(snake1.getHead(), Color.BLUE);
        for (Position pos : snake1.getBody()) {
            setColorByPosition(pos, Color.CYAN);
        }

        Snake snake2 = game.getSnake2();
        setColorByPosition(snake2.getHead(), Color.GREEN);
        for (Position pos : snake2.getBody()) {
            setColorByPosition(pos, Color.ORANGE);
        }
    }

    public void drawFood() {
        for (Position pos : game.getFood()) {
            setColorByPosition(pos, Color.RED);
            break;
        }
    }

    private void checkSpeedyAchievement() {
        // if the snake has already gotten the achievement, don't add it again
        if (game.getAchievements().getAchievement("The Speedy", game.getSnake1()) != null || game.getAchievements()
                .getAchievement("The Speedy", game.getSnake2()) != null) {
            return;
        }

        if (game.getAchievements().getAchievement("Total Rounds", game.getSnake1()).getValue() <= 10) {
            game.getAchievements().addAchievement(
                    new GeneralAchievement("The Speedy", "Finish the game in less than 20 rounds", game.getSnake1()));
            game.getAchievements().addAchievement(
                    new GeneralAchievement("The Speedy", "Finish the game in less than 20 rounds", game.getSnake2()));
        }
    }

    public void setColorByPosition(Position p, Color color) {
        int index = (p.getPosY() * COLUMNS) - 1 + p.getPosX() - 1;
        if (index < 0 || index >= ROWS * COLUMNS) {
            return;
        }
        JPanel grid = (JPanel) gamePanel.getComponent(index);
        grid.setBackground(color);
    }

    public void displaySelectDifficultyWindow() {
        selectDifficultyWindow = new JDialog(gameWindow, "Snake Game");
        difficultyPanel = new JPanel();
        selectDifficultyWindow.add(difficultyPanel);
        difficultyPanel.add(new JLabel("Please select difficulty"));
        difficultyComboBox = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
        difficultyComboBox.setSelectedItem(null);
        difficultyComboBox.addActionListener(this);
        difficultyPanel.add(difficultyComboBox);
        selectDifficultyWindow.pack();
        selectDifficultyWindow.setVisible(true);
    }

    public void displayLoadWindow() {
        loadWindow = new JDialog(gameWindow, "Snake Game");
        JPanel loadPanel = new JPanel();
        loadPanel.add(new JLabel("Do you want to load the previous game?"));
        yesLoadButton = new JButton("Yes");
        yesLoadButton.addActionListener(this);
        loadPanel.add(yesLoadButton);
        noLoadButton = new JButton("No");
        noLoadButton.addActionListener(this);
        loadPanel.add(noLoadButton);
        loadWindow.add(loadPanel);
        loadWindow.pack();
        loadWindow.setVisible(true);
    }

    public JMenuBar setUpMenuBar() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.addActionListener(this);
        menuBar.add(fileMenu);
        saveMenuItem = new JMenuItem("Save Game");
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);
        return menuBar;
    }

    public JPanel createGrids() {
        this.gamePanel = new JPanel();
        this.gamePanel.setLayout(new GridLayout(ROWS, COLUMNS));
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            JPanel grid = new JPanel();
            grid.setSize(GRID_SIZE, GRID_SIZE);
            grid.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
            this.gamePanel.add(grid);
        }
        return this.gamePanel;
    }

    public void setDifficulty(String selectedDifficulty) {
        switch (selectedDifficulty) {
            case "Easy":
                Game.setTicksPerSecond(5);
                break;
            case "Medium":
                Game.setTicksPerSecond(10);
                break;
            case "Hard":
                Game.setTicksPerSecond(15);
                break;
        }
    }

    public void setUpGameWindow() {
        gameWindow = new JFrame("Snake Game");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setResizable(false);
        gameWindow.add(createGrids());
        gameWindow.add(setUpMenuBar(), BorderLayout.NORTH);

        scorePanel = new JPanel();
        scorePanel.add(new JLabel("Score 1: \t" + game.getScore1()));
        scorePanel.add(new JLabel("Score 2: \t" + game.getScore2()));
        gameWindow.add(scorePanel, BorderLayout.SOUTH);

        gameWindow.addKeyListener(this);
        gameWindow.pack();
        gameWindow.setResizable(false);
        gameWindow.setVisible(true);

        try {
            beginTicks();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public void setUpEndWindow() {
        gameWindow.setVisible(false);
        gameWindow.getContentPane().removeAll();
        JPanel endPanel = new JPanel();
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 50));
        endPanel.add(gameOverLabel);
        gameWindow.add(endPanel, BorderLayout.CENTER);

        setUpAchievementPanel();

        gameWindow.pack();
        gameWindow.setVisible(true);

        gameTimer.stop();
        game.endGame();
    }

    private void setUpAchievementPanel() {
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        achievementPanel.add(new JLabel("Achievements"));
        achievementFilterComboBox = new JComboBox<>(
                new String[] { "All", "Snake 1", "Snake 2", "Special", "Statistical" });
        achievementFilterComboBox.setSelectedItem(null);
        achievementPanel.add(achievementFilterComboBox);
        achievementFilterComboBox.addActionListener(this);

        gameWindow.add(achievementPanel, BorderLayout.AFTER_LAST_LINE);
        achievementsPanel = new JTextPane();
        achievementsPanel.setPreferredSize(new Dimension(300, 300));
        achievementScrollPane = new JScrollPane(achievementsPanel);
        achievementScrollPane.setPreferredSize(new Dimension(300, 300));
        achievementScrollPane.setVerticalScrollBar(new JScrollBar());

        achievementPanel.add(achievementScrollPane, BorderLayout.AFTER_LINE_ENDS);
    }

    private void updateStepAchievement(Snake snake) {
        updateStep(snake, game);
    }

    private void updateAchievementsPanel(String filter) {
        ArrayList<Achievement> allAchievements = game.getAchievements().getAchievements();
        ArrayList<Achievement> selectedAchievements = new ArrayList<>();
        if (filter == null) {
            return;
        }
        switch (filter) {
            case "All":
                selectedAchievements = allAchievements;
                break;
            case "Snake 1":
                addAchievementsForSnake(game.getSnake1(), allAchievements, selectedAchievements);
                break;
            case "Snake 2":
                addAchievementsForSnake(game.getSnake2(), allAchievements, selectedAchievements);
                break;
            case "Special":
                getGeneralAchievements(allAchievements, selectedAchievements);
                break;
            case "Statistical":
                getStatisticalAchievements(allAchievements, selectedAchievements);
                break;
        }
        extractAchievementsString(selectedAchievements);
    }

    private void addAchievementsForSnake(Snake s, ArrayList<Achievement> allAchievements,
            ArrayList<Achievement> selectedAchievements) {
        for (Achievement achievement : allAchievements) {
            if (achievement.getSnake() == s) {
                selectedAchievements.add(achievement);
            }
        }
    }

    private void extractAchievementsString(ArrayList<Achievement> selectedAchievements) {
        StringBuilder text = new StringBuilder();
        for (Achievement a : selectedAchievements) {
            text.append(a.toString());
            text.append("\n");
        }
        achievementsPanel.setText(text.toString());
        achievementsPanel.revalidate();
        achievementsPanel.repaint();
        achievementScrollPane.revalidate();
        achievementScrollPane.repaint();
        gameWindow.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == this.yesLoadButton) {
            loadOrNot(true);
        } else if (source == this.noLoadButton) {
            loadOrNot(false);
        } else if (source == this.saveMenuItem) {
            JsonSaver.saveGame("data/save1.json", game);
            game.endGame();
            setUpEndWindow();
        } else if (source == this.difficultyComboBox) {
            if (((JComboBox<?>) source).getSelectedItem() == null) {
                return;
            }
            String selectedDifficulty = ((JComboBox<?>) source).getSelectedItem().toString();
            this.setDifficulty(selectedDifficulty);

            // start the game after the user selects the difficulty
            selectDifficultyWindow.setVisible(false);
            selectDifficultyWindow.dispose();
            this.setUpGameWindow();
        } else if (source == this.achievementFilterComboBox) {
            handleAchievementFilter((JComboBox<?>) source);
        }
    }

    private void handleAchievementFilter(JComboBox<?> source) {
        if (source.getSelectedItem() == null) {
            return;
        }
        String selectedFilter = source.getSelectedItem().toString();
        this.updateAchievementsPanel(selectedFilter);
    }

    private void loadOrNot(boolean load) {
        loadWindow.setVisible(load);
        loadWindow.dispose();
        if (load) {
            JsonLoader.loadGame("data/save1.json", game, game.getSnake1(), game.getSnake2());
            setUpGameWindow();
        } else {
            displaySelectDifficultyWindow();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        handleUserInput(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void handleUserInput(KeyEvent e) {
        int key = e.getKeyCode();

        Direction direction = directionFromKey(e);
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
            game.getSnake2().setDirection(direction);
        } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT
                || key == KeyEvent.VK_RIGHT) {
            game.getSnake1().setDirection(direction);
        } else if (key == KeyEvent.VK_ESCAPE) {
            if (isPaused) {
                gameTimer.start();
                isPaused = false;
            } else {
                gameTimer.stop();
                isPaused = true;
            }
        }
    }

    private Direction directionFromKey(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                return Direction.UP;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                return Direction.DOWN;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                return Direction.RIGHT;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                return Direction.LEFT;
            case KeyEvent.VK_ESCAPE:
                return Direction.PAUSE;
            default:
                return null;
        }
    }
}
