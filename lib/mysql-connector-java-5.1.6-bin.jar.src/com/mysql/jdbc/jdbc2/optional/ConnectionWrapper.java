/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.Connection;
/*      */ import com.mysql.jdbc.Extension;
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.Util;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ConnectionWrapper
/*      */   extends WrapperBase
/*      */   implements Connection
/*      */ {
/*   63 */   protected Connection mc = null;
/*      */   
/*   65 */   private MysqlPooledConnection mpc = null;
/*      */   
/*   67 */   private String invalidHandleStr = "Logical handle no longer valid";
/*      */   
/*      */   private boolean closed;
/*      */   
/*      */   private boolean isForXa;
/*      */   
/*      */   private static final Constructor JDBC_4_CONNECTION_WRAPPER_CTOR;
/*      */   
/*      */   static {
/*   76 */     if (Util.isJdbc4()) {
/*      */       try {
/*   78 */         JDBC_4_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4ConnectionWrapper").getConstructor(new Class[] { MysqlPooledConnection.class, Connection.class, boolean.class });
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*   83 */       catch (SecurityException e) {
/*   84 */         throw new RuntimeException(e);
/*   85 */       } catch (NoSuchMethodException e) {
/*   86 */         throw new RuntimeException(e);
/*   87 */       } catch (ClassNotFoundException e) {
/*   88 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*   91 */       JDBC_4_CONNECTION_WRAPPER_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static ConnectionWrapper getInstance(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa) throws SQLException {
/*   98 */     if (!Util.isJdbc4()) {
/*   99 */       return new ConnectionWrapper(mysqlPooledConnection, mysqlConnection, forXa);
/*      */     }
/*      */ 
/*      */     
/*  103 */     return (ConnectionWrapper)Util.handleNewInstance(JDBC_4_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlPooledConnection, mysqlConnection, Boolean.valueOf(forXa) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa) throws SQLException {
/*  122 */     this.mpc = mysqlPooledConnection;
/*  123 */     this.mc = mysqlConnection;
/*  124 */     this.closed = false;
/*  125 */     this.pooledConnection = this.mpc;
/*  126 */     this.isForXa = forXa;
/*      */     
/*  128 */     if (this.isForXa) {
/*  129 */       setInGlobalTx(false);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoCommit(boolean autoCommit) throws SQLException {
/*  140 */     checkClosed();
/*      */     
/*  142 */     if (autoCommit && isInGlobalTx()) {
/*  143 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  150 */       this.mc.setAutoCommit(autoCommit);
/*  151 */     } catch (SQLException sqlException) {
/*  152 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoCommit() throws SQLException {
/*  163 */     checkClosed();
/*      */     
/*      */     try {
/*  166 */       return this.mc.getAutoCommit();
/*  167 */     } catch (SQLException sqlException) {
/*  168 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  171 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCatalog(String catalog) throws SQLException {
/*  181 */     checkClosed();
/*      */     
/*      */     try {
/*  184 */       this.mc.setCatalog(catalog);
/*  185 */     } catch (SQLException sqlException) {
/*  186 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCatalog() throws SQLException {
/*  200 */     checkClosed();
/*      */     
/*      */     try {
/*  203 */       return this.mc.getCatalog();
/*  204 */     } catch (SQLException sqlException) {
/*  205 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  208 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() throws SQLException {
/*  218 */     return (this.closed || this.mc.isClosed());
/*      */   }
/*      */   
/*      */   public boolean isMasterConnection() {
/*  222 */     return this.mc.isMasterConnection();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHoldability(int arg0) throws SQLException {
/*  229 */     checkClosed();
/*      */     
/*      */     try {
/*  232 */       this.mc.setHoldability(arg0);
/*  233 */     } catch (SQLException sqlException) {
/*  234 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHoldability() throws SQLException {
/*  242 */     checkClosed();
/*      */     
/*      */     try {
/*  245 */       return this.mc.getHoldability();
/*  246 */     } catch (SQLException sqlException) {
/*  247 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  250 */       return 1;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getIdleFor() {
/*  260 */     return this.mc.getIdleFor();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DatabaseMetaData getMetaData() throws SQLException {
/*  273 */     checkClosed();
/*      */     
/*      */     try {
/*  276 */       return this.mc.getMetaData();
/*  277 */     } catch (SQLException sqlException) {
/*  278 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  281 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setReadOnly(boolean readOnly) throws SQLException {
/*  291 */     checkClosed();
/*      */     
/*      */     try {
/*  294 */       this.mc.setReadOnly(readOnly);
/*  295 */     } catch (SQLException sqlException) {
/*  296 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() throws SQLException {
/*  307 */     checkClosed();
/*      */     
/*      */     try {
/*  310 */       return this.mc.isReadOnly();
/*  311 */     } catch (SQLException sqlException) {
/*  312 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  315 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Savepoint setSavepoint() throws SQLException {
/*  322 */     checkClosed();
/*      */     
/*  324 */     if (isInGlobalTx()) {
/*  325 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  332 */       return this.mc.setSavepoint();
/*  333 */     } catch (SQLException sqlException) {
/*  334 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  337 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Savepoint setSavepoint(String arg0) throws SQLException {
/*  344 */     checkClosed();
/*      */     
/*  346 */     if (isInGlobalTx()) {
/*  347 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  354 */       return this.mc.setSavepoint(arg0);
/*  355 */     } catch (SQLException sqlException) {
/*  356 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  359 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTransactionIsolation(int level) throws SQLException {
/*  369 */     checkClosed();
/*      */     
/*      */     try {
/*  372 */       this.mc.setTransactionIsolation(level);
/*  373 */     } catch (SQLException sqlException) {
/*  374 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTransactionIsolation() throws SQLException {
/*  385 */     checkClosed();
/*      */     
/*      */     try {
/*  388 */       return this.mc.getTransactionIsolation();
/*  389 */     } catch (SQLException sqlException) {
/*  390 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  393 */       return 4;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTypeMap(Map map) throws SQLException {
/*  404 */     checkClosed();
/*      */     
/*      */     try {
/*  407 */       this.mc.setTypeMap(map);
/*  408 */     } catch (SQLException sqlException) {
/*  409 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map getTypeMap() throws SQLException {
/*  420 */     checkClosed();
/*      */     
/*      */     try {
/*  423 */       return this.mc.getTypeMap();
/*  424 */     } catch (SQLException sqlException) {
/*  425 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  428 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLWarning getWarnings() throws SQLException {
/*  438 */     checkClosed();
/*      */     
/*      */     try {
/*  441 */       return this.mc.getWarnings();
/*  442 */     } catch (SQLException sqlException) {
/*  443 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  446 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*  457 */     checkClosed();
/*      */     
/*      */     try {
/*  460 */       this.mc.clearWarnings();
/*  461 */     } catch (SQLException sqlException) {
/*  462 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws SQLException {
/*  477 */     close(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void commit() throws SQLException {
/*  488 */     checkClosed();
/*      */     
/*  490 */     if (isInGlobalTx()) {
/*  491 */       throw SQLError.createSQLException("Can't call commit() on an XAConnection associated with a global transaction", "2D000", 1401);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  499 */       this.mc.commit();
/*  500 */     } catch (SQLException sqlException) {
/*  501 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Statement createStatement() throws SQLException {
/*  512 */     checkClosed();
/*      */     
/*      */     try {
/*  515 */       return StatementWrapper.getInstance(this, this.mpc, this.mc.createStatement());
/*      */     }
/*  517 */     catch (SQLException sqlException) {
/*  518 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  521 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/*  532 */     checkClosed();
/*      */     
/*      */     try {
/*  535 */       return StatementWrapper.getInstance(this, this.mpc, this.mc.createStatement(resultSetType, resultSetConcurrency));
/*      */     }
/*  537 */     catch (SQLException sqlException) {
/*  538 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  541 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
/*  549 */     checkClosed();
/*      */     
/*      */     try {
/*  552 */       return StatementWrapper.getInstance(this, this.mpc, this.mc.createStatement(arg0, arg1, arg2));
/*      */     }
/*  554 */     catch (SQLException sqlException) {
/*  555 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  558 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nativeSQL(String sql) throws SQLException {
/*  568 */     checkClosed();
/*      */     
/*      */     try {
/*  571 */       return this.mc.nativeSQL(sql);
/*  572 */     } catch (SQLException sqlException) {
/*  573 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  576 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement prepareCall(String sql) throws SQLException {
/*  587 */     checkClosed();
/*      */     
/*      */     try {
/*  590 */       return CallableStatementWrapper.getInstance(this, this.mpc, this.mc.prepareCall(sql));
/*      */     }
/*  592 */     catch (SQLException sqlException) {
/*  593 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  596 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  607 */     checkClosed();
/*      */     
/*      */     try {
/*  610 */       return CallableStatementWrapper.getInstance(this, this.mpc, this.mc.prepareCall(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*  612 */     catch (SQLException sqlException) {
/*  613 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  616 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
/*  624 */     checkClosed();
/*      */     
/*      */     try {
/*  627 */       return CallableStatementWrapper.getInstance(this, this.mpc, this.mc.prepareCall(arg0, arg1, arg2, arg3));
/*      */     }
/*  629 */     catch (SQLException sqlException) {
/*  630 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  633 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepare(String sql) throws SQLException {
/*  638 */     checkClosed();
/*      */     
/*      */     try {
/*  641 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.clientPrepareStatement(sql));
/*      */     }
/*  643 */     catch (SQLException sqlException) {
/*  644 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  647 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  652 */     checkClosed();
/*      */     
/*      */     try {
/*  655 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     
/*      */     }
/*  658 */     catch (SQLException sqlException) {
/*  659 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  662 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*  673 */     checkClosed();
/*      */     
/*      */     try {
/*  676 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.prepareStatement(sql));
/*      */     }
/*  678 */     catch (SQLException sqlException) {
/*  679 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  682 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  693 */     checkClosed();
/*      */     
/*      */     try {
/*  696 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.prepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     
/*      */     }
/*  699 */     catch (SQLException sqlException) {
/*  700 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  703 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
/*  711 */     checkClosed();
/*      */     
/*      */     try {
/*  714 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.prepareStatement(arg0, arg1, arg2, arg3));
/*      */     }
/*  716 */     catch (SQLException sqlException) {
/*  717 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  720 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
/*  728 */     checkClosed();
/*      */     
/*      */     try {
/*  731 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*  733 */     catch (SQLException sqlException) {
/*  734 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  737 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
/*  745 */     checkClosed();
/*      */     
/*      */     try {
/*  748 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*  750 */     catch (SQLException sqlException) {
/*  751 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  754 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
/*  762 */     checkClosed();
/*      */     
/*      */     try {
/*  765 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*  767 */     catch (SQLException sqlException) {
/*  768 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  771 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void releaseSavepoint(Savepoint arg0) throws SQLException {
/*  778 */     checkClosed();
/*      */     
/*      */     try {
/*  781 */       this.mc.releaseSavepoint(arg0);
/*  782 */     } catch (SQLException sqlException) {
/*  783 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollback() throws SQLException {
/*  794 */     checkClosed();
/*      */     
/*  796 */     if (isInGlobalTx()) {
/*  797 */       throw SQLError.createSQLException("Can't call rollback() on an XAConnection associated with a global transaction", "2D000", 1401);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  805 */       this.mc.rollback();
/*  806 */     } catch (SQLException sqlException) {
/*  807 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollback(Savepoint arg0) throws SQLException {
/*  815 */     checkClosed();
/*      */     
/*  817 */     if (isInGlobalTx()) {
/*  818 */       throw SQLError.createSQLException("Can't call rollback() on an XAConnection associated with a global transaction", "2D000", 1401);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  826 */       this.mc.rollback(arg0);
/*  827 */     } catch (SQLException sqlException) {
/*  828 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isSameResource(Connection c) {
/*  833 */     if (c instanceof ConnectionWrapper)
/*  834 */       return this.mc.isSameResource(((ConnectionWrapper)c).mc); 
/*  835 */     if (c instanceof Connection) {
/*  836 */       return this.mc.isSameResource(c);
/*      */     }
/*      */     
/*  839 */     return false;
/*      */   }
/*      */   
/*      */   protected void close(boolean fireClosedEvent) throws SQLException {
/*  843 */     synchronized (this.mpc) {
/*  844 */       if (this.closed) {
/*      */         return;
/*      */       }
/*      */       
/*  848 */       if (!isInGlobalTx() && this.mc.getRollbackOnPooledClose() && !getAutoCommit())
/*      */       {
/*  850 */         rollback();
/*      */       }
/*      */       
/*  853 */       if (fireClosedEvent) {
/*  854 */         this.mpc.callConnectionEventListeners(2, null);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  863 */       this.closed = true;
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void checkClosed() throws SQLException {
/*  868 */     if (this.closed) {
/*  869 */       throw SQLError.createSQLException(this.invalidHandleStr);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isInGlobalTx() {
/*  874 */     return this.mc.isInGlobalTx();
/*      */   }
/*      */   
/*      */   public void setInGlobalTx(boolean flag) {
/*  878 */     this.mc.setInGlobalTx(flag);
/*      */   }
/*      */   
/*      */   public void ping() throws SQLException {
/*  882 */     if (this.mc != null) {
/*  883 */       this.mc.ping();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void changeUser(String userName, String newPassword) throws SQLException {
/*  889 */     checkClosed();
/*      */     
/*      */     try {
/*  892 */       this.mc.changeUser(userName, newPassword);
/*  893 */     } catch (SQLException sqlException) {
/*  894 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void clearHasTriedMaster() {
/*  899 */     this.mc.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/*  904 */     checkClosed();
/*      */     
/*      */     try {
/*  907 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.clientPrepareStatement(sql));
/*      */     }
/*  909 */     catch (SQLException sqlException) {
/*  910 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  913 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     try {
/*  919 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.clientPrepareStatement(sql, autoGenKeyIndex));
/*      */     }
/*  921 */     catch (SQLException sqlException) {
/*  922 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  925 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     try {
/*  931 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     
/*      */     }
/*  934 */     catch (SQLException sqlException) {
/*  935 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  938 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     try {
/*  945 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
/*      */     
/*      */     }
/*  948 */     catch (SQLException sqlException) {
/*  949 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  952 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     try {
/*  958 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.clientPrepareStatement(sql, autoGenKeyIndexes));
/*      */     }
/*  960 */     catch (SQLException sqlException) {
/*  961 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  964 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     try {
/*  970 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.clientPrepareStatement(sql, autoGenKeyColNames));
/*      */     }
/*  972 */     catch (SQLException sqlException) {
/*  973 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/*  976 */       return null;
/*      */     } 
/*      */   }
/*      */   public int getActiveStatementCount() {
/*  980 */     return this.mc.getActiveStatementCount();
/*      */   }
/*      */   
/*      */   public Log getLog() throws SQLException {
/*  984 */     return this.mc.getLog();
/*      */   }
/*      */   
/*      */   public String getServerCharacterEncoding() {
/*  988 */     return this.mc.getServerCharacterEncoding();
/*      */   }
/*      */   
/*      */   public TimeZone getServerTimezoneTZ() {
/*  992 */     return this.mc.getServerTimezoneTZ();
/*      */   }
/*      */   
/*      */   public String getStatementComment() {
/*  996 */     return this.mc.getStatementComment();
/*      */   }
/*      */   
/*      */   public boolean hasTriedMaster() {
/* 1000 */     return this.mc.hasTriedMaster();
/*      */   }
/*      */   
/*      */   public boolean isAbonormallyLongQuery(long millisOrNanos) {
/* 1004 */     return this.mc.isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */   
/*      */   public boolean isNoBackslashEscapesSet() {
/* 1008 */     return this.mc.isNoBackslashEscapesSet();
/*      */   }
/*      */   
/*      */   public boolean lowerCaseTableNames() {
/* 1012 */     return this.mc.lowerCaseTableNames();
/*      */   }
/*      */   
/*      */   public boolean parserKnowsUnicode() {
/* 1016 */     return this.mc.parserKnowsUnicode();
/*      */   }
/*      */   
/*      */   public void reportQueryTime(long millisOrNanos) {
/* 1020 */     this.mc.reportQueryTime(millisOrNanos);
/*      */   }
/*      */   
/*      */   public void resetServerState() throws SQLException {
/* 1024 */     checkClosed();
/*      */     
/*      */     try {
/* 1027 */       this.mc.resetServerState();
/* 1028 */     } catch (SQLException sqlException) {
/* 1029 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/* 1035 */     checkClosed();
/*      */     
/*      */     try {
/* 1038 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.serverPrepareStatement(sql));
/*      */     }
/* 1040 */     catch (SQLException sqlException) {
/* 1041 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1044 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     try {
/* 1050 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.serverPrepareStatement(sql, autoGenKeyIndex));
/*      */     }
/* 1052 */     catch (SQLException sqlException) {
/* 1053 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1056 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     try {
/* 1062 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     
/*      */     }
/* 1065 */     catch (SQLException sqlException) {
/* 1066 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1069 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     try {
/* 1076 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
/*      */     
/*      */     }
/* 1079 */     catch (SQLException sqlException) {
/* 1080 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1083 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     try {
/* 1089 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.serverPrepareStatement(sql, autoGenKeyIndexes));
/*      */     }
/* 1091 */     catch (SQLException sqlException) {
/* 1092 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1095 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     try {
/* 1101 */       return PreparedStatementWrapper.getInstance(this, this.mpc, this.mc.serverPrepareStatement(sql, autoGenKeyColNames));
/*      */     }
/* 1103 */     catch (SQLException sqlException) {
/* 1104 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1107 */       return null;
/*      */     } 
/*      */   }
/*      */   public void setFailedOver(boolean flag) {
/* 1111 */     this.mc.setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPreferSlaveDuringFailover(boolean flag) {
/* 1116 */     this.mc.setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */   
/*      */   public void setStatementComment(String comment) {
/* 1120 */     this.mc.setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   
/*      */   public void shutdownServer() throws SQLException {
/* 1125 */     checkClosed();
/*      */     
/*      */     try {
/* 1128 */       this.mc.shutdownServer();
/* 1129 */     } catch (SQLException sqlException) {
/* 1130 */       checkAndFireConnectionError(sqlException);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean supportsIsolationLevel() {
/* 1136 */     return this.mc.supportsIsolationLevel();
/*      */   }
/*      */   
/*      */   public boolean supportsQuotedIdentifiers() {
/* 1140 */     return this.mc.supportsQuotedIdentifiers();
/*      */   }
/*      */   
/*      */   public boolean supportsTransactions() {
/* 1144 */     return this.mc.supportsTransactions();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
/* 1149 */     checkClosed();
/*      */     
/*      */     try {
/* 1152 */       return this.mc.versionMeetsMinimum(major, minor, subminor);
/* 1153 */     } catch (SQLException sqlException) {
/* 1154 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1157 */       return false;
/*      */     } 
/*      */   }
/*      */   public String exposeAsXml() throws SQLException {
/* 1161 */     checkClosed();
/*      */     
/*      */     try {
/* 1164 */       return this.mc.exposeAsXml();
/* 1165 */     } catch (SQLException sqlException) {
/* 1166 */       checkAndFireConnectionError(sqlException);
/*      */ 
/*      */       
/* 1169 */       return null;
/*      */     } 
/*      */   }
/*      */   public boolean getAllowLoadLocalInfile() {
/* 1173 */     return this.mc.getAllowLoadLocalInfile();
/*      */   }
/*      */   
/*      */   public boolean getAllowMultiQueries() {
/* 1177 */     return this.mc.getAllowMultiQueries();
/*      */   }
/*      */   
/*      */   public boolean getAllowNanAndInf() {
/* 1181 */     return this.mc.getAllowNanAndInf();
/*      */   }
/*      */   
/*      */   public boolean getAllowUrlInLocalInfile() {
/* 1185 */     return this.mc.getAllowUrlInLocalInfile();
/*      */   }
/*      */   
/*      */   public boolean getAlwaysSendSetIsolation() {
/* 1189 */     return this.mc.getAlwaysSendSetIsolation();
/*      */   }
/*      */   
/*      */   public boolean getAutoClosePStmtStreams() {
/* 1193 */     return this.mc.getAutoClosePStmtStreams();
/*      */   }
/*      */   
/*      */   public boolean getAutoDeserialize() {
/* 1197 */     return this.mc.getAutoDeserialize();
/*      */   }
/*      */   
/*      */   public boolean getAutoGenerateTestcaseScript() {
/* 1201 */     return this.mc.getAutoGenerateTestcaseScript();
/*      */   }
/*      */   
/*      */   public boolean getAutoReconnectForPools() {
/* 1205 */     return this.mc.getAutoReconnectForPools();
/*      */   }
/*      */   
/*      */   public boolean getAutoSlowLog() {
/* 1209 */     return this.mc.getAutoSlowLog();
/*      */   }
/*      */   
/*      */   public int getBlobSendChunkSize() {
/* 1213 */     return this.mc.getBlobSendChunkSize();
/*      */   }
/*      */   
/*      */   public boolean getBlobsAreStrings() {
/* 1217 */     return this.mc.getBlobsAreStrings();
/*      */   }
/*      */   
/*      */   public boolean getCacheCallableStatements() {
/* 1221 */     return this.mc.getCacheCallableStatements();
/*      */   }
/*      */   
/*      */   public boolean getCacheCallableStmts() {
/* 1225 */     return this.mc.getCacheCallableStmts();
/*      */   }
/*      */   
/*      */   public boolean getCachePrepStmts() {
/* 1229 */     return this.mc.getCachePrepStmts();
/*      */   }
/*      */   
/*      */   public boolean getCachePreparedStatements() {
/* 1233 */     return this.mc.getCachePreparedStatements();
/*      */   }
/*      */   
/*      */   public boolean getCacheResultSetMetadata() {
/* 1237 */     return this.mc.getCacheResultSetMetadata();
/*      */   }
/*      */   
/*      */   public boolean getCacheServerConfiguration() {
/* 1241 */     return this.mc.getCacheServerConfiguration();
/*      */   }
/*      */   
/*      */   public int getCallableStatementCacheSize() {
/* 1245 */     return this.mc.getCallableStatementCacheSize();
/*      */   }
/*      */   
/*      */   public int getCallableStmtCacheSize() {
/* 1249 */     return this.mc.getCallableStmtCacheSize();
/*      */   }
/*      */   
/*      */   public boolean getCapitalizeTypeNames() {
/* 1253 */     return this.mc.getCapitalizeTypeNames();
/*      */   }
/*      */   
/*      */   public String getCharacterSetResults() {
/* 1257 */     return this.mc.getCharacterSetResults();
/*      */   }
/*      */   
/*      */   public String getClientCertificateKeyStorePassword() {
/* 1261 */     return this.mc.getClientCertificateKeyStorePassword();
/*      */   }
/*      */   
/*      */   public String getClientCertificateKeyStoreType() {
/* 1265 */     return this.mc.getClientCertificateKeyStoreType();
/*      */   }
/*      */   
/*      */   public String getClientCertificateKeyStoreUrl() {
/* 1269 */     return this.mc.getClientCertificateKeyStoreUrl();
/*      */   }
/*      */   
/*      */   public String getClientInfoProvider() {
/* 1273 */     return this.mc.getClientInfoProvider();
/*      */   }
/*      */   
/*      */   public String getClobCharacterEncoding() {
/* 1277 */     return this.mc.getClobCharacterEncoding();
/*      */   }
/*      */   
/*      */   public boolean getClobberStreamingResults() {
/* 1281 */     return this.mc.getClobberStreamingResults();
/*      */   }
/*      */   
/*      */   public int getConnectTimeout() {
/* 1285 */     return this.mc.getConnectTimeout();
/*      */   }
/*      */   
/*      */   public String getConnectionCollation() {
/* 1289 */     return this.mc.getConnectionCollation();
/*      */   }
/*      */   
/*      */   public String getConnectionLifecycleInterceptors() {
/* 1293 */     return this.mc.getConnectionLifecycleInterceptors();
/*      */   }
/*      */   
/*      */   public boolean getContinueBatchOnError() {
/* 1297 */     return this.mc.getContinueBatchOnError();
/*      */   }
/*      */   
/*      */   public boolean getCreateDatabaseIfNotExist() {
/* 1301 */     return this.mc.getCreateDatabaseIfNotExist();
/*      */   }
/*      */   
/*      */   public int getDefaultFetchSize() {
/* 1305 */     return this.mc.getDefaultFetchSize();
/*      */   }
/*      */   
/*      */   public boolean getDontTrackOpenResources() {
/* 1309 */     return this.mc.getDontTrackOpenResources();
/*      */   }
/*      */   
/*      */   public boolean getDumpMetadataOnColumnNotFound() {
/* 1313 */     return this.mc.getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */   
/*      */   public boolean getDumpQueriesOnException() {
/* 1317 */     return this.mc.getDumpQueriesOnException();
/*      */   }
/*      */   
/*      */   public boolean getDynamicCalendars() {
/* 1321 */     return this.mc.getDynamicCalendars();
/*      */   }
/*      */   
/*      */   public boolean getElideSetAutoCommits() {
/* 1325 */     return this.mc.getElideSetAutoCommits();
/*      */   }
/*      */   
/*      */   public boolean getEmptyStringsConvertToZero() {
/* 1329 */     return this.mc.getEmptyStringsConvertToZero();
/*      */   }
/*      */   
/*      */   public boolean getEmulateLocators() {
/* 1333 */     return this.mc.getEmulateLocators();
/*      */   }
/*      */   
/*      */   public boolean getEmulateUnsupportedPstmts() {
/* 1337 */     return this.mc.getEmulateUnsupportedPstmts();
/*      */   }
/*      */   
/*      */   public boolean getEnablePacketDebug() {
/* 1341 */     return this.mc.getEnablePacketDebug();
/*      */   }
/*      */   
/*      */   public boolean getEnableQueryTimeouts() {
/* 1345 */     return this.mc.getEnableQueryTimeouts();
/*      */   }
/*      */   
/*      */   public String getEncoding() {
/* 1349 */     return this.mc.getEncoding();
/*      */   }
/*      */   
/*      */   public boolean getExplainSlowQueries() {
/* 1353 */     return this.mc.getExplainSlowQueries();
/*      */   }
/*      */   
/*      */   public boolean getFailOverReadOnly() {
/* 1357 */     return this.mc.getFailOverReadOnly();
/*      */   }
/*      */   
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 1361 */     return this.mc.getFunctionsNeverReturnBlobs();
/*      */   }
/*      */   
/*      */   public boolean getGatherPerfMetrics() {
/* 1365 */     return this.mc.getGatherPerfMetrics();
/*      */   }
/*      */   
/*      */   public boolean getGatherPerformanceMetrics() {
/* 1369 */     return this.mc.getGatherPerformanceMetrics();
/*      */   }
/*      */   
/*      */   public boolean getGenerateSimpleParameterMetadata() {
/* 1373 */     return this.mc.getGenerateSimpleParameterMetadata();
/*      */   }
/*      */   
/*      */   public boolean getHoldResultsOpenOverStatementClose() {
/* 1377 */     return this.mc.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */   
/*      */   public boolean getIgnoreNonTxTables() {
/* 1381 */     return this.mc.getIgnoreNonTxTables();
/*      */   }
/*      */   
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 1385 */     return this.mc.getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */   
/*      */   public int getInitialTimeout() {
/* 1389 */     return this.mc.getInitialTimeout();
/*      */   }
/*      */   
/*      */   public boolean getInteractiveClient() {
/* 1393 */     return this.mc.getInteractiveClient();
/*      */   }
/*      */   
/*      */   public boolean getIsInteractiveClient() {
/* 1397 */     return this.mc.getIsInteractiveClient();
/*      */   }
/*      */   
/*      */   public boolean getJdbcCompliantTruncation() {
/* 1401 */     return this.mc.getJdbcCompliantTruncation();
/*      */   }
/*      */   
/*      */   public boolean getJdbcCompliantTruncationForReads() {
/* 1405 */     return this.mc.getJdbcCompliantTruncationForReads();
/*      */   }
/*      */   
/*      */   public String getLargeRowSizeThreshold() {
/* 1409 */     return this.mc.getLargeRowSizeThreshold();
/*      */   }
/*      */   
/*      */   public String getLoadBalanceStrategy() {
/* 1413 */     return this.mc.getLoadBalanceStrategy();
/*      */   }
/*      */   
/*      */   public String getLocalSocketAddress() {
/* 1417 */     return this.mc.getLocalSocketAddress();
/*      */   }
/*      */   
/*      */   public int getLocatorFetchBufferSize() {
/* 1421 */     return this.mc.getLocatorFetchBufferSize();
/*      */   }
/*      */   
/*      */   public boolean getLogSlowQueries() {
/* 1425 */     return this.mc.getLogSlowQueries();
/*      */   }
/*      */   
/*      */   public boolean getLogXaCommands() {
/* 1429 */     return this.mc.getLogXaCommands();
/*      */   }
/*      */   
/*      */   public String getLogger() {
/* 1433 */     return this.mc.getLogger();
/*      */   }
/*      */   
/*      */   public String getLoggerClassName() {
/* 1437 */     return this.mc.getLoggerClassName();
/*      */   }
/*      */   
/*      */   public boolean getMaintainTimeStats() {
/* 1441 */     return this.mc.getMaintainTimeStats();
/*      */   }
/*      */   
/*      */   public int getMaxQuerySizeToLog() {
/* 1445 */     return this.mc.getMaxQuerySizeToLog();
/*      */   }
/*      */   
/*      */   public int getMaxReconnects() {
/* 1449 */     return this.mc.getMaxReconnects();
/*      */   }
/*      */   
/*      */   public int getMaxRows() {
/* 1453 */     return this.mc.getMaxRows();
/*      */   }
/*      */   
/*      */   public int getMetadataCacheSize() {
/* 1457 */     return this.mc.getMetadataCacheSize();
/*      */   }
/*      */   
/*      */   public int getNetTimeoutForStreamingResults() {
/* 1461 */     return this.mc.getNetTimeoutForStreamingResults();
/*      */   }
/*      */   
/*      */   public boolean getNoAccessToProcedureBodies() {
/* 1465 */     return this.mc.getNoAccessToProcedureBodies();
/*      */   }
/*      */   
/*      */   public boolean getNoDatetimeStringSync() {
/* 1469 */     return this.mc.getNoDatetimeStringSync();
/*      */   }
/*      */   
/*      */   public boolean getNoTimezoneConversionForTimeType() {
/* 1473 */     return this.mc.getNoTimezoneConversionForTimeType();
/*      */   }
/*      */   
/*      */   public boolean getNullCatalogMeansCurrent() {
/* 1477 */     return this.mc.getNullCatalogMeansCurrent();
/*      */   }
/*      */   
/*      */   public boolean getNullNamePatternMatchesAll() {
/* 1481 */     return this.mc.getNullNamePatternMatchesAll();
/*      */   }
/*      */   
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 1485 */     return this.mc.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */   
/*      */   public int getPacketDebugBufferSize() {
/* 1489 */     return this.mc.getPacketDebugBufferSize();
/*      */   }
/*      */   
/*      */   public boolean getPadCharsWithSpace() {
/* 1493 */     return this.mc.getPadCharsWithSpace();
/*      */   }
/*      */   
/*      */   public boolean getParanoid() {
/* 1497 */     return this.mc.getParanoid();
/*      */   }
/*      */   
/*      */   public boolean getPedantic() {
/* 1501 */     return this.mc.getPedantic();
/*      */   }
/*      */   
/*      */   public boolean getPinGlobalTxToPhysicalConnection() {
/* 1505 */     return this.mc.getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */   
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 1509 */     return this.mc.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */   
/*      */   public int getPrepStmtCacheSize() {
/* 1513 */     return this.mc.getPrepStmtCacheSize();
/*      */   }
/*      */   
/*      */   public int getPrepStmtCacheSqlLimit() {
/* 1517 */     return this.mc.getPrepStmtCacheSqlLimit();
/*      */   }
/*      */   
/*      */   public int getPreparedStatementCacheSize() {
/* 1521 */     return this.mc.getPreparedStatementCacheSize();
/*      */   }
/*      */   
/*      */   public int getPreparedStatementCacheSqlLimit() {
/* 1525 */     return this.mc.getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */   
/*      */   public boolean getProcessEscapeCodesForPrepStmts() {
/* 1529 */     return this.mc.getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */   
/*      */   public boolean getProfileSQL() {
/* 1533 */     return this.mc.getProfileSQL();
/*      */   }
/*      */   
/*      */   public boolean getProfileSql() {
/* 1537 */     return this.mc.getProfileSql();
/*      */   }
/*      */   
/*      */   public String getPropertiesTransform() {
/* 1541 */     return this.mc.getPropertiesTransform();
/*      */   }
/*      */   
/*      */   public int getQueriesBeforeRetryMaster() {
/* 1545 */     return this.mc.getQueriesBeforeRetryMaster();
/*      */   }
/*      */   
/*      */   public boolean getReconnectAtTxEnd() {
/* 1549 */     return this.mc.getReconnectAtTxEnd();
/*      */   }
/*      */   
/*      */   public boolean getRelaxAutoCommit() {
/* 1553 */     return this.mc.getRelaxAutoCommit();
/*      */   }
/*      */   
/*      */   public int getReportMetricsIntervalMillis() {
/* 1557 */     return this.mc.getReportMetricsIntervalMillis();
/*      */   }
/*      */   
/*      */   public boolean getRequireSSL() {
/* 1561 */     return this.mc.getRequireSSL();
/*      */   }
/*      */   
/*      */   public String getResourceId() {
/* 1565 */     return this.mc.getResourceId();
/*      */   }
/*      */   
/*      */   public int getResultSetSizeThreshold() {
/* 1569 */     return this.mc.getResultSetSizeThreshold();
/*      */   }
/*      */   
/*      */   public boolean getRewriteBatchedStatements() {
/* 1573 */     return this.mc.getRewriteBatchedStatements();
/*      */   }
/*      */   
/*      */   public boolean getRollbackOnPooledClose() {
/* 1577 */     return this.mc.getRollbackOnPooledClose();
/*      */   }
/*      */   
/*      */   public boolean getRoundRobinLoadBalance() {
/* 1581 */     return this.mc.getRoundRobinLoadBalance();
/*      */   }
/*      */   
/*      */   public boolean getRunningCTS13() {
/* 1585 */     return this.mc.getRunningCTS13();
/*      */   }
/*      */   
/*      */   public int getSecondsBeforeRetryMaster() {
/* 1589 */     return this.mc.getSecondsBeforeRetryMaster();
/*      */   }
/*      */   
/*      */   public String getServerTimezone() {
/* 1593 */     return this.mc.getServerTimezone();
/*      */   }
/*      */   
/*      */   public String getSessionVariables() {
/* 1597 */     return this.mc.getSessionVariables();
/*      */   }
/*      */   
/*      */   public int getSlowQueryThresholdMillis() {
/* 1601 */     return this.mc.getSlowQueryThresholdMillis();
/*      */   }
/*      */   
/*      */   public long getSlowQueryThresholdNanos() {
/* 1605 */     return this.mc.getSlowQueryThresholdNanos();
/*      */   }
/*      */   
/*      */   public String getSocketFactory() {
/* 1609 */     return this.mc.getSocketFactory();
/*      */   }
/*      */   
/*      */   public String getSocketFactoryClassName() {
/* 1613 */     return this.mc.getSocketFactoryClassName();
/*      */   }
/*      */   
/*      */   public int getSocketTimeout() {
/* 1617 */     return this.mc.getSocketTimeout();
/*      */   }
/*      */   
/*      */   public String getStatementInterceptors() {
/* 1621 */     return this.mc.getStatementInterceptors();
/*      */   }
/*      */   
/*      */   public boolean getStrictFloatingPoint() {
/* 1625 */     return this.mc.getStrictFloatingPoint();
/*      */   }
/*      */   
/*      */   public boolean getStrictUpdates() {
/* 1629 */     return this.mc.getStrictUpdates();
/*      */   }
/*      */   
/*      */   public boolean getTcpKeepAlive() {
/* 1633 */     return this.mc.getTcpKeepAlive();
/*      */   }
/*      */   
/*      */   public boolean getTcpNoDelay() {
/* 1637 */     return this.mc.getTcpNoDelay();
/*      */   }
/*      */   
/*      */   public int getTcpRcvBuf() {
/* 1641 */     return this.mc.getTcpRcvBuf();
/*      */   }
/*      */   
/*      */   public int getTcpSndBuf() {
/* 1645 */     return this.mc.getTcpSndBuf();
/*      */   }
/*      */   
/*      */   public int getTcpTrafficClass() {
/* 1649 */     return this.mc.getTcpTrafficClass();
/*      */   }
/*      */   
/*      */   public boolean getTinyInt1isBit() {
/* 1653 */     return this.mc.getTinyInt1isBit();
/*      */   }
/*      */   
/*      */   public boolean getTraceProtocol() {
/* 1657 */     return this.mc.getTraceProtocol();
/*      */   }
/*      */   
/*      */   public boolean getTransformedBitIsBoolean() {
/* 1661 */     return this.mc.getTransformedBitIsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getTreatUtilDateAsTimestamp() {
/* 1665 */     return this.mc.getTreatUtilDateAsTimestamp();
/*      */   }
/*      */   
/*      */   public String getTrustCertificateKeyStorePassword() {
/* 1669 */     return this.mc.getTrustCertificateKeyStorePassword();
/*      */   }
/*      */   
/*      */   public String getTrustCertificateKeyStoreType() {
/* 1673 */     return this.mc.getTrustCertificateKeyStoreType();
/*      */   }
/*      */   
/*      */   public String getTrustCertificateKeyStoreUrl() {
/* 1677 */     return this.mc.getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */   
/*      */   public boolean getUltraDevHack() {
/* 1681 */     return this.mc.getUltraDevHack();
/*      */   }
/*      */   
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 1685 */     return this.mc.getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */   
/*      */   public boolean getUseCompression() {
/* 1689 */     return this.mc.getUseCompression();
/*      */   }
/*      */   
/*      */   public String getUseConfigs() {
/* 1693 */     return this.mc.getUseConfigs();
/*      */   }
/*      */   
/*      */   public boolean getUseCursorFetch() {
/* 1697 */     return this.mc.getUseCursorFetch();
/*      */   }
/*      */   
/*      */   public boolean getUseDirectRowUnpack() {
/* 1701 */     return this.mc.getUseDirectRowUnpack();
/*      */   }
/*      */   
/*      */   public boolean getUseDynamicCharsetInfo() {
/* 1705 */     return this.mc.getUseDynamicCharsetInfo();
/*      */   }
/*      */   
/*      */   public boolean getUseFastDateParsing() {
/* 1709 */     return this.mc.getUseFastDateParsing();
/*      */   }
/*      */   
/*      */   public boolean getUseFastIntParsing() {
/* 1713 */     return this.mc.getUseFastIntParsing();
/*      */   }
/*      */   
/*      */   public boolean getUseGmtMillisForDatetimes() {
/* 1717 */     return this.mc.getUseGmtMillisForDatetimes();
/*      */   }
/*      */   
/*      */   public boolean getUseHostsInPrivileges() {
/* 1721 */     return this.mc.getUseHostsInPrivileges();
/*      */   }
/*      */   
/*      */   public boolean getUseInformationSchema() {
/* 1725 */     return this.mc.getUseInformationSchema();
/*      */   }
/*      */   
/*      */   public boolean getUseJDBCCompliantTimezoneShift() {
/* 1729 */     return this.mc.getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */   
/*      */   public boolean getUseJvmCharsetConverters() {
/* 1733 */     return this.mc.getUseJvmCharsetConverters();
/*      */   }
/*      */   
/*      */   public boolean getUseLocalSessionState() {
/* 1737 */     return this.mc.getUseLocalSessionState();
/*      */   }
/*      */   
/*      */   public boolean getUseNanosForElapsedTime() {
/* 1741 */     return this.mc.getUseNanosForElapsedTime();
/*      */   }
/*      */   
/*      */   public boolean getUseOldAliasMetadataBehavior() {
/* 1745 */     return this.mc.getUseOldAliasMetadataBehavior();
/*      */   }
/*      */   
/*      */   public boolean getUseOldUTF8Behavior() {
/* 1749 */     return this.mc.getUseOldUTF8Behavior();
/*      */   }
/*      */   
/*      */   public boolean getUseOnlyServerErrorMessages() {
/* 1753 */     return this.mc.getUseOnlyServerErrorMessages();
/*      */   }
/*      */   
/*      */   public boolean getUseReadAheadInput() {
/* 1757 */     return this.mc.getUseReadAheadInput();
/*      */   }
/*      */   
/*      */   public boolean getUseSSL() {
/* 1761 */     return this.mc.getUseSSL();
/*      */   }
/*      */   
/*      */   public boolean getUseSSPSCompatibleTimezoneShift() {
/* 1765 */     return this.mc.getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */   
/*      */   public boolean getUseServerPrepStmts() {
/* 1769 */     return this.mc.getUseServerPrepStmts();
/*      */   }
/*      */   
/*      */   public boolean getUseServerPreparedStmts() {
/* 1773 */     return this.mc.getUseServerPreparedStmts();
/*      */   }
/*      */   
/*      */   public boolean getUseSqlStateCodes() {
/* 1777 */     return this.mc.getUseSqlStateCodes();
/*      */   }
/*      */   
/*      */   public boolean getUseStreamLengthsInPrepStmts() {
/* 1781 */     return this.mc.getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */   
/*      */   public boolean getUseTimezone() {
/* 1785 */     return this.mc.getUseTimezone();
/*      */   }
/*      */   
/*      */   public boolean getUseUltraDevWorkAround() {
/* 1789 */     return this.mc.getUseUltraDevWorkAround();
/*      */   }
/*      */   
/*      */   public boolean getUseUnbufferedInput() {
/* 1793 */     return this.mc.getUseUnbufferedInput();
/*      */   }
/*      */   
/*      */   public boolean getUseUnicode() {
/* 1797 */     return this.mc.getUseUnicode();
/*      */   }
/*      */   
/*      */   public boolean getUseUsageAdvisor() {
/* 1801 */     return this.mc.getUseUsageAdvisor();
/*      */   }
/*      */   
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 1805 */     return this.mc.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */   
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 1809 */     return this.mc.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */   
/*      */   public boolean getYearIsDateType() {
/* 1813 */     return this.mc.getYearIsDateType();
/*      */   }
/*      */   
/*      */   public String getZeroDateTimeBehavior() {
/* 1817 */     return this.mc.getZeroDateTimeBehavior();
/*      */   }
/*      */   
/*      */   public void setAllowLoadLocalInfile(boolean property) {
/* 1821 */     this.mc.setAllowLoadLocalInfile(property);
/*      */   }
/*      */   
/*      */   public void setAllowMultiQueries(boolean property) {
/* 1825 */     this.mc.setAllowMultiQueries(property);
/*      */   }
/*      */   
/*      */   public void setAllowNanAndInf(boolean flag) {
/* 1829 */     this.mc.setAllowNanAndInf(flag);
/*      */   }
/*      */   
/*      */   public void setAllowUrlInLocalInfile(boolean flag) {
/* 1833 */     this.mc.setAllowUrlInLocalInfile(flag);
/*      */   }
/*      */   
/*      */   public void setAlwaysSendSetIsolation(boolean flag) {
/* 1837 */     this.mc.setAlwaysSendSetIsolation(flag);
/*      */   }
/*      */   
/*      */   public void setAutoClosePStmtStreams(boolean flag) {
/* 1841 */     this.mc.setAutoClosePStmtStreams(flag);
/*      */   }
/*      */   
/*      */   public void setAutoDeserialize(boolean flag) {
/* 1845 */     this.mc.setAutoDeserialize(flag);
/*      */   }
/*      */   
/*      */   public void setAutoGenerateTestcaseScript(boolean flag) {
/* 1849 */     this.mc.setAutoGenerateTestcaseScript(flag);
/*      */   }
/*      */   
/*      */   public void setAutoReconnect(boolean flag) {
/* 1853 */     this.mc.setAutoReconnect(flag);
/*      */   }
/*      */   
/*      */   public void setAutoReconnectForConnectionPools(boolean property) {
/* 1857 */     this.mc.setAutoReconnectForConnectionPools(property);
/*      */   }
/*      */   
/*      */   public void setAutoReconnectForPools(boolean flag) {
/* 1861 */     this.mc.setAutoReconnectForPools(flag);
/*      */   }
/*      */   
/*      */   public void setAutoSlowLog(boolean flag) {
/* 1865 */     this.mc.setAutoSlowLog(flag);
/*      */   }
/*      */   
/*      */   public void setBlobSendChunkSize(String value) throws SQLException {
/* 1869 */     this.mc.setBlobSendChunkSize(value);
/*      */   }
/*      */   
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 1873 */     this.mc.setBlobsAreStrings(flag);
/*      */   }
/*      */   
/*      */   public void setCacheCallableStatements(boolean flag) {
/* 1877 */     this.mc.setCacheCallableStatements(flag);
/*      */   }
/*      */   
/*      */   public void setCacheCallableStmts(boolean flag) {
/* 1881 */     this.mc.setCacheCallableStmts(flag);
/*      */   }
/*      */   
/*      */   public void setCachePrepStmts(boolean flag) {
/* 1885 */     this.mc.setCachePrepStmts(flag);
/*      */   }
/*      */   
/*      */   public void setCachePreparedStatements(boolean flag) {
/* 1889 */     this.mc.setCachePreparedStatements(flag);
/*      */   }
/*      */   
/*      */   public void setCacheResultSetMetadata(boolean property) {
/* 1893 */     this.mc.setCacheResultSetMetadata(property);
/*      */   }
/*      */   
/*      */   public void setCacheServerConfiguration(boolean flag) {
/* 1897 */     this.mc.setCacheServerConfiguration(flag);
/*      */   }
/*      */   
/*      */   public void setCallableStatementCacheSize(int size) {
/* 1901 */     this.mc.setCallableStatementCacheSize(size);
/*      */   }
/*      */   
/*      */   public void setCallableStmtCacheSize(int cacheSize) {
/* 1905 */     this.mc.setCallableStmtCacheSize(cacheSize);
/*      */   }
/*      */   
/*      */   public void setCapitalizeDBMDTypes(boolean property) {
/* 1909 */     this.mc.setCapitalizeDBMDTypes(property);
/*      */   }
/*      */   
/*      */   public void setCapitalizeTypeNames(boolean flag) {
/* 1913 */     this.mc.setCapitalizeTypeNames(flag);
/*      */   }
/*      */   
/*      */   public void setCharacterEncoding(String encoding) {
/* 1917 */     this.mc.setCharacterEncoding(encoding);
/*      */   }
/*      */   
/*      */   public void setCharacterSetResults(String characterSet) {
/* 1921 */     this.mc.setCharacterSetResults(characterSet);
/*      */   }
/*      */   
/*      */   public void setClientCertificateKeyStorePassword(String value) {
/* 1925 */     this.mc.setClientCertificateKeyStorePassword(value);
/*      */   }
/*      */   
/*      */   public void setClientCertificateKeyStoreType(String value) {
/* 1929 */     this.mc.setClientCertificateKeyStoreType(value);
/*      */   }
/*      */   
/*      */   public void setClientCertificateKeyStoreUrl(String value) {
/* 1933 */     this.mc.setClientCertificateKeyStoreUrl(value);
/*      */   }
/*      */   
/*      */   public void setClientInfoProvider(String classname) {
/* 1937 */     this.mc.setClientInfoProvider(classname);
/*      */   }
/*      */   
/*      */   public void setClobCharacterEncoding(String encoding) {
/* 1941 */     this.mc.setClobCharacterEncoding(encoding);
/*      */   }
/*      */   
/*      */   public void setClobberStreamingResults(boolean flag) {
/* 1945 */     this.mc.setClobberStreamingResults(flag);
/*      */   }
/*      */   
/*      */   public void setConnectTimeout(int timeoutMs) {
/* 1949 */     this.mc.setConnectTimeout(timeoutMs);
/*      */   }
/*      */   
/*      */   public void setConnectionCollation(String collation) {
/* 1953 */     this.mc.setConnectionCollation(collation);
/*      */   }
/*      */   
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 1957 */     this.mc.setConnectionLifecycleInterceptors(interceptors);
/*      */   }
/*      */   
/*      */   public void setContinueBatchOnError(boolean property) {
/* 1961 */     this.mc.setContinueBatchOnError(property);
/*      */   }
/*      */   
/*      */   public void setCreateDatabaseIfNotExist(boolean flag) {
/* 1965 */     this.mc.setCreateDatabaseIfNotExist(flag);
/*      */   }
/*      */   
/*      */   public void setDefaultFetchSize(int n) {
/* 1969 */     this.mc.setDefaultFetchSize(n);
/*      */   }
/*      */   
/*      */   public void setDetectServerPreparedStmts(boolean property) {
/* 1973 */     this.mc.setDetectServerPreparedStmts(property);
/*      */   }
/*      */   
/*      */   public void setDontTrackOpenResources(boolean flag) {
/* 1977 */     this.mc.setDontTrackOpenResources(flag);
/*      */   }
/*      */   
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag) {
/* 1981 */     this.mc.setDumpMetadataOnColumnNotFound(flag);
/*      */   }
/*      */   
/*      */   public void setDumpQueriesOnException(boolean flag) {
/* 1985 */     this.mc.setDumpQueriesOnException(flag);
/*      */   }
/*      */   
/*      */   public void setDynamicCalendars(boolean flag) {
/* 1989 */     this.mc.setDynamicCalendars(flag);
/*      */   }
/*      */   
/*      */   public void setElideSetAutoCommits(boolean flag) {
/* 1993 */     this.mc.setElideSetAutoCommits(flag);
/*      */   }
/*      */   
/*      */   public void setEmptyStringsConvertToZero(boolean flag) {
/* 1997 */     this.mc.setEmptyStringsConvertToZero(flag);
/*      */   }
/*      */   
/*      */   public void setEmulateLocators(boolean property) {
/* 2001 */     this.mc.setEmulateLocators(property);
/*      */   }
/*      */   
/*      */   public void setEmulateUnsupportedPstmts(boolean flag) {
/* 2005 */     this.mc.setEmulateUnsupportedPstmts(flag);
/*      */   }
/*      */   
/*      */   public void setEnablePacketDebug(boolean flag) {
/* 2009 */     this.mc.setEnablePacketDebug(flag);
/*      */   }
/*      */   
/*      */   public void setEnableQueryTimeouts(boolean flag) {
/* 2013 */     this.mc.setEnableQueryTimeouts(flag);
/*      */   }
/*      */   
/*      */   public void setEncoding(String property) {
/* 2017 */     this.mc.setEncoding(property);
/*      */   }
/*      */   
/*      */   public void setExplainSlowQueries(boolean flag) {
/* 2021 */     this.mc.setExplainSlowQueries(flag);
/*      */   }
/*      */   
/*      */   public void setFailOverReadOnly(boolean flag) {
/* 2025 */     this.mc.setFailOverReadOnly(flag);
/*      */   }
/*      */   
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 2029 */     this.mc.setFunctionsNeverReturnBlobs(flag);
/*      */   }
/*      */   
/*      */   public void setGatherPerfMetrics(boolean flag) {
/* 2033 */     this.mc.setGatherPerfMetrics(flag);
/*      */   }
/*      */   
/*      */   public void setGatherPerformanceMetrics(boolean flag) {
/* 2037 */     this.mc.setGatherPerformanceMetrics(flag);
/*      */   }
/*      */   
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag) {
/* 2041 */     this.mc.setGenerateSimpleParameterMetadata(flag);
/*      */   }
/*      */   
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag) {
/* 2045 */     this.mc.setHoldResultsOpenOverStatementClose(flag);
/*      */   }
/*      */   
/*      */   public void setIgnoreNonTxTables(boolean property) {
/* 2049 */     this.mc.setIgnoreNonTxTables(property);
/*      */   }
/*      */   
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 2053 */     this.mc.setIncludeInnodbStatusInDeadlockExceptions(flag);
/*      */   }
/*      */   
/*      */   public void setInitialTimeout(int property) {
/* 2057 */     this.mc.setInitialTimeout(property);
/*      */   }
/*      */   
/*      */   public void setInteractiveClient(boolean property) {
/* 2061 */     this.mc.setInteractiveClient(property);
/*      */   }
/*      */   
/*      */   public void setIsInteractiveClient(boolean property) {
/* 2065 */     this.mc.setIsInteractiveClient(property);
/*      */   }
/*      */   
/*      */   public void setJdbcCompliantTruncation(boolean flag) {
/* 2069 */     this.mc.setJdbcCompliantTruncation(flag);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
/* 2074 */     this.mc.setJdbcCompliantTruncationForReads(jdbcCompliantTruncationForReads);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setLargeRowSizeThreshold(String value) {
/* 2079 */     this.mc.setLargeRowSizeThreshold(value);
/*      */   }
/*      */   
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 2083 */     this.mc.setLoadBalanceStrategy(strategy);
/*      */   }
/*      */   
/*      */   public void setLocalSocketAddress(String address) {
/* 2087 */     this.mc.setLocalSocketAddress(address);
/*      */   }
/*      */   
/*      */   public void setLocatorFetchBufferSize(String value) throws SQLException {
/* 2091 */     this.mc.setLocatorFetchBufferSize(value);
/*      */   }
/*      */   
/*      */   public void setLogSlowQueries(boolean flag) {
/* 2095 */     this.mc.setLogSlowQueries(flag);
/*      */   }
/*      */   
/*      */   public void setLogXaCommands(boolean flag) {
/* 2099 */     this.mc.setLogXaCommands(flag);
/*      */   }
/*      */   
/*      */   public void setLogger(String property) {
/* 2103 */     this.mc.setLogger(property);
/*      */   }
/*      */   
/*      */   public void setLoggerClassName(String className) {
/* 2107 */     this.mc.setLoggerClassName(className);
/*      */   }
/*      */   
/*      */   public void setMaintainTimeStats(boolean flag) {
/* 2111 */     this.mc.setMaintainTimeStats(flag);
/*      */   }
/*      */   
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes) {
/* 2115 */     this.mc.setMaxQuerySizeToLog(sizeInBytes);
/*      */   }
/*      */   
/*      */   public void setMaxReconnects(int property) {
/* 2119 */     this.mc.setMaxReconnects(property);
/*      */   }
/*      */   
/*      */   public void setMaxRows(int property) {
/* 2123 */     this.mc.setMaxRows(property);
/*      */   }
/*      */   
/*      */   public void setMetadataCacheSize(int value) {
/* 2127 */     this.mc.setMetadataCacheSize(value);
/*      */   }
/*      */   
/*      */   public void setNetTimeoutForStreamingResults(int value) {
/* 2131 */     this.mc.setNetTimeoutForStreamingResults(value);
/*      */   }
/*      */   
/*      */   public void setNoAccessToProcedureBodies(boolean flag) {
/* 2135 */     this.mc.setNoAccessToProcedureBodies(flag);
/*      */   }
/*      */   
/*      */   public void setNoDatetimeStringSync(boolean flag) {
/* 2139 */     this.mc.setNoDatetimeStringSync(flag);
/*      */   }
/*      */   
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag) {
/* 2143 */     this.mc.setNoTimezoneConversionForTimeType(flag);
/*      */   }
/*      */   
/*      */   public void setNullCatalogMeansCurrent(boolean value) {
/* 2147 */     this.mc.setNullCatalogMeansCurrent(value);
/*      */   }
/*      */   
/*      */   public void setNullNamePatternMatchesAll(boolean value) {
/* 2151 */     this.mc.setNullNamePatternMatchesAll(value);
/*      */   }
/*      */   
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
/* 2155 */     this.mc.setOverrideSupportsIntegrityEnhancementFacility(flag);
/*      */   }
/*      */   
/*      */   public void setPacketDebugBufferSize(int size) {
/* 2159 */     this.mc.setPacketDebugBufferSize(size);
/*      */   }
/*      */   
/*      */   public void setPadCharsWithSpace(boolean flag) {
/* 2163 */     this.mc.setPadCharsWithSpace(flag);
/*      */   }
/*      */   
/*      */   public void setParanoid(boolean property) {
/* 2167 */     this.mc.setParanoid(property);
/*      */   }
/*      */   
/*      */   public void setPedantic(boolean property) {
/* 2171 */     this.mc.setPedantic(property);
/*      */   }
/*      */   
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag) {
/* 2175 */     this.mc.setPinGlobalTxToPhysicalConnection(flag);
/*      */   }
/*      */   
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 2179 */     this.mc.setPopulateInsertRowWithDefaultValues(flag);
/*      */   }
/*      */   
/*      */   public void setPrepStmtCacheSize(int cacheSize) {
/* 2183 */     this.mc.setPrepStmtCacheSize(cacheSize);
/*      */   }
/*      */   
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit) {
/* 2187 */     this.mc.setPrepStmtCacheSqlLimit(sqlLimit);
/*      */   }
/*      */   
/*      */   public void setPreparedStatementCacheSize(int cacheSize) {
/* 2191 */     this.mc.setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */   
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) {
/* 2195 */     this.mc.setPreparedStatementCacheSqlLimit(cacheSqlLimit);
/*      */   }
/*      */   
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag) {
/* 2199 */     this.mc.setProcessEscapeCodesForPrepStmts(flag);
/*      */   }
/*      */   
/*      */   public void setProfileSQL(boolean flag) {
/* 2203 */     this.mc.setProfileSQL(flag);
/*      */   }
/*      */   
/*      */   public void setProfileSql(boolean property) {
/* 2207 */     this.mc.setProfileSql(property);
/*      */   }
/*      */   
/*      */   public void setPropertiesTransform(String value) {
/* 2211 */     this.mc.setPropertiesTransform(value);
/*      */   }
/*      */   
/*      */   public void setQueriesBeforeRetryMaster(int property) {
/* 2215 */     this.mc.setQueriesBeforeRetryMaster(property);
/*      */   }
/*      */   
/*      */   public void setReconnectAtTxEnd(boolean property) {
/* 2219 */     this.mc.setReconnectAtTxEnd(property);
/*      */   }
/*      */   
/*      */   public void setRelaxAutoCommit(boolean property) {
/* 2223 */     this.mc.setRelaxAutoCommit(property);
/*      */   }
/*      */   
/*      */   public void setReportMetricsIntervalMillis(int millis) {
/* 2227 */     this.mc.setReportMetricsIntervalMillis(millis);
/*      */   }
/*      */   
/*      */   public void setRequireSSL(boolean property) {
/* 2231 */     this.mc.setRequireSSL(property);
/*      */   }
/*      */   
/*      */   public void setResourceId(String resourceId) {
/* 2235 */     this.mc.setResourceId(resourceId);
/*      */   }
/*      */   
/*      */   public void setResultSetSizeThreshold(int threshold) {
/* 2239 */     this.mc.setResultSetSizeThreshold(threshold);
/*      */   }
/*      */   
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag) {
/* 2243 */     this.mc.setRetainStatementAfterResultSetClose(flag);
/*      */   }
/*      */   
/*      */   public void setRewriteBatchedStatements(boolean flag) {
/* 2247 */     this.mc.setRewriteBatchedStatements(flag);
/*      */   }
/*      */   
/*      */   public void setRollbackOnPooledClose(boolean flag) {
/* 2251 */     this.mc.setRollbackOnPooledClose(flag);
/*      */   }
/*      */   
/*      */   public void setRoundRobinLoadBalance(boolean flag) {
/* 2255 */     this.mc.setRoundRobinLoadBalance(flag);
/*      */   }
/*      */   
/*      */   public void setRunningCTS13(boolean flag) {
/* 2259 */     this.mc.setRunningCTS13(flag);
/*      */   }
/*      */   
/*      */   public void setSecondsBeforeRetryMaster(int property) {
/* 2263 */     this.mc.setSecondsBeforeRetryMaster(property);
/*      */   }
/*      */   
/*      */   public void setServerTimezone(String property) {
/* 2267 */     this.mc.setServerTimezone(property);
/*      */   }
/*      */   
/*      */   public void setSessionVariables(String variables) {
/* 2271 */     this.mc.setSessionVariables(variables);
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdMillis(int millis) {
/* 2275 */     this.mc.setSlowQueryThresholdMillis(millis);
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdNanos(long nanos) {
/* 2279 */     this.mc.setSlowQueryThresholdNanos(nanos);
/*      */   }
/*      */   
/*      */   public void setSocketFactory(String name) {
/* 2283 */     this.mc.setSocketFactory(name);
/*      */   }
/*      */   
/*      */   public void setSocketFactoryClassName(String property) {
/* 2287 */     this.mc.setSocketFactoryClassName(property);
/*      */   }
/*      */   
/*      */   public void setSocketTimeout(int property) {
/* 2291 */     this.mc.setSocketTimeout(property);
/*      */   }
/*      */   
/*      */   public void setStatementInterceptors(String value) {
/* 2295 */     this.mc.setStatementInterceptors(value);
/*      */   }
/*      */   
/*      */   public void setStrictFloatingPoint(boolean property) {
/* 2299 */     this.mc.setStrictFloatingPoint(property);
/*      */   }
/*      */   
/*      */   public void setStrictUpdates(boolean property) {
/* 2303 */     this.mc.setStrictUpdates(property);
/*      */   }
/*      */   
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 2307 */     this.mc.setTcpKeepAlive(flag);
/*      */   }
/*      */   
/*      */   public void setTcpNoDelay(boolean flag) {
/* 2311 */     this.mc.setTcpNoDelay(flag);
/*      */   }
/*      */   
/*      */   public void setTcpRcvBuf(int bufSize) {
/* 2315 */     this.mc.setTcpRcvBuf(bufSize);
/*      */   }
/*      */   
/*      */   public void setTcpSndBuf(int bufSize) {
/* 2319 */     this.mc.setTcpSndBuf(bufSize);
/*      */   }
/*      */   
/*      */   public void setTcpTrafficClass(int classFlags) {
/* 2323 */     this.mc.setTcpTrafficClass(classFlags);
/*      */   }
/*      */   
/*      */   public void setTinyInt1isBit(boolean flag) {
/* 2327 */     this.mc.setTinyInt1isBit(flag);
/*      */   }
/*      */   
/*      */   public void setTraceProtocol(boolean flag) {
/* 2331 */     this.mc.setTraceProtocol(flag);
/*      */   }
/*      */   
/*      */   public void setTransformedBitIsBoolean(boolean flag) {
/* 2335 */     this.mc.setTransformedBitIsBoolean(flag);
/*      */   }
/*      */   
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag) {
/* 2339 */     this.mc.setTreatUtilDateAsTimestamp(flag);
/*      */   }
/*      */   
/*      */   public void setTrustCertificateKeyStorePassword(String value) {
/* 2343 */     this.mc.setTrustCertificateKeyStorePassword(value);
/*      */   }
/*      */   
/*      */   public void setTrustCertificateKeyStoreType(String value) {
/* 2347 */     this.mc.setTrustCertificateKeyStoreType(value);
/*      */   }
/*      */   
/*      */   public void setTrustCertificateKeyStoreUrl(String value) {
/* 2351 */     this.mc.setTrustCertificateKeyStoreUrl(value);
/*      */   }
/*      */   
/*      */   public void setUltraDevHack(boolean flag) {
/* 2355 */     this.mc.setUltraDevHack(flag);
/*      */   }
/*      */   
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 2359 */     this.mc.setUseBlobToStoreUTF8OutsideBMP(flag);
/*      */   }
/*      */   
/*      */   public void setUseCompression(boolean property) {
/* 2363 */     this.mc.setUseCompression(property);
/*      */   }
/*      */   
/*      */   public void setUseConfigs(String configs) {
/* 2367 */     this.mc.setUseConfigs(configs);
/*      */   }
/*      */   
/*      */   public void setUseCursorFetch(boolean flag) {
/* 2371 */     this.mc.setUseCursorFetch(flag);
/*      */   }
/*      */   
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 2375 */     this.mc.setUseDirectRowUnpack(flag);
/*      */   }
/*      */   
/*      */   public void setUseDynamicCharsetInfo(boolean flag) {
/* 2379 */     this.mc.setUseDynamicCharsetInfo(flag);
/*      */   }
/*      */   
/*      */   public void setUseFastDateParsing(boolean flag) {
/* 2383 */     this.mc.setUseFastDateParsing(flag);
/*      */   }
/*      */   
/*      */   public void setUseFastIntParsing(boolean flag) {
/* 2387 */     this.mc.setUseFastIntParsing(flag);
/*      */   }
/*      */   
/*      */   public void setUseGmtMillisForDatetimes(boolean flag) {
/* 2391 */     this.mc.setUseGmtMillisForDatetimes(flag);
/*      */   }
/*      */   
/*      */   public void setUseHostsInPrivileges(boolean property) {
/* 2395 */     this.mc.setUseHostsInPrivileges(property);
/*      */   }
/*      */   
/*      */   public void setUseInformationSchema(boolean flag) {
/* 2399 */     this.mc.setUseInformationSchema(flag);
/*      */   }
/*      */   
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag) {
/* 2403 */     this.mc.setUseJDBCCompliantTimezoneShift(flag);
/*      */   }
/*      */   
/*      */   public void setUseJvmCharsetConverters(boolean flag) {
/* 2407 */     this.mc.setUseJvmCharsetConverters(flag);
/*      */   }
/*      */   
/*      */   public void setUseLocalSessionState(boolean flag) {
/* 2411 */     this.mc.setUseLocalSessionState(flag);
/*      */   }
/*      */   
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 2415 */     this.mc.setUseNanosForElapsedTime(flag);
/*      */   }
/*      */   
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag) {
/* 2419 */     this.mc.setUseOldAliasMetadataBehavior(flag);
/*      */   }
/*      */   
/*      */   public void setUseOldUTF8Behavior(boolean flag) {
/* 2423 */     this.mc.setUseOldUTF8Behavior(flag);
/*      */   }
/*      */   
/*      */   public void setUseOnlyServerErrorMessages(boolean flag) {
/* 2427 */     this.mc.setUseOnlyServerErrorMessages(flag);
/*      */   }
/*      */   
/*      */   public void setUseReadAheadInput(boolean flag) {
/* 2431 */     this.mc.setUseReadAheadInput(flag);
/*      */   }
/*      */   
/*      */   public void setUseSSL(boolean property) {
/* 2435 */     this.mc.setUseSSL(property);
/*      */   }
/*      */   
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
/* 2439 */     this.mc.setUseSSPSCompatibleTimezoneShift(flag);
/*      */   }
/*      */   
/*      */   public void setUseServerPrepStmts(boolean flag) {
/* 2443 */     this.mc.setUseServerPrepStmts(flag);
/*      */   }
/*      */   
/*      */   public void setUseServerPreparedStmts(boolean flag) {
/* 2447 */     this.mc.setUseServerPreparedStmts(flag);
/*      */   }
/*      */   
/*      */   public void setUseSqlStateCodes(boolean flag) {
/* 2451 */     this.mc.setUseSqlStateCodes(flag);
/*      */   }
/*      */   
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property) {
/* 2455 */     this.mc.setUseStreamLengthsInPrepStmts(property);
/*      */   }
/*      */   
/*      */   public void setUseTimezone(boolean property) {
/* 2459 */     this.mc.setUseTimezone(property);
/*      */   }
/*      */   
/*      */   public void setUseUltraDevWorkAround(boolean property) {
/* 2463 */     this.mc.setUseUltraDevWorkAround(property);
/*      */   }
/*      */   
/*      */   public void setUseUnbufferedInput(boolean flag) {
/* 2467 */     this.mc.setUseUnbufferedInput(flag);
/*      */   }
/*      */   
/*      */   public void setUseUnicode(boolean flag) {
/* 2471 */     this.mc.setUseUnicode(flag);
/*      */   }
/*      */   
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
/* 2475 */     this.mc.setUseUsageAdvisor(useUsageAdvisorFlag);
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 2479 */     this.mc.setUtf8OutsideBmpExcludedColumnNamePattern(regexPattern);
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 2483 */     this.mc.setUtf8OutsideBmpIncludedColumnNamePattern(regexPattern);
/*      */   }
/*      */   
/*      */   public void setYearIsDateType(boolean flag) {
/* 2487 */     this.mc.setYearIsDateType(flag);
/*      */   }
/*      */   
/*      */   public void setZeroDateTimeBehavior(String behavior) {
/* 2491 */     this.mc.setZeroDateTimeBehavior(behavior);
/*      */   }
/*      */   
/*      */   public boolean useUnbufferedInput() {
/* 2495 */     return this.mc.useUnbufferedInput();
/*      */   }
/*      */   
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 2499 */     this.mc.initializeExtension(ex);
/*      */   }
/*      */   
/*      */   public String getProfilerEventHandler() {
/* 2503 */     return this.mc.getProfilerEventHandler();
/*      */   }
/*      */   
/*      */   public void setProfilerEventHandler(String handler) {
/* 2507 */     this.mc.setProfilerEventHandler(handler);
/*      */   }
/*      */   
/*      */   public boolean getVerifyServerCertificate() {
/* 2511 */     return this.mc.getVerifyServerCertificate();
/*      */   }
/*      */   
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 2515 */     this.mc.setVerifyServerCertificate(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 2519 */     return this.mc.getUseLegacyDatetimeCode();
/*      */   }
/*      */   
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 2523 */     this.mc.setUseLegacyDatetimeCode(flag);
/*      */   }
/*      */   
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 2527 */     return this.mc.getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */   
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 2531 */     return this.mc.getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) {
/* 2535 */     this.mc.setSelfDestructOnPingMaxOperations(maxOperations);
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) {
/* 2539 */     this.mc.setSelfDestructOnPingSecondsLifetime(seconds);
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\ConnectionWrapper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */