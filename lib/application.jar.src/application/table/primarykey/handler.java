package application.table.primarykey;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String column_name;
  
  public application.table.column.handler[] column;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.column_name = paramAttributes.getValue("column");
  }
  
  public void action() {
    String[] arrayOfString = this.column_name.split("[ \n\t,]+");
    this.column_name = null;
    this.column = new application.table.column.handler[arrayOfString.length];
    application.table.column.handler[] arrayOfHandler = ((application.table.handler)this.parent).column;
    for (byte b = 0; b < arrayOfString.length; b++) {
      for (byte b1 = 0; b1 < arrayOfHandler.length; b1++) {
        if ((arrayOfHandler[b1]).name.compareTo(arrayOfString[b]) == 0) {
          this.column[b] = arrayOfHandler[b1];
          break;
        } 
      } 
      if (this.column[b] == null)
        System.err.println("PRIMARY KEY '" + this.name + "' REF '" + this.parent.name + "." + arrayOfString[b] + "' Miss"); 
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("PRIMARY KEY (");
    for (byte b = 0; b < this.column.length; b++)
      stringBuffer.append((this.column[b]).name).append(','); 
    return stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(",")).append(")").toString();
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\table\primarykey\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */