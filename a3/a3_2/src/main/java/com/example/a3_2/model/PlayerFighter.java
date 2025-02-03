package com.example.a3_2.model;

import java.util.HashMap;
import java.util.Map;

import com.example.a3_2.Controller.LeftPlayerKey;
import com.example.a3_2.Controller.RightPlayerKey;

public class PlayerFighter extends Fighter {

  public Map<Object, ActionState> keyActionMap;

  public PlayerFighter(FaceDirection directionFacing, double viewWidth, double viewHeight) {
    
    super(directionFacing, viewWidth, viewHeight);

    // use different key mappings in case of PvP, and map keys to fighter actions
    keyActionMap = new HashMap<>();
    Object[] keys = (directionFacing == FaceDirection.left) ? RightPlayerKey.values() : LeftPlayerKey.values();
    for (int i = 0; i < ActionState.values().length - 1; i++) keyActionMap.put(keys[i], ActionState.values()[i]);
  }
}
