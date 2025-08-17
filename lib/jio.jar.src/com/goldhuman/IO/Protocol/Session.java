package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.Common.TimerObserver;
import com.goldhuman.IO.NetIO.NetSession;
import java.net.SocketAddress;
import java.util.LinkedList;

public final class Session extends NetSession {
  protected Manager manager;
  
  protected static boolean need_wakeup = false;
  
  private State state;
  
  private Stream is;
  
  private LinkedList os;
  
  private TimerObserver.WatchDog timer;
  
  public Object clone() {
    try {
      Session session = (Session)super.clone();
      session.state = new State(this.state);
      session.is = new Stream(this);
      session.os = new LinkedList();
      session.timer = new TimerObserver.WatchDog();
      return session;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public String Identification() {
    return this.manager.Identification();
  }
  
  public SocketAddress OnCheckAddress(SocketAddress paramSocketAddress) {
    return this.manager.OnCheckAddress(paramSocketAddress);
  }
  
  protected void OnOpen() {
    this.timer.Reset();
    this.manager.AddSession(this);
  }
  
  protected void OnClose() {
    this.manager.DelSession(this);
  }
  
  public void OnAbort() {
    this.manager.AbortSession(this);
  }
  
  protected void OnRecv() {
    this.timer.Reset();
    Octets octets = Input();
    this.is.insert(this.is.size(), octets);
    octets.clear();
    try {
      Protocol protocol;
      while ((protocol = Protocol.Decode(this.is)) != null)
        Task.Dispatch(this.manager, this, protocol); 
    } catch (ProtocolException protocolException) {
      Close();
    } 
  }
  
  protected void OnSend() {
    if (this.state.TimePolicy(this.timer.Elapse())) {
      if (this.os.size() != 0) {
        do {
          OctetsStream octetsStream = this.os.getFirst();
          if (!Output((Octets)octetsStream))
            break; 
          this.os.removeFirst();
        } while (this.os.size() != 0);
        this.timer.Reset();
      } 
    } else {
      Close();
    } 
  }
  
  protected boolean Send(Protocol paramProtocol) {
    synchronized (this) {
      OctetsStream octetsStream = new OctetsStream();
      paramProtocol.Encode(octetsStream);
      if (paramProtocol.SizePolicy(octetsStream.size())) {
        this.os.addLast(octetsStream);
        need_wakeup = true;
        return true;
      } 
    } 
    return false;
  }
  
  protected boolean StatePolicy(int paramInt) {
    return this.state.TypePolicy(paramInt);
  }
  
  protected void Close() {
    this.closing = true;
  }
  
  protected void ChangeState(String paramString) {
    synchronized (this) {
      this.state = State.Get(paramString);
    } 
  }
  
  public Session(Manager paramManager) {
    this.manager = paramManager;
    this.state = this.manager.GetInitState();
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Session.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */