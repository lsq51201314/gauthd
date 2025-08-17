package application.table.update;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String condition;
  
  private String column_name;
  
  private String sql_clause;
  
  private Object[] parameter;
  
  private application.table.column.handler[] column;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.column_name = paramAttributes.getValue("column");
    this.condition = paramAttributes.getValue("condition");
  }
  
  public void action() {
    byte b = 0;
    StringBuffer stringBuffer = new StringBuffer("UPDATE ");
    stringBuffer.append(this.parent.name).append(" SET ");
    application.table.column.handler[] arrayOfHandler = ((application.table.handler)this.parent).column;
    String[] arrayOfString = this.column_name.split("[ \n\t,]+");
    this.column_name = null;
    this.column = new application.table.column.handler[arrayOfString.length];
    int i;
    for (i = 0; i < arrayOfString.length; i++) {
      byte b1 = 0;
      while (true) {
        if (b1 < arrayOfHandler.length) {
          if ((arrayOfHandler[b1]).name.compareTo(arrayOfString[i]) == 0) {
            this.column[b++] = arrayOfHandler[b1];
            stringBuffer.append(arrayOfString[i]).append("=?,");
            break;
          } 
          b1++;
          continue;
        } 
        System.err.println("UPDATE '" + this.name + "' REF '" + this.parent.name + "." + arrayOfString[i] + "' Miss");
        break;
      } 
    } 
    stringBuffer.setCharAt(stringBuffer.lastIndexOf(","), ' ');
    if (this.condition != null) {
      stringBuffer.append(this.condition);
      i = this.condition.length();
      for (byte b1 = 0; b1 < i; b1++) {
        if (this.condition.charAt(b1) == '?')
          b++; 
      } 
    } 
    this.parameter = new Object[b];
    this.sql_clause = stringBuffer.toString();
  }
  
  public String toString() {
    return this.sql_clause;
  }
  
  public int execute(Object[] paramArrayOfObject, String paramString) throws Exception {
    return ((application.table.handler)this.parent).executeUpdate(this.sql_clause, this.parameter, paramArrayOfObject, paramString);
  }
  
  public int execute(Object[] paramArrayOfObject) throws Exception {
    return execute(paramArrayOfObject, (String)null);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\tabl\\update\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */