/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
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
/*     */ public class JDBC4UpdatableResultSet
/*     */   extends UpdatableResultSet
/*     */ {
/*     */   public JDBC4UpdatableResultSet(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt) throws SQLException {
/*  47 */     super(catalog, fields, tuples, conn, creatorStmt);
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/*  51 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
/*  56 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
/*  61 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
/*  66 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
/*  71 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/*  75 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
/*  80 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/*  86 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader) throws SQLException {
/*  91 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
/*  96 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 101 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/* 106 */     updateNCharacterStream(columnIndex, x, (int)length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader) throws SQLException {
/* 112 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
/* 117 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/* 121 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateRowId(int columnIndex, RowId x) throws SQLException {
/* 126 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
/* 130 */     updateAsciiStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 134 */     updateAsciiStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
/* 138 */     updateBinaryStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 142 */     updateBinaryStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/* 146 */     updateBlob(findColumn(columnLabel), inputStream);
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
/* 150 */     updateBlob(findColumn(columnLabel), inputStream, length);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 154 */     updateCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/* 158 */     updateCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader) throws SQLException {
/* 162 */     updateClob(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 166 */     updateClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 170 */     updateNCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/* 175 */     updateNCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
/* 180 */     updateNClob(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 185 */     updateNClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
/* 189 */     updateSQLXML(findColumn(columnLabel), xmlObject);
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
/*     */   
/*     */   public synchronized void updateNCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
/* 212 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/* 213 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 214 */       throw new SQLException("Can not call updateNCharacterStream() when field's character set isn't UTF-8");
/*     */     }
/*     */ 
/*     */     
/* 218 */     if (!this.onInsertRow) {
/* 219 */       if (!this.doingUpdates) {
/* 220 */         this.doingUpdates = true;
/* 221 */         syncUpdate();
/*     */       } 
/*     */       
/* 224 */       ((JDBC4PreparedStatement)this.updater).setNCharacterStream(columnIndex, x, length);
/*     */     } else {
/* 226 */       ((JDBC4PreparedStatement)this.inserter).setNCharacterStream(columnIndex, x, length);
/*     */       
/* 228 */       if (x == null) {
/* 229 */         this.thisRow.setColumnValue(columnIndex, null);
/*     */       } else {
/* 231 */         this.thisRow.setColumnValue(columnIndex, STREAM_DATA_MARKER);
/*     */       } 
/*     */     } 
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
/*     */   public synchronized void updateNCharacterStream(String columnName, Reader reader, int length) throws SQLException {
/* 255 */     updateNCharacterStream(findColumn(columnName), reader, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
/* 263 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/* 264 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 265 */       throw new SQLException("Can not call updateNClob() when field's character set isn't UTF-8");
/*     */     }
/*     */     
/* 268 */     if (nClob == null) {
/* 269 */       updateNull(columnIndex);
/*     */     } else {
/* 271 */       updateNCharacterStream(columnIndex, nClob.getCharacterStream(), (int)nClob.length());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateNClob(String columnName, NClob nClob) throws SQLException {
/* 281 */     updateNClob(findColumn(columnName), nClob);
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
/*     */   public synchronized void updateNString(int columnIndex, String x) throws SQLException {
/* 300 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/* 301 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 302 */       throw new SQLException("Can not call updateNString() when field's character set isn't UTF-8");
/*     */     }
/*     */     
/* 305 */     if (!this.onInsertRow) {
/* 306 */       if (!this.doingUpdates) {
/* 307 */         this.doingUpdates = true;
/* 308 */         syncUpdate();
/*     */       } 
/*     */       
/* 311 */       ((JDBC4PreparedStatement)this.updater).setNString(columnIndex, x);
/*     */     } else {
/* 313 */       ((JDBC4PreparedStatement)this.inserter).setNString(columnIndex, x);
/*     */       
/* 315 */       if (x == null) {
/* 316 */         this.thisRow.setColumnValue(columnIndex, null);
/*     */       } else {
/* 318 */         this.thisRow.setColumnValue(columnIndex, StringUtils.getBytes(x, this.charConverter, fieldEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode()));
/*     */       } 
/*     */     } 
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
/*     */   public synchronized void updateNString(String columnName, String x) throws SQLException {
/* 342 */     updateNString(findColumn(columnName), x);
/*     */   }
/*     */   
/*     */   public int getHoldability() throws SQLException {
/* 346 */     throw SQLError.notImplemented();
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
/*     */   protected NClob getNativeNClob(int columnIndex) throws SQLException {
/* 362 */     String stringVal = getStringForNClob(columnIndex);
/*     */     
/* 364 */     if (stringVal == null) {
/* 365 */       return null;
/*     */     }
/*     */     
/* 368 */     return getNClobFromString(stringVal, columnIndex);
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
/*     */   public Reader getNCharacterStream(int columnIndex) throws SQLException {
/* 387 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/* 388 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 389 */       throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/*     */     
/* 393 */     return getCharacterStream(columnIndex);
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
/*     */   public Reader getNCharacterStream(String columnName) throws SQLException {
/* 412 */     return getNCharacterStream(findColumn(columnName));
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
/*     */   public NClob getNClob(int columnIndex) throws SQLException {
/* 427 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/*     */     
/* 429 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 430 */       throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/*     */     
/* 434 */     if (!this.isBinaryEncoded) {
/* 435 */       String asString = getStringForNClob(columnIndex);
/*     */       
/* 437 */       if (asString == null) {
/* 438 */         return null;
/*     */       }
/*     */       
/* 441 */       return new JDBC4NClob(asString);
/*     */     } 
/*     */     
/* 444 */     return getNativeNClob(columnIndex);
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
/*     */   public NClob getNClob(String columnName) throws SQLException {
/* 459 */     return getNClob(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   
/*     */   private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException {
/* 464 */     return new JDBC4NClob(stringVal);
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
/*     */   public String getNString(int columnIndex) throws SQLException {
/* 481 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/*     */     
/* 483 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 484 */       throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/*     */     
/* 488 */     return getString(columnIndex);
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
/*     */   public String getNString(String columnName) throws SQLException {
/* 506 */     return getNString(findColumn(columnName));
/*     */   }
/*     */   
/*     */   public RowId getRowId(int columnIndex) throws SQLException {
/* 510 */     throw SQLError.notImplemented();
/*     */   }
/*     */   
/*     */   public RowId getRowId(String columnLabel) throws SQLException {
/* 514 */     return getRowId(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/* 518 */     return new JDBC4MysqlSQLXML(this, columnIndex);
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/* 522 */     return getSQLXML(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 526 */     String asString = null;
/*     */     
/* 528 */     String forcedEncoding = "UTF-8";
/*     */     
/*     */     try {
/* 531 */       byte[] asBytes = null;
/*     */       
/* 533 */       if (!this.isBinaryEncoded) {
/* 534 */         asBytes = getBytes(columnIndex);
/*     */       } else {
/* 536 */         asBytes = getNativeBytes(columnIndex, true);
/*     */       } 
/*     */       
/* 539 */       if (asBytes != null) {
/* 540 */         asString = new String(asBytes, forcedEncoding);
/*     */       }
/* 542 */     } catch (UnsupportedEncodingException uee) {
/* 543 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009");
/*     */     } 
/*     */ 
/*     */     
/* 547 */     return asString;
/*     */   }
/*     */   
/*     */   public synchronized boolean isClosed() throws SQLException {
/* 551 */     return this.isClosed;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 577 */     checkClosed();
/*     */ 
/*     */ 
/*     */     
/* 581 */     return iface.isInstance(this);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 607 */       return iface.cast(this);
/* 608 */     } catch (ClassCastException cce) {
/* 609 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4UpdatableResultSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */