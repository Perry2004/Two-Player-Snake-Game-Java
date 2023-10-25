package persistence;

import java.io.PrintWriter;

/**
 * The class that is used to save Jsonizable objects to a file
 */
public class JsonSaver {
    private static final int TAB = 4;

    /**
     * REQUIRES: path is a valid path that can be written to; toSave is a valid
     * Jsonizable object
     * EFFECTS: saves the given Jsonizable object to the given path in JSON format
     * 
     * @param path   the path to save the object to
     * @param toSave the object to save
     */
    public static boolean saveGame(String path, Jsonizable toSave) {
        try (PrintWriter writer = new PrintWriter(path)) {
            writer.print(toSave.toJson().toString(TAB));
            System.out.println("Game successfully saved to " + path);
            return true;
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;
        }
    }
}