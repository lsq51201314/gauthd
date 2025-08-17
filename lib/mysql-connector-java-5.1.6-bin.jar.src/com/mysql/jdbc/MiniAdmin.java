/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ public class MiniAdmin
/*     */ {
/*     */   private Connection conn;
/*     */   
/*     */   public MiniAdmin(Connection conn) throws SQLException {
/*  54 */     if (conn == null) {
/*  55 */       throw SQLError.createSQLException(Messages.getString("MiniAdmin.0"), "S1000");
/*     */     }
/*     */ 
/*     */     
/*  59 */     if (!(conn instanceof Connection)) {
/*  60 */       throw SQLError.createSQLException(Messages.getString("MiniAdmin.1"), "S1000");
/*     */     }
/*     */ 
/*     */     
/*  64 */     this.conn = (Connection)conn;
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
/*     */   public MiniAdmin(String jdbcUrl) throws SQLException {
/*  77 */     this(jdbcUrl, new Properties());
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
/*     */   public MiniAdmin(String jdbcUrl, Properties props) throws SQLException {
/*  93 */     this.conn = (Connection)(new Driver()).connect(jdbcUrl, props);
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
/*     */   public void shutdown() throws SQLException {
/* 107 */     this.conn.shutdownServer();
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\MiniAdmin.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */