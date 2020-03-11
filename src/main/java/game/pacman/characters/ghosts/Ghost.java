package game.pacman.characters.ghosts;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import game.pacman.GameData;
import game.pacman.Maze.Maze;
import game.pacman.AstarPath.Node;
import game.pacman.AstarPath.Path;
import game.pacman.characters.Character;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Roszerza Character, implementuje większość zachowań Duszka (Ghost)
 */
public abstract class Ghost extends Character {

    /**
     * Trasa do przejścia
     */
    private Path ghostPath;
    /**
     * Czy poza domem
     */
    protected AtomicBoolean freeToGo;
    protected AtomicBoolean inHome;
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
        freeToGo = new AtomicBoolean();
        inHome = new AtomicBoolean();
        setInHomeMode();
    }

    /**
     * Krok w zależności od trybu w którym się znajduje
     */
    @Override
    protected synchronized void moveOneStep() {
        boolean teleport = false;
        synchronized (maze) {
            if (maze.isPaused()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (steps == 0 && fullSteps!=0) {
            if (inHome.get() && !freeToGo.get()) {
                moveInHome();
                moving = true;
                synchronized (maze.getPacMan()) {
                    if ((double) maze.getPacMan().getDotsEaten() / (double) dotsCount >= dotsRatio) {
                        freeToGo.set(true);
                    }
                }
            } else {
                if(inHome.get() && freeToGo.get()){
                    moveOutOfHome();
                }
                else if (frightned.get()) {
                    setDirection(15, 11);
                }
                else if (scatter.get()) {
                    setDirection(scatterX, scatterY);
                    moving = true;
                } else if (chase.get()) {
                    moveChase();
                    moving = true;
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

            if (frightned.get()) {
                if (speed != frightendSpeed) {
                    speed = frightendSpeed;
                    fullSteps = maze.getGameData().getTileGAP() / speed;
                }
            } else {
                if (speed != fullSpeed) {
                    speed = fullSpeed;
                    fullSteps = maze.getGameData().getTileGAP() / speed;
                }
            }

        }

        if (moving) {
            moveImage(teleport);
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


    public void reset() {
        moveAtStart();
        setInHomeMode();
        endFrightendMode();
        fullSpeed = GameData.getGhostSpeed();
        frightendSpeed = GameData.getGhostFrightendSped();
        speed=fullSpeed;
        try{
            fullSteps = maze.getGameData().getTileGAP()/fullSpeed;
        }catch (ArithmeticException e){
            fullSteps=0;
        }

        pacX = 0;
        pacY = 0;
    }

    protected void lostLife(){
        moveAtStart();
        setInHomeMode();
    }


    protected void setNormalColor() {
        Platform.runLater(() -> {
                    this.imageCircle.setFill(color);
                    this.imageCircle.setStrokeWidth(0);
                }
        );
    }

    protected void setCloseToEndColor(){
        Platform.runLater(() ->
                this.imageCircle.setFill(color));
    }

    /**
     * Zmiana stanu po zjedzeniu przez PacMana
     */
    public void ghostEaten() {
        moveAtStart();
        setInHomeMode();
    }

    /**
     * Poruszanie podczas pobytu w domu na środku maze
     */
    private void moveInHome() {
        if (y == 13 && x < 16) {
            setDirection(x + 1, y);
        } else if (x == 16 && y < 15) {
            setDirection(x, y + 1);
        } else if (x > 11 && y == 15) {
            setDirection(x - 1, y);
        } else setDirection(x, y - 1);
    }

    /**
     * Opuść dom na najbliższą kratkę za bramą
     */
    private void moveOutOfHome() {
        if ((x == 14 || x==13) && y == 12) {
            if(!frightned.get())
                setChaseMode();
            inHome.set(false);
        } else if ((x == 13 || x == 14) && (y == 14 || y == 13)) {
            directionX = 0;
            directionY = -1;
        } else {
            setDirection(14, 11);
        }
    }

    @Override
    protected void startStatus() {
        prevY=y;
        prevX=x;
    }


    private synchronized void setInHomeMode(){
        freeToGo.set(false);
        inHome.set(true);
        scatter.set(false);
        chase.set(false);
    }

    @Override
    public synchronized void setFrightendMode() {
        frightned.set(true);
        sinceFrightendOn  = 0;
        Platform.runLater(() ->{
            this.imageCircle.setStroke(Color.MEDIUMVIOLETRED/*CADETBLUE*/);
            this.imageCircle.setStrokeWidth(3);
            this.imageCircle.setFill(Color.WHITE);
        });
    }

    protected synchronized void endFrightendMode(){
        frightned.set(false);
        setNormalColor();
        sinceFrightendOn = 0;
    }

    @Override
    protected synchronized void setChaseMode() {
        frightned.set(false);
        chase.set(true);
        scatter.set(false);
        freeToGo.set(true);
        inHome.set(false);

        sinceModeChange = 0;

    }

    protected synchronized void setScatterMode(){
        frightned.set(false);
        chase.set(false);
        scatter.set(true);
        freeToGo.set(true);
        inHome.set(false);

        sinceModeChange = 0;
    }

    @Override
    protected void createTimer() {

        timer = new AnimationTimer() {

            /**
             * Najpierw sprawdzenie czy zmiana trybu i zmiana,
             * co 45ms porusz o krok
             */
            @Override
            public void handle(long l) {
                if(!maze.getPaused()){

                    if (frightned.get()){
                        if(sinceFrightendOn==156)
                            setCloseToEndColor();
                        else if(sinceFrightendOn == 176) {
                            if (!inHome.get()) {
                                endFrightendMode();
                                setScatterMode();
                            } else
                                endFrightendMode();
                        }
                    }
                    else if (scatter.get() && sinceModeChange == 66) {
                        setChaseMode();
                    } else if (chase.get() && sinceModeChange == 286) {
                        setScatterMode();
                    }
                    if (l - lastUpdate >= 45000000) {
                        moveOneStep();
                        lastUpdate = l;
                        if (frightned.get()) {
                            sinceFrightendOn++;
                        } else {
                            sinceModeChange++;
                        }
                    }
                }
            }
        };
    }
}