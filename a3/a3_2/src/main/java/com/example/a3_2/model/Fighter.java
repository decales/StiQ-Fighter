package com.example.a3_2.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class Fighter {

  public enum ActionState { movingLeft, movingRight, lowAttacking, midAttacking, highAttacking, lowBlocking, midBlocking, highBlocking, idle };
  public enum FaceDirection { left, right };
  
  public ActionState action;
  public FaceDirection directionFacing;

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
  
  public Fighter(FaceDirection directionFacing, double viewWidth, double viewHeight) {

    // State-based attributes
    this.directionFacing = directionFacing;
    action = ActionState.idle;

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
    width = viewWidth * 0.0667;
    height = viewHeight * 0.333;
    minX = 0;
    maxX = viewWidth;
    initX = (directionFacing == FaceDirection.left) ? viewWidth * 0.8 : viewWidth * 0.2 - width;
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
    return action == ActionState.lowAttacking || action == ActionState.midAttacking || action == ActionState.highAttacking;
  }


  private boolean isBlocking() {
    return action == ActionState.lowBlocking || action == ActionState.midBlocking || action == ActionState.highBlocking;
  }


  public boolean executeAction(ActionState action) {
    // when a fighter initiates an attack or is parried, they are locked in this action until the animation completes
    if (!isAttacking() && !isParried) {
      this.action = action;

      switch(action) {
        case idle -> {}
        case movingLeft -> { return updatePosition(posX - deltaX, posY); }
        case movingRight -> { return updatePosition(posX + deltaX, posY); }
        case highAttacking, midAttacking, lowAttacking -> startAttackTimer();
        case highBlocking, midBlocking, lowBlocking -> startParryWindowTimer();
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
      attackX = posX + ((directionFacing == FaceDirection.left) ? 0 : width);
      attackY = posY + (0.5 * height); 
      
      return true;
    }
    return false;
  }


  private void startAttackTimer() {
    int numberFrames = (attackDuration * 60) / 1000; // 60 frames per 1000 ms
    double frameDuration = attackDuration / numberFrames; // time in ms of each frame
    double attackDeltaX =  ((directionFacing == FaceDirection.left) ? -attackReach : attackReach) / numberFrames; // attack dx per frame
    
    Timeline attackTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> attackX += attackDeltaX));
    Timeline resetTimer = new Timeline(new KeyFrame(Duration.millis(attackResetDuration))); // delay after attack to prevent attack spam
    attackTimer.setOnFinished(e -> resetTimer.play());
    resetTimer.setOnFinished(e -> {action = ActionState.idle; updatePosition(posX, posY);});
    attackTimer.setCycleCount(numberFrames);
    attackTimer.play();
  }


  private void startParryWindowTimer() {
    // timer to reset parry window after delay
    canParry =  true;
    Timeline timer = new Timeline(new KeyFrame(Duration.millis(parryWindowDuration)));
    timer.setOnFinished(e -> canParry = false);
    timer.play();
  }


  private void startInvulnerableTimer() {
    // timer to reset invulnerability status after delay
    isInvulnerable =  true;
    Timeline timer = new Timeline(new KeyFrame(Duration.millis(invulnerabilityDuration)));
    timer.setOnFinished(e -> isInvulnerable = false);
    timer.play();
  }


  private void startParriedTimer() {
    // timer to reset parried status after delay
    isParried =  true;
    Timeline timer = new Timeline(new KeyFrame(Duration.millis(parriedDuration)));
    timer.setOnFinished(e -> isParried = false);
    timer.play();
  }
  

  public boolean detectHit(Fighter opponent) {
    // only check for hit if the opponent is attacking and the fighter was not already recently hit
    if (opponent.isAttacking() && !isInvulnerable) {

      // check if fighter hitbox contains opponent attack
      if (opponent.attackX >= posX && opponent.attackX <= posX + width && opponent.attackY >= posY && opponent.attackY < posY + height) {

        boolean attackBlocked; // check if fighter blocked the type of opponent attack
        switch(action) {
          case lowBlocking -> attackBlocked = opponent.action == ActionState.lowAttacking;
          case midBlocking -> attackBlocked = opponent.action == ActionState.midAttacking;
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
