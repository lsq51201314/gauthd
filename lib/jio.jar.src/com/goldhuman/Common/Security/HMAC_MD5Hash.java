package com.goldhuman.Common.Security;

import com.goldhuman.Common.Octets;

public final class HMAC_MD5Hash extends Security {
  private Octets k_opad = new Octets(64);
  
  private MD5Hash md5hash = new MD5Hash();
  
  public Object clone() {
    try {
      HMAC_MD5Hash hMAC_MD5Hash = (HMAC_MD5Hash)super.clone();
      (hMAC_MD5Hash.k_opad = (Octets)this.k_opad.clone()).reserve(64);
      hMAC_MD5Hash.md5hash = (MD5Hash)this.md5hash.clone();
      return hMAC_MD5Hash;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void SetParameter(Octets paramOctets) {
    Octets octets = new Octets(64);
    int i = paramOctets.size();
    if (i > 64) {
      Octets octets1 = MD5Hash.Digest(paramOctets);
      octets.replace(octets1);
      this.k_opad.replace(octets1);
      i = octets1.size();
    } else {
      octets.replace(paramOctets);
      this.k_opad.replace(paramOctets);
    } 
    byte b;
    for (b = 0; b < i; b++) {
      octets.setByte(b, (byte)(octets.getByte(b) ^ 0x36));
      this.k_opad.setByte(b, (byte)(this.k_opad.getByte(b) ^ 0x5C));
    } 
    while (b < 64) {
      octets.setByte(b, (byte)54);
      this.k_opad.setByte(b, (byte)92);
      b++;
    } 
    octets.resize(64);
    this.k_opad.resize(64);
    this.md5hash.Update(octets);
  }
  
  public Octets Update(Octets paramOctets) {
    this.md5hash.Update(paramOctets);
    return paramOctets;
  }
  
  public Octets Final(Octets paramOctets) {
    this.md5hash.Final(paramOctets);
    MD5Hash mD5Hash = new MD5Hash();
    mD5Hash.Update(this.k_opad);
    mD5Hash.Update(paramOctets);
    return mD5Hash.Final(paramOctets);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Security\HMAC_MD5Hash.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */