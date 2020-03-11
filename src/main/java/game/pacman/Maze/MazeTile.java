package game.pacman.Maze;


import java.util.Objects;

/**
 * Kratka w mazeGrid
 */
public class MazeTile {
    protected int x;
    protected int y;
    private int type;


    public MazeTile(int x, int y) {
        this(x, y, 0);
    }

    public MazeTile(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return this.x == ((MazeTile) obj).x && this.y == ((MazeTile) obj).y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
