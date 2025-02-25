package com.example.a3_2.model;

import com.example.a3_2.model.Model.AppState;
import com.example.a3_2.model.Model.GameMode;

public interface PublishSubscribe {

  void update(
      AppState appState, int frame, double viewSize,
      GameMode gameMode, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins);
}
