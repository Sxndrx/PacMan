/**
 * @author Patrycja Uhl I7Y6S1
 */
package game.pacman;

import game.dataBase.HibernateUtil;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import game.menuViews.EndGameMes;
import game.menuViews.Menu;
import game.pacman.SceneController.SceneController;
import game.pacman.Maze.Maze;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//
        HibernateUtil.getSessionFactory().openSession();

        primaryStage.setTitle("Pac-Man by Patrycja Uhl //June 2019");
        primaryStage.setWidth(GameData.getWidth());
        primaryStage.setHeight(GameData.getHeight());
        primaryStage.setResizable(false);

        System.out.println("Main Thred: " + Thread.currentThread().getId());
        final EndGameMes endGameMes = new EndGameMes();
        final Scene endScene  = new Scene(endGameMes);
        endScene.setFill(Color.web("#b9d8e1"));

        final Group root = new Group();
        final Scene mazeScene = new Scene(root);
        Maze maze = new Maze(root, endGameMes);
        mazeScene.addEventHandler(KeyEvent.KEY_PRESSED, KeyEvent->maze.handleKeyboard(KeyEvent.getCode()));

        final Menu menu = new Menu(mazeScene, primaryStage, maze);
        Scene menuScene = new Scene(menu);
        menuScene.setFill(Color.web("#b9d8e1"));


        SceneController sceneController = new SceneController(primaryStage, mazeScene, menuScene, endScene);
        menu.setSceneController(sceneController);
        maze.setSceneController(sceneController);
        endGameMes.setSceneController(sceneController);
        primaryStage.setScene(menuScene);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}