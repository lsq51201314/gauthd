package protocol;

import java.io.Serializable;

class UseridZoneid implements Serializable {
  int userid;
  
  int zoneid;
  
  UseridZoneid(int paramInt1, int paramInt2) {
    this.userid = paramInt1;
    this.zoneid = paramInt2;
  }
  
  public String toString() {
    return "userid=" + this.userid + " zoneid=" + this.zoneid;
  }
  
  public int hashCode() {
    return this.userid ^ this.zoneid;
  }
  
  public boolean equals(Object paramObject) {
    UseridZoneid useridZoneid = (UseridZoneid)paramObject;
    return (this.userid == useridZoneid.userid && this.zoneid == useridZoneid.zoneid);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$UseridZoneid.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */