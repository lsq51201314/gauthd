/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class IterateBlock
/*    */ {
/*    */   DatabaseMetaData.IteratorWithCleanup iteratorWithCleanup;
/*    */   Iterator javaIterator;
/*    */   boolean stopIterating = false;
/*    */   
/*    */   IterateBlock(DatabaseMetaData.IteratorWithCleanup i) {
/* 37 */     this.iteratorWithCleanup = i;
/* 38 */     this.javaIterator = null;
/*    */   }
/*    */   
/*    */   IterateBlock(Iterator i) {
/* 42 */     this.javaIterator = i;
/* 43 */     this.iteratorWithCleanup = null;
/*    */   }
/*    */   
/*    */   public void doForAll() throws SQLException {
/* 47 */     if (this.iteratorWithCleanup != null) {
/*    */       try {
/* 49 */         while (this.iteratorWithCleanup.hasNext()) {
/* 50 */           forEach(this.iteratorWithCleanup.next());
/*    */           
/* 52 */           if (this.stopIterating) {
/*    */             break;
/*    */           }
/*    */         } 
/*    */       } finally {
/* 57 */         this.iteratorWithCleanup.close();
/*    */       } 
/*    */     } else {
/* 60 */       while (this.javaIterator.hasNext()) {
/* 61 */         forEach(this.javaIterator.next());
/*    */         
/* 63 */         if (this.stopIterating) {
/*    */           break;
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   abstract void forEach(Object paramObject) throws SQLException;
/*    */   
/*    */   public final boolean fullIteration() {
/* 73 */     return !this.stopIterating;
/*    */   }
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\IterateBlock.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */