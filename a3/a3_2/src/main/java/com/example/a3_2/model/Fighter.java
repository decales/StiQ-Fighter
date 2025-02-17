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

  public ActionState actionState;
  public FighterSide side;

  public int initHealthPoints;
  public int healthPoints;
  public int attackWindupDuration;
  public int attackDuration;
  public int attackResetDuration;
  public int preBlockDuration;
  public int postBlockDuration;
  public boolean isInvulnerable;
  public int invulnerabilityDuration;
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
  
  public Fighter(FighterSide side, double viewSize, int frameRate) {

    // action/animation attributes
    frameDuration = 1000 / frameRate;
    actionTimer = new Timeline();

    // State-based attributes
    actionState = ActionState.idle;
    this.side = side;

    // Stat-based attributes
    // all status and animation durations are in frames
    initHealthPoints = 8;
    healthPoints = initHealthPoints;
    attackWindupDuration = 21;
    attackDuration = 12;
    attackResetDuration = 15;
    preBlockDuration = 18;
    postBlockDuration = 24;
    invulnerabilityDuration = 75;
    parriedDuration = 180;
    parryWindowDuration = 5;

    // Position-based attributes
    height = viewSize * 0.275; // main scaler
    width = height * 0.32;
    minX = 0;
    maxX = viewSize;
    initX = (side == FighterSide.left) ? viewSize * 0.2 - width : viewSize * 0.8;
    initY = viewSize * 0.4667 - height;
    deltaX = (viewSize * 0.0025);
    attackReach = width * 2.85;
    attackRadius = width * 0.05;
    
    updatePosition(initX, initY);
  }


  public void reset() {
    actionState = ActionState.idle;
    healthPoints = initHealthPoints;
    isInvulnerable = false;
    updatePosition(initX, initY);
  }


  private boolean isAnimationLocked() {
    // fighter is locked into certain animations based on action state
    return actionState == ActionState.attacking 
        || actionState == ActionState.preBlocking 
        || actionState == ActionState.postBlocking
        || actionState == ActionState.parried;
  }


  public boolean executeAction(Action action) {
    if (!isAnimationLocked()) {
      
      actionTimer.stop(); // stop the timer to cancel previous animation
      actionFrame = 0; // reset frame count for next animation

      // if transitioning from a block, play block wind-down animation then execute the next action
      if (actionState == ActionState.blocking) postBlock(action);
      else { // otherwise execute action normally
        switch(action) {
          case moveLeft -> moveLeft();
          case moveRight -> moveRight();
          case attack -> attack();
          case block -> preBlock();
          case null -> actionState = ActionState.idle;
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
    actionState = ActionState.movingLeft;
    actionTimer = new Timeline(new KeyFrame (Duration.millis(frameDuration), e -> { updatePosition(posX - deltaX, posY); actionFrame++; }));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.play();
  }


  private void moveRight() {
    actionState = ActionState.movingRight;
    actionTimer = new Timeline(new KeyFrame (Duration.millis(frameDuration), e -> { updatePosition(posX + deltaX, posY); actionFrame++; }));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.play();
  }


  private void attack() {
    actionState = ActionState.attacking;
    // entire attack animation is 48 frames
    // attack comes out on frame 24 and reaches its peak range on frames 30 - 33 
    double attackDeltaX =  ((side == FighterSide.left) ? attackReach : -attackReach) / attackDuration;

    // wind-up animation
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e0 -> actionFrame++));
    actionTimer.setCycleCount(attackWindupDuration);
    actionTimer.setOnFinished(e1 -> { 
      // thrust animation
      attackY += attackRadius; // lowers attack hitbox so that it can hit the opponent hitbox
      actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e2 -> { attackX += attackDeltaX; actionFrame++; }));
      actionTimer.setCycleCount(attackDuration);
      actionTimer.setOnFinished(e3 -> {
        // reset animation
        resetAttackHitbox();
        actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e2 -> actionFrame++));
        actionTimer.setOnFinished(e4 -> actionState = ActionState.idle);
        actionTimer.setCycleCount(attackResetDuration);

        actionTimer.play();
      });
      actionTimer.play();
    });
    actionTimer.play();
  }
  

  private void preBlock() {
    actionState = ActionState.preBlocking;
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> actionFrame++ ));
    actionTimer.setOnFinished(e -> block());  
    actionTimer.setCycleCount(preBlockDuration);
    actionTimer.play();
  }


  private void block() {
    actionState = ActionState.blocking;
    startParryWindowTimer(); // opponent attacks can be parried within short window at start of blocking state
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> actionFrame++ ));
    actionTimer.setCycleCount(Animation.INDEFINITE);
    actionTimer.play();
  }


  private void postBlock(Action action) {
    actionState = ActionState.postBlocking;
    actionTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> actionFrame++ ));
    actionTimer.setOnFinished(e -> { actionState = ActionState.idle; executeAction(action);});
    actionTimer.setCycleCount(postBlockDuration);
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


  private void startParriedTimer() {
    // timer to reset parried status after delay
    actionState = ActionState.parried;
    Timeline parriedTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration)));
    parriedTimer.setCycleCount(parriedDuration);
    parriedTimer.setOnFinished(e -> actionState = ActionState.idle);
    parriedTimer.play();
  }


  private void startInvulnerableTimer() {
    // timer to reset invulnerability status after delay
    isInvulnerable =  true;
    Timeline invulnerableTimer = new Timeline(new KeyFrame(Duration.millis(frameDuration)));
    invulnerableTimer.setCycleCount(invulnerabilityDuration);
    invulnerableTimer.setOnFinished(e -> isInvulnerable = false);
    invulnerableTimer.play();
  }


  public boolean detectHit(Fighter opponent) {
    // only check for hit if the opponent is attacking and the fighter was not already recently hit
    if (opponent.actionState == ActionState.attacking && !isInvulnerable) {

      // check if fighter hitbox contains opponent attack
      if (opponent.attackX >= posX && opponent.attackX <= posX + width && opponent.attackY >= posY && opponent.attackY < posY + height) {
        if (actionState == ActionState.blocking) { // check if fighter blocked attack
          if (canParry) opponent.startParriedTimer(); // prevent damage and parry the opponent if their attack was blocked within the parry window
          return false; // otherwise simply prevent damage
        } 
        else { // fighter takes damage
          // take extra damage when fighter is hit while in parried status
          if (actionState == ActionState.parried) { actionState = ActionState.idle; healthPoints -= 3; }
          else healthPoints -= 1;

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
