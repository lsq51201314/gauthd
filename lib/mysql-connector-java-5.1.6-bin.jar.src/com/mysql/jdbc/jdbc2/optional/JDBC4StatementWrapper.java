/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4StatementWrapper
/*     */   extends StatementWrapper
/*     */ {
/*     */   public JDBC4StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
/*  60 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   public void close() throws SQLException {
/*     */     try {
/*  65 */       super.close();
/*     */     } finally {
/*  67 */       this.unwrappedInterfaces = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isClosed() throws SQLException {
/*     */     try {
/*  73 */       if (this.wrappedStmt != null) {
/*  74 */         return this.wrappedStmt.isClosed();
/*     */       }
/*  76 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     
/*     */     }
/*  79 */     catch (SQLException sqlEx) {
/*  80 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/*  83 */       return false;
/*     */     } 
/*     */   }
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/*  88 */       if (this.wrappedStmt != null) {
/*  89 */         this.wrappedStmt.setPoolable(poolable);
/*     */       } else {
/*  91 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/*  94 */     } catch (SQLException sqlEx) {
/*  95 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/* 101 */       if (this.wrappedStmt != null) {
/* 102 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 104 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     
/*     */     }
/* 107 */     catch (SQLException sqlEx) {
/* 108 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 111 */       return false;
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
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 138 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 140 */     if (isInstance) {
/* 141 */       return true;
/*     */     }
/*     */     
/* 144 */     String interfaceClassName = iface.getName();
/*     */     
/* 146 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName.equals("java.sql.Wrapper"));
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
/*     */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 173 */       if ("java.sql.Statement".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName()))
/*     */       {
/* 175 */         return iface.cast(this);
/*     */       }
/*     */       
/* 178 */       if (this.unwrappedInterfaces == null) {
/* 179 */         this.unwrappedInterfaces = new HashMap<Object, Object>();
/*     */       }
/*     */       
/* 182 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 184 */       if (cachedUnwrapped == null) {
/* 185 */         cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */ 
/*     */ 
/*     */         
/* 189 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       } 
/*     */       
/* 192 */       return iface.cast(cachedUnwrapped);
/* 193 */     } catch (ClassCastException cce) {
/* 194 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4StatementWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */