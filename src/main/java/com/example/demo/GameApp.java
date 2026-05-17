package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
        GamePane game = new GamePane();

        Scene scene = new Scene(game, 800, 450);

        stage.setTitle("Crystal Princess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        game.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}