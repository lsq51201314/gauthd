/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Statement;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
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
/*     */ public class StatementWrapper
/*     */   extends WrapperBase
/*     */   implements Statement
/*     */ {
/*     */   private static final Constructor JDBC_4_STATEMENT_WRAPPER_CTOR;
/*     */   protected Statement wrappedStmt;
/*     */   protected ConnectionWrapper wrappedConn;
/*     */   
/*     */   static {
/*  50 */     if (Util.isJdbc4()) {
/*     */       try {
/*  52 */         JDBC_4_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4StatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, Statement.class });
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*  57 */       catch (SecurityException e) {
/*  58 */         throw new RuntimeException(e);
/*  59 */       } catch (NoSuchMethodException e) {
/*  60 */         throw new RuntimeException(e);
/*  61 */       } catch (ClassNotFoundException e) {
/*  62 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/*  65 */       JDBC_4_STATEMENT_WRAPPER_CTOR = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) throws SQLException {
/*  72 */     if (!Util.isJdbc4()) {
/*  73 */       return new StatementWrapper(c, conn, toWrap);
/*     */     }
/*     */ 
/*     */     
/*  77 */     return (StatementWrapper)Util.handleNewInstance(JDBC_4_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap });
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
/*     */   public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
/*  89 */     this.pooledConnection = conn;
/*  90 */     this.wrappedStmt = toWrap;
/*  91 */     this.wrappedConn = c;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*     */     try {
/* 101 */       if (this.wrappedStmt != null) {
/* 102 */         return (Connection)this.wrappedConn;
/*     */       }
/*     */       
/* 105 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 107 */     catch (SQLException sqlEx) {
/* 108 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 111 */       return null;
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
/*     */   public void setCursorName(String name) throws SQLException {
/*     */     try {
/* 124 */       if (this.wrappedStmt != null) {
/* 125 */         this.wrappedStmt.setCursorName(name);
/*     */       } else {
/* 127 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 130 */     } catch (SQLException sqlEx) {
/* 131 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEscapeProcessing(boolean enable) throws SQLException {
/*     */     try {
/* 142 */       if (this.wrappedStmt != null) {
/* 143 */         this.wrappedStmt.setEscapeProcessing(enable);
/*     */       } else {
/* 145 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 148 */     } catch (SQLException sqlEx) {
/* 149 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFetchDirection(int direction) throws SQLException {
/*     */     try {
/* 160 */       if (this.wrappedStmt != null) {
/* 161 */         this.wrappedStmt.setFetchDirection(direction);
/*     */       } else {
/* 163 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 166 */     } catch (SQLException sqlEx) {
/* 167 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFetchDirection() throws SQLException {
/*     */     try {
/* 178 */       if (this.wrappedStmt != null) {
/* 179 */         return this.wrappedStmt.getFetchDirection();
/*     */       }
/*     */       
/* 182 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 184 */     catch (SQLException sqlEx) {
/* 185 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 188 */       return 1000;
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
/*     */   public void setFetchSize(int rows) throws SQLException {
/*     */     try {
/* 201 */       if (this.wrappedStmt != null) {
/* 202 */         this.wrappedStmt.setFetchSize(rows);
/*     */       } else {
/* 204 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 207 */     } catch (SQLException sqlEx) {
/* 208 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFetchSize() throws SQLException {
/*     */     try {
/* 219 */       if (this.wrappedStmt != null) {
/* 220 */         return this.wrappedStmt.getFetchSize();
/*     */       }
/*     */       
/* 223 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 225 */     catch (SQLException sqlEx) {
/* 226 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 229 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getGeneratedKeys() throws SQLException {
/*     */     try {
/* 241 */       if (this.wrappedStmt != null) {
/* 242 */         return this.wrappedStmt.getGeneratedKeys();
/*     */       }
/*     */       
/* 245 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 247 */     catch (SQLException sqlEx) {
/* 248 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 251 */       return null;
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
/*     */   public void setMaxFieldSize(int max) throws SQLException {
/*     */     try {
/* 264 */       if (this.wrappedStmt != null) {
/* 265 */         this.wrappedStmt.setMaxFieldSize(max);
/*     */       } else {
/* 267 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 270 */     } catch (SQLException sqlEx) {
/* 271 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxFieldSize() throws SQLException {
/*     */     try {
/* 282 */       if (this.wrappedStmt != null) {
/* 283 */         return this.wrappedStmt.getMaxFieldSize();
/*     */       }
/*     */       
/* 286 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 288 */     catch (SQLException sqlEx) {
/* 289 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 292 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxRows(int max) throws SQLException {
/*     */     try {
/* 304 */       if (this.wrappedStmt != null) {
/* 305 */         this.wrappedStmt.setMaxRows(max);
/*     */       } else {
/* 307 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 310 */     } catch (SQLException sqlEx) {
/* 311 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/*     */     try {
/* 322 */       if (this.wrappedStmt != null) {
/* 323 */         return this.wrappedStmt.getMaxRows();
/*     */       }
/*     */       
/* 326 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 328 */     catch (SQLException sqlEx) {
/* 329 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 332 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getMoreResults() throws SQLException {
/*     */     try {
/* 344 */       if (this.wrappedStmt != null) {
/* 345 */         return this.wrappedStmt.getMoreResults();
/*     */       }
/*     */       
/* 348 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 350 */     catch (SQLException sqlEx) {
/* 351 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 354 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getMoreResults(int current) throws SQLException {
/*     */     try {
/* 364 */       if (this.wrappedStmt != null) {
/* 365 */         return this.wrappedStmt.getMoreResults(current);
/*     */       }
/*     */       
/* 368 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 370 */     catch (SQLException sqlEx) {
/* 371 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 374 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQueryTimeout(int seconds) throws SQLException {
/*     */     try {
/* 384 */       if (this.wrappedStmt != null) {
/* 385 */         this.wrappedStmt.setQueryTimeout(seconds);
/*     */       } else {
/* 387 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 390 */     } catch (SQLException sqlEx) {
/* 391 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getQueryTimeout() throws SQLException {
/*     */     try {
/* 402 */       if (this.wrappedStmt != null) {
/* 403 */         return this.wrappedStmt.getQueryTimeout();
/*     */       }
/*     */       
/* 406 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 408 */     catch (SQLException sqlEx) {
/* 409 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 412 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/*     */     try {
/* 422 */       if (this.wrappedStmt != null) {
/* 423 */         ResultSet rs = this.wrappedStmt.getResultSet();
/*     */         
/* 425 */         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */         
/* 427 */         return rs;
/*     */       } 
/*     */       
/* 430 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 432 */     catch (SQLException sqlEx) {
/* 433 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 436 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getResultSetConcurrency() throws SQLException {
/*     */     try {
/* 446 */       if (this.wrappedStmt != null) {
/* 447 */         return this.wrappedStmt.getResultSetConcurrency();
/*     */       }
/*     */       
/* 450 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 452 */     catch (SQLException sqlEx) {
/* 453 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 456 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getResultSetHoldability() throws SQLException {
/*     */     try {
/* 466 */       if (this.wrappedStmt != null) {
/* 467 */         return this.wrappedStmt.getResultSetHoldability();
/*     */       }
/*     */       
/* 470 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 472 */     catch (SQLException sqlEx) {
/* 473 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 476 */       return 1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getResultSetType() throws SQLException {
/*     */     try {
/* 486 */       if (this.wrappedStmt != null) {
/* 487 */         return this.wrappedStmt.getResultSetType();
/*     */       }
/*     */       
/* 490 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 492 */     catch (SQLException sqlEx) {
/* 493 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 496 */       return 1003;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUpdateCount() throws SQLException {
/*     */     try {
/* 506 */       if (this.wrappedStmt != null) {
/* 507 */         return this.wrappedStmt.getUpdateCount();
/*     */       }
/*     */       
/* 510 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 512 */     catch (SQLException sqlEx) {
/* 513 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 516 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/*     */     try {
/* 526 */       if (this.wrappedStmt != null) {
/* 527 */         return this.wrappedStmt.getWarnings();
/*     */       }
/*     */       
/* 530 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 532 */     catch (SQLException sqlEx) {
/* 533 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 536 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addBatch(String sql) throws SQLException {
/*     */     try {
/* 546 */       if (this.wrappedStmt != null) {
/* 547 */         this.wrappedStmt.addBatch(sql);
/*     */       }
/* 549 */     } catch (SQLException sqlEx) {
/* 550 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel() throws SQLException {
/*     */     try {
/* 561 */       if (this.wrappedStmt != null) {
/* 562 */         this.wrappedStmt.cancel();
/*     */       }
/* 564 */     } catch (SQLException sqlEx) {
/* 565 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearBatch() throws SQLException {
/*     */     try {
/* 576 */       if (this.wrappedStmt != null) {
/* 577 */         this.wrappedStmt.clearBatch();
/*     */       }
/* 579 */     } catch (SQLException sqlEx) {
/* 580 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/*     */     try {
/* 591 */       if (this.wrappedStmt != null) {
/* 592 */         this.wrappedStmt.clearWarnings();
/*     */       }
/* 594 */     } catch (SQLException sqlEx) {
/* 595 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/*     */     try {
/* 606 */       if (this.wrappedStmt != null) {
/* 607 */         this.wrappedStmt.close();
/*     */       }
/* 609 */     } catch (SQLException sqlEx) {
/* 610 */       checkAndFireConnectionError(sqlEx);
/*     */     } finally {
/* 612 */       this.wrappedStmt = null;
/* 613 */       this.pooledConnection = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 625 */       if (this.wrappedStmt != null) {
/* 626 */         return this.wrappedStmt.execute(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 629 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 631 */     catch (SQLException sqlEx) {
/* 632 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 635 */       return false;
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
/*     */   public boolean execute(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 648 */       if (this.wrappedStmt != null) {
/* 649 */         return this.wrappedStmt.execute(sql, columnIndexes);
/*     */       }
/*     */       
/* 652 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 654 */     catch (SQLException sqlEx) {
/* 655 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 658 */       return false;
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
/*     */   public boolean execute(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 672 */       if (this.wrappedStmt != null) {
/* 673 */         return this.wrappedStmt.execute(sql, columnNames);
/*     */       }
/*     */       
/* 676 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 678 */     catch (SQLException sqlEx) {
/* 679 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 682 */       return false;
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
/*     */   public boolean execute(String sql) throws SQLException {
/*     */     try {
/* 695 */       if (this.wrappedStmt != null) {
/* 696 */         return this.wrappedStmt.execute(sql);
/*     */       }
/*     */       
/* 699 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 701 */     catch (SQLException sqlEx) {
/* 702 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 705 */       return false;
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
/*     */   public int[] executeBatch() throws SQLException {
/*     */     try {
/* 718 */       if (this.wrappedStmt != null) {
/* 719 */         return this.wrappedStmt.executeBatch();
/*     */       }
/*     */       
/* 722 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 724 */     catch (SQLException sqlEx) {
/* 725 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 728 */       return null;
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
/*     */   public ResultSet executeQuery(String sql) throws SQLException {
/*     */     try {
/* 741 */       if (this.wrappedStmt != null) {
/*     */         
/* 743 */         ResultSet rs = this.wrappedStmt.executeQuery(sql);
/* 744 */         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */         
/* 746 */         return rs;
/*     */       } 
/*     */       
/* 749 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 751 */     catch (SQLException sqlEx) {
/* 752 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 755 */       return null;
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
/*     */   public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 769 */       if (this.wrappedStmt != null) {
/* 770 */         return this.wrappedStmt.executeUpdate(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 773 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 775 */     catch (SQLException sqlEx) {
/* 776 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 779 */       return -1;
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
/*     */   public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 792 */       if (this.wrappedStmt != null) {
/* 793 */         return this.wrappedStmt.executeUpdate(sql, columnIndexes);
/*     */       }
/*     */       
/* 796 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 798 */     catch (SQLException sqlEx) {
/* 799 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 802 */       return -1;
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
/*     */   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 816 */       if (this.wrappedStmt != null) {
/* 817 */         return this.wrappedStmt.executeUpdate(sql, columnNames);
/*     */       }
/*     */       
/* 820 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 822 */     catch (SQLException sqlEx) {
/* 823 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 826 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int executeUpdate(String sql) throws SQLException {
/*     */     try {
/* 838 */       if (this.wrappedStmt != null) {
/* 839 */         return this.wrappedStmt.executeUpdate(sql);
/*     */       }
/*     */       
/* 842 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     }
/* 844 */     catch (SQLException sqlEx) {
/* 845 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 848 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void enableStreamingResults() throws SQLException {
/*     */     try {
/* 855 */       if (this.wrappedStmt != null) {
/* 856 */         ((Statement)this.wrappedStmt).enableStreamingResults();
/*     */       } else {
/*     */         
/* 859 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 863 */     catch (SQLException sqlEx) {
/* 864 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\StatementWrapper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */