package protocol;

import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;
import java.util.Calendar;
import java.util.Date;

public final class GQueryPasswd extends Rpc {
  private static byte[] forbidIPReason = null;
  
  private static byte[] lockReason = null;
  
  private static void initReason() {
    if (forbidIPReason == null || lockReason == null)
      try {
        forbidIPReason = (new String("此IP段不符合设定允许的范围，禁止登录。")).getBytes("UTF-16LE");
        lockReason = (new String("此帐号已被手机锁定，禁止登录。")).getBytes("UTF-16LE");
      } catch (Exception exception) {
        forbidIPReason = (new String("forbidden ip address")).getBytes();
        lockReason = (new String("account locked by mobile")).getBytes();
      }  
  }
  
  private static int bswap(int paramInt) {
    int i = paramInt & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt >> 16 & 0xFF;
    int m = paramInt >> 24 & 0xFF;
    return 0xFF000000 & i << 24 | 0xFF0000 & j << 16 | 0xFF00 & k << 8 | 0xFF & m;
  }
  
  private static String toHexString(byte[] paramArrayOfbyte) {
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfbyte.length * 2);
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      byte b1 = paramArrayOfbyte[b];
      int i = b1 >> 4 & 0xF;
      stringBuffer.append((char)((i >= 10) ? (97 + i - 10) : (48 + i)));
      i = b1 & 0xF;
      stringBuffer.append((char)((i >= 10) ? (97 + i - 10) : (48 + i)));
    } 
    return stringBuffer.toString();
  }
  
  public void Server(Rpc.Data paramData1, Rpc.Data paramData2, Manager paramManager, Session paramSession) throws ProtocolException {
    GQueryPasswdArg gQueryPasswdArg = (GQueryPasswdArg)paramData1;
    GQueryPasswdRes gQueryPasswdRes = (GQueryPasswdRes)paramData2;
    try {
      System.out.println("GQueryPasswd:account is " + gQueryPasswdArg.account.getString() + " , login ip is " + gQueryPasswdArg.loginip);
      Object[] arrayOfObject1 = storage.acquireIdPasswd(gQueryPasswdArg.account.getString());
      if (arrayOfObject1 == null) {
        GAuthServer.GetLog().info("GQueryPasswd:can not find user " + gQueryPasswdArg.account.getString());
        gQueryPasswdRes.retcode = 2;
        return;
      } 
      if (arrayOfObject1[0] == null)
        System.out.println("userinfo[0] is null."); 
      if (arrayOfObject1[1] == null)
        System.out.println("userinfo[1] is null."); 
      gQueryPasswdRes.response.replace((byte[])arrayOfObject1[1]);
      gQueryPasswdRes.userid = ((Integer)arrayOfObject1[0]).intValue();
      gQueryPasswdRes.retcode = 0;
      Integer integer = new Integer(gQueryPasswdRes.userid);
      storage.deleteTimeoutForbid(integer);
      GAuthServer gAuthServer = GAuthServer.GetInstance();
      QueryUserForbid_Re queryUserForbid_Re = (QueryUserForbid_Re)Rpc.Create("QUERYUSERFORBID_RE");
      queryUserForbid_Re.userid = integer.intValue();
      queryUserForbid_Re.list_type = 0;
      Object[] arrayOfObject2 = storage.acquireForbid(integer);
      boolean bool = false;
      if (arrayOfObject2 != null)
        for (byte b = 0; b < arrayOfObject2.length; b++) {
          Object[] arrayOfObject = (Object[])arrayOfObject2[b];
          GRoleForbid gRoleForbid = new GRoleForbid();
          gRoleForbid.type = (byte)((Integer)arrayOfObject[1]).intValue();
          if (gRoleForbid.type == 100)
            bool = true; 
          gRoleForbid.createtime = (int)(((Date)arrayOfObject[2]).getTime() / 1000L);
          gRoleForbid.time = ((Integer)arrayOfObject[3]).intValue() + gRoleForbid.createtime - (int)(Calendar.getInstance().getTime().getTime() / 1000L);
          gRoleForbid.reason.replace((byte[])arrayOfObject[4]);
          GAuthServer.GetLog().info("===userid=" + integer + ", Forbid type " + gRoleForbid.type + ", timeleft=" + gRoleForbid.time);
          queryUserForbid_Re.forbid.add(gRoleForbid);
        }  
      if (!bool) {
        int i = storage.checkIPLimit(integer, new Integer(bswap(gQueryPasswdArg.loginip)));
        if (i == 1 || i == 2) {
          initReason();
          GRoleForbid gRoleForbid = new GRoleForbid();
          gRoleForbid.type = 100;
          gRoleForbid.createtime = (int)((new Date()).getTime() / 1000L);
          gRoleForbid.time = 31536000;
          gRoleForbid.reason.replace((i == 2) ? forbidIPReason : lockReason);
          queryUserForbid_Re.forbid.add(gRoleForbid);
        } 
      } 
      gAuthServer.Send(paramSession, queryUserForbid_Re);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void Client(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    GQueryPasswdArg gQueryPasswdArg = (GQueryPasswdArg)paramData1;
    GQueryPasswdRes gQueryPasswdRes = (GQueryPasswdRes)paramData2;
  }
  
  public void OnTimeout() {}
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GQueryPasswd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */