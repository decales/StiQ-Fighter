package com.example.a3_2.view;

import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;

import javafx.scene.layout.Pane;

public class Arena extends Pane implements PublishSubscribe {

  public Arena() {
     
  }


  public void update(Fighter leftFighter, Fighter rightFighter) {
    getChildren().clear();

    FighterView leftFighterView = new FighterView(leftFighter);
    FighterView rightFighterView = new FighterView(rightFighter);
    getChildren().addAll(leftFighterView, rightFighterView);
  }
}


