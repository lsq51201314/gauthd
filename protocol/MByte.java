package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Rpc;

public class MByte extends Rpc.Data {
  private byte value = 0;
  
  public MByte() {}
  
  public MByte(byte paramByte) {}
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    return paramOctetsStream.marshal(this.value);
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.value = paramOctetsStream.unmarshal_byte();
    return paramOctetsStream;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\MByte.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */