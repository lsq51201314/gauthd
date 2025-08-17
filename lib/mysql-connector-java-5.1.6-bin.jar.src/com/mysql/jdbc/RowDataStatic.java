/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
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
/*     */ public class RowDataStatic
/*     */   implements RowData
/*     */ {
/*     */   private Field[] metadata;
/*     */   private int index;
/*     */   ResultSetImpl owner;
/*     */   private List rows;
/*     */   
/*     */   public RowDataStatic(List rows) {
/*  52 */     this.index = -1;
/*  53 */     this.rows = rows;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRow(ResultSetRow row) {
/*  63 */     this.rows.add(row);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterLast() {
/*  70 */     this.index = this.rows.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeFirst() {
/*  77 */     this.index = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeLast() {
/*  84 */     this.index = this.rows.size() - 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetRow getAt(int atIndex) throws SQLException {
/* 102 */     if (atIndex < 0 || atIndex >= this.rows.size()) {
/* 103 */       return null;
/*     */     }
/*     */     
/* 106 */     return ((ResultSetRow)this.rows.get(atIndex)).setMetadata(this.metadata);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCurrentRowNumber() {
/* 115 */     return this.index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetInternalMethods getOwner() {
/* 122 */     return this.owner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 131 */     boolean hasMore = (this.index + 1 < this.rows.size());
/*     */     
/* 133 */     return hasMore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 142 */     return (this.index >= this.rows.size());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBeforeFirst() {
/* 151 */     return (this.index == -1 && this.rows.size() != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDynamic() {
/* 160 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 169 */     return (this.rows.size() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFirst() {
/* 178 */     return (this.index == 0);
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
/*     */   public boolean isLast() {
/* 191 */     if (this.rows.size() == 0) {
/* 192 */       return false;
/*     */     }
/*     */     
/* 195 */     return (this.index == this.rows.size() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void moveRowRelative(int rowsToMove) {
/* 205 */     this.index += rowsToMove;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetRow next() throws SQLException {
/* 214 */     this.index++;
/*     */     
/* 216 */     if (this.index < this.rows.size()) {
/* 217 */       ResultSetRow row = this.rows.get(this.index);
/*     */       
/* 219 */       return row.setMetadata(this.metadata);
/*     */     } 
/*     */     
/* 222 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeRow(int atIndex) {
/* 232 */     this.rows.remove(atIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCurrentRow(int newIndex) {
/* 242 */     this.index = newIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOwner(ResultSetImpl rs) {
/* 249 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 258 */     return this.rows.size();
/*     */   }
/*     */   
/*     */   public boolean wasEmpty() {
/* 262 */     return (this.rows != null && this.rows.size() == 0);
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {
/* 266 */     this.metadata = metadata;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\RowDataStatic.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */