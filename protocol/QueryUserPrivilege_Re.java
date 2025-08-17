package protocol;

import com.goldhuman.Common.Marshal.Marshal;
import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;

public final class QueryUserPrivilege_Re extends Protocol {
  public int userid;
  
  public Rpc.Data.DataVector auth = new Rpc.Data.DataVector(new MByte());
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal((Marshal)this.auth);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal((Marshal)this.auth);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      QueryUserPrivilege_Re queryUserPrivilege_Re = (QueryUserPrivilege_Re)super.clone();
      queryUserPrivilege_Re.auth = (Rpc.Data.DataVector)this.auth.clone();
      return queryUserPrivilege_Re;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryUserPrivilege_Re.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */