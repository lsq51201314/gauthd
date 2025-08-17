package com.goldhuman.Common.Security;

import com.goldhuman.Common.Octets;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Security implements Cloneable {
  private static final HashMap map = new HashMap<Object, Object>();
  
  private int type;
  
  public void SetParameter(Octets paramOctets) {}
  
  public void GetParameter(Octets paramOctets) {}
  
  public Octets Update(Octets paramOctets) {
    return paramOctets;
  }
  
  public Octets Final(Octets paramOctets) {
    return paramOctets;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static Security Create(String paramString) {
    Security security = (Security)map.get(paramString.toUpperCase());
    return (security == null) ? new NullSecurity() : (Security)security.clone();
  }
  
  public static Security Create(int paramInt) {
    return Create(Integer.toString(paramInt));
  }
  
  static {
    try {
      SAXParser sAXParser = SAXParserFactory.newInstance().newSAXParser();
      sAXParser.parse(Security.class.getResource("/config.xml").openStream(), new DefaultHandler() {
            private boolean parsing = false;
            
            public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) {
              if (param1String3.compareTo("security") == 0)
                this.parsing = true; 
              if (this.parsing && param1String3.compareTo("entity") == 0)
                try {
                  String str1 = param1Attributes.getValue("class").trim();
                  String str2 = param1Attributes.getValue("name").trim().toUpperCase();
                  String str3 = param1Attributes.getValue("type").trim();
                  Security security = (Security)Class.forName(str1).newInstance();
                  security.type = Integer.parseInt(str3);
                  Security.map.put(str2, security);
                  Security.map.put(str3, security);
                } catch (Exception exception) {
                  exception.printStackTrace();
                }  
            }
            
            public void endElement(String param1String1, String param1String2, String param1String3) {
              if (param1String3.compareTo("security") == 0)
                this.parsing = false; 
            }
          });
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Security\Security.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */