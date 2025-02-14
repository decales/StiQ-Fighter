package com.example.a3_2.model;

import java.util.Arrays;
import java.util.List;

import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Model {

  public enum AppState { selectingMode, inGame }
  public enum GameMode { PvP, PvC, CvC }

  private List<PublishSubscribe> subscribers;
  private double viewWidth, viewHeight;
  private AppState appState;

  private Timeline gameTimer;
  private GameMode gameMode;
  private Fighter leftFighter, rightFighter;
  private int leftWins, rightWins;

  public Model(double viewWidth, double viewHeight) {

    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
    appState = AppState.selectingMode;
  }


  public void startGame(GameMode gameMode) {
    // start game and initialize fighter types based on selected game mode
    appState = AppState.inGame;
    this.gameMode = gameMode;

    switch(gameMode) {
      case PvP -> { // player vs player
        leftFighter = new PlayerFighter(FighterSide.left, viewWidth, viewHeight);
        rightFighter = new PlayerFighter(FighterSide.right, viewWidth, viewHeight);
      }
      case PvC -> { // player vs computer
        leftFighter = new PlayerFighter(FighterSide.left, viewWidth, viewHeight);
        rightFighter = new ComputerFighter(FighterSide.right, viewWidth, viewHeight);
      }
      case CvC -> { // computer vs computer
        leftFighter = new ComputerFighter(FighterSide.left, viewWidth, viewHeight);
        rightFighter = new ComputerFighter(FighterSide.right, viewWidth, viewHeight);
      }
    }

    // start game update timer
    gameTimer = new Timeline(new KeyFrame(Duration.millis(1000), e -> handleGameUpdates()));
    gameTimer.setCycleCount(Animation.INDEFINITE);
    gameTimer.setRate(60);
    gameTimer.play();
  }


  private void handleGameUpdates() {
    faceFighers();
    controlComputerFighters(); // only applies in PvC or CvC, but called regardless
    leftFighter.detectHit(rightFighter);
    rightFighter.detectHit(leftFighter);
    checkReset();
    System.out.println(String.format("%s", leftFighter.action));
    updateSubscribers();
  }


  public void addSubscribers(PublishSubscribe... subscribers) {
    if (this.subscribers == null) this.subscribers = Arrays.asList(subscribers);
    else this.subscribers.addAll(Arrays.asList(subscribers));
    updateSubscribers();
  }


  private void updateSubscribers() {
    for (PublishSubscribe subscriber : subscribers) {
      subscriber.update(appState, leftFighter, rightFighter);
    }
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
    // reset the fight when a player wins
    if (leftFighter.healthPoints <= 0 || rightFighter.healthPoints <=0) {

      if (leftFighter.healthPoints <= 0) leftWins ++;
      else rightWins ++;

      leftFighter.reset();
      rightFighter.reset();
    }
  }


  private void controlComputerFighters() {
    if (gameMode == GameMode.PvC || gameMode == GameMode.CvC) {
      ((ComputerFighter) rightFighter).determineAction(leftFighter);
      if (gameMode == GameMode.CvC) ((ComputerFighter) leftFighter).determineAction(rightFighter);
    }
  }
  

  public void controlPlayerFighter(Object key, FighterSide side) {
    PlayerFighter fighter;

    if (gameMode == GameMode.PvC && side == FighterSide.left) {
      fighter = (PlayerFighter) leftFighter;
      fighter.executeAction(fighter.keyActionMap.get(key));
    }
    else if (gameMode == GameMode.PvP) {
      fighter = (side == FighterSide.left) ? (PlayerFighter) leftFighter : (PlayerFighter) rightFighter;
      fighter.executeAction(fighter.keyActionMap.get(key));
    }
  }
}
