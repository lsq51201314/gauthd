package com.goldhuman.Common.Marshal;

public interface Marshal {
  OctetsStream marshal(OctetsStream paramOctetsStream);
  
  OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException;
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Marshal\Marshal.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */