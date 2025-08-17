package protocol;

import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Enthrallment implements EnthrallmentMBean, SMMBean {
  private final int ACT_LOGIN = 1;
  
  private final int ACT_LOGOUT = 2;
  
  private final int ACT_REMOVE = 3;
  
  private final int ACT_LOGOUT_BY_ZONEID = 4;
  
  private TimerTask clear_timer_task = null;
  
  private int maxtriggers;
  
  private int zerotriggers;
  
  private long totaltriggers;
  
  private long totallengths;
  
  private int triggertimes;
  
  private boolean enable = true;
  
  private boolean alert = false;
  
  private boolean debug = false;
  
  private Timer timer = new Timer("Enthrallment", true);
  
  private static final Enthrallment instance = new Enthrallment();
  
  private Data data;
  
  public static String FILENAME_ENTHRALL = "Enthrallment.data";
  
  public static Enthrallment getInstance() {
    return instance;
  }
  
  private Player makePlayer(int paramInt1, int paramInt2, int paramInt3) {
    Integer integer = this.data.config.getGameidBy(paramInt2, paramInt3);
    if (null == integer)
      return null; 
    int i = integer.intValue();
    String str = null;
    return new Player(str, paramInt1, i);
  }
  
  public void login(int paramInt1, int paramInt2, int paramInt3) {
    try {
      if ((GAuthServer.GetInstance()).enable_enthrallment && getEnable()) {
        if (this.clear_timer_task == null) {
          this.clear_timer_task = new TimerTask() {
              public void run() {
                try {
                  Enthrallment.this.clear("iamsure");
                } catch (Exception exception) {}
              }
            };
          Calendar calendar = Calendar.getInstance();
          calendar.roll(5, true);
          calendar.set(11, 0);
          calendar.set(12, 0);
          calendar.set(13, 0);
          this.timer.schedule(this.clear_timer_task, calendar.getTime(), 86400000L);
        } 
        this.timer.schedule(new DelayTask(1, paramInt1, paramInt2, paramInt3), 1000L);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void logout(int paramInt1, int paramInt2) {
    try {
      this.timer.schedule(new DelayTask(2, paramInt1, paramInt2), 1L);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void OnDelSession(int paramInt) {
    try {
      this.timer.schedule(new DelayTask(4, 0, paramInt), 1L);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void remove(int paramInt1, int paramInt2) {
    this.timer.schedule(new DelayTask(3, paramInt1, paramInt2), 1L);
  }
  
  public synchronized String find(int paramInt1, int paramInt2) {
    Player player = this.data.onlines.get(new UseridZoneid(paramInt1, paramInt2));
    return (player != null) ? player.toString() : "null";
  }
  
  public void clear(String paramString) {
    if (false == paramString.equals("iamsure"))
      throw new RuntimeException("Are you sure?"); 
    this.data.timeout.stop();
    synchronized (this) {
      this.data.reset();
    } 
  }
  
  public synchronized int getCountOnlines() {
    return this.data.onlines.size();
  }
  
  public synchronized int getCountPlayers() {
    return this.data.players.size();
  }
  
  public synchronized int getCountTimeouts() {
    return this.data.timeout.size();
  }
  
  public synchronized int getCapacityTimeouts() {
    return this.data.timeout.capacity();
  }
  
  public synchronized long getTotalTriggers() {
    return this.totaltriggers;
  }
  
  public synchronized long getTotalLengths() {
    return this.totallengths;
  }
  
  public synchronized int getTriggerTimes() {
    return this.triggertimes;
  }
  
  public synchronized int getMaxTriggers() {
    return this.maxtriggers;
  }
  
  public synchronized int getZeroTriggers() {
    return this.zerotriggers;
  }
  
  public synchronized void timeoutTrimToSize() {
    this.data.timeout.trimToSize();
  }
  
  public synchronized void dumpTimeout(String paramString) {
    if (false == paramString.equals("iamsure"))
      throw new RuntimeException("Are you sure ?"); 
    System.out.println("dumpTimeout");
    this.data.timeout.dump();
  }
  
  public synchronized void dumpPlayers(String paramString) {
    if (false == paramString.equals("iamsure"))
      throw new RuntimeException("Are you sure ?"); 
    System.out.println("dumpPlayers");
    System.out.println(this.data.players.keySet());
    System.out.println(this.data.onlines.keySet());
  }
  
  public synchronized void setEnable(boolean paramBoolean) {
    this.enable = paramBoolean;
  }
  
  public synchronized boolean getEnable() {
    return this.enable;
  }
  
  public synchronized void setAlert(boolean paramBoolean) {
    this.alert = paramBoolean;
  }
  
  public synchronized boolean getAlert() {
    return this.alert;
  }
  
  public synchronized void initialize(String paramString) throws Exception {
    if (null != this.data)
      return; 
    if (null != paramString) {
      ObjectInputStream objectInputStream = null;
      try {
        objectInputStream = new ObjectInputStream(new FileInputStream(new File(paramString, FILENAME_ENTHRALL)));
        Data data = (Data)objectInputStream.readObject();
        data.timeout.start_thread();
        this.data = data;
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        if (null != objectInputStream)
          objectInputStream.close(); 
      } 
    } 
    if (null == this.data)
      this.data = new Data(); 
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName1 = new ObjectName("Enthrallment:type=config");
    EnthrallmentConfig enthrallmentConfig = new EnthrallmentConfig();
    enthrallmentConfig.assign(this.data.config);
    mBeanServer.registerMBean(enthrallmentConfig, objectName1);
    ObjectName objectName2 = new ObjectName("Enthrallment:type=control");
    mBeanServer.registerMBean(this, objectName2);
  }
  
  public void load(String paramString) {
    try {
      initialize(paramString);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public synchronized void save(String paramString) throws Exception {
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(paramString, FILENAME_ENTHRALL)));
    objectOutputStream.writeObject(this.data);
    objectOutputStream.close();
  }
  
  public synchronized void getConfig(EnthrallmentConfig paramEnthrallmentConfig) {
    paramEnthrallmentConfig.assign(this.data.config);
  }
  
  public synchronized void apply(EnthrallmentConfig paramEnthrallmentConfig) {
    this.data.apply(paramEnthrallmentConfig);
  }
  
  static final class Light implements Serializable {
    int[] sids;
    
    Enthrallment.Player[] data;
    
    int size;
    
    Light() {
      this(9);
    }
    
    Light(int param1Int) {
      this.data = new Enthrallment.Player[param1Int];
      this.sids = new int[param1Int];
    }
    
    void trigger(Enthrallment param1Enthrallment, boolean param1Boolean) {
      for (byte b = 0; b < this.size; b++)
        this.data[b].onTimeout(this.sids[b], param1Boolean); 
      if (null != param1Enthrallment) {
        if (this.size > param1Enthrallment.maxtriggers)
          param1Enthrallment.maxtriggers = this.size; 
        if (this.size == 0) {
          ++param1Enthrallment.zerotriggers;
        } else {
          param1Enthrallment.totallengths += this.data.length;
          param1Enthrallment.totaltriggers += this.size;
          ++param1Enthrallment.triggertimes;
        } 
      } 
    }
    
    boolean isEmpty() {
      return (0 == this.size);
    }
    
    void add(int param1Int, Enthrallment.Player param1Player) {
      if (this.size == this.data.length)
        ensureCapacity(this.size + 1); 
      this.data[this.size] = param1Player;
      this.sids[this.size] = param1Int;
      this.size++;
    }
    
    void ensureCapacity(int param1Int) {
      if (param1Int > this.data.length) {
        int i = this.data.length * 2;
        int j = Math.max(this.data.length * 2, param1Int);
        realloc(j);
      } 
    }
    
    void realloc(int param1Int) {
      if (param1Int < this.size)
        return; 
      Enthrallment.Player[] arrayOfPlayer = new Enthrallment.Player[param1Int];
      System.arraycopy(this.data, 0, arrayOfPlayer, 0, this.size);
      this.data = arrayOfPlayer;
      int[] arrayOfInt = new int[param1Int];
      System.arraycopy(this.sids, 0, arrayOfInt, 0, this.size);
      this.sids = arrayOfInt;
    }
    
    void trimToSize() {
      realloc(this.size);
    }
    
    void dump() {
      for (byte b = 0; b < this.size; b++)
        System.out.println("timeout: sid=" + this.sids[b] + " -> " + this.data[b]); 
    }
  }
  
  static final class LightTimeout implements Runnable, Serializable {
    public static final int SID_NULL = 0;
    
    int sid;
    
    private int next;
    
    private Enthrallment.Light[] lights;
    
    private int max_timeout;
    
    private int precision;
    
    private transient Thread thread = null;
    
    public void dump() {
      for (byte b = 0; b < this.lights.length; b++)
        this.lights[b].dump(); 
    }
    
    public void trimToSize() {
      for (byte b = 0; b < this.lights.length; b++)
        this.lights[b].trimToSize(); 
    }
    
    public int capacity() {
      int i = 0;
      for (byte b = 0; b < this.lights.length; b++)
        i += (this.lights[b]).data.length; 
      return i;
    }
    
    public int size() {
      int i = 0;
      for (byte b = 0; b < this.lights.length; b++)
        i += (this.lights[b]).size; 
      return i;
    }
    
    LightTimeout(int param1Int1, int param1Int2) {
      start(param1Int1, param1Int2);
      start_thread();
    }
    
    void start_thread() {
      if (null == this.thread) {
        this.thread = new Thread(this, "Enthrallment.timeout");
        this.thread.start();
      } 
    }
    
    void start(int param1Int1, int param1Int2) {
      if (param1Int2 <= 0 || param1Int1 <= 0)
        throw new IllegalArgumentException(); 
      int i = param1Int1 / param1Int2 + 1;
      if (i > 20000)
        throw new IllegalArgumentException(); 
      Enthrallment.Light[] arrayOfLight = _start(param1Int1, param1Int2, i);
      if (null == arrayOfLight)
        return; 
      for (byte b = 0; b < arrayOfLight.length; b++)
        arrayOfLight[b].trigger(null, true); 
    }
    
    Enthrallment.Light[] _start(int param1Int1, int param1Int2, int param1Int3) {
      if (this.max_timeout == param1Int1 && this.precision == param1Int2)
        return null; 
      this.max_timeout = param1Int1;
      this.precision = param1Int2;
      this.next = 0;
      Enthrallment.Light[] arrayOfLight = this.lights;
      this.lights = new Enthrallment.Light[param1Int3];
      for (byte b = 0; b < this.lights.length; b++)
        this.lights[b] = new Enthrallment.Light(); 
      return arrayOfLight;
    }
    
    int setup(Enthrallment.Player param1Player, int param1Int) {
      if (param1Int > this.max_timeout) {
        System.out.println("timeout=" + param1Int + " max=" + this.max_timeout + " player=" + param1Player);
        param1Int = this.max_timeout;
      } 
      this.sid++;
      if (0 == this.sid)
        this.sid++; 
      int i = param1Int / this.precision;
      int j = (i + this.next) % this.lights.length;
      this.lights[j].add(this.sid, param1Player);
      return this.sid;
    }
    
    public void stop() {
      Thread thread = null;
      synchronized (this) {
        thread = this.thread;
        this.thread = null;
      } 
      while (thread != null) {
        try {
          synchronized (thread) {
            thread.notify();
          } 
          thread.join();
          thread = null;
        } catch (InterruptedException interruptedException) {}
      } 
    }
    
    private synchronized Thread getThread() {
      return this.thread;
    }
    
    public void run() {
      long l = System.currentTimeMillis() + getPrecision();
      for (Thread thread = getThread(); thread != null; thread = getThread()) {
        try {
          long l1 = l - System.currentTimeMillis();
          if (l1 > 0L) {
            synchronized (thread) {
              thread.wait(l1);
            } 
          } else {
            l += getPrecision();
            Enthrallment enthrallment = Enthrallment.instance;
            synchronized (enthrallment) {
              nextLight().trigger(enthrallment, false);
            } 
          } 
        } catch (Exception exception) {}
      } 
    }
    
    private int getPrecision() {
      synchronized (Enthrallment.instance) {
        return this.precision;
      } 
    }
    
    private Enthrallment.Light nextLight() {
      Enthrallment.Light light = this.lights[this.next];
      this.lights[this.next] = new Enthrallment.Light();
      this.next = (this.next + 1) % this.lights.length;
      return light;
    }
  }
  
  static class Login extends UseridZoneid {
    public static final int SIZE = 16;
    
    long time;
    
    Login(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.time = System.currentTimeMillis();
    }
    
    Login(ByteBuffer param1ByteBuffer) {
      super(0, 0);
      this.userid = param1ByteBuffer.getInt();
      this.zoneid = param1ByteBuffer.getInt();
      this.time = param1ByteBuffer.getLong();
    }
    
    void marshal(ByteBuffer param1ByteBuffer) {
      param1ByteBuffer.putInt(this.userid);
      param1ByteBuffer.putInt(this.zoneid);
      param1ByteBuffer.putLong(this.time);
    }
    
    public String toString() {
      return super.toString() + " time=" + this.time;
    }
  }
  
  private static final class Player implements Serializable {
    byte[] loginbytes = null;
    
    int total_play_time = 0;
    
    int total_rest_time = 0;
    
    int sid_timeout = 0;
    
    int sid_timer = 0;
    
    final byte[] indent;
    
    void logoutByZoneid(int param1Int) {
      Logins logins = new Logins(this.loginbytes);
      if (logins.logoutByZoneid(param1Int)) {
        this.loginbytes = logins.getBytes();
        update(logins);
      } 
    }
    
    Logins getLogins() {
      return new Logins(this.loginbytes);
    }
    
    void login(int param1Int1, int param1Int2) {
      Logins logins = new Logins(this.loginbytes);
      logins.login(param1Int1, param1Int2);
      this.loginbytes = logins.getBytes();
      update(logins);
    }
    
    void logout(int param1Int1, int param1Int2) {
      Logins logins = new Logins(this.loginbytes);
      if (logins.logout(param1Int1, param1Int2)) {
        this.loginbytes = logins.getBytes();
        update(logins);
      } 
    }
    
    void onTimeout(int param1Int, boolean param1Boolean) {
      Logins logins = new Logins(this.loginbytes);
      if (param1Int == this.sid_timeout) {
        println("           onTimeout sid=" + this.sid_timeout);
        if (logins.update_rest())
          this.loginbytes = logins.getBytes(); 
        update(logins);
      } else if (param1Int == this.sid_timer) {
        println("           onAlert sid=" + this.sid_timer);
        Enthrallment enthrallment = Enthrallment.instance;
        int i = enthrallment.data.config.playtime2state(logins.getPlaytime(), (logins.size() == 0));
        if (false == param1Boolean || i == 3)
          startAlert(enthrallment.data.config.getStateAlertTime(i)); 
      } 
    }
    
    private void update(Logins param1Logins) {
      Enthrallment enthrallment = Enthrallment.instance;
      int i = param1Logins.getPlaytime();
      int j = enthrallment.data.config.playtime2state(i, (param1Logins.size() == 0));
      if (enthrallment.getAlert())
        startAlert(enthrallment.data.config.getStateAlertTime(j)); 
      if (j > 0) {
        int k = (enthrallment.data.config.getPlayingStateTime(j) - i) / param1Logins.size() + 1;
        startTimeout(k);
        sendAddictionControl(i, j, param1Logins);
        return;
      } 
    }
    
    void sendAddictionControl(int param1Int1, int param1Int2, Logins param1Logins) {
      println("[AC] state=" + param1Int2 + " - ");
      Enthrallment enthrallment = Enthrallment.instance;
      AddictionControl addictionControl = (AddictionControl)Protocol.Create("ADDICTIONCONTROL");
      addictionControl.rate = param1Int2 - 1;
      GPair gPair1 = new GPair();
      gPair1.key = 1;
      gPair1.value = param1Int1 / 1000;
      addictionControl.data.add(gPair1);
      long l = System.currentTimeMillis();
      int i = (enthrallment.data.config.getPlay100Time() - param1Int1) / param1Logins.size();
      GPair gPair2 = new GPair();
      gPair2.key = 2;
      gPair2.value = (int)((l + i) / 1000L);
      addictionControl.data.add(gPair2);
      int j = (enthrallment.data.config.getPlay100Time() + enthrallment.data.config.getPlay50Time() - param1Int1) / param1Logins.size();
      GPair gPair3 = new GPair();
      gPair3.key = 3;
      gPair3.value = (int)((l + j) / 1000L);
      addictionControl.data.add(gPair3);
      GAuthServer.GetLog().info("sendAddictionControl, state=" + addictionControl.rate + " playtime=" + param1Int1 + " resttime=" + this.total_rest_time + " logins=" + param1Logins);
      GAuthServer gAuthServer = GAuthServer.GetInstance();
      for (Enthrallment.Login login : param1Logins.data.values()) {
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
    
    private boolean startTimeout(int param1Int) {
      if (param1Int > 0) {
        Enthrallment enthrallment = Enthrallment.instance;
        this.sid_timeout = enthrallment.data.timeout.setup(this, param1Int);
        println("start Timeout delay=" + param1Int);
        return true;
      } 
      this.sid_timeout = 0;
      return false;
    }
    
    private boolean startAlert(int param1Int) {
      if (param1Int > 0) {
        Enthrallment enthrallment = Enthrallment.instance;
        this.sid_timer = enthrallment.data.timeout.setup(this, param1Int);
        println("start Alert period=" + param1Int);
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
    
    public boolean equals(Object param1Object) {
      return Arrays.equals(this.indent, ((Player)param1Object).indent);
    }
    
    Player(String param1String, int param1Int1, int param1Int2) {
      try {
        StringBuilder stringBuilder = new StringBuilder(128);
        if (null != param1String) {
          stringBuilder.append('i').append(param1String);
        } else {
          stringBuilder.append('u').append(param1Int1);
        } 
        stringBuilder.append('g').append(param1Int2);
        this.indent = stringBuilder.toString().getBytes("utf8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new RuntimeException(unsupportedEncodingException);
      } 
    }
    
    void println(String param1String) {
      if (Enthrallment.instance.debug)
        GAuthServer.GetLog().info("Enthrallment," + param1String + "," + this); 
    }
    
    public String toString() {
      try {
        Logins logins = new Logins(this.loginbytes);
        int i = logins.getPlaytime();
        int j = Enthrallment.instance.data.config.playtime2state(i, (logins.size() == 0));
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
  
  static class Data implements Serializable {
    Map<Enthrallment.Player, Enthrallment.Player> players;
    
    Map<Enthrallment.UseridZoneid, Enthrallment.Player> onlines;
    
    Enthrallment.LightTimeout timeout;
    
    EnthrallmentConfig config;
    
    Data() {
      reset();
    }
    
    void reset() {
      this.config = new EnthrallmentConfig();
      this.players = new HashMap<Enthrallment.Player, Enthrallment.Player>();
      this.onlines = new HashMap<Enthrallment.UseridZoneid, Enthrallment.Player>();
      this.timeout = new Enthrallment.LightTimeout(this.config.getMaxTimeout() + 1, this.config.getPrecision());
    }
    
    void apply(EnthrallmentConfig param1EnthrallmentConfig) {
      EnthrallmentConfig enthrallmentConfig = this.config;
      try {
        this.config = param1EnthrallmentConfig;
        this.timeout.start(this.config.getMaxTimeout() + 1, this.config.getPrecision());
      } catch (Throwable throwable) {
        this.config = enthrallmentConfig;
        throw new RuntimeException(throwable);
      } 
    }
  }
  
  private static class UseridZoneid implements Serializable {
    int userid;
    
    int zoneid;
    
    UseridZoneid(int param1Int1, int param1Int2) {
      this.userid = param1Int1;
      this.zoneid = param1Int2;
    }
    
    public String toString() {
      return "userid=" + this.userid + " zoneid=" + this.zoneid;
    }
    
    public int hashCode() {
      return this.userid ^ this.zoneid;
    }
    
    public boolean equals(Object param1Object) {
      UseridZoneid useridZoneid = (UseridZoneid)param1Object;
      return (this.userid == useridZoneid.userid && this.zoneid == useridZoneid.zoneid);
    }
  }
  
  private class DelayTask extends TimerTask {
    private final int action;
    
    private final int userid;
    
    private final int zoneid;
    
    private int areaid = 0;
    
    protected DelayTask(int param1Int1, int param1Int2, int param1Int3) {
      this.action = param1Int1;
      this.userid = param1Int2;
      this.zoneid = param1Int3;
    }
    
    protected DelayTask(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this(param1Int1, param1Int2, param1Int4);
      this.areaid = param1Int3;
    }
    
    public void run() {
      try {
        cancel();
        synchronized (Enthrallment.instance) {
          dispatch();
        } 
      } catch (Exception exception) {
        System.out.println(this);
        exception.printStackTrace();
      } 
    }
    
    public String toString() {
      return "action=" + this.action + " userid=" + this.userid + " zoneid=" + this.zoneid;
    }
    
    private void dispatch() {
      if (4 == this.action) {
        doLogoutByZoneid(this.zoneid);
        return;
      } 
      Enthrallment.Player player = null;
      switch (this.action) {
        case 1:
          player = Enthrallment.this.makePlayer(this.userid, this.areaid, this.zoneid);
          break;
        case 2:
        case 3:
          player = Enthrallment.this.data.onlines.get(new Enthrallment.UseridZoneid(this.userid, this.zoneid));
          break;
      } 
      if (null == player)
        return; 
      GAuthServer.GetLog().info("Enthrallment " + this + " player=" + player);
      switch (this.action) {
        case 1:
          player = doLogin(player);
          if (null != player) {
            GAuthServer.GetLog().info("Enthrallment 'logout lost and idcard changed' " + this + " player=" + player);
            doLogout(player);
          } 
          break;
        case 2:
          doLogout(player);
          Enthrallment.this.data.onlines.remove(new Enthrallment.UseridZoneid(this.userid, this.zoneid));
          break;
        case 3:
          doRemove(player);
          break;
      } 
    }
    
    private Enthrallment.Player doLogin(Enthrallment.Player param1Player) {
      Enthrallment.Player player1 = Enthrallment.this.data.players.get(param1Player);
      if (null != player1) {
        param1Player = player1;
      } else {
        Enthrallment.this.data.players.put(param1Player, param1Player);
      } 
      param1Player.login(this.userid, this.zoneid);
      Enthrallment.Player player2 = Enthrallment.this.data.onlines.put(new Enthrallment.UseridZoneid(this.userid, this.zoneid), param1Player);
      return (player2 == param1Player) ? null : player2;
    }
    
    private void doLogout(Enthrallment.Player param1Player) {
      param1Player = Enthrallment.this.data.players.get(param1Player);
      if (null != param1Player)
        param1Player.logout(this.userid, this.zoneid); 
    }
    
    private void doLogoutByZoneid(int param1Int) {
      for (Enthrallment.Player player : (Enthrallment.Player[])Enthrallment.this.data.players.keySet().toArray((Object[])new Enthrallment.Player[0]))
        player.logoutByZoneid(param1Int); 
    }
    
    private void doRemove(Enthrallment.Player param1Player) {
      param1Player = Enthrallment.this.data.players.get(param1Player);
      if (null != param1Player) {
        param1Player.cancel();
        Enthrallment.this.data.players.remove(param1Player);
        for (Enthrallment.Login login : (param1Player.getLogins()).data.values())
          Enthrallment.this.data.onlines.remove(login); 
      } 
      Enthrallment.this.data.onlines.remove(new Enthrallment.UseridZoneid(this.userid, this.zoneid));
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */