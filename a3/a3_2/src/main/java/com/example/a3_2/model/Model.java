package com.example.a3_2.model;

import java.util.Arrays;
import java.util.List;

import com.example.a3_2.model.Fighter.FighterSide;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Model {

  public enum AppState { selectingMode, inGame }
  public enum GameMode { PvP, PvC, CvC }

  private List<PublishSubscribe> subscribers;
  private double viewSize;
  private AppState appState;

  private Timeline animationTimer;
  private int frameRate;
  private int frame;

  private GameMode gameMode;
  private Fighter leftFighter, rightFighter;
  private int leftWins, rightWins;

  public Model(double viewSize) {

    frameRate = 60;
    animationTimer = new Timeline(new KeyFrame(Duration.millis(1000 / frameRate), e -> handleUpdates()));
    animationTimer.setCycleCount(Animation.INDEFINITE);
    animationTimer.play();

    this.viewSize = viewSize;
    appState = AppState.selectingMode;
  }


  public void startGame(GameMode gameMode) {
    // start game and initialize fighter types based on selected game mode
    // used as onClick method for select mode screen buttons
    appState = AppState.inGame;
    this.gameMode = gameMode;

    switch(gameMode) {
      case PvP -> { // player vs player
        leftFighter = new PlayerFighter(FighterSide.left, viewSize, frameRate);
        rightFighter = new PlayerFighter(FighterSide.right, viewSize, frameRate);
      }
      case PvC -> { // player vs computer
        leftFighter = new PlayerFighter(FighterSide.left, viewSize, frameRate);
        rightFighter = new ComputerFighter(FighterSide.right, viewSize, frameRate);
      }
      case CvC -> { // computer vs computer
        leftFighter = new ComputerFighter(FighterSide.left, viewSize, frameRate);
        rightFighter = new ComputerFighter(FighterSide.right, viewSize, frameRate);
      }
    }
  }


  private void handleUpdates() {
    frame ++;

    if (appState == AppState.inGame) {
      faceFighers();
      controlComputerFighters(); // only applies in PvC or CvC, but called regardless
      leftFighter.detectHit(rightFighter, frame);
      rightFighter.detectHit(leftFighter, frame);
      leftFighter.syncAction(leftFighter.actionState, frame);
      rightFighter.syncAction(rightFighter.actionState, frame);
      checkReset();
    }
    updateSubscribers();
  }


  private void faceFighers() {
    // ensures fighters are always facing each other regardless of which side of the arena they are on
    double leftFighterCenterX = leftFighter.posX + leftFighter.width / 2;
    double rightFighterCenterX = rightFighter.posX + rightFighter.width / 2;

    if (leftFighterCenterX < rightFighterCenterX) {
      leftFighter.side = FighterSide.left;
      rightFighter.side = FighterSide.right;
    }
    else {
      leftFighter.side = FighterSide.right;
      rightFighter.side = FighterSide.left;
    }
  }


  private void checkReset() {
    // round reset when fighter wins a round
    if (leftFighter.healthPoints <= 0 || rightFighter.healthPoints <= 0) {
      if (leftFighter.healthPoints <= 0) rightWins ++;
      else leftWins ++;

      leftFighter.initialize(frame);
      rightFighter.initialize(frame);

      // full reset when player wins match
      if (leftWins == 5 || rightWins == 5) {
        if (leftWins == 5) {}
        else {}
        appState = AppState.selectingMode;
      }
    }
  }


  private void controlComputerFighters() {
    // computer actions determined via Q-learning implemented directly in ComputerFighter
    if (gameMode == GameMode.PvC || gameMode == GameMode.CvC) {
      ((ComputerFighter) rightFighter).determineAction(leftFighter, frame);
      if (gameMode == GameMode.CvC) ((ComputerFighter) leftFighter).determineAction(rightFighter, frame);
    }
  }
  

  public void controlPlayerFighter(Object key, FighterSide side) {
    // player fighter actions determined via inputs and handled by controller.controller
    // move left - left player: A   right player: K
    // move right - left player: S   right player: L
    // attack - left player: Q   right player: I
    // block - left player: W   right player: O

    PlayerFighter fighter;

    if (gameMode == GameMode.PvC && side == FighterSide.left) {
      fighter = (PlayerFighter) leftFighter;
      fighter.executeAction(fighter.keyActionMap.get(key), frame);
    }
    else if (gameMode == GameMode.PvP) {
      fighter = (side == FighterSide.left) ? (PlayerFighter) leftFighter : (PlayerFighter) rightFighter;
      fighter.executeAction(fighter.keyActionMap.get(key), frame);
    }

  }
  
  public void addSubscribers(PublishSubscribe... subscribers) {
    if (this.subscribers == null) this.subscribers = Arrays.asList(subscribers);
    else this.subscribers.addAll(Arrays.asList(subscribers));
    updateSubscribers();
  }


  private void updateSubscribers() {
    for (PublishSubscribe subscriber : subscribers) {
      subscriber.update(appState, frame, viewSize, leftFighter, rightFighter, leftWins, rightWins);
    }
  }
}
