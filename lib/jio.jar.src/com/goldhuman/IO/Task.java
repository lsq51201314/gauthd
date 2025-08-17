package com.goldhuman.IO;

import com.goldhuman.Common.Runnable;
import com.goldhuman.Common.ThreadPool;

class Task extends Runnable {
  protected Task() {
    super(1);
  }
  
  public void run() {
    PollIO.Poll(1000L);
    ThreadPool.AddTask(this);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Task.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */