package com.goldhuman.IO.NetIO;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

public class StreamIO extends NetIO {
  protected void PollIn() {
    try {
      ByteBuffer byteBuffer = this.session.ibuffer.getByteBuffer(this.session.ibuffer.size(), this.session.ibuffer.capacity() - this.session.ibuffer.size());
      if (((SocketChannel)this.channel).read(byteBuffer) > 0) {
        this.session.ibuffer.resize(byteBuffer.position());
        return;
      } 
    } catch (Exception exception) {}
    this.session.obuffer.clear();
    this.session.closing = true;
  }
  
  protected void PollOut() {
    try {
      ByteBuffer byteBuffer = this.session.obuffer.getByteBuffer(0, this.session.obuffer.size());
      if (((SocketChannel)this.channel).write(byteBuffer) > 0) {
        this.session.obuffer.erase(0, byteBuffer.position());
        return;
      } 
    } catch (Exception exception) {}
    this.session.obuffer.clear();
    this.session.closing = true;
  }
  
  protected int UpdateEvent() {
    int i = 0;
    synchronized (this.session) {
      if (this.session.ibuffer.size() > 0)
        this.session.OnRecv(); 
      if (!this.session.closing)
        this.session.OnSend(); 
      if (this.session.obuffer.size() > 0)
        i = 4; 
      if (this.session.closing) {
        try {
          this.channel.close();
        } catch (Exception exception) {}
        return -1;
      } 
      if (this.session.ibuffer.size() < this.session.ibuffer.capacity())
        i |= 0x1; 
    } 
    return i;
  }
  
  public StreamIO(SelectableChannel paramSelectableChannel, NetSession paramNetSession) {
    super(paramSelectableChannel, paramNetSession);
    paramNetSession.OnOpen();
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\NetIO\StreamIO.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */