package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class AnnounceZoneid3 extends Protocol {
  public int zoneid;
  
  public int aid;
  
  public byte blreset;
  
  public int ip1;
  
  public int ip2;
  
  public int ip3;
  
  public int reserved1;
  
  public int reserved2;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.aid);
    paramOctetsStream.marshal(this.blreset);
    paramOctetsStream.marshal(this.ip1);
    paramOctetsStream.marshal(this.ip2);
    paramOctetsStream.marshal(this.ip3);
    paramOctetsStream.marshal(this.reserved1);
    paramOctetsStream.marshal(this.reserved2);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.zoneid = paramOctetsStream.unmarshal_int();
    this.aid = paramOctetsStream.unmarshal_int();
    this.blreset = paramOctetsStream.unmarshal_byte();
    this.ip1 = paramOctetsStream.unmarshal_int();
    this.ip2 = paramOctetsStream.unmarshal_int();
    this.ip3 = paramOctetsStream.unmarshal_int();
    this.reserved1 = paramOctetsStream.unmarshal_int();
    this.reserved2 = paramOctetsStream.unmarshal_int();
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
      GAuthServer.GetLog().info("AnnounceZoneid3, clear all online records on zone " + integer1);
      storage.clearOnlineRecords(integer1, integer2);
    } 
    Session session = gAuthServer.GetSessionbyZoneid(integer1);
    if (null != session) {
      GAuthServer.GetLog().info("AnnounceZoneid3 ERROR, zoneid already exists. New session will replace Old!!! zoneid=" + integer1);
      GAuthServer.zonemap.remove(session);
      GAuthServer.aidmap.remove(session);
    } 
    GAuthServer.zonemap.put(paramSession, integer1);
    GAuthServer.aidmap.put(paramSession, integer2);
    GAuthServer.GetLog().info("AnnounceZoneid3, aid=" + this.aid + ",zoneid=" + this.zoneid + ",ip1=" + getIP(this.ip1) + ",ip2=" + getIP(this.ip2) + ",ip3=" + getIP(this.ip3));
  }
  
  private static int bswap(int paramInt) {
    int i = paramInt & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt >> 16 & 0xFF;
    int m = paramInt >> 24 & 0xFF;
    return 0xFF000000 & i << 24 | 0xFF0000 & j << 16 | 0xFF00 & k << 8 | 0xFF & m;
  }
  
  private static String getIP(int paramInt) {
    try {
      paramInt = bswap(paramInt);
      return "" + (paramInt >> 24 & 0xFF) + "." + (paramInt >> 16 & 0xFF) + "." + (paramInt >> 8 & 0xFF) + "." + (paramInt & 0xFF);
    } catch (Exception exception) {
      return "";
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\AnnounceZoneid3.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */