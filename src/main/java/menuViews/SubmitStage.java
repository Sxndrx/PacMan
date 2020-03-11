package menuViews;

import dataBase.DBAccess;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;


public class SubmitStage {

    public SubmitStage(int points) {

        FlowPane flowPane = new FlowPane();
        TextField textField = new TextField();
        textField.setPrefWidth(200);
        Label label = new Label();
        label.setText(points+ " \ninsert your name");
        label.setPrefWidth(textField.getPrefWidth());
        Stage stage = new Stage();
        Group group = new Group();
        Scene scene = new Scene(group);
        Label labelWrong = new Label();
        labelWrong = new Label("name can't be empty");
        labelWrong.setTextFill(Color.RED);

        Button submitButton;
        submitButton = new Button("SUBMIT");
        submitButton.setPrefWidth(textField.getPrefWidth());
        Label finalLabelWrong = labelWrong;
        submitButton.setOnAction(event -> {
            String name = textField.getText();
            if(name.length()>0) {
                if(name.length()>30){
                    name = name.substring(0, 29);
                }
                DBAccess dbAccess = new DBAccess();
                try {

                    dbAccess.submitScore(points, textField.getText());
                    stage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else{
                finalLabelWrong.setVisible(true);
            }
        });
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(120);
        stage.setResizable(false);
        flowPane = new FlowPane();
        flowPane.setPrefHeight(120);
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPadding(new Insets(0, 50, 0, 50));
        flowPane.getChildren().addAll(label, textField, submitButton, labelWrong);
        labelWrong.setVisible(false);
        group.getChildren().addAll(flowPane);
        stage.show();
    }
}
