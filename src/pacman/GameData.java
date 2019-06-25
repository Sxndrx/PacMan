package pacman;

import javafx.scene.paint.Color;

import java.io.*;

public class GameData {
    /**
     * Szerokosc planszy w pikselach
     */
    private static final int width = 600;
    /**
     * Wysokość planszy w pikselach
     */
    private static final int height = 680;
    /**
     * Szerokość kratki w pikselach
     */
    private final int tileGAP = 20;
    /**
     * Liczba kratek w rzędzie
     */
    private final int xTiles = 28;
    /**
     * Liczba kratek w kolumnie
     */
    private final int yTiles = 31;
    /**
     * Offset z lewej i prawej od krawędzi
     */
    private final int xOffset = 1;
    /**
     * Offset od górnej krawędzi
     */
    private final int yOffsetTop = 1;
    /**
     * Offset od dolnje krawędzi
     */
    private final int yOffsetBottom = 2;
    private final String mazePic = "src/images/maze.png";
    /**
     * Sciezka do pliku txt reprezentującego typu kratek
     */
    private final String mazeOne = "src/images/mazelv1.txt";
    /**
     * Tablica Tile (kretek) budujących Maze
     */
    private Tile[][] mazeGrid;
    private static final int inkyNR = 3;
    private static final int clydeNR = 1;
    private static final int pinkyNR = 2;
    private static final int blinkyNR = 0;
    private static final int pacNR = 4;
    /**
     * Poziom trudnosci
     */
    private static final int maxSpeed = 20;
    private static double difficulty = 0.2;

    private static final Color[] ghostColor = new Color[]{Color.web("#cc0000"), Color.web("#ff6714"), Color.web("#f763b2"), Color.web("#00ced1"), Color.web("#ffd700")};
    /**
     * dotRatio - stosunek zjedzonych kropek (Dot) do wszystkich kropek
     */
    private static final double[] dotsRatios = new double[]{0, 0.1, 0, 0.1}; //changed ratio


    /**
     * Prędkość normalna Ghost
     */
    private static int ghostSpeed = 20;
    /**
     * Prędkość Ghost w trybie frightend
     */
    private static int ghostFrightendSped = 10;

    public GameData() {
        mazeGrid = new Tile[xTiles][yTiles];
        collectTileData();
        setDifficulty(0.2);

    }

    public static void setDifficulty(double diff) {
        difficulty = diff;
        ghostSpeed = (int) ( (double) maxSpeed * difficulty);
        ghostFrightendSped = (int) ((double) (maxSpeed/2 )* difficulty);
        System.out.println(ghostSpeed);
        if(ghostFrightendSped==3){
            ghostFrightendSped=2;
        }
    }

    /**
     * Pobiera dane z pliku mazeOne.txt i buduje mazeGrid[][]
     */
    public void collectTileData() {
        File file = new File(mazeOne);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            Reader reader = new InputStreamReader(in);
            Reader buffer = new BufferedReader(reader);
            int r;
            int type;
            int tileX = 0;
            int tileY = 0;
            while ((r = buffer.read()) != -1) {
                if (r >= 48 && r < 57) {
                    type = r - 48;
                    mazeGrid[tileX][tileY] = new Tile(tileX, tileY, type);
                    tileX = (++tileX) % xTiles;
                    if (tileX == 0)
                        tileY = (++tileY) % yTiles;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return true jestli komórka od [x, y] jest scianą lub poza mazeGrid, false w przeciwnym wypadku
     */
    public boolean isWall(int x, int y) {
        if (x < 0 || x >= getxTiles() || y < 0 || y >= getyTiles())
            return true;
        int type = mazeGrid[x][y].getType();
        return type == 6;
    }


    /**
     * @param colNumber numer kolmuny w mazeGrid
     * @return współrzędna x w pikselach
     */
    public int calcXPos(int colNumber) {
        return (colNumber + xOffset) * tileGAP;
    }

    /**
     * @param rowNumber numer rzędu w mazeGrid
     * @return współrzędna y w pikselach
     */
    public int calcYPos(int rowNumber) {
        return (rowNumber + yOffsetTop) * tileGAP;
    }


    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public String getMazePic() {
        return mazePic;
    }

    public int getxTiles() {
        return xTiles;
    }

    public int getyTiles() {
        return yTiles;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffsetTop() {
        return yOffsetTop;
    }

    public int getyOffsetBottom() {
        return yOffsetBottom;
    }

    public int getTileGAP() {
        return tileGAP;
    }

    public int getTileType(int x, int y) {
        return mazeGrid[x][y].getType();
    }


    public Tile getTile(int x, int y) {
        return mazeGrid[x][y];
    }


    public static int getInkyNR() {
        return inkyNR;
    }

    public static int getClydeNR() {
        return clydeNR;
    }

    public static int getPinkyNR() {
        return pinkyNR;
    }

    public static int getBlinkyNR() {
        return blinkyNR;
    }

    public static int getPacNR() {
        return pacNR;
    }

    public static Color[] getGhostColor() {
        return ghostColor;
    }


    public static int getGhostSpeed() {
        return ghostSpeed;
    }

    /**
     * @param x współrzędna w pikselach
     * @return numer kolumny w mazeGRid
     */
    public int calcGridX(int x) {
        x = x - xOffset * tileGAP;
        x = x / tileGAP;
        return x;
    }

    /**
     * @param y współrzędna w pikselach
     * @return numer rzędu w mazeGRid
     */
    public int calcGridY(int y) {
        y = y - yOffsetTop * tileGAP;
        y = y / tileGAP;
        return y;
    }

    public static double getDotsRatios(int nr) {
        return dotsRatios[nr];
    }

    public static int getGhostFrightendSped() {
        return ghostFrightendSped;
    }
}

