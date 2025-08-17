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
/*     */ public class JDBC4ResultSet
/*     */   extends ResultSetImpl
/*     */ {
/*     */   public JDBC4ResultSet(long updateCount, long updateID, ConnectionImpl conn, StatementImpl creatorStmt) {
/*  47 */     super(updateCount, updateID, conn, creatorStmt);
/*     */   }
/*     */ 
/*     */   
/*     */   public JDBC4ResultSet(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt) throws SQLException {
/*  52 */     super(catalog, fields, tuples, conn, creatorStmt);
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
/*  71 */     checkColumnBounds(columnIndex);
/*     */     
/*  73 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/*  74 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/*  75 */       throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*     */     }
/*     */     
/*  78 */     return getCharacterStream(columnIndex);
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
/*  97 */     return getNCharacterStream(findColumn(columnName));
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
/* 112 */     checkColumnBounds(columnIndex);
/*     */     
/* 114 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/* 115 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 116 */       throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*     */     }
/*     */     
/* 119 */     if (!this.isBinaryEncoded) {
/* 120 */       String asString = getStringForNClob(columnIndex);
/*     */       
/* 122 */       if (asString == null) {
/* 123 */         return null;
/*     */       }
/*     */       
/* 126 */       return new JDBC4NClob(asString);
/*     */     } 
/*     */     
/* 129 */     return getNativeNClob(columnIndex);
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
/* 144 */     return getNClob(findColumn(columnName));
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
/* 160 */     String stringVal = getStringForNClob(columnIndex);
/*     */     
/* 162 */     if (stringVal == null) {
/* 163 */       return null;
/*     */     }
/*     */     
/* 166 */     return getNClobFromString(stringVal, columnIndex);
/*     */   }
/*     */   
/*     */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 170 */     String asString = null;
/*     */     
/* 172 */     String forcedEncoding = "UTF-8";
/*     */     
/*     */     try {
/* 175 */       byte[] asBytes = null;
/*     */       
/* 177 */       if (!this.isBinaryEncoded) {
/* 178 */         asBytes = getBytes(columnIndex);
/*     */       } else {
/* 180 */         asBytes = getNativeBytes(columnIndex, true);
/*     */       } 
/*     */       
/* 183 */       if (asBytes != null) {
/* 184 */         asString = new String(asBytes, forcedEncoding);
/*     */       }
/* 186 */     } catch (UnsupportedEncodingException uee) {
/* 187 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009");
/*     */     } 
/*     */ 
/*     */     
/* 191 */     return asString;
/*     */   }
/*     */ 
/*     */   
/*     */   private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException {
/* 196 */     return new JDBC4NClob(stringVal);
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
/* 213 */     checkColumnBounds(columnIndex);
/*     */     
/* 215 */     String fieldEncoding = this.fields[columnIndex - 1].getCharacterSet();
/* 216 */     if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 217 */       throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*     */     }
/*     */     
/* 220 */     return getString(columnIndex);
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
/* 238 */     return getNString(findColumn(columnName));
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
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
/* 262 */     throw new NotUpdatable();
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
/*     */   public void updateNCharacterStream(String columnName, Reader reader, int length) throws SQLException {
/* 284 */     updateNCharacterStream(findColumn(columnName), reader, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateNClob(String columnName, NClob nClob) throws SQLException {
/* 291 */     updateNClob(findColumn(columnName), nClob);
/*     */   }
/*     */   
/*     */   public void updateRowId(int columnIndex, RowId x) throws SQLException {
/* 295 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateRowId(String columnName, RowId x) throws SQLException {
/* 299 */     updateRowId(findColumn(columnName), x);
/*     */   }
/*     */   
/*     */   public int getHoldability() throws SQLException {
/* 303 */     throw SQLError.notImplemented();
/*     */   }
/*     */   
/*     */   public RowId getRowId(int columnIndex) throws SQLException {
/* 307 */     throw SQLError.notImplemented();
/*     */   }
/*     */   
/*     */   public RowId getRowId(String columnLabel) throws SQLException {
/* 311 */     return getRowId(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/* 315 */     checkColumnBounds(columnIndex);
/*     */     
/* 317 */     return new JDBC4MysqlSQLXML(this, columnIndex);
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/* 321 */     return getSQLXML(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   public synchronized boolean isClosed() throws SQLException {
/* 325 */     return this.isClosed;
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/* 329 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
/* 334 */     updateAsciiStream(findColumn(columnLabel), x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
/* 339 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 344 */     updateAsciiStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
/* 348 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
/* 353 */     updateBinaryStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
/* 357 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 362 */     updateBinaryStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
/* 366 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/* 370 */     updateBlob(findColumn(columnLabel), inputStream);
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/* 374 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
/* 379 */     updateBlob(findColumn(columnLabel), inputStream, length);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 383 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 388 */     updateCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/* 392 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/* 397 */     updateCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader) throws SQLException {
/* 401 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader) throws SQLException {
/* 406 */     updateClob(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
/* 410 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 415 */     updateClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 419 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 424 */     updateNCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/* 429 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/* 434 */     updateNCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
/* 438 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader) throws SQLException {
/* 443 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
/* 448 */     updateNClob(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
/* 453 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 457 */     updateNClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNString(int columnIndex, String nString) throws SQLException {
/* 461 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateNString(String columnLabel, String nString) throws SQLException {
/* 466 */     updateNString(findColumn(columnLabel), nString);
/*     */   }
/*     */   
/*     */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/* 470 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
/* 475 */     updateSQLXML(findColumn(columnLabel), xmlObject);
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
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 495 */     checkClosed();
/*     */ 
/*     */ 
/*     */     
/* 499 */     return iface.isInstance(this);
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
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 520 */       return iface.cast(this);
/* 521 */     } catch (ClassCastException cce) {
/* 522 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4ResultSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */