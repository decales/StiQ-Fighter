package com.example.a3_2.view.game;

import java.io.File;
import java.util.HashMap;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;
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

  public FighterView() {

    spriteMap = new HashMap<ActionState, Image[]>();
    frameRepetition = 3; // number of times each frame repeats to control animation speed
    fighterRatio = 46.0 / 168.0; // width of fighter based on where it is drawn in the sprite, used for hitbox scaling purposes
    blinkTimer = new Timeline(new KeyFrame(Duration.millis(75), e -> setVisible(!isVisible())));

    // Initialize a map of all sprite frames for each action
    for (ActionState action : ActionState.values()) {
      File[] spriteFiles = new File(getClass().getResource(String.format("/game/fighter/%s", action)).getPath()).listFiles();
      Image[] sprites = new Image[spriteFiles.length];
      for (int i = 0; i < sprites.length; i++) sprites[i] = new Image(spriteFiles[i].toURI().toString());
      spriteMap.put(action, sprites);
    }
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
    setX(fighter.posX);
    setY(fighter.posY);
    setScaleX((fighter.side == FighterSide.left) ? 1 : -1);
    setTranslateX((fighter.side == FighterSide.left) ? 0 : -getFitWidth() + (getFitWidth() * fighterRatio));

    // sprite blinks in and out after fighter is damaged
    if (fighter.isInvulnerable) blinkTimer.play();
    else { blinkTimer.stop(); setVisible(true); }
  }
}

