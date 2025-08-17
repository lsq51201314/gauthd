package protocol;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SM implements SMMBean {
  private List<SMMBean> smes = new ArrayList<SMMBean>();
  
  public SM() throws Exception {
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName("SM:type=manager");
    mBeanServer.registerMBean(this, objectName);
  }
  
  public synchronized void add(SMMBean paramSMMBean) {
    if (paramSMMBean != this)
      this.smes.add(paramSMMBean); 
  }
  
  public synchronized void save(String paramString) throws Exception {
    for (SMMBean sMMBean : this.smes)
      sMMBean.save(paramString); 
  }
  
  public synchronized void load(String paramString) {
    for (SMMBean sMMBean : this.smes)
      sMMBean.load(paramString); 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\SM.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */