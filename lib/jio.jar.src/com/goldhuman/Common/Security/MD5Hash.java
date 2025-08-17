package com.goldhuman.Common.Security;

import com.goldhuman.Common.Octets;
import java.security.MessageDigest;

public final class MD5Hash extends Security {
  private MessageDigest md5 = null;
  
  protected MD5Hash() {
    try {
      this.md5 = MessageDigest.getInstance("MD5");
    } catch (Exception exception) {}
  }
  
  public Object clone() {
    try {
      MD5Hash mD5Hash = (MD5Hash)super.clone();
      mD5Hash.md5 = (MessageDigest)this.md5.clone();
      return mD5Hash;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Octets Update(Octets paramOctets) {
    if (this.md5 != null)
      this.md5.update(paramOctets.array(), 0, paramOctets.size()); 
    return paramOctets;
  }
  
  public Octets Final(Octets paramOctets) {
    if (this.md5 != null)
      paramOctets.replace(this.md5.digest()); 
    return paramOctets;
  }
  
  public static Octets Digest(Octets paramOctets) {
    try {
      return new Octets(MessageDigest.getInstance("MD5").digest(paramOctets.getBytes()));
    } catch (Exception exception) {
      return new Octets();
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Security\MD5Hash.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */