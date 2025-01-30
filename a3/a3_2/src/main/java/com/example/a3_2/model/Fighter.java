package com.example.a3_2.model;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class Fighter {

  public enum FaceDirection { left, right };
  public enum ControlType { human, computer };
  public enum ActionState { idle, lowAttacking, midAttacking, highAttacking, lowBlocking, midBlocking, highBlocking };
  
  public FaceDirection directionFacing;
  public ControlType controlType;
  public ActionState action;

  public double width, height;
  public double initX, initY;
  public double posX, posY;
  public double deltaX, deltaY;

  public double weaponRadius;
  public double weaponX, weaponY;
  

  public Fighter(FaceDirection directionFacing, ControlType controlType, double displayWidth, double displayHeight) {

    this.directionFacing = directionFacing;
    this.controlType = controlType;
    action = ActionState.idle;

    width = displayWidth * 0.0667;
    height = displayHeight * 0.333;
    initX = (directionFacing == FaceDirection.left) ? displayWidth * 0.8 : displayWidth * 0.2 - width;
    initY = displayHeight * 0.667 - height;
    weaponRadius = width * 0.1;
    updatePosition(initX, initY);
  }

  private void updatePosition(double posX, double posY) {
    // update fighter body hitbox position (position values represent topleft corner)
    this.posX = posX;
    this.posY = posY;

    // update weapon tip hitbox constant distance from fighter based which direction fighter faces
    weaponX = posX + ((directionFacing == FaceDirection.left) ? -width : width * 2);
    weaponY = posY + (0.333 * height); 
  }
  
}
