package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class GQueryPasswdRes extends Rpc.Data {
  public int retcode;
  
  public int userid;
  
  public Octets response = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.response);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_int();
    this.userid = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.response);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GQueryPasswdRes gQueryPasswdRes = (GQueryPasswdRes)super.clone();
      gQueryPasswdRes.response = (Octets)this.response.clone();
      return gQueryPasswdRes;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GQueryPasswdRes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */