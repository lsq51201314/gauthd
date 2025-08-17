package protocol;

import java.io.Serializable;

final class LightTimeout implements Runnable, Serializable {
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
  
  LightTimeout(int paramInt1, int paramInt2) {
    start(paramInt1, paramInt2);
    start_thread();
  }
  
  void start_thread() {
    if (null == this.thread) {
      this.thread = new Thread(this, "Enthrallment.timeout");
      this.thread.start();
    } 
  }
  
  void start(int paramInt1, int paramInt2) {
    if (paramInt2 <= 0 || paramInt1 <= 0)
      throw new IllegalArgumentException(); 
    int i = paramInt1 / paramInt2 + 1;
    if (i > 20000)
      throw new IllegalArgumentException(); 
    Enthrallment.Light[] arrayOfLight = _start(paramInt1, paramInt2, i);
    if (null == arrayOfLight)
      return; 
    for (byte b = 0; b < arrayOfLight.length; b++)
      arrayOfLight[b].trigger(null, true); 
  }
  
  Enthrallment.Light[] _start(int paramInt1, int paramInt2, int paramInt3) {
    if (this.max_timeout == paramInt1 && this.precision == paramInt2)
      return null; 
    this.max_timeout = paramInt1;
    this.precision = paramInt2;
    this.next = 0;
    Enthrallment.Light[] arrayOfLight = this.lights;
    this.lights = new Enthrallment.Light[paramInt3];
    for (byte b = 0; b < this.lights.length; b++)
      this.lights[b] = new Enthrallment.Light(); 
    return arrayOfLight;
  }
  
  int setup(Enthrallment.Player paramPlayer, int paramInt) {
    if (paramInt > this.max_timeout) {
      System.out.println("timeout=" + paramInt + " max=" + this.max_timeout + " player=" + paramPlayer);
      paramInt = this.max_timeout;
    } 
    this.sid++;
    if (0 == this.sid)
      this.sid++; 
    int i = paramInt / this.precision;
    int j = (i + this.next) % this.lights.length;
    this.lights[j].add(this.sid, paramPlayer);
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
          Enthrallment enthrallment = Enthrallment.access$000();
          synchronized (enthrallment) {
            nextLight().trigger(enthrallment, false);
          } 
        } 
      } catch (Exception exception) {}
    } 
  }
  
  private int getPrecision() {
    synchronized (Enthrallment.access$000()) {
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


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$LightTimeout.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */