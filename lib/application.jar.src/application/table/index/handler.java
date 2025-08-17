package application.table.index;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String column_name;
  
  public boolean unique = false;
  
  public application.table.column.handler[] column;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.column_name = paramAttributes.getValue("column");
    String str = paramAttributes.getValue("unique");
    if (str != null && str.compareTo("true") == 0)
      this.unique = true; 
  }
  
  public void action() {
    String[] arrayOfString = this.column_name.split("[ \n\t,]+");
    this.column_name = null;
    this.column = new application.table.column.handler[arrayOfString.length];
    application.table.column.handler[] arrayOfHandler = ((application.table.handler)this.parent).column;
    for (byte b = 0; b < arrayOfString.length; b++) {
      byte b1 = 0;
      while (true) {
        if (b1 < arrayOfHandler.length) {
          if ((arrayOfHandler[b1]).name.compareTo(arrayOfString[b]) == 0) {
            this.column[b] = arrayOfHandler[b1];
            break;
          } 
          b1++;
          continue;
        } 
        System.err.println("INDEX '" + this.name + "' REF '" + this.parent.name + "." + arrayOfString[b] + "' Miss");
        break;
      } 
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("CREATE " + (this.unique ? "UNIQUE " : "") + "INDEX " + this.name + " ON " + this.parent.name + " (");
    for (byte b = 0; b < this.column.length; b++)
      stringBuffer.append((this.column[b]).name).append(','); 
    return stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(",")).append(")\n").toString();
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\table\index\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */