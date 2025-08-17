package protocol;

import com.goldhuman.Common.Octets;
import com.goldhuman.IO.PollIO;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.IO.Protocol.State;
import com.goldhuman.account.storage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class GAuthServer extends Manager {
  private static final Log log = LogFactory.getLog(GAuthServer.class);
  
  protected static Map usermap = new HashMap<Object, Object>();
  
  protected static Map zonemap = Collections.synchronizedMap(new HashMap<Object, Object>());
  
  protected static Map aidmap = Collections.synchronizedMap(new HashMap<Object, Object>());
  
  protected static Map accntmap = new HashMap<Object, Object>();
  
  protected static Manager manager = null;
  
  protected static Session session = null;
  
  private static GAuthServer instance = new GAuthServer();
  
  public Octets shared_key;
  
  protected void OnAddSession(Session paramSession) {
    System.out.println("GAuthServer::OnAddSession " + paramSession);
  }
  
  protected void OnDelSession(Session paramSession) {
    GetLog().info("GAuthServer::nDelSession " + paramSession);
    Integer integer1 = (Integer)zonemap.get(paramSession);
    Integer integer2 = (Integer)aidmap.get(paramSession);
    if (integer1 != null && integer2 != null) {
      GetLog().info("GAuthServer::OnDelSession " + paramSession + "zoneid=" + integer1 + ", aid=" + integer2);
    } else {
      GetLog().info("GAuthServer::OnDelSession " + paramSession);
    } 
    zonemap.remove(paramSession);
    aidmap.remove(paramSession);
  }
  
  protected State GetInitState() {
    return State.Get("GAuthServer");
  }
  
  protected String Identification() {
    return "GAuthServer";
  }
  
  public static GAuthServer GetInstance() {
    return instance;
  }
  
  public static Log GetLog() {
    return log;
  }
  
  public Session GetSessionbyZoneid(Integer paramInteger) {
    GAuthServer gAuthServer = GetInstance();
    if (!zonemap.containsValue(paramInteger))
      return null; 
    Set set = zonemap.entrySet();
    for (Map.Entry entry : set) {
      if (((Integer)entry.getValue()).intValue() == paramInteger.intValue())
        return (Session)entry.getKey(); 
    } 
    return null;
  }
  
  public void SetUseCashSession(Manager paramManager, Session paramSession) {
    this;
    manager = paramManager;
    this;
    session = paramSession;
  }
  
  public boolean SendUseCash_Re(int paramInt1, int paramInt2, int paramInt3) {
    GetLog().info("SendUseCash_Re: retcode=" + paramInt3 + ",userid=" + paramInt1 + ",zoneid=" + paramInt2);
    if (null == manager || null == session)
      return false; 
    UseCash_Re useCash_Re = (UseCash_Re)Protocol.Create("USECASH_RE");
    useCash_Re.retcode = paramInt3;
    useCash_Re.userid = paramInt1;
    useCash_Re.zoneid = paramInt2;
    return manager.Send(session, useCash_Re);
  }
  
  static {
    (new Timer()).schedule(new TimerTask() {
          public void run() {
            try {
              GAuthServer gAuthServer = GAuthServer.GetInstance();
              Object[] arrayOfObject = null;
              arrayOfObject = storage.getUseCashNow(new Integer(0));
              if (null != arrayOfObject)
                for (byte b = 0; b < arrayOfObject.length; b++) {
                  Object[] arrayOfObject1 = (Object[])arrayOfObject[b];
                  Integer integer1 = (Integer)arrayOfObject1[0];
                  Integer integer2 = (Integer)arrayOfObject1[1];
                  Integer integer3 = (Integer)arrayOfObject1[2];
                  int i = storage.useCash(new Integer(integer1.intValue()), new Integer(integer2.intValue()), new Integer(integer3.intValue()), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(0));
                  if (0 == i) {
                    Session session = gAuthServer.GetSessionbyZoneid(integer2);
                    GetAddCashSNArg getAddCashSNArg = new GetAddCashSNArg();
                    getAddCashSNArg.userid = integer1.intValue();
                    getAddCashSNArg.zoneid = integer2.intValue();
                    GetAddCashSN getAddCashSN = (GetAddCashSN)Rpc.Call("GetAddCashSN", getAddCashSNArg);
                    if (null != session && gAuthServer.Send(session, (Protocol)getAddCashSN)) {
                      PollIO.WakeUp();
                      Thread.sleep(5000L);
                    } 
                  } 
                  GAuthServer.GetLog().info("UseCashTimerTask: status=0,userid=" + integer1 + ",zoneid=" + integer2 + ",sn=" + integer3 + ",ret=" + i);
                }  
              arrayOfObject = storage.getUseCashNow(new Integer(1));
              if (null != arrayOfObject)
                for (byte b = 0; b < arrayOfObject.length; b++) {
                  Object[] arrayOfObject1 = (Object[])arrayOfObject[b];
                  Integer integer1 = (Integer)arrayOfObject1[0];
                  Integer integer2 = (Integer)arrayOfObject1[1];
                  Integer integer3 = (Integer)arrayOfObject1[2];
                  Session session = gAuthServer.GetSessionbyZoneid(integer2);
                  GetAddCashSNArg getAddCashSNArg = new GetAddCashSNArg();
                  getAddCashSNArg.userid = integer1.intValue();
                  getAddCashSNArg.zoneid = integer2.intValue();
                  GetAddCashSN getAddCashSN = (GetAddCashSN)Rpc.Call("GetAddCashSN", getAddCashSNArg);
                  if (null != session && gAuthServer.Send(session, (Protocol)getAddCashSN))
                    PollIO.WakeUp(); 
                  GAuthServer.GetLog().info("UseCashTimerTask: status=1,userid=" + integer1 + ",zoneid=" + integer2 + ",sn=" + integer3 + ",session=" + session);
                }  
              arrayOfObject = storage.getUseCashNow(new Integer(2));
              if (null != arrayOfObject)
                for (byte b = 0; b < arrayOfObject.length; b++) {
                  Object[] arrayOfObject1 = (Object[])arrayOfObject[b];
                  Integer integer1 = (Integer)arrayOfObject1[0];
                  Integer integer2 = (Integer)arrayOfObject1[1];
                  Integer integer3 = (Integer)arrayOfObject1[2];
                  int i = storage.useCash(new Integer(integer1.intValue()), new Integer(integer2.intValue()), new Integer(integer3.intValue()), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(3));
                  GAuthServer.GetLog().info("UseCashTimerTask: status=2,userid=" + integer1 + ",zoneid=" + integer2 + ",sn=" + integer3 + ",ret=" + i);
                }  
              arrayOfObject = storage.getUseCashNow(new Integer(3));
              if (null != arrayOfObject)
                for (byte b = 0; b < arrayOfObject.length; b++) {
                  Object[] arrayOfObject1 = (Object[])arrayOfObject[b];
                  Integer integer1 = (Integer)arrayOfObject1[0];
                  Integer integer2 = (Integer)arrayOfObject1[1];
                  Integer integer3 = (Integer)arrayOfObject1[2];
                  Integer integer4 = (Integer)arrayOfObject1[5];
                  Session session = gAuthServer.GetSessionbyZoneid(integer2);
                  AddCash addCash = (AddCash)Protocol.Create("ADDCASH");
                  addCash.userid = integer1.intValue();
                  addCash.zoneid = integer2.intValue();
                  addCash.sn = integer3.intValue();
                  addCash.cash = integer4.intValue();
                  if (null != session && gAuthServer.Send(session, addCash))
                    PollIO.WakeUp(); 
                  GAuthServer.GetLog().info("UseCashTimerTask: status=3,userid=" + integer1 + ",zoneid=" + integer2 + ",sn=" + integer3 + ",session=" + session);
                }  
            } catch (Exception exception) {
              exception.printStackTrace();
            } 
          }
        }300000L, 300000L);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GAuthServer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */