package com.example.a3_2.view;

import com.example.a3_2.model.GameData;
import com.example.a3_2.model.PublishSubscribe;

import javafx.scene.layout.Pane;

public class Arena extends Pane implements PublishSubscribe {

  public Arena() {
     
  }



  public void update(GameData gameData) {

    getChildren().clear();

    FighterView fighterView1 = new FighterView(gameData.fighter1);
    FighterView fighterView2 = new FighterView(gameData.fighter2);
    getChildren().addAll(fighterView1, fighterView2);
  }
}


