package pacman.characters.ghosts;

import javafx.scene.Group;
import pacman.Maze.Maze;

/**
 * Różowy Ghost
 */
public class Pinky extends Ghost {
    public Pinky(Maze maze, Group group, int[][] gCodnt, int nr) {
        super(maze, group, gCodnt, nr);
        startX = 13;
        startY = 15;
        scatterX = 1;
        scatterY = 1;
        reset();
    }

    /**
     * Szuka kratki na 4 do przodu przed PacManem
     */
    @Override
    protected void moveChase() {
        if (steps == 0) {
            synchronized (maze) {
                pacX = maze.getGameData().calcGridX(imageCoordinates[4][0]);
                pacY = maze.getGameData().calcGridY(imageCoordinates[4][1]);

            }
            int endX = pacX+4*maze.getPacMan().getDirectionX(); //getDirection is synchronised
            int endY = pacY+4*maze.getPacMan().getDirectionY();

            setDirection(endX, endY);

        }

    }

}
