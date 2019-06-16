package pacman;


import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
    protected int dotsEaten;

    /**
     * Ghost sciga PacMana
     */
    protected boolean chase;
    /**
     * PacMan może zjeść Ghost
     */
    protected boolean frightned;
    /**
     * Ghost porusza się do wyznaczonego punktu
     */
    protected boolean scatter;

    public Character(Maze maze, Group group, int[][] gCodnt, int nr) {
        frightned = false;
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
        dotsEaten = 0;
        createTimer();
    }

    /**
     * Wymagana zaimplementowania, zrobienie jednego kroku animacji
     */
    protected abstract void moveOneStep();

    /**
     * Przenosi na pozycję startową
     */
    public void moveAtStart() {
        this.x = startX;
        this.y = startY;
        steps = 0;
        synchronized (maze) {
            Platform.runLater(() -> imageY.set(maze.getGameData().calcYPos(y) + 10));
            Platform.runLater(() -> imageX.set(maze.getGameData().calcXPos(x) + 10));
            imageCoordinates[nr][0] = imageX.get();
            imageCoordinates[nr][1] = imageY.get();
        }
        startStatus();
    }

    /**
     * resetuje stan Character, wymagana zaimplementowania
     */
    protected abstract void reset();

    /**
     * ustawia status początkowy Character
     */
    protected abstract void startStatus();

    /**
     * zmienia tryb na frightend
     */
    protected abstract void setFrightned();

    /**
     * Powrót do trybu Chase
     */
    protected abstract void setNormal();

    /**
     * Utworzenie nowego timera
     */
    protected void createTimer() {
        timer = new AnimationTimer() {
            /**
             * Czas od ostatniego przemieszczenia
             */
            private long lastUpdate = 0;
            /**
             * Liczba pulsow od zmiany trybu chase<->scatter
             */
            private int sinceModeChange = 0;
            /**
             * Liczba pulsow od wlaczenia trybu frightend
             */
            private int sinceFrightendOn = 0;

            /**
             * Najpierw sprawdzenie czy zmiana trybu i zmiana,
             * co 45ms porusz o krok
             */
            @Override
            public void handle(long l) {
                if (scatter && sinceModeChange == 110) {
                    sinceModeChange = 0;
                    scatter = false;
                    chase = true;
                } else if (chase && sinceModeChange == 220) {
                    sinceModeChange = 0;
                    scatter = true;
                    chase = false;
                } else if (frightned && sinceFrightendOn == 176) {
                    Platform.runLater(() -> setNormal());
                    sinceFrightendOn = 0;
                }
                if (l - lastUpdate >= 45000000) {
                    moveOneStep();
                    lastUpdate = l;
                    if (frightned) {
                        sinceFrightendOn++;
                    } else {
                        sinceModeChange++;
                    }
                }
            }
        };
    }

    /**
     * @return nr Ghost z którym nastąpiła kolizja, -1 jeśli kolizja nie nastąpiła
     * Kolizja jeśli środki imageCircle dwóch Character są w odległości mniejszej niż 2*radius
     */
    protected int isGhostCollision() {
        synchronized (maze) {
            for (int i = 0; i < 4; i++) {
                int ghostX = imageCoordinates[i][0];
                int ghostY = imageCoordinates[i][1];
                //   System.out.println("gX: "+ghostX +" gY: " +ghostY +"   x: " +imageX.get()+ " y: "+imageY.get());
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
        if (maze.getGameData().isWall(x + dirX, y + dirY))
            return false;
        else
            return true;
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
}
