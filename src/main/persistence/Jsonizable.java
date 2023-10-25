package persistence;

import org.json.JSONObject;

/**
 * Represents an object that can be converted to JSON
 */
public interface Jsonizable {
    /**
     * EFFECTS: returns the JSON representation of the object
     * 
     * @return the JSON representation of the object
     */
    JSONObject toJson();
}
