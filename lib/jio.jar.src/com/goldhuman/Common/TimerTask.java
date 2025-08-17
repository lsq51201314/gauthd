package com.goldhuman.Common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class TimerTask implements Observer {
  private static TimerTask instance = new TimerTask();
  
  private LinkedList tasks = new LinkedList();
  
  private long elapse = 0L;
  
  private TimerTask() {
    TimerObserver.GetInstance().addObserver(this);
  }
  
  public synchronized void update(Observable paramObservable, Object paramObject) {
    this.elapse++;
    Iterator<TaskPair> iterator = this.tasks.iterator();
    while (iterator.hasNext()) {
      TaskPair taskPair = iterator.next();
      if (taskPair.waitsecds > this.elapse)
        break; 
      ThreadPool.AddTask(taskPair.task);
      iterator.remove();
    } 
  }
  
  public synchronized void AddTask(Runnable paramRunnable, long paramLong) {
    this.tasks.add(new TaskPair(paramLong + this.elapse, paramRunnable));
  }
  
  public static void AddTimerTask(Runnable paramRunnable, long paramLong) {
    instance.AddTask(paramRunnable, paramLong);
  }
  
  private class TaskPair {
    long waitsecds;
    
    Runnable task;
    
    TaskPair(long param1Long, Runnable param1Runnable) {
      this.waitsecds = param1Long;
      this.task = param1Runnable;
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\TimerTask.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */