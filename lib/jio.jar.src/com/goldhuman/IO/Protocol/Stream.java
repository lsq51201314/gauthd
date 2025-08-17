package com.goldhuman.IO.Protocol;

import com.goldhuman.Common.Marshal.OctetsStream;

public final class Stream extends OctetsStream {
  protected Session session;
  
  protected boolean check_policy = true;
  
  protected int checked_size = 0;
  
  protected Stream(Session paramSession) {
    this.session = paramSession;
  }
  
  protected Stream(Session paramSession, int paramInt) {
    super(paramInt);
    this.session = paramSession;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Stream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */