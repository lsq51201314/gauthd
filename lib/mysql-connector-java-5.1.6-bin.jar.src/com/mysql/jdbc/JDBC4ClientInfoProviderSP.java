/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
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
/*     */ public class JDBC4ClientInfoProviderSP
/*     */   implements JDBC4ClientInfoProvider
/*     */ {
/*     */   PreparedStatement setClientInfoSp;
/*     */   PreparedStatement getClientInfoSp;
/*     */   PreparedStatement getClientInfoBulkSp;
/*     */   
/*     */   public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
/*  42 */     String identifierQuote = conn.getMetaData().getIdentifierQuoteString();
/*  43 */     String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
/*     */     
/*  45 */     String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
/*     */     
/*  47 */     String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
/*     */     
/*  49 */     String clientInfoCatalog = configurationProps.getProperty("clientInfoCatalog", "");
/*     */ 
/*     */ 
/*     */     
/*  53 */     String catalog = "".equals(clientInfoCatalog) ? conn.getCatalog() : clientInfoCatalog;
/*     */ 
/*     */     
/*  56 */     this.setClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  61 */     this.getClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL" + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  66 */     this.getClientInfoBulkSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void destroy() throws SQLException {
/*  73 */     if (this.setClientInfoSp != null) {
/*  74 */       this.setClientInfoSp.close();
/*  75 */       this.setClientInfoSp = null;
/*     */     } 
/*     */     
/*  78 */     if (this.getClientInfoSp != null) {
/*  79 */       this.getClientInfoSp.close();
/*  80 */       this.getClientInfoSp = null;
/*     */     } 
/*     */     
/*  83 */     if (this.getClientInfoBulkSp != null) {
/*  84 */       this.getClientInfoBulkSp.close();
/*  85 */       this.getClientInfoBulkSp = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Properties getClientInfo(Connection conn) throws SQLException {
/*  91 */     ResultSet rs = null;
/*     */     
/*  93 */     Properties props = new Properties();
/*     */     
/*     */     try {
/*  96 */       this.getClientInfoBulkSp.execute();
/*     */       
/*  98 */       rs = this.getClientInfoBulkSp.getResultSet();
/*     */       
/* 100 */       while (rs.next()) {
/* 101 */         props.setProperty(rs.getString(1), rs.getString(2));
/*     */       }
/*     */     } finally {
/* 104 */       if (rs != null) {
/* 105 */         rs.close();
/*     */       }
/*     */     } 
/*     */     
/* 109 */     return props;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized String getClientInfo(Connection conn, String name) throws SQLException {
/* 114 */     ResultSet rs = null;
/*     */     
/* 116 */     String clientInfo = null;
/*     */     
/*     */     try {
/* 119 */       this.getClientInfoSp.setString(1, name);
/* 120 */       this.getClientInfoSp.execute();
/*     */       
/* 122 */       rs = this.getClientInfoSp.getResultSet();
/*     */       
/* 124 */       if (rs.next()) {
/* 125 */         clientInfo = rs.getString(1);
/*     */       }
/*     */     } finally {
/* 128 */       if (rs != null) {
/* 129 */         rs.close();
/*     */       }
/*     */     } 
/*     */     
/* 133 */     return clientInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 139 */       Enumeration<?> propNames = properties.propertyNames();
/*     */       
/* 141 */       while (propNames.hasMoreElements()) {
/* 142 */         String name = (String)propNames.nextElement();
/* 143 */         String value = properties.getProperty(name);
/*     */         
/* 145 */         setClientInfo(conn, name, value);
/*     */       } 
/* 147 */     } catch (SQLException sqlEx) {
/* 148 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 149 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 151 */       throw clientInfoEx;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 158 */       this.setClientInfoSp.setString(1, name);
/* 159 */       this.setClientInfoSp.setString(2, value);
/* 160 */       this.setClientInfoSp.execute();
/* 161 */     } catch (SQLException sqlEx) {
/* 162 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 163 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 165 */       throw clientInfoEx;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4ClientInfoProviderSP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */