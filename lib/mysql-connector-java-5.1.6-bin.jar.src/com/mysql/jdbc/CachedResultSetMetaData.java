/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.util.Map;
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
/*    */ public class CachedResultSetMetaData
/*    */ {
/* 31 */   Map columnNameToIndex = null;
/*    */ 
/*    */   
/*    */   Field[] fields;
/*    */ 
/*    */   
/* 37 */   Map fullColumnNameToIndex = null;
/*    */   
/*    */   ResultSetMetaData metadata;
/*    */ 
/*    */   
/*    */   public Map getColumnNameToIndex() {
/* 43 */     return this.columnNameToIndex;
/*    */   }
/*    */   
/*    */   public Field[] getFields() {
/* 47 */     return this.fields;
/*    */   }
/*    */   
/*    */   public Map getFullColumnNameToIndex() {
/* 51 */     return this.fullColumnNameToIndex;
/*    */   }
/*    */   
/*    */   public ResultSetMetaData getMetadata() {
/* 55 */     return this.metadata;
/*    */   }
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\CachedResultSetMetaData.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */