package protocol;

import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;
import java.util.Map;
import java.util.Set;

public final class UserLogin extends Rpc {
  public Session GetSessionbyZoneid(Integer paramInteger) {
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    if (!GAuthServer.zonemap.containsValue(paramInteger))
      return null; 
    Set set = GAuthServer.zonemap.entrySet();
    for (Map.Entry entry : set) {
      if (((Integer)entry.getValue()).intValue() == paramInteger.intValue())
        return (Session)entry.getKey(); 
    } 
    return null;
  }
  
  public void Server(Rpc.Data paramData1, Rpc.Data paramData2, Manager paramManager, Session paramSession) throws ProtocolException {
    UserLoginArg userLoginArg = (UserLoginArg)paramData1;
    UserLoginRes userLoginRes = (UserLoginRes)paramData2;
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    if (GAuthServer.zonemap.get(paramSession) == null) {
      userLoginRes.retcode = -1;
      return;
    } 
    if (GAuthServer.aidmap.get(paramSession) == null) {
      userLoginRes.retcode = -1;
      return;
    } 
    int i = ((Integer)GAuthServer.zonemap.get(paramSession)).intValue();
    int j = ((Integer)GAuthServer.aidmap.get(paramSession)).intValue();
    userLoginRes.remain_playtime = 0;
    userLoginRes.free_time_left = 0;
    userLoginRes.free_time_end = 0;
    userLoginRes.creatime = 0;
    userLoginRes.func = 0;
    userLoginRes.funcparm = 0;
    userLoginRes.adduppoint = 0;
    userLoginRes.soldpoint = 0;
    userLoginRes.creatime = storage.acquireUserCreatime(new Integer(userLoginArg.userid));
    Object[] arrayOfObject1 = { new Integer(i), new Integer(userLoginArg.localsid), new Integer(userLoginArg.blkickuser) };
    if (!storage.recordUserOnline(arrayOfObject1, new Integer(userLoginArg.userid), new Integer(j))) {
      userLoginRes.retcode = 8;
      return;
    } 
    if (arrayOfObject1[0] != null && arrayOfObject1[1] != null && arrayOfObject1[2] != null && (((Integer)arrayOfObject1[0]).intValue() != i || ((Integer)arrayOfObject1[1]).intValue() != userLoginArg.localsid))
      if (((Integer)arrayOfObject1[2]).intValue() == 1) {
        KickoutUser kickoutUser = (KickoutUser)Rpc.Create("KICKOUTUSER");
        kickoutUser.userid = userLoginArg.userid;
        kickoutUser.localsid = ((Integer)arrayOfObject1[1]).intValue();
        kickoutUser.cause = 32;
        GAuthServer.GetLog().info("Send Kickout userid=" + kickoutUser.userid + " sid=" + kickoutUser.localsid);
        Session session = GetSessionbyZoneid((Integer)arrayOfObject1[0]);
        if (session != null) {
          gAuthServer.Send(session, kickoutUser);
        } else {
          GAuthServer.GetLog().info("Error: kickout user " + kickoutUser.userid + " failed.");
        } 
      } else {
        userLoginRes.retcode = 10;
        return;
      }  
    userLoginRes.retcode = 0;
    Object[] arrayOfObject2 = storage.acquireUserPrivilege(new Integer(userLoginArg.userid), new Integer(i));
    userLoginRes.blIsGM = (arrayOfObject2 != null && arrayOfObject2.length != 0) ? 1 : 0;
    GAuthServer.GetLog().info("UserLogin:userid=" + userLoginArg.userid + ",sid=" + userLoginArg.localsid + ",aid=" + j + ",zoneid=" + i + ",remaintime=" + userLoginRes.remain_playtime + ",free_time_left=" + userLoginRes.free_time_left + ",free_time_end=" + userLoginRes.free_time_end + ",func=" + userLoginRes.func + ",funcparm=" + userLoginRes.funcparm + ",creatime=" + userLoginRes.creatime + ",adduppoint=" + userLoginRes.adduppoint + ",soldpoint=" + userLoginRes.soldpoint);
  }
  
  public void Client(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    UserLoginArg userLoginArg = (UserLoginArg)paramData1;
    UserLoginRes userLoginRes = (UserLoginRes)paramData2;
  }
  
  public void OnTimeout() {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\UserLogin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */