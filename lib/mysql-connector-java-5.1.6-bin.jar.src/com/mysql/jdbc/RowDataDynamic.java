/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*     */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*     */ import java.sql.SQLException;
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
/*     */ public class RowDataDynamic
/*     */   implements RowData
/*     */ {
/*     */   private int columnCount;
/*     */   private Field[] metadata;
/*     */   
/*     */   class OperationNotSupportedException
/*     */     extends SQLException
/*     */   {
/*     */     private final RowDataDynamic this$0;
/*     */     
/*     */     OperationNotSupportedException(RowDataDynamic this$0) {
/*  44 */       super(Messages.getString("RowDataDynamic.10"), "S1009");
/*     */       this.this$0 = this$0;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private int index = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   private MysqlIO io;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isAfterEnd = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isAtEnd = false;
/*     */ 
/*     */   
/*     */   private boolean isBinaryEncoded = false;
/*     */ 
/*     */   
/*     */   private ResultSetRow nextRow;
/*     */ 
/*     */   
/*     */   private ResultSetImpl owner;
/*     */ 
/*     */   
/*     */   private boolean streamerClosed = false;
/*     */ 
/*     */   
/*     */   private boolean wasEmpty = false;
/*     */ 
/*     */   
/*     */   private boolean useBufferRowExplicit;
/*     */ 
/*     */   
/*     */   private boolean moreResultsExisted;
/*     */ 
/*     */ 
/*     */   
/*     */   public RowDataDynamic(MysqlIO io, int colCount, Field[] fields, boolean isBinaryEncoded) throws SQLException {
/*  91 */     this.io = io;
/*  92 */     this.columnCount = colCount;
/*  93 */     this.isBinaryEncoded = isBinaryEncoded;
/*  94 */     this.metadata = fields;
/*     */     
/*  96 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
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
/*     */   public void addRow(ResultSetRow row) throws SQLException {
/* 108 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterLast() throws SQLException {
/* 118 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeFirst() throws SQLException {
/* 128 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeLast() throws SQLException {
/* 138 */     notSupported();
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
/*     */   public void close() throws SQLException {
/* 153 */     Object mutex = this;
/*     */     
/* 155 */     ConnectionImpl conn = null;
/*     */     
/* 157 */     if (this.owner != null) {
/* 158 */       conn = this.owner.connection;
/*     */       
/* 160 */       if (conn != null) {
/* 161 */         mutex = conn.getMutex();
/*     */       }
/*     */     } 
/*     */     
/* 165 */     boolean hadMore = false;
/* 166 */     int howMuchMore = 0;
/*     */     
/* 168 */     synchronized (mutex) {
/*     */       
/* 170 */       while (next() != null) {
/* 171 */         hadMore = true;
/* 172 */         howMuchMore++;
/*     */         
/* 174 */         if (howMuchMore % 100 == 0) {
/* 175 */           Thread.yield();
/*     */         }
/*     */       } 
/*     */       
/* 179 */       if (conn != null) {
/* 180 */         if (!conn.getClobberStreamingResults() && conn.getNetTimeoutForStreamingResults() > 0) {
/*     */           
/* 182 */           String oldValue = conn.getServerVariable("net_write_timeout");
/*     */ 
/*     */           
/* 185 */           if (oldValue == null || oldValue.length() == 0) {
/* 186 */             oldValue = "60";
/*     */           }
/*     */           
/* 189 */           this.io.clearInputStream();
/*     */           
/* 191 */           Statement stmt = null;
/*     */           
/*     */           try {
/* 194 */             stmt = conn.createStatement();
/* 195 */             stmt.executeUpdate("SET net_write_timeout=" + oldValue);
/*     */           } finally {
/* 197 */             if (stmt != null) {
/* 198 */               stmt.close();
/*     */             }
/*     */           } 
/*     */         } 
/*     */         
/* 203 */         if (conn.getUseUsageAdvisor() && 
/* 204 */           hadMore) {
/*     */           
/* 206 */           ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(conn);
/*     */ 
/*     */           
/* 209 */           eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owner.owningStatement == null) ? "N/A" : this.owner.owningStatement.currentCatalog, this.owner.connectionId, (this.owner.owningStatement == null) ? -1 : this.owner.owningStatement.getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, null, Messages.getString("RowDataDynamic.2") + howMuchMore + Messages.getString("RowDataDynamic.3") + Messages.getString("RowDataDynamic.4") + Messages.getString("RowDataDynamic.5") + Messages.getString("RowDataDynamic.6") + this.owner.pointOfOrigin));
/*     */         } 
/*     */       } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 240 */     this.metadata = null;
/* 241 */     this.owner = null;
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
/*     */   public ResultSetRow getAt(int ind) throws SQLException {
/* 254 */     notSupported();
/*     */     
/* 256 */     return null;
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
/* 267 */     notSupported();
/*     */     
/* 269 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods getOwner() {
/* 276 */     return this.owner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() throws SQLException {
/* 287 */     boolean hasNext = (this.nextRow != null);
/*     */     
/* 289 */     if (!hasNext && !this.streamerClosed) {
/* 290 */       this.io.closeStreamer(this);
/* 291 */       this.streamerClosed = true;
/*     */     } 
/*     */     
/* 294 */     return hasNext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() throws SQLException {
/* 305 */     return this.isAfterEnd;
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
/* 316 */     return (this.index < 0);
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
/* 328 */     return true;
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
/* 339 */     notSupported();
/*     */     
/* 341 */     return false;
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
/* 352 */     notSupported();
/*     */     
/* 354 */     return false;
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
/* 365 */     notSupported();
/*     */     
/* 367 */     return false;
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
/* 379 */     notSupported();
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
/*     */   public ResultSetRow next() throws SQLException {
/* 392 */     nextRecord();
/*     */     
/* 394 */     if (this.nextRow == null && !this.streamerClosed && !this.moreResultsExisted) {
/* 395 */       this.io.closeStreamer(this);
/* 396 */       this.streamerClosed = true;
/*     */     } 
/*     */     
/* 399 */     if (this.nextRow != null && 
/* 400 */       this.index != Integer.MAX_VALUE) {
/* 401 */       this.index++;
/*     */     }
/*     */ 
/*     */     
/* 405 */     return this.nextRow;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void nextRecord() throws SQLException {
/*     */     try {
/* 412 */       if (!this.isAtEnd) {
/* 413 */         this.nextRow = this.io.nextRow(this.metadata, this.columnCount, this.isBinaryEncoded, 1007, true, this.useBufferRowExplicit, true, null);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 418 */         if (this.nextRow == null) {
/* 419 */           this.isAtEnd = true;
/* 420 */           this.moreResultsExisted = this.io.tackOnMoreStreamingResults(this.owner);
/*     */           
/* 422 */           if (this.index == -1) {
/* 423 */             this.wasEmpty = true;
/*     */           }
/*     */         } 
/*     */       } else {
/* 427 */         this.isAfterEnd = true;
/*     */       } 
/* 429 */     } catch (SQLException sqlEx) {
/* 430 */       if (sqlEx instanceof StreamingNotifiable) {
/* 431 */         ((StreamingNotifiable)sqlEx).setWasStreamingResults();
/*     */       }
/*     */ 
/*     */       
/* 435 */       throw sqlEx;
/* 436 */     } catch (Exception ex) {
/* 437 */       String exceptionType = ex.getClass().getName();
/* 438 */       String exceptionMessage = ex.getMessage();
/*     */       
/* 440 */       exceptionMessage = exceptionMessage + Messages.getString("RowDataDynamic.7");
/* 441 */       exceptionMessage = exceptionMessage + Util.stackTraceToString(ex);
/*     */       
/* 443 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("RowDataDynamic.8") + exceptionType + Messages.getString("RowDataDynamic.9") + exceptionMessage, "S1000");
/*     */ 
/*     */ 
/*     */       
/* 447 */       sqlEx.initCause(ex);
/*     */       
/* 449 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void notSupported() throws SQLException {
/* 454 */     throw new OperationNotSupportedException(this);
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
/* 466 */     notSupported();
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
/* 478 */     notSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOwner(ResultSetImpl rs) {
/* 485 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 494 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean wasEmpty() {
/* 498 */     return this.wasEmpty;
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {
/* 502 */     this.metadata = metadata;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\RowDataDynamic.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */