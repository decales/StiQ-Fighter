package com.example.a3_2.view;

import com.example.a3_2.model.Fighter;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class FighterView extends Pane {


  public FighterView(Fighter fighter) {

    Rectangle bodyHitBox = new Rectangle(fighter.posX, fighter.posY, fighter.width, fighter.height);
    bodyHitBox.setFill(Color.RED);

    Circle weaponHitBox = new Circle(fighter.weaponX, fighter.weaponY, fighter.weaponRadius);
    weaponHitBox.setFill(Color.BLUE);

    getChildren().addAll(bodyHitBox, weaponHitBox);
  }
}

