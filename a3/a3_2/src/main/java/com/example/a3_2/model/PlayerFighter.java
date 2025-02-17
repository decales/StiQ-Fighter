package com.example.a3_2.model;

import java.util.HashMap;
import java.util.Map;

import com.example.a3_2.controller.Controller.LeftPlayerKey;
import com.example.a3_2.controller.Controller.RightPlayerKey;


public class PlayerFighter extends Fighter {

  public Map<Object, Action> keyActionMap;

  public PlayerFighter(FighterSide side, double viewSize, int frameRate) {
    
    super(side, viewSize, frameRate);

    // use different key mappings in case of PvP, and map keys to fighter actions
    keyActionMap = new HashMap<>();
    Object[] keys = (side == FighterSide.left) ? LeftPlayerKey.values() : RightPlayerKey.values();
    for (int i = 0; i < keys.length; i++) keyActionMap.put(keys[i], Action.values()[i]);
  }
}
