package com.example.a3_2.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ComputerFighter extends Fighter {

  public Map<GameState, Map<Action, Double>> qTable;
  private double alpha, gamma, epsilon;
  private Action action, previousAction;
  private GameState state, previousState;
  private Random random;
  
  public ComputerFighter(FighterSide side, double viewSize, int frameRate) {

    super(side, viewSize, frameRate);

    qTable = new HashMap<>();
    alpha = 0.2; // learning rate - conservative learning <-> aggressive learning
    gamma = 0.8; // discount factor - short-term rewards <-> long-term rewards
    epsilon = 0.4; // exploration rate - known action <-> random action
    random = new Random();
  }


  @Override
  public void initialize(int frame) {
    super.initialize(frame);
    epsilon *= 0.85;// explore less and exploit more each round when initialize is called in model
                   // initial -> 40%
                   // 5 rounds -> ~20%
                   // 10 rounds -> ~10%
                   // 15 rounds -> ~5%
                   // 20 rounds -> ~2%
  }


  public void determineAction(Fighter opponent, int frame) {
    if (!isAnimationLocked()) {

      previousState = state; // save the state of the game prior to executing the computer's last action, s
      state = new GameState(this, opponent); // observe state of game after computer's last action, s'
      scoreAction(); // determine immediate reward of the last action that lead from state s to s' and update qTable

      previousAction = action; // save the last action before getting the next one

      // explore value of executing random action or choose next best action from qTable
      if (random.nextDouble(1.0) < epsilon) action = Action.values()[random.nextInt(Action.values().length)];
      else {
        // check if current state has already been visited
        if (qTable.containsKey(state)) { 
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
        // otherwise first time in state, choose random action to explore
        else action = Action.values()[random.nextInt(Action.values().length)];
      }
      // check if computer is continuing to execute one of the continuous actions (for animation purposes)
      if (!((action == Action.moveLeft || action == Action.moveRight || action == Action.block) && action == previousAction))
      { executeAction(action, frame); }
    }
  }
  

  // private void scoreAction() {
  //   if (previousState != null) {
  //     int reward = 0;
  //     // score using policy based on the last action executed in state s resulting in s'
  //     switch (action) {
  //
  //       case moveLeft, moveRight -> {
  //         switch(previousState.opponentState) {
  //           case parried -> reward -= 20; // should have attacked when opponent is parried - medium punishment
  //           case preAttacking, attacking -> {
  //             // attempted to dodge opponent attack - low/very low reward based on dodge success
  //             if (!state.inAttackRange && previousState.inAttackRange) reward += (!isInvincible) ? 10 : 5;
  //             // risked walking into opponent attack - low/medium punishment based on whether computer was hit
  //             else if (!previousState.inAttackRange && state.inAttackRange) reward -= (isInvincible) ? 20 : 10; 
  //           }
  //           default -> {
  //             if (!previousState.inAttackRange) {
  //               // small reward/punishment depending on whether fighter approached opponent when not in attack range
  //               reward += (state.opponentSide == (action == Action.moveLeft ? FighterSide.left : FighterSide.right)) ? 10 : -10;
  //             }
  //           }
  //         }
  //       }
  //
  //       case attack -> {
  //         // opponent was already in range or moved in range
  //         if (previousState.inAttackRange || state.inAttackRange && !previousState.opponentIsInvincible) {
  //           switch(previousState.opponentState) {
  //             // attempted to attack when opponent vulnerable - low/medium reward depending on attack success
  //             case idle, movingLeft, movingRight, postBlocking, postAttacking -> reward += (state.opponentIsInvincible) ? 10 : 20;
  //             // attempted attack with risk of trading damage - neutral or low reward/punishment based on who took damage
  //             case preAttacking, attacking -> {
  //               reward +=
  //                 (!previousState.isInvincible && state.isInvincible && state.opponentIsInvincible) ? 0 // traded hits
  //                 : (!previousState.opponentIsInvincible && isInvincible) ? -10 // only computer was hit
  //                 : (state.opponentIsInvincible) ? 10 // only opponenet was hit
  //                 : 0; // neither fighter was hit
  //             } 
  //             case parried -> reward += 30; // capitalized post parry - high reward
  //             default -> { /* no punishment if opponent was defending */ }
  //           }
  //           if (actionState == ActionState.parried) reward -= 30; // opponent parried the attack - high punishment
  //         } 
  //         else reward -= 30; // opponent could not be hit - high punishment
  //       }
  //
  //       case block -> {
  //         // was/is range to block an attack
  //         if (previousState.inAttackRange || state.inAttackRange) {
  //           switch(previousState.opponentState) {
  //             case parried -> reward -= 20; // should have attacked when opponent was parried - medium punishment
  //             // attempted to block opponent attack - high/medium/low reward based on effectiveness of block
  //             case preAttacking, attacking -> {
  //               reward += 
  //                 (state.opponentState == ActionState.parried) ? 30 // prevented damage and parried opponenent
  //                 : (state.isInvincible) ? 20 // only prevented damage
  //                 : 10; // attempted block
  //             }
  //             default -> {}
  //           }
  //         } 
  //         else reward -= 30; // unecessarily blocked - high punishment
  //       }
  //     }
  //     updateTable(reward); // update qTable using the immediate reward
  //   }
  // }
  private void scoreAction() {
    if (previousState != null) {

      int reward = 0; // score using policy based on the last action executed in state s resulting in s'
      
      switch (action) {

        case moveLeft, moveRight -> {
          if (previousState.opponentIsAttacking) {
            // attempted to dodge opponent attack - low/very low reward based on dodge success
            if (!state.inAttackRange && previousState.inAttackRange) reward += (!isInvincible) ? 10 : 5;
            // risked walking into opponent attack - low/medium punishment based on whether computer was hit
            else if (!previousState.inAttackRange && state.inAttackRange) reward -= (isInvincible) ? 20 : 10; 
          }
          if (!previousState.inAttackRange) {
            // small reward/punishment depending on whether fighter approached opponent when not in attack range
            reward += (state.opponentSide == (action == Action.moveLeft ? FighterSide.left : FighterSide.right)) ? 10 : -10;
          }
        }

        case attack -> {
          // opponent was already in range or moved in range
          if (previousState.inAttackRange || state.inAttackRange && !previousState.opponentIsInvincible) {
            // attempted attack with risk of trading damage - neutral or low reward/punishment based on who took damage
            if (previousState.opponentIsAttacking) {
              reward +=
                (!previousState.isInvincible && state.isInvincible && state.opponentIsInvincible) ? 0 // traded hits
                : (!previousState.opponentIsInvincible && isInvincible) ? -10 // only computer was hit
                : (state.opponentIsInvincible) ? 10 // only opponenet was hit
                : 0; // neither fighter was hit
            }
            // attempted to attack when opponent vulnerable - low/medium reward depending on attack success
            else if (previousState.opponentIsVulnerable) reward += (state.opponentIsInvincible) ? 5 : 10;
            // opponent parried the attack - high punishment
            if (actionState == ActionState.parried) reward -= 30; 
          } 
          else reward -= 30; // opponent could not be hit - high punishment
        }

        case block -> {
          // was/is range to block an attack
          if (previousState.inAttackRange || state.inAttackRange) {
            // attempted to block opponent attack - high/medium/low reward based on whether the block was successful or not
            if (previousState.opponentIsAttacking) reward +=  (state.isInvincible) ? 20 : 10;
          } 
          else reward -= 30; // unecessarily blocked - high punishment
        }
      }
      updateTable(reward); // update qTable using the immediate reward
    }
  }


  private void updateTable(int reward) {
    // Q(s, a) = (1 - alpha) * Q(s, a) + alpha * (reward + gamma * max(Q(s', a')))
    double previousQ = qTable.getOrDefault(previousState, new HashMap<>()).getOrDefault(action, 0.0); // Q(s, a)
    double maxQ = qTable.getOrDefault(state, new HashMap<>()).values().stream().max(Double::compare).orElse(0.0); // max(Q(s', a'))
    double newQ = (1 - alpha) * previousQ + alpha * (reward + gamma * maxQ);

    qTable.computeIfAbsent(previousState, k -> new HashMap<>()).put(action, newQ); // update table
  }
}
