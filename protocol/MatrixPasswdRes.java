package protocol;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Rpc;

public final class MatrixPasswdRes extends Rpc.Data {
  public int retcode;
  
  public int userid;
  
  public int algorithm;
  
  public Octets response = new Octets();
  
  public Octets matrix = new Octets();
  
  public OctetsStream marshal(OctetsStream paramOctetsStream) {
    paramOctetsStream.marshal(this.retcode);
    paramOctetsStream.marshal(this.userid);
    paramOctetsStream.marshal(this.algorithm);
    paramOctetsStream.marshal(this.response);
    paramOctetsStream.marshal(this.matrix);
    return paramOctetsStream;
  }
  
  public OctetsStream unmarshal(OctetsStream paramOctetsStream) throws MarshalException {
    this.retcode = paramOctetsStream.unmarshal_int();
    this.userid = paramOctetsStream.unmarshal_int();
    this.algorithm = paramOctetsStream.unmarshal_int();
    paramOctetsStream.unmarshal(this.response);
    paramOctetsStream.unmarshal(this.matrix);
    return paramOctetsStream;
  }
  
  public Object clone() {
    try {
      MatrixPasswdRes matrixPasswdRes = (MatrixPasswdRes)super.clone();
      matrixPasswdRes.response = (Octets)this.response.clone();
      matrixPasswdRes.matrix = (Octets)this.matrix.clone();
      return matrixPasswdRes;
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\MatrixPasswdRes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */