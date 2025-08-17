/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.NClob;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLXML;
/*      */ import java.util.HashMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JDBC4CallableStatementWrapper
/*      */   extends CallableStatementWrapper
/*      */ {
/*      */   public JDBC4CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) {
/*   62 */     super(c, conn, toWrap);
/*      */   }
/*      */   
/*      */   public void close() throws SQLException {
/*      */     try {
/*   67 */       super.close();
/*      */     } finally {
/*   69 */       this.unwrappedInterfaces = null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isClosed() throws SQLException {
/*      */     try {
/*   75 */       if (this.wrappedStmt != null) {
/*   76 */         return this.wrappedStmt.isClosed();
/*      */       }
/*   78 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*      */     
/*      */     }
/*   81 */     catch (SQLException sqlEx) {
/*   82 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*   85 */       return false;
/*      */     } 
/*      */   }
/*      */   public void setPoolable(boolean poolable) throws SQLException {
/*      */     try {
/*   90 */       if (this.wrappedStmt != null) {
/*   91 */         this.wrappedStmt.setPoolable(poolable);
/*      */       } else {
/*   93 */         throw SQLError.createSQLException("Statement already closed", "S1009");
/*      */       }
/*      */     
/*   96 */     } catch (SQLException sqlEx) {
/*   97 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isPoolable() throws SQLException {
/*      */     try {
/*  103 */       if (this.wrappedStmt != null) {
/*  104 */         return this.wrappedStmt.isPoolable();
/*      */       }
/*  106 */       throw SQLError.createSQLException("Statement already closed", "S1009");
/*      */     
/*      */     }
/*  109 */     catch (SQLException sqlEx) {
/*  110 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  113 */       return false;
/*      */     } 
/*      */   }
/*      */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*      */     try {
/*  118 */       if (this.wrappedStmt != null) {
/*  119 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*      */       } else {
/*      */         
/*  122 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  126 */     catch (SQLException sqlEx) {
/*  127 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*      */     try {
/*  133 */       if (this.wrappedStmt != null) {
/*  134 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
/*      */       } else {
/*      */         
/*  137 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  141 */     catch (SQLException sqlEx) {
/*  142 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*      */     try {
/*  149 */       if (this.wrappedStmt != null) {
/*  150 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*      */       } else {
/*      */         
/*  153 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  157 */     catch (SQLException sqlEx) {
/*  158 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNString(int parameterIndex, String value) throws SQLException {
/*      */     try {
/*  167 */       if (this.wrappedStmt != null) {
/*  168 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*      */       } else {
/*      */         
/*  171 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  175 */     catch (SQLException sqlEx) {
/*  176 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
/*      */     try {
/*  185 */       if (this.wrappedStmt != null) {
/*  186 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*      */       } else {
/*      */         
/*  189 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  193 */     catch (SQLException sqlEx) {
/*  194 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     try {
/*  203 */       if (this.wrappedStmt != null) {
/*  204 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*      */       } else {
/*      */         
/*  207 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  211 */     catch (SQLException sqlEx) {
/*  212 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/*      */     try {
/*  221 */       if (this.wrappedStmt != null) {
/*  222 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*      */       } else {
/*      */         
/*  225 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  229 */     catch (SQLException sqlEx) {
/*  230 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     try {
/*  239 */       if (this.wrappedStmt != null) {
/*  240 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*      */       } else {
/*      */         
/*  243 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  247 */     catch (SQLException sqlEx) {
/*  248 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*      */     try {
/*  257 */       if (this.wrappedStmt != null) {
/*  258 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*      */       } else {
/*      */         
/*  261 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  265 */     catch (SQLException sqlEx) {
/*  266 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*      */     try {
/*  275 */       if (this.wrappedStmt != null) {
/*  276 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*      */       } else {
/*      */         
/*  279 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  283 */     catch (SQLException sqlEx) {
/*  284 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     try {
/*  293 */       if (this.wrappedStmt != null) {
/*  294 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*      */       } else {
/*      */         
/*  297 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  301 */     catch (SQLException sqlEx) {
/*  302 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/*      */     try {
/*  310 */       if (this.wrappedStmt != null) {
/*  311 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*      */       } else {
/*      */         
/*  314 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  318 */     catch (SQLException sqlEx) {
/*  319 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/*      */     try {
/*  327 */       if (this.wrappedStmt != null) {
/*  328 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*      */       } else {
/*      */         
/*  331 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  335 */     catch (SQLException sqlEx) {
/*  336 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/*      */     try {
/*  344 */       if (this.wrappedStmt != null) {
/*  345 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*      */       } else {
/*      */         
/*  348 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  352 */     catch (SQLException sqlEx) {
/*  353 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/*      */     try {
/*  362 */       if (this.wrappedStmt != null) {
/*  363 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*      */       } else {
/*      */         
/*  366 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  370 */     catch (SQLException sqlEx) {
/*  371 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClob(int parameterIndex, Reader reader) throws SQLException {
/*      */     try {
/*  380 */       if (this.wrappedStmt != null) {
/*  381 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*      */       } else {
/*      */         
/*  384 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  388 */     catch (SQLException sqlEx) {
/*  389 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/*      */     try {
/*  398 */       if (this.wrappedStmt != null) {
/*  399 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*      */       } else {
/*      */         
/*  402 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  406 */     catch (SQLException sqlEx) {
/*  407 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/*      */     try {
/*  415 */       if (this.wrappedStmt != null) {
/*  416 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*      */       } else {
/*      */         
/*  419 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  423 */     catch (SQLException sqlEx) {
/*  424 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*  452 */     boolean isInstance = iface.isInstance(this);
/*      */     
/*  454 */     if (isInstance) {
/*  455 */       return true;
/*      */     }
/*      */     
/*  458 */     String interfaceClassName = iface.getName();
/*      */     
/*  460 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName.equals("java.sql.PreparedStatement") || interfaceClassName.equals("java.sql.Wrapper"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     try {
/*  488 */       if ("java.sql.Statement".equals(iface.getName()) || "java.sql.PreparedStatement".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName()))
/*      */       {
/*      */         
/*  491 */         return iface.cast(this);
/*      */       }
/*      */       
/*  494 */       if (this.unwrappedInterfaces == null) {
/*  495 */         this.unwrappedInterfaces = new HashMap<Object, Object>();
/*      */       }
/*      */       
/*  498 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*      */       
/*  500 */       if (cachedUnwrapped == null) {
/*  501 */         if (cachedUnwrapped == null) {
/*  502 */           cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*      */ 
/*      */ 
/*      */           
/*  506 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*      */         } 
/*  508 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*      */       } 
/*      */       
/*  511 */       return iface.cast(cachedUnwrapped);
/*  512 */     } catch (ClassCastException cce) {
/*  513 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*      */     try {
/*  520 */       if (this.wrappedStmt != null) {
/*  521 */         ((CallableStatement)this.wrappedStmt).setRowId(parameterName, x);
/*      */       } else {
/*  523 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  527 */     catch (SQLException sqlEx) {
/*  528 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*      */     try {
/*  534 */       if (this.wrappedStmt != null) {
/*  535 */         ((CallableStatement)this.wrappedStmt).setSQLXML(parameterName, xmlObject);
/*      */       } else {
/*  537 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  541 */     catch (SQLException sqlEx) {
/*  542 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public SQLXML getSQLXML(int parameterIndex) throws SQLException {
/*      */     try {
/*  548 */       if (this.wrappedStmt != null) {
/*  549 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterIndex);
/*      */       }
/*  551 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  555 */     catch (SQLException sqlEx) {
/*  556 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  559 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public SQLXML getSQLXML(String parameterName) throws SQLException {
/*      */     try {
/*  565 */       if (this.wrappedStmt != null) {
/*  566 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterName);
/*      */       }
/*  568 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  572 */     catch (SQLException sqlEx) {
/*  573 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  576 */       return null;
/*      */     } 
/*      */   }
/*      */   public RowId getRowId(String parameterName) throws SQLException {
/*      */     try {
/*  581 */       if (this.wrappedStmt != null) {
/*  582 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterName);
/*      */       }
/*  584 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  588 */     catch (SQLException sqlEx) {
/*  589 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  592 */       return null;
/*      */     } 
/*      */   }
/*      */   public void setNClob(String parameterName, NClob value) throws SQLException {
/*      */     try {
/*  597 */       if (this.wrappedStmt != null) {
/*  598 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, value);
/*      */       } else {
/*  600 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  604 */     catch (SQLException sqlEx) {
/*  605 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  611 */       if (this.wrappedStmt != null) {
/*  612 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader);
/*      */       } else {
/*  614 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  618 */     catch (SQLException sqlEx) {
/*  619 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  625 */       if (this.wrappedStmt != null) {
/*  626 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader, length);
/*      */       } else {
/*  628 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  632 */     catch (SQLException sqlEx) {
/*  633 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNString(String parameterName, String value) throws SQLException {
/*      */     try {
/*  639 */       if (this.wrappedStmt != null) {
/*  640 */         ((CallableStatement)this.wrappedStmt).setNString(parameterName, value);
/*      */       } else {
/*  642 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  646 */     catch (SQLException sqlEx) {
/*  647 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getCharacterStream(int parameterIndex) throws SQLException {
/*      */     try {
/*  656 */       if (this.wrappedStmt != null) {
/*  657 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterIndex);
/*      */       }
/*  659 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  663 */     catch (SQLException sqlEx) {
/*  664 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  667 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getCharacterStream(String parameterName) throws SQLException {
/*      */     try {
/*  675 */       if (this.wrappedStmt != null) {
/*  676 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterName);
/*      */       }
/*  678 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  682 */     catch (SQLException sqlEx) {
/*  683 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  686 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/*      */     try {
/*  694 */       if (this.wrappedStmt != null) {
/*  695 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterIndex);
/*      */       }
/*  697 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  701 */     catch (SQLException sqlEx) {
/*  702 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  705 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getNCharacterStream(String parameterName) throws SQLException {
/*      */     try {
/*  713 */       if (this.wrappedStmt != null) {
/*  714 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterName);
/*      */       }
/*  716 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  720 */     catch (SQLException sqlEx) {
/*  721 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  724 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public NClob getNClob(String parameterName) throws SQLException {
/*      */     try {
/*  732 */       if (this.wrappedStmt != null) {
/*  733 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterName);
/*      */       }
/*  735 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  739 */     catch (SQLException sqlEx) {
/*  740 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  743 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNString(String parameterName) throws SQLException {
/*      */     try {
/*  751 */       if (this.wrappedStmt != null) {
/*  752 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterName);
/*      */       }
/*  754 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  758 */     catch (SQLException sqlEx) {
/*  759 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  762 */       return null;
/*      */     } 
/*      */   }
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/*  767 */       if (this.wrappedStmt != null) {
/*  768 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x);
/*      */       } else {
/*  770 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  774 */     catch (SQLException sqlEx) {
/*  775 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/*  781 */       if (this.wrappedStmt != null) {
/*  782 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       } else {
/*  784 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  788 */     catch (SQLException sqlEx) {
/*  789 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/*  795 */       if (this.wrappedStmt != null) {
/*  796 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x);
/*      */       } else {
/*  798 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  802 */     catch (SQLException sqlEx) {
/*  803 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/*  809 */       if (this.wrappedStmt != null) {
/*  810 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       } else {
/*  812 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  816 */     catch (SQLException sqlEx) {
/*  817 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/*  823 */       if (this.wrappedStmt != null) {
/*  824 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*      */       } else {
/*  826 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  830 */     catch (SQLException sqlEx) {
/*  831 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/*  837 */       if (this.wrappedStmt != null) {
/*  838 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x, length);
/*      */       } else {
/*  840 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  844 */     catch (SQLException sqlEx) {
/*  845 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException {
/*      */     try {
/*  851 */       if (this.wrappedStmt != null) {
/*  852 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*      */       } else {
/*  854 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  858 */     catch (SQLException sqlEx) {
/*  859 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  865 */       if (this.wrappedStmt != null) {
/*  866 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader);
/*      */       } else {
/*  868 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  872 */     catch (SQLException sqlEx) {
/*  873 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  879 */       if (this.wrappedStmt != null) {
/*  880 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       } else {
/*  882 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  886 */     catch (SQLException sqlEx) {
/*  887 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setClob(String parameterName, Clob x) throws SQLException {
/*      */     try {
/*  893 */       if (this.wrappedStmt != null) {
/*  894 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, x);
/*      */       } else {
/*  896 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  900 */     catch (SQLException sqlEx) {
/*  901 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  907 */       if (this.wrappedStmt != null) {
/*  908 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader);
/*      */       } else {
/*  910 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  914 */     catch (SQLException sqlEx) {
/*  915 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  921 */       if (this.wrappedStmt != null) {
/*  922 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader, length);
/*      */       } else {
/*  924 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  928 */     catch (SQLException sqlEx) {
/*  929 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  935 */       if (this.wrappedStmt != null) {
/*  936 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader);
/*      */       } else {
/*  938 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  942 */     catch (SQLException sqlEx) {
/*  943 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  949 */       if (this.wrappedStmt != null) {
/*  950 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader, length);
/*      */       } else {
/*  952 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  956 */     catch (SQLException sqlEx) {
/*  957 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public NClob getNClob(int parameterIndex) throws SQLException {
/*      */     try {
/*  963 */       if (this.wrappedStmt != null) {
/*  964 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterIndex);
/*      */       }
/*  966 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  970 */     catch (SQLException sqlEx) {
/*  971 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  974 */       return null;
/*      */     } 
/*      */   }
/*      */   public String getNString(int parameterIndex) throws SQLException {
/*      */     try {
/*  979 */       if (this.wrappedStmt != null) {
/*  980 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterIndex);
/*      */       }
/*  982 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  986 */     catch (SQLException sqlEx) {
/*  987 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  990 */       return null;
/*      */     } 
/*      */   }
/*      */   public RowId getRowId(int parameterIndex) throws SQLException {
/*      */     try {
/*  995 */       if (this.wrappedStmt != null) {
/*  996 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterIndex);
/*      */       }
/*  998 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1002 */     catch (SQLException sqlEx) {
/* 1003 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1006 */       return null;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4CallableStatementWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */