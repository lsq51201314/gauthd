package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class MatrixPasswdArg extends Rpc.Data {
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
      MatrixPasswdArg matrixPasswdArg = (MatrixPasswdArg)super.clone();
      matrixPasswdArg.account = (Octets)this.account.clone();
      matrixPasswdArg.challenge = (Octets)this.challenge.clone();
      return matrixPasswdArg;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\MatrixPasswdArg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */