package com.example.a3_2.model;

public abstract class Fighter {

  public enum FighterSide { left, right };
  public enum Action { moveLeft, moveRight, attack, block };
  public enum ActionState { 
  idle, movingLeft, movingRight, preAttacking, attacking, postAttacking, preBlocking, blocking, postBlocking, deflecting, parried 
  };
  
  public int actionFrame, executeFrame;
  public int invulnerableFrame;
  public int parryFrame;

  public ActionState actionState;
  public FighterSide side;

  private int initHealthPoints;
  public int healthPoints;
  public boolean isInvulnerable;
  private int invulnerabilityDuration;
  private boolean canParry;
  private int parryDuration;

  public double width, height;
  private double minX, maxX;
  private double deltaX;
  private double initX, initY;
  public double posX, posY;

  private double attackX, attackY;
  public double attackReach;
  
  public Fighter(FighterSide side, double viewSize, int frameRate) {

    // Stat-based attributes
    initHealthPoints = 8;
    invulnerabilityDuration = 75; // in frames
    parryDuration = 5; // in frames

    // Position-based attributes
    height = viewSize * 0.275; // main scaler
    width = height * 0.32;
    minX = 0;
    maxX = viewSize;
    initX = (side == FighterSide.left) ? viewSize * 0.2 - width : viewSize * 0.8;
    initY = viewSize * 0.4667 - height;
    deltaX = (viewSize * 0.0025);
    attackReach = (width * 2.55);
    
    initialize(0);
  }


  public void initialize(int frame) {
    actionState = ActionState.idle;
    actionFrame = frame;
    invulnerableFrame = -invulnerabilityDuration;
    parryFrame = -parryDuration;
    healthPoints = initHealthPoints;
    updatePosition(initX, initY);
  }


  protected boolean isAnimationLocked() {
    // various action states lock the fighter in an animation - these are the ones that don't
    return !(actionState == ActionState.idle || actionState == ActionState.movingLeft
          || actionState == ActionState.movingRight || actionState == ActionState.blocking);
  }


  public void syncAction(ActionState actionState, int frame) {
    // sync action state of fighter with current frame passed from model to animate actions
    
    actionFrame = frame - executeFrame; // execFrame is the frame an action begins execution from executeAction(), passed from model
    this.actionState = actionState;

    // invulnerable and parry frames are set on the frame an opponent attack hits the fighter from detectHit() 
    isInvulnerable = (frame - invulnerableFrame < invulnerabilityDuration);
    canParry = (frame - parryFrame < parryDuration);

    // in lack of a better way to do this, I have hard coded the animation durations directly into the cases below
    // each duration matches the size of the array for the respective ActionStatus in the sprite map in view.FighterView
    // I'm aware this is less than ideal :)
    switch(actionState) {
      // idle
      case idle -> {}
      // move left
      case movingLeft -> updatePosition(posX - deltaX, posY);
      // move right
      case movingRight -> updatePosition(posX + deltaX, posY);
      // pre-attack - wind-up animation
      case preAttacking -> {
        if (actionFrame == 20) {
          executeFrame = frame;
          syncAction(ActionState.attacking, frame);
        }
      }
      // attack - can damage opponent
      case attacking -> {
        if (actionFrame == 11) {
          executeFrame = frame;
          resetAttackHitbox();
          syncAction(ActionState.postAttacking, frame);
        }
        else attackX += ((side == FighterSide.left) ? attackReach : -attackReach) / 11; // move attack hitbox each frame
      }
      // post-attack - wind-down animation 
      case postAttacking -> {
        if (actionFrame == 26) {
          executeFrame = frame;
          syncAction(ActionState.idle, frame);
        }
      }
      // pre-block - wind-up animation
      case preBlocking -> {
        if (actionFrame == 17) {
          executeFrame = frame;
          parryFrame = frame;
          syncAction(ActionState.blocking, frame);
        }
      }
      // block - cannot take damage from opponent
      case blocking -> { }
      // post-block - wind-down animation
      case postBlocking -> {
        if (actionFrame == 26) {
          executeFrame = frame;
          syncAction(ActionState.idle, frame);
        }
      }
      // deflect - animation after blocking an attack from opponent
      case deflecting -> {
        if (actionFrame == 20) {
          executeFrame = frame;
          syncAction(ActionState.idle, frame);
        }
      }
      // parried - animation after well timed block from opponent, fighter is stunned and takes triple damage
      case parried -> {
        if (actionFrame == 65) {
          executeFrame = frame;
          syncAction(ActionState.idle, frame);
        }
        else resetAttackHitbox();
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
    // only check for hit if the opponent is attacking
    if (opponent.actionState == ActionState.attacking) {
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
        // fighter takes damage in non blocking-deflecting-invulnernable states
        else if (actionState != ActionState.deflecting && !isInvulnerable) { 
          // take extra damage when fighter is hit while in parried status
          if (actionState == ActionState.parried) { actionState = ActionState.idle; healthPoints -= 3; }
          else healthPoints -= 1;

          // fighter is temporarily invulnerable after taking damage
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
