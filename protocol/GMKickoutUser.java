package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;
import java.util.Calendar;
import java.util.Iterator;

public final class GMKickoutUser extends Protocol {
  public int gmroleid;
  
  public int localsid;
  
  public int kickuserid;
  
  public int forbid_time;
  
  public Octets reason = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.gmroleid);
    paramOctetsStream.marshal(this.localsid);
    paramOctetsStream.marshal(this.kickuserid);
    paramOctetsStream.marshal(this.forbid_time);
    paramOctetsStream.marshal(this.reason);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.gmroleid = paramOctetsStream.unmarshal_int();
    this.localsid = paramOctetsStream.unmarshal_int();
    this.kickuserid = paramOctetsStream.unmarshal_int();
    this.forbid_time = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.reason);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      GMKickoutUser gMKickoutUser = (GMKickoutUser)super.clone();
      gMKickoutUser.reason = (Octets)this.reason.clone();
      return gMKickoutUser;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void Process(Manager paramManager, Session paramSession) throws ProtocolException {
    Object[] arrayOfObject = storage.getUserOnlineInfo(new Integer(this.kickuserid));
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    if (arrayOfObject != null && arrayOfObject.length != 0 && this.forbid_time >= 0)
      for (byte b = 0; b < arrayOfObject.length; b++) {
        Object[] arrayOfObject1 = (Object[])arrayOfObject[b];
        Integer integer1 = (Integer)arrayOfObject1[0];
        Integer integer2 = (Integer)arrayOfObject1[1];
        Integer integer3 = (Integer)arrayOfObject1[2];
        if (integer1 != null && integer2 != null && integer3 != null) {
          KickoutUser kickoutUser = (KickoutUser)Protocol.Create("KICKOUTUSER");
          kickoutUser.userid = this.kickuserid;
          kickoutUser.localsid = integer2.intValue();
          kickoutUser.cause = 32;
          GAuthServer.GetLog().info("GMKickoutUser: Send Kickout userid=" + kickoutUser.userid + " sid=" + kickoutUser.localsid + " zoneid=" + integer1 + " aid=" + integer3);
          Session session = gAuthServer.GetSessionbyZoneid(integer1);
          if (session != null)
            GAuthServer.GetInstance().Send(session, kickoutUser); 
          Object[] arrayOfObject2 = { integer1, integer2, new Integer(1) };
          storage.recordUserOffline(arrayOfObject2, new Integer(this.kickuserid), integer3);
        } 
      }  
    GAuthServer.GetLog().info("Add PRV_FORCE_OFFLINE to user " + this.kickuserid + " for " + this.forbid_time + " seconds. localsid=" + this.localsid);
    if (-2 != this.localsid)
      storage.addForbid(new Integer(this.kickuserid), new Integer(100), new Integer(this.forbid_time), this.reason.array(), new Integer(this.gmroleid)); 
    QueryUserForbid_Re queryUserForbid_Re = (QueryUserForbid_Re)Protocol.Create("QUERYUSERFORBID_RE");
    queryUserForbid_Re.userid = this.kickuserid;
    queryUserForbid_Re.list_type = 1;
    GRoleForbid gRoleForbid = new GRoleForbid();
    gRoleForbid.type = 100;
    gRoleForbid.createtime = (int)Calendar.getInstance().getTime().getTime() / 1000;
    gRoleForbid.time = this.forbid_time;
    if (gRoleForbid.time <= 0)
      gRoleForbid.time = 0; 
    gRoleForbid.reason = this.reason;
    queryUserForbid_Re.forbid.add(gRoleForbid);
    Iterator<Session> iterator = GAuthServer.zonemap.keySet().iterator();
    while (iterator.hasNext())
      gAuthServer.Send(iterator.next(), queryUserForbid_Re); 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GMKickoutUser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */