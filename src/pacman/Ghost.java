package pacman;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;


/**
 * Roszerza Character, implementuje większość zachowań Duszka (Ghost)
 */
public abstract class Ghost extends Character {

    /**
     * Trasa do przejścia
     */
    protected Path ghostPath;
    /**
     * Czy poza domem
     */
    protected boolean free;
    /**
     * poprzednia pozycja x
     */
    protected int prevX;
    /**
     * poprzednia pozycja y
     */
    protected int prevY;
    /**
     * pozycja x Pacmana
     */
    protected int pacX;
    /**
     * pozycja y Pacmana
     */
    protected int pacY;
    /**
     * pozycja x szukanej kratki w trybie scatter
     */
    protected int scatterX;
    /**
     * pozycja y szukanej kratki w trybie scatter
     */
    protected int scatterY;
    /**
     * dotsRatio przy którym Ghost opuszcza Domek na śrokdu maze
     */
    protected double dotsRatio;
    protected int frightendSpeed;
    protected int fullSpeed;
    public Ghost(Maze maze, Group group, int[][] gCodnt, int nr) {
        super(maze, group, gCodnt, nr);
        this.imageX = new SimpleIntegerProperty(0);
        this.imageY = new SimpleIntegerProperty(0);
        imageCircle.centerXProperty().bind(imageX);
        imageCircle.centerYProperty().bind(imageY);
        fullSpeed = GameData.getGhostSpeed();
        speed = fullSpeed;
        frightendSpeed = GameData.getGhostFrightendSped();
        fullSteps = maze.getGameData().getTileGAP() / speed;
        ghostPath = new Path(maze);
        dotsRatio = GameData.getDotsRatios(nr);
    }

    /**
     * Krok w zależności od trybu w którym się znajduje
     */
    @Override
    protected void moveOneStep() {
        boolean teleport = false;
        if (!paused.get()) {
            if (steps == 0) {
                if(!frightned) {
                    synchronized (maze.getPacMan()) {
                        dotsEaten = maze.getPacMan().dotsEaten;
                        //    System.out.println(dotsEaten);
                    }
                }
                if ((double) dotsEaten / (double) dotsCount >= dotsRatio) {
                    free = true;
                }
                if (!free) {
                    moveInHome();
                    moving = true;
                } else {
                    if (scatter) {
                        setDirection(scatterX, scatterY);
                        moving = true;
                    } else if (chase) {
                        moveChase();
                        moving = true;
                    }  else if(frightned){
                        setDirection(15, 11);
                    }
                    else {
                        moveOutOfHome();
                    }
                }

                int tempX, tempY;
                tempX = x + directionX;
                tempY = y + directionY;
                if (tempX < 0) {
                    tempX = maze.getGameData().getxTiles() - 1;
                    teleport = true;
                }
                if (tempX == maze.getGameData().getxTiles()) {
                    tempX = 0;
                    teleport = true;
                }
                if (directionY != 0 || directionX != 0) {
                    prevY = y;
                }
                prevX = x;
                x = tempX;
                y = tempY;

                if(frightned){
                    if(speed!=frightendSpeed) {
                        speed=frightendSpeed;
                        fullSteps = maze.getGameData().getTileGAP()/speed;
                    }
                }
                else{
                    if(speed!=fullSpeed) {
                        speed=fullSpeed;
                        fullSteps = maze.getGameData().getTileGAP()/speed;
                    }
                }

            }

            if (moving) {
                //   System.out.println(this.getClass() + " x: " +speed*directionX + " y: " + speed*directionY);
                moveImage(teleport);
            }
        }
    }

    /**
     * Zaimplementowana w ghost, wyjątkowe dla każdego duszka
     */
    protected abstract void moveChase();

    /**
     * @param endX szukana kratka x (mazeGrid)
     * @param endY szukana kratka y (mazeGrid)
     */
    @Override
    public void setDirection(int endX, int endY) {
        Node nextnode = null;
        ghostPath.searchPath(x, y, endX, endY, prevX, prevY, directionX, directionY);
        //   System.out.println("[" + x + "][" + y + "]-[" + pacX + "][" + pacY +"]");
        if (ghostPath.getPath().size() > 0)
            nextnode = ghostPath.getFirstNode();

        if (x == 0 && nextnode.getX() == maze.getGameData().getxTiles() - 1) {
            directionX = -1;
            directionY = 0;
            return;
        } else if (x == maze.getGameData().getxTiles() - 1 && nextnode.getX() == 0) {
            directionX = 1;
            directionY = 0;
            return;
        }

        if (x != nextnode.getX())
            directionX = (nextnode.getX() - x) / Math.abs(nextnode.getX() - x);
        else directionX = 0;
        if (y != nextnode.getY())
            directionY = (nextnode.getY() - y) / Math.abs(nextnode.getY() - y);
        else directionY = 0;
    }


    @Override
    protected void reset() {
        moveAtStart();
        pacX = 0;
        pacY = 0;
    }

    @Override
    protected void setFrightned() {
        frightned = true;
        chase = false;
        scatter = false;
        if(free){
            Platform.runLater(()->
                    imageCircle.setStroke(Color.CADETBLUE));
           Platform.runLater(()->
                    imageCircle.setStrokeWidth(3));
            Platform.runLater(()->
                    imageCircle.setFill(Color.DARKBLUE));

        }
    }

    @Override
    protected void setNormal() {
        startStatus();
        frightned = false;
        Platform.runLater(()->
                imageCircle.setFill(GameData.getGhostColor()[nr]));
        Platform.runLater(()->
                imageCircle.setStrokeWidth(0));

    }

    /**
     * Zmiana stanu po zjedzeniu przez PacMana
     */
    protected void ghostEaten(){
        moveAtStart();
        frightned = true;
        chase = false;
        scatter = false;
        free = false;
        Platform.runLater(()->
                imageCircle.setFill(GameData.getGhostColor()[nr]));
        Platform.runLater(()->
                imageCircle.setStrokeWidth(0));
    }

    /**
     * Poruszanie podczas pobytu w domu na środku maze
     */
    public void moveInHome() {
        if (y == 13 && x < 16) {
            setDirection(x + 1, y);
        } else if (x == 16 && y < 15) {
            setDirection(x, y + 1);
        } else if (x > 11 && y == 15) {
            setDirection(x-1, y);
        }
        else setDirection(x, y-1);
    }

    /**
     * Opuść dom na najbliższą kratkę za bramą
     */
    public void moveOutOfHome(){
        if(x==15 && y==11){
            chase=true;
        }
        else if((x==13 || x==14) &&(y==12 || y==13)){
            directionX=0;
            directionY=-1;
       //     System.out.println("here");
        } else {
            setDirection(15, 12);
        }
    }
}