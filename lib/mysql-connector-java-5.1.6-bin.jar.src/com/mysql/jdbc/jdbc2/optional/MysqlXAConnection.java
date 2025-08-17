/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ConnectionImpl;
/*     */ import com.mysql.jdbc.Constants;
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MysqlXAConnection
/*     */   extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private ConnectionImpl underlyingConnection;
/*     */   private static final Map MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
/*     */   private Log log;
/*     */   protected boolean logXaCommands;
/*     */   private static final Constructor JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
/*     */   
/*     */   static {
/*  77 */     HashMap temp = new HashMap();
/*     */     
/*  79 */     temp.put(Constants.integerValueOf(1397), Constants.integerValueOf(-4));
/*  80 */     temp.put(Constants.integerValueOf(1398), Constants.integerValueOf(-5));
/*  81 */     temp.put(Constants.integerValueOf(1399), Constants.integerValueOf(-7));
/*  82 */     temp.put(Constants.integerValueOf(1400), Constants.integerValueOf(-9));
/*  83 */     temp.put(Constants.integerValueOf(1401), Constants.integerValueOf(-3));
/*  84 */     temp.put(Constants.integerValueOf(1402), Constants.integerValueOf(100));
/*     */     
/*  86 */     MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  92 */     if (Util.isJdbc4()) {
/*     */       try {
/*  94 */         JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection").getConstructor(new Class[] { ConnectionImpl.class, boolean.class });
/*     */ 
/*     */       
/*     */       }
/*  98 */       catch (SecurityException e) {
/*  99 */         throw new RuntimeException(e);
/* 100 */       } catch (NoSuchMethodException e) {
/* 101 */         throw new RuntimeException(e);
/* 102 */       } catch (ClassNotFoundException e) {
/* 103 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/* 106 */       JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected static MysqlXAConnection getInstance(ConnectionImpl mysqlConnection, boolean logXaCommands) throws SQLException {
/* 112 */     if (!Util.isJdbc4()) {
/* 113 */       return new MysqlXAConnection(mysqlConnection, logXaCommands);
/*     */     }
/*     */     
/* 116 */     return (MysqlXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlConnection, Boolean.valueOf(logXaCommands) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MysqlXAConnection(ConnectionImpl connection, boolean logXaCommands) throws SQLException {
/* 127 */     super((Connection)connection);
/* 128 */     this.underlyingConnection = connection;
/* 129 */     this.log = connection.getLog();
/* 130 */     this.logXaCommands = logXaCommands;
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
/*     */   public XAResource getXAResource() throws SQLException {
/* 143 */     return this;
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
/*     */   public int getTransactionTimeout() throws XAException {
/* 161 */     return 0;
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
/*     */   public boolean setTransactionTimeout(int arg0) throws XAException {
/* 187 */     return false;
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
/*     */   public boolean isSameRM(XAResource xares) throws XAException {
/* 207 */     if (xares instanceof MysqlXAConnection) {
/* 208 */       return this.underlyingConnection.isSameResource((Connection)((MysqlXAConnection)xares).underlyingConnection);
/*     */     }
/*     */ 
/*     */     
/* 212 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Xid[] recover(int flag) throws XAException {
/* 253 */     return recover((Connection)this.underlyingConnection, flag);
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
/*     */   protected static Xid[] recover(Connection c, int flag) throws XAException {
/* 277 */     boolean startRscan = ((flag & 0x1000000) > 0);
/* 278 */     boolean endRscan = ((flag & 0x800000) > 0);
/*     */     
/* 280 */     if (!startRscan && !endRscan && flag != 0) {
/* 281 */       throw new MysqlXAException(-5, "Invalid flag, must use TMNOFLAGS, or any combination of TMSTARTRSCAN and TMENDRSCAN", null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 294 */     if (!startRscan) {
/* 295 */       return new Xid[0];
/*     */     }
/*     */     
/* 298 */     ResultSet rs = null;
/* 299 */     Statement stmt = null;
/*     */     
/* 301 */     List recoveredXidList = new ArrayList();
/*     */ 
/*     */     
/*     */     try {
/* 305 */       stmt = c.createStatement();
/*     */       
/* 307 */       rs = stmt.executeQuery("XA RECOVER");
/*     */       
/* 309 */       while (rs.next()) {
/* 310 */         int formatId = rs.getInt(1);
/* 311 */         int gtridLength = rs.getInt(2);
/* 312 */         int bqualLength = rs.getInt(3);
/* 313 */         byte[] gtridAndBqual = rs.getBytes(4);
/*     */         
/* 315 */         byte[] gtrid = new byte[gtridLength];
/* 316 */         byte[] bqual = new byte[bqualLength];
/*     */         
/* 318 */         if (gtridAndBqual.length != gtridLength + bqualLength) {
/* 319 */           throw new MysqlXAException(105, "Error while recovering XIDs from RM. GTRID and BQUAL are wrong sizes", null);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 324 */         System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
/*     */         
/* 326 */         System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
/*     */ 
/*     */         
/* 329 */         recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
/*     */       }
/*     */     
/* 332 */     } catch (SQLException sqlEx) {
/* 333 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 335 */       if (rs != null) {
/*     */         try {
/* 337 */           rs.close();
/* 338 */         } catch (SQLException sqlEx) {
/* 339 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         } 
/*     */       }
/*     */       
/* 343 */       if (stmt != null) {
/*     */         try {
/* 345 */           stmt.close();
/* 346 */         } catch (SQLException sqlEx) {
/* 347 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 352 */     int numXids = recoveredXidList.size();
/*     */     
/* 354 */     Xid[] asXids = new Xid[numXids];
/* 355 */     Object[] asObjects = recoveredXidList.toArray();
/*     */     
/* 357 */     for (int i = 0; i < numXids; i++) {
/* 358 */       asXids[i] = (Xid)asObjects[i];
/*     */     }
/*     */     
/* 361 */     return asXids;
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
/*     */   public int prepare(Xid xid) throws XAException {
/* 383 */     StringBuffer commandBuf = new StringBuffer();
/* 384 */     commandBuf.append("XA PREPARE ");
/* 385 */     commandBuf.append(xidToString(xid));
/*     */     
/* 387 */     dispatchCommand(commandBuf.toString());
/*     */     
/* 389 */     return 0;
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
/*     */   public void forget(Xid xid) throws XAException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback(Xid xid) throws XAException {
/* 425 */     StringBuffer commandBuf = new StringBuffer();
/* 426 */     commandBuf.append("XA ROLLBACK ");
/* 427 */     commandBuf.append(xidToString(xid));
/*     */     
/*     */     try {
/* 430 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 432 */       this.underlyingConnection.setInGlobalTx(false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(Xid xid, int flags) throws XAException {
/* 464 */     StringBuffer commandBuf = new StringBuffer();
/* 465 */     commandBuf.append("XA END ");
/* 466 */     commandBuf.append(xidToString(xid));
/*     */     
/* 468 */     switch (flags) {
/*     */       case 67108864:
/*     */         break;
/*     */       case 33554432:
/* 472 */         commandBuf.append(" SUSPEND");
/*     */         break;
/*     */       case 536870912:
/*     */         break;
/*     */       default:
/* 477 */         throw new XAException(-5);
/*     */     } 
/*     */     
/* 480 */     dispatchCommand(commandBuf.toString());
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
/*     */   public void start(Xid xid, int flags) throws XAException {
/* 507 */     StringBuffer commandBuf = new StringBuffer();
/* 508 */     commandBuf.append("XA START ");
/* 509 */     commandBuf.append(xidToString(xid));
/*     */     
/* 511 */     switch (flags) {
/*     */       case 2097152:
/* 513 */         commandBuf.append(" JOIN");
/*     */         break;
/*     */       case 134217728:
/* 516 */         commandBuf.append(" RESUME");
/*     */         break;
/*     */       
/*     */       case 0:
/*     */         break;
/*     */       default:
/* 522 */         throw new XAException(-5);
/*     */     } 
/*     */     
/* 525 */     dispatchCommand(commandBuf.toString());
/*     */     
/* 527 */     this.underlyingConnection.setInGlobalTx(true);
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
/*     */   public void commit(Xid xid, boolean onePhase) throws XAException {
/* 552 */     StringBuffer commandBuf = new StringBuffer();
/* 553 */     commandBuf.append("XA COMMIT ");
/* 554 */     commandBuf.append(xidToString(xid));
/*     */     
/* 556 */     if (onePhase) {
/* 557 */       commandBuf.append(" ONE PHASE");
/*     */     }
/*     */     
/*     */     try {
/* 561 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 563 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     } 
/*     */   }
/*     */   
/*     */   private ResultSet dispatchCommand(String command) throws XAException {
/* 568 */     Statement stmt = null;
/*     */     
/*     */     try {
/* 571 */       if (this.logXaCommands) {
/* 572 */         this.log.logDebug("Executing XA statement: " + command);
/*     */       }
/*     */ 
/*     */       
/* 576 */       stmt = this.underlyingConnection.createStatement();
/*     */ 
/*     */       
/* 579 */       stmt.execute(command);
/*     */       
/* 581 */       ResultSet rs = stmt.getResultSet();
/*     */       
/* 583 */       return rs;
/* 584 */     } catch (SQLException sqlEx) {
/* 585 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 587 */       if (stmt != null) {
/*     */         try {
/* 589 */           stmt.close();
/* 590 */         } catch (SQLException sqlEx) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
/* 598 */     Integer xaCode = (Integer)MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Constants.integerValueOf(sqlEx.getErrorCode()));
/*     */ 
/*     */     
/* 601 */     if (xaCode != null) {
/* 602 */       return new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), null);
/*     */     }
/*     */ 
/*     */     
/* 606 */     return new MysqlXAException(sqlEx.getMessage(), null);
/*     */   }
/*     */   
/*     */   private static String xidToString(Xid xid) {
/* 610 */     byte[] gtrid = xid.getGlobalTransactionId();
/*     */     
/* 612 */     byte[] btrid = xid.getBranchQualifier();
/*     */     
/* 614 */     int lengthAsString = 6;
/*     */     
/* 616 */     if (gtrid != null) {
/* 617 */       lengthAsString += 2 * gtrid.length;
/*     */     }
/*     */     
/* 620 */     if (btrid != null) {
/* 621 */       lengthAsString += 2 * btrid.length;
/*     */     }
/*     */     
/* 624 */     String formatIdInHex = Integer.toHexString(xid.getFormatId());
/*     */     
/* 626 */     lengthAsString += formatIdInHex.length();
/* 627 */     lengthAsString += 3;
/*     */     
/* 629 */     StringBuffer asString = new StringBuffer(lengthAsString);
/*     */     
/* 631 */     asString.append("0x");
/*     */     
/* 633 */     if (gtrid != null) {
/* 634 */       for (int i = 0; i < gtrid.length; i++) {
/* 635 */         String asHex = Integer.toHexString(gtrid[i] & 0xFF);
/*     */         
/* 637 */         if (asHex.length() == 1) {
/* 638 */           asString.append("0");
/*     */         }
/*     */         
/* 641 */         asString.append(asHex);
/*     */       } 
/*     */     }
/*     */     
/* 645 */     asString.append(",");
/*     */     
/* 647 */     if (btrid != null) {
/* 648 */       asString.append("0x");
/*     */       
/* 650 */       for (int i = 0; i < btrid.length; i++) {
/* 651 */         String asHex = Integer.toHexString(btrid[i] & 0xFF);
/*     */         
/* 653 */         if (asHex.length() == 1) {
/* 654 */           asString.append("0");
/*     */         }
/*     */         
/* 657 */         asString.append(asHex);
/*     */       } 
/*     */     } 
/*     */     
/* 661 */     asString.append(",0x");
/* 662 */     asString.append(formatIdInHex);
/*     */     
/* 664 */     return asString.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/* 673 */     Connection connToWrap = getConnection(false, true);
/*     */     
/* 675 */     return connToWrap;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\MysqlXAConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */