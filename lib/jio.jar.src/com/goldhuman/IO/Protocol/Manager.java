package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Octets;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Manager {
  private Set set = Collections.synchronizedSet(new HashSet());
  
  protected void AddSession(Session paramSession) {
    this.set.add(paramSession);
    OnAddSession(paramSession);
  }
  
  protected void DelSession(Session paramSession) {
    OnDelSession(paramSession);
    this.set.remove(paramSession);
  }
  
  protected void AbortSession(Session paramSession) {
    OnAbortSession(paramSession);
  }
  
  public boolean SetISecurity(Session paramSession, String paramString, Octets paramOctets) {
    if (!this.set.contains(paramSession))
      return false; 
    paramSession.SetISecurity(paramString, paramOctets);
    return true;
  }
  
  public boolean SetOSecurity(Session paramSession, String paramString, Octets paramOctets) {
    if (!this.set.contains(paramSession))
      return false; 
    paramSession.SetOSecurity(paramString, paramOctets);
    return true;
  }
  
  public boolean Send(Session paramSession, Protocol paramProtocol) {
    return !this.set.contains(paramSession) ? false : paramSession.Send(paramProtocol);
  }
  
  public boolean Close(Session paramSession) {
    if (!this.set.contains(paramSession))
      return false; 
    paramSession.Close();
    return true;
  }
  
  public boolean ChangeState(Session paramSession, String paramString) {
    if (!this.set.contains(paramSession))
      return false; 
    paramSession.ChangeState(paramString);
    return true;
  }
  
  protected abstract void OnAddSession(Session paramSession);
  
  protected abstract void OnDelSession(Session paramSession);
  
  protected void OnAbortSession(Session paramSession) {}
  
  protected abstract State GetInitState();
  
  protected int PriorPolicy(int paramInt) {
    return Protocol.GetStub(paramInt).PriorPolicy();
  }
  
  protected boolean InputPolicy(int paramInt1, int paramInt2) {
    return Protocol.GetStub(paramInt1).SizePolicy(paramInt2);
  }
  
  protected abstract String Identification();
  
  protected SocketAddress OnCheckAddress(SocketAddress paramSocketAddress) {
    return paramSocketAddress;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Manager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */