package pacman;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import menuUI.EndGameMes;
import menuUI.SceneController;
import pacman.characters.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Maze{
    private Group root;
    private ImageView mazeImage;
    /**
     * dane o grze
     */
    private GameData gameData;
    /**
     * mapa Dots klucz kratka, na której znajduje się Dot (value)
     */
    private Map<Tile, Dot> dots;
    private PacMan pacMan;
    private Blinky blinky;
    private Clyde clyde;
    private Pinky pinky;
    private Inky inky;
    private SimpleBooleanProperty paused;
    /**
     * Tablica punktow
     */
    private ScoreBoard scoreBoard;
    private volatile int[][] imageCoordinates;
    private Thread[] charThreads;
    private SceneController sceneController;
    private EndGameMes endGameMes;
    /**
     * Komunikat wyświetlany gdy gra jest wstrzymana
     */
    private Label pauseLabel;
    /**
     * Komunikat po zakączeniu gry
     */
    private Label endMessage;

    public Maze(Group root, EndGameMes endGameMes) throws FileNotFoundException {
        this.root = root;
        this.endGameMes = endGameMes;
        initialize();
    }

    private void initialize() throws FileNotFoundException {
        gameData = new GameData();
        Image maze = new Image(new FileInputStream(gameData.getMazePic()));
        mazeImage = new ImageView(maze);
        this.root.getChildren().add(mazeImage);
        paused = new SimpleBooleanProperty(true);
        imageCoordinates = new int[5][2];
        charThreads = new Thread[5];

        pauseLabel = new Label();
        pauseLabel.setText("TO START " +
                           "OR PAUSE\n PRESS \"P\" ");
        pauseLabel.setPrefSize(330, 200);
        pauseLabel.setTextAlignment(TextAlignment.CENTER);
        pauseLabel.setLayoutX(135);
        pauseLabel.setLayoutY(240);
        pauseLabel.setStyle("-fx-background-color: BLACK; -fx-font-size: 30px; -fx-text-fill: ORANGE");
        pauseLabel.setVisible(true);

        endMessage = new Label();
        endMessage.setVisible(false);




        dots = new HashMap<>();
        createDots();


        scoreBoard = new ScoreBoard(root, this);
        root.getChildren().add(scoreBoard);

        pacMan = new PacMan(root, this, imageCoordinates, GameData.getPacNR());
        charThreads[GameData.getPacNR()] = new Thread(pacMan);
        charThreads[GameData.getPacNR()] .setDaemon(true);

        blinky = new Blinky(this, root, imageCoordinates, GameData.getBlinkyNR());
        charThreads[GameData.getBlinkyNR()] = new Thread(blinky);
        charThreads[GameData.getBlinkyNR()].setDaemon(true);

        clyde = new Clyde(this, root, imageCoordinates, GameData.getClydeNR());
        charThreads[GameData.getClydeNR()]= new Thread(clyde);
        charThreads[GameData.getClydeNR()] .setDaemon(true);

        pinky = new Pinky(this, root, imageCoordinates, GameData.getPinkyNR());
        charThreads[GameData.getPinkyNR()]  = new Thread(pinky);
        charThreads[GameData.getPinkyNR()].setDaemon(true);

        inky = new Inky(this, root, imageCoordinates, GameData.getInkyNR());
        charThreads[GameData.getInkyNR()] = new Thread(inky);
        charThreads[GameData.getInkyNR()].setDaemon(true);

        scoreBoard.lifesProperty().bind(pacMan.lifesProperty());
        scoreBoard.pointsProperty().bind(pacMan.pointsProperty());

        startGame();

        root.getChildren().add(pauseLabel);
        root.getChildren().add(endMessage);

    }


    /**
     * Wystartuj wątki
     */
    public void startGame(){
        for (Thread thread:charThreads){
            thread.start();
        }
   /* charThreads[0].start();
    charThreads[4].start();*/
    }

    /**
     * Na podstawie gameData utwórz mapę dots
     */
    private void createDots(){
        for(int i = 0; i< gameData.getxTiles(); i++){
            for(int j = 0; j< gameData.getyTiles(); j++) {
                int type = gameData.getTileType(i, j);
                if(type ==4 || type == 8){
                    double radius = type==4?2:5;
                    Dot d = new Dot(gameData.calcXPos(i)+10, gameData.calcYPos(j)+10, radius , Color.YELLOW);
                    d.pausedProperty().bind(paused);
                    root.getChildren().add(d);
                    dots.put(gameData.getTile(i,j), d);
                }
            }
        }
    }

    /**
     * Przechwyć naciśnięcie "P" i klawiszt strzałek
     */
    public void handleKeyboard(KeyCode keyCode) {
        if(keyCode.isArrowKey()){
            String dir = keyCode.toString();
            pacMan.setDirection(dir);
        }
        if(keyCode.toString()=="P"){
            if(paused.get()) {
                setPaused(false);
                pauseLabel.setVisible(false);
            } else {
                pauseLabel.setVisible(true);
                setPaused(true);
            }
            if(endMessage.isVisible())
                endMessage.setVisible(false);
        }
        if(keyCode.toString()=="W"){
            winGame();
        }
        if(keyCode.toString()=="L"){
            looseGame();
        }
        if(keyCode.toString()=="ESCAPE"){
            looseGame();
        }
    }

    /**
     * Update maze po straconym życiu przez pacmana
     */
    public void looseLife(){
        setPaused(true);
        scoreBoard.hideLife();
        if(scoreBoard.lifesProperty().get()==0){
            looseGame();
        }
        else{
           /* blinky.lostLife();
            clyde.lostLife();
            pinky.lostLife();
            inky.lostLife();*/
           blinky.reset();
           clyde.reset();
           pinky.reset();
           inky.reset();
//            pauseLabel.setVisible(true);
        }

    }

    /**
     * Update maze po przegranej grze
     */
    private void looseGame() {
        endGameMes.setMessage(false, scoreBoard.getPoints());
        sceneController.displayEnd();
        resetGame();
    }

    /**
     * Przywrócenie stanu początkowego maze
     */
    public void resetGame(){

        setPaused(true);
        scoreBoard.resetLifes();
        pacMan.reset();
        blinky.reset();
        clyde.reset();
        inky.reset();
        pinky.reset();
        for(Map.Entry<Tile, Dot> dot: dots.entrySet())
            dot.getValue().setVisible(true);
        //notifyAll();
    }

    /**
     * update maze po wygranej grze
     */
    public void winGame(){

        endGameMes.setMessage(true, scoreBoard.getPoints());
        sceneController.displayEnd();

        resetGame();
    }

    /**
     * ustaw tryb frightend wszstkim postaciom (Character)
     */
    public void setFrightendGameMode(){
        setPaused(true);
        blinky.setFrightendMode();
        clyde.setFrightendMode();
        inky.setFrightendMode();
        pinky.setFrightendMode();
        pacMan.setFrightendMode();
        setPaused(false);
    }

    /**
     * @param nr numer Ghost do zjedzenia
     *           zjedz duszka
     */
    public void eatGhost(int nr){
        switch (nr){
            case 0:
                blinky.ghostEaten();
                return;
            case 1:
                clyde.ghostEaten();
                return;
            case 2:
                pinky.ghostEaten();
                return;
            case 3:
                inky.ghostEaten();
                return;
        }
    }

    public GameData getGameData(){
        return gameData;
    }

    public PacMan getPacMan() {
        return pacMan;
    }

    public boolean isPaused() {
        return paused.get();
    }

    public SimpleBooleanProperty pausedProperty() {
        return paused;
    }

    public synchronized void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    public synchronized boolean getPaused(){ return this.paused.get();}

    public Map<Tile, Dot> getDots() {
        return dots;
    }

    public void setDots(Map<Tile, Dot> dots) {
        this.dots = dots;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public void setSceneController(SceneController sceneController){
        this.sceneController = sceneController;
    }
}

