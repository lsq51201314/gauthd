package application.query.select;

import application.query.select.cache.handler;
import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private String sql_clause;
  
  private String condition;
  
  private boolean unique;
  
  public Object[] parameter;
  
  private handler cache;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.condition = paramAttributes.getValue("condition");
    String str = paramAttributes.getValue("unique");
    this.unique = (str != null && str.compareTo("true") == 0);
  }
  
  public void action() {
    application.query.column.handler[] arrayOfHandler = ((application.query.handler)this.parent).column;
    application.query.table.handler[] arrayOfHandler1 = ((application.query.handler)this.parent).table;
    StringBuffer stringBuffer = new StringBuffer("SELECT ");
    if (this.unique)
      stringBuffer.append("DISTINCT "); 
    byte b1;
    for (b1 = 0; b1 < arrayOfHandler.length; b1++)
      stringBuffer.append(((arrayOfHandler[b1]).compute == null) ? (arrayOfHandler[b1]).canonical_name : (arrayOfHandler[b1]).compute).append(" AS ").append((arrayOfHandler[b1]).name).append(','); 
    stringBuffer.setCharAt(stringBuffer.lastIndexOf(","), ' ');
    stringBuffer.append("FROM ");
    for (b1 = 0; b1 < arrayOfHandler1.length; b1++) {
      stringBuffer.append((arrayOfHandler1[b1]).name);
      if ((arrayOfHandler1[b1]).alias != null)
        stringBuffer.append(' ').append((arrayOfHandler1[b1]).alias); 
      stringBuffer.append(',');
    } 
    stringBuffer.setCharAt(stringBuffer.lastIndexOf(","), ' ');
    b1 = 0;
    if (this.condition != null) {
      stringBuffer.append(this.condition);
      int i = this.condition.length();
      for (byte b = 0; b < i; b++) {
        if (this.condition.charAt(b) == '?')
          b1++; 
      } 
    } 
    this.parameter = new Object[b1];
    this.sql_clause = stringBuffer.toString();
    xmlobject[] arrayOfXmlobject = this.children;
    for (byte b2 = 0; b2 < arrayOfXmlobject.length; b2++) {
      if (arrayOfXmlobject[b2] instanceof handler) {
        arrayOfXmlobject[b2].action();
        this.cache = (handler)arrayOfXmlobject[b2];
        break;
      } 
    } 
  }
  
  public String toString() {
    return this.sql_clause;
  }
  
  public Object[] execute(Object[] paramArrayOfObject, String paramString) throws Exception {
    Object[] arrayOfObject;
    if (this.cache != null && (arrayOfObject = this.cache.search(paramArrayOfObject)) != null)
      return arrayOfObject; 
    arrayOfObject = ((application.query.handler)this.parent).executeQuery(this.sql_clause, this.parameter, paramArrayOfObject, paramString);
    if (this.cache != null)
      this.cache.append(paramArrayOfObject, arrayOfObject); 
    return arrayOfObject;
  }
  
  public Object[] execute(Object[] paramArrayOfObject) throws Exception {
    return execute(paramArrayOfObject, (String)null);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\query\select\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */