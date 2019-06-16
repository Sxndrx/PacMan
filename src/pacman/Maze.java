package pacman;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

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
    private Thread charThreads[];

    /**
     * Komunikat wyświetlany gdy gra jest wstrzymana
     */
    private Label pauseLabel;
    /**
     * Komunikat po zakączeniu gry
     */
    private Label endMessage;

    public Maze(Group root) throws FileNotFoundException {
        this.root = root;
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
            Platform.runLater(()->thread.start());
        }
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
            paused.set(!paused.get());
            if(paused.get())
                pauseLabel.setVisible(true);
            else
                pauseLabel.setVisible(false);
            if(endMessage.isVisible())
                endMessage.setVisible(false);
        }
    }

    /**
     * Update maze po straconym życiu przez pacmana
     */
    public void looseLife(){
        paused.set(true);
        scoreBoard.hideLife();
        if(scoreBoard.lifesProperty().get()==0){
            looseGame();
        }
        else{
            blinky.moveAtStart();
            pacMan.moveAtStart();
            clyde.moveAtStart();
            pinky.moveAtStart();
            inky.moveAtStart();
            pauseLabel.setVisible(true);
        }

    }

    /**
     * Update maze po przegranej grze
     */
    private void looseGame() {
        endMessage.setText("SORRY \nYOU LOST\n\nYOU GOT:\n "+ scoreBoard.pointsProperty().get() +" POINTS\n\n PRESS \"P\" \nTO CONTINUE");
        endMessage.setTextFill(Color.DARKBLUE);
        endMessage.setPrefSize(360, 500);
        endMessage.setTextAlignment(TextAlignment.CENTER);

        endMessage.setStyle("-fx-background-color: BLACK; -fx-font-size: 50px");
        endMessage.setLayoutY(80);
        endMessage.setLayoutX(120);
        endMessage.setVisible(true);
        resetGame();
    }

    /**
     * Przywrócenie stanu początkowego maze
     */
    public void resetGame(){
        paused.set(true);
        scoreBoard.resetLifes();
        pacMan.reset();
        blinky.reset();
        clyde.reset();
        inky.reset();
        for(Map.Entry<Tile, Dot> dot: dots.entrySet())
            dot.getValue().setVisible(true);
    }

    /**
     * update maze po wygranej grze
     */
    public void winGame(){
        endMessage.setText("CONGRATULATIONS \nYOU WON!!\n\nYOU GOT: \n"+ scoreBoard.pointsProperty().get() +" POINTS\n\n PRESS \"P\" \nTO CONTINUE");
        endMessage.setStyle(" -fx-text-fill: DARKMAGENTA");
        resetGame();
        endMessage.setPrefSize(360, 500);
        endMessage.setTextAlignment(TextAlignment.CENTER);
        endMessage.setStyle("-fx-background-color: BLACK; -fx-font-size: 50px");
        endMessage.setLayoutY(80);
        endMessage.setLayoutX(120);
        endMessage.setVisible(true);
    }

    /**
     * ustaw tryb frightend wszstkim postaciom (Character)
     */
    public void setFrightendGameMode(){
        paused.set(true);
        pacMan.setFrightned();
        blinky.setFrightned();
        clyde.setFrightned();
        inky.setFrightned();
        pinky.setFrightned();
        paused.set(false);
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

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

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

}