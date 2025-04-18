package com.example.StiqFighter.view.menu;

import com.example.StiqFighter.controller.Controller;
import com.example.StiqFighter.model.Model.GameMode;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MenuButton extends VBox {

  public GameMode gameMode;
  private ImageView buttonSprite;
  private ImageView labelSprite;
  private boolean buttonSelected;
  private double angle;
  
  public MenuButton(GameMode gameMode, Controller controller) {

    this.gameMode = gameMode;

    // button sprite
    buttonSprite = new ImageView(new Image(getClass().getResource(String.format( "/menu/%s/button.png", gameMode.toString().toLowerCase())).toString()));
    buttonSprite.setOnMouseEntered(e -> buttonSelected = true);
    buttonSprite.setOnMouseExited(e -> buttonSelected = false);
    buttonSprite.setPreserveRatio(true);
    buttonSprite.setPickOnBounds(true);

    // label sprite
    labelSprite = new ImageView(new Image(getClass().getResource(String.format( "/menu/%s/label.png", gameMode.toString().toLowerCase())).toString()));
    labelSprite.setPreserveRatio(true);

    setAlignment(Pos.BASELINE_CENTER);
    getChildren().addAll(buttonSprite, labelSprite);

    // event handlers
    setOnMouseMoved( e -> { if (buttonSelected) controller.handleMouseEntered(e); else controller.handleMouseExited(e); });
    setOnMouseClicked(e -> { if (buttonSelected) controller.handleMouseClicked(e); }); 
    setOnMouseExited(controller::handleMouseExited);
  }


  public void update(double viewSize) {
    buttonSprite.setFitWidth(viewSize * 0.2);
    labelSprite.setFitWidth(viewSize * 0.1);
    rotateButton(0.05, 5);
  }


  private void rotateButton(double speed, double max) {
    if (buttonSelected) angle += speed;
    else angle = 0;
    buttonSprite.setRotate(max * Math.sin(angle));
  }
}
