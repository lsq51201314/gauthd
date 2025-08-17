/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.sql.Clob;
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
/*     */ public class Clob
/*     */   implements Clob, OutputStreamWatcher, WriterWatcher
/*     */ {
/*     */   private String charData;
/*     */   
/*     */   Clob() {
/*  45 */     this.charData = "";
/*     */   }
/*     */   
/*     */   Clob(String charDataInit) {
/*  49 */     this.charData = charDataInit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getAsciiStream() throws SQLException {
/*  56 */     if (this.charData != null) {
/*  57 */       return new ByteArrayInputStream(this.charData.getBytes());
/*     */     }
/*     */     
/*  60 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getCharacterStream() throws SQLException {
/*  67 */     if (this.charData != null) {
/*  68 */       return new StringReader(this.charData);
/*     */     }
/*     */     
/*  71 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSubString(long startPos, int length) throws SQLException {
/*  78 */     if (startPos < 1L) {
/*  79 */       throw SQLError.createSQLException(Messages.getString("Clob.6"), "S1009");
/*     */     }
/*     */ 
/*     */     
/*  83 */     int adjustedStartPos = (int)startPos - 1;
/*  84 */     int adjustedEndIndex = adjustedStartPos + length;
/*     */     
/*  86 */     if (this.charData != null) {
/*  87 */       if (adjustedEndIndex > this.charData.length()) {
/*  88 */         throw SQLError.createSQLException(Messages.getString("Clob.7"), "S1009");
/*     */       }
/*     */ 
/*     */       
/*  92 */       return this.charData.substring(adjustedStartPos, adjustedEndIndex);
/*     */     } 
/*     */ 
/*     */     
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long length() throws SQLException {
/* 103 */     if (this.charData != null) {
/* 104 */       return this.charData.length();
/*     */     }
/*     */     
/* 107 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position(Clob arg0, long arg1) throws SQLException {
/* 114 */     return position(arg0.getSubString(0L, (int)arg0.length()), arg1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position(String stringToFind, long startPos) throws SQLException {
/* 122 */     if (startPos < 1L) {
/* 123 */       throw SQLError.createSQLException(Messages.getString("Clob.8") + startPos + Messages.getString("Clob.9"), "S1009");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 128 */     if (this.charData != null) {
/* 129 */       if (startPos - 1L > this.charData.length()) {
/* 130 */         throw SQLError.createSQLException(Messages.getString("Clob.10"), "S1009");
/*     */       }
/*     */ 
/*     */       
/* 134 */       int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
/*     */       
/* 136 */       return (pos == -1) ? -1L : (pos + 1);
/*     */     } 
/*     */     
/* 139 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream setAsciiStream(long indexToWriteAt) throws SQLException {
/* 146 */     if (indexToWriteAt < 1L) {
/* 147 */       throw SQLError.createSQLException(Messages.getString("Clob.0"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 151 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 152 */     bytesOut.setWatcher(this);
/*     */     
/* 154 */     if (indexToWriteAt > 0L) {
/* 155 */       bytesOut.write(this.charData.getBytes(), 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/*     */     
/* 159 */     return bytesOut;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Writer setCharacterStream(long indexToWriteAt) throws SQLException {
/* 166 */     if (indexToWriteAt < 1L) {
/* 167 */       throw SQLError.createSQLException(Messages.getString("Clob.1"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 171 */     WatchableWriter writer = new WatchableWriter();
/* 172 */     writer.setWatcher(this);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 177 */     if (indexToWriteAt > 1L) {
/* 178 */       writer.write(this.charData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */     
/* 181 */     return writer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setString(long pos, String str) throws SQLException {
/* 188 */     if (pos < 1L) {
/* 189 */       throw SQLError.createSQLException(Messages.getString("Clob.2"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 193 */     if (str == null) {
/* 194 */       throw SQLError.createSQLException(Messages.getString("Clob.3"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 198 */     StringBuffer charBuf = new StringBuffer(this.charData);
/*     */     
/* 200 */     pos--;
/*     */     
/* 202 */     int strLength = str.length();
/*     */     
/* 204 */     charBuf.replace((int)pos, (int)(pos + strLength), str);
/*     */     
/* 206 */     this.charData = charBuf.toString();
/*     */     
/* 208 */     return strLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setString(long pos, String str, int offset, int len) throws SQLException {
/* 216 */     if (pos < 1L) {
/* 217 */       throw SQLError.createSQLException(Messages.getString("Clob.4"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 221 */     if (str == null) {
/* 222 */       throw SQLError.createSQLException(Messages.getString("Clob.5"), "S1009");
/*     */     }
/*     */ 
/*     */     
/* 226 */     StringBuffer charBuf = new StringBuffer(this.charData);
/*     */     
/* 228 */     pos--;
/*     */     
/* 230 */     String replaceString = str.substring(offset, len);
/*     */     
/* 232 */     charBuf.replace((int)pos, (int)(pos + replaceString.length()), replaceString);
/*     */ 
/*     */     
/* 235 */     this.charData = charBuf.toString();
/*     */     
/* 237 */     return len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void streamClosed(WatchableOutputStream out) {
/* 244 */     int streamSize = out.size();
/*     */     
/* 246 */     if (streamSize < this.charData.length()) {
/*     */       try {
/* 248 */         out.write(StringUtils.getBytes(this.charData, (String)null, (String)null, false, (ConnectionImpl)null), streamSize, this.charData.length() - streamSize);
/*     */       
/*     */       }
/* 251 */       catch (SQLException ex) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 256 */     this.charData = StringUtils.toAsciiString(out.toByteArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(long length) throws SQLException {
/* 263 */     if (length > this.charData.length()) {
/* 264 */       throw SQLError.createSQLException(Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 270 */     this.charData = this.charData.substring(0, (int)length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writerClosed(char[] charDataBeingWritten) {
/* 277 */     this.charData = new String(charDataBeingWritten);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writerClosed(WatchableWriter out) {
/* 284 */     int dataLength = out.size();
/*     */     
/* 286 */     if (dataLength < this.charData.length()) {
/* 287 */       out.write(this.charData, dataLength, this.charData.length() - dataLength);
/*     */     }
/*     */ 
/*     */     
/* 291 */     this.charData = out.toString();
/*     */   }
/*     */   
/*     */   public void free() throws SQLException {
/* 295 */     this.charData = null;
/*     */   }
/*     */   
/*     */   public Reader getCharacterStream(long pos, long length) throws SQLException {
/* 299 */     return new StringReader(getSubString(pos, (int)length));
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Clob.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */