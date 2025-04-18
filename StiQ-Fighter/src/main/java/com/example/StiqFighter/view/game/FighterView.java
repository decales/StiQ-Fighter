package com.example.StiqFighter.view.game;

import java.io.File;
import java.util.HashMap;
import com.example.StiqFighter.model.Fighter;
import com.example.StiqFighter.model.Fighter.ActionState;
import com.example.StiqFighter.model.Fighter.FighterSide;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class FighterView extends ImageView {

  private HashMap<ActionState, Image[]> spriteMap;
  private int frameRepetition;
  private double fighterRatio;
  private Timeline blinkTimer;

  public FighterView(HashMap<ActionState, Image[]> spriteMap) {
    this.spriteMap = spriteMap;
    frameRepetition = 3; // number of times each frame repeats to control animation speed
    fighterRatio = 46.0 / 168.0; // width of fighter based on where it is drawn in the sprite, used for hitbox scaling purposes
    blinkTimer = new Timeline(new KeyFrame(Duration.millis(75), e -> setVisible(!isVisible())));
  }


  public void update(Fighter fighter, int frame) {
    // on each frame, update the sprite with the frame of the fighter's current action
    ActionState actionState;
    if (fighter.side == FighterSide.right) {
      if (fighter.actionState == ActionState.movingRight) actionState = ActionState.movingLeft;
      else if (fighter.actionState == ActionState.movingLeft) actionState = ActionState.movingRight;
      else actionState = fighter.actionState;
    } else actionState = fighter.actionState;

    setImage(spriteMap.get(actionState)[(fighter.actionFrame / frameRepetition) % (spriteMap.get(actionState).length)]);

    // scale and translate frame depending on which relative side the fighter is on
    setFitHeight(fighter.height);
    setFitWidth(getFitHeight() * getImage().getWidth() / getImage().getHeight());
    
    if (!fighter.isAnimationLocked()) {
      setX(fighter.posX);
      setY(fighter.posY);
    }
    
    setScaleX((fighter.side == FighterSide.left) ? 1 : -1);
    setTranslateX((fighter.side == FighterSide.left) ? 0 : -getFitWidth() + (getFitWidth() * fighterRatio));

    // sprite blinks in and out after fighter is damaged
    if (fighter.isInvincible) blinkTimer.play();
    else { blinkTimer.stop(); setVisible(true); }
  }
}

