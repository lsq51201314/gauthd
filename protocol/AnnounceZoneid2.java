package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class AnnounceZoneid2 extends Protocol {
  public int zoneid;
  
  public int aid;
  
  public byte blreset;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.aid);
    paramOctetsStream.marshal(this.blreset);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.zoneid = paramOctetsStream.unmarshal_int();
    this.aid = paramOctetsStream.unmarshal_int();
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
    Integer integer1 = new Integer(this.zoneid);
    Integer integer2 = new Integer(this.aid);
    if (this.blreset != 0) {
      GAuthServer.GetLog().info("AnnounceZoneid2, clear all online records on zone " + integer1);
      storage.clearOnlineRecords(integer1, integer2);
    } 
    Session session = gAuthServer.GetSessionbyZoneid(integer1);
    if (null != session) {
      GAuthServer.GetLog().info("AnnounceZoneid2 ERROR, zoneid already exists. New session will replace Old!!! zoneid=" + integer1);
      GAuthServer.zonemap.remove(session);
      GAuthServer.aidmap.remove(session);
    } 
    GAuthServer.zonemap.put(paramSession, integer1);
    GAuthServer.aidmap.put(paramSession, integer2);
    GAuthServer.GetLog().info("AnnounceZoneid2, zone " + integer1 + "  aid " + integer2 + " announced.");
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\AnnounceZoneid2.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */