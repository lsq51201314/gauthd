package com.goldhuman.IO;

import com.goldhuman.Common.Runnable;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class PollIO {
  private static Selector iomap = null;
  
  private static final Runnable task = new Task();
  
  private static Object regist_locker = new Object();
  
  protected SelectableChannel channel;
  
  protected abstract int UpdateEvent();
  
  protected abstract boolean Close();
  
  protected void PollIn() {}
  
  protected void PollOut() {}
  
  protected void PollAccept() {}
  
  protected void PollConnect() {}
  
  public static PollIO register(PollIO paramPollIO) {
    synchronized (regist_locker) {
      WakeUp();
      try {
        paramPollIO.channel.register(iomap, 0, paramPollIO);
      } catch (ClosedChannelException closedChannelException) {
        closedChannelException.printStackTrace();
      } 
      return paramPollIO;
    } 
  }
  
  protected static synchronized void Poll(long paramLong) {
    try {
      ArrayList arrayList = new ArrayList();
      synchronized (regist_locker) {
        Iterator<SelectionKey> iterator = iomap.keys().iterator();
        while (iterator.hasNext())
          arrayList.add(iterator.next()); 
      } 
      for (SelectionKey selectionKey : arrayList) {
        PollIO pollIO = (PollIO)selectionKey.attachment();
        int i = pollIO.UpdateEvent();
        if (i == -1) {
          if (pollIO.Close()) {
            try {
              pollIO.channel.close();
            } catch (Exception exception) {}
            selectionKey.cancel();
          } 
          continue;
        } 
        try {
          selectionKey.interestOps(i);
        } catch (Exception exception) {}
      } 
      iomap.selectedKeys().clear();
      if (paramLong < 0L) {
        iomap.select();
      } else if (paramLong == 0L) {
        iomap.selectNow();
      } else {
        iomap.select(paramLong);
      } 
      for (SelectionKey selectionKey : iomap.selectedKeys()) {
        PollIO pollIO = (PollIO)selectionKey.attachment();
        if (selectionKey.isAcceptable())
          pollIO.PollAccept(); 
        if (selectionKey.isConnectable())
          pollIO.PollConnect(); 
        if (selectionKey.isReadable())
          pollIO.PollIn(); 
        if (selectionKey.isWritable())
          pollIO.PollOut(); 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  protected PollIO(SelectableChannel paramSelectableChannel) {
    try {
      this.channel = paramSelectableChannel;
      paramSelectableChannel.configureBlocking(false);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static Runnable GetTask() {
    return task;
  }
  
  public static void WakeUp() {
    iomap.wakeup();
  }
  
  static {
    try {
      iomap = Selector.open();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\PollIO.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */