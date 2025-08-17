/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MysqlPooledConnection
/*     */   implements PooledConnection
/*     */ {
/*     */   private static final Constructor JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR;
/*     */   public static final int CONNECTION_ERROR_EVENT = 1;
/*     */   public static final int CONNECTION_CLOSED_EVENT = 2;
/*     */   private Map connectionEventListeners;
/*     */   private Connection logicalHandle;
/*     */   private Connection physicalConn;
/*     */   
/*     */   static {
/*  55 */     if (Util.isJdbc4()) {
/*     */       try {
/*  57 */         JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection").getConstructor(new Class[] { Connection.class });
/*     */ 
/*     */       
/*     */       }
/*  61 */       catch (SecurityException e) {
/*  62 */         throw new RuntimeException(e);
/*  63 */       } catch (NoSuchMethodException e) {
/*  64 */         throw new RuntimeException(e);
/*  65 */       } catch (ClassNotFoundException e) {
/*  66 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/*  69 */       JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected static MysqlPooledConnection getInstance(Connection connection) throws SQLException {
/*  74 */     if (!Util.isJdbc4()) {
/*  75 */       return new MysqlPooledConnection(connection);
/*     */     }
/*     */     
/*  78 */     return (MysqlPooledConnection)Util.handleNewInstance(JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR, new Object[] { connection });
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
/*     */   public MysqlPooledConnection(Connection connection) {
/* 110 */     this.logicalHandle = null;
/* 111 */     this.physicalConn = connection;
/* 112 */     this.connectionEventListeners = new HashMap();
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
/*     */   public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener) {
/* 125 */     if (this.connectionEventListeners != null) {
/* 126 */       this.connectionEventListeners.put(connectioneventlistener, connectioneventlistener);
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
/*     */   public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener) {
/* 141 */     if (this.connectionEventListeners != null) {
/* 142 */       this.connectionEventListeners.remove(connectioneventlistener);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/* 153 */     return getConnection(true, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized Connection getConnection(boolean resetServerState, boolean forXa) throws SQLException {
/* 160 */     if (this.physicalConn == null) {
/*     */       
/* 162 */       SQLException sqlException = SQLError.createSQLException("Physical Connection doesn't exist");
/*     */       
/* 164 */       callConnectionEventListeners(1, sqlException);
/*     */       
/* 166 */       throw sqlException;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 171 */       if (this.logicalHandle != null) {
/* 172 */         ((ConnectionWrapper)this.logicalHandle).close(false);
/*     */       }
/*     */       
/* 175 */       if (resetServerState) {
/* 176 */         this.physicalConn.resetServerState();
/*     */       }
/*     */       
/* 179 */       this.logicalHandle = (Connection)ConnectionWrapper.getInstance(this, this.physicalConn, forXa);
/*     */     
/*     */     }
/* 182 */     catch (SQLException sqlException) {
/* 183 */       callConnectionEventListeners(1, sqlException);
/*     */       
/* 185 */       throw sqlException;
/*     */     } 
/*     */     
/* 188 */     return this.logicalHandle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void close() throws SQLException {
/* 199 */     if (this.physicalConn != null) {
/* 200 */       this.physicalConn.close();
/*     */       
/* 202 */       this.physicalConn = null;
/*     */     } 
/*     */     
/* 205 */     if (this.connectionEventListeners != null) {
/* 206 */       this.connectionEventListeners.clear();
/*     */       
/* 208 */       this.connectionEventListeners = null;
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
/*     */   protected synchronized void callConnectionEventListeners(int eventType, SQLException sqlException) {
/* 227 */     if (this.connectionEventListeners == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 232 */     Iterator iterator = this.connectionEventListeners.entrySet().iterator();
/*     */     
/* 234 */     ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);
/*     */ 
/*     */     
/* 237 */     while (iterator.hasNext()) {
/*     */       
/* 239 */       ConnectionEventListener connectioneventlistener = (ConnectionEventListener)((Map.Entry)iterator.next()).getValue();
/*     */ 
/*     */       
/* 242 */       if (eventType == 2) {
/* 243 */         connectioneventlistener.connectionClosed(connectionevent); continue;
/* 244 */       }  if (eventType == 1)
/* 245 */         connectioneventlistener.connectionErrorOccurred(connectionevent); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\MysqlPooledConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */