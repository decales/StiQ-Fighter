package com.example.a3_2.model;

import java.util.Arrays;
import java.util.List;

public class Model {

  private List<PublishSubscribe> subscribers;
  private double viewWidth, viewHeight;
  private GameData gameData;


  public Model(double viewWidth, double viewHeight) {

    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
    initialize();
  }

  public void initialize() {

    gameData = new GameData(viewWidth, viewHeight);
  }


  public void addSubscribers(PublishSubscribe... subscribers) {
    if (this.subscribers == null) this.subscribers = Arrays.asList(subscribers);
    else this.subscribers.addAll(Arrays.asList(subscribers));
    updateSubscribers();
  }


  private void updateSubscribers() {
    for (PublishSubscribe subscriber : subscribers) {
      subscriber.update(gameData);
    }
  }
}
