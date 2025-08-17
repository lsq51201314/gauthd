/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.Util;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Map;
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
/*      */ public class CallableStatementWrapper
/*      */   extends PreparedStatementWrapper
/*      */   implements CallableStatement
/*      */ {
/*      */   private static final Constructor JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR;
/*      */   
/*      */   static {
/*   59 */     if (Util.isJdbc4()) {
/*      */       try {
/*   61 */         JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4CallableStatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, CallableStatement.class });
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*   66 */       catch (SecurityException e) {
/*   67 */         throw new RuntimeException(e);
/*   68 */       } catch (NoSuchMethodException e) {
/*   69 */         throw new RuntimeException(e);
/*   70 */       } catch (ClassNotFoundException e) {
/*   71 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*   74 */       JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static CallableStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) throws SQLException {
/*   81 */     if (!Util.isJdbc4()) {
/*   82 */       return new CallableStatementWrapper(c, conn, toWrap);
/*      */     }
/*      */ 
/*      */     
/*   86 */     return (CallableStatementWrapper)Util.handleNewInstance(JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap });
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
/*      */   public CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) {
/*   99 */     super(c, conn, toWrap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
/*      */     try {
/*  110 */       if (this.wrappedStmt != null) {
/*  111 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
/*      */       } else {
/*      */         
/*  114 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  118 */     catch (SQLException sqlEx) {
/*  119 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
/*      */     try {
/*  131 */       if (this.wrappedStmt != null) {
/*  132 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
/*      */       } else {
/*      */         
/*  135 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  139 */     catch (SQLException sqlEx) {
/*  140 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean wasNull() throws SQLException {
/*      */     try {
/*  151 */       if (this.wrappedStmt != null) {
/*  152 */         return ((CallableStatement)this.wrappedStmt).wasNull();
/*      */       }
/*  154 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  158 */     catch (SQLException sqlEx) {
/*  159 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  162 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(int parameterIndex) throws SQLException {
/*      */     try {
/*  172 */       if (this.wrappedStmt != null) {
/*  173 */         return ((CallableStatement)this.wrappedStmt).getString(parameterIndex);
/*      */       }
/*      */       
/*  176 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  180 */     catch (SQLException sqlEx) {
/*  181 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  183 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int parameterIndex) throws SQLException {
/*      */     try {
/*  193 */       if (this.wrappedStmt != null) {
/*  194 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterIndex);
/*      */       }
/*      */       
/*  197 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  201 */     catch (SQLException sqlEx) {
/*  202 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  205 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getByte(int parameterIndex) throws SQLException {
/*      */     try {
/*  215 */       if (this.wrappedStmt != null) {
/*  216 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterIndex);
/*      */       }
/*      */       
/*  219 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  223 */     catch (SQLException sqlEx) {
/*  224 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  227 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShort(int parameterIndex) throws SQLException {
/*      */     try {
/*  237 */       if (this.wrappedStmt != null) {
/*  238 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterIndex);
/*      */       }
/*      */       
/*  241 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  245 */     catch (SQLException sqlEx) {
/*  246 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  249 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInt(int parameterIndex) throws SQLException {
/*      */     try {
/*  259 */       if (this.wrappedStmt != null) {
/*  260 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterIndex);
/*      */       }
/*      */       
/*  263 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  267 */     catch (SQLException sqlEx) {
/*  268 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  271 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLong(int parameterIndex) throws SQLException {
/*      */     try {
/*  281 */       if (this.wrappedStmt != null) {
/*  282 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterIndex);
/*      */       }
/*      */       
/*  285 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  289 */     catch (SQLException sqlEx) {
/*  290 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  293 */       return 0L;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloat(int parameterIndex) throws SQLException {
/*      */     try {
/*  303 */       if (this.wrappedStmt != null) {
/*  304 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterIndex);
/*      */       }
/*      */       
/*  307 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  311 */     catch (SQLException sqlEx) {
/*  312 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  315 */       return 0.0F;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble(int parameterIndex) throws SQLException {
/*      */     try {
/*  325 */       if (this.wrappedStmt != null) {
/*  326 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterIndex);
/*      */       }
/*      */       
/*  329 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  333 */     catch (SQLException sqlEx) {
/*  334 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  337 */       return 0.0D;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
/*      */     try {
/*  348 */       if (this.wrappedStmt != null) {
/*  349 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex, scale);
/*      */       }
/*      */       
/*  352 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  356 */     catch (SQLException sqlEx) {
/*  357 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  360 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBytes(int parameterIndex) throws SQLException {
/*      */     try {
/*  370 */       if (this.wrappedStmt != null) {
/*  371 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterIndex);
/*      */       }
/*      */       
/*  374 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  378 */     catch (SQLException sqlEx) {
/*  379 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  382 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(int parameterIndex) throws SQLException {
/*      */     try {
/*  392 */       if (this.wrappedStmt != null) {
/*  393 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex);
/*      */       }
/*      */       
/*  396 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  400 */     catch (SQLException sqlEx) {
/*  401 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  404 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(int parameterIndex) throws SQLException {
/*      */     try {
/*  414 */       if (this.wrappedStmt != null) {
/*  415 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex);
/*      */       }
/*      */       
/*  418 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  422 */     catch (SQLException sqlEx) {
/*  423 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  426 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/*      */     try {
/*  436 */       if (this.wrappedStmt != null) {
/*  437 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex);
/*      */       }
/*      */       
/*  440 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  444 */     catch (SQLException sqlEx) {
/*  445 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  448 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int parameterIndex) throws SQLException {
/*      */     try {
/*  458 */       if (this.wrappedStmt != null) {
/*  459 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex);
/*      */       }
/*      */       
/*  462 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  466 */     catch (SQLException sqlEx) {
/*  467 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  470 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/*      */     try {
/*  480 */       if (this.wrappedStmt != null) {
/*  481 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex);
/*      */       }
/*      */       
/*  484 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  488 */     catch (SQLException sqlEx) {
/*  489 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  492 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int parameterIndex, Map typeMap) throws SQLException {
/*      */     try {
/*  503 */       if (this.wrappedStmt != null) {
/*  504 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex, typeMap);
/*      */       }
/*      */       
/*  507 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  511 */     catch (SQLException sqlEx) {
/*  512 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  514 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(int parameterIndex) throws SQLException {
/*      */     try {
/*  524 */       if (this.wrappedStmt != null) {
/*  525 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterIndex);
/*      */       }
/*      */       
/*  528 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  532 */     catch (SQLException sqlEx) {
/*  533 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  536 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Blob getBlob(int parameterIndex) throws SQLException {
/*      */     try {
/*  546 */       if (this.wrappedStmt != null) {
/*  547 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterIndex);
/*      */       }
/*      */       
/*  550 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  554 */     catch (SQLException sqlEx) {
/*  555 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  558 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Clob getClob(int parameterIndex) throws SQLException {
/*      */     try {
/*  568 */       if (this.wrappedStmt != null) {
/*  569 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterIndex);
/*      */       }
/*      */       
/*  572 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  576 */     catch (SQLException sqlEx) {
/*  577 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  579 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Array getArray(int parameterIndex) throws SQLException {
/*      */     try {
/*  589 */       if (this.wrappedStmt != null) {
/*  590 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterIndex);
/*      */       }
/*      */       
/*  593 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  597 */     catch (SQLException sqlEx) {
/*  598 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  600 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
/*      */     try {
/*  610 */       if (this.wrappedStmt != null) {
/*  611 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex, cal);
/*      */       }
/*      */       
/*  614 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  618 */     catch (SQLException sqlEx) {
/*  619 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  621 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
/*      */     try {
/*  631 */       if (this.wrappedStmt != null) {
/*  632 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex, cal);
/*      */       }
/*      */       
/*  635 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  639 */     catch (SQLException sqlEx) {
/*  640 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  642 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
/*      */     try {
/*  653 */       if (this.wrappedStmt != null) {
/*  654 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex, cal);
/*      */       }
/*      */       
/*  657 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  661 */     catch (SQLException sqlEx) {
/*  662 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  664 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
/*      */     try {
/*  676 */       if (this.wrappedStmt != null) {
/*  677 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(paramIndex, sqlType, typeName);
/*      */       } else {
/*      */         
/*  680 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  684 */     catch (SQLException sqlEx) {
/*  685 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
/*      */     try {
/*  698 */       if (this.wrappedStmt != null) {
/*  699 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
/*      */       } else {
/*      */         
/*  702 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  706 */     catch (SQLException sqlEx) {
/*  707 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
/*      */     try {
/*  720 */       if (this.wrappedStmt != null) {
/*  721 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
/*      */       } else {
/*      */         
/*  724 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  728 */     catch (SQLException sqlEx) {
/*  729 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
/*      */     try {
/*  742 */       if (this.wrappedStmt != null) {
/*  743 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
/*      */       } else {
/*      */         
/*  746 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  750 */     catch (SQLException sqlEx) {
/*  751 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(int parameterIndex) throws SQLException {
/*      */     try {
/*  762 */       if (this.wrappedStmt != null) {
/*  763 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterIndex);
/*      */       }
/*      */       
/*  766 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/*  770 */     catch (SQLException sqlEx) {
/*  771 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  774 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURL(String parameterName, URL val) throws SQLException {
/*      */     try {
/*  784 */       if (this.wrappedStmt != null) {
/*  785 */         ((CallableStatement)this.wrappedStmt).setURL(parameterName, val);
/*      */       } else {
/*      */         
/*  788 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  792 */     catch (SQLException sqlEx) {
/*  793 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNull(String parameterName, int sqlType) throws SQLException {
/*      */     try {
/*  804 */       if (this.wrappedStmt != null) {
/*  805 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType);
/*      */       } else {
/*      */         
/*  808 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  812 */     catch (SQLException sqlEx) {
/*  813 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBoolean(String parameterName, boolean x) throws SQLException {
/*      */     try {
/*  824 */       if (this.wrappedStmt != null) {
/*  825 */         ((CallableStatement)this.wrappedStmt).setBoolean(parameterName, x);
/*      */       } else {
/*      */         
/*  828 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  832 */     catch (SQLException sqlEx) {
/*  833 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setByte(String parameterName, byte x) throws SQLException {
/*      */     try {
/*  844 */       if (this.wrappedStmt != null) {
/*  845 */         ((CallableStatement)this.wrappedStmt).setByte(parameterName, x);
/*      */       } else {
/*      */         
/*  848 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  852 */     catch (SQLException sqlEx) {
/*  853 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setShort(String parameterName, short x) throws SQLException {
/*      */     try {
/*  864 */       if (this.wrappedStmt != null) {
/*  865 */         ((CallableStatement)this.wrappedStmt).setShort(parameterName, x);
/*      */       } else {
/*      */         
/*  868 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  872 */     catch (SQLException sqlEx) {
/*  873 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInt(String parameterName, int x) throws SQLException {
/*      */     try {
/*  884 */       if (this.wrappedStmt != null) {
/*  885 */         ((CallableStatement)this.wrappedStmt).setInt(parameterName, x);
/*      */       } else {
/*  887 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  891 */     catch (SQLException sqlEx) {
/*  892 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLong(String parameterName, long x) throws SQLException {
/*      */     try {
/*  903 */       if (this.wrappedStmt != null) {
/*  904 */         ((CallableStatement)this.wrappedStmt).setLong(parameterName, x);
/*      */       } else {
/*      */         
/*  907 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  911 */     catch (SQLException sqlEx) {
/*  912 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFloat(String parameterName, float x) throws SQLException {
/*      */     try {
/*  923 */       if (this.wrappedStmt != null) {
/*  924 */         ((CallableStatement)this.wrappedStmt).setFloat(parameterName, x);
/*      */       } else {
/*      */         
/*  927 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  931 */     catch (SQLException sqlEx) {
/*  932 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDouble(String parameterName, double x) throws SQLException {
/*      */     try {
/*  943 */       if (this.wrappedStmt != null) {
/*  944 */         ((CallableStatement)this.wrappedStmt).setDouble(parameterName, x);
/*      */       } else {
/*      */         
/*  947 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  951 */     catch (SQLException sqlEx) {
/*  952 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
/*      */     try {
/*  965 */       if (this.wrappedStmt != null) {
/*  966 */         ((CallableStatement)this.wrappedStmt).setBigDecimal(parameterName, x);
/*      */       } else {
/*      */         
/*  969 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  973 */     catch (SQLException sqlEx) {
/*  974 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setString(String parameterName, String x) throws SQLException {
/*      */     try {
/*  986 */       if (this.wrappedStmt != null) {
/*  987 */         ((CallableStatement)this.wrappedStmt).setString(parameterName, x);
/*      */       } else {
/*      */         
/*  990 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/*  994 */     catch (SQLException sqlEx) {
/*  995 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBytes(String parameterName, byte[] x) throws SQLException {
/*      */     try {
/* 1006 */       if (this.wrappedStmt != null) {
/* 1007 */         ((CallableStatement)this.wrappedStmt).setBytes(parameterName, x);
/*      */       } else {
/*      */         
/* 1010 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1014 */     catch (SQLException sqlEx) {
/* 1015 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(String parameterName, Date x) throws SQLException {
/*      */     try {
/* 1026 */       if (this.wrappedStmt != null) {
/* 1027 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x);
/*      */       } else {
/*      */         
/* 1030 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1034 */     catch (SQLException sqlEx) {
/* 1035 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTime(String parameterName, Time x) throws SQLException {
/*      */     try {
/* 1046 */       if (this.wrappedStmt != null) {
/* 1047 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x);
/*      */       } else {
/*      */         
/* 1050 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1054 */     catch (SQLException sqlEx) {
/* 1055 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
/*      */     try {
/* 1068 */       if (this.wrappedStmt != null) {
/* 1069 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x);
/*      */       } else {
/*      */         
/* 1072 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1076 */     catch (SQLException sqlEx) {
/* 1077 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
/*      */     try {
/* 1090 */       if (this.wrappedStmt != null) {
/* 1091 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       } else {
/*      */         
/* 1094 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1098 */     catch (SQLException sqlEx) {
/* 1099 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
/*      */     try {
/* 1113 */       if (this.wrappedStmt != null) {
/* 1114 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       } else {
/*      */         
/* 1117 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1121 */     catch (SQLException sqlEx) {
/* 1122 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
/*      */     try {
/* 1135 */       if (this.wrappedStmt != null) {
/* 1136 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scale);
/*      */       } else {
/*      */         
/* 1139 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1143 */     catch (SQLException sqlEx) {
/* 1144 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
/*      */     try {
/* 1157 */       if (this.wrappedStmt != null) {
/* 1158 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
/*      */       } else {
/*      */         
/* 1161 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1165 */     catch (SQLException sqlEx) {
/* 1166 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x) throws SQLException {
/*      */     try {
/* 1178 */       if (this.wrappedStmt != null) {
/* 1179 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x);
/*      */       } else {
/*      */         
/* 1182 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1186 */     catch (SQLException sqlEx) {
/* 1187 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
/*      */     try {
/* 1200 */       if (this.wrappedStmt != null) {
/* 1201 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       } else {
/*      */         
/* 1204 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1208 */     catch (SQLException sqlEx) {
/* 1209 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
/*      */     try {
/* 1222 */       if (this.wrappedStmt != null) {
/* 1223 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x, cal);
/*      */       } else {
/*      */         
/* 1226 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1230 */     catch (SQLException sqlEx) {
/* 1231 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
/*      */     try {
/* 1244 */       if (this.wrappedStmt != null) {
/* 1245 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x, cal);
/*      */       } else {
/*      */         
/* 1248 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1252 */     catch (SQLException sqlEx) {
/* 1253 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
/*      */     try {
/* 1266 */       if (this.wrappedStmt != null) {
/* 1267 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x, cal);
/*      */       } else {
/*      */         
/* 1270 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1274 */     catch (SQLException sqlEx) {
/* 1275 */       checkAndFireConnectionError(sqlEx);
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
/*      */   public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
/*      */     try {
/* 1288 */       if (this.wrappedStmt != null) {
/* 1289 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType, typeName);
/*      */       } else {
/*      */         
/* 1292 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */       }
/*      */     
/*      */     }
/* 1296 */     catch (SQLException sqlEx) {
/* 1297 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(String parameterName) throws SQLException {
/*      */     try {
/* 1308 */       if (this.wrappedStmt != null) {
/* 1309 */         return ((CallableStatement)this.wrappedStmt).getString(parameterName);
/*      */       }
/*      */       
/* 1312 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1316 */     catch (SQLException sqlEx) {
/* 1317 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1319 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean(String parameterName) throws SQLException {
/*      */     try {
/* 1329 */       if (this.wrappedStmt != null) {
/* 1330 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterName);
/*      */       }
/*      */       
/* 1333 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1337 */     catch (SQLException sqlEx) {
/* 1338 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1341 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getByte(String parameterName) throws SQLException {
/*      */     try {
/* 1351 */       if (this.wrappedStmt != null) {
/* 1352 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterName);
/*      */       }
/*      */       
/* 1355 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1359 */     catch (SQLException sqlEx) {
/* 1360 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1363 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShort(String parameterName) throws SQLException {
/*      */     try {
/* 1373 */       if (this.wrappedStmt != null) {
/* 1374 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterName);
/*      */       }
/*      */       
/* 1377 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1381 */     catch (SQLException sqlEx) {
/* 1382 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1385 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInt(String parameterName) throws SQLException {
/*      */     try {
/* 1395 */       if (this.wrappedStmt != null) {
/* 1396 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterName);
/*      */       }
/*      */       
/* 1399 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1403 */     catch (SQLException sqlEx) {
/* 1404 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1407 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLong(String parameterName) throws SQLException {
/*      */     try {
/* 1417 */       if (this.wrappedStmt != null) {
/* 1418 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterName);
/*      */       }
/*      */       
/* 1421 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1425 */     catch (SQLException sqlEx) {
/* 1426 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1429 */       return 0L;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloat(String parameterName) throws SQLException {
/*      */     try {
/* 1439 */       if (this.wrappedStmt != null) {
/* 1440 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterName);
/*      */       }
/*      */       
/* 1443 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1447 */     catch (SQLException sqlEx) {
/* 1448 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1451 */       return 0.0F;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble(String parameterName) throws SQLException {
/*      */     try {
/* 1461 */       if (this.wrappedStmt != null) {
/* 1462 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterName);
/*      */       }
/*      */       
/* 1465 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1469 */     catch (SQLException sqlEx) {
/* 1470 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1473 */       return 0.0D;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBytes(String parameterName) throws SQLException {
/*      */     try {
/* 1483 */       if (this.wrappedStmt != null) {
/* 1484 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterName);
/*      */       }
/*      */       
/* 1487 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1491 */     catch (SQLException sqlEx) {
/* 1492 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1495 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(String parameterName) throws SQLException {
/*      */     try {
/* 1505 */       if (this.wrappedStmt != null) {
/* 1506 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName);
/*      */       }
/*      */       
/* 1509 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1513 */     catch (SQLException sqlEx) {
/* 1514 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1517 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(String parameterName) throws SQLException {
/*      */     try {
/* 1527 */       if (this.wrappedStmt != null) {
/* 1528 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName);
/*      */       }
/*      */       
/* 1531 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1535 */     catch (SQLException sqlEx) {
/* 1536 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1539 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String parameterName) throws SQLException {
/*      */     try {
/* 1549 */       if (this.wrappedStmt != null) {
/* 1550 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName);
/*      */       }
/*      */       
/* 1553 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1557 */     catch (SQLException sqlEx) {
/* 1558 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1561 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String parameterName) throws SQLException {
/*      */     try {
/* 1571 */       if (this.wrappedStmt != null) {
/* 1572 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName);
/*      */       }
/*      */       
/* 1575 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1579 */     catch (SQLException sqlEx) {
/* 1580 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1583 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getBigDecimal(String parameterName) throws SQLException {
/*      */     try {
/* 1593 */       if (this.wrappedStmt != null) {
/* 1594 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterName);
/*      */       }
/*      */       
/* 1597 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1601 */     catch (SQLException sqlEx) {
/* 1602 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1605 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String parameterName, Map typeMap) throws SQLException {
/*      */     try {
/* 1616 */       if (this.wrappedStmt != null) {
/* 1617 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName, typeMap);
/*      */       }
/*      */       
/* 1620 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1624 */     catch (SQLException sqlEx) {
/* 1625 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1627 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(String parameterName) throws SQLException {
/*      */     try {
/* 1637 */       if (this.wrappedStmt != null) {
/* 1638 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterName);
/*      */       }
/*      */       
/* 1641 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1645 */     catch (SQLException sqlEx) {
/* 1646 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1649 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Blob getBlob(String parameterName) throws SQLException {
/*      */     try {
/* 1659 */       if (this.wrappedStmt != null) {
/* 1660 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterName);
/*      */       }
/*      */       
/* 1663 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1667 */     catch (SQLException sqlEx) {
/* 1668 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1671 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Clob getClob(String parameterName) throws SQLException {
/*      */     try {
/* 1681 */       if (this.wrappedStmt != null) {
/* 1682 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterName);
/*      */       }
/*      */       
/* 1685 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1689 */     catch (SQLException sqlEx) {
/* 1690 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1692 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Array getArray(String parameterName) throws SQLException {
/*      */     try {
/* 1702 */       if (this.wrappedStmt != null) {
/* 1703 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterName);
/*      */       }
/*      */       
/* 1706 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1710 */     catch (SQLException sqlEx) {
/* 1711 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1713 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(String parameterName, Calendar cal) throws SQLException {
/*      */     try {
/* 1723 */       if (this.wrappedStmt != null) {
/* 1724 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName, cal);
/*      */       }
/*      */       
/* 1727 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1731 */     catch (SQLException sqlEx) {
/* 1732 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1734 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(String parameterName, Calendar cal) throws SQLException {
/*      */     try {
/* 1744 */       if (this.wrappedStmt != null) {
/* 1745 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName, cal);
/*      */       }
/*      */       
/* 1748 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1752 */     catch (SQLException sqlEx) {
/* 1753 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1755 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
/*      */     try {
/* 1766 */       if (this.wrappedStmt != null) {
/* 1767 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName, cal);
/*      */       }
/*      */       
/* 1770 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1774 */     catch (SQLException sqlEx) {
/* 1775 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1777 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(String parameterName) throws SQLException {
/*      */     try {
/* 1787 */       if (this.wrappedStmt != null) {
/* 1788 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterName);
/*      */       }
/*      */       
/* 1791 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*      */ 
/*      */     
/*      */     }
/* 1795 */     catch (SQLException sqlEx) {
/* 1796 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1799 */       return null;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\CallableStatementWrapper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */