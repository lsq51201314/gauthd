package protocol;

import java.nio.ByteBuffer;

class Login extends Enthrallment.UseridZoneid {
  public static final int SIZE = 16;
  
  long time;
  
  Login(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
    this.time = System.currentTimeMillis();
  }
  
  Login(ByteBuffer paramByteBuffer) {
    super(0, 0);
    this.userid = paramByteBuffer.getInt();
    this.zoneid = paramByteBuffer.getInt();
    this.time = paramByteBuffer.getLong();
  }
  
  void marshal(ByteBuffer paramByteBuffer) {
    paramByteBuffer.putInt(this.userid);
    paramByteBuffer.putInt(this.zoneid);
    paramByteBuffer.putLong(this.time);
  }
  
  public String toString() {
    return super.toString() + " time=" + this.time;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$Login.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */