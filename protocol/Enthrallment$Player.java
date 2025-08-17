package protocol;

import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.Session;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class Player implements Serializable {
  byte[] loginbytes = null;
  
  int total_play_time = 0;
  
  int total_rest_time = 0;
  
  int sid_timeout = 0;
  
  int sid_timer = 0;
  
  final byte[] indent;
  
  void logoutByZoneid(int paramInt) {
    Logins logins = new Logins(this.loginbytes);
    if (logins.logoutByZoneid(paramInt)) {
      this.loginbytes = logins.getBytes();
      update(logins);
    } 
  }
  
  Logins getLogins() {
    return new Logins(this.loginbytes);
  }
  
  void login(int paramInt1, int paramInt2) {
    Logins logins = new Logins(this.loginbytes);
    logins.login(paramInt1, paramInt2);
    this.loginbytes = logins.getBytes();
    update(logins);
  }
  
  void logout(int paramInt1, int paramInt2) {
    Logins logins = new Logins(this.loginbytes);
    if (logins.logout(paramInt1, paramInt2)) {
      this.loginbytes = logins.getBytes();
      update(logins);
    } 
  }
  
  void onTimeout(int paramInt, boolean paramBoolean) {
    Logins logins = new Logins(this.loginbytes);
    if (paramInt == this.sid_timeout) {
      println("           onTimeout sid=" + this.sid_timeout);
      if (logins.update_rest())
        this.loginbytes = logins.getBytes(); 
      update(logins);
    } else if (paramInt == this.sid_timer) {
      println("           onAlert sid=" + this.sid_timer);
      Enthrallment enthrallment = Enthrallment.access$000();
      int i = (Enthrallment.access$200(enthrallment)).config.playtime2state(logins.getPlaytime(), (logins.size() == 0));
      if (false == paramBoolean || i == 3)
        startAlert((Enthrallment.access$200(enthrallment)).config.getStateAlertTime(i)); 
    } 
  }
  
  private void update(Logins paramLogins) {
    Enthrallment enthrallment = Enthrallment.access$000();
    int i = paramLogins.getPlaytime();
    int j = (Enthrallment.access$200(enthrallment)).config.playtime2state(i, (paramLogins.size() == 0));
    if (enthrallment.getAlert())
      startAlert((Enthrallment.access$200(enthrallment)).config.getStateAlertTime(j)); 
    if (j > 0) {
      int k = ((Enthrallment.access$200(enthrallment)).config.getPlayingStateTime(j) - i) / paramLogins.size() + 1;
      startTimeout(k);
      sendAddictionControl(i, j, paramLogins);
      return;
    } 
  }
  
  void sendAddictionControl(int paramInt1, int paramInt2, Logins paramLogins) {
    println("[AC] state=" + paramInt2 + " - ");
    Enthrallment enthrallment = Enthrallment.access$000();
    AddictionControl addictionControl = (AddictionControl)Protocol.Create("ADDICTIONCONTROL");
    addictionControl.rate = paramInt2 - 1;
    GPair gPair1 = new GPair();
    gPair1.key = 1;
    gPair1.value = paramInt1 / 1000;
    addictionControl.data.add(gPair1);
    long l = System.currentTimeMillis();
    int i = ((Enthrallment.access$200(enthrallment)).config.getPlay100Time() - paramInt1) / paramLogins.size();
    GPair gPair2 = new GPair();
    gPair2.key = 2;
    gPair2.value = (int)((l + i) / 1000L);
    addictionControl.data.add(gPair2);
    int j = ((Enthrallment.access$200(enthrallment)).config.getPlay100Time() + (Enthrallment.access$200(enthrallment)).config.getPlay50Time() - paramInt1) / paramLogins.size();
    GPair gPair3 = new GPair();
    gPair3.key = 3;
    gPair3.value = (int)((l + j) / 1000L);
    addictionControl.data.add(gPair3);
    GAuthServer.GetLog().info("sendAddictionControl, state=" + addictionControl.rate + " playtime=" + paramInt1 + " resttime=" + this.total_rest_time + " logins=" + paramLogins);
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    for (Enthrallment.Login login : paramLogins.data.values()) {
      Session session = gAuthServer.GetSessionbyZoneid(Integer.valueOf(login.zoneid));
      if (null != session) {
        addictionControl.zoneid = login.zoneid;
        addictionControl.userid = login.userid;
        try {
          gAuthServer.Send(session, addictionControl);
        } catch (Exception exception) {
          GAuthServer.GetLog().info("sendAddictionControl, Send Error, login=" + login + " e=" + exception);
        } 
        continue;
      } 
      GAuthServer.GetLog().info("sendAddictionControl, Session Not Found, login=" + login);
    } 
  }
  
  private boolean startTimeout(int paramInt) {
    if (paramInt > 0) {
      Enthrallment enthrallment = Enthrallment.access$000();
      this.sid_timeout = (Enthrallment.access$200(enthrallment)).timeout.setup(this, paramInt);
      println("start Timeout delay=" + paramInt);
      return true;
    } 
    this.sid_timeout = 0;
    return false;
  }
  
  private boolean startAlert(int paramInt) {
    if (paramInt > 0) {
      Enthrallment enthrallment = Enthrallment.access$000();
      this.sid_timer = (Enthrallment.access$200(enthrallment)).timeout.setup(this, paramInt);
      println("start Alert period=" + paramInt);
      return true;
    } 
    this.sid_timer = 0;
    return false;
  }
  
  void cancel() {
    this.sid_timeout = 0;
    this.sid_timer = 0;
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.indent);
  }
  
  public boolean equals(Object paramObject) {
    return Arrays.equals(this.indent, ((Player)paramObject).indent);
  }
  
  Player(String paramString, int paramInt1, int paramInt2) {
    try {
      StringBuilder stringBuilder = new StringBuilder(128);
      if (null != paramString) {
        stringBuilder.append('i').append(paramString);
      } else {
        stringBuilder.append('u').append(paramInt1);
      } 
      stringBuilder.append('g').append(paramInt2);
      this.indent = stringBuilder.toString().getBytes("utf8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    } 
  }
  
  void println(String paramString) {
    if (Enthrallment.access$300(Enthrallment.access$000()))
      GAuthServer.GetLog().info("Enthrallment," + paramString + "," + this); 
  }
  
  public String toString() {
    try {
      Logins logins = new Logins(this.loginbytes);
      int i = logins.getPlaytime();
      int j = (Enthrallment.access$200(Enthrallment.access$000())).config.playtime2state(i, (logins.size() == 0));
      return new String(this.indent, "utf8") + " state=" + j + " playtime=" + i + " resttime=" + this.total_rest_time + " sidtimeout=" + this.sid_timeout + " sidtimer=" + this.sid_timer + " logins=" + logins;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    } 
  }
  
  class Logins {
    Map<Enthrallment.Login, Enthrallment.Login> data = new HashMap<Enthrallment.Login, Enthrallment.Login>();
    
    long rest_begin_time = System.currentTimeMillis();
    
    int size() {
      return this.data.size();
    }
    
    boolean update_rest() {
      if (this.data.isEmpty()) {
        Enthrallment.Player.this.println("   rest update ...");
        long l = System.currentTimeMillis();
        Enthrallment.Player.this.total_rest_time = _safeadd(Enthrallment.Player.this.total_rest_time, l - this.rest_begin_time, "update_rest");
        this.rest_begin_time = l;
        return true;
      } 
      return false;
    }
    
    boolean login(int param2Int1, int param2Int2) {
      Enthrallment.Login login = new Enthrallment.Login(param2Int1, param2Int2);
      if (!this.data.containsKey(login)) {
        update_rest();
        this.data.put(login, login);
        return true;
      } 
      return false;
    }
    
    private int _safeadd(int param2Int, long param2Long, String param2String) {
      return (param2Long < 0L) ? param2Int : ((param2Long >= (Integer.MAX_VALUE - param2Int)) ? Integer.MAX_VALUE : (param2Int + (int)param2Long));
    }
    
    boolean logout(int param2Int1, int param2Int2) {
      Enthrallment.Login login = this.data.remove(new Enthrallment.Login(param2Int1, param2Int2));
      if (null == login)
        return false; 
      long l = System.currentTimeMillis();
      if (this.data.isEmpty()) {
        Enthrallment.Player.this.println("   rest begin ...");
        this.rest_begin_time = l;
      } 
      Enthrallment.Player.this.total_play_time = _safeadd(Enthrallment.Player.this.total_play_time, l - login.time, "logout");
      return true;
    }
    
    boolean logoutByZoneid(int param2Int) {
      ArrayList<Integer> arrayList = new ArrayList();
      for (Enthrallment.Login login : this.data.keySet()) {
        if (login.zoneid == param2Int)
          arrayList.add(Integer.valueOf(login.userid)); 
      } 
      if (arrayList.isEmpty())
        return false; 
      for (Integer integer : arrayList)
        logout(integer.intValue(), param2Int); 
      return true;
    }
    
    int getPlaytime() {
      int i = Enthrallment.Player.this.total_play_time;
      long l = System.currentTimeMillis();
      for (Enthrallment.Login login : this.data.values())
        i = _safeadd(i, l - login.time, "getPlayTime"); 
      return i;
    }
    
    Logins(byte[] param2ArrayOfbyte) {
      if (null == param2ArrayOfbyte)
        return; 
      ByteBuffer byteBuffer = ByteBuffer.wrap(param2ArrayOfbyte);
      int i = byteBuffer.getInt();
      if (i == 0) {
        this.rest_begin_time = byteBuffer.getLong();
      } else {
        for (byte b = 0; b < i; b++) {
          Enthrallment.Login login = new Enthrallment.Login(byteBuffer);
          this.data.put(login, login);
        } 
      } 
    }
    
    byte[] getBytes() {
      int i = this.data.size();
      ByteBuffer byteBuffer = ByteBuffer.allocate(16 * i + 12);
      byteBuffer.putInt(i);
      if (0 == i) {
        byteBuffer.putLong(this.rest_begin_time);
      } else {
        for (Enthrallment.Login login : this.data.values())
          login.marshal(byteBuffer); 
      } 
      byte[] arrayOfByte = new byte[byteBuffer.position()];
      System.arraycopy(byteBuffer.array(), 0, arrayOfByte, 0, byteBuffer.position());
      return arrayOfByte;
    }
    
    public String toString() {
      return this.data.values().toString() + " restbegin=" + this.rest_begin_time;
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$Player.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */