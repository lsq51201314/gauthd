package com.goldhuman.Common;

import java.nio.ByteBuffer;

public class Octets implements Cloneable, Comparable {
  private static final int DEFAULT_SIZE = 128;
  
  private static String DEFAULT_CHARSET = "ISO-8859-1";
  
  private byte[] buffer = null;
  
  private int count = 0;
  
  private byte[] roundup(int paramInt) {
    int i;
    for (i = 16; paramInt > i; i <<= 1);
    return new byte[i];
  }
  
  public void reserve(int paramInt) {
    if (this.buffer == null) {
      this.buffer = roundup(paramInt);
    } else if (paramInt > this.buffer.length) {
      byte[] arrayOfByte = roundup(paramInt);
      System.arraycopy(this.buffer, 0, arrayOfByte, 0, this.count);
      this.buffer = arrayOfByte;
    } 
  }
  
  public Octets replace(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    reserve(paramInt2);
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buffer, 0, paramInt2);
    this.count = paramInt2;
    return this;
  }
  
  public Octets replace(Octets paramOctets, int paramInt1, int paramInt2) {
    return replace(paramOctets.buffer, paramInt1, paramInt2);
  }
  
  public Octets replace(byte[] paramArrayOfbyte) {
    return replace(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public Octets replace(Octets paramOctets) {
    return replace(paramOctets.buffer, 0, paramOctets.count);
  }
  
  public Octets() {
    reserve(128);
  }
  
  public Octets(int paramInt) {
    reserve(paramInt);
  }
  
  public Octets(Octets paramOctets) {
    replace(paramOctets);
  }
  
  public Octets(byte[] paramArrayOfbyte) {
    replace(paramArrayOfbyte);
  }
  
  public Octets(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    replace(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public Octets(Octets paramOctets, int paramInt1, int paramInt2) {
    replace(paramOctets, paramInt1, paramInt2);
  }
  
  public Octets resize(int paramInt) {
    reserve(paramInt);
    this.count = paramInt;
    return this;
  }
  
  public int size() {
    return this.count;
  }
  
  public int capacity() {
    return this.buffer.length;
  }
  
  public Octets clear() {
    this.count = 0;
    return this;
  }
  
  public Octets swap(Octets paramOctets) {
    int i = this.count;
    this.count = paramOctets.count;
    paramOctets.count = i;
    byte[] arrayOfByte = paramOctets.buffer;
    paramOctets.buffer = this.buffer;
    this.buffer = arrayOfByte;
    return this;
  }
  
  public Octets push_back(byte paramByte) {
    reserve(this.count + 1);
    this.buffer[this.count++] = paramByte;
    return this;
  }
  
  public Octets erase(int paramInt1, int paramInt2) {
    System.arraycopy(this.buffer, paramInt2, this.buffer, paramInt1, this.count -= paramInt2 - paramInt1);
    return this;
  }
  
  public Octets insert(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    reserve(this.count + paramInt3);
    System.arraycopy(this.buffer, paramInt1, this.buffer, paramInt1 + paramInt3, this.count - paramInt1);
    System.arraycopy(paramArrayOfbyte, paramInt2, this.buffer, paramInt1, paramInt3);
    this.count += paramInt3;
    return this;
  }
  
  public Octets insert(int paramInt1, Octets paramOctets, int paramInt2, int paramInt3) {
    return insert(paramInt1, paramOctets.buffer, paramInt2, paramInt3);
  }
  
  public Octets insert(int paramInt, byte[] paramArrayOfbyte) {
    return insert(paramInt, paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public Octets insert(int paramInt, Octets paramOctets) {
    return insert(paramInt, paramOctets.buffer, 0, paramOctets.size());
  }
  
  public Object clone() {
    return new Octets(this);
  }
  
  public int compareTo(Octets paramOctets) {
    int i = Math.min(this.count, paramOctets.count);
    byte[] arrayOfByte1 = this.buffer;
    byte[] arrayOfByte2 = paramOctets.buffer;
    for (byte b = 0; b < i; b++) {
      int j = arrayOfByte1[b] - arrayOfByte2[b];
      if (j != 0)
        return j; 
    } 
    return this.count - paramOctets.count;
  }
  
  public int compareTo(Object paramObject) {
    return compareTo((Octets)paramObject);
  }
  
  public byte[] getBytes() {
    byte[] arrayOfByte = new byte[this.count];
    System.arraycopy(this.buffer, 0, arrayOfByte, 0, this.count);
    return arrayOfByte;
  }
  
  public byte[] array() {
    return this.buffer;
  }
  
  public byte getByte(int paramInt) {
    return this.buffer[paramInt];
  }
  
  public void setByte(int paramInt, byte paramByte) {
    this.buffer[paramInt] = paramByte;
  }
  
  public ByteBuffer getByteBuffer(int paramInt1, int paramInt2) {
    return ByteBuffer.wrap(this.buffer, paramInt1, paramInt2);
  }
  
  public ByteBuffer getByteBuffer(int paramInt) {
    return ByteBuffer.wrap(this.buffer, paramInt, this.count - paramInt);
  }
  
  public ByteBuffer getByteBuffer() {
    return ByteBuffer.wrap(this.buffer, 0, this.count);
  }
  
  public String getString() throws Exception {
    return new String(this.buffer, 0, this.count, DEFAULT_CHARSET);
  }
  
  public void setString(String paramString) throws Exception {
    this.buffer = paramString.getBytes(DEFAULT_CHARSET);
    this.count = this.buffer.length;
  }
  
  public static void setDefaultCharset(String paramString) {
    DEFAULT_CHARSET = paramString;
  }
  
  public static void main(String[] paramArrayOfString) {
    Octets octets1 = new Octets("ddd".getBytes());
    octets1.replace("abc".getBytes());
    octets1.replace("defghijklmn".getBytes());
    try {
      octets1.replace("0123456789".getBytes("UTF-8"));
    } catch (Exception exception) {}
    octets1.insert(octets1.size(), "abc".getBytes());
    octets1.insert(octets1.size(), "def".getBytes());
    System.out.println(new String(octets1.getBytes()));
    System.out.println("size = " + octets1.size());
    Octets octets2 = new Octets("ABC".getBytes());
    octets1.insert(octets1.size(), octets2);
    System.out.println(new String(octets1.getBytes()));
    Octets octets3 = (Octets)octets1.clone();
    System.out.println(new String(octets3.getBytes()));
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Octets.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */