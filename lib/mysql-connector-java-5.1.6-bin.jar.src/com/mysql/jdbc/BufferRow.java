/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
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
/*     */ 
/*     */ 
/*     */ public class BufferRow
/*     */   extends ResultSetRow
/*     */ {
/*     */   private Buffer rowFromServer;
/*  57 */   private int homePosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   private int preNullBitmaskHomePosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   private int lastRequestedIndex = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int lastRequestedPos;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Field[] metadata;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isBinaryEncoded;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean[] isNull;
/*     */ 
/*     */ 
/*     */   
/*     */   private List openStreams;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferRow(Buffer buf, Field[] fields, boolean isBinaryEncoded) throws SQLException {
/* 101 */     this.rowFromServer = buf;
/* 102 */     this.metadata = fields;
/* 103 */     this.isBinaryEncoded = isBinaryEncoded;
/* 104 */     this.homePosition = this.rowFromServer.getPosition();
/* 105 */     this.preNullBitmaskHomePosition = this.homePosition;
/*     */     
/* 107 */     if (fields != null) {
/* 108 */       setMetadata(fields);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void closeOpenStreams() {
/* 113 */     if (this.openStreams != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 119 */       Iterator iter = this.openStreams.iterator();
/*     */       
/* 121 */       while (iter.hasNext()) {
/*     */         
/*     */         try {
/* 124 */           ((InputStream)iter.next()).close();
/* 125 */         } catch (IOException e) {}
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 130 */       this.openStreams.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private int findAndSeekToOffset(int index) throws SQLException {
/* 135 */     if (!this.isBinaryEncoded) {
/*     */       
/* 137 */       if (index == 0) {
/* 138 */         this.lastRequestedIndex = 0;
/* 139 */         this.lastRequestedPos = this.homePosition;
/* 140 */         this.rowFromServer.setPosition(this.homePosition);
/*     */         
/* 142 */         return 0;
/*     */       } 
/*     */       
/* 145 */       if (index == this.lastRequestedIndex) {
/* 146 */         this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */         
/* 148 */         return this.lastRequestedPos;
/*     */       } 
/*     */       
/* 151 */       int startingIndex = 0;
/*     */       
/* 153 */       if (index > this.lastRequestedIndex) {
/* 154 */         if (this.lastRequestedIndex >= 0) {
/* 155 */           startingIndex = this.lastRequestedIndex;
/*     */         } else {
/* 157 */           startingIndex = 0;
/*     */         } 
/*     */         
/* 160 */         this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */       } else {
/* 162 */         this.rowFromServer.setPosition(this.homePosition);
/*     */       } 
/*     */       
/* 165 */       for (int i = startingIndex; i < index; i++) {
/* 166 */         this.rowFromServer.fastSkipLenByteArray();
/*     */       }
/*     */       
/* 169 */       this.lastRequestedIndex = index;
/* 170 */       this.lastRequestedPos = this.rowFromServer.getPosition();
/*     */       
/* 172 */       return this.lastRequestedPos;
/*     */     } 
/*     */     
/* 175 */     return findAndSeekToOffsetForBinaryEncoding(index);
/*     */   }
/*     */ 
/*     */   
/*     */   private int findAndSeekToOffsetForBinaryEncoding(int index) throws SQLException {
/* 180 */     if (index == 0) {
/* 181 */       this.lastRequestedIndex = 0;
/* 182 */       this.lastRequestedPos = this.homePosition;
/* 183 */       this.rowFromServer.setPosition(this.homePosition);
/*     */       
/* 185 */       return 0;
/*     */     } 
/*     */     
/* 188 */     if (index == this.lastRequestedIndex) {
/* 189 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */       
/* 191 */       return this.lastRequestedPos;
/*     */     } 
/*     */     
/* 194 */     int startingIndex = 0;
/*     */     
/* 196 */     if (index > this.lastRequestedIndex) {
/* 197 */       if (this.lastRequestedIndex >= 0) {
/* 198 */         startingIndex = this.lastRequestedIndex;
/*     */       } else {
/*     */         
/* 201 */         startingIndex = 0;
/* 202 */         this.lastRequestedPos = this.homePosition;
/*     */       } 
/*     */       
/* 205 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */     } else {
/* 207 */       this.rowFromServer.setPosition(this.homePosition);
/*     */     } 
/*     */     
/* 210 */     for (int i = startingIndex; i < index; i++) {
/* 211 */       if (!this.isNull[i]) {
/*     */ 
/*     */ 
/*     */         
/* 215 */         int curPosition = this.rowFromServer.getPosition();
/*     */         
/* 217 */         switch (this.metadata[i].getMysqlType()) {
/*     */           case 6:
/*     */             break;
/*     */ 
/*     */           
/*     */           case 1:
/* 223 */             this.rowFromServer.setPosition(curPosition + 1);
/*     */             break;
/*     */           
/*     */           case 2:
/*     */           case 13:
/* 228 */             this.rowFromServer.setPosition(curPosition + 2);
/*     */             break;
/*     */           
/*     */           case 3:
/*     */           case 9:
/* 233 */             this.rowFromServer.setPosition(curPosition + 4);
/*     */             break;
/*     */           
/*     */           case 8:
/* 237 */             this.rowFromServer.setPosition(curPosition + 8);
/*     */             break;
/*     */           
/*     */           case 4:
/* 241 */             this.rowFromServer.setPosition(curPosition + 4);
/*     */             break;
/*     */           
/*     */           case 5:
/* 245 */             this.rowFromServer.setPosition(curPosition + 8);
/*     */             break;
/*     */           
/*     */           case 11:
/* 249 */             this.rowFromServer.fastSkipLenByteArray();
/*     */             break;
/*     */ 
/*     */           
/*     */           case 10:
/* 254 */             this.rowFromServer.fastSkipLenByteArray();
/*     */             break;
/*     */           
/*     */           case 7:
/*     */           case 12:
/* 259 */             this.rowFromServer.fastSkipLenByteArray();
/*     */             break;
/*     */           
/*     */           case 0:
/*     */           case 15:
/*     */           case 16:
/*     */           case 246:
/*     */           case 249:
/*     */           case 250:
/*     */           case 251:
/*     */           case 252:
/*     */           case 253:
/*     */           case 254:
/*     */           case 255:
/* 273 */             this.rowFromServer.fastSkipLenByteArray();
/*     */             break;
/*     */ 
/*     */           
/*     */           default:
/* 278 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + this.metadata[i].getMysqlType() + Messages.getString("MysqlIO.98") + (i + 1) + Messages.getString("MysqlIO.99") + this.metadata.length + Messages.getString("MysqlIO.100"), "S1000");
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       } 
/*     */     } 
/* 290 */     this.lastRequestedIndex = index;
/* 291 */     this.lastRequestedPos = this.rowFromServer.getPosition();
/*     */     
/* 293 */     return this.lastRequestedPos;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized InputStream getBinaryInputStream(int columnIndex) throws SQLException {
/* 298 */     if (this.isBinaryEncoded && 
/* 299 */       isNull(columnIndex)) {
/* 300 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 304 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 306 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 308 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 310 */     if (length == -1L) {
/* 311 */       return null;
/*     */     }
/*     */     
/* 314 */     InputStream stream = new ByteArrayInputStream(this.rowFromServer.getByteBuffer(), offset, (int)length);
/*     */ 
/*     */     
/* 317 */     if (this.openStreams == null) {
/* 318 */       this.openStreams = new LinkedList();
/*     */     }
/*     */     
/* 321 */     return stream;
/*     */   }
/*     */   
/*     */   public byte[] getColumnValue(int index) throws SQLException {
/* 325 */     findAndSeekToOffset(index);
/*     */     
/* 327 */     if (!this.isBinaryEncoded) {
/* 328 */       return this.rowFromServer.readLenByteArray(0);
/*     */     }
/*     */     
/* 331 */     if (this.isNull[index]) {
/* 332 */       return null;
/*     */     }
/*     */     
/* 335 */     switch (this.metadata[index].getMysqlType()) {
/*     */       case 6:
/* 337 */         return null;
/*     */       
/*     */       case 1:
/* 340 */         return new byte[] { this.rowFromServer.readByte() };
/*     */       
/*     */       case 2:
/*     */       case 13:
/* 344 */         return this.rowFromServer.getBytes(2);
/*     */       
/*     */       case 3:
/*     */       case 9:
/* 348 */         return this.rowFromServer.getBytes(4);
/*     */       
/*     */       case 8:
/* 351 */         return this.rowFromServer.getBytes(8);
/*     */       
/*     */       case 4:
/* 354 */         return this.rowFromServer.getBytes(4);
/*     */       
/*     */       case 5:
/* 357 */         return this.rowFromServer.getBytes(8);
/*     */       
/*     */       case 0:
/*     */       case 7:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/*     */       case 15:
/*     */       case 16:
/*     */       case 246:
/*     */       case 249:
/*     */       case 250:
/*     */       case 251:
/*     */       case 252:
/*     */       case 253:
/*     */       case 254:
/*     */       case 255:
/* 374 */         return this.rowFromServer.readLenByteArray(0);
/*     */     } 
/*     */     
/* 377 */     throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + this.metadata[index].getMysqlType() + Messages.getString("MysqlIO.98") + (index + 1) + Messages.getString("MysqlIO.99") + this.metadata.length + Messages.getString("MysqlIO.100"), "S1000");
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
/*     */   public int getInt(int columnIndex) throws SQLException {
/* 389 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 391 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 393 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 395 */     if (length == -1L) {
/* 396 */       return 0;
/*     */     }
/*     */     
/* 399 */     return StringUtils.getInt(this.rowFromServer.getByteBuffer(), offset, offset + (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int columnIndex) throws SQLException {
/* 404 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 406 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 408 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 410 */     if (length == -1L) {
/* 411 */       return 0L;
/*     */     }
/*     */     
/* 414 */     return StringUtils.getLong(this.rowFromServer.getByteBuffer(), offset, offset + (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getNativeDouble(int columnIndex) throws SQLException {
/* 419 */     if (isNull(columnIndex)) {
/* 420 */       return 0.0D;
/*     */     }
/*     */     
/* 423 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 425 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 427 */     return getNativeDouble(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */   
/*     */   public float getNativeFloat(int columnIndex) throws SQLException {
/* 431 */     if (isNull(columnIndex)) {
/* 432 */       return 0.0F;
/*     */     }
/*     */     
/* 435 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 437 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 439 */     return getNativeFloat(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */   
/*     */   public int getNativeInt(int columnIndex) throws SQLException {
/* 443 */     if (isNull(columnIndex)) {
/* 444 */       return 0;
/*     */     }
/*     */     
/* 447 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 449 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 451 */     return getNativeInt(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */   
/*     */   public long getNativeLong(int columnIndex) throws SQLException {
/* 455 */     if (isNull(columnIndex)) {
/* 456 */       return 0L;
/*     */     }
/*     */     
/* 459 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 461 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 463 */     return getNativeLong(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */   
/*     */   public short getNativeShort(int columnIndex) throws SQLException {
/* 467 */     if (isNull(columnIndex)) {
/* 468 */       return 0;
/*     */     }
/*     */     
/* 471 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 473 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 475 */     return getNativeShort(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 481 */     if (isNull(columnIndex)) {
/* 482 */       return null;
/*     */     }
/*     */     
/* 485 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 487 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 489 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 491 */     return getNativeTimestamp(this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(int columnIndex) throws SQLException {
/* 496 */     InputStream stream = getBinaryInputStream(columnIndex);
/*     */     
/* 498 */     if (stream == null) {
/* 499 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 503 */       return new InputStreamReader(stream, this.metadata[columnIndex].getCharacterSet());
/*     */     }
/* 505 */     catch (UnsupportedEncodingException e) {
/* 506 */       SQLException sqlEx = SQLError.createSQLException("");
/*     */       
/* 508 */       sqlEx.initCause(e);
/*     */       
/* 510 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(int columnIndex, String encoding, ConnectionImpl conn) throws SQLException {
/* 516 */     if (this.isBinaryEncoded && 
/* 517 */       isNull(columnIndex)) {
/* 518 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 522 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 524 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 526 */     if (length == -1L) {
/* 527 */       return null;
/*     */     }
/*     */     
/* 530 */     if (length == 0L) {
/* 531 */       return "";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 537 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 539 */     return getString(encoding, conn, this.rowFromServer.getByteBuffer(), offset, (int)length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 546 */     if (isNull(columnIndex)) {
/* 547 */       return null;
/*     */     }
/*     */     
/* 550 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 552 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 554 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 556 */     return getTimeFast(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 563 */     if (isNull(columnIndex)) {
/* 564 */       return null;
/*     */     }
/*     */     
/* 567 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 569 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 571 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 573 */     return getTimestampFast(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFloatingPointNumber(int index) throws SQLException {
/* 579 */     if (this.isBinaryEncoded) {
/* 580 */       switch (this.metadata[index].getSQLType()) {
/*     */         case 2:
/*     */         case 3:
/*     */         case 6:
/*     */         case 8:
/* 585 */           return true;
/*     */       } 
/* 587 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 591 */     findAndSeekToOffset(index);
/*     */     
/* 593 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 595 */     if (length == -1L) {
/* 596 */       return false;
/*     */     }
/*     */     
/* 599 */     if (length == 0L) {
/* 600 */       return false;
/*     */     }
/*     */     
/* 603 */     for (int i = 0; i < (int)length; i++) {
/* 604 */       char c = (char)this.rowFromServer.readByte();
/*     */       
/* 606 */       if (c == 'e' || c == 'E') {
/* 607 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 611 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isNull(int index) throws SQLException {
/* 615 */     if (!this.isBinaryEncoded) {
/* 616 */       findAndSeekToOffset(index);
/*     */       
/* 618 */       return (this.rowFromServer.readFieldLength() == -1L);
/*     */     } 
/*     */     
/* 621 */     return this.isNull[index];
/*     */   }
/*     */   
/*     */   public long length(int index) throws SQLException {
/* 625 */     findAndSeekToOffset(index);
/*     */     
/* 627 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 629 */     if (length == -1L) {
/* 630 */       return 0L;
/*     */     }
/*     */     
/* 633 */     return length;
/*     */   }
/*     */   
/*     */   public void setColumnValue(int index, byte[] value) throws SQLException {
/* 637 */     throw new OperationNotSupportedException();
/*     */   }
/*     */   
/*     */   public ResultSetRow setMetadata(Field[] f) throws SQLException {
/* 641 */     super.setMetadata(f);
/*     */     
/* 643 */     if (this.isBinaryEncoded) {
/* 644 */       setupIsNullBitmask();
/*     */     }
/*     */     
/* 647 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setupIsNullBitmask() throws SQLException {
/* 656 */     if (this.isNull != null) {
/*     */       return;
/*     */     }
/*     */     
/* 660 */     this.rowFromServer.setPosition(this.preNullBitmaskHomePosition);
/*     */     
/* 662 */     int nullCount = (this.metadata.length + 9) / 8;
/*     */     
/* 664 */     byte[] nullBitMask = new byte[nullCount];
/*     */     
/* 666 */     for (int i = 0; i < nullCount; i++) {
/* 667 */       nullBitMask[i] = this.rowFromServer.readByte();
/*     */     }
/*     */     
/* 670 */     this.homePosition = this.rowFromServer.getPosition();
/*     */     
/* 672 */     this.isNull = new boolean[this.metadata.length];
/*     */     
/* 674 */     int nullMaskPos = 0;
/* 675 */     int bit = 4;
/*     */     
/* 677 */     for (int j = 0; j < this.metadata.length; j++) {
/*     */       
/* 679 */       this.isNull[j] = ((nullBitMask[nullMaskPos] & bit) != 0);
/*     */       
/* 681 */       if (((bit <<= 1) & 0xFF) == 0) {
/* 682 */         bit = 1;
/*     */         
/* 684 */         nullMaskPos++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getDateFast(int columnIndex, ConnectionImpl conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
/* 691 */     if (isNull(columnIndex)) {
/* 692 */       return null;
/*     */     }
/*     */     
/* 695 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 697 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 699 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 701 */     return getDateFast(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, conn, rs, targetCalendar);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getNativeDate(int columnIndex, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 707 */     if (isNull(columnIndex)) {
/* 708 */       return null;
/*     */     }
/*     */     
/* 711 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 713 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 715 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 717 */     return getNativeDate(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 725 */     if (isNull(columnIndex)) {
/* 726 */       return null;
/*     */     }
/*     */     
/* 729 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 731 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 733 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 735 */     return getNativeDateTimeValue(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, jdbcType, mysqlType, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 743 */     if (isNull(columnIndex)) {
/* 744 */       return null;
/*     */     }
/*     */     
/* 747 */     findAndSeekToOffset(columnIndex);
/*     */     
/* 749 */     long length = this.rowFromServer.readFieldLength();
/*     */     
/* 751 */     int offset = this.rowFromServer.getPosition();
/*     */     
/* 753 */     return getNativeTime(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\BufferRow.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */