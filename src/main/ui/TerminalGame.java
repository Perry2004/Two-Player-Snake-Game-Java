package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.ScrollBar;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import model.Direction;
import model.Game;
import model.Position;
import model.Snake;
import model.achievements.Achievement;
import model.achievements.GeneralAchievement;

import java.io.IOException;
import java.util.Scanner;

public class TerminalGame {
    private Game game;
    private Screen screen;
    private WindowBasedTextGUI endGui;

    /**
     * Begins the game and method does not leave execution
     * until game is complete.
     */
    public void start() throws IOException, InterruptedException {
        int difficulty = 0;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Please enter the difficulty level (1-3): ");
            while (difficulty < 1 || difficulty > 3) {
                difficulty = scanner.nextInt();
            }
        }

        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        TerminalSize terminalSize = screen.getTerminalSize();

        game = new Game(
                // divide the columns in two
                (terminalSize.getColumns() - 1) / 2,
                // first row is reserved
                terminalSize.getRows() - 2);

        if (difficulty == 1) {
            Game.setTicksPerSecond(5);
        } else if (difficulty == 2) {
            Game.setTicksPerSecond(10);
        } else {
            Game.setTicksPerSecond(15);
        }

        beginTicks();
    }

    /**
     * Begins the game cycle. Ticks once every Game.TICKS_PER_SECOND until
     * game has ended and the endGui has been exited.
     */
    @SuppressWarnings("methodlength")
    private void beginTicks() throws IOException, InterruptedException {
        while (!game.isEnded() || endGui.getActiveWindow() != null) {
            tick();
            // update round achievements
            game.getAchievements().getAchievement("Total Rounds").updateValue(1);
            game.getAchievements().getAchievement("Total Rounds").updateValue(1);
            // update step for different directions
            switch (game.getSnake1().getDirection()) {
                case UP:
                    game.getAchievements().getAchievement("Step Upwards").updateValue(1);
                    break;
                case DOWN:
                    game.getAchievements().getAchievement("Step Downwards").updateValue(1);
                    break;
                case LEFT:
                    game.getAchievements().getAchievement("Step Leftwards").updateValue(1);
                    break;
                case RIGHT:
                    game.getAchievements().getAchievement("Step Rightwards").updateValue(1);
                    break;
            }
            switch (game.getSnake2().getDirection()) {
                case UP:
                    game.getAchievements().getAchievement("Step Upwards").updateValue(1);
                    break;
                case DOWN:
                    game.getAchievements().getAchievement("Step Downwards").updateValue(1);
                    break;
                case LEFT:
                    game.getAchievements().getAchievement("Step Leftwards").updateValue(1);
                    break;
                case RIGHT:
                    game.getAchievements().getAchievement("Step Rightwards").updateValue(1);
                    break;
            }
            Thread.sleep(1000L / Game.getTicksPerSecond());
        }

        // check The Speedy achievement
        if (game.getAchievements().getAchievement("Total Rounds").getValue() <= 10) {
            game.getAchievements().addAchievement(
                    new GeneralAchievement("The Speedy", "Finish the game in less than 20 rounds", game.getSnake1()));
            game.getAchievements().addAchievement(
                    new GeneralAchievement("The Speedy", "Finish the game in less than 20 rounds", game.getSnake2()));
        }

        // testing achievement
        for (Achievement a : game.getAchievements().getAchievements()) {
            System.out.println(a);
        }

        System.exit(0);
    }

    /**
     * Handles one cycle in the game by taking user input,
     * ticking the game internally, and rendering the effects
     */
    private void tick() throws IOException {
        handleUserInput();

        game.tick();

        screen.setCursorPosition(new TerminalPosition(0, 0));
        screen.clear();
        render();
        screen.refresh();

        screen.setCursorPosition(new TerminalPosition(screen.getTerminalSize().getColumns() - 1, 0));
    }

    /**
     * Sets the snake's direction corresponding to the
     * user's keystroke
     */
    private void handleUserInput() throws IOException {
        KeyStroke stroke = screen.pollInput();

        if (stroke == null) {
            return;
        }

        Direction dir = directionFrom(stroke);

        if (dir == null) {
            return;
        }

        // handle two snakes. Arrow keys for snake 1, WASD for snake 2
        if (stroke.getKeyType() == KeyType.ArrowUp || stroke.getKeyType() == KeyType.ArrowDown
                || stroke.getKeyType() == KeyType.ArrowRight || stroke.getKeyType() == KeyType.ArrowLeft) {
            game.getSnake1().setDirection(dir);
            // update key stroke stat
            game.getAchievements().getAchievement("Key Stroke").updateValue(1);
        } else if (stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 'w'
                || stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 'a'
                || stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 's'
                || stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 'd') {
            game.getSnake2().setDirection(dir);
            // update key stroke stat
            game.getAchievements().getAchievement("Key Stroke").updateValue(1);
        }
    }

    /**
     * Returns the natural direction corresponding to the KeyType.
     * Null if none found.
     */
    private Direction directionFrom(KeyStroke key) {
        KeyType type = key.getKeyType();

        switch (type) {
            case ArrowUp:
                return Direction.UP;
            case ArrowDown:
                return Direction.DOWN;
            case ArrowRight:
                return Direction.RIGHT;
            case ArrowLeft:
                return Direction.LEFT;
            case Character:
                if (key.getCharacter() == 'w') {
                    return Direction.UP;
                } else if (key.getCharacter() == 'a') {
                    return Direction.LEFT;
                } else if (key.getCharacter() == 's') {
                    return Direction.DOWN;
                } else if (key.getCharacter() == 'd') {
                    return Direction.RIGHT;
                }
            default:
                return null;
        }
    }

    /**
     * Renders the current screen.
     * Draws the end screen if the game has ended, otherwise
     * draws the score, snake, and food.
     */
    private void render() {
        if (game.isEnded()) {
            if (endGui == null) {
                drawEndScreen();
            }

            return;
        }

        drawScore();
        drawSnake();
        drawFood();
    }

    private void drawEndScreen() {
        endGui = new MultiWindowTextGUI(screen);

        new MessageDialogBuilder()
                .setTitle("Game over!")
                .setText("Score 1: " + game.getScore1() + "\nScore 2: " + game.getScore2())
                .addButton(MessageDialogButton.Close)
                .build()
                .showDialog(endGui);

        // Create a scrollable text area to display achievements using lanterna
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(com.googlecode.lanterna.gui2.Direction.VERTICAL));

        // Create a scrollable text area
        TextBox textArea = new TextBox();
        textArea.setReadOnly(true);
        textArea.setPreferredSize(new TerminalSize(50, 10));
        textArea.setText(game.getAchievements().toString());

        // Add the text area to the panel
        panel.addComponent(textArea);

        // Create a scroll pane and add the panel to it
        ScrollBar scrollBar = new ScrollBar(com.googlecode.lanterna.gui2.Direction.VERTICAL);
        scrollBar.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        scrollBar.addTo(panel);

    }

    private void drawScore() {
        TextGraphics text = screen.newTextGraphics();
        text.setForegroundColor(TextColor.ANSI.GREEN);
        text.putString(1, 0, "Score1: ");

        text = screen.newTextGraphics();
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(8, 0, String.valueOf(game.getScore1()));

        text = screen.newTextGraphics();
        text.setForegroundColor(TextColor.ANSI.GREEN);
        text.putString(1, 1, "Score2: ");

        text = screen.newTextGraphics();
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(8, 1, String.valueOf(game.getScore2()));
    }


    private void drawSnake() {
        Snake snake1 = game.getSnake1();

        drawPosition(snake1.getHead(), TextColor.ANSI.GREEN, '*', true);

        for (Position pos : snake1.getBody()) {
            drawPosition(pos, TextColor.ANSI.GREEN, '+', true);
        }

        Snake snake2 = game.getSnake2();

        drawPosition(snake2.getHead(), TextColor.ANSI.BLUE, '*', true);

        for (Position pos : snake2.getBody()) {
            drawPosition(pos, TextColor.ANSI.BLUE, '+', true);
        }
    }

    private void drawFood() {
        for (Position food : game.getFood()) {
            drawPosition(food, TextColor.ANSI.RED, '#', false);
        }
    }

    /**
     * Draws a character in a given position on the terminal.
     * If wide, it will draw the character twice to make it appear wide.
     */
    private void drawPosition(Position pos, TextColor color, char c, boolean wide) {
        TextGraphics text = screen.newTextGraphics();
        text.setForegroundColor(color);
        text.putString(pos.getPosX() * 2, pos.getPosY() + 1, String.valueOf(c));

        if (wide) {
            text.putString(pos.getPosX() * 2 + 1, pos.getPosY() + 1, String.valueOf(c));
        }
    }
}
