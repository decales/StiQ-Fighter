package com.example.a3_2.view;

import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Fighter.FighterSide;
import com.example.a3_2.model.Model.AppState;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class GameView extends Pane implements PublishSubscribe {

  private FighterView leftFighterView;
  private FighterView rightFighterView;

  private HBox topBar;
  private FighterBar leftBar;
  private FighterBar rightBar;

  private Line floor;

  public GameView() {
    // fighter sprites
    leftFighterView = new FighterView();
    rightFighterView = new FighterView();

    // fighter bars
    topBar = new HBox();
    leftBar = new FighterBar(FighterSide.left);
    rightBar = new FighterBar(FighterSide.right);
    topBar.getChildren().addAll(leftBar, rightBar);

    floor = new Line();

    getChildren().addAll(leftFighterView, rightFighterView, topBar, floor);
  }

  public void update(AppState appState, int frame, double viewSize, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins) {
    if (appState == AppState.inGame) {

      setVisible(true);

      // Draw the floor of the stage
      floor.setStrokeWidth(viewSize * 0.005);
      floor.setStartY(viewSize * 0.4);
      floor.setEndY(viewSize * 0.4);
      floor.setStartX(0);
      floor.setEndX(viewSize);

      topBar.setMinWidth(viewSize);
      topBar.setPadding(new Insets(viewSize * 0.0075));

      leftFighterView.update(leftFighter, frame);
      rightFighterView.update(rightFighter, frame);

      leftBar.update(viewSize, frame, leftFighter.healthPoints, leftWins);
      rightBar.update(viewSize, frame, rightFighter.healthPoints, rightWins);
    }
    else setVisible(false);
  }
}


