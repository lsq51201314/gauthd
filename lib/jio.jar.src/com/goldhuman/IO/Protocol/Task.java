package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Runnable;
import com.goldhuman.Common.ThreadPool;
import com.goldhuman.IO.PollIO;

public final class Task extends Runnable {
  private Manager manager;
  
  private Session session;
  
  private Protocol protocol;
  
  private boolean immediately = false;
  
  private Task(int paramInt, Manager paramManager, Session paramSession, Protocol paramProtocol) {
    super(paramInt);
    this.manager = paramManager;
    this.session = paramSession;
    this.protocol = paramProtocol;
  }
  
  public void run() {
    try {
      this.protocol.Process(this.manager, this.session);
      if (Session.need_wakeup && !this.immediately) {
        PollIO.WakeUp();
        Session.need_wakeup = false;
      } 
    } catch (ProtocolException protocolException) {
      this.manager.Close(this.session);
    } 
  }
  
  protected static void Dispatch(Manager paramManager, Session paramSession, Protocol paramProtocol) {
    int i = paramManager.PriorPolicy(paramProtocol.type);
    Task task = new Task(i, paramManager, paramSession, paramProtocol);
    if (i > 0) {
      ThreadPool.AddTask(task);
    } else {
      task.immediately = true;
      task.run();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Task.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */