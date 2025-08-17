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
/*     */ public class JDBC4CallableStatement
/*     */   extends CallableStatement
/*     */ {
/*     */   public JDBC4CallableStatement(ConnectionImpl conn, CallableStatement.CallableStatementParamInfo paramInfo) throws SQLException {
/*  41 */     super(conn, paramInfo);
/*     */   }
/*     */ 
/*     */   
/*     */   public JDBC4CallableStatement(ConnectionImpl conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
/*  46 */     super(conn, sql, catalog, isFunctionCall);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*  51 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*     */   }
/*     */   
/*     */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*  55 */     JDBC4PreparedStatementHelper.setRowId(this, getNamedParamIndex(parameterName, false), x);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*  61 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*  66 */     JDBC4PreparedStatementHelper.setSQLXML(this, getNamedParamIndex(parameterName, false), xmlObject);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SQLXML getSQLXML(int parameterIndex) throws SQLException {
/*  72 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/*  74 */     SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*     */     
/*  77 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/*  79 */     return retValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public SQLXML getSQLXML(String parameterName) throws SQLException {
/*  84 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML(fixParameterName(parameterName));
/*     */ 
/*     */     
/*  92 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/*  94 */     return retValue;
/*     */   }
/*     */   
/*     */   public RowId getRowId(int parameterIndex) throws SQLException {
/*  98 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 100 */     RowId retValue = ((JDBC4ResultSet)rs).getRowId(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*     */     
/* 103 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 105 */     return retValue;
/*     */   }
/*     */   
/*     */   public RowId getRowId(String parameterName) throws SQLException {
/* 109 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     RowId retValue = ((JDBC4ResultSet)rs).getRowId(fixParameterName(parameterName));
/*     */ 
/*     */     
/* 117 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 119 */     return retValue;
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
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/* 134 */     JDBC4PreparedStatementHelper.setNClob(this, parameterIndex, value);
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, NClob value) throws SQLException {
/* 138 */     JDBC4PreparedStatementHelper.setNClob(this, getNamedParamIndex(parameterName, false), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNClob(String parameterName, Reader reader) throws SQLException {
/* 145 */     setNClob(getNamedParamIndex(parameterName, false), reader);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
/* 151 */     setNClob(getNamedParamIndex(parameterName, false), reader, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNString(String parameterName, String value) throws SQLException {
/* 157 */     setNString(getNamedParamIndex(parameterName, false), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getCharacterStream(int parameterIndex) throws SQLException {
/* 164 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 166 */     Reader retValue = rs.getCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*     */     
/* 169 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 171 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getCharacterStream(String parameterName) throws SQLException {
/* 178 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     Reader retValue = rs.getCharacterStream(fixParameterName(parameterName));
/*     */ 
/*     */     
/* 186 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 188 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/* 195 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 197 */     Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*     */     
/* 200 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 202 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getNCharacterStream(String parameterName) throws SQLException {
/* 209 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 214 */     Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream(fixParameterName(parameterName));
/*     */ 
/*     */     
/* 217 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 219 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NClob getNClob(int parameterIndex) throws SQLException {
/* 226 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 228 */     NClob retValue = ((JDBC4ResultSet)rs).getNClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*     */     
/* 231 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 233 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NClob getNClob(String parameterName) throws SQLException {
/* 240 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 245 */     NClob retValue = ((JDBC4ResultSet)rs).getNClob(fixParameterName(parameterName));
/*     */ 
/*     */     
/* 248 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 250 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNString(int parameterIndex) throws SQLException {
/* 257 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 259 */     String retValue = ((JDBC4ResultSet)rs).getNString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*     */     
/* 262 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 264 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNString(String parameterName) throws SQLException {
/* 271 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 276 */     String retValue = ((JDBC4ResultSet)rs).getNString(fixParameterName(parameterName));
/*     */ 
/*     */     
/* 279 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 281 */     return retValue;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4CallableStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */