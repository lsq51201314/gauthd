package protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
  
  boolean login(int paramInt1, int paramInt2) {
    Enthrallment.Login login = new Enthrallment.Login(paramInt1, paramInt2);
    if (!this.data.containsKey(login)) {
      update_rest();
      this.data.put(login, login);
      return true;
    } 
    return false;
  }
  
  private int _safeadd(int paramInt, long paramLong, String paramString) {
    return (paramLong < 0L) ? paramInt : ((paramLong >= (Integer.MAX_VALUE - paramInt)) ? Integer.MAX_VALUE : (paramInt + (int)paramLong));
  }
  
  boolean logout(int paramInt1, int paramInt2) {
    Enthrallment.Login login = this.data.remove(new Enthrallment.Login(paramInt1, paramInt2));
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
  
  boolean logoutByZoneid(int paramInt) {
    ArrayList<Integer> arrayList = new ArrayList();
    for (Enthrallment.Login login : this.data.keySet()) {
      if (login.zoneid == paramInt)
        arrayList.add(Integer.valueOf(login.userid)); 
    } 
    if (arrayList.isEmpty())
      return false; 
    for (Integer integer : arrayList)
      logout(integer.intValue(), paramInt); 
    return true;
  }
  
  int getPlaytime() {
    int i = Enthrallment.Player.this.total_play_time;
    long l = System.currentTimeMillis();
    for (Enthrallment.Login login : this.data.values())
      i = _safeadd(i, l - login.time, "getPlayTime"); 
    return i;
  }
  
  Logins(byte[] paramArrayOfbyte) {
    if (null == paramArrayOfbyte)
      return; 
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte);
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


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$Player$Logins.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */