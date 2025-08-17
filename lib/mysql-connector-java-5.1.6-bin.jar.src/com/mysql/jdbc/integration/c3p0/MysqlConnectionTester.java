/*     */ package com.mysql.jdbc.integration.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mysql.jdbc.Connection;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MysqlConnectionTester
/*     */   implements QueryConnectionTester
/*     */ {
/*     */   private static final long serialVersionUID = 3256444690067896368L;
/*  47 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*     */   
/*     */   private Method pingMethod;
/*     */   
/*     */   public MysqlConnectionTester() {
/*     */     try {
/*  53 */       this.pingMethod = Connection.class.getMethod("ping", null);
/*     */     }
/*  55 */     catch (Exception ex) {}
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
/*     */   public int activeCheckConnection(Connection con) {
/*     */     try {
/*  69 */       if (this.pingMethod != null) {
/*  70 */         if (con instanceof Connection) {
/*     */ 
/*     */           
/*  73 */           ((Connection)con).ping();
/*     */         } else {
/*     */           
/*  76 */           C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
/*  77 */           castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
/*     */         } 
/*     */       } else {
/*     */         
/*  81 */         Statement pingStatement = null;
/*     */         
/*     */         try {
/*  84 */           pingStatement = con.createStatement();
/*  85 */           pingStatement.executeQuery("SELECT 1").close();
/*     */         } finally {
/*  87 */           if (pingStatement != null) {
/*  88 */             pingStatement.close();
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/*  93 */       return 0;
/*  94 */     } catch (Exception ex) {
/*  95 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int statusOnException(Connection arg0, Throwable throwable) {
/* 106 */     if (throwable instanceof com.mysql.jdbc.CommunicationsException || "com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals(throwable.getClass().getName()))
/*     */     {
/*     */       
/* 109 */       return -1;
/*     */     }
/*     */     
/* 112 */     if (throwable instanceof SQLException) {
/* 113 */       String sqlState = ((SQLException)throwable).getSQLState();
/*     */       
/* 115 */       if (sqlState != null && sqlState.startsWith("08")) {
/* 116 */         return -1;
/*     */       }
/*     */       
/* 119 */       return 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 124 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int activeCheckConnection(Connection arg0, String arg1) {
/* 134 */     return 0;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\integration\c3p0\MysqlConnectionTester.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */