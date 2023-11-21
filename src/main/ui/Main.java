package ui;

import java.util.Scanner;

/**
 * Driver class for the game
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        ask the user which version of the game they want to play
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Select which version of the game you want to play: "
                    + "(1) Swing GUI, (2) Lanterna Terminal");
            while (true) {
                int choice = scanner.nextInt();
                if (choice == 1) {
                    GameView gameView = new GameView();
                    gameView.displayLoadWindow();
                    break;
                } else if (choice == 2) {
                    TerminalGame gameHandler = new TerminalGame();
                    gameHandler.start();
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                }
            }
        }
    }
}
