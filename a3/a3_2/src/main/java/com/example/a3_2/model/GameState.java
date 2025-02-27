package com.example.a3_2.model;

import java.util.Objects;
import com.example.a3_2.model.Fighter.ActionState;
import com.example.a3_2.model.Fighter.FighterSide;

public class GameState {

  public boolean isInvincible;
  public boolean opponentIsInvincible;
  // ActionState opponentState;
  public boolean opponentIsAttacking;
  public boolean opponentIsVulnerable;
  public FighterSide opponentSide;
  public boolean inAttackRange;



  public GameState(Fighter self, Fighter opponent) {

    isInvincible = self.isInvincible;
    opponentIsInvincible = opponent.isInvincible;

    opponentIsAttacking =
      opponent.actionState == ActionState.preAttacking ||
      opponent.actionState == ActionState.attacking;
    
    opponentIsVulnerable = !(
        opponent.actionState == ActionState.preBlocking ||
        opponent.actionState==ActionState.blocking || 
        opponent.actionState==ActionState.deflecting);

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
        // isInvincible, opponentIsInvincible,
        // opponentState, opponentSide,
        // inAttackRange
        isInvincible, opponentIsInvincible, opponentIsAttacking, opponentIsVulnerable, opponentSide, inAttackRange
    );
  }
}
