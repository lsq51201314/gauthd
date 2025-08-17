package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Marshal.Marshal;
import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.TimerObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class Rpc extends Protocol {
  private static Map map = Collections.synchronizedMap(new HashMap<Object, Object>());
  
  private static HouseKeeper housekeeper = new HouseKeeper();
  
  private XID xid = new XID();
  
  private TimerObserver.WatchDog timer = new TimerObserver.WatchDog();
  
  protected Data argument;
  
  protected Data result;
  
  protected long time_policy;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.xid);
    paramOctetsStream.marshal(this.xid.IsRequest() ? this.argument : this.result);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    paramOctetsStream.unmarshal(this.xid);
    if (this.xid.IsRequest())
      return paramOctetsStream.unmarshal(this.argument); 
    Rpc rpc = (Rpc)map.get(this.xid);
    if (rpc != null)
      paramOctetsStream.unmarshal(rpc.result); 
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      Rpc rpc = (Rpc)super.clone();
      rpc.xid = (XID)this.xid.clone();
      rpc.timer = new TimerObserver.WatchDog();
      rpc.argument = (Data)this.argument.clone();
      rpc.result = (Data)this.result.clone();
      return rpc;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {
    if (this.xid.IsRequest()) {
      Server(this.argument, this.result, paramManager, paramSession);
      this.xid.ClrRequest();
      paramManager.Send(paramSession, this);
      return;
    } 
    Rpc rpc = (Rpc)map.remove(this.xid);
    if (rpc != null)
      rpc.Client(rpc.argument, rpc.result); 
  }
  
  protected void Server(Data paramData1, Data paramData2) throws ProtocolException {}
  
  protected void Server(Data paramData1, Data paramData2, Manager paramManager, Session paramSession) throws ProtocolException {
    Server(paramData1, paramData2);
  }
  
  protected void Client(Data paramData1, Data paramData2) throws ProtocolException {}
  
  protected void OnTimeout() {}
  
  private static Rpc Call(Rpc paramRpc, Data paramData) {
    paramRpc.xid.SetRequest();
    paramRpc.argument = paramData;
    map.put(paramRpc.xid, paramRpc);
    return paramRpc;
  }
  
  public static Rpc Call(int paramInt, Data paramData) {
    return Call((Rpc)Protocol.Create(paramInt), paramData);
  }
  
  public static Rpc Call(String paramString, Data paramData) {
    return Call((Rpc)Protocol.Create(paramString), paramData);
  }
  
  public static abstract class Data implements Marshal, Cloneable {
    protected Object clone() {
      try {
        return super.clone();
      } catch (Exception exception) {
        return null;
      } 
    }
    
    public static class DataVector extends Vector implements Marshal, Cloneable {
      protected Rpc.Data stub;
      
      private DataVector() {}
      
      public Object clone() {
        try {
          DataVector dataVector = (DataVector)super.clone();
          dataVector.stub = (Rpc.Data)this.stub.clone();
          return dataVector;
        } catch (Exception exception) {
          return null;
        } 
      }
      
      public DataVector(Rpc.Data param2Data) {
        this.stub = param2Data;
      }
      
      public OctetsStream marshal(OctetsStream param2OctetsStream) {
        param2OctetsStream.compact_uint32(size());
        for (byte b = 0; b < size(); b++)
          ((Marshal)get(b)).marshal(param2OctetsStream); 
        return param2OctetsStream;
      }
      
      public OctetsStream unmarshal(OctetsStream param2OctetsStream) throws MarshalException {
        int i = param2OctetsStream.uncompact_uint32();
        for (byte b = 0; b < i; b++) {
          Rpc.Data data = (Rpc.Data)this.stub.clone();
          data.unmarshal(param2OctetsStream);
          add((E)data);
        } 
        return param2OctetsStream;
      }
    }
  }
  
  private static class XID implements Marshal, Cloneable {
    public int count = 0;
    
    private boolean is_request = true;
    
    private static int xid_count = 0;
    
    private static Object xid_locker = new Object();
    
    private XID() {}
    
    public OctetsStream marshal(OctetsStream param1OctetsStream) {
      return param1OctetsStream.marshal(this.is_request ? (this.count | Integer.MIN_VALUE) : (this.count & Integer.MAX_VALUE));
    }
    
    public OctetsStream unmarshal(OctetsStream param1OctetsStream) throws MarshalException {
      this.count = param1OctetsStream.unmarshal_int();
      this.is_request = ((this.count & Integer.MIN_VALUE) != 0);
      return param1OctetsStream;
    }
    
    public boolean IsRequest() {
      return this.is_request;
    }
    
    public void ClrRequest() {
      this.is_request = false;
    }
    
    public void SetRequest() {
      this.is_request = true;
      synchronized (xid_locker) {
        this.count = xid_count++;
      } 
    }
    
    public Object clone() {
      try {
        return super.clone();
      } catch (Exception exception) {
        exception.printStackTrace();
        return null;
      } 
    }
    
    public boolean equals(Object param1Object) {
      return ((((XID)param1Object).count & Integer.MAX_VALUE) == (this.count & Integer.MAX_VALUE));
    }
    
    public int hashCode() {
      return this.count & Integer.MAX_VALUE;
    }
  }
  
  private static class HouseKeeper implements Observer {
    public HouseKeeper() {
      TimerObserver.GetInstance().addObserver(this);
    }
    
    public void update(Observable param1Observable, Object param1Object) {
      ArrayList<Rpc> arrayList = new ArrayList();
      synchronized (Rpc.map) {
        Iterator<Map.Entry> iterator1 = Rpc.map.entrySet().iterator();
        while (iterator1.hasNext()) {
          Rpc rpc = (Rpc)((Map.Entry)iterator1.next()).getValue();
          if (rpc.time_policy < rpc.timer.Elapse()) {
            arrayList.add(rpc);
            iterator1.remove();
          } 
        } 
      } 
      Iterator<Rpc> iterator = arrayList.iterator();
      while (iterator.hasNext())
        ((Rpc)iterator.next()).OnTimeout(); 
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Rpc.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */