package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class QueryPasswdRes extends Rpc.Data {
  public byte retcode;
  
  public int userid;
  
  public Octets password = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.password);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_byte();
    this.userid = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.password);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      QueryPasswdRes queryPasswdRes = (QueryPasswdRes)super.clone();
      queryPasswdRes.password = (Octets)this.password.clone();
      return queryPasswdRes;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryPasswdRes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */