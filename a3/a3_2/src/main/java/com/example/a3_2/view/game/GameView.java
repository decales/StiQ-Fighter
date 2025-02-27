package com.example.a3_2.view.game;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Fighter.FighterSide;
import com.example.a3_2.model.Model.AppState;
import com.example.a3_2.model.Model.GameMode;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

public class GameView extends StackPane implements PublishSubscribe {

  private FighterView leftFighterView;
  private FighterView rightFighterView;
  private FighterBar leftBar;
  private FighterBar rightBar;
  private Line floor;
  private QuitButton quitButton;

  public GameView(Controller controller) {
    // fighter sprites
    Pane fighterPane = new Pane();
    leftFighterView = new FighterView();
    rightFighterView = new FighterView();

    // fighter bars
    leftBar = new FighterBar(FighterSide.left);
    rightBar = new FighterBar(FighterSide.right);

    floor = new Line(); // very lazy 'floor' in background
    fighterPane.getChildren().addAll(leftFighterView, rightFighterView, floor);

    quitButton = new QuitButton(controller);
    setAlignment(quitButton, Pos.BOTTOM_LEFT);

    getChildren().addAll(leftBar, rightBar, fighterPane, quitButton);
  }


  public void update(
      AppState appState, int frame, double viewSize, 
      GameMode gameMode, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins) {

    if (appState == AppState.inGame) {

      setVisible(true);

      // Draw the floor of the stage
      floor.setStrokeWidth(viewSize * 0.005);
      floor.setStartY(viewSize * 0.4);
      floor.setEndY(viewSize * 0.4);
      floor.setStartX(0);
      floor.setEndX(viewSize);

      leftFighterView.update(leftFighter, frame);
      rightFighterView.update(rightFighter, frame);

      leftBar.update(viewSize, frame, leftFighter.healthPoints, leftWins);
      rightBar.update(viewSize, frame, rightFighter.healthPoints, rightWins);

      quitButton.update(viewSize);
      setMargin(quitButton, new Insets(viewSize * 0.01));
    }
    else setVisible(false);
  }
}


