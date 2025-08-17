package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class GChangePasswdRes extends Rpc.Data {
  public int retcode;
  
  public Octets username = new Octets();
  
  public Octets newpwd = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.username);
    paramOctetsStream.marshal(this.newpwd);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.username);
    paramOctetsStream.unmarshal(this.newpwd);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GChangePasswdRes gChangePasswdRes = (GChangePasswdRes)super.clone();
      gChangePasswdRes.username = (Octets)this.username.clone();
      gChangePasswdRes.newpwd = (Octets)this.newpwd.clone();
      return gChangePasswdRes;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GChangePasswdRes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */