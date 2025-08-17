/*     */ package com.mysql.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.Statement;
/*     */ import com.mysql.jdbc.StatementInterceptor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResultSetScannerInterceptor
/*     */   implements StatementInterceptor
/*     */ {
/*     */   private Pattern regexP;
/*     */   
/*     */   public void init(Connection conn, Properties props) throws SQLException {
/*  44 */     String regexFromUser = props.getProperty("resultSetScannerRegex");
/*     */     
/*  46 */     if (regexFromUser == null || regexFromUser.length() == 0) {
/*  47 */       throw new SQLException("resultSetScannerRegex must be configured, and must be > 0 characters");
/*     */     }
/*     */     
/*     */     try {
/*  51 */       this.regexP = Pattern.compile(regexFromUser);
/*  52 */     } catch (Throwable t) {
/*  53 */       SQLException sqlEx = new SQLException("Can't use configured regex due to underlying exception.");
/*  54 */       sqlEx.initCause(t);
/*     */       
/*  56 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection) throws SQLException {
/*  66 */     ResultSetInternalMethods finalResultSet = originalResultSet;
/*     */     
/*  68 */     return (ResultSetInternalMethods)Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class[] { ResultSetInternalMethods.class }, new InvocationHandler(this, finalResultSet)
/*     */         {
/*     */           private final ResultSetInternalMethods val$finalResultSet;
/*     */           
/*     */           private final ResultSetScannerInterceptor this$0;
/*     */           
/*     */           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  75 */             Object invocationResult = method.invoke(this.val$finalResultSet, args);
/*     */             
/*  77 */             String methodName = method.getName();
/*     */             
/*  79 */             if ((invocationResult != null && invocationResult instanceof String) || "getString".equals(methodName) || "getObject".equals(methodName) || "getObjectStoredProc".equals(methodName)) {
/*     */ 
/*     */ 
/*     */               
/*  83 */               Matcher matcher = this.this$0.regexP.matcher(invocationResult.toString());
/*     */               
/*  85 */               if (matcher.matches()) {
/*  86 */                 throw new SQLException("value disallowed by filter");
/*     */               }
/*     */             } 
/*     */             
/*  90 */             return invocationResult;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean executeTopLevelOnly() {
/* 105 */     return false;
/*     */   }
/*     */   
/*     */   public void destroy() {}
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\interceptors\ResultSetScannerInterceptor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */