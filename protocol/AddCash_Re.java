package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class AddCash_Re extends Protocol {
  public int retcode;
  
  public int userid;
  
  public int zoneid;
  
  public int sn;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.sn);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_int();
    this.userid = paramOctetsStream.unmarshal_int();
    this.zoneid = paramOctetsStream.unmarshal_int();
    this.sn = paramOctetsStream.unmarshal_int();
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
    GAuthServer.GetLog().info("AddCash_Re: retcode=" + this.retcode + ",userid=" + this.userid + ",zoneid=" + this.zoneid + ",sn=" + this.sn);
    if (0 != this.retcode) {
      if (this.retcode >= 1 && this.retcode <= 4)
        this.retcode = -1; 
      storage.useCash(new Integer(this.userid), new Integer(this.zoneid), new Integer(this.sn), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(this.retcode));
      GAuthServer.GetInstance().SendUseCash_Re(this.userid, this.zoneid, this.retcode);
      return;
    } 
    int i = storage.useCash(new Integer(this.userid), new Integer(this.zoneid), new Integer(this.sn), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(4));
    GAuthServer.GetInstance().SendUseCash_Re(this.userid, this.zoneid, i);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\AddCash_Re.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */