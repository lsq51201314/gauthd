/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ConnectionImpl;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class SuspendableXAConnection
/*     */   extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private static final Constructor JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
/*     */   
/*     */   static {
/*  23 */     if (Util.isJdbc4()) {
/*     */       try {
/*  25 */         JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection").getConstructor(new Class[] { ConnectionImpl.class });
/*     */ 
/*     */       
/*     */       }
/*  29 */       catch (SecurityException e) {
/*  30 */         throw new RuntimeException(e);
/*  31 */       } catch (NoSuchMethodException e) {
/*  32 */         throw new RuntimeException(e);
/*  33 */       } catch (ClassNotFoundException e) {
/*  34 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/*  37 */       JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected static SuspendableXAConnection getInstance(ConnectionImpl mysqlConnection) throws SQLException {
/*  42 */     if (!Util.isJdbc4()) {
/*  43 */       return new SuspendableXAConnection(mysqlConnection);
/*     */     }
/*     */     
/*  46 */     return (SuspendableXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlConnection });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SuspendableXAConnection(ConnectionImpl connection) {
/*  52 */     super((Connection)connection);
/*  53 */     this.underlyingConnection = connection;
/*     */   }
/*     */   
/*  56 */   private static final Map XIDS_TO_PHYSICAL_CONNECTIONS = new HashMap();
/*     */ 
/*     */   
/*     */   private Xid currentXid;
/*     */ 
/*     */   
/*     */   private XAConnection currentXAConnection;
/*     */ 
/*     */   
/*     */   private XAResource currentXAResource;
/*     */ 
/*     */   
/*     */   private ConnectionImpl underlyingConnection;
/*     */ 
/*     */ 
/*     */   
/*     */   private static synchronized XAConnection findConnectionForXid(ConnectionImpl connectionToWrap, Xid xid) throws SQLException {
/*  73 */     XAConnection conn = (XAConnection)XIDS_TO_PHYSICAL_CONNECTIONS.get(xid);
/*     */     
/*  75 */     if (conn == null) {
/*  76 */       conn = new MysqlXAConnection(connectionToWrap, connectionToWrap.getLogXaCommands());
/*     */     }
/*     */ 
/*     */     
/*  80 */     return conn;
/*     */   }
/*     */   
/*     */   private static synchronized void removeXAConnectionMapping(Xid xid) {
/*  84 */     XIDS_TO_PHYSICAL_CONNECTIONS.remove(xid);
/*     */   }
/*     */   
/*     */   private synchronized void switchToXid(Xid xid) throws XAException {
/*  88 */     if (xid == null) {
/*  89 */       throw new XAException();
/*     */     }
/*     */     
/*     */     try {
/*  93 */       if (!xid.equals(this.currentXid)) {
/*  94 */         XAConnection toSwitchTo = findConnectionForXid(this.underlyingConnection, xid);
/*  95 */         this.currentXAConnection = toSwitchTo;
/*  96 */         this.currentXid = xid;
/*  97 */         this.currentXAResource = toSwitchTo.getXAResource();
/*     */       } 
/*  99 */     } catch (SQLException sqlEx) {
/* 100 */       throw new XAException();
/*     */     } 
/*     */   }
/*     */   
/*     */   public XAResource getXAResource() throws SQLException {
/* 105 */     return this;
/*     */   }
/*     */   
/*     */   public void commit(Xid xid, boolean arg1) throws XAException {
/* 109 */     switchToXid(xid);
/* 110 */     this.currentXAResource.commit(xid, arg1);
/* 111 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */   
/*     */   public void end(Xid xid, int arg1) throws XAException {
/* 115 */     switchToXid(xid);
/* 116 */     this.currentXAResource.end(xid, arg1);
/*     */   }
/*     */   
/*     */   public void forget(Xid xid) throws XAException {
/* 120 */     switchToXid(xid);
/* 121 */     this.currentXAResource.forget(xid);
/*     */     
/* 123 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTransactionTimeout() throws XAException {
/* 128 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean isSameRM(XAResource xaRes) throws XAException {
/* 132 */     return (xaRes == this);
/*     */   }
/*     */   
/*     */   public int prepare(Xid xid) throws XAException {
/* 136 */     switchToXid(xid);
/* 137 */     return this.currentXAResource.prepare(xid);
/*     */   }
/*     */   
/*     */   public Xid[] recover(int flag) throws XAException {
/* 141 */     return MysqlXAConnection.recover((Connection)this.underlyingConnection, flag);
/*     */   }
/*     */   
/*     */   public void rollback(Xid xid) throws XAException {
/* 145 */     switchToXid(xid);
/* 146 */     this.currentXAResource.rollback(xid);
/* 147 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setTransactionTimeout(int arg0) throws XAException {
/* 152 */     return false;
/*     */   }
/*     */   
/*     */   public void start(Xid xid, int arg1) throws XAException {
/* 156 */     switchToXid(xid);
/*     */     
/* 158 */     if (arg1 != 2097152) {
/* 159 */       this.currentXAResource.start(xid, arg1);
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 168 */     this.currentXAResource.start(xid, 134217728);
/*     */   }
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/* 172 */     if (this.currentXAConnection == null) {
/* 173 */       return getConnection(false, true);
/*     */     }
/*     */     
/* 176 */     return this.currentXAConnection.getConnection();
/*     */   }
/*     */   
/*     */   public void close() throws SQLException {
/* 180 */     if (this.currentXAConnection == null) {
/* 181 */       super.close();
/*     */     } else {
/* 183 */       removeXAConnectionMapping(this.currentXid);
/* 184 */       this.currentXAConnection.close();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\SuspendableXAConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */