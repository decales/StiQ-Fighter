package com.example.a3_2.view;

import java.io.File;

import com.example.a3_2.model.Fighter.FighterSide;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WinMarker extends ImageView {

  private Image[] sprites;
  private FighterSide side;

  public WinMarker(FighterSide side) {

    this.side = side;

    File[] spriteFiles = new File(getClass().getResource("/wins/").getPath()).listFiles();
    sprites = new Image[spriteFiles.length];
    for (int i = 0; i < sprites.length; i++) sprites[i] = new Image( spriteFiles[i].toURI().toString());

    // flip sprite for right bar to maintain symmetry
    if (side == FighterSide.right) setScaleX(-1); 
    setPreserveRatio(true);
  }

  public void update(double viewSize, int wins) {
    setFitWidth(viewSize * 0.0425);
    setImage(sprites[wins]);

  }
}
