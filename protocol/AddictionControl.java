package protocol;

import com.goldhuman.Common.Marshal.Marshal;
import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;

public final class AddictionControl extends Protocol {
  public int zoneid;
  
  public int userid;
  
  public int rate;
  
  public int msg;
  
  public Rpc.Data.DataVector data = new Rpc.Data.DataVector(new GPair());
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.rate);
    paramOctetsStream.marshal(this.msg);
    paramOctetsStream.marshal((Marshal)this.data);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.zoneid = paramOctetsStream.unmarshal_int();
    this.userid = paramOctetsStream.unmarshal_int();
    this.rate = paramOctetsStream.unmarshal_int();
    this.msg = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal((Marshal)this.data);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      AddictionControl addictionControl = (AddictionControl)super.clone();
      addictionControl.data = (Rpc.Data.DataVector)this.data.clone();
      return addictionControl;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\AddictionControl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */