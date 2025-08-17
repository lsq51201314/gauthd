/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
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
/*     */ public class PreparedStatementWrapper
/*     */   extends StatementWrapper
/*     */   implements PreparedStatement
/*     */ {
/*     */   private static final Constructor JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR;
/*     */   
/*     */   static {
/*  63 */     if (Util.isJdbc4()) {
/*     */       try {
/*  65 */         JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4PreparedStatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, PreparedStatement.class });
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*  70 */       catch (SecurityException e) {
/*  71 */         throw new RuntimeException(e);
/*  72 */       } catch (NoSuchMethodException e) {
/*  73 */         throw new RuntimeException(e);
/*  74 */       } catch (ClassNotFoundException e) {
/*  75 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/*  78 */       JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static PreparedStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) throws SQLException {
/*  85 */     if (!Util.isJdbc4()) {
/*  86 */       return new PreparedStatementWrapper(c, conn, toWrap);
/*     */     }
/*     */ 
/*     */     
/*  90 */     return (PreparedStatementWrapper)Util.handleNewInstance(JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) {
/*  98 */     super(c, conn, toWrap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArray(int parameterIndex, Array x) throws SQLException {
/*     */     try {
/* 108 */       if (this.wrappedStmt != null) {
/* 109 */         ((PreparedStatement)this.wrappedStmt).setArray(parameterIndex, x);
/*     */       } else {
/*     */         
/* 112 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 116 */     catch (SQLException sqlEx) {
/* 117 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 130 */       if (this.wrappedStmt != null) {
/* 131 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/*     */         
/* 134 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 138 */     catch (SQLException sqlEx) {
/* 139 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/*     */     try {
/* 151 */       if (this.wrappedStmt != null) {
/* 152 */         ((PreparedStatement)this.wrappedStmt).setBigDecimal(parameterIndex, x);
/*     */       } else {
/*     */         
/* 155 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 159 */     catch (SQLException sqlEx) {
/* 160 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 173 */       if (this.wrappedStmt != null) {
/* 174 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/*     */         
/* 177 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 181 */     catch (SQLException sqlEx) {
/* 182 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, Blob x) throws SQLException {
/*     */     try {
/* 193 */       if (this.wrappedStmt != null) {
/* 194 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, x);
/*     */       } else {
/*     */         
/* 197 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 201 */     catch (SQLException sqlEx) {
/* 202 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/*     */     try {
/* 213 */       if (this.wrappedStmt != null) {
/* 214 */         ((PreparedStatement)this.wrappedStmt).setBoolean(parameterIndex, x);
/*     */       } else {
/*     */         
/* 217 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 221 */     catch (SQLException sqlEx) {
/* 222 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setByte(int parameterIndex, byte x) throws SQLException {
/*     */     try {
/* 233 */       if (this.wrappedStmt != null) {
/* 234 */         ((PreparedStatement)this.wrappedStmt).setByte(parameterIndex, x);
/*     */       } else {
/*     */         
/* 237 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 241 */     catch (SQLException sqlEx) {
/* 242 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/*     */     try {
/* 253 */       if (this.wrappedStmt != null) {
/* 254 */         ((PreparedStatement)this.wrappedStmt).setBytes(parameterIndex, x);
/*     */       } else {
/*     */         
/* 257 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 261 */     catch (SQLException sqlEx) {
/* 262 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/*     */     try {
/* 275 */       if (this.wrappedStmt != null) {
/* 276 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/*     */         
/* 279 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 283 */     catch (SQLException sqlEx) {
/* 284 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Clob x) throws SQLException {
/*     */     try {
/* 295 */       if (this.wrappedStmt != null) {
/* 296 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, x);
/*     */       } else {
/*     */         
/* 299 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 303 */     catch (SQLException sqlEx) {
/* 304 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x) throws SQLException {
/*     */     try {
/* 315 */       if (this.wrappedStmt != null) {
/* 316 */         ((PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
/*     */     try {
/* 337 */       if (this.wrappedStmt != null) {
/* 338 */         ((PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x, cal);
/*     */       } else {
/*     */         
/* 341 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 345 */     catch (SQLException sqlEx) {
/* 346 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDouble(int parameterIndex, double x) throws SQLException {
/*     */     try {
/* 357 */       if (this.wrappedStmt != null) {
/* 358 */         ((PreparedStatement)this.wrappedStmt).setDouble(parameterIndex, x);
/*     */       } else {
/*     */         
/* 361 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 365 */     catch (SQLException sqlEx) {
/* 366 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFloat(int parameterIndex, float x) throws SQLException {
/*     */     try {
/* 377 */       if (this.wrappedStmt != null) {
/* 378 */         ((PreparedStatement)this.wrappedStmt).setFloat(parameterIndex, x);
/*     */       } else {
/*     */         
/* 381 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 385 */     catch (SQLException sqlEx) {
/* 386 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInt(int parameterIndex, int x) throws SQLException {
/*     */     try {
/* 397 */       if (this.wrappedStmt != null) {
/* 398 */         ((PreparedStatement)this.wrappedStmt).setInt(parameterIndex, x);
/*     */       } else {
/*     */         
/* 401 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 405 */     catch (SQLException sqlEx) {
/* 406 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLong(int parameterIndex, long x) throws SQLException {
/*     */     try {
/* 417 */       if (this.wrappedStmt != null) {
/* 418 */         ((PreparedStatement)this.wrappedStmt).setLong(parameterIndex, x);
/*     */       } else {
/*     */         
/* 421 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 425 */     catch (SQLException sqlEx) {
/* 426 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/*     */     try {
/* 437 */       if (this.wrappedStmt != null) {
/* 438 */         return ((PreparedStatement)this.wrappedStmt).getMetaData();
/*     */       }
/*     */       
/* 441 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */     
/*     */     }
/* 444 */     catch (SQLException sqlEx) {
/* 445 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 448 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType) throws SQLException {
/*     */     try {
/* 458 */       if (this.wrappedStmt != null) {
/* 459 */         ((PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType);
/*     */       } else {
/*     */         
/* 462 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 466 */     catch (SQLException sqlEx) {
/* 467 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
/*     */     try {
/* 479 */       if (this.wrappedStmt != null) {
/* 480 */         ((PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType, typeName);
/*     */       } else {
/*     */         
/* 483 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 487 */     catch (SQLException sqlEx) {
/* 488 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x) throws SQLException {
/*     */     try {
/* 499 */       if (this.wrappedStmt != null) {
/* 500 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x);
/*     */       } else {
/*     */         
/* 503 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 507 */     catch (SQLException sqlEx) {
/* 508 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
/*     */     try {
/* 520 */       if (this.wrappedStmt != null) {
/* 521 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*     */       } else {
/*     */         
/* 524 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 528 */     catch (SQLException sqlEx) {
/* 529 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
/*     */     try {
/* 542 */       if (this.wrappedStmt != null) {
/* 543 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scale);
/*     */       } else {
/*     */         
/* 546 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 550 */     catch (SQLException sqlEx) {
/* 551 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*     */     try {
/* 562 */       if (this.wrappedStmt != null) {
/* 563 */         return ((PreparedStatement)this.wrappedStmt).getParameterMetaData();
/*     */       }
/*     */ 
/*     */       
/* 567 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */     
/*     */     }
/* 570 */     catch (SQLException sqlEx) {
/* 571 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 574 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRef(int parameterIndex, Ref x) throws SQLException {
/*     */     try {
/* 584 */       if (this.wrappedStmt != null) {
/* 585 */         ((PreparedStatement)this.wrappedStmt).setRef(parameterIndex, x);
/*     */       } else {
/*     */         
/* 588 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 592 */     catch (SQLException sqlEx) {
/* 593 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShort(int parameterIndex, short x) throws SQLException {
/*     */     try {
/* 604 */       if (this.wrappedStmt != null) {
/* 605 */         ((PreparedStatement)this.wrappedStmt).setShort(parameterIndex, x);
/*     */       } else {
/*     */         
/* 608 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 612 */     catch (SQLException sqlEx) {
/* 613 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setString(int parameterIndex, String x) throws SQLException {
/*     */     try {
/* 624 */       if (this.wrappedStmt != null) {
/* 625 */         ((PreparedStatement)this.wrappedStmt).setString(parameterIndex, x);
/*     */       } else {
/*     */         
/* 628 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 632 */     catch (SQLException sqlEx) {
/* 633 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTime(int parameterIndex, Time x) throws SQLException {
/*     */     try {
/* 644 */       if (this.wrappedStmt != null) {
/* 645 */         ((PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x);
/*     */       } else {
/*     */         
/* 648 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 652 */     catch (SQLException sqlEx) {
/* 653 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/*     */     try {
/* 666 */       if (this.wrappedStmt != null) {
/* 667 */         ((PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x, cal);
/*     */       } else {
/*     */         
/* 670 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 674 */     catch (SQLException sqlEx) {
/* 675 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
/*     */     try {
/* 687 */       if (this.wrappedStmt != null) {
/* 688 */         ((PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x);
/*     */       } else {
/*     */         
/* 691 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 695 */     catch (SQLException sqlEx) {
/* 696 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/*     */     try {
/* 709 */       if (this.wrappedStmt != null) {
/* 710 */         ((PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x, cal);
/*     */       } else {
/*     */         
/* 713 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 717 */     catch (SQLException sqlEx) {
/* 718 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setURL(int parameterIndex, URL x) throws SQLException {
/*     */     try {
/* 729 */       if (this.wrappedStmt != null) {
/* 730 */         ((PreparedStatement)this.wrappedStmt).setURL(parameterIndex, x);
/*     */       } else {
/*     */         
/* 733 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 737 */     catch (SQLException sqlEx) {
/* 738 */       checkAndFireConnectionError(sqlEx);
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
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 762 */       if (this.wrappedStmt != null) {
/* 763 */         ((PreparedStatement)this.wrappedStmt).setUnicodeStream(parameterIndex, x, length);
/*     */       } else {
/*     */         
/* 766 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 770 */     catch (SQLException sqlEx) {
/* 771 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addBatch() throws SQLException {
/*     */     try {
/* 782 */       if (this.wrappedStmt != null) {
/* 783 */         ((PreparedStatement)this.wrappedStmt).addBatch();
/*     */       } else {
/* 785 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 789 */     catch (SQLException sqlEx) {
/* 790 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearParameters() throws SQLException {
/*     */     try {
/* 801 */       if (this.wrappedStmt != null) {
/* 802 */         ((PreparedStatement)this.wrappedStmt).clearParameters();
/*     */       } else {
/* 804 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */       }
/*     */     
/*     */     }
/* 808 */     catch (SQLException sqlEx) {
/* 809 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean execute() throws SQLException {
/*     */     try {
/* 820 */       if (this.wrappedStmt != null) {
/* 821 */         return ((PreparedStatement)this.wrappedStmt).execute();
/*     */       }
/*     */       
/* 824 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */     
/*     */     }
/* 827 */     catch (SQLException sqlEx) {
/* 828 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 831 */       return false;
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
/*     */   public ResultSet executeQuery() throws SQLException {
/*     */     try {
/* 844 */       if (this.wrappedStmt != null) {
/* 845 */         ResultSet rs = ((PreparedStatement)this.wrappedStmt).executeQuery();
/*     */ 
/*     */         
/* 848 */         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */         
/* 850 */         return rs;
/*     */       } 
/*     */       
/* 853 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */     
/*     */     }
/* 856 */     catch (SQLException sqlEx) {
/* 857 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 860 */       return null;
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
/*     */   public int executeUpdate() throws SQLException {
/*     */     try {
/* 873 */       if (this.wrappedStmt != null) {
/* 874 */         return ((PreparedStatement)this.wrappedStmt).executeUpdate();
/*     */       }
/*     */       
/* 877 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000");
/*     */     
/*     */     }
/* 880 */     catch (SQLException sqlEx) {
/* 881 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 884 */       return -1;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\jdbc2\optional\PreparedStatementWrapper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */