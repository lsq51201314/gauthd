/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ResultSetImpl
/*      */   implements ResultSetInternalMethods
/*      */ {
/*      */   private static final Constructor JDBC_4_RS_4_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_RS_6_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_UPD_RS_6_ARG_CTOR;
/*      */   
/*      */   static {
/*  123 */     if (Util.isJdbc4()) {
/*      */       try {
/*  125 */         JDBC_4_RS_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { long.class, long.class, ConnectionImpl.class, StatementImpl.class });
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  130 */         JDBC_4_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { String.class, (array$Lcom$mysql$jdbc$Field == null) ? (array$Lcom$mysql$jdbc$Field = class$("[Lcom.mysql.jdbc.Field;")) : array$Lcom$mysql$jdbc$Field, RowData.class, ConnectionImpl.class, StatementImpl.class });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  136 */         JDBC_4_UPD_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4UpdatableResultSet").getConstructor(new Class[] { String.class, (array$Lcom$mysql$jdbc$Field == null) ? (array$Lcom$mysql$jdbc$Field = class$("[Lcom.mysql.jdbc.Field;")) : array$Lcom$mysql$jdbc$Field, RowData.class, ConnectionImpl.class, StatementImpl.class });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  143 */       catch (SecurityException e) {
/*  144 */         throw new RuntimeException(e);
/*  145 */       } catch (NoSuchMethodException e) {
/*  146 */         throw new RuntimeException(e);
/*  147 */       } catch (ClassNotFoundException e) {
/*  148 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*  151 */       JDBC_4_RS_4_ARG_CTOR = null;
/*  152 */       JDBC_4_RS_6_ARG_CTOR = null;
/*  153 */       JDBC_4_UPD_RS_6_ARG_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  160 */   protected static final double MIN_DIFF_PREC = Float.parseFloat(Float.toString(Float.MIN_VALUE)) - Double.parseDouble(Float.toString(Float.MIN_VALUE));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  166 */   protected static final double MAX_DIFF_PREC = Float.parseFloat(Float.toString(Float.MAX_VALUE)) - Double.parseDouble(Float.toString(Float.MAX_VALUE));
/*      */ 
/*      */ 
/*      */   
/*  170 */   protected static int resultCounter = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static BigInteger convertLongToUlong(long longVal) {
/*  177 */     byte[] asBytes = new byte[8];
/*  178 */     asBytes[7] = (byte)(int)(longVal & 0xFFL);
/*  179 */     asBytes[6] = (byte)(int)(longVal >>> 8L);
/*  180 */     asBytes[5] = (byte)(int)(longVal >>> 16L);
/*  181 */     asBytes[4] = (byte)(int)(longVal >>> 24L);
/*  182 */     asBytes[3] = (byte)(int)(longVal >>> 32L);
/*  183 */     asBytes[2] = (byte)(int)(longVal >>> 40L);
/*  184 */     asBytes[1] = (byte)(int)(longVal >>> 48L);
/*  185 */     asBytes[0] = (byte)(int)(longVal >>> 56L);
/*      */     
/*  187 */     return new BigInteger(1, asBytes);
/*      */   }
/*      */ 
/*      */   
/*  191 */   protected String catalog = null;
/*      */ 
/*      */   
/*  194 */   protected Map columnNameToIndex = null;
/*      */ 
/*      */   
/*  197 */   protected boolean[] columnUsed = null;
/*      */ 
/*      */   
/*      */   protected ConnectionImpl connection;
/*      */ 
/*      */   
/*  203 */   protected long connectionId = 0L;
/*      */ 
/*      */   
/*  206 */   protected int currentRow = -1;
/*      */ 
/*      */   
/*      */   TimeZone defaultTimeZone;
/*      */   
/*      */   protected boolean doingUpdates = false;
/*      */   
/*  213 */   protected ProfilerEventHandler eventSink = null;
/*      */   
/*  215 */   Calendar fastDateCal = null;
/*      */ 
/*      */   
/*  218 */   protected int fetchDirection = 1000;
/*      */ 
/*      */   
/*  221 */   protected int fetchSize = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Field[] fields;
/*      */ 
/*      */ 
/*      */   
/*      */   protected char firstCharOfQuery;
/*      */ 
/*      */ 
/*      */   
/*  234 */   protected Map fullColumnNameToIndex = null;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean hasBuiltIndexMapping = false;
/*      */ 
/*      */   
/*      */   protected boolean isBinaryEncoded = false;
/*      */ 
/*      */   
/*      */   protected boolean isClosed = false;
/*      */ 
/*      */   
/*  247 */   protected ResultSetInternalMethods nextResultSet = null;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean onInsertRow = false;
/*      */ 
/*      */ 
/*      */   
/*      */   protected StatementImpl owningStatement;
/*      */ 
/*      */ 
/*      */   
/*      */   protected Throwable pointOfOrigin;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean profileSql = false;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean reallyResult = false;
/*      */ 
/*      */   
/*      */   protected int resultId;
/*      */ 
/*      */   
/*  273 */   protected int resultSetConcurrency = 0;
/*      */ 
/*      */   
/*  276 */   protected int resultSetType = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected RowData rowData;
/*      */ 
/*      */ 
/*      */   
/*  285 */   protected String serverInfo = null;
/*      */ 
/*      */   
/*      */   PreparedStatement statementUsedForFetchingRows;
/*      */   
/*  290 */   protected ResultSetRow thisRow = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long updateCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  304 */   protected long updateId = -1L;
/*      */ 
/*      */   
/*      */   private boolean useStrictFloatingPoint = false;
/*      */   
/*      */   protected boolean useUsageAdvisor = false;
/*      */   
/*  311 */   protected SQLWarning warningChain = null;
/*      */ 
/*      */   
/*      */   protected boolean wasNullFlag = false;
/*      */   
/*      */   protected Statement wrapperStatement;
/*      */   
/*      */   protected boolean retainOwningStatement;
/*      */   
/*  320 */   protected Calendar gmtCalendar = null;
/*      */   
/*      */   protected boolean useFastDateParsing = false;
/*      */   
/*      */   private boolean padCharsWithSpace = false;
/*      */   
/*      */   private boolean jdbcCompliantTruncationForReads;
/*      */   
/*      */   private boolean useFastIntParsing = true;
/*      */   
/*  330 */   protected static final char[] EMPTY_SPACE = new char[255]; private boolean onValidRow; private String invalidRowReason; protected boolean useLegacyDatetimeCode; private TimeZone serverTimeZoneTz; static Class array$Lcom$mysql$jdbc$Field;
/*      */   
/*      */   static {
/*  333 */     for (int i = 0; i < EMPTY_SPACE.length; i++) {
/*  334 */       EMPTY_SPACE[i] = ' ';
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected static ResultSetImpl getInstance(long updateCount, long updateID, ConnectionImpl conn, StatementImpl creatorStmt) throws SQLException {
/*  340 */     if (!Util.isJdbc4()) {
/*  341 */       return new ResultSetImpl(updateCount, updateID, conn, creatorStmt);
/*      */     }
/*      */     
/*  344 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, new Object[] { Constants.longValueOf(updateCount), Constants.longValueOf(updateID), conn, creatorStmt });
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
/*      */   protected static ResultSetImpl getInstance(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt, boolean isUpdatable) throws SQLException {
/*  360 */     if (!Util.isJdbc4()) {
/*  361 */       if (!isUpdatable) {
/*  362 */         return new ResultSetImpl(catalog, fields, tuples, conn, creatorStmt);
/*      */       }
/*      */       
/*  365 */       return new UpdatableResultSet(catalog, fields, tuples, conn, creatorStmt);
/*      */     } 
/*      */ 
/*      */     
/*  369 */     if (!isUpdatable) {
/*  370 */       return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt });
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  375 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_UPD_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initializeWithMetadata() throws SQLException {
/*  487 */     this.rowData.setMetadata(this.fields);
/*      */     
/*  489 */     if (this.profileSql || this.connection.getUseUsageAdvisor()) {
/*  490 */       this.columnUsed = new boolean[this.fields.length];
/*  491 */       this.pointOfOrigin = new Throwable();
/*  492 */       this.resultId = resultCounter++;
/*  493 */       this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  494 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */     } 
/*      */     
/*  497 */     if (this.connection.getGatherPerformanceMetrics()) {
/*  498 */       this.connection.incrementNumberOfResultSetsCreated();
/*      */       
/*  500 */       Map tableNamesMap = new HashMap();
/*      */       
/*  502 */       for (int i = 0; i < this.fields.length; i++) {
/*  503 */         Field f = this.fields[i];
/*      */         
/*  505 */         String tableName = f.getOriginalTableName();
/*      */         
/*  507 */         if (tableName == null) {
/*  508 */           tableName = f.getTableName();
/*      */         }
/*      */         
/*  511 */         if (tableName != null) {
/*  512 */           if (this.connection.lowerCaseTableNames()) {
/*  513 */             tableName = tableName.toLowerCase();
/*      */           }
/*      */ 
/*      */           
/*  517 */           tableNamesMap.put(tableName, null);
/*      */         } 
/*      */       } 
/*      */       
/*  521 */       this.connection.reportNumberOfTablesAccessed(tableNamesMap.size());
/*      */     } 
/*      */   }
/*      */   
/*      */   private synchronized void createCalendarIfNeeded() {
/*  526 */     if (this.fastDateCal == null) {
/*  527 */       this.fastDateCal = new GregorianCalendar(Locale.US);
/*  528 */       this.fastDateCal.setTimeZone(getDefaultTimeZone());
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
/*      */ 
/*      */   
/*      */   public boolean absolute(int row) throws SQLException {
/*      */     boolean b;
/*  571 */     checkClosed();
/*      */ 
/*      */ 
/*      */     
/*  575 */     if (this.rowData.size() == 0) {
/*  576 */       b = false;
/*      */     } else {
/*  578 */       if (row == 0) {
/*  579 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Cannot_absolute_position_to_row_0_110"), "S1009");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  585 */       if (this.onInsertRow) {
/*  586 */         this.onInsertRow = false;
/*      */       }
/*      */       
/*  589 */       if (this.doingUpdates) {
/*  590 */         this.doingUpdates = false;
/*      */       }
/*      */       
/*  593 */       if (this.thisRow != null) {
/*  594 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */       
/*  597 */       if (row == 1) {
/*  598 */         b = first();
/*  599 */       } else if (row == -1) {
/*  600 */         b = last();
/*  601 */       } else if (row > this.rowData.size()) {
/*  602 */         afterLast();
/*  603 */         b = false;
/*      */       }
/*  605 */       else if (row < 0) {
/*      */         
/*  607 */         int newRowPosition = this.rowData.size() + row + 1;
/*      */         
/*  609 */         if (newRowPosition <= 0) {
/*  610 */           beforeFirst();
/*  611 */           b = false;
/*      */         } else {
/*  613 */           b = absolute(newRowPosition);
/*      */         } 
/*      */       } else {
/*  616 */         row--;
/*  617 */         this.rowData.setCurrentRow(row);
/*  618 */         this.thisRow = this.rowData.getAt(row);
/*  619 */         b = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  624 */     setRowPositionValidity();
/*      */     
/*  626 */     return b;
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
/*      */   public void afterLast() throws SQLException {
/*  642 */     checkClosed();
/*      */     
/*  644 */     if (this.onInsertRow) {
/*  645 */       this.onInsertRow = false;
/*      */     }
/*      */     
/*  648 */     if (this.doingUpdates) {
/*  649 */       this.doingUpdates = false;
/*      */     }
/*      */     
/*  652 */     if (this.thisRow != null) {
/*  653 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */     
/*  656 */     if (this.rowData.size() != 0) {
/*  657 */       this.rowData.afterLast();
/*  658 */       this.thisRow = null;
/*      */     } 
/*      */     
/*  661 */     setRowPositionValidity();
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
/*      */   public void beforeFirst() throws SQLException {
/*  677 */     checkClosed();
/*      */     
/*  679 */     if (this.onInsertRow) {
/*  680 */       this.onInsertRow = false;
/*      */     }
/*      */     
/*  683 */     if (this.doingUpdates) {
/*  684 */       this.doingUpdates = false;
/*      */     }
/*      */     
/*  687 */     if (this.rowData.size() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*  691 */     if (this.thisRow != null) {
/*  692 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */     
/*  695 */     this.rowData.beforeFirst();
/*  696 */     this.thisRow = null;
/*      */     
/*  698 */     setRowPositionValidity();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void buildIndexMapping() throws SQLException {
/*  709 */     int numFields = this.fields.length;
/*  710 */     this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  711 */     this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  726 */     for (int i = numFields - 1; i >= 0; i--) {
/*  727 */       Integer index = Constants.integerValueOf(i);
/*  728 */       String columnName = this.fields[i].getName();
/*  729 */       String fullColumnName = this.fields[i].getFullName();
/*      */       
/*  731 */       if (columnName != null) {
/*  732 */         this.columnNameToIndex.put(columnName, index);
/*      */       }
/*      */       
/*  735 */       if (fullColumnName != null) {
/*  736 */         this.fullColumnNameToIndex.put(fullColumnName, index);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  741 */     this.hasBuiltIndexMapping = true;
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
/*      */   public void cancelRowUpdates() throws SQLException {
/*  757 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void checkClosed() throws SQLException {
/*  767 */     if (this.isClosed) {
/*  768 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000");
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
/*      */   protected final void checkColumnBounds(int columnIndex) throws SQLException {
/*  785 */     if (columnIndex < 1) {
/*  786 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Constants.integerValueOf(columnIndex), Constants.integerValueOf(this.fields.length) }), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  791 */     if (columnIndex > this.fields.length) {
/*  792 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Constants.integerValueOf(columnIndex), Constants.integerValueOf(this.fields.length) }), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  799 */     if (this.profileSql || this.useUsageAdvisor) {
/*  800 */       this.columnUsed[columnIndex - 1] = true;
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
/*      */   protected void checkRowPos() throws SQLException {
/*  812 */     checkClosed();
/*      */     
/*  814 */     if (!this.onValidRow) {
/*  815 */       throw SQLError.createSQLException(this.invalidRowReason, "S1000");
/*      */     }
/*      */   }
/*      */   
/*      */   public ResultSetImpl(long updateCount, long updateID, ConnectionImpl conn, StatementImpl creatorStmt) {
/*  820 */     this.onValidRow = false;
/*  821 */     this.invalidRowReason = null; this.updateCount = updateCount; this.updateId = updateID; this.reallyResult = false; this.fields = new Field[0]; this.connection = conn; this.owningStatement = creatorStmt; this.retainOwningStatement = false; if (this.connection != null) { this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose(); this.connectionId = this.connection.getId(); this.serverTimeZoneTz = this.connection.getServerTimezoneTZ(); }  this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode(); } public ResultSetImpl(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt) throws SQLException { this.onValidRow = false; this.invalidRowReason = null; this.connection = conn; this.retainOwningStatement = false; if (this.connection != null) { this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint(); setDefaultTimeZone(this.connection.getDefaultTimeZone()); this.connectionId = this.connection.getId(); this.useFastDateParsing = this.connection.getUseFastDateParsing(); this.profileSql = this.connection.getProfileSql(); this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose(); this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads(); this.useFastIntParsing = this.connection.getUseFastIntParsing(); this.serverTimeZoneTz = this.connection.getServerTimezoneTZ(); }
/*      */      this.owningStatement = creatorStmt; this.catalog = catalog; this.fields = fields; this.rowData = tuples; this.updateCount = this.rowData.size(); this.reallyResult = true; if (this.rowData.size() > 0) { if (this.updateCount == 1L && this.thisRow == null) { this.rowData.close(); this.updateCount = -1L; }
/*      */        }
/*      */     else { this.thisRow = null; }
/*      */      this.rowData.setOwner(this); if (this.fields != null)
/*  826 */       initializeWithMetadata();  this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode(); } private void setRowPositionValidity() throws SQLException { if (!this.rowData.isDynamic() && this.rowData.size() == 0) {
/*  827 */       this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
/*      */       
/*  829 */       this.onValidRow = false;
/*  830 */     } else if (this.rowData.isBeforeFirst()) {
/*  831 */       this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
/*      */       
/*  833 */       this.onValidRow = false;
/*  834 */     } else if (this.rowData.isAfterLast()) {
/*  835 */       this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
/*      */       
/*  837 */       this.onValidRow = false;
/*      */     } else {
/*  839 */       this.onValidRow = true;
/*  840 */       this.invalidRowReason = null;
/*      */     }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearNextResult() {
/*  849 */     this.nextResultSet = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*  860 */     this.warningChain = null;
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
/*      */   public void close() throws SQLException {
/*  881 */     realClose(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int convertToZeroWithEmptyCheck() throws SQLException {
/*  888 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  889 */       return 0;
/*      */     }
/*      */     
/*  892 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String convertToZeroLiteralStringWithEmptyCheck() throws SQLException {
/*  899 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  900 */       return "0";
/*      */     }
/*      */     
/*  903 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSetInternalMethods copy() throws SQLException {
/*  911 */     ResultSetInternalMethods rs = getInstance(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement, false);
/*      */ 
/*      */     
/*  914 */     return rs;
/*      */   }
/*      */   
/*      */   public void redefineFieldsForDBMD(Field[] f) {
/*  918 */     this.fields = f;
/*      */     
/*  920 */     for (int i = 0; i < this.fields.length; i++) {
/*  921 */       this.fields[i].setUseOldNameMetadata(true);
/*  922 */       this.fields[i].setConnection(this.connection);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
/*  928 */     cachedMetaData.fields = this.fields;
/*  929 */     cachedMetaData.columnNameToIndex = this.columnNameToIndex;
/*  930 */     cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
/*  931 */     cachedMetaData.metadata = getMetaData();
/*      */   }
/*      */   
/*      */   public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
/*  935 */     this.fields = cachedMetaData.fields;
/*  936 */     this.columnNameToIndex = cachedMetaData.columnNameToIndex;
/*  937 */     this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
/*  938 */     this.hasBuiltIndexMapping = true;
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
/*      */   public void deleteRow() throws SQLException {
/*  953 */     throw new NotUpdatable();
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
/*      */   private String extractStringFromNativeColumn(int columnIndex, int mysqlType) throws SQLException {
/*  965 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/*  967 */     this.wasNullFlag = false;
/*      */     
/*  969 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/*  970 */       this.wasNullFlag = true;
/*      */       
/*  972 */       return null;
/*      */     } 
/*      */     
/*  975 */     this.wasNullFlag = false;
/*      */     
/*  977 */     String encoding = this.fields[columnIndexMinusOne].getCharacterSet();
/*      */ 
/*      */     
/*  980 */     return this.thisRow.getString(columnIndex - 1, encoding, this.connection);
/*      */   }
/*      */ 
/*      */   
/*      */   protected synchronized Date fastDateCreate(Calendar cal, int year, int month, int day) {
/*  985 */     if (this.useLegacyDatetimeCode) {
/*  986 */       return TimeUtil.fastDateCreate(year, month, day, cal);
/*      */     }
/*      */     
/*  989 */     if (cal == null) {
/*  990 */       createCalendarIfNeeded();
/*  991 */       cal = this.fastDateCal;
/*      */     } 
/*      */     
/*  994 */     boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */     
/*  996 */     return TimeUtil.fastDateCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : cal, cal, year, month, day);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized Time fastTimeCreate(Calendar cal, int hour, int minute, int second) throws SQLException {
/* 1003 */     if (!this.useLegacyDatetimeCode) {
/* 1004 */       return TimeUtil.fastTimeCreate(hour, minute, second, cal);
/*      */     }
/*      */     
/* 1007 */     if (cal == null) {
/* 1008 */       createCalendarIfNeeded();
/* 1009 */       cal = this.fastDateCal;
/*      */     } 
/*      */     
/* 1012 */     return TimeUtil.fastTimeCreate(cal, hour, minute, second);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
/* 1018 */     if (!this.useLegacyDatetimeCode) {
/* 1019 */       return TimeUtil.fastTimestampCreate(cal.getTimeZone(), year, month, day, hour, minute, seconds, secondsPart);
/*      */     }
/*      */ 
/*      */     
/* 1023 */     if (cal == null) {
/* 1024 */       createCalendarIfNeeded();
/* 1025 */       cal = this.fastDateCal;
/*      */     } 
/*      */     
/* 1028 */     boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */     
/* 1030 */     return TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : null, cal, year, month, day, hour, minute, seconds, secondsPart);
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
/*      */   public synchronized int findColumn(String columnName) throws SQLException {
/* 1068 */     if (!this.hasBuiltIndexMapping) {
/* 1069 */       buildIndexMapping();
/*      */     }
/*      */     
/* 1072 */     Integer index = (Integer)this.columnNameToIndex.get(columnName);
/*      */     
/* 1074 */     if (index == null) {
/* 1075 */       index = (Integer)this.fullColumnNameToIndex.get(columnName);
/*      */     }
/*      */     
/* 1078 */     if (index != null) {
/* 1079 */       return index.intValue() + 1;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1084 */     for (int i = 0; i < this.fields.length; i++) {
/* 1085 */       if (this.fields[i].getName().equalsIgnoreCase(columnName))
/* 1086 */         return i + 1; 
/* 1087 */       if (this.fields[i].getFullName().equalsIgnoreCase(columnName))
/*      */       {
/* 1089 */         return i + 1;
/*      */       }
/*      */     } 
/*      */     
/* 1093 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022");
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
/*      */   public boolean first() throws SQLException {
/* 1113 */     checkClosed();
/*      */     
/* 1115 */     boolean b = true;
/*      */     
/* 1117 */     if (this.rowData.isEmpty()) {
/* 1118 */       b = false;
/*      */     } else {
/*      */       
/* 1121 */       if (this.onInsertRow) {
/* 1122 */         this.onInsertRow = false;
/*      */       }
/*      */       
/* 1125 */       if (this.doingUpdates) {
/* 1126 */         this.doingUpdates = false;
/*      */       }
/*      */       
/* 1129 */       this.rowData.beforeFirst();
/* 1130 */       this.thisRow = this.rowData.next();
/*      */     } 
/*      */     
/* 1133 */     setRowPositionValidity();
/*      */     
/* 1135 */     return b;
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
/*      */   public Array getArray(int i) throws SQLException {
/* 1152 */     checkColumnBounds(i);
/*      */     
/* 1154 */     throw SQLError.notImplemented();
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
/*      */   public Array getArray(String colName) throws SQLException {
/* 1171 */     return getArray(findColumn(colName));
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
/*      */   public InputStream getAsciiStream(int columnIndex) throws SQLException {
/* 1200 */     checkRowPos();
/*      */     
/* 1202 */     if (!this.isBinaryEncoded) {
/* 1203 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */     
/* 1206 */     return getNativeBinaryStream(columnIndex);
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
/*      */   public InputStream getAsciiStream(String columnName) throws SQLException {
/* 1221 */     return getAsciiStream(findColumn(columnName));
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
/*      */   public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
/* 1238 */     if (!this.isBinaryEncoded) {
/* 1239 */       String stringVal = getString(columnIndex);
/*      */ 
/*      */       
/* 1242 */       if (stringVal != null) {
/* 1243 */         if (stringVal.length() == 0) {
/*      */           
/* 1245 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */ 
/*      */           
/* 1248 */           return val;
/*      */         } 
/*      */         
/*      */         try {
/* 1252 */           BigDecimal val = new BigDecimal(stringVal);
/*      */           
/* 1254 */           return val;
/* 1255 */         } catch (NumberFormatException ex) {
/* 1256 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1264 */       return null;
/*      */     } 
/*      */     
/* 1267 */     return getNativeBigDecimal(columnIndex);
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
/*      */   public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
/* 1288 */     if (!this.isBinaryEncoded) {
/* 1289 */       String stringVal = getString(columnIndex);
/*      */ 
/*      */       
/* 1292 */       if (stringVal != null) {
/* 1293 */         BigDecimal val; if (stringVal.length() == 0) {
/* 1294 */           val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */ 
/*      */           
/*      */           try {
/* 1298 */             return val.setScale(scale);
/* 1299 */           } catch (ArithmeticException ex) {
/*      */             try {
/* 1301 */               return val.setScale(scale, 4);
/*      */             }
/* 1303 */             catch (ArithmeticException arEx) {
/* 1304 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009");
/*      */             } 
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1314 */           val = new BigDecimal(stringVal);
/* 1315 */         } catch (NumberFormatException ex) {
/* 1316 */           if (this.fields[columnIndex - 1].getMysqlType() == 16) {
/* 1317 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */             
/* 1319 */             val = new BigDecimal(valueAsLong);
/*      */           } else {
/* 1321 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Constants.integerValueOf(columnIndex), stringVal }), "S1009");
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1330 */           return val.setScale(scale);
/* 1331 */         } catch (ArithmeticException ex) {
/*      */           try {
/* 1333 */             return val.setScale(scale, 4);
/* 1334 */           } catch (ArithmeticException arithEx) {
/* 1335 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Constants.integerValueOf(columnIndex), stringVal }), "S1009");
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1344 */       return null;
/*      */     } 
/*      */     
/* 1347 */     return getNativeBigDecimal(columnIndex, scale);
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
/*      */   public BigDecimal getBigDecimal(String columnName) throws SQLException {
/* 1363 */     return getBigDecimal(findColumn(columnName));
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
/*      */   public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
/* 1383 */     return getBigDecimal(findColumn(columnName), scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale) throws SQLException {
/* 1390 */     if (stringVal != null) {
/* 1391 */       if (stringVal.length() == 0) {
/* 1392 */         BigDecimal bdVal = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */         
/*      */         try {
/* 1395 */           return bdVal.setScale(scale);
/* 1396 */         } catch (ArithmeticException ex) {
/*      */           try {
/* 1398 */             return bdVal.setScale(scale, 4);
/* 1399 */           } catch (ArithmeticException arEx) {
/* 1400 */             throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1411 */         return (new BigDecimal(stringVal)).setScale(scale);
/* 1412 */       } catch (ArithmeticException ex) {
/*      */         try {
/* 1414 */           return (new BigDecimal(stringVal)).setScale(scale, 4);
/*      */         }
/* 1416 */         catch (ArithmeticException arEx) {
/* 1417 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/* 1424 */       catch (NumberFormatException ex) {
/* 1425 */         if (this.fields[columnIndex - 1].getMysqlType() == 16) {
/* 1426 */           long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */           
/*      */           try {
/* 1429 */             return (new BigDecimal(valueAsLong)).setScale(scale);
/* 1430 */           } catch (ArithmeticException arEx1) {
/*      */             try {
/* 1432 */               return (new BigDecimal(valueAsLong)).setScale(scale, 4);
/*      */             }
/* 1434 */             catch (ArithmeticException arEx2) {
/* 1435 */               throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */             } 
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1444 */         if (this.fields[columnIndex - 1].getMysqlType() == 1 && this.connection.getTinyInt1isBit() && this.fields[columnIndex - 1].getLength() == 1L)
/*      */         {
/* 1446 */           return (new BigDecimal(stringVal.equalsIgnoreCase("true") ? 1.0D : 0.0D)).setScale(scale);
/*      */         }
/*      */         
/* 1449 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1457 */     return null;
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
/*      */   public InputStream getBinaryStream(int columnIndex) throws SQLException {
/* 1478 */     checkRowPos();
/*      */     
/* 1480 */     if (!this.isBinaryEncoded) {
/* 1481 */       checkColumnBounds(columnIndex);
/*      */       
/* 1483 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1485 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1486 */         this.wasNullFlag = true;
/*      */         
/* 1488 */         return null;
/*      */       } 
/*      */       
/* 1491 */       this.wasNullFlag = false;
/*      */       
/* 1493 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     } 
/*      */     
/* 1496 */     return getNativeBinaryStream(columnIndex);
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
/*      */   public InputStream getBinaryStream(String columnName) throws SQLException {
/* 1511 */     return getBinaryStream(findColumn(columnName));
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
/*      */   public Blob getBlob(int columnIndex) throws SQLException {
/* 1526 */     if (!this.isBinaryEncoded) {
/* 1527 */       checkRowPos();
/*      */       
/* 1529 */       checkColumnBounds(columnIndex);
/*      */       
/* 1531 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1533 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1534 */         this.wasNullFlag = true;
/*      */       } else {
/* 1536 */         this.wasNullFlag = false;
/*      */       } 
/*      */       
/* 1539 */       if (this.wasNullFlag) {
/* 1540 */         return null;
/*      */       }
/*      */       
/* 1543 */       if (!this.connection.getEmulateLocators()) {
/* 1544 */         return new Blob(this.thisRow.getColumnValue(columnIndexMinusOne));
/*      */       }
/*      */       
/* 1547 */       return new BlobFromLocator(this, columnIndex);
/*      */     } 
/*      */     
/* 1550 */     return getNativeBlob(columnIndex);
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
/*      */   public Blob getBlob(String colName) throws SQLException {
/* 1565 */     return getBlob(findColumn(colName));
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
/*      */   public boolean getBoolean(int columnIndex) throws SQLException {
/*      */     long boolVal;
/* 1581 */     checkColumnBounds(columnIndex);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1588 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 1590 */     Field field = this.fields[columnIndexMinusOne];
/*      */     
/* 1592 */     if (field.getMysqlType() == 16) {
/* 1593 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1596 */     this.wasNullFlag = false;
/*      */     
/* 1598 */     int sqlType = field.getSQLType();
/*      */     
/* 1600 */     switch (sqlType) {
/*      */       case -7:
/*      */       case -6:
/*      */       case -5:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 16:
/* 1612 */         boolVal = getLong(columnIndex, false);
/*      */         
/* 1614 */         return (boolVal == -1L || boolVal > 0L);
/*      */     } 
/* 1616 */     if (this.connection.getPedantic())
/*      */     {
/* 1618 */       switch (sqlType) {
/*      */         case -4:
/*      */         case -3:
/*      */         case -2:
/*      */         case 70:
/*      */         case 91:
/*      */         case 92:
/*      */         case 93:
/*      */         case 2000:
/*      */         case 2002:
/*      */         case 2003:
/*      */         case 2004:
/*      */         case 2005:
/*      */         case 2006:
/* 1632 */           throw SQLError.createSQLException("Required type conversion not allowed", "22018");
/*      */       } 
/*      */ 
/*      */     
/*      */     }
/* 1637 */     if (sqlType == -2 || sqlType == -3 || sqlType == -4 || sqlType == 2004)
/*      */     {
/*      */ 
/*      */       
/* 1641 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1644 */     if (this.useUsageAdvisor) {
/* 1645 */       issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow.getColumnValue(columnIndexMinusOne), this.fields[columnIndex], new int[] { 16, 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1657 */     String stringVal = getString(columnIndex);
/*      */     
/* 1659 */     return getBooleanFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException {
/* 1664 */     Object value = this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */     
/* 1666 */     if (value == null) {
/* 1667 */       this.wasNullFlag = true;
/*      */       
/* 1669 */       return false;
/*      */     } 
/*      */     
/* 1672 */     this.wasNullFlag = false;
/*      */     
/* 1674 */     if (((byte[])value).length == 0) {
/* 1675 */       return false;
/*      */     }
/*      */     
/* 1678 */     byte boolVal = ((byte[])value)[0];
/*      */     
/* 1680 */     if (boolVal == 49)
/* 1681 */       return true; 
/* 1682 */     if (boolVal == 48) {
/* 1683 */       return false;
/*      */     }
/*      */     
/* 1686 */     return (boolVal == -1 || boolVal > 0);
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
/*      */   public boolean getBoolean(String columnName) throws SQLException {
/* 1701 */     return getBoolean(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final boolean getBooleanFromString(String stringVal, int columnIndex) throws SQLException {
/* 1706 */     if (stringVal != null && stringVal.length() > 0) {
/* 1707 */       int c = Character.toLowerCase(stringVal.charAt(0));
/*      */       
/* 1709 */       return (c == 116 || c == 121 || c == 49 || stringVal.equals("-1"));
/*      */     } 
/*      */ 
/*      */     
/* 1713 */     return false;
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
/*      */   public byte getByte(int columnIndex) throws SQLException {
/* 1728 */     if (!this.isBinaryEncoded) {
/* 1729 */       String stringVal = getString(columnIndex);
/*      */       
/* 1731 */       if (this.wasNullFlag || stringVal == null) {
/* 1732 */         return 0;
/*      */       }
/*      */       
/* 1735 */       return getByteFromString(stringVal, columnIndex);
/*      */     } 
/*      */     
/* 1738 */     return getNativeByte(columnIndex);
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
/*      */   public byte getByte(String columnName) throws SQLException {
/* 1753 */     return getByte(findColumn(columnName));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final byte getByteFromString(String stringVal, int columnIndex) throws SQLException {
/* 1759 */     if (stringVal != null && stringVal.length() == 0) {
/* 1760 */       return (byte)convertToZeroWithEmptyCheck();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1771 */     if (stringVal == null) {
/* 1772 */       return 0;
/*      */     }
/*      */     
/* 1775 */     stringVal = stringVal.trim();
/*      */     
/*      */     try {
/* 1778 */       int decimalIndex = stringVal.indexOf(".");
/*      */ 
/*      */       
/* 1781 */       if (decimalIndex != -1) {
/* 1782 */         double valueAsDouble = Double.parseDouble(stringVal);
/*      */         
/* 1784 */         if (this.jdbcCompliantTruncationForReads && (
/* 1785 */           valueAsDouble < -128.0D || valueAsDouble > 127.0D))
/*      */         {
/* 1787 */           throwRangeException(stringVal, columnIndex, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1792 */         return (byte)(int)valueAsDouble;
/*      */       } 
/*      */       
/* 1795 */       long valueAsLong = Long.parseLong(stringVal);
/*      */       
/* 1797 */       if (this.jdbcCompliantTruncationForReads && (
/* 1798 */         valueAsLong < -128L || valueAsLong > 127L))
/*      */       {
/* 1800 */         throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1805 */       return (byte)(int)valueAsLong;
/* 1806 */     } catch (NumberFormatException NFE) {
/* 1807 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Value____173") + stringVal + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), "S1009");
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
/*      */   public byte[] getBytes(int columnIndex) throws SQLException {
/* 1832 */     return getBytes(columnIndex, false);
/*      */   }
/*      */ 
/*      */   
/*      */   protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException {
/* 1837 */     if (!this.isBinaryEncoded) {
/* 1838 */       checkRowPos();
/*      */       
/* 1840 */       checkColumnBounds(columnIndex);
/*      */       
/* 1842 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1844 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1845 */         this.wasNullFlag = true;
/*      */       } else {
/* 1847 */         this.wasNullFlag = false;
/*      */       } 
/*      */       
/* 1850 */       if (this.wasNullFlag) {
/* 1851 */         return null;
/*      */       }
/*      */       
/* 1854 */       return this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */     } 
/*      */     
/* 1857 */     return getNativeBytes(columnIndex, noConversion);
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
/*      */   public byte[] getBytes(String columnName) throws SQLException {
/* 1872 */     return getBytes(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final byte[] getBytesFromString(String stringVal, int columnIndex) throws SQLException {
/* 1877 */     if (stringVal != null) {
/* 1878 */       return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1885 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Calendar getCalendarInstanceForSessionOrNew() {
/* 1893 */     if (this.connection != null) {
/* 1894 */       return this.connection.getCalendarInstanceForSessionOrNew();
/*      */     }
/*      */     
/* 1897 */     return new GregorianCalendar();
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
/*      */   public Reader getCharacterStream(int columnIndex) throws SQLException {
/* 1918 */     if (!this.isBinaryEncoded) {
/* 1919 */       checkColumnBounds(columnIndex);
/*      */       
/* 1921 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1923 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1924 */         this.wasNullFlag = true;
/*      */         
/* 1926 */         return null;
/*      */       } 
/*      */       
/* 1929 */       this.wasNullFlag = false;
/*      */       
/* 1931 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     } 
/*      */     
/* 1934 */     return getNativeCharacterStream(columnIndex);
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
/*      */   public Reader getCharacterStream(String columnName) throws SQLException {
/* 1954 */     return getCharacterStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final Reader getCharacterStreamFromString(String stringVal, int columnIndex) throws SQLException {
/* 1959 */     if (stringVal != null) {
/* 1960 */       return new StringReader(stringVal);
/*      */     }
/*      */     
/* 1963 */     return null;
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
/*      */   public Clob getClob(int i) throws SQLException {
/* 1978 */     if (!this.isBinaryEncoded) {
/* 1979 */       String asString = getStringForClob(i);
/*      */       
/* 1981 */       if (asString == null) {
/* 1982 */         return null;
/*      */       }
/*      */       
/* 1985 */       return new Clob(asString);
/*      */     } 
/*      */     
/* 1988 */     return getNativeClob(i);
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
/*      */   public Clob getClob(String colName) throws SQLException {
/* 2003 */     return getClob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final Clob getClobFromString(String stringVal, int columnIndex) throws SQLException {
/* 2008 */     return new Clob(stringVal);
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
/*      */   public int getConcurrency() throws SQLException {
/* 2021 */     return 1007;
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
/*      */   public String getCursorName() throws SQLException {
/* 2050 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00");
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
/*      */   public Date getDate(int columnIndex) throws SQLException {
/* 2067 */     return getDate(columnIndex, (Calendar)null);
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
/*      */   public Date getDate(int columnIndex, Calendar cal) throws SQLException {
/* 2088 */     if (this.isBinaryEncoded) {
/* 2089 */       return getNativeDate(columnIndex, (cal != null) ? cal.getTimeZone() : getDefaultTimeZone());
/*      */     }
/*      */ 
/*      */     
/* 2093 */     if (!this.useFastDateParsing) {
/* 2094 */       String stringVal = getStringInternal(columnIndex, false);
/*      */       
/* 2096 */       if (stringVal == null) {
/* 2097 */         return null;
/*      */       }
/*      */       
/* 2100 */       return getDateFromString(stringVal, columnIndex, cal);
/*      */     } 
/*      */     
/* 2103 */     checkColumnBounds(columnIndex);
/*      */     
/* 2105 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 2107 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2108 */       this.wasNullFlag = true;
/*      */       
/* 2110 */       return null;
/*      */     } 
/*      */     
/* 2113 */     this.wasNullFlag = false;
/*      */     
/* 2115 */     return this.thisRow.getDateFast(columnIndexMinusOne, this.connection, this, cal);
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
/*      */   public Date getDate(String columnName) throws SQLException {
/* 2131 */     return getDate(findColumn(columnName));
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
/*      */   public Date getDate(String columnName, Calendar cal) throws SQLException {
/* 2151 */     return getDate(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */   
/*      */   private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException {
/* 2156 */     int year = 0;
/* 2157 */     int month = 0;
/* 2158 */     int day = 0;
/*      */     
/*      */     try {
/* 2161 */       this.wasNullFlag = false;
/*      */       
/* 2163 */       if (stringVal == null) {
/* 2164 */         this.wasNullFlag = true;
/*      */         
/* 2166 */         return null;
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
/* 2177 */       stringVal = stringVal.trim();
/*      */       
/* 2179 */       if (stringVal.equals("0") || stringVal.equals("0000-00-00") || stringVal.equals("0000-00-00 00:00:00") || stringVal.equals("00000000000000") || stringVal.equals("0")) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2184 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/*      */           
/* 2186 */           this.wasNullFlag = true;
/*      */           
/* 2188 */           return null;
/* 2189 */         }  if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 2191 */           throw SQLError.createSQLException("Value '" + stringVal + "' can not be represented as java.sql.Date", "S1009");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2198 */         return fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       } 
/* 2200 */       if (this.fields[columnIndex - 1].getMysqlType() == 7) {
/*      */         
/* 2202 */         switch (stringVal.length()) {
/*      */           case 19:
/*      */           case 21:
/* 2205 */             year = Integer.parseInt(stringVal.substring(0, 4));
/* 2206 */             month = Integer.parseInt(stringVal.substring(5, 7));
/* 2207 */             day = Integer.parseInt(stringVal.substring(8, 10));
/*      */             
/* 2209 */             return fastDateCreate(targetCalendar, year, month, day);
/*      */ 
/*      */           
/*      */           case 8:
/*      */           case 14:
/* 2214 */             year = Integer.parseInt(stringVal.substring(0, 4));
/* 2215 */             month = Integer.parseInt(stringVal.substring(4, 6));
/* 2216 */             day = Integer.parseInt(stringVal.substring(6, 8));
/*      */             
/* 2218 */             return fastDateCreate(targetCalendar, year, month, day);
/*      */ 
/*      */           
/*      */           case 6:
/*      */           case 10:
/*      */           case 12:
/* 2224 */             year = Integer.parseInt(stringVal.substring(0, 2));
/*      */             
/* 2226 */             if (year <= 69) {
/* 2227 */               year += 100;
/*      */             }
/*      */             
/* 2230 */             month = Integer.parseInt(stringVal.substring(2, 4));
/* 2231 */             day = Integer.parseInt(stringVal.substring(4, 6));
/*      */             
/* 2233 */             return fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */ 
/*      */           
/*      */           case 4:
/* 2237 */             year = Integer.parseInt(stringVal.substring(0, 4));
/*      */             
/* 2239 */             if (year <= 69) {
/* 2240 */               year += 100;
/*      */             }
/*      */             
/* 2243 */             month = Integer.parseInt(stringVal.substring(2, 4));
/*      */             
/* 2245 */             return fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */ 
/*      */           
/*      */           case 2:
/* 2249 */             year = Integer.parseInt(stringVal.substring(0, 2));
/*      */             
/* 2251 */             if (year <= 69) {
/* 2252 */               year += 100;
/*      */             }
/*      */             
/* 2255 */             return fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         } 
/*      */ 
/*      */         
/* 2259 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2264 */       if (this.fields[columnIndex - 1].getMysqlType() == 13) {
/*      */         
/* 2266 */         if (stringVal.length() == 2 || stringVal.length() == 1) {
/* 2267 */           year = Integer.parseInt(stringVal);
/*      */           
/* 2269 */           if (year <= 69) {
/* 2270 */             year += 100;
/*      */           }
/*      */           
/* 2273 */           year += 1900;
/*      */         } else {
/* 2275 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */         } 
/*      */         
/* 2278 */         return fastDateCreate(targetCalendar, year, 1, 1);
/* 2279 */       }  if (this.fields[columnIndex - 1].getMysqlType() == 11) {
/* 2280 */         return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/* 2282 */       if (stringVal.length() < 10) {
/* 2283 */         if (stringVal.length() == 8) {
/* 2284 */           return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */         
/* 2287 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2293 */       if (stringVal.length() != 18) {
/* 2294 */         year = Integer.parseInt(stringVal.substring(0, 4));
/* 2295 */         month = Integer.parseInt(stringVal.substring(5, 7));
/* 2296 */         day = Integer.parseInt(stringVal.substring(8, 10));
/*      */       } else {
/*      */         
/* 2299 */         StringTokenizer st = new StringTokenizer(stringVal, "- ");
/*      */         
/* 2301 */         year = Integer.parseInt(st.nextToken());
/* 2302 */         month = Integer.parseInt(st.nextToken());
/* 2303 */         day = Integer.parseInt(st.nextToken());
/*      */       } 
/*      */ 
/*      */       
/* 2307 */       return fastDateCreate(targetCalendar, year, month, day);
/* 2308 */     } catch (SQLException sqlEx) {
/* 2309 */       throw sqlEx;
/* 2310 */     } catch (Exception e) {
/* 2311 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2316 */       sqlEx.initCause(e);
/*      */       
/* 2318 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */   
/*      */   private TimeZone getDefaultTimeZone() {
/* 2323 */     if (!this.useLegacyDatetimeCode && this.connection != null) {
/* 2324 */       return this.serverTimeZoneTz;
/*      */     }
/*      */     
/* 2327 */     return this.connection.getDefaultTimeZone();
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
/*      */   public double getDouble(int columnIndex) throws SQLException {
/* 2342 */     if (!this.isBinaryEncoded) {
/* 2343 */       return getDoubleInternal(columnIndex);
/*      */     }
/*      */     
/* 2346 */     return getNativeDouble(columnIndex);
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
/*      */   public double getDouble(String columnName) throws SQLException {
/* 2361 */     return getDouble(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException {
/* 2366 */     return getDoubleInternal(stringVal, columnIndex);
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
/*      */   protected double getDoubleInternal(int colIndex) throws SQLException {
/* 2382 */     return getDoubleInternal(getString(colIndex), colIndex);
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
/*      */   protected double getDoubleInternal(String stringVal, int colIndex) throws SQLException {
/*      */     try {
/* 2402 */       if (stringVal == null) {
/* 2403 */         return 0.0D;
/*      */       }
/*      */       
/* 2406 */       if (stringVal.length() == 0) {
/* 2407 */         return convertToZeroWithEmptyCheck();
/*      */       }
/*      */       
/* 2410 */       double d = Double.parseDouble(stringVal);
/*      */       
/* 2412 */       if (this.useStrictFloatingPoint)
/*      */       {
/* 2414 */         if (d == 2.147483648E9D) {
/*      */           
/* 2416 */           d = 2.147483647E9D;
/* 2417 */         } else if (d == 1.0000000036275E-15D) {
/*      */           
/* 2419 */           d = 1.0E-15D;
/* 2420 */         } else if (d == 9.999999869911E14D) {
/* 2421 */           d = 9.99999999999999E14D;
/* 2422 */         } else if (d == 1.4012984643248E-45D) {
/* 2423 */           d = 1.4E-45D;
/* 2424 */         } else if (d == 1.4013E-45D) {
/* 2425 */           d = 1.4E-45D;
/* 2426 */         } else if (d == 3.4028234663853E37D) {
/* 2427 */           d = 3.4028235E37D;
/* 2428 */         } else if (d == -2.14748E9D) {
/* 2429 */           d = -2.147483648E9D;
/* 2430 */         } else if (d == 3.40282E37D) {
/* 2431 */           d = 3.4028235E37D;
/*      */         } 
/*      */       }
/*      */       
/* 2435 */       return d;
/* 2436 */     } catch (NumberFormatException e) {
/* 2437 */       if (this.fields[colIndex - 1].getMysqlType() == 16) {
/* 2438 */         long valueAsLong = getNumericRepresentationOfSQLBitType(colIndex);
/*      */         
/* 2440 */         return valueAsLong;
/*      */       } 
/*      */       
/* 2443 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_number", new Object[] { stringVal, Constants.integerValueOf(colIndex) }), "S1009");
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
/*      */   public int getFetchDirection() throws SQLException {
/* 2459 */     return this.fetchDirection;
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
/*      */   public int getFetchSize() throws SQLException {
/* 2471 */     return this.fetchSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char getFirstCharOfQuery() {
/* 2481 */     return this.firstCharOfQuery;
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
/*      */   public float getFloat(int columnIndex) throws SQLException {
/* 2496 */     if (!this.isBinaryEncoded) {
/* 2497 */       String val = null;
/*      */       
/* 2499 */       val = getString(columnIndex);
/*      */       
/* 2501 */       return getFloatFromString(val, columnIndex);
/*      */     } 
/*      */     
/* 2504 */     return getNativeFloat(columnIndex);
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
/*      */   public float getFloat(String columnName) throws SQLException {
/* 2519 */     return getFloat(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final float getFloatFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 2525 */       if (val != null) {
/* 2526 */         if (val.length() == 0) {
/* 2527 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2530 */         float f = Float.parseFloat(val);
/*      */         
/* 2532 */         if (this.jdbcCompliantTruncationForReads && (
/* 2533 */           f == Float.MIN_VALUE || f == Float.MAX_VALUE)) {
/* 2534 */           double valAsDouble = Double.parseDouble(val);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2540 */           if (valAsDouble < 1.401298464324817E-45D - MIN_DIFF_PREC || valAsDouble > 3.4028234663852886E38D - MAX_DIFF_PREC)
/*      */           {
/* 2542 */             throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 2548 */         return f;
/*      */       } 
/*      */       
/* 2551 */       return 0.0F;
/* 2552 */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2554 */         Double valueAsDouble = new Double(val);
/* 2555 */         float valueAsFloat = valueAsDouble.floatValue();
/*      */         
/* 2557 */         if (this.jdbcCompliantTruncationForReads)
/*      */         {
/* 2559 */           if ((this.jdbcCompliantTruncationForReads && valueAsFloat == Float.NEGATIVE_INFINITY) || valueAsFloat == Float.POSITIVE_INFINITY)
/*      */           {
/*      */             
/* 2562 */             throwRangeException(valueAsDouble.toString(), columnIndex, 6);
/*      */           }
/*      */         }
/*      */ 
/*      */         
/* 2567 */         return valueAsFloat;
/* 2568 */       } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */         
/* 2572 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, "S1009");
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
/*      */   public int getInt(int columnIndex) throws SQLException {
/* 2593 */     checkRowPos();
/*      */     
/* 2595 */     if (!this.isBinaryEncoded) {
/* 2596 */       int columnIndexMinusOne = columnIndex - 1;
/* 2597 */       if (this.useFastIntParsing) {
/* 2598 */         checkColumnBounds(columnIndex);
/*      */         
/* 2600 */         if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2601 */           this.wasNullFlag = true;
/*      */         } else {
/* 2603 */           this.wasNullFlag = false;
/*      */         } 
/*      */         
/* 2606 */         if (this.wasNullFlag) {
/* 2607 */           return 0;
/*      */         }
/*      */         
/* 2610 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2611 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2614 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */ 
/*      */         
/* 2617 */         if (!needsFullParse) {
/*      */           try {
/* 2619 */             return getIntWithOverflowCheck(columnIndexMinusOne);
/* 2620 */           } catch (NumberFormatException nfe) {
/*      */             
/*      */             try {
/* 2623 */               return parseIntAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
/*      */ 
/*      */ 
/*      */             
/*      */             }
/* 2628 */             catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */               
/* 2632 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2633 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */                 
/* 2635 */                 if (this.connection.getJdbcCompliantTruncationForReads() && (valueAsLong < -2147483648L || valueAsLong > 2147483647L))
/*      */                 {
/*      */                   
/* 2638 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */                 }
/*      */ 
/*      */ 
/*      */                 
/* 2643 */                 return (int)valueAsLong;
/*      */               } 
/*      */               
/* 2646 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009");
/*      */             } 
/*      */           } 
/*      */         }
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
/* 2662 */       String val = null;
/*      */       
/*      */       try {
/* 2665 */         val = getString(columnIndex);
/*      */         
/* 2667 */         if (val != null) {
/* 2668 */           if (val.length() == 0) {
/* 2669 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */           
/* 2672 */           if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1) {
/*      */             
/* 2674 */             int i = Integer.parseInt(val);
/*      */             
/* 2676 */             checkForIntegerTruncation(columnIndex, null, val, i);
/*      */             
/* 2678 */             return i;
/*      */           } 
/*      */ 
/*      */           
/* 2682 */           int intVal = parseIntAsDouble(columnIndex, val);
/*      */           
/* 2684 */           checkForIntegerTruncation(columnIndex, null, val, intVal);
/*      */           
/* 2686 */           return intVal;
/*      */         } 
/*      */         
/* 2689 */         return 0;
/* 2690 */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2692 */           return parseIntAsDouble(columnIndex, val);
/* 2693 */         } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */           
/* 2697 */           if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2698 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */             
/* 2700 */             if (this.jdbcCompliantTruncationForReads && (valueAsLong < -2147483648L || valueAsLong > 2147483647L))
/*      */             {
/* 2702 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */ 
/*      */             
/* 2706 */             return (int)valueAsLong;
/*      */           } 
/*      */           
/* 2709 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + val + "'", "S1009");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2719 */     return getNativeInt(columnIndex);
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
/*      */   public int getInt(String columnName) throws SQLException {
/* 2734 */     return getInt(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final int getIntFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 2740 */       if (val != null) {
/*      */         
/* 2742 */         if (val.length() == 0) {
/* 2743 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2746 */         if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2756 */           val = val.trim();
/*      */           
/* 2758 */           int valueAsInt = Integer.parseInt(val);
/*      */           
/* 2760 */           if (this.jdbcCompliantTruncationForReads && (
/* 2761 */             valueAsInt == Integer.MIN_VALUE || valueAsInt == Integer.MAX_VALUE)) {
/*      */             
/* 2763 */             long valueAsLong = Long.parseLong(val);
/*      */             
/* 2765 */             if (valueAsLong < -2147483648L || valueAsLong > 2147483647L)
/*      */             {
/* 2767 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2774 */           return valueAsInt;
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 2779 */         double valueAsDouble = Double.parseDouble(val);
/*      */         
/* 2781 */         if (this.jdbcCompliantTruncationForReads && (
/* 2782 */           valueAsDouble < -2.147483648E9D || valueAsDouble > 2.147483647E9D))
/*      */         {
/* 2784 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 2789 */         return (int)valueAsDouble;
/*      */       } 
/*      */       
/* 2792 */       return 0;
/* 2793 */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2795 */         double valueAsDouble = Double.parseDouble(val);
/*      */         
/* 2797 */         if (this.jdbcCompliantTruncationForReads && (
/* 2798 */           valueAsDouble < -2.147483648E9D || valueAsDouble > 2.147483647E9D))
/*      */         {
/* 2800 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 2805 */         return (int)valueAsDouble;
/* 2806 */       } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */         
/* 2810 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, "S1009");
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
/*      */   public long getLong(int columnIndex) throws SQLException {
/* 2830 */     return getLong(columnIndex, true);
/*      */   }
/*      */   
/*      */   private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
/* 2834 */     if (!this.isBinaryEncoded) {
/* 2835 */       checkRowPos();
/*      */       
/* 2837 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 2839 */       if (this.useFastIntParsing) {
/*      */         
/* 2841 */         checkColumnBounds(columnIndex);
/*      */         
/* 2843 */         if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2844 */           this.wasNullFlag = true;
/*      */         } else {
/* 2846 */           this.wasNullFlag = false;
/*      */         } 
/*      */         
/* 2849 */         if (this.wasNullFlag) {
/* 2850 */           return 0L;
/*      */         }
/*      */         
/* 2853 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2854 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2857 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */         
/* 2859 */         if (!needsFullParse) {
/*      */           try {
/* 2861 */             return getLongWithOverflowCheck(columnIndexMinusOne, overflowCheck);
/* 2862 */           } catch (NumberFormatException nfe) {
/*      */             
/*      */             try {
/* 2865 */               return parseLongAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
/*      */ 
/*      */ 
/*      */             
/*      */             }
/* 2870 */             catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */               
/* 2874 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2875 */                 return getNumericRepresentationOfSQLBitType(columnIndex);
/*      */               }
/*      */               
/* 2878 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009");
/*      */             } 
/*      */           } 
/*      */         }
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
/* 2892 */       String val = null;
/*      */       
/*      */       try {
/* 2895 */         val = getString(columnIndex);
/*      */         
/* 2897 */         if (val != null) {
/* 2898 */           if (val.length() == 0) {
/* 2899 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */           
/* 2902 */           if (val.indexOf("e") == -1 && val.indexOf("E") == -1) {
/* 2903 */             return parseLongWithOverflowCheck(columnIndex, null, val, overflowCheck);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 2908 */           return parseLongAsDouble(columnIndex, val);
/*      */         } 
/*      */         
/* 2911 */         return 0L;
/* 2912 */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2914 */           return parseLongAsDouble(columnIndex, val);
/* 2915 */         } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */           
/* 2919 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + val + "'", "S1009");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2927 */     return getNativeLong(columnIndex, overflowCheck, true);
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
/*      */   public long getLong(String columnName) throws SQLException {
/* 2942 */     return getLong(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final long getLongFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 2948 */       if (val != null) {
/*      */         
/* 2950 */         if (val.length() == 0) {
/* 2951 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2954 */         if (val.indexOf("e") == -1 && val.indexOf("E") == -1) {
/* 2955 */           return parseLongWithOverflowCheck(columnIndex, null, val, true);
/*      */         }
/*      */ 
/*      */         
/* 2959 */         return parseLongAsDouble(columnIndex, val);
/*      */       } 
/*      */       
/* 2962 */       return 0L;
/* 2963 */     } catch (NumberFormatException nfe) {
/*      */       
/*      */       try {
/* 2966 */         return parseLongAsDouble(columnIndex, val);
/* 2967 */       } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */         
/* 2971 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + columnIndex, "S1009");
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
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/* 2990 */     checkClosed();
/*      */     
/* 2992 */     return new ResultSetMetaData(this.fields, this.connection.getUseOldAliasMetadataBehavior());
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
/*      */   protected Array getNativeArray(int i) throws SQLException {
/* 3010 */     throw SQLError.notImplemented();
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
/*      */   protected InputStream getNativeAsciiStream(int columnIndex) throws SQLException {
/* 3040 */     checkRowPos();
/*      */     
/* 3042 */     return getNativeBinaryStream(columnIndex);
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
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex) throws SQLException {
/* 3061 */     checkColumnBounds(columnIndex);
/*      */     
/* 3063 */     int scale = this.fields[columnIndex - 1].getDecimals();
/*      */     
/* 3065 */     return getNativeBigDecimal(columnIndex, scale);
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
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex, int scale) throws SQLException {
/* 3084 */     checkColumnBounds(columnIndex);
/*      */     
/* 3086 */     String stringVal = null;
/*      */     
/* 3088 */     Field f = this.fields[columnIndex - 1];
/*      */     
/* 3090 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 3092 */     if (value == null) {
/* 3093 */       this.wasNullFlag = true;
/*      */       
/* 3095 */       return null;
/*      */     } 
/*      */     
/* 3098 */     this.wasNullFlag = false;
/*      */     
/* 3100 */     switch (f.getSQLType())
/*      */     { case 2:
/*      */       case 3:
/* 3103 */         stringVal = StringUtils.toAsciiString((byte[])value);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3110 */         return getBigDecimalFromString(stringVal, columnIndex, scale); }  stringVal = getNativeString(columnIndex); return getBigDecimalFromString(stringVal, columnIndex, scale);
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
/*      */   protected InputStream getNativeBinaryStream(int columnIndex) throws SQLException {
/* 3132 */     checkRowPos();
/*      */     
/* 3134 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 3136 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3137 */       this.wasNullFlag = true;
/*      */       
/* 3139 */       return null;
/*      */     } 
/*      */     
/* 3142 */     this.wasNullFlag = false;
/*      */     
/* 3144 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */       case -7:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case 2004:
/* 3150 */         return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     } 
/*      */     
/* 3153 */     byte[] b = getNativeBytes(columnIndex, false);
/*      */     
/* 3155 */     if (b != null) {
/* 3156 */       return new ByteArrayInputStream(b);
/*      */     }
/*      */     
/* 3159 */     return null;
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
/*      */   protected Blob getNativeBlob(int columnIndex) throws SQLException {
/* 3174 */     checkRowPos();
/*      */     
/* 3176 */     checkColumnBounds(columnIndex);
/*      */     
/* 3178 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 3180 */     if (value == null) {
/* 3181 */       this.wasNullFlag = true;
/*      */     } else {
/* 3183 */       this.wasNullFlag = false;
/*      */     } 
/*      */     
/* 3186 */     if (this.wasNullFlag) {
/* 3187 */       return null;
/*      */     }
/*      */     
/* 3190 */     int mysqlType = this.fields[columnIndex - 1].getMysqlType();
/*      */     
/* 3192 */     byte[] dataAsBytes = null;
/*      */     
/* 3194 */     switch (mysqlType) {
/*      */       case 249:
/*      */       case 250:
/*      */       case 251:
/*      */       case 252:
/* 3199 */         dataAsBytes = (byte[])value;
/*      */         break;
/*      */       
/*      */       default:
/* 3203 */         dataAsBytes = getNativeBytes(columnIndex, false);
/*      */         break;
/*      */     } 
/* 3206 */     if (!this.connection.getEmulateLocators()) {
/* 3207 */       return new Blob(dataAsBytes);
/*      */     }
/*      */     
/* 3210 */     return new BlobFromLocator(this, columnIndex);
/*      */   }
/*      */   
/*      */   public static boolean arraysEqual(byte[] left, byte[] right) {
/* 3214 */     if (left == null) {
/* 3215 */       return (right == null);
/*      */     }
/* 3217 */     if (right == null) {
/* 3218 */       return false;
/*      */     }
/* 3220 */     if (left.length != right.length) {
/* 3221 */       return false;
/*      */     }
/* 3223 */     for (int i = 0; i < left.length; i++) {
/* 3224 */       if (left[i] != right[i]) {
/* 3225 */         return false;
/*      */       }
/*      */     } 
/* 3228 */     return true;
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
/*      */   protected byte getNativeByte(int columnIndex) throws SQLException {
/* 3243 */     return getNativeByte(columnIndex, true); } protected byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException { long valueAsLong; byte valueAsByte; short valueAsShort;
/*      */     int valueAsInt;
/*      */     float valueAsFloat;
/*      */     double valueAsDouble;
/* 3247 */     checkRowPos();
/*      */     
/* 3249 */     checkColumnBounds(columnIndex);
/*      */     
/* 3251 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 3253 */     if (value == null) {
/* 3254 */       this.wasNullFlag = true;
/*      */       
/* 3256 */       return 0;
/*      */     } 
/*      */     
/* 3259 */     if (value == null) {
/* 3260 */       this.wasNullFlag = true;
/*      */     } else {
/* 3262 */       this.wasNullFlag = false;
/*      */     } 
/*      */     
/* 3265 */     if (this.wasNullFlag) {
/* 3266 */       return 0;
/*      */     }
/*      */     
/* 3269 */     columnIndex--;
/*      */     
/* 3271 */     Field field = this.fields[columnIndex];
/*      */     
/* 3273 */     switch (field.getMysqlType()) {
/*      */       case 16:
/* 3275 */         valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */         
/* 3277 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong < -128L || valueAsLong > 127L))
/*      */         {
/*      */           
/* 3280 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */         
/* 3284 */         return (byte)(int)valueAsLong;
/*      */       case 1:
/* 3286 */         valueAsByte = ((byte[])value)[0];
/*      */         
/* 3288 */         if (!field.isUnsigned()) {
/* 3289 */           return valueAsByte;
/*      */         }
/*      */         
/* 3292 */         valueAsShort = (valueAsByte >= 0) ? (short)valueAsByte : (short)(valueAsByte + 256);
/*      */ 
/*      */         
/* 3295 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && 
/* 3296 */           valueAsShort > 127) {
/* 3297 */           throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3302 */         return (byte)valueAsShort;
/*      */       
/*      */       case 2:
/*      */       case 13:
/* 3306 */         valueAsShort = getNativeShort(columnIndex + 1);
/*      */         
/* 3308 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 3309 */           valueAsShort < -128 || valueAsShort > 127))
/*      */         {
/* 3311 */           throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3316 */         return (byte)valueAsShort;
/*      */       case 3:
/*      */       case 9:
/* 3319 */         valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */         
/* 3321 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 3322 */           valueAsInt < -128 || valueAsInt > 127)) {
/* 3323 */           throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3328 */         return (byte)valueAsInt;
/*      */       
/*      */       case 4:
/* 3331 */         valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */         
/* 3333 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 3334 */           valueAsFloat < -128.0F || valueAsFloat > 127.0F))
/*      */         {
/*      */           
/* 3337 */           throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3342 */         return (byte)(int)valueAsFloat;
/*      */       
/*      */       case 5:
/* 3345 */         valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */         
/* 3347 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 3348 */           valueAsDouble < -128.0D || valueAsDouble > 127.0D))
/*      */         {
/* 3350 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3355 */         return (byte)(int)valueAsDouble;
/*      */       
/*      */       case 8:
/* 3358 */         valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */         
/* 3360 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 3361 */           valueAsLong < -128L || valueAsLong > 127L))
/*      */         {
/* 3363 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3368 */         return (byte)(int)valueAsLong;
/*      */     } 
/*      */     
/* 3371 */     if (this.useUsageAdvisor) {
/* 3372 */       issueConversionViaParsingWarning("getByte()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3382 */     return getByteFromString(getNativeString(columnIndex + 1), columnIndex + 1); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] getNativeBytes(int columnIndex, boolean noConversion) throws SQLException {
/* 3404 */     checkRowPos();
/*      */     
/* 3406 */     checkColumnBounds(columnIndex);
/*      */     
/* 3408 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 3410 */     if (value == null) {
/* 3411 */       this.wasNullFlag = true;
/*      */     } else {
/* 3413 */       this.wasNullFlag = false;
/*      */     } 
/*      */     
/* 3416 */     if (this.wasNullFlag) {
/* 3417 */       return null;
/*      */     }
/*      */     
/* 3420 */     Field field = this.fields[columnIndex - 1];
/*      */     
/* 3422 */     int mysqlType = field.getMysqlType();
/*      */ 
/*      */ 
/*      */     
/* 3426 */     if (noConversion) {
/* 3427 */       mysqlType = 252;
/*      */     }
/*      */     
/* 3430 */     switch (mysqlType) {
/*      */       case 16:
/*      */       case 249:
/*      */       case 250:
/*      */       case 251:
/*      */       case 252:
/* 3436 */         return (byte[])value;
/*      */       
/*      */       case 15:
/*      */       case 253:
/*      */       case 254:
/* 3441 */         if (value instanceof byte[]) {
/* 3442 */           return (byte[])value;
/*      */         }
/*      */         break;
/*      */     } 
/* 3446 */     int sqlType = field.getSQLType();
/*      */     
/* 3448 */     if (sqlType == -3 || sqlType == -2) {
/* 3449 */       return (byte[])value;
/*      */     }
/*      */     
/* 3452 */     return getBytesFromString(getNativeString(columnIndex), columnIndex);
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
/*      */   protected Reader getNativeCharacterStream(int columnIndex) throws SQLException {
/* 3473 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 3475 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*      */       case 2005:
/* 3480 */         if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3481 */           this.wasNullFlag = true;
/*      */           
/* 3483 */           return null;
/*      */         } 
/*      */         
/* 3486 */         this.wasNullFlag = false;
/*      */         
/* 3488 */         return this.thisRow.getReader(columnIndexMinusOne);
/*      */     } 
/*      */     
/* 3491 */     String asString = null;
/*      */     
/* 3493 */     asString = getStringForClob(columnIndex);
/*      */     
/* 3495 */     if (asString == null) {
/* 3496 */       return null;
/*      */     }
/*      */     
/* 3499 */     return getCharacterStreamFromString(asString, columnIndex);
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
/*      */   protected Clob getNativeClob(int columnIndex) throws SQLException {
/* 3514 */     String stringVal = getStringForClob(columnIndex);
/*      */     
/* 3516 */     if (stringVal == null) {
/* 3517 */       return null;
/*      */     }
/*      */     
/* 3520 */     return getClobFromString(stringVal, columnIndex); } private String getNativeConvertToString(int columnIndex, Field field) throws SQLException { boolean booleanVal; byte tinyintVal; short unsignedTinyVal; int intVal; long longVal; float floatVal; double doubleVal;
/*      */     String stringVal;
/*      */     byte[] data;
/*      */     Date dt;
/*      */     Object obj;
/*      */     Time tm;
/*      */     Timestamp tstamp;
/*      */     String result;
/* 3528 */     int sqlType = field.getSQLType();
/* 3529 */     int mysqlType = field.getMysqlType();
/*      */     
/* 3531 */     switch (sqlType) {
/*      */       case -7:
/* 3533 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       case 16:
/* 3535 */         booleanVal = getBoolean(columnIndex);
/*      */         
/* 3537 */         if (this.wasNullFlag) {
/* 3538 */           return null;
/*      */         }
/*      */         
/* 3541 */         return String.valueOf(booleanVal);
/*      */       
/*      */       case -6:
/* 3544 */         tinyintVal = getNativeByte(columnIndex, false);
/*      */         
/* 3546 */         if (this.wasNullFlag) {
/* 3547 */           return null;
/*      */         }
/*      */         
/* 3550 */         if (!field.isUnsigned() || tinyintVal >= 0) {
/* 3551 */           return String.valueOf(tinyintVal);
/*      */         }
/*      */         
/* 3554 */         unsignedTinyVal = (short)(tinyintVal & 0xFF);
/*      */         
/* 3556 */         return String.valueOf(unsignedTinyVal);
/*      */ 
/*      */       
/*      */       case 5:
/* 3560 */         intVal = getNativeInt(columnIndex, false);
/*      */         
/* 3562 */         if (this.wasNullFlag) {
/* 3563 */           return null;
/*      */         }
/*      */         
/* 3566 */         if (!field.isUnsigned() || intVal >= 0) {
/* 3567 */           return String.valueOf(intVal);
/*      */         }
/*      */         
/* 3570 */         intVal &= 0xFFFF;
/*      */         
/* 3572 */         return String.valueOf(intVal);
/*      */       
/*      */       case 4:
/* 3575 */         intVal = getNativeInt(columnIndex, false);
/*      */         
/* 3577 */         if (this.wasNullFlag) {
/* 3578 */           return null;
/*      */         }
/*      */         
/* 3581 */         if (!field.isUnsigned() || intVal >= 0 || field.getMysqlType() == 9)
/*      */         {
/*      */           
/* 3584 */           return String.valueOf(intVal);
/*      */         }
/*      */         
/* 3587 */         longVal = intVal & 0xFFFFFFFFL;
/*      */         
/* 3589 */         return String.valueOf(longVal);
/*      */ 
/*      */       
/*      */       case -5:
/* 3593 */         if (!field.isUnsigned()) {
/* 3594 */           longVal = getNativeLong(columnIndex, false, true);
/*      */           
/* 3596 */           if (this.wasNullFlag) {
/* 3597 */             return null;
/*      */           }
/*      */           
/* 3600 */           return String.valueOf(longVal);
/*      */         } 
/*      */         
/* 3603 */         longVal = getNativeLong(columnIndex, false, false);
/*      */         
/* 3605 */         if (this.wasNullFlag) {
/* 3606 */           return null;
/*      */         }
/*      */         
/* 3609 */         return String.valueOf(convertLongToUlong(longVal));
/*      */       case 7:
/* 3611 */         floatVal = getNativeFloat(columnIndex);
/*      */         
/* 3613 */         if (this.wasNullFlag) {
/* 3614 */           return null;
/*      */         }
/*      */         
/* 3617 */         return String.valueOf(floatVal);
/*      */       
/*      */       case 6:
/*      */       case 8:
/* 3621 */         doubleVal = getNativeDouble(columnIndex);
/*      */         
/* 3623 */         if (this.wasNullFlag) {
/* 3624 */           return null;
/*      */         }
/*      */         
/* 3627 */         return String.valueOf(doubleVal);
/*      */       
/*      */       case 2:
/*      */       case 3:
/* 3631 */         stringVal = StringUtils.toAsciiString(this.thisRow.getColumnValue(columnIndex - 1));
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3636 */         if (stringVal != null) {
/* 3637 */           BigDecimal val; this.wasNullFlag = false;
/*      */           
/* 3639 */           if (stringVal.length() == 0) {
/* 3640 */             val = new BigDecimal(0.0D);
/*      */             
/* 3642 */             return val.toString();
/*      */           } 
/*      */           
/*      */           try {
/* 3646 */             val = new BigDecimal(stringVal);
/* 3647 */           } catch (NumberFormatException ex) {
/* 3648 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3655 */           return val.toString();
/*      */         } 
/*      */         
/* 3658 */         this.wasNullFlag = true;
/*      */         
/* 3660 */         return null;
/*      */ 
/*      */       
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 3666 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/* 3671 */         if (!field.isBlob())
/* 3672 */           return extractStringFromNativeColumn(columnIndex, mysqlType); 
/* 3673 */         if (!field.isBinary()) {
/* 3674 */           return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */         }
/* 3676 */         data = getBytes(columnIndex);
/* 3677 */         obj = data;
/*      */         
/* 3679 */         if (data != null && data.length >= 2) {
/* 3680 */           if (data[0] == -84 && data[1] == -19) {
/*      */             
/*      */             try {
/* 3683 */               ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */               
/* 3685 */               ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */               
/* 3687 */               obj = objIn.readObject();
/* 3688 */               objIn.close();
/* 3689 */               bytesIn.close();
/* 3690 */             } catch (ClassNotFoundException cnfe) {
/* 3691 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"));
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             }
/* 3697 */             catch (IOException ex) {
/* 3698 */               obj = data;
/*      */             } 
/*      */           }
/*      */           
/* 3702 */           return obj.toString();
/*      */         } 
/*      */         
/* 3705 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 91:
/* 3711 */         if (mysqlType == 13) {
/* 3712 */           short shortVal = getNativeShort(columnIndex);
/*      */           
/* 3714 */           if (!this.connection.getYearIsDateType()) {
/*      */             
/* 3716 */             if (this.wasNullFlag) {
/* 3717 */               return null;
/*      */             }
/*      */             
/* 3720 */             return String.valueOf(shortVal);
/*      */           } 
/*      */           
/* 3723 */           if (field.getLength() == 2L) {
/*      */             
/* 3725 */             if (shortVal <= 69) {
/* 3726 */               shortVal = (short)(shortVal + 100);
/*      */             }
/*      */             
/* 3729 */             shortVal = (short)(shortVal + 1900);
/*      */           } 
/*      */           
/* 3732 */           return fastDateCreate(null, shortVal, 1, 1).toString();
/*      */         } 
/*      */ 
/*      */         
/* 3736 */         dt = getNativeDate(columnIndex);
/*      */         
/* 3738 */         if (dt == null) {
/* 3739 */           return null;
/*      */         }
/*      */         
/* 3742 */         return String.valueOf(dt);
/*      */       
/*      */       case 92:
/* 3745 */         tm = getNativeTime(columnIndex, null, this.defaultTimeZone, false);
/*      */         
/* 3747 */         if (tm == null) {
/* 3748 */           return null;
/*      */         }
/*      */         
/* 3751 */         return String.valueOf(tm);
/*      */       
/*      */       case 93:
/* 3754 */         tstamp = getNativeTimestamp(columnIndex, null, this.defaultTimeZone, false);
/*      */ 
/*      */         
/* 3757 */         if (tstamp == null) {
/* 3758 */           return null;
/*      */         }
/*      */         
/* 3761 */         result = String.valueOf(tstamp);
/*      */         
/* 3763 */         if (!this.connection.getNoDatetimeStringSync()) {
/* 3764 */           return result;
/*      */         }
/*      */         
/* 3767 */         if (result.endsWith(".0")) {
/* 3768 */           return result.substring(0, result.length() - 2);
/*      */         }
/*      */         break;
/*      */     } 
/* 3772 */     return extractStringFromNativeColumn(columnIndex, mysqlType); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Date getNativeDate(int columnIndex) throws SQLException {
/* 3788 */     return getNativeDate(columnIndex, null);
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
/*      */   protected Date getNativeDate(int columnIndex, TimeZone tz) throws SQLException {
/* 3809 */     checkRowPos();
/* 3810 */     checkColumnBounds(columnIndex);
/*      */     
/* 3812 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 3814 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */     
/* 3816 */     Date dateToReturn = null;
/*      */     
/* 3818 */     if (mysqlType == 10) {
/*      */       
/* 3820 */       dateToReturn = this.thisRow.getNativeDate(columnIndexMinusOne, this.connection, this);
/*      */     }
/*      */     else {
/*      */       
/* 3824 */       boolean rollForward = (tz != null && !tz.equals(getDefaultTimeZone()));
/*      */       
/* 3826 */       dateToReturn = (Date)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 91, mysqlType, tz, rollForward, this.connection, this);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3838 */     if (dateToReturn == null) {
/*      */       
/* 3840 */       this.wasNullFlag = true;
/*      */       
/* 3842 */       return null;
/*      */     } 
/*      */     
/* 3845 */     this.wasNullFlag = false;
/*      */     
/* 3847 */     return dateToReturn;
/*      */   }
/*      */   
/*      */   Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
/* 3851 */     if (this.useUsageAdvisor) {
/* 3852 */       issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex - 1], new int[] { 10 });
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 3857 */     String stringVal = getNativeString(columnIndex);
/*      */     
/* 3859 */     return getDateFromString(stringVal, columnIndex, null);
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
/*      */   protected double getNativeDouble(int columnIndex) throws SQLException {
/*      */     long valueAsLong;
/*      */     BigInteger asBigInt;
/* 3874 */     checkRowPos();
/* 3875 */     checkColumnBounds(columnIndex);
/*      */     
/* 3877 */     columnIndex--;
/*      */     
/* 3879 */     if (this.thisRow.isNull(columnIndex)) {
/* 3880 */       this.wasNullFlag = true;
/*      */       
/* 3882 */       return 0.0D;
/*      */     } 
/*      */     
/* 3885 */     this.wasNullFlag = false;
/*      */     
/* 3887 */     Field f = this.fields[columnIndex];
/*      */     
/* 3889 */     switch (f.getMysqlType()) {
/*      */       case 5:
/* 3891 */         return this.thisRow.getNativeDouble(columnIndex);
/*      */       case 1:
/* 3893 */         if (!f.isUnsigned()) {
/* 3894 */           return getNativeByte(columnIndex + 1);
/*      */         }
/*      */         
/* 3897 */         return getNativeShort(columnIndex + 1);
/*      */       case 2:
/*      */       case 13:
/* 3900 */         if (!f.isUnsigned()) {
/* 3901 */           return getNativeShort(columnIndex + 1);
/*      */         }
/*      */         
/* 3904 */         return getNativeInt(columnIndex + 1);
/*      */       case 3:
/*      */       case 9:
/* 3907 */         if (!f.isUnsigned()) {
/* 3908 */           return getNativeInt(columnIndex + 1);
/*      */         }
/*      */         
/* 3911 */         return getNativeLong(columnIndex + 1);
/*      */       case 8:
/* 3913 */         valueAsLong = getNativeLong(columnIndex + 1);
/*      */         
/* 3915 */         if (!f.isUnsigned()) {
/* 3916 */           return valueAsLong;
/*      */         }
/*      */         
/* 3919 */         asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/*      */ 
/*      */         
/* 3923 */         return asBigInt.doubleValue();
/*      */       case 4:
/* 3925 */         return getNativeFloat(columnIndex + 1);
/*      */       case 16:
/* 3927 */         return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     } 
/* 3929 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 3931 */     if (this.useUsageAdvisor) {
/* 3932 */       issueConversionViaParsingWarning("getDouble()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3942 */     return getDoubleFromString(stringVal, columnIndex + 1);
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
/*      */   protected float getNativeFloat(int columnIndex) throws SQLException {
/*      */     long valueAsLong;
/*      */     Double valueAsDouble;
/*      */     float valueAsFloat;
/*      */     BigInteger asBigInt;
/* 3958 */     checkRowPos();
/* 3959 */     checkColumnBounds(columnIndex);
/*      */     
/* 3961 */     columnIndex--;
/*      */     
/* 3963 */     if (this.thisRow.isNull(columnIndex)) {
/* 3964 */       this.wasNullFlag = true;
/*      */       
/* 3966 */       return 0.0F;
/*      */     } 
/*      */     
/* 3969 */     this.wasNullFlag = false;
/*      */     
/* 3971 */     Field f = this.fields[columnIndex];
/*      */     
/* 3973 */     switch (f.getMysqlType()) {
/*      */       case 16:
/* 3975 */         valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */         
/* 3977 */         return (float)valueAsLong;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 5:
/* 3984 */         valueAsDouble = new Double(getNativeDouble(columnIndex + 1));
/*      */         
/* 3986 */         valueAsFloat = valueAsDouble.floatValue();
/*      */         
/* 3988 */         if ((this.jdbcCompliantTruncationForReads && valueAsFloat == Float.NEGATIVE_INFINITY) || valueAsFloat == Float.POSITIVE_INFINITY)
/*      */         {
/*      */           
/* 3991 */           throwRangeException(valueAsDouble.toString(), columnIndex + 1, 6);
/*      */         }
/*      */ 
/*      */         
/* 3995 */         return (float)getNativeDouble(columnIndex + 1);
/*      */       case 1:
/* 3997 */         if (!f.isUnsigned()) {
/* 3998 */           return getNativeByte(columnIndex + 1);
/*      */         }
/*      */         
/* 4001 */         return getNativeShort(columnIndex + 1);
/*      */       case 2:
/*      */       case 13:
/* 4004 */         if (!f.isUnsigned()) {
/* 4005 */           return getNativeShort(columnIndex + 1);
/*      */         }
/*      */         
/* 4008 */         return getNativeInt(columnIndex + 1);
/*      */       case 3:
/*      */       case 9:
/* 4011 */         if (!f.isUnsigned()) {
/* 4012 */           return getNativeInt(columnIndex + 1);
/*      */         }
/*      */         
/* 4015 */         return (float)getNativeLong(columnIndex + 1);
/*      */       case 8:
/* 4017 */         valueAsLong = getNativeLong(columnIndex + 1);
/*      */         
/* 4019 */         if (!f.isUnsigned()) {
/* 4020 */           return (float)valueAsLong;
/*      */         }
/*      */         
/* 4023 */         asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/*      */ 
/*      */         
/* 4027 */         return asBigInt.floatValue();
/*      */       
/*      */       case 4:
/* 4030 */         return this.thisRow.getNativeFloat(columnIndex);
/*      */     } 
/*      */     
/* 4033 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 4035 */     if (this.useUsageAdvisor) {
/* 4036 */       issueConversionViaParsingWarning("getFloat()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4046 */     return getFloatFromString(stringVal, columnIndex + 1);
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
/*      */   protected int getNativeInt(int columnIndex) throws SQLException {
/* 4062 */     return getNativeInt(columnIndex, true); } protected int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException { long valueAsLong; byte tinyintVal;
/*      */     short asShort;
/*      */     int valueAsInt;
/*      */     double valueAsDouble;
/* 4066 */     checkRowPos();
/* 4067 */     checkColumnBounds(columnIndex);
/*      */     
/* 4069 */     columnIndex--;
/*      */     
/* 4071 */     if (this.thisRow.isNull(columnIndex)) {
/* 4072 */       this.wasNullFlag = true;
/*      */       
/* 4074 */       return 0;
/*      */     } 
/*      */     
/* 4077 */     this.wasNullFlag = false;
/*      */     
/* 4079 */     Field f = this.fields[columnIndex];
/*      */     
/* 4081 */     switch (f.getMysqlType()) {
/*      */       case 16:
/* 4083 */         valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */         
/* 4085 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong < -2147483648L || valueAsLong > 2147483647L))
/*      */         {
/*      */           
/* 4088 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */         }
/*      */ 
/*      */         
/* 4092 */         return (short)(int)valueAsLong;
/*      */       case 1:
/* 4094 */         tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */         
/* 4096 */         if (!f.isUnsigned() || tinyintVal >= 0) {
/* 4097 */           return tinyintVal;
/*      */         }
/*      */         
/* 4100 */         return tinyintVal + 256;
/*      */       case 2:
/*      */       case 13:
/* 4103 */         asShort = getNativeShort(columnIndex + 1, false);
/*      */         
/* 4105 */         if (!f.isUnsigned() || asShort >= 0) {
/* 4106 */           return asShort;
/*      */         }
/*      */         
/* 4109 */         return asShort + 65536;
/*      */       
/*      */       case 3:
/*      */       case 9:
/* 4113 */         valueAsInt = this.thisRow.getNativeInt(columnIndex);
/*      */         
/* 4115 */         if (!f.isUnsigned()) {
/* 4116 */           return valueAsInt;
/*      */         }
/*      */         
/* 4119 */         valueAsLong = (valueAsInt >= 0) ? valueAsInt : (valueAsInt + 4294967296L);
/*      */ 
/*      */         
/* 4122 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsLong > 2147483647L)
/*      */         {
/* 4124 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */         }
/*      */ 
/*      */         
/* 4128 */         return (int)valueAsLong;
/*      */       case 8:
/* 4130 */         valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */         
/* 4132 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4133 */           valueAsLong < -2147483648L || valueAsLong > 2147483647L))
/*      */         {
/* 4135 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4140 */         return (int)valueAsLong;
/*      */       case 5:
/* 4142 */         valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */         
/* 4144 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4145 */           valueAsDouble < -2.147483648E9D || valueAsDouble > 2.147483647E9D))
/*      */         {
/* 4147 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4152 */         return (int)valueAsDouble;
/*      */       case 4:
/* 4154 */         valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */         
/* 4156 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4157 */           valueAsDouble < -2.147483648E9D || valueAsDouble > 2.147483647E9D))
/*      */         {
/* 4159 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4164 */         return (int)valueAsDouble;
/*      */     } 
/*      */     
/* 4167 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 4169 */     if (this.useUsageAdvisor) {
/* 4170 */       issueConversionViaParsingWarning("getInt()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4180 */     return getIntFromString(stringVal, columnIndex + 1); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long getNativeLong(int columnIndex) throws SQLException {
/* 4196 */     return getNativeLong(columnIndex, true, true); } protected long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException {
/*      */     int asInt;
/*      */     long valueAsLong;
/*      */     BigInteger asBigInt;
/*      */     double valueAsDouble;
/* 4201 */     checkRowPos();
/* 4202 */     checkColumnBounds(columnIndex);
/*      */     
/* 4204 */     columnIndex--;
/*      */     
/* 4206 */     if (this.thisRow.isNull(columnIndex)) {
/* 4207 */       this.wasNullFlag = true;
/*      */       
/* 4209 */       return 0L;
/*      */     } 
/*      */     
/* 4212 */     this.wasNullFlag = false;
/*      */     
/* 4214 */     Field f = this.fields[columnIndex];
/*      */     
/* 4216 */     switch (f.getMysqlType()) {
/*      */       case 16:
/* 4218 */         return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */       case 1:
/* 4220 */         if (!f.isUnsigned()) {
/* 4221 */           return getNativeByte(columnIndex + 1);
/*      */         }
/*      */         
/* 4224 */         return getNativeInt(columnIndex + 1);
/*      */       case 2:
/* 4226 */         if (!f.isUnsigned()) {
/* 4227 */           return getNativeShort(columnIndex + 1);
/*      */         }
/*      */         
/* 4230 */         return getNativeInt(columnIndex + 1, false);
/*      */       
/*      */       case 13:
/* 4233 */         return getNativeShort(columnIndex + 1);
/*      */       case 3:
/*      */       case 9:
/* 4236 */         asInt = getNativeInt(columnIndex + 1, false);
/*      */         
/* 4238 */         if (!f.isUnsigned() || asInt >= 0) {
/* 4239 */           return asInt;
/*      */         }
/*      */         
/* 4242 */         return asInt + 4294967296L;
/*      */       case 8:
/* 4244 */         valueAsLong = this.thisRow.getNativeLong(columnIndex);
/*      */         
/* 4246 */         if (!f.isUnsigned() || !expandUnsignedLong) {
/* 4247 */           return valueAsLong;
/*      */         }
/*      */         
/* 4250 */         asBigInt = convertLongToUlong(valueAsLong);
/*      */         
/* 4252 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (asBigInt.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) > 0 || asBigInt.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) < 0))
/*      */         {
/*      */           
/* 4255 */           throwRangeException(asBigInt.toString(), columnIndex + 1, -5);
/*      */         }
/*      */ 
/*      */         
/* 4259 */         return getLongFromString(asBigInt.toString(), columnIndex + 1);
/*      */       
/*      */       case 5:
/* 4262 */         valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */         
/* 4264 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4265 */           valueAsDouble < -9.223372036854776E18D || valueAsDouble > 9.223372036854776E18D))
/*      */         {
/* 4267 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4272 */         return (long)valueAsDouble;
/*      */       case 4:
/* 4274 */         valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */         
/* 4276 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4277 */           valueAsDouble < -9.223372036854776E18D || valueAsDouble > 9.223372036854776E18D))
/*      */         {
/* 4279 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4284 */         return (long)valueAsDouble;
/*      */     } 
/* 4286 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 4288 */     if (this.useUsageAdvisor) {
/* 4289 */       issueConversionViaParsingWarning("getLong()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4299 */     return getLongFromString(stringVal, columnIndex + 1);
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
/*      */   protected Ref getNativeRef(int i) throws SQLException {
/* 4317 */     throw SQLError.notImplemented();
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
/*      */   protected short getNativeShort(int columnIndex) throws SQLException {
/* 4332 */     return getNativeShort(columnIndex, true); } protected short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException { byte tinyintVal; short asShort; int valueAsInt; long valueAsLong;
/*      */     BigInteger asBigInt;
/*      */     double valueAsDouble;
/*      */     float valueAsFloat;
/* 4336 */     checkRowPos();
/* 4337 */     checkColumnBounds(columnIndex);
/*      */     
/* 4339 */     columnIndex--;
/*      */ 
/*      */     
/* 4342 */     if (this.thisRow.isNull(columnIndex)) {
/* 4343 */       this.wasNullFlag = true;
/*      */       
/* 4345 */       return 0;
/*      */     } 
/*      */     
/* 4348 */     this.wasNullFlag = false;
/*      */     
/* 4350 */     Field f = this.fields[columnIndex];
/*      */     
/* 4352 */     switch (f.getMysqlType()) {
/*      */       
/*      */       case 1:
/* 4355 */         tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */         
/* 4357 */         if (!f.isUnsigned() || tinyintVal >= 0) {
/* 4358 */           return (short)tinyintVal;
/*      */         }
/*      */         
/* 4361 */         return (short)(tinyintVal + 256);
/*      */       
/*      */       case 2:
/*      */       case 13:
/* 4365 */         asShort = this.thisRow.getNativeShort(columnIndex);
/*      */         
/* 4367 */         if (!f.isUnsigned()) {
/* 4368 */           return asShort;
/*      */         }
/*      */         
/* 4371 */         valueAsInt = asShort & 0xFFFF;
/*      */         
/* 4373 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsInt > 32767)
/*      */         {
/* 4375 */           throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */         }
/*      */ 
/*      */         
/* 4379 */         return (short)valueAsInt;
/*      */       case 3:
/*      */       case 9:
/* 4382 */         if (!f.isUnsigned()) {
/* 4383 */           valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */           
/* 4385 */           if ((overflowCheck && this.jdbcCompliantTruncationForReads && valueAsInt > 32767) || valueAsInt < -32768)
/*      */           {
/*      */             
/* 4388 */             throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */           }
/*      */ 
/*      */           
/* 4392 */           return (short)valueAsInt;
/*      */         } 
/*      */         
/* 4395 */         valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */         
/* 4397 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsLong > 32767L)
/*      */         {
/* 4399 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */         }
/*      */ 
/*      */         
/* 4403 */         return (short)(int)valueAsLong;
/*      */       
/*      */       case 8:
/* 4406 */         valueAsLong = getNativeLong(columnIndex + 1, false, false);
/*      */         
/* 4408 */         if (!f.isUnsigned()) {
/* 4409 */           if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4410 */             valueAsLong < -32768L || valueAsLong > 32767L))
/*      */           {
/* 4412 */             throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 4417 */           return (short)(int)valueAsLong;
/*      */         } 
/*      */         
/* 4420 */         asBigInt = convertLongToUlong(valueAsLong);
/*      */         
/* 4422 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (asBigInt.compareTo(new BigInteger(String.valueOf(32767))) > 0 || asBigInt.compareTo(new BigInteger(String.valueOf(-32768))) < 0))
/*      */         {
/*      */           
/* 4425 */           throwRangeException(asBigInt.toString(), columnIndex + 1, 5);
/*      */         }
/*      */ 
/*      */         
/* 4429 */         return (short)getIntFromString(asBigInt.toString(), columnIndex + 1);
/*      */       
/*      */       case 5:
/* 4432 */         valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */         
/* 4434 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4435 */           valueAsDouble < -32768.0D || valueAsDouble > 32767.0D))
/*      */         {
/* 4437 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 5);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4442 */         return (short)(int)valueAsDouble;
/*      */       case 4:
/* 4444 */         valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */         
/* 4446 */         if (overflowCheck && this.jdbcCompliantTruncationForReads && (
/* 4447 */           valueAsFloat < -32768.0F || valueAsFloat > 32767.0F))
/*      */         {
/* 4449 */           throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, 5);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4454 */         return (short)(int)valueAsFloat;
/*      */     } 
/* 4456 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 4458 */     if (this.useUsageAdvisor) {
/* 4459 */       issueConversionViaParsingWarning("getShort()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4469 */     return getShortFromString(stringVal, columnIndex + 1); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getNativeString(int columnIndex) throws SQLException {
/* 4485 */     checkRowPos();
/* 4486 */     checkColumnBounds(columnIndex);
/*      */     
/* 4488 */     if (this.fields == null) {
/* 4489 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), "S1002");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4495 */     if (this.thisRow.isNull(columnIndex - 1)) {
/* 4496 */       this.wasNullFlag = true;
/*      */       
/* 4498 */       return null;
/*      */     } 
/*      */     
/* 4501 */     this.wasNullFlag = false;
/*      */     
/* 4503 */     String stringVal = null;
/*      */     
/* 4505 */     Field field = this.fields[columnIndex - 1];
/*      */ 
/*      */     
/* 4508 */     stringVal = getNativeConvertToString(columnIndex, field);
/*      */     
/* 4510 */     if (field.isZeroFill() && stringVal != null) {
/* 4511 */       int origLength = stringVal.length();
/*      */       
/* 4513 */       StringBuffer zeroFillBuf = new StringBuffer(origLength);
/*      */       
/* 4515 */       long numZeros = field.getLength() - origLength;
/*      */       long i;
/* 4517 */       for (i = 0L; i < numZeros; i++) {
/* 4518 */         zeroFillBuf.append('0');
/*      */       }
/*      */       
/* 4521 */       zeroFillBuf.append(stringVal);
/*      */       
/* 4523 */       stringVal = zeroFillBuf.toString();
/*      */     } 
/*      */     
/* 4526 */     return stringVal;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4532 */     checkRowPos();
/* 4533 */     checkColumnBounds(columnIndex);
/*      */     
/* 4535 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 4537 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */     
/* 4539 */     Time timeVal = null;
/*      */     
/* 4541 */     if (mysqlType == 11) {
/* 4542 */       timeVal = this.thisRow.getNativeTime(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */     else {
/*      */       
/* 4546 */       timeVal = (Time)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 92, mysqlType, tz, rollForward, this.connection, this);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4558 */     if (timeVal == null) {
/*      */       
/* 4560 */       this.wasNullFlag = true;
/*      */       
/* 4562 */       return null;
/*      */     } 
/*      */     
/* 4565 */     this.wasNullFlag = false;
/*      */     
/* 4567 */     return timeVal;
/*      */   }
/*      */ 
/*      */   
/*      */   Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4572 */     if (this.useUsageAdvisor) {
/* 4573 */       issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex - 1], new int[] { 11 });
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 4578 */     String strTime = getNativeString(columnIndex);
/*      */     
/* 4580 */     return getTimeFromString(strTime, targetCalendar, columnIndex, tz, rollForward);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4587 */     checkRowPos();
/* 4588 */     checkColumnBounds(columnIndex);
/*      */     
/* 4590 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 4592 */     Timestamp tsVal = null;
/*      */     
/* 4594 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */     
/* 4596 */     switch (mysqlType) {
/*      */       case 7:
/*      */       case 12:
/* 4599 */         tsVal = this.thisRow.getNativeTimestamp(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       default:
/* 4606 */         tsVal = (Timestamp)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 93, mysqlType, tz, rollForward, this.connection, this);
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4618 */     if (tsVal == null) {
/*      */       
/* 4620 */       this.wasNullFlag = true;
/*      */       
/* 4622 */       return null;
/*      */     } 
/*      */     
/* 4625 */     this.wasNullFlag = false;
/*      */     
/* 4627 */     return tsVal;
/*      */   }
/*      */ 
/*      */   
/*      */   Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4632 */     if (this.useUsageAdvisor) {
/* 4633 */       issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex - 1], new int[] { 7, 12 });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4639 */     String strTimestamp = getNativeString(columnIndex);
/*      */     
/* 4641 */     return getTimestampFromString(columnIndex, targetCalendar, strTimestamp, tz, rollForward);
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
/*      */   protected InputStream getNativeUnicodeStream(int columnIndex) throws SQLException {
/* 4668 */     checkRowPos();
/*      */     
/* 4670 */     return getBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected URL getNativeURL(int colIndex) throws SQLException {
/* 4677 */     String val = getString(colIndex);
/*      */     
/* 4679 */     if (val == null) {
/* 4680 */       return null;
/*      */     }
/*      */     
/*      */     try {
/* 4684 */       return new URL(val);
/* 4685 */     } catch (MalformedURLException mfe) {
/* 4686 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", "S1009");
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
/*      */   public ResultSetInternalMethods getNextResultSet() {
/* 4698 */     return this.nextResultSet;
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
/*      */   public Object getObject(int columnIndex) throws SQLException {
/*      */     String stringVal;
/* 4725 */     checkRowPos();
/* 4726 */     checkColumnBounds(columnIndex);
/*      */     
/* 4728 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 4730 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 4731 */       this.wasNullFlag = true;
/*      */       
/* 4733 */       return null;
/*      */     } 
/*      */     
/* 4736 */     this.wasNullFlag = false;
/*      */ 
/*      */     
/* 4739 */     Field field = this.fields[columnIndexMinusOne];
/*      */     
/* 4741 */     switch (field.getSQLType()) {
/*      */       case -7:
/*      */       case 16:
/* 4744 */         if (field.getMysqlType() == 16 && !field.isSingleBit())
/*      */         {
/* 4746 */           return getBytes(columnIndex);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4752 */         return Boolean.valueOf(getBoolean(columnIndex));
/*      */       
/*      */       case -6:
/* 4755 */         if (!field.isUnsigned()) {
/* 4756 */           return Constants.integerValueOf(getByte(columnIndex));
/*      */         }
/*      */         
/* 4759 */         return Constants.integerValueOf(getInt(columnIndex));
/*      */ 
/*      */       
/*      */       case 5:
/* 4763 */         return Constants.integerValueOf(getInt(columnIndex));
/*      */ 
/*      */       
/*      */       case 4:
/* 4767 */         if (!field.isUnsigned() || field.getMysqlType() == 9)
/*      */         {
/* 4769 */           return Constants.integerValueOf(getInt(columnIndex));
/*      */         }
/*      */         
/* 4772 */         return Constants.longValueOf(getLong(columnIndex));
/*      */ 
/*      */       
/*      */       case -5:
/* 4776 */         if (!field.isUnsigned()) {
/* 4777 */           return Constants.longValueOf(getLong(columnIndex));
/*      */         }
/*      */         
/* 4780 */         stringVal = getString(columnIndex);
/*      */         
/* 4782 */         if (stringVal == null) {
/* 4783 */           return null;
/*      */         }
/*      */         
/*      */         try {
/* 4787 */           return new BigInteger(stringVal);
/* 4788 */         } catch (NumberFormatException nfe) {
/* 4789 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Constants.integerValueOf(columnIndex), stringVal }), "S1009");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 2:
/*      */       case 3:
/* 4797 */         stringVal = getString(columnIndex);
/*      */ 
/*      */ 
/*      */         
/* 4801 */         if (stringVal != null) {
/* 4802 */           BigDecimal val; if (stringVal.length() == 0) {
/* 4803 */             val = new BigDecimal(0.0D);
/*      */             
/* 4805 */             return val;
/*      */           } 
/*      */           
/*      */           try {
/* 4809 */             val = new BigDecimal(stringVal);
/* 4810 */           } catch (NumberFormatException ex) {
/* 4811 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009");
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 4818 */           return val;
/*      */         } 
/*      */         
/* 4821 */         return null;
/*      */       
/*      */       case 7:
/* 4824 */         return new Float(getFloat(columnIndex));
/*      */       
/*      */       case 6:
/*      */       case 8:
/* 4828 */         return new Double(getDouble(columnIndex));
/*      */       
/*      */       case 1:
/*      */       case 12:
/* 4832 */         if (!field.isOpaqueBinary()) {
/* 4833 */           return getString(columnIndex);
/*      */         }
/*      */         
/* 4836 */         return getBytes(columnIndex);
/*      */       case -1:
/* 4838 */         if (!field.isOpaqueBinary()) {
/* 4839 */           return getStringForClob(columnIndex);
/*      */         }
/*      */         
/* 4842 */         return getBytes(columnIndex);
/*      */       
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/* 4847 */         if (field.getMysqlType() == 255)
/* 4848 */           return getBytes(columnIndex); 
/* 4849 */         if (field.isBinary() || field.isBlob()) {
/* 4850 */           byte[] data = getBytes(columnIndex);
/*      */           
/* 4852 */           if (this.connection.getAutoDeserialize()) {
/* 4853 */             Object obj = data;
/*      */             
/* 4855 */             if (data != null && data.length >= 2) {
/* 4856 */               if (data[0] == -84 && data[1] == -19) {
/*      */                 
/*      */                 try {
/* 4859 */                   ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */                   
/* 4861 */                   ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */                   
/* 4863 */                   obj = objIn.readObject();
/* 4864 */                   objIn.close();
/* 4865 */                   bytesIn.close();
/* 4866 */                 } catch (ClassNotFoundException cnfe) {
/* 4867 */                   throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"));
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*      */                 }
/* 4873 */                 catch (IOException ex) {
/* 4874 */                   obj = data;
/*      */                 } 
/*      */               } else {
/* 4877 */                 return getString(columnIndex);
/*      */               } 
/*      */             }
/*      */             
/* 4881 */             return obj;
/*      */           } 
/*      */           
/* 4884 */           return data;
/*      */         } 
/*      */         
/* 4887 */         return getBytes(columnIndex);
/*      */       
/*      */       case 91:
/* 4890 */         if (field.getMysqlType() == 13 && !this.connection.getYearIsDateType())
/*      */         {
/* 4892 */           return Constants.shortValueOf(getShort(columnIndex));
/*      */         }
/*      */         
/* 4895 */         return getDate(columnIndex);
/*      */       
/*      */       case 92:
/* 4898 */         return getTime(columnIndex);
/*      */       
/*      */       case 93:
/* 4901 */         return getTimestamp(columnIndex);
/*      */     } 
/*      */     
/* 4904 */     return getString(columnIndex);
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
/*      */   public Object getObject(int i, Map map) throws SQLException {
/* 4924 */     return getObject(i);
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
/*      */   public Object getObject(String columnName) throws SQLException {
/* 4951 */     return getObject(findColumn(columnName));
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
/*      */   public Object getObject(String colName, Map map) throws SQLException {
/* 4971 */     return getObject(findColumn(colName), map);
/*      */   }
/*      */   
/*      */   public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
/*      */     String stringVal;
/* 4976 */     checkRowPos();
/* 4977 */     checkColumnBounds(columnIndex);
/*      */     
/* 4979 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 4981 */     if (value == null) {
/* 4982 */       this.wasNullFlag = true;
/*      */       
/* 4984 */       return null;
/*      */     } 
/*      */     
/* 4987 */     this.wasNullFlag = false;
/*      */ 
/*      */     
/* 4990 */     Field field = this.fields[columnIndex - 1];
/*      */     
/* 4992 */     switch (desiredSqlType) {
/*      */ 
/*      */ 
/*      */       
/*      */       case -7:
/*      */       case 16:
/* 4998 */         return Boolean.valueOf(getBoolean(columnIndex));
/*      */       
/*      */       case -6:
/* 5001 */         return Constants.integerValueOf(getInt(columnIndex));
/*      */       
/*      */       case 5:
/* 5004 */         return Constants.integerValueOf(getInt(columnIndex));
/*      */ 
/*      */       
/*      */       case 4:
/* 5008 */         if (!field.isUnsigned() || field.getMysqlType() == 9)
/*      */         {
/* 5010 */           return Constants.integerValueOf(getInt(columnIndex));
/*      */         }
/*      */         
/* 5013 */         return Constants.longValueOf(getLong(columnIndex));
/*      */ 
/*      */       
/*      */       case -5:
/* 5017 */         if (field.isUnsigned()) {
/* 5018 */           return getBigDecimal(columnIndex);
/*      */         }
/*      */         
/* 5021 */         return Constants.longValueOf(getLong(columnIndex));
/*      */ 
/*      */       
/*      */       case 2:
/*      */       case 3:
/* 5026 */         stringVal = getString(columnIndex);
/*      */ 
/*      */         
/* 5029 */         if (stringVal != null) {
/* 5030 */           BigDecimal val; if (stringVal.length() == 0) {
/* 5031 */             val = new BigDecimal(0.0D);
/*      */             
/* 5033 */             return val;
/*      */           } 
/*      */           
/*      */           try {
/* 5037 */             val = new BigDecimal(stringVal);
/* 5038 */           } catch (NumberFormatException ex) {
/* 5039 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009");
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5046 */           return val;
/*      */         } 
/*      */         
/* 5049 */         return null;
/*      */       
/*      */       case 7:
/* 5052 */         return new Float(getFloat(columnIndex));
/*      */ 
/*      */       
/*      */       case 6:
/* 5056 */         if (!this.connection.getRunningCTS13()) {
/* 5057 */           return new Double(getFloat(columnIndex));
/*      */         }
/* 5059 */         return new Float(getFloat(columnIndex));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 8:
/* 5066 */         return new Double(getDouble(columnIndex));
/*      */       
/*      */       case 1:
/*      */       case 12:
/* 5070 */         return getString(columnIndex);
/*      */       case -1:
/* 5072 */         return getStringForClob(columnIndex);
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/* 5076 */         return getBytes(columnIndex);
/*      */       
/*      */       case 91:
/* 5079 */         if (field.getMysqlType() == 13 && !this.connection.getYearIsDateType())
/*      */         {
/* 5081 */           return Constants.shortValueOf(getShort(columnIndex));
/*      */         }
/*      */         
/* 5084 */         return getDate(columnIndex);
/*      */       
/*      */       case 92:
/* 5087 */         return getTime(columnIndex);
/*      */       
/*      */       case 93:
/* 5090 */         return getTimestamp(columnIndex);
/*      */     } 
/*      */     
/* 5093 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObjectStoredProc(int i, Map map, int desiredSqlType) throws SQLException {
/* 5099 */     return getObjectStoredProc(i, desiredSqlType);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
/* 5104 */     return getObjectStoredProc(findColumn(columnName), desiredSqlType);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getObjectStoredProc(String colName, Map map, int desiredSqlType) throws SQLException {
/* 5109 */     return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
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
/*      */   public Ref getRef(int i) throws SQLException {
/* 5126 */     checkColumnBounds(i);
/* 5127 */     throw SQLError.notImplemented();
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
/*      */   public Ref getRef(String colName) throws SQLException {
/* 5144 */     return getRef(findColumn(colName));
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
/*      */   public int getRow() throws SQLException {
/* 5161 */     checkClosed();
/*      */     
/* 5163 */     int currentRowNumber = this.rowData.getCurrentRowNumber();
/* 5164 */     int row = 0;
/*      */ 
/*      */ 
/*      */     
/* 5168 */     if (!this.rowData.isDynamic()) {
/* 5169 */       if (currentRowNumber < 0 || this.rowData.isAfterLast() || this.rowData.isEmpty()) {
/*      */         
/* 5171 */         row = 0;
/*      */       } else {
/* 5173 */         row = currentRowNumber + 1;
/*      */       } 
/*      */     } else {
/*      */       
/* 5177 */       row = currentRowNumber + 1;
/*      */     } 
/*      */     
/* 5180 */     return row;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getServerInfo() {
/* 5189 */     return this.serverInfo;
/*      */   }
/*      */ 
/*      */   
/*      */   private long getNumericRepresentationOfSQLBitType(int columnIndex) throws SQLException {
/* 5194 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 5196 */     if (this.fields[columnIndex - 1].isSingleBit() || ((byte[])value).length == 1)
/*      */     {
/* 5198 */       return ((byte[])value)[0];
/*      */     }
/*      */ 
/*      */     
/* 5202 */     byte[] asBytes = (byte[])value;
/*      */ 
/*      */     
/* 5205 */     int shift = 0;
/*      */     
/* 5207 */     long[] steps = new long[asBytes.length];
/*      */     
/* 5209 */     for (int i = asBytes.length - 1; i >= 0; i--) {
/* 5210 */       steps[i] = (asBytes[i] & 0xFF) << shift;
/* 5211 */       shift += 8;
/*      */     } 
/*      */     
/* 5214 */     long valueAsLong = 0L;
/*      */     
/* 5216 */     for (int j = 0; j < asBytes.length; j++) {
/* 5217 */       valueAsLong |= steps[j];
/*      */     }
/*      */     
/* 5220 */     return valueAsLong;
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
/*      */   public short getShort(int columnIndex) throws SQLException {
/* 5235 */     if (!this.isBinaryEncoded) {
/* 5236 */       checkRowPos();
/*      */       
/* 5238 */       if (this.useFastIntParsing) {
/*      */         
/* 5240 */         checkColumnBounds(columnIndex);
/*      */         
/* 5242 */         Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */         
/* 5244 */         if (value == null) {
/* 5245 */           this.wasNullFlag = true;
/*      */         } else {
/* 5247 */           this.wasNullFlag = false;
/*      */         } 
/*      */         
/* 5250 */         if (this.wasNullFlag) {
/* 5251 */           return 0;
/*      */         }
/*      */         
/* 5254 */         byte[] shortAsBytes = (byte[])value;
/*      */         
/* 5256 */         if (shortAsBytes.length == 0) {
/* 5257 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 5260 */         boolean needsFullParse = false;
/*      */         
/* 5262 */         for (int i = 0; i < shortAsBytes.length; i++) {
/* 5263 */           if ((char)shortAsBytes[i] == 'e' || (char)shortAsBytes[i] == 'E') {
/*      */             
/* 5265 */             needsFullParse = true;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */         
/* 5271 */         if (!needsFullParse) {
/*      */           try {
/* 5273 */             return parseShortWithOverflowCheck(columnIndex, shortAsBytes, null);
/*      */           }
/* 5275 */           catch (NumberFormatException nfe) {
/*      */             
/*      */             try {
/* 5278 */               return parseShortAsDouble(columnIndex, new String(shortAsBytes));
/*      */             }
/* 5280 */             catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */               
/* 5284 */               if (this.fields[columnIndex - 1].getMysqlType() == 16) {
/* 5285 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */                 
/* 5287 */                 if (this.jdbcCompliantTruncationForReads && (valueAsLong < -32768L || valueAsLong > 32767L))
/*      */                 {
/*      */                   
/* 5290 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */                 }
/*      */ 
/*      */                 
/* 5294 */                 return (short)(int)valueAsLong;
/*      */               } 
/*      */               
/* 5297 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + new String(shortAsBytes) + "'", "S1009");
/*      */             } 
/*      */           } 
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5307 */       String val = null;
/*      */       
/*      */       try {
/* 5310 */         val = getString(columnIndex);
/*      */         
/* 5312 */         if (val != null) {
/*      */           
/* 5314 */           if (val.length() == 0) {
/* 5315 */             return (short)convertToZeroWithEmptyCheck();
/*      */           }
/*      */           
/* 5318 */           if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1)
/*      */           {
/* 5320 */             return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 5325 */           return parseShortAsDouble(columnIndex, val);
/*      */         } 
/*      */         
/* 5328 */         return 0;
/* 5329 */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 5331 */           return parseShortAsDouble(columnIndex, val);
/* 5332 */         } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */           
/* 5336 */           if (this.fields[columnIndex - 1].getMysqlType() == 16) {
/* 5337 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */             
/* 5339 */             if (this.jdbcCompliantTruncationForReads && (valueAsLong < -32768L || valueAsLong > 32767L))
/*      */             {
/*      */               
/* 5342 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */             }
/*      */ 
/*      */             
/* 5346 */             return (short)(int)valueAsLong;
/*      */           } 
/*      */           
/* 5349 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + val + "'", "S1009");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5357 */     return getNativeShort(columnIndex);
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
/*      */   public short getShort(String columnName) throws SQLException {
/* 5372 */     return getShort(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   
/*      */   private final short getShortFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 5378 */       if (val != null) {
/*      */         
/* 5380 */         if (val.length() == 0) {
/* 5381 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 5384 */         if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1)
/*      */         {
/* 5386 */           return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */         }
/*      */ 
/*      */         
/* 5390 */         return parseShortAsDouble(columnIndex, val);
/*      */       } 
/*      */       
/* 5393 */       return 0;
/* 5394 */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 5396 */         return parseShortAsDouble(columnIndex, val);
/* 5397 */       } catch (NumberFormatException newNfe) {
/*      */ 
/*      */ 
/*      */         
/* 5401 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, "S1009");
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
/*      */   public Statement getStatement() throws SQLException {
/* 5420 */     if (this.isClosed && !this.retainOwningStatement) {
/* 5421 */       throw SQLError.createSQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5429 */     if (this.wrapperStatement != null) {
/* 5430 */       return this.wrapperStatement;
/*      */     }
/*      */     
/* 5433 */     return this.owningStatement;
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
/*      */   public String getString(int columnIndex) throws SQLException {
/* 5448 */     String stringVal = getStringInternal(columnIndex, true);
/*      */     
/* 5450 */     if (this.padCharsWithSpace) {
/* 5451 */       Field f = this.fields[columnIndex - 1];
/*      */       
/* 5453 */       if (f.getMysqlType() == 254) {
/* 5454 */         int fieldLength = (int)f.getLength() / f.getMaxBytesPerCharacter();
/*      */ 
/*      */         
/* 5457 */         int currentLength = stringVal.length();
/*      */         
/* 5459 */         if (currentLength < fieldLength) {
/* 5460 */           StringBuffer paddedBuf = new StringBuffer(fieldLength);
/* 5461 */           paddedBuf.append(stringVal);
/*      */           
/* 5463 */           int difference = fieldLength - currentLength;
/*      */           
/* 5465 */           paddedBuf.append(EMPTY_SPACE, 0, difference);
/*      */           
/* 5467 */           stringVal = paddedBuf.toString();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 5472 */     return stringVal;
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
/*      */   public String getString(String columnName) throws SQLException {
/* 5488 */     return getString(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private String getStringForClob(int columnIndex) throws SQLException {
/* 5492 */     String asString = null;
/*      */     
/* 5494 */     String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/*      */     
/* 5497 */     if (forcedEncoding == null) {
/* 5498 */       if (!this.isBinaryEncoded) {
/* 5499 */         asString = getString(columnIndex);
/*      */       } else {
/* 5501 */         asString = getNativeString(columnIndex);
/*      */       } 
/*      */     } else {
/*      */       try {
/* 5505 */         byte[] asBytes = null;
/*      */         
/* 5507 */         if (!this.isBinaryEncoded) {
/* 5508 */           asBytes = getBytes(columnIndex);
/*      */         } else {
/* 5510 */           asBytes = getNativeBytes(columnIndex, true);
/*      */         } 
/*      */         
/* 5513 */         if (asBytes != null) {
/* 5514 */           asString = new String(asBytes, forcedEncoding);
/*      */         }
/* 5516 */       } catch (UnsupportedEncodingException uee) {
/* 5517 */         throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009");
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 5522 */     return asString;
/*      */   }
/*      */ 
/*      */   
/*      */   protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException {
/* 5527 */     if (!this.isBinaryEncoded) {
/* 5528 */       checkRowPos();
/* 5529 */       checkColumnBounds(columnIndex);
/*      */       
/* 5531 */       if (this.fields == null) {
/* 5532 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), "S1002");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5540 */       int internalColumnIndex = columnIndex - 1;
/*      */       
/* 5542 */       if (this.thisRow.isNull(internalColumnIndex)) {
/* 5543 */         this.wasNullFlag = true;
/*      */         
/* 5545 */         return null;
/*      */       } 
/*      */       
/* 5548 */       this.wasNullFlag = false;
/*      */ 
/*      */       
/* 5551 */       Field metadata = this.fields[internalColumnIndex];
/*      */       
/* 5553 */       String stringVal = null;
/*      */       
/* 5555 */       if (metadata.getMysqlType() == 16) {
/* 5556 */         if (metadata.isSingleBit()) {
/* 5557 */           byte[] value = this.thisRow.getColumnValue(internalColumnIndex);
/*      */           
/* 5559 */           if (value.length == 0) {
/* 5560 */             return String.valueOf(convertToZeroWithEmptyCheck());
/*      */           }
/*      */           
/* 5563 */           return String.valueOf(value[0]);
/*      */         } 
/*      */         
/* 5566 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       } 
/*      */       
/* 5569 */       String encoding = metadata.getCharacterSet();
/*      */       
/* 5571 */       stringVal = this.thisRow.getString(internalColumnIndex, encoding, this.connection);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5578 */       if (metadata.getMysqlType() == 13) {
/* 5579 */         if (!this.connection.getYearIsDateType()) {
/* 5580 */           return stringVal;
/*      */         }
/*      */         
/* 5583 */         Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */         
/* 5585 */         if (dt == null) {
/* 5586 */           this.wasNullFlag = true;
/*      */           
/* 5588 */           return null;
/*      */         } 
/*      */         
/* 5591 */         this.wasNullFlag = false;
/*      */         
/* 5593 */         return dt.toString();
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 5598 */       if (checkDateTypes && !this.connection.getNoDatetimeStringSync()) {
/* 5599 */         Time tm; Date dt; Timestamp ts; switch (metadata.getSQLType()) {
/*      */           case 92:
/* 5601 */             tm = getTimeFromString(stringVal, null, columnIndex, getDefaultTimeZone(), false);
/*      */ 
/*      */             
/* 5604 */             if (tm == null) {
/* 5605 */               this.wasNullFlag = true;
/*      */               
/* 5607 */               return null;
/*      */             } 
/*      */             
/* 5610 */             this.wasNullFlag = false;
/*      */             
/* 5612 */             return tm.toString();
/*      */           
/*      */           case 91:
/* 5615 */             dt = getDateFromString(stringVal, columnIndex, null);
/*      */             
/* 5617 */             if (dt == null) {
/* 5618 */               this.wasNullFlag = true;
/*      */               
/* 5620 */               return null;
/*      */             } 
/*      */             
/* 5623 */             this.wasNullFlag = false;
/*      */             
/* 5625 */             return dt.toString();
/*      */           case 93:
/* 5627 */             ts = getTimestampFromString(columnIndex, null, stringVal, getDefaultTimeZone(), false);
/*      */ 
/*      */             
/* 5630 */             if (ts == null) {
/* 5631 */               this.wasNullFlag = true;
/*      */               
/* 5633 */               return null;
/*      */             } 
/*      */             
/* 5636 */             this.wasNullFlag = false;
/*      */             
/* 5638 */             return ts.toString();
/*      */         } 
/*      */ 
/*      */ 
/*      */       
/*      */       } 
/* 5644 */       return stringVal;
/*      */     } 
/*      */     
/* 5647 */     return getNativeString(columnIndex);
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
/*      */   public Time getTime(int columnIndex) throws SQLException {
/* 5662 */     return getTimeInternal(columnIndex, null, getDefaultTimeZone(), false);
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
/*      */   public Time getTime(int columnIndex, Calendar cal) throws SQLException {
/* 5682 */     return getTimeInternal(columnIndex, cal, cal.getTimeZone(), true);
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
/*      */   public Time getTime(String columnName) throws SQLException {
/* 5697 */     return getTime(findColumn(columnName));
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
/*      */   public Time getTime(String columnName, Calendar cal) throws SQLException {
/* 5717 */     return getTime(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward) throws SQLException {
/* 5724 */     int hr = 0;
/* 5725 */     int min = 0;
/* 5726 */     int sec = 0;
/*      */ 
/*      */     
/*      */     try {
/* 5730 */       if (timeAsString == null) {
/* 5731 */         this.wasNullFlag = true;
/*      */         
/* 5733 */         return null;
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
/* 5744 */       timeAsString = timeAsString.trim();
/*      */       
/* 5746 */       if (timeAsString.equals("0") || timeAsString.equals("0000-00-00") || timeAsString.equals("0000-00-00 00:00:00") || timeAsString.equals("00000000000000")) {
/*      */ 
/*      */ 
/*      */         
/* 5750 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/*      */           
/* 5752 */           this.wasNullFlag = true;
/*      */           
/* 5754 */           return null;
/* 5755 */         }  if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5757 */           throw SQLError.createSQLException("Value '" + timeAsString + "' can not be represented as java.sql.Time", "S1009");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5764 */         return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */       } 
/*      */       
/* 5767 */       this.wasNullFlag = false;
/*      */       
/* 5769 */       Field timeColField = this.fields[columnIndex - 1];
/*      */       
/* 5771 */       if (timeColField.getMysqlType() == 7)
/*      */       
/* 5773 */       { int length = timeAsString.length();
/*      */         
/* 5775 */         switch (length) {
/*      */           
/*      */           case 19:
/* 5778 */             hr = Integer.parseInt(timeAsString.substring(length - 8, length - 6));
/*      */             
/* 5780 */             min = Integer.parseInt(timeAsString.substring(length - 5, length - 3));
/*      */             
/* 5782 */             sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 12:
/*      */           case 14:
/* 5789 */             hr = Integer.parseInt(timeAsString.substring(length - 6, length - 4));
/*      */             
/* 5791 */             min = Integer.parseInt(timeAsString.substring(length - 4, length - 2));
/*      */             
/* 5793 */             sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 10:
/* 5800 */             hr = Integer.parseInt(timeAsString.substring(6, 8));
/* 5801 */             min = Integer.parseInt(timeAsString.substring(8, 10));
/* 5802 */             sec = 0;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/* 5808 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[columnIndex - 1] + ").", "S1009");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5817 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[columnIndex - 1] + ").");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5824 */         if (this.warningChain == null) {
/* 5825 */           this.warningChain = precisionLost;
/*      */         } else {
/* 5827 */           this.warningChain.setNextWarning(precisionLost);
/*      */         }  }
/* 5829 */       else if (timeColField.getMysqlType() == 12)
/* 5830 */       { hr = Integer.parseInt(timeAsString.substring(11, 13));
/* 5831 */         min = Integer.parseInt(timeAsString.substring(14, 16));
/* 5832 */         sec = Integer.parseInt(timeAsString.substring(17, 19));
/*      */         
/* 5834 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[columnIndex - 1] + ").");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5841 */         if (this.warningChain == null) {
/* 5842 */           this.warningChain = precisionLost;
/*      */         } else {
/* 5844 */           this.warningChain.setNextWarning(precisionLost);
/*      */         }  }
/* 5846 */       else { if (timeColField.getMysqlType() == 10) {
/* 5847 */           return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */         }
/*      */ 
/*      */         
/* 5851 */         if (timeAsString.length() != 5 && timeAsString.length() != 8)
/*      */         {
/* 5853 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString("ResultSet.___in_column__268") + columnIndex, "S1009");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5860 */         hr = Integer.parseInt(timeAsString.substring(0, 2));
/* 5861 */         min = Integer.parseInt(timeAsString.substring(3, 5));
/* 5862 */         sec = (timeAsString.length() == 5) ? 0 : Integer.parseInt(timeAsString.substring(6)); }
/*      */ 
/*      */ 
/*      */       
/* 5866 */       Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */       
/* 5868 */       synchronized (sessionCalendar) {
/* 5869 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimeCreate(sessionCalendar, hr, min, sec), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 5877 */     catch (Exception ex) {
/* 5878 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009");
/*      */       
/* 5880 */       sqlEx.initCause(ex);
/*      */       
/* 5882 */       throw sqlEx;
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
/*      */   private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 5903 */     if (this.isBinaryEncoded) {
/* 5904 */       return getNativeTime(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */     
/* 5907 */     if (!this.useFastDateParsing) {
/* 5908 */       String timeAsString = getStringInternal(columnIndex, false);
/*      */       
/* 5910 */       return getTimeFromString(timeAsString, targetCalendar, columnIndex, tz, rollForward);
/*      */     } 
/*      */ 
/*      */     
/* 5914 */     checkColumnBounds(columnIndex);
/*      */     
/* 5916 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 5918 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 5919 */       this.wasNullFlag = true;
/*      */       
/* 5921 */       return null;
/*      */     } 
/*      */     
/* 5924 */     this.wasNullFlag = false;
/*      */     
/* 5926 */     return this.thisRow.getTimeFast(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
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
/*      */   public Timestamp getTimestamp(int columnIndex) throws SQLException {
/* 5943 */     return getTimestampInternal(columnIndex, null, getDefaultTimeZone(), false);
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
/*      */   public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
/* 5965 */     return getTimestampInternal(columnIndex, cal, cal.getTimeZone(), true);
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
/*      */   public Timestamp getTimestamp(String columnName) throws SQLException {
/* 5981 */     return getTimestamp(findColumn(columnName));
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
/*      */   public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
/* 6002 */     return getTimestamp(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Timestamp getTimestampFromString(int columnIndex, Calendar targetCalendar, String timestampValue, TimeZone tz, boolean rollForward) throws SQLException {
/*      */     try {
/* 6010 */       this.wasNullFlag = false;
/*      */       
/* 6012 */       if (timestampValue == null) {
/* 6013 */         this.wasNullFlag = true;
/*      */         
/* 6015 */         return null;
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
/* 6026 */       timestampValue = timestampValue.trim();
/*      */       
/* 6028 */       int length = timestampValue.length();
/*      */       
/* 6030 */       Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */       
/* 6034 */       synchronized (sessionCalendar) {
/* 6035 */         if (length > 0 && timestampValue.charAt(0) == '0' && (timestampValue.equals("0000-00-00") || timestampValue.equals("0000-00-00 00:00:00") || timestampValue.equals("00000000000000") || timestampValue.equals("0"))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 6042 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/*      */             
/* 6044 */             this.wasNullFlag = true;
/*      */             
/* 6046 */             return null;
/* 6047 */           }  if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6049 */             throw SQLError.createSQLException("Value '" + timestampValue + "' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 6056 */           return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */         } 
/* 6058 */         if (this.fields[columnIndex - 1].getMysqlType() == 13) {
/*      */           
/* 6060 */           if (!this.useLegacyDatetimeCode) {
/* 6061 */             return TimeUtil.fastTimestampCreate(tz, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/*      */           
/* 6065 */           return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 6075 */         if (timestampValue.endsWith(".")) {
/* 6076 */           timestampValue = timestampValue.substring(0, timestampValue.length() - 1);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 6082 */         int year = 0;
/* 6083 */         int month = 0;
/* 6084 */         int day = 0;
/* 6085 */         int hour = 0;
/* 6086 */         int minutes = 0;
/* 6087 */         int seconds = 0;
/* 6088 */         int nanos = 0;
/*      */         
/* 6090 */         switch (length) {
/*      */           case 19:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*      */           case 23:
/*      */           case 24:
/*      */           case 25:
/*      */           case 26:
/* 6099 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6100 */             month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */             
/* 6102 */             day = Integer.parseInt(timestampValue.substring(8, 10));
/* 6103 */             hour = Integer.parseInt(timestampValue.substring(11, 13));
/*      */             
/* 6105 */             minutes = Integer.parseInt(timestampValue.substring(14, 16));
/*      */             
/* 6107 */             seconds = Integer.parseInt(timestampValue.substring(17, 19));
/*      */ 
/*      */             
/* 6110 */             nanos = 0;
/*      */             
/* 6112 */             if (length > 19) {
/* 6113 */               int decimalIndex = timestampValue.lastIndexOf('.');
/*      */               
/* 6115 */               if (decimalIndex != -1) {
/* 6116 */                 if (decimalIndex + 2 <= timestampValue.length()) {
/* 6117 */                   nanos = Integer.parseInt(timestampValue.substring(decimalIndex + 1));
/*      */                   break;
/*      */                 } 
/* 6120 */                 throw new IllegalArgumentException();
/*      */               } 
/*      */             } 
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 14:
/* 6134 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6135 */             month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */             
/* 6137 */             day = Integer.parseInt(timestampValue.substring(6, 8));
/* 6138 */             hour = Integer.parseInt(timestampValue.substring(8, 10));
/*      */             
/* 6140 */             minutes = Integer.parseInt(timestampValue.substring(10, 12));
/*      */             
/* 6142 */             seconds = Integer.parseInt(timestampValue.substring(12, 14));
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 12:
/* 6149 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */             
/* 6151 */             if (year <= 69) {
/* 6152 */               year += 100;
/*      */             }
/*      */             
/* 6155 */             year += 1900;
/*      */             
/* 6157 */             month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */             
/* 6159 */             day = Integer.parseInt(timestampValue.substring(4, 6));
/* 6160 */             hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 6161 */             minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */             
/* 6163 */             seconds = Integer.parseInt(timestampValue.substring(10, 12));
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 10:
/* 6170 */             if (this.fields[columnIndex - 1].getMysqlType() == 10 || timestampValue.indexOf("-") != -1) {
/*      */               
/* 6172 */               year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6173 */               month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */               
/* 6175 */               day = Integer.parseInt(timestampValue.substring(8, 10));
/* 6176 */               hour = 0;
/* 6177 */               minutes = 0; break;
/*      */             } 
/* 6179 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */             
/* 6181 */             if (year <= 69) {
/* 6182 */               year += 100;
/*      */             }
/*      */             
/* 6185 */             month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */             
/* 6187 */             day = Integer.parseInt(timestampValue.substring(4, 6));
/* 6188 */             hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 6189 */             minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/*      */             
/* 6192 */             year += 1900;
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 8:
/* 6199 */             if (timestampValue.indexOf(":") != -1) {
/* 6200 */               hour = Integer.parseInt(timestampValue.substring(0, 2));
/*      */               
/* 6202 */               minutes = Integer.parseInt(timestampValue.substring(3, 5));
/*      */               
/* 6204 */               seconds = Integer.parseInt(timestampValue.substring(6, 8));
/*      */               
/* 6206 */               year = 1970;
/* 6207 */               month = 1;
/* 6208 */               day = 1;
/*      */               
/*      */               break;
/*      */             } 
/* 6212 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6213 */             month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */             
/* 6215 */             day = Integer.parseInt(timestampValue.substring(6, 8));
/*      */             
/* 6217 */             year -= 1900;
/* 6218 */             month--;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 6:
/* 6224 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */             
/* 6226 */             if (year <= 69) {
/* 6227 */               year += 100;
/*      */             }
/*      */             
/* 6230 */             year += 1900;
/*      */             
/* 6232 */             month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */             
/* 6234 */             day = Integer.parseInt(timestampValue.substring(4, 6));
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 4:
/* 6240 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */             
/* 6242 */             if (year <= 69) {
/* 6243 */               year += 100;
/*      */             }
/*      */             
/* 6246 */             year += 1900;
/*      */             
/* 6248 */             month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/*      */             
/* 6251 */             day = 1;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 2:
/* 6257 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */             
/* 6259 */             if (year <= 69) {
/* 6260 */               year += 100;
/*      */             }
/*      */             
/* 6263 */             year += 1900;
/* 6264 */             month = 1;
/* 6265 */             day = 1;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/* 6271 */             throw new SQLException("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + ".", "S1009");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 6277 */         if (!this.useLegacyDatetimeCode) {
/* 6278 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/*      */         
/* 6282 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 6290 */     catch (Exception e) {
/* 6291 */       SQLException sqlEx = SQLError.createSQLException("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP.", "S1009");
/*      */ 
/*      */       
/* 6294 */       sqlEx.initCause(e);
/*      */       
/* 6296 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Timestamp getTimestampFromBytes(int columnIndex, Calendar targetCalendar, byte[] timestampAsBytes, TimeZone tz, boolean rollForward) throws SQLException {
/* 6305 */     checkColumnBounds(columnIndex);
/*      */     
/*      */     try {
/* 6308 */       this.wasNullFlag = false;
/*      */       
/* 6310 */       if (timestampAsBytes == null) {
/* 6311 */         this.wasNullFlag = true;
/*      */         
/* 6313 */         return null;
/*      */       } 
/*      */       
/* 6316 */       int length = timestampAsBytes.length;
/*      */       
/* 6318 */       Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */       
/* 6322 */       synchronized (sessionCalendar) {
/* 6323 */         boolean allZeroTimestamp = true;
/*      */         
/* 6325 */         boolean onlyTimePresent = (StringUtils.indexOf(timestampAsBytes, ':') != -1);
/*      */         
/* 6327 */         for (int i = 0; i < length; i++) {
/* 6328 */           byte b = timestampAsBytes[i];
/*      */           
/* 6330 */           if (b == 32 || b == 45 || b == 47) {
/* 6331 */             onlyTimePresent = false;
/*      */           }
/*      */           
/* 6334 */           if (b != 48 && b != 32 && b != 58 && b != 45 && b != 47 && b != 46) {
/*      */             
/* 6336 */             allZeroTimestamp = false;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */         
/* 6342 */         if (!onlyTimePresent && allZeroTimestamp) {
/*      */           
/* 6344 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/*      */             
/* 6346 */             this.wasNullFlag = true;
/*      */             
/* 6348 */             return null;
/* 6349 */           }  if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6351 */             throw SQLError.createSQLException("Value '" + timestampAsBytes + "' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 6358 */           if (!this.useLegacyDatetimeCode) {
/* 6359 */             return TimeUtil.fastTimestampCreate(tz, 1, 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/*      */           
/* 6363 */           return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/* 6364 */         }  if (this.fields[columnIndex - 1].getMysqlType() == 13) {
/*      */           
/* 6366 */           if (!this.useLegacyDatetimeCode) {
/* 6367 */             return TimeUtil.fastTimestampCreate(tz, StringUtils.getInt(timestampAsBytes, 0, 4), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/*      */           
/* 6371 */           return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, StringUtils.getInt(timestampAsBytes, 0, 4), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 6379 */         if (timestampAsBytes[length - 1] == 46) {
/* 6380 */           length--;
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 6385 */         int year = 0;
/* 6386 */         int month = 0;
/* 6387 */         int day = 0;
/* 6388 */         int hour = 0;
/* 6389 */         int minutes = 0;
/* 6390 */         int seconds = 0;
/* 6391 */         int nanos = 0;
/*      */         
/* 6393 */         switch (length) {
/*      */           case 19:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*      */           case 23:
/*      */           case 24:
/*      */           case 25:
/*      */           case 26:
/* 6402 */             year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6403 */             month = StringUtils.getInt(timestampAsBytes, 5, 7);
/* 6404 */             day = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6405 */             hour = StringUtils.getInt(timestampAsBytes, 11, 13);
/* 6406 */             minutes = StringUtils.getInt(timestampAsBytes, 14, 16);
/* 6407 */             seconds = StringUtils.getInt(timestampAsBytes, 17, 19);
/*      */             
/* 6409 */             nanos = 0;
/*      */             
/* 6411 */             if (length > 19) {
/* 6412 */               int decimalIndex = StringUtils.lastIndexOf(timestampAsBytes, '.');
/*      */               
/* 6414 */               if (decimalIndex != -1) {
/* 6415 */                 if (decimalIndex + 2 <= length) {
/* 6416 */                   nanos = StringUtils.getInt(timestampAsBytes, decimalIndex + 1, length); break;
/*      */                 } 
/* 6418 */                 throw new IllegalArgumentException();
/*      */               } 
/*      */             } 
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 14:
/* 6432 */             year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6433 */             month = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6434 */             day = StringUtils.getInt(timestampAsBytes, 6, 8);
/* 6435 */             hour = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6436 */             minutes = StringUtils.getInt(timestampAsBytes, 10, 12);
/* 6437 */             seconds = StringUtils.getInt(timestampAsBytes, 12, 14);
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 12:
/* 6443 */             year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */             
/* 6445 */             if (year <= 69) {
/* 6446 */               year += 100;
/*      */             }
/*      */             
/* 6449 */             year += 1900;
/*      */             
/* 6451 */             month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6452 */             day = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6453 */             hour = StringUtils.getInt(timestampAsBytes, 6, 8);
/* 6454 */             minutes = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6455 */             seconds = StringUtils.getInt(timestampAsBytes, 10, 12);
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 10:
/* 6461 */             if (this.fields[columnIndex - 1].getMysqlType() == 10 || StringUtils.indexOf(timestampAsBytes, '-') != -1) {
/*      */               
/* 6463 */               year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6464 */               month = StringUtils.getInt(timestampAsBytes, 5, 7);
/* 6465 */               day = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6466 */               hour = 0;
/* 6467 */               minutes = 0; break;
/*      */             } 
/* 6469 */             year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */             
/* 6471 */             if (year <= 69) {
/* 6472 */               year += 100;
/*      */             }
/*      */             
/* 6475 */             month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6476 */             day = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6477 */             hour = StringUtils.getInt(timestampAsBytes, 6, 8);
/* 6478 */             minutes = StringUtils.getInt(timestampAsBytes, 8, 10);
/*      */             
/* 6480 */             year += 1900;
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 8:
/* 6487 */             if (StringUtils.indexOf(timestampAsBytes, ':') != -1) {
/* 6488 */               hour = StringUtils.getInt(timestampAsBytes, 0, 2);
/* 6489 */               minutes = StringUtils.getInt(timestampAsBytes, 3, 5);
/* 6490 */               seconds = StringUtils.getInt(timestampAsBytes, 6, 8);
/*      */               
/* 6492 */               year = 1970;
/* 6493 */               month = 1;
/* 6494 */               day = 1;
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 6499 */             year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6500 */             month = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6501 */             day = StringUtils.getInt(timestampAsBytes, 6, 8);
/*      */             
/* 6503 */             year -= 1900;
/* 6504 */             month--;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 6:
/* 6510 */             year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */             
/* 6512 */             if (year <= 69) {
/* 6513 */               year += 100;
/*      */             }
/*      */             
/* 6516 */             year += 1900;
/*      */             
/* 6518 */             month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6519 */             day = StringUtils.getInt(timestampAsBytes, 4, 6);
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 4:
/* 6525 */             year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */             
/* 6527 */             if (year <= 69) {
/* 6528 */               year += 100;
/*      */             }
/*      */             
/* 6531 */             year += 1900;
/*      */             
/* 6533 */             month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6534 */             day = 1;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 2:
/* 6540 */             year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */             
/* 6542 */             if (year <= 69) {
/* 6543 */               year += 100;
/*      */             }
/*      */             
/* 6546 */             year += 1900;
/* 6547 */             month = 1;
/* 6548 */             day = 1;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/* 6554 */             throw new SQLException("Bad format for Timestamp '" + new String(timestampAsBytes) + "' in column " + columnIndex + ".", "S1009");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 6560 */         if (!this.useLegacyDatetimeCode) {
/* 6561 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/*      */         
/* 6565 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 6573 */     catch (Exception e) {
/* 6574 */       SQLException sqlEx = SQLError.createSQLException("Cannot convert value '" + new String(timestampAsBytes) + "' from column " + columnIndex + " to TIMESTAMP.", "S1009");
/*      */ 
/*      */       
/* 6577 */       sqlEx.initCause(e);
/*      */       
/* 6579 */       throw sqlEx;
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
/*      */   private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 6600 */     if (this.isBinaryEncoded) {
/* 6601 */       return getNativeTimestamp(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */     
/* 6604 */     Timestamp tsVal = null;
/*      */     
/* 6606 */     if (!this.useFastDateParsing) {
/* 6607 */       String timestampValue = getStringInternal(columnIndex, false);
/*      */       
/* 6609 */       tsVal = getTimestampFromString(columnIndex, targetCalendar, timestampValue, tz, rollForward);
/*      */     }
/*      */     else {
/*      */       
/* 6613 */       checkClosed();
/* 6614 */       checkRowPos();
/* 6615 */       checkColumnBounds(columnIndex);
/*      */       
/* 6617 */       tsVal = this.thisRow.getTimestampFast(columnIndex - 1, targetCalendar, tz, rollForward, this.connection, this);
/*      */     } 
/*      */ 
/*      */     
/* 6621 */     if (tsVal == null) {
/* 6622 */       this.wasNullFlag = true;
/*      */     } else {
/* 6624 */       this.wasNullFlag = false;
/*      */     } 
/*      */     
/* 6627 */     return tsVal;
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
/*      */   public int getType() throws SQLException {
/* 6641 */     return this.resultSetType;
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
/*      */   public InputStream getUnicodeStream(int columnIndex) throws SQLException {
/* 6663 */     if (!this.isBinaryEncoded) {
/* 6664 */       checkRowPos();
/*      */       
/* 6666 */       return getBinaryStream(columnIndex);
/*      */     } 
/*      */     
/* 6669 */     return getNativeBinaryStream(columnIndex);
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
/*      */   public InputStream getUnicodeStream(String columnName) throws SQLException {
/* 6686 */     return getUnicodeStream(findColumn(columnName));
/*      */   }
/*      */   
/*      */   public long getUpdateCount() {
/* 6690 */     return this.updateCount;
/*      */   }
/*      */   
/*      */   public long getUpdateID() {
/* 6694 */     return this.updateId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(int colIndex) throws SQLException {
/* 6701 */     String val = getString(colIndex);
/*      */     
/* 6703 */     if (val == null) {
/* 6704 */       return null;
/*      */     }
/*      */     
/*      */     try {
/* 6708 */       return new URL(val);
/* 6709 */     } catch (MalformedURLException mfe) {
/* 6710 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(String colName) throws SQLException {
/* 6720 */     String val = getString(colName);
/*      */     
/* 6722 */     if (val == null) {
/* 6723 */       return null;
/*      */     }
/*      */     
/*      */     try {
/* 6727 */       return new URL(val);
/* 6728 */     } catch (MalformedURLException mfe) {
/* 6729 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009");
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
/*      */   public SQLWarning getWarnings() throws SQLException {
/* 6756 */     return this.warningChain;
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
/*      */   public void insertRow() throws SQLException {
/* 6771 */     throw new NotUpdatable();
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
/*      */   public boolean isAfterLast() throws SQLException {
/* 6788 */     checkClosed();
/*      */     
/* 6790 */     boolean b = this.rowData.isAfterLast();
/*      */     
/* 6792 */     return b;
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
/*      */   public boolean isBeforeFirst() throws SQLException {
/* 6809 */     checkClosed();
/*      */     
/* 6811 */     return this.rowData.isBeforeFirst();
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
/*      */   public boolean isFirst() throws SQLException {
/* 6827 */     checkClosed();
/*      */     
/* 6829 */     return this.rowData.isFirst();
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
/*      */   public boolean isLast() throws SQLException {
/* 6848 */     checkClosed();
/*      */     
/* 6850 */     return this.rowData.isLast();
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
/*      */   private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion) throws SQLException {
/* 6862 */     StringBuffer originalQueryBuf = new StringBuffer();
/*      */     
/* 6864 */     if (this.owningStatement != null && this.owningStatement instanceof PreparedStatement) {
/*      */       
/* 6866 */       originalQueryBuf.append(Messages.getString("ResultSet.CostlyConversionCreatedFromQuery"));
/* 6867 */       originalQueryBuf.append(((PreparedStatement)this.owningStatement).originalSql);
/*      */       
/* 6869 */       originalQueryBuf.append("\n\n");
/*      */     } else {
/* 6871 */       originalQueryBuf.append(".");
/*      */     } 
/*      */     
/* 6874 */     StringBuffer convertibleTypesBuf = new StringBuffer();
/*      */     
/* 6876 */     for (int i = 0; i < typesWithNoParseConversion.length; i++) {
/* 6877 */       convertibleTypesBuf.append(MysqlDefs.typeToName(typesWithNoParseConversion[i]));
/* 6878 */       convertibleTypesBuf.append("\n");
/*      */     } 
/*      */     
/* 6881 */     String message = Messages.getString("ResultSet.CostlyConversion", new Object[] { methodName, new Integer(columnIndex + 1), fieldInfo.getOriginalName(), fieldInfo.getOriginalTableName(), originalQueryBuf.toString(), (value != null) ? value.getClass().getName() : ResultSetMetaData.getClassNameForJavaType(fieldInfo.getSQLType(), fieldInfo.isUnsigned(), fieldInfo.getMysqlType(), (fieldInfo.isBinary() || fieldInfo.isBlob()), fieldInfo.isOpaqueBinary()), MysqlDefs.typeToName(fieldInfo.getMysqlType()), convertibleTypesBuf.toString() });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6896 */     this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owningStatement == null) ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, (this.owningStatement == null) ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
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
/*      */   public boolean last() throws SQLException {
/* 6920 */     checkClosed();
/*      */     
/* 6922 */     boolean b = true;
/*      */     
/* 6924 */     if (this.rowData.size() == 0) {
/* 6925 */       b = false;
/*      */     } else {
/*      */       
/* 6928 */       if (this.onInsertRow) {
/* 6929 */         this.onInsertRow = false;
/*      */       }
/*      */       
/* 6932 */       if (this.doingUpdates) {
/* 6933 */         this.doingUpdates = false;
/*      */       }
/*      */       
/* 6936 */       if (this.thisRow != null) {
/* 6937 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */       
/* 6940 */       this.rowData.beforeLast();
/* 6941 */       this.thisRow = this.rowData.next();
/*      */     } 
/*      */     
/* 6944 */     setRowPositionValidity();
/*      */     
/* 6946 */     return b;
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
/*      */   public void moveToCurrentRow() throws SQLException {
/* 6968 */     throw new NotUpdatable();
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
/*      */   public void moveToInsertRow() throws SQLException {
/* 6989 */     throw new NotUpdatable();
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
/*      */   public boolean next() throws SQLException {
/*      */     boolean b;
/* 7008 */     checkClosed();
/*      */     
/* 7010 */     if (this.onInsertRow) {
/* 7011 */       this.onInsertRow = false;
/*      */     }
/*      */     
/* 7014 */     if (this.doingUpdates) {
/* 7015 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 7020 */     if (!reallyResult()) {
/* 7021 */       throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7027 */     if (this.thisRow != null) {
/* 7028 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */     
/* 7031 */     if (this.rowData.size() == 0) {
/* 7032 */       b = false;
/*      */     } else {
/* 7034 */       this.thisRow = this.rowData.next();
/*      */       
/* 7036 */       if (this.thisRow == null) {
/* 7037 */         b = false;
/*      */       } else {
/* 7039 */         clearWarnings();
/*      */         
/* 7041 */         b = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 7046 */     setRowPositionValidity();
/*      */     
/* 7048 */     return b;
/*      */   }
/*      */ 
/*      */   
/*      */   private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
/* 7053 */     if (val == null) {
/* 7054 */       return 0;
/*      */     }
/*      */     
/* 7057 */     double valueAsDouble = Double.parseDouble(val);
/*      */     
/* 7059 */     if (this.jdbcCompliantTruncationForReads && (
/* 7060 */       valueAsDouble < -2.147483648E9D || valueAsDouble > 2.147483647E9D))
/*      */     {
/* 7062 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 7067 */     return (int)valueAsDouble;
/*      */   }
/*      */   
/*      */   private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
/* 7071 */     int intValue = this.thisRow.getInt(columnIndex);
/*      */     
/* 7073 */     checkForIntegerTruncation(columnIndex + 1, null, this.thisRow.getString(columnIndex, this.fields[columnIndex].getCharacterSet(), this.connection), intValue);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7078 */     return intValue;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, String valueAsString, int intValue) throws SQLException {
/* 7084 */     if (this.jdbcCompliantTruncationForReads && (
/* 7085 */       intValue == Integer.MIN_VALUE || intValue == Integer.MAX_VALUE)) {
/* 7086 */       long valueAsLong = Long.parseLong((valueAsString == null) ? new String(valueAsBytes) : valueAsString);
/*      */ 
/*      */ 
/*      */       
/* 7090 */       if (valueAsLong < -2147483648L || valueAsLong > 2147483647L)
/*      */       {
/* 7092 */         throwRangeException((valueAsString == null) ? new String(valueAsBytes) : valueAsString, columnIndex, 4);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long parseLongAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
/* 7102 */     if (val == null) {
/* 7103 */       return 0L;
/*      */     }
/*      */     
/* 7106 */     double valueAsDouble = Double.parseDouble(val);
/*      */     
/* 7108 */     if (this.jdbcCompliantTruncationForReads && (
/* 7109 */       valueAsDouble < -9.223372036854776E18D || valueAsDouble > 9.223372036854776E18D))
/*      */     {
/* 7111 */       throwRangeException(val, columnIndex, -5);
/*      */     }
/*      */ 
/*      */     
/* 7115 */     return (long)valueAsDouble;
/*      */   }
/*      */   
/*      */   private long getLongWithOverflowCheck(int columnIndex, boolean doOverflowCheck) throws SQLException {
/* 7119 */     long longValue = this.thisRow.getLong(columnIndex);
/*      */     
/* 7121 */     if (doOverflowCheck) {
/* 7122 */       checkForLongTruncation(columnIndex + 1, null, this.thisRow.getString(columnIndex, this.fields[columnIndex].getCharacterSet(), this.connection), longValue);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7128 */     return longValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long parseLongWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString, boolean doCheck) throws NumberFormatException, SQLException {
/* 7135 */     long longValue = 0L;
/*      */     
/* 7137 */     if (valueAsBytes == null && valueAsString == null) {
/* 7138 */       return 0L;
/*      */     }
/*      */     
/* 7141 */     if (valueAsBytes != null) {
/* 7142 */       longValue = StringUtils.getLong(valueAsBytes);
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 7152 */       valueAsString = valueAsString.trim();
/*      */       
/* 7154 */       longValue = Long.parseLong(valueAsString);
/*      */     } 
/*      */     
/* 7157 */     if (doCheck && this.jdbcCompliantTruncationForReads) {
/* 7158 */       checkForLongTruncation(columnIndex, valueAsBytes, valueAsString, longValue);
/*      */     }
/*      */ 
/*      */     
/* 7162 */     return longValue;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkForLongTruncation(int columnIndex, byte[] valueAsBytes, String valueAsString, long longValue) throws SQLException {
/* 7167 */     if (longValue == Long.MIN_VALUE || longValue == Long.MAX_VALUE) {
/*      */       
/* 7169 */       double valueAsDouble = Double.parseDouble((valueAsString == null) ? new String(valueAsBytes) : valueAsString);
/*      */ 
/*      */ 
/*      */       
/* 7173 */       if (valueAsDouble < -9.223372036854776E18D || valueAsDouble > 9.223372036854776E18D)
/*      */       {
/* 7175 */         throwRangeException((valueAsString == null) ? new String(valueAsBytes) : valueAsString, columnIndex, -5);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private short parseShortAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
/* 7184 */     if (val == null) {
/* 7185 */       return 0;
/*      */     }
/*      */     
/* 7188 */     double valueAsDouble = Double.parseDouble(val);
/*      */     
/* 7190 */     if (this.jdbcCompliantTruncationForReads && (
/* 7191 */       valueAsDouble < -32768.0D || valueAsDouble > 32767.0D))
/*      */     {
/* 7193 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 7198 */     return (short)(int)valueAsDouble;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString) throws NumberFormatException, SQLException {
/* 7205 */     short shortValue = 0;
/*      */     
/* 7207 */     if (valueAsBytes == null && valueAsString == null) {
/* 7208 */       return 0;
/*      */     }
/*      */     
/* 7211 */     if (valueAsBytes != null) {
/* 7212 */       shortValue = StringUtils.getShort(valueAsBytes);
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 7222 */       valueAsString = valueAsString.trim();
/*      */       
/* 7224 */       shortValue = Short.parseShort(valueAsString);
/*      */     } 
/*      */     
/* 7227 */     if (this.jdbcCompliantTruncationForReads && (
/* 7228 */       shortValue == Short.MIN_VALUE || shortValue == Short.MAX_VALUE)) {
/* 7229 */       long valueAsLong = Long.parseLong((valueAsString == null) ? new String(valueAsBytes) : valueAsString);
/*      */ 
/*      */ 
/*      */       
/* 7233 */       if (valueAsLong < -32768L || valueAsLong > 32767L)
/*      */       {
/* 7235 */         throwRangeException((valueAsString == null) ? new String(valueAsBytes) : valueAsString, columnIndex, 5);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7242 */     return shortValue;
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
/*      */   public boolean prev() throws SQLException {
/* 7266 */     checkClosed();
/*      */     
/* 7268 */     int rowIndex = this.rowData.getCurrentRowNumber();
/*      */     
/* 7270 */     if (this.thisRow != null) {
/* 7271 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */     
/* 7274 */     boolean b = true;
/*      */     
/* 7276 */     if (rowIndex - 1 >= 0) {
/* 7277 */       rowIndex--;
/* 7278 */       this.rowData.setCurrentRow(rowIndex);
/* 7279 */       this.thisRow = this.rowData.getAt(rowIndex);
/*      */       
/* 7281 */       b = true;
/* 7282 */     } else if (rowIndex - 1 == -1) {
/* 7283 */       rowIndex--;
/* 7284 */       this.rowData.setCurrentRow(rowIndex);
/* 7285 */       this.thisRow = null;
/*      */       
/* 7287 */       b = false;
/*      */     } else {
/* 7289 */       b = false;
/*      */     } 
/*      */     
/* 7292 */     setRowPositionValidity();
/*      */     
/* 7294 */     return b;
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
/*      */   public boolean previous() throws SQLException {
/* 7316 */     if (this.onInsertRow) {
/* 7317 */       this.onInsertRow = false;
/*      */     }
/*      */     
/* 7320 */     if (this.doingUpdates) {
/* 7321 */       this.doingUpdates = false;
/*      */     }
/*      */     
/* 7324 */     return prev();
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
/*      */   public void realClose(boolean calledExplicitly) throws SQLException {
/* 7337 */     if (this.isClosed) {
/*      */       return;
/*      */     }
/*      */     
/*      */     try {
/* 7342 */       if (this.useUsageAdvisor)
/*      */       {
/*      */ 
/*      */         
/* 7346 */         if (!calledExplicitly) {
/* 7347 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owningStatement == null) ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, (this.owningStatement == null) ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver")));
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 7366 */         if (this.rowData instanceof RowDataStatic) {
/*      */ 
/*      */ 
/*      */           
/* 7370 */           if (this.rowData.size() > this.connection.getResultSetSizeThreshold())
/*      */           {
/* 7372 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owningStatement == null) ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, (this.owningStatement == null) ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] { new Integer(this.rowData.size()), new Integer(this.connection.getResultSetSizeThreshold()) })));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 7400 */           if (!isLast() && !isAfterLast() && this.rowData.size() != 0)
/*      */           {
/* 7402 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owningStatement == null) ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, (this.owningStatement == null) ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] { new Integer(getRow()), new Integer(this.rowData.size()) })));
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 7435 */         if (this.columnUsed.length > 0 && !this.rowData.wasEmpty()) {
/* 7436 */           StringBuffer buf = new StringBuffer(Messages.getString("ResultSet.The_following_columns_were_never_referenced"));
/*      */ 
/*      */ 
/*      */           
/* 7440 */           boolean issueWarn = false;
/*      */           
/* 7442 */           for (int i = 0; i < this.columnUsed.length; i++) {
/* 7443 */             if (!this.columnUsed[i]) {
/* 7444 */               if (!issueWarn) {
/* 7445 */                 issueWarn = true;
/*      */               } else {
/* 7447 */                 buf.append(", ");
/*      */               } 
/*      */               
/* 7450 */               buf.append(this.fields[i].getFullName());
/*      */             } 
/*      */           } 
/*      */           
/* 7454 */           if (issueWarn) {
/* 7455 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owningStatement == null) ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, (this.owningStatement == null) ? -1 : this.owningStatement.getId(), 0, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, buf.toString()));
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */ 
/*      */       
/* 7469 */       SQLException exceptionDuringClose = null;
/*      */       
/* 7471 */       if (this.rowData != null) {
/*      */         try {
/* 7473 */           this.rowData.close();
/* 7474 */         } catch (SQLException sqlEx) {
/* 7475 */           exceptionDuringClose = sqlEx;
/*      */         } 
/*      */       }
/*      */       
/* 7479 */       if (this.statementUsedForFetchingRows != null) {
/*      */         try {
/* 7481 */           this.statementUsedForFetchingRows.realClose(true, false);
/* 7482 */         } catch (SQLException sqlEx) {
/* 7483 */           if (exceptionDuringClose != null) {
/* 7484 */             exceptionDuringClose.setNextException(sqlEx);
/*      */           } else {
/* 7486 */             exceptionDuringClose = sqlEx;
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/* 7491 */       this.rowData = null;
/* 7492 */       this.defaultTimeZone = null;
/* 7493 */       this.fields = null;
/* 7494 */       this.columnNameToIndex = null;
/* 7495 */       this.fullColumnNameToIndex = null;
/* 7496 */       this.eventSink = null;
/* 7497 */       this.warningChain = null;
/*      */       
/* 7499 */       if (!this.retainOwningStatement) {
/* 7500 */         this.owningStatement = null;
/*      */       }
/*      */       
/* 7503 */       this.catalog = null;
/* 7504 */       this.serverInfo = null;
/* 7505 */       this.thisRow = null;
/* 7506 */       this.fastDateCal = null;
/* 7507 */       this.connection = null;
/*      */       
/* 7509 */       this.isClosed = true;
/*      */       
/* 7511 */       if (exceptionDuringClose != null) {
/* 7512 */         throw exceptionDuringClose;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean reallyResult() {
/* 7518 */     if (this.rowData != null) {
/* 7519 */       return true;
/*      */     }
/*      */     
/* 7522 */     return this.reallyResult;
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
/*      */   public void refreshRow() throws SQLException {
/* 7546 */     throw new NotUpdatable();
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
/*      */   public boolean relative(int rows) throws SQLException {
/* 7576 */     checkClosed();
/*      */     
/* 7578 */     if (this.rowData.size() == 0) {
/* 7579 */       setRowPositionValidity();
/*      */       
/* 7581 */       return false;
/*      */     } 
/*      */     
/* 7584 */     if (this.thisRow != null) {
/* 7585 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */     
/* 7588 */     this.rowData.moveRowRelative(rows);
/* 7589 */     this.thisRow = this.rowData.getAt(this.rowData.getCurrentRowNumber());
/*      */     
/* 7591 */     setRowPositionValidity();
/*      */     
/* 7593 */     return (!this.rowData.isAfterLast() && !this.rowData.isBeforeFirst());
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
/*      */   public boolean rowDeleted() throws SQLException {
/* 7612 */     throw SQLError.notImplemented();
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
/*      */   public boolean rowInserted() throws SQLException {
/* 7630 */     throw SQLError.notImplemented();
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
/*      */   public boolean rowUpdated() throws SQLException {
/* 7648 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setBinaryEncoded() {
/* 7656 */     this.isBinaryEncoded = true;
/*      */   }
/*      */   
/*      */   private void setDefaultTimeZone(TimeZone defaultTimeZone) {
/* 7660 */     this.defaultTimeZone = defaultTimeZone;
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
/*      */   public void setFetchDirection(int direction) throws SQLException {
/* 7679 */     if (direction != 1000 && direction != 1001 && direction != 1002)
/*      */     {
/* 7681 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7687 */     this.fetchDirection = direction;
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
/*      */   public void setFetchSize(int rows) throws SQLException {
/* 7707 */     if (rows < 0) {
/* 7708 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7714 */     this.fetchSize = rows;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFirstCharOfQuery(char c) {
/* 7725 */     this.firstCharOfQuery = c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setNextResultSet(ResultSetInternalMethods nextResultSet) {
/* 7736 */     this.nextResultSet = nextResultSet;
/*      */   }
/*      */   
/*      */   public void setOwningStatement(StatementImpl owningStatement) {
/* 7740 */     this.owningStatement = owningStatement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setResultSetConcurrency(int concurrencyFlag) {
/* 7750 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setResultSetType(int typeFlag) {
/* 7761 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setServerInfo(String info) {
/* 7771 */     this.serverInfo = info;
/*      */   }
/*      */   
/*      */   public void setStatementUsedForFetchingRows(PreparedStatement stmt) {
/* 7775 */     this.statementUsedForFetchingRows = stmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWrapperStatement(Statement wrapperStatement) {
/* 7783 */     this.wrapperStatement = wrapperStatement;
/*      */   }
/*      */ 
/*      */   
/*      */   private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException {
/* 7788 */     String datatype = null;
/*      */     
/* 7790 */     switch (jdbcType)
/*      */     { case -6:
/* 7792 */         datatype = "TINYINT";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 7819 */         throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case 5: datatype = "SMALLINT"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case 4: datatype = "INTEGER"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case -5: datatype = "BIGINT"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case 7: datatype = "REAL"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case 6: datatype = "FLOAT"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case 8: datatype = "DOUBLE"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");case 3: datatype = "DECIMAL"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003"); }  datatype = " (JDBC type '" + jdbcType + "')"; throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 7830 */     if (this.reallyResult) {
/* 7831 */       return super.toString();
/*      */     }
/*      */     
/* 7834 */     return "Result set representing update count of " + this.updateCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateArray(int arg0, Array arg1) throws SQLException {
/* 7841 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateArray(String arg0, Array arg1) throws SQLException {
/* 7848 */     throw SQLError.notImplemented();
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
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
/* 7872 */     throw new NotUpdatable();
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
/*      */   public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
/* 7894 */     updateAsciiStream(findColumn(columnName), x, length);
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
/*      */   public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
/* 7915 */     throw new NotUpdatable();
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
/*      */   public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
/* 7934 */     updateBigDecimal(findColumn(columnName), x);
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
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
/* 7958 */     throw new NotUpdatable();
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
/*      */   public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
/* 7980 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(int arg0, Blob arg1) throws SQLException {
/* 7987 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(String arg0, Blob arg1) throws SQLException {
/* 7994 */     throw new NotUpdatable();
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
/*      */   public void updateBoolean(int columnIndex, boolean x) throws SQLException {
/* 8014 */     throw new NotUpdatable();
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
/*      */   public void updateBoolean(String columnName, boolean x) throws SQLException {
/* 8032 */     updateBoolean(findColumn(columnName), x);
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
/*      */   public void updateByte(int columnIndex, byte x) throws SQLException {
/* 8052 */     throw new NotUpdatable();
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
/*      */   public void updateByte(String columnName, byte x) throws SQLException {
/* 8070 */     updateByte(findColumn(columnName), x);
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
/*      */   public void updateBytes(int columnIndex, byte[] x) throws SQLException {
/* 8090 */     throw new NotUpdatable();
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
/*      */   public void updateBytes(String columnName, byte[] x) throws SQLException {
/* 8108 */     updateBytes(findColumn(columnName), x);
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
/*      */   public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
/* 8132 */     throw new NotUpdatable();
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
/*      */   public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
/* 8154 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(int arg0, Clob arg1) throws SQLException {
/* 8161 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(String columnName, Clob clob) throws SQLException {
/* 8169 */     updateClob(findColumn(columnName), clob);
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
/*      */   public void updateDate(int columnIndex, Date x) throws SQLException {
/* 8190 */     throw new NotUpdatable();
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
/*      */   public void updateDate(String columnName, Date x) throws SQLException {
/* 8209 */     updateDate(findColumn(columnName), x);
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
/*      */   public void updateDouble(int columnIndex, double x) throws SQLException {
/* 8229 */     throw new NotUpdatable();
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
/*      */   public void updateDouble(String columnName, double x) throws SQLException {
/* 8247 */     updateDouble(findColumn(columnName), x);
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
/*      */   public void updateFloat(int columnIndex, float x) throws SQLException {
/* 8267 */     throw new NotUpdatable();
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
/*      */   public void updateFloat(String columnName, float x) throws SQLException {
/* 8285 */     updateFloat(findColumn(columnName), x);
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
/*      */   public void updateInt(int columnIndex, int x) throws SQLException {
/* 8305 */     throw new NotUpdatable();
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
/*      */   public void updateInt(String columnName, int x) throws SQLException {
/* 8323 */     updateInt(findColumn(columnName), x);
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
/*      */   public void updateLong(int columnIndex, long x) throws SQLException {
/* 8343 */     throw new NotUpdatable();
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
/*      */   public void updateLong(String columnName, long x) throws SQLException {
/* 8361 */     updateLong(findColumn(columnName), x);
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
/*      */   public void updateNull(int columnIndex) throws SQLException {
/* 8379 */     throw new NotUpdatable();
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
/*      */   public void updateNull(String columnName) throws SQLException {
/* 8395 */     updateNull(findColumn(columnName));
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
/*      */   public void updateObject(int columnIndex, Object x) throws SQLException {
/* 8415 */     throw new NotUpdatable();
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
/*      */   public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
/* 8440 */     throw new NotUpdatable();
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
/*      */   public void updateObject(String columnName, Object x) throws SQLException {
/* 8458 */     updateObject(findColumn(columnName), x);
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
/*      */   public void updateObject(String columnName, Object x, int scale) throws SQLException {
/* 8481 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRef(int arg0, Ref arg1) throws SQLException {
/* 8488 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRef(String arg0, Ref arg1) throws SQLException {
/* 8495 */     throw SQLError.notImplemented();
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
/*      */   public void updateRow() throws SQLException {
/* 8509 */     throw new NotUpdatable();
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
/*      */   public void updateShort(int columnIndex, short x) throws SQLException {
/* 8529 */     throw new NotUpdatable();
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
/*      */   public void updateShort(String columnName, short x) throws SQLException {
/* 8547 */     updateShort(findColumn(columnName), x);
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
/*      */   public void updateString(int columnIndex, String x) throws SQLException {
/* 8567 */     throw new NotUpdatable();
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
/*      */   public void updateString(String columnName, String x) throws SQLException {
/* 8585 */     updateString(findColumn(columnName), x);
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
/*      */   public void updateTime(int columnIndex, Time x) throws SQLException {
/* 8606 */     throw new NotUpdatable();
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
/*      */   public void updateTime(String columnName, Time x) throws SQLException {
/* 8625 */     updateTime(findColumn(columnName), x);
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
/*      */   public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
/* 8647 */     throw new NotUpdatable();
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
/*      */   public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
/* 8666 */     updateTimestamp(findColumn(columnName), x);
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
/*      */   public boolean wasNull() throws SQLException {
/* 8681 */     return this.wasNullFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Calendar getGmtCalendar() {
/* 8688 */     if (this.gmtCalendar == null) {
/* 8689 */       this.gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */     
/* 8692 */     return this.gmtCalendar;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ResultSetImpl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */