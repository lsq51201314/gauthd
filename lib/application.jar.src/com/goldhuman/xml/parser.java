package com.goldhuman.xml;

import java.io.InputStream;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class parser extends DefaultHandler {
  private Stack stack = new Stack();
  
  private StringBuffer sbcp;
  
  public parser(String paramString) {
    this.sbcp = new StringBuffer(paramString);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
    try {
      if (this.sbcp.length() > 0)
        this.sbcp.append("."); 
      this.sbcp.append(paramString3);
      xmlobject xmlobject = (xmlobject)Class.forName(this.sbcp + ".handler").newInstance();
      if (this.stack.empty()) {
        xmlobject.setparent(null);
      } else {
        xmlobject xmlobject1 = this.stack.peek();
        xmlobject.setparent(xmlobject1);
        xmlobject1.setchild(xmlobject);
      } 
      this.stack.push(xmlobject);
      xmlobject.setattr(paramAttributes);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) {
    xmlobject xmlobject = this.stack.pop();
    if (this.stack.empty()) {
      xmlobject.action();
    } else {
      this.sbcp.delete(this.sbcp.lastIndexOf("."), this.sbcp.length());
    } 
  }
  
  public void characters(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    xmlobject xmlobject = this.stack.peek();
    xmlobject.content += new String(paramArrayOfchar, paramInt1, paramInt2);
  }
  
  public static void parse(InputStream paramInputStream, String paramString) {
    try {
      SAXParser sAXParser = SAXParserFactory.newInstance().newSAXParser();
      sAXParser.parse(paramInputStream, new parser(paramString));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static void parse(InputStream paramInputStream) {
    parse(paramInputStream, "");
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\com\goldhuman\xml\parser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */