package com.goldhuman.Common.Marshal;

import com.goldhuman.Common.Octets;

public class OctetsStream extends Octets {
  private static final int MAXSPARE = 16384;
  
  private int pos = 0;
  
  private int tranpos = 0;
  
  public OctetsStream() {}
  
  public OctetsStream(int paramInt) {
    super(paramInt);
  }
  
  public OctetsStream(Octets paramOctets) {
    super(paramOctets);
  }
  
  public static OctetsStream wrap(Octets paramOctets) {
    OctetsStream octetsStream = new OctetsStream();
    octetsStream.swap(paramOctets);
    return octetsStream;
  }
  
  public Object clone() {
    OctetsStream octetsStream = (OctetsStream)super.clone();
    octetsStream.pos = this.pos;
    octetsStream.tranpos = this.pos;
    return octetsStream;
  }
  
  public boolean eos() {
    return (this.pos == size());
  }
  
  public OctetsStream marshal(byte paramByte) {
    push_back(paramByte);
    return this;
  }
  
  public OctetsStream marshal(short paramShort) {
    return marshal((byte)(paramShort >> 8)).marshal((byte)paramShort);
  }
  
  public OctetsStream marshal(char paramChar) {
    return marshal((byte)(paramChar >> 8)).marshal((byte)paramChar);
  }
  
  public OctetsStream marshal(int paramInt) {
    return marshal((byte)(paramInt >> 24)).marshal((byte)(paramInt >> 16)).marshal((byte)(paramInt >> 8)).marshal((byte)paramInt);
  }
  
  public OctetsStream marshal(long paramLong) {
    return marshal((byte)(int)(paramLong >> 56L)).marshal((byte)(int)(paramLong >> 48L)).marshal((byte)(int)(paramLong >> 40L)).marshal((byte)(int)(paramLong >> 32L)).marshal((byte)(int)(paramLong >> 24L)).marshal((byte)(int)(paramLong >> 16L)).marshal((byte)(int)(paramLong >> 8L)).marshal((byte)(int)paramLong);
  }
  
  public OctetsStream marshal(float paramFloat) {
    return marshal(Float.floatToRawIntBits(paramFloat));
  }
  
  public OctetsStream marshal(double paramDouble) {
    return marshal(Double.doubleToRawLongBits(paramDouble));
  }
  
  public OctetsStream compact_uint32(int paramInt) {
    if (paramInt < 64)
      return marshal((byte)paramInt); 
    if (paramInt < 16384)
      return marshal((short)(paramInt | 0x8000)); 
    if (paramInt < 536870912)
      return marshal(paramInt | 0xC0000000); 
    marshal((byte)-32);
    return marshal(paramInt);
  }
  
  public OctetsStream compact_sint32(int paramInt) {
    if (paramInt >= 0) {
      if (paramInt < 64)
        return marshal((byte)paramInt); 
      if (paramInt < 8192)
        return marshal((short)(paramInt | 0x8000)); 
      if (paramInt < 268435456)
        return marshal(paramInt | 0xC0000000); 
      marshal((byte)-32);
      return marshal(paramInt);
    } 
    if (-paramInt > 0) {
      paramInt = -paramInt;
      if (paramInt < 64)
        return marshal((byte)(paramInt | 0x40)); 
      if (paramInt < 8192)
        return marshal((short)(paramInt | 0xA000)); 
      if (paramInt < 268435456)
        return marshal(paramInt | 0xD0000000); 
      marshal((byte)-16);
      return marshal(paramInt);
    } 
    marshal((byte)-16);
    return marshal(paramInt);
  }
  
  public OctetsStream marshal(Marshal paramMarshal) {
    return paramMarshal.marshal(this);
  }
  
  public OctetsStream marshal(Octets paramOctets) {
    compact_uint32(paramOctets.size());
    insert(size(), paramOctets);
    return this;
  }
  
  public OctetsStream Begin() {
    this.tranpos = this.pos;
    return this;
  }
  
  public OctetsStream Rollback() {
    this.pos = this.tranpos;
    return this;
  }
  
  public OctetsStream Commit() {
    if (this.pos >= 16384) {
      erase(0, this.pos);
      this.pos = 0;
    } 
    return this;
  }
  
  public byte unmarshal_byte() throws MarshalException {
    if (this.pos + 1 > size())
      throw new MarshalException(); 
    return getByte(this.pos++);
  }
  
  public short unmarshal_short() throws MarshalException {
    if (this.pos + 2 > size())
      throw new MarshalException(); 
    byte b1 = getByte(this.pos++);
    byte b2 = getByte(this.pos++);
    return (short)(b1 << 8 | b2 & 0xFF);
  }
  
  public char unmarshal_char() throws MarshalException {
    if (this.pos + 2 > size())
      throw new MarshalException(); 
    byte b1 = getByte(this.pos++);
    byte b2 = getByte(this.pos++);
    return (char)(b1 << 8 | b2 & 0xFF);
  }
  
  public int unmarshal_int() throws MarshalException {
    if (this.pos + 4 > size())
      throw new MarshalException(); 
    byte b1 = getByte(this.pos++);
    byte b2 = getByte(this.pos++);
    byte b3 = getByte(this.pos++);
    byte b4 = getByte(this.pos++);
    return (b1 & 0xFF) << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF) << 0;
  }
  
  public long unmarshal_long() throws MarshalException {
    if (this.pos + 8 > size())
      throw new MarshalException(); 
    byte b1 = getByte(this.pos++);
    byte b2 = getByte(this.pos++);
    byte b3 = getByte(this.pos++);
    byte b4 = getByte(this.pos++);
    byte b5 = getByte(this.pos++);
    byte b6 = getByte(this.pos++);
    byte b7 = getByte(this.pos++);
    byte b8 = getByte(this.pos++);
    return (b1 & 0xFFL) << 56L | (b2 & 0xFFL) << 48L | (b3 & 0xFFL) << 40L | (b4 & 0xFFL) << 32L | (b5 & 0xFFL) << 24L | (b6 & 0xFFL) << 16L | (b7 & 0xFFL) << 8L | (b8 & 0xFFL) << 0L;
  }
  
  public float unmarshal_float() throws MarshalException {
    return Float.intBitsToFloat(unmarshal_int());
  }
  
  public double unmarshal_double() throws MarshalException {
    return Double.longBitsToDouble(unmarshal_long());
  }
  
  public int uncompact_uint32() throws MarshalException {
    if (this.pos == size())
      throw new MarshalException(); 
    switch (getByte(this.pos) & 0xE0) {
      case 224:
        unmarshal_byte();
        return unmarshal_int();
      case 192:
        return unmarshal_int() & 0x3FFFFFFF;
      case 128:
      case 160:
        return unmarshal_short() & Short.MAX_VALUE;
    } 
    return unmarshal_byte();
  }
  
  public int uncompact_sint32() throws MarshalException {
    if (this.pos == size())
      throw new MarshalException(); 
    switch (getByte(this.pos) & 0xF0) {
      case 240:
        unmarshal_byte();
        return -unmarshal_int();
      case 224:
        unmarshal_byte();
        return unmarshal_int();
      case 208:
        return -(unmarshal_int() & 0x2FFFFFFF);
      case 192:
        return unmarshal_int() & 0x3FFFFFFF;
      case 160:
      case 176:
        return -(unmarshal_short() & 0x5FFF);
      case 128:
      case 144:
        return unmarshal_short() & Short.MAX_VALUE;
      case 64:
      case 80:
      case 96:
      case 112:
        return -(unmarshal_byte() & 0xFFFFFFBF);
    } 
    return unmarshal_byte();
  }
  
  public Octets unmarshal_Octets() throws MarshalException {
    int i = uncompact_uint32();
    if (this.pos + i > size())
      throw new MarshalException(); 
    Octets octets = new Octets(this, this.pos, i);
    this.pos += i;
    return octets;
  }
  
  public OctetsStream unmarshal(Octets paramOctets) throws MarshalException {
    int i = uncompact_uint32();
    if (this.pos + i > size())
      throw new MarshalException(); 
    paramOctets.replace(this, this.pos, i);
    this.pos += i;
    return this;
  }
  
  public OctetsStream unmarshal(Marshal paramMarshal) throws MarshalException {
    return paramMarshal.unmarshal(this);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Marshal\OctetsStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */