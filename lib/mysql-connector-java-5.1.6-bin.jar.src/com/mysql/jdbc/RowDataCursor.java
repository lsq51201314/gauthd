/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RowDataCursor
/*     */   implements RowData
/*     */ {
/*     */   private static final int BEFORE_START_OF_ROWS = -1;
/*     */   private List fetchedRows;
/*  49 */   private int currentPositionInEntireResult = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   private int currentPositionInFetchedRows = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResultSetImpl owner;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean lastRowFetched = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Field[] metadata;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MysqlIO mysql;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long statementIdOnServer;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ServerPreparedStatement prepStmt;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SERVER_STATUS_LAST_ROW_SENT = 128;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean firstFetchCompleted = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean wasEmpty = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean useBufferRowExplicit = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowDataCursor(MysqlIO ioChannel, ServerPreparedStatement creatingStatement, Field[] metadata) {
/* 116 */     this.currentPositionInEntireResult = -1;
/* 117 */     this.metadata = metadata;
/* 118 */     this.mysql = ioChannel;
/* 119 */     this.statementIdOnServer = creatingStatement.getServerStatementId();
/* 120 */     this.prepStmt = creatingStatement;
/* 121 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 131 */     return (this.lastRowFetched && this.currentPositionInFetchedRows > this.fetchedRows.size());
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
/*     */   public ResultSetRow getAt(int ind) throws SQLException {
/* 145 */     notSupported();
/*     */     
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBeforeFirst() throws SQLException {
/* 158 */     return (this.currentPositionInEntireResult < 0);
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
/*     */   public void setCurrentRow(int rowNumber) throws SQLException {
/* 170 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCurrentRowNumber() throws SQLException {
/* 181 */     return this.currentPositionInEntireResult + 1;
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
/*     */   public boolean isDynamic() {
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() throws SQLException {
/* 204 */     return (isBeforeFirst() && isAfterLast());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFirst() throws SQLException {
/* 215 */     return (this.currentPositionInEntireResult == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLast() throws SQLException {
/* 226 */     return (this.lastRowFetched && this.currentPositionInFetchedRows == this.fetchedRows.size() - 1);
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
/*     */   public void addRow(ResultSetRow row) throws SQLException {
/* 240 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterLast() throws SQLException {
/* 250 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeFirst() throws SQLException {
/* 260 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeLast() throws SQLException {
/* 270 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/* 281 */     this.metadata = null;
/* 282 */     this.owner = null;
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
/*     */   public boolean hasNext() throws SQLException {
/* 294 */     if (this.fetchedRows != null && this.fetchedRows.size() == 0) {
/* 295 */       return false;
/*     */     }
/*     */     
/* 298 */     if (this.owner != null && this.owner.owningStatement != null) {
/* 299 */       int maxRows = this.owner.owningStatement.maxRows;
/*     */       
/* 301 */       if (maxRows != -1 && this.currentPositionInEntireResult + 1 > maxRows) {
/* 302 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 306 */     if (this.currentPositionInEntireResult != -1) {
/*     */ 
/*     */       
/* 309 */       if (this.currentPositionInFetchedRows < this.fetchedRows.size() - 1)
/* 310 */         return true; 
/* 311 */       if (this.currentPositionInFetchedRows == this.fetchedRows.size() && this.lastRowFetched)
/*     */       {
/*     */         
/* 314 */         return false;
/*     */       }
/*     */       
/* 317 */       fetchMoreRows();
/*     */       
/* 319 */       return (this.fetchedRows.size() > 0);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 325 */     fetchMoreRows();
/*     */     
/* 327 */     return (this.fetchedRows.size() > 0);
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
/*     */   public void moveRowRelative(int rows) throws SQLException {
/* 339 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetRow next() throws SQLException {
/* 350 */     if (this.fetchedRows == null && this.currentPositionInEntireResult != -1) {
/* 351 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 357 */     if (!hasNext()) {
/* 358 */       return null;
/*     */     }
/*     */     
/* 361 */     this.currentPositionInEntireResult++;
/* 362 */     this.currentPositionInFetchedRows++;
/*     */ 
/*     */     
/* 365 */     if (this.fetchedRows != null && this.fetchedRows.size() == 0) {
/* 366 */       return null;
/*     */     }
/*     */     
/* 369 */     if (this.currentPositionInFetchedRows > this.fetchedRows.size() - 1) {
/* 370 */       fetchMoreRows();
/* 371 */       this.currentPositionInFetchedRows = 0;
/*     */     } 
/*     */     
/* 374 */     ResultSetRow row = this.fetchedRows.get(this.currentPositionInFetchedRows);
/*     */ 
/*     */     
/* 377 */     row.setMetadata(this.metadata);
/*     */     
/* 379 */     return row;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fetchMoreRows() throws SQLException {
/* 386 */     if (this.lastRowFetched) {
/* 387 */       this.fetchedRows = new ArrayList(0);
/*     */       
/*     */       return;
/*     */     } 
/* 391 */     synchronized (this.owner.connection.getMutex()) {
/* 392 */       boolean oldFirstFetchCompleted = this.firstFetchCompleted;
/*     */       
/* 394 */       if (!this.firstFetchCompleted) {
/* 395 */         this.firstFetchCompleted = true;
/*     */       }
/*     */       
/* 398 */       int numRowsToFetch = this.owner.getFetchSize();
/*     */       
/* 400 */       if (numRowsToFetch == 0) {
/* 401 */         numRowsToFetch = this.prepStmt.getFetchSize();
/*     */       }
/*     */       
/* 404 */       if (numRowsToFetch == Integer.MIN_VALUE)
/*     */       {
/*     */ 
/*     */         
/* 408 */         numRowsToFetch = 1;
/*     */       }
/*     */       
/* 411 */       this.fetchedRows = this.mysql.fetchRowsViaCursor(this.fetchedRows, this.statementIdOnServer, this.metadata, numRowsToFetch, this.useBufferRowExplicit);
/*     */ 
/*     */       
/* 414 */       this.currentPositionInFetchedRows = -1;
/*     */       
/* 416 */       if ((this.mysql.getServerStatus() & 0x80) != 0) {
/* 417 */         this.lastRowFetched = true;
/*     */         
/* 419 */         if (!oldFirstFetchCompleted && this.fetchedRows.size() == 0) {
/* 420 */           this.wasEmpty = true;
/*     */         }
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
/*     */   public void removeRow(int ind) throws SQLException {
/* 435 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 444 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   private void nextRecord() throws SQLException {}
/*     */ 
/*     */   
/*     */   private void notSupported() throws SQLException {
/* 452 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOwner(ResultSetImpl rs) {
/* 461 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods getOwner() {
/* 470 */     return this.owner;
/*     */   }
/*     */   
/*     */   public boolean wasEmpty() {
/* 474 */     return this.wasEmpty;
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {
/* 478 */     this.metadata = metadata;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\RowDataCursor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */