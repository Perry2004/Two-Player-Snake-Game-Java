package model;

import java.util.Objects;

/**
 * Represents a position in the game space.
 */
public class Position {
    private final int posX;
    private final int posY;

    public Position(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public int getPosX() {
        return posX;
    }

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
}
