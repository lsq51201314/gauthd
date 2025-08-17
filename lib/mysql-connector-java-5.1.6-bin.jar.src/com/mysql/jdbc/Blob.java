/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
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
/*     */ public class Blob
/*     */   implements Blob, OutputStreamWatcher
/*     */ {
/*  59 */   private byte[] binaryData = null;
/*     */ 
/*     */   
/*     */   private boolean isClosed = false;
/*     */ 
/*     */   
/*     */   Blob() {
/*  66 */     setBinaryData(Constants.EMPTY_BYTE_ARRAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Blob(byte[] data) {
/*  76 */     setBinaryData(data);
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
/*     */   
/*     */   Blob(byte[] data, ResultSetInternalMethods creatorResultSetToSet, int columnIndexToSet) {
/*  90 */     setBinaryData(data);
/*     */   }
/*     */   
/*     */   private synchronized byte[] getBinaryData() {
/*  94 */     return this.binaryData;
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
/*     */   public synchronized InputStream getBinaryStream() throws SQLException {
/* 106 */     checkClosed();
/*     */     
/* 108 */     return new ByteArrayInputStream(getBinaryData());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized byte[] getBytes(long pos, int length) throws SQLException {
/* 127 */     checkClosed();
/*     */     
/* 129 */     if (pos < 1L) {
/* 130 */       throw SQLError.createSQLException(Messages.getString("Blob.2"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 134 */     pos--;
/*     */     
/* 136 */     if (pos > this.binaryData.length) {
/* 137 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009");
/*     */     }
/*     */ 
/*     */     
/* 141 */     if (pos + length > this.binaryData.length) {
/* 142 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009");
/*     */     }
/*     */ 
/*     */     
/* 146 */     byte[] newData = new byte[length];
/* 147 */     System.arraycopy(getBinaryData(), (int)pos, newData, 0, length);
/*     */     
/* 149 */     return newData;
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
/*     */   public synchronized long length() throws SQLException {
/* 162 */     checkClosed();
/*     */     
/* 164 */     return (getBinaryData()).length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized long position(byte[] pattern, long start) throws SQLException {
/* 171 */     throw SQLError.createSQLException("Not implemented");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized long position(Blob pattern, long start) throws SQLException {
/* 189 */     checkClosed();
/*     */     
/* 191 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */   
/*     */   private synchronized void setBinaryData(byte[] newBinaryData) {
/* 195 */     this.binaryData = newBinaryData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
/* 203 */     checkClosed();
/*     */     
/* 205 */     if (indexToWriteAt < 1L) {
/* 206 */       throw SQLError.createSQLException(Messages.getString("Blob.0"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 210 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 211 */     bytesOut.setWatcher(this);
/*     */     
/* 213 */     if (indexToWriteAt > 0L) {
/* 214 */       bytesOut.write(this.binaryData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */     
/* 217 */     return bytesOut;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes) throws SQLException {
/* 224 */     checkClosed();
/*     */     
/* 226 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
/* 234 */     checkClosed();
/*     */     
/* 236 */     OutputStream bytesOut = setBinaryStream(writeAt);
/*     */     
/*     */     try {
/* 239 */       bytesOut.write(bytes, offset, length);
/* 240 */     } catch (IOException ioEx) {
/* 241 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Blob.1"), "S1000");
/*     */       
/* 243 */       sqlEx.initCause(ioEx);
/*     */       
/* 245 */       throw sqlEx;
/*     */     } finally {
/*     */       try {
/* 248 */         bytesOut.close();
/* 249 */       } catch (IOException doNothing) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 254 */     return length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void streamClosed(byte[] byteData) {
/* 261 */     this.binaryData = byteData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void streamClosed(WatchableOutputStream out) {
/* 268 */     int streamSize = out.size();
/*     */     
/* 270 */     if (streamSize < this.binaryData.length) {
/* 271 */       out.write(this.binaryData, streamSize, this.binaryData.length - streamSize);
/*     */     }
/*     */ 
/*     */     
/* 275 */     this.binaryData = out.toByteArray();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void truncate(long len) throws SQLException {
/* 297 */     checkClosed();
/*     */     
/* 299 */     if (len < 0L) {
/* 300 */       throw SQLError.createSQLException("\"len\" argument can not be < 1.", "S1009");
/*     */     }
/*     */ 
/*     */     
/* 304 */     if (len > this.binaryData.length) {
/* 305 */       throw SQLError.createSQLException("\"len\" argument can not be larger than the BLOB's length.", "S1009");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 312 */     byte[] newData = new byte[(int)len];
/* 313 */     System.arraycopy(getBinaryData(), 0, newData, 0, (int)len);
/* 314 */     this.binaryData = newData;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void free() throws SQLException {
/* 336 */     this.binaryData = null;
/* 337 */     this.isClosed = true;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized InputStream getBinaryStream(long pos, long length) throws SQLException {
/* 357 */     checkClosed();
/*     */     
/* 359 */     if (pos < 1L) {
/* 360 */       throw SQLError.createSQLException("\"pos\" argument can not be < 1.", "S1009");
/*     */     }
/*     */ 
/*     */     
/* 364 */     pos--;
/*     */     
/* 366 */     if (pos > this.binaryData.length) {
/* 367 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009");
/*     */     }
/*     */ 
/*     */     
/* 371 */     if (pos + length > this.binaryData.length) {
/* 372 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009");
/*     */     }
/*     */ 
/*     */     
/* 376 */     return new ByteArrayInputStream(getBinaryData(), (int)pos, (int)length);
/*     */   }
/*     */   
/*     */   private synchronized void checkClosed() throws SQLException {
/* 380 */     if (this.isClosed)
/* 381 */       throw SQLError.createSQLException("Invalid operation on closed BLOB", "S1009"); 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Blob.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */