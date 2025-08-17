package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class GChangePasswdArg extends Rpc.Data {
  public Octets username = new Octets();
  
  public Octets newpwd = new Octets();
  
  public Octets oldpwd = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.username);
    paramOctetsStream.marshal(this.newpwd);
    paramOctetsStream.marshal(this.oldpwd);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    paramOctetsStream.unmarshal(this.username);
    paramOctetsStream.unmarshal(this.newpwd);
    paramOctetsStream.unmarshal(this.oldpwd);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GChangePasswdArg gChangePasswdArg = (GChangePasswdArg)super.clone();
      gChangePasswdArg.username = (Octets)this.username.clone();
      gChangePasswdArg.newpwd = (Octets)this.newpwd.clone();
      gChangePasswdArg.oldpwd = (Octets)this.oldpwd.clone();
      return gChangePasswdArg;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GChangePasswdArg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */