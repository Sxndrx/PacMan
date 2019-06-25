package menuUI;

import dataBase.DBAccess;
import javafx.application.Platform;
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
import pacman.GameData;
import pacman.Maze;

import java.util.ArrayList;


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
    private DBAccess dbAccess;
    private FlowPane flowPane;
    private Label name;
    private Label points;
    private Label date;
    GridPane table;
    AnchorPane anchorPane;
    String show = "TOP TEN";
    String hide = "HIDE RESULTS";
//    private HBox table;

    public Menu(Scene scene, Stage stage, Maze maze, DBAccess dbAccess) {

        this.stage = stage;
        this.maze = maze;
        this.dbAccess = dbAccess;

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
            System.out.println("EXIT");
            Platform.exit();
        });

        showTenBestButton = new Button(show);
        showTenBestButton.setPrefWidth(playButton.getPrefWidth());
        showTenBestButton.setOnAction(event -> {
            if(showTenBestButton.getText().equals(show)){
                ArrayList<String[]> list;
                list =  dbAccess.readTenScores();
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
        // flowPane.setPadding(new Insets(0, 200, 0, 200));
        table = new GridPane();
//        table = new HBox();
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


    private void buildTableView(ArrayList<String[]> records) {
        /*String n, p, d;
        n = "NAME\n";
        p = "POINTS\n";
        d = "DATE\n";*/
        table.getChildren().removeAll();

        name = new Label("NAME\n");
        points = new Label("POINTS\n");
        date = new Label("DATE\n");
        for(String[] record : records){
        //    System.out.println(record[0]);
            name.setText(name.getText().concat(record[0] + "\n"));
            points.setText(points.getText().concat(record[1] + "\n"));
            date.setText(date.getText().concat(record[2] + "\n"));
        }

   //     System.out.println(name.getText());;

        name.setMinWidth(stage.getWidth()/3-60);
        name.setVisible(true);
//        name.setLayoutY(300);
//        name.setLayoutX(stage.getMaxWidth()-90);
        points.setMinWidth(stage.getWidth()/3-60);
        points.setVisible(true);
//        points.setLayoutY(name.getLayoutY());
//        points.setLayoutX(name.getLayoutX()+name.getWidth());
        date.setMinWidth(stage.getWidth()/3-60);
        date.setVisible(true);
//        date.setLayoutY(name.getLayoutY());
//        date.setLayoutX(points.getLayoutX()+name.getWidth());

//        table.getChildren().addAll(name, points, date);
       // table.setVgap(10);
       // table.addColumn(1, name);
      //  table.setColumnSpan(name, (int) name.getMinWidth());
        //table.addColumn(2, points);
      //  table.setColumnSpan(points, (int) points.getMinWidth());
        //table.addColumn(3, date);
       // table.setColumnSpan(date, (int) date.getMinWidth());

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
