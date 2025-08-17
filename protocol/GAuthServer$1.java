package protocol;

import com.goldhuman.IO.PollIO;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.IO.Protocol.Rpc;
import com.goldhuman.IO.Protocol.Session;
import com.goldhuman.account.storage;
import java.util.TimerTask;

class null extends TimerTask {
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
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\GAuthServer$1.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */