package protocol;

import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.account.storage;

public final class QueryPasswd extends Rpc {
  public void Server(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    QueryPasswdArg queryPasswdArg = (QueryPasswdArg)paramData1;
    QueryPasswdRes queryPasswdRes = (QueryPasswdRes)paramData2;
    try {
      GAuthServer.GetLog().info("Receive QueryPasswd for user " + queryPasswdArg.account.getString());
      Object[] arrayOfObject = storage.acquireIdPasswd(queryPasswdArg.account.getString());
      if (arrayOfObject == null) {
        queryPasswdRes.retcode = 8;
        return;
      } 
      queryPasswdRes.retcode = 0;
      queryPasswdRes.userid = ((Integer)arrayOfObject[0]).intValue();
      queryPasswdRes.password.replace((byte[])arrayOfObject[1]);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void Client(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    QueryPasswdArg queryPasswdArg = (QueryPasswdArg)paramData1;
    QueryPasswdRes queryPasswdRes = (QueryPasswdRes)paramData2;
  }
  
  public void OnTimeout() {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\QueryPasswd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */