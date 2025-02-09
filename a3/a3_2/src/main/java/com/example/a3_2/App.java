package com.example.a3_2;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Model;
import com.example.a3_2.view.GameView;
import com.example.a3_2.view.HealthBar;
import com.example.a3_2.view.ModeSelectionView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

        // model + controller
        Model model = new Model(viewWidth, viewHeight);
        Controller controller = new Controller(model);

        // ui components
        StackPane root = new StackPane();
        ModeSelectionView modeSelectionView = new ModeSelectionView(controller);
        GameView gameView = new GameView();
        gameView.setPrefSize(displayWidth, displayHeight);

        root.getChildren().addAll(modeSelectionView, gameView);
        model.addSubscribers(modeSelectionView, gameView);

        Scene scene = new Scene(root, viewWidth, viewHeight);
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyReleased(controller::handleKeyReleased);

        stage.setTitle("");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
