package com.example.a3_2.model;

import java.util.Objects;

import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;

// public class GameState {
//
//   boolean isInvulnerable, opponentIsInvulnerable;
//   boolean isParried, opponentIsParried;
//   boolean opponentIsAttacking;
//   FighterSide opponentSide;
//   boolean inAttackRange;
//
//   public GameState(Fighter self, Fighter opponent) {
//
//     isInvulnerable = self.isInvulnerable;
//     opponentIsInvulnerable = opponent.isInvulnerable;
//
//     isParried = self.actionState == ActionState.parried;
//     opponentIsParried = opponent.actionState == ActionState.parried;
//
//     opponentIsAttacking = opponent.actionState == ActionState.preAttacking || opponent.actionState == ActionState.attacking;
//
//     opponentSide = opponent.side;
//
//     inAttackRange = (self.side == FighterSide.left) 
//       ?  (self.posX + self.width + self.attackReach) >= opponent.posX
//       :  (self.posX - self.attackReach) <= (opponent.posX + opponent.width);
//   }
//
//
//   public boolean equals(Object object) {
//     if (object instanceof GameState) return object.hashCode() == hashCode();
//     else return false;
//   }
//
//
//   public int hashCode() {
//     return Objects.hash(isInvulnerable, opponentIsInvulnerable, isParried, opponentIsParried, opponentSide, inAttackRange);
//   }
// }

public class GameState {

  boolean isInvulnerable, opponentIsInvulnerable;
  ActionState opponentState;
  FighterSide opponentSide;
  boolean inAttackRange;

  public GameState(Fighter self, Fighter opponent) {

    opponentIsInvulnerable = opponent.isInvulnerable;
    opponentState = opponent.actionState;
    opponentSide = opponent.side;

    inAttackRange = (self.side == FighterSide.left) 
      ?  (self.posX + self.width + self.attackReach) >= opponent.posX
      :  (self.posX - self.attackReach) <= (opponent.posX + opponent.width);
  }


  public boolean equals(Object object) {
    if (object instanceof GameState) return object.hashCode() == hashCode();
    else return false;
  }


  public int hashCode() {
    return Objects.hash(
        opponentIsInvulnerable,
        opponentState, opponentSide,
        inAttackRange
    );
  }
}
