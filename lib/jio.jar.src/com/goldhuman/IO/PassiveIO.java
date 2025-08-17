package com.goldhuman.IO;

import com.goldhuman.Common.Conf;
import com.goldhuman.IO.NetIO.NetSession;
import com.goldhuman.IO.NetIO.StreamIO;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class PassiveIO extends PollIO {
  boolean closing;
  
  NetSession assoc_session;
  
  protected int UpdateEvent() {
    return this.closing ? -1 : 16;
  }
  
  protected void PollAccept() {
    try {
      SocketChannel socketChannel = ((ServerSocketChannel)this.channel).accept();
      if (socketChannel != null)
        register((PollIO)new StreamIO(socketChannel, (NetSession)this.assoc_session.clone())); 
    } catch (Exception exception) {}
  }
  
  private PassiveIO(ServerSocketChannel paramServerSocketChannel, NetSession paramNetSession) {
    super(paramServerSocketChannel);
    (this.assoc_session = paramNetSession).LoadConfig();
    this.closing = false;
  }
  
  public boolean Close() {
    return this.closing = true;
  }
  
  public static PassiveIO Open(NetSession paramNetSession) {
    Conf conf = Conf.GetInstance();
    String str1 = paramNetSession.Identification();
    String str2 = conf.find(str1, "type");
    if (str2.compareToIgnoreCase("tcp") == 0)
      try {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = null;
        try {
          inetSocketAddress = new InetSocketAddress(InetAddress.getByName(conf.find(str1, "address")), Integer.parseInt(conf.find(str1, "port")));
        } catch (Exception exception) {}
        ServerSocket serverSocket = serverSocketChannel.socket();
        try {
          serverSocket.setReuseAddress(true);
          serverSocket.setReceiveBufferSize(Integer.parseInt(conf.find(str1, "so_rcvbuf")));
        } catch (Exception exception) {}
        serverSocket.bind(paramNetSession.OnCheckAddress(inetSocketAddress), Integer.parseInt(conf.find(str1, "listen_backlog")));
        return (PassiveIO)register(new PassiveIO(serverSocketChannel, paramNetSession));
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
    return null;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\PassiveIO.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */