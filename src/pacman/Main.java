/**
 * @author Patrycja Uhl I7Y6S1
 */
package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Pac-Man by Patrycja Uhl //June 2019");
        primaryStage.setWidth(GameData.getWidth());
        primaryStage.setHeight(GameData.getHeight());
        primaryStage.setResizable(false);

        System.out.println("Main Thred: " + Thread.currentThread().getId());
        final Group root = new Group();
        final Scene scene = new Scene(root);
        Maze maze = new Maze(root);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, KeyEvent->maze.handleKeyboard(KeyEvent.getCode()));
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}