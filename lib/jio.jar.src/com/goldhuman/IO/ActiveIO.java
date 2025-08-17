package com.goldhuman.IO;

import com.goldhuman.Common.Conf;
import com.goldhuman.IO.NetIO.NetSession;
import com.goldhuman.IO.NetIO.StreamIO;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class ActiveIO extends PollIO {
  boolean closing;
  
  NetSession assoc_session;
  
  protected int UpdateEvent() {
    return this.closing ? -1 : 8;
  }
  
  protected void PollConnect() {
    this.closing = true;
  }
  
  private ActiveIO(SocketChannel paramSocketChannel, NetSession paramNetSession) {
    super(paramSocketChannel);
    (this.assoc_session = paramNetSession).LoadConfig();
    this.closing = false;
    PollIO.WakeUp();
  }
  
  public boolean Close() {
    boolean bool = true;
    try {
      SocketChannel socketChannel = (SocketChannel)this.channel;
      if (socketChannel.finishConnect()) {
        bool = false;
        register((PollIO)new StreamIO(socketChannel, (NetSession)this.assoc_session.clone()));
        PollIO.WakeUp();
        return false;
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println("assoc_session = " + this.assoc_session + " activeio = " + this);
    } 
    try {
      this.assoc_session.OnAbort();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println("assoc_session = " + this.assoc_session + " activeio = " + this);
      return bool;
    } 
    return true;
  }
  
  public static ActiveIO Open(NetSession paramNetSession) {
    Conf conf = Conf.GetInstance();
    String str1 = paramNetSession.Identification();
    String str2 = conf.find(str1, "type");
    if (str2.compareToIgnoreCase("tcp") == 0)
      try {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = null;
        try {
          inetSocketAddress = new InetSocketAddress(InetAddress.getByName(conf.find(str1, "address")), Integer.parseInt(conf.find(str1, "port")));
        } catch (Exception exception) {}
        Socket socket = socketChannel.socket();
        try {
          socket.setReceiveBufferSize(Integer.parseInt(conf.find(str1, "so_rcvbuf")));
        } catch (Exception exception) {}
        try {
          socket.setSendBufferSize(Integer.parseInt(conf.find(str1, "so_sndbuf")));
        } catch (Exception exception) {}
        try {
          if (Integer.parseInt(conf.find(str1, "tcp_nodelay")) != 0)
            socket.setTcpNoDelay(true); 
        } catch (Exception exception) {}
        socketChannel.connect(paramNetSession.OnCheckAddress(inetSocketAddress));
        return (ActiveIO)register(new ActiveIO(socketChannel, (NetSession)paramNetSession.clone()));
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
    return null;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\ActiveIO.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */