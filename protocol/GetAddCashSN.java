package protocol;

import com.goldhuman.IO.PollIO;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;

public final class GetAddCashSN extends Rpc {
  public void Server(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    GetAddCashSNArg getAddCashSNArg = (GetAddCashSNArg)paramData1;
    GetAddCashSNRes getAddCashSNRes = (GetAddCashSNRes)paramData2;
  }
  
  public void Client(Rpc.Data paramData1, Rpc.Data paramData2) throws ProtocolException {
    GetAddCashSNArg getAddCashSNArg = (GetAddCashSNArg)paramData1;
    GetAddCashSNRes getAddCashSNRes = (GetAddCashSNRes)paramData2;
    GAuthServer.GetLog().info("GetAddCashSN Client: retcode=" + getAddCashSNRes.retcode + ",userid=" + getAddCashSNRes.userid + ",zoneid=" + getAddCashSNRes.zoneid + ",sn=" + getAddCashSNRes.sn);
    if (0 != getAddCashSNRes.retcode) {
      if (getAddCashSNRes.retcode >= 1 && getAddCashSNRes.retcode <= 4)
        getAddCashSNRes.retcode = -1; 
      storage.useCash(new Integer(getAddCashSNRes.userid), new Integer(getAddCashSNRes.zoneid), new Integer(getAddCashSNRes.sn), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(getAddCashSNRes.retcode));
      GAuthServer.GetInstance().SendUseCash_Re(getAddCashSNRes.userid, getAddCashSNRes.zoneid, getAddCashSNRes.retcode);
      return;
    } 
    int i = storage.useCash(new Integer(getAddCashSNRes.userid), new Integer(getAddCashSNRes.zoneid), new Integer(getAddCashSNRes.sn), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(2));
    if (i < 0) {
      GAuthServer.GetInstance().SendUseCash_Re(getAddCashSNRes.userid, getAddCashSNRes.zoneid, i);
      return;
    } 
    Object[] arrayOfObject = storage.getUseCashNow(new Integer(getAddCashSNRes.userid), new Integer(getAddCashSNRes.zoneid), new Integer(getAddCashSNRes.sn));
    if (null == arrayOfObject || arrayOfObject.length < 1 || null == arrayOfObject[0] || ((Object[])arrayOfObject[0]).length < 6 || null == ((Object[])arrayOfObject[0])[5]) {
      GAuthServer.GetInstance().SendUseCash_Re(getAddCashSNRes.userid, getAddCashSNRes.zoneid, -12);
      return;
    } 
    Integer integer = (Integer)((Object[])arrayOfObject[0])[5];
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    Session session = gAuthServer.GetSessionbyZoneid(new Integer(getAddCashSNRes.zoneid));
    AddCash addCash = (AddCash)Protocol.Create("ADDCASH");
    addCash.userid = getAddCashSNRes.userid;
    addCash.zoneid = getAddCashSNRes.zoneid;
    addCash.sn = getAddCashSNRes.sn;
    addCash.cash = integer.intValue();
    i = storage.useCash(new Integer(getAddCashSNRes.userid), new Integer(getAddCashSNRes.zoneid), new Integer(getAddCashSNRes.sn), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(3));
    if (0 != i) {
      GAuthServer.GetInstance().SendUseCash_Re(getAddCashSNRes.userid, getAddCashSNRes.zoneid, i);
    } else if (null != session && gAuthServer.Send(session, addCash)) {
      PollIO.WakeUp();
    } else {
      GAuthServer.GetInstance().SendUseCash_Re(getAddCashSNRes.userid, getAddCashSNRes.zoneid, -14);
    } 
  }
  
  public void OnTimeout() {
    GetAddCashSNArg getAddCashSNArg = (GetAddCashSNArg)this.argument;
    GAuthServer.GetInstance().SendUseCash_Re(getAddCashSNArg.userid, getAddCashSNArg.zoneid, -15);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GetAddCashSN.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */