/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.RowIdLifetime;
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ 
/*    */ public class JDBC4DatabaseMetaDataUsingInfoSchema
/*    */   extends DatabaseMetaDataUsingInfoSchema
/*    */ {
/*    */   public JDBC4DatabaseMetaDataUsingInfoSchema(ConnectionImpl connToSet, String databaseToSet) throws SQLException {
/* 36 */     super(connToSet, databaseToSet);
/*    */   }
/*    */   
/*    */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/* 40 */     return RowIdLifetime.ROWID_UNSUPPORTED;
/*    */   }
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
/*    */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 61 */     return iface.isInstance(this);
/*    */   }
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
/*    */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*    */     try {
/* 82 */       return iface.cast(this);
/* 83 */     } catch (ClassCastException cce) {
/* 84 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getJDBC4FunctionNoTableConstant() {
/* 90 */     return 1;
/*    */   }
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4DatabaseMetaDataUsingInfoSchema.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */