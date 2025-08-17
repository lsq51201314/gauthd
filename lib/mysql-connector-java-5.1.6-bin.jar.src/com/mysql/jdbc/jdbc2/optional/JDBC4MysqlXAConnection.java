/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.sql.StatementEvent;
/*    */ import javax.sql.StatementEventListener;
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
/*    */ public class JDBC4MysqlXAConnection
/*    */   extends MysqlXAConnection
/*    */ {
/*    */   private Map<StatementEventListener, StatementEventListener> statementEventListeners;
/*    */   
/*    */   public JDBC4MysqlXAConnection(ConnectionImpl connection, boolean logXaCommands) throws SQLException {
/* 42 */     super(connection, logXaCommands);
/*    */     
/* 44 */     this.statementEventListeners = new HashMap<StatementEventListener, StatementEventListener>();
/*    */   }
/*    */   
/*    */   public synchronized void close() throws SQLException {
/* 48 */     super.close();
/*    */     
/* 50 */     if (this.statementEventListeners != null) {
/* 51 */       this.statementEventListeners.clear();
/*    */       
/* 53 */       this.statementEventListeners = null;
/*    */     } 
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
/*    */   public void addStatementEventListener(StatementEventListener listener) {
/* 70 */     synchronized (this.statementEventListeners) {
/* 71 */       this.statementEventListeners.put(listener, listener);
/*    */     } 
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
/*    */   public void removeStatementEventListener(StatementEventListener listener) {
/* 87 */     synchronized (this.statementEventListeners) {
/* 88 */       this.statementEventListeners.remove(listener);
/*    */     } 
/*    */   }
/*    */   
/*    */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 93 */     synchronized (this.statementEventListeners) {
/* 94 */       for (StatementEventListener listener : this.statementEventListeners.keySet())
/* 95 */         listener.statementClosed(event); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4MysqlXAConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */