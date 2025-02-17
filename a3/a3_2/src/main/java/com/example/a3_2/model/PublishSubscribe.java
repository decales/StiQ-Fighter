package com.example.a3_2.model;

import com.example.a3_2.model.Model.AppState;

public interface PublishSubscribe {

  void update(AppState appState, int frame, double viewSize, Fighter leftFighter, Fighter rightFighter, int leftWins, int rightWins);
}
