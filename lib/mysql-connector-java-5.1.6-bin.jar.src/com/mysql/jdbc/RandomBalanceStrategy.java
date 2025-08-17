/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
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
/*     */ public class RandomBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public void destroy() {}
/*     */   
/*     */   public void init(Connection conn, Properties props) throws SQLException {}
/*     */   
/*     */   public Connection pickConnection(LoadBalancingConnectionProxy proxy, List configuredHosts, Map liveConnections, long[] responseTimes, int numRetries) throws SQLException {
/*  48 */     int numHosts = configuredHosts.size();
/*     */     
/*  50 */     SQLException ex = null;
/*     */     
/*  52 */     Map whiteListMap = new HashMap(numHosts);
/*  53 */     List whiteList = new ArrayList(numHosts);
/*  54 */     whiteList.addAll(configuredHosts);
/*     */     
/*  56 */     for (int i = 0; i < numHosts; i++) {
/*  57 */       whiteListMap.put(whiteList.get(i), new Integer(i));
/*     */     }
/*     */     
/*  60 */     for (int attempts = 0; attempts < numRetries; ) {
/*  61 */       int random = (int)(Math.random() * whiteList.size());
/*     */       
/*  63 */       if (random == whiteList.size()) {
/*  64 */         random--;
/*     */       }
/*     */       
/*  67 */       String hostPortSpec = whiteList.get(random);
/*     */       
/*  69 */       Connection conn = (Connection)liveConnections.get(hostPortSpec);
/*     */       
/*  71 */       if (conn == null) {
/*     */         try {
/*  73 */           conn = proxy.createConnectionForHost(hostPortSpec);
/*  74 */         } catch (SQLException sqlEx) {
/*  75 */           ex = sqlEx;
/*     */           
/*  77 */           if (sqlEx instanceof CommunicationsException || "08S01".equals(sqlEx.getSQLState())) {
/*     */ 
/*     */             
/*  80 */             Integer whiteListIndex = (Integer)whiteListMap.get(hostPortSpec);
/*     */ 
/*     */ 
/*     */             
/*  84 */             if (whiteListIndex != null) {
/*  85 */               whiteList.remove(whiteListIndex.intValue());
/*     */             }
/*     */             
/*  88 */             if (whiteList.size() == 0) {
/*     */               try {
/*  90 */                 Thread.sleep(250L);
/*  91 */               } catch (InterruptedException e) {}
/*     */ 
/*     */ 
/*     */               
/*  95 */               whiteList.addAll(configuredHosts);
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 100 */             throw sqlEx;
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 105 */       return conn;
/*     */     } 
/*     */     
/* 108 */     if (ex != null) {
/* 109 */       throw ex;
/*     */     }
/*     */     
/* 112 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\RandomBalanceStrategy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */