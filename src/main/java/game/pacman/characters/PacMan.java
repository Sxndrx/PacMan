package game.pacman.characters;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import game.pacman.Dot;
import game.pacman.Maze.Maze;
import game.pacman.Maze.MazeTile;

import java.io.FileNotFoundException;

/**
 * PacMan implementuje Character, główna postać w grze, sterowana przez użytkownika
 */
public class PacMan extends Character {

    /**
     * zdobyte punkty
     */
    private SimpleIntegerProperty points;

    public int getDotsEaten() {
        return dotsEaten;
    }

    protected int dotsEaten;
    /**
     * życia - początkowo 3
     */
    private SimpleIntegerProperty lifes;
    private int[] dirTemp = {0, 0};

    public PacMan(Group root, Maze maze, int[][] gCodnt, int nr) throws FileNotFoundException {
        super(maze, root, gCodnt, nr);
        startX = 15;
        startY = 23;

        this.imageX = new SimpleIntegerProperty(maze.getGameData().calcXPos(startX));
        this.imageY = new SimpleIntegerProperty(maze.getGameData().calcYPos(startY));
        imageCircle.centerXProperty().bind(imageX);
        imageCircle.centerYProperty().bind(imageY);
       Platform.runLater(()->{
           imageCircle.setStrokeWidth(2);
           imageCircle.setStroke(Color.web("#d4d5d6"));
       });
        this.speed = 4;
        fullSteps = maze.getGameData().getTileGAP() / speed;
        points = new SimpleIntegerProperty(0);
        lifes = new SimpleIntegerProperty(3);
        moving = true;

        reset();

    }

    public void reset() {
        moveAtStart();
        startStatus();
        lastUpdate=0;
        sinceFrightendOn=0;
        sinceModeChange=0;
    }

    /**
     * zrobienie kroku i sprawdzenie kolizji z Dot i Ghost
     */
    @Override
    public void moveOneStep() {

        int tempX;
        int tempY;
        boolean teleport = false;
        if (steps == 0) {

            synchronized (dirTemp) {
                if (validateDirection(dirTemp[0], dirTemp[1])) {
                    directionY = dirTemp[1];
                    directionX = dirTemp[0];
                }
            }

            if (isDotCollision())
                eatDot();

            tempX = x + directionX;
            if (tempX < 0) {
                tempX = maze.getGameData().getxTiles() - 1;
                teleport = true;
            } else if (tempX == maze.getGameData().getxTiles()) {
                tempX = 0;
                x = tempX;
                teleport = true;
            }
            tempY = y + directionY;
            if (maze.getGameData().isWall(tempX, tempY))
                moving = false;
            else {
                moving = true;
                x = tempX;
                y = tempY;
            }
        }

        if (moving) {
            moveImage(teleport);
        }

        int gNr = isGhostCollision();
        if (gNr != -1) {
            if (frightned.get()) {
                eatGhost(gNr);
            } else {
                lifes.set(lifes.get() - 1);
                moveAtStart();
                maze.looseLife();
            }
        }
    }


    @Override
    protected void startStatus() {
        points.set(0);
        lifes.set(3);
        dotsEaten = 0;
        setDirection("RIGHT");
    }

    /**
     * @param direction kierunek wyrażony String
     *                  wywoływana przy wciśnięciu klawisza strzałki
     *                  używa tablicy tymczasowego kierunku
     */
    @Override
    public void setDirection(String direction) {
        //int xd, yd;
        synchronized (dirTemp){

            dirTemp[0] = directionX;
            dirTemp[1] = directionY;
            switch (direction) {
                case "RIGHT":
                    dirTemp[0] = 1;
                    dirTemp[1] = 0;
                    break;
                case "LEFT":
                    dirTemp[0] = -1;
                    dirTemp[1] = 0;
                    break;
                case "UP":
                    dirTemp[0] = 0;
                    dirTemp[1] = -1;
                    break;
                case "DOWN":
                    dirTemp[0] = 0;
                    dirTemp[1] = 1;
                    break;
            }
        }
    }

    @Override
    public synchronized void setFrightendMode() {
        frightned.set(true);
        sinceFrightendOn=0;
        chase.set(false);
    }

    @Override
    protected void setChaseMode() {
        frightned.set(false);
        chase.set(true);
    }

    /**
     * Zjedzenie kropki
     */
    private void eatDot() {
        Dot d = maze.getDots().get(new MazeTile(x, y));
        d.hide();
        Platform.runLater(() ->
                points.set(points.get() + d.getPoints()));
        synchronized (this) {
            dotsEaten++;
        }
        if (d.getType() == 8) {
            maze.setFrightendGameMode();
        }
        if (dotsEaten == dotsCount)
            maze.winGame();
    }

    /**
     * @return true jesli jest kolizja z Dot, false przeciwnie
     */
    private boolean isDotCollision() {
        if (maze.getDots().containsKey(new MazeTile(x, y))) {
            return maze.getDots().get(new MazeTile(x, y)).isVisible();
        }
        return false;
    }

    /**
     * @param nr numer Ghost do zjedzenie
     *           zjedz Ghost
     */
    private void eatGhost(int nr) {
        Platform.runLater(() -> points.set(points.get() + 301));
        maze.eatGhost(nr);
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
                    if (frightned.get() && sinceFrightendOn==176) {
                        setChaseMode();
                        sinceFrightendOn = 0;
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

    public int getPoints() {
        return points.get();
    }

    public SimpleIntegerProperty pointsProperty() {
        return points;
    }

    public int getLifes() {
        return lifes.get();
    }

    public SimpleIntegerProperty lifesProperty() {
        return lifes;
    }

}