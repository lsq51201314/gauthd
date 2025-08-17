/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ 
/*     */ class RowDataKeyset
/*     */   implements RowData
/*     */ {
/*     */   private ResultSetInternalMethods keyset;
/*     */   
/*     */   private void buildKeysetColumnsClause(Field[] originalQueryMetadata) throws SQLException {
/*  12 */     StringBuffer buf = new StringBuffer();
/*     */     
/*  14 */     for (int i = 0; i < originalQueryMetadata.length; i++) {
/*  15 */       if (originalQueryMetadata[i].isPrimaryKey()) {
/*  16 */         if (buf.length() != 0) {
/*  17 */           buf.append(", ");
/*     */         }
/*     */         
/*  20 */         buf.append("`");
/*  21 */         buf.append(originalQueryMetadata[i].getDatabaseName());
/*  22 */         buf.append("`.`");
/*  23 */         buf.append(originalQueryMetadata[i].getOriginalTableName());
/*  24 */         buf.append("`.`");
/*  25 */         buf.append(originalQueryMetadata[i].getOriginalName());
/*  26 */         buf.append("`");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private String extractWhereClause(String sql) {
/*  32 */     String delims = "'`\"";
/*     */     
/*  34 */     String canonicalSql = StringUtils.stripComments(sql, delims, delims, true, false, true, true);
/*     */ 
/*     */     
/*  37 */     int whereClausePos = StringUtils.indexOfIgnoreCaseRespectMarker(0, canonicalSql, " WHERE ", delims, delims, false);
/*     */ 
/*     */     
/*  40 */     if (whereClausePos == -1) {
/*  41 */       return "";
/*     */     }
/*     */     
/*  44 */     return canonicalSql.substring(whereClausePos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRow(ResultSetRow row) throws SQLException {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterLast() throws SQLException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeFirst() throws SQLException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeLast() throws SQLException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/*  68 */     SQLException caughtWhileClosing = null;
/*     */     
/*  70 */     if (this.keyset != null) {
/*     */       try {
/*  72 */         this.keyset.close();
/*  73 */       } catch (SQLException sqlEx) {
/*  74 */         caughtWhileClosing = sqlEx;
/*     */       } 
/*     */       
/*  77 */       this.keyset = null;
/*     */     } 
/*     */     
/*  80 */     if (caughtWhileClosing != null) {
/*  81 */       throw caughtWhileClosing;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSetRow getAt(int index) throws SQLException {
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCurrentRowNumber() throws SQLException {
/*  92 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods getOwner() {
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() throws SQLException {
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() throws SQLException {
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBeforeFirst() throws SQLException {
/* 112 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDynamic() throws SQLException {
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() throws SQLException {
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirst() throws SQLException {
/* 127 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLast() throws SQLException {
/* 132 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void moveRowRelative(int rows) throws SQLException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetRow next() throws SQLException {
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeRow(int index) throws SQLException {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCurrentRow(int rowNumber) throws SQLException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOwner(ResultSetImpl rs) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() throws SQLException {
/* 162 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean wasEmpty() {
/* 167 */     return false;
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {}
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\RowDataKeyset.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */