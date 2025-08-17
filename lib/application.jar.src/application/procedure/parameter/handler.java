package application.procedure.parameter;

import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  public String sql_type;
  
  public Class java_type;
  
  public boolean in;
  
  public boolean out;
  
  public int out_type = 0;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.sql_type = paramAttributes.getValue("sql-type");
    try {
      this.java_type = Class.forName(paramAttributes.getValue("java-type"));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    String str = paramAttributes.getValue("in");
    this.in = (str != null && str.compareTo("true") == 0);
    str = paramAttributes.getValue("out");
    this.out = (str != null && str.compareTo("true") == 0);
    try {
      this.out_type = Class.forName("java.sql.Types").getField(this.sql_type.split("[\\W]+")[0].toUpperCase()).getInt(null);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void action() {}
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\procedure\parameter\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */