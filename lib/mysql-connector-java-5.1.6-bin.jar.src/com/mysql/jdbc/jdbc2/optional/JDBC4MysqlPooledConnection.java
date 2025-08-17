/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.StatementEvent;
/*     */ import javax.sql.StatementEventListener;
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
/*     */ public class JDBC4MysqlPooledConnection
/*     */   extends MysqlPooledConnection
/*     */ {
/*     */   private Map<StatementEventListener, StatementEventListener> statementEventListeners;
/*     */   
/*     */   public JDBC4MysqlPooledConnection(Connection connection) {
/*  50 */     super(connection);
/*     */     
/*  52 */     this.statementEventListeners = new HashMap<StatementEventListener, StatementEventListener>();
/*     */   }
/*     */   
/*     */   public synchronized void close() throws SQLException {
/*  56 */     super.close();
/*     */     
/*  58 */     if (this.statementEventListeners != null) {
/*  59 */       this.statementEventListeners.clear();
/*     */       
/*  61 */       this.statementEventListeners = null;
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
/*     */   public void addStatementEventListener(StatementEventListener listener) {
/*  78 */     synchronized (this.statementEventListeners) {
/*  79 */       this.statementEventListeners.put(listener, listener);
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
/*     */   public void removeStatementEventListener(StatementEventListener listener) {
/*  95 */     synchronized (this.statementEventListeners) {
/*  96 */       this.statementEventListeners.remove(listener);
/*     */     } 
/*     */   }
/*     */   
/*     */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 101 */     synchronized (this.statementEventListeners) {
/* 102 */       for (StatementEventListener listener : this.statementEventListeners.keySet())
/* 103 */         listener.statementClosed(event); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4MysqlPooledConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */