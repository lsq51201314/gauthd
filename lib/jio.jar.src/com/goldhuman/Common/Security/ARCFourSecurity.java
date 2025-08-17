package com.goldhuman.Common.Security;

import com.goldhuman.Common.Octets;

public final class ARCFourSecurity extends Security {
  private byte[] perm = new byte[256];
  
  private byte index1;
  
  private byte index2;
  
  public Object clone() {
    try {
      ARCFourSecurity aRCFourSecurity = (ARCFourSecurity)super.clone();
      aRCFourSecurity.perm = new byte[256];
      System.arraycopy(this.perm, 0, aRCFourSecurity.perm, 0, 256);
      return aRCFourSecurity;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void SetParameter(Octets paramOctets) {
    int i = paramOctets.size();
    byte b = 0;
    byte b1;
    for (b1 = 0; b1 < 'Ā'; b1++)
      this.perm[b1] = (byte)b1; 
    for (b1 = 0; b1 < 'Ā'; b1++) {
      b = (byte)(b + this.perm[b1] + paramOctets.getByte(b1 % i));
      byte b2 = this.perm[b1];
      this.perm[b1] = this.perm[b & 0xFF];
      this.perm[b & 0xFF] = b2;
    } 
    this.index1 = this.index2 = 0;
  }
  
  public Octets Update(Octets paramOctets) {
    int i = paramOctets.size();
    for (byte b = 0; b < i; b++) {
      this.index2 = (byte)(this.index2 + this.perm[(this.index1 = (byte)(this.index1 + 1)) & 0xFF]);
      byte b1 = this.perm[this.index1 & 0xFF];
      this.perm[this.index1 & 0xFF] = this.perm[this.index2 & 0xFF];
      this.perm[this.index2 & 0xFF] = b1;
      byte b2 = (byte)(this.perm[this.index1 & 0xFF] + this.perm[this.index2 & 0xFF]);
      paramOctets.setByte(b, (byte)(paramOctets.getByte(b) ^ this.perm[b2 & 0xFF]));
    } 
    return paramOctets;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Security\ARCFourSecurity.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */