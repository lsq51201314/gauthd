/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogFactory;
/*      */ import com.mysql.jdbc.log.NullLogger;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import com.mysql.jdbc.util.LRUCache;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
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
/*      */ public class ConnectionImpl
/*      */   extends ConnectionPropertiesImpl
/*      */   implements Connection
/*      */ {
/*      */   private static final String JDBC_LOCAL_CHARACTER_SET_RESULTS = "jdbc.local.character_set_results";
/*      */   
/*      */   class CompoundCacheKey
/*      */   {
/*      */     String componentOne;
/*      */     String componentTwo;
/*      */     int hashCode;
/*      */     private final ConnectionImpl this$0;
/*      */     
/*      */     CompoundCacheKey(ConnectionImpl this$0, String partOne, String partTwo) {
/*   90 */       this.this$0 = this$0;
/*   91 */       this.componentOne = partOne;
/*   92 */       this.componentTwo = partTwo;
/*      */ 
/*      */ 
/*      */       
/*   96 */       this.hashCode = (((this.componentOne != null) ? this.componentOne : "") + this.componentTwo).hashCode();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  106 */       if (obj instanceof CompoundCacheKey) {
/*  107 */         CompoundCacheKey another = (CompoundCacheKey)obj;
/*      */         
/*  109 */         boolean firstPartEqual = false;
/*      */         
/*  111 */         if (this.componentOne == null) {
/*  112 */           firstPartEqual = (another.componentOne == null);
/*      */         } else {
/*  114 */           firstPartEqual = this.componentOne.equals(another.componentOne);
/*      */         } 
/*      */ 
/*      */         
/*  118 */         return (firstPartEqual && this.componentTwo.equals(another.componentTwo));
/*      */       } 
/*      */ 
/*      */       
/*  122 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  131 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  139 */   private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map charsetMap;
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int HISTOGRAM_BUCKETS = 20;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String LOGGER_INSTANCE_NAME = "MySQL";
/*      */ 
/*      */ 
/*      */   
/*  159 */   private static Map mapTransIsolationNameToValue = null;
/*      */ 
/*      */   
/*  162 */   private static final Log NULL_LOGGER = (Log)new NullLogger("MySQL");
/*      */   
/*      */   private static Map roundRobinStatsMap;
/*      */   
/*  166 */   private static final Map serverCollationByUrl = new HashMap();
/*      */   
/*  168 */   private static final Map serverConfigByUrl = new HashMap();
/*      */   
/*      */   private long queryTimeCount;
/*      */   
/*      */   private double queryTimeSum;
/*      */   
/*      */   private double queryTimeSumSquares;
/*      */   
/*      */   private double queryTimeMean;
/*      */   private static Timer cancelTimer;
/*      */   private List connectionLifecycleInterceptors;
/*      */   private static final Constructor JDBC_4_CONNECTION_CTOR;
/*      */   
/*      */   static {
/*  182 */     mapTransIsolationNameToValue = new HashMap(8);
/*  183 */     mapTransIsolationNameToValue.put("READ-UNCOMMITED", Constants.integerValueOf(1));
/*      */     
/*  185 */     mapTransIsolationNameToValue.put("READ-UNCOMMITTED", Constants.integerValueOf(1));
/*      */     
/*  187 */     mapTransIsolationNameToValue.put("READ-COMMITTED", Constants.integerValueOf(2));
/*      */     
/*  189 */     mapTransIsolationNameToValue.put("REPEATABLE-READ", Constants.integerValueOf(4));
/*      */     
/*  191 */     mapTransIsolationNameToValue.put("SERIALIZABLE", Constants.integerValueOf(8));
/*      */ 
/*      */     
/*  194 */     boolean createdNamedTimer = false;
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  199 */       Constructor ctr = Timer.class.getConstructor(new Class[] { String.class, boolean.class });
/*      */       
/*  201 */       cancelTimer = ctr.newInstance(new Object[] { "MySQL Statement Cancellation Timer", Boolean.TRUE });
/*  202 */       createdNamedTimer = true;
/*  203 */     } catch (Throwable t) {
/*  204 */       createdNamedTimer = false;
/*      */     } 
/*      */     
/*  207 */     if (!createdNamedTimer) {
/*  208 */       cancelTimer = new Timer(true);
/*      */     }
/*      */     
/*  211 */     if (Util.isJdbc4()) {
/*      */       try {
/*  213 */         JDBC_4_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4Connection").getConstructor(new Class[] { String.class, int.class, Properties.class, String.class, String.class });
/*      */ 
/*      */       
/*      */       }
/*  217 */       catch (SecurityException e) {
/*  218 */         throw new RuntimeException(e);
/*  219 */       } catch (NoSuchMethodException e) {
/*  220 */         throw new RuntimeException(e);
/*  221 */       } catch (ClassNotFoundException e) {
/*  222 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*  225 */       JDBC_4_CONNECTION_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend) {
/*  231 */     String origMessage = sqlEx.getMessage();
/*  232 */     String sqlState = sqlEx.getSQLState();
/*  233 */     int vendorErrorCode = sqlEx.getErrorCode();
/*      */     
/*  235 */     StringBuffer messageBuf = new StringBuffer(origMessage.length() + messageToAppend.length());
/*      */     
/*  237 */     messageBuf.append(origMessage);
/*  238 */     messageBuf.append(messageToAppend);
/*      */     
/*  240 */     SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(messageBuf.toString(), sqlState, vendorErrorCode);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  250 */       Method getStackTraceMethod = null;
/*  251 */       Method setStackTraceMethod = null;
/*  252 */       Object theStackTraceAsObject = null;
/*      */       
/*  254 */       Class stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
/*      */       
/*  256 */       Class stackTraceElementArrayClass = Array.newInstance(stackTraceElementClass, new int[] { 0 }).getClass();
/*      */ 
/*      */       
/*  259 */       getStackTraceMethod = Throwable.class.getMethod("getStackTrace", new Class[0]);
/*      */ 
/*      */       
/*  262 */       setStackTraceMethod = Throwable.class.getMethod("setStackTrace", new Class[] { stackTraceElementArrayClass });
/*      */ 
/*      */       
/*  265 */       if (getStackTraceMethod != null && setStackTraceMethod != null) {
/*  266 */         theStackTraceAsObject = getStackTraceMethod.invoke(sqlEx, new Object[0]);
/*      */         
/*  268 */         setStackTraceMethod.invoke(sqlExceptionWithNewMessage, new Object[] { theStackTraceAsObject });
/*      */       }
/*      */     
/*  271 */     } catch (NoClassDefFoundError noClassDefFound) {
/*      */     
/*  273 */     } catch (NoSuchMethodException noSuchMethodEx) {
/*      */     
/*  275 */     } catch (Throwable catchAll) {}
/*      */ 
/*      */ 
/*      */     
/*  279 */     return sqlExceptionWithNewMessage;
/*      */   }
/*      */   
/*      */   protected static Timer getCancelTimer() {
/*  283 */     return cancelTimer;
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
/*      */   protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
/*  297 */     if (!Util.isJdbc4()) {
/*  298 */       return new ConnectionImpl(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*      */     }
/*      */ 
/*      */     
/*  302 */     return (Connection)Util.handleNewInstance(JDBC_4_CONNECTION_CTOR, new Object[] { hostToConnectTo, Constants.integerValueOf(portToConnectTo), info, databaseToConnectTo, url });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static synchronized int getNextRoundRobinHostIndex(String url, List hostList) {
/*  313 */     int indexRange = hostList.size() - 1;
/*      */     
/*  315 */     int index = (int)(Math.random() * indexRange);
/*      */     
/*  317 */     return index;
/*      */   }
/*      */   
/*      */   private static boolean nullSafeCompare(String s1, String s2) {
/*  321 */     if (s1 == null && s2 == null) {
/*  322 */       return true;
/*      */     }
/*      */     
/*  325 */     if (s1 == null && s2 != null) {
/*  326 */       return false;
/*      */     }
/*      */     
/*  329 */     return s1.equals(s2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean autoCommit = true;
/*      */ 
/*      */   
/*      */   private Map cachedPreparedStatementParams;
/*      */ 
/*      */   
/*  341 */   private String characterSetMetadata = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  347 */   private String characterSetResultsOnServer = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  354 */   private Map charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Map charsetToNumBytesMap;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  364 */   private long connectionCreationTimeMillis = 0L;
/*      */ 
/*      */   
/*      */   private long connectionId;
/*      */ 
/*      */   
/*  370 */   private String database = null;
/*      */ 
/*      */   
/*  373 */   private DatabaseMetaData dbmd = null;
/*      */ 
/*      */   
/*      */   private TimeZone defaultTimeZone;
/*      */ 
/*      */   
/*      */   private ProfilerEventHandler eventSink;
/*      */ 
/*      */   
/*      */   private boolean executingFailoverReconnect = false;
/*      */ 
/*      */   
/*      */   private boolean failedOver = false;
/*      */ 
/*      */   
/*      */   private Throwable forceClosedReason;
/*      */ 
/*      */   
/*      */   private Throwable forcedClosedLocation;
/*      */ 
/*      */   
/*      */   private boolean hasIsolationLevels = false;
/*      */   
/*      */   private boolean hasQuotedIdentifiers = false;
/*      */   
/*  398 */   private String host = null;
/*      */ 
/*      */   
/*  401 */   private List hostList = null;
/*      */ 
/*      */   
/*  404 */   private int hostListSize = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  410 */   private String[] indexToCharsetMapping = CharsetMapping.INDEX_TO_CHARSET;
/*      */ 
/*      */   
/*  413 */   private MysqlIO io = null;
/*      */ 
/*      */   
/*      */   private boolean isClientTzUTC = false;
/*      */ 
/*      */   
/*      */   private boolean isClosed = true;
/*      */ 
/*      */   
/*      */   private boolean isInGlobalTx = false;
/*      */ 
/*      */   
/*      */   private boolean isRunningOnJDK13 = false;
/*      */   
/*  427 */   private int isolationLevel = 2;
/*      */ 
/*      */   
/*      */   private boolean isServerTzUTC = false;
/*      */   
/*  432 */   private long lastQueryFinishedTime = 0L;
/*      */ 
/*      */   
/*  435 */   private Log log = NULL_LOGGER;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  441 */   private long longestQueryTimeMs = 0L;
/*      */ 
/*      */   
/*      */   private boolean lowerCaseTableNames = false;
/*      */ 
/*      */   
/*  447 */   private long masterFailTimeMillis = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  453 */   private int maxAllowedPacket = 65536;
/*      */   
/*  455 */   private long maximumNumberTablesAccessed = 0L;
/*      */ 
/*      */   
/*      */   private boolean maxRowsChanged = false;
/*      */ 
/*      */   
/*      */   private long metricsLastReportedMs;
/*      */   
/*  463 */   private long minimumNumberTablesAccessed = Long.MAX_VALUE;
/*      */ 
/*      */   
/*  466 */   private final Object mutex = new Object();
/*      */ 
/*      */   
/*  469 */   private String myURL = null;
/*      */ 
/*      */   
/*      */   private boolean needsPing = false;
/*      */   
/*  474 */   private int netBufferLength = 16384;
/*      */   
/*      */   private boolean noBackslashEscapes = false;
/*      */   
/*  478 */   private long numberOfPreparedExecutes = 0L;
/*      */   
/*  480 */   private long numberOfPrepares = 0L;
/*      */   
/*  482 */   private long numberOfQueriesIssued = 0L;
/*      */   
/*  484 */   private long numberOfResultSetsCreated = 0L;
/*      */   
/*      */   private long[] numTablesMetricsHistBreakpoints;
/*      */   
/*      */   private int[] numTablesMetricsHistCounts;
/*      */   
/*  490 */   private long[] oldHistBreakpoints = null;
/*      */   
/*  492 */   private int[] oldHistCounts = null;
/*      */ 
/*      */   
/*      */   private Map openStatements;
/*      */ 
/*      */   
/*      */   private LRUCache parsedCallableStatementCache;
/*      */   
/*      */   private boolean parserKnowsUnicode = false;
/*      */   
/*  502 */   private String password = null;
/*      */ 
/*      */   
/*      */   private long[] perfMetricsHistBreakpoints;
/*      */ 
/*      */   
/*      */   private int[] perfMetricsHistCounts;
/*      */   
/*      */   private Throwable pointOfOrigin;
/*      */   
/*  512 */   private int port = 3306;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean preferSlaveDuringFailover = false;
/*      */ 
/*      */ 
/*      */   
/*  521 */   protected Properties props = null;
/*      */ 
/*      */   
/*  524 */   private long queriesIssuedFailedOver = 0L;
/*      */ 
/*      */   
/*      */   private boolean readInfoMsg = false;
/*      */ 
/*      */   
/*      */   private boolean readOnly = false;
/*      */ 
/*      */   
/*      */   protected LRUCache resultSetMetadataCache;
/*      */ 
/*      */   
/*  536 */   private TimeZone serverTimezoneTZ = null;
/*      */ 
/*      */   
/*  539 */   private Map serverVariables = null;
/*      */   
/*  541 */   private long shortestQueryTimeMs = Long.MAX_VALUE;
/*      */ 
/*      */   
/*      */   private Map statementsUsingMaxRows;
/*      */   
/*  546 */   private double totalQueryTimeMs = 0.0D;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean transactionsSupported = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private Map typeMap;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean useAnsiQuotes = false;
/*      */ 
/*      */   
/*  561 */   private String user = null;
/*      */ 
/*      */   
/*      */   private boolean useServerPreparedStmts = false;
/*      */ 
/*      */   
/*      */   private LRUCache serverSideStatementCheckCache;
/*      */ 
/*      */   
/*      */   private LRUCache serverSideStatementCache;
/*      */ 
/*      */   
/*      */   private Calendar sessionCalendar;
/*      */   
/*      */   private Calendar utcCalendar;
/*      */   
/*      */   private String origHostToConnectTo;
/*      */   
/*      */   private int origPortToConnectTo;
/*      */   
/*      */   private String origDatabaseToConnectTo;
/*      */   
/*  583 */   private String errorMessageEncoding = "Cp1252";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean usePlatformCharsetConverters;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasTriedMasterFlag = false;
/*      */ 
/*      */ 
/*      */   
/*  596 */   private String statementComment = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean usingCachedConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound) {
/*  762 */     if (histogramCounts == null) {
/*  763 */       createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
/*      */     } else {
/*      */       
/*  766 */       for (int i = 0; i < 20; i++) {
/*  767 */         if (histogramBreakpoints[i] >= value) {
/*  768 */           histogramCounts[i] = histogramCounts[i] + numberOfTimes;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void addToPerformanceHistogram(long value, int numberOfTimes) {
/*  777 */     checkAndCreatePerformanceHistogram();
/*      */     
/*  779 */     addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, (this.shortestQueryTimeMs == Long.MAX_VALUE) ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addToTablesAccessedHistogram(long value, int numberOfTimes) {
/*  786 */     checkAndCreateTablesAccessedHistogram();
/*      */     
/*  788 */     addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, (this.minimumNumberTablesAccessed == Long.MAX_VALUE) ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
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
/*      */   private void buildCollationMapping() throws SQLException {
/*  803 */     if (versionMeetsMinimum(4, 1, 0)) {
/*      */       
/*  805 */       TreeMap sortedCollationMap = null;
/*      */       
/*  807 */       if (getCacheServerConfiguration()) {
/*  808 */         synchronized (serverConfigByUrl) {
/*  809 */           sortedCollationMap = (TreeMap)serverCollationByUrl.get(getURL());
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/*  814 */       Statement stmt = null;
/*  815 */       ResultSet results = null;
/*      */       
/*      */       try {
/*  818 */         if (sortedCollationMap == null) {
/*  819 */           sortedCollationMap = new TreeMap();
/*      */           
/*  821 */           stmt = getMetadataSafeStatement();
/*      */           
/*  823 */           results = stmt.executeQuery("SHOW COLLATION");
/*      */ 
/*      */           
/*  826 */           while (results.next()) {
/*  827 */             String charsetName = results.getString(2);
/*  828 */             Integer charsetIndex = Constants.integerValueOf(results.getInt(3));
/*      */             
/*  830 */             sortedCollationMap.put(charsetIndex, charsetName);
/*      */           } 
/*      */           
/*  833 */           if (getCacheServerConfiguration()) {
/*  834 */             synchronized (serverConfigByUrl) {
/*  835 */               serverCollationByUrl.put(getURL(), sortedCollationMap);
/*      */             } 
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  843 */         int highestIndex = ((Integer)sortedCollationMap.lastKey()).intValue();
/*      */ 
/*      */         
/*  846 */         if (CharsetMapping.INDEX_TO_CHARSET.length > highestIndex) {
/*  847 */           highestIndex = CharsetMapping.INDEX_TO_CHARSET.length;
/*      */         }
/*      */         
/*  850 */         this.indexToCharsetMapping = new String[highestIndex + 1];
/*      */         
/*  852 */         for (int i = 0; i < CharsetMapping.INDEX_TO_CHARSET.length; i++) {
/*  853 */           this.indexToCharsetMapping[i] = CharsetMapping.INDEX_TO_CHARSET[i];
/*      */         }
/*      */         
/*  856 */         Iterator indexIter = sortedCollationMap.entrySet().iterator();
/*  857 */         while (indexIter.hasNext()) {
/*  858 */           Map.Entry indexEntry = indexIter.next();
/*      */           
/*  860 */           String mysqlCharsetName = (String)indexEntry.getValue();
/*      */           
/*  862 */           this.indexToCharsetMapping[((Integer)indexEntry.getKey()).intValue()] = CharsetMapping.getJavaEncodingForMysqlEncoding(mysqlCharsetName, this);
/*      */         
/*      */         }
/*      */       
/*      */       }
/*  867 */       catch (SQLException e) {
/*  868 */         throw e;
/*      */       } finally {
/*  870 */         if (results != null) {
/*      */           try {
/*  872 */             results.close();
/*  873 */           } catch (SQLException sqlE) {}
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  878 */         if (stmt != null) {
/*      */           try {
/*  880 */             stmt.close();
/*  881 */           } catch (SQLException sqlE) {}
/*      */         
/*      */         }
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  889 */       this.indexToCharsetMapping = CharsetMapping.INDEX_TO_CHARSET;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException {
/*  895 */     if (sql == null || sql.length() == 0) {
/*  896 */       return true;
/*      */     }
/*      */     
/*  899 */     if (!this.useServerPreparedStmts) {
/*  900 */       return false;
/*      */     }
/*      */     
/*  903 */     if (getCachePreparedStatements()) {
/*  904 */       synchronized (this.serverSideStatementCheckCache) {
/*  905 */         Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
/*      */         
/*  907 */         if (flag != null) {
/*  908 */           return flag.booleanValue();
/*      */         }
/*      */         
/*  911 */         boolean canHandle = canHandleAsServerPreparedStatementNoCache(sql);
/*      */         
/*  913 */         if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/*  914 */           this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE);
/*      */         }
/*      */ 
/*      */         
/*  918 */         return canHandle;
/*      */       } 
/*      */     }
/*      */     
/*  922 */     return canHandleAsServerPreparedStatementNoCache(sql);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean canHandleAsServerPreparedStatementNoCache(String sql) throws SQLException {
/*  929 */     if (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL")) {
/*  930 */       return false;
/*      */     }
/*      */     
/*  933 */     boolean canHandleAsStatement = true;
/*      */     
/*  935 */     if (!versionMeetsMinimum(5, 0, 7) && (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "SELECT") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "DELETE") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "INSERT") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "UPDATE") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "REPLACE"))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  953 */       int currentPos = 0;
/*  954 */       int statementLength = sql.length();
/*  955 */       int lastPosToLook = statementLength - 7;
/*  956 */       boolean allowBackslashEscapes = !this.noBackslashEscapes;
/*  957 */       char quoteChar = this.useAnsiQuotes ? '"' : '\'';
/*  958 */       boolean foundLimitWithPlaceholder = false;
/*      */       
/*  960 */       while (currentPos < lastPosToLook) {
/*  961 */         int limitStart = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, sql, "LIMIT ", quoteChar, allowBackslashEscapes);
/*      */ 
/*      */ 
/*      */         
/*  965 */         if (limitStart == -1) {
/*      */           break;
/*      */         }
/*      */         
/*  969 */         currentPos = limitStart + 7;
/*      */         
/*  971 */         while (currentPos < statementLength) {
/*  972 */           char c = sql.charAt(currentPos);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  979 */           if (!Character.isDigit(c) && !Character.isWhitespace(c) && c != ',' && c != '?') {
/*      */             break;
/*      */           }
/*      */ 
/*      */           
/*  984 */           if (c == '?') {
/*  985 */             foundLimitWithPlaceholder = true;
/*      */             
/*      */             break;
/*      */           } 
/*  989 */           currentPos++;
/*      */         } 
/*      */       } 
/*      */       
/*  993 */       canHandleAsStatement = !foundLimitWithPlaceholder;
/*  994 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
/*  995 */       canHandleAsStatement = false;
/*  996 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "DO")) {
/*  997 */       canHandleAsStatement = false;
/*  998 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "SET")) {
/*  999 */       canHandleAsStatement = false;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1004 */     return canHandleAsStatement;
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
/*      */   public void changeUser(String userName, String newPassword) throws SQLException {
/* 1022 */     if (userName == null || userName.equals("")) {
/* 1023 */       userName = "";
/*      */     }
/*      */     
/* 1026 */     if (newPassword == null) {
/* 1027 */       newPassword = "";
/*      */     }
/*      */     
/* 1030 */     this.io.changeUser(userName, newPassword, this.database);
/* 1031 */     this.user = userName;
/* 1032 */     this.password = newPassword;
/*      */     
/* 1034 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 1035 */       configureClientCharacterSet(true);
/*      */     }
/*      */     
/* 1038 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean characterSetNamesMatches(String mysqlEncodingName) {
/* 1045 */     return (mysqlEncodingName != null && mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_client")) && mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_connection")));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkAndCreatePerformanceHistogram() {
/* 1051 */     if (this.perfMetricsHistCounts == null) {
/* 1052 */       this.perfMetricsHistCounts = new int[20];
/*      */     }
/*      */     
/* 1055 */     if (this.perfMetricsHistBreakpoints == null) {
/* 1056 */       this.perfMetricsHistBreakpoints = new long[20];
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkAndCreateTablesAccessedHistogram() {
/* 1061 */     if (this.numTablesMetricsHistCounts == null) {
/* 1062 */       this.numTablesMetricsHistCounts = new int[20];
/*      */     }
/*      */     
/* 1065 */     if (this.numTablesMetricsHistBreakpoints == null) {
/* 1066 */       this.numTablesMetricsHistBreakpoints = new long[20];
/*      */     }
/*      */   }
/*      */   
/*      */   protected void checkClosed() throws SQLException {
/* 1071 */     if (this.isClosed) {
/* 1072 */       StringBuffer messageBuf = new StringBuffer("No operations allowed after connection closed.");
/*      */ 
/*      */       
/* 1075 */       if (this.forcedClosedLocation != null || this.forceClosedReason != null) {
/* 1076 */         messageBuf.append("Connection was implicitly closed ");
/*      */       }
/*      */ 
/*      */       
/* 1080 */       if (this.forcedClosedLocation != null) {
/* 1081 */         messageBuf.append("\n\n at (stack trace):\n");
/* 1082 */         messageBuf.append(Util.stackTraceToString(this.forcedClosedLocation));
/*      */       } 
/*      */ 
/*      */       
/* 1086 */       if (this.forceClosedReason != null) {
/* 1087 */         if (this.forcedClosedLocation != null) {
/* 1088 */           messageBuf.append("\n\nDue ");
/*      */         } else {
/* 1090 */           messageBuf.append("due ");
/*      */         } 
/*      */         
/* 1093 */         messageBuf.append("to underlying exception/error:\n");
/* 1094 */         messageBuf.append(Util.stackTraceToString(this.forceClosedReason));
/*      */       } 
/*      */ 
/*      */       
/* 1098 */       throw SQLError.createSQLException(messageBuf.toString(), "08003");
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
/*      */   private void checkServerEncoding() throws SQLException {
/* 1111 */     if (getUseUnicode() && getEncoding() != null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1116 */     String serverEncoding = (String)this.serverVariables.get("character_set");
/*      */ 
/*      */     
/* 1119 */     if (serverEncoding == null)
/*      */     {
/* 1121 */       serverEncoding = (String)this.serverVariables.get("character_set_server");
/*      */     }
/*      */ 
/*      */     
/* 1125 */     String mappedServerEncoding = null;
/*      */     
/* 1127 */     if (serverEncoding != null) {
/* 1128 */       mappedServerEncoding = CharsetMapping.getJavaEncodingForMysqlEncoding(serverEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1136 */     if (!getUseUnicode() && mappedServerEncoding != null) {
/* 1137 */       SingleByteCharsetConverter converter = getCharsetConverter(mappedServerEncoding);
/*      */       
/* 1139 */       if (converter != null) {
/* 1140 */         setUseUnicode(true);
/* 1141 */         setEncoding(mappedServerEncoding);
/*      */ 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1151 */     if (serverEncoding != null) {
/* 1152 */       if (mappedServerEncoding == null)
/*      */       {
/*      */         
/* 1155 */         if (Character.isLowerCase(serverEncoding.charAt(0))) {
/* 1156 */           char[] ach = serverEncoding.toCharArray();
/* 1157 */           ach[0] = Character.toUpperCase(serverEncoding.charAt(0));
/* 1158 */           setEncoding(new String(ach));
/*      */         } 
/*      */       }
/*      */       
/* 1162 */       if (mappedServerEncoding == null) {
/* 1163 */         throw SQLError.createSQLException("Unknown character encoding on server '" + serverEncoding + "', use 'characterEncoding=' property " + " to provide correct mapping", "01S00");
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
/*      */       try {
/* 1175 */         "abc".getBytes(mappedServerEncoding);
/* 1176 */         setEncoding(mappedServerEncoding);
/* 1177 */         setUseUnicode(true);
/* 1178 */       } catch (UnsupportedEncodingException UE) {
/* 1179 */         throw SQLError.createSQLException("The driver can not map the character encoding '" + getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You " + "can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" " + "to your JDBC URL.", "0S100");
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
/*      */   private void checkTransactionIsolationLevel() throws SQLException {
/* 1199 */     String txIsolationName = null;
/*      */     
/* 1201 */     if (versionMeetsMinimum(4, 0, 3)) {
/* 1202 */       txIsolationName = "tx_isolation";
/*      */     } else {
/* 1204 */       txIsolationName = "transaction_isolation";
/*      */     } 
/*      */     
/* 1207 */     String s = (String)this.serverVariables.get(txIsolationName);
/*      */     
/* 1209 */     if (s != null) {
/* 1210 */       Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */       
/* 1212 */       if (intTI != null) {
/* 1213 */         this.isolationLevel = intTI.intValue();
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
/*      */   protected void abortInternal() throws SQLException {
/* 1225 */     if (this.io != null) {
/*      */       try {
/* 1227 */         this.io.forceClose();
/* 1228 */       } catch (Throwable t) {}
/*      */ 
/*      */       
/* 1231 */       this.io = null;
/*      */     } 
/*      */     
/* 1234 */     this.isClosed = true;
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
/*      */   private void cleanup(Throwable whyCleanedUp) {
/*      */     try {
/* 1247 */       if (this.io != null && !isClosed()) {
/* 1248 */         realClose(false, false, false, whyCleanedUp);
/* 1249 */       } else if (this.io != null) {
/* 1250 */         this.io.forceClose();
/*      */       } 
/* 1252 */     } catch (SQLException sqlEx) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1257 */     this.isClosed = true;
/*      */   }
/*      */   
/*      */   public void clearHasTriedMaster() {
/* 1261 */     this.hasTriedMasterFlag = false;
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
/*      */   public void clearWarnings() throws SQLException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/* 1286 */     return clientPrepareStatement(sql, 1005, 1007);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/* 1296 */     PreparedStatement pStmt = clientPrepareStatement(sql);
/*      */     
/* 1298 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
/*      */ 
/*      */     
/* 1301 */     return pStmt;
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
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 1319 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException {
/* 1327 */     checkClosed();
/*      */     
/* 1329 */     String nativeSql = (processEscapeCodesIfNeeded && getProcessEscapeCodesForPrepStmts()) ? nativeSQL(sql) : sql;
/*      */     
/* 1331 */     PreparedStatement pStmt = null;
/*      */     
/* 1333 */     if (getCachePreparedStatements()) {
/* 1334 */       synchronized (this.cachedPreparedStatementParams) {
/* 1335 */         PreparedStatement.ParseInfo pStmtInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(nativeSql);
/*      */ 
/*      */         
/* 1338 */         if (pStmtInfo == null) {
/* 1339 */           pStmt = PreparedStatement.getInstance(this, nativeSql, this.database);
/*      */ 
/*      */           
/* 1342 */           PreparedStatement.ParseInfo parseInfo = pStmt.getParseInfo();
/*      */           
/* 1344 */           if (parseInfo.statementLength < getPreparedStatementCacheSqlLimit()) {
/* 1345 */             if (this.cachedPreparedStatementParams.size() >= getPreparedStatementCacheSize()) {
/* 1346 */               Iterator oldestIter = this.cachedPreparedStatementParams.keySet().iterator();
/*      */               
/* 1348 */               long lruTime = Long.MAX_VALUE;
/* 1349 */               String oldestSql = null;
/*      */               
/* 1351 */               while (oldestIter.hasNext()) {
/* 1352 */                 String sqlKey = oldestIter.next();
/* 1353 */                 PreparedStatement.ParseInfo lruInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(sqlKey);
/*      */ 
/*      */                 
/* 1356 */                 if (lruInfo.lastUsed < lruTime) {
/* 1357 */                   lruTime = lruInfo.lastUsed;
/* 1358 */                   oldestSql = sqlKey;
/*      */                 } 
/*      */               } 
/*      */               
/* 1362 */               if (oldestSql != null) {
/* 1363 */                 this.cachedPreparedStatementParams.remove(oldestSql);
/*      */               }
/*      */             } 
/*      */ 
/*      */             
/* 1368 */             this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
/*      */           } 
/*      */         } else {
/*      */           
/* 1372 */           pStmtInfo.lastUsed = System.currentTimeMillis();
/* 1373 */           pStmt = new PreparedStatement(this, nativeSql, this.database, pStmtInfo);
/*      */         } 
/*      */       } 
/*      */     } else {
/*      */       
/* 1378 */       pStmt = PreparedStatement.getInstance(this, nativeSql, this.database);
/*      */     } 
/*      */ 
/*      */     
/* 1382 */     pStmt.setResultSetType(resultSetType);
/* 1383 */     pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */     
/* 1385 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/* 1394 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */     
/* 1396 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
/*      */ 
/*      */ 
/*      */     
/* 1400 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/* 1408 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */     
/* 1410 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
/*      */ 
/*      */ 
/*      */     
/* 1414 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 1420 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
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
/*      */   public synchronized void close() throws SQLException {
/* 1436 */     if (this.connectionLifecycleInterceptors != null) {
/* 1437 */       (new IterateBlock(this, this.connectionLifecycleInterceptors.iterator()) { private final ConnectionImpl this$0;
/*      */           void forEach(Object each) throws SQLException {
/* 1439 */             ((ConnectionLifecycleInterceptor)each).close();
/*      */           } }
/*      */         ).doForAll();
/*      */     }
/*      */     
/* 1444 */     realClose(true, true, false, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void closeAllOpenStatements() throws SQLException {
/* 1454 */     SQLException postponedException = null;
/*      */     
/* 1456 */     if (this.openStatements != null) {
/* 1457 */       List currentlyOpenStatements = new ArrayList();
/*      */ 
/*      */ 
/*      */       
/* 1461 */       Iterator iter = this.openStatements.keySet().iterator();
/* 1462 */       while (iter.hasNext()) {
/* 1463 */         currentlyOpenStatements.add(iter.next());
/*      */       }
/*      */       
/* 1466 */       int numStmts = currentlyOpenStatements.size();
/*      */       
/* 1468 */       for (int i = 0; i < numStmts; i++) {
/* 1469 */         StatementImpl stmt = currentlyOpenStatements.get(i);
/*      */         
/*      */         try {
/* 1472 */           stmt.realClose(false, true);
/* 1473 */         } catch (SQLException sqlEx) {
/* 1474 */           postponedException = sqlEx;
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1479 */       if (postponedException != null) {
/* 1480 */         throw postponedException;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void closeStatement(Statement stmt) {
/* 1486 */     if (stmt != null) {
/*      */       try {
/* 1488 */         stmt.close();
/* 1489 */       } catch (SQLException sqlEx) {}
/*      */ 
/*      */ 
/*      */       
/* 1493 */       stmt = null;
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
/*      */   public void commit() throws SQLException {
/* 1512 */     synchronized (getMutex()) {
/* 1513 */       checkClosed();
/*      */       
/*      */       try {
/* 1516 */         if (this.connectionLifecycleInterceptors != null) {
/* 1517 */           IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator()) { private final ConnectionImpl this$0;
/*      */               
/*      */               void forEach(Object each) throws SQLException {
/* 1520 */                 if (!((ConnectionLifecycleInterceptor)each).commit()) {
/* 1521 */                   this.stopIterating = true;
/*      */                 }
/*      */               } }
/*      */             ;
/*      */           
/* 1526 */           iter.doForAll();
/*      */           
/* 1528 */           if (!iter.fullIteration()) {
/*      */             return;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/* 1534 */         if (this.autoCommit && !getRelaxAutoCommit())
/* 1535 */           throw SQLError.createSQLException("Can't call commit when autocommit=true"); 
/* 1536 */         if (this.transactionsSupported) {
/* 1537 */           if (getUseLocalSessionState() && versionMeetsMinimum(5, 0, 0) && 
/* 1538 */             !this.io.inTransactionOnServer()) {
/*      */             return;
/*      */           }
/*      */ 
/*      */           
/* 1543 */           execSQL((StatementImpl)null, "commit", -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/* 1549 */       catch (SQLException sqlException) {
/* 1550 */         if ("08S01".equals(sqlException.getSQLState()))
/*      */         {
/* 1552 */           throw SQLError.createSQLException("Communications link failure during commit(). Transaction resolution unknown.", "08007");
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1557 */         throw sqlException;
/*      */       } finally {
/* 1559 */         this.needsPing = getReconnectAtTxEnd();
/*      */       } 
/*      */       return;
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
/*      */   private void configureCharsetProperties() throws SQLException {
/* 1573 */     if (getEncoding() != null) {
/*      */       
/*      */       try {
/*      */         
/* 1577 */         String testString = "abc";
/* 1578 */         testString.getBytes(getEncoding());
/* 1579 */       } catch (UnsupportedEncodingException UE) {
/*      */         
/* 1581 */         String oldEncoding = getEncoding();
/*      */         
/* 1583 */         setEncoding(CharsetMapping.getJavaEncodingForMysqlEncoding(oldEncoding, this));
/*      */ 
/*      */         
/* 1586 */         if (getEncoding() == null) {
/* 1587 */           throw SQLError.createSQLException("Java does not support the MySQL character encoding  encoding '" + oldEncoding + "'.", "01S00");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1594 */           String testString = "abc";
/* 1595 */           testString.getBytes(getEncoding());
/* 1596 */         } catch (UnsupportedEncodingException encodingEx) {
/* 1597 */           throw SQLError.createSQLException("Unsupported character encoding '" + getEncoding() + "'.", "01S00");
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
/*      */   private boolean configureClientCharacterSet(boolean dontCheckServerMatch) throws SQLException {
/* 1619 */     String realJavaEncoding = getEncoding();
/* 1620 */     boolean characterSetAlreadyConfigured = false;
/*      */     
/*      */     try {
/* 1623 */       if (versionMeetsMinimum(4, 1, 0)) {
/* 1624 */         characterSetAlreadyConfigured = true;
/*      */         
/* 1626 */         setUseUnicode(true);
/*      */         
/* 1628 */         configureCharsetProperties();
/* 1629 */         realJavaEncoding = getEncoding();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1637 */           if (this.props != null && this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex") != null) {
/* 1638 */             this.io.serverCharsetIndex = Integer.parseInt(this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex"));
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1643 */           String serverEncodingToSet = CharsetMapping.INDEX_TO_CHARSET[this.io.serverCharsetIndex];
/*      */ 
/*      */           
/* 1646 */           if (serverEncodingToSet == null || serverEncodingToSet.length() == 0) {
/* 1647 */             if (realJavaEncoding != null) {
/*      */               
/* 1649 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 1651 */               throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000");
/*      */             } 
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1660 */           if (versionMeetsMinimum(4, 1, 0) && "ISO8859_1".equalsIgnoreCase(serverEncodingToSet))
/*      */           {
/* 1662 */             serverEncodingToSet = "Cp1252";
/*      */           }
/*      */           
/* 1665 */           setEncoding(serverEncodingToSet);
/*      */         }
/* 1667 */         catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 1668 */           if (realJavaEncoding != null) {
/*      */             
/* 1670 */             setEncoding(realJavaEncoding);
/*      */           } else {
/* 1672 */             throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000");
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1680 */         if (getEncoding() == null)
/*      */         {
/* 1682 */           setEncoding("ISO8859_1");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1689 */         if (getUseUnicode()) {
/* 1690 */           if (realJavaEncoding != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1696 */             if (realJavaEncoding.equalsIgnoreCase("UTF-8") || realJavaEncoding.equalsIgnoreCase("UTF8")) {
/*      */ 
/*      */ 
/*      */               
/* 1700 */               if (!getUseOldUTF8Behavior() && (
/* 1701 */                 dontCheckServerMatch || !characterSetNamesMatches("utf8"))) {
/* 1702 */                 execSQL((StatementImpl)null, "SET NAMES utf8", -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */               }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1709 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 1711 */               String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(realJavaEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1726 */               if (mysqlEncodingName != null)
/*      */               {
/* 1728 */                 if (dontCheckServerMatch || !characterSetNamesMatches(mysqlEncodingName)) {
/* 1729 */                   execSQL((StatementImpl)null, "SET NAMES " + mysqlEncodingName, -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */                 }
/*      */               }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1740 */               setEncoding(realJavaEncoding);
/*      */             } 
/* 1742 */           } else if (getEncoding() != null) {
/*      */ 
/*      */ 
/*      */             
/* 1746 */             String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(getEncoding().toUpperCase(Locale.ENGLISH), this);
/*      */ 
/*      */ 
/*      */             
/* 1750 */             if (dontCheckServerMatch || !characterSetNamesMatches(mysqlEncodingName)) {
/* 1751 */               execSQL((StatementImpl)null, "SET NAMES " + mysqlEncodingName, -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1757 */             realJavaEncoding = getEncoding();
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
/* 1768 */         String onServer = null;
/* 1769 */         boolean isNullOnServer = false;
/*      */         
/* 1771 */         if (this.serverVariables != null) {
/* 1772 */           onServer = (String)this.serverVariables.get("character_set_results");
/*      */           
/* 1774 */           isNullOnServer = (onServer == null || "NULL".equalsIgnoreCase(onServer) || onServer.length() == 0);
/*      */         } 
/*      */         
/* 1777 */         if (getCharacterSetResults() == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1784 */           if (!isNullOnServer) {
/* 1785 */             execSQL((StatementImpl)null, "SET character_set_results = NULL", -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1790 */             if (!this.usingCachedConfig) {
/* 1791 */               this.serverVariables.put("jdbc.local.character_set_results", null);
/*      */             }
/*      */           }
/* 1794 */           else if (!this.usingCachedConfig) {
/* 1795 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           } 
/*      */         } else {
/*      */           
/* 1799 */           String charsetResults = getCharacterSetResults();
/* 1800 */           String mysqlEncodingName = null;
/*      */           
/* 1802 */           if ("UTF-8".equalsIgnoreCase(charsetResults) || "UTF8".equalsIgnoreCase(charsetResults)) {
/*      */             
/* 1804 */             mysqlEncodingName = "utf8";
/*      */           } else {
/* 1806 */             mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(charsetResults.toUpperCase(Locale.ENGLISH), this);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1815 */           if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_results"))) {
/*      */             
/* 1817 */             StringBuffer setBuf = new StringBuffer("SET character_set_results = ".length() + mysqlEncodingName.length());
/*      */ 
/*      */             
/* 1820 */             setBuf.append("SET character_set_results = ").append(mysqlEncodingName);
/*      */ 
/*      */             
/* 1823 */             execSQL((StatementImpl)null, setBuf.toString(), -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1828 */             if (!this.usingCachedConfig) {
/* 1829 */               this.serverVariables.put("jdbc.local.character_set_results", mysqlEncodingName);
/*      */             
/*      */             }
/*      */           }
/* 1833 */           else if (!this.usingCachedConfig) {
/* 1834 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1839 */         if (getConnectionCollation() != null) {
/* 1840 */           StringBuffer setBuf = new StringBuffer("SET collation_connection = ".length() + getConnectionCollation().length());
/*      */ 
/*      */           
/* 1843 */           setBuf.append("SET collation_connection = ").append(getConnectionCollation());
/*      */ 
/*      */           
/* 1846 */           execSQL((StatementImpl)null, setBuf.toString(), -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1853 */         realJavaEncoding = getEncoding();
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/* 1861 */       setEncoding(realJavaEncoding);
/*      */     } 
/*      */     
/* 1864 */     return characterSetAlreadyConfigured;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void configureTimezone() throws SQLException {
/* 1875 */     String configuredTimeZoneOnServer = (String)this.serverVariables.get("timezone");
/*      */ 
/*      */     
/* 1878 */     if (configuredTimeZoneOnServer == null) {
/* 1879 */       configuredTimeZoneOnServer = (String)this.serverVariables.get("time_zone");
/*      */ 
/*      */       
/* 1882 */       if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
/* 1883 */         configuredTimeZoneOnServer = (String)this.serverVariables.get("system_time_zone");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1888 */     String canoncicalTimezone = getServerTimezone();
/*      */     
/* 1890 */     if ((getUseTimezone() || !getUseLegacyDatetimeCode()) && configuredTimeZoneOnServer != null) {
/*      */       
/* 1892 */       if (canoncicalTimezone == null || StringUtils.isEmptyOrWhitespaceOnly(canoncicalTimezone)) {
/*      */         try {
/* 1894 */           canoncicalTimezone = TimeUtil.getCanoncialTimezone(configuredTimeZoneOnServer);
/*      */ 
/*      */           
/* 1897 */           if (canoncicalTimezone == null) {
/* 1898 */             throw SQLError.createSQLException("Can't map timezone '" + configuredTimeZoneOnServer + "' to " + " canonical timezone.", "S1009");
/*      */           
/*      */           }
/*      */         
/*      */         }
/* 1903 */         catch (IllegalArgumentException iae) {
/* 1904 */           throw SQLError.createSQLException(iae.getMessage(), "S1000");
/*      */         } 
/*      */       }
/*      */     } else {
/*      */       
/* 1909 */       canoncicalTimezone = getServerTimezone();
/*      */     } 
/*      */     
/* 1912 */     if (canoncicalTimezone != null && canoncicalTimezone.length() > 0) {
/* 1913 */       this.serverTimezoneTZ = TimeZone.getTimeZone(canoncicalTimezone);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1920 */       if (!canoncicalTimezone.equalsIgnoreCase("GMT") && this.serverTimezoneTZ.getID().equals("GMT"))
/*      */       {
/* 1922 */         throw SQLError.createSQLException("No timezone mapping entry for '" + canoncicalTimezone + "'", "S1009");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1927 */       if ("GMT".equalsIgnoreCase(this.serverTimezoneTZ.getID())) {
/* 1928 */         this.isServerTzUTC = true;
/*      */       } else {
/* 1930 */         this.isServerTzUTC = false;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound) {
/* 1938 */     double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
/*      */     
/* 1940 */     if (bucketSize < 1.0D) {
/* 1941 */       bucketSize = 1.0D;
/*      */     }
/*      */     
/* 1944 */     for (int i = 0; i < 20; i++) {
/* 1945 */       breakpoints[i] = lowerBound;
/* 1946 */       lowerBound = (long)(lowerBound + bucketSize);
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
/*      */   protected void createNewIO(boolean isForReconnect) throws SQLException {
/* 1969 */     synchronized (this.mutex) {
/* 1970 */       Properties mergedProps = exposeAsProperties(this.props);
/*      */       
/* 1972 */       long queriesIssuedFailedOverCopy = this.queriesIssuedFailedOver;
/* 1973 */       this.queriesIssuedFailedOver = 0L;
/*      */       
/*      */       try {
/* 1976 */         if (!getHighAvailability() && !this.failedOver) {
/* 1977 */           boolean connectionGood = false;
/* 1978 */           Exception connectionNotEstablishedBecause = null;
/*      */           
/* 1980 */           int hostIndex = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1988 */           if (getRoundRobinLoadBalance()) {
/* 1989 */             hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList);
/*      */           }
/*      */ 
/*      */           
/* 1993 */           for (; hostIndex < this.hostListSize; hostIndex++) {
/*      */             
/* 1995 */             if (hostIndex == 0) {
/* 1996 */               this.hasTriedMasterFlag = true;
/*      */             }
/*      */             
/*      */             try {
/* 2000 */               String newHostPortPair = this.hostList.get(hostIndex);
/*      */ 
/*      */               
/* 2003 */               int newPort = 3306;
/*      */               
/* 2005 */               String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(newHostPortPair);
/*      */               
/* 2007 */               String newHost = hostPortPair[0];
/*      */               
/* 2009 */               if (newHost == null || StringUtils.isEmptyOrWhitespaceOnly(newHost)) {
/* 2010 */                 newHost = "localhost";
/*      */               }
/*      */               
/* 2013 */               if (hostPortPair[1] != null) {
/*      */                 try {
/* 2015 */                   newPort = Integer.parseInt(hostPortPair[1]);
/*      */                 }
/* 2017 */                 catch (NumberFormatException nfe) {
/* 2018 */                   throw SQLError.createSQLException("Illegal connection port value '" + hostPortPair[1] + "'", "01S00");
/*      */                 } 
/*      */               }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2026 */               this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), this, getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2031 */               this.io.doHandshake(this.user, this.password, this.database);
/*      */               
/* 2033 */               this.connectionId = this.io.getThreadId();
/* 2034 */               this.isClosed = false;
/*      */ 
/*      */               
/* 2037 */               boolean oldAutoCommit = getAutoCommit();
/* 2038 */               int oldIsolationLevel = this.isolationLevel;
/* 2039 */               boolean oldReadOnly = isReadOnly();
/* 2040 */               String oldCatalog = getCatalog();
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2045 */               initializePropsFromServer();
/*      */               
/* 2047 */               if (isForReconnect) {
/*      */                 
/* 2049 */                 setAutoCommit(oldAutoCommit);
/*      */                 
/* 2051 */                 if (this.hasIsolationLevels) {
/* 2052 */                   setTransactionIsolation(oldIsolationLevel);
/*      */                 }
/*      */                 
/* 2055 */                 setCatalog(oldCatalog);
/*      */               } 
/*      */               
/* 2058 */               if (hostIndex != 0) {
/* 2059 */                 setFailedOverState();
/* 2060 */                 queriesIssuedFailedOverCopy = 0L;
/*      */               } else {
/* 2062 */                 this.failedOver = false;
/* 2063 */                 queriesIssuedFailedOverCopy = 0L;
/*      */                 
/* 2065 */                 if (this.hostListSize > 1) {
/* 2066 */                   setReadOnlyInternal(false);
/*      */                 } else {
/* 2068 */                   setReadOnlyInternal(oldReadOnly);
/*      */                 } 
/*      */               } 
/*      */               
/* 2072 */               connectionGood = true;
/*      */               
/*      */               break;
/* 2075 */             } catch (Exception EEE) {
/* 2076 */               if (this.io != null) {
/* 2077 */                 this.io.forceClose();
/*      */               }
/*      */               
/* 2080 */               connectionNotEstablishedBecause = EEE;
/*      */               
/* 2082 */               connectionGood = false;
/*      */               
/* 2084 */               if (EEE instanceof SQLException) {
/* 2085 */                 SQLException sqlEx = (SQLException)EEE;
/*      */                 
/* 2087 */                 String sqlState = sqlEx.getSQLState();
/*      */ 
/*      */ 
/*      */                 
/* 2091 */                 if (sqlState == null || !sqlState.equals("08S01"))
/*      */                 {
/*      */                   
/* 2094 */                   throw sqlEx;
/*      */                 }
/*      */               } 
/*      */ 
/*      */               
/* 2099 */               if (getRoundRobinLoadBalance()) {
/* 2100 */                 hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList) - 1;
/*      */               }
/* 2102 */               else if (this.hostListSize - 1 == hostIndex) {
/* 2103 */                 throw SQLError.createCommunicationsException(this, (this.io != null) ? this.io.getLastPacketSentTimeMs() : 0L, (this.io != null) ? this.io.getLastPacketReceivedTimeMs() : 0L, EEE);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2113 */           if (!connectionGood) {
/*      */             
/* 2115 */             SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001");
/*      */ 
/*      */             
/* 2118 */             chainedEx.initCause(connectionNotEstablishedBecause);
/*      */             
/* 2120 */             throw chainedEx;
/*      */           } 
/*      */         } else {
/* 2123 */           double timeout = getInitialTimeout();
/* 2124 */           boolean connectionGood = false;
/*      */           
/* 2126 */           Exception connectionException = null;
/*      */           
/* 2128 */           int hostIndex = 0;
/*      */           
/* 2130 */           if (getRoundRobinLoadBalance()) {
/* 2131 */             hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList);
/*      */           }
/*      */ 
/*      */           
/* 2135 */           for (; hostIndex < this.hostListSize && !connectionGood; hostIndex++) {
/* 2136 */             if (hostIndex == 0) {
/* 2137 */               this.hasTriedMasterFlag = true;
/*      */             }
/*      */             
/* 2140 */             if (this.preferSlaveDuringFailover && hostIndex == 0) {
/* 2141 */               hostIndex++;
/*      */             }
/*      */             
/* 2144 */             int attemptCount = 0;
/* 2145 */             for (; attemptCount < getMaxReconnects() && !connectionGood; attemptCount++) {
/*      */               try {
/* 2147 */                 if (this.io != null) {
/* 2148 */                   this.io.forceClose();
/*      */                 }
/*      */                 
/* 2151 */                 String newHostPortPair = this.hostList.get(hostIndex);
/*      */ 
/*      */                 
/* 2154 */                 int newPort = 3306;
/*      */                 
/* 2156 */                 String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(newHostPortPair);
/*      */                 
/* 2158 */                 String newHost = hostPortPair[0];
/*      */                 
/* 2160 */                 if (newHost == null || StringUtils.isEmptyOrWhitespaceOnly(newHost)) {
/* 2161 */                   newHost = "localhost";
/*      */                 }
/*      */                 
/* 2164 */                 if (hostPortPair[1] != null) {
/*      */                   try {
/* 2166 */                     newPort = Integer.parseInt(hostPortPair[1]);
/*      */                   }
/* 2168 */                   catch (NumberFormatException nfe) {
/* 2169 */                     throw SQLError.createSQLException("Illegal connection port value '" + hostPortPair[1] + "'", "01S00");
/*      */                   } 
/*      */                 }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2177 */                 this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), this, getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
/*      */ 
/*      */ 
/*      */                 
/* 2181 */                 this.io.doHandshake(this.user, this.password, this.database);
/*      */                 
/* 2183 */                 pingInternal(false);
/* 2184 */                 this.connectionId = this.io.getThreadId();
/* 2185 */                 this.isClosed = false;
/*      */ 
/*      */                 
/* 2188 */                 boolean oldAutoCommit = getAutoCommit();
/* 2189 */                 int oldIsolationLevel = this.isolationLevel;
/* 2190 */                 boolean oldReadOnly = isReadOnly();
/* 2191 */                 String oldCatalog = getCatalog();
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2196 */                 initializePropsFromServer();
/*      */                 
/* 2198 */                 if (isForReconnect) {
/*      */                   
/* 2200 */                   setAutoCommit(oldAutoCommit);
/*      */                   
/* 2202 */                   if (this.hasIsolationLevels) {
/* 2203 */                     setTransactionIsolation(oldIsolationLevel);
/*      */                   }
/*      */                   
/* 2206 */                   setCatalog(oldCatalog);
/*      */                 } 
/*      */                 
/* 2209 */                 connectionGood = true;
/*      */                 
/* 2211 */                 if (hostIndex != 0) {
/* 2212 */                   setFailedOverState();
/* 2213 */                   queriesIssuedFailedOverCopy = 0L; break;
/*      */                 } 
/* 2215 */                 this.failedOver = false;
/* 2216 */                 queriesIssuedFailedOverCopy = 0L;
/*      */                 
/* 2218 */                 if (this.hostListSize > 1) {
/* 2219 */                   setReadOnlyInternal(false); break;
/*      */                 } 
/* 2221 */                 setReadOnlyInternal(oldReadOnly);
/*      */ 
/*      */ 
/*      */                 
/*      */                 break;
/* 2226 */               } catch (Exception EEE) {
/* 2227 */                 connectionException = EEE;
/* 2228 */                 connectionGood = false;
/*      */ 
/*      */                 
/* 2231 */                 if (getRoundRobinLoadBalance()) {
/* 2232 */                   hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList) - 1;
/*      */                 }
/*      */ 
/*      */ 
/*      */                 
/* 2237 */                 if (connectionGood) {
/*      */                   break;
/*      */                 }
/*      */                 
/* 2241 */                 if (attemptCount > 0) {
/*      */                   try {
/* 2243 */                     Thread.sleep((long)timeout * 1000L);
/* 2244 */                   } catch (InterruptedException IE) {}
/*      */                 }
/*      */               } 
/*      */             } 
/*      */           } 
/*      */ 
/*      */           
/* 2251 */           if (!connectionGood) {
/*      */             
/* 2253 */             SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnectWithRetries", new Object[] { new Integer(getMaxReconnects()) }), "08001");
/*      */ 
/*      */ 
/*      */             
/* 2257 */             chainedEx.initCause(connectionException);
/*      */             
/* 2259 */             throw chainedEx;
/*      */           } 
/*      */         } 
/*      */         
/* 2263 */         if (getParanoid() && !getHighAvailability() && this.hostListSize <= 1) {
/*      */           
/* 2265 */           this.password = null;
/* 2266 */           this.user = null;
/*      */         } 
/*      */         
/* 2269 */         if (isForReconnect) {
/*      */ 
/*      */ 
/*      */           
/* 2273 */           Iterator statementIter = this.openStatements.values().iterator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2285 */           Stack serverPreparedStatements = null;
/*      */           
/* 2287 */           while (statementIter.hasNext()) {
/* 2288 */             Object statementObj = statementIter.next();
/*      */             
/* 2290 */             if (statementObj instanceof ServerPreparedStatement) {
/* 2291 */               if (serverPreparedStatements == null) {
/* 2292 */                 serverPreparedStatements = new Stack();
/*      */               }
/*      */               
/* 2295 */               serverPreparedStatements.add(statementObj);
/*      */             } 
/*      */           } 
/*      */           
/* 2299 */           if (serverPreparedStatements != null) {
/* 2300 */             while (!serverPreparedStatements.isEmpty()) {
/* 2301 */               ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
/*      */             }
/*      */           }
/*      */         } 
/*      */       } finally {
/*      */         
/* 2307 */         this.queriesIssuedFailedOver = queriesIssuedFailedOverCopy;
/*      */         
/* 2309 */         if (this.io != null && getStatementInterceptors() != null) {
/* 2310 */           this.io.initializeStatementInterceptors(getStatementInterceptors(), mergedProps);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void createPreparedStatementCaches() {
/* 2318 */     int cacheSize = getPreparedStatementCacheSize();
/*      */     
/* 2320 */     this.cachedPreparedStatementParams = new HashMap(cacheSize);
/*      */     
/* 2322 */     if (getUseServerPreparedStmts()) {
/* 2323 */       this.serverSideStatementCheckCache = new LRUCache(cacheSize);
/*      */       
/* 2325 */       this.serverSideStatementCache = new LRUCache(this, cacheSize) { private final ConnectionImpl this$0;
/*      */           protected boolean removeEldestEntry(Map.Entry eldest) {
/* 2327 */             if (this.maxElements <= 1) {
/* 2328 */               return false;
/*      */             }
/*      */             
/* 2331 */             boolean removeIt = super.removeEldestEntry(eldest);
/*      */             
/* 2333 */             if (removeIt) {
/* 2334 */               ServerPreparedStatement ps = (ServerPreparedStatement)eldest.getValue();
/*      */               
/* 2336 */               ps.isCached = false;
/* 2337 */               ps.setClosed(false);
/*      */               
/*      */               try {
/* 2340 */                 ps.close();
/* 2341 */               } catch (SQLException sqlEx) {}
/*      */             } 
/*      */ 
/*      */ 
/*      */             
/* 2346 */             return removeIt;
/*      */           } }
/*      */         ;
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
/*      */   public Statement createStatement() throws SQLException {
/* 2362 */     return createStatement(1003, 1007);
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
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/* 2380 */     checkClosed();
/*      */     
/* 2382 */     StatementImpl stmt = new StatementImpl(this, this.database);
/* 2383 */     stmt.setResultSetType(resultSetType);
/* 2384 */     stmt.setResultSetConcurrency(resultSetConcurrency);
/*      */     
/* 2386 */     return stmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 2395 */     if (getPedantic() && 
/* 2396 */       resultSetHoldability != 1) {
/* 2397 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2403 */     return createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   protected void dumpTestcaseQuery(String query) {
/* 2407 */     System.err.println(query);
/*      */   }
/*      */   
/*      */   protected Connection duplicate() throws SQLException {
/* 2411 */     return new ConnectionImpl(this.origHostToConnectTo, this.origPortToConnectTo, this.props, this.origDatabaseToConnectTo, this.myURL);
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
/*      */   ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws SQLException {
/* 2465 */     return execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, false);
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
/*      */   ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException {
/* 2480 */     synchronized (this.mutex) {
/* 2481 */       long queryStartTime = 0L;
/*      */       
/* 2483 */       int endOfQueryPacketPosition = 0;
/*      */       
/* 2485 */       if (packet != null) {
/* 2486 */         endOfQueryPacketPosition = packet.getPosition();
/*      */       }
/*      */       
/* 2489 */       if (getGatherPerformanceMetrics()) {
/* 2490 */         queryStartTime = System.currentTimeMillis();
/*      */       }
/*      */       
/* 2493 */       this.lastQueryFinishedTime = 0L;
/*      */       
/* 2495 */       if (this.failedOver && this.autoCommit && !isBatch && 
/* 2496 */         shouldFallBack() && !this.executingFailoverReconnect) {
/*      */         try {
/* 2498 */           this.executingFailoverReconnect = true;
/*      */           
/* 2500 */           createNewIO(true);
/*      */           
/* 2502 */           String connectedHost = this.io.getHost();
/*      */           
/* 2504 */           if (connectedHost != null && this.hostList.get(0).equals(connectedHost)) {
/*      */             
/* 2506 */             this.failedOver = false;
/* 2507 */             this.queriesIssuedFailedOver = 0L;
/* 2508 */             setReadOnlyInternal(false);
/*      */           } 
/*      */         } finally {
/* 2511 */           this.executingFailoverReconnect = false;
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/* 2516 */       if ((getHighAvailability() || this.failedOver) && (this.autoCommit || getAutoReconnectForPools()) && this.needsPing && !isBatch) {
/*      */         
/*      */         try {
/*      */           
/* 2520 */           pingInternal(false);
/*      */           
/* 2522 */           this.needsPing = false;
/* 2523 */         } catch (Exception Ex) {
/* 2524 */           createNewIO(true);
/*      */         } 
/*      */       }
/*      */       
/*      */       try {
/* 2529 */         if (packet == null) {
/* 2530 */           String encoding = null;
/*      */           
/* 2532 */           if (getUseUnicode()) {
/* 2533 */             encoding = getEncoding();
/*      */           }
/*      */           
/* 2536 */           return this.io.sqlQueryDirect(callingStatement, sql, encoding, null, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2542 */         return this.io.sqlQueryDirect(callingStatement, null, null, packet, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);
/*      */ 
/*      */       
/*      */       }
/* 2546 */       catch (SQLException sqlE) {
/*      */ 
/*      */         
/* 2549 */         if (getDumpQueriesOnException()) {
/* 2550 */           String extractedSql = extractSqlFromPacket(sql, packet, endOfQueryPacketPosition);
/*      */           
/* 2552 */           StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */           
/* 2554 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */           
/* 2556 */           messageBuf.append(extractedSql);
/*      */           
/* 2558 */           sqlE = appendMessageToException(sqlE, messageBuf.toString());
/*      */         } 
/*      */         
/* 2561 */         if (getHighAvailability() || this.failedOver) {
/* 2562 */           this.needsPing = true;
/*      */         } else {
/* 2564 */           String sqlState = sqlE.getSQLState();
/*      */           
/* 2566 */           if (sqlState != null && sqlState.equals("08S01"))
/*      */           {
/*      */             
/* 2569 */             cleanup(sqlE);
/*      */           }
/*      */         } 
/*      */         
/* 2573 */         throw sqlE;
/* 2574 */       } catch (Exception ex) {
/* 2575 */         if (getHighAvailability() || this.failedOver) {
/* 2576 */           this.needsPing = true;
/* 2577 */         } else if (ex instanceof java.io.IOException) {
/* 2578 */           cleanup(ex);
/*      */         } 
/*      */         
/* 2581 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnexpectedException"), "S1000");
/*      */ 
/*      */         
/* 2584 */         sqlEx.initCause(ex);
/*      */         
/* 2586 */         throw sqlEx;
/*      */       } finally {
/* 2588 */         if (getMaintainTimeStats()) {
/* 2589 */           this.lastQueryFinishedTime = System.currentTimeMillis();
/*      */         }
/*      */         
/* 2592 */         if (this.failedOver) {
/* 2593 */           this.queriesIssuedFailedOver++;
/*      */         }
/*      */         
/* 2596 */         if (getGatherPerformanceMetrics()) {
/* 2597 */           long queryTime = System.currentTimeMillis() - queryStartTime;
/*      */ 
/*      */           
/* 2600 */           registerQueryExecutionTime(queryTime);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition) throws SQLException {
/* 2610 */     String extractedSql = null;
/*      */     
/* 2612 */     if (possibleSqlQuery != null) {
/* 2613 */       if (possibleSqlQuery.length() > getMaxQuerySizeToLog()) {
/* 2614 */         StringBuffer truncatedQueryBuf = new StringBuffer(possibleSqlQuery.substring(0, getMaxQuerySizeToLog()));
/*      */         
/* 2616 */         truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
/* 2617 */         extractedSql = truncatedQueryBuf.toString();
/*      */       } else {
/* 2619 */         extractedSql = possibleSqlQuery;
/*      */       } 
/*      */     }
/*      */     
/* 2623 */     if (extractedSql == null) {
/*      */ 
/*      */ 
/*      */       
/* 2627 */       int extractPosition = endOfQueryPacketPosition;
/*      */       
/* 2629 */       boolean truncated = false;
/*      */       
/* 2631 */       if (endOfQueryPacketPosition > getMaxQuerySizeToLog()) {
/* 2632 */         extractPosition = getMaxQuerySizeToLog();
/* 2633 */         truncated = true;
/*      */       } 
/*      */       
/* 2636 */       extractedSql = new String(queryPacket.getByteBuffer(), 5, extractPosition - 5);
/*      */ 
/*      */       
/* 2639 */       if (truncated) {
/* 2640 */         extractedSql = extractedSql + Messages.getString("MysqlIO.25");
/*      */       }
/*      */     } 
/*      */     
/* 2644 */     return extractedSql;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void finalize() throws Throwable {
/* 2655 */     cleanup((Throwable)null);
/*      */     
/* 2657 */     super.finalize();
/*      */   }
/*      */   
/*      */   protected StringBuffer generateConnectionCommentBlock(StringBuffer buf) {
/* 2661 */     buf.append("/* conn id ");
/* 2662 */     buf.append(getId());
/* 2663 */     buf.append(" */ ");
/*      */     
/* 2665 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getActiveStatementCount() {
/* 2671 */     if (this.openStatements != null) {
/* 2672 */       synchronized (this.openStatements) {
/* 2673 */         return this.openStatements.size();
/*      */       } 
/*      */     }
/*      */     
/* 2677 */     return 0;
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
/*      */   public boolean getAutoCommit() throws SQLException {
/* 2689 */     return this.autoCommit;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Calendar getCalendarInstanceForSessionOrNew() {
/* 2697 */     if (getDynamicCalendars()) {
/* 2698 */       return Calendar.getInstance();
/*      */     }
/*      */     
/* 2701 */     return getSessionLockedCalendar();
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
/*      */   public String getCatalog() throws SQLException {
/* 2716 */     return this.database;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getCharacterSetMetadata() {
/* 2723 */     return this.characterSetMetadata;
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
/*      */   SingleByteCharsetConverter getCharsetConverter(String javaEncodingName) throws SQLException {
/* 2736 */     if (javaEncodingName == null) {
/* 2737 */       return null;
/*      */     }
/*      */     
/* 2740 */     if (this.usePlatformCharsetConverters) {
/* 2741 */       return null;
/*      */     }
/*      */ 
/*      */     
/* 2745 */     SingleByteCharsetConverter converter = null;
/*      */     
/* 2747 */     synchronized (this.charsetConverterMap) {
/* 2748 */       Object asObject = this.charsetConverterMap.get(javaEncodingName);
/*      */ 
/*      */       
/* 2751 */       if (asObject == CHARSET_CONVERTER_NOT_AVAILABLE_MARKER) {
/* 2752 */         return null;
/*      */       }
/*      */       
/* 2755 */       converter = (SingleByteCharsetConverter)asObject;
/*      */       
/* 2757 */       if (converter == null) {
/*      */         try {
/* 2759 */           converter = SingleByteCharsetConverter.getInstance(javaEncodingName, this);
/*      */ 
/*      */           
/* 2762 */           if (converter == null) {
/* 2763 */             this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */           } else {
/*      */             
/* 2766 */             this.charsetConverterMap.put(javaEncodingName, converter);
/*      */           } 
/* 2768 */         } catch (UnsupportedEncodingException unsupEncEx) {
/* 2769 */           this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */ 
/*      */           
/* 2772 */           converter = null;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 2777 */     return converter;
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
/*      */   protected String getCharsetNameForIndex(int charsetIndex) throws SQLException {
/* 2792 */     String charsetName = null;
/*      */     
/* 2794 */     if (getUseOldUTF8Behavior()) {
/* 2795 */       return getEncoding();
/*      */     }
/*      */     
/* 2798 */     if (charsetIndex != -1) {
/*      */       try {
/* 2800 */         charsetName = this.indexToCharsetMapping[charsetIndex];
/*      */         
/* 2802 */         if ("sjis".equalsIgnoreCase(charsetName) || "MS932".equalsIgnoreCase(charsetName))
/*      */         {
/*      */           
/* 2805 */           if (CharsetMapping.isAliasForSjis(getEncoding())) {
/* 2806 */             charsetName = getEncoding();
/*      */           }
/*      */         }
/* 2809 */       } catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 2810 */         throw SQLError.createSQLException("Unknown character set index for field '" + charsetIndex + "' received from server.", "S1000");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2817 */       if (charsetName == null) {
/* 2818 */         charsetName = getEncoding();
/*      */       }
/*      */     } else {
/* 2821 */       charsetName = getEncoding();
/*      */     } 
/*      */     
/* 2824 */     return charsetName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TimeZone getDefaultTimeZone() {
/* 2833 */     return this.defaultTimeZone;
/*      */   }
/*      */   
/*      */   protected String getErrorMessageEncoding() {
/* 2837 */     return this.errorMessageEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHoldability() throws SQLException {
/* 2844 */     return 2;
/*      */   }
/*      */   
/*      */   long getId() {
/* 2848 */     return this.connectionId;
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
/*      */   public long getIdleFor() {
/* 2860 */     if (this.lastQueryFinishedTime == 0L) {
/* 2861 */       return 0L;
/*      */     }
/*      */     
/* 2864 */     long now = System.currentTimeMillis();
/* 2865 */     long idleTime = now - this.lastQueryFinishedTime;
/*      */     
/* 2867 */     return idleTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected MysqlIO getIO() throws SQLException {
/* 2878 */     if (this.io == null || this.isClosed) {
/* 2879 */       throw SQLError.createSQLException("Operation not allowed on closed connection", "08003");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 2884 */     return this.io;
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
/*      */   public Log getLog() throws SQLException {
/* 2896 */     return this.log;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getMaxAllowedPacket() {
/* 2905 */     return this.maxAllowedPacket;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
/* 2911 */     String charset = CharsetMapping.getMysqlEncodingForJavaEncoding(javaCharsetName, this);
/*      */ 
/*      */     
/* 2914 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 2915 */       Map mapToCheck = null;
/*      */       
/* 2917 */       if (!getUseDynamicCharsetInfo()) {
/* 2918 */         mapToCheck = CharsetMapping.STATIC_CHARSET_TO_NUM_BYTES_MAP;
/*      */       } else {
/* 2920 */         mapToCheck = this.charsetToNumBytesMap;
/*      */         
/* 2922 */         synchronized (this.charsetToNumBytesMap) {
/* 2923 */           if (this.charsetToNumBytesMap.isEmpty()) {
/*      */             
/* 2925 */             Statement stmt = null;
/* 2926 */             ResultSet rs = null;
/*      */             
/*      */             try {
/* 2929 */               stmt = getMetadataSafeStatement();
/*      */               
/* 2931 */               rs = stmt.executeQuery("SHOW CHARACTER SET");
/*      */               
/* 2933 */               while (rs.next()) {
/* 2934 */                 this.charsetToNumBytesMap.put(rs.getString("Charset"), Constants.integerValueOf(rs.getInt("Maxlen")));
/*      */               }
/*      */ 
/*      */               
/* 2938 */               rs.close();
/* 2939 */               rs = null;
/*      */               
/* 2941 */               stmt.close();
/*      */               
/* 2943 */               stmt = null;
/*      */             } finally {
/* 2945 */               if (rs != null) {
/* 2946 */                 rs.close();
/* 2947 */                 rs = null;
/*      */               } 
/*      */               
/* 2950 */               if (stmt != null) {
/* 2951 */                 stmt.close();
/* 2952 */                 stmt = null;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 2959 */       Integer mbPerChar = (Integer)mapToCheck.get(charset);
/*      */       
/* 2961 */       if (mbPerChar != null) {
/* 2962 */         return mbPerChar.intValue();
/*      */       }
/*      */       
/* 2965 */       return 1;
/*      */     } 
/*      */     
/* 2968 */     return 1;
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
/*      */   public DatabaseMetaData getMetaData() throws SQLException {
/* 2982 */     return getMetaData(true, true);
/*      */   }
/*      */   
/*      */   private DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
/* 2986 */     if (checkClosed) {
/* 2987 */       checkClosed();
/*      */     }
/*      */     
/* 2990 */     return DatabaseMetaData.getInstance(this, this.database, checkForInfoSchema);
/*      */   }
/*      */   
/*      */   protected Statement getMetadataSafeStatement() throws SQLException {
/* 2994 */     Statement stmt = createStatement();
/*      */     
/* 2996 */     if (stmt.getMaxRows() != 0) {
/* 2997 */       stmt.setMaxRows(0);
/*      */     }
/*      */     
/* 3000 */     stmt.setEscapeProcessing(false);
/*      */     
/* 3002 */     if (stmt.getFetchSize() != 0) {
/* 3003 */       stmt.setFetchSize(0);
/*      */     }
/*      */     
/* 3006 */     return stmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object getMutex() throws SQLException {
/* 3017 */     if (this.io == null) {
/* 3018 */       throw SQLError.createSQLException("Connection.close() has already been called. Invalid operation in this state.", "08003");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 3023 */     reportMetricsIfNeeded();
/*      */     
/* 3025 */     return this.mutex;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getNetBufferLength() {
/* 3034 */     return this.netBufferLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getServerCharacterEncoding() {
/* 3043 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3044 */       return (String)this.serverVariables.get("character_set_server");
/*      */     }
/* 3046 */     return (String)this.serverVariables.get("character_set");
/*      */   }
/*      */ 
/*      */   
/*      */   int getServerMajorVersion() {
/* 3051 */     return this.io.getServerMajorVersion();
/*      */   }
/*      */   
/*      */   int getServerMinorVersion() {
/* 3055 */     return this.io.getServerMinorVersion();
/*      */   }
/*      */   
/*      */   int getServerSubMinorVersion() {
/* 3059 */     return this.io.getServerSubMinorVersion();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimeZone getServerTimezoneTZ() {
/* 3068 */     return this.serverTimezoneTZ;
/*      */   }
/*      */ 
/*      */   
/*      */   String getServerVariable(String variableName) {
/* 3073 */     if (this.serverVariables != null) {
/* 3074 */       return (String)this.serverVariables.get(variableName);
/*      */     }
/*      */     
/* 3077 */     return null;
/*      */   }
/*      */   
/*      */   String getServerVersion() {
/* 3081 */     return this.io.getServerVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   protected Calendar getSessionLockedCalendar() {
/* 3086 */     return this.sessionCalendar;
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
/*      */   public int getTransactionIsolation() throws SQLException {
/* 3098 */     if (this.hasIsolationLevels && !getUseLocalSessionState()) {
/* 3099 */       Statement stmt = null;
/* 3100 */       ResultSet rs = null;
/*      */       
/*      */       try {
/* 3103 */         stmt = getMetadataSafeStatement();
/*      */         
/* 3105 */         String query = null;
/*      */         
/* 3107 */         int offset = 0;
/*      */         
/* 3109 */         if (versionMeetsMinimum(4, 0, 3)) {
/* 3110 */           query = "SELECT @@session.tx_isolation";
/* 3111 */           offset = 1;
/*      */         } else {
/* 3113 */           query = "SHOW VARIABLES LIKE 'transaction_isolation'";
/* 3114 */           offset = 2;
/*      */         } 
/*      */         
/* 3117 */         rs = stmt.executeQuery(query);
/*      */         
/* 3119 */         if (rs.next()) {
/* 3120 */           String s = rs.getString(offset);
/*      */           
/* 3122 */           if (s != null) {
/* 3123 */             Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/*      */             
/* 3126 */             if (intTI != null) {
/* 3127 */               return intTI.intValue();
/*      */             }
/*      */           } 
/*      */           
/* 3131 */           throw SQLError.createSQLException("Could not map transaction isolation '" + s + " to a valid JDBC level.", "S1000");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3137 */         throw SQLError.createSQLException("Could not retrieve transaction isolation level from server", "S1000");
/*      */       
/*      */       }
/*      */       finally {
/*      */         
/* 3142 */         if (rs != null) {
/*      */           try {
/* 3144 */             rs.close();
/* 3145 */           } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3150 */           rs = null;
/*      */         } 
/*      */         
/* 3153 */         if (stmt != null) {
/*      */           try {
/* 3155 */             stmt.close();
/* 3156 */           } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3161 */           stmt = null;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 3166 */     return this.isolationLevel;
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
/*      */   public synchronized Map getTypeMap() throws SQLException {
/* 3178 */     if (this.typeMap == null) {
/* 3179 */       this.typeMap = new HashMap();
/*      */     }
/*      */     
/* 3182 */     return this.typeMap;
/*      */   }
/*      */   
/*      */   String getURL() {
/* 3186 */     return this.myURL;
/*      */   }
/*      */   
/*      */   String getUser() {
/* 3190 */     return this.user;
/*      */   }
/*      */   
/*      */   protected Calendar getUtcCalendar() {
/* 3194 */     return this.utcCalendar;
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
/*      */   public SQLWarning getWarnings() throws SQLException {
/* 3207 */     return null;
/*      */   }
/*      */   
/*      */   public boolean hasSameProperties(Connection c) {
/* 3211 */     return this.props.equals(((ConnectionImpl)c).props);
/*      */   }
/*      */   
/*      */   public boolean hasTriedMaster() {
/* 3215 */     return this.hasTriedMasterFlag;
/*      */   }
/*      */   
/*      */   protected void incrementNumberOfPreparedExecutes() {
/* 3219 */     if (getGatherPerformanceMetrics()) {
/* 3220 */       this.numberOfPreparedExecutes++;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3225 */       this.numberOfQueriesIssued++;
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void incrementNumberOfPrepares() {
/* 3230 */     if (getGatherPerformanceMetrics()) {
/* 3231 */       this.numberOfPrepares++;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void incrementNumberOfResultSetsCreated() {
/* 3236 */     if (getGatherPerformanceMetrics()) {
/* 3237 */       this.numberOfResultSetsCreated++;
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
/*      */   private void initializeDriverProperties(Properties info) throws SQLException {
/* 3252 */     initializeProperties(info);
/*      */     
/* 3254 */     this.usePlatformCharsetConverters = getUseJvmCharsetConverters();
/*      */     
/* 3256 */     this.log = LogFactory.getLogger(getLogger(), "MySQL");
/*      */     
/* 3258 */     if (getProfileSql() || getUseUsageAdvisor()) {
/* 3259 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this);
/*      */     }
/*      */     
/* 3262 */     if (getCachePreparedStatements()) {
/* 3263 */       createPreparedStatementCaches();
/*      */     }
/*      */     
/* 3266 */     if (getNoDatetimeStringSync() && getUseTimezone()) {
/* 3267 */       throw SQLError.createSQLException("Can't enable noDatetimeSync and useTimezone configuration properties at the same time", "01S00");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3273 */     if (getCacheCallableStatements()) {
/* 3274 */       this.parsedCallableStatementCache = new LRUCache(getCallableStatementCacheSize());
/*      */     }
/*      */ 
/*      */     
/* 3278 */     if (getAllowMultiQueries()) {
/* 3279 */       setCacheResultSetMetadata(false);
/*      */     }
/*      */     
/* 3282 */     if (getCacheResultSetMetadata()) {
/* 3283 */       this.resultSetMetadataCache = new LRUCache(getMetadataCacheSize());
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
/*      */   private void initializePropsFromServer() throws SQLException {
/* 3298 */     String connectionInterceptorClasses = getConnectionLifecycleInterceptors();
/*      */     
/* 3300 */     this.connectionLifecycleInterceptors = null;
/*      */     
/* 3302 */     if (connectionInterceptorClasses != null) {
/* 3303 */       this.connectionLifecycleInterceptors = Util.loadExtensions(this, this.props, connectionInterceptorClasses, "Connection.badLifecycleInterceptor");
/*      */ 
/*      */ 
/*      */       
/* 3307 */       Iterator iter = this.connectionLifecycleInterceptors.iterator();
/*      */       
/* 3309 */       (new IterateBlock(this, iter) { private final ConnectionImpl this$0;
/*      */           
/*      */           void forEach(Object each) throws SQLException {
/* 3312 */             ((ConnectionLifecycleInterceptor)each).init(this.this$0, this.this$0.props);
/*      */           } }
/*      */         ).doForAll();
/*      */     } 
/*      */     
/* 3317 */     setSessionVariables();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3323 */     if (!versionMeetsMinimum(4, 1, 0)) {
/* 3324 */       setTransformedBitIsBoolean(false);
/*      */     }
/*      */     
/* 3327 */     this.parserKnowsUnicode = versionMeetsMinimum(4, 1, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3332 */     if (getUseServerPreparedStmts() && versionMeetsMinimum(4, 1, 0)) {
/* 3333 */       this.useServerPreparedStmts = true;
/*      */       
/* 3335 */       if (versionMeetsMinimum(5, 0, 0) && !versionMeetsMinimum(5, 0, 3)) {
/* 3336 */         this.useServerPreparedStmts = false;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 3342 */     this.serverVariables.clear();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3347 */     if (versionMeetsMinimum(3, 21, 22)) {
/* 3348 */       loadServerVariables();
/*      */       
/* 3350 */       buildCollationMapping();
/*      */       
/* 3352 */       LicenseConfiguration.checkLicenseType(this.serverVariables);
/*      */       
/* 3354 */       String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
/*      */ 
/*      */       
/* 3357 */       this.lowerCaseTableNames = ("on".equalsIgnoreCase(lowerCaseTables) || "1".equalsIgnoreCase(lowerCaseTables) || "2".equalsIgnoreCase(lowerCaseTables));
/*      */ 
/*      */ 
/*      */       
/* 3361 */       configureTimezone();
/*      */       
/* 3363 */       if (this.serverVariables.containsKey("max_allowed_packet")) {
/* 3364 */         this.maxAllowedPacket = getServerVariableAsInt("max_allowed_packet", 1048576);
/*      */         
/* 3366 */         int preferredBlobSendChunkSize = getBlobSendChunkSize();
/*      */         
/* 3368 */         int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, this.maxAllowedPacket) - 8192 - 11;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3373 */         setBlobSendChunkSize(String.valueOf(allowedBlobSendChunkSize));
/*      */       } 
/*      */       
/* 3376 */       if (this.serverVariables.containsKey("net_buffer_length")) {
/* 3377 */         this.netBufferLength = getServerVariableAsInt("net_buffer_length", 16384);
/*      */       }
/*      */       
/* 3380 */       checkTransactionIsolationLevel();
/*      */       
/* 3382 */       if (!versionMeetsMinimum(4, 1, 0)) {
/* 3383 */         checkServerEncoding();
/*      */       }
/*      */       
/* 3386 */       this.io.checkForCharsetMismatch();
/*      */       
/* 3388 */       if (this.serverVariables.containsKey("sql_mode")) {
/* 3389 */         int sqlMode = 0;
/*      */         
/* 3391 */         String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
/*      */         
/*      */         try {
/* 3394 */           sqlMode = Integer.parseInt(sqlModeAsString);
/* 3395 */         } catch (NumberFormatException nfe) {
/*      */ 
/*      */           
/* 3398 */           sqlMode = 0;
/*      */           
/* 3400 */           if (sqlModeAsString != null) {
/* 3401 */             if (sqlModeAsString.indexOf("ANSI_QUOTES") != -1) {
/* 3402 */               sqlMode |= 0x4;
/*      */             }
/*      */             
/* 3405 */             if (sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1) {
/* 3406 */               this.noBackslashEscapes = true;
/*      */             }
/*      */           } 
/*      */         } 
/*      */         
/* 3411 */         if ((sqlMode & 0x4) > 0) {
/* 3412 */           this.useAnsiQuotes = true;
/*      */         } else {
/* 3414 */           this.useAnsiQuotes = false;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 3419 */     this.errorMessageEncoding = CharsetMapping.getCharacterEncodingForErrorMessages(this);
/*      */ 
/*      */ 
/*      */     
/* 3423 */     boolean overrideDefaultAutocommit = isAutoCommitNonDefaultOnServer();
/*      */     
/* 3425 */     configureClientCharacterSet(false);
/*      */     
/* 3427 */     if (versionMeetsMinimum(3, 23, 15)) {
/* 3428 */       this.transactionsSupported = true;
/*      */       
/* 3430 */       if (!overrideDefaultAutocommit) {
/* 3431 */         setAutoCommit(true);
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 3436 */       this.transactionsSupported = false;
/*      */     } 
/*      */ 
/*      */     
/* 3440 */     if (versionMeetsMinimum(3, 23, 36)) {
/* 3441 */       this.hasIsolationLevels = true;
/*      */     } else {
/* 3443 */       this.hasIsolationLevels = false;
/*      */     } 
/*      */     
/* 3446 */     this.hasQuotedIdentifiers = versionMeetsMinimum(3, 23, 6);
/*      */     
/* 3448 */     this.io.resetMaxBuf();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3458 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3459 */       String characterSetResultsOnServerMysql = (String)this.serverVariables.get("jdbc.local.character_set_results");
/*      */ 
/*      */       
/* 3462 */       if (characterSetResultsOnServerMysql == null || StringUtils.startsWithIgnoreCaseAndWs(characterSetResultsOnServerMysql, "NULL") || characterSetResultsOnServerMysql.length() == 0) {
/*      */ 
/*      */ 
/*      */         
/* 3466 */         String defaultMetadataCharsetMysql = (String)this.serverVariables.get("character_set_system");
/*      */         
/* 3468 */         String defaultMetadataCharset = null;
/*      */         
/* 3470 */         if (defaultMetadataCharsetMysql != null) {
/* 3471 */           defaultMetadataCharset = CharsetMapping.getJavaEncodingForMysqlEncoding(defaultMetadataCharsetMysql, this);
/*      */         }
/*      */         else {
/*      */           
/* 3475 */           defaultMetadataCharset = "UTF-8";
/*      */         } 
/*      */         
/* 3478 */         this.characterSetMetadata = defaultMetadataCharset;
/*      */       } else {
/* 3480 */         this.characterSetResultsOnServer = CharsetMapping.getJavaEncodingForMysqlEncoding(characterSetResultsOnServerMysql, this);
/*      */ 
/*      */         
/* 3483 */         this.characterSetMetadata = this.characterSetResultsOnServer;
/*      */       } 
/*      */     } else {
/* 3486 */       this.characterSetMetadata = getEncoding();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3493 */     if (versionMeetsMinimum(4, 1, 0) && !versionMeetsMinimum(4, 1, 10) && getAllowMultiQueries())
/*      */     {
/*      */       
/* 3496 */       if ("ON".equalsIgnoreCase((String)this.serverVariables.get("query_cache_type")) && !"0".equalsIgnoreCase((String)this.serverVariables.get("query_cache_size")))
/*      */       {
/*      */ 
/*      */         
/* 3500 */         setAllowMultiQueries(false);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3508 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */   
/*      */   private int getServerVariableAsInt(String variableName, int fallbackValue) throws SQLException {
/*      */     try {
/* 3514 */       return Integer.parseInt((String)this.serverVariables.get(variableName));
/*      */     }
/* 3516 */     catch (NumberFormatException nfe) {
/* 3517 */       getLog().logWarn(Messages.getString("Connection.BadValueInServerVariables", new Object[] { variableName, this.serverVariables.get(variableName), new Integer(fallbackValue) }));
/*      */ 
/*      */       
/* 3520 */       return fallbackValue;
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
/*      */   private boolean isAutoCommitNonDefaultOnServer() throws SQLException {
/* 3533 */     boolean overrideDefaultAutocommit = false;
/*      */     
/* 3535 */     String initConnectValue = (String)this.serverVariables.get("init_connect");
/*      */ 
/*      */     
/* 3538 */     if (versionMeetsMinimum(4, 1, 2) && initConnectValue != null && initConnectValue.length() > 0)
/*      */     {
/* 3540 */       if (!getElideSetAutoCommits()) {
/*      */         
/* 3542 */         ResultSet rs = null;
/* 3543 */         Statement stmt = null;
/*      */         
/*      */         try {
/* 3546 */           stmt = getMetadataSafeStatement();
/*      */           
/* 3548 */           rs = stmt.executeQuery("SELECT @@session.autocommit");
/*      */           
/* 3550 */           if (rs.next()) {
/* 3551 */             this.autoCommit = rs.getBoolean(1);
/* 3552 */             if (this.autoCommit != true) {
/* 3553 */               overrideDefaultAutocommit = true;
/*      */             }
/*      */           } 
/*      */         } finally {
/*      */           
/* 3558 */           if (rs != null) {
/*      */             try {
/* 3560 */               rs.close();
/* 3561 */             } catch (SQLException sqlEx) {}
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 3566 */           if (stmt != null) {
/*      */             try {
/* 3568 */               stmt.close();
/* 3569 */             } catch (SQLException sqlEx) {}
/*      */           
/*      */           }
/*      */         }
/*      */       
/*      */       }
/* 3575 */       else if (getIO().isSetNeededForAutoCommitMode(true)) {
/*      */         
/* 3577 */         this.autoCommit = false;
/* 3578 */         overrideDefaultAutocommit = true;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3583 */     return overrideDefaultAutocommit;
/*      */   }
/*      */   
/*      */   protected boolean isClientTzUTC() {
/* 3587 */     return this.isClientTzUTC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() {
/* 3596 */     return this.isClosed;
/*      */   }
/*      */   
/*      */   protected boolean isCursorFetchEnabled() throws SQLException {
/* 3600 */     return (versionMeetsMinimum(5, 0, 2) && getUseCursorFetch());
/*      */   }
/*      */   
/*      */   public boolean isInGlobalTx() {
/* 3604 */     return this.isInGlobalTx;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isMasterConnection() {
/* 3615 */     return !this.failedOver;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNoBackslashEscapesSet() {
/* 3625 */     return this.noBackslashEscapes;
/*      */   }
/*      */   
/*      */   boolean isReadInfoMsgEnabled() {
/* 3629 */     return this.readInfoMsg;
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
/*      */   public boolean isReadOnly() throws SQLException {
/* 3642 */     return this.readOnly;
/*      */   }
/*      */   
/*      */   protected boolean isRunningOnJDK13() {
/* 3646 */     return this.isRunningOnJDK13;
/*      */   }
/*      */   
/*      */   public synchronized boolean isSameResource(Connection otherConnection) {
/* 3650 */     if (otherConnection == null) {
/* 3651 */       return false;
/*      */     }
/*      */     
/* 3654 */     boolean directCompare = true;
/*      */     
/* 3656 */     String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
/* 3657 */     String otherOrigDatabase = ((ConnectionImpl)otherConnection).origDatabaseToConnectTo;
/* 3658 */     String otherCurrentCatalog = ((ConnectionImpl)otherConnection).database;
/*      */     
/* 3660 */     if (!nullSafeCompare(otherHost, this.origHostToConnectTo)) {
/* 3661 */       directCompare = false;
/* 3662 */     } else if (otherHost != null && otherHost.indexOf(',') == -1 && otherHost.indexOf(':') == -1) {
/*      */ 
/*      */       
/* 3665 */       directCompare = (((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo);
/*      */     } 
/*      */ 
/*      */     
/* 3669 */     if (directCompare) {
/* 3670 */       if (!nullSafeCompare(otherOrigDatabase, this.origDatabaseToConnectTo)) { directCompare = false;
/* 3671 */         directCompare = false; }
/* 3672 */       else if (!nullSafeCompare(otherCurrentCatalog, this.database))
/* 3673 */       { directCompare = false; }
/*      */     
/*      */     }
/*      */     
/* 3677 */     if (directCompare) {
/* 3678 */       return true;
/*      */     }
/*      */ 
/*      */     
/* 3682 */     String otherResourceId = ((ConnectionImpl)otherConnection).getResourceId();
/* 3683 */     String myResourceId = getResourceId();
/*      */     
/* 3685 */     if (otherResourceId != null || myResourceId != null) {
/* 3686 */       directCompare = nullSafeCompare(otherResourceId, myResourceId);
/*      */       
/* 3688 */       if (directCompare) {
/* 3689 */         return true;
/*      */       }
/*      */     } 
/*      */     
/* 3693 */     return false;
/*      */   }
/*      */   
/*      */   protected boolean isServerTzUTC() {
/* 3697 */     return this.isServerTzUTC;
/*      */   }
/*      */   
/* 3700 */   protected ConnectionImpl() { this.usingCachedConfig = false; } protected ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException { this.usingCachedConfig = false; this.charsetToNumBytesMap = new HashMap(); this.connectionCreationTimeMillis = System.currentTimeMillis(); this.pointOfOrigin = new Throwable(); this.origHostToConnectTo = hostToConnectTo; this.origPortToConnectTo = portToConnectTo; this.origDatabaseToConnectTo = databaseToConnectTo; try { Blob.class.getMethod("truncate", new Class[] { long.class }); this.isRunningOnJDK13 = false; } catch (NoSuchMethodException nsme) { this.isRunningOnJDK13 = true; }  this.sessionCalendar = new GregorianCalendar(); this.utcCalendar = new GregorianCalendar(); this.utcCalendar.setTimeZone(TimeZone.getTimeZone("GMT")); this.log = LogFactory.getLogger(getLogger(), "MySQL"); this.defaultTimeZone = Util.getDefaultTimeZone(); if ("GMT".equalsIgnoreCase(this.defaultTimeZone.getID())) { this.isClientTzUTC = true; } else { this.isClientTzUTC = false; }  this.openStatements = new HashMap(); this.serverVariables = new HashMap(); this.hostList = new ArrayList(); if (hostToConnectTo == null) { this.host = "localhost"; this.hostList.add(this.host); } else if (hostToConnectTo.indexOf(',') != -1) { StringTokenizer hostTokenizer = new StringTokenizer(hostToConnectTo, ",", false); while (hostTokenizer.hasMoreTokens())
/*      */         this.hostList.add(hostTokenizer.nextToken().trim());  }
/*      */     else { this.host = hostToConnectTo; this.hostList.add(this.host); }
/*      */      this.hostListSize = this.hostList.size(); this.port = portToConnectTo; if (databaseToConnectTo == null)
/*      */       databaseToConnectTo = "";  this.database = databaseToConnectTo; this.myURL = url; this.user = info.getProperty("user"); this.password = info.getProperty("password"); if (this.user == null || this.user.equals(""))
/*      */       this.user = "";  if (this.password == null)
/*      */       this.password = "";  this.props = info; initializeDriverProperties(info); try { this.dbmd = getMetaData(false, false); createNewIO(false); }
/*      */     catch (SQLException ex) { cleanup(ex); throw ex; }
/*      */     catch (Exception ex) { cleanup(ex); StringBuffer mesg = new StringBuffer(128); if (getParanoid()) { mesg.append("Cannot connect to MySQL server on "); mesg.append(this.host); mesg.append(":"); mesg.append(this.port); mesg.append(".\n\n"); mesg.append("Make sure that there is a MySQL server "); mesg.append("running on the machine/port you are trying "); mesg.append("to connect to and that the machine this software is running on "); mesg.append("is able to connect to this host/port (i.e. not firewalled). "); mesg.append("Also make sure that the server has not been started with the --skip-networking "); mesg.append("flag.\n\n"); }
/*      */       else { mesg.append("Unable to connect to database."); }
/*      */        SQLException sqlEx = SQLError.createSQLException(mesg.toString(), "08S01"); sqlEx.initCause(ex); throw sqlEx; }
/* 3711 */      } private void loadServerVariables() throws SQLException { if (getCacheServerConfiguration()) {
/* 3712 */       synchronized (serverConfigByUrl) {
/* 3713 */         Map cachedVariableMap = (Map)serverConfigByUrl.get(getURL());
/*      */         
/* 3715 */         if (cachedVariableMap != null) {
/* 3716 */           this.serverVariables = cachedVariableMap;
/* 3717 */           this.usingCachedConfig = true;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 3724 */     Statement stmt = null;
/* 3725 */     ResultSet results = null;
/*      */     
/*      */     try {
/* 3728 */       stmt = getMetadataSafeStatement();
/*      */       
/* 3730 */       String version = this.dbmd.getDriverVersion();
/*      */       
/* 3732 */       if (version != null && version.indexOf('*') != -1) {
/* 3733 */         StringBuffer buf = new StringBuffer(version.length() + 10);
/*      */         
/* 3735 */         for (int i = 0; i < version.length(); i++) {
/* 3736 */           char c = version.charAt(i);
/*      */           
/* 3738 */           if (c == '*') {
/* 3739 */             buf.append("[star]");
/*      */           } else {
/* 3741 */             buf.append(c);
/*      */           } 
/*      */         } 
/*      */         
/* 3745 */         version = buf.toString();
/*      */       } 
/*      */       
/* 3748 */       String versionComment = (getParanoid() || version == null) ? "" : ("/* " + version + " */");
/*      */ 
/*      */       
/* 3751 */       String query = versionComment + "SHOW VARIABLES";
/*      */       
/* 3753 */       if (versionMeetsMinimum(5, 0, 3)) {
/* 3754 */         query = versionComment + "SHOW VARIABLES WHERE Variable_name ='language'" + " OR Variable_name = 'net_write_timeout'" + " OR Variable_name = 'interactive_timeout'" + " OR Variable_name = 'wait_timeout'" + " OR Variable_name = 'character_set_client'" + " OR Variable_name = 'character_set_connection'" + " OR Variable_name = 'character_set'" + " OR Variable_name = 'character_set_server'" + " OR Variable_name = 'tx_isolation'" + " OR Variable_name = 'transaction_isolation'" + " OR Variable_name = 'character_set_results'" + " OR Variable_name = 'timezone'" + " OR Variable_name = 'time_zone'" + " OR Variable_name = 'system_time_zone'" + " OR Variable_name = 'lower_case_table_names'" + " OR Variable_name = 'max_allowed_packet'" + " OR Variable_name = 'net_buffer_length'" + " OR Variable_name = 'sql_mode'" + " OR Variable_name = 'query_cache_type'" + " OR Variable_name = 'query_cache_size'" + " OR Variable_name = 'init_connect'";
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3777 */       results = stmt.executeQuery(query);
/*      */       
/* 3779 */       while (results.next()) {
/* 3780 */         this.serverVariables.put(results.getString(1), results.getString(2));
/*      */       }
/*      */ 
/*      */       
/* 3784 */       if (getCacheServerConfiguration()) {
/* 3785 */         synchronized (serverConfigByUrl) {
/* 3786 */           serverConfigByUrl.put(getURL(), this.serverVariables);
/*      */         } 
/*      */       }
/* 3789 */     } catch (SQLException e) {
/* 3790 */       throw e;
/*      */     } finally {
/* 3792 */       if (results != null) {
/*      */         try {
/* 3794 */           results.close();
/* 3795 */         } catch (SQLException sqlE) {}
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 3800 */       if (stmt != null) {
/*      */         try {
/* 3802 */           stmt.close();
/* 3803 */         } catch (SQLException sqlE) {}
/*      */       }
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
/*      */   public boolean lowerCaseTableNames() {
/* 3816 */     return this.lowerCaseTableNames;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void maxRowsChanged(StatementImpl stmt) {
/* 3826 */     synchronized (this.mutex) {
/* 3827 */       if (this.statementsUsingMaxRows == null) {
/* 3828 */         this.statementsUsingMaxRows = new HashMap();
/*      */       }
/*      */       
/* 3831 */       this.statementsUsingMaxRows.put(stmt, stmt);
/*      */       
/* 3833 */       this.maxRowsChanged = true;
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
/*      */   public String nativeSQL(String sql) throws SQLException {
/* 3850 */     if (sql == null) {
/* 3851 */       return null;
/*      */     }
/*      */     
/* 3854 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), this);
/*      */ 
/*      */ 
/*      */     
/* 3858 */     if (escapedSqlResult instanceof String) {
/* 3859 */       return (String)escapedSqlResult;
/*      */     }
/*      */     
/* 3862 */     return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */   }
/*      */ 
/*      */   
/*      */   private CallableStatement parseCallableStatement(String sql) throws SQLException {
/* 3867 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), this);
/*      */ 
/*      */     
/* 3870 */     boolean isFunctionCall = false;
/* 3871 */     String parsedSql = null;
/*      */     
/* 3873 */     if (escapedSqlResult instanceof EscapeProcessorResult) {
/* 3874 */       parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/* 3875 */       isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
/*      */     } else {
/* 3877 */       parsedSql = (String)escapedSqlResult;
/* 3878 */       isFunctionCall = false;
/*      */     } 
/*      */     
/* 3881 */     return CallableStatement.getInstance(this, parsedSql, this.database, isFunctionCall);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean parserKnowsUnicode() {
/* 3891 */     return this.parserKnowsUnicode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void ping() throws SQLException {
/* 3901 */     pingInternal(true);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void pingInternal(boolean checkForClosedConnection) throws SQLException {
/* 3906 */     if (checkForClosedConnection) {
/* 3907 */       checkClosed();
/*      */     }
/*      */     
/* 3910 */     long pingMillisLifetime = getSelfDestructOnPingSecondsLifetime();
/* 3911 */     int pingMaxOperations = getSelfDestructOnPingMaxOperations();
/*      */     
/* 3913 */     if ((pingMillisLifetime > 0L && System.currentTimeMillis() - this.connectionCreationTimeMillis > pingMillisLifetime) || (pingMaxOperations > 0 && pingMaxOperations <= this.io.getCommandCount())) {
/*      */ 
/*      */ 
/*      */       
/* 3917 */       close();
/*      */       
/* 3919 */       throw SQLError.createSQLException(Messages.getString("Connection.exceededConnectionLifetime"), "08S01");
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 3924 */     this.io.sendCommand(14, null, null, false, null);
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
/*      */   public CallableStatement prepareCall(String sql) throws SQLException {
/* 3939 */     return prepareCall(sql, 1003, 1007);
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
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 3960 */     if (versionMeetsMinimum(5, 0, 0)) {
/* 3961 */       CallableStatement cStmt = null;
/*      */       
/* 3963 */       if (!getCacheCallableStatements()) {
/*      */         
/* 3965 */         cStmt = parseCallableStatement(sql);
/*      */       } else {
/* 3967 */         synchronized (this.parsedCallableStatementCache) {
/* 3968 */           CompoundCacheKey key = new CompoundCacheKey(this, getCatalog(), sql);
/*      */           
/* 3970 */           CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
/*      */ 
/*      */           
/* 3973 */           if (cachedParamInfo != null) {
/* 3974 */             cStmt = CallableStatement.getInstance(this, cachedParamInfo);
/*      */           } else {
/* 3976 */             cStmt = parseCallableStatement(sql);
/*      */             
/* 3978 */             cachedParamInfo = cStmt.paramInfo;
/*      */             
/* 3980 */             this.parsedCallableStatementCache.put(key, cachedParamInfo);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 3985 */       cStmt.setResultSetType(resultSetType);
/* 3986 */       cStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */       
/* 3988 */       return cStmt;
/*      */     } 
/*      */     
/* 3991 */     throw SQLError.createSQLException("Callable statements not supported.", "S1C00");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 4001 */     if (getPedantic() && 
/* 4002 */       resultSetHoldability != 1) {
/* 4003 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4009 */     CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */ 
/*      */     
/* 4012 */     return cStmt;
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
/*      */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/* 4042 */     return prepareStatement(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/* 4051 */     PreparedStatement pStmt = prepareStatement(sql);
/*      */     
/* 4053 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
/*      */ 
/*      */     
/* 4056 */     return pStmt;
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
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 4076 */     checkClosed();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4082 */     PreparedStatement pStmt = null;
/*      */     
/* 4084 */     boolean canServerPrepare = true;
/*      */     
/* 4086 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4088 */     if (this.useServerPreparedStmts && getEmulateUnsupportedPstmts()) {
/* 4089 */       canServerPrepare = canHandleAsServerPreparedStatement(nativeSql);
/*      */     }
/*      */     
/* 4092 */     if (this.useServerPreparedStmts && canServerPrepare) {
/* 4093 */       if (getCachePreparedStatements()) {
/* 4094 */         synchronized (this.serverSideStatementCache) {
/* 4095 */           pStmt = (ServerPreparedStatement)this.serverSideStatementCache.remove(sql);
/*      */           
/* 4097 */           if (pStmt != null) {
/* 4098 */             ((ServerPreparedStatement)pStmt).setClosed(false);
/* 4099 */             pStmt.clearParameters();
/*      */           } 
/*      */           
/* 4102 */           if (pStmt == null) {
/*      */             try {
/* 4104 */               pStmt = ServerPreparedStatement.getInstance(this, nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */               
/* 4106 */               if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 4107 */                 ((ServerPreparedStatement)pStmt).isCached = true;
/*      */               }
/*      */               
/* 4110 */               pStmt.setResultSetType(resultSetType);
/* 4111 */               pStmt.setResultSetConcurrency(resultSetConcurrency);
/* 4112 */             } catch (SQLException sqlEx) {
/*      */               
/* 4114 */               if (getEmulateUnsupportedPstmts()) {
/* 4115 */                 pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */                 
/* 4117 */                 if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 4118 */                   this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
/*      */                 }
/*      */               } else {
/* 4121 */                 throw sqlEx;
/*      */               } 
/*      */             } 
/*      */           }
/*      */         } 
/*      */       } else {
/*      */         try {
/* 4128 */           pStmt = ServerPreparedStatement.getInstance(this, nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */ 
/*      */           
/* 4131 */           pStmt.setResultSetType(resultSetType);
/* 4132 */           pStmt.setResultSetConcurrency(resultSetConcurrency);
/* 4133 */         } catch (SQLException sqlEx) {
/*      */           
/* 4135 */           if (getEmulateUnsupportedPstmts()) {
/* 4136 */             pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */           } else {
/* 4138 */             throw sqlEx;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } else {
/* 4143 */       pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */     } 
/*      */     
/* 4146 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 4155 */     if (getPedantic() && 
/* 4156 */       resultSetHoldability != 1) {
/* 4157 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4163 */     return prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/* 4171 */     PreparedStatement pStmt = prepareStatement(sql);
/*      */     
/* 4173 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
/*      */ 
/*      */ 
/*      */     
/* 4177 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/* 4185 */     PreparedStatement pStmt = prepareStatement(sql);
/*      */     
/* 4187 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
/*      */ 
/*      */ 
/*      */     
/* 4191 */     return pStmt;
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
/*      */   protected void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
/* 4206 */     SQLException sqlEx = null;
/*      */     
/* 4208 */     if (isClosed()) {
/*      */       return;
/*      */     }
/*      */     
/* 4212 */     this.forceClosedReason = reason;
/*      */     
/*      */     try {
/* 4215 */       if (!skipLocalTeardown) {
/* 4216 */         if (!getAutoCommit() && issueRollback) {
/*      */           try {
/* 4218 */             rollback();
/* 4219 */           } catch (SQLException ex) {
/* 4220 */             sqlEx = ex;
/*      */           } 
/*      */         }
/*      */         
/* 4224 */         reportMetrics();
/*      */         
/* 4226 */         if (getUseUsageAdvisor()) {
/* 4227 */           if (!calledExplicitly) {
/* 4228 */             String message = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks.";
/*      */             
/* 4230 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 4238 */           long connectionLifeTime = System.currentTimeMillis() - this.connectionCreationTimeMillis;
/*      */ 
/*      */           
/* 4241 */           if (connectionLifeTime < 500L) {
/* 4242 */             String message = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient.";
/*      */             
/* 4244 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 4254 */           closeAllOpenStatements();
/* 4255 */         } catch (SQLException ex) {
/* 4256 */           sqlEx = ex;
/*      */         } 
/*      */         
/* 4259 */         if (this.io != null) {
/*      */           try {
/* 4261 */             this.io.quit();
/* 4262 */           } catch (Exception e) {}
/*      */         
/*      */         }
/*      */       }
/*      */       else {
/*      */         
/* 4268 */         this.io.forceClose();
/*      */       } 
/*      */     } finally {
/* 4271 */       this.openStatements = null;
/* 4272 */       this.io = null;
/* 4273 */       ProfilerEventHandlerFactory.removeInstance(this);
/* 4274 */       this.isClosed = true;
/*      */     } 
/*      */     
/* 4277 */     if (sqlEx != null) {
/* 4278 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void recachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
/* 4284 */     if (pstmt.isPoolable()) {
/* 4285 */       synchronized (this.serverSideStatementCache) {
/* 4286 */         this.serverSideStatementCache.put(pstmt.originalSql, pstmt);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void registerQueryExecutionTime(long queryTimeMs) {
/* 4297 */     if (queryTimeMs > this.longestQueryTimeMs) {
/* 4298 */       this.longestQueryTimeMs = queryTimeMs;
/*      */       
/* 4300 */       repartitionPerformanceHistogram();
/*      */     } 
/*      */     
/* 4303 */     addToPerformanceHistogram(queryTimeMs, 1);
/*      */     
/* 4305 */     if (queryTimeMs < this.shortestQueryTimeMs) {
/* 4306 */       this.shortestQueryTimeMs = (queryTimeMs == 0L) ? 1L : queryTimeMs;
/*      */     }
/*      */     
/* 4309 */     this.numberOfQueriesIssued++;
/*      */     
/* 4311 */     this.totalQueryTimeMs += queryTimeMs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void registerStatement(StatementImpl stmt) {
/* 4321 */     synchronized (this.openStatements) {
/* 4322 */       this.openStatements.put(stmt, stmt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void releaseSavepoint(Savepoint arg0) throws SQLException {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound) {
/* 4336 */     if (this.oldHistCounts == null) {
/* 4337 */       this.oldHistCounts = new int[histCounts.length];
/* 4338 */       this.oldHistBreakpoints = new long[histBreakpoints.length];
/*      */     } 
/*      */     
/* 4341 */     System.arraycopy(histCounts, 0, this.oldHistCounts, 0, histCounts.length);
/*      */     
/* 4343 */     System.arraycopy(histBreakpoints, 0, this.oldHistBreakpoints, 0, histBreakpoints.length);
/*      */ 
/*      */     
/* 4346 */     createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
/*      */ 
/*      */     
/* 4349 */     for (int i = 0; i < 20; i++) {
/* 4350 */       addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void repartitionPerformanceHistogram() {
/* 4356 */     checkAndCreatePerformanceHistogram();
/*      */     
/* 4358 */     repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, (this.shortestQueryTimeMs == Long.MAX_VALUE) ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void repartitionTablesAccessedHistogram() {
/* 4365 */     checkAndCreateTablesAccessedHistogram();
/*      */     
/* 4367 */     repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, (this.minimumNumberTablesAccessed == Long.MAX_VALUE) ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void reportMetrics() {
/* 4375 */     if (getGatherPerformanceMetrics()) {
/* 4376 */       StringBuffer logMessage = new StringBuffer(256);
/*      */       
/* 4378 */       logMessage.append("** Performance Metrics Report **\n");
/* 4379 */       logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
/*      */       
/* 4381 */       logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
/*      */       
/* 4383 */       logMessage.append("\nAverage query execution time: " + (this.totalQueryTimeMs / this.numberOfQueriesIssued) + " ms");
/*      */ 
/*      */ 
/*      */       
/* 4387 */       logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
/*      */       
/* 4389 */       logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
/*      */       
/* 4391 */       logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
/*      */       
/* 4393 */       logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
/*      */ 
/*      */       
/* 4396 */       if (this.perfMetricsHistBreakpoints != null) {
/* 4397 */         logMessage.append("\n\n\tTiming Histogram:\n");
/* 4398 */         int maxNumPoints = 20;
/* 4399 */         int highestCount = Integer.MIN_VALUE;
/*      */         int i;
/* 4401 */         for (i = 0; i < 20; i++) {
/* 4402 */           if (this.perfMetricsHistCounts[i] > highestCount) {
/* 4403 */             highestCount = this.perfMetricsHistCounts[i];
/*      */           }
/*      */         } 
/*      */         
/* 4407 */         if (highestCount == 0) {
/* 4408 */           highestCount = 1;
/*      */         }
/*      */         
/* 4411 */         for (i = 0; i < 19; i++) {
/*      */           
/* 4413 */           if (i == 0) {
/* 4414 */             logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */           else {
/*      */             
/* 4418 */             logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 4424 */           logMessage.append("\t");
/*      */           
/* 4426 */           int numPointsToGraph = (int)(maxNumPoints * this.perfMetricsHistCounts[i] / highestCount);
/*      */           
/* 4428 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4429 */             logMessage.append("*");
/*      */           }
/*      */           
/* 4432 */           if (this.longestQueryTimeMs < this.perfMetricsHistCounts[i + 1]) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */         
/* 4437 */         if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
/* 4438 */           logMessage.append("\n\tbetween ");
/* 4439 */           logMessage.append(this.perfMetricsHistBreakpoints[18]);
/*      */           
/* 4441 */           logMessage.append(" and ");
/* 4442 */           logMessage.append(this.perfMetricsHistBreakpoints[19]);
/*      */           
/* 4444 */           logMessage.append(" ms: \t");
/* 4445 */           logMessage.append(this.perfMetricsHistCounts[19]);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 4450 */       if (this.numTablesMetricsHistBreakpoints != null) {
/* 4451 */         logMessage.append("\n\n\tTable Join Histogram:\n");
/* 4452 */         int maxNumPoints = 20;
/* 4453 */         int highestCount = Integer.MIN_VALUE;
/*      */         int i;
/* 4455 */         for (i = 0; i < 20; i++) {
/* 4456 */           if (this.numTablesMetricsHistCounts[i] > highestCount) {
/* 4457 */             highestCount = this.numTablesMetricsHistCounts[i];
/*      */           }
/*      */         } 
/*      */         
/* 4461 */         if (highestCount == 0) {
/* 4462 */           highestCount = 1;
/*      */         }
/*      */         
/* 4465 */         for (i = 0; i < 19; i++) {
/*      */           
/* 4467 */           if (i == 0) {
/* 4468 */             logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[i + 1] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 4473 */             logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[i + 1] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 4481 */           logMessage.append("\t");
/*      */           
/* 4483 */           int numPointsToGraph = (int)(maxNumPoints * this.numTablesMetricsHistCounts[i] / highestCount);
/*      */           
/* 4485 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4486 */             logMessage.append("*");
/*      */           }
/*      */           
/* 4489 */           if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[i + 1]) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */         
/* 4494 */         if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
/* 4495 */           logMessage.append("\n\tbetween ");
/* 4496 */           logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
/*      */           
/* 4498 */           logMessage.append(" and ");
/* 4499 */           logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
/*      */           
/* 4501 */           logMessage.append(" tables: ");
/* 4502 */           logMessage.append(this.numTablesMetricsHistCounts[19]);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 4507 */       this.log.logInfo(logMessage);
/*      */       
/* 4509 */       this.metricsLastReportedMs = System.currentTimeMillis();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void reportMetricsIfNeeded() {
/* 4518 */     if (getGatherPerformanceMetrics() && 
/* 4519 */       System.currentTimeMillis() - this.metricsLastReportedMs > getReportMetricsIntervalMillis()) {
/* 4520 */       reportMetrics();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void reportNumberOfTablesAccessed(int numTablesAccessed) {
/* 4526 */     if (numTablesAccessed < this.minimumNumberTablesAccessed) {
/* 4527 */       this.minimumNumberTablesAccessed = numTablesAccessed;
/*      */     }
/*      */     
/* 4530 */     if (numTablesAccessed > this.maximumNumberTablesAccessed) {
/* 4531 */       this.maximumNumberTablesAccessed = numTablesAccessed;
/*      */       
/* 4533 */       repartitionTablesAccessedHistogram();
/*      */     } 
/*      */     
/* 4536 */     addToTablesAccessedHistogram(numTablesAccessed, 1);
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
/*      */   public void resetServerState() throws SQLException {
/* 4548 */     if (!getParanoid() && this.io != null && versionMeetsMinimum(4, 0, 6))
/*      */     {
/* 4550 */       changeUser(this.user, this.password);
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
/*      */   public void rollback() throws SQLException {
/* 4564 */     synchronized (getMutex()) {
/* 4565 */       checkClosed();
/*      */       
/*      */       try {
/* 4568 */         if (this.connectionLifecycleInterceptors != null) {
/* 4569 */           IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator()) { private final ConnectionImpl this$0;
/*      */               
/*      */               void forEach(Object each) throws SQLException {
/* 4572 */                 if (!((ConnectionLifecycleInterceptor)each).rollback()) {
/* 4573 */                   this.stopIterating = true;
/*      */                 }
/*      */               } }
/*      */             ;
/*      */           
/* 4578 */           iter.doForAll();
/*      */           
/* 4580 */           if (!iter.fullIteration()) {
/*      */             return;
/*      */           }
/*      */         } 
/*      */         
/* 4585 */         if (this.autoCommit && !getRelaxAutoCommit()) {
/* 4586 */           throw SQLError.createSQLException("Can't call rollback when autocommit=true", "08003");
/*      */         }
/*      */         
/* 4589 */         if (this.transactionsSupported) {
/*      */           try {
/* 4591 */             rollbackNoChecks();
/* 4592 */           } catch (SQLException sqlEx) {
/*      */             
/* 4594 */             if (getIgnoreNonTxTables() && sqlEx.getErrorCode() != 1196)
/*      */             {
/* 4596 */               throw sqlEx;
/*      */             }
/*      */           } 
/*      */         }
/* 4600 */       } catch (SQLException sqlException) {
/* 4601 */         if ("08S01".equals(sqlException.getSQLState()))
/*      */         {
/* 4603 */           throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007");
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 4608 */         throw sqlException;
/*      */       } finally {
/* 4610 */         this.needsPing = getReconnectAtTxEnd();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollback(Savepoint savepoint) throws SQLException {
/* 4620 */     if (versionMeetsMinimum(4, 0, 14) || versionMeetsMinimum(4, 1, 1)) {
/* 4621 */       synchronized (getMutex()) {
/* 4622 */         checkClosed();
/*      */         
/*      */         try {
/* 4625 */           if (this.connectionLifecycleInterceptors != null) {
/* 4626 */             IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator(), savepoint) { private final Savepoint val$savepoint; private final ConnectionImpl this$0;
/*      */                 
/*      */                 void forEach(Object each) throws SQLException {
/* 4629 */                   if (!((ConnectionLifecycleInterceptor)each).rollback(this.val$savepoint)) {
/* 4630 */                     this.stopIterating = true;
/*      */                   }
/*      */                 } }
/*      */               ;
/*      */             
/* 4635 */             iter.doForAll();
/*      */             
/* 4637 */             if (!iter.fullIteration()) {
/*      */               return;
/*      */             }
/*      */           } 
/*      */           
/* 4642 */           StringBuffer rollbackQuery = new StringBuffer("ROLLBACK TO SAVEPOINT ");
/*      */           
/* 4644 */           rollbackQuery.append('`');
/* 4645 */           rollbackQuery.append(savepoint.getSavepointName());
/* 4646 */           rollbackQuery.append('`');
/*      */           
/* 4648 */           Statement stmt = null;
/*      */           
/*      */           try {
/* 4651 */             stmt = getMetadataSafeStatement();
/*      */             
/* 4653 */             stmt.executeUpdate(rollbackQuery.toString());
/* 4654 */           } catch (SQLException sqlEx) {
/* 4655 */             int errno = sqlEx.getErrorCode();
/*      */             
/* 4657 */             if (errno == 1181) {
/* 4658 */               String msg = sqlEx.getMessage();
/*      */               
/* 4660 */               if (msg != null) {
/* 4661 */                 int indexOfError153 = msg.indexOf("153");
/*      */                 
/* 4663 */                 if (indexOfError153 != -1) {
/* 4664 */                   throw SQLError.createSQLException("Savepoint '" + savepoint.getSavepointName() + "' does not exist", "S1009", errno);
/*      */                 }
/*      */               } 
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 4674 */             if (getIgnoreNonTxTables() && sqlEx.getErrorCode() != 1196)
/*      */             {
/* 4676 */               throw sqlEx;
/*      */             }
/*      */             
/* 4679 */             if ("08S01".equals(sqlEx.getSQLState()))
/*      */             {
/* 4681 */               throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007");
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 4686 */             throw sqlEx;
/*      */           } finally {
/* 4688 */             closeStatement(stmt);
/*      */           } 
/*      */         } finally {
/* 4691 */           this.needsPing = getReconnectAtTxEnd();
/*      */         } 
/*      */       } 
/*      */     } else {
/* 4695 */       throw SQLError.notImplemented();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void rollbackNoChecks() throws SQLException {
/* 4700 */     if (getUseLocalSessionState() && versionMeetsMinimum(5, 0, 0) && 
/* 4701 */       !this.io.inTransactionOnServer()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 4706 */     execSQL((StatementImpl)null, "rollback", -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
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
/*      */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/* 4718 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4720 */     return ServerPreparedStatement.getInstance(this, nativeSql, getCatalog(), 1005, 1007);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/* 4730 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4732 */     PreparedStatement pStmt = ServerPreparedStatement.getInstance(this, nativeSql, getCatalog(), 1005, 1007);
/*      */ 
/*      */ 
/*      */     
/* 4736 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
/*      */ 
/*      */     
/* 4739 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 4747 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4749 */     return ServerPreparedStatement.getInstance(this, nativeSql, getCatalog(), resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 4760 */     if (getPedantic() && 
/* 4761 */       resultSetHoldability != 1) {
/* 4762 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4768 */     return serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/* 4777 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */     
/* 4779 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
/*      */ 
/*      */ 
/*      */     
/* 4783 */     return pStmt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/* 4791 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */     
/* 4793 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
/*      */ 
/*      */ 
/*      */     
/* 4797 */     return pStmt;
/*      */   }
/*      */   
/*      */   protected boolean serverSupportsConvertFn() throws SQLException {
/* 4801 */     return versionMeetsMinimum(4, 0, 2);
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
/*      */   public void setAutoCommit(boolean autoCommitFlag) throws SQLException {
/* 4827 */     synchronized (getMutex()) {
/* 4828 */       checkClosed();
/*      */       
/* 4830 */       if (this.connectionLifecycleInterceptors != null) {
/* 4831 */         IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator(), autoCommitFlag) { private final boolean val$autoCommitFlag; private final ConnectionImpl this$0;
/*      */             
/*      */             void forEach(Object each) throws SQLException {
/* 4834 */               if (!((ConnectionLifecycleInterceptor)each).setAutoCommit(this.val$autoCommitFlag)) {
/* 4835 */                 this.stopIterating = true;
/*      */               }
/*      */             } }
/*      */           ;
/*      */         
/* 4840 */         iter.doForAll();
/*      */         
/* 4842 */         if (!iter.fullIteration()) {
/*      */           return;
/*      */         }
/*      */       } 
/*      */       
/* 4847 */       if (getAutoReconnectForPools()) {
/* 4848 */         setHighAvailability(true);
/*      */       }
/*      */       
/*      */       try {
/* 4852 */         if (this.transactionsSupported) {
/*      */           
/* 4854 */           boolean needsSetOnServer = true;
/*      */           
/* 4856 */           if (getUseLocalSessionState() && this.autoCommit == autoCommitFlag) {
/*      */             
/* 4858 */             needsSetOnServer = false;
/* 4859 */           } else if (!getHighAvailability()) {
/* 4860 */             needsSetOnServer = getIO().isSetNeededForAutoCommitMode(autoCommitFlag);
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
/* 4871 */           this.autoCommit = autoCommitFlag;
/*      */           
/* 4873 */           if (needsSetOnServer) {
/* 4874 */             execSQL((StatementImpl)null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 4882 */           if (!autoCommitFlag && !getRelaxAutoCommit()) {
/* 4883 */             throw SQLError.createSQLException("MySQL Versions Older than 3.23.15 do not support transactions", "08003");
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 4888 */           this.autoCommit = autoCommitFlag;
/*      */         } 
/*      */       } finally {
/* 4891 */         if (getAutoReconnectForPools()) {
/* 4892 */           setHighAvailability(false);
/*      */         }
/*      */       } 
/*      */       return;
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
/*      */   public void setCatalog(String catalog) throws SQLException {
/* 4914 */     synchronized (getMutex()) {
/* 4915 */       checkClosed();
/*      */       
/* 4917 */       if (catalog == null) {
/* 4918 */         throw SQLError.createSQLException("Catalog can not be null", "S1009");
/*      */       }
/*      */ 
/*      */       
/* 4922 */       if (this.connectionLifecycleInterceptors != null) {
/* 4923 */         IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator(), catalog) { private final String val$catalog; private final ConnectionImpl this$0;
/*      */             
/*      */             void forEach(Object each) throws SQLException {
/* 4926 */               if (!((ConnectionLifecycleInterceptor)each).setCatalog(this.val$catalog)) {
/* 4927 */                 this.stopIterating = true;
/*      */               }
/*      */             } }
/*      */           ;
/*      */         
/* 4932 */         iter.doForAll();
/*      */         
/* 4934 */         if (!iter.fullIteration()) {
/*      */           return;
/*      */         }
/*      */       } 
/*      */       
/* 4939 */       if (getUseLocalSessionState()) {
/* 4940 */         if (this.lowerCaseTableNames) {
/* 4941 */           if (this.database.equalsIgnoreCase(catalog)) {
/*      */             return;
/*      */           }
/*      */         }
/* 4945 */         else if (this.database.equals(catalog)) {
/*      */           return;
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/* 4951 */       String quotedId = this.dbmd.getIdentifierQuoteString();
/*      */       
/* 4953 */       if (quotedId == null || quotedId.equals(" ")) {
/* 4954 */         quotedId = "";
/*      */       }
/*      */       
/* 4957 */       StringBuffer query = new StringBuffer("USE ");
/* 4958 */       query.append(quotedId);
/* 4959 */       query.append(catalog);
/* 4960 */       query.append(quotedId);
/*      */       
/* 4962 */       execSQL((StatementImpl)null, query.toString(), -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4967 */       this.database = catalog;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFailedOver(boolean flag) {
/* 4976 */     if (flag && getRoundRobinLoadBalance()) {
/*      */       return;
/*      */     }
/*      */     
/* 4980 */     this.failedOver = flag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setFailedOverState() throws SQLException {
/* 4990 */     if (getRoundRobinLoadBalance()) {
/*      */       return;
/*      */     }
/*      */     
/* 4994 */     if (getFailOverReadOnly()) {
/* 4995 */       setReadOnlyInternal(true);
/*      */     }
/*      */     
/* 4998 */     this.queriesIssuedFailedOver = 0L;
/* 4999 */     this.failedOver = true;
/* 5000 */     this.masterFailTimeMillis = System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHoldability(int arg0) throws SQLException {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInGlobalTx(boolean flag) {
/* 5011 */     this.isInGlobalTx = flag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPreferSlaveDuringFailover(boolean flag) {
/* 5020 */     this.preferSlaveDuringFailover = flag;
/*      */   }
/*      */   
/*      */   void setReadInfoMsgEnabled(boolean flag) {
/* 5024 */     this.readInfoMsg = flag;
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
/*      */   public void setReadOnly(boolean readOnlyFlag) throws SQLException {
/* 5038 */     checkClosed();
/*      */ 
/*      */ 
/*      */     
/* 5042 */     if (this.failedOver && getFailOverReadOnly() && !readOnlyFlag) {
/*      */       return;
/*      */     }
/*      */     
/* 5046 */     setReadOnlyInternal(readOnlyFlag);
/*      */   }
/*      */   
/*      */   protected void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
/* 5050 */     this.readOnly = readOnlyFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Savepoint setSavepoint() throws SQLException {
/* 5057 */     MysqlSavepoint savepoint = new MysqlSavepoint();
/*      */     
/* 5059 */     setSavepoint(savepoint);
/*      */     
/* 5061 */     return savepoint;
/*      */   }
/*      */ 
/*      */   
/*      */   private void setSavepoint(MysqlSavepoint savepoint) throws SQLException {
/* 5066 */     if (versionMeetsMinimum(4, 0, 14) || versionMeetsMinimum(4, 1, 1)) {
/* 5067 */       synchronized (getMutex()) {
/* 5068 */         checkClosed();
/*      */         
/* 5070 */         StringBuffer savePointQuery = new StringBuffer("SAVEPOINT ");
/* 5071 */         savePointQuery.append('`');
/* 5072 */         savePointQuery.append(savepoint.getSavepointName());
/* 5073 */         savePointQuery.append('`');
/*      */         
/* 5075 */         Statement stmt = null;
/*      */         
/*      */         try {
/* 5078 */           stmt = getMetadataSafeStatement();
/*      */           
/* 5080 */           stmt.executeUpdate(savePointQuery.toString());
/*      */         } finally {
/* 5082 */           closeStatement(stmt);
/*      */         } 
/*      */       } 
/*      */     } else {
/* 5086 */       throw SQLError.notImplemented();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Savepoint setSavepoint(String name) throws SQLException {
/* 5094 */     MysqlSavepoint savepoint = new MysqlSavepoint(name);
/*      */     
/* 5096 */     setSavepoint(savepoint);
/*      */     
/* 5098 */     return savepoint;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setSessionVariables() throws SQLException {
/* 5105 */     if (versionMeetsMinimum(4, 0, 0) && getSessionVariables() != null) {
/* 5106 */       List variablesToSet = StringUtils.split(getSessionVariables(), ",", "\"'", "\"'", false);
/*      */ 
/*      */       
/* 5109 */       int numVariablesToSet = variablesToSet.size();
/*      */       
/* 5111 */       Statement stmt = null;
/*      */       
/*      */       try {
/* 5114 */         stmt = getMetadataSafeStatement();
/*      */         
/* 5116 */         for (int i = 0; i < numVariablesToSet; i++) {
/* 5117 */           String variableValuePair = variablesToSet.get(i);
/*      */           
/* 5119 */           if (variableValuePair.startsWith("@")) {
/* 5120 */             stmt.executeUpdate("SET " + variableValuePair);
/*      */           } else {
/* 5122 */             stmt.executeUpdate("SET SESSION " + variableValuePair);
/*      */           } 
/*      */         } 
/*      */       } finally {
/* 5126 */         if (stmt != null) {
/* 5127 */           stmt.close();
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
/*      */   public synchronized void setTransactionIsolation(int level) throws SQLException {
/* 5143 */     checkClosed();
/*      */     
/* 5145 */     if (this.hasIsolationLevels) {
/* 5146 */       String sql = null;
/*      */       
/* 5148 */       boolean shouldSendSet = false;
/*      */       
/* 5150 */       if (getAlwaysSendSetIsolation()) {
/* 5151 */         shouldSendSet = true;
/*      */       }
/* 5153 */       else if (level != this.isolationLevel) {
/* 5154 */         shouldSendSet = true;
/*      */       } 
/*      */ 
/*      */       
/* 5158 */       if (getUseLocalSessionState()) {
/* 5159 */         shouldSendSet = (this.isolationLevel != level);
/*      */       }
/*      */       
/* 5162 */       if (shouldSendSet) {
/* 5163 */         switch (level) {
/*      */           case 0:
/* 5165 */             throw SQLError.createSQLException("Transaction isolation level NONE not supported by MySQL");
/*      */ 
/*      */           
/*      */           case 2:
/* 5169 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
/*      */             break;
/*      */ 
/*      */           
/*      */           case 1:
/* 5174 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
/*      */             break;
/*      */ 
/*      */           
/*      */           case 4:
/* 5179 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
/*      */             break;
/*      */ 
/*      */           
/*      */           case 8:
/* 5184 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
/*      */             break;
/*      */ 
/*      */           
/*      */           default:
/* 5189 */             throw SQLError.createSQLException("Unsupported transaction isolation level '" + level + "'", "S1C00");
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 5194 */         execSQL((StatementImpl)null, sql, -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5199 */         this.isolationLevel = level;
/*      */       } 
/*      */     } else {
/* 5202 */       throw SQLError.createSQLException("Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", "S1C00");
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
/*      */   public synchronized void setTypeMap(Map map) throws SQLException {
/* 5218 */     this.typeMap = map;
/*      */   }
/*      */   
/*      */   private void setupServerForTruncationChecks() throws SQLException {
/* 5222 */     if (getJdbcCompliantTruncation() && 
/* 5223 */       versionMeetsMinimum(5, 0, 2)) {
/* 5224 */       String currentSqlMode = (String)this.serverVariables.get("sql_mode");
/*      */ 
/*      */       
/* 5227 */       boolean strictTransTablesIsSet = (StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1);
/*      */       
/* 5229 */       if (currentSqlMode == null || currentSqlMode.length() == 0 || !strictTransTablesIsSet) {
/*      */         
/* 5231 */         StringBuffer commandBuf = new StringBuffer("SET sql_mode='");
/*      */         
/* 5233 */         if (currentSqlMode != null && currentSqlMode.length() > 0) {
/* 5234 */           commandBuf.append(currentSqlMode);
/* 5235 */           commandBuf.append(",");
/*      */         } 
/*      */         
/* 5238 */         commandBuf.append("STRICT_TRANS_TABLES'");
/*      */         
/* 5240 */         execSQL((StatementImpl)null, commandBuf.toString(), -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5245 */         setJdbcCompliantTruncation(false);
/* 5246 */       } else if (strictTransTablesIsSet) {
/*      */         
/* 5248 */         setJdbcCompliantTruncation(false);
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
/*      */   private boolean shouldFallBack() {
/* 5263 */     long secondsSinceFailedOver = (System.currentTimeMillis() - this.masterFailTimeMillis) / 1000L;
/*      */ 
/*      */     
/* 5266 */     boolean tryFallback = (secondsSinceFailedOver >= getSecondsBeforeRetryMaster() || this.queriesIssuedFailedOver >= getQueriesBeforeRetryMaster());
/*      */     
/* 5268 */     return tryFallback;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdownServer() throws SQLException {
/*      */     try {
/* 5279 */       this.io.sendCommand(8, null, null, false, null);
/* 5280 */     } catch (Exception ex) {
/* 5281 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000");
/*      */ 
/*      */ 
/*      */       
/* 5285 */       sqlEx.initCause(ex);
/*      */       
/* 5287 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsIsolationLevel() {
/* 5297 */     return this.hasIsolationLevels;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsQuotedIdentifiers() {
/* 5306 */     return this.hasQuotedIdentifiers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTransactions() {
/* 5315 */     return this.transactionsSupported;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void unregisterStatement(StatementImpl stmt) {
/* 5325 */     if (this.openStatements != null) {
/* 5326 */       synchronized (this.openStatements) {
/* 5327 */         this.openStatements.remove(stmt);
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
/*      */   void unsetMaxRows(StatementImpl stmt) throws SQLException {
/* 5343 */     synchronized (this.mutex) {
/* 5344 */       if (this.statementsUsingMaxRows != null) {
/* 5345 */         Object found = this.statementsUsingMaxRows.remove(stmt);
/*      */         
/* 5347 */         if (found != null && this.statementsUsingMaxRows.size() == 0) {
/*      */           
/* 5349 */           execSQL((StatementImpl)null, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, (Buffer)null, 1003, 1007, false, this.database, (Field[])null, false);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5354 */           this.maxRowsChanged = false;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   boolean useAnsiQuotedIdentifiers() {
/* 5361 */     return this.useAnsiQuotes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean useMaxRows() {
/* 5370 */     synchronized (this.mutex) {
/* 5371 */       return this.maxRowsChanged;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
/* 5377 */     checkClosed();
/*      */     
/* 5379 */     return this.io.versionMeetsMinimum(major, minor, subminor);
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
/*      */   protected CachedResultSetMetaData getCachedMetaData(String sql) {
/* 5397 */     if (this.resultSetMetadataCache != null) {
/* 5398 */       synchronized (this.resultSetMetadataCache) {
/* 5399 */         return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 5404 */     return null;
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
/*      */   protected void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
/* 5425 */     if (cachedMetaData == null) {
/*      */ 
/*      */       
/* 5428 */       cachedMetaData = new CachedResultSetMetaData();
/*      */ 
/*      */ 
/*      */       
/* 5432 */       resultSet.buildIndexMapping();
/* 5433 */       resultSet.initializeWithMetadata();
/*      */       
/* 5435 */       if (resultSet instanceof UpdatableResultSet) {
/* 5436 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */       }
/*      */       
/* 5439 */       resultSet.populateCachedMetaData(cachedMetaData);
/*      */       
/* 5441 */       this.resultSetMetadataCache.put(sql, cachedMetaData);
/*      */     } else {
/* 5443 */       resultSet.initializeFromCachedMetaData(cachedMetaData);
/* 5444 */       resultSet.initializeWithMetadata();
/*      */       
/* 5446 */       if (resultSet instanceof UpdatableResultSet) {
/* 5447 */         ((UpdatableResultSet)resultSet).checkUpdatability();
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
/*      */   public String getStatementComment() {
/* 5460 */     return this.statementComment;
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
/*      */   public void setStatementComment(String comment) {
/* 5472 */     this.statementComment = comment;
/*      */   }
/*      */   
/*      */   public synchronized void reportQueryTime(long millisOrNanos) {
/* 5476 */     this.queryTimeCount++;
/* 5477 */     this.queryTimeSum += millisOrNanos;
/* 5478 */     this.queryTimeSumSquares += (millisOrNanos * millisOrNanos);
/* 5479 */     this.queryTimeMean = (this.queryTimeMean * (this.queryTimeCount - 1L) + millisOrNanos) / this.queryTimeCount;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos) {
/* 5484 */     if (this.queryTimeCount < 15L) {
/* 5485 */       return false;
/*      */     }
/*      */     
/* 5488 */     double stddev = Math.sqrt((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / this.queryTimeCount) / (this.queryTimeCount - 1L));
/*      */     
/* 5490 */     return (millisOrNanos > this.queryTimeMean + 5.0D * stddev);
/*      */   }
/*      */   
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 5494 */     ex.init(this, this.props);
/*      */   }
/*      */   
/*      */   protected void transactionBegun() throws SQLException {
/* 5498 */     if (this.connectionLifecycleInterceptors != null) {
/* 5499 */       IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator()) { private final ConnectionImpl this$0;
/*      */           
/*      */           void forEach(Object each) throws SQLException {
/* 5502 */             ((ConnectionLifecycleInterceptor)each).transactionBegun();
/*      */           } }
/*      */         ;
/*      */       
/* 5506 */       iter.doForAll();
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void transactionCompleted() throws SQLException {
/* 5511 */     if (this.connectionLifecycleInterceptors != null) {
/* 5512 */       IterateBlock iter = new IterateBlock(this, this.connectionLifecycleInterceptors.iterator()) { private final ConnectionImpl this$0;
/*      */           
/*      */           void forEach(Object each) throws SQLException {
/* 5515 */             ((ConnectionLifecycleInterceptor)each).transactionCompleted();
/*      */           } }
/*      */         ;
/*      */       
/* 5519 */       iter.doForAll();
/*      */     } 
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ConnectionImpl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */