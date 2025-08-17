package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Runnable;

public class ReconnectTask extends Runnable {
  public Manager manager;
  
  public ReconnectTask(Manager paramManager, int paramInt) {
    super(paramInt);
    this.manager = paramManager;
  }
  
  public void run() {
    Protocol.Client(this.manager);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\ReconnectTask.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */