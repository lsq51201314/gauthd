/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoadBalancingConnectionProxy
/*     */   implements InvocationHandler, PingTarget
/*     */ {
/*     */   private static Method getLocalTimeMethod;
/*     */   private Connection currentConn;
/*     */   private List hostList;
/*     */   private Map liveConnections;
/*     */   private Map connectionsToHostsMap;
/*     */   private long[] responseTimes;
/*     */   private Map hostsToListIndexMap;
/*     */   
/*     */   static {
/*     */     try {
/*  64 */       getLocalTimeMethod = System.class.getMethod("nanoTime", new Class[0]);
/*     */     }
/*  66 */     catch (SecurityException e) {
/*     */     
/*  68 */     } catch (NoSuchMethodException e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   protected class ConnectionErrorFiringInvocationHandler
/*     */     implements InvocationHandler
/*     */   {
/*     */     Object invokeOn;
/*     */     private final LoadBalancingConnectionProxy this$0;
/*     */     
/*     */     public ConnectionErrorFiringInvocationHandler(LoadBalancingConnectionProxy this$0, Object toInvokeOn) {
/*  79 */       this.this$0 = this$0; this.invokeOn = null;
/*  80 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  85 */       Object result = null;
/*     */       
/*     */       try {
/*  88 */         result = method.invoke(this.invokeOn, args);
/*     */         
/*  90 */         if (result != null) {
/*  91 */           result = this.this$0.proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */         }
/*  93 */       } catch (InvocationTargetException e) {
/*  94 */         this.this$0.dealWithInvocationException(e);
/*     */       } 
/*     */       
/*  97 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean inTransaction = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   private long transactionStartTime = 0L;
/*     */ 
/*     */ 
/*     */   
/*     */   private Properties localProps;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isClosed = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private BalanceStrategy balancer;
/*     */ 
/*     */ 
/*     */   
/*     */   private int retriesAllDown;
/*     */ 
/*     */ 
/*     */   
/*     */   LoadBalancingConnectionProxy(List hosts, Properties props) throws SQLException {
/* 136 */     this.hostList = hosts;
/*     */     
/* 138 */     int numHosts = this.hostList.size();
/*     */     
/* 140 */     this.liveConnections = new HashMap(numHosts);
/* 141 */     this.connectionsToHostsMap = new HashMap(numHosts);
/* 142 */     this.responseTimes = new long[numHosts];
/* 143 */     this.hostsToListIndexMap = new HashMap(numHosts);
/*     */     
/* 145 */     for (int i = 0; i < numHosts; i++) {
/* 146 */       this.hostsToListIndexMap.put(this.hostList.get(i), new Integer(i));
/*     */     }
/*     */     
/* 149 */     this.localProps = (Properties)props.clone();
/* 150 */     this.localProps.remove("HOST");
/* 151 */     this.localProps.remove("PORT");
/* 152 */     this.localProps.setProperty("useLocalSessionState", "true");
/*     */     
/* 154 */     String strategy = this.localProps.getProperty("loadBalanceStrategy", "random");
/*     */ 
/*     */     
/* 157 */     String retriesAllDownAsString = this.localProps.getProperty("retriesAllDown", "120");
/*     */     
/*     */     try {
/* 160 */       this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
/* 161 */     } catch (NumberFormatException nfe) {
/* 162 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 168 */     if ("random".equals(strategy)) {
/* 169 */       this.balancer = Util.loadExtensions(null, props, "com.mysql.jdbc.RandomBalanceStrategy", "InvalidLoadBalanceStrategy").get(0);
/*     */     
/*     */     }
/* 172 */     else if ("bestResponseTime".equals(strategy)) {
/* 173 */       this.balancer = Util.loadExtensions(null, props, "com.mysql.jdbc.BestResponseTimeBalanceStrategy", "InvalidLoadBalanceStrategy").get(0);
/*     */     }
/*     */     else {
/*     */       
/* 177 */       this.balancer = Util.loadExtensions(null, props, strategy, "InvalidLoadBalanceStrategy").get(0);
/*     */     } 
/*     */ 
/*     */     
/* 181 */     this.balancer.init(null, props);
/*     */     
/* 183 */     pickNewConnection();
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
/*     */   public synchronized Connection createConnectionForHost(String hostPortSpec) throws SQLException {
/* 196 */     Properties connProps = (Properties)this.localProps.clone();
/*     */     
/* 198 */     String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(hostPortSpec);
/*     */ 
/*     */     
/* 201 */     if (hostPortPair[1] == null) {
/* 202 */       hostPortPair[1] = "3306";
/*     */     }
/*     */     
/* 205 */     connProps.setProperty("HOST", hostPortSpec);
/*     */     
/* 207 */     connProps.setProperty("PORT", hostPortPair[1]);
/*     */ 
/*     */     
/* 210 */     Connection conn = ConnectionImpl.getInstance(hostPortSpec, Integer.parseInt(hostPortPair[1]), connProps, connProps.getProperty("DBNAME"), "jdbc:mysql://" + hostPortPair[0] + ":" + hostPortPair[1] + "/");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 215 */     this.liveConnections.put(hostPortSpec, conn);
/* 216 */     this.connectionsToHostsMap.put(conn, hostPortSpec);
/*     */     
/* 218 */     return conn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void dealWithInvocationException(InvocationTargetException e) throws SQLException, Throwable, InvocationTargetException {
/* 229 */     Throwable t = e.getTargetException();
/*     */     
/* 231 */     if (t != null) {
/* 232 */       if (t instanceof SQLException) {
/* 233 */         String sqlState = ((SQLException)t).getSQLState();
/*     */         
/* 235 */         if (sqlState != null && 
/* 236 */           sqlState.startsWith("08"))
/*     */         {
/*     */           
/* 239 */           invalidateCurrentConnection();
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 244 */       throw t;
/*     */     } 
/*     */     
/* 247 */     throw e;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void invalidateCurrentConnection() throws SQLException {
/*     */     try {
/* 257 */       if (!this.currentConn.isClosed()) {
/* 258 */         this.currentConn.close();
/*     */       }
/*     */     } finally {
/*     */       
/* 262 */       this.liveConnections.remove(this.connectionsToHostsMap.get(this.currentConn));
/*     */       
/* 264 */       this.connectionsToHostsMap.remove(this.currentConn);
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
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 276 */     String methodName = method.getName();
/*     */     
/* 278 */     if ("close".equals(methodName)) {
/* 279 */       synchronized (this.liveConnections) {
/*     */         
/* 281 */         Iterator allConnections = this.liveConnections.values().iterator();
/*     */ 
/*     */         
/* 284 */         while (allConnections.hasNext()) {
/* 285 */           ((Connection)allConnections.next()).close();
/*     */         }
/*     */         
/* 288 */         if (!this.isClosed) {
/* 289 */           this.balancer.destroy();
/*     */         }
/*     */         
/* 292 */         this.liveConnections.clear();
/* 293 */         this.connectionsToHostsMap.clear();
/*     */       } 
/*     */       
/* 296 */       return null;
/*     */     } 
/*     */     
/* 299 */     if ("isClosed".equals(methodName)) {
/* 300 */       return Boolean.valueOf(this.isClosed);
/*     */     }
/*     */     
/* 303 */     if (this.isClosed) {
/* 304 */       throw SQLError.createSQLException("No operations allowed after connection closed.", "08003");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 309 */     if (!this.inTransaction) {
/* 310 */       this.inTransaction = true;
/* 311 */       this.transactionStartTime = getLocalTimeBestResolution();
/*     */     } 
/*     */     
/* 314 */     Object result = null;
/*     */     
/*     */     try {
/* 317 */       result = method.invoke(this.currentConn, args);
/*     */       
/* 319 */       if (result != null) {
/* 320 */         if (result instanceof Statement) {
/* 321 */           ((Statement)result).setPingTarget(this);
/*     */         }
/*     */         
/* 324 */         result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */       } 
/* 326 */     } catch (InvocationTargetException e) {
/* 327 */       dealWithInvocationException(e);
/*     */     } finally {
/* 329 */       if ("commit".equals(methodName) || "rollback".equals(methodName)) {
/* 330 */         this.inTransaction = false;
/*     */ 
/*     */         
/* 333 */         int hostIndex = ((Integer)this.hostsToListIndexMap.get(this.connectionsToHostsMap.get(this.currentConn))).intValue();
/*     */ 
/*     */ 
/*     */         
/* 337 */         synchronized (this.responseTimes) {
/* 338 */           this.responseTimes[hostIndex] = getLocalTimeBestResolution() - this.transactionStartTime;
/*     */         } 
/*     */ 
/*     */         
/* 342 */         pickNewConnection();
/*     */       } 
/*     */     } 
/*     */     
/* 346 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void pickNewConnection() throws SQLException {
/* 356 */     if (this.currentConn == null) {
/* 357 */       this.currentConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 366 */     Connection newConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 372 */     newConn.setTransactionIsolation(this.currentConn.getTransactionIsolation());
/*     */     
/* 374 */     newConn.setAutoCommit(this.currentConn.getAutoCommit());
/* 375 */     this.currentConn = newConn;
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
/*     */   Object proxyIfInterfaceIsJdbc(Object toProxy, Class clazz) {
/* 388 */     Class[] interfaces = clazz.getInterfaces();
/*     */     
/* 390 */     int i = 0; if (i < interfaces.length) {
/* 391 */       String packageName = interfaces[i].getPackage().getName();
/*     */       
/* 393 */       if ("java.sql".equals(packageName) || "javax.sql".equals(packageName))
/*     */       {
/* 395 */         return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(this, toProxy));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 400 */       return proxyIfInterfaceIsJdbc(toProxy, interfaces[i]);
/*     */     } 
/*     */     
/* 403 */     return toProxy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long getLocalTimeBestResolution() {
/* 411 */     if (getLocalTimeMethod != null) {
/*     */       try {
/* 413 */         return ((Long)getLocalTimeMethod.invoke(null, null)).longValue();
/*     */       }
/* 415 */       catch (IllegalArgumentException e) {
/*     */       
/* 417 */       } catch (IllegalAccessException e) {
/*     */       
/* 419 */       } catch (InvocationTargetException e) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 424 */     return System.currentTimeMillis();
/*     */   }
/*     */   
/*     */   public synchronized void doPing() throws SQLException {
/* 428 */     Iterator allConns = this.liveConnections.values().iterator();
/*     */     
/* 430 */     while (allConns.hasNext())
/* 431 */       ((Connection)allConns.next()).ping(); 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\LoadBalancingConnectionProxy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */