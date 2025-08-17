package application.table;

import application.table.column.handler;
import application.table.delete.handler;
import application.table.index.handler;
import application.table.insert.handler;
import application.table.primarykey.handler;
import application.table.update.handler;
import com.goldhuman.xml.xmlobject;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private static Map instance = new HashMap<Object, Object>();
  
  private Set conn_set = new HashSet();
  
  private String operate;
  
  private Map delete_map = new HashMap<Object, Object>();
  
  private Map insert_map = new HashMap<Object, Object>();
  
  private Map update_map = new HashMap<Object, Object>();
  
  public handler[] column;
  
  public handler primarykey;
  
  public handler[] index;
  
  public Vector keys = new Vector();
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    this.conn_set.addAll(Arrays.asList(paramAttributes.getValue("connection").split("[ \n\t,]+")));
    this.operate = paramAttributes.getValue("operate");
    instance.put(this.name, this);
  }
  
  public void action() {
    xmlobject[] arrayOfXmlobject = this.parent.children;
    for (String str : this.conn_set) {
      boolean bool = false;
      if (application.handler.debug)
        System.err.println("Table '" + this.name + "' Bind '" + str + "'"); 
      for (byte b1 = 0; b1 < arrayOfXmlobject.length; b1++) {
        if (arrayOfXmlobject[b1] instanceof application.connection.handler && (arrayOfXmlobject[b1]).name.compareTo(str) == 0) {
          bool = true;
          break;
        } 
      } 
      if (!bool) {
        System.err.println("In Table '" + this.name + "' Connection '" + str + "' Miss");
        return;
      } 
    } 
    arrayOfXmlobject = this.children;
    HashSet<String> hashSet = new HashSet();
    Vector<xmlobject> vector = new Vector();
    byte b;
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        if (hashSet.add((arrayOfXmlobject[b]).name)) {
          vector.add(arrayOfXmlobject[b]);
        } else {
          System.err.println("In Table '" + this.name + "' Duplicate column '" + (arrayOfXmlobject[b]).name + "'");
        } 
      } 
    } 
    this.column = new handler[vector.size()];
    for (b = 0; b < vector.size(); b++)
      this.column[b] = (handler)vector.get(b); 
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        this.primarykey = (handler)arrayOfXmlobject[b];
        break;
      } 
    } 
    hashSet.clear();
    vector.clear();
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        if (hashSet.add((arrayOfXmlobject[b]).name)) {
          vector.add(arrayOfXmlobject[b]);
        } else {
          System.err.println("In Table '" + this.name + "' Duplicate index '" + (arrayOfXmlobject[b]).name + "'");
        } 
      } 
    } 
    this.index = new handler[vector.size()];
    for (b = 0; b < vector.size(); b++)
      this.index[b] = (handler)vector.get(b); 
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        if (this.insert_map.put((arrayOfXmlobject[b]).name, arrayOfXmlobject[b]) != null)
          System.err.println("In Table '" + this.name + "' Duplicate insert '" + (arrayOfXmlobject[b]).name + "'"); 
      } 
    } 
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        if (this.delete_map.put((arrayOfXmlobject[b]).name, arrayOfXmlobject[b]) != null)
          System.err.println("In Table '" + this.name + "' Duplicate delete '" + (arrayOfXmlobject[b]).name + "'"); 
      } 
    } 
    for (b = 0; b < arrayOfXmlobject.length; b++) {
      if (arrayOfXmlobject[b] instanceof handler) {
        arrayOfXmlobject[b].action();
        if (this.update_map.put((arrayOfXmlobject[b]).name, arrayOfXmlobject[b]) != null)
          System.err.println("In Table '" + this.name + "' Duplicate update '" + (arrayOfXmlobject[b]).name + "'"); 
      } 
    } 
    if (this.primarykey != null)
      this.keys.add(this.primarykey.column); 
    if (this.index != null)
      for (b = 0; b < this.index.length; b++) {
        if ((this.index[b]).unique)
          this.keys.add((this.index[b]).column); 
      }  
    Collections.sort(this.keys, new Comparator() {
          public int compare(Object param1Object1, Object param1Object2) {
            return Array.getLength(param1Object1) - Array.getLength(param1Object2);
          }
        });
    LinkedList<handler[]> linkedList = new LinkedList(this.keys);
    this.keys.clear();
    while (!linkedList.isEmpty()) {
      handler[] arrayOfHandler = linkedList.removeFirst();
      ListIterator<handler> listIterator = linkedList.listIterator();
      label120: while (listIterator.hasNext()) {
        handler[] arrayOfHandler1 = (handler[])listIterator.next();
        for (byte b1 = 0; b1 < arrayOfHandler.length; b1++) {
          if (arrayOfHandler[b1] != arrayOfHandler1[b1])
            continue label120; 
        } 
        listIterator.remove();
      } 
      this.keys.add(arrayOfHandler);
    } 
    if (application.handler.debug)
      System.err.println(this); 
    if (this.operate != null) {
      if (this.operate.compareTo("create") == 0)
        Create(); 
      if (this.operate.compareTo("drop") == 0)
        Drop(); 
      if (this.operate.compareTo("replace") == 0) {
        Drop();
        Create();
      } 
    } 
  }
  
  private String DDLTable() {
    StringBuffer stringBuffer = new StringBuffer("CREATE TABLE " + this.name + " (\n");
    for (byte b = 0; b < this.column.length; b++)
      stringBuffer.append('\t').append(this.column[b]).append(",\n"); 
    if (this.primarykey != null)
      stringBuffer.append('\t').append(this.primarykey).append(",\n"); 
    return stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(",")).append(")\n").toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(DDLTable());
    byte b;
    for (b = 0; b < this.index.length; b++)
      stringBuffer.append(this.index[b]); 
    for (b = 0; b < this.keys.size(); b++) {
      handler[] arrayOfHandler = this.keys.get(b);
      stringBuffer.append("Key[").append(b).append("]:");
      for (byte b1 = 0; b1 < arrayOfHandler.length; b1++)
        stringBuffer.append(' ').append((arrayOfHandler[b1]).name); 
      stringBuffer.append('\n');
    } 
    Iterator<Map.Entry> iterator = this.insert_map.entrySet().iterator();
    while (iterator.hasNext())
      stringBuffer.append(((Map.Entry)iterator.next()).getValue()).append("\n"); 
    iterator = this.delete_map.entrySet().iterator();
    while (iterator.hasNext())
      stringBuffer.append(((Map.Entry)iterator.next()).getValue()).append("\n"); 
    iterator = this.update_map.entrySet().iterator();
    while (iterator.hasNext())
      stringBuffer.append(((Map.Entry)iterator.next()).getValue()).append("\n"); 
    return stringBuffer.toString();
  }
  
  private void Drop() {
    Iterator<String> iterator = this.conn_set.iterator();
    while (iterator.hasNext()) {
      Connection connection = application.connection.handler.get(iterator.next());
      try {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE " + this.name);
        statement.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      application.connection.handler.put(connection);
    } 
  }
  
  private void Create() {
    Iterator<String> iterator = this.conn_set.iterator();
    while (iterator.hasNext()) {
      Connection connection = application.connection.handler.get(iterator.next());
      try {
        Statement statement = connection.createStatement();
        statement.addBatch(DDLTable());
        for (byte b = 0; b < this.index.length; b++)
          statement.addBatch(this.index[b].toString()); 
        statement.executeBatch();
        statement.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      application.connection.handler.put(connection);
    } 
  }
  
  public int executeUpdate(String paramString1, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, String paramString2) throws Exception {
    if (paramArrayOfObject2.length != paramArrayOfObject1.length)
      throw new SQLException("Parameter number error"); 
    if (paramString2 != null && !this.conn_set.contains(paramString2))
      throw new SQLException("Connection '" + paramString2 + "' NOT Match"); 
    Connection connection = application.connection.handler.get(paramString2);
    PreparedStatement preparedStatement = null;
    int i = -1;
    try {
      preparedStatement = connection.prepareStatement(paramString1);
      for (byte b = 0; b < paramArrayOfObject2.length; b++)
        preparedStatement.setObject(b + 1, paramArrayOfObject2[b]); 
      i = preparedStatement.executeUpdate();
    } catch (Exception exception) {
      throw exception;
    } finally {
      try {
        if (preparedStatement != null)
          preparedStatement.close(); 
      } catch (Exception exception) {}
      application.connection.handler.put(connection);
    } 
    return i;
  }
  
  public handler delete(String paramString) {
    return (handler)this.delete_map.get(paramString);
  }
  
  public handler insert(String paramString) {
    return (handler)this.insert_map.get(paramString);
  }
  
  public handler update(String paramString) {
    return (handler)this.update_map.get(paramString);
  }
  
  public static handler get(String paramString) {
    return (handler)instance.get(paramString);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\table\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */