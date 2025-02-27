package com.example.a3_2.view.game;

import com.example.a3_2.model.Fighter.FighterSide;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class FighterBar extends HBox {

  private HealthBar healthBar;
  private WinMarker winMarker;

  public FighterBar(FighterSide side) {

    healthBar = new HealthBar(side);
    winMarker = new WinMarker(side);

    if (side == FighterSide.left) getChildren().addAll(healthBar, winMarker);
    else getChildren().addAll(winMarker, healthBar);

    setAlignment((side == FighterSide.left) ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
  } 


  public void update(double viewSize, int frame, int healthPoints, int wins) {
    setPadding(new Insets(viewSize * 0.01));
    setSpacing(viewSize * 0.02);
    healthBar.update(viewSize, healthPoints, frame);
    winMarker.update(viewSize, wins);
  }
}
