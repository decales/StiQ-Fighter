package com.example.a3_2.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ComputerFighter extends Fighter {

  public Map<GameState, Map<Action, Double>> qTable;
  private double alpha, gamma, epsilon;
  private Action previousAction;
  private GameState previousState;
  private GameState currentState;
  private Random random;
  
  public ComputerFighter(FighterSide side, double viewSize, int frameRate) {

    super(side, viewSize, frameRate);

    qTable = new HashMap<>();
    alpha = 0.2; // learning rate - conservative learning <-> aggressive learning
    gamma = 0.25; // discount factor - short-term rewards <-> long-term rewards
    epsilon = 0.015; // exploration rate - known action <-> random action (kept low because of high frame rate)
    random = new Random();
  }

  
  private void updateTable(int reward) {
    // update qTable with the value of a recent action

    double previousQ = qTable.getOrDefault(previousState, new HashMap<>()).getOrDefault(previousAction, 0.0); // Q(s, a)
    double maxQ = qTable.getOrDefault(currentState, new HashMap<>()).values().stream().max(Double::compare).orElse(0.0); // max(Q(s', a'))
    double newQ = (1 - alpha) * previousQ + alpha * (reward + gamma * maxQ);

    if (!qTable.containsKey(previousState)) {
      HashMap<Action, Double> actionValue = new HashMap<>();
      actionValue.put(previousAction, newQ);
      qTable.put(previousState, actionValue);
    }
  }


  private void scoreAction() {
    // determine value of recent action by comparing its effect on the state of the game in favour of computer
    if (previousState != null) {

      int reward = 0;

      switch(previousAction) {

        case moveLeft, moveRight -> {
          if (currentState.opponentSide == FighterSide.left) {
            if (previousAction == Action.moveLeft) reward += 10; // reward for moving towards opponent
            else reward -= 10; // punishment for moving away
          }
          else { // opponent on right
            if (previousAction == Action.moveRight) reward += 10; // reward for moving towards opponenet
            else reward -= 10; // punishment for moving away
          }

          if (currentState.inAttackRange) { // reward being in attack attack range
            if (!previousState.inAttackRange) { // further reward for moving into attack range
              reward += 10;
            } 
            reward += 10;
          }
          else if (!currentState.inAttackRange && previousState.inAttackRange) { // moving out of attack range
            if (currentState.opponentIsAttacking) { // reward for dodging attack
              // reward += 10;
            }
            else { // punishment for unecessarily leaving attack range
              reward -= 20;
            }
          }
        }

        case attack -> {
          if (currentState.inAttackRange) { // reward for attempting to attack while in attack range
            if (currentState.healthDifference > previousState.healthDifference) { // further reward if attack damages opponenet
              if (currentState.opponentIsParried) { // even further reward for following up a parry with an attack
                reward += 10;
              }
              reward += 10;
            } 
            reward += 10;

            if (currentState.opponentIsBlocking) { // punishment if attack was blocked
              reward -= 5;
            }
            if (currentState.opponentIsInvulnerable) { // punishment if opponent is invulnerable
              reward -= 10;
            }
          }
          else { // punishment for missing attack / unnecessarily attacking
            reward -= 20;
          }
        }

        case block -> {
          if (currentState.inAttackRange) { // reward for attempting to block while in attack range
            if (currentState.opponentIsAttacking) { // further reward for blocking opponent's attack
              reward += 10;
            }
            if (currentState.opponentIsInvulnerable) { // reward for playing defensively while opponent can't take damage
              reward += 10;
            }
            reward += 10;
          }
          else { // punishment for unnecessarily blocking
            reward -= 20;
          }
        }
      }

      // update qTable with the value of the recently performed action
      updateTable(reward);
    }
  }
  

  public void determineAction(Fighter opponent, int frame) {

    if (!isAnimationLocked()) {

      previousState = currentState; // keep track of previous state for reference in determining the value of an action
      currentState = new GameState(this, opponent); // update state of game from computer's point of view
      Action nextAction = null;

      // explore value of executing random action or choose next best action from qTable
      if (random.nextDouble(1.0) < epsilon) nextAction = Action.values()[random.nextInt(Action.values().length)];
      else {
        Map<Action, Double> actionValue = qTable.get(currentState);
        // check if current state has already been visited
        if (actionValue != null) { 

          // get the action with the highest immediate reward in the qTable
          double maxValue = Integer.MIN_VALUE;
          for (Object key : actionValue.keySet().toArray() ) { 
            if (key instanceof Action actionKey) {

              if (actionValue.get(actionKey) > maxValue) {
                nextAction = actionKey;
                maxValue = actionValue.get(actionKey);
              }
            }
          }
        } // otherwise first time in state, choose random action
        else nextAction = Action.values()[random.nextInt(Action.values().length)];
      }


      if (executeAction(nextAction, frame)) {
        previousAction = nextAction; // action executed successfully, mark it as previous action
        scoreAction();
      }
    }
  }
}
