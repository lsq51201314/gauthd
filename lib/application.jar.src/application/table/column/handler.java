package application.table.column;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String sql_type;
  
  private Class java_type;
  
  private boolean not_null = false;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.sql_type = paramAttributes.getValue("sql-type");
    try {
      this.java_type = Class.forName(paramAttributes.getValue("java-type"));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    String str = paramAttributes.getValue("not-null");
    this.not_null = (str != null && str.compareTo("true") == 0);
  }
  
  public void action() {}
  
  public String toString() {
    return this.name + " " + this.sql_type + (this.not_null ? " NOT " : " ") + "NULL";
  }
  
  public Class type() {
    return this.java_type;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\table\column\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */