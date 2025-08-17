/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
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
/*     */ public class ByteArrayRow
/*     */   extends ResultSetRow
/*     */ {
/*     */   byte[][] internalRowData;
/*     */   
/*     */   public ByteArrayRow(byte[][] internalRowData) {
/*  48 */     this.internalRowData = internalRowData;
/*     */   }
/*     */   
/*     */   public byte[] getColumnValue(int index) throws SQLException {
/*  52 */     return this.internalRowData[index];
/*     */   }
/*     */   
/*     */   public void setColumnValue(int index, byte[] value) throws SQLException {
/*  56 */     this.internalRowData[index] = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(int index, String encoding, ConnectionImpl conn) throws SQLException {
/*  61 */     byte[] columnData = this.internalRowData[index];
/*     */     
/*  63 */     if (columnData == null) {
/*  64 */       return null;
/*     */     }
/*     */     
/*  67 */     return getString(encoding, conn, columnData, 0, columnData.length);
/*     */   }
/*     */   
/*     */   public boolean isNull(int index) throws SQLException {
/*  71 */     return (this.internalRowData[index] == null);
/*     */   }
/*     */   
/*     */   public boolean isFloatingPointNumber(int index) throws SQLException {
/*  75 */     byte[] numAsBytes = this.internalRowData[index];
/*     */     
/*  77 */     if (this.internalRowData[index] == null || (this.internalRowData[index]).length == 0)
/*     */     {
/*  79 */       return false;
/*     */     }
/*     */     
/*  82 */     for (int i = 0; i < numAsBytes.length; i++) {
/*  83 */       if ((char)numAsBytes[i] == 'e' || (char)numAsBytes[i] == 'E') {
/*  84 */         return true;
/*     */       }
/*     */     } 
/*     */     
/*  88 */     return false;
/*     */   }
/*     */   
/*     */   public long length(int index) throws SQLException {
/*  92 */     if (this.internalRowData[index] == null) {
/*  93 */       return 0L;
/*     */     }
/*     */     
/*  96 */     return (this.internalRowData[index]).length;
/*     */   }
/*     */   
/*     */   public int getInt(int columnIndex) {
/* 100 */     if (this.internalRowData[columnIndex] == null) {
/* 101 */       return 0;
/*     */     }
/*     */     
/* 104 */     return StringUtils.getInt(this.internalRowData[columnIndex]);
/*     */   }
/*     */   
/*     */   public long getLong(int columnIndex) {
/* 108 */     if (this.internalRowData[columnIndex] == null) {
/* 109 */       return 0L;
/*     */     }
/*     */     
/* 112 */     return StringUtils.getLong(this.internalRowData[columnIndex]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 118 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 120 */     if (columnValue == null) {
/* 121 */       return null;
/*     */     }
/*     */     
/* 124 */     return getTimestampFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNativeDouble(int columnIndex) throws SQLException {
/* 130 */     if (this.internalRowData[columnIndex] == null) {
/* 131 */       return 0.0D;
/*     */     }
/*     */     
/* 134 */     return getNativeDouble(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public float getNativeFloat(int columnIndex) throws SQLException {
/* 138 */     if (this.internalRowData[columnIndex] == null) {
/* 139 */       return 0.0F;
/*     */     }
/*     */     
/* 142 */     return getNativeFloat(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public int getNativeInt(int columnIndex) throws SQLException {
/* 146 */     if (this.internalRowData[columnIndex] == null) {
/* 147 */       return 0;
/*     */     }
/*     */     
/* 150 */     return getNativeInt(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public long getNativeLong(int columnIndex) throws SQLException {
/* 154 */     if (this.internalRowData[columnIndex] == null) {
/* 155 */       return 0L;
/*     */     }
/*     */     
/* 158 */     return getNativeLong(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public short getNativeShort(int columnIndex) throws SQLException {
/* 162 */     if (this.internalRowData[columnIndex] == null) {
/* 163 */       return 0;
/*     */     }
/*     */     
/* 166 */     return getNativeShort(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 172 */     byte[] bits = this.internalRowData[columnIndex];
/*     */     
/* 174 */     if (bits == null) {
/* 175 */       return null;
/*     */     }
/*     */     
/* 178 */     return getNativeTimestamp(bits, 0, bits.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeOpenStreams() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getBinaryInputStream(int columnIndex) throws SQLException {
/* 188 */     if (this.internalRowData[columnIndex] == null) {
/* 189 */       return null;
/*     */     }
/*     */     
/* 192 */     return new ByteArrayInputStream(this.internalRowData[columnIndex]);
/*     */   }
/*     */   
/*     */   public Reader getReader(int columnIndex) throws SQLException {
/* 196 */     InputStream stream = getBinaryInputStream(columnIndex);
/*     */     
/* 198 */     if (stream == null) {
/* 199 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 203 */       return new InputStreamReader(stream, this.metadata[columnIndex].getCharacterSet());
/*     */     }
/* 205 */     catch (UnsupportedEncodingException e) {
/* 206 */       SQLException sqlEx = SQLError.createSQLException("");
/*     */       
/* 208 */       sqlEx.initCause(e);
/*     */       
/* 210 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 217 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 219 */     if (columnValue == null) {
/* 220 */       return null;
/*     */     }
/*     */     
/* 223 */     return getTimeFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getDateFast(int columnIndex, ConnectionImpl conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
/* 229 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 231 */     if (columnValue == null) {
/* 232 */       return null;
/*     */     }
/*     */     
/* 235 */     return getDateFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, conn, rs, targetCalendar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 243 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 245 */     if (columnValue == null) {
/* 246 */       return null;
/*     */     }
/*     */     
/* 249 */     return getNativeDateTimeValue(columnIndex, columnValue, 0, columnValue.length, targetCalendar, jdbcType, mysqlType, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getNativeDate(int columnIndex, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 256 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 258 */     if (columnValue == null) {
/* 259 */       return null;
/*     */     }
/*     */     
/* 262 */     return getNativeDate(columnIndex, columnValue, 0, columnValue.length, conn, rs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/* 269 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 271 */     if (columnValue == null) {
/* 272 */       return null;
/*     */     }
/*     */     
/* 275 */     return getNativeTime(columnIndex, columnValue, 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ByteArrayRow.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */