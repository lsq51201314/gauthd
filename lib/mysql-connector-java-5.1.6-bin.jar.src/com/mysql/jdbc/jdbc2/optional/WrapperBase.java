/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
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
/*     */ abstract class WrapperBase
/*     */ {
/*     */   protected MysqlPooledConnection pooledConnection;
/*     */   
/*     */   protected void checkAndFireConnectionError(SQLException sqlEx) throws SQLException {
/*  56 */     if (this.pooledConnection != null && 
/*  57 */       "08S01".equals(sqlEx.getSQLState()))
/*     */     {
/*  59 */       this.pooledConnection.callConnectionEventListeners(1, sqlEx);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  64 */     throw sqlEx;
/*     */   }
/*     */   
/*  67 */   protected Map unwrappedInterfaces = null;
/*     */   
/*     */   protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler { Object invokeOn;
/*     */     
/*     */     public ConnectionErrorFiringInvocationHandler(WrapperBase this$0, Object toInvokeOn) {
/*  72 */       this.this$0 = this$0; this.invokeOn = null;
/*  73 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */     private final WrapperBase this$0;
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  78 */       Object result = null;
/*     */       
/*     */       try {
/*  81 */         result = method.invoke(this.invokeOn, args);
/*     */         
/*  83 */         if (result != null) {
/*  84 */           result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */         }
/*     */       }
/*  87 */       catch (InvocationTargetException e) {
/*  88 */         if (e.getTargetException() instanceof SQLException) {
/*  89 */           this.this$0.checkAndFireConnectionError((SQLException)e.getTargetException());
/*     */         } else {
/*     */           
/*  92 */           throw e;
/*     */         } 
/*     */       } 
/*     */       
/*  96 */       return result;
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
/*     */     private Object proxyIfInterfaceIsJdbc(Object toProxy, Class clazz) {
/* 108 */       Class[] interfaces = clazz.getInterfaces();
/*     */       
/* 110 */       int i = 0; if (i < interfaces.length) {
/* 111 */         String packageName = interfaces[i].getPackage().getName();
/*     */         
/* 113 */         if ("java.sql".equals(packageName) || "javax.sql".equals(packageName))
/*     */         {
/* 115 */           return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(this.this$0, toProxy));
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 120 */         return proxyIfInterfaceIsJdbc(toProxy, interfaces[i]);
/*     */       } 
/*     */       
/* 123 */       return toProxy;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\WrapperBase.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */