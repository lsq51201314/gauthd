/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4ConnectionWrapper
/*     */   extends ConnectionWrapper
/*     */ {
/*     */   public JDBC4ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa) throws SQLException {
/*  69 */     super(mysqlPooledConnection, mysqlConnection, forXa);
/*     */   }
/*     */   
/*     */   public void close() throws SQLException {
/*     */     try {
/*  74 */       super.close();
/*     */     } finally {
/*  76 */       this.unwrappedInterfaces = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public SQLXML createSQLXML() throws SQLException {
/*  81 */     checkClosed();
/*     */     
/*     */     try {
/*  84 */       return this.mc.createSQLXML();
/*  85 */     } catch (SQLException sqlException) {
/*  86 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/*  89 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*  94 */     checkClosed();
/*     */     
/*     */     try {
/*  97 */       return this.mc.createArrayOf(typeName, elements);
/*     */     }
/*  99 */     catch (SQLException sqlException) {
/* 100 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 103 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/* 108 */     checkClosed();
/*     */     
/*     */     try {
/* 111 */       return this.mc.createStruct(typeName, attributes);
/*     */     }
/* 113 */     catch (SQLException sqlException) {
/* 114 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 117 */       return null;
/*     */     } 
/*     */   }
/*     */   public Properties getClientInfo() throws SQLException {
/* 121 */     checkClosed();
/*     */     
/*     */     try {
/* 124 */       return this.mc.getClientInfo();
/* 125 */     } catch (SQLException sqlException) {
/* 126 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 129 */       return null;
/*     */     } 
/*     */   }
/*     */   public String getClientInfo(String name) throws SQLException {
/* 133 */     checkClosed();
/*     */     
/*     */     try {
/* 136 */       return this.mc.getClientInfo(name);
/* 137 */     } catch (SQLException sqlException) {
/* 138 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 141 */       return null;
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
/*     */   public synchronized boolean isValid(int timeout) throws SQLException {
/*     */     try {
/* 168 */       return this.mc.isValid(timeout);
/* 169 */     } catch (SQLException sqlException) {
/* 170 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 173 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 179 */       checkClosed();
/*     */       
/* 181 */       this.mc.setClientInfo(properties);
/* 182 */     } catch (SQLException sqlException) {
/*     */       try {
/* 184 */         checkAndFireConnectionError(sqlException);
/* 185 */       } catch (SQLException sqlEx2) {
/* 186 */         SQLClientInfoException clientEx = new SQLClientInfoException();
/* 187 */         clientEx.initCause(sqlEx2);
/*     */         
/* 189 */         throw clientEx;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 197 */       checkClosed();
/*     */       
/* 199 */       this.mc.setClientInfo(name, value);
/* 200 */     } catch (SQLException sqlException) {
/*     */       try {
/* 202 */         checkAndFireConnectionError(sqlException);
/* 203 */       } catch (SQLException sqlEx2) {
/* 204 */         SQLClientInfoException clientEx = new SQLClientInfoException();
/* 205 */         clientEx.initCause(sqlEx2);
/*     */         
/* 207 */         throw clientEx;
/*     */       } 
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
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 235 */     checkClosed();
/*     */     
/* 237 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 239 */     if (isInstance) {
/* 240 */       return true;
/*     */     }
/*     */     
/* 243 */     return (iface.getName().equals("com.mysql.jdbc.Connection") || iface.getName().equals("com.mysql.jdbc.ConnectionProperties"));
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
/*     */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 269 */       if ("java.sql.Connection".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName()))
/*     */       {
/* 271 */         return iface.cast(this);
/*     */       }
/*     */       
/* 274 */       if (this.unwrappedInterfaces == null) {
/* 275 */         this.unwrappedInterfaces = new HashMap<Object, Object>();
/*     */       }
/*     */       
/* 278 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 280 */       if (cachedUnwrapped == null) {
/* 281 */         cachedUnwrapped = Proxy.newProxyInstance(this.mc.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.mc));
/*     */ 
/*     */         
/* 284 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       } 
/*     */       
/* 287 */       return iface.cast(cachedUnwrapped);
/* 288 */     } catch (ClassCastException cce) {
/* 289 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Blob createBlob() throws SQLException {
/* 298 */     checkClosed();
/*     */     
/*     */     try {
/* 301 */       return this.mc.createBlob();
/* 302 */     } catch (SQLException sqlException) {
/* 303 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 306 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Clob createClob() throws SQLException {
/* 313 */     checkClosed();
/*     */     
/*     */     try {
/* 316 */       return this.mc.createClob();
/* 317 */     } catch (SQLException sqlException) {
/* 318 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 321 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NClob createNClob() throws SQLException {
/* 328 */     checkClosed();
/*     */     
/*     */     try {
/* 331 */       return this.mc.createNClob();
/* 332 */     } catch (SQLException sqlException) {
/* 333 */       checkAndFireConnectionError(sqlException);
/*     */ 
/*     */       
/* 336 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4ConnectionWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */