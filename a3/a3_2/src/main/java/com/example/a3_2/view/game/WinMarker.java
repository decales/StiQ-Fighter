package com.example.a3_2.view.game;

import com.example.a3_2.model.Fighter.FighterSide;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WinMarker extends ImageView {

  private Image[] sprites;

  public WinMarker(FighterSide side) {
    sprites = new Image[21];
    for (int i = 0; i < sprites.length; i++) {
      sprites[i] = new Image(getClass().getResource(String.format("/game/wins/wins_%04d.png", i + 1)).toString());
    }

    // flip sprite for right bar to maintain symmetry
    if (side == FighterSide.right) setScaleX(-1); 
    setPreserveRatio(true);
  }


  public void update(double viewSize, int wins) {
    setFitWidth(viewSize * 0.08125);

    if (wins >= 11) setScaleX(1);
    if (wins >= sprites.length) setImage(sprites[sprites.length - 1]);
    else setImage(sprites[wins]);
  }
}
