package application.query.column;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String column_name;
  
  public String canonical_name;
  
  public String compute;
  
  public Class java_type;
  
  public application.table.column.handler column;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.column_name = paramAttributes.getValue("column");
    this.compute = paramAttributes.getValue("compute");
    try {
      this.java_type = Class.forName(paramAttributes.getValue("java-type"));
    } catch (Exception exception) {}
  }
  
  public void action() {
    byte b;
    if (this.compute != null) {
      if (this.java_type == null)
        System.err.println("In Query '" + this.parent.name + "' Compute Column '" + this.compute + "' MUST Have java-type"); 
      return;
    } 
    String[] arrayOfString = this.column_name.split("[\\.]+");
    application.query.table.handler[] arrayOfHandler = ((application.query.handler)this.parent).table;
    application.table.column.handler[] arrayOfHandler1 = null;
    switch (arrayOfString.length) {
      case 2:
        for (b = 0; b < arrayOfHandler.length; b++) {
          if ((arrayOfHandler[b]).name.compareTo(arrayOfString[0]) == 0 || ((arrayOfHandler[b]).alias != null && (arrayOfHandler[b]).alias.compareTo(arrayOfString[0]) == 0)) {
            arrayOfHandler1 = (arrayOfHandler[b]).table.column;
            this.canonical_name = (((arrayOfHandler[b]).alias != null) ? (arrayOfHandler[b]).alias : (arrayOfHandler[b]).name) + ".";
            break;
          } 
        } 
        if (arrayOfHandler1 != null)
          for (b = 0; b < arrayOfHandler1.length; b++) {
            if ((arrayOfHandler1[b]).name.compareTo(arrayOfString[1]) == 0) {
              this.column = arrayOfHandler1[b];
              this.canonical_name += arrayOfString[1];
              break;
            } 
          }  
        break;
      case 1:
        for (b = 0; b < arrayOfHandler.length; b++) {
          arrayOfHandler1 = (arrayOfHandler[b]).table.column;
          for (byte b1 = 0; b1 < arrayOfHandler1.length; b1++) {
            if ((arrayOfHandler1[b1]).name.compareTo(arrayOfString[0]) == 0) {
              this.column = arrayOfHandler1[b1];
              this.canonical_name = (((arrayOfHandler[b]).alias != null) ? (arrayOfHandler[b]).alias : (arrayOfHandler[b]).name) + "." + arrayOfString[0];
              // Byte code: goto -> 482
            } 
          } 
        } 
        break;
      default:
        System.err.println("In Query '" + this.parent.name + "' Column format MUST [table].column ");
        return;
    } 
    if (this.column == null)
      System.err.println("In Query '" + this.parent.name + "' Column '" + this.column_name + "' Miss"); 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\query\column\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */