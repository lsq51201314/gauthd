package protocol;

import com.goldhuman.Common.Marshal.Marshal;
import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;

public final class QueryUserForbid_Re extends Protocol {
  public int userid;
  
  public int list_type;
  
  public Rpc.Data.DataVector forbid = new Rpc.Data.DataVector(new GRoleForbid());
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.list_type);
    paramOctetsStream.marshal((Marshal)this.forbid);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    this.list_type = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal((Marshal)this.forbid);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      QueryUserForbid_Re queryUserForbid_Re = (QueryUserForbid_Re)super.clone();
      queryUserForbid_Re.forbid = (Rpc.Data.DataVector)this.forbid.clone();
      return queryUserForbid_Re;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryUserForbid_Re.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */