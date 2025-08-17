package application;

import application.connection.handler;
import application.driver.handler;
import application.procedure.handler;
import application.query.handler;
import application.table.handler;
import com.goldhuman.xml.xmlobject;
import java.util.HashSet;
import java.util.Vector;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  public static boolean debug;
  
  private void traverse(Class paramClass) {
    xmlobject[] arrayOfXmlobject = this.children;
    HashSet<String> hashSet = new HashSet();
    Vector vector = new Vector();
    for (byte b = 0; b < arrayOfXmlobject.length; b++) {
      if (paramClass.isInstance(arrayOfXmlobject[b]))
        if (hashSet.add((arrayOfXmlobject[b]).name)) {
          arrayOfXmlobject[b].action();
        } else {
          System.err.println("Duplicate " + paramClass.getName() + " " + (arrayOfXmlobject[b]).name);
        }  
    } 
  }
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    String str = paramAttributes.getValue("debug");
    debug = (str != null && str.compareTo("true") == 0);
  }
  
  public void action() {
    traverse(handler.class);
    traverse(handler.class);
    traverse(handler.class);
    traverse(handler.class);
    traverse(handler.class);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */