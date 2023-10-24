package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
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
import persistence.JSONSaver;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONObject;

/**
 * The interface for the game. Handles user input and rendering.
 */
public class TerminalGame {
    private Game game;
    private Screen screen;
    private WindowBasedTextGUI endGui;
    private WindowBasedTextGUI pauseGui;

    /**
     * EFFECTS: Ask the user for the difficulty level and start the game
     * MODIFIES: this
     * 
     * @throws IOException          throws when the screen cannot be created
     * @throws InterruptedException throws when the thread is interrupted
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
     * EFFECTS: start the game rounds. Moves the snakes and update stats. If the
     * game is ended, print out all achievements.
     * MODIFIES: this
     * 
     * @throws IOException          throws when the screen cannot be created
     * @throws InterruptedException throws when the thread is interrupted
     */
    private void beginTicks() throws IOException, InterruptedException {
        while (!game.isEnded() || endGui.getActiveWindow() != null) {
            tick();
            // update round achievements
            game.getAchievements().getAchievement("Total Rounds", game.getSnake1()).updateValue(1);
            game.getAchievements().getAchievement("Total Rounds", game.getSnake2()).updateValue(1);
            // update step for different directions
            updateStepAchievement(game.getSnake1());
            updateStepAchievement(game.getSnake2());
            Thread.sleep(1000L / Game.getTicksPerSecond());
        }
        // check The Speedy achievement
        checkSpeedyAchievement();
        // print out all achievements
        for (Achievement a : game.getAchievements().getAchievements()) {
            System.out.println(a);
        }
        System.exit(0);
    }

    /**
     * MODIFIES: this
     * EFFECTS: check if any of the achievement is achieved. If so, add it to the
     * achievement collection
     * 
     * @param snake the snake to check
     */
    private void updateStepAchievement(Snake snake) {
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

    /**
     * MODIFIES: this
     * EFFECTS: check if the "The Speedy" achievement is achieved. If so, add it to
     * the achievement collection
     */
    private void checkSpeedyAchievement() {
        if (game.getAchievements().getAchievement("Total Rounds", game.getSnake1()).getValue() <= 10) {
            game.getAchievements().addAchievement(
                    new GeneralAchievement("The Speedy", "Finish the game in less than 20 rounds", game.getSnake1()));
            game.getAchievements().addAchievement(
                    new GeneralAchievement("The Speedy", "Finish the game in less than 20 rounds", game.getSnake2()));
        }
    }

    /**
     * MODIFIES: this
     * EFFECTS: runs a single round of the game. Handles user input and sets the
     * snake's direction.
     * 
     * @throws IOException throws when the screen cannot be created
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
     * MODIFIES: this
     * EFFECTS: sets the snake's direction based on user input
     * 
     * @throws IOException throws when the screen cannot be created
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

        // handle pause (escape key)
        if (stroke.getKeyType() == KeyType.Escape) {
            drawPausedScreen();
            return;
        }

        // handle two snakes. Arrow keys for snake 1, WASD for snake 2
        if (stroke.getKeyType() == KeyType.ArrowUp || stroke.getKeyType() == KeyType.ArrowDown
                || stroke.getKeyType() == KeyType.ArrowRight || stroke.getKeyType() == KeyType.ArrowLeft) {
            game.getSnake1().setDirection(dir);
            // update key stroke stat
            game.getAchievements().getAchievement("Key Stroke", game.getSnake1()).updateValue(1);
        } else if (stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 'w'
                || stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 'a'
                || stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 's'
                || stroke.getKeyType() == KeyType.Character && stroke.getCharacter() == 'd') {
            game.getSnake2().setDirection(dir);
            // update key stroke stat
            game.getAchievements().getAchievement("Key Stroke", game.getSnake2()).updateValue(1);
        }
    }

    /**
     * EFFECTS: returns the direction corresponding to the given key. Arrow keys for
     * snake 1, WASD for snake 2
     * 
     * @param key the key pressed by the user
     * @return the direction corresponding to the given key
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
            case Escape:
                return Direction.PAUSE;
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
     * MODIFIES: this
     * EFFECTS: draws the game on the terminal using lanterna
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

    /**
     * MODIFIES: this
     * EFFECTS: draws the end screen using lanterna
     */
    private void drawEndScreen() {
        endGui = new MultiWindowTextGUI(screen);

        new MessageDialogBuilder()
                .setTitle("Game over!")
                .setText("Score 1: " + game.getScore1() + "\nScore 2: " + game.getScore2())
                .addButton(MessageDialogButton.Close)
                .build()
                .showDialog(endGui);
    }

    /**
     * MODIFIES: this
     * EFFECTS: draws the paused screen using lanterna.
     */
    private void drawPausedScreen() {
        pauseGui = new MultiWindowTextGUI(screen);

        if (new MessageDialogBuilder()
                .setTitle("Game paused!")
                .setText("Score 1: " + game.getScore1() + "\nScore 2: " + game.getScore2() + "\nDo you want to save?")
                .addButton(MessageDialogButton.Continue)
                .addButton(MessageDialogButton.Yes)
                .build()
                .showDialog(pauseGui).equals(MessageDialogButton.Yes)) {
            JSONSaver.saveGame("data/save1.json", game);
        }

    }

    /**
     * MODIFIES: this
     * EFFECTS: draws the score on the terminal using lanterna
     */
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

    /**
     * MODIFIES: this
     * EFFECTS: draws the snake on the terminal using lanterna
     */
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

    /**
     * MODIFIES: this
     * EFFECTS: draws the food on the terminal using lanterna
     */
    private void drawFood() {
        for (Position food : game.getFood()) {
            drawPosition(food, TextColor.ANSI.RED, '#', false);
        }
    }

    /**
     * MODIFIES: this
     * EFFECTS: draws a position on the terminal using lanterna
     * 
     * @param pos   the position to draw
     * @param color the color to draw the position in
     * @param c     the character to draw
     * @param wide  whether the character should be drawn as two characters wide
     *              Private helper method for drawSnake and drawFood
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
