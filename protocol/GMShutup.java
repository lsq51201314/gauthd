package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class GMShutup extends Protocol {
  public int gmroleid;
  
  public int localsid;
  
  public int dstuserid;
  
  public int forbid_time;
  
  public Octets reason = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.gmroleid);
    paramOctetsStream.marshal(this.localsid);
    paramOctetsStream.marshal(this.dstuserid);
    paramOctetsStream.marshal(this.forbid_time);
    paramOctetsStream.marshal(this.reason);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.gmroleid = paramOctetsStream.unmarshal_int();
    this.localsid = paramOctetsStream.unmarshal_int();
    this.dstuserid = paramOctetsStream.unmarshal_int();
    this.forbid_time = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.reason);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GMShutup gMShutup = (GMShutup)super.clone();
      gMShutup.reason = (Octets)this.reason.clone();
      return gMShutup;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {
    System.out.println("Add PRV_FORBID_TALK to user " + this.dstuserid + " for " + this.forbid_time + " seconds.");
    storage.addForbid(new Integer(this.dstuserid), new Integer(101), new Integer(this.forbid_time), this.reason.array(), new Integer(this.gmroleid));
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GMShutup.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */