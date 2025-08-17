package protocol;

import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class UserLogout extends Rpc {
  public void Server(Rpc.Data paramData1, Rpc.Data paramData2, Manager paramManager, Session paramSession) throws ProtocolException {
    UserLogoutArg userLogoutArg = (UserLogoutArg)paramData1;
    UserLogoutRes userLogoutRes = (UserLogoutRes)paramData2;
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    if (GAuthServer.zonemap.get(paramSession) == null) {
      userLogoutRes.retcode = 12;
      return;
    } 
    if (GAuthServer.aidmap.get(paramSession) == null) {
      userLogoutRes.retcode = 12;
      return;
    } 
    int i = ((Integer)GAuthServer.zonemap.get(paramSession)).intValue();
    int j = ((Integer)GAuthServer.aidmap.get(paramSession)).intValue();
    Object[] arrayOfObject = { new Integer(i), new Integer(userLogoutArg.localsid), new Integer(1) };
    storage.recordUserOffline(arrayOfObject, new Integer(userLogoutArg.userid), new Integer(j));
    if (arrayOfObject == null || arrayOfObject[2] == null) {
      userLogoutRes.retcode = 12;
      return;
    } 
    if (((Integer)arrayOfObject[2]).intValue() == 1) {
      GAuthServer.GetLog().info("UserLogout::User " + userLogoutArg.userid + " logout successfully.");
      userLogoutRes.retcode = 0;
    } else {
      GAuthServer.GetLog().info("UserLogout::WARNING: User " + userLogoutArg.userid + " logout failed. User info is different from DB data.");
      userLogoutRes.retcode = 12;
    } 
  }
  
  public void Client(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    UserLogoutArg userLogoutArg = (UserLogoutArg)paramData1;
    UserLogoutRes userLogoutRes = (UserLogoutRes)paramData2;
  }
  
  public void OnTimeout() {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\UserLogout.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */