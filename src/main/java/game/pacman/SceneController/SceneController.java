package game.pacman.SceneController;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
    private Stage primaryStage;
    private Scene mazeScene;
    private Scene menuScene;
    private Scene endScene;

    public SceneController(Stage primaryStage, Scene mazeScene, Scene menuScene, Scene endScene) {
        this.primaryStage = primaryStage;
        this.mazeScene = mazeScene;
        this.menuScene = menuScene;
        this.endScene = endScene;
    }

    public void displayMaze(){
        primaryStage.setScene(mazeScene);
    }

    public void displayMenu(){
        primaryStage.setScene(menuScene);
    }

    public void displayEnd(){
        primaryStage.setScene(endScene);
    }

    public double getWidth(){
        return primaryStage.getWidth();
    }
}
