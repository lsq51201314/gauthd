package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class QueryPasswdArg extends Rpc.Data {
  public Octets account = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.account);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    paramOctetsStream.unmarshal(this.account);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      QueryPasswdArg queryPasswdArg = (QueryPasswdArg)super.clone();
      queryPasswdArg.account = (Octets)this.account.clone();
      return queryPasswdArg;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryPasswdArg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */