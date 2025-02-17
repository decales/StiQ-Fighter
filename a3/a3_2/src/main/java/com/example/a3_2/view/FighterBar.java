package com.example.a3_2.view;

import com.example.a3_2.model.Fighter.FighterSide;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class FighterBar extends HBox {

  private HealthBar healthBar;
  private WinMarker winMarker;

  public FighterBar(FighterSide side) {

    healthBar = new HealthBar(side);
    winMarker = new WinMarker(side);

    if (side == FighterSide.left) getChildren().addAll(healthBar, winMarker);
    else getChildren().addAll(winMarker, healthBar);

    setAlignment((side == FighterSide.left) ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
    setHgrow(this, Priority.ALWAYS);
  } 


  public void update(double viewSize, int frame, int healthPoints, int wins) {
    setSpacing(viewSize * 0.02);
    healthBar.update(viewSize, healthPoints, frame);
    winMarker.update(viewSize, wins);
  }
}
