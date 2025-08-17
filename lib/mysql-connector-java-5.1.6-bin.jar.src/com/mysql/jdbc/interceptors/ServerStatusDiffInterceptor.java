/*     */ package com.mysql.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.Statement;
/*     */ import com.mysql.jdbc.StatementInterceptor;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerStatusDiffInterceptor
/*     */   implements StatementInterceptor
/*     */ {
/*  39 */   private Map preExecuteValues = new HashMap();
/*     */   
/*  41 */   private Map postExecuteValues = new HashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(Connection conn, Properties props) throws SQLException {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection) throws SQLException {
/*  52 */     if (connection.versionMeetsMinimum(5, 0, 2)) {
/*  53 */       populateMapWithSessionStatusValues(connection, this.postExecuteValues);
/*     */       
/*  55 */       connection.getLog().logInfo("Server status change for statement:\n" + Util.calculateDifferences(this.preExecuteValues, this.postExecuteValues));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  61 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void populateMapWithSessionStatusValues(Connection connection, Map toPopulate) throws SQLException {
/*  67 */     Statement stmt = null;
/*  68 */     ResultSet rs = null;
/*     */     
/*     */     try {
/*  71 */       toPopulate.clear();
/*     */       
/*  73 */       stmt = connection.createStatement();
/*  74 */       rs = stmt.executeQuery("SHOW SESSION STATUS");
/*  75 */       Util.resultSetToMap(toPopulate, rs);
/*     */     } finally {
/*  77 */       if (rs != null) {
/*  78 */         rs.close();
/*     */       }
/*     */       
/*  81 */       if (stmt != null) {
/*  82 */         stmt.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/*  91 */     if (connection.versionMeetsMinimum(5, 0, 2)) {
/*  92 */       populateMapWithSessionStatusValues(connection, this.preExecuteValues);
/*     */     }
/*     */ 
/*     */     
/*  96 */     return null;
/*     */   }
/*     */   
/*     */   public boolean executeTopLevelOnly() {
/* 100 */     return true;
/*     */   }
/*     */   
/*     */   public void destroy() {}
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\interceptors\ServerStatusDiffInterceptor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */