package com.goldhuman.IO.NetIO;

import com.goldhuman.IO.PollIO;
import java.nio.channels.SelectableChannel;

public abstract class NetIO extends PollIO {
  protected NetSession session;
  
  protected NetIO(SelectableChannel paramSelectableChannel, NetSession paramNetSession) {
    super(paramSelectableChannel);
    this.session = paramNetSession;
  }
  
  protected boolean Close() {
    this.session.OnClose();
    return true;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\NetIO\NetIO.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */