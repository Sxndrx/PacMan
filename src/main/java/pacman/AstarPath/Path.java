package pacman.AstarPath;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pacman.Maze.Maze;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Sciezka dla Ghost
 */
public class Path {
    /**
     * reprezentacja sciezki
     */
    private List<Node> path;
    private Maze maze;

    public Path(Maze maze) {
        path = new LinkedList<>();
        this.maze = maze;
    }

    /**
     * @param xStart poczatek sciezki x
     * @param yStart poczatek sciezki y
     * @param xStop koniec sciezki x
     * @param yStop koniec sciezki y
     * @param prevX poprzedni node x
     * @param prevY poprzedni node y
     * @param dirX dotychczasowy kierunek x
     * @param dirY dotychczasowy kierunek y
     *             Wykorzystuje algorytm a*
     *             Jeśli po jego wykonaniu path jest puste szuka najbliższego wolnego Node z wyższym priorytetem dla dotychczasowego kierunku
     */
    public void searchPath(int xStart, int yStart, int xStop, int yStop, int prevX, int prevY, int dirX, int dirY) {
        path.clear();
        List<Node> open = new LinkedList<>();
        List<Node> close = new LinkedList<>();
        List<Node> neighbours = new LinkedList<>();
        Node end = new Node(xStop, yStop);
        Node start = new Node(xStart, yStart);
        Node prev = new Node(prevX, prevY);
        open.add(start);
        close.add(prev);
        Node current;

        while (!open.isEmpty()) {

            sortList(open);
            current = open.get(0);
            open.remove(current);
            close.add(current);
            if (current.equals(end)) {
                buildPath(current);
                open.clear();
                path.remove(start);
            } else {
                addNeighbours(current, neighbours);

                for (Node successor : neighbours) {
                    if (!maze.getGameData().isWall(successor.getX(), successor.getY())) {
                        int h, g;
                        h = g = 0;
                        boolean inClosed = false;
                        boolean inOpen = false;
                        boolean better = false;
                        int i = 0;
                        while (i < close.size() && !inClosed) {
                            if (successor.equals(close.get(i)))
                                inClosed = true;
                            i++;
                        }
                        if (!inClosed) {
                            h = calcManhattanDist(successor, end);
                            g = current.getgCost() + 1;

                            i = 0;
                            while (i < open.size() && !inOpen) {
                                if (successor.equals(open.get(i))) {
                                    if (open.get(i).getfCost() > h + g) {
                                        better = true;
                                    }
                                    inOpen = true;
                                }
                                i++;
                            }

                            if (!inOpen) {
                                open.add(successor);
                                better = true;
                            }
                            if (better) {
                                successor.setgCost(g);
                                successor.sethCost(h);
                                successor.setParent(current);
                            }
                        }
                    }else if (successor.equals(end)) {
                        buildPath(current);
                        path.remove(start);
                        open.clear();
                    }
                    else close.add(successor);
                }
            }
            neighbours.clear();
        }

        if(path.size()<=0){
            open.clear();
            addNeighbours(start, neighbours);
            for (Node node: neighbours){
                if(!node.equals(prev) && !maze.getGameData().isWall(node.getX(), node.getY()))
                   open.add(node);
            }
            for (Node node:open){
                if(node.getX()==xStart+dirX && node.getY()==yStart+dirY) {
                    buildPath(node);
                    return;
                }
            }
            buildPath(open.get(0));
        }

    }

    /**
     * @param current koniec sciezki
     *                budowa path
     *                na koniec path.reverse
     */
    private void buildPath(Node current) {
        if (current != null) {
            while (current.getParent() != null) {
                this.path.add(current);
                current = current.getParent();
            }
            this.path.add(current);
        }
        Collections.reverse(path);
    }

    /**
     * tylko na potrzeby testów
     *
     */
    public void printPath(Group root) {
        if (path.isEmpty())
            System.out.println("NO AVAIBLE PATH");
        for (Node node : path) {
            System.out.println("[" + node.getX() + "][" + node.getY() + "]");
            Rectangle r = new Rectangle(20, 20, Color.ROYALBLUE);
            r.setX(maze.getGameData().calcXPos(node.getX()));
            r.setY(maze.getGameData().calcYPos(node.getY()));
            r.setVisible(true);
            root.getChildren().add(r);
        }
    }

    /**
     * @param current badany node
     * @param neighbours lista sasiadów
     *                   Znajdź i dodaj do listy sąsiednie Node
     */
    private void addNeighbours(Node current, List<Node> neighbours) {
        int tempX = current.getX();
        int tempY = current.getY();


        tempX -= 1;
        if (tempX < 0 && tempY == 14)
            tempX = maze.getGameData().getxTiles() - 1;
            neighbours.add(new Node(tempX, tempY));

        tempX = current.getX() + 1;
        if (tempX == maze.getGameData().getxTiles() && tempY == 14) {
            tempX = 0;
        }
            neighbours.add(new Node(tempX, tempY));

        tempX = current.getX();
        tempY = current.getY() - 1;
            neighbours.add(new Node(tempX, tempY));

        tempY = current.getY()+1;
            neighbours.add(new Node(tempX, tempY));
    }

    private void sortList(List<Node> list) {
        list.sort(Comparator.comparing(Node::getfCost));
    }

    /**
     * @param start
     * @param stop
     * @return dystans od start do stop zgodnie z metryką uliczną
     */
    private int calcManhattanDist(Node start, Node stop) {
        return Math.abs(start.getX() - stop.getX()) + Math.abs(start.getY() - stop.getY());
    }

    public List<Node> getPath() {
        return path;
    }

    public Node getFirstNode(){
                  Node temp = this.path.get(0);
        this.path.remove(0);
        return temp;
    }

}
