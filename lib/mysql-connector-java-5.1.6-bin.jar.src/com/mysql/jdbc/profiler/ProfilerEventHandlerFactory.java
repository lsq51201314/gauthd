/*    */ package com.mysql.jdbc.profiler;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.Util;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProfilerEventHandlerFactory
/*    */ {
/* 39 */   private static final Map CONNECTIONS_TO_SINKS = new HashMap();
/*    */   
/* 41 */   private Connection ownerConnection = null;
/*    */   
/* 43 */   private Log log = null;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized ProfilerEventHandler getInstance(Connection conn) throws SQLException {
/* 54 */     ProfilerEventHandler handler = (ProfilerEventHandler)CONNECTIONS_TO_SINKS.get(conn);
/*    */ 
/*    */     
/* 57 */     if (handler == null) {
/* 58 */       handler = (ProfilerEventHandler)Util.getInstance(conn.getProfilerEventHandler(), new Class[0], new Object[0]);
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 63 */       conn.initializeExtension(handler);
/*    */       
/* 65 */       CONNECTIONS_TO_SINKS.put(conn, handler);
/*    */     } 
/*    */     
/* 68 */     return handler;
/*    */   }
/*    */   
/*    */   public static synchronized void removeInstance(Connection conn) {
/* 72 */     ProfilerEventHandler handler = (ProfilerEventHandler)CONNECTIONS_TO_SINKS.remove(conn);
/*    */     
/* 74 */     if (handler != null) {
/* 75 */       handler.destroy();
/*    */     }
/*    */   }
/*    */   
/*    */   private ProfilerEventHandlerFactory(Connection conn) {
/* 80 */     this.ownerConnection = conn;
/*    */     
/*    */     try {
/* 83 */       this.log = this.ownerConnection.getLog();
/* 84 */     } catch (SQLException sqlEx) {
/* 85 */       throw new RuntimeException("Unable to get logger from connection");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\profiler\ProfilerEventHandlerFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */