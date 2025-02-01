package com.example.a3_2;

import java.util.Stack;
import com.example.a3_2.model.Model;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

public class Controller {

  private Model model;
  private Stack<KeyCode> keydownStack;
  private Timeline timer;
  
  public Controller(Model model) {
    this.model = model;
    keydownStack = new Stack<>();
  }


  public void handleKeyPressed(KeyEvent e) {
    KeyCode key = e.getCode();
    if (!keydownStack.contains(key)) {
      keydownStack.push(key);
      keyAction();
    }
  }


  public void handleKeyReleased(KeyEvent e) {
    KeyCode key = e.getCode();
    if (keydownStack.contains(key)) {
      keydownStack.remove(key);
    }
  }


  private void keyAction() {
    if (timer != null) timer.stop();

    timer = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
      if (!keydownStack.isEmpty()) {
        KeyCode recentKey = keydownStack.peek();
        switch(recentKey) {
          case A, D, J, K, L, U, I, O -> model.controlFighter(1, recentKey); 
          default -> {}
        }
      }
    }));
    timer.setCycleCount(Animation.INDEFINITE);
    timer.setRate(60);
    timer.play();
  }
}
