import com.goldhuman.Common.Conf;
import com.goldhuman.Common.Octets;
import com.goldhuman.Common.ThreadPool;
import com.goldhuman.IO.PollIO;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.xml.parser;
import java.io.FileInputStream;
import protocol.GAuthServer;

public class authd {
  public static void main(String[] paramArrayOfString) {
    try {
      Conf conf = Conf.GetInstance("/etc/authd.conf");
      GAuthServer gAuthServer = GAuthServer.GetInstance();
      (GAuthServer.GetInstance()).shared_key = new Octets(conf.find("GAuthServer", "shared_key").getBytes());
      Protocol.Server((Manager)gAuthServer);
      System.out.println("authd:: add PollIO task.");
      try {
        parser.parse(new FileInputStream(paramArrayOfString[0]));
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      ThreadPool.AddTask(PollIO.GetTask());
      while (true) {
        try {
          while (true)
            Thread.sleep(1000L); 
          break;
        } catch (Exception exception) {}
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\authd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */