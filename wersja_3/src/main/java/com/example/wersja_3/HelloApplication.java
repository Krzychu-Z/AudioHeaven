package com.example.wersja_3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("gui1.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 525);
        stage.setTitle("AudioHeaven v1.0");
        stage.setScene(scene);
        stage.setMinHeight(200);
        stage.setMinWidth(400);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}