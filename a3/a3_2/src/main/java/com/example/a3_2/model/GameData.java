package com.example.a3_2.model;

import com.example.a3_2.model.Fighter.FaceDirection;

public class GameData {

  public enum GameState { inMenu, inGame }
  public enum GameMode { PvP, PvC, CvC }

  public GameState state;
  public GameMode mode;
  public Fighter fighterOne, fighterTwo;
  
  public GameData(GameMode mode, double displayWidth, double displayHeight) {

    this.mode = mode;

    switch(mode) {
      case PvP -> { // player vs player
        fighterOne = new PlayerFighter(FaceDirection.right, displayWidth, displayHeight);
        fighterTwo = new PlayerFighter(FaceDirection.left, displayWidth, displayHeight);
      }
      case PvC -> { // player vs computer
        fighterOne = new PlayerFighter(FaceDirection.right, displayWidth, displayHeight);
        fighterTwo = new ComputerFighter(FaceDirection.left, displayWidth, displayHeight);
      }
      case CvC -> { // computer vs computer
        fighterOne = new ComputerFighter(FaceDirection.right, displayWidth, displayHeight);
        fighterTwo = new ComputerFighter(FaceDirection.left, displayWidth, displayHeight);
      }
    }
  }
}
