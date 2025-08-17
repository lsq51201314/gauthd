package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Rpc;

public final class GPair extends Rpc.Data {
  public int key;
  
  public int value;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.key);
    paramOctetsStream.marshal(this.value);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.key = paramOctetsStream.unmarshal_int();
    this.value = paramOctetsStream.unmarshal_int();
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GPair.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */