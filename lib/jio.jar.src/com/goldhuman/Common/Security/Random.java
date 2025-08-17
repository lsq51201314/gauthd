package com.goldhuman.Common.Security;

import com.goldhuman.Common.Octets;

public final class Random extends Security {
  private static java.util.Random r = new java.util.Random();
  
  public Octets Update(Octets paramOctets) {
    r.nextBytes(paramOctets.array());
    return paramOctets;
  }
  
  static {
    r.setSeed(System.currentTimeMillis());
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Security\Random.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */