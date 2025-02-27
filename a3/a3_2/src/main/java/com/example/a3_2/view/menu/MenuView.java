package com.example.a3_2.view.menu;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Model.AppState;
import com.example.a3_2.model.Model.GameMode;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MenuView extends StackPane implements PublishSubscribe  {

  private VBox vBox;
  private HBox buttonBox;
  private MenuSelection menuSelection;

  public MenuView(Controller controller) {

    vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);

    menuSelection = new MenuSelection();

    buttonBox = new HBox();
    buttonBox.setAlignment(Pos.BASELINE_CENTER);

    for (GameMode mode : GameMode.values()) {
      buttonBox.getChildren().add( new MenuButton(mode, controller) );
    }

    vBox.getChildren().addAll(menuSelection, buttonBox);
    getChildren().addAll(vBox);
  }


  public void update(
      AppState appState, int frame, double viewSize,
      GameMode gameMode, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins) {

    setVisible(appState == AppState.inMenu);

    vBox.setSpacing(viewSize * 0.05);
    menuSelection.update(viewSize, gameMode);

    buttonBox.setMinWidth(viewSize);
    buttonBox.setSpacing(viewSize * 0.0667);
    for (Object object : buttonBox.getChildren()) {
      if (object instanceof MenuButton button) button.update(viewSize);
    }
  }
}
