package com.example.StiqFighter.model;

import com.example.StiqFighter.model.Model.AppState;
import com.example.StiqFighter.model.Model.GameMode;

public interface PublishSubscribe {

  void update(
      AppState appState, int frame, double viewSize,
      GameMode gameMode, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins);
}
