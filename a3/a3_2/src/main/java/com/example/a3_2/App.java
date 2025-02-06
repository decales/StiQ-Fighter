package com.example.a3_2;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Model;
import com.example.a3_2.view.Arena;
import com.example.a3_2.view.HealthBar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {

        // Screen information
        double displayWidth = Screen.getPrimary().getBounds().getWidth();
        double displayHeight = Screen.getPrimary().getBounds().getHeight();
        double displayRatio = 0.8;

        double viewWidth = displayWidth * displayRatio;
        double viewHeight = displayHeight * displayRatio;

        // Model
        Model model = new Model(viewWidth, viewHeight);

        // Controllers
        Controller commonController = new Controller(model);

        // // Initialize UI components
        VBox root = new VBox();
        Arena arena = new Arena();
        HealthBar healthBar = new HealthBar();
        arena.setPrefSize(displayWidth, displayHeight);

        root.getChildren().addAll(healthBar, arena);
        model.addSubscribers(healthBar, arena);

        Scene scene = new Scene(root, viewWidth, viewHeight);
        scene.setOnMouseClicked(commonController::handleMouseEvent);
        scene.setOnKeyPressed(commonController::handleKeyPressed);
        scene.setOnKeyReleased(commonController::handleKeyReleased);

        stage.setTitle("");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
