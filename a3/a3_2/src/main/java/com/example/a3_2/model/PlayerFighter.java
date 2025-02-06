package com.example.a3_2.model;

import java.util.HashMap;
import java.util.Map;

import com.example.a3_2.controller.Controller.LeftPlayerKey;
import com.example.a3_2.controller.Controller.RightPlayerKey;


public class PlayerFighter extends Fighter {

  public Map<Object, ActionState> keyActionMap;

  public PlayerFighter(FighterSide side, double viewWidth, double viewHeight) {
    
    super(side, viewWidth, viewHeight);

    // use different key mappings in case of PvP, and map keys to fighter actions
    keyActionMap = new HashMap<>();
    Object[] keys = (side == FighterSide.left) ? LeftPlayerKey.values() : RightPlayerKey.values();
    for (int i = 0; i < ActionState.values().length - 1; i++) keyActionMap.put(keys[i], ActionState.values()[i]);
  }
}
