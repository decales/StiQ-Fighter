package com.example.a3_2.model;

import java.util.Arrays;
import java.util.List;
import com.example.a3_2.Controller.LeftPlayerKey;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FaceDirection;
import com.example.a3_2.model.GameData.GameMode;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Model {

  private List<PublishSubscribe> subscribers;
  private double viewWidth, viewHeight;
  private GameData gameData;
  private Timeline gameTimer;
  private GameMode selectedMode;

  public Model(double viewWidth, double viewHeight) {
    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
    
    selectedMode = GameMode.PvC;
    initialize();
  }

  public void initialize() {
    // initialize game data in mode selected by user
    gameData = new GameData(selectedMode, viewWidth, viewHeight);

    // main game update loop
    gameTimer = new Timeline(new KeyFrame(Duration.millis(1000), e -> handleGameUpdates()));
    gameTimer.setCycleCount(Animation.INDEFINITE);
    gameTimer.setRate(60);
    gameTimer.play();
  }

  private void handleGameUpdates() {
    
    faceFighers();

    // detect if players have hit each other
    gameData.fighterOne.detectHit(gameData.fighterTwo);
    gameData.fighterTwo.detectHit(gameData.fighterOne);

    updateSubscribers();
  }


  public void addSubscribers(PublishSubscribe... subscribers) {
    if (this.subscribers == null) this.subscribers = Arrays.asList(subscribers);
    else this.subscribers.addAll(Arrays.asList(subscribers));
    updateSubscribers();
  }


  private void updateSubscribers() {
    for (PublishSubscribe subscriber : subscribers) {
      subscriber.update(gameData);
    }
  }


  private void faceFighers() {
    // ensures fighters are always facing each other regardless of which side of the arena they are on
    double fighterOneCenterX = gameData.fighterOne.posX + gameData.fighterOne.width / 2;
    double fighterTwoCenterX = gameData.fighterTwo.posX + gameData.fighterTwo.width / 2;

    if (fighterOneCenterX > fighterTwoCenterX) {
      gameData.fighterOne.directionFacing = FaceDirection.left;
      gameData.fighterTwo.directionFacing = FaceDirection.right;
    }
    else {
      gameData.fighterOne.directionFacing = FaceDirection.right;
      gameData.fighterTwo.directionFacing = FaceDirection.left;
    }
  }
  

  public void controlFighter(Object key) {
    Fighter fighter = (key instanceof LeftPlayerKey) ? gameData.fighterOne : gameData.fighterTwo;
    
    ActionState action;
    if (fighter instanceof PlayerFighter player) action = player.keyActionMap.get(key);
    else if (fighter instanceof ComputerFighter computer) action = null;
    else return;

    switch(action) {
      case movingLeft -> { if (fighter.posX >= 0) fighter.executeAction(action); }
      case movingRight -> { if (fighter.posX + fighter.width <= viewWidth) fighter.executeAction(action); }
      default -> fighter.executeAction(action);
    }
  }
}
