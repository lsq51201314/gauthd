package com.goldhuman.Common;

import java.util.Observable;
import java.util.Observer;

public abstract class StatefulRunnable extends Observable implements Observer, TaskState {
  TaskGraph graph = null;
  
  TaskContext GetContext() {
    return this.graph.context;
  }
  
  public void Destroy() {}
  
  public void Init() {}
  
  public void update(Observable paramObservable, Object paramObject) {}
  
  public abstract int GetState();
  
  public abstract void Run();
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\StatefulRunnable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */