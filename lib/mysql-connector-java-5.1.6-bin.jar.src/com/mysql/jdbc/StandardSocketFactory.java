/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*     */   public static final String TCP_NO_DELAY_PROPERTY_NAME = "tcpNoDelay";
/*     */   public static final String TCP_KEEP_ALIVE_DEFAULT_VALUE = "true";
/*     */   public static final String TCP_KEEP_ALIVE_PROPERTY_NAME = "tcpKeepAlive";
/*     */   public static final String TCP_RCV_BUF_PROPERTY_NAME = "tcpRcvBuf";
/*     */   public static final String TCP_SND_BUF_PROPERTY_NAME = "tcpSndBuf";
/*     */   public static final String TCP_TRAFFIC_CLASS_PROPERTY_NAME = "tcpTrafficClass";
/*     */   public static final String TCP_RCV_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_SND_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_TRAFFIC_CLASS_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_NO_DELAY_DEFAULT_VALUE = "true";
/*     */   private static Method setTraficClassMethod;
/*     */   
/*     */   static {
/*     */     try {
/*  69 */       setTraficClassMethod = Socket.class.getMethod("setTrafficClass", new Class[] { int.class });
/*     */     }
/*  71 */     catch (SecurityException e) {
/*  72 */       setTraficClassMethod = null;
/*  73 */     } catch (NoSuchMethodException e) {
/*  74 */       setTraficClassMethod = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  79 */   protected String host = null;
/*     */ 
/*     */   
/*  82 */   protected int port = 3306;
/*     */ 
/*     */   
/*  85 */   protected Socket rawSocket = null;
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
/*     */   public Socket afterHandshake() throws SocketException, IOException {
/*  99 */     return this.rawSocket;
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
/*     */   public Socket beforeHandshake() throws SocketException, IOException {
/* 114 */     return this.rawSocket;
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
/*     */   private void configureSocket(Socket sock, Properties props) throws SocketException, IOException {
/*     */     try {
/* 128 */       sock.setTcpNoDelay(Boolean.valueOf(props.getProperty("tcpNoDelay", "true")).booleanValue());
/*     */ 
/*     */ 
/*     */       
/* 132 */       String keepAlive = props.getProperty("tcpKeepAlive", "true");
/*     */ 
/*     */       
/* 135 */       if (keepAlive != null && keepAlive.length() > 0) {
/* 136 */         sock.setKeepAlive(Boolean.valueOf(keepAlive).booleanValue());
/*     */       }
/*     */ 
/*     */       
/* 140 */       int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */ 
/*     */       
/* 143 */       if (receiveBufferSize > 0) {
/* 144 */         sock.setReceiveBufferSize(receiveBufferSize);
/*     */       }
/*     */       
/* 147 */       int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */ 
/*     */       
/* 150 */       if (sendBufferSize > 0) {
/* 151 */         sock.setSendBufferSize(sendBufferSize);
/*     */       }
/*     */       
/* 154 */       int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */ 
/*     */ 
/*     */       
/* 158 */       if (trafficClass > 0 && setTraficClassMethod != null) {
/* 159 */         setTraficClassMethod.invoke(sock, new Object[] { new Integer(trafficClass) });
/*     */       }
/*     */     }
/* 162 */     catch (Throwable t) {
/* 163 */       unwrapExceptionToProperClassAndThrowIt(t);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket connect(String hostname, int portNumber, Properties props) throws SocketException, IOException {
/* 173 */     if (props != null) {
/* 174 */       this.host = hostname;
/*     */       
/* 176 */       this.port = portNumber;
/*     */       
/* 178 */       Method connectWithTimeoutMethod = null;
/* 179 */       Method socketBindMethod = null;
/* 180 */       Class socketAddressClass = null;
/*     */       
/* 182 */       String localSocketHostname = props.getProperty("localSocketAddress");
/*     */ 
/*     */       
/* 185 */       String connectTimeoutStr = props.getProperty("connectTimeout");
/*     */       
/* 187 */       int connectTimeout = 0;
/*     */       
/* 189 */       boolean wantsTimeout = (connectTimeoutStr != null && connectTimeoutStr.length() > 0 && !connectTimeoutStr.equals("0"));
/*     */ 
/*     */ 
/*     */       
/* 193 */       boolean wantsLocalBind = (localSocketHostname != null && localSocketHostname.length() > 0);
/*     */ 
/*     */       
/* 196 */       boolean needsConfigurationBeforeConnect = socketNeedsConfigurationBeforeConnect(props);
/*     */       
/* 198 */       if (wantsTimeout || wantsLocalBind || needsConfigurationBeforeConnect) {
/*     */         
/* 200 */         if (connectTimeoutStr != null) {
/*     */           try {
/* 202 */             connectTimeout = Integer.parseInt(connectTimeoutStr);
/* 203 */           } catch (NumberFormatException nfe) {
/* 204 */             throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
/*     */           } 
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 212 */           socketAddressClass = Class.forName("java.net.SocketAddress");
/*     */ 
/*     */           
/* 215 */           connectWithTimeoutMethod = Socket.class.getMethod("connect", new Class[] { socketAddressClass, int.class });
/*     */ 
/*     */ 
/*     */           
/* 219 */           socketBindMethod = Socket.class.getMethod("bind", new Class[] { socketAddressClass });
/*     */         
/*     */         }
/* 222 */         catch (NoClassDefFoundError noClassDefFound) {
/*     */         
/* 224 */         } catch (NoSuchMethodException noSuchMethodEx) {
/*     */         
/* 226 */         } catch (Throwable catchAll) {}
/*     */ 
/*     */ 
/*     */         
/* 230 */         if (wantsLocalBind && socketBindMethod == null) {
/* 231 */           throw new SocketException("Can't specify \"localSocketAddress\" on JVMs older than 1.4");
/*     */         }
/*     */ 
/*     */         
/* 235 */         if (wantsTimeout && connectWithTimeoutMethod == null) {
/* 236 */           throw new SocketException("Can't specify \"connectTimeout\" on JVMs older than 1.4");
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 241 */       if (this.host != null) {
/* 242 */         if (!wantsLocalBind && !wantsTimeout && !needsConfigurationBeforeConnect) {
/* 243 */           InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/*     */           
/* 246 */           Throwable caughtWhileConnecting = null;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 251 */           for (int i = 0; i < possibleAddresses.length; i++) {
/*     */             try {
/* 253 */               this.rawSocket = new Socket(possibleAddresses[i], this.port);
/*     */ 
/*     */               
/* 256 */               configureSocket(this.rawSocket, props);
/*     */               
/*     */               break;
/* 259 */             } catch (Exception ex) {
/* 260 */               caughtWhileConnecting = ex;
/*     */             } 
/*     */           } 
/*     */           
/* 264 */           if (this.rawSocket == null) {
/* 265 */             unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
/*     */           }
/*     */         } else {
/*     */ 
/*     */           
/*     */           try {
/*     */             
/* 272 */             InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/*     */             
/* 275 */             Throwable caughtWhileConnecting = null;
/*     */             
/* 277 */             Object localSockAddr = null;
/*     */             
/* 279 */             Class inetSocketAddressClass = null;
/*     */             
/* 281 */             Constructor addrConstructor = null;
/*     */             
/*     */             try {
/* 284 */               inetSocketAddressClass = Class.forName("java.net.InetSocketAddress");
/*     */ 
/*     */               
/* 287 */               addrConstructor = inetSocketAddressClass.getConstructor(new Class[] { InetAddress.class, int.class });
/*     */ 
/*     */ 
/*     */               
/* 291 */               if (wantsLocalBind) {
/* 292 */                 localSockAddr = addrConstructor.newInstance(new Object[] { InetAddress.getByName(localSocketHostname), new Integer(0) });
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               }
/*     */ 
/*     */ 
/*     */             
/*     */             }
/* 302 */             catch (Throwable ex) {
/* 303 */               unwrapExceptionToProperClassAndThrowIt(ex);
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 309 */             for (int i = 0; i < possibleAddresses.length; i++) {
/*     */               
/*     */               try {
/* 312 */                 this.rawSocket = new Socket();
/*     */                 
/* 314 */                 configureSocket(this.rawSocket, props);
/*     */                 
/* 316 */                 Object sockAddr = addrConstructor.newInstance(new Object[] { possibleAddresses[i], new Integer(this.port) });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 323 */                 socketBindMethod.invoke(this.rawSocket, new Object[] { localSockAddr });
/*     */ 
/*     */                 
/* 326 */                 connectWithTimeoutMethod.invoke(this.rawSocket, new Object[] { sockAddr, new Integer(connectTimeout) });
/*     */ 
/*     */ 
/*     */                 
/*     */                 break;
/* 331 */               } catch (Exception ex) {
/* 332 */                 this.rawSocket = null;
/*     */                 
/* 334 */                 caughtWhileConnecting = ex;
/*     */               } 
/*     */             } 
/*     */             
/* 338 */             if (this.rawSocket == null) {
/* 339 */               unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
/*     */             }
/*     */           }
/* 342 */           catch (Throwable t) {
/* 343 */             unwrapExceptionToProperClassAndThrowIt(t);
/*     */           } 
/*     */         } 
/*     */         
/* 347 */         return this.rawSocket;
/*     */       } 
/*     */     } 
/*     */     
/* 351 */     throw new SocketException("Unable to create socket");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean socketNeedsConfigurationBeforeConnect(Properties props) {
/* 360 */     int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */ 
/*     */     
/* 363 */     if (receiveBufferSize > 0) {
/* 364 */       return true;
/*     */     }
/*     */     
/* 367 */     int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */ 
/*     */     
/* 370 */     if (sendBufferSize > 0) {
/* 371 */       return true;
/*     */     }
/*     */     
/* 374 */     int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */ 
/*     */ 
/*     */     
/* 378 */     if (trafficClass > 0 && setTraficClassMethod != null) {
/* 379 */       return true;
/*     */     }
/*     */     
/* 382 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void unwrapExceptionToProperClassAndThrowIt(Throwable caughtWhileConnecting) throws SocketException, IOException {
/* 388 */     if (caughtWhileConnecting instanceof InvocationTargetException)
/*     */     {
/*     */ 
/*     */       
/* 392 */       caughtWhileConnecting = ((InvocationTargetException)caughtWhileConnecting).getTargetException();
/*     */     }
/*     */ 
/*     */     
/* 396 */     if (caughtWhileConnecting instanceof SocketException) {
/* 397 */       throw (SocketException)caughtWhileConnecting;
/*     */     }
/*     */     
/* 400 */     if (caughtWhileConnecting instanceof IOException) {
/* 401 */       throw (IOException)caughtWhileConnecting;
/*     */     }
/*     */     
/* 404 */     throw new SocketException(caughtWhileConnecting.toString());
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\StandardSocketFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */