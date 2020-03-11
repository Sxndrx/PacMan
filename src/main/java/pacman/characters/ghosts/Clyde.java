package pacman.characters.ghosts;

import javafx.scene.Group;
import pacman.Maze.Maze;

/**
 * Pomaranczowy Ghost
 */
public class Clyde extends Ghost {
    public Clyde(Maze maze, Group group, int[][] gCodnt, int nr) {
        super(maze, group, gCodnt, nr);
        startX = 16;
        startY = 14;
        reset();
        scatterX=1;
        scatterY=29;
   }

    /**
     * Scigaj bezpośrednio PacMana, jeśli liczba kratek od PacMana jest mniejsza niż 8 idź do punktu scatterX, scatterY
     */
    @Override
    public void moveChase(){
        if (steps == 0) {

            synchronized (maze) {
                pacX = maze.getGameData().calcGridX(imageCoordinates[4][0]);
                pacY = maze.getGameData().calcGridY(imageCoordinates[4][1]);
                if(pacX==x && pacY==y)
                    moving=false;
            }
            if(Math.sqrt((pacX-x)*(pacX-x)+(pacY-y)*(pacY-y))<=8)
                setDirection(scatterX, scatterY);
            else
                setDirection(pacX, pacY);
        }
    }

}
