package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class QueryUserPrivilege extends Protocol {
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
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {
    Object[] arrayOfObject1 = storage.acquireUserPrivilege(new Integer(this.userid), new Integer(0xFF & this.zoneid));
    if (arrayOfObject1 == null)
      return; 
    QueryUserPrivilege_Re queryUserPrivilege_Re = (QueryUserPrivilege_Re)Protocol.Create("QUERYUSERPRIVILEGE_RE");
    queryUserPrivilege_Re.userid = this.userid;
    Object[] arrayOfObject2 = null;
    for (byte b = 0; b < arrayOfObject1.length; b++) {
      arrayOfObject2 = (Object[])arrayOfObject1[b];
      if (arrayOfObject2 != null && ((Integer)arrayOfObject2[0]).intValue() < 255)
        queryUserPrivilege_Re.auth.add(new MByte((byte)((Integer)arrayOfObject2[0]).intValue())); 
    } 
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    gAuthServer.Send(paramSession, queryUserPrivilege_Re);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryUserPrivilege.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */