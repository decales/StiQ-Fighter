package com.example.a3_2.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Fighter {

  public enum FaceDirection { left, right };
  public enum ControlType { human, computer };
  public enum ActionState { idle, moving, lowAttacking, midAttacking, highAttacking, lowBlocking, midBlocking, highBlocking };
  
  public FaceDirection directionFacing;
  public ControlType controlType;
  public ActionState action;

  public int healthPoints;
  public int attackDamage;
  public int attackDuration;
  public int attackResetDuration;
  public boolean isInvulnerable;
  public int invulnerabilityDuration;
  public boolean isParried;
  public int parryDuration;

  public double width, height;
  public double initX, initY;
  public double posX, posY;
  public double deltaX, deltaY;

  public double attackRadius;
  public double attackX, attackY;
  public double attackReach;
  
  public Fighter(FaceDirection directionFacing, ControlType controlType, double displayWidth, double displayHeight) {

    // State-based attributes
    this.directionFacing = directionFacing;
    this.controlType = controlType;
    action = ActionState.idle;

    // Stat-based attributes
    healthPoints = 100;
    attackDamage = 10;
    attackDuration = 150;
    attackResetDuration = 200;
    invulnerabilityDuration = 1000;
    parryDuration = 3000;

    // Position-based attributes
    width = displayWidth * 0.0667;
    height = displayHeight * 0.333;
    initX = (directionFacing == FaceDirection.left) ? displayWidth * 0.8 : displayWidth * 0.2 - width;
    initY = displayHeight * 0.667 - height;
    deltaX = (displayWidth * 0.00333);
    attackReach = width * 2;
    attackRadius = width * 0.1;
    
    updatePosition(initX, initY);
  }


  private boolean isAttacking() {
    // when a fighter initiates an attack, they are locked in this action until the attack animation completes
    return action == ActionState.lowAttacking || action == ActionState.midAttacking || action == ActionState.highAttacking;
  }


  public boolean isDead() {
    return healthPoints <= 0;
  }
  

  private void updatePosition(double posX, double posY) {
    // update fighter body hitbox position (position values represent topleft corner)
    this.posX = posX;
    this.posY = posY;

    // update attack hitbox a constant distance from fighter based which direction fighter is facing
    attackX = posX + ((directionFacing == FaceDirection.left) ? 0 : width);
    attackY = posY + (0.5 * height); 
  }


  protected void moveLeft() {
    if (!isAttacking() && !isParried) {
      action = ActionState.moving;
      updatePosition(posX - deltaX, posY);
    }
  }


  protected void moveRight() {
    if (!isAttacking() && !isParried) {
      action = ActionState.moving;
      updatePosition(posX + deltaX, posY);
    }
  }


  protected void attack(ActionState attackAction) {
    // attack type passed into attackAction in the Model.controlFigher()
    if (!isAttacking() && !isParried) {
      action = attackAction;
      startAttackTimer();
    }
  }


  protected void block(ActionState blockAction) {
    // block type passed into blockAction in Model.controlFigher()
    if (!isAttacking() && !isParried) action = blockAction;
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
  

  private void startStatusTimer() {
    // timer to reset invulnerability and parried statuses after delay
    Timeline statusTimer = new Timeline();

    if (isInvulnerable) {
      statusTimer.getKeyFrames().setAll(new KeyFrame(Duration.millis(invulnerabilityDuration)));
      statusTimer.setOnFinished(e -> isInvulnerable = false);
    }
    else if (isParried) {
      statusTimer.getKeyFrames().setAll(new KeyFrame(Duration.millis(parryDuration)));
      statusTimer.setOnFinished(e -> isParried = false);
    }
    statusTimer.play();
  }


  public void detectHit(Fighter opponent) {
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
          // if the figher started their block immediately before the opponent's attack would land, parry the opponent

        }
        else { // fighter takes damage
          // take extra damage fighter is hit while in parried status
          double damageMultiplier = 1;
          if (isParried) { isParried = false; damageMultiplier = 2.5; }
          healthPoints -= opponent.attackDamage * damageMultiplier;

          // fighter is temporarily invulnerable after taking damage
          isInvulnerable = true;
          startStatusTimer();
        }
      }
    }
  }
}
