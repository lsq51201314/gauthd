package com.goldhuman.xml;

import org.xml.sax.Attributes;

public abstract class xmlobject {
  public String name;
  
  public String content = "";
  
  public xmlobject parent;
  
  public xmlobject[] children = new xmlobject[0];
  
  protected void setchild(xmlobject paramxmlobject) {
    xmlobject[] arrayOfXmlobject = new xmlobject[this.children.length + 1];
    System.arraycopy(this.children, 0, arrayOfXmlobject, 0, this.children.length);
    arrayOfXmlobject[this.children.length] = paramxmlobject;
    this.children = arrayOfXmlobject;
  }
  
  protected void setparent(xmlobject paramxmlobject) {
    this.parent = paramxmlobject;
  }
  
  protected void setattr(Attributes paramAttributes) {
    this.name = paramAttributes.getValue("name");
  }
  
  public abstract void action();
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\com\goldhuman\xml\xmlobject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */