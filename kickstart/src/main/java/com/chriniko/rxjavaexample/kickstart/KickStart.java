package com.chriniko.rxjavaexample.kickstart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class KickStart extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(
                FXMLLoader.load(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResource("ui_app.fxml")
                        )
                ));

        primaryStage.setTitle("ChriNiko News Feed Application");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
