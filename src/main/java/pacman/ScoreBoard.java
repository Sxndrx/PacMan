package pacman;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import pacman.Maze.Maze;

/**
 * Tablica punktow
 */
public class ScoreBoard extends AnchorPane {
    /**
     * połączone z points PacMana
     */
    private SimpleIntegerProperty points;
    /**
     * połączone z lifes PacMana
     */
    private SimpleIntegerProperty lifes;
    private HBox board;
    /**
     * Tablice Circle reprezentujących życia
     */
    private Circle[] lifesImages;
    private Group root;
    private Maze maze;
    private int boardHeight;
    private int sceneHeight;
    private final Color LifeColor = Color.YELLOWGREEN;
    private Label name;
    private Label pointLabel;

    public ScoreBoard(Group root, Maze maze) {
       this.maze=maze;
       this.root = root;

        points = new SimpleIntegerProperty(0);
        lifes = new SimpleIntegerProperty(0);
        board = new HBox();
        sceneHeight = GameData.getHeight();
        boardHeight = maze.getGameData().getyOffsetBottom()*maze.getGameData().getTileGAP();
        board.setMaxSize(GameData.getWidth(), boardHeight);
        lifesImages = new Circle[3];
        name = new Label("Points ");
        pointLabel = new Label();
        pointLabel.textProperty().bind(points.asString());
        board.setPadding(new Insets(5, -10, 10, 40));
        board.setSpacing(10);


        drawBoard();


        super.getChildren().add(board);   // Add grid from Example 1-5
        super.setTopAnchor(board, (double) (sceneHeight-boardHeight));
        super.setVisible(true);
    }

    /**
     * Rysowanie ScoreBoard
     */
    public void drawBoard(){

        for(int i=0; i<3; i++) {
            lifesImages[i] = new Circle(12, LifeColor);
            board.getChildren().add(lifesImages[i]);
        }

        pointLabel.setFont(Font.font("Cambria", 20));
        name.setFont(Font.font("Cambria", 20));
        board.getChildren().add(name);
        board.getChildren().add(pointLabel);

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

    /**
     * Wywoływana po utracie życia, chowa Circle
     */
    public void hideLife(){
        for(int i=0; i<3; i++){
            if(lifes.get()-i<=0){
                lifesImages[i].setVisible(false);
            }
        }
    }

    /**
     * Wszystkie Circle na widoczne
     */
    public void resetLifes(){
        for(int i=0; i<3; i++){
                lifesImages[i].setVisible(true);
        }
    }


}
