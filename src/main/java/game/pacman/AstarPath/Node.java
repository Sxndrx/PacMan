package game.pacman.AstarPath;

import game.pacman.Maze.MazeTile;

/**
 * Węzeł w ścieżce ghostPath
 */
public class Node extends MazeTile {
    /**
     * poprzedni węzeł
     */
    private Node parent;
    /**
     * Koszt od startu
     */
    private int gCost;
    /**
     * koszt dojscia do konca
     */
    private int hCost;
    /**
     * sumaryczny koszt
     */
    private int fCost;

    public Node(int x, int y) {
        super(x, y);

        this.gCost = 0;
        this.hCost = 0;
        this.fCost = this.gCost + this.hCost;

    }

    public int getgCost() {
        return gCost;
    }

    /**
     * aktualizuje również gCost
     */
    public void setgCost(int gCost) {
        this.gCost = gCost;
        fCost=hCost+gCost;
    }

    public int getfCost() {
        return fCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public int gethCost() {
        return hCost;
    }

    /**
     * aktualizuje również gCost
     */
    public void sethCost(int hCost) {
        this.hCost = hCost;
        fCost=hCost+gCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "[" + x + "][" + y + "]" + fCost ;
    }
}
