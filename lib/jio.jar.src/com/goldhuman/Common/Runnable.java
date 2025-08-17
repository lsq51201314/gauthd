package com.goldhuman.Common;

public abstract class Runnable implements java.lang.Runnable {
  private int priority = 0;
  
  public Runnable() {}
  
  public Runnable(int paramInt) {}
  
  public int GetPriority() {
    return this.priority;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Runnable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */