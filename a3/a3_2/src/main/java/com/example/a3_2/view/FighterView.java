package com.example.a3_2.view;

import java.util.HashMap;

import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.Fighter.ActionState;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class FighterView extends Pane {


  private HashMap<ActionState, Image[]> spriteMap;


  public FighterView(Fighter fighter) {


    // for (ActionState action : ActionState.values()) {
    //   Image[] actionsSprites = new Image[9];
    //   for (int i = 0; i < actionsSprites.length; i++) {
    //     actionsSprites[i] = new Image()
    //   }
    //   // spriteMap.pu9t(action );
    // }
    //

    ImageView imageView =  new ImageView(new Image("idle.png"));
    imageView.setX(fighter.posX);
    imageView.setY(fighter.posY);
    // imageView.setFitWidth(fighter.width / 2);
    imageView.setFitHeight(fighter.height);
    imageView.preserveRatioProperty().set(true);
    
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

    getChildren().addAll(bodyHitBox, imageView, weaponHitBox);
  }
}

