package com.example.a3_2.view;

import java.io.File;
import java.util.HashMap;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class FighterView extends Pane { // TODO should extend ImageView when hitbox debugging no longer needed

  private HashMap<ActionState, Image[]> spriteMap;
  private ImageView sprite;
  private int frameRepetition;
  private double fighterRatio;
  private Timeline blinkTimer;
  private Rectangle hbox;
  private Circle whbox;

  public FighterView() {

    spriteMap = new HashMap<ActionState, Image[]>();
    sprite = new ImageView();
    frameRepetition = 3; // number of times each frame repeats to control animation speed
    fighterRatio = 46.0 / 168.0; // width of fighter based on where it is drawn in the sprite, used for hitbox scaling purposes
    blinkTimer = new Timeline(new KeyFrame(Duration.millis(75), e -> sprite.setVisible(!sprite.isVisible())));

    // Initialize a map of all sprite frames for each action
    for (ActionState action : ActionState.values()) {
      if (action == ActionState.parried) continue;
      File[] spriteFiles = new File(getClass().getResource( String.format("/fighter/%s", action)).getPath()).listFiles();
      Image[] sprites = new Image[spriteFiles.length];
      for (int i = 0; i < sprites.length; i++) sprites[i] = new Image(spriteFiles[i].toURI().toString());
      spriteMap.put(action, sprites);
    }

    hbox = new Rectangle();
    whbox = new Circle();

    getChildren().addAll(hbox, whbox, sprite);
  }

  public void update(Fighter fighter) {

    // hbox.setX(fighter.posX);
    // hbox.setY(fighter.posY);
    // hbox.setWidth(fighter.width);
    // hbox.setHeight(fighter.height);
    // hbox.setFill(Color.RED);
    //
    // whbox.setCenterX(fighter.attackX);
    // whbox.setCenterY(fighter.attackY);
    // whbox.setRadius(fighter.attackRadius);
    // whbox.setFill(Color.RED);

    // on each frame, update the sprite with the frame of the fighter's current action
    ActionState actionState;
    if (fighter.side == FighterSide.right) {
      if (fighter.actionState == ActionState.movingRight) actionState = ActionState.movingLeft;
      else if (fighter.actionState == ActionState.movingLeft) actionState = ActionState.movingRight;
      else actionState = fighter.actionState;
    } else actionState = fighter.actionState;


    // if (actionState == ActionState.preBlocking || actionState == ActionState.blocking || actionState == ActionState.postBlocking) {
    //   int f = (fighter.actionFrame / frameRepetition) % spriteMap.get(actionState).length;
    //   System.out.println(String.format("%s: %d", actionState, f));
    // }

    sprite.setImage(spriteMap.get(actionState)[(fighter.actionFrame / frameRepetition) % spriteMap.get(actionState).length]);

    // scale and translate frame depending on which relative side the fighter is on
    sprite.setFitHeight(fighter.height);
    sprite.setFitWidth(sprite.getFitHeight() * sprite.getImage().getWidth() / sprite.getImage().getHeight());
    sprite.setX(fighter.posX);
    sprite.setY(fighter.posY);
    sprite.setScaleX((fighter.side == FighterSide.left) ? 1 : -1);
    sprite.setTranslateX((fighter.side == FighterSide.left) ? 0 : -sprite.getFitWidth() + (sprite.getFitWidth() * fighterRatio));

    // sprite blinks in and out after fighter is damaged
    if (fighter.isInvulnerable) blinkTimer.play();
    else { blinkTimer.stop(); sprite.setVisible(true); }
  }
}

