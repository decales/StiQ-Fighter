package com.example.a3_2.model;

import java.util.Map;
import java.util.Random;

import com.example.a3_2.model.GameData.GameState;

public class ComputerFighter extends Fighter {

  public Map<GameState, Map<ActionState, Double>> qMap;
  private double alpha, gamma, epsilon;
  private Random random;
  
  public ComputerFighter(FaceDirection directionFacing, double viewWidth, double viewHeight) {

    super(directionFacing, viewWidth, viewHeight);

    alpha = 0.0; // learning rate - conservative learning <-> aggressive learning
    gamma = 0.0; // discount factor - short-term rewards <-> long-term rewards
    epsilon = 0.0; // exploration rate - known action <-> random action

    random = new Random();

  }
  
  public void determineAction() {



    


  }

  private void doRandomAction() {


    // switch(random.nextInt(ActionState.values().length) ) {
    //   case 0 -> {}
    //   case 1  
    //
    //   default -> {}
    // }








        // case A, LEFT -> { if (fighter.posX >= 0) fighter.moveLeft(); }
        // case D, RIGHT -> { if (fighter.posX + fighter.width <= viewWidth) fighter.moveRight(); }
        // case J -> fighter.attack(ActionState.highAttacking);
        // case K -> fighter.attack(ActionState.midAttacking);
        // case L -> fighter.attack(ActionState.lowAttacking);
        // case U -> fighter.block(ActionState.highBlocking);
        // case I -> fighter.block(ActionState.midBlocking);
        // case O -> fighter.block(ActionState.lowBlocking);

    

  }
  
}
