package com.example.StiqFighter;

import com.example.StiqFighter.controller.Controller;
import com.example.StiqFighter.model.Model;
import com.example.StiqFighter.view.game.GameView;
import com.example.StiqFighter.view.menu.MenuView;
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
      double displayRatio = 0.75;

      double viewWidth = displayWidth * displayRatio;
      double viewHeight = displayHeight * displayRatio;

      // model + controller
      Model model = new Model(viewWidth);
      Controller controller = new Controller(model);

      // ui components
      StackPane root = new StackPane();
      MenuView menuView = new MenuView(controller);
      GameView gameView = new GameView(controller);
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
