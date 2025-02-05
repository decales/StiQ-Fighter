package com.example.a3_2.view;

import com.example.a3_2.model.Fighter;
import com.example.a3_2.model.PublishSubscribe;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;



public class HealthBar extends HBox implements PublishSubscribe {
  



  @Override
public void update(Fighter leftFighter, Fighter rightFighter) {

  getChildren().clear();

  Label a = new Label(String.format("%d", leftFighter.healthPoints));
  Label b = new Label(String.format("                    %d", rightFighter.healthPoints));


  getChildren().addAll(a, b);
    
  
}


  
}
