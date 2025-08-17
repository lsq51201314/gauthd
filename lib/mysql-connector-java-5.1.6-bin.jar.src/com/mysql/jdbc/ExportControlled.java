/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.sql.SQLException;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExportControlled
/*     */ {
/*     */   private static final String SQL_STATE_BAD_SSL_PARAMS = "08000";
/*     */   
/*     */   protected static boolean enabled() {
/*  61 */     return true;
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
/*     */   protected static void transformSocketToSSLSocket(MysqlIO mysqlIO) throws SQLException {
/*  79 */     SSLSocketFactory sslFact = getSSLSocketFactoryDefaultOrConfigured(mysqlIO);
/*     */     
/*     */     try {
/*  82 */       mysqlIO.mysqlConnection = sslFact.createSocket(mysqlIO.mysqlConnection, mysqlIO.host, mysqlIO.port, true);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  87 */       ((SSLSocket)mysqlIO.mysqlConnection).setEnabledProtocols(new String[] { "TLSv1" });
/*     */       
/*  89 */       ((SSLSocket)mysqlIO.mysqlConnection).startHandshake();
/*     */ 
/*     */       
/*  92 */       if (mysqlIO.connection.getUseUnbufferedInput()) {
/*  93 */         mysqlIO.mysqlInput = mysqlIO.mysqlConnection.getInputStream();
/*     */       } else {
/*  95 */         mysqlIO.mysqlInput = new BufferedInputStream(mysqlIO.mysqlConnection.getInputStream(), 16384);
/*     */       } 
/*     */ 
/*     */       
/*  99 */       mysqlIO.mysqlOutput = new BufferedOutputStream(mysqlIO.mysqlConnection.getOutputStream(), 16384);
/*     */ 
/*     */       
/* 102 */       mysqlIO.mysqlOutput.flush();
/* 103 */     } catch (IOException ioEx) {
/* 104 */       throw SQLError.createCommunicationsException(mysqlIO.connection, mysqlIO.getLastPacketSentTimeMs(), mysqlIO.getLastPacketReceivedTimeMs(), ioEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static SSLSocketFactory getSSLSocketFactoryDefaultOrConfigured(MysqlIO mysqlIO) throws SQLException {
/* 115 */     String clientCertificateKeyStoreUrl = mysqlIO.connection.getClientCertificateKeyStoreUrl();
/*     */     
/* 117 */     String trustCertificateKeyStoreUrl = mysqlIO.connection.getTrustCertificateKeyStoreUrl();
/*     */     
/* 119 */     String clientCertificateKeyStoreType = mysqlIO.connection.getClientCertificateKeyStoreType();
/*     */     
/* 121 */     String clientCertificateKeyStorePassword = mysqlIO.connection.getClientCertificateKeyStorePassword();
/*     */     
/* 123 */     String trustCertificateKeyStoreType = mysqlIO.connection.getTrustCertificateKeyStoreType();
/*     */     
/* 125 */     String trustCertificateKeyStorePassword = mysqlIO.connection.getTrustCertificateKeyStorePassword();
/*     */ 
/*     */     
/* 128 */     if (StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl) && StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl))
/*     */     {
/* 130 */       if (mysqlIO.connection.getVerifyServerCertificate()) {
/* 131 */         return (SSLSocketFactory)SSLSocketFactory.getDefault();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 136 */     TrustManagerFactory tmf = null;
/* 137 */     KeyManagerFactory kmf = null;
/*     */     
/*     */     try {
/* 140 */       tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*     */       
/* 142 */       kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/*     */     }
/* 144 */     catch (NoSuchAlgorithmException nsae) {
/* 145 */       throw SQLError.createSQLException("Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file.", "08000", 0, false);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     if (StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) {
/*     */       try {
/* 153 */         if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreType)) {
/* 154 */           KeyStore clientKeyStore = KeyStore.getInstance(clientCertificateKeyStoreType);
/*     */           
/* 156 */           URL ksURL = new URL(clientCertificateKeyStoreUrl);
/* 157 */           char[] password = (clientCertificateKeyStorePassword == null) ? new char[0] : clientCertificateKeyStorePassword.toCharArray();
/*     */           
/* 159 */           clientKeyStore.load(ksURL.openStream(), password);
/* 160 */           kmf.init(clientKeyStore, password);
/*     */         } 
/* 162 */       } catch (UnrecoverableKeyException uke) {
/* 163 */         throw SQLError.createSQLException("Could not recover keys from client keystore.  Check password?", "08000", 0, false);
/*     */ 
/*     */       
/*     */       }
/* 167 */       catch (NoSuchAlgorithmException nsae) {
/* 168 */         throw SQLError.createSQLException("Unsupported keystore algorithm [" + nsae.getMessage() + "]", "08000", 0, false);
/*     */       
/*     */       }
/* 171 */       catch (KeyStoreException kse) {
/* 172 */         throw SQLError.createSQLException("Could not create KeyStore instance [" + kse.getMessage() + "]", "08000", 0, false);
/*     */       
/*     */       }
/* 175 */       catch (CertificateException nsae) {
/* 176 */         throw SQLError.createSQLException("Could not load client" + clientCertificateKeyStoreType + " keystore from " + clientCertificateKeyStoreUrl);
/*     */       
/*     */       }
/* 179 */       catch (MalformedURLException mue) {
/* 180 */         throw SQLError.createSQLException(clientCertificateKeyStoreUrl + " does not appear to be a valid URL.", "08000", 0, false);
/*     */       
/*     */       }
/* 183 */       catch (IOException ioe) {
/* 184 */         SQLException sqlEx = SQLError.createSQLException("Cannot open " + clientCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", "08000", 0, false);
/*     */ 
/*     */         
/* 187 */         sqlEx.initCause(ioe);
/*     */         
/* 189 */         throw sqlEx;
/*     */       } 
/*     */     }
/*     */     
/* 193 */     if (StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl)) {
/*     */       
/*     */       try {
/* 196 */         if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreType)) {
/* 197 */           KeyStore trustKeyStore = KeyStore.getInstance(trustCertificateKeyStoreType);
/*     */           
/* 199 */           URL ksURL = new URL(trustCertificateKeyStoreUrl);
/*     */           
/* 201 */           char[] password = (trustCertificateKeyStorePassword == null) ? new char[0] : trustCertificateKeyStorePassword.toCharArray();
/*     */           
/* 203 */           trustKeyStore.load(ksURL.openStream(), password);
/* 204 */           tmf.init(trustKeyStore);
/*     */         } 
/* 206 */       } catch (NoSuchAlgorithmException nsae) {
/* 207 */         throw SQLError.createSQLException("Unsupported keystore algorithm [" + nsae.getMessage() + "]", "08000", 0, false);
/*     */       
/*     */       }
/* 210 */       catch (KeyStoreException kse) {
/* 211 */         throw SQLError.createSQLException("Could not create KeyStore instance [" + kse.getMessage() + "]", "08000", 0, false);
/*     */       
/*     */       }
/* 214 */       catch (CertificateException nsae) {
/* 215 */         throw SQLError.createSQLException("Could not load trust" + trustCertificateKeyStoreType + " keystore from " + trustCertificateKeyStoreUrl, "08000", 0, false);
/*     */       
/*     */       }
/* 218 */       catch (MalformedURLException mue) {
/* 219 */         throw SQLError.createSQLException(trustCertificateKeyStoreUrl + " does not appear to be a valid URL.", "08000", 0, false);
/*     */       
/*     */       }
/* 222 */       catch (IOException ioe) {
/* 223 */         SQLException sqlEx = SQLError.createSQLException("Cannot open " + trustCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", "08000", 0, false);
/*     */ 
/*     */ 
/*     */         
/* 227 */         sqlEx.initCause(ioe);
/*     */         
/* 229 */         throw sqlEx;
/*     */       } 
/*     */     }
/*     */     
/* 233 */     SSLContext sslContext = null;
/*     */     
/*     */     try {
/* 236 */       sslContext = SSLContext.getInstance("TLS");
/* 237 */       (new X509TrustManager[1])[0] = new X509TrustManager()
/*     */         {
/*     */           public void checkClientTrusted(X509Certificate[] chain, String authType) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public X509Certificate[] getAcceptedIssuers() {
/* 251 */             return null;
/*     */           }
/*     */         };
/*     */       sslContext.init(StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl) ? null : kmf.getKeyManagers(), mysqlIO.connection.getVerifyServerCertificate() ? tmf.getTrustManagers() : (TrustManager[])new X509TrustManager[1], null);
/* 255 */       return sslContext.getSocketFactory();
/* 256 */     } catch (NoSuchAlgorithmException nsae) {
/* 257 */       throw SQLError.createSQLException("TLS is not a valid SSL protocol.", "08000", 0, false);
/*     */     
/*     */     }
/* 260 */     catch (KeyManagementException kme) {
/* 261 */       throw SQLError.createSQLException("KeyManagementException: " + kme.getMessage(), "08000", 0, false);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ExportControlled.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */