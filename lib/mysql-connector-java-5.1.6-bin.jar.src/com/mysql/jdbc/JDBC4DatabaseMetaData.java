/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.RowIdLifetime;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4DatabaseMetaData
/*     */   extends DatabaseMetaData
/*     */ {
/*     */   public JDBC4DatabaseMetaData(ConnectionImpl connToSet, String databaseToSet) {
/*  38 */     super(connToSet, databaseToSet);
/*     */   }
/*     */   
/*     */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/*  42 */     return RowIdLifetime.ROWID_UNSUPPORTED;
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
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*  63 */     return iface.isInstance(this);
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
/*  84 */       return iface.cast(this);
/*  85 */     } catch (ClassCastException cce) {
/*  86 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getClientInfoProperties() throws SQLException {
/* 118 */     Field[] fields = new Field[4];
/* 119 */     fields[0] = new Field("", "NAME", 12, 255);
/* 120 */     fields[1] = new Field("", "MAX_LEN", 4, 10);
/* 121 */     fields[2] = new Field("", "DEFAULT_VALUE", 12, 255);
/* 122 */     fields[3] = new Field("", "DESCRIPTION", 12, 255);
/*     */     
/* 124 */     ArrayList tuples = new ArrayList();
/*     */     
/* 126 */     return buildResultSet(fields, tuples, this.conn);
/*     */   }
/*     */   
/*     */   public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
/* 130 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
/* 183 */     Field[] fields = new Field[6];
/*     */     
/* 185 */     fields[0] = new Field("", "FUNCTION_CAT", 1, 255);
/* 186 */     fields[1] = new Field("", "FUNCTION_SCHEM", 1, 255);
/* 187 */     fields[2] = new Field("", "FUNCTION_NAME", 1, 255);
/* 188 */     fields[3] = new Field("", "REMARKS", 1, 255);
/* 189 */     fields[4] = new Field("", "FUNCTION_TYPE", 5, 6);
/* 190 */     fields[5] = new Field("", "SPECIFIC_NAME", 1, 255);
/*     */     
/* 192 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, functionNamePattern, false, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getJDBC4FunctionNoTableConstant() {
/* 202 */     return 1;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4DatabaseMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */