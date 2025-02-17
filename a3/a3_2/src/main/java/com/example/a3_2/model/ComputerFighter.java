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
    alpha = 0.5; // learning rate - conservative learning <-> aggressive learning
    gamma = 0.1; // discount factor - short-term rewards <-> long-term rewards
    epsilon = 0.05; // exploration rate - known action <-> random action
    random = new Random();
  }

  
  private void updateTable(int reward) {
    // update qTable with the value of a recent action
    // Q(s,a) = Q(s,a) + alpha * [reward + gamma * max(Q(s',a')) - Q(s,a)]

    double previousQ = qTable.getOrDefault(previousState, new HashMap<>()).getOrDefault(previousAction, 0.0); // Q(s, a)
    double maxQ = qTable.getOrDefault(currentState, new HashMap<>()).values().stream().max(Double::compare).orElse(0.0); // max(Q(s', a'))
    double newQ = previousQ + alpha * (reward + gamma * maxQ - previousQ); // left side of formula, updated Q value 

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

      if (actionState == ActionState.attacking && !currentState.inAttackRange) reward -= 20;

      reward += (currentState.inAttackRange) ? 20 : -20; // computer close enough to attack opponent
      // if (currentState.isParried && !previousState.isParried) reward -= 25; // was parried by opponent
      if (currentState.healthPoints < previousState.healthPoints) { // was hit by opponent
        if (currentState.healthPoints <= 0) reward -= 100; // hit taken resulted in death
        else reward -= 10;
      }
      // if (currentState.opponentIsParried && !previousState.opponentIsParried) reward += 25; // parried the opponent
      if (currentState.opponentHealthPoints < previousState.opponentHealthPoints) { // hit the opponent
        if (currentState.healthPoints <= 0) reward += 100; // hit given killed the opponent
        else reward += 10;
      }
      // update qTable with the value of the recently performed action
      updateTable(reward);
    }
  }
  

  public void determineAction(Fighter opponent) {

    previousState = currentState; // keep track of previous state for reference in determining the value of an action
    currentState = new GameState(this, opponent); // update state of game from computer's point of view
    Action nextAction = null;

    // explore value of executing random action or choose next best action from qTable
    if (random.nextDouble(1.0) < epsilon) nextAction = Action.values()[random.nextInt(Action.values().length - 1)];
    else {
      Map<Action, Double> actionValue = qTable.get(currentState);
      if (actionValue != null) {
        
        double maxValue = Integer.MIN_VALUE;
        for (Object key : actionValue.keySet().toArray() ) {
          if (key instanceof Action actionKey) {

            if (actionValue.get(actionKey) > maxValue) {
              nextAction = actionKey;
              maxValue = actionValue.get(actionKey);
            }
          }
        }
      }
      else nextAction = Action.values()[random.nextInt(Action.values().length - 1)];
    }

    if (executeAction(nextAction)) {
      System.out.println(String.format("Computer executing %s", nextAction));
      scoreAction();
    }
  }
}
