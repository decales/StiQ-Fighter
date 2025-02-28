package com.example.a3_2.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ComputerFighter extends Fighter {

  private Map<GameState, Map<Action, Double>> qTable;
  private Map<GameState, Double> stateEpsilonMap;
  private double epsilon, alpha, gamma;
  private Action action, previousAction;
  private GameState state, previousState;
  private Random random;
  
  public ComputerFighter(FighterSide side, double viewSize, int frameRate) {

    super(side, viewSize, frameRate);

    qTable = new HashMap<>();

    // epsilon is treated as state dependent in this implementation
    // upon visiting a new state, the epsilon value for the state is initialized to the value below, and decays with each re-visit
    stateEpsilonMap = new HashMap<>();

    epsilon = 0.5; // (initial) exploration rate - known action <-> random action
    alpha = 0.15; // learning rate - conservative learning <-> aggressive learning
    gamma = 0.95; // discount factor - short-term rewards <-> long-term rewards
    
    random = new Random();
  }


  @Override
  public void initialize(int frame) {
    super.initialize(frame);
    previousAction = null;
  }


  public void determineAction(Fighter opponent, int frame) {
    if (!isAnimationLocked()) {

      previousState = state; // save the state of the game prior to executing the computer's last action, s
      state = new GameState(this, opponent); // observe state of game after computer's last action, s'
      scoreAction(); // determine immediate reward of the last action that lead from state s to s' and update qTable

      previousAction = action; // save the last action before getting the next one

      // check if current state s' has already been visited
      if (qTable.containsKey(state)) {

        // explore random action based state epsilon or exploit the best action in the state
        if (random.nextDouble(1.0) < stateEpsilonMap.get(state)) action = Action.values()[random.nextInt(Action.values().length)];
        else {
          Map<Action, Double> actionValue = qTable.get(state);

          // get the action with the highest immediate reward in the qTable
          double maxValue = Integer.MIN_VALUE;
          for (Object key : actionValue.keySet().toArray() ) {
            if (key instanceof Action actionKey) {
              if (actionValue.get(actionKey) > maxValue) {
                action = actionKey;
                maxValue = actionValue.get(actionKey);
              }
            }
          }
        } 
        // state-epsilon value decays each time state is exploited
        stateEpsilonMap.replace(state, stateEpsilonMap.get(state) * 0.985);
      }
      else { // otherwise, first time visiting state
        action = Action.values()[random.nextInt(Action.values().length)]; // choose a random action
        stateEpsilonMap.put(state, epsilon); // add the new state to the state-epsilon map, and initialize it with the default epsilon value
      }

      // check if computer is continuing to execute one of the continuous actions (for animation purposes)
      if (!((action == Action.moveLeft || action == Action.moveRight || action == Action.block) && action == previousAction))
      { executeAction(action, frame); }
    }
  }
  

  private void scoreAction() {
    if (previousState != null) {
      int reward = 0;
      // score using policy based on the last action executed in state s resulting in s'
      switch (action) {

        case moveLeft, moveRight -> {
          switch(previousState.opponentState) {
            case preAttacking, attacking -> {
              // attempted to dodge opponent attack - medium/low reward based on dodge success
              if (!state.inAttackRange && previousState.inAttackRange) reward += (!isInvincible) ? 20 : 10;
              // risked walking into opponent attack - medium/low punishment based on whether computer was hit
              else if (!previousState.inAttackRange && state.inAttackRange) reward -= (isInvincible) ? 20 : 10; 
            }
            default -> {
              if (!previousState.inAttackRange) {
                // low reward/punishment depending on whether fighter approached opponent when not in attack range
                reward += (state.opponentSide == (action == Action.moveLeft ? FighterSide.left : FighterSide.right)) ? 10 : -10;
              }
            }
          }
        }

        case attack -> {
          // opponent was already in range or moved in range
          if (previousState.inAttackRange || state.inAttackRange && !previousState.opponentIsInvincible) {
            switch(previousState.opponentState) {
              // attempted to attack when opponent vulnerable - low/medium reward depending on attack success
              case idle, movingLeft, movingRight, postBlocking, postAttacking -> reward += (state.opponentIsInvincible) ? 10 : 20;
              // attempted attack with risk of trading damage - neutral or low reward/punishment based on who took damage
              case preAttacking, attacking -> {
                reward +=
                  (!previousState.isInvincible && state.isInvincible && state.opponentIsInvincible) ? 0 // traded hits
                  : (!previousState.opponentIsInvincible && isInvincible) ? -10 // only computer was hit
                  : (state.opponentIsInvincible) ? 10 // only opponenet was hit
                  : 0; // neither fighter was hit
              } 
              case parried -> reward += 30; // capitalized post parry - high reward
              default -> reward -= 5; // very low punishment when attack is blocked
            }
            if (actionState == ActionState.parried) reward -= 30; // opponent parried the attack - high punishment
          } 
          else reward -= 30; // opponent could not be hit - high punishment
        }

        case block -> {
          // was/is range to block an attack
          if (previousState.inAttackRange || state.inAttackRange) {
            switch(previousState.opponentState) {
              // attempted to block opponent attack - high/medium/low reward based on effectiveness of block
              case preAttacking, attacking -> {
                reward += 
                  (state.opponentState == ActionState.parried) ? 30 // prevented damage and parried opponenent
                  : (state.isInvincible) ? 20 // only prevented damage
                  : 10; // attempted block
              }
              default -> {}
            }
          } 
          else reward -= 30; // unecessarily blocked - high punishment
        }
      }
      updateTable(reward); // update qTable using the immediate reward
    }
  }

  // private void scoreAction() {
  //   if (previousState != null) {
  //
  //     int reward = 0; // score using policy based on the last action executed in state s resulting in s'
  //     
  //     switch (action) {
  //
  //       case moveLeft, moveRight -> {
  //         if (previousState.opponentIsAttacking) {
  //           // attempted to dodge opponent attack - low/very low reward based on dodge success
  //           if (!state.inAttackRange && previousState.inAttackRange) reward += (!isInvincible) ? 10 : 5;
  //           // risked walking into opponent attack - low/medium punishment based on whether computer was hit
  //           else if (!previousState.inAttackRange && state.inAttackRange) reward -= (isInvincible) ? 20 : 10; 
  //         }
  //         if (!previousState.inAttackRange) {
  //           // small reward/punishment depending on whether fighter approached opponent when not in attack range
  //           reward += (state.opponentSide == (action == Action.moveLeft ? FighterSide.left : FighterSide.right)) ? 10 : -10;
  //         }
  //       }
  //
  //       case attack -> {
  //         // opponent was already in range or moved in range
  //         if (previousState.inAttackRange || state.inAttackRange && !previousState.opponentIsInvincible) {
  //           // attempted attack with risk of trading damage - neutral or low reward/punishment based on who took damage
  //           if (previousState.opponentIsAttacking) {
  //             reward +=
  //               (!previousState.isInvincible && state.isInvincible && state.opponentIsInvincible) ? 0 // traded hits
  //               : (!previousState.opponentIsInvincible && isInvincible) ? -10 // only computer was hit
  //               : (state.opponentIsInvincible) ? 10 // only opponenet was hit
  //               : 0; // neither fighter was hit
  //           }
  //           // attempted to attack when opponent vulnerable - low/medium reward depending on attack success
  //           else if (previousState.opponentIsVulnerable) reward += (state.opponentIsInvincible) ? 5 : 10;
  //           // opponent parried the attack - high punishment
  //           if (actionState == ActionState.parried) reward -= 30; 
  //         } 
  //         else reward -= 30; // opponent could not be hit - high punishment
  //       }
  //
  //       case block -> {
  //         // was/is range to block an attack
  //         if (previousState.inAttackRange || state.inAttackRange) {
  //           // attempted to block opponent attack - high/medium/low reward based on whether the block was successful or not
  //           if (previousState.opponentIsAttacking) reward +=  (state.isInvincible) ? 20 : 10;
  //         } 
  //         else reward -= 30; // unecessarily blocked - high punishment
  //       }
  //     }
  //     updateTable(reward); // update qTable using the immediate reward
  //   }
  // }


  private void updateTable(int reward) {
    // Q(s, a) = (1 - alpha) * Q(s, a) + alpha * (reward + gamma * max(Q(s', a')))
    double previousQ = qTable.getOrDefault(previousState, new HashMap<>()).getOrDefault(action, 0.0); // Q(s, a)
    double maxQ = qTable.getOrDefault(state, new HashMap<>()).values().stream().max(Double::compare).orElse(0.0); // max(Q(s', a'))
    double newQ = (1 - alpha) * previousQ + alpha * (reward + gamma * maxQ);

    qTable.computeIfAbsent(previousState, k -> new HashMap<>()).put(action, newQ); // update table
  }
}
