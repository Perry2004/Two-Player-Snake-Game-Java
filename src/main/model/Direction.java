package model;

/**
 * The available directions in the game
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    RIGHT(1, 0),
    LEFT(-1, 0);

    private final int directX;
    private final int directY;

    /**
     * REQUIRE: directX and directY are integers
     * EFFECTS: constructs a new direction
     * @param directX the offset in the x direction
     * @param directY the offset in the y direction
     */
    Direction(int directX, int directY) {
        this.directX = directX;
        this.directY = directY;
    }

    /**
     * REQUIRE: pos != null
     * EFFECTS: returns the position that is one step in the direction of this direction
     * @param pos the position
     * @return the position that is one step in the direction of this direction
     */
    public Position move(Position pos) {
        return new Position(
                pos.getPosX() + directX,
                pos.getPosY() + directY);
    }
}
