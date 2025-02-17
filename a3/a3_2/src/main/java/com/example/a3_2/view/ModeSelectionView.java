package com.example.a3_2.view;

import java.util.List;

import com.example.a3_2.controller.Controller;
import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;
import com.example.a3_2.model.Model.AppState;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class ModeSelectionView extends StackPane implements PublishSubscribe  {

  public ModeSelectionView(Controller controller) {

    HBox buttonBox = new HBox();

    Button pvpButton = new Button("PvP");
    pvpButton.setId("PvP");

    Button pvcButton = new Button("PvC");
    pvcButton.setId("PvC");
    
    Button cvcButton = new Button("CvC");
    cvcButton.setId("CvC");

    // add event on-click handlers to buttons
    for (Button button : List.of(pvpButton, pvcButton, cvcButton)) {
      button.setOnAction(controller::handleActionEvent);
      buttonBox.getChildren().add(button);
    }
    getChildren().addAll(buttonBox);
  }

  public void update(AppState appState, int frame, double viewSize, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins) {
    setVisible(appState == AppState.selectingMode);
  }
}
