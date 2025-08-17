/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.TimeZone;
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
/*      */ public class ServerPreparedStatement
/*      */   extends PreparedStatement
/*      */ {
/*      */   private static final Constructor JDBC_4_SPS_CTOR;
/*      */   protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
/*      */   private static final byte MAX_DATE_REP_LENGTH = 5;
/*      */   private static final byte MAX_DATETIME_REP_LENGTH = 12;
/*      */   private static final byte MAX_TIME_REP_LENGTH = 13;
/*      */   
/*      */   static {
/*   67 */     if (Util.isJdbc4()) {
/*      */       try {
/*   69 */         JDBC_4_SPS_CTOR = Class.forName("com.mysql.jdbc.JDBC4ServerPreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class, int.class, int.class });
/*      */ 
/*      */       
/*      */       }
/*   73 */       catch (SecurityException e) {
/*   74 */         throw new RuntimeException(e);
/*   75 */       } catch (NoSuchMethodException e) {
/*   76 */         throw new RuntimeException(e);
/*   77 */       } catch (ClassNotFoundException e) {
/*   78 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*   81 */       JDBC_4_SPS_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static class BatchedBindValues
/*      */   {
/*      */     ServerPreparedStatement.BindValue[] batchedParameterValues;
/*      */     
/*      */     BatchedBindValues(ServerPreparedStatement.BindValue[] paramVals) {
/*   91 */       int numParams = paramVals.length;
/*      */       
/*   93 */       this.batchedParameterValues = new ServerPreparedStatement.BindValue[numParams];
/*      */       
/*   95 */       for (int i = 0; i < numParams; i++) {
/*   96 */         this.batchedParameterValues[i] = new ServerPreparedStatement.BindValue(paramVals[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static class BindValue
/*      */   {
/*  103 */     long boundBeforeExecutionNum = 0L;
/*      */     
/*      */     public long bindLength;
/*      */     
/*      */     int bufferType;
/*      */     
/*      */     byte byteBinding;
/*      */     
/*      */     double doubleBinding;
/*      */     
/*      */     float floatBinding;
/*      */     
/*      */     int intBinding;
/*      */     
/*      */     public boolean isLongData;
/*      */     
/*      */     public boolean isNull;
/*      */     
/*      */     boolean isSet = false;
/*      */     
/*      */     long longBinding;
/*      */     
/*      */     short shortBinding;
/*      */     
/*      */     public Object value;
/*      */ 
/*      */     
/*      */     BindValue() {}
/*      */     
/*      */     BindValue(BindValue copyMe) {
/*  133 */       this.value = copyMe.value;
/*  134 */       this.isSet = copyMe.isSet;
/*  135 */       this.isLongData = copyMe.isLongData;
/*  136 */       this.isNull = copyMe.isNull;
/*  137 */       this.bufferType = copyMe.bufferType;
/*  138 */       this.bindLength = copyMe.bindLength;
/*  139 */       this.byteBinding = copyMe.byteBinding;
/*  140 */       this.shortBinding = copyMe.shortBinding;
/*  141 */       this.intBinding = copyMe.intBinding;
/*  142 */       this.longBinding = copyMe.longBinding;
/*  143 */       this.floatBinding = copyMe.floatBinding;
/*  144 */       this.doubleBinding = copyMe.doubleBinding;
/*      */     }
/*      */     
/*      */     void reset() {
/*  148 */       this.isSet = false;
/*  149 */       this.value = null;
/*  150 */       this.isLongData = false;
/*      */       
/*  152 */       this.byteBinding = 0;
/*  153 */       this.shortBinding = 0;
/*  154 */       this.intBinding = 0;
/*  155 */       this.longBinding = 0L;
/*  156 */       this.floatBinding = 0.0F;
/*  157 */       this.doubleBinding = 0.0D;
/*      */     }
/*      */     
/*      */     public String toString() {
/*  161 */       return toString(false);
/*      */     }
/*      */     
/*      */     public String toString(boolean quoteIfNeeded) {
/*  165 */       if (this.isLongData) {
/*  166 */         return "' STREAM DATA '";
/*      */       }
/*      */       
/*  169 */       switch (this.bufferType) {
/*      */         case 1:
/*  171 */           return String.valueOf(this.byteBinding);
/*      */         case 2:
/*  173 */           return String.valueOf(this.shortBinding);
/*      */         case 3:
/*  175 */           return String.valueOf(this.intBinding);
/*      */         case 8:
/*  177 */           return String.valueOf(this.longBinding);
/*      */         case 4:
/*  179 */           return String.valueOf(this.floatBinding);
/*      */         case 5:
/*  181 */           return String.valueOf(this.doubleBinding);
/*      */         case 7:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 15:
/*      */         case 253:
/*      */         case 254:
/*  189 */           if (quoteIfNeeded) {
/*  190 */             return "'" + String.valueOf(this.value) + "'";
/*      */           }
/*  192 */           return String.valueOf(this.value);
/*      */       } 
/*      */       
/*  195 */       if (this.value instanceof byte[]) {
/*  196 */         return "byte data";
/*      */       }
/*      */       
/*  199 */       if (quoteIfNeeded) {
/*  200 */         return "'" + String.valueOf(this.value) + "'";
/*      */       }
/*  202 */       return String.valueOf(this.value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long getBoundLength() {
/*  209 */       if (this.isNull) {
/*  210 */         return 0L;
/*      */       }
/*      */       
/*  213 */       if (this.isLongData) {
/*  214 */         return this.bindLength;
/*      */       }
/*      */       
/*  217 */       switch (this.bufferType) {
/*      */         
/*      */         case 1:
/*  220 */           return 1L;
/*      */         case 2:
/*  222 */           return 2L;
/*      */         case 3:
/*  224 */           return 4L;
/*      */         case 8:
/*  226 */           return 8L;
/*      */         case 4:
/*  228 */           return 4L;
/*      */         case 5:
/*  230 */           return 8L;
/*      */         case 11:
/*  232 */           return 9L;
/*      */         case 10:
/*  234 */           return 7L;
/*      */         case 7:
/*      */         case 12:
/*  237 */           return 11L;
/*      */         case 0:
/*      */         case 15:
/*      */         case 246:
/*      */         case 253:
/*      */         case 254:
/*  243 */           if (this.value instanceof byte[]) {
/*  244 */             return ((byte[])this.value).length;
/*      */           }
/*  246 */           return ((String)this.value).length();
/*      */       } 
/*      */       
/*  249 */       return 0L;
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
/*      */   private void storeTime(Buffer intoBuf, Time tm) throws SQLException {
/*  271 */     intoBuf.ensureCapacity(9);
/*  272 */     intoBuf.writeByte((byte)8);
/*  273 */     intoBuf.writeByte((byte)0);
/*  274 */     intoBuf.writeLong(0L);
/*      */     
/*  276 */     Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */     
/*  278 */     synchronized (sessionCalendar) {
/*  279 */       Date oldTime = sessionCalendar.getTime();
/*      */       try {
/*  281 */         sessionCalendar.setTime(tm);
/*  282 */         intoBuf.writeByte((byte)sessionCalendar.get(11));
/*  283 */         intoBuf.writeByte((byte)sessionCalendar.get(12));
/*  284 */         intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */       }
/*      */       finally {
/*      */         
/*  288 */         sessionCalendar.setTime(oldTime);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean detectedLongParameterSwitch = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private int fieldCount;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean invalid = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private SQLException invalidationException;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isSelectQuery;
/*      */ 
/*      */   
/*      */   private Buffer outByteBuffer;
/*      */ 
/*      */   
/*      */   private BindValue[] parameterBindings;
/*      */ 
/*      */   
/*      */   private Field[] parameterFields;
/*      */ 
/*      */   
/*      */   private Field[] resultFields;
/*      */ 
/*      */   
/*      */   private boolean sendTypesToServer = false;
/*      */ 
/*      */   
/*      */   private long serverStatementId;
/*      */ 
/*      */   
/*  333 */   private int stringTypeCode = 254;
/*      */   private boolean serverNeedsResetBeforeEachExecution;
/*      */   protected boolean isCached; private boolean useAutoSlowLog; private Calendar serverTzCalendar; private Calendar defaultTzCalendar; public synchronized void addBatch() throws SQLException { checkClosed(); if (this.batchedArgs == null)
/*      */       this.batchedArgs = new ArrayList();  this.batchedArgs.add(new BatchedBindValues(this.parameterBindings)); } protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException { if (this.isClosed)
/*      */       return "statement has been closed, no further internal information available";  PreparedStatement pStmtForSub = null; try { pStmtForSub = PreparedStatement.getInstance(this.connection, this.originalSql, this.currentCatalog); int numParameters = pStmtForSub.parameterCount; int ourNumParameters = this.parameterCount; for (int i = 0; i < numParameters && i < ourNumParameters; i++) { if (this.parameterBindings[i] != null)
/*      */           if ((this.parameterBindings[i]).isNull) { pStmtForSub.setNull(i + 1, 0); } else { BindValue bindValue = this.parameterBindings[i]; switch (bindValue.bufferType) { case 1: pStmtForSub.setByte(i + 1, bindValue.byteBinding); break;
/*      */               case 2: pStmtForSub.setShort(i + 1, bindValue.shortBinding); break;
/*      */               case 3: pStmtForSub.setInt(i + 1, bindValue.intBinding); break;
/*      */               case 8: pStmtForSub.setLong(i + 1, bindValue.longBinding); break;
/*      */               case 4: pStmtForSub.setFloat(i + 1, bindValue.floatBinding); break;
/*      */               case 5:
/*      */                 pStmtForSub.setDouble(i + 1, bindValue.doubleBinding); break;
/*      */               default:
/*      */                 pStmtForSub.setObject(i + 1, (this.parameterBindings[i]).value); break; }  }   }  return pStmtForSub.asSql(quoteStreamsAndUnknowns); } finally { if (pStmtForSub != null)
/*  347 */         try { pStmtForSub.close(); } catch (SQLException sqlEx) {}  }  } protected static ServerPreparedStatement getInstance(ConnectionImpl conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException { if (!Util.isJdbc4()) {
/*  348 */       return new ServerPreparedStatement(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/*  353 */       return JDBC_4_SPS_CTOR.newInstance(new Object[] { conn, sql, catalog, Constants.integerValueOf(resultSetType), Constants.integerValueOf(resultSetConcurrency) });
/*      */     
/*      */     }
/*  356 */     catch (IllegalArgumentException e) {
/*  357 */       throw new SQLException(e.toString(), "S1000");
/*  358 */     } catch (InstantiationException e) {
/*  359 */       throw new SQLException(e.toString(), "S1000");
/*  360 */     } catch (IllegalAccessException e) {
/*  361 */       throw new SQLException(e.toString(), "S1000");
/*  362 */     } catch (InvocationTargetException e) {
/*  363 */       Throwable target = e.getTargetException();
/*      */       
/*  365 */       if (target instanceof SQLException) {
/*  366 */         throw (SQLException)target;
/*      */       }
/*      */       
/*  369 */       throw new SQLException(target.toString(), "S1000");
/*      */     }  }
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
/*      */   protected ServerPreparedStatement(ConnectionImpl conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  389 */     super(conn, catalog);
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
/*  572 */     this.isCached = false; checkNullOrEmptyQuery(sql); int startOfStatement = findStartOfStatement(sql); this.firstCharOfStmt = StringUtils.firstAlphaCharUc(sql, startOfStatement); this.isSelectQuery = ('S' == this.firstCharOfStmt); if (this.connection.versionMeetsMinimum(5, 0, 0)) { this.serverNeedsResetBeforeEachExecution = !this.connection.versionMeetsMinimum(5, 0, 3); }
/*      */     else { this.serverNeedsResetBeforeEachExecution = !this.connection.versionMeetsMinimum(4, 1, 10); }
/*      */      this.useAutoSlowLog = this.connection.getAutoSlowLog(); this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23); this.hasLimitClause = (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1); String statementComment = this.connection.getStatementComment(); this.originalSql = (statementComment == null) ? sql : ("/* " + statementComment + " */ " + sql); if (this.connection.versionMeetsMinimum(4, 1, 2)) { this.stringTypeCode = 253; }
/*      */     else { this.stringTypeCode = 254; }
/*      */      try { serverPrepare(sql); }
/*      */     catch (SQLException sqlEx)
/*      */     { realClose(false, true); throw sqlEx; }
/*      */     catch (Exception ex)
/*      */     { realClose(false, true); SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000"); sqlEx.initCause(ex); throw sqlEx; }
/*  581 */      setResultSetType(resultSetType); setResultSetConcurrency(resultSetConcurrency); this.parameterTypes = new int[this.parameterCount]; } protected void setClosed(boolean flag) { this.isClosed = flag; } protected void checkClosed() throws SQLException { if (this.invalid)
/*      */       throw this.invalidationException;  super.checkClosed(); }
/*      */   public void clearParameters() throws SQLException { checkClosed(); clearParametersInternal(true); }
/*      */   private void clearParametersInternal(boolean clearServerParameters) throws SQLException { boolean hadLongData = false; if (this.parameterBindings != null)
/*      */       for (int i = 0; i < this.parameterCount; i++) { if (this.parameterBindings[i] != null && (this.parameterBindings[i]).isLongData)
/*      */           hadLongData = true;  this.parameterBindings[i].reset(); }   if (clearServerParameters && hadLongData) { serverResetStatement(); this.detectedLongParameterSwitch = false; }
/*      */      }
/*  588 */   public synchronized void close() throws SQLException { if (this.isCached && !this.isClosed) {
/*  589 */       clearParameters();
/*      */       
/*  591 */       this.isClosed = true;
/*      */       
/*  593 */       this.connection.recachePreparedStatement(this);
/*      */       
/*      */       return;
/*      */     } 
/*  597 */     realClose(true, true); }
/*      */ 
/*      */   
/*      */   private void dumpCloseForTestcase() {
/*  601 */     StringBuffer buf = new StringBuffer();
/*  602 */     this.connection.generateConnectionCommentBlock(buf);
/*  603 */     buf.append("DEALLOCATE PREPARE debug_stmt_");
/*  604 */     buf.append(this.statementId);
/*  605 */     buf.append(";\n");
/*      */     
/*  607 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */   
/*      */   private void dumpExecuteForTestcase() throws SQLException {
/*  611 */     StringBuffer buf = new StringBuffer();
/*      */     int i;
/*  613 */     for (i = 0; i < this.parameterCount; i++) {
/*  614 */       this.connection.generateConnectionCommentBlock(buf);
/*      */       
/*  616 */       buf.append("SET @debug_stmt_param");
/*  617 */       buf.append(this.statementId);
/*  618 */       buf.append("_");
/*  619 */       buf.append(i);
/*  620 */       buf.append("=");
/*      */       
/*  622 */       if ((this.parameterBindings[i]).isNull) {
/*  623 */         buf.append("NULL");
/*      */       } else {
/*  625 */         buf.append(this.parameterBindings[i].toString(true));
/*      */       } 
/*      */       
/*  628 */       buf.append(";\n");
/*      */     } 
/*      */     
/*  631 */     this.connection.generateConnectionCommentBlock(buf);
/*      */     
/*  633 */     buf.append("EXECUTE debug_stmt_");
/*  634 */     buf.append(this.statementId);
/*      */     
/*  636 */     if (this.parameterCount > 0) {
/*  637 */       buf.append(" USING ");
/*  638 */       for (i = 0; i < this.parameterCount; i++) {
/*  639 */         if (i > 0) {
/*  640 */           buf.append(", ");
/*      */         }
/*      */         
/*  643 */         buf.append("@debug_stmt_param");
/*  644 */         buf.append(this.statementId);
/*  645 */         buf.append("_");
/*  646 */         buf.append(i);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  651 */     buf.append(";\n");
/*      */     
/*  653 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   
/*      */   private void dumpPrepareForTestcase() throws SQLException {
/*  658 */     StringBuffer buf = new StringBuffer(this.originalSql.length() + 64);
/*      */     
/*  660 */     this.connection.generateConnectionCommentBlock(buf);
/*      */     
/*  662 */     buf.append("PREPARE debug_stmt_");
/*  663 */     buf.append(this.statementId);
/*  664 */     buf.append(" FROM \"");
/*  665 */     buf.append(this.originalSql);
/*  666 */     buf.append("\";\n");
/*      */     
/*  668 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */   
/*      */   protected int[] executeBatchSerially(int batchTimeout) throws SQLException {
/*  672 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */     
/*  674 */     if (locallyScopedConn == null) {
/*  675 */       checkClosed();
/*      */     }
/*      */     
/*  678 */     if (locallyScopedConn.isReadOnly()) {
/*  679 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  685 */     checkClosed();
/*      */     
/*  687 */     synchronized (locallyScopedConn.getMutex()) {
/*  688 */       clearWarnings();
/*      */ 
/*      */ 
/*      */       
/*  692 */       BindValue[] oldBindValues = this.parameterBindings;
/*      */       
/*      */       try {
/*  695 */         int[] updateCounts = null;
/*      */         
/*  697 */         if (this.batchedArgs != null) {
/*  698 */           int nbrCommands = this.batchedArgs.size();
/*  699 */           updateCounts = new int[nbrCommands];
/*      */           
/*  701 */           if (this.retrieveGeneratedKeys) {
/*  702 */             this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */           }
/*      */           
/*  705 */           for (int i = 0; i < nbrCommands; i++) {
/*  706 */             updateCounts[i] = -3;
/*      */           }
/*      */           
/*  709 */           SQLException sqlEx = null;
/*      */           
/*  711 */           int commandIndex = 0;
/*      */           
/*  713 */           BindValue[] previousBindValuesForBatch = null;
/*      */           
/*  715 */           StatementImpl.CancelTask timeoutTask = null;
/*      */           
/*      */           try {
/*  718 */             if (locallyScopedConn.getEnableQueryTimeouts() && batchTimeout != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */               
/*  721 */               timeoutTask = new StatementImpl.CancelTask(this, this);
/*  722 */               ConnectionImpl.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */             } 
/*      */ 
/*      */             
/*  726 */             for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*  727 */               Object arg = this.batchedArgs.get(commandIndex);
/*      */               
/*  729 */               if (arg instanceof String) {
/*  730 */                 updateCounts[commandIndex] = executeUpdate((String)arg);
/*      */               } else {
/*  732 */                 this.parameterBindings = ((BatchedBindValues)arg).batchedParameterValues;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*      */                 try {
/*  739 */                   if (previousBindValuesForBatch != null) {
/*  740 */                     for (int j = 0; j < this.parameterBindings.length; j++) {
/*  741 */                       if ((this.parameterBindings[j]).bufferType != (previousBindValuesForBatch[j]).bufferType) {
/*  742 */                         this.sendTypesToServer = true;
/*      */                         
/*      */                         break;
/*      */                       } 
/*      */                     } 
/*      */                   }
/*      */                   
/*      */                   try {
/*  750 */                     updateCounts[commandIndex] = executeUpdate(false, true);
/*      */                   } finally {
/*  752 */                     previousBindValuesForBatch = this.parameterBindings;
/*      */                   } 
/*      */                   
/*  755 */                   if (this.retrieveGeneratedKeys) {
/*  756 */                     ResultSet rs = null;
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
/*      */                   }
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
/*      */                 }
/*  781 */                 catch (SQLException ex) {
/*  782 */                   updateCounts[commandIndex] = -3;
/*      */                   
/*  784 */                   if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException)) {
/*      */ 
/*      */                     
/*  787 */                     sqlEx = ex;
/*      */                   } else {
/*  789 */                     int[] newUpdateCounts = new int[commandIndex];
/*  790 */                     System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*      */                     
/*  793 */                     throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */                   }
/*      */                 
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } finally {
/*      */             
/*  801 */             if (timeoutTask != null) {
/*  802 */               timeoutTask.cancel();
/*      */             }
/*      */             
/*  805 */             resetCancelledState();
/*      */           } 
/*      */           
/*  808 */           if (sqlEx != null) {
/*  809 */             throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  815 */         return (updateCounts != null) ? updateCounts : new int[0];
/*      */       } finally {
/*  817 */         this.parameterBindings = oldBindValues;
/*  818 */         this.sendTypesToServer = true;
/*      */         
/*  820 */         clearBatch();
/*      */       } 
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
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch) throws SQLException {
/*  834 */     this.numberOfExecutions++;
/*      */ 
/*      */     
/*      */     try {
/*  838 */       return serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadataFromCache);
/*      */     }
/*  840 */     catch (SQLException sqlEx) {
/*      */       
/*  842 */       if (this.connection.getEnablePacketDebug()) {
/*  843 */         this.connection.getIO().dumpPacketRingBuffer();
/*      */       }
/*      */       
/*  846 */       if (this.connection.getDumpQueriesOnException()) {
/*  847 */         String extractedSql = toString();
/*  848 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */         
/*  850 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */         
/*  852 */         messageBuf.append(extractedSql);
/*      */         
/*  854 */         sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString());
/*      */       } 
/*      */ 
/*      */       
/*  858 */       throw sqlEx;
/*  859 */     } catch (Exception ex) {
/*  860 */       if (this.connection.getEnablePacketDebug()) {
/*  861 */         this.connection.getIO().dumpPacketRingBuffer();
/*      */       }
/*      */       
/*  864 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000");
/*      */ 
/*      */       
/*  867 */       if (this.connection.getDumpQueriesOnException()) {
/*  868 */         String extractedSql = toString();
/*  869 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */         
/*  871 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */         
/*  873 */         messageBuf.append(extractedSql);
/*      */         
/*  875 */         sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString());
/*      */       } 
/*      */ 
/*      */       
/*  879 */       sqlEx.initCause(ex);
/*      */       
/*  881 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Buffer fillSendPacket() throws SQLException {
/*  889 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths) throws SQLException {
/*  899 */     return null;
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
/*      */   protected BindValue getBinding(int parameterIndex, boolean forLongData) throws SQLException {
/*  913 */     checkClosed();
/*      */     
/*  915 */     if (this.parameterBindings.length == 0) {
/*  916 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.8"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  921 */     parameterIndex--;
/*      */     
/*  923 */     if (parameterIndex < 0 || parameterIndex >= this.parameterBindings.length)
/*      */     {
/*  925 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + this.parameterBindings.length, "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  933 */     if (this.parameterBindings[parameterIndex] == null) {
/*  934 */       this.parameterBindings[parameterIndex] = new BindValue();
/*      */     }
/*  936 */     else if ((this.parameterBindings[parameterIndex]).isLongData && !forLongData) {
/*      */       
/*  938 */       this.detectedLongParameterSwitch = true;
/*      */     } 
/*      */ 
/*      */     
/*  942 */     (this.parameterBindings[parameterIndex]).isSet = true;
/*  943 */     (this.parameterBindings[parameterIndex]).boundBeforeExecutionNum = this.numberOfExecutions;
/*      */     
/*  945 */     return this.parameterBindings[parameterIndex];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   byte[] getBytes(int parameterIndex) throws SQLException {
/*  952 */     BindValue bindValue = getBinding(parameterIndex, false);
/*      */     
/*  954 */     if (bindValue.isNull)
/*  955 */       return null; 
/*  956 */     if (bindValue.isLongData) {
/*  957 */       throw SQLError.notImplemented();
/*      */     }
/*  959 */     if (this.outByteBuffer == null) {
/*  960 */       this.outByteBuffer = new Buffer(this.connection.getNetBufferLength());
/*      */     }
/*      */ 
/*      */     
/*  964 */     this.outByteBuffer.clear();
/*      */     
/*  966 */     int originalPosition = this.outByteBuffer.getPosition();
/*      */     
/*  968 */     storeBinding(this.outByteBuffer, bindValue, this.connection.getIO());
/*      */     
/*  970 */     int newPosition = this.outByteBuffer.getPosition();
/*      */     
/*  972 */     int length = newPosition - originalPosition;
/*      */     
/*  974 */     byte[] valueAsBytes = new byte[length];
/*      */     
/*  976 */     System.arraycopy(this.outByteBuffer.getByteBuffer(), originalPosition, valueAsBytes, 0, length);
/*      */ 
/*      */     
/*  979 */     return valueAsBytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/*  987 */     checkClosed();
/*      */     
/*  989 */     if (this.resultFields == null) {
/*  990 */       return null;
/*      */     }
/*      */     
/*  993 */     return new ResultSetMetaData(this.resultFields, this.connection.getUseOldAliasMetadataBehavior());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException {
/* 1001 */     checkClosed();
/*      */     
/* 1003 */     if (this.parameterMetaData == null) {
/* 1004 */       this.parameterMetaData = new MysqlParameterMetadata(this.parameterFields, this.parameterCount);
/*      */     }
/*      */ 
/*      */     
/* 1008 */     return this.parameterMetaData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isNull(int paramIndex) {
/* 1015 */     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
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
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
/* 1030 */     if (this.isClosed) {
/*      */       return;
/*      */     }
/*      */     
/* 1034 */     if (this.connection != null) {
/* 1035 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1036 */         dumpCloseForTestcase();
/*      */       }
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
/* 1050 */       SQLException exceptionDuringClose = null;
/*      */       
/* 1052 */       if (calledExplicitly && !this.connection.isClosed()) {
/* 1053 */         synchronized (this.connection.getMutex()) {
/*      */           
/*      */           try {
/* 1056 */             MysqlIO mysql = this.connection.getIO();
/*      */             
/* 1058 */             Buffer packet = mysql.getSharedSendPacket();
/*      */             
/* 1060 */             packet.writeByte((byte)25);
/* 1061 */             packet.writeLong(this.serverStatementId);
/*      */             
/* 1063 */             mysql.sendCommand(25, null, packet, true, null);
/*      */           }
/* 1065 */           catch (SQLException sqlEx) {
/* 1066 */             exceptionDuringClose = sqlEx;
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/* 1071 */       super.realClose(calledExplicitly, closeOpenResults);
/*      */       
/* 1073 */       clearParametersInternal(false);
/* 1074 */       this.parameterBindings = null;
/*      */       
/* 1076 */       this.parameterFields = null;
/* 1077 */       this.resultFields = null;
/*      */       
/* 1079 */       if (exceptionDuringClose != null) {
/* 1080 */         throw exceptionDuringClose;
/*      */       }
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
/*      */   protected void rePrepare() throws SQLException {
/* 1093 */     this.invalidationException = null;
/*      */     
/*      */     try {
/* 1096 */       serverPrepare(this.originalSql);
/* 1097 */     } catch (SQLException sqlEx) {
/*      */       
/* 1099 */       this.invalidationException = sqlEx;
/* 1100 */     } catch (Exception ex) {
/* 1101 */       this.invalidationException = SQLError.createSQLException(ex.toString(), "S1000");
/*      */       
/* 1103 */       this.invalidationException.initCause(ex);
/*      */     } 
/*      */     
/* 1106 */     if (this.invalidationException != null) {
/* 1107 */       this.invalid = true;
/*      */       
/* 1109 */       this.parameterBindings = null;
/*      */       
/* 1111 */       this.parameterFields = null;
/* 1112 */       this.resultFields = null;
/*      */       
/* 1114 */       if (this.results != null) {
/*      */         try {
/* 1116 */           this.results.close();
/* 1117 */         } catch (Exception ex) {}
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1122 */       if (this.connection != null) {
/* 1123 */         if (this.maxRowsChanged) {
/* 1124 */           this.connection.unsetMaxRows(this);
/*      */         }
/*      */         
/* 1127 */         if (!this.connection.getDontTrackOpenResources()) {
/* 1128 */           this.connection.unregisterStatement(this);
/*      */         }
/*      */       } 
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
/*      */   private ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, Field[] metadataFromCache) throws SQLException {
/* 1170 */     synchronized (this.connection.getMutex()) {
/* 1171 */       if (this.detectedLongParameterSwitch) {
/*      */         
/* 1173 */         boolean firstFound = false;
/* 1174 */         long boundTimeToCheck = 0L;
/*      */         
/* 1176 */         for (int m = 0; m < this.parameterCount - 1; m++) {
/* 1177 */           if ((this.parameterBindings[m]).isLongData) {
/* 1178 */             if (firstFound && boundTimeToCheck != (this.parameterBindings[m]).boundBeforeExecutionNum)
/*      */             {
/* 1180 */               throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.11") + Messages.getString("ServerPreparedStatement.12"), "S1C00");
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 1185 */             firstFound = true;
/* 1186 */             boundTimeToCheck = (this.parameterBindings[m]).boundBeforeExecutionNum;
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1194 */         serverResetStatement();
/*      */       } 
/*      */       
/*      */       int i;
/*      */       
/* 1199 */       for (i = 0; i < this.parameterCount; i++) {
/* 1200 */         if (!(this.parameterBindings[i]).isSet) {
/* 1201 */           throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.13") + (i + 1) + Messages.getString("ServerPreparedStatement.14"), "S1009");
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1211 */       for (i = 0; i < this.parameterCount; i++) {
/* 1212 */         if ((this.parameterBindings[i]).isLongData) {
/* 1213 */           serverLongData(i, this.parameterBindings[i]);
/*      */         }
/*      */       } 
/*      */       
/* 1217 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1218 */         dumpExecuteForTestcase();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1224 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1226 */       Buffer packet = mysql.getSharedSendPacket();
/*      */       
/* 1228 */       packet.clear();
/* 1229 */       packet.writeByte((byte)23);
/* 1230 */       packet.writeLong(this.serverStatementId);
/*      */       
/* 1232 */       boolean usingCursor = false;
/*      */       
/* 1234 */       if (this.connection.versionMeetsMinimum(4, 1, 2)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1241 */         if (this.resultFields != null && this.connection.isCursorFetchEnabled() && getResultSetType() == 1003 && getResultSetConcurrency() == 1007 && getFetchSize() > 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1246 */           packet.writeByte((byte)1);
/* 1247 */           usingCursor = true;
/*      */         } else {
/* 1249 */           packet.writeByte((byte)0);
/*      */         } 
/*      */         
/* 1252 */         packet.writeLong(1L);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1257 */       int nullCount = (this.parameterCount + 7) / 8;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1262 */       int nullBitsPosition = packet.getPosition();
/*      */       
/* 1264 */       for (int j = 0; j < nullCount; j++) {
/* 1265 */         packet.writeByte((byte)0);
/*      */       }
/*      */       
/* 1268 */       byte[] nullBitsBuffer = new byte[nullCount];
/*      */ 
/*      */       
/* 1271 */       packet.writeByte(this.sendTypesToServer ? 1 : 0);
/*      */       
/* 1273 */       if (this.sendTypesToServer)
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 1278 */         for (int m = 0; m < this.parameterCount; m++) {
/* 1279 */           packet.writeInt((this.parameterBindings[m]).bufferType);
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1286 */       for (int k = 0; k < this.parameterCount; k++) {
/* 1287 */         if (!(this.parameterBindings[k]).isLongData) {
/* 1288 */           if (!(this.parameterBindings[k]).isNull) {
/* 1289 */             storeBinding(packet, this.parameterBindings[k], mysql);
/*      */           } else {
/* 1291 */             nullBitsBuffer[k / 8] = (byte)(nullBitsBuffer[k / 8] | 1 << (k & 0x7));
/*      */           } 
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1300 */       int endPosition = packet.getPosition();
/* 1301 */       packet.setPosition(nullBitsPosition);
/* 1302 */       packet.writeBytesNoNull(nullBitsBuffer);
/* 1303 */       packet.setPosition(endPosition);
/*      */       
/* 1305 */       long begin = 0L;
/*      */       
/* 1307 */       boolean logSlowQueries = this.connection.getLogSlowQueries();
/* 1308 */       boolean gatherPerformanceMetrics = this.connection.getGatherPerformanceMetrics();
/*      */ 
/*      */       
/* 1311 */       if (this.profileSQL || logSlowQueries || gatherPerformanceMetrics) {
/* 1312 */         begin = mysql.getCurrentTimeNanosOrMillis();
/*      */       }
/*      */       
/* 1315 */       resetCancelledState();
/*      */       
/* 1317 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       
/*      */       try {
/* 1320 */         if (this.connection.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && this.connection.versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */           
/* 1323 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1324 */           ConnectionImpl.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         } 
/*      */ 
/*      */         
/* 1328 */         Buffer resultPacket = mysql.sendCommand(23, null, packet, false, null);
/*      */ 
/*      */         
/* 1331 */         long queryEndTime = 0L;
/*      */         
/* 1333 */         if (logSlowQueries || gatherPerformanceMetrics || this.profileSQL) {
/* 1334 */           queryEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */         }
/*      */         
/* 1337 */         if (timeoutTask != null) {
/* 1338 */           timeoutTask.cancel();
/*      */           
/* 1340 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1341 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */           
/* 1344 */           timeoutTask = null;
/*      */         } 
/*      */         
/* 1347 */         synchronized (this.cancelTimeoutMutex) {
/* 1348 */           if (this.wasCancelled) {
/* 1349 */             MySQLStatementCancelledException mySQLStatementCancelledException; SQLException cause = null;
/*      */             
/* 1351 */             if (this.wasCancelledByTimeout) {
/* 1352 */               MySQLTimeoutException mySQLTimeoutException = new MySQLTimeoutException();
/*      */             } else {
/* 1354 */               mySQLStatementCancelledException = new MySQLStatementCancelledException();
/*      */             } 
/*      */             
/* 1357 */             resetCancelledState();
/*      */             
/* 1359 */             throw mySQLStatementCancelledException;
/*      */           } 
/*      */         } 
/*      */         
/* 1363 */         boolean queryWasSlow = false;
/*      */         
/* 1365 */         if (logSlowQueries || gatherPerformanceMetrics) {
/* 1366 */           long elapsedTime = queryEndTime - begin;
/*      */           
/* 1368 */           if (logSlowQueries) {
/* 1369 */             if (this.useAutoSlowLog) {
/* 1370 */               queryWasSlow = (elapsedTime > this.connection.getSlowQueryThresholdMillis());
/*      */             } else {
/* 1372 */               queryWasSlow = this.connection.isAbonormallyLongQuery(elapsedTime);
/*      */               
/* 1374 */               this.connection.reportQueryTime(elapsedTime);
/*      */             } 
/*      */           }
/*      */           
/* 1378 */           if (queryWasSlow) {
/*      */             
/* 1380 */             StringBuffer mesgBuf = new StringBuffer(48 + this.originalSql.length());
/*      */             
/* 1382 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15"));
/*      */             
/* 1384 */             mesgBuf.append(mysql.getSlowQueryThreshold());
/* 1385 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15a"));
/*      */             
/* 1387 */             mesgBuf.append(elapsedTime);
/* 1388 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.16"));
/*      */ 
/*      */             
/* 1391 */             mesgBuf.append("as prepared: ");
/* 1392 */             mesgBuf.append(this.originalSql);
/* 1393 */             mesgBuf.append("\n\n with parameters bound:\n\n");
/* 1394 */             mesgBuf.append(asSql(true));
/*      */             
/* 1396 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)6, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), elapsedTime, mysql.getQueryTimingUnits(), null, new Throwable(), mesgBuf.toString()));
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1406 */           if (gatherPerformanceMetrics) {
/* 1407 */             this.connection.registerQueryExecutionTime(elapsedTime);
/*      */           }
/*      */         } 
/*      */         
/* 1411 */         this.connection.incrementNumberOfPreparedExecutes();
/*      */         
/* 1413 */         if (this.profileSQL) {
/* 1414 */           this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/*      */           
/* 1417 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)4, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), (int)(mysql.getCurrentTimeNanosOrMillis() - begin), mysql.getQueryTimingUnits(), null, new Throwable(), truncateQueryToLog(asSql(true))));
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1427 */         ResultSetInternalMethods rs = mysql.readAllResults(this, maxRowsToRetrieve, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, resultPacket, true, this.fieldCount, metadataFromCache);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1433 */         if (this.profileSQL) {
/* 1434 */           long fetchEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */           
/* 1436 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)5, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), fetchEndTime - queryEndTime, mysql.getQueryTimingUnits(), null, new Throwable(), null));
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1445 */         if (queryWasSlow && this.connection.getExplainSlowQueries()) {
/* 1446 */           String queryAsString = asSql(true);
/*      */           
/* 1448 */           mysql.explainSlowQuery(queryAsString.getBytes(), queryAsString);
/*      */         } 
/*      */ 
/*      */         
/* 1452 */         if (!createStreamingResultSet && this.serverNeedsResetBeforeEachExecution)
/*      */         {
/* 1454 */           serverResetStatement();
/*      */         }
/*      */ 
/*      */         
/* 1458 */         this.sendTypesToServer = false;
/* 1459 */         this.results = rs;
/*      */         
/* 1461 */         if (mysql.hadWarnings()) {
/* 1462 */           mysql.scanForAndThrowDataTruncation();
/*      */         }
/*      */         
/* 1465 */         return rs;
/*      */       } finally {
/* 1467 */         if (timeoutTask != null) {
/* 1468 */           timeoutTask.cancel();
/*      */         }
/*      */       } 
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void serverLongData(int parameterIndex, BindValue longData) throws SQLException {
/* 1503 */     synchronized (this.connection.getMutex()) {
/* 1504 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1506 */       Buffer packet = mysql.getSharedSendPacket();
/*      */       
/* 1508 */       Object value = longData.value;
/*      */       
/* 1510 */       if (value instanceof byte[]) {
/* 1511 */         packet.clear();
/* 1512 */         packet.writeByte((byte)24);
/* 1513 */         packet.writeLong(this.serverStatementId);
/* 1514 */         packet.writeInt(parameterIndex);
/*      */         
/* 1516 */         packet.writeBytesNoNull((byte[])longData.value);
/*      */         
/* 1518 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/* 1520 */       else if (value instanceof InputStream) {
/* 1521 */         storeStream(mysql, parameterIndex, packet, (InputStream)value);
/* 1522 */       } else if (value instanceof Blob) {
/* 1523 */         storeStream(mysql, parameterIndex, packet, ((Blob)value).getBinaryStream());
/*      */       }
/* 1525 */       else if (value instanceof Reader) {
/* 1526 */         storeReader(mysql, parameterIndex, packet, (Reader)value);
/*      */       } else {
/* 1528 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", "S1009");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void serverPrepare(String sql) throws SQLException {
/* 1537 */     synchronized (this.connection.getMutex()) {
/* 1538 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1540 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1541 */         dumpPrepareForTestcase();
/*      */       }
/*      */       
/*      */       try {
/* 1545 */         long begin = 0L;
/*      */         
/* 1547 */         if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA")) {
/* 1548 */           this.isLoadDataQuery = true;
/*      */         } else {
/* 1550 */           this.isLoadDataQuery = false;
/*      */         } 
/*      */         
/* 1553 */         if (this.connection.getProfileSql()) {
/* 1554 */           begin = System.currentTimeMillis();
/*      */         }
/*      */         
/* 1557 */         String characterEncoding = null;
/* 1558 */         String connectionEncoding = this.connection.getEncoding();
/*      */         
/* 1560 */         if (!this.isLoadDataQuery && this.connection.getUseUnicode() && connectionEncoding != null)
/*      */         {
/* 1562 */           characterEncoding = connectionEncoding;
/*      */         }
/*      */         
/* 1565 */         Buffer prepareResultPacket = mysql.sendCommand(22, sql, null, false, characterEncoding);
/*      */ 
/*      */ 
/*      */         
/* 1569 */         if (this.connection.versionMeetsMinimum(4, 1, 1)) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1574 */           prepareResultPacket.setPosition(1);
/*      */         }
/*      */         else {
/*      */           
/* 1578 */           prepareResultPacket.setPosition(0);
/*      */         } 
/*      */         
/* 1581 */         this.serverStatementId = prepareResultPacket.readLong();
/* 1582 */         this.fieldCount = prepareResultPacket.readInt();
/* 1583 */         this.parameterCount = prepareResultPacket.readInt();
/* 1584 */         this.parameterBindings = new BindValue[this.parameterCount];
/*      */         
/* 1586 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1587 */           this.parameterBindings[i] = new BindValue();
/*      */         }
/*      */         
/* 1590 */         this.connection.incrementNumberOfPrepares();
/*      */         
/* 1592 */         if (this.profileSQL) {
/* 1593 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)2, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), mysql.getCurrentTimeNanosOrMillis() - begin, mysql.getQueryTimingUnits(), null, new Throwable(), truncateQueryToLog(sql)));
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1603 */         if (this.parameterCount > 0 && 
/* 1604 */           this.connection.versionMeetsMinimum(4, 1, 2) && !mysql.isVersion(5, 0, 0)) {
/*      */           
/* 1606 */           this.parameterFields = new Field[this.parameterCount];
/*      */           
/* 1608 */           Buffer metaDataPacket = mysql.readPacket();
/*      */           
/* 1610 */           int j = 0;
/*      */ 
/*      */           
/* 1613 */           while (!metaDataPacket.isLastDataPacket() && j < this.parameterCount) {
/* 1614 */             this.parameterFields[j++] = mysql.unpackField(metaDataPacket, false);
/*      */             
/* 1616 */             metaDataPacket = mysql.readPacket();
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1621 */         if (this.fieldCount > 0) {
/* 1622 */           this.resultFields = new Field[this.fieldCount];
/*      */           
/* 1624 */           Buffer fieldPacket = mysql.readPacket();
/*      */           
/* 1626 */           int j = 0;
/*      */ 
/*      */ 
/*      */           
/* 1630 */           while (!fieldPacket.isLastDataPacket() && j < this.fieldCount) {
/* 1631 */             this.resultFields[j++] = mysql.unpackField(fieldPacket, false);
/*      */             
/* 1633 */             fieldPacket = mysql.readPacket();
/*      */           } 
/*      */         } 
/* 1636 */       } catch (SQLException sqlEx) {
/* 1637 */         if (this.connection.getDumpQueriesOnException()) {
/* 1638 */           StringBuffer messageBuf = new StringBuffer(this.originalSql.length() + 32);
/*      */           
/* 1640 */           messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
/*      */           
/* 1642 */           messageBuf.append(this.originalSql);
/*      */           
/* 1644 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString());
/*      */         } 
/*      */ 
/*      */         
/* 1648 */         throw sqlEx;
/*      */       
/*      */       }
/*      */       finally {
/*      */         
/* 1653 */         this.connection.getIO().clearInputStream();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private String truncateQueryToLog(String sql) {
/* 1659 */     String query = null;
/*      */     
/* 1661 */     if (sql.length() > this.connection.getMaxQuerySizeToLog()) {
/* 1662 */       StringBuffer queryBuf = new StringBuffer(this.connection.getMaxQuerySizeToLog() + 12);
/*      */       
/* 1664 */       queryBuf.append(sql.substring(0, this.connection.getMaxQuerySizeToLog()));
/* 1665 */       queryBuf.append(Messages.getString("MysqlIO.25"));
/*      */       
/* 1667 */       query = queryBuf.toString();
/*      */     } else {
/* 1669 */       query = sql;
/*      */     } 
/*      */     
/* 1672 */     return query;
/*      */   }
/*      */   
/*      */   private void serverResetStatement() throws SQLException {
/* 1676 */     synchronized (this.connection.getMutex()) {
/*      */       
/* 1678 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1680 */       Buffer packet = mysql.getSharedSendPacket();
/*      */       
/* 1682 */       packet.clear();
/* 1683 */       packet.writeByte((byte)26);
/* 1684 */       packet.writeLong(this.serverStatementId);
/*      */       
/*      */       try {
/* 1687 */         mysql.sendCommand(26, null, packet, !this.connection.versionMeetsMinimum(4, 1, 2), null);
/*      */       }
/* 1689 */       catch (SQLException sqlEx) {
/* 1690 */         throw sqlEx;
/* 1691 */       } catch (Exception ex) {
/* 1692 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000");
/*      */         
/* 1694 */         sqlEx.initCause(ex);
/*      */         
/* 1696 */         throw sqlEx;
/*      */       } finally {
/* 1698 */         mysql.clearInputStream();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setArray(int i, Array x) throws SQLException {
/* 1707 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 1716 */     checkClosed();
/*      */     
/* 1718 */     if (x == null) {
/* 1719 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1721 */       BindValue binding = getBinding(parameterIndex, true);
/* 1722 */       setType(binding, 252);
/*      */       
/* 1724 */       binding.value = x;
/* 1725 */       binding.isNull = false;
/* 1726 */       binding.isLongData = true;
/*      */       
/* 1728 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1729 */         binding.bindLength = length;
/*      */       } else {
/* 1731 */         binding.bindLength = -1L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/* 1741 */     checkClosed();
/*      */     
/* 1743 */     if (x == null) {
/* 1744 */       setNull(parameterIndex, 3);
/*      */     } else {
/*      */       
/* 1747 */       BindValue binding = getBinding(parameterIndex, false);
/*      */       
/* 1749 */       if (this.connection.versionMeetsMinimum(5, 0, 3)) {
/* 1750 */         setType(binding, 246);
/*      */       } else {
/* 1752 */         setType(binding, this.stringTypeCode);
/*      */       } 
/*      */       
/* 1755 */       binding.value = StringUtils.fixDecimalExponent(StringUtils.consistentToString(x));
/*      */       
/* 1757 */       binding.isNull = false;
/* 1758 */       binding.isLongData = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 1768 */     checkClosed();
/*      */     
/* 1770 */     if (x == null) {
/* 1771 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1773 */       BindValue binding = getBinding(parameterIndex, true);
/* 1774 */       setType(binding, 252);
/*      */       
/* 1776 */       binding.value = x;
/* 1777 */       binding.isNull = false;
/* 1778 */       binding.isLongData = true;
/*      */       
/* 1780 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1781 */         binding.bindLength = length;
/*      */       } else {
/* 1783 */         binding.bindLength = -1L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBlob(int parameterIndex, Blob x) throws SQLException {
/* 1792 */     checkClosed();
/*      */     
/* 1794 */     if (x == null) {
/* 1795 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1797 */       BindValue binding = getBinding(parameterIndex, true);
/* 1798 */       setType(binding, 252);
/*      */       
/* 1800 */       binding.value = x;
/* 1801 */       binding.isNull = false;
/* 1802 */       binding.isLongData = true;
/*      */       
/* 1804 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1805 */         binding.bindLength = x.length();
/*      */       } else {
/* 1807 */         binding.bindLength = -1L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/* 1816 */     setByte(parameterIndex, x ? 1 : 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setByte(int parameterIndex, byte x) throws SQLException {
/* 1823 */     checkClosed();
/*      */     
/* 1825 */     BindValue binding = getBinding(parameterIndex, false);
/* 1826 */     setType(binding, 1);
/*      */     
/* 1828 */     binding.value = null;
/* 1829 */     binding.byteBinding = x;
/* 1830 */     binding.isNull = false;
/* 1831 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/* 1838 */     checkClosed();
/*      */     
/* 1840 */     if (x == null) {
/* 1841 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1843 */       BindValue binding = getBinding(parameterIndex, false);
/* 1844 */       setType(binding, 253);
/*      */       
/* 1846 */       binding.value = x;
/* 1847 */       binding.isNull = false;
/* 1848 */       binding.isLongData = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/* 1858 */     checkClosed();
/*      */     
/* 1860 */     if (reader == null) {
/* 1861 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1863 */       BindValue binding = getBinding(parameterIndex, true);
/* 1864 */       setType(binding, 252);
/*      */       
/* 1866 */       binding.value = reader;
/* 1867 */       binding.isNull = false;
/* 1868 */       binding.isLongData = true;
/*      */       
/* 1870 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1871 */         binding.bindLength = length;
/*      */       } else {
/* 1873 */         binding.bindLength = -1L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClob(int parameterIndex, Clob x) throws SQLException {
/* 1882 */     checkClosed();
/*      */     
/* 1884 */     if (x == null) {
/* 1885 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1887 */       BindValue binding = getBinding(parameterIndex, true);
/* 1888 */       setType(binding, 252);
/*      */       
/* 1890 */       binding.value = x.getCharacterStream();
/* 1891 */       binding.isNull = false;
/* 1892 */       binding.isLongData = true;
/*      */       
/* 1894 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1895 */         binding.bindLength = x.length();
/*      */       } else {
/* 1897 */         binding.bindLength = -1L;
/*      */       } 
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
/*      */   public void setDate(int parameterIndex, Date x) throws SQLException {
/* 1915 */     setDate(parameterIndex, x, (Calendar)null);
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
/*      */   public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
/* 1934 */     if (x == null) {
/* 1935 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 1937 */       BindValue binding = getBinding(parameterIndex, false);
/* 1938 */       setType(binding, 10);
/*      */       
/* 1940 */       binding.value = x;
/* 1941 */       binding.isNull = false;
/* 1942 */       binding.isLongData = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDouble(int parameterIndex, double x) throws SQLException {
/* 1950 */     checkClosed();
/*      */     
/* 1952 */     if (!this.connection.getAllowNanAndInf() && (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || Double.isNaN(x)))
/*      */     {
/*      */       
/* 1955 */       throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1961 */     BindValue binding = getBinding(parameterIndex, false);
/* 1962 */     setType(binding, 5);
/*      */     
/* 1964 */     binding.value = null;
/* 1965 */     binding.doubleBinding = x;
/* 1966 */     binding.isNull = false;
/* 1967 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFloat(int parameterIndex, float x) throws SQLException {
/* 1974 */     checkClosed();
/*      */     
/* 1976 */     BindValue binding = getBinding(parameterIndex, false);
/* 1977 */     setType(binding, 4);
/*      */     
/* 1979 */     binding.value = null;
/* 1980 */     binding.floatBinding = x;
/* 1981 */     binding.isNull = false;
/* 1982 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInt(int parameterIndex, int x) throws SQLException {
/* 1989 */     checkClosed();
/*      */     
/* 1991 */     BindValue binding = getBinding(parameterIndex, false);
/* 1992 */     setType(binding, 3);
/*      */     
/* 1994 */     binding.value = null;
/* 1995 */     binding.intBinding = x;
/* 1996 */     binding.isNull = false;
/* 1997 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLong(int parameterIndex, long x) throws SQLException {
/* 2004 */     checkClosed();
/*      */     
/* 2006 */     BindValue binding = getBinding(parameterIndex, false);
/* 2007 */     setType(binding, 8);
/*      */     
/* 2009 */     binding.value = null;
/* 2010 */     binding.longBinding = x;
/* 2011 */     binding.isNull = false;
/* 2012 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNull(int parameterIndex, int sqlType) throws SQLException {
/* 2019 */     checkClosed();
/*      */     
/* 2021 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2027 */     if (binding.bufferType == 0) {
/* 2028 */       setType(binding, 6);
/*      */     }
/*      */     
/* 2031 */     binding.value = null;
/* 2032 */     binding.isNull = true;
/* 2033 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
/* 2041 */     checkClosed();
/*      */     
/* 2043 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2049 */     if (binding.bufferType == 0) {
/* 2050 */       setType(binding, 6);
/*      */     }
/*      */     
/* 2053 */     binding.value = null;
/* 2054 */     binding.isNull = true;
/* 2055 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRef(int i, Ref x) throws SQLException {
/* 2062 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setShort(int parameterIndex, short x) throws SQLException {
/* 2069 */     checkClosed();
/*      */     
/* 2071 */     BindValue binding = getBinding(parameterIndex, false);
/* 2072 */     setType(binding, 2);
/*      */     
/* 2074 */     binding.value = null;
/* 2075 */     binding.shortBinding = x;
/* 2076 */     binding.isNull = false;
/* 2077 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setString(int parameterIndex, String x) throws SQLException {
/* 2084 */     checkClosed();
/*      */     
/* 2086 */     if (x == null) {
/* 2087 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 2089 */       BindValue binding = getBinding(parameterIndex, false);
/*      */       
/* 2091 */       setType(binding, this.stringTypeCode);
/*      */       
/* 2093 */       binding.value = x;
/* 2094 */       binding.isNull = false;
/* 2095 */       binding.isLongData = false;
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
/*      */   public void setTime(int parameterIndex, Time x) throws SQLException {
/* 2112 */     setTimeInternal(parameterIndex, x, (Calendar)null, this.connection.getDefaultTimeZone(), false);
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
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/* 2132 */     setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
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
/*      */   public void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 2153 */     if (x == null) {
/* 2154 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 2156 */       BindValue binding = getBinding(parameterIndex, false);
/* 2157 */       setType(binding, 11);
/*      */       
/* 2159 */       if (!this.useLegacyDatetimeCode) {
/* 2160 */         binding.value = x;
/*      */       } else {
/* 2162 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */         
/* 2164 */         synchronized (sessionCalendar) {
/* 2165 */           binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2174 */       binding.isNull = false;
/* 2175 */       binding.isLongData = false;
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
/*      */   public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
/* 2193 */     setTimestampInternal(parameterIndex, x, (Calendar)null, this.connection.getDefaultTimeZone(), false);
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
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/* 2212 */     setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 2219 */     if (x == null) {
/* 2220 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 2222 */       BindValue binding = getBinding(parameterIndex, false);
/* 2223 */       setType(binding, 12);
/*      */       
/* 2225 */       if (!this.useLegacyDatetimeCode) {
/* 2226 */         binding.value = x;
/*      */       } else {
/* 2228 */         Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */         
/* 2232 */         synchronized (sessionCalendar) {
/* 2233 */           binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2241 */         binding.isNull = false;
/* 2242 */         binding.isLongData = false;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void setType(BindValue oldValue, int bufferType) {
/* 2248 */     if (oldValue.bufferType != bufferType) {
/* 2249 */       this.sendTypesToServer = true;
/*      */     }
/*      */     
/* 2252 */     oldValue.bufferType = bufferType;
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
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 2276 */     checkClosed();
/*      */     
/* 2278 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURL(int parameterIndex, URL x) throws SQLException {
/* 2285 */     checkClosed();
/*      */     
/* 2287 */     setString(parameterIndex, x.toString());
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
/*      */   private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql) throws SQLException {
/*      */     try {
/* 2304 */       Object value = bindValue.value;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2309 */       switch (bindValue.bufferType) {
/*      */         
/*      */         case 1:
/* 2312 */           packet.writeByte(bindValue.byteBinding);
/*      */           return;
/*      */         case 2:
/* 2315 */           packet.ensureCapacity(2);
/* 2316 */           packet.writeInt(bindValue.shortBinding);
/*      */           return;
/*      */         case 3:
/* 2319 */           packet.ensureCapacity(4);
/* 2320 */           packet.writeLong(bindValue.intBinding);
/*      */           return;
/*      */         case 8:
/* 2323 */           packet.ensureCapacity(8);
/* 2324 */           packet.writeLongLong(bindValue.longBinding);
/*      */           return;
/*      */         case 4:
/* 2327 */           packet.ensureCapacity(4);
/* 2328 */           packet.writeFloat(bindValue.floatBinding);
/*      */           return;
/*      */         case 5:
/* 2331 */           packet.ensureCapacity(8);
/* 2332 */           packet.writeDouble(bindValue.doubleBinding);
/*      */           return;
/*      */         case 11:
/* 2335 */           storeTime(packet, (Time)value);
/*      */           return;
/*      */         case 7:
/*      */         case 10:
/*      */         case 12:
/* 2340 */           storeDateTime(packet, (Date)value, mysql, bindValue.bufferType);
/*      */           return;
/*      */         case 0:
/*      */         case 15:
/*      */         case 246:
/*      */         case 253:
/*      */         case 254:
/* 2347 */           if (value instanceof byte[]) {
/* 2348 */             packet.writeLenBytes((byte[])value);
/* 2349 */           } else if (!this.isLoadDataQuery) {
/* 2350 */             packet.writeLenString((String)value, this.charEncoding, this.connection.getServerCharacterEncoding(), this.charConverter, this.connection.parserKnowsUnicode(), this.connection);
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 2356 */             packet.writeLenBytes(((String)value).getBytes());
/*      */           } 
/*      */           return;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/* 2363 */     } catch (UnsupportedEncodingException uEE) {
/* 2364 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.22") + this.connection.getEncoding() + "'", "S1000");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void storeDateTime412AndOlder(Buffer intoBuf, Date dt, int bufferType) throws SQLException {
/* 2374 */     Calendar sessionCalendar = null;
/*      */     
/* 2376 */     if (!this.useLegacyDatetimeCode) {
/* 2377 */       if (bufferType == 10) {
/* 2378 */         sessionCalendar = getDefaultTzCalendar();
/*      */       } else {
/* 2380 */         sessionCalendar = getServerTzCalendar();
/*      */       } 
/*      */     } else {
/* 2383 */       sessionCalendar = (dt instanceof Timestamp && this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2388 */     synchronized (sessionCalendar) {
/* 2389 */       Date oldTime = sessionCalendar.getTime();
/*      */       
/*      */       try {
/* 2392 */         intoBuf.ensureCapacity(8);
/* 2393 */         intoBuf.writeByte((byte)7);
/*      */         
/* 2395 */         sessionCalendar.setTime(dt);
/*      */         
/* 2397 */         int year = sessionCalendar.get(1);
/* 2398 */         int month = sessionCalendar.get(2) + 1;
/* 2399 */         int date = sessionCalendar.get(5);
/*      */         
/* 2401 */         intoBuf.writeInt(year);
/* 2402 */         intoBuf.writeByte((byte)month);
/* 2403 */         intoBuf.writeByte((byte)date);
/*      */         
/* 2405 */         if (dt instanceof Date) {
/* 2406 */           intoBuf.writeByte((byte)0);
/* 2407 */           intoBuf.writeByte((byte)0);
/* 2408 */           intoBuf.writeByte((byte)0);
/*      */         } else {
/* 2410 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/*      */           
/* 2412 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/*      */           
/* 2414 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         } 
/*      */       } finally {
/*      */         
/* 2418 */         sessionCalendar.setTime(oldTime);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void storeDateTime(Buffer intoBuf, Date dt, MysqlIO mysql, int bufferType) throws SQLException {
/* 2425 */     if (this.connection.versionMeetsMinimum(4, 1, 3)) {
/* 2426 */       storeDateTime413AndNewer(intoBuf, dt, bufferType);
/*      */     } else {
/* 2428 */       storeDateTime412AndOlder(intoBuf, dt, bufferType);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void storeDateTime413AndNewer(Buffer intoBuf, Date dt, int bufferType) throws SQLException {
/* 2434 */     Calendar sessionCalendar = null;
/*      */     
/* 2436 */     if (!this.useLegacyDatetimeCode) {
/* 2437 */       if (bufferType == 10) {
/* 2438 */         sessionCalendar = getDefaultTzCalendar();
/*      */       } else {
/* 2440 */         sessionCalendar = getServerTzCalendar();
/*      */       } 
/*      */     } else {
/* 2443 */       sessionCalendar = (dt instanceof Timestamp && this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2448 */     synchronized (sessionCalendar) {
/* 2449 */       Date oldTime = sessionCalendar.getTime();
/*      */       
/*      */       try {
/* 2452 */         sessionCalendar.setTime(dt);
/*      */         
/* 2454 */         if (dt instanceof Date) {
/* 2455 */           sessionCalendar.set(11, 0);
/* 2456 */           sessionCalendar.set(12, 0);
/* 2457 */           sessionCalendar.set(13, 0);
/*      */         } 
/*      */         
/* 2460 */         byte length = 7;
/*      */         
/* 2462 */         if (dt instanceof Timestamp) {
/* 2463 */           length = 11;
/*      */         }
/*      */         
/* 2466 */         intoBuf.ensureCapacity(length);
/*      */         
/* 2468 */         intoBuf.writeByte(length);
/*      */         
/* 2470 */         int year = sessionCalendar.get(1);
/* 2471 */         int month = sessionCalendar.get(2) + 1;
/* 2472 */         int date = sessionCalendar.get(5);
/*      */         
/* 2474 */         intoBuf.writeInt(year);
/* 2475 */         intoBuf.writeByte((byte)month);
/* 2476 */         intoBuf.writeByte((byte)date);
/*      */         
/* 2478 */         if (dt instanceof Date) {
/* 2479 */           intoBuf.writeByte((byte)0);
/* 2480 */           intoBuf.writeByte((byte)0);
/* 2481 */           intoBuf.writeByte((byte)0);
/*      */         } else {
/* 2483 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/*      */           
/* 2485 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/*      */           
/* 2487 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         } 
/*      */ 
/*      */         
/* 2491 */         if (length == 11)
/*      */         {
/* 2493 */           intoBuf.writeLong((((Timestamp)dt).getNanos() / 1000));
/*      */         }
/*      */       } finally {
/*      */         
/* 2497 */         sessionCalendar.setTime(oldTime);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private Calendar getServerTzCalendar() {
/* 2503 */     synchronized (this) {
/* 2504 */       if (this.serverTzCalendar == null) {
/* 2505 */         this.serverTzCalendar = new GregorianCalendar(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */       
/* 2508 */       return this.serverTzCalendar;
/*      */     } 
/*      */   }
/*      */   
/*      */   private Calendar getDefaultTzCalendar() {
/* 2513 */     synchronized (this) {
/* 2514 */       if (this.defaultTzCalendar == null) {
/* 2515 */         this.defaultTzCalendar = new GregorianCalendar(TimeZone.getDefault());
/*      */       }
/*      */       
/* 2518 */       return this.defaultTzCalendar;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void storeReader(MysqlIO mysql, int parameterIndex, Buffer packet, Reader inStream) throws SQLException {
/* 2527 */     String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */     
/* 2529 */     String clobEncoding = (forcedEncoding == null) ? this.connection.getEncoding() : forcedEncoding;
/*      */ 
/*      */     
/* 2532 */     int maxBytesChar = 2;
/*      */     
/* 2534 */     if (clobEncoding != null) {
/* 2535 */       if (!clobEncoding.equals("UTF-16")) {
/* 2536 */         maxBytesChar = this.connection.getMaxBytesPerChar(clobEncoding);
/*      */         
/* 2538 */         if (maxBytesChar == 1) {
/* 2539 */           maxBytesChar = 2;
/*      */         }
/*      */       } else {
/* 2542 */         maxBytesChar = 4;
/*      */       } 
/*      */     }
/*      */     
/* 2546 */     char[] buf = new char[8192 / maxBytesChar];
/*      */     
/* 2548 */     int numRead = 0;
/*      */     
/* 2550 */     int bytesInPacket = 0;
/* 2551 */     int totalBytesRead = 0;
/* 2552 */     int bytesReadAtLastSend = 0;
/* 2553 */     int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 2558 */       packet.clear();
/* 2559 */       packet.writeByte((byte)24);
/* 2560 */       packet.writeLong(this.serverStatementId);
/* 2561 */       packet.writeInt(parameterIndex);
/*      */       
/* 2563 */       boolean readAny = false;
/*      */       
/* 2565 */       while ((numRead = inStream.read(buf)) != -1) {
/* 2566 */         readAny = true;
/*      */         
/* 2568 */         byte[] valueAsBytes = StringUtils.getBytes(buf, (SingleByteCharsetConverter)null, clobEncoding, this.connection.getServerCharacterEncoding(), 0, numRead, this.connection.parserKnowsUnicode());
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2573 */         packet.writeBytesNoNull(valueAsBytes, 0, valueAsBytes.length);
/*      */         
/* 2575 */         bytesInPacket += valueAsBytes.length;
/* 2576 */         totalBytesRead += valueAsBytes.length;
/*      */         
/* 2578 */         if (bytesInPacket >= packetIsFullAt) {
/* 2579 */           bytesReadAtLastSend = totalBytesRead;
/*      */           
/* 2581 */           mysql.sendCommand(24, null, packet, true, null);
/*      */ 
/*      */           
/* 2584 */           bytesInPacket = 0;
/* 2585 */           packet.clear();
/* 2586 */           packet.writeByte((byte)24);
/* 2587 */           packet.writeLong(this.serverStatementId);
/* 2588 */           packet.writeInt(parameterIndex);
/*      */         } 
/*      */       } 
/*      */       
/* 2592 */       if (totalBytesRead != bytesReadAtLastSend) {
/* 2593 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/*      */ 
/*      */       
/* 2597 */       if (!readAny) {
/* 2598 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/*      */     }
/* 2601 */     catch (IOException ioEx) {
/* 2602 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.24") + ioEx.toString(), "S1000");
/*      */ 
/*      */       
/* 2605 */       sqlEx.initCause(ioEx);
/*      */       
/* 2607 */       throw sqlEx;
/*      */     } finally {
/* 2609 */       if (this.connection.getAutoClosePStmtStreams() && 
/* 2610 */         inStream != null) {
/*      */         try {
/* 2612 */           inStream.close();
/* 2613 */         } catch (IOException ioEx) {}
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void storeStream(MysqlIO mysql, int parameterIndex, Buffer packet, InputStream inStream) throws SQLException {
/* 2623 */     byte[] buf = new byte[8192];
/*      */     
/* 2625 */     int numRead = 0;
/*      */     
/*      */     try {
/* 2628 */       int bytesInPacket = 0;
/* 2629 */       int totalBytesRead = 0;
/* 2630 */       int bytesReadAtLastSend = 0;
/* 2631 */       int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */       
/* 2633 */       packet.clear();
/* 2634 */       packet.writeByte((byte)24);
/* 2635 */       packet.writeLong(this.serverStatementId);
/* 2636 */       packet.writeInt(parameterIndex);
/*      */       
/* 2638 */       boolean readAny = false;
/*      */       
/* 2640 */       while ((numRead = inStream.read(buf)) != -1) {
/*      */         
/* 2642 */         readAny = true;
/*      */         
/* 2644 */         packet.writeBytesNoNull(buf, 0, numRead);
/* 2645 */         bytesInPacket += numRead;
/* 2646 */         totalBytesRead += numRead;
/*      */         
/* 2648 */         if (bytesInPacket >= packetIsFullAt) {
/* 2649 */           bytesReadAtLastSend = totalBytesRead;
/*      */           
/* 2651 */           mysql.sendCommand(24, null, packet, true, null);
/*      */ 
/*      */           
/* 2654 */           bytesInPacket = 0;
/* 2655 */           packet.clear();
/* 2656 */           packet.writeByte((byte)24);
/* 2657 */           packet.writeLong(this.serverStatementId);
/* 2658 */           packet.writeInt(parameterIndex);
/*      */         } 
/*      */       } 
/*      */       
/* 2662 */       if (totalBytesRead != bytesReadAtLastSend) {
/* 2663 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/*      */ 
/*      */       
/* 2667 */       if (!readAny) {
/* 2668 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/*      */     }
/* 2671 */     catch (IOException ioEx) {
/* 2672 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.25") + ioEx.toString(), "S1000");
/*      */ 
/*      */       
/* 2675 */       sqlEx.initCause(ioEx);
/*      */       
/* 2677 */       throw sqlEx;
/*      */     } finally {
/* 2679 */       if (this.connection.getAutoClosePStmtStreams() && 
/* 2680 */         inStream != null) {
/*      */         try {
/* 2682 */           inStream.close();
/* 2683 */         } catch (IOException ioEx) {}
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2695 */     StringBuffer toStringBuf = new StringBuffer();
/*      */     
/* 2697 */     toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement[");
/* 2698 */     toStringBuf.append(this.serverStatementId);
/* 2699 */     toStringBuf.append("] - ");
/*      */     
/*      */     try {
/* 2702 */       toStringBuf.append(asSql());
/* 2703 */     } catch (SQLException sqlEx) {
/* 2704 */       toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
/* 2705 */       toStringBuf.append(sqlEx);
/*      */     } 
/*      */     
/* 2708 */     return toStringBuf.toString();
/*      */   }
/*      */   
/*      */   protected long getServerStatementId() {
/* 2712 */     return this.serverStatementId;
/*      */   }
/*      */   
/*      */   public synchronized boolean canRewriteAsMultivalueInsertStatement() {
/* 2716 */     if (!super.canRewriteAsMultivalueInsertStatement()) {
/* 2717 */       return false;
/*      */     }
/*      */     
/* 2720 */     BindValue[] currentBindValues = null;
/* 2721 */     BindValue[] previousBindValues = null;
/*      */     
/* 2723 */     int nbrCommands = this.batchedArgs.size();
/*      */ 
/*      */ 
/*      */     
/* 2727 */     for (int commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 2728 */       Object arg = this.batchedArgs.get(commandIndex);
/*      */       
/* 2730 */       if (!(arg instanceof String)) {
/*      */         
/* 2732 */         currentBindValues = ((BatchedBindValues)arg).batchedParameterValues;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2738 */         if (previousBindValues != null) {
/* 2739 */           for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2740 */             if ((currentBindValues[j]).bufferType != (previousBindValues[j]).bufferType) {
/* 2741 */               return false;
/*      */             }
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 2748 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) {
/* 2756 */     long sizeOfEntireBatch = 10L;
/* 2757 */     long maxSizeOfParameterSet = 0L;
/*      */     
/* 2759 */     for (int i = 0; i < numBatchedArgs; i++) {
/* 2760 */       BindValue[] paramArg = ((BatchedBindValues)this.batchedArgs.get(i)).batchedParameterValues;
/*      */       
/* 2762 */       long sizeOfParameterSet = 0L;
/*      */       
/* 2764 */       sizeOfParameterSet += ((this.parameterCount + 7) / 8);
/*      */       
/* 2766 */       sizeOfParameterSet += (this.parameterCount * 2);
/*      */       
/* 2768 */       for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2769 */         if (!(paramArg[j]).isNull) {
/*      */           
/* 2771 */           long size = paramArg[j].getBoundLength();
/*      */           
/* 2773 */           if ((paramArg[j]).isLongData) {
/* 2774 */             if (size != -1L) {
/* 2775 */               sizeOfParameterSet += size;
/*      */             }
/*      */           } else {
/* 2778 */             sizeOfParameterSet += size;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 2783 */       sizeOfEntireBatch += sizeOfParameterSet;
/*      */       
/* 2785 */       if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 2786 */         maxSizeOfParameterSet = sizeOfParameterSet;
/*      */       }
/*      */     } 
/*      */     
/* 2790 */     return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int setOneBatchedParameterSet(PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
/* 2796 */     BindValue[] paramArg = ((BatchedBindValues)paramSet).batchedParameterValues;
/*      */     
/* 2798 */     for (int j = 0; j < paramArg.length; j++) {
/* 2799 */       if ((paramArg[j]).isNull) {
/* 2800 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 2802 */       else if ((paramArg[j]).isLongData) {
/* 2803 */         Object value = (paramArg[j]).value;
/*      */         
/* 2805 */         if (value instanceof InputStream) {
/* 2806 */           batchedStatement.setBinaryStream(batchedParamIndex++, (InputStream)value, (int)(paramArg[j]).bindLength);
/*      */         }
/*      */         else {
/*      */           
/* 2810 */           batchedStatement.setCharacterStream(batchedParamIndex++, (Reader)value, (int)(paramArg[j]).bindLength);
/*      */         } 
/*      */       } else {
/*      */         Object value;
/*      */         
/*      */         BindValue asBound;
/* 2816 */         switch ((paramArg[j]).bufferType) {
/*      */           
/*      */           case 1:
/* 2819 */             batchedStatement.setByte(batchedParamIndex++, (paramArg[j]).byteBinding);
/*      */             break;
/*      */           
/*      */           case 2:
/* 2823 */             batchedStatement.setShort(batchedParamIndex++, (paramArg[j]).shortBinding);
/*      */             break;
/*      */           
/*      */           case 3:
/* 2827 */             batchedStatement.setInt(batchedParamIndex++, (paramArg[j]).intBinding);
/*      */             break;
/*      */           
/*      */           case 8:
/* 2831 */             batchedStatement.setLong(batchedParamIndex++, (paramArg[j]).longBinding);
/*      */             break;
/*      */           
/*      */           case 4:
/* 2835 */             batchedStatement.setFloat(batchedParamIndex++, (paramArg[j]).floatBinding);
/*      */             break;
/*      */           
/*      */           case 5:
/* 2839 */             batchedStatement.setDouble(batchedParamIndex++, (paramArg[j]).doubleBinding);
/*      */             break;
/*      */           
/*      */           case 11:
/* 2843 */             batchedStatement.setTime(batchedParamIndex++, (Time)(paramArg[j]).value);
/*      */             break;
/*      */           
/*      */           case 10:
/* 2847 */             batchedStatement.setDate(batchedParamIndex++, (Date)(paramArg[j]).value);
/*      */             break;
/*      */           
/*      */           case 7:
/*      */           case 12:
/* 2852 */             batchedStatement.setTimestamp(batchedParamIndex++, (Timestamp)(paramArg[j]).value);
/*      */             break;
/*      */           
/*      */           case 0:
/*      */           case 15:
/*      */           case 246:
/*      */           case 253:
/*      */           case 254:
/* 2860 */             value = (paramArg[j]).value;
/*      */             
/* 2862 */             if (value instanceof byte[]) {
/* 2863 */               batchedStatement.setBytes(batchedParamIndex, (byte[])value);
/*      */             } else {
/*      */               
/* 2866 */               batchedStatement.setString(batchedParamIndex, (String)value);
/*      */             } 
/*      */ 
/*      */             
/* 2870 */             asBound = ((ServerPreparedStatement)batchedStatement).getBinding(batchedParamIndex + 1, false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2877 */             asBound.bufferType = (paramArg[j]).bufferType;
/*      */             
/* 2879 */             batchedParamIndex++;
/*      */             break;
/*      */           
/*      */           default:
/* 2883 */             throw new IllegalArgumentException("Unknown type when re-binding parameter into batched statement for parameter index " + batchedParamIndex);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       } 
/*      */     } 
/* 2891 */     return batchedParamIndex;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ServerPreparedStatement.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */