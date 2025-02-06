package com.example.a3_2.view;

import com.example.a3_2.model.Fighter;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class FighterView extends Pane {


  public FighterView(Fighter fighter) {

    Rectangle bodyHitBox = new Rectangle(fighter.posX, fighter.posY, fighter.width, fighter.height);
    Circle weaponHitBox = new Circle(fighter.attackX, fighter.attackY, fighter.attackRadius);

    Color attackColour;
    Color blockColour;

    switch(fighter.action) {
      case highAttacking ->  {
        attackColour = Color.RED;
        blockColour = Color.GREY;
      }
      case lowAttacking ->  {
        attackColour = Color.LIME;
        blockColour = Color.GREY;
      }
      case highBlocking ->  {
        attackColour = Color.GREY;
        blockColour = Color.RED;
      }
      case lowBlocking ->  {
        attackColour = Color.GREY;
        blockColour = Color.LIME;
      }
      default -> {
        attackColour = Color.GREY;
        blockColour = Color.GREY;
      }
    }
    if (fighter.isInvulnerable) blockColour = Color.DARKGREY;
    if (fighter.isParried) blockColour = Color.FUCHSIA;

    weaponHitBox.setFill(attackColour);
    bodyHitBox.setFill(blockColour);

    getChildren().addAll(bodyHitBox, weaponHitBox);
  }
}

