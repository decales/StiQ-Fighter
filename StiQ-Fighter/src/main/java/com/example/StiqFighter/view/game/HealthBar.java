package com.example.StiqFighter.view.game;

import com.example.StiqFighter.model.Fighter.FighterSide;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class HealthBar extends HBox {

  private FighterSide side;
  private int frameRepetition;
  private int animationInterval;
  private Image[] sprites;

  public HealthBar(FighterSide side) {
    this.side = side;
    frameRepetition = 5; 
    animationInterval = 120; // health sprites animate every x frames

    // initialize sprite array
    sprites = new Image[4];
    for (int i = 0; i < sprites.length; i++) {
      sprites[i] = new Image(getClass().getResource(String.format("/game/health/health_%04d.png", i + 1)).toString());
    }
  }  


  public void update(double viewSize, int healthPoints, int frame) {
    setSpacing(viewSize * 0.006);
    
    // initialize health sprites
    if (getChildren().isEmpty()) {
      for (int i = 0; i < healthPoints; i++) {
        ImageView sprite = new ImageView();
        sprite.setFitWidth(viewSize * 0.04);
        sprite.setPreserveRatio(true);
        getChildren().add(sprite);
      }
    }
    // animation speeds up as health decreases, just cause
    int hpLost = getChildren().size() - healthPoints;
    int animationInterval = (int) (this.animationInterval - (this.animationInterval * ((double) hpLost / (double) getChildren().size())));
    int animateFrame = (frame % animationInterval) / frameRepetition; // frames to animate on

    // update health sprites
    Object[] children = ((side == FighterSide.left) ? getChildren().reversed() : getChildren()).toArray();
    for (Object child : children) {
      if (child instanceof ImageView sprite) {

        if (hpLost > 0) { sprite.setImage(sprites[sprites.length - 1]); hpLost --; } // last sprite in array is HP lost sprite
        else if (animateFrame < sprites.length -1) sprite.setImage(sprites[animateFrame]);
        else sprite.setImage(sprites[0]);
      }
    }
  }
}
