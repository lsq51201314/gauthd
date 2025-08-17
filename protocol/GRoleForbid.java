package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class GRoleForbid extends Rpc.Data {
  public byte type;
  
  public int time;
  
  public int createtime;
  
  public Octets reason = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.type);
    paramOctetsStream.marshal(this.time);
    paramOctetsStream.marshal(this.createtime);
    paramOctetsStream.marshal(this.reason);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.type = paramOctetsStream.unmarshal_byte();
    this.time = paramOctetsStream.unmarshal_int();
    this.createtime = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.reason);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GRoleForbid gRoleForbid = (GRoleForbid)super.clone();
      gRoleForbid.reason = (Octets)this.reason.clone();
      return gRoleForbid;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GRoleForbid.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */