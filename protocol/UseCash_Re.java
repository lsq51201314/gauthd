package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;

public final class UseCash_Re extends Protocol {
  public int retcode;
  
  public int userid;
  
  public int zoneid;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.zoneid);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_int();
    this.userid = paramOctetsStream.unmarshal_int();
    this.zoneid = paramOctetsStream.unmarshal_int();
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


/* Location:              D:\UserData\Desktop\authd\!\protocol\UseCash_Re.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */