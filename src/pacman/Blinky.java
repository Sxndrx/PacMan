package pacman;

import javafx.scene.Group;

/**
 * Czerwony Ghost
 */
public class Blinky extends Ghost {
    public Blinky(Maze maze, Group group, int[][] gCodnt, int nr) {
        super(maze, group, gCodnt, nr);
        startX = 15;
        startY = 13;
        scatterX = 26;
        scatterY = 1;
        reset();
    }

    @Override
    protected void startStatus() {
        moving = true;
        free = false;
        chase = false;
        scatter = frightned = false;
        prevX=x;
        prevY=y;
    }

    /**
     * Blinky ściga PacMana szukając najkrótszej ścieżki
     */
    public void moveChase(){
        if (steps == 0) {

            synchronized (maze) {
                    pacX = maze.getGameData().calcGridX(imageCoordinates[4][0]);
                    pacY = maze.getGameData().calcGridY(imageCoordinates[4][1]);

            }
            setDirection(pacX, pacY);
         //   System.out.println("x: " + imageCoordinates[4][0]+"y: " + imageCoordinates[4][1]);
         //   System.out.println("x: " + x+"y: " + y);
        }
    }

}
