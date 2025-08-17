package com.goldhuman.IO.NetIO;

import com.goldhuman.Common.Conf;
import com.goldhuman.Common.Octets;
import com.goldhuman.Common.Security.Security;
import java.net.SocketAddress;

public abstract class NetSession implements Cloneable {
  private static final int DEFAULTIOBUF = 8192;
  
  protected Octets ibuffer = new Octets(8192);
  
  protected Octets obuffer = new Octets(8192);
  
  protected Octets isecbuf = new Octets(8192);
  
  Security isec = Security.Create("NULLSECURITY");
  
  Security osec = Security.Create("NULLSECURITY");
  
  protected boolean closing = false;
  
  protected boolean Output(Octets paramOctets) {
    if (paramOctets.size() + this.obuffer.size() > this.obuffer.capacity())
      return false; 
    this.osec.Update(paramOctets);
    this.obuffer.insert(this.obuffer.size(), paramOctets);
    return true;
  }
  
  protected Octets Input() {
    this.isec.Update(this.ibuffer);
    this.isecbuf.insert(this.isecbuf.size(), this.ibuffer);
    this.ibuffer.clear();
    return this.isecbuf;
  }
  
  public void SetISecurity(String paramString, Octets paramOctets) {
    synchronized (this) {
      this.isec = Security.Create(paramString);
      this.isec.SetParameter(paramOctets);
    } 
  }
  
  public void SetOSecurity(String paramString, Octets paramOctets) {
    synchronized (this) {
      this.osec = Security.Create(paramString);
      this.osec.SetParameter(paramOctets);
    } 
  }
  
  public void LoadConfig() {
    Conf conf = Conf.GetInstance();
    String str = Identification();
    try {
      this.ibuffer.reserve(Integer.parseInt(conf.find(str, "ibuffermax")));
    } catch (Exception exception) {}
    try {
      this.obuffer.reserve(Integer.parseInt(conf.find(str, "obuffermax")));
    } catch (Exception exception) {}
    try {
      SetISecurity(conf.find(str, "isec").trim(), new Octets(conf.find(str, "iseckey").getBytes()));
    } catch (Exception exception) {}
    try {
      SetOSecurity(conf.find(str, "osec").trim(), new Octets(conf.find(str, "oseckey").getBytes()));
    } catch (Exception exception) {}
  }
  
  protected void Close() {
    this.closing = true;
  }
  
  protected abstract void OnRecv();
  
  protected abstract void OnSend();
  
  protected abstract void OnOpen();
  
  protected abstract void OnClose();
  
  public void OnAbort() {}
  
  public abstract String Identification();
  
  public SocketAddress OnCheckAddress(SocketAddress paramSocketAddress) {
    return paramSocketAddress;
  }
  
  public Object clone() {
    try {
      NetSession netSession = (NetSession)super.clone();
      netSession.ibuffer = new Octets(this.ibuffer.capacity());
      netSession.obuffer = new Octets(this.obuffer.capacity());
      netSession.isecbuf = new Octets(this.isecbuf.capacity());
      netSession.isec = (Security)this.isec.clone();
      netSession.osec = (Security)this.osec.clone();
      return netSession;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\NetIO\NetSession.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */