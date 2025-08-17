package protocol;

import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.account.storage;

public final class GChangePasswd extends Rpc {
  public void Server(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    GChangePasswdArg gChangePasswdArg = (GChangePasswdArg)paramData1;
    GChangePasswdRes gChangePasswdRes = (GChangePasswdRes)paramData2;
    gChangePasswdRes.retcode = -1;
    try {
      gChangePasswdRes.username = gChangePasswdArg.username;
      gChangePasswdRes.newpwd = gChangePasswdArg.newpwd;
      if (storage.changePasswdWithOld(gChangePasswdArg.username.getString(), gChangePasswdArg.newpwd.getString(), gChangePasswdArg.oldpwd.getString()))
        gChangePasswdRes.retcode = 0; 
    } catch (Exception exception) {
      exception.printStackTrace(System.out);
    } 
  }
  
  public void Client(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    GChangePasswdArg gChangePasswdArg = (GChangePasswdArg)paramData1;
    GChangePasswdRes gChangePasswdRes = (GChangePasswdRes)paramData2;
  }
  
  public void OnTimeout() {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GChangePasswd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */