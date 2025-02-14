package com.example.a3_2.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class Fighter {

  public enum Action { moveLeft, moveRight, attack, block };
  public enum ActionState { idle, movingLeft, movingRight, attacking, preBlocking, blocking, postBlocking, parried };
  public enum FighterSide { left, right };
  
  public int actionFrame;
  public double frameDuration;
  private Timeline actionTimer;

  public ActionState action;
  public FighterSide side;

  public int initHealthPoints;
  public int healthPoints;
  public int attackDamage;
  public int attackDuration;
  public boolean isInvulnerable;
  public int invulnerabilityDuration;
  public boolean isParried;
  public int parriedDuration;
  public boolean canParry;
  public int parryWindowDuration;

  public double width, height;
  public double minX, maxX;
  public double initX, initY;
  public double posX, posY;
  public double deltaX;

  public double attackRadius;
  public double attackX, attackY;
  public double attackReach;
  
  public Fighter(FighterSide side, double viewWidth, double viewHeight) {

    // action/animation attributes
    frameDuration = 1000 / 60;
    actionTimer = new Timeline();

    // State-based attributes
    action = ActionState.idle;
    this.side = side;

    // Stat-based attributes
    // all status and animation durations are in frames
    initHealthPoints = 100;
    healthPoints = initHealthPoints;
    attackDamage = 10;
    attackDuration = 48;
    invulnerabilityDuration = 60;
    parriedDuration = 180;
    parryWindowDuration = 15;

    // Position-based attributes
    width = viewWidth * 0.075;
    height = viewHeight * 0.4;
    minX = 0;
    maxX = viewWidth;
    initX = (side == FighterSide.left) ? viewWidth * 0.2 - width : viewWidth * 0.8;
    initY = viewHeight * 0.667 - height;
    deltaX = (viewWidth * 0.0025);
    attackReach = width * 3;
    attackRadius = width * 0.05;
    
    updatePosition(initX, initY);
  }


  public void reset() {
    healthPoints = initHealthPoints;
    isInvulnerable = false;
    isParried = false;
    updatePosition(initX, initY);
  }


  private boolean isAnimationLocked() {
    // fighter is locked into certain animations based on action state
    return action == ActionState.attacking 
        || action == ActionState.preBlocking 
        || action == ActionState.postBlocking
        || action == ActionState.parried;
  }


  public boolean executeAction(Action action) {
    if (!isAnimationLocked()) {
      
      actionTimer.stop(); // stop the timer to cancel previous animation
      actionFrame = 0; // reset frame count for next animation

      // if transitioning from a block, play block wind-down animation then execute the next action
      if (this.action == ActionState.blocking) postBlock(action);
      else {
        switch(action) {
          case moveLeft -> moveLeft();
          case moveRight -> moveRight();
          case attack -> attack();
          case block -> preBlock();
          case null -> this.action = ActionState.idle;
        }
        return true;
      }
    }
    return false;
  }
  

  private boolean updatePosition(double posX, double posY) {
    if (posX > minX && posX + width < maxX) {
      // update fighter body hitbox position (position values represent topleft corner)
      this.posX = posX;
      this.posY = posY;
      resetAttackHitbox();
      return true;
    }
    return false;
  }

  private void resetAttackHitbox() {
    // update attack hitbox a constant distance from fighter based which direction fighter is facing
    attackX = posX + ((side == FighterSide.left) ? width : 0);
    attackY = posY - attackRadius; 
  }


  private void moveLeft() {
    action = ActionState.movingLeft;
    actionTimer = new Timeline(new KeyFrame (Duration.millis(frameDuration), e -> { updatePosition(posX - deltaX, posY); actionFrame++; }));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.play();
  }


  private void moveRight() {
    action = ActionState.movingRight;
    actionTimer = new Timeline(new KeyFrame (Duration.millis(frameDuration), e -> { updatePosition(posX + deltaX, posY); actionFrame++; }));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.play();
  }


  private void attack() {
    action = ActionState.attacking;
    // attack comes out on frame 24 reaches its peak range on frames 30 - 33 
    double attackDeltaX =  ((side == FighterSide.left) ? attackReach : -attackReach) / 8;

    // wind-up animation
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e0 -> actionFrame++));
    actionTimer.setCycleCount(24);
    actionTimer.setOnFinished(e1 -> { 
      // thrust animation
      attackY += attackRadius; // lowers attack hitbox so that it can hit the opponent hitbox
      actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e2 -> { attackX += attackDeltaX; actionFrame++; }));
      actionTimer.setCycleCount(8);
      actionTimer.setOnFinished( e3-> {
        // wind-down animation
        resetAttackHitbox();
        actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e2 -> actionFrame++));
        actionTimer.setOnFinished(e4 -> action = ActionState.idle);
        actionTimer.setCycleCount(attackDuration - 32);
        actionTimer.play();
      });
      actionTimer.play();
    });
    actionTimer.play();
  }
  

  private void preBlock() {
    action = ActionState.preBlocking;
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> actionFrame++ ));
    actionTimer.setOnFinished(e -> block());  
    actionTimer.setCycleCount(18);
    actionTimer.play();
  }


  private void block() {
    action = ActionState.blocking;
    startParryWindowTimer(); // opponent attacks can be parried within short window at start of blocking state
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> actionFrame++ ));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.play();
  }


  private void postBlock(Action action) {
    this.action = ActionState.postBlocking;
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> actionFrame++ ));
    actionTimer.setOnFinished(e -> { this.action = ActionState.idle; executeAction(action);});
    actionTimer.setCycleCount(24);
    actionTimer.play();
  }


  private void startParryWindowTimer() {
    // timer to reset parry window after delay
    canParry =  true;
    Timeline parryTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration)));
    parryTimer.setCycleCount(parryWindowDuration);
    parryTimer.setOnFinished(e -> canParry = false);
    parryTimer.play();
  }


  private void startInvulnerableTimer() {
    // timer to reset invulnerability status after delay
    isInvulnerable =  true;
    Timeline invulnerableTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration)));
    invulnerableTimer.setCycleCount(invulnerabilityDuration);
    invulnerableTimer.setOnFinished(e -> isInvulnerable = false);
    invulnerableTimer.play();
  }


  private void startParriedTimer() {
    // timer to reset parried status after delay
    isParried =  true;
    Timeline parriedTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration)));
    parriedTimer.setCycleCount(parriedDuration);
    parriedTimer.setOnFinished(e -> isParried = false);
    parriedTimer.play();
  }
  

  public boolean detectHit(Fighter opponent) {
    // only check for hit if the opponent is attacking and the fighter was not already recently hit
    if (opponent.action == ActionState.attacking && !isInvulnerable) {

      // check if fighter hitbox contains opponent attack
      if (opponent.attackX >= posX && opponent.attackX <= posX + width && opponent.attackY >= posY && opponent.attackY < posY + height) {
        if (action == ActionState.blocking) { // check if fighter blocked attack
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
