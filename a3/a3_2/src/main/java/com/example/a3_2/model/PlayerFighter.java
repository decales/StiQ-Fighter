package com.example.a3_2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.example.a3_2.controller.Controller.LeftPlayerKey;
import com.example.a3_2.controller.Controller.RightPlayerKey;


public class PlayerFighter extends Fighter {

  public Map<Object, Action> keyActionMap;
  // private Queue<Action> actionQueue;

  public PlayerFighter(FighterSide side, double viewWidth, double viewHeight) {
    
    super(side, viewWidth, viewHeight);

    // use different key mappings in case of PvP, and map keys to fighter actions
    keyActionMap = new HashMap<>();
    Object[] keys = (side == FighterSide.left) ? LeftPlayerKey.values() : RightPlayerKey.values();
    for (int i = 0; i < keys.length; i++) keyActionMap.put(keys[i], Action.values()[i]);
  }
}
