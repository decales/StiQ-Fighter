package com.example.a3_2.model;

import com.example.a3_2.model.Fighter.ControlType;
import com.example.a3_2.model.Fighter.FaceDirection;

public class GameData {

  public enum GameState { inMenu, inGame }

  public GameState state;
  public Fighter fighter1, fighter2;
  
  public GameData(double displayWidth, double displayHeight) {

    fighter1 = new Fighter(FaceDirection.left, ControlType.human, displayWidth, displayHeight);
    fighter2 = new Fighter(FaceDirection.right, ControlType.computer, displayWidth, displayHeight);
  }
}
