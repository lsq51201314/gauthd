package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;

public final class QueryUserForbid extends Protocol {
  public int userid;
  
  public byte zoneid;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.zoneid);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    this.zoneid = paramOctetsStream.unmarshal_byte();
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


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryUserForbid.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */