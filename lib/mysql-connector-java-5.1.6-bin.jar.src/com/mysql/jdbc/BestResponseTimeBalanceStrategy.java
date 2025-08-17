/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ public class BestResponseTimeBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public void destroy() {}
/*     */   
/*     */   public void init(Connection conn, Properties props) throws SQLException {}
/*     */   
/*     */   public Connection pickConnection(LoadBalancingConnectionProxy proxy, List configuredHosts, Map liveConnections, long[] responseTimes, int numRetries) throws SQLException {
/*  50 */     long minResponseTime = Long.MAX_VALUE;
/*     */     
/*  52 */     int bestHostIndex = 0;
/*     */     
/*  54 */     Map blackList = new HashMap(configuredHosts.size());
/*     */     
/*  56 */     SQLException ex = null;
/*     */     
/*  58 */     for (int attempts = 0; attempts < numRetries; ) {
/*     */       
/*  60 */       if (blackList.size() == configuredHosts.size()) {
/*  61 */         blackList.clear();
/*     */       }
/*     */       
/*  64 */       for (int i = 0; i < responseTimes.length; i++) {
/*  65 */         long candidateResponseTime = responseTimes[i];
/*     */         
/*  67 */         if (candidateResponseTime < minResponseTime && !blackList.containsKey(configuredHosts.get(i))) {
/*     */           
/*  69 */           if (candidateResponseTime == 0L) {
/*  70 */             bestHostIndex = i;
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/*  75 */           bestHostIndex = i;
/*  76 */           minResponseTime = candidateResponseTime;
/*     */         } 
/*     */       } 
/*     */       
/*  80 */       String bestHost = configuredHosts.get(bestHostIndex);
/*     */       
/*  82 */       Connection conn = (Connection)liveConnections.get(bestHost);
/*     */       
/*  84 */       if (conn == null) {
/*     */         try {
/*  86 */           conn = proxy.createConnectionForHost(bestHost);
/*  87 */         } catch (SQLException sqlEx) {
/*  88 */           ex = sqlEx;
/*     */           
/*  90 */           if (sqlEx instanceof CommunicationsException || "08S01".equals(sqlEx.getSQLState())) {
/*     */             
/*  92 */             blackList.put(bestHost, null);
/*     */             
/*  94 */             if (blackList.size() == configuredHosts.size()) {
/*  95 */               blackList.clear();
/*     */               
/*     */               try {
/*  98 */                 Thread.sleep(250L);
/*  99 */               } catch (InterruptedException e) {}
/*     */             }
/*     */           
/*     */           }
/*     */           else {
/*     */             
/* 105 */             throw sqlEx;
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 110 */       return conn;
/*     */     } 
/*     */     
/* 113 */     if (ex != null) {
/* 114 */       throw ex;
/*     */     }
/*     */     
/* 117 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\BestResponseTimeBalanceStrategy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */