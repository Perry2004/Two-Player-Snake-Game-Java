package model;

import java.util.Objects;

import org.json.JSONObject;

import persistence.JSONizable;

/**
 * The immutable class that represents a position in the game
 */
public class Position implements JSONizable{
    private final int posX;
    private final int posY;

    /**
     * REQUIRE: x and y are positive integers
     * EFFECTS: constructs a new position
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Position(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * EFFECTS: returns the x coordinate
     * @return the x coordinate
     */
    public int getPosX() {
        return posX;
    }

    /**
     * EFFECTS: returns the y coordinate
     * @return the y coordinate
     */
    public int getPosY() {
        return posY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position snakeNode = (Position) o;
        return posX == snakeNode.posX && posY == snakeNode.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    /**
     * EFFECTS: returns the position as a JSON object
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("posX", posX);
        json.put("posY", posY);
        return json;
    }
}
