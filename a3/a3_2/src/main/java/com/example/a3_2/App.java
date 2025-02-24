package com.example.a3_2;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Model;
import com.example.a3_2.view.GameView;
import com.example.a3_2.view.MenuView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
        Model model = new Model(viewWidth);
        Controller controller = new Controller(model);

        // ui components
        StackPane root = new StackPane();
        MenuView menuView = new MenuView(controller);
        GameView gameView = new GameView();
        gameView.setPrefSize(displayWidth, displayHeight);

        root.getChildren().addAll(menuView, gameView);
        model.addSubscribers(menuView, gameView);

        Scene scene = new Scene(root, viewWidth, viewHeight);
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyReleased(controller::handleKeyReleased);

        stage.setTitle("Q-Duel");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
