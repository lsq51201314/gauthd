package com.goldhuman.Common;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class TimerObserver extends Observable {
  private static TimerObserver instance = new TimerObserver();
  
  private static long now = System.currentTimeMillis();
  
  private Timer timer = new Timer();
  
  public static TimerObserver GetInstance() {
    return instance;
  }
  
  public TimerObserver() {
    this.timer.schedule(new TimerTask() {
          public void run() {
            TimerObserver.now = scheduledExecutionTime();
            TimerObserver.instance.setChanged();
            TimerObserver.instance.notifyObservers();
          }
        },  0L, 1000L);
  }
  
  public void StopTimer() {
    this.timer.cancel();
  }
  
  public static class WatchDog {
    private long t = TimerObserver.now;
    
    public long GetTime() {
      return TimerObserver.now;
    }
    
    public long Elapse() {
      return TimerObserver.now - this.t;
    }
    
    public void Reset() {
      this.t = TimerObserver.now;
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\TimerObserver.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */