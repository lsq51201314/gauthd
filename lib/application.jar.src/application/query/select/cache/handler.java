package application.query.select.cache;

import com.goldhuman.Common.Cache;
import com.goldhuman.xml.xmlobject;
import org.xml.sax.Attributes;

public class handler extends xmlobject {
  private static String DEFAULT_KEY = "DEFAULT";
  
  private int size = Cache.default_size;
  
  private int timeout = Cache.default_timeout;
  
  public Cache cache;
  
  protected void setattr(Attributes paramAttributes) {
    super.setattr(paramAttributes);
    String str = paramAttributes.getValue("size");
    if (str != null)
      this.size = Integer.parseInt(str); 
    str = paramAttributes.getValue("timeout");
    if (str != null)
      this.timeout = Integer.parseInt(str); 
  }
  
  public void action() {
    Object[] arrayOfObject = ((application.query.select.handler)this.parent).parameter;
    int i = arrayOfObject.length;
    if (i == 0)
      i = 1; 
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++)
      arrayOfInt[b] = b; 
    this.cache = Cache.Create(this.parent.parent.name + "_" + this.parent.name, i + 1, arrayOfInt, this.size, this.timeout);
  }
  
  public Object[] search(Object[] paramArrayOfObject) {
    try {
      Cache.Item item = this.cache.newItem();
      if (paramArrayOfObject.length > 0) {
        for (byte b = 0; b < paramArrayOfObject.length; b++)
          item.set(b, paramArrayOfObject[b]); 
        return (Object[])this.cache.find(item).get(paramArrayOfObject.length);
      } 
      return (Object[])this.cache.find(item.set(0, DEFAULT_KEY)).get(1);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void append(Object[] paramArrayOfObject, Object paramObject) {
    try {
      Cache.Item item = this.cache.newItem();
      if (paramArrayOfObject.length > 0) {
        for (byte b = 0; b < paramArrayOfObject.length; b++)
          item.set(b, paramArrayOfObject[b]); 
        item.set(paramArrayOfObject.length, paramObject).commit();
      } else {
        item.set(0, DEFAULT_KEY).set(1, paramObject).commit();
      } 
    } catch (Exception exception) {}
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\query\select\cache\handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */