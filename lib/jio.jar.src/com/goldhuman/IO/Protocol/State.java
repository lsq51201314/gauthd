package com.goldhuman.IO.Protocol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class State {
  private static final Map map = new HashMap<Object, Object>();
  
  private Set set = new HashSet();
  
  private long timeout;
  
  protected State(long paramLong) {
    this.timeout = paramLong;
  }
  
  protected State(State paramState) {
    try {
      this.set = (HashSet)((HashSet)paramState.set).clone();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("in state");
      System.exit(0);
    } 
    this.timeout = paramState.timeout;
  }
  
  protected void AddProtocolType(String paramString) {
    this.set.add(paramString);
  }
  
  protected boolean TypePolicy(int paramInt) {
    return this.set.contains(Integer.toString(paramInt));
  }
  
  protected boolean TimePolicy(long paramLong) {
    return (this.timeout < 0L || paramLong < this.timeout);
  }
  
  public static State Get(String paramString) {
    return (State)map.get(paramString.toUpperCase());
  }
  
  static {
    try {
      Parser.ParseState(map);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\State.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */