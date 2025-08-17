/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Buffer
/*     */ {
/*     */   static final int MAX_BYTES_TO_DUMP = 512;
/*     */   static final int NO_LENGTH_LIMIT = -1;
/*     */   static final long NULL_LENGTH = -1L;
/*  44 */   private int bufLength = 0;
/*     */   
/*     */   private byte[] byteBuffer;
/*     */   
/*  48 */   private int position = 0;
/*     */   
/*     */   protected boolean wasMultiPacket = false;
/*     */   
/*     */   Buffer(byte[] buf) {
/*  53 */     this.byteBuffer = buf;
/*  54 */     setBufLength(buf.length);
/*     */   }
/*     */   
/*     */   Buffer(int size) {
/*  58 */     this.byteBuffer = new byte[size];
/*  59 */     setBufLength(this.byteBuffer.length);
/*  60 */     this.position = 4;
/*     */   }
/*     */   
/*     */   final void clear() {
/*  64 */     this.position = 4;
/*     */   }
/*     */   
/*     */   final void dump() {
/*  68 */     dump(getBufLength());
/*     */   }
/*     */   
/*     */   final String dump(int numBytes) {
/*  72 */     return StringUtils.dumpAsHex(getBytes(0, (numBytes > getBufLength()) ? getBufLength() : numBytes), (numBytes > getBufLength()) ? getBufLength() : numBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final String dumpClampedBytes(int numBytes) {
/*  78 */     int numBytesToDump = (numBytes < 512) ? numBytes : 512;
/*     */ 
/*     */     
/*  81 */     String dumped = StringUtils.dumpAsHex(getBytes(0, (numBytesToDump > getBufLength()) ? getBufLength() : numBytesToDump), (numBytesToDump > getBufLength()) ? getBufLength() : numBytesToDump);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     if (numBytesToDump < numBytes) {
/*  88 */       return dumped + " ....(packet exceeds max. dump length)";
/*     */     }
/*     */     
/*  91 */     return dumped;
/*     */   }
/*     */   
/*     */   final void dumpHeader() {
/*  95 */     for (int i = 0; i < 4; i++) {
/*  96 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */       
/*  98 */       if (hexVal.length() == 1) {
/*  99 */         hexVal = "0" + hexVal;
/*     */       }
/*     */       
/* 102 */       System.out.print(hexVal + " ");
/*     */     } 
/*     */   }
/*     */   
/*     */   final void dumpNBytes(int start, int nBytes) {
/* 107 */     StringBuffer asciiBuf = new StringBuffer();
/*     */     
/* 109 */     for (int i = start; i < start + nBytes && i < getBufLength(); i++) {
/* 110 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */       
/* 112 */       if (hexVal.length() == 1) {
/* 113 */         hexVal = "0" + hexVal;
/*     */       }
/*     */       
/* 116 */       System.out.print(hexVal + " ");
/*     */       
/* 118 */       if (readByte(i) > 32 && readByte(i) < Byte.MAX_VALUE) {
/* 119 */         asciiBuf.append((char)readByte(i));
/*     */       } else {
/* 121 */         asciiBuf.append(".");
/*     */       } 
/*     */       
/* 124 */       asciiBuf.append(" ");
/*     */     } 
/*     */     
/* 127 */     System.out.println("    " + asciiBuf.toString());
/*     */   }
/*     */   
/*     */   final void ensureCapacity(int additionalData) throws SQLException {
/* 131 */     if (this.position + additionalData > getBufLength()) {
/* 132 */       if (this.position + additionalData < this.byteBuffer.length) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 138 */         setBufLength(this.byteBuffer.length);
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 144 */         int newLength = (int)(this.byteBuffer.length * 1.25D);
/*     */         
/* 146 */         if (newLength < this.byteBuffer.length + additionalData) {
/* 147 */           newLength = this.byteBuffer.length + (int)(additionalData * 1.25D);
/*     */         }
/*     */ 
/*     */         
/* 151 */         if (newLength < this.byteBuffer.length) {
/* 152 */           newLength = this.byteBuffer.length + additionalData;
/*     */         }
/*     */         
/* 155 */         byte[] newBytes = new byte[newLength];
/*     */         
/* 157 */         System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
/*     */         
/* 159 */         this.byteBuffer = newBytes;
/* 160 */         setBufLength(this.byteBuffer.length);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int fastSkipLenString() {
/* 171 */     long len = readFieldLength();
/*     */     
/* 173 */     this.position = (int)(this.position + len);
/*     */     
/* 175 */     return (int)len;
/*     */   }
/*     */   
/*     */   public void fastSkipLenByteArray() {
/* 179 */     long len = readFieldLength();
/*     */     
/* 181 */     if (len == -1L || len == 0L) {
/*     */       return;
/*     */     }
/*     */     
/* 185 */     this.position = (int)(this.position + len);
/*     */   }
/*     */   
/*     */   protected final byte[] getBufferSource() {
/* 189 */     return this.byteBuffer;
/*     */   }
/*     */   
/*     */   int getBufLength() {
/* 193 */     return this.bufLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getByteBuffer() {
/* 202 */     return this.byteBuffer;
/*     */   }
/*     */   
/*     */   final byte[] getBytes(int len) {
/* 206 */     byte[] b = new byte[len];
/* 207 */     System.arraycopy(this.byteBuffer, this.position, b, 0, len);
/* 208 */     this.position += len;
/*     */     
/* 210 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] getBytes(int offset, int len) {
/* 219 */     byte[] dest = new byte[len];
/* 220 */     System.arraycopy(this.byteBuffer, offset, dest, 0, len);
/*     */     
/* 222 */     return dest;
/*     */   }
/*     */   
/*     */   int getCapacity() {
/* 226 */     return this.byteBuffer.length;
/*     */   }
/*     */   
/*     */   public ByteBuffer getNioBuffer() {
/* 230 */     throw new IllegalArgumentException(Messages.getString("ByteArrayBuffer.0"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPosition() {
/* 240 */     return this.position;
/*     */   }
/*     */ 
/*     */   
/*     */   final boolean isLastDataPacket() {
/* 245 */     return (getBufLength() < 9 && (this.byteBuffer[0] & 0xFF) == 254);
/*     */   }
/*     */   
/*     */   final long newReadLength() {
/* 249 */     int sw = this.byteBuffer[this.position++] & 0xFF;
/*     */     
/* 251 */     switch (sw) {
/*     */       case 251:
/* 253 */         return 0L;
/*     */       
/*     */       case 252:
/* 256 */         return readInt();
/*     */       
/*     */       case 253:
/* 259 */         return readLongInt();
/*     */       
/*     */       case 254:
/* 262 */         return readLongLong();
/*     */     } 
/*     */     
/* 265 */     return sw;
/*     */   }
/*     */ 
/*     */   
/*     */   final byte readByte() {
/* 270 */     return this.byteBuffer[this.position++];
/*     */   }
/*     */   
/*     */   final byte readByte(int readAt) {
/* 274 */     return this.byteBuffer[readAt];
/*     */   }
/*     */   
/*     */   final long readFieldLength() {
/* 278 */     int sw = this.byteBuffer[this.position++] & 0xFF;
/*     */     
/* 280 */     switch (sw) {
/*     */       case 251:
/* 282 */         return -1L;
/*     */       
/*     */       case 252:
/* 285 */         return readInt();
/*     */       
/*     */       case 253:
/* 288 */         return readLongInt();
/*     */       
/*     */       case 254:
/* 291 */         return readLongLong();
/*     */     } 
/*     */     
/* 294 */     return sw;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final int readInt() {
/* 300 */     byte[] b = this.byteBuffer;
/*     */     
/* 302 */     return b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8;
/*     */   }
/*     */   
/*     */   final int readIntAsLong() {
/* 306 */     byte[] b = this.byteBuffer;
/*     */     
/* 308 */     return b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8 | (b[this.position++] & 0xFF) << 16 | (b[this.position++] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final byte[] readLenByteArray(int offset) {
/* 314 */     long len = readFieldLength();
/*     */     
/* 316 */     if (len == -1L) {
/* 317 */       return null;
/*     */     }
/*     */     
/* 320 */     if (len == 0L) {
/* 321 */       return Constants.EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 324 */     this.position += offset;
/*     */     
/* 326 */     return getBytes((int)len);
/*     */   }
/*     */   
/*     */   final long readLength() {
/* 330 */     int sw = this.byteBuffer[this.position++] & 0xFF;
/*     */     
/* 332 */     switch (sw) {
/*     */       case 251:
/* 334 */         return 0L;
/*     */       
/*     */       case 252:
/* 337 */         return readInt();
/*     */       
/*     */       case 253:
/* 340 */         return readLongInt();
/*     */       
/*     */       case 254:
/* 343 */         return readLong();
/*     */     } 
/*     */     
/* 346 */     return sw;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final long readLong() {
/* 352 */     byte[] b = this.byteBuffer;
/*     */     
/* 354 */     return b[this.position++] & 0xFFL | (b[this.position++] & 0xFFL) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int readLongInt() {
/* 362 */     byte[] b = this.byteBuffer;
/*     */     
/* 364 */     return b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8 | (b[this.position++] & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final long readLongLong() {
/* 370 */     byte[] b = this.byteBuffer;
/*     */     
/* 372 */     return (b[this.position++] & 0xFF) | (b[this.position++] & 0xFF) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L | (b[this.position++] & 0xFF) << 32L | (b[this.position++] & 0xFF) << 40L | (b[this.position++] & 0xFF) << 48L | (b[this.position++] & 0xFF) << 56L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int readnBytes() {
/* 383 */     int sw = this.byteBuffer[this.position++] & 0xFF;
/*     */     
/* 385 */     switch (sw) {
/*     */       case 1:
/* 387 */         return this.byteBuffer[this.position++] & 0xFF;
/*     */       
/*     */       case 2:
/* 390 */         return readInt();
/*     */       
/*     */       case 3:
/* 393 */         return readLongInt();
/*     */       
/*     */       case 4:
/* 396 */         return (int)readLong();
/*     */     } 
/*     */     
/* 399 */     return 255;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String readString() {
/* 410 */     int i = this.position;
/* 411 */     int len = 0;
/* 412 */     int maxLen = getBufLength();
/*     */     
/* 414 */     while (i < maxLen && this.byteBuffer[i] != 0) {
/* 415 */       len++;
/* 416 */       i++;
/*     */     } 
/*     */     
/* 419 */     String s = new String(this.byteBuffer, this.position, len);
/* 420 */     this.position += len + 1;
/*     */     
/* 422 */     return s;
/*     */   }
/*     */   
/*     */   final String readString(String encoding) throws SQLException {
/* 426 */     int i = this.position;
/* 427 */     int len = 0;
/* 428 */     int maxLen = getBufLength();
/*     */     
/* 430 */     while (i < maxLen && this.byteBuffer[i] != 0) {
/* 431 */       len++;
/* 432 */       i++;
/*     */     } 
/*     */     
/*     */     try {
/* 436 */       return new String(this.byteBuffer, this.position, len, encoding);
/* 437 */     } catch (UnsupportedEncodingException uEE) {
/* 438 */       throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", "S1009");
/*     */     } finally {
/*     */       
/* 441 */       this.position += len + 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   void setBufLength(int bufLengthToSet) {
/* 446 */     this.bufLength = bufLengthToSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setByteBuffer(byte[] byteBufferToSet) {
/* 456 */     this.byteBuffer = byteBufferToSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPosition(int positionToSet) {
/* 466 */     this.position = positionToSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWasMultiPacket(boolean flag) {
/* 476 */     this.wasMultiPacket = flag;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 480 */     return dumpClampedBytes(getPosition());
/*     */   }
/*     */   
/*     */   public String toSuperString() {
/* 484 */     return super.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean wasMultiPacket() {
/* 493 */     return this.wasMultiPacket;
/*     */   }
/*     */   
/*     */   final void writeByte(byte b) throws SQLException {
/* 497 */     ensureCapacity(1);
/*     */     
/* 499 */     this.byteBuffer[this.position++] = b;
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeBytesNoNull(byte[] bytes) throws SQLException {
/* 504 */     int len = bytes.length;
/* 505 */     ensureCapacity(len);
/* 506 */     System.arraycopy(bytes, 0, this.byteBuffer, this.position, len);
/* 507 */     this.position += len;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final void writeBytesNoNull(byte[] bytes, int offset, int length) throws SQLException {
/* 513 */     ensureCapacity(length);
/* 514 */     System.arraycopy(bytes, offset, this.byteBuffer, this.position, length);
/* 515 */     this.position += length;
/*     */   }
/*     */   
/*     */   final void writeDouble(double d) throws SQLException {
/* 519 */     long l = Double.doubleToLongBits(d);
/* 520 */     writeLongLong(l);
/*     */   }
/*     */   
/*     */   final void writeFieldLength(long length) throws SQLException {
/* 524 */     if (length < 251L) {
/* 525 */       writeByte((byte)(int)length);
/* 526 */     } else if (length < 65536L) {
/* 527 */       ensureCapacity(3);
/* 528 */       writeByte((byte)-4);
/* 529 */       writeInt((int)length);
/* 530 */     } else if (length < 16777216L) {
/* 531 */       ensureCapacity(4);
/* 532 */       writeByte((byte)-3);
/* 533 */       writeLongInt((int)length);
/*     */     } else {
/* 535 */       ensureCapacity(9);
/* 536 */       writeByte((byte)-2);
/* 537 */       writeLongLong(length);
/*     */     } 
/*     */   }
/*     */   
/*     */   final void writeFloat(float f) throws SQLException {
/* 542 */     ensureCapacity(4);
/*     */     
/* 544 */     int i = Float.floatToIntBits(f);
/* 545 */     byte[] b = this.byteBuffer;
/* 546 */     b[this.position++] = (byte)(i & 0xFF);
/* 547 */     b[this.position++] = (byte)(i >>> 8);
/* 548 */     b[this.position++] = (byte)(i >>> 16);
/* 549 */     b[this.position++] = (byte)(i >>> 24);
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeInt(int i) throws SQLException {
/* 554 */     ensureCapacity(2);
/*     */     
/* 556 */     byte[] b = this.byteBuffer;
/* 557 */     b[this.position++] = (byte)(i & 0xFF);
/* 558 */     b[this.position++] = (byte)(i >>> 8);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final void writeLenBytes(byte[] b) throws SQLException {
/* 564 */     int len = b.length;
/* 565 */     ensureCapacity(len + 9);
/* 566 */     writeFieldLength(len);
/* 567 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 568 */     this.position += len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode, ConnectionImpl conn) throws UnsupportedEncodingException, SQLException {
/* 577 */     byte[] b = null;
/*     */     
/* 579 */     if (converter != null) {
/* 580 */       b = converter.toBytes(s);
/*     */     } else {
/* 582 */       b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn);
/*     */     } 
/*     */ 
/*     */     
/* 586 */     int len = b.length;
/* 587 */     ensureCapacity(len + 9);
/* 588 */     writeFieldLength(len);
/* 589 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 590 */     this.position += len;
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeLong(long i) throws SQLException {
/* 595 */     ensureCapacity(4);
/*     */     
/* 597 */     byte[] b = this.byteBuffer;
/* 598 */     b[this.position++] = (byte)(int)(i & 0xFFL);
/* 599 */     b[this.position++] = (byte)(int)(i >>> 8L);
/* 600 */     b[this.position++] = (byte)(int)(i >>> 16L);
/* 601 */     b[this.position++] = (byte)(int)(i >>> 24L);
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeLongInt(int i) throws SQLException {
/* 606 */     ensureCapacity(3);
/* 607 */     byte[] b = this.byteBuffer;
/* 608 */     b[this.position++] = (byte)(i & 0xFF);
/* 609 */     b[this.position++] = (byte)(i >>> 8);
/* 610 */     b[this.position++] = (byte)(i >>> 16);
/*     */   }
/*     */   
/*     */   final void writeLongLong(long i) throws SQLException {
/* 614 */     ensureCapacity(8);
/* 615 */     byte[] b = this.byteBuffer;
/* 616 */     b[this.position++] = (byte)(int)(i & 0xFFL);
/* 617 */     b[this.position++] = (byte)(int)(i >>> 8L);
/* 618 */     b[this.position++] = (byte)(int)(i >>> 16L);
/* 619 */     b[this.position++] = (byte)(int)(i >>> 24L);
/* 620 */     b[this.position++] = (byte)(int)(i >>> 32L);
/* 621 */     b[this.position++] = (byte)(int)(i >>> 40L);
/* 622 */     b[this.position++] = (byte)(int)(i >>> 48L);
/* 623 */     b[this.position++] = (byte)(int)(i >>> 56L);
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeString(String s) throws SQLException {
/* 628 */     ensureCapacity(s.length() * 2 + 1);
/* 629 */     writeStringNoNull(s);
/* 630 */     this.byteBuffer[this.position++] = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeString(String s, String encoding, ConnectionImpl conn) throws SQLException {
/* 635 */     ensureCapacity(s.length() * 2 + 1);
/*     */     try {
/* 637 */       writeStringNoNull(s, encoding, encoding, false, conn);
/* 638 */     } catch (UnsupportedEncodingException ue) {
/* 639 */       throw new SQLException(ue.toString(), "S1000");
/*     */     } 
/*     */     
/* 642 */     this.byteBuffer[this.position++] = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   final void writeStringNoNull(String s) throws SQLException {
/* 647 */     int len = s.length();
/* 648 */     ensureCapacity(len * 2);
/* 649 */     System.arraycopy(s.getBytes(), 0, this.byteBuffer, this.position, len);
/* 650 */     this.position += len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, ConnectionImpl conn) throws UnsupportedEncodingException, SQLException {
/* 663 */     byte[] b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn);
/*     */ 
/*     */     
/* 666 */     int len = b.length;
/* 667 */     ensureCapacity(len);
/* 668 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 669 */     this.position += len;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Buffer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */