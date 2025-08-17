package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.IO.Protocol.Rpc;

public final class UserLoginRes extends Rpc.Data {
  public byte retcode;
  
  public int remain_playtime;
  
  public int func;
  
  public int funcparm;
  
  public byte blIsGM;
  
  public int free_time_left;
  
  public int free_time_end;
  
  public int creatime;
  
  public int adduppoint;
  
  public int soldpoint;
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.remain_playtime);
    paramOctetsStream.marshal(this.func);
    paramOctetsStream.marshal(this.funcparm);
    paramOctetsStream.marshal(this.blIsGM);
    paramOctetsStream.marshal(this.free_time_left);
    paramOctetsStream.marshal(this.free_time_end);
    paramOctetsStream.marshal(this.creatime);
    paramOctetsStream.marshal(this.adduppoint);
    paramOctetsStream.marshal(this.soldpoint);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_byte();
    this.remain_playtime = paramOctetsStream.unmarshal_int();
    this.func = paramOctetsStream.unmarshal_int();
    this.funcparm = paramOctetsStream.unmarshal_int();
    this.blIsGM = paramOctetsStream.unmarshal_byte();
    this.free_time_left = paramOctetsStream.unmarshal_int();
    this.free_time_end = paramOctetsStream.unmarshal_int();
    this.creatime = paramOctetsStream.unmarshal_int();
    this.adduppoint = paramOctetsStream.unmarshal_int();
    this.soldpoint = paramOctetsStream.unmarshal_int();
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\UserLoginRes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */