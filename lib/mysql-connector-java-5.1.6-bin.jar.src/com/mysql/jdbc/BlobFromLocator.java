/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
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
/*     */ 
/*     */ public class BlobFromLocator
/*     */   implements Blob
/*     */ {
/*  55 */   private List primaryKeyColumns = null;
/*     */   
/*  57 */   private List primaryKeyValues = null;
/*     */ 
/*     */   
/*     */   private ResultSetImpl creatorResultSet;
/*     */   
/*  62 */   private String blobColumnName = null;
/*     */   
/*  64 */   private String tableName = null;
/*     */   
/*  66 */   private int numColsInResultSet = 0;
/*     */   
/*  68 */   private int numPrimaryKeys = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private String quotedId;
/*     */ 
/*     */ 
/*     */   
/*     */   BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex) throws SQLException {
/*  77 */     this.creatorResultSet = creatorResultSetToSet;
/*     */     
/*  79 */     this.numColsInResultSet = this.creatorResultSet.fields.length;
/*  80 */     this.quotedId = this.creatorResultSet.connection.getMetaData().getIdentifierQuoteString();
/*     */ 
/*     */     
/*  83 */     if (this.numColsInResultSet > 1) {
/*  84 */       this.primaryKeyColumns = new ArrayList();
/*  85 */       this.primaryKeyValues = new ArrayList();
/*     */       
/*  87 */       for (int i = 0; i < this.numColsInResultSet; i++) {
/*  88 */         if (this.creatorResultSet.fields[i].isPrimaryKey()) {
/*  89 */           StringBuffer keyName = new StringBuffer();
/*  90 */           keyName.append(this.quotedId);
/*     */           
/*  92 */           String originalColumnName = this.creatorResultSet.fields[i].getOriginalName();
/*     */ 
/*     */           
/*  95 */           if (originalColumnName != null && originalColumnName.length() > 0) {
/*     */             
/*  97 */             keyName.append(originalColumnName);
/*     */           } else {
/*  99 */             keyName.append(this.creatorResultSet.fields[i].getName());
/*     */           } 
/*     */ 
/*     */           
/* 103 */           keyName.append(this.quotedId);
/*     */           
/* 105 */           this.primaryKeyColumns.add(keyName.toString());
/* 106 */           this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 111 */       notEnoughInformationInQuery();
/*     */     } 
/*     */     
/* 114 */     this.numPrimaryKeys = this.primaryKeyColumns.size();
/*     */     
/* 116 */     if (this.numPrimaryKeys == 0) {
/* 117 */       notEnoughInformationInQuery();
/*     */     }
/*     */     
/* 120 */     if (this.creatorResultSet.fields[0].getOriginalTableName() != null) {
/* 121 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */       
/* 123 */       String databaseName = this.creatorResultSet.fields[0].getDatabaseName();
/*     */ 
/*     */       
/* 126 */       if (databaseName != null && databaseName.length() > 0) {
/* 127 */         tableNameBuffer.append(this.quotedId);
/* 128 */         tableNameBuffer.append(databaseName);
/* 129 */         tableNameBuffer.append(this.quotedId);
/* 130 */         tableNameBuffer.append('.');
/*     */       } 
/*     */       
/* 133 */       tableNameBuffer.append(this.quotedId);
/* 134 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getOriginalTableName());
/*     */       
/* 136 */       tableNameBuffer.append(this.quotedId);
/*     */       
/* 138 */       this.tableName = tableNameBuffer.toString();
/*     */     } else {
/* 140 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */       
/* 142 */       tableNameBuffer.append(this.quotedId);
/* 143 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getTableName());
/*     */       
/* 145 */       tableNameBuffer.append(this.quotedId);
/*     */       
/* 147 */       this.tableName = tableNameBuffer.toString();
/*     */     } 
/*     */     
/* 150 */     this.blobColumnName = this.quotedId + this.creatorResultSet.getString(blobColumnIndex) + this.quotedId;
/*     */   }
/*     */ 
/*     */   
/*     */   private void notEnoughInformationInQuery() throws SQLException {
/* 155 */     throw SQLError.createSQLException("Emulated BLOB locators must come from a ResultSet with only one table selected, and all primary keys selected", "S1000");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
/* 165 */     throw SQLError.notImplemented();
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
/*     */   public InputStream getBinaryStream() throws SQLException {
/* 178 */     return new BufferedInputStream(new LocatorInputStream(this), this.creatorResultSet.connection.getLocatorFetchBufferSize());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
/* 187 */     PreparedStatement pStmt = null;
/*     */     
/* 189 */     if (offset + length > bytes.length) {
/* 190 */       length = bytes.length - offset;
/*     */     }
/*     */     
/* 193 */     byte[] bytesToWrite = new byte[length];
/* 194 */     System.arraycopy(bytes, offset, bytesToWrite, 0, length);
/*     */ 
/*     */     
/* 197 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 198 */     query.append(this.tableName);
/* 199 */     query.append(" SET ");
/* 200 */     query.append(this.blobColumnName);
/* 201 */     query.append(" = INSERT(");
/* 202 */     query.append(this.blobColumnName);
/* 203 */     query.append(", ");
/* 204 */     query.append(writeAt);
/* 205 */     query.append(", ");
/* 206 */     query.append(length);
/* 207 */     query.append(", ?) WHERE ");
/*     */     
/* 209 */     query.append(this.primaryKeyColumns.get(0));
/* 210 */     query.append(" = ?");
/*     */     int i;
/* 212 */     for (i = 1; i < this.numPrimaryKeys; i++) {
/* 213 */       query.append(" AND ");
/* 214 */       query.append(this.primaryKeyColumns.get(i));
/* 215 */       query.append(" = ?");
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 220 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/*     */       
/* 223 */       pStmt.setBytes(1, bytesToWrite);
/*     */       
/* 225 */       for (i = 0; i < this.numPrimaryKeys; i++) {
/* 226 */         pStmt.setString(i + 2, this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 229 */       int rowsUpdated = pStmt.executeUpdate();
/*     */       
/* 231 */       if (rowsUpdated != 1) {
/* 232 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */       }
/*     */     }
/*     */     finally {
/*     */       
/* 237 */       if (pStmt != null) {
/*     */         try {
/* 239 */           pStmt.close();
/* 240 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 244 */         pStmt = null;
/*     */       } 
/*     */     } 
/*     */     
/* 248 */     return (int)length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(long writeAt, byte[] bytes) throws SQLException {
/* 255 */     return setBytes(writeAt, bytes, 0, bytes.length);
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
/*     */   public byte[] getBytes(long pos, int length) throws SQLException {
/* 274 */     PreparedStatement pStmt = null;
/*     */ 
/*     */     
/*     */     try {
/* 278 */       pStmt = createGetBytesStatement();
/*     */       
/* 280 */       return getBytesInternal(pStmt, pos, length);
/*     */     } finally {
/* 282 */       if (pStmt != null) {
/*     */         try {
/* 284 */           pStmt.close();
/* 285 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 289 */         pStmt = null;
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
/*     */   public long length() throws SQLException {
/* 304 */     ResultSet blobRs = null;
/* 305 */     PreparedStatement pStmt = null;
/*     */ 
/*     */     
/* 308 */     StringBuffer query = new StringBuffer("SELECT LENGTH(");
/* 309 */     query.append(this.blobColumnName);
/* 310 */     query.append(") FROM ");
/* 311 */     query.append(this.tableName);
/* 312 */     query.append(" WHERE ");
/*     */     
/* 314 */     query.append(this.primaryKeyColumns.get(0));
/* 315 */     query.append(" = ?");
/*     */     int i;
/* 317 */     for (i = 1; i < this.numPrimaryKeys; i++) {
/* 318 */       query.append(" AND ");
/* 319 */       query.append(this.primaryKeyColumns.get(i));
/* 320 */       query.append(" = ?");
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 325 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/*     */       
/* 328 */       for (i = 0; i < this.numPrimaryKeys; i++) {
/* 329 */         pStmt.setString(i + 1, this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 332 */       blobRs = pStmt.executeQuery();
/*     */       
/* 334 */       if (blobRs.next()) {
/* 335 */         return blobRs.getLong(1);
/*     */       }
/*     */       
/* 338 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */     }
/*     */     finally {
/*     */       
/* 342 */       if (blobRs != null) {
/*     */         try {
/* 344 */           blobRs.close();
/* 345 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 349 */         blobRs = null;
/*     */       } 
/*     */       
/* 352 */       if (pStmt != null) {
/*     */         try {
/* 354 */           pStmt.close();
/* 355 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 359 */         pStmt = null;
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
/*     */   public long position(Blob pattern, long start) throws SQLException {
/* 379 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position(byte[] pattern, long start) throws SQLException {
/* 386 */     ResultSet blobRs = null;
/* 387 */     PreparedStatement pStmt = null;
/*     */ 
/*     */     
/* 390 */     StringBuffer query = new StringBuffer("SELECT LOCATE(");
/* 391 */     query.append("?, ");
/* 392 */     query.append(this.blobColumnName);
/* 393 */     query.append(", ");
/* 394 */     query.append(start);
/* 395 */     query.append(") FROM ");
/* 396 */     query.append(this.tableName);
/* 397 */     query.append(" WHERE ");
/*     */     
/* 399 */     query.append(this.primaryKeyColumns.get(0));
/* 400 */     query.append(" = ?");
/*     */     int i;
/* 402 */     for (i = 1; i < this.numPrimaryKeys; i++) {
/* 403 */       query.append(" AND ");
/* 404 */       query.append(this.primaryKeyColumns.get(i));
/* 405 */       query.append(" = ?");
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 410 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */       
/* 412 */       pStmt.setBytes(1, pattern);
/*     */       
/* 414 */       for (i = 0; i < this.numPrimaryKeys; i++) {
/* 415 */         pStmt.setString(i + 2, this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 418 */       blobRs = pStmt.executeQuery();
/*     */       
/* 420 */       if (blobRs.next()) {
/* 421 */         return blobRs.getLong(1);
/*     */       }
/*     */       
/* 424 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */     }
/*     */     finally {
/*     */       
/* 428 */       if (blobRs != null) {
/*     */         try {
/* 430 */           blobRs.close();
/* 431 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 435 */         blobRs = null;
/*     */       } 
/*     */       
/* 438 */       if (pStmt != null) {
/*     */         try {
/* 440 */           pStmt.close();
/* 441 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 445 */         pStmt = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(long length) throws SQLException {
/* 454 */     PreparedStatement pStmt = null;
/*     */ 
/*     */     
/* 457 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 458 */     query.append(this.tableName);
/* 459 */     query.append(" SET ");
/* 460 */     query.append(this.blobColumnName);
/* 461 */     query.append(" = LEFT(");
/* 462 */     query.append(this.blobColumnName);
/* 463 */     query.append(", ");
/* 464 */     query.append(length);
/* 465 */     query.append(") WHERE ");
/*     */     
/* 467 */     query.append(this.primaryKeyColumns.get(0));
/* 468 */     query.append(" = ?");
/*     */     int i;
/* 470 */     for (i = 1; i < this.numPrimaryKeys; i++) {
/* 471 */       query.append(" AND ");
/* 472 */       query.append(this.primaryKeyColumns.get(i));
/* 473 */       query.append(" = ?");
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 478 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/*     */       
/* 481 */       for (i = 0; i < this.numPrimaryKeys; i++) {
/* 482 */         pStmt.setString(i + 1, this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 485 */       int rowsUpdated = pStmt.executeUpdate();
/*     */       
/* 487 */       if (rowsUpdated != 1) {
/* 488 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */       }
/*     */     }
/*     */     finally {
/*     */       
/* 493 */       if (pStmt != null) {
/*     */         try {
/* 495 */           pStmt.close();
/* 496 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 500 */         pStmt = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   PreparedStatement createGetBytesStatement() throws SQLException {
/* 506 */     StringBuffer query = new StringBuffer("SELECT SUBSTRING(");
/*     */     
/* 508 */     query.append(this.blobColumnName);
/* 509 */     query.append(", ");
/* 510 */     query.append("?");
/* 511 */     query.append(", ");
/* 512 */     query.append("?");
/* 513 */     query.append(") FROM ");
/* 514 */     query.append(this.tableName);
/* 515 */     query.append(" WHERE ");
/*     */     
/* 517 */     query.append(this.primaryKeyColumns.get(0));
/* 518 */     query.append(" = ?");
/*     */     
/* 520 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 521 */       query.append(" AND ");
/* 522 */       query.append(this.primaryKeyColumns.get(i));
/* 523 */       query.append(" = ?");
/*     */     } 
/*     */     
/* 526 */     return this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length) throws SQLException {
/* 533 */     ResultSet blobRs = null;
/*     */ 
/*     */     
/*     */     try {
/* 537 */       pStmt.setLong(1, pos);
/* 538 */       pStmt.setInt(2, length);
/*     */       
/* 540 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 541 */         pStmt.setString(i + 3, this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 544 */       blobRs = pStmt.executeQuery();
/*     */       
/* 546 */       if (blobRs.next()) {
/* 547 */         return ((ResultSetImpl)blobRs).getBytes(1, true);
/*     */       }
/*     */       
/* 550 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */     }
/*     */     finally {
/*     */       
/* 554 */       if (blobRs != null) {
/*     */         try {
/* 556 */           blobRs.close();
/* 557 */         } catch (SQLException sqlEx) {}
/*     */ 
/*     */ 
/*     */         
/* 561 */         blobRs = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   class LocatorInputStream extends InputStream {
/*     */     long currentPositionInBlob;
/*     */     long length;
/*     */     PreparedStatement pStmt;
/*     */     private final BlobFromLocator this$0;
/*     */     
/*     */     LocatorInputStream(BlobFromLocator this$0) throws SQLException {
/* 573 */       this.this$0 = this$0; this.currentPositionInBlob = 0L; this.length = 0L; this.pStmt = null;
/* 574 */       this.length = this$0.length();
/* 575 */       this.pStmt = this$0.createGetBytesStatement();
/*     */     }
/*     */     LocatorInputStream(BlobFromLocator this$0, long pos, long len) throws SQLException {
/* 578 */       this.this$0 = this$0; this.currentPositionInBlob = 0L; this.length = 0L; this.pStmt = null;
/* 579 */       this.length = pos + len;
/* 580 */       this.currentPositionInBlob = pos;
/* 581 */       long blobLength = this$0.length();
/*     */       
/* 583 */       if (pos + len > blobLength) {
/* 584 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamLength", new Object[] { new Long(blobLength), new Long(pos), new Long(len) }), "S1009");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 590 */       if (pos < 1L) {
/* 591 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009");
/*     */       }
/*     */ 
/*     */       
/* 595 */       if (pos > blobLength) {
/* 596 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009");
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 602 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 603 */         return -1;
/*     */       }
/*     */       
/*     */       try {
/* 607 */         byte[] asBytes = this.this$0.getBytesInternal(this.pStmt, this.currentPositionInBlob++ + 1L, 1);
/*     */ 
/*     */         
/* 610 */         if (asBytes == null) {
/* 611 */           return -1;
/*     */         }
/*     */         
/* 614 */         return asBytes[0];
/* 615 */       } catch (SQLException sqlEx) {
/* 616 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 626 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 627 */         return -1;
/*     */       }
/*     */       
/*     */       try {
/* 631 */         byte[] asBytes = this.this$0.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, len);
/*     */ 
/*     */         
/* 634 */         if (asBytes == null) {
/* 635 */           return -1;
/*     */         }
/*     */         
/* 638 */         System.arraycopy(asBytes, 0, b, off, asBytes.length);
/*     */         
/* 640 */         this.currentPositionInBlob += asBytes.length;
/*     */         
/* 642 */         return asBytes.length;
/* 643 */       } catch (SQLException sqlEx) {
/* 644 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b) throws IOException {
/* 654 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 655 */         return -1;
/*     */       }
/*     */       
/*     */       try {
/* 659 */         byte[] asBytes = this.this$0.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, b.length);
/*     */ 
/*     */         
/* 662 */         if (asBytes == null) {
/* 663 */           return -1;
/*     */         }
/*     */         
/* 666 */         System.arraycopy(asBytes, 0, b, 0, asBytes.length);
/*     */         
/* 668 */         this.currentPositionInBlob += asBytes.length;
/*     */         
/* 670 */         return asBytes.length;
/* 671 */       } catch (SQLException sqlEx) {
/* 672 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 682 */       if (this.pStmt != null) {
/*     */         try {
/* 684 */           this.pStmt.close();
/* 685 */         } catch (SQLException sqlEx) {
/* 686 */           throw new IOException(sqlEx.toString());
/*     */         } 
/*     */       }
/*     */       
/* 690 */       super.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void free() throws SQLException {
/* 695 */     this.creatorResultSet = null;
/* 696 */     this.primaryKeyColumns = null;
/* 697 */     this.primaryKeyValues = null;
/*     */   }
/*     */   
/*     */   public InputStream getBinaryStream(long pos, long length) throws SQLException {
/* 701 */     return new LocatorInputStream(this, pos, length);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\BlobFromLocator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */