package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Rpc;

public final class UseCashArg extends Rpc.Data {
  public int zoneid;
  
  public int userid;
  
  public int aid;
  
  public int point;
  
  public int cash;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.aid);
    paramOctetsStream.marshal(this.point);
    paramOctetsStream.marshal(this.cash);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.zoneid = paramOctetsStream.unmarshal_int();
    this.userid = paramOctetsStream.unmarshal_int();
    this.aid = paramOctetsStream.unmarshal_int();
    this.point = paramOctetsStream.unmarshal_int();
    this.cash = paramOctetsStream.unmarshal_int();
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


/* Location:              D:\UserData\Desktop\authd\!\protocol\UseCashArg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */