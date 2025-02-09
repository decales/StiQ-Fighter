package com.example.a3_2.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class Fighter {

  public enum ActionState { movingLeft, movingRight, lowAttacking, highAttacking, lowBlocking, highBlocking, idle };
  public enum FighterSide { left, right };
  
  private Timeline actionTimer;
  public ActionState action;
  public FighterSide side;

  public int initHealthPoints;
  public int healthPoints;
  public int attackDamage;
  public int attackDuration;
  public int attackResetDuration;
  public boolean isInvulnerable;
  public int invulnerabilityDuration;
  public boolean isParried;
  public int parriedDuration;
  public boolean canParry;
  public double parryWindowDuration;

  public double width, height;
  public double minX, maxX;
  public double initX, initY;
  public double posX, posY;
  public double deltaX;

  public double attackRadius;
  public double attackX, attackY;
  public double attackReach;
  
  public Fighter(FighterSide side, double viewWidth, double viewHeight) {

    // State-based attributes
    actionTimer = new Timeline();
    action = ActionState.idle;
    this.side = side;

    // Stat-based attributes
    initHealthPoints = 100;
    healthPoints = initHealthPoints;
    attackDamage = 10;
    attackDuration = 100;
    attackResetDuration = 400;
    invulnerabilityDuration = 1000;
    parriedDuration = 3000;
    parryWindowDuration = 250;

    // Position-based attributes
    width = viewWidth * 0.075;
    height = viewHeight * 0.333;
    minX = 0;
    maxX = viewWidth;
    initX = (side == FighterSide.left) ? viewWidth * 0.2 - width : viewWidth * 0.8;
    initY = viewHeight * 0.667 - height;
    deltaX = (viewWidth * 0.00333);
    attackReach = width * 2.5;
    attackRadius = width * 0.1;
    
    updatePosition(initX, initY);
  }


  public void reset() {
    healthPoints = initHealthPoints;
    isInvulnerable = false;
    isParried = false;
    posX = initX;
    posY = initY;
  }


  protected boolean isAttacking() {
    return action == ActionState.lowAttacking || action == ActionState.highAttacking;
  }


  private boolean isBlocking() {
    return action == ActionState.lowBlocking || action == ActionState.highBlocking;
  }


  private boolean isMoving() {
    return action == ActionState.movingLeft || action == ActionState.movingRight;
  }


  public boolean executeAction(ActionState action) {
    // when a fighter initiates an attack or is parried, they are locked in this action until the animation completes
    if (!isAttacking() && !isParried) {
      
      this.action = action;
      actionTimer.stop(); // stop the timer to cancel previous animation

      switch(action) {
        case idle -> {}
        case movingLeft -> startMovingTimer();
        case movingRight -> startMovingTimer();
        case highAttacking, lowAttacking -> startAttackTimer();
        case highBlocking, lowBlocking -> startParryWindowTimer();
      }
      return true;
    }
    return false;
  }
  

  private boolean updatePosition(double posX, double posY) {
    if (posX > minX && posX + width < maxX) {
      // update fighter body hitbox position (position values represent topleft corner)
      this.posX = posX;
      this.posY = posY;
      // update attack hitbox a constant distance from fighter based which direction fighter is facing
      attackX = posX + ((side == FighterSide.left) ? width : 0);
      attackY = posY + (0.5 * height); 
      
      return true;
    }
    return false;
  }


  private void startMovingTimer() {
    double deltaX = (action == ActionState.movingLeft) ? -this.deltaX : this.deltaX;
    actionTimer = new Timeline(new KeyFrame (Duration.millis(1000), e -> updatePosition(posX + deltaX, posY)));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.setRate(60);
    actionTimer.play();
  }


  private void startAttackTimer() {
    int numberFrames = (attackDuration * 60) / 1000; // 60 frames per 1000 ms
    double frameDuration = attackDuration / numberFrames; // time in ms of each frame
    double attackDeltaX =  ((side == FighterSide.left) ? attackReach : -attackReach) / numberFrames; // attack dx per frame
    
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> attackX += attackDeltaX));
    Timeline resetTimer = new Timeline(new KeyFrame(Duration.millis(attackResetDuration))); // delay after attack to prevent attack spam
    actionTimer.setOnFinished(e -> resetTimer.play());
    resetTimer.setOnFinished(e -> {action = ActionState.idle; updatePosition(posX, posY);});
    actionTimer.setCycleCount(numberFrames);
    actionTimer.play();
  }


  private void startParryWindowTimer() {
    // timer to reset parry window after delay
    canParry =  true;
    actionTimer = new Timeline(new KeyFrame(Duration.millis(parryWindowDuration)));
    actionTimer.setOnFinished(e -> canParry = false);
    actionTimer.play();
  }


  private void startInvulnerableTimer() {
    // timer to reset invulnerability status after delay
    isInvulnerable =  true;
    Timeline invulnerableTimer = new Timeline(new KeyFrame(Duration.millis(invulnerabilityDuration)));
    invulnerableTimer.setOnFinished(e -> isInvulnerable = false);
    invulnerableTimer.play();
  }


  private void startParriedTimer() {
    // timer to reset parried status after delay
    isParried =  true;
    Timeline parriedTimer = new Timeline(new KeyFrame(Duration.millis(parriedDuration)));
    parriedTimer.setOnFinished(e -> isParried = false);
    parriedTimer.play();
  }
  

  public boolean detectHit(Fighter opponent) {
    // only check for hit if the opponent is attacking and the fighter was not already recently hit
    if (opponent.isAttacking() && !isInvulnerable) {

      // check if fighter hitbox contains opponent attack
      if (opponent.attackX >= posX && opponent.attackX <= posX + width && opponent.attackY >= posY && opponent.attackY < posY + height) {

        boolean attackBlocked; // check if fighter blocked the type of opponent attack
        switch(action) {
          case lowBlocking -> attackBlocked = opponent.action == ActionState.lowAttacking;
          case highBlocking -> attackBlocked = opponent.action == ActionState.highAttacking;
          default -> attackBlocked = false;
        }

        if (attackBlocked) {
          if (canParry) opponent.startParriedTimer(); // prevent damage and parry the opponent if their attack was blocked within the parry window
          return false; // otherwise simply prevent damage
        } 
        else { // fighter takes damage
          // take extra damage when fighter is hit while in parried status
          double damageMultiplier = 1;
          if (isParried) { isParried = false; damageMultiplier = 3; }
          healthPoints -= opponent.attackDamage * damageMultiplier;

          // fighter is temporarily invulnerable after taking damage
          startInvulnerableTimer();
          return true;
        }
      }
      return false;
    }
    return false;
  }
}
