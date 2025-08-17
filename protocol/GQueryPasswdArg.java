package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class GQueryPasswdArg extends Rpc.Data {
  public Octets account = new Octets();
  
  public Octets challenge = new Octets();
  
  public int loginip;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.account);
    paramOctetsStream.marshal(this.challenge);
    paramOctetsStream.marshal(this.loginip);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    paramOctetsStream.unmarshal(this.account);
    paramOctetsStream.unmarshal(this.challenge);
    this.loginip = paramOctetsStream.unmarshal_int();
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GQueryPasswdArg gQueryPasswdArg = (GQueryPasswdArg)super.clone();
      gQueryPasswdArg.account = (Octets)this.account.clone();
      gQueryPasswdArg.challenge = (Octets)this.challenge.clone();
      return gQueryPasswdArg;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GQueryPasswdArg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */