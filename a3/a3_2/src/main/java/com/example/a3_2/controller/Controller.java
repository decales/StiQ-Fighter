package com.example.a3_2.controller;

import java.util.Stack;
import com.example.a3_2.model.Model;
import com.example.a3_2.model.Fighter.FighterSide;
import com.example.a3_2.view.MenuButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Controller {
  
  // keys mapped in order to left, right, attack, block
  public enum LeftPlayerKey { A, S, Q, W };
  public enum RightPlayerKey { K, L, I, O };

  private Model model;
  private Stack<LeftPlayerKey> leftKeyStack;
  private Stack<RightPlayerKey> rightKeyStack;

  public Controller(Model model){
    this.model = model;
    leftKeyStack = new Stack<>();
    rightKeyStack = new Stack<>();
  }


  public void handleMouseClicked(MouseEvent e) {
    if (e.getSource() instanceof MenuButton menuButton) {
      model.startGame(menuButton.gameMode);
    }
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
        leftKeyAction();
      }
    }
    else if (isRightKey(keyStr)) {
      if (rightKeyStack.contains(RightPlayerKey.valueOf(keyStr))) {
        rightKeyStack.remove(RightPlayerKey.valueOf(keyStr));
        rightKeyAction();
      }
    }
  }


  private void leftKeyAction() {
    model.controlPlayerFighter( ((leftKeyStack.isEmpty()) ? null : leftKeyStack.peek()), FighterSide.left);
  }


  private void rightKeyAction() {
    model.controlPlayerFighter( ((rightKeyStack.isEmpty()) ? null : rightKeyStack.peek()), FighterSide.right);
  }
}
