package game.menuViews;

import game.Utils;
import game.dataBase.Score;
import game.dataBase.ScoreDAO;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import game.pacman.SceneController.SceneController;

import java.util.List;


public class EndGameMes extends Group {
    private SceneController sceneController;

    private Label endMess;
    private Button restartButton;
    private Button backToMenu;
    private Button submitButton;
    private Button showResultButton;
    private Button exitButton;
    private FlowPane flowPane;

    private int points;
    private List<Score> highestPoints;

    public EndGameMes() {
        endMess = new Label();

        restartButton = new Button("RESTART");
        restartButton.setAlignment(Pos.CENTER);
        restartButton.setPrefWidth(200);
        restartButton.setOnAction(event -> sceneController.displayMaze());

        backToMenu = new Button("BACK TO MENU");
        backToMenu.setOnAction(event -> sceneController.displayMenu());
        backToMenu.setPrefWidth(restartButton.getPrefWidth());

        submitButton = new Button("SUBMIT");
        submitButton.setOnAction(event -> new SubmitStage(points));
        submitButton.setPrefWidth(restartButton.getPrefWidth());

        exitButton =  new Button("EXIT");
        exitButton.setOnAction(event -> Utils.exitGame());
        exitButton.setPrefWidth(restartButton.getPrefWidth());
        flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setVgap(10);
        flowPane.getChildren().addAll(endMess, restartButton, backToMenu, submitButton, exitButton);

        this.getChildren().addAll(flowPane);

    }


    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;

        flowPane.setPrefWidth(sceneController.getWidth());
    }

    public void setMessage(boolean win, int points){
        this.points = points;
        if(win){
            endMess.setText("CONGRATULATIONS!\n");
        }
        else endMess.setText("TRY AGAIN!\n");

        highestPoints = ScoreDAO.getHighest(1);

        if(points>highestPoints.get(0).getScore()){
            endMess.setText(endMess.getText().concat("IT'S THE HIGHEST SCORE!\n"));
        }

        endMess.setText(endMess.getText().concat("YOU GOT " + points));


    }
}
