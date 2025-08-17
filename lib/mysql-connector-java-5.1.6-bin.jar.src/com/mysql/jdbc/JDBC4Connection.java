/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.Properties;
/*     */ import java.util.TimerTask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4Connection
/*     */   extends ConnectionImpl
/*     */ {
/*     */   private JDBC4ClientInfoProvider infoProvider;
/*     */   
/*     */   public JDBC4Connection(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
/*  46 */     super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*     */   }
/*     */ 
/*     */   
/*     */   public SQLXML createSQLXML() throws SQLException {
/*  51 */     return new JDBC4MysqlSQLXML();
/*     */   }
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*  55 */     throw SQLError.notImplemented();
/*     */   }
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*  59 */     throw SQLError.notImplemented();
/*     */   }
/*     */   
/*     */   public Properties getClientInfo() throws SQLException {
/*  63 */     return getClientInfoProviderImpl().getClientInfo(this);
/*     */   }
/*     */   
/*     */   public String getClientInfo(String name) throws SQLException {
/*  67 */     return getClientInfoProviderImpl().getClientInfo(this, name);
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
/*     */   public synchronized boolean isValid(int timeout) throws SQLException {
/*  92 */     if (isClosed()) {
/*  93 */       return false;
/*     */     }
/*     */     
/*  96 */     TimerTask timeoutTask = null;
/*     */     
/*  98 */     if (timeout != 0) {
/*  99 */       timeoutTask = new TimerTask() {
/*     */           public void run() {
/* 101 */             (new Thread() {
/*     */                 public void run() {
/*     */                   try {
/* 104 */                     JDBC4Connection.this.abortInternal();
/* 105 */                   } catch (Throwable t) {
/* 106 */                     throw new RuntimeException(t);
/*     */                   } 
/*     */                 }
/*     */               }).start();
/*     */           }
/*     */         };
/*     */       
/* 113 */       getCancelTimer().schedule(timeoutTask, (timeout * 1000));
/*     */     } 
/*     */     
/*     */     try {
/* 117 */       synchronized (getMutex()) {
/*     */         try {
/* 119 */           pingInternal(false);
/*     */           
/* 121 */           if (timeoutTask != null) {
/* 122 */             timeoutTask.cancel();
/*     */           }
/*     */           
/* 125 */           timeoutTask = null;
/* 126 */         } catch (Throwable t) {
/*     */           try {
/* 128 */             abortInternal();
/* 129 */           } catch (Throwable ignoreThrown) {}
/*     */ 
/*     */ 
/*     */           
/* 133 */           return false;
/*     */         } finally {
/* 135 */           if (timeoutTask != null) {
/* 136 */             timeoutTask.cancel();
/*     */           }
/*     */         } 
/*     */       } 
/* 140 */     } catch (Throwable t) {
/* 141 */       return false;
/*     */     } 
/*     */     
/* 144 */     return true;
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 149 */       getClientInfoProviderImpl().setClientInfo(this, properties);
/* 150 */     } catch (SQLClientInfoException ciEx) {
/* 151 */       throw ciEx;
/* 152 */     } catch (SQLException sqlEx) {
/* 153 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 154 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 156 */       throw clientInfoEx;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 162 */       getClientInfoProviderImpl().setClientInfo(this, name, value);
/* 163 */     } catch (SQLClientInfoException ciEx) {
/* 164 */       throw ciEx;
/* 165 */     } catch (SQLException sqlEx) {
/* 166 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 167 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 169 */       throw clientInfoEx;
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
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 189 */     checkClosed();
/*     */ 
/*     */ 
/*     */     
/* 193 */     return iface.isInstance(this);
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
/* 214 */       return iface.cast(this);
/* 215 */     } catch (ClassCastException cce) {
/* 216 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Blob createBlob() {
/* 225 */     return new Blob();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Clob createClob() {
/* 232 */     return new Clob();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NClob createNClob() {
/* 239 */     return new JDBC4NClob();
/*     */   }
/*     */   
/*     */   protected synchronized JDBC4ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/* 243 */     if (this.infoProvider == null) {
/*     */       try {
/*     */         try {
/* 246 */           this.infoProvider = (JDBC4ClientInfoProvider)Util.getInstance(getClientInfoProvider(), new Class[0], new Object[0]);
/*     */         }
/* 248 */         catch (SQLException sqlEx) {
/* 249 */           if (sqlEx.getCause() instanceof ClassCastException)
/*     */           {
/* 251 */             this.infoProvider = (JDBC4ClientInfoProvider)Util.getInstance("com.mysql.jdbc." + getClientInfoProvider(), new Class[0], new Object[0]);
/*     */           }
/*     */         }
/*     */       
/*     */       }
/* 256 */       catch (ClassCastException cce) {
/* 257 */         throw SQLError.createSQLException(Messages.getString("JDBC4Connection.ClientInfoNotImplemented", new Object[] { getClientInfoProvider() }), "S1009");
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 262 */       this.infoProvider.initialize(this, this.props);
/*     */     } 
/*     */     
/* 265 */     return this.infoProvider;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4Connection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */