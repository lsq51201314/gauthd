package application.query;

import application.query.column.handler;
import application.query.select.handler;
import application.query.table.handler;
import com.goldhuman.xml.xmlobject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private static Map instance = new HashMap<Object, Object>();
  
  private Map select_map = new HashMap<Object, Object>();
  
  public handler[] column;
  
  public handler[] table;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    instance.put(this.name, this);
  }
  
  public void action() {
    xmlobject[] arrayOfXmlobject = this.children;
    Vector<xmlobject> vector = new Vector();
    byte b;
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        vector.add(arrayOfXmlobject[b]);
      } 
    } 
    this.table = new handler[vector.size()];
    for (b = 0; b < vector.size(); b++)
      this.table[b] = (handler)vector.get(b); 
    vector.clear();
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        vector.add(arrayOfXmlobject[b]);
      } 
    } 
    this.column = new handler[vector.size()];
    for (b = 0; b < vector.size(); b++)
      this.column[b] = (handler)vector.get(b); 
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        if (this.select_map.put((arrayOfXmlobject[b]).name, arrayOfXmlobject[b]) != null)
          System.err.println("In Query '" + this.name + "' Duplicate select '" + (arrayOfXmlobject[b]).name + "'"); 
      } 
    } 
    if (application.handler.debug)
      System.err.println(this); 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Iterator<Map.Entry> iterator = this.select_map.entrySet().iterator();
    while (iterator.hasNext())
      stringBuffer.append(((Map.Entry)iterator.next()).getValue()).append("\n"); 
    return stringBuffer.toString();
  }
  
  public Object[] executeQuery(String paramString1, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, String paramString2) throws Exception {
    if (paramArrayOfObject2.length != paramArrayOfObject1.length)
      throw new SQLException("Parameter number error"); 
    Connection connection = application.connection.handler.get(paramString2);
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Vector<Object[]> vector = new Vector();
    try {
      preparedStatement = connection.prepareStatement(paramString1);
      for (byte b = 0; b < paramArrayOfObject2.length; b++)
        preparedStatement.setObject(b + 1, paramArrayOfObject2[b]); 
      resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Object[] arrayOfObject = new Object[this.column.length];
        for (byte b1 = 0; b1 < this.column.length; b1++)
          arrayOfObject[b1] = resultSet.getObject(b1 + 1); 
        vector.add(arrayOfObject);
      } 
    } catch (Exception exception) {
      throw exception;
    } finally {
      try {
        if (resultSet != null)
          resultSet.close(); 
      } catch (Exception exception) {}
      try {
        if (preparedStatement != null)
          preparedStatement.close(); 
      } catch (Exception exception) {}
      application.connection.handler.put(connection);
    } 
    return vector.toArray();
  }
  
  public handler select(String paramString) {
    return (handler)this.select_map.get(paramString);
  }
  
  public static handler get(String paramString) {
    return (handler)instance.get(paramString);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\query\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */