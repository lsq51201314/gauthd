package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class StatusAnnounce extends Protocol {
  public int userid;
  
  public int localsid;
  
  public byte status;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.localsid);
    paramOctetsStream.marshal(this.status);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    this.localsid = paramOctetsStream.unmarshal_int();
    this.status = paramOctetsStream.unmarshal_byte();
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
    if (GAuthServer.zonemap.get(paramSession) == null)
      return; 
    if (GAuthServer.aidmap.get(paramSession) == null)
      return; 
    int i = ((Integer)GAuthServer.zonemap.get(paramSession)).intValue();
    int j = ((Integer)GAuthServer.aidmap.get(paramSession)).intValue();
    Object[] arrayOfObject = { new Integer(i), new Integer(this.localsid), new Integer(1) };
    storage.recordUserOffline(arrayOfObject, new Integer(this.userid), new Integer(j));
    if (arrayOfObject[2] == null)
      return; 
    if (((Integer)arrayOfObject[2]).intValue() == 1) {
      GAuthServer.GetLog().info("User " + this.userid + " logout successfully.");
    } else {
      GAuthServer.GetLog().info("WARNING: User " + this.userid + " logout failed. User info is different from DB data.");
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\StatusAnnounce.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */