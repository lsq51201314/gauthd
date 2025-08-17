package application.connection;

import com.goldhuman.xml.xmlobject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private static Map pools = new HashMap<Object, Object>();
  
  private String url;
  
  private String username;
  
  private String password;
  
  private int initSize;
  
  public int getInitSize() {
    return this.initSize;
  }
  
  public static Connection get(String paramString) {
    return (paramString == null) ? ((SimplePool)((Map.Entry)pools.entrySet().iterator().next()).getValue()).getConnection() : ((SimplePool)pools.get(paramString)).getConnection();
  }
  
  public static Connection get() {
    return get(null);
  }
  
  public static void put(Connection paramConnection) {
    try {
      Iterator<Map.Entry> iterator = pools.entrySet().iterator();
      while (iterator.hasNext())
        ((SimplePool)((Map.Entry)iterator.next()).getValue()).returnConnection(paramConnection); 
    } catch (Exception exception) {}
  }
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.url = paramAttributes.getValue("url");
    this.username = paramAttributes.getValue("username");
    this.password = paramAttributes.getValue("password");
    String str = paramAttributes.getValue("poolsize");
    this.initSize = (str == null) ? 10 : Integer.parseInt(str);
  }
  
  public void action() {
    try {
      if (application.handler.debug)
        System.err.print("Connect to " + this.url); 
      pools.put(this.name, new SimplePool(this.initSize, null, this.url, this.username, this.password));
      if (application.handler.debug)
        System.err.println("pool of " + this.name + " init successed"); 
      if (application.handler.debug)
        System.err.println(); 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  static {
    (new Timer()).schedule(new CheckTask(), 600000L, 600000L);
  }
  
  class SimplePool {
    private Dummy[] pool = null;
    
    private int size;
    
    String connurl;
    
    String usrname;
    
    String pwd;
    
    SimplePool(int param1Int, String param1String1, String param1String2, String param1String3, String param1String4) {
      this.size = param1Int;
      this.connurl = param1String2;
      handler.this.username = param1String3;
      this.pwd = param1String4;
      try {
        this.pool = new Dummy[this.size];
        if (param1String1 != null)
          Class.forName(param1String1); 
        for (byte b = 0; b < this.size; b++) {
          try {
            this.pool[b] = new Dummy();
            (this.pool[b]).conn = DriverManager.getConnection(this.connurl, handler.this.username, this.pwd);
            (this.pool[b]).isValid = true;
          } catch (Exception exception) {}
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    }
    
    public synchronized void doCheck() {
      for (byte b = 0; b < this.size; b++) {
        try {
          (this.pool[b]).isValid = !(this.pool[b]).conn.isClosed();
        } catch (Exception exception) {
          (this.pool[b]).isValid = false;
        } 
        if (!(this.pool[b]).isValid)
          try {
            (this.pool[b]).conn = DriverManager.getConnection(this.connurl, handler.this.username, this.pwd);
            (this.pool[b]).isValid = true;
            notifyAll();
          } catch (Exception exception) {} 
      } 
    }
    
    private Connection _getConnection() {
      for (byte b = 0; b < this.size; b++) {
        if ((this.pool[b]).isValid && !(this.pool[b]).isActive) {
          (this.pool[b]).isActive = true;
          return (this.pool[b]).conn;
        } 
      } 
      return null;
    }
    
    public synchronized Connection getConnection() {
      Connection connection = null;
      while ((connection = _getConnection()) == null) {
        try {
          wait();
        } catch (Exception exception) {}
      } 
      return connection;
    }
    
    public synchronized void returnConnection(Connection param1Connection) {
      for (byte b = 0; b < this.size; b++) {
        if ((this.pool[b]).conn == param1Connection) {
          (this.pool[b]).isActive = false;
          try {
            notifyAll();
          } catch (Exception exception) {}
          return;
        } 
      } 
    }
    
    private class Dummy {
      Connection conn;
      
      boolean isActive = false;
      
      boolean isValid = false;
      
      private Dummy() {}
    }
  }
  
  static class CheckTask extends TimerTask {
    public void run() {
      Iterator<Map.Entry> iterator = handler.pools.entrySet().iterator();
      while (iterator.hasNext()) {
        handler.SimplePool simplePool = (handler.SimplePool)((Map.Entry)iterator.next()).getValue();
        simplePool.doCheck();
      } 
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\connection\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */