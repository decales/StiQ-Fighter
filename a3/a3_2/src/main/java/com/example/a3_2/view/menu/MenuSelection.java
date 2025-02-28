package com.example.a3_2.view.menu;

import java.util.HashMap;
import com.example.a3_2.model.Model.GameMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuSelection extends ImageView {

  private HashMap<GameMode, Image> spriteMap;

  public MenuSelection() {
    spriteMap = new HashMap<>();
    for (GameMode mode : GameMode.values()) {
      spriteMap.put(mode, new Image(getClass().getResource(String.format("/menu/%s/selection.png", mode.toString().toLowerCase())).toString()));
    } spriteMap.put(null, new Image(getClass().getResource("/menu/selection.png").toString()));

    setPreserveRatio(true);
  }


  public void update(double viewWidth, GameMode gameMode) {
    setFitWidth(viewWidth * 0.3667);
    setImage(spriteMap.get(gameMode));
  }
}
