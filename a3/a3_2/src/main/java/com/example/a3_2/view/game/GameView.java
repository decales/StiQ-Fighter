package com.example.a3_2.view.game;

import java.util.HashMap;
import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;
import com.example.a3_2.model.Model.AppState;
import com.example.a3_2.model.Model.GameMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
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

    HashMap<ActionState, Image[]> actionMap = new HashMap<>(); // initialize a map of sprites to animate the fighters

    // idle, movingLeft, movingRight, preAttacking, attacking, postAttacking, preBlocking, blocking, postBlocking, deflecting, parried 
    int[] actionFileCount = { 19, 9, 9, 7, 4, 9, 6, 1, 9, 7, 22 };
    for (int i = 0; i < ActionState.values().length; i++) {
      Image[] spriteFiles = new Image[actionFileCount[i]];
      for (int j = 0; j < spriteFiles.length; j++) {
        spriteFiles[j] = new Image(getClass().getResource(
              String.format("/game/fighter/%s/%s_%04d.png", ActionState.values()[i], ActionState.values()[i], j + 1)).toString());
      }
      actionMap.put(ActionState.values()[i], spriteFiles);
    }

    // fighter sprites
    Pane fighterPane = new Pane();
    leftFighterView = new FighterView(actionMap);
    rightFighterView = new FighterView(actionMap);

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


