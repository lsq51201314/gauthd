/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4CommentClientInfoProvider
/*     */   implements JDBC4ClientInfoProvider
/*     */ {
/*     */   private Properties clientInfo;
/*     */   
/*     */   public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
/*  51 */     this.clientInfo = new Properties();
/*     */   }
/*     */   
/*     */   public synchronized void destroy() throws SQLException {
/*  55 */     this.clientInfo = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Properties getClientInfo(Connection conn) throws SQLException {
/*  60 */     return this.clientInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized String getClientInfo(Connection conn, String name) throws SQLException {
/*  65 */     return this.clientInfo.getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
/*  70 */     this.clientInfo = new Properties();
/*     */     
/*  72 */     Enumeration<?> propNames = properties.propertyNames();
/*     */     
/*  74 */     while (propNames.hasMoreElements()) {
/*  75 */       String name = (String)propNames.nextElement();
/*     */       
/*  77 */       this.clientInfo.put(name, properties.getProperty(name));
/*     */     } 
/*     */     
/*  80 */     setComment(conn);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
/*  85 */     this.clientInfo.setProperty(name, value);
/*  86 */     setComment(conn);
/*     */   }
/*     */   
/*     */   private synchronized void setComment(Connection conn) {
/*  90 */     StringBuffer commentBuf = new StringBuffer();
/*  91 */     Iterator<Map.Entry<Object, Object>> elements = this.clientInfo.entrySet().iterator();
/*     */     
/*  93 */     while (elements.hasNext()) {
/*  94 */       if (commentBuf.length() > 0) {
/*  95 */         commentBuf.append(", ");
/*     */       }
/*     */       
/*  98 */       Map.Entry entry = elements.next();
/*  99 */       commentBuf.append("" + entry.getKey());
/* 100 */       commentBuf.append("=");
/* 101 */       commentBuf.append("" + entry.getValue());
/*     */     } 
/*     */     
/* 104 */     ((Connection)conn).setStatementComment(commentBuf.toString());
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4CommentClientInfoProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */