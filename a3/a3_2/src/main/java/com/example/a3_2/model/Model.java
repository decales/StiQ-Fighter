package com.example.a3_2.model;

import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.Control;

import com.example.a3_2.Controller.LeftPlayerKey;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FaceDirection;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Model {

  public enum AppState { inMenu, inGame }
  public enum GameMode { PvP, PvC, CvC }

  private List<PublishSubscribe> subscribers;
  private double viewWidth, viewHeight;
  private Timeline gameTimer;
  private GameMode gameMode;
  private Fighter leftFighter, rightFighter;
  private int leftWins, rightWins;

  public Model(double viewWidth, double viewHeight) {

    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
    
    startGame(GameMode.PvC);
  }


  public void startGame(GameMode gameMode) {
    // initialize fighter types based on selected game mode
    this.gameMode = gameMode;

    switch(gameMode) {
      case PvP -> { // player vs player
        leftFighter = new PlayerFighter(FaceDirection.right, viewWidth, viewHeight);
        rightFighter = new PlayerFighter(FaceDirection.left, viewWidth, viewHeight);
      }
      case PvC -> { // player vs computer
        leftFighter = new PlayerFighter(FaceDirection.right, viewWidth, viewHeight);
        rightFighter = new ComputerFighter(FaceDirection.left, viewWidth, viewHeight);
      }
      case CvC -> { // computer vs computer
        leftFighter = new ComputerFighter(FaceDirection.right, viewWidth, viewHeight);
        rightFighter = new ComputerFighter(FaceDirection.left, viewWidth, viewHeight);
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
    controlComputerFighters(); // only applies to PvC or CvC, but called regardless
    leftFighter.detectHit(rightFighter);
    rightFighter.detectHit(leftFighter);
    checkReset();
    updateSubscribers();
  }


  public void addSubscribers(PublishSubscribe... subscribers) {
    if (this.subscribers == null) this.subscribers = Arrays.asList(subscribers);
    else this.subscribers.addAll(Arrays.asList(subscribers));
    updateSubscribers();
  }


  private void updateSubscribers() {
    for (PublishSubscribe subscriber : subscribers) {
      subscriber.update(leftFighter, rightFighter);
    }
  }


  private void faceFighers() {
    // ensures fighters are always facing each other regardless of which side of the arena they are on
    double leftFighterCenterX = leftFighter.posX + leftFighter.width / 2;
    double rightFighterCenterX = rightFighter.posX + rightFighter.width / 2;

    if (leftFighterCenterX > rightFighterCenterX) {
      leftFighter.directionFacing = FaceDirection.left;
      rightFighter.directionFacing = FaceDirection.right;
    }
    else {
      leftFighter.directionFacing = FaceDirection.right;
      rightFighter.directionFacing = FaceDirection.left;
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
  

  public void controlPlayerFighter(Object key) {
    // determine whether left or right player should be controlled based on the key that was pressed
    Fighter fighter = (key instanceof LeftPlayerKey) ? leftFighter : rightFighter;
    
    if (fighter instanceof PlayerFighter player) {
      ActionState action = player.keyActionMap.get(key);
      fighter.executeAction(action);
    }
  }
}
