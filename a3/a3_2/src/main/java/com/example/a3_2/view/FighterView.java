package com.example.a3_2.view;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class FighterView extends Pane {

  private HashMap<ActionState, Image[]> spriteMap;
  private ImageView sprite;
  private Timeline blinkTimer;
  private int frameRepetitionCount;
  private Rectangle hbox;
  private Circle whbox;

  public FighterView() {

    spriteMap = new HashMap<ActionState, Image[]>();
    sprite = new ImageView();
    blinkTimer = new Timeline(new KeyFrame(Duration.millis(75), e -> sprite.setVisible(!sprite.isVisible())));
    frameRepetitionCount = 3;

    // Initialize a map of all sprite frames for each action
    for (ActionState action: List.of(
          ActionState.idle, 
          ActionState.movingLeft, ActionState.movingRight, 
          ActionState.attacking, 
          ActionState.preBlocking, ActionState.blocking, ActionState.postBlocking)) {
    // for (ActionState action : ActionState.values()) {
      int actionSpriteCount = new File(getClass().getResource( String.format("/fighter/%s", action)).getPath()).listFiles().length; // # sprite files
      Image[] actionSprites = new Image[actionSpriteCount * frameRepetitionCount]; // array size is total number of frames in the animation 

      int spriteNumber = 0;
      for (int i = 0; i < actionSprites.length; i++) {
        if (i % frameRepetitionCount == 0) spriteNumber ++;
        actionSprites[i] = new Image(getClass().getResource(String.format("/fighter/%s/%s_%04d.png", action, action, spriteNumber)).toString()); 
      }
      spriteMap.put(action, actionSprites);
    }

    hbox = new Rectangle();
    whbox = new Circle();

    getChildren().addAll(hbox, whbox, sprite);
  }

  public void updateSprite(Fighter fighter) {

    hbox.setX(fighter.posX);
    hbox.setY(fighter.posY);
    hbox.setWidth(fighter.width);
    hbox.setHeight(fighter.height);
    hbox.setFill(Color.RED);

    whbox.setCenterX(fighter.attackX);
    whbox.setCenterY(fighter.attackY);
    whbox.setRadius(fighter.attackRadius);
    whbox.setFill(Color.RED);

    ActionState action;
    if (fighter.side == FighterSide.right) {
      if (fighter.action == ActionState.movingRight) action = ActionState.movingLeft;
      else if (fighter.action == ActionState.movingLeft) action = ActionState.movingRight;
      else action = fighter.action;
    }
    else action = fighter.action;

    int actionFrame = fighter.actionFrame % spriteMap.get(action).length;
    sprite.setImage(spriteMap.get(action)[actionFrame]);
    sprite.setScaleX((fighter.side == FighterSide.left) ? 1 : -1);
    sprite.setTranslateX((fighter.side == FighterSide.left) ? 0 : -sprite.getImage().getWidth());

    sprite.setX(fighter.posX);
    sprite.setY(fighter.posY);

    // sprite blinks in and out after fighter is damaged
    if (fighter.isInvulnerable) blinkTimer.play();
    else { blinkTimer.stop(); sprite.setVisible(true); }


    sprite.setFitHeight(fighter.height);
    sprite.preserveRatioProperty().set(true);
  }
}

