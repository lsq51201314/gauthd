/*    */ package com.mysql.jdbc.interceptors;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ResultSetInternalMethods;
/*    */ import com.mysql.jdbc.Statement;
/*    */ import com.mysql.jdbc.StatementInterceptor;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class SessionAssociationInterceptor
/*    */   implements StatementInterceptor
/*    */ {
/*    */   protected String currentSessionKey;
/* 15 */   protected static ThreadLocal sessionLocal = new ThreadLocal();
/*    */   
/*    */   public static final void setSessionKey(String key) {
/* 18 */     sessionLocal.set(key);
/*    */   }
/*    */   
/*    */   public static final void resetSessionKey() {
/* 22 */     sessionLocal.set(null);
/*    */   }
/*    */   
/*    */   public static final String getSessionKey() {
/* 26 */     return sessionLocal.get();
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 30 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection) throws SQLException {
/* 42 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/* 48 */     String key = getSessionKey();
/*    */     
/* 50 */     if (key != null && !key.equals(this.currentSessionKey)) {
/* 51 */       PreparedStatement pstmt = connection.clientPrepareStatement("SET @mysql_proxy_session=?");
/*    */       
/*    */       try {
/* 54 */         pstmt.setString(1, key);
/* 55 */         pstmt.execute();
/*    */       } finally {
/* 57 */         pstmt.close();
/*    */       } 
/*    */       
/* 60 */       this.currentSessionKey = key;
/*    */     } 
/*    */     
/* 63 */     return null;
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\interceptors\SessionAssociationInterceptor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */