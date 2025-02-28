package com.example.a3_2.view.game;

import com.example.a3_2.controller.Controller;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QuitButton extends ImageView {

  private boolean buttonSelected;
  private double angle;

  public QuitButton(Controller controller) {

    setImage(new Image (getClass().getResource("/game/quit.png").toString()));
    setPreserveRatio(true);
    setPickOnBounds(true);
    setOnMouseEntered(e -> buttonSelected = true);
    setOnMouseExited(e -> buttonSelected = false);
    setOnMouseClicked(controller::handleMouseClicked);
  }


  public void update(double viewSize) {
    setFitWidth(viewSize * 0.06);
    rotateButton(0.1, 5);
  }


  private void rotateButton(double speed, double max) {
    if (buttonSelected) angle += speed;
    else angle = 0;
    setRotate(max * Math.sin(angle));
  }
}
