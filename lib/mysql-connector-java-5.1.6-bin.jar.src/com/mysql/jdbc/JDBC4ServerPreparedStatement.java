/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.Reader;
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
/*     */ public class JDBC4ServerPreparedStatement
/*     */   extends ServerPreparedStatement
/*     */ {
/*     */   public JDBC4ServerPreparedStatement(ConnectionImpl conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  43 */     super(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*  54 */     if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8"))
/*     */     {
/*  56 */       throw SQLError.createSQLException("Can not call setNCharacterStream() when connection character set isn't UTF-8");
/*     */     }
/*     */ 
/*     */     
/*  60 */     checkClosed();
/*     */     
/*  62 */     if (reader == null) {
/*  63 */       setNull(parameterIndex, -2);
/*     */     } else {
/*  65 */       ServerPreparedStatement.BindValue binding = getBinding(parameterIndex, true);
/*  66 */       setType(binding, 252);
/*     */       
/*  68 */       binding.value = reader;
/*  69 */       binding.isNull = false;
/*  70 */       binding.isLongData = true;
/*     */       
/*  72 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/*  73 */         binding.bindLength = length;
/*     */       } else {
/*  75 */         binding.bindLength = -1L;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob x) throws SQLException {
/*  84 */     setNClob(parameterIndex, x.getCharacterStream(), this.connection.getUseStreamLengthsInPrepStmts() ? x.length() : -1L);
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
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/* 104 */     if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8"))
/*     */     {
/* 106 */       throw SQLError.createSQLException("Can not call setNClob() when connection character set isn't UTF-8");
/*     */     }
/*     */ 
/*     */     
/* 110 */     checkClosed();
/*     */     
/* 112 */     if (reader == null) {
/* 113 */       setNull(parameterIndex, 2011);
/*     */     } else {
/* 115 */       ServerPreparedStatement.BindValue binding = getBinding(parameterIndex, true);
/* 116 */       setType(binding, 252);
/*     */       
/* 118 */       binding.value = reader;
/* 119 */       binding.isNull = false;
/* 120 */       binding.isLongData = true;
/*     */       
/* 122 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 123 */         binding.bindLength = length;
/*     */       } else {
/* 125 */         binding.bindLength = -1L;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNString(int parameterIndex, String x) throws SQLException {
/* 134 */     if (this.charEncoding.equalsIgnoreCase("UTF-8") || this.charEncoding.equalsIgnoreCase("utf8")) {
/*     */       
/* 136 */       setString(parameterIndex, x);
/*     */     } else {
/* 138 */       throw SQLError.createSQLException("Can not call setNString() when connection character set isn't UTF-8");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/* 144 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/* 149 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4ServerPreparedStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */