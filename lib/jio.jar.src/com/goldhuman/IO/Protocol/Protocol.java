package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Marshal.Marshal;
import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.Common.TimerObserver;
import com.goldhuman.IO.ActiveIO;
import com.goldhuman.IO.PassiveIO;
import java.util.HashMap;
import java.util.Map;

public abstract class Protocol implements Marshal, Cloneable {
  private static final Map map = new HashMap<Object, Object>();
  
  protected int type;
  
  protected int size_policy;
  
  protected int prior_policy;
  
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public static Protocol GetStub(String paramString) {
    return (Protocol)map.get(paramString.toUpperCase());
  }
  
  public static Protocol GetStub(int paramInt) {
    return GetStub(Integer.toString(paramInt));
  }
  
  public static Protocol Create(String paramString) {
    Protocol protocol = GetStub(paramString);
    return (protocol == null) ? null : (Protocol)protocol.clone();
  }
  
  public static Protocol Create(int paramInt) {
    return Create(Integer.toString(paramInt));
  }
  
  protected void Encode(OctetsStream paramOctetsStream) {
    paramOctetsStream.compact_uint32(this.type).marshal((Octets)(new OctetsStream()).marshal(this));
  }
  
  protected static Protocol Decode(Stream paramStream) throws ProtocolException {
    if (paramStream.eos())
      return null; 
    Protocol protocol = null;
    try {
      if (paramStream.check_policy) {
        paramStream.Begin();
        int j = paramStream.uncompact_uint32();
        int k = paramStream.uncompact_uint32();
        paramStream.Rollback();
        if (!paramStream.session.StatePolicy(j) || !paramStream.session.manager.InputPolicy(j, k)) {
          System.out.println("Protocol Decode Error:type=" + j + ",size=" + k);
          throw new ProtocolException();
        } 
        paramStream.check_policy = false;
        paramStream.checked_size = k;
      } 
      Stream stream = new Stream(paramStream.session, paramStream.checked_size);
      paramStream.Begin();
      int i = paramStream.uncompact_uint32();
      paramStream.unmarshal((Octets)stream);
      paramStream.Commit();
      if ((protocol = Create(i)) != null)
        protocol.unmarshal(stream); 
    } catch (MarshalException marshalException) {
      paramStream.Rollback();
      if (protocol != null)
        throw new ProtocolException(); 
    } 
    return protocol;
  }
  
  public static PassiveIO Server(Manager paramManager) {
    return PassiveIO.Open(new Session(paramManager));
  }
  
  public static ActiveIO Client(Manager paramManager) {
    return ActiveIO.Open(new Session(paramManager));
  }
  
  protected int PriorPolicy() {
    return this.prior_policy;
  }
  
  protected boolean SizePolicy(int paramInt) {
    return (paramInt <= 0 || paramInt < this.size_policy);
  }
  
  public abstract void Process(Manager paramManager, Session paramSession) throws ProtocolException;
  
  public static void main(String[] paramArrayOfString) {
    TimerObserver.GetInstance().StopTimer();
  }
  
  static {
    try {
      Parser.ParseProtocol(map);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    try {
      Parser.ParseRpc(map);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Protocol.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */