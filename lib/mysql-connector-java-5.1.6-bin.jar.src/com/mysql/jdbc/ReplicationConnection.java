/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
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
/*      */ public class ReplicationConnection
/*      */   implements Connection, PingTarget
/*      */ {
/*      */   protected Connection currentConnection;
/*      */   protected Connection masterConnection;
/*      */   protected Connection slavesConnection;
/*      */   
/*      */   protected ReplicationConnection() {}
/*      */   
/*      */   public ReplicationConnection(Properties masterProperties, Properties slaveProperties) throws SQLException {
/*   56 */     NonRegisteringDriver driver = new NonRegisteringDriver();
/*      */     
/*   58 */     StringBuffer masterUrl = new StringBuffer("jdbc:mysql://");
/*   59 */     StringBuffer slaveUrl = new StringBuffer("jdbc:mysql://");
/*      */     
/*   61 */     String masterHost = masterProperties.getProperty("HOST");
/*      */ 
/*      */     
/*   64 */     if (masterHost != null) {
/*   65 */       masterUrl.append(masterHost);
/*      */     }
/*      */     
/*   68 */     String slaveHost = slaveProperties.getProperty("HOST");
/*      */ 
/*      */     
/*   71 */     if (slaveHost != null) {
/*   72 */       slaveUrl.append(slaveHost);
/*      */     }
/*      */     
/*   75 */     String masterDb = masterProperties.getProperty("DBNAME");
/*      */ 
/*      */     
/*   78 */     masterUrl.append("/");
/*      */     
/*   80 */     if (masterDb != null) {
/*   81 */       masterUrl.append(masterDb);
/*      */     }
/*      */     
/*   84 */     String slaveDb = slaveProperties.getProperty("DBNAME");
/*      */ 
/*      */     
/*   87 */     slaveUrl.append("/");
/*      */     
/*   89 */     if (slaveDb != null) {
/*   90 */       slaveUrl.append(slaveDb);
/*      */     }
/*      */     
/*   93 */     slaveProperties.setProperty("roundRobinLoadBalance", "true");
/*      */     
/*   95 */     this.masterConnection = (Connection)driver.connect(masterUrl.toString(), masterProperties);
/*      */     
/*   97 */     this.slavesConnection = (Connection)driver.connect(slaveUrl.toString(), slaveProperties);
/*      */     
/*   99 */     this.slavesConnection.setReadOnly(true);
/*      */     
/*  101 */     this.currentConnection = this.masterConnection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void clearWarnings() throws SQLException {
/*  110 */     this.currentConnection.clearWarnings();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close() throws SQLException {
/*  119 */     this.masterConnection.close();
/*  120 */     this.slavesConnection.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void commit() throws SQLException {
/*  129 */     this.currentConnection.commit();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Statement createStatement() throws SQLException {
/*  138 */     Statement stmt = this.currentConnection.createStatement();
/*  139 */     ((Statement)stmt).setPingTarget(this);
/*      */     
/*  141 */     return stmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/*  151 */     Statement stmt = this.currentConnection.createStatement(resultSetType, resultSetConcurrency);
/*      */ 
/*      */     
/*  154 */     ((Statement)stmt).setPingTarget(this);
/*      */     
/*  156 */     return stmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  167 */     Statement stmt = this.currentConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */ 
/*      */     
/*  170 */     ((Statement)stmt).setPingTarget(this);
/*      */     
/*  172 */     return stmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getAutoCommit() throws SQLException {
/*  181 */     return this.currentConnection.getAutoCommit();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getCatalog() throws SQLException {
/*  190 */     return this.currentConnection.getCatalog();
/*      */   }
/*      */   
/*      */   public synchronized Connection getCurrentConnection() {
/*  194 */     return this.currentConnection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getHoldability() throws SQLException {
/*  203 */     return this.currentConnection.getHoldability();
/*      */   }
/*      */   
/*      */   public synchronized Connection getMasterConnection() {
/*  207 */     return this.masterConnection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized DatabaseMetaData getMetaData() throws SQLException {
/*  216 */     return this.currentConnection.getMetaData();
/*      */   }
/*      */   
/*      */   public synchronized Connection getSlavesConnection() {
/*  220 */     return this.slavesConnection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getTransactionIsolation() throws SQLException {
/*  229 */     return this.currentConnection.getTransactionIsolation();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Map getTypeMap() throws SQLException {
/*  238 */     return this.currentConnection.getTypeMap();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized SQLWarning getWarnings() throws SQLException {
/*  247 */     return this.currentConnection.getWarnings();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isClosed() throws SQLException {
/*  256 */     return this.currentConnection.isClosed();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isReadOnly() throws SQLException {
/*  265 */     return (this.currentConnection == this.slavesConnection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String nativeSQL(String sql) throws SQLException {
/*  274 */     return this.currentConnection.nativeSQL(sql);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement prepareCall(String sql) throws SQLException {
/*  283 */     return this.currentConnection.prepareCall(sql);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  293 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
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
/*      */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  305 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*  315 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql);
/*      */     
/*  317 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  319 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
/*  329 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, autoGeneratedKeys);
/*      */     
/*  331 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  333 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  343 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */ 
/*      */     
/*  346 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  348 */     return pstmt;
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
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  360 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */ 
/*      */     
/*  363 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  365 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
/*  375 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, columnIndexes);
/*      */     
/*  377 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  379 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
/*  390 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, columnNames);
/*      */     
/*  392 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  394 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
/*  404 */     this.currentConnection.releaseSavepoint(savepoint);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void rollback() throws SQLException {
/*  413 */     this.currentConnection.rollback();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void rollback(Savepoint savepoint) throws SQLException {
/*  422 */     this.currentConnection.rollback(savepoint);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoCommit(boolean autoCommit) throws SQLException {
/*  432 */     this.currentConnection.setAutoCommit(autoCommit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCatalog(String catalog) throws SQLException {
/*  441 */     this.currentConnection.setCatalog(catalog);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setHoldability(int holdability) throws SQLException {
/*  451 */     this.currentConnection.setHoldability(holdability);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setReadOnly(boolean readOnly) throws SQLException {
/*  460 */     if (readOnly) {
/*  461 */       if (this.currentConnection != this.slavesConnection) {
/*  462 */         switchToSlavesConnection();
/*      */       }
/*      */     }
/*  465 */     else if (this.currentConnection != this.masterConnection) {
/*  466 */       switchToMasterConnection();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Savepoint setSavepoint() throws SQLException {
/*  477 */     return this.currentConnection.setSavepoint();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Savepoint setSavepoint(String name) throws SQLException {
/*  486 */     return this.currentConnection.setSavepoint(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTransactionIsolation(int level) throws SQLException {
/*  496 */     this.currentConnection.setTransactionIsolation(level);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTypeMap(Map arg0) throws SQLException {
/*  507 */     this.currentConnection.setTypeMap(arg0);
/*      */   }
/*      */   
/*      */   private synchronized void switchToMasterConnection() throws SQLException {
/*  511 */     swapConnections(this.masterConnection, this.slavesConnection);
/*      */   }
/*      */   
/*      */   private synchronized void switchToSlavesConnection() throws SQLException {
/*  515 */     swapConnections(this.slavesConnection, this.masterConnection);
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
/*      */   private synchronized void swapConnections(Connection switchToConnection, Connection switchFromConnection) throws SQLException {
/*  530 */     String switchFromCatalog = switchFromConnection.getCatalog();
/*  531 */     String switchToCatalog = switchToConnection.getCatalog();
/*      */     
/*  533 */     if (switchToCatalog != null && !switchToCatalog.equals(switchFromCatalog)) {
/*  534 */       switchToConnection.setCatalog(switchFromCatalog);
/*  535 */     } else if (switchFromCatalog != null) {
/*  536 */       switchToConnection.setCatalog(switchFromCatalog);
/*      */     } 
/*      */     
/*  539 */     boolean switchToAutoCommit = switchToConnection.getAutoCommit();
/*  540 */     boolean switchFromConnectionAutoCommit = switchFromConnection.getAutoCommit();
/*      */     
/*  542 */     if (switchFromConnectionAutoCommit != switchToAutoCommit) {
/*  543 */       switchToConnection.setAutoCommit(switchFromConnectionAutoCommit);
/*      */     }
/*      */     
/*  546 */     int switchToIsolation = switchToConnection.getTransactionIsolation();
/*      */ 
/*      */     
/*  549 */     int switchFromIsolation = switchFromConnection.getTransactionIsolation();
/*      */     
/*  551 */     if (switchFromIsolation != switchToIsolation) {
/*  552 */       switchToConnection.setTransactionIsolation(switchFromIsolation);
/*      */     }
/*      */ 
/*      */     
/*  556 */     this.currentConnection = switchToConnection;
/*      */   }
/*      */   
/*      */   public synchronized void doPing() throws SQLException {
/*  560 */     if (this.masterConnection != null) {
/*  561 */       this.masterConnection.ping();
/*      */     }
/*      */     
/*  564 */     if (this.slavesConnection != null) {
/*  565 */       this.slavesConnection.ping();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void changeUser(String userName, String newPassword) throws SQLException {}
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void clearHasTriedMaster() {
/*  576 */     this.masterConnection.clearHasTriedMaster();
/*  577 */     this.slavesConnection.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/*  583 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql);
/*  584 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  586 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*  591 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyIndex);
/*  592 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  594 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  599 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*  600 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  602 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*  607 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyIndexes);
/*  608 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  610 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  616 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*  617 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  619 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*  624 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyColNames);
/*  625 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  627 */     return pstmt;
/*      */   }
/*      */   
/*      */   public synchronized int getActiveStatementCount() {
/*  631 */     return this.currentConnection.getActiveStatementCount();
/*      */   }
/*      */   
/*      */   public synchronized long getIdleFor() {
/*  635 */     return this.currentConnection.getIdleFor();
/*      */   }
/*      */   
/*      */   public synchronized Log getLog() throws SQLException {
/*  639 */     return this.currentConnection.getLog();
/*      */   }
/*      */   
/*      */   public synchronized String getServerCharacterEncoding() {
/*  643 */     return this.currentConnection.getServerCharacterEncoding();
/*      */   }
/*      */   
/*      */   public synchronized TimeZone getServerTimezoneTZ() {
/*  647 */     return this.currentConnection.getServerTimezoneTZ();
/*      */   }
/*      */   
/*      */   public synchronized String getStatementComment() {
/*  651 */     return this.currentConnection.getStatementComment();
/*      */   }
/*      */   
/*      */   public synchronized boolean hasTriedMaster() {
/*  655 */     return this.currentConnection.hasTriedMaster();
/*      */   }
/*      */   
/*      */   public synchronized void initializeExtension(Extension ex) throws SQLException {
/*  659 */     this.currentConnection.initializeExtension(ex);
/*      */   }
/*      */   
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos) {
/*  663 */     return this.currentConnection.isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */   
/*      */   public synchronized boolean isInGlobalTx() {
/*  667 */     return this.currentConnection.isInGlobalTx();
/*      */   }
/*      */   
/*      */   public synchronized boolean isMasterConnection() {
/*  671 */     return this.currentConnection.isMasterConnection();
/*      */   }
/*      */   
/*      */   public synchronized boolean isNoBackslashEscapesSet() {
/*  675 */     return this.currentConnection.isNoBackslashEscapesSet();
/*      */   }
/*      */   
/*      */   public synchronized boolean lowerCaseTableNames() {
/*  679 */     return this.currentConnection.lowerCaseTableNames();
/*      */   }
/*      */   
/*      */   public synchronized boolean parserKnowsUnicode() {
/*  683 */     return this.currentConnection.parserKnowsUnicode();
/*      */   }
/*      */   
/*      */   public synchronized void ping() throws SQLException {
/*  687 */     this.masterConnection.ping();
/*  688 */     this.slavesConnection.ping();
/*      */   }
/*      */   
/*      */   public synchronized void reportQueryTime(long millisOrNanos) {
/*  692 */     this.currentConnection.reportQueryTime(millisOrNanos);
/*      */   }
/*      */   
/*      */   public synchronized void resetServerState() throws SQLException {
/*  696 */     this.currentConnection.resetServerState();
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/*  701 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql);
/*  702 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  704 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*  709 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyIndex);
/*  710 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  712 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  717 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*  718 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  720 */     return pstmt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  726 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*  727 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  729 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*  734 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyIndexes);
/*  735 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  737 */     return pstmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*  742 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyColNames);
/*  743 */     ((Statement)pstmt).setPingTarget(this);
/*      */     
/*  745 */     return pstmt;
/*      */   }
/*      */   
/*      */   public synchronized void setFailedOver(boolean flag) {
/*  749 */     this.currentConnection.setFailedOver(flag);
/*      */   }
/*      */   
/*      */   public synchronized void setPreferSlaveDuringFailover(boolean flag) {
/*  753 */     this.currentConnection.setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */   
/*      */   public synchronized void setStatementComment(String comment) {
/*  757 */     this.masterConnection.setStatementComment(comment);
/*  758 */     this.slavesConnection.setStatementComment(comment);
/*      */   }
/*      */   
/*      */   public synchronized void shutdownServer() throws SQLException {
/*  762 */     this.currentConnection.shutdownServer();
/*      */   }
/*      */   
/*      */   public synchronized boolean supportsIsolationLevel() {
/*  766 */     return this.currentConnection.supportsIsolationLevel();
/*      */   }
/*      */   
/*      */   public synchronized boolean supportsQuotedIdentifiers() {
/*  770 */     return this.currentConnection.supportsQuotedIdentifiers();
/*      */   }
/*      */   
/*      */   public synchronized boolean supportsTransactions() {
/*  774 */     return this.currentConnection.supportsTransactions();
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
/*  779 */     return this.currentConnection.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */   
/*      */   public synchronized String exposeAsXml() throws SQLException {
/*  783 */     return this.currentConnection.exposeAsXml();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAllowLoadLocalInfile() {
/*  787 */     return this.currentConnection.getAllowLoadLocalInfile();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAllowMultiQueries() {
/*  791 */     return this.currentConnection.getAllowMultiQueries();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAllowNanAndInf() {
/*  795 */     return this.currentConnection.getAllowNanAndInf();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAllowUrlInLocalInfile() {
/*  799 */     return this.currentConnection.getAllowUrlInLocalInfile();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAlwaysSendSetIsolation() {
/*  803 */     return this.currentConnection.getAlwaysSendSetIsolation();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAutoClosePStmtStreams() {
/*  807 */     return this.currentConnection.getAutoClosePStmtStreams();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAutoDeserialize() {
/*  811 */     return this.currentConnection.getAutoDeserialize();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAutoGenerateTestcaseScript() {
/*  815 */     return this.currentConnection.getAutoGenerateTestcaseScript();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAutoReconnectForPools() {
/*  819 */     return this.currentConnection.getAutoReconnectForPools();
/*      */   }
/*      */   
/*      */   public synchronized boolean getAutoSlowLog() {
/*  823 */     return this.currentConnection.getAutoSlowLog();
/*      */   }
/*      */   
/*      */   public synchronized int getBlobSendChunkSize() {
/*  827 */     return this.currentConnection.getBlobSendChunkSize();
/*      */   }
/*      */   
/*      */   public synchronized boolean getBlobsAreStrings() {
/*  831 */     return this.currentConnection.getBlobsAreStrings();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCacheCallableStatements() {
/*  835 */     return this.currentConnection.getCacheCallableStatements();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCacheCallableStmts() {
/*  839 */     return this.currentConnection.getCacheCallableStmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCachePrepStmts() {
/*  843 */     return this.currentConnection.getCachePrepStmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCachePreparedStatements() {
/*  847 */     return this.currentConnection.getCachePreparedStatements();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCacheResultSetMetadata() {
/*  851 */     return this.currentConnection.getCacheResultSetMetadata();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCacheServerConfiguration() {
/*  855 */     return this.currentConnection.getCacheServerConfiguration();
/*      */   }
/*      */   
/*      */   public synchronized int getCallableStatementCacheSize() {
/*  859 */     return this.currentConnection.getCallableStatementCacheSize();
/*      */   }
/*      */   
/*      */   public synchronized int getCallableStmtCacheSize() {
/*  863 */     return this.currentConnection.getCallableStmtCacheSize();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCapitalizeTypeNames() {
/*  867 */     return this.currentConnection.getCapitalizeTypeNames();
/*      */   }
/*      */   
/*      */   public synchronized String getCharacterSetResults() {
/*  871 */     return this.currentConnection.getCharacterSetResults();
/*      */   }
/*      */   
/*      */   public synchronized String getClientCertificateKeyStorePassword() {
/*  875 */     return this.currentConnection.getClientCertificateKeyStorePassword();
/*      */   }
/*      */   
/*      */   public synchronized String getClientCertificateKeyStoreType() {
/*  879 */     return this.currentConnection.getClientCertificateKeyStoreType();
/*      */   }
/*      */   
/*      */   public synchronized String getClientCertificateKeyStoreUrl() {
/*  883 */     return this.currentConnection.getClientCertificateKeyStoreUrl();
/*      */   }
/*      */   
/*      */   public synchronized String getClientInfoProvider() {
/*  887 */     return this.currentConnection.getClientInfoProvider();
/*      */   }
/*      */   
/*      */   public synchronized String getClobCharacterEncoding() {
/*  891 */     return this.currentConnection.getClobCharacterEncoding();
/*      */   }
/*      */   
/*      */   public synchronized boolean getClobberStreamingResults() {
/*  895 */     return this.currentConnection.getClobberStreamingResults();
/*      */   }
/*      */   
/*      */   public synchronized int getConnectTimeout() {
/*  899 */     return this.currentConnection.getConnectTimeout();
/*      */   }
/*      */   
/*      */   public synchronized String getConnectionCollation() {
/*  903 */     return this.currentConnection.getConnectionCollation();
/*      */   }
/*      */   
/*      */   public synchronized String getConnectionLifecycleInterceptors() {
/*  907 */     return this.currentConnection.getConnectionLifecycleInterceptors();
/*      */   }
/*      */   
/*      */   public synchronized boolean getContinueBatchOnError() {
/*  911 */     return this.currentConnection.getContinueBatchOnError();
/*      */   }
/*      */   
/*      */   public synchronized boolean getCreateDatabaseIfNotExist() {
/*  915 */     return this.currentConnection.getCreateDatabaseIfNotExist();
/*      */   }
/*      */   
/*      */   public synchronized int getDefaultFetchSize() {
/*  919 */     return this.currentConnection.getDefaultFetchSize();
/*      */   }
/*      */   
/*      */   public synchronized boolean getDontTrackOpenResources() {
/*  923 */     return this.currentConnection.getDontTrackOpenResources();
/*      */   }
/*      */   
/*      */   public synchronized boolean getDumpMetadataOnColumnNotFound() {
/*  927 */     return this.currentConnection.getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */   
/*      */   public synchronized boolean getDumpQueriesOnException() {
/*  931 */     return this.currentConnection.getDumpQueriesOnException();
/*      */   }
/*      */   
/*      */   public synchronized boolean getDynamicCalendars() {
/*  935 */     return this.currentConnection.getDynamicCalendars();
/*      */   }
/*      */   
/*      */   public synchronized boolean getElideSetAutoCommits() {
/*  939 */     return this.currentConnection.getElideSetAutoCommits();
/*      */   }
/*      */   
/*      */   public synchronized boolean getEmptyStringsConvertToZero() {
/*  943 */     return this.currentConnection.getEmptyStringsConvertToZero();
/*      */   }
/*      */   
/*      */   public synchronized boolean getEmulateLocators() {
/*  947 */     return this.currentConnection.getEmulateLocators();
/*      */   }
/*      */   
/*      */   public synchronized boolean getEmulateUnsupportedPstmts() {
/*  951 */     return this.currentConnection.getEmulateUnsupportedPstmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getEnablePacketDebug() {
/*  955 */     return this.currentConnection.getEnablePacketDebug();
/*      */   }
/*      */   
/*      */   public synchronized boolean getEnableQueryTimeouts() {
/*  959 */     return this.currentConnection.getEnableQueryTimeouts();
/*      */   }
/*      */   
/*      */   public synchronized String getEncoding() {
/*  963 */     return this.currentConnection.getEncoding();
/*      */   }
/*      */   
/*      */   public synchronized boolean getExplainSlowQueries() {
/*  967 */     return this.currentConnection.getExplainSlowQueries();
/*      */   }
/*      */   
/*      */   public synchronized boolean getFailOverReadOnly() {
/*  971 */     return this.currentConnection.getFailOverReadOnly();
/*      */   }
/*      */   
/*      */   public synchronized boolean getFunctionsNeverReturnBlobs() {
/*  975 */     return this.currentConnection.getFunctionsNeverReturnBlobs();
/*      */   }
/*      */   
/*      */   public synchronized boolean getGatherPerfMetrics() {
/*  979 */     return this.currentConnection.getGatherPerfMetrics();
/*      */   }
/*      */   
/*      */   public synchronized boolean getGatherPerformanceMetrics() {
/*  983 */     return this.currentConnection.getGatherPerformanceMetrics();
/*      */   }
/*      */   
/*      */   public synchronized boolean getGenerateSimpleParameterMetadata() {
/*  987 */     return this.currentConnection.getGenerateSimpleParameterMetadata();
/*      */   }
/*      */   
/*      */   public synchronized boolean getHoldResultsOpenOverStatementClose() {
/*  991 */     return this.currentConnection.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */   
/*      */   public synchronized boolean getIgnoreNonTxTables() {
/*  995 */     return this.currentConnection.getIgnoreNonTxTables();
/*      */   }
/*      */   
/*      */   public synchronized boolean getIncludeInnodbStatusInDeadlockExceptions() {
/*  999 */     return this.currentConnection.getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */   
/*      */   public synchronized int getInitialTimeout() {
/* 1003 */     return this.currentConnection.getInitialTimeout();
/*      */   }
/*      */   
/*      */   public synchronized boolean getInteractiveClient() {
/* 1007 */     return this.currentConnection.getInteractiveClient();
/*      */   }
/*      */   
/*      */   public synchronized boolean getIsInteractiveClient() {
/* 1011 */     return this.currentConnection.getIsInteractiveClient();
/*      */   }
/*      */   
/*      */   public synchronized boolean getJdbcCompliantTruncation() {
/* 1015 */     return this.currentConnection.getJdbcCompliantTruncation();
/*      */   }
/*      */   
/*      */   public synchronized boolean getJdbcCompliantTruncationForReads() {
/* 1019 */     return this.currentConnection.getJdbcCompliantTruncationForReads();
/*      */   }
/*      */   
/*      */   public synchronized String getLargeRowSizeThreshold() {
/* 1023 */     return this.currentConnection.getLargeRowSizeThreshold();
/*      */   }
/*      */   
/*      */   public synchronized String getLoadBalanceStrategy() {
/* 1027 */     return this.currentConnection.getLoadBalanceStrategy();
/*      */   }
/*      */   
/*      */   public synchronized String getLocalSocketAddress() {
/* 1031 */     return this.currentConnection.getLocalSocketAddress();
/*      */   }
/*      */   
/*      */   public synchronized int getLocatorFetchBufferSize() {
/* 1035 */     return this.currentConnection.getLocatorFetchBufferSize();
/*      */   }
/*      */   
/*      */   public synchronized boolean getLogSlowQueries() {
/* 1039 */     return this.currentConnection.getLogSlowQueries();
/*      */   }
/*      */   
/*      */   public synchronized boolean getLogXaCommands() {
/* 1043 */     return this.currentConnection.getLogXaCommands();
/*      */   }
/*      */   
/*      */   public synchronized String getLogger() {
/* 1047 */     return this.currentConnection.getLogger();
/*      */   }
/*      */   
/*      */   public synchronized String getLoggerClassName() {
/* 1051 */     return this.currentConnection.getLoggerClassName();
/*      */   }
/*      */   
/*      */   public synchronized boolean getMaintainTimeStats() {
/* 1055 */     return this.currentConnection.getMaintainTimeStats();
/*      */   }
/*      */   
/*      */   public synchronized int getMaxQuerySizeToLog() {
/* 1059 */     return this.currentConnection.getMaxQuerySizeToLog();
/*      */   }
/*      */   
/*      */   public synchronized int getMaxReconnects() {
/* 1063 */     return this.currentConnection.getMaxReconnects();
/*      */   }
/*      */   
/*      */   public synchronized int getMaxRows() {
/* 1067 */     return this.currentConnection.getMaxRows();
/*      */   }
/*      */   
/*      */   public synchronized int getMetadataCacheSize() {
/* 1071 */     return this.currentConnection.getMetadataCacheSize();
/*      */   }
/*      */   
/*      */   public synchronized int getNetTimeoutForStreamingResults() {
/* 1075 */     return this.currentConnection.getNetTimeoutForStreamingResults();
/*      */   }
/*      */   
/*      */   public synchronized boolean getNoAccessToProcedureBodies() {
/* 1079 */     return this.currentConnection.getNoAccessToProcedureBodies();
/*      */   }
/*      */   
/*      */   public synchronized boolean getNoDatetimeStringSync() {
/* 1083 */     return this.currentConnection.getNoDatetimeStringSync();
/*      */   }
/*      */   
/*      */   public synchronized boolean getNoTimezoneConversionForTimeType() {
/* 1087 */     return this.currentConnection.getNoTimezoneConversionForTimeType();
/*      */   }
/*      */   
/*      */   public synchronized boolean getNullCatalogMeansCurrent() {
/* 1091 */     return this.currentConnection.getNullCatalogMeansCurrent();
/*      */   }
/*      */   
/*      */   public synchronized boolean getNullNamePatternMatchesAll() {
/* 1095 */     return this.currentConnection.getNullNamePatternMatchesAll();
/*      */   }
/*      */   
/*      */   public synchronized boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 1099 */     return this.currentConnection.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */   
/*      */   public synchronized int getPacketDebugBufferSize() {
/* 1103 */     return this.currentConnection.getPacketDebugBufferSize();
/*      */   }
/*      */   
/*      */   public synchronized boolean getPadCharsWithSpace() {
/* 1107 */     return this.currentConnection.getPadCharsWithSpace();
/*      */   }
/*      */   
/*      */   public synchronized boolean getParanoid() {
/* 1111 */     return this.currentConnection.getParanoid();
/*      */   }
/*      */   
/*      */   public synchronized boolean getPedantic() {
/* 1115 */     return this.currentConnection.getPedantic();
/*      */   }
/*      */   
/*      */   public synchronized boolean getPinGlobalTxToPhysicalConnection() {
/* 1119 */     return this.currentConnection.getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */   
/*      */   public synchronized boolean getPopulateInsertRowWithDefaultValues() {
/* 1123 */     return this.currentConnection.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */   
/*      */   public synchronized int getPrepStmtCacheSize() {
/* 1127 */     return this.currentConnection.getPrepStmtCacheSize();
/*      */   }
/*      */   
/*      */   public synchronized int getPrepStmtCacheSqlLimit() {
/* 1131 */     return this.currentConnection.getPrepStmtCacheSqlLimit();
/*      */   }
/*      */   
/*      */   public synchronized int getPreparedStatementCacheSize() {
/* 1135 */     return this.currentConnection.getPreparedStatementCacheSize();
/*      */   }
/*      */   
/*      */   public synchronized int getPreparedStatementCacheSqlLimit() {
/* 1139 */     return this.currentConnection.getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */   
/*      */   public synchronized boolean getProcessEscapeCodesForPrepStmts() {
/* 1143 */     return this.currentConnection.getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getProfileSQL() {
/* 1147 */     return this.currentConnection.getProfileSQL();
/*      */   }
/*      */   
/*      */   public synchronized boolean getProfileSql() {
/* 1151 */     return this.currentConnection.getProfileSql();
/*      */   }
/*      */   
/*      */   public synchronized String getProfilerEventHandler() {
/* 1155 */     return this.currentConnection.getProfilerEventHandler();
/*      */   }
/*      */   
/*      */   public synchronized String getPropertiesTransform() {
/* 1159 */     return this.currentConnection.getPropertiesTransform();
/*      */   }
/*      */   
/*      */   public synchronized int getQueriesBeforeRetryMaster() {
/* 1163 */     return this.currentConnection.getQueriesBeforeRetryMaster();
/*      */   }
/*      */   
/*      */   public synchronized boolean getReconnectAtTxEnd() {
/* 1167 */     return this.currentConnection.getReconnectAtTxEnd();
/*      */   }
/*      */   
/*      */   public synchronized boolean getRelaxAutoCommit() {
/* 1171 */     return this.currentConnection.getRelaxAutoCommit();
/*      */   }
/*      */   
/*      */   public synchronized int getReportMetricsIntervalMillis() {
/* 1175 */     return this.currentConnection.getReportMetricsIntervalMillis();
/*      */   }
/*      */   
/*      */   public synchronized boolean getRequireSSL() {
/* 1179 */     return this.currentConnection.getRequireSSL();
/*      */   }
/*      */   
/*      */   public synchronized String getResourceId() {
/* 1183 */     return this.currentConnection.getResourceId();
/*      */   }
/*      */   
/*      */   public synchronized int getResultSetSizeThreshold() {
/* 1187 */     return this.currentConnection.getResultSetSizeThreshold();
/*      */   }
/*      */   
/*      */   public synchronized boolean getRewriteBatchedStatements() {
/* 1191 */     return this.currentConnection.getRewriteBatchedStatements();
/*      */   }
/*      */   
/*      */   public synchronized boolean getRollbackOnPooledClose() {
/* 1195 */     return this.currentConnection.getRollbackOnPooledClose();
/*      */   }
/*      */   
/*      */   public synchronized boolean getRoundRobinLoadBalance() {
/* 1199 */     return this.currentConnection.getRoundRobinLoadBalance();
/*      */   }
/*      */   
/*      */   public synchronized boolean getRunningCTS13() {
/* 1203 */     return this.currentConnection.getRunningCTS13();
/*      */   }
/*      */   
/*      */   public synchronized int getSecondsBeforeRetryMaster() {
/* 1207 */     return this.currentConnection.getSecondsBeforeRetryMaster();
/*      */   }
/*      */   
/*      */   public synchronized int getSelfDestructOnPingMaxOperations() {
/* 1211 */     return this.currentConnection.getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */   
/*      */   public synchronized int getSelfDestructOnPingSecondsLifetime() {
/* 1215 */     return this.currentConnection.getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */   
/*      */   public synchronized String getServerTimezone() {
/* 1219 */     return this.currentConnection.getServerTimezone();
/*      */   }
/*      */   
/*      */   public synchronized String getSessionVariables() {
/* 1223 */     return this.currentConnection.getSessionVariables();
/*      */   }
/*      */   
/*      */   public synchronized int getSlowQueryThresholdMillis() {
/* 1227 */     return this.currentConnection.getSlowQueryThresholdMillis();
/*      */   }
/*      */   
/*      */   public synchronized long getSlowQueryThresholdNanos() {
/* 1231 */     return this.currentConnection.getSlowQueryThresholdNanos();
/*      */   }
/*      */   
/*      */   public synchronized String getSocketFactory() {
/* 1235 */     return this.currentConnection.getSocketFactory();
/*      */   }
/*      */   
/*      */   public synchronized String getSocketFactoryClassName() {
/* 1239 */     return this.currentConnection.getSocketFactoryClassName();
/*      */   }
/*      */   
/*      */   public synchronized int getSocketTimeout() {
/* 1243 */     return this.currentConnection.getSocketTimeout();
/*      */   }
/*      */   
/*      */   public synchronized String getStatementInterceptors() {
/* 1247 */     return this.currentConnection.getStatementInterceptors();
/*      */   }
/*      */   
/*      */   public synchronized boolean getStrictFloatingPoint() {
/* 1251 */     return this.currentConnection.getStrictFloatingPoint();
/*      */   }
/*      */   
/*      */   public synchronized boolean getStrictUpdates() {
/* 1255 */     return this.currentConnection.getStrictUpdates();
/*      */   }
/*      */   
/*      */   public synchronized boolean getTcpKeepAlive() {
/* 1259 */     return this.currentConnection.getTcpKeepAlive();
/*      */   }
/*      */   
/*      */   public synchronized boolean getTcpNoDelay() {
/* 1263 */     return this.currentConnection.getTcpNoDelay();
/*      */   }
/*      */   
/*      */   public synchronized int getTcpRcvBuf() {
/* 1267 */     return this.currentConnection.getTcpRcvBuf();
/*      */   }
/*      */   
/*      */   public synchronized int getTcpSndBuf() {
/* 1271 */     return this.currentConnection.getTcpSndBuf();
/*      */   }
/*      */   
/*      */   public synchronized int getTcpTrafficClass() {
/* 1275 */     return this.currentConnection.getTcpTrafficClass();
/*      */   }
/*      */   
/*      */   public synchronized boolean getTinyInt1isBit() {
/* 1279 */     return this.currentConnection.getTinyInt1isBit();
/*      */   }
/*      */   
/*      */   public synchronized boolean getTraceProtocol() {
/* 1283 */     return this.currentConnection.getTraceProtocol();
/*      */   }
/*      */   
/*      */   public synchronized boolean getTransformedBitIsBoolean() {
/* 1287 */     return this.currentConnection.getTransformedBitIsBoolean();
/*      */   }
/*      */   
/*      */   public synchronized boolean getTreatUtilDateAsTimestamp() {
/* 1291 */     return this.currentConnection.getTreatUtilDateAsTimestamp();
/*      */   }
/*      */   
/*      */   public synchronized String getTrustCertificateKeyStorePassword() {
/* 1295 */     return this.currentConnection.getTrustCertificateKeyStorePassword();
/*      */   }
/*      */   
/*      */   public synchronized String getTrustCertificateKeyStoreType() {
/* 1299 */     return this.currentConnection.getTrustCertificateKeyStoreType();
/*      */   }
/*      */   
/*      */   public synchronized String getTrustCertificateKeyStoreUrl() {
/* 1303 */     return this.currentConnection.getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUltraDevHack() {
/* 1307 */     return this.currentConnection.getUltraDevHack();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 1311 */     return this.currentConnection.getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseCompression() {
/* 1315 */     return this.currentConnection.getUseCompression();
/*      */   }
/*      */   
/*      */   public synchronized String getUseConfigs() {
/* 1319 */     return this.currentConnection.getUseConfigs();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseCursorFetch() {
/* 1323 */     return this.currentConnection.getUseCursorFetch();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseDirectRowUnpack() {
/* 1327 */     return this.currentConnection.getUseDirectRowUnpack();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseDynamicCharsetInfo() {
/* 1331 */     return this.currentConnection.getUseDynamicCharsetInfo();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseFastDateParsing() {
/* 1335 */     return this.currentConnection.getUseFastDateParsing();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseFastIntParsing() {
/* 1339 */     return this.currentConnection.getUseFastIntParsing();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseGmtMillisForDatetimes() {
/* 1343 */     return this.currentConnection.getUseGmtMillisForDatetimes();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseHostsInPrivileges() {
/* 1347 */     return this.currentConnection.getUseHostsInPrivileges();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseInformationSchema() {
/* 1351 */     return this.currentConnection.getUseInformationSchema();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseJDBCCompliantTimezoneShift() {
/* 1355 */     return this.currentConnection.getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseJvmCharsetConverters() {
/* 1359 */     return this.currentConnection.getUseJvmCharsetConverters();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseLegacyDatetimeCode() {
/* 1363 */     return this.currentConnection.getUseLegacyDatetimeCode();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseLocalSessionState() {
/* 1367 */     return this.currentConnection.getUseLocalSessionState();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseNanosForElapsedTime() {
/* 1371 */     return this.currentConnection.getUseNanosForElapsedTime();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseOldAliasMetadataBehavior() {
/* 1375 */     return this.currentConnection.getUseOldAliasMetadataBehavior();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseOldUTF8Behavior() {
/* 1379 */     return this.currentConnection.getUseOldUTF8Behavior();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseOnlyServerErrorMessages() {
/* 1383 */     return this.currentConnection.getUseOnlyServerErrorMessages();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseReadAheadInput() {
/* 1387 */     return this.currentConnection.getUseReadAheadInput();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseSSL() {
/* 1391 */     return this.currentConnection.getUseSSL();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseSSPSCompatibleTimezoneShift() {
/* 1395 */     return this.currentConnection.getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseServerPrepStmts() {
/* 1399 */     return this.currentConnection.getUseServerPrepStmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseServerPreparedStmts() {
/* 1403 */     return this.currentConnection.getUseServerPreparedStmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseSqlStateCodes() {
/* 1407 */     return this.currentConnection.getUseSqlStateCodes();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseStreamLengthsInPrepStmts() {
/* 1411 */     return this.currentConnection.getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseTimezone() {
/* 1415 */     return this.currentConnection.getUseTimezone();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseUltraDevWorkAround() {
/* 1419 */     return this.currentConnection.getUseUltraDevWorkAround();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseUnbufferedInput() {
/* 1423 */     return this.currentConnection.getUseUnbufferedInput();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseUnicode() {
/* 1427 */     return this.currentConnection.getUseUnicode();
/*      */   }
/*      */   
/*      */   public synchronized boolean getUseUsageAdvisor() {
/* 1431 */     return this.currentConnection.getUseUsageAdvisor();
/*      */   }
/*      */   
/*      */   public synchronized String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 1435 */     return this.currentConnection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */   
/*      */   public synchronized String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 1439 */     return this.currentConnection.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */   
/*      */   public synchronized boolean getVerifyServerCertificate() {
/* 1443 */     return this.currentConnection.getVerifyServerCertificate();
/*      */   }
/*      */   
/*      */   public synchronized boolean getYearIsDateType() {
/* 1447 */     return this.currentConnection.getYearIsDateType();
/*      */   }
/*      */   
/*      */   public synchronized String getZeroDateTimeBehavior() {
/* 1451 */     return this.currentConnection.getZeroDateTimeBehavior();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAllowLoadLocalInfile(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAllowMultiQueries(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAllowNanAndInf(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAllowUrlInLocalInfile(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAlwaysSendSetIsolation(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoClosePStmtStreams(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoDeserialize(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoGenerateTestcaseScript(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoReconnect(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoReconnectForConnectionPools(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoReconnectForPools(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setAutoSlowLog(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setBlobSendChunkSize(String value) throws SQLException {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setBlobsAreStrings(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCacheCallableStatements(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCacheCallableStmts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCachePrepStmts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCachePreparedStatements(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCacheResultSetMetadata(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCacheServerConfiguration(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCallableStatementCacheSize(int size) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCallableStmtCacheSize(int cacheSize) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCapitalizeDBMDTypes(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCapitalizeTypeNames(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCharacterEncoding(String encoding) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCharacterSetResults(String characterSet) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setClientCertificateKeyStorePassword(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setClientCertificateKeyStoreType(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setClientCertificateKeyStoreUrl(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setClientInfoProvider(String classname) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setClobCharacterEncoding(String encoding) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setClobberStreamingResults(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setConnectTimeout(int timeoutMs) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setConnectionCollation(String collation) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setConnectionLifecycleInterceptors(String interceptors) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setContinueBatchOnError(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setCreateDatabaseIfNotExist(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setDefaultFetchSize(int n) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setDetectServerPreparedStmts(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setDontTrackOpenResources(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setDumpMetadataOnColumnNotFound(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setDumpQueriesOnException(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setDynamicCalendars(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setElideSetAutoCommits(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setEmptyStringsConvertToZero(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setEmulateLocators(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setEmulateUnsupportedPstmts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setEnablePacketDebug(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setEnableQueryTimeouts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setEncoding(String property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setExplainSlowQueries(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFailOverReadOnly(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFunctionsNeverReturnBlobs(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setGatherPerfMetrics(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setGatherPerformanceMetrics(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setGenerateSimpleParameterMetadata(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setHoldResultsOpenOverStatementClose(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setIgnoreNonTxTables(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setInitialTimeout(int property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setInteractiveClient(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setIsInteractiveClient(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setJdbcCompliantTruncation(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLargeRowSizeThreshold(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLoadBalanceStrategy(String strategy) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLocalSocketAddress(String address) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLocatorFetchBufferSize(String value) throws SQLException {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLogSlowQueries(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLogXaCommands(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLogger(String property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLoggerClassName(String className) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setMaintainTimeStats(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setMaxQuerySizeToLog(int sizeInBytes) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setMaxReconnects(int property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setMaxRows(int property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setMetadataCacheSize(int value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNetTimeoutForStreamingResults(int value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNoAccessToProcedureBodies(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNoDatetimeStringSync(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNoTimezoneConversionForTimeType(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNullCatalogMeansCurrent(boolean value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNullNamePatternMatchesAll(boolean value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPacketDebugBufferSize(int size) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPadCharsWithSpace(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setParanoid(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPedantic(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPinGlobalTxToPhysicalConnection(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPopulateInsertRowWithDefaultValues(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPrepStmtCacheSize(int cacheSize) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPrepStmtCacheSqlLimit(int sqlLimit) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPreparedStatementCacheSize(int cacheSize) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setProcessEscapeCodesForPrepStmts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setProfileSQL(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setProfileSql(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setProfilerEventHandler(String handler) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPropertiesTransform(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setQueriesBeforeRetryMaster(int property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setReconnectAtTxEnd(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRelaxAutoCommit(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setReportMetricsIntervalMillis(int millis) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRequireSSL(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setResourceId(String resourceId) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setResultSetSizeThreshold(int threshold) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRetainStatementAfterResultSetClose(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRewriteBatchedStatements(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRollbackOnPooledClose(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRoundRobinLoadBalance(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRunningCTS13(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSecondsBeforeRetryMaster(int property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSelfDestructOnPingMaxOperations(int maxOperations) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSelfDestructOnPingSecondsLifetime(int seconds) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setServerTimezone(String property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSessionVariables(String variables) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSlowQueryThresholdMillis(int millis) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSlowQueryThresholdNanos(long nanos) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSocketFactory(String name) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSocketFactoryClassName(String property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSocketTimeout(int property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setStatementInterceptors(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setStrictFloatingPoint(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setStrictUpdates(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTcpKeepAlive(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTcpNoDelay(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTcpRcvBuf(int bufSize) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTcpSndBuf(int bufSize) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTcpTrafficClass(int classFlags) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTinyInt1isBit(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTraceProtocol(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTransformedBitIsBoolean(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTreatUtilDateAsTimestamp(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTrustCertificateKeyStorePassword(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTrustCertificateKeyStoreType(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setTrustCertificateKeyStoreUrl(String value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUltraDevHack(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseCompression(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseConfigs(String configs) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseCursorFetch(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseDirectRowUnpack(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseDynamicCharsetInfo(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseFastDateParsing(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseFastIntParsing(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseGmtMillisForDatetimes(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseHostsInPrivileges(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseInformationSchema(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseJDBCCompliantTimezoneShift(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseJvmCharsetConverters(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseLegacyDatetimeCode(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseLocalSessionState(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseNanosForElapsedTime(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseOldAliasMetadataBehavior(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseOldUTF8Behavior(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseOnlyServerErrorMessages(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseReadAheadInput(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseSSL(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseSSPSCompatibleTimezoneShift(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseServerPrepStmts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseServerPreparedStmts(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseSqlStateCodes(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseStreamLengthsInPrepStmts(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseTimezone(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseUltraDevWorkAround(boolean property) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseUnbufferedInput(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseUnicode(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setVerifyServerCertificate(boolean flag) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setYearIsDateType(boolean flag) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setZeroDateTimeBehavior(String behavior) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean useUnbufferedInput() {
/* 2321 */     return this.currentConnection.useUnbufferedInput();
/*      */   }
/*      */   
/*      */   public synchronized boolean isSameResource(Connection c) {
/* 2325 */     return this.currentConnection.isSameResource(c);
/*      */   }
/*      */   
/*      */   public void setInGlobalTx(boolean flag) {
/* 2329 */     this.currentConnection.setInGlobalTx(flag);
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ReplicationConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */