/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.SQLException;
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
/*     */ public class MysqlParameterMetadata
/*     */   implements ParameterMetaData
/*     */ {
/*     */   boolean returnSimpleMetadata = false;
/*  33 */   ResultSetMetaData metadata = null;
/*     */   
/*  35 */   int parameterCount = 0;
/*     */   
/*     */   MysqlParameterMetadata(Field[] fieldInfo, int parameterCount) {
/*  38 */     this.metadata = new ResultSetMetaData(fieldInfo, false);
/*     */     
/*  40 */     this.parameterCount = parameterCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MysqlParameterMetadata(int count) {
/*  50 */     this.parameterCount = count;
/*  51 */     this.returnSimpleMetadata = true;
/*     */   }
/*     */   
/*     */   public int getParameterCount() throws SQLException {
/*  55 */     return this.parameterCount;
/*     */   }
/*     */   
/*     */   public int isNullable(int arg0) throws SQLException {
/*  59 */     checkAvailable();
/*     */     
/*  61 */     return this.metadata.isNullable(arg0);
/*     */   }
/*     */   
/*     */   private void checkAvailable() throws SQLException {
/*  65 */     if (this.metadata == null || this.metadata.fields == null) {
/*  66 */       throw SQLError.createSQLException("Parameter metadata not available for the given statement", "S1C00");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSigned(int arg0) throws SQLException {
/*  73 */     if (this.returnSimpleMetadata) {
/*  74 */       checkBounds(arg0);
/*     */       
/*  76 */       return false;
/*     */     } 
/*     */     
/*  79 */     checkAvailable();
/*     */     
/*  81 */     return this.metadata.isSigned(arg0);
/*     */   }
/*     */   
/*     */   public int getPrecision(int arg0) throws SQLException {
/*  85 */     if (this.returnSimpleMetadata) {
/*  86 */       checkBounds(arg0);
/*     */       
/*  88 */       return 0;
/*     */     } 
/*     */     
/*  91 */     checkAvailable();
/*     */     
/*  93 */     return this.metadata.getPrecision(arg0);
/*     */   }
/*     */   
/*     */   public int getScale(int arg0) throws SQLException {
/*  97 */     if (this.returnSimpleMetadata) {
/*  98 */       checkBounds(arg0);
/*     */       
/* 100 */       return 0;
/*     */     } 
/*     */     
/* 103 */     checkAvailable();
/*     */     
/* 105 */     return this.metadata.getScale(arg0);
/*     */   }
/*     */   
/*     */   public int getParameterType(int arg0) throws SQLException {
/* 109 */     if (this.returnSimpleMetadata) {
/* 110 */       checkBounds(arg0);
/*     */       
/* 112 */       return 12;
/*     */     } 
/*     */     
/* 115 */     checkAvailable();
/*     */     
/* 117 */     return this.metadata.getColumnType(arg0);
/*     */   }
/*     */   
/*     */   public String getParameterTypeName(int arg0) throws SQLException {
/* 121 */     if (this.returnSimpleMetadata) {
/* 122 */       checkBounds(arg0);
/*     */       
/* 124 */       return "VARCHAR";
/*     */     } 
/*     */     
/* 127 */     checkAvailable();
/*     */     
/* 129 */     return this.metadata.getColumnTypeName(arg0);
/*     */   }
/*     */   
/*     */   public String getParameterClassName(int arg0) throws SQLException {
/* 133 */     if (this.returnSimpleMetadata) {
/* 134 */       checkBounds(arg0);
/*     */       
/* 136 */       return "java.lang.String";
/*     */     } 
/*     */     
/* 139 */     checkAvailable();
/*     */     
/* 141 */     return this.metadata.getColumnClassName(arg0);
/*     */   }
/*     */   
/*     */   public int getParameterMode(int arg0) throws SQLException {
/* 145 */     return 1;
/*     */   }
/*     */   
/*     */   private void checkBounds(int paramNumber) throws SQLException {
/* 149 */     if (paramNumber < 1) {
/* 150 */       throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is invalid.", "S1009");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 155 */     if (paramNumber > this.parameterCount) {
/* 156 */       throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is greater than number of parameters, which is '" + this.parameterCount + "'.", "S1009");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class iface) throws SQLException {
/* 184 */     return iface.isInstance(this);
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
/*     */   
/*     */   public Object unwrap(Class iface) throws SQLException {
/*     */     try {
/* 205 */       return Util.cast(iface, this);
/* 206 */     } catch (ClassCastException cce) {
/* 207 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\MysqlParameterMetadata.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */