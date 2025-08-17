package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class AnnounceZoneid extends Protocol {
  public byte zoneid;
  
  public byte aid;
  
  public byte blreset;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.aid);
    paramOctetsStream.marshal(this.blreset);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.zoneid = paramOctetsStream.unmarshal_byte();
    this.aid = paramOctetsStream.unmarshal_byte();
    this.blreset = paramOctetsStream.unmarshal_byte();
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    Integer integer1 = new Integer(0xFF & this.zoneid);
    Integer integer2 = new Integer(0xFF & this.aid);
    if (this.blreset != 0) {
      GAuthServer.GetLog().info("<<< clear all online records on zone " + integer1 + " >>>");
      storage.clearOnlineRecords(integer1, integer2);
    } 
    GAuthServer.zonemap.put(paramSession, integer1);
    GAuthServer.aidmap.put(paramSession, integer2);
    GAuthServer.GetLog().info("zone " + integer1 + "  aid " + integer2 + " announced.");
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\AnnounceZoneid.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */