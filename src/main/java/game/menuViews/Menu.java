package game.menuViews;

import game.Utils;
import game.dataBase.Score;
import game.dataBase.ScoreDAO;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import game.pacman.GameData;
import game.pacman.Maze.Maze;
import game.pacman.SceneController.SceneController;

import java.util.List;


public class Menu extends Group {
    private SceneController sceneController;
    private Button playButton;
    private Button exitButton;
    private Button showTenBestButton;
    private ComboBox<String> comboBox;
    private BorderPane borderPane;
    private Scene scene;
    private Stage stage;
    private Maze maze;
    private FlowPane flowPane;
    private Label name;
    private Label points;
    private Label date;
    GridPane table;
    AnchorPane anchorPane;
    String show = "TOP TEN";
    String hide = "HIDE RESULTS";

    public Menu(Scene scene, Stage stage, Maze maze) {

        this.stage = stage;
        this.maze = maze;

        playButton = new Button("PLAY");
        playButton.setPrefWidth(200);
        playButton.setOnAction(event -> {
            System.out.println(comboBox.getValue());
            System.out.println("PLAY");
            GameData.setDifficulty(getDifficulty(comboBox.getValue()));
            maze.resetGame();
            sceneController.displayMaze();
            table.setVisible(false);
        });

        exitButton = new Button("EXIT");
        exitButton.setPrefWidth(playButton.getPrefWidth());
        exitButton.setOnAction(event -> {
            Utils.exitGame();
        });

        showTenBestButton = new Button(show);
        showTenBestButton.setPrefWidth(playButton.getPrefWidth());
        showTenBestButton.setOnAction(event -> {
            if(showTenBestButton.getText().equals(show)){
                List<Score> list;
                list = ScoreDAO.getHighest(10);
                buildTableView(list);
                table.setVisible(true);
                showTenBestButton.setText(hide);
            }else{
                table.setVisible(false);
                showTenBestButton.setText(show);
            }
        });

        comboBox = new ComboBox<>();
        comboBox.getItems().addAll("TOO EASY", "EASY", "MEDIUM", "HARD", "EXTREME");
        comboBox.setValue("MEDIUM");
        comboBox.setPrefWidth(playButton.getPrefWidth());
        comboBox.getEditor().setAlignment(Pos.CENTER);
        flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setVgap(10);
        flowPane.setPrefWidth(stage.getWidth());
        table = new GridPane();
        table.setGridLinesVisible(true);
        table.setPadding(new Insets(20, 20, 20, 20));
        table.setVisible(false);
        table.setMinSize(stage.getWidth(), 400);
        flowPane.getChildren().addAll(comboBox, playButton, exitButton, showTenBestButton);

        anchorPane = new AnchorPane();
        anchorPane.setPrefSize(stage.getWidth(), stage.getHeight());
        AnchorPane.setTopAnchor(flowPane, 20.0);
        AnchorPane.setBottomAnchor(table, 0.0);
        anchorPane.getChildren().addAll(flowPane, table);
        anchorPane.setVisible(true);

        this.getChildren().addAll(anchorPane);
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    private double getDifficulty(String dif) {
        switch (dif) {
            case "TOO EASY":
                return 0;
            case "EASY":
                return 0.1;
            case "MEDIUM":
                return 0.2;
            case "HARD":
                return 0.25;
            case "EXTREME":
                return 0.5;
            default:
                return 0.2;
        }
    }


    private void buildTableView(List<Score> records) {
        table.getChildren().removeAll();

        name = new Label("NAME\n");
        points = new Label("POINTS\n");
        date = new Label("DATE\n");
        for(Score record : records){
            name.setText(name.getText().concat(record.getName() + "\n"));
            points.setText(points.getText().concat(record.getScore() + "\n"));
            if(record.getPlaydate()!=null){
                date.setText(date.getText().concat(record.getPlaydate().toString() + "\n"));
            }
        }


        name.setMinWidth(stage.getWidth()/3-60);
        name.setVisible(true);
        points.setMinWidth(stage.getWidth()/3-60);
        points.setVisible(true);
        date.setMinWidth(stage.getWidth()/3-60);
        date.setVisible(true);

        GridPane.setRowIndex(name, 0);
        GridPane.setColumnIndex(name, 0);
        GridPane.setHgrow(name, Priority.ALWAYS);
        GridPane.setRowIndex(points, 0);
        GridPane.setColumnIndex(points, 1);
        GridPane.setHgrow(points, Priority.ALWAYS);
        GridPane.setRowIndex(date, 0);
        GridPane.setColumnIndex(date, 2);
        GridPane.setHgrow(date, Priority.ALWAYS);
        table.getChildren().addAll(name, points, date);

    }

}
