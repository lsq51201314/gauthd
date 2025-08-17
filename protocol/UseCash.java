package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.PollIO;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;

public final class UseCash extends Protocol {
  public int userid;
  
  public int zoneid;
  
  public int aid;
  
  public int point;
  
  public int cash;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.zoneid);
    paramOctetsStream.marshal(this.aid);
    paramOctetsStream.marshal(this.point);
    paramOctetsStream.marshal(this.cash);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.userid = paramOctetsStream.unmarshal_int();
    this.zoneid = paramOctetsStream.unmarshal_int();
    this.aid = paramOctetsStream.unmarshal_int();
    this.point = paramOctetsStream.unmarshal_int();
    this.cash = paramOctetsStream.unmarshal_int();
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
    GAuthServer.GetLog().info("UseCash: userid=" + this.userid + ",zoneid=" + this.zoneid + ",aid=" + this.aid + ",point=" + this.point + ",cash=" + this.cash);
    GAuthServer gAuthServer = GAuthServer.GetInstance();
    Session session = gAuthServer.GetSessionbyZoneid(new Integer(this.zoneid));
    UseCash_Re useCash_Re = (UseCash_Re)Protocol.Create("USECASH_RE");
    useCash_Re.userid = this.userid;
    useCash_Re.zoneid = this.zoneid;
    if (null == session) {
      useCash_Re.retcode = -6;
      paramManager.Send(paramSession, useCash_Re);
      return;
    } 
    GetAddCashSNArg getAddCashSNArg = new GetAddCashSNArg();
    getAddCashSNArg.userid = this.userid;
    getAddCashSNArg.zoneid = this.zoneid;
    GetAddCashSN getAddCashSN = (GetAddCashSN)Rpc.Call("GetAddCashSN", getAddCashSNArg);
    if (gAuthServer.Send(session, (Protocol)getAddCashSN)) {
      PollIO.WakeUp();
      useCash_Re.retcode = 0;
      gAuthServer.SetUseCashSession(paramManager, paramSession);
    } else {
      useCash_Re.retcode = -13;
      paramManager.Send(paramSession, useCash_Re);
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\UseCash.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */