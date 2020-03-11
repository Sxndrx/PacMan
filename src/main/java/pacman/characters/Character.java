package pacman.characters;


import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import pacman.GameData;
import pacman.Maze.Maze;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * klasa Character implementuje interfejs Runnable.
 * Klasa rodzic dla każdej postaci w grze.
 * Potrzebuje instancji klasy Maze, Group, swojego numeru nr i współdzielonej tablicy współrzędnych obrazów innych postaci.
 */
public abstract class Character implements Runnable {
    /**
     * imageCircle - Circle będące reprezentacją postci
     */
    protected Circle imageCircle;
    /**
     * współrzędna x kratki w labiryncie
     */
    protected int x;
    /**
     * współrzędna y kratki w labiryncie
     */
    protected int y;
    /**
     * współrzędna x środka imageCircle
     */
    protected IntegerProperty imageX;
    /**
     * współrzędna y środka imageCircle
     */
    protected IntegerProperty imageY;
    /**
     * prędkość poruszenia postaci, ile pikseli przy pojedynczym przesunięciu
     */
    protected int speed;
    /**
     * kierunek poruszania po osi X (-1, 0, 1)
     */
    protected int directionX;
    /**
     * kierunek poruszania po osi Y (-1, 0, 1)
     */
    protected int directionY;
    /**
     * x pozycji, w której Character zaczyna grę
     */
    protected int startX;

    /**
     * y pozycji, w której Character zaczyna grę
     */
    protected int startY;
    /**
     * grupa węzłów UI JavaFX, do której należy Character
     */
    protected Group root;
    /**
     * Maze, w którym porusza się Character
     */
    protected Maze maze;
    /**
     * Timer pełniący rolę głównej pętli programowej dla postaci
     */
    protected AnimationTimer timer;
    /**
     * czy gra jest spauzowana
     */
    protected SimpleBooleanProperty paused;
    /**
     * Promien imageCircle
     */
    protected final int radius = 12;

    /**
     * Czy imageCircle bedzie przemieszczany
     */
    protected boolean moving;
    /**
     * Ile krokow animacji bylo wykonane
     */
    protected int steps;
    /**
     * Kroki animacji do zmiany kratki
     */
    protected int fullSteps;
    protected Color color;
    protected volatile int[][] imageCoordinates;
    protected int nr;
    protected int dotsCount;

    /**
     * Ghost sciga PacMana
     */
    protected AtomicBoolean chase;
    /** PacMan może zjeść Ghost*/
    protected AtomicBoolean frightned;
    /** Ghost porusza się do wyznaczonego punktu*/
    protected AtomicBoolean scatter;


    /**
     * Czas od ostatniego przemieszczenia
     */
    protected long lastUpdate;
    /**
     * Liczba pulsow od zmiany trybu chase<->scatter
     */
    protected int sinceModeChange;
    /**
     * Liczba pulsow od wlaczenia trybu frightend
     */
    protected int sinceFrightendOn;

    public Character(Maze maze, Group group, int[][] gCodnt, int nr) {
        this.maze = maze;
        this.root = group;
        paused = new SimpleBooleanProperty();
        paused.bind(maze.pausedProperty());
        this.imageCoordinates = gCodnt;
        color = GameData.getGhostColor()[nr];
        imageCircle = new Circle(radius, color);
        root.getChildren().add(imageCircle);
        this.nr = nr;
        dotsCount = maze.getDots().size();
        scatter = new AtomicBoolean();
        frightned = new AtomicBoolean();
        chase = new AtomicBoolean();
        createTimer();
    }

    /**
     * Wymagana zaimplementowania, zrobienie jednego kroku animacji
     */
    protected abstract void moveOneStep();

    /**
     * Przenosi na pozycję startową
     */
    public  void moveAtStart() {
        synchronized (this){

            this.x = startX;
            this.y = startY;
            steps = 0;
        }
        synchronized (maze) {
            Platform.runLater(() ->{
                imageY.set(maze.getGameData().calcYPos(y) + 10);
                imageX.set(maze.getGameData().calcXPos(x) + 10);
            } );
            imageCoordinates[nr][0] = imageX.get();
            imageCoordinates[nr][1] = imageY.get();
        }
    }


    /**
     * ustawia status początkowy Character
     */
    protected abstract void startStatus();


    /**
     * Utworzenie nowego timera
     */
    protected abstract void createTimer();

    /**
     * @return nr Ghost z którym nastąpiła kolizja, -1 jeśli kolizja nie nastąpiła
     * Kolizja jeśli środki imageCircle dwóch Character są w odległości mniejszej niż 2*radius
     */
    protected int isGhostCollision() {
        synchronized (maze) {
            for (int i = 0; i < 4; i++) {
                int ghostX = imageCoordinates[i][0];
                int ghostY = imageCoordinates[i][1];
                if (Math.abs(ghostX - imageX.get()) < 2 * radius && (Math.abs(ghostY - imageY.get()) < 2 * radius) && i != nr) {
                    return i;
                }
            }
            return -1;
        }
    }

    public synchronized void setDirection(int xEnd, int yEnd) {

    }

    public void setDirection(String direction) {

    }

    /**
     * @param dirX kierunek x
     * @param dirY kierunek y
     * @return false - nie da się przejść w tym kierunku, true - poprawny kierunek
     */
    public boolean validateDirection(int dirX, int dirY) {
        return !maze.getGameData().isWall(x + dirX, y + dirY);
    }

    /**
     * Teleport przez tunel
     */
    protected void doTeleport() {
        synchronized (maze) {
            imageX.set(maze.getGameData().calcXPos(x) + 10);
        }
    }

    protected void moveImageHorizontally() {
        synchronized (maze) {
            imageX.set(imageX.get() + directionX * speed);
        }
    }

    protected void moveImageVertically() {
        synchronized (maze) {
            imageY.set(imageY.get() + directionY * speed);
        }
    }

    /**
     * @param teleport czy nalezy przeniesc obrazek
     * za kazdym wywowłaniem  steps+1
     * update współdzielonej tablicy współrzędnych
     */
    protected void moveImage(boolean teleport) {
        try {
            steps = ((steps + 1) % fullSteps);
            if (teleport) {
                Platform.runLater(() -> doTeleport());
                steps = 0;
            } else if (directionX != 0) {
                Platform.runLater(() -> moveImageHorizontally());
            } else {
                Platform.runLater(() -> moveImageVertically());
            }

            synchronized (maze) {
                imageCoordinates[nr][0] = imageX.get();
                imageCoordinates[nr][1] = imageY.get();
            }
        }catch (ArithmeticException e){
            steps=0;
        }
    }


    public int getDirectionX() {
        return directionX;
    }


    public synchronized int getDirectionY() {
        return directionY;
    }


    /**
     * Uruchamia timer
     */
    @Override
    public void run() {
        timer.start();
    }

    protected abstract void setFrightendMode();
    protected abstract void setChaseMode();
}
