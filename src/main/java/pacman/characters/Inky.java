package pacman.characters;

import javafx.scene.Group;
import pacman.GameData;
import pacman.Maze;

/**
 * Niebieski Ghost
 */
public class Inky extends Ghost {
    public Inky(Maze maze, Group group, int[][] gCodnt, int nr) {
        super(maze, group, gCodnt, nr);
        startX = 11;
        startY = 14;
        scatterX= 26;
        scatterY = 29;
        reset();
    }

    /**
     * Wyznacz trasę w zależności od pozycji pacMana i Blinky
     */
    @Override
    protected void moveChase() {
        int blinkyX;
        int blinkyY;
        if (steps == 0) {
            synchronized (maze) {
                pacX = maze.getGameData().calcGridX(imageCoordinates[4][0]);
                pacY = maze.getGameData().calcGridY(imageCoordinates[4][1]);
                blinkyX = maze.getGameData().calcGridX(imageCoordinates[GameData.getBlinkyNR()][0]);
                blinkyY = maze.getGameData().calcGridX(imageCoordinates[GameData.getBlinkyNR()][1]);

            }
            int endX = pacX+((pacX - blinkyX)*2) %maze.getGameData().getxTiles();
            int endY = pacY+((pacY - blinkyY)*2) %maze.getGameData().getyTiles();

            setDirection(endX, endY);

        }
    }
}
