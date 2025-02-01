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

    Circle weaponHitBox = new Circle(fighter.attackX, fighter.attackY, fighter.attackRadius);

    Color attackColour;
    switch(fighter.action) {
      case highAttacking -> attackColour = Color.RED;
      case midAttacking -> attackColour = Color.ORANGE;
      case lowAttacking -> attackColour = Color.LIME;
      default -> attackColour = Color.GREY;
    }
    weaponHitBox.setFill(attackColour);

    getChildren().addAll(bodyHitBox, weaponHitBox);
  }
}

