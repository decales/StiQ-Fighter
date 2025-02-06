package com.example.a3_2.controller;

import java.util.Stack;

import com.example.a3_2.model.Model;
import com.example.a3_2.model.Fighter.FighterSide;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class Controller {
  
  public enum LeftPlayerKey { Z, X, C, V, D, F };
  public enum RightPlayerKey { N, M, COMMA, PERIOD, K, L };

  private Model model;
  private Stack<LeftPlayerKey> leftKeyStack;
  private Stack<RightPlayerKey> rightKeyStack;
  private Timeline leftTimer;
  private Timeline rightTimer;

  public Controller(Model model){
    this.model = model;
    leftKeyStack = new Stack<>();
    rightKeyStack = new Stack<>();
  }


  public void handleMouseEvent(MouseEvent e) {
    System.out.println(e);
  }


  private boolean isLeftKey(String keyStr) {
    try { LeftPlayerKey.valueOf(keyStr); return true; }
    catch (IllegalArgumentException e) { return false; }
  }


  private boolean isRightKey(String keyStr) {
    try { RightPlayerKey.valueOf(keyStr); return true; }
    catch (IllegalArgumentException e) { return false; }
  }


  public void handleKeyPressed(KeyEvent e) {
    String keyStr = e.getCode().toString();

    if (isLeftKey(keyStr)) {
      if (!leftKeyStack.contains(LeftPlayerKey.valueOf(keyStr))) {
        leftKeyStack.push(LeftPlayerKey.valueOf(keyStr));
        leftKeyAction();
      }
    }
    else if (isRightKey(keyStr)) {
      if (!rightKeyStack.contains(RightPlayerKey.valueOf(keyStr))) {
        rightKeyStack.push(RightPlayerKey.valueOf(keyStr));
        rightKeyAction();
      }
    }
  }


  public void handleKeyReleased(KeyEvent e) {
    String keyStr = e.getCode().toString();

    if (isLeftKey(keyStr)) {
      if (leftKeyStack.contains(LeftPlayerKey.valueOf(keyStr))) {
        leftKeyStack.remove(LeftPlayerKey.valueOf(keyStr));
      }
    }
    else if (isRightKey(keyStr)) {
      if (rightKeyStack.contains(RightPlayerKey.valueOf(keyStr))) {
        rightKeyStack.remove(RightPlayerKey.valueOf(keyStr));
      }
    }
  }


  private void leftKeyAction() {
    if (leftTimer != null) leftTimer.stop();
    leftTimer = new Timeline(new KeyFrame(Duration.millis(1000), event -> { 
      if (!leftKeyStack.isEmpty()) {
        model.controlPlayerFighter(leftKeyStack.peek(), FighterSide.left);
      }   
    }));
    leftTimer.setCycleCount(Animation.INDEFINITE);
    leftTimer.setRate(60);
    leftTimer.play();
  }


  private void rightKeyAction() {
    if (rightTimer != null) rightTimer.stop();
    rightTimer = new Timeline(new KeyFrame(Duration.millis(1000), event -> { 
      if (!rightKeyStack.isEmpty()) {
        model.controlPlayerFighter(rightKeyStack.peek(), FighterSide.right);
      }   
    }));
    rightTimer.setCycleCount(Animation.INDEFINITE);
    rightTimer.setRate(60);
    rightTimer.play();
  }
}
