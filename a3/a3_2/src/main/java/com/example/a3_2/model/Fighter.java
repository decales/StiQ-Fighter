package com.example.a3_2.model;

public abstract class Fighter {

  public enum FighterSide { left, right };
  public enum Action { moveLeft, moveRight, attack, block };
  public enum ActionState { idle, movingLeft, movingRight, preAttacking, attacking, postAttacking, preBlocking, blocking, postBlocking, deflecting, parried };
  
  public int actionFrame;
  public int invulnerableFrame;
  public int parryFrame;
  public int executeFrame;
  public double frameDuration;

  public ActionState actionState;
  public FighterSide side;

  public int initHealthPoints;
  public int healthPoints;
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

    // State-based attributes
    actionState = ActionState.idle;
    this.side = side;

    // Stat-based attributes
    // all status and animation durations are in frames
    initHealthPoints = 8;
    healthPoints = initHealthPoints;
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
    attackRadius = width * 0.05;
    attackReach = (width * 2.8) + attackRadius;
    
    updatePosition(initX, initY);
  }


  public void reset() {
    healthPoints = initHealthPoints;
    isInvulnerable = false;
    updatePosition(initX, initY);
  }


  protected boolean isAnimationLocked() {
    // fighter is locked into certain animations based on action state
    return actionState == ActionState.preAttacking
        || actionState == ActionState.attacking
        || actionState == ActionState.postAttacking
        || actionState == ActionState.preBlocking 
        || actionState == ActionState.postBlocking
        || actionState ==  ActionState.deflecting
        || actionState == ActionState.parried;
  }


  public void sync(ActionState actionState, int frame) {
    // sync action state of fighter with current frame passed from model to animate actions
    
    actionFrame = frame - executeFrame; // execFrame is the frame an action begins execution from executeAction(), passed from model
    this.actionState = actionState;

    // invulnerable and parry frames are set on the frame an opponent attack hits the fighter from detectHit() 
    isInvulnerable = (frame - invulnerableFrame < invulnerabilityDuration);
    canParry = (frame - parryFrame < parryWindowDuration);

    switch(actionState) {
      // idle
      case idle -> {}
      // move left
      case movingLeft -> {
        updatePosition(posX - deltaX, posY);
      }
      // move right
      case movingRight -> {
        updatePosition(posX + deltaX, posY);
      }
      // pre-attack - wind-up animation
      case preAttacking -> {
        if (actionFrame == 20) {
          executeFrame = frame;
          sync(ActionState.attacking, frame);
        }
      }
      // attack - can damage opponent
      case attacking -> {
        if (actionFrame == 11) {
          executeFrame = frame;
          resetAttackHitbox();
          sync(ActionState.postAttacking, frame);
        }
        else attackX += ((side == FighterSide.left) ? attackReach : -attackReach) / 12; // move attack hitbox each frame
      }
      // post-attack - wind-down animation 
      case postAttacking -> {
        if (actionFrame == 26) {
          executeFrame = frame;
          sync(ActionState.idle, frame);
        }
      }
      // pre-block - wind-up animation
      case preBlocking -> {
        if (actionFrame == 17) {
          executeFrame = frame;
          parryFrame = frame;
          sync(ActionState.blocking, frame);
        }
      }
      // block - cannot take damage from opponent
      case blocking -> { }
      // post-block - wind-down animation
      case postBlocking -> {
        if (actionFrame == 26) {
          executeFrame = frame;
          sync(ActionState.idle, frame);
        }
      }
      // deflect - animation after blocking an attack from opponent
      case deflecting -> {
        if (actionFrame == 20) {
          executeFrame = frame;
          sync(ActionState.idle, frame);
        }
      }
      // parried - animation after well timed block from opponent, fighter is stunned and takes triple damage
      case parried -> {
        if (actionFrame == 120) {
          executeFrame = frame;
          sync(ActionState.idle, frame);
        }
      }
    }
  }


  public boolean executeAction(Action action, int frame) {
    if (!isAnimationLocked()) {

      // save the frame the action is is executed on for animation length purposes
      executeFrame = frame + 1; // +1 because of async update order between gameTimer and inputs in model

      if (actionState == ActionState.blocking) {
        actionState = ActionState.postBlocking;
        return false;
      }

      switch(action) {
        case moveLeft -> actionState = ActionState.movingLeft;
        case moveRight -> actionState = ActionState.movingRight;
        case attack -> actionState = ActionState.preAttacking;
        case block -> actionState = ActionState.preBlocking;
        case null -> actionState = ActionState.idle;
      }
      return true;
    }
    return false;
  }
  

  public void detectHit(Fighter opponent, int frame) {
    // only check for hit if the opponent is attacking and the fighter was not already recently hit
    if (opponent.actionState == ActionState.attacking && !isInvulnerable) {
      // check if fighter hitbox contains opponent attack
      if (opponent.attackX >= posX && opponent.attackX <= posX + width && opponent.attackY >= posY && opponent.attackY < posY + height) {
        // check if fighter blocked attack
        if (actionState == ActionState.blocking) { 
          // prevent damage and parry the opponent if their attack was blocked within the parry window
          if (canParry) {
            opponent.actionState = ActionState.parried;
            opponent.executeFrame = frame;
          } 
          //play deflecting animation
          actionState = ActionState.deflecting;
          executeFrame = frame;
        } 
        else if (actionState == ActionState.deflecting) { } // prevent damage in deflecting animation
        else { // fighter takes damage
          // take extra damage when fighter is hit while in parried status
          if (actionState == ActionState.parried) { actionState = ActionState.idle; healthPoints -= 3; }
          else healthPoints -= 1;

          // fighter is temporarily invulnerable after taking damage
          isInvulnerable = true;
          invulnerableFrame = frame;
        }
      }
    }
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
    attackY = posY + height / 2; 
  }
}
