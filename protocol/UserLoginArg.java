package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Rpc;

public final class UserLoginArg extends Rpc.Data {
  public int userid;
  
  public int localsid;
  
  public byte blkickuser;
  
  public int freecreatime;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.localsid);
    paramOctetsStream.marshal(this.blkickuser);
    paramOctetsStream.marshal(this.freecreatime);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    this.localsid = paramOctetsStream.unmarshal_int();
    this.blkickuser = paramOctetsStream.unmarshal_byte();
    this.freecreatime = paramOctetsStream.unmarshal_int();
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


/* Location:              D:\UserData\Desktop\authd\!\protocol\UserLoginArg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */