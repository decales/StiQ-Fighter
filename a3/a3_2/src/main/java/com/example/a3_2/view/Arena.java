package com.example.a3_2.view;

import com.example.a3_2.model.GameData;
import com.example.a3_2.model.PublishSubscribe;

import javafx.scene.layout.Pane;

public class Arena extends Pane implements PublishSubscribe {

  public Arena() {
     
  }


  public void update(GameData gameData) {
    getChildren().clear();

    FighterView fighterOneView = new FighterView(gameData.fighterOne);
    FighterView fighterTwoView = new FighterView(gameData.fighterTwo);
    getChildren().addAll(fighterOneView, fighterTwoView);
  }
}


