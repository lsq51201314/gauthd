package application.driver;

import com.goldhuman.xml.xmlobject;
import java.sql.Driver;
import java.sql.DriverManager;

public class handler extends xmlobject {
  public void action() {
    if (application.handler.debug)
      System.err.println("Load Driver " + this.name); 
    try {
      DriverManager.registerDriver((Driver)Class.forName(this.name).newInstance());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\driver\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */