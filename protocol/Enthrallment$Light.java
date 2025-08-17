package protocol;

import java.io.Serializable;

final class Light implements Serializable {
  int[] sids;
  
  Enthrallment.Player[] data;
  
  int size;
  
  Light() {
    this(9);
  }
  
  Light(int paramInt) {
    this.data = new Enthrallment.Player[paramInt];
    this.sids = new int[paramInt];
  }
  
  void trigger(Enthrallment paramEnthrallment, boolean paramBoolean) {
    for (byte b = 0; b < this.size; b++)
      this.data[b].onTimeout(this.sids[b], paramBoolean); 
    if (null != paramEnthrallment) {
      if (this.size > Enthrallment.access$400(paramEnthrallment))
        Enthrallment.access$402(paramEnthrallment, this.size); 
      if (this.size == 0) {
        Enthrallment.access$504(paramEnthrallment);
      } else {
        Enthrallment.access$614(paramEnthrallment, this.data.length);
        Enthrallment.access$714(paramEnthrallment, this.size);
        Enthrallment.access$804(paramEnthrallment);
      } 
    } 
  }
  
  boolean isEmpty() {
    return (0 == this.size);
  }
  
  void add(int paramInt, Enthrallment.Player paramPlayer) {
    if (this.size == this.data.length)
      ensureCapacity(this.size + 1); 
    this.data[this.size] = paramPlayer;
    this.sids[this.size] = paramInt;
    this.size++;
  }
  
  void ensureCapacity(int paramInt) {
    if (paramInt > this.data.length) {
      int i = this.data.length * 2;
      int j = Math.max(this.data.length * 2, paramInt);
      realloc(j);
    } 
  }
  
  void realloc(int paramInt) {
    if (paramInt < this.size)
      return; 
    Enthrallment.Player[] arrayOfPlayer = new Enthrallment.Player[paramInt];
    System.arraycopy(this.data, 0, arrayOfPlayer, 0, this.size);
    this.data = arrayOfPlayer;
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(this.sids, 0, arrayOfInt, 0, this.size);
    this.sids = arrayOfInt;
  }
  
  void trimToSize() {
    realloc(this.size);
  }
  
  void dump() {
    for (byte b = 0; b < this.size; b++)
      System.out.println("timeout: sid=" + this.sids[b] + " -> " + this.data[b]); 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$Light.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */