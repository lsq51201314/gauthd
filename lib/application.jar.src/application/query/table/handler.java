package application.query.table;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  public String alias;
  
  public application.table.handler table;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.alias = paramAttributes.getValue("alias");
  }
  
  public void action() {
    xmlobject[] arrayOfXmlobject = this.parent.parent.children;
    for (byte b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof application.table.handler && (arrayOfXmlobject[b]).name.compareTo(this.name) == 0) {
        this.table = (application.table.handler)arrayOfXmlobject[b];
        break;
      } 
    } 
    if (this.table == null)
      System.err.println("In Query '" + this.parent.name + "' table '" + this.name + "' Miss"); 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\query\table\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */