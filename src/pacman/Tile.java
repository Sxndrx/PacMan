package pacman;


import java.util.Objects;

/**
 * Kratka w mazeGrid
 */
public class Tile{
    protected int x;
    protected int y;
    private int type;


    public Tile(int x, int y) {
        this(x, y, 0);
    }

    public Tile(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if(this.x == ((Tile)obj).x && this.y == ((Tile)obj).y){
            return true;
        }
        else return false;
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

    protected int getX() {
        return x;
    }

    protected int getY() {
        return y;
    }

}
