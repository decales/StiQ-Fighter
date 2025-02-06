package com.example.a3_2.view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class ModeSelectionView extends StackPane {

  public ModeSelectionView() {

    HBox buttonBox = new HBox();

    Button pvpButton = new Button("PvP");
    Button pvcButton = new Button("PvC");
    Button cvcButton = new Button("CvC");

    buttonBox.getChildren().addAll(pvpButton, pvcButton, cvcButton);
    getChildren().addAll(buttonBox);



  }
  
}
