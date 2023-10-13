package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that represents a snake in the game
 */
public class Snake {
    private Position head;
    // first element is last part, last element is next to head
    private final List<Position> body;
    // current facing direction
    private Direction direction;
    // the last position that was removed as a result of moving
    private Position lastRemoved;
    private final String name;
    private int numApplesEaten;

    /**
     * REQUIRE: posX and posY are positive integers
     * EFFECTS: constructs a new snake with the given position and name
     * 
     * @param posX the x coordinate of the head
     * @param posY the y coordinate of the head
     * @param name the name of the snake
     */
    public Snake(int posX, int posY, String name) {
        this.head = new Position(posX, posY);
        this.body = new ArrayList<>();
        this.direction = Direction.RIGHT;
        this.name = name;
    }

    /**
     * EFFECTS: moves the snake in the current direction
     */
    public void move() {
        // add the head to the end to fill the gap from moving
        body.add(head);
        // remove the tail of the snake to maintain size
        lastRemoved = body.remove(0);

        this.head = direction.move(this.head);
    }

    /**
     * EFFECTS: returns whether the snake has collided with the given position
     * 
     * @param pos the position to check
     * @return true if the snake has collided with the given position, false
     *         otherwise
     */
    public boolean hasCollided(Position pos) {
        return head.equals(pos) || body.contains(pos);
    }

    /**
     * EFFECTS: returns whether the snake has collided with itself
     * 
     * @return true if the snake has collided with itself, false otherwise
     */
    public boolean hasCollidedWithSelf() {
        return body.contains(head);
    }

    /**
     * MODIFIES: this
     * EFFECTS: grows the snake (length) by one unit
     */
    public void grow() {
        body.add(0, lastRemoved);
    }

    /**
     * MODIFIES: this
     * EFFECTS: sets the direction of the snake
     * @param direction the direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * EFFECTS: returns the head position of the snake
     * @return the head position of the snake
     */
    public Position getHead() {
        return head;
    }

    /**
     * EFFECTS: returns the body of the snake
     * @return the body of the snake
     */
    public List<Position> getBody() {
        return body;
    }

    /**
     * EFFECTS: returns the name of the snake
     * @return the name of the snake
     */
    public String getName() {
        return name;
    }

    /**
     * EFFECTS: returns the number of apples eaten by the snake
     * @return the number of apples eaten by the snake
     */
    public int getNumApplesEaten() {
        return numApplesEaten;
    }

    /**
     * MODIFIES: this
     * EFFECTS: increments the number of apples eaten by the snake by 1
     */
    public void eatApple() {
        numApplesEaten++;
    }

    /**
     * EFFECTS: returns the direction of the snake
     * @return the direction of the snake
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * MODIFIES: this
     * EFFECTS: sets the head position of the snake
     * @param head the head position of the snake
     */
    public void setHead(Position head) {
        this.head = head;
    }

}
