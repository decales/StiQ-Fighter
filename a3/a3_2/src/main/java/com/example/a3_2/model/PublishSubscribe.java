package com.example.a3_2.model;

import com.example.a3_2.model.Model.AppState;

public interface PublishSubscribe {

  void update(AppState appState,  Fighter leftFighter, Fighter rightFighter);
}
