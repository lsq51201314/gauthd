/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.NClob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.util.HashMap;
/*     */ import javax.sql.StatementEvent;
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
/*     */ 
/*     */ public class JDBC4PreparedStatementWrapper
/*     */   extends PreparedStatementWrapper
/*     */ {
/*     */   public JDBC4PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) {
/*  62 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   public void close() throws SQLException {
/*     */     try {
/*  67 */       super.close();
/*     */     } finally {
/*     */       try {
/*  70 */         ((JDBC4MysqlPooledConnection)this.pooledConnection).fireStatementEvent(new StatementEvent(this.pooledConnection, this));
/*     */       } finally {
/*     */         
/*  73 */         this.unwrappedInterfaces = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isClosed() throws SQLException {
/*     */     try {
/*  80 */       if (this.wrappedStmt != null) {
/*  81 */         return this.wrappedStmt.isClosed();
/*     */       }
/*  83 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     
/*     */     }
/*  86 */     catch (SQLException sqlEx) {
/*  87 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/*  90 */       return false;
/*     */     } 
/*     */   }
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/*  95 */       if (this.wrappedStmt != null) {
/*  96 */         this.wrappedStmt.setPoolable(poolable);
/*     */       } else {
/*  98 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */       }
/*     */     
/* 101 */     } catch (SQLException sqlEx) {
/* 102 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/* 108 */       if (this.wrappedStmt != null) {
/* 109 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 111 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*     */     
/*     */     }
/* 114 */     catch (SQLException sqlEx) {
/* 115 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 118 */       return false;
/*     */     } 
/*     */   }
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*     */     try {
/* 123 */       if (this.wrappedStmt != null) {
/* 124 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*     */       } else {
/*     */         
/* 127 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 131 */     catch (SQLException sqlEx) {
/* 132 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*     */     try {
/* 138 */       if (this.wrappedStmt != null) {
/* 139 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
/*     */       } else {
/*     */         
/* 142 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 146 */     catch (SQLException sqlEx) {
/* 147 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*     */     try {
/* 154 */       if (this.wrappedStmt != null) {
/* 155 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*     */       } else {
/*     */         
/* 158 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 162 */     catch (SQLException sqlEx) {
/* 163 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNString(int parameterIndex, String value) throws SQLException {
/*     */     try {
/* 172 */       if (this.wrappedStmt != null) {
/* 173 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*     */       } else {
/*     */         
/* 176 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 180 */     catch (SQLException sqlEx) {
/* 181 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
/*     */     try {
/* 190 */       if (this.wrappedStmt != null) {
/* 191 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*     */       } else {
/*     */         
/* 194 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 198 */     catch (SQLException sqlEx) {
/* 199 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 208 */       if (this.wrappedStmt != null) {
/* 209 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*     */       } else {
/*     */         
/* 212 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 216 */     catch (SQLException sqlEx) {
/* 217 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/*     */     try {
/* 226 */       if (this.wrappedStmt != null) {
/* 227 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*     */       } else {
/*     */         
/* 230 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 234 */     catch (SQLException sqlEx) {
/* 235 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 244 */       if (this.wrappedStmt != null) {
/* 245 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*     */       } else {
/*     */         
/* 248 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 252 */     catch (SQLException sqlEx) {
/* 253 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 262 */       if (this.wrappedStmt != null) {
/* 263 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/*     */         
/* 266 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 270 */     catch (SQLException sqlEx) {
/* 271 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 280 */       if (this.wrappedStmt != null) {
/* 281 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/*     */         
/* 284 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 288 */     catch (SQLException sqlEx) {
/* 289 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 298 */       if (this.wrappedStmt != null) {
/* 299 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/*     */         
/* 302 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 306 */     catch (SQLException sqlEx) {
/* 307 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 315 */       if (this.wrappedStmt != null) {
/* 316 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*     */       } else {
/*     */         
/* 319 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 323 */     catch (SQLException sqlEx) {
/* 324 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 332 */       if (this.wrappedStmt != null) {
/* 333 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*     */       } else {
/*     */         
/* 336 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 340 */     catch (SQLException sqlEx) {
/* 341 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 349 */       if (this.wrappedStmt != null) {
/* 350 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*     */       } else {
/*     */         
/* 353 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 357 */     catch (SQLException sqlEx) {
/* 358 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/*     */     try {
/* 367 */       if (this.wrappedStmt != null) {
/* 368 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*     */       } else {
/*     */         
/* 371 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 375 */     catch (SQLException sqlEx) {
/* 376 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 385 */       if (this.wrappedStmt != null) {
/* 386 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*     */       } else {
/*     */         
/* 389 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 393 */     catch (SQLException sqlEx) {
/* 394 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/*     */     try {
/* 403 */       if (this.wrappedStmt != null) {
/* 404 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*     */       } else {
/*     */         
/* 407 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 411 */     catch (SQLException sqlEx) {
/* 412 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 420 */       if (this.wrappedStmt != null) {
/* 421 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*     */       } else {
/*     */         
/* 424 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 428 */     catch (SQLException sqlEx) {
/* 429 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 457 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 459 */     if (isInstance) {
/* 460 */       return true;
/*     */     }
/*     */     
/* 463 */     String interfaceClassName = iface.getName();
/*     */     
/* 465 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName.equals("java.sql.PreparedStatement") || interfaceClassName.equals("java.sql.Wrapper"));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 493 */       if ("java.sql.Statement".equals(iface.getName()) || "java.sql.PreparedStatement".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName()))
/*     */       {
/*     */         
/* 496 */         return iface.cast(this);
/*     */       }
/*     */       
/* 499 */       if (this.unwrappedInterfaces == null) {
/* 500 */         this.unwrappedInterfaces = new HashMap<Object, Object>();
/*     */       }
/*     */       
/* 503 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 505 */       if (cachedUnwrapped == null) {
/* 506 */         if (cachedUnwrapped == null) {
/* 507 */           cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */ 
/*     */ 
/*     */           
/* 511 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */         } 
/* 513 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       } 
/*     */       
/* 516 */       return iface.cast(cachedUnwrapped);
/* 517 */     } catch (ClassCastException cce) {
/* 518 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4PreparedStatementWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */