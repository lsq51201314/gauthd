package application.table.insert;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String condition;
  
  private String sql_clause;
  
  private Object[] parameter;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.condition = paramAttributes.getValue("condition");
  }
  
  public void action() {
    if (this.condition == null) {
      application.table.column.handler[] arrayOfHandler = ((application.table.handler)this.parent).column;
      StringBuffer stringBuffer1 = new StringBuffer("(");
      StringBuffer stringBuffer2 = new StringBuffer("(");
      this.parameter = new Object[arrayOfHandler.length];
      for (byte b = 0; b < arrayOfHandler.length; b++) {
        stringBuffer1.append((arrayOfHandler[b]).name).append(',');
        stringBuffer2.append("?,");
      } 
      stringBuffer1.setCharAt(stringBuffer1.lastIndexOf(","), ')');
      stringBuffer2.setCharAt(stringBuffer2.lastIndexOf(","), ')');
      this.sql_clause = "INSERT INTO " + this.parent.name + " " + stringBuffer1 + " VALUES " + stringBuffer2;
    } else {
      int i = this.condition.length();
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        if (this.condition.charAt(b2) == '?')
          b1++; 
      } 
      this.parameter = new Object[b1];
      this.sql_clause = "INSERT INTO " + this.parent.name + " " + this.condition;
    } 
  }
  
  public String toString() {
    return this.sql_clause;
  }
  
  public int execute(Object[] paramArrayOfObject, String paramString) throws Exception {
    return ((application.table.handler)this.parent).executeUpdate(this.sql_clause, this.parameter, paramArrayOfObject, paramString);
  }
  
  public int execute(Object[] paramArrayOfObject) throws Exception {
    return execute(paramArrayOfObject, null);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\table\insert\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */