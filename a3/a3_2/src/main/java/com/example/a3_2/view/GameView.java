package com.example.a3_2.view;

import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Model.AppState;

import javafx.scene.layout.Pane;

public class GameView extends Pane implements PublishSubscribe {

  private FighterView leftFighterView;
  private FighterView rightFighterView;
  private HealthBar leftHealthBar;
  private HealthBar rightHealthBar;

  public GameView() {
     
  }


  public void update(AppState appState, Fighter leftFighter, Fighter rightFighter) {
    if (appState == AppState.inGame) {
      setVisible(true);
      getChildren().clear();
      FighterView leftFighterView = new FighterView(leftFighter);
      FighterView rightFighterView = new FighterView(rightFighter);
      getChildren().addAll(leftFighterView, rightFighterView);
    }
    else setVisible(false);
  }
}


