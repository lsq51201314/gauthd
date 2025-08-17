package application.table.delete;

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
    int i = this.condition.length();
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      if (this.condition.charAt(b2) == '?')
        b1++; 
    } 
    this.parameter = new Object[b1];
    this.sql_clause = "DELETE FROM " + this.parent.name + " " + this.condition;
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


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\table\delete\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */