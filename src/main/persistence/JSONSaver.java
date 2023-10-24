package persistence;

import java.io.File;
import java.io.PrintWriter;

/**
 * The class that is used to save JSONizable objects to a file
 */
public class JSONSaver {
    private static final int TAB = 4;
    /**
     * REQUIRES: path is a valid path that can be written to; toSave is a valid JSONizable object
     * EFFECTS: saves the given JSONizable object to the given path in JSON format
     * @param path the path to save the object to
     * @param toSave the object to save
     */
    public static void saveGame(String path, JSONizable toSave) {
        try (PrintWriter writer = new PrintWriter(new File(path))) {
            writer.print(toSave.toJson().toString(TAB));
            System.out.println("Game successfully saved to " + path);
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}