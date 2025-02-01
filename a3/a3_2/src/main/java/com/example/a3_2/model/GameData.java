package com.example.a3_2.model;

import com.example.a3_2.model.Fighter.ControlType;
import com.example.a3_2.model.Fighter.FaceDirection;

public class GameData {

  public enum GameState { inMenu, inGame }

  public GameState state;
  public Fighter fighterOne, fighterTwo;
  
  public GameData(double displayWidth, double displayHeight) {

    fighterOne = new Fighter(FaceDirection.right, ControlType.human, displayWidth, displayHeight);
    fighterTwo = new Fighter(FaceDirection.left, ControlType.human, displayWidth, displayHeight);
  }
}
