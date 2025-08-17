package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Marshal.OctetsStream;
import java.util.Iterator;
import java.util.Vector;

public final class ProtocolBinder extends Protocol {
  private Vector binder = new Vector();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    synchronized (this.binder) {
      Iterator<Protocol> iterator = this.binder.iterator();
      while (iterator.hasNext())
        ((Protocol)iterator.next()).Encode(paramOctetsStream); 
    } 
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) {
    Stream stream = (Stream)paramOctetsStream;
    synchronized (this.binder) {
      while (true) {
        try {
          Protocol protocol;
          if ((protocol = Protocol.Decode(stream)) != null) {
            this.binder.add(protocol);
            continue;
          } 
          Iterator<Protocol> iterator = this.binder.iterator();
          while (iterator.hasNext())
            Task.Dispatch(stream.session.manager, stream.session, iterator.next()); 
        } catch (Exception exception) {
          stream.session.Close();
        } 
        break;
      } 
    } 
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      ProtocolBinder protocolBinder = (ProtocolBinder)super.clone();
      synchronized (this.binder) {
        Iterator<Protocol> iterator = this.binder.iterator();
        while (iterator.hasNext())
          protocolBinder.binder.add(((Protocol)iterator.next()).clone()); 
        return protocolBinder;
      } 
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {}
  
  ProtocolBinder bind(Protocol paramProtocol) {
    synchronized (this.binder) {
      this.binder.add(paramProtocol);
    } 
    return this;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\ProtocolBinder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */