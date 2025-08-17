package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;

public final class MatrixFailure extends Protocol {
  public int userid;
  
  public int loginip;
  
  public int weight;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.loginip);
    paramOctetsStream.marshal(this.weight);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    this.loginip = paramOctetsStream.unmarshal_int();
    this.weight = paramOctetsStream.unmarshal_int();
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\MatrixFailure.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */