package com.example.a3_2.model;

import java.util.Objects;

import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FaceDirection;

public class GameState {

  ActionState action;
  ActionState opponentAction;
  double healthPoints;
  double opponentHealthPoints;
  boolean isParried;
  boolean opponentIsParried;
  boolean isInvulnerable;
  boolean opponentIsInvulnerable;
  double distanceToOpponent;
  boolean inAttackRange;

  public GameState(Fighter self, Fighter opponent) {

    healthPoints = self.healthPoints;
    opponentHealthPoints = opponent.healthPoints;

    isParried = self.isParried;
    opponentIsParried = opponent.isParried;

    isInvulnerable = self.isInvulnerable;
    opponentIsInvulnerable = opponent.isInvulnerable;

    // distanceToOpponent = Math.abs(self.posX - opponent.posX);
    inAttackRange = (self.directionFacing == FaceDirection.right) 
      ?  (self.posX + self.width + self.attackReach) >= opponent.posX
      :  (self.posX - self.attackReach) <= (opponent.posX + opponent.width);
  }


  public boolean equals(Object object) {
    if (object instanceof GameState) return object.hashCode() == this.hashCode();
    else return false;
  }

  
  public int hashCode() {
    return Objects.hash(
        healthPoints, opponentHealthPoints,
        isParried, opponentIsParried, 
        isInvulnerable, opponentIsInvulnerable,
        inAttackRange);
  }
}
