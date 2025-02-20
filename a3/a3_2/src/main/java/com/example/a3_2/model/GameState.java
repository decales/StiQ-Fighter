package com.example.a3_2.model;

import java.util.Objects;

import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;

public class GameState {

  boolean opponentIsAttacking;
  boolean opponentIsBlocking;
  boolean opponentIsParried;
  boolean opponentIsInvulnerable;
  FighterSide opponentSide;
  int healthDifference;
  boolean inAttackRange;

  public GameState(Fighter self, Fighter opponent) {

    opponentIsAttacking = opponent.actionState == ActionState.attacking;
    opponentIsBlocking = opponent.actionState == ActionState.preBlocking || opponent.actionState == ActionState.blocking;
    opponentIsParried = opponent.actionState == ActionState.parried;
    opponentIsInvulnerable = opponent.isInvulnerable;
    opponentSide = opponent.side;

    healthDifference = self.healthPoints - opponent.healthPoints; // (-): AI losing   (+): AI winning

    inAttackRange = (self.side == FighterSide.left) 
      ?  (self.posX + self.width + self.attackReach) >= opponent.posX
      :  (self.posX - self.attackReach) <= (opponent.posX + opponent.width);
  }



  public boolean equals(Object object) {
    if (object instanceof GameState) return object.hashCode() == this.hashCode();
    else return false;
  }


  public int hashCode() {
    return Objects.hash(
        opponentIsAttacking,
        opponentIsBlocking,
        opponentIsParried,
        opponentIsInvulnerable,
        opponentSide,
        healthDifference,
        inAttackRange
    );
  }
}
