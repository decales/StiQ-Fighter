package com.example.a3_2.view;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Model.AppState;
import com.example.a3_2.model.Model.GameMode;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class MenuView extends StackPane implements PublishSubscribe  {

  private MenuButton pvpButton;
  private MenuButton pvcButton;
  private MenuButton cvcButton;

  public MenuView(Controller controller) {

    HBox buttonBox = new HBox();

    pvpButton = new MenuButton(GameMode.PvP, controller);
    pvcButton = new MenuButton(GameMode.PvC, controller);
    cvcButton = new MenuButton(GameMode.CvC, controller);

    buttonBox.getChildren().addAll(pvpButton, pvcButton, cvcButton);

    getChildren().addAll(buttonBox);
  }

  public void update(AppState appState, int frame, double viewSize, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins) {
    setVisible(appState == AppState.selectingMode);

    pvpButton.update(viewSize);
    pvcButton.update(viewSize);
    cvcButton.update(viewSize);
  }
}
