/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import com.mysql.jdbc.util.ReadAheadInputStream;
/*      */ import com.mysql.jdbc.util.ResultSetUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.Deflater;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class MysqlIO
/*      */ {
/*      */   private static final int UTF8_CHARSET_INDEX = 33;
/*      */   private static final String CODE_PAGE_1252 = "Cp1252";
/*      */   protected static final int NULL_LENGTH = -1;
/*      */   protected static final int COMP_HEADER_LENGTH = 3;
/*      */   protected static final int MIN_COMPRESS_LEN = 50;
/*      */   protected static final int HEADER_LENGTH = 4;
/*      */   protected static final int AUTH_411_OVERHEAD = 33;
/*   75 */   private static int maxBufferSize = 65535;
/*      */   
/*      */   private static final int CLIENT_COMPRESS = 32;
/*      */   
/*      */   protected static final int CLIENT_CONNECT_WITH_DB = 8;
/*      */   
/*      */   private static final int CLIENT_FOUND_ROWS = 2;
/*      */   
/*      */   private static final int CLIENT_LOCAL_FILES = 128;
/*      */   
/*      */   private static final int CLIENT_LONG_FLAG = 4;
/*      */   
/*      */   private static final int CLIENT_LONG_PASSWORD = 1;
/*      */   
/*      */   private static final int CLIENT_PROTOCOL_41 = 512;
/*      */   
/*      */   private static final int CLIENT_INTERACTIVE = 1024;
/*      */   
/*      */   protected static final int CLIENT_SSL = 2048;
/*      */   
/*      */   private static final int CLIENT_TRANSACTIONS = 8192;
/*      */   protected static final int CLIENT_RESERVED = 16384;
/*      */   protected static final int CLIENT_SECURE_CONNECTION = 32768;
/*      */   private static final int CLIENT_MULTI_QUERIES = 65536;
/*      */   private static final int CLIENT_MULTI_RESULTS = 131072;
/*      */   private static final int SERVER_STATUS_IN_TRANS = 1;
/*      */   private static final int SERVER_STATUS_AUTOCOMMIT = 2;
/*      */   static final int SERVER_MORE_RESULTS_EXISTS = 8;
/*      */   private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
/*      */   private static final int SERVER_QUERY_NO_INDEX_USED = 32;
/*      */   private static final int SERVER_STATUS_CURSOR_EXISTS = 64;
/*      */   private static final String FALSE_SCRAMBLE = "xxxxxxxx";
/*      */   protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
/*      */   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
/*      */   protected static final int INITIAL_PACKET_SIZE = 1024;
/*  110 */   private static String jvmPlatformCharset = null;
/*      */   
/*      */   protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
/*      */   
/*      */   protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
/*      */   
/*      */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*      */ 
/*      */   
/*      */   static {
/*  120 */     OutputStreamWriter outWriter = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  128 */       outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
/*  129 */       jvmPlatformCharset = outWriter.getEncoding();
/*      */     } finally {
/*      */       try {
/*  132 */         if (outWriter != null) {
/*  133 */           outWriter.close();
/*      */         }
/*  135 */       } catch (IOException ioEx) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean packetSequenceReset = false;
/*      */ 
/*      */ 
/*      */   
/*      */   protected int serverCharsetIndex;
/*      */ 
/*      */ 
/*      */   
/*  151 */   private Buffer reusablePacket = null;
/*  152 */   private Buffer sendPacket = null;
/*  153 */   private Buffer sharedSendPacket = null;
/*      */ 
/*      */   
/*  156 */   protected BufferedOutputStream mysqlOutput = null;
/*      */   protected ConnectionImpl connection;
/*  158 */   private Deflater deflater = null;
/*  159 */   protected InputStream mysqlInput = null;
/*  160 */   private LinkedList packetDebugRingBuffer = null;
/*  161 */   private RowData streamingData = null;
/*      */ 
/*      */   
/*  164 */   protected Socket mysqlConnection = null;
/*  165 */   private SocketFactory socketFactory = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SoftReference loadFileBufRef;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SoftReference splitBufRef;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  181 */   protected String host = null;
/*      */   protected String seed;
/*  183 */   private String serverVersion = null;
/*  184 */   private String socketFactoryClassName = null;
/*  185 */   private byte[] packetHeaderBuf = new byte[4];
/*      */   
/*      */   private boolean colDecimalNeedsBump = false;
/*      */   
/*      */   private boolean hadWarnings = false;
/*      */   
/*      */   private boolean has41NewNewProt = false;
/*      */   
/*      */   private boolean hasLongColumnInfo = false;
/*      */   
/*      */   private boolean isInteractiveClient = false;
/*      */   
/*      */   private boolean logSlowQueries = false;
/*      */   
/*      */   private boolean platformDbCharsetMatches = true;
/*      */   
/*      */   private boolean profileSql = false;
/*      */   
/*      */   private boolean queryBadIndexUsed = false;
/*      */   private boolean queryNoIndexUsed = false;
/*      */   private boolean use41Extensions = false;
/*      */   private boolean useCompression = false;
/*      */   private boolean useNewLargePackets = false;
/*      */   private boolean useNewUpdateCounts = false;
/*  209 */   private byte packetSequence = 0;
/*  210 */   private byte readPacketSequence = -1;
/*      */   private boolean checkPacketSequence = false;
/*  212 */   private byte protocolVersion = 0;
/*  213 */   private int maxAllowedPacket = 1048576;
/*  214 */   protected int maxThreeBytes = 16581375;
/*  215 */   protected int port = 3306;
/*      */   protected int serverCapabilities;
/*  217 */   private int serverMajorVersion = 0;
/*  218 */   private int serverMinorVersion = 0;
/*  219 */   private int oldServerStatus = 0;
/*  220 */   private int serverStatus = 0;
/*  221 */   private int serverSubMinorVersion = 0;
/*  222 */   private int warningCount = 0;
/*  223 */   protected long clientParam = 0L;
/*  224 */   protected long lastPacketSentTimeMs = 0L;
/*  225 */   protected long lastPacketReceivedTimeMs = 0L;
/*      */   private boolean traceProtocol = false;
/*      */   private boolean enablePacketDebug = false;
/*      */   private Calendar sessionCalendar;
/*      */   private boolean useConnectWithDb;
/*      */   private boolean needToGrabQueryFromPacket;
/*      */   private boolean autoGenerateTestcaseScript;
/*      */   private long threadId;
/*      */   private boolean useNanosForElapsedTime;
/*      */   private long slowQueryThreshold;
/*      */   private String queryTimingUnits;
/*      */   private List statementInterceptors;
/*      */   private boolean useDirectRowUnpack = true;
/*      */   private int useBufferRowSizeThreshold;
/*  239 */   private int commandCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int statementExecutionDepth;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean useAutoSlowLog;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void initializeStatementInterceptors(String interceptorClasses, Properties props) throws SQLException {
/*  333 */     this.statementInterceptors = Util.loadExtensions(this.connection, props, interceptorClasses, "MysqlIo.BadStatementInterceptor");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasLongColumnInfo() {
/*  344 */     return this.hasLongColumnInfo;
/*      */   }
/*      */   
/*      */   protected boolean isDataAvailable() throws SQLException {
/*      */     try {
/*  349 */       return (this.mysqlInput.available() > 0);
/*  350 */     } catch (IOException ioEx) {
/*  351 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long getLastPacketSentTimeMs() {
/*  362 */     return this.lastPacketSentTimeMs;
/*      */   }
/*      */   
/*      */   protected long getLastPacketReceivedTimeMs() {
/*  366 */     return this.lastPacketReceivedTimeMs;
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
/*      */   protected ResultSetImpl getResultSet(StatementImpl callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, Field[] metadataFromCache) throws SQLException {
/*  396 */     Field[] fields = null;
/*      */ 
/*      */ 
/*      */     
/*  400 */     if (metadataFromCache == null) {
/*  401 */       fields = new Field[(int)columnCount];
/*      */       
/*  403 */       for (int i = 0; i < columnCount; i++) {
/*  404 */         Buffer fieldPacket = null;
/*      */         
/*  406 */         fieldPacket = readPacket();
/*  407 */         fields[i] = unpackField(fieldPacket, false);
/*      */       } 
/*      */     } else {
/*  410 */       for (int i = 0; i < columnCount; i++) {
/*  411 */         skipPacket();
/*      */       }
/*      */     } 
/*      */     
/*  415 */     Buffer packet = reuseAndReadPacket(this.reusablePacket);
/*      */     
/*  417 */     readServerStatusForResultSets(packet);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  423 */     if (this.connection.versionMeetsMinimum(5, 0, 2) && this.connection.getUseCursorFetch() && isBinaryEncoded && callingStatement != null && callingStatement.getFetchSize() != 0 && callingStatement.getResultSetType() == 1003) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  429 */       ServerPreparedStatement prepStmt = (ServerPreparedStatement)callingStatement;
/*      */       
/*  431 */       boolean usingCursor = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  439 */       if (this.connection.versionMeetsMinimum(5, 0, 5)) {
/*  440 */         usingCursor = ((this.serverStatus & 0x40) != 0);
/*      */       }
/*      */ 
/*      */       
/*  444 */       if (usingCursor) {
/*  445 */         RowData rows = new RowDataCursor(this, prepStmt, fields);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  450 */         ResultSetImpl resultSetImpl = buildResultSetWithRows(callingStatement, catalog, fields, rows, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  456 */         if (usingCursor) {
/*  457 */           resultSetImpl.setFetchSize(callingStatement.getFetchSize());
/*      */         }
/*      */         
/*  460 */         return resultSetImpl;
/*      */       } 
/*      */     } 
/*      */     
/*  464 */     RowData rowData = null;
/*      */     
/*  466 */     if (!streamResults) {
/*  467 */       rowData = readSingleRowSet(columnCount, maxRows, resultSetConcurrency, isBinaryEncoded, (metadataFromCache == null) ? fields : metadataFromCache);
/*      */     }
/*      */     else {
/*      */       
/*  471 */       rowData = new RowDataDynamic(this, (int)columnCount, (metadataFromCache == null) ? fields : metadataFromCache, isBinaryEncoded);
/*      */ 
/*      */       
/*  474 */       this.streamingData = rowData;
/*      */     } 
/*      */     
/*  477 */     ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, (metadataFromCache == null) ? fields : metadataFromCache, rowData, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  483 */     return rs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void forceClose() {
/*      */     try {
/*  491 */       if (this.mysqlInput != null) {
/*  492 */         this.mysqlInput.close();
/*      */       }
/*  494 */     } catch (IOException ioEx) {
/*      */ 
/*      */       
/*  497 */       this.mysqlInput = null;
/*      */     } 
/*      */     
/*      */     try {
/*  501 */       if (this.mysqlOutput != null) {
/*  502 */         this.mysqlOutput.close();
/*      */       }
/*  504 */     } catch (IOException ioEx) {
/*      */ 
/*      */       
/*  507 */       this.mysqlOutput = null;
/*      */     } 
/*      */     
/*      */     try {
/*  511 */       if (this.mysqlConnection != null) {
/*  512 */         this.mysqlConnection.close();
/*      */       }
/*  514 */     } catch (IOException ioEx) {
/*      */ 
/*      */       
/*  517 */       this.mysqlConnection = null;
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
/*      */   protected final void skipPacket() throws SQLException {
/*      */     try {
/*  530 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*      */       
/*  533 */       if (lengthRead < 4) {
/*  534 */         forceClose();
/*  535 */         throw new IOException(Messages.getString("MysqlIO.1"));
/*      */       } 
/*      */       
/*  538 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/*      */ 
/*      */       
/*  542 */       if (this.traceProtocol) {
/*  543 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */         
/*  545 */         traceMessageBuf.append(Messages.getString("MysqlIO.2"));
/*  546 */         traceMessageBuf.append(packetLength);
/*  547 */         traceMessageBuf.append(Messages.getString("MysqlIO.3"));
/*  548 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */ 
/*      */         
/*  551 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       } 
/*      */       
/*  554 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */       
/*  556 */       if (!this.packetSequenceReset) {
/*  557 */         if (this.enablePacketDebug && this.checkPacketSequence) {
/*  558 */           checkPacketSequencing(multiPacketSeq);
/*      */         }
/*      */       } else {
/*  561 */         this.packetSequenceReset = false;
/*      */       } 
/*      */       
/*  564 */       this.readPacketSequence = multiPacketSeq;
/*      */       
/*  566 */       skipFully(this.mysqlInput, packetLength);
/*  567 */     } catch (IOException ioEx) {
/*  568 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     }
/*  570 */     catch (OutOfMemoryError oom) {
/*      */       
/*  572 */       try { this.connection.realClose(false, false, true, oom);
/*      */         
/*  574 */         throw oom; } finally { Exception exception = null; }
/*      */     
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
/*      */   protected final Buffer readPacket() throws SQLException {
/*      */     try {
/*  590 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*      */       
/*  593 */       if (lengthRead < 4) {
/*  594 */         forceClose();
/*  595 */         throw new IOException(Messages.getString("MysqlIO.1"));
/*      */       } 
/*      */       
/*  598 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/*      */ 
/*      */       
/*  602 */       if (packetLength > this.maxAllowedPacket) {
/*  603 */         throw new PacketTooBigException(packetLength, this.maxAllowedPacket);
/*      */       }
/*      */       
/*  606 */       if (this.traceProtocol) {
/*  607 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */         
/*  609 */         traceMessageBuf.append(Messages.getString("MysqlIO.2"));
/*  610 */         traceMessageBuf.append(packetLength);
/*  611 */         traceMessageBuf.append(Messages.getString("MysqlIO.3"));
/*  612 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */ 
/*      */         
/*  615 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       } 
/*      */       
/*  618 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */       
/*  620 */       if (!this.packetSequenceReset) {
/*  621 */         if (this.enablePacketDebug && this.checkPacketSequence) {
/*  622 */           checkPacketSequencing(multiPacketSeq);
/*      */         }
/*      */       } else {
/*  625 */         this.packetSequenceReset = false;
/*      */       } 
/*      */       
/*  628 */       this.readPacketSequence = multiPacketSeq;
/*      */ 
/*      */       
/*  631 */       byte[] buffer = new byte[packetLength + 1];
/*  632 */       int numBytesRead = readFully(this.mysqlInput, buffer, 0, packetLength);
/*      */ 
/*      */       
/*  635 */       if (numBytesRead != packetLength) {
/*  636 */         throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
/*      */       }
/*      */ 
/*      */       
/*  640 */       buffer[packetLength] = 0;
/*      */       
/*  642 */       Buffer packet = new Buffer(buffer);
/*  643 */       packet.setBufLength(packetLength + 1);
/*      */       
/*  645 */       if (this.traceProtocol) {
/*  646 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */         
/*  648 */         traceMessageBuf.append(Messages.getString("MysqlIO.4"));
/*  649 */         traceMessageBuf.append(getPacketDumpToLog(packet, packetLength));
/*      */ 
/*      */         
/*  652 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       } 
/*      */       
/*  655 */       if (this.enablePacketDebug) {
/*  656 */         enqueuePacketForDebugging(false, false, 0, this.packetHeaderBuf, packet);
/*      */       }
/*      */ 
/*      */       
/*  660 */       if (this.connection.getMaintainTimeStats()) {
/*  661 */         this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/*      */       }
/*      */       
/*  664 */       return packet;
/*  665 */     } catch (IOException ioEx) {
/*  666 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     }
/*  668 */     catch (OutOfMemoryError oom) {
/*      */       
/*  670 */       try { this.connection.realClose(false, false, true, oom);
/*      */         
/*  672 */         throw oom; } finally { Exception exception = null; }
/*      */     
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
/*      */   protected final Field unpackField(Buffer packet, boolean extractDefaultValues) throws SQLException {
/*  690 */     if (this.use41Extensions) {
/*      */ 
/*      */       
/*  693 */       if (this.has41NewNewProt) {
/*      */         
/*  695 */         int catalogNameStart = packet.getPosition() + 1;
/*  696 */         int catalogNameLength = packet.fastSkipLenString();
/*  697 */         catalogNameStart = adjustStartForFieldLength(catalogNameStart, catalogNameLength);
/*      */       } 
/*      */       
/*  700 */       int databaseNameStart = packet.getPosition() + 1;
/*  701 */       int databaseNameLength = packet.fastSkipLenString();
/*  702 */       databaseNameStart = adjustStartForFieldLength(databaseNameStart, databaseNameLength);
/*      */       
/*  704 */       int i = packet.getPosition() + 1;
/*  705 */       int j = packet.fastSkipLenString();
/*  706 */       i = adjustStartForFieldLength(i, j);
/*      */ 
/*      */       
/*  709 */       int originalTableNameStart = packet.getPosition() + 1;
/*  710 */       int originalTableNameLength = packet.fastSkipLenString();
/*  711 */       originalTableNameStart = adjustStartForFieldLength(originalTableNameStart, originalTableNameLength);
/*      */ 
/*      */       
/*  714 */       int k = packet.getPosition() + 1;
/*  715 */       int m = packet.fastSkipLenString();
/*      */       
/*  717 */       k = adjustStartForFieldLength(k, m);
/*      */ 
/*      */       
/*  720 */       int originalColumnNameStart = packet.getPosition() + 1;
/*  721 */       int originalColumnNameLength = packet.fastSkipLenString();
/*  722 */       originalColumnNameStart = adjustStartForFieldLength(originalColumnNameStart, originalColumnNameLength);
/*      */       
/*  724 */       packet.readByte();
/*      */       
/*  726 */       short charSetNumber = (short)packet.readInt();
/*      */       
/*  728 */       long l = 0L;
/*      */       
/*  730 */       if (this.has41NewNewProt) {
/*  731 */         l = packet.readLong();
/*      */       } else {
/*  733 */         l = packet.readLongInt();
/*      */       } 
/*      */       
/*  736 */       int n = packet.readByte() & 0xFF;
/*      */       
/*  738 */       short s1 = 0;
/*      */       
/*  740 */       if (this.hasLongColumnInfo) {
/*  741 */         s1 = (short)packet.readInt();
/*      */       } else {
/*  743 */         s1 = (short)(packet.readByte() & 0xFF);
/*      */       } 
/*      */       
/*  746 */       int i1 = packet.readByte() & 0xFF;
/*      */       
/*  748 */       int defaultValueStart = -1;
/*  749 */       int defaultValueLength = -1;
/*      */       
/*  751 */       if (extractDefaultValues) {
/*  752 */         defaultValueStart = packet.getPosition() + 1;
/*  753 */         defaultValueLength = packet.fastSkipLenString();
/*      */       } 
/*      */       
/*  756 */       Field field1 = new Field(this.connection, packet.getByteBuffer(), databaseNameStart, databaseNameLength, i, j, originalTableNameStart, originalTableNameLength, k, m, originalColumnNameStart, originalColumnNameLength, l, n, s1, i1, defaultValueStart, defaultValueLength, charSetNumber);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  764 */       return field1;
/*      */     } 
/*      */     
/*  767 */     int tableNameStart = packet.getPosition() + 1;
/*  768 */     int tableNameLength = packet.fastSkipLenString();
/*  769 */     tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */     
/*  771 */     int nameStart = packet.getPosition() + 1;
/*  772 */     int nameLength = packet.fastSkipLenString();
/*  773 */     nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */     
/*  775 */     int colLength = packet.readnBytes();
/*  776 */     int colType = packet.readnBytes();
/*  777 */     packet.readByte();
/*      */     
/*  779 */     short colFlag = 0;
/*      */     
/*  781 */     if (this.hasLongColumnInfo) {
/*  782 */       colFlag = (short)packet.readInt();
/*      */     } else {
/*  784 */       colFlag = (short)(packet.readByte() & 0xFF);
/*      */     } 
/*      */     
/*  787 */     int colDecimals = packet.readByte() & 0xFF;
/*      */     
/*  789 */     if (this.colDecimalNeedsBump) {
/*  790 */       colDecimals++;
/*      */     }
/*      */     
/*  793 */     Field field = new Field(this.connection, packet.getByteBuffer(), nameStart, nameLength, tableNameStart, tableNameLength, colLength, colType, colFlag, colDecimals);
/*      */ 
/*      */ 
/*      */     
/*  797 */     return field;
/*      */   }
/*      */   
/*      */   private int adjustStartForFieldLength(int nameStart, int nameLength) {
/*  801 */     if (nameLength < 251) {
/*  802 */       return nameStart;
/*      */     }
/*      */     
/*  805 */     if (nameLength >= 251 && nameLength < 65536) {
/*  806 */       return nameStart + 2;
/*      */     }
/*      */     
/*  809 */     if (nameLength >= 65536 && nameLength < 16777216) {
/*  810 */       return nameStart + 3;
/*      */     }
/*      */     
/*  813 */     return nameStart + 8;
/*      */   }
/*      */   
/*      */   protected boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
/*  817 */     if (this.use41Extensions && this.connection.getElideSetAutoCommits()) {
/*  818 */       boolean autoCommitModeOnServer = ((this.serverStatus & 0x2) != 0);
/*      */ 
/*      */       
/*  821 */       if (!autoCommitFlag && versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */ 
/*      */         
/*  825 */         boolean inTransactionOnServer = ((this.serverStatus & 0x1) != 0);
/*      */ 
/*      */         
/*  828 */         return !inTransactionOnServer;
/*      */       } 
/*      */       
/*  831 */       return (autoCommitModeOnServer != autoCommitFlag);
/*      */     } 
/*      */     
/*  834 */     return true;
/*      */   }
/*      */   
/*      */   protected boolean inTransactionOnServer() {
/*  838 */     return ((this.serverStatus & 0x1) != 0);
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
/*      */   protected void changeUser(String userName, String password, String database) throws SQLException {
/*  852 */     this.packetSequence = -1;
/*      */     
/*  854 */     int passwordLength = 16;
/*  855 */     int userLength = (userName != null) ? userName.length() : 0;
/*  856 */     int databaseLength = (database != null) ? database.length() : 0;
/*      */     
/*  858 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */     
/*  860 */     if ((this.serverCapabilities & 0x8000) != 0) {
/*  861 */       Buffer changeUserPacket = new Buffer(packLength + 1);
/*  862 */       changeUserPacket.writeByte((byte)17);
/*      */       
/*  864 */       if (versionMeetsMinimum(4, 1, 1)) {
/*  865 */         secureAuth411(changeUserPacket, packLength, userName, password, database, false);
/*      */       } else {
/*      */         
/*  868 */         secureAuth(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  873 */       Buffer packet = new Buffer(packLength);
/*  874 */       packet.writeByte((byte)17);
/*      */ 
/*      */       
/*  877 */       packet.writeString(userName);
/*      */       
/*  879 */       if (this.protocolVersion > 9) {
/*  880 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       } else {
/*  882 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       } 
/*      */       
/*  885 */       boolean localUseConnectWithDb = (this.useConnectWithDb && database != null && database.length() > 0);
/*      */ 
/*      */       
/*  888 */       if (localUseConnectWithDb) {
/*  889 */         packet.writeString(database);
/*      */       }
/*      */       
/*  892 */       send(packet, packet.getPosition());
/*  893 */       checkErrorPacket();
/*      */       
/*  895 */       if (!localUseConnectWithDb) {
/*  896 */         changeDatabaseTo(database);
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
/*      */   protected Buffer checkErrorPacket() throws SQLException {
/*  910 */     return checkErrorPacket(-1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkForCharsetMismatch() {
/*  917 */     if (this.connection.getUseUnicode() && this.connection.getEncoding() != null) {
/*      */       
/*  919 */       String encodingToCheck = jvmPlatformCharset;
/*      */       
/*  921 */       if (encodingToCheck == null) {
/*  922 */         encodingToCheck = System.getProperty("file.encoding");
/*      */       }
/*      */       
/*  925 */       if (encodingToCheck == null) {
/*  926 */         this.platformDbCharsetMatches = false;
/*      */       } else {
/*  928 */         this.platformDbCharsetMatches = encodingToCheck.equals(this.connection.getEncoding());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void clearInputStream() throws SQLException {
/*      */     try {
/*  936 */       int len = this.mysqlInput.available();
/*      */       
/*  938 */       while (len > 0) {
/*  939 */         this.mysqlInput.skip(len);
/*  940 */         len = this.mysqlInput.available();
/*      */       } 
/*  942 */     } catch (IOException ioEx) {
/*  943 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void resetReadPacketSequence() {
/*  949 */     this.readPacketSequence = 0;
/*      */   }
/*      */   
/*      */   protected void dumpPacketRingBuffer() throws SQLException {
/*  953 */     if (this.packetDebugRingBuffer != null && this.connection.getEnablePacketDebug()) {
/*      */       
/*  955 */       StringBuffer dumpBuffer = new StringBuffer();
/*      */       
/*  957 */       dumpBuffer.append("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
/*      */       
/*  959 */       dumpBuffer.append("\n");
/*      */       
/*  961 */       Iterator ringBufIter = this.packetDebugRingBuffer.iterator();
/*  962 */       while (ringBufIter.hasNext()) {
/*  963 */         dumpBuffer.append(ringBufIter.next());
/*  964 */         dumpBuffer.append("\n");
/*      */       } 
/*      */       
/*  967 */       this.connection.getLog().logTrace(dumpBuffer.toString());
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
/*      */   protected void explainSlowQuery(byte[] querySQL, String truncatedQuery) throws SQLException {
/*  981 */     if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT")) {
/*      */       
/*  983 */       PreparedStatement stmt = null;
/*  984 */       ResultSet rs = null;
/*      */ 
/*      */       
/*  987 */       try { stmt = (PreparedStatement)this.connection.clientPrepareStatement("EXPLAIN ?");
/*  988 */         stmt.setBytesNoEscapeNoQuotes(1, querySQL);
/*  989 */         rs = stmt.executeQuery();
/*      */         
/*  991 */         StringBuffer explainResults = new StringBuffer(Messages.getString("MysqlIO.8") + truncatedQuery + Messages.getString("MysqlIO.9"));
/*      */ 
/*      */ 
/*      */         
/*  995 */         ResultSetUtil.appendResultSetSlashGStyle(explainResults, rs);
/*      */         
/*  997 */         this.connection.getLog().logWarn(explainResults.toString()); }
/*  998 */       catch (SQLException sqlEx) {  }
/*      */       finally
/* 1000 */       { if (rs != null) {
/* 1001 */           rs.close();
/*      */         }
/*      */         
/* 1004 */         if (stmt != null) {
/* 1005 */           stmt.close();
/*      */         } }
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static int getMaxBuf() {
/* 1013 */     return maxBufferSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int getServerMajorVersion() {
/* 1022 */     return this.serverMajorVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int getServerMinorVersion() {
/* 1031 */     return this.serverMinorVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int getServerSubMinorVersion() {
/* 1040 */     return this.serverSubMinorVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getServerVersion() {
/* 1049 */     return this.serverVersion;
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
/*      */   void doHandshake(String user, String password, String database) throws SQLException {
/* 1066 */     this.checkPacketSequence = false;
/* 1067 */     this.readPacketSequence = 0;
/*      */     
/* 1069 */     Buffer buf = readPacket();
/*      */ 
/*      */     
/* 1072 */     this.protocolVersion = buf.readByte();
/*      */     
/* 1074 */     if (this.protocolVersion == -1) {
/*      */       try {
/* 1076 */         this.mysqlConnection.close();
/* 1077 */       } catch (Exception e) {}
/*      */ 
/*      */ 
/*      */       
/* 1081 */       int errno = 2000;
/*      */       
/* 1083 */       errno = buf.readInt();
/*      */       
/* 1085 */       String serverErrorMessage = buf.readString("ASCII");
/*      */       
/* 1087 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.10"));
/*      */       
/* 1089 */       errorBuf.append(serverErrorMessage);
/* 1090 */       errorBuf.append("\"");
/*      */       
/* 1092 */       String xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */ 
/*      */       
/* 1095 */       throw SQLError.createSQLException(SQLError.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno);
/*      */     } 
/*      */ 
/*      */     
/* 1099 */     this.serverVersion = buf.readString("ASCII");
/*      */ 
/*      */     
/* 1102 */     int point = this.serverVersion.indexOf('.');
/*      */     
/* 1104 */     if (point != -1) {
/*      */       try {
/* 1106 */         int n = Integer.parseInt(this.serverVersion.substring(0, point));
/* 1107 */         this.serverMajorVersion = n;
/* 1108 */       } catch (NumberFormatException NFE1) {}
/*      */ 
/*      */ 
/*      */       
/* 1112 */       String remaining = this.serverVersion.substring(point + 1, this.serverVersion.length());
/*      */       
/* 1114 */       point = remaining.indexOf('.');
/*      */       
/* 1116 */       if (point != -1) {
/*      */         try {
/* 1118 */           int n = Integer.parseInt(remaining.substring(0, point));
/* 1119 */           this.serverMinorVersion = n;
/* 1120 */         } catch (NumberFormatException nfe) {}
/*      */ 
/*      */ 
/*      */         
/* 1124 */         remaining = remaining.substring(point + 1, remaining.length());
/*      */         
/* 1126 */         int pos = 0;
/*      */         
/* 1128 */         while (pos < remaining.length() && 
/* 1129 */           remaining.charAt(pos) >= '0' && remaining.charAt(pos) <= '9')
/*      */         {
/*      */ 
/*      */ 
/*      */           
/* 1134 */           pos++;
/*      */         }
/*      */         
/*      */         try {
/* 1138 */           int n = Integer.parseInt(remaining.substring(0, pos));
/* 1139 */           this.serverSubMinorVersion = n;
/* 1140 */         } catch (NumberFormatException nfe) {}
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1146 */     if (versionMeetsMinimum(4, 0, 8)) {
/* 1147 */       this.maxThreeBytes = 16777215;
/* 1148 */       this.useNewLargePackets = true;
/*      */     } else {
/* 1150 */       this.maxThreeBytes = 16581375;
/* 1151 */       this.useNewLargePackets = false;
/*      */     } 
/*      */     
/* 1154 */     this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
/* 1155 */     this.colDecimalNeedsBump = !versionMeetsMinimum(3, 23, 15);
/* 1156 */     this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
/*      */     
/* 1158 */     this.threadId = buf.readLong();
/* 1159 */     this.seed = buf.readString("ASCII");
/*      */     
/* 1161 */     this.serverCapabilities = 0;
/*      */     
/* 1163 */     if (buf.getPosition() < buf.getBufLength()) {
/* 1164 */       this.serverCapabilities = buf.readInt();
/*      */     }
/*      */     
/* 1167 */     if (versionMeetsMinimum(4, 1, 1)) {
/* 1168 */       int position = buf.getPosition();
/*      */ 
/*      */       
/* 1171 */       this.serverCharsetIndex = buf.readByte() & 0xFF;
/* 1172 */       this.serverStatus = buf.readInt();
/* 1173 */       checkTransactionState(0);
/* 1174 */       buf.setPosition(position + 16);
/*      */       
/* 1176 */       String seedPart2 = buf.readString("ASCII");
/* 1177 */       StringBuffer newSeed = new StringBuffer(20);
/* 1178 */       newSeed.append(this.seed);
/* 1179 */       newSeed.append(seedPart2);
/* 1180 */       this.seed = newSeed.toString();
/*      */     } 
/*      */     
/* 1183 */     if ((this.serverCapabilities & 0x20) != 0 && this.connection.getUseCompression())
/*      */     {
/* 1185 */       this.clientParam |= 0x20L;
/*      */     }
/*      */     
/* 1188 */     this.useConnectWithDb = (database != null && database.length() > 0 && !this.connection.getCreateDatabaseIfNotExist());
/*      */ 
/*      */ 
/*      */     
/* 1192 */     if (this.useConnectWithDb) {
/* 1193 */       this.clientParam |= 0x8L;
/*      */     }
/*      */     
/* 1196 */     if ((this.serverCapabilities & 0x800) == 0 && this.connection.getUseSSL()) {
/*      */       
/* 1198 */       if (this.connection.getRequireSSL()) {
/* 1199 */         this.connection.close();
/* 1200 */         forceClose();
/* 1201 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.15"), "08001");
/*      */       } 
/*      */ 
/*      */       
/* 1205 */       this.connection.setUseSSL(false);
/*      */     } 
/*      */     
/* 1208 */     if ((this.serverCapabilities & 0x4) != 0) {
/*      */       
/* 1210 */       this.clientParam |= 0x4L;
/* 1211 */       this.hasLongColumnInfo = true;
/*      */     } 
/*      */ 
/*      */     
/* 1215 */     this.clientParam |= 0x2L;
/*      */     
/* 1217 */     if (this.connection.getAllowLoadLocalInfile()) {
/* 1218 */       this.clientParam |= 0x80L;
/*      */     }
/*      */     
/* 1221 */     if (this.isInteractiveClient) {
/* 1222 */       this.clientParam |= 0x400L;
/*      */     }
/*      */ 
/*      */     
/* 1226 */     if (this.protocolVersion > 9) {
/* 1227 */       this.clientParam |= 0x1L;
/*      */     } else {
/* 1229 */       this.clientParam &= 0xFFFFFFFFFFFFFFFEL;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1235 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 1236 */       if (versionMeetsMinimum(4, 1, 1)) {
/* 1237 */         this.clientParam |= 0x200L;
/* 1238 */         this.has41NewNewProt = true;
/*      */ 
/*      */         
/* 1241 */         this.clientParam |= 0x2000L;
/*      */ 
/*      */         
/* 1244 */         this.clientParam |= 0x20000L;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1249 */         if (this.connection.getAllowMultiQueries()) {
/* 1250 */           this.clientParam |= 0x10000L;
/*      */         }
/*      */       } else {
/* 1253 */         this.clientParam |= 0x4000L;
/* 1254 */         this.has41NewNewProt = false;
/*      */       } 
/*      */       
/* 1257 */       this.use41Extensions = true;
/*      */     } 
/*      */     
/* 1260 */     int passwordLength = 16;
/* 1261 */     int userLength = (user != null) ? user.length() : 0;
/* 1262 */     int databaseLength = (database != null) ? database.length() : 0;
/*      */     
/* 1264 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */     
/* 1266 */     Buffer packet = null;
/*      */     
/* 1268 */     if (!this.connection.getUseSSL()) {
/* 1269 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1270 */         this.clientParam |= 0x8000L;
/*      */         
/* 1272 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 1273 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         } else {
/*      */           
/* 1276 */           secureAuth(null, packLength, user, password, database, true);
/*      */         } 
/*      */       } else {
/*      */         
/* 1280 */         packet = new Buffer(packLength);
/*      */         
/* 1282 */         if ((this.clientParam & 0x4000L) != 0L) {
/* 1283 */           if (versionMeetsMinimum(4, 1, 1)) {
/* 1284 */             packet.writeLong(this.clientParam);
/* 1285 */             packet.writeLong(this.maxThreeBytes);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1290 */             packet.writeByte((byte)8);
/*      */ 
/*      */             
/* 1293 */             packet.writeBytesNoNull(new byte[23]);
/*      */           } else {
/* 1295 */             packet.writeLong(this.clientParam);
/* 1296 */             packet.writeLong(this.maxThreeBytes);
/*      */           } 
/*      */         } else {
/* 1299 */           packet.writeInt((int)this.clientParam);
/* 1300 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         } 
/*      */ 
/*      */         
/* 1304 */         packet.writeString(user, "Cp1252", this.connection);
/*      */         
/* 1306 */         if (this.protocolVersion > 9) {
/* 1307 */           packet.writeString(Util.newCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         } else {
/* 1309 */           packet.writeString(Util.oldCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         } 
/*      */         
/* 1312 */         if (this.useConnectWithDb) {
/* 1313 */           packet.writeString(database, "Cp1252", this.connection);
/*      */         }
/*      */         
/* 1316 */         send(packet, packet.getPosition());
/*      */       } 
/*      */     } else {
/* 1319 */       negotiateSSLConnection(user, password, database, packLength);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1325 */     if (!versionMeetsMinimum(4, 1, 1)) {
/* 1326 */       checkErrorPacket();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1332 */     if ((this.serverCapabilities & 0x20) != 0 && this.connection.getUseCompression()) {
/*      */ 
/*      */ 
/*      */       
/* 1336 */       this.deflater = new Deflater();
/* 1337 */       this.useCompression = true;
/* 1338 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     } 
/*      */ 
/*      */     
/* 1342 */     if (!this.useConnectWithDb) {
/* 1343 */       changeDatabaseTo(database);
/*      */     }
/*      */     
/*      */     try {
/* 1347 */       this.mysqlConnection = this.socketFactory.afterHandshake();
/* 1348 */     } catch (IOException ioEx) {
/* 1349 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void changeDatabaseTo(String database) throws SQLException {
/* 1354 */     if (database == null || database.length() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*      */     try {
/* 1359 */       sendCommand(2, database, null, false, null);
/* 1360 */     } catch (Exception ex) {
/* 1361 */       if (this.connection.getCreateDatabaseIfNotExist()) {
/* 1362 */         sendCommand(3, "CREATE DATABASE IF NOT EXISTS " + database, null, false, null);
/*      */ 
/*      */         
/* 1365 */         sendCommand(2, database, null, false, null);
/*      */       } else {
/* 1367 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex);
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
/*      */   final ResultSetRow nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacketForBufferRow, Buffer existingRowPacket) throws SQLException {
/* 1395 */     if (this.useDirectRowUnpack && existingRowPacket == null && !isBinaryEncoded && !useBufferRowIfPossible && !useBufferRowExplicit)
/*      */     {
/*      */       
/* 1398 */       return nextRowFast(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacketForBufferRow);
/*      */     }
/*      */ 
/*      */     
/* 1402 */     Buffer rowPacket = null;
/*      */     
/* 1404 */     if (existingRowPacket == null) {
/* 1405 */       rowPacket = checkErrorPacket();
/*      */       
/* 1407 */       if (!useBufferRowExplicit && useBufferRowIfPossible && 
/* 1408 */         rowPacket.getBufLength() > this.useBufferRowSizeThreshold) {
/* 1409 */         useBufferRowExplicit = true;
/*      */       
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1415 */       rowPacket = existingRowPacket;
/* 1416 */       checkErrorPacket(existingRowPacket);
/*      */     } 
/*      */ 
/*      */     
/* 1420 */     if (!isBinaryEncoded) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1425 */       rowPacket.setPosition(rowPacket.getPosition() - 1);
/*      */       
/* 1427 */       if (!rowPacket.isLastDataPacket()) {
/* 1428 */         if (resultSetConcurrency == 1008 || (!useBufferRowIfPossible && !useBufferRowExplicit)) {
/*      */ 
/*      */           
/* 1431 */           byte[][] rowData = new byte[columnCount][];
/*      */           
/* 1433 */           for (int i = 0; i < columnCount; i++) {
/* 1434 */             rowData[i] = rowPacket.readLenByteArray(0);
/*      */           }
/*      */           
/* 1437 */           return new ByteArrayRow(rowData);
/*      */         } 
/*      */         
/* 1440 */         if (!canReuseRowPacketForBufferRow) {
/* 1441 */           this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */         }
/*      */         
/* 1444 */         return new BufferRow(rowPacket, fields, false);
/*      */       } 
/*      */ 
/*      */       
/* 1448 */       readServerStatusForResultSets(rowPacket);
/*      */       
/* 1450 */       return null;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1457 */     if (!rowPacket.isLastDataPacket()) {
/* 1458 */       if (resultSetConcurrency == 1008 || (!useBufferRowIfPossible && !useBufferRowExplicit))
/*      */       {
/* 1460 */         return unpackBinaryResultSetRow(fields, rowPacket, resultSetConcurrency);
/*      */       }
/*      */ 
/*      */       
/* 1464 */       if (!canReuseRowPacketForBufferRow) {
/* 1465 */         this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */       }
/*      */       
/* 1468 */       return new BufferRow(rowPacket, fields, true);
/*      */     } 
/*      */     
/* 1471 */     rowPacket.setPosition(rowPacket.getPosition() - 1);
/* 1472 */     readServerStatusForResultSets(rowPacket);
/*      */     
/* 1474 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final ResultSetRow nextRowFast(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacket) throws SQLException {
/*      */     try {
/* 1483 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*      */       
/* 1486 */       if (lengthRead < 4) {
/* 1487 */         forceClose();
/* 1488 */         throw new RuntimeException(Messages.getString("MysqlIO.43"));
/*      */       } 
/*      */       
/* 1491 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1496 */       if (packetLength == this.maxThreeBytes) {
/* 1497 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */ 
/*      */         
/* 1500 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacket, this.reusablePacket);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1507 */       if (packetLength > this.useBufferRowSizeThreshold) {
/* 1508 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */ 
/*      */         
/* 1511 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, true, true, false, this.reusablePacket);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1516 */       int remaining = packetLength;
/*      */       
/* 1518 */       boolean firstTime = true;
/*      */       
/* 1520 */       byte[][] rowData = (byte[][])null;
/*      */       
/* 1522 */       for (int i = 0; i < columnCount; i++) {
/*      */         
/* 1524 */         int sw = this.mysqlInput.read() & 0xFF;
/* 1525 */         remaining--;
/*      */         
/* 1527 */         if (firstTime) {
/* 1528 */           if (sw == 255) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1533 */             Buffer errorPacket = new Buffer(packetLength + 4);
/* 1534 */             errorPacket.setPosition(0);
/* 1535 */             errorPacket.writeByte(this.packetHeaderBuf[0]);
/* 1536 */             errorPacket.writeByte(this.packetHeaderBuf[1]);
/* 1537 */             errorPacket.writeByte(this.packetHeaderBuf[2]);
/* 1538 */             errorPacket.writeByte((byte)1);
/* 1539 */             errorPacket.writeByte((byte)sw);
/* 1540 */             readFully(this.mysqlInput, errorPacket.getByteBuffer(), 5, packetLength - 1);
/* 1541 */             errorPacket.setPosition(4);
/* 1542 */             checkErrorPacket(errorPacket);
/*      */           } 
/*      */           
/* 1545 */           if (sw == 254 && packetLength < 9) {
/* 1546 */             if (this.use41Extensions) {
/* 1547 */               this.warningCount = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8;
/*      */               
/* 1549 */               remaining -= 2;
/*      */               
/* 1551 */               if (this.warningCount > 0) {
/* 1552 */                 this.hadWarnings = true;
/*      */               }
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1558 */               this.oldServerStatus = this.serverStatus;
/*      */               
/* 1560 */               this.serverStatus = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8;
/*      */               
/* 1562 */               checkTransactionState(this.oldServerStatus);
/*      */               
/* 1564 */               remaining -= 2;
/*      */               
/* 1566 */               if (remaining > 0) {
/* 1567 */                 skipFully(this.mysqlInput, remaining);
/*      */               }
/*      */             } 
/*      */             
/* 1571 */             return null;
/*      */           } 
/*      */           
/* 1574 */           rowData = new byte[columnCount][];
/*      */           
/* 1576 */           firstTime = false;
/*      */         } 
/*      */         
/* 1579 */         int len = 0;
/*      */         
/* 1581 */         switch (sw) {
/*      */           case 251:
/* 1583 */             len = -1;
/*      */             break;
/*      */           
/*      */           case 252:
/* 1587 */             len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8;
/*      */             
/* 1589 */             remaining -= 2;
/*      */             break;
/*      */           
/*      */           case 253:
/* 1593 */             len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16;
/*      */ 
/*      */ 
/*      */             
/* 1597 */             remaining -= 3;
/*      */             break;
/*      */           
/*      */           case 254:
/* 1601 */             len = (int)((this.mysqlInput.read() & 0xFF) | (this.mysqlInput.read() & 0xFF) << 8L | (this.mysqlInput.read() & 0xFF) << 16L | (this.mysqlInput.read() & 0xFF) << 24L | (this.mysqlInput.read() & 0xFF) << 32L | (this.mysqlInput.read() & 0xFF) << 40L | (this.mysqlInput.read() & 0xFF) << 48L | (this.mysqlInput.read() & 0xFF) << 56L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1609 */             remaining -= 8;
/*      */             break;
/*      */           
/*      */           default:
/* 1613 */             len = sw;
/*      */             break;
/*      */         } 
/* 1616 */         if (len == -1) {
/* 1617 */           rowData[i] = null;
/* 1618 */         } else if (len == 0) {
/* 1619 */           rowData[i] = Constants.EMPTY_BYTE_ARRAY;
/*      */         } else {
/* 1621 */           rowData[i] = new byte[len];
/*      */           
/* 1623 */           int bytesRead = readFully(this.mysqlInput, rowData[i], 0, len);
/*      */ 
/*      */           
/* 1626 */           if (bytesRead != len) {
/* 1627 */             throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException(Messages.getString("MysqlIO.43")));
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1632 */           remaining -= bytesRead;
/*      */         } 
/*      */       } 
/*      */       
/* 1636 */       if (remaining > 0) {
/* 1637 */         skipFully(this.mysqlInput, remaining);
/*      */       }
/*      */       
/* 1640 */       return new ByteArrayRow(rowData);
/* 1641 */     } catch (IOException ioEx) {
/* 1642 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void quit() throws SQLException {
/* 1653 */     Buffer packet = new Buffer(6);
/* 1654 */     this.packetSequence = -1;
/* 1655 */     packet.writeByte((byte)1);
/* 1656 */     send(packet, packet.getPosition());
/* 1657 */     forceClose();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Buffer getSharedSendPacket() {
/* 1667 */     if (this.sharedSendPacket == null) {
/* 1668 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */     
/* 1671 */     return this.sharedSendPacket;
/*      */   }
/*      */   
/*      */   void closeStreamer(RowData streamer) throws SQLException {
/* 1675 */     if (this.streamingData == null) {
/* 1676 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"));
/*      */     }
/*      */ 
/*      */     
/* 1680 */     if (streamer != this.streamingData) {
/* 1681 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.19") + streamer + Messages.getString("MysqlIO.20") + Messages.getString("MysqlIO.21") + Messages.getString("MysqlIO.22"));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1687 */     this.streamingData = null;
/*      */   }
/*      */   
/*      */   boolean tackOnMoreStreamingResults(ResultSetImpl addingTo) throws SQLException {
/* 1691 */     if ((this.serverStatus & 0x8) != 0) {
/*      */       
/* 1693 */       boolean moreRowSetsExist = true;
/* 1694 */       ResultSetImpl currentResultSet = addingTo;
/* 1695 */       boolean firstTime = true;
/*      */       
/* 1697 */       while (moreRowSetsExist && (
/* 1698 */         firstTime || !currentResultSet.reallyResult())) {
/*      */ 
/*      */ 
/*      */         
/* 1702 */         firstTime = false;
/*      */         
/* 1704 */         Buffer fieldPacket = checkErrorPacket();
/* 1705 */         fieldPacket.setPosition(0);
/*      */         
/* 1707 */         Statement owningStatement = addingTo.getStatement();
/*      */         
/* 1709 */         int maxRows = owningStatement.getMaxRows();
/*      */ 
/*      */ 
/*      */         
/* 1713 */         ResultSetImpl newResultSet = readResultsForQueryOrUpdate((StatementImpl)owningStatement, maxRows, owningStatement.getResultSetType(), owningStatement.getResultSetConcurrency(), true, owningStatement.getConnection().getCatalog(), fieldPacket, addingTo.isBinaryEncoded, -1L, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1721 */         currentResultSet.setNextResultSet(newResultSet);
/*      */         
/* 1723 */         currentResultSet = newResultSet;
/*      */         
/* 1725 */         moreRowSetsExist = ((this.serverStatus & 0x8) != 0);
/*      */         
/* 1727 */         if (!currentResultSet.reallyResult() && !moreRowSetsExist)
/*      */         {
/* 1729 */           return false;
/*      */         }
/*      */       } 
/*      */       
/* 1733 */       return true;
/*      */     } 
/*      */     
/* 1736 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   ResultSetImpl readAllResults(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException {
/* 1744 */     resultPacket.setPosition(resultPacket.getPosition() - 1);
/*      */     
/* 1746 */     ResultSetImpl topLevelResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1751 */     ResultSetImpl currentResultSet = topLevelResultSet;
/*      */     
/* 1753 */     boolean checkForMoreResults = ((this.clientParam & 0x20000L) != 0L);
/*      */ 
/*      */     
/* 1756 */     boolean serverHasMoreResults = ((this.serverStatus & 0x8) != 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1762 */     if (serverHasMoreResults && streamResults) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1767 */       if (topLevelResultSet.getUpdateCount() != -1L) {
/* 1768 */         tackOnMoreStreamingResults(topLevelResultSet);
/*      */       }
/*      */       
/* 1771 */       reclaimLargeReusablePacket();
/*      */       
/* 1773 */       return topLevelResultSet;
/*      */     } 
/*      */     
/* 1776 */     boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
/*      */     
/* 1778 */     while (moreRowSetsExist) {
/* 1779 */       Buffer fieldPacket = checkErrorPacket();
/* 1780 */       fieldPacket.setPosition(0);
/*      */       
/* 1782 */       ResultSetImpl newResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, fieldPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1787 */       currentResultSet.setNextResultSet(newResultSet);
/*      */       
/* 1789 */       currentResultSet = newResultSet;
/*      */       
/* 1791 */       moreRowSetsExist = ((this.serverStatus & 0x8) != 0);
/*      */     } 
/*      */     
/* 1794 */     if (!streamResults) {
/* 1795 */       clearInputStream();
/*      */     }
/*      */     
/* 1798 */     reclaimLargeReusablePacket();
/*      */     
/* 1800 */     return topLevelResultSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void resetMaxBuf() {
/* 1807 */     this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
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
/*      */   final Buffer sendCommand(int command, String extraData, Buffer queryPacket, boolean skipCheck, String extraDataCharEncoding) throws SQLException {
/* 1833 */     this.commandCount++;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1840 */     this.enablePacketDebug = this.connection.getEnablePacketDebug();
/* 1841 */     this.traceProtocol = this.connection.getTraceProtocol();
/* 1842 */     this.readPacketSequence = 0;
/*      */ 
/*      */     
/*      */     try {
/* 1846 */       checkForOutstandingStreamingData();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1851 */       this.oldServerStatus = this.serverStatus;
/* 1852 */       this.serverStatus = 0;
/* 1853 */       this.hadWarnings = false;
/* 1854 */       this.warningCount = 0;
/*      */       
/* 1856 */       this.queryNoIndexUsed = false;
/* 1857 */       this.queryBadIndexUsed = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1863 */       if (this.useCompression) {
/* 1864 */         int bytesLeft = this.mysqlInput.available();
/*      */         
/* 1866 */         if (bytesLeft > 0) {
/* 1867 */           this.mysqlInput.skip(bytesLeft);
/*      */         }
/*      */       } 
/*      */       
/*      */       try {
/* 1872 */         clearInputStream();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1881 */         if (queryPacket == null) {
/* 1882 */           int packLength = 8 + ((extraData != null) ? extraData.length() : 0) + 2;
/*      */ 
/*      */           
/* 1885 */           if (this.sendPacket == null) {
/* 1886 */             this.sendPacket = new Buffer(packLength);
/*      */           }
/*      */           
/* 1889 */           this.packetSequence = -1;
/* 1890 */           this.readPacketSequence = 0;
/* 1891 */           this.checkPacketSequence = true;
/* 1892 */           this.sendPacket.clear();
/*      */           
/* 1894 */           this.sendPacket.writeByte((byte)command);
/*      */           
/* 1896 */           if (command == 2 || command == 5 || command == 6 || command == 3 || command == 22) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1901 */             if (extraDataCharEncoding == null) {
/* 1902 */               this.sendPacket.writeStringNoNull(extraData);
/*      */             } else {
/* 1904 */               this.sendPacket.writeStringNoNull(extraData, extraDataCharEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */             
/*      */             }
/*      */           
/*      */           }
/* 1909 */           else if (command == 12) {
/* 1910 */             long id = Long.parseLong(extraData);
/* 1911 */             this.sendPacket.writeLong(id);
/*      */           } 
/*      */           
/* 1914 */           send(this.sendPacket, this.sendPacket.getPosition());
/*      */         } else {
/* 1916 */           this.packetSequence = -1;
/* 1917 */           send(queryPacket, queryPacket.getPosition());
/*      */         } 
/* 1919 */       } catch (SQLException sqlEx) {
/*      */         
/* 1921 */         throw sqlEx;
/* 1922 */       } catch (Exception ex) {
/* 1923 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex);
/*      */       } 
/*      */ 
/*      */       
/* 1927 */       Buffer returnPacket = null;
/*      */       
/* 1929 */       if (!skipCheck) {
/* 1930 */         if (command == 23 || command == 26) {
/*      */           
/* 1932 */           this.readPacketSequence = 0;
/* 1933 */           this.packetSequenceReset = true;
/*      */         } 
/*      */         
/* 1936 */         returnPacket = checkErrorPacket(command);
/*      */       } 
/*      */       
/* 1939 */       return returnPacket;
/* 1940 */     } catch (IOException ioEx) {
/* 1941 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     } 
/*      */   }
/*      */   
/*      */   public MysqlIO(String host, int port, Properties props, String socketFactoryClassName, ConnectionImpl conn, int socketTimeout, int useBufferRowSizeThreshold) throws IOException, SQLException {
/* 1946 */     this.statementExecutionDepth = 0; this.connection = conn; if (this.connection.getEnablePacketDebug())
/*      */       this.packetDebugRingBuffer = new LinkedList();  this.useAutoSlowLog = this.connection.getAutoSlowLog(); this.useBufferRowSizeThreshold = useBufferRowSizeThreshold; this.useDirectRowUnpack = this.connection.getUseDirectRowUnpack(); this.logSlowQueries = this.connection.getLogSlowQueries(); this.reusablePacket = new Buffer(1024); this.sendPacket = new Buffer(1024); this.port = port; this.host = host; this.socketFactoryClassName = socketFactoryClassName; this.socketFactory = createSocketFactory(); this.mysqlConnection = this.socketFactory.connect(this.host, this.port, props);
/*      */     if (socketTimeout != 0)
/*      */       try {
/*      */         this.mysqlConnection.setSoTimeout(socketTimeout);
/*      */       } catch (Exception ex) {} 
/*      */     this.mysqlConnection = this.socketFactory.beforeHandshake();
/*      */     if (this.connection.getUseReadAheadInput()) {
/*      */       this.mysqlInput = (InputStream)new ReadAheadInputStream(this.mysqlConnection.getInputStream(), 16384, this.connection.getTraceProtocol(), this.connection.getLog());
/*      */     } else if (this.connection.useUnbufferedInput()) {
/*      */       this.mysqlInput = this.mysqlConnection.getInputStream();
/*      */     } else {
/*      */       this.mysqlInput = new BufferedInputStream(this.mysqlConnection.getInputStream(), 16384);
/*      */     } 
/*      */     this.mysqlOutput = new BufferedOutputStream(this.mysqlConnection.getOutputStream(), 16384);
/*      */     this.isInteractiveClient = this.connection.getInteractiveClient();
/*      */     this.profileSql = this.connection.getProfileSql();
/*      */     this.sessionCalendar = Calendar.getInstance();
/*      */     this.autoGenerateTestcaseScript = this.connection.getAutoGenerateTestcaseScript();
/*      */     this.needToGrabQueryFromPacket = (this.profileSql || this.logSlowQueries || this.autoGenerateTestcaseScript);
/*      */     if (this.connection.getUseNanosForElapsedTime() && Util.nanoTimeAvailable()) {
/*      */       this.useNanosForElapsedTime = true;
/*      */       this.queryTimingUnits = Messages.getString("Nanoseconds");
/*      */     } else {
/*      */       this.queryTimingUnits = Messages.getString("Milliseconds");
/*      */     } 
/*      */     if (this.connection.getLogSlowQueries())
/* 1973 */       calculateSlowQueryThreshold();  } final ResultSetInternalMethods sqlQueryDirect(StatementImpl callingStatement, String query, String characterEncoding, Buffer queryPacket, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws Exception { this.statementExecutionDepth++;
/*      */     
/*      */     try {
/* 1976 */       if (this.statementInterceptors != null) {
/* 1977 */         ResultSetInternalMethods interceptedResults = invokeStatementInterceptorsPre(query, callingStatement);
/*      */ 
/*      */         
/* 1980 */         if (interceptedResults != null) {
/* 1981 */           return interceptedResults;
/*      */         }
/*      */       } 
/*      */       
/* 1985 */       long queryStartTime = 0L;
/* 1986 */       long queryEndTime = 0L;
/*      */       
/* 1988 */       if (query != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1993 */         int packLength = 5 + query.length() * 2 + 2;
/*      */         
/* 1995 */         String statementComment = this.connection.getStatementComment();
/*      */         
/* 1997 */         byte[] commentAsBytes = null;
/*      */         
/* 1999 */         if (statementComment != null) {
/* 2000 */           commentAsBytes = StringUtils.getBytes(statementComment, (SingleByteCharsetConverter)null, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2005 */           packLength += commentAsBytes.length;
/* 2006 */           packLength += 6;
/*      */         } 
/*      */         
/* 2009 */         if (this.sendPacket == null) {
/* 2010 */           this.sendPacket = new Buffer(packLength);
/*      */         } else {
/* 2012 */           this.sendPacket.clear();
/*      */         } 
/*      */         
/* 2015 */         this.sendPacket.writeByte((byte)3);
/*      */         
/* 2017 */         if (commentAsBytes != null) {
/* 2018 */           this.sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2019 */           this.sendPacket.writeBytesNoNull(commentAsBytes);
/* 2020 */           this.sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */         } 
/*      */         
/* 2023 */         if (characterEncoding != null) {
/* 2024 */           if (this.platformDbCharsetMatches) {
/* 2025 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */ 
/*      */ 
/*      */           
/*      */           }
/* 2030 */           else if (StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA")) {
/* 2031 */             this.sendPacket.writeBytesNoNull(query.getBytes());
/*      */           } else {
/* 2033 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */           
/*      */           }
/*      */ 
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 2041 */           this.sendPacket.writeStringNoNull(query);
/*      */         } 
/*      */         
/* 2044 */         queryPacket = this.sendPacket;
/*      */       } 
/*      */       
/* 2047 */       byte[] queryBuf = null;
/* 2048 */       int oldPacketPosition = 0;
/*      */       
/* 2050 */       if (this.needToGrabQueryFromPacket) {
/* 2051 */         queryBuf = queryPacket.getByteBuffer();
/*      */ 
/*      */         
/* 2054 */         oldPacketPosition = queryPacket.getPosition();
/*      */         
/* 2056 */         queryStartTime = getCurrentTimeNanosOrMillis();
/*      */       } 
/*      */ 
/*      */       
/* 2060 */       Buffer resultPacket = sendCommand(3, null, queryPacket, false, null);
/*      */ 
/*      */       
/* 2063 */       long fetchBeginTime = 0L;
/* 2064 */       long fetchEndTime = 0L;
/*      */       
/* 2066 */       String profileQueryToLog = null;
/*      */       
/* 2068 */       boolean queryWasSlow = false;
/*      */       
/* 2070 */       if (this.profileSql || this.logSlowQueries) {
/* 2071 */         queryEndTime = System.currentTimeMillis();
/*      */         
/* 2073 */         boolean shouldExtractQuery = false;
/*      */         
/* 2075 */         if (this.profileSql) {
/* 2076 */           shouldExtractQuery = true;
/* 2077 */         } else if (this.logSlowQueries) {
/* 2078 */           long queryTime = queryEndTime - queryStartTime;
/*      */           
/* 2080 */           boolean logSlow = false;
/*      */           
/* 2082 */           if (this.useAutoSlowLog) {
/* 2083 */             logSlow = (queryTime > this.connection.getSlowQueryThresholdMillis());
/*      */           } else {
/* 2085 */             logSlow = this.connection.isAbonormallyLongQuery(queryTime);
/*      */             
/* 2087 */             this.connection.reportQueryTime(queryTime);
/*      */           } 
/*      */           
/* 2090 */           if (logSlow) {
/* 2091 */             shouldExtractQuery = true;
/* 2092 */             queryWasSlow = true;
/*      */           } 
/*      */         } 
/*      */         
/* 2096 */         if (shouldExtractQuery) {
/*      */           
/* 2098 */           boolean truncated = false;
/*      */           
/* 2100 */           int extractPosition = oldPacketPosition;
/*      */           
/* 2102 */           if (oldPacketPosition > this.connection.getMaxQuerySizeToLog()) {
/* 2103 */             extractPosition = this.connection.getMaxQuerySizeToLog() + 5;
/* 2104 */             truncated = true;
/*      */           } 
/*      */           
/* 2107 */           profileQueryToLog = new String(queryBuf, 5, extractPosition - 5);
/*      */ 
/*      */           
/* 2110 */           if (truncated) {
/* 2111 */             profileQueryToLog = profileQueryToLog + Messages.getString("MysqlIO.25");
/*      */           }
/*      */         } 
/*      */         
/* 2115 */         fetchBeginTime = queryEndTime;
/*      */       } 
/*      */       
/* 2118 */       if (this.autoGenerateTestcaseScript) {
/* 2119 */         String testcaseQuery = null;
/*      */         
/* 2121 */         if (query != null) {
/* 2122 */           testcaseQuery = query;
/*      */         } else {
/* 2124 */           testcaseQuery = new String(queryBuf, 5, oldPacketPosition - 5);
/*      */         } 
/*      */ 
/*      */         
/* 2128 */         StringBuffer debugBuf = new StringBuffer(testcaseQuery.length() + 32);
/* 2129 */         this.connection.generateConnectionCommentBlock(debugBuf);
/* 2130 */         debugBuf.append(testcaseQuery);
/* 2131 */         debugBuf.append(';');
/* 2132 */         this.connection.dumpTestcaseQuery(debugBuf.toString());
/*      */       } 
/*      */       
/* 2135 */       ResultSetInternalMethods rs = readAllResults(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, false, -1L, cachedMetadata);
/*      */ 
/*      */ 
/*      */       
/* 2139 */       if (queryWasSlow) {
/* 2140 */         StringBuffer mesgBuf = new StringBuffer(48 + profileQueryToLog.length());
/*      */ 
/*      */         
/* 2143 */         mesgBuf.append(Messages.getString("MysqlIO.SlowQuery", new Object[] { new Long(this.slowQueryThreshold), this.queryTimingUnits, new Long(queryEndTime - queryStartTime) }));
/*      */ 
/*      */ 
/*      */         
/* 2147 */         mesgBuf.append(profileQueryToLog);
/*      */         
/* 2149 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */         
/* 2151 */         eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), (callingStatement != null) ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), this.queryTimingUnits, null, new Throwable(), mesgBuf.toString()));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2158 */         if (this.connection.getExplainSlowQueries()) {
/* 2159 */           if (oldPacketPosition < 1048576) {
/* 2160 */             explainSlowQuery(queryPacket.getBytes(5, oldPacketPosition - 5), profileQueryToLog);
/*      */           } else {
/*      */             
/* 2163 */             this.connection.getLog().logWarn(Messages.getString("MysqlIO.28") + 1048576 + Messages.getString("MysqlIO.29"));
/*      */           } 
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2171 */       if (this.logSlowQueries) {
/*      */         
/* 2173 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */         
/* 2175 */         if (this.queryBadIndexUsed) {
/* 2176 */           eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), (callingStatement != null) ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), Messages.getString("MysqlIO.33") + profileQueryToLog));
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
/* 2189 */         if (this.queryNoIndexUsed) {
/* 2190 */           eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), (callingStatement != null) ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), Messages.getString("MysqlIO.35") + profileQueryToLog));
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
/* 2204 */       if (this.profileSql) {
/* 2205 */         fetchEndTime = getCurrentTimeNanosOrMillis();
/*      */         
/* 2207 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */         
/* 2209 */         eventSink.consumeEvent(new ProfilerEvent((byte)3, "", catalog, this.connection.getId(), (callingStatement != null) ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), profileQueryToLog));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2217 */         eventSink.consumeEvent(new ProfilerEvent((byte)5, "", catalog, this.connection.getId(), (callingStatement != null) ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), fetchEndTime - fetchBeginTime, this.queryTimingUnits, null, new Throwable(), null));
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2226 */       if (this.hadWarnings) {
/* 2227 */         scanForAndThrowDataTruncation();
/*      */       }
/*      */       
/* 2230 */       if (this.statementInterceptors != null) {
/* 2231 */         ResultSetInternalMethods interceptedResults = invokeStatementInterceptorsPost(query, callingStatement, rs);
/*      */ 
/*      */         
/* 2234 */         if (interceptedResults != null) {
/* 2235 */           rs = interceptedResults;
/*      */         }
/*      */       } 
/*      */       
/* 2239 */       return rs;
/*      */     } finally {
/* 2241 */       this.statementExecutionDepth--;
/*      */     }  }
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods invokeStatementInterceptorsPre(String sql, Statement interceptedStatement) throws SQLException {
/* 2247 */     ResultSetInternalMethods previousResultSet = null;
/*      */     
/* 2249 */     Iterator interceptors = this.statementInterceptors.iterator();
/*      */     
/* 2251 */     while (interceptors.hasNext()) {
/* 2252 */       StatementInterceptor interceptor = interceptors.next();
/*      */ 
/*      */       
/* 2255 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2256 */       boolean shouldExecute = ((executeTopLevelOnly && this.statementExecutionDepth == 1) || !executeTopLevelOnly);
/*      */ 
/*      */       
/* 2259 */       if (shouldExecute) {
/* 2260 */         String sqlToInterceptor = sql;
/*      */         
/* 2262 */         if (interceptedStatement instanceof PreparedStatement) {
/* 2263 */           sqlToInterceptor = ((PreparedStatement)interceptedStatement).asSql();
/*      */         }
/*      */ 
/*      */         
/* 2267 */         ResultSetInternalMethods interceptedResultSet = interceptor.preProcess(sqlToInterceptor, interceptedStatement, this.connection);
/*      */ 
/*      */ 
/*      */         
/* 2271 */         if (interceptedResultSet != null) {
/* 2272 */           previousResultSet = interceptedResultSet;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 2277 */     return previousResultSet;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods invokeStatementInterceptorsPost(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet) throws SQLException {
/* 2283 */     Iterator interceptors = this.statementInterceptors.iterator();
/*      */     
/* 2285 */     while (interceptors.hasNext()) {
/* 2286 */       StatementInterceptor interceptor = interceptors.next();
/*      */ 
/*      */       
/* 2289 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2290 */       boolean shouldExecute = ((executeTopLevelOnly && this.statementExecutionDepth == 1) || !executeTopLevelOnly);
/*      */ 
/*      */       
/* 2293 */       if (shouldExecute) {
/* 2294 */         String sqlToInterceptor = sql;
/*      */         
/* 2296 */         if (interceptedStatement instanceof PreparedStatement) {
/* 2297 */           sqlToInterceptor = ((PreparedStatement)interceptedStatement).asSql();
/*      */         }
/*      */ 
/*      */         
/* 2301 */         ResultSetInternalMethods interceptedResultSet = interceptor.postProcess(sqlToInterceptor, interceptedStatement, originalResultSet, this.connection);
/*      */ 
/*      */ 
/*      */         
/* 2305 */         if (interceptedResultSet != null) {
/* 2306 */           originalResultSet = interceptedResultSet;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 2311 */     return originalResultSet;
/*      */   }
/*      */   
/*      */   private void calculateSlowQueryThreshold() {
/* 2315 */     this.slowQueryThreshold = this.connection.getSlowQueryThresholdMillis();
/*      */     
/* 2317 */     if (this.connection.getUseNanosForElapsedTime()) {
/* 2318 */       long nanosThreshold = this.connection.getSlowQueryThresholdNanos();
/*      */       
/* 2320 */       if (nanosThreshold != 0L) {
/* 2321 */         this.slowQueryThreshold = nanosThreshold;
/*      */       } else {
/* 2323 */         this.slowQueryThreshold *= 1000000L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected long getCurrentTimeNanosOrMillis() {
/* 2329 */     if (this.useNanosForElapsedTime) {
/* 2330 */       return Util.getCurrentTimeNanosOrMillis();
/*      */     }
/*      */     
/* 2333 */     return System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getHost() {
/* 2342 */     return this.host;
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
/*      */   boolean isVersion(int major, int minor, int subminor) {
/* 2357 */     return (major == getServerMajorVersion() && minor == getServerMinorVersion() && subminor == getServerSubMinorVersion());
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
/*      */   boolean versionMeetsMinimum(int major, int minor, int subminor) {
/* 2373 */     if (getServerMajorVersion() >= major) {
/* 2374 */       if (getServerMajorVersion() == major) {
/* 2375 */         if (getServerMinorVersion() >= minor) {
/* 2376 */           if (getServerMinorVersion() == minor) {
/* 2377 */             return (getServerSubMinorVersion() >= subminor);
/*      */           }
/*      */ 
/*      */           
/* 2381 */           return true;
/*      */         } 
/*      */ 
/*      */         
/* 2385 */         return false;
/*      */       } 
/*      */ 
/*      */       
/* 2389 */       return true;
/*      */     } 
/*      */     
/* 2392 */     return false;
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
/*      */   private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength) {
/* 2406 */     if (packetLength < 1024) {
/* 2407 */       return packetToDump.dump(packetLength);
/*      */     }
/*      */     
/* 2410 */     StringBuffer packetDumpBuf = new StringBuffer(4096);
/* 2411 */     packetDumpBuf.append(packetToDump.dump(1024));
/* 2412 */     packetDumpBuf.append(Messages.getString("MysqlIO.36"));
/* 2413 */     packetDumpBuf.append(1024);
/* 2414 */     packetDumpBuf.append(Messages.getString("MysqlIO.37"));
/*      */     
/* 2416 */     return packetDumpBuf.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
/* 2421 */     if (len < 0) {
/* 2422 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */     
/* 2425 */     int n = 0;
/*      */     
/* 2427 */     while (n < len) {
/* 2428 */       int count = in.read(b, off + n, len - n);
/*      */       
/* 2430 */       if (count < 0) {
/* 2431 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { new Integer(len), new Integer(n) }));
/*      */       }
/*      */ 
/*      */       
/* 2435 */       n += count;
/*      */     } 
/*      */     
/* 2438 */     return n;
/*      */   }
/*      */   
/*      */   private final long skipFully(InputStream in, long len) throws IOException {
/* 2442 */     if (len < 0L) {
/* 2443 */       throw new IOException("Negative skip length not allowed");
/*      */     }
/*      */     
/* 2446 */     long n = 0L;
/*      */     
/* 2448 */     while (n < len) {
/* 2449 */       long count = in.skip(len - n);
/*      */       
/* 2451 */       if (count < 0L) {
/* 2452 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { new Long(len), new Long(n) }));
/*      */       }
/*      */ 
/*      */       
/* 2456 */       n += count;
/*      */     } 
/*      */     
/* 2459 */     return n;
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
/*      */   protected final ResultSetImpl readResultsForQueryOrUpdate(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException {
/* 2487 */     long columnCount = resultPacket.readFieldLength();
/*      */     
/* 2489 */     if (columnCount == 0L)
/* 2490 */       return buildResultSetWithUpdates(callingStatement, resultPacket); 
/* 2491 */     if (columnCount == -1L) {
/* 2492 */       String charEncoding = null;
/*      */       
/* 2494 */       if (this.connection.getUseUnicode()) {
/* 2495 */         charEncoding = this.connection.getEncoding();
/*      */       }
/*      */       
/* 2498 */       String fileName = null;
/*      */       
/* 2500 */       if (this.platformDbCharsetMatches) {
/* 2501 */         fileName = (charEncoding != null) ? resultPacket.readString(charEncoding) : resultPacket.readString();
/*      */       }
/*      */       else {
/*      */         
/* 2505 */         fileName = resultPacket.readString();
/*      */       } 
/*      */       
/* 2508 */       return sendFileToServer(callingStatement, fileName);
/*      */     } 
/* 2510 */     ResultSetImpl results = getResultSet(callingStatement, columnCount, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, isBinaryEncoded, metadataFromCache);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2515 */     return results;
/*      */   }
/*      */ 
/*      */   
/*      */   private int alignPacketSize(int a, int l) {
/* 2520 */     return a + l - 1 & (l - 1 ^ 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultSetImpl buildResultSetWithRows(StatementImpl callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded) throws SQLException {
/* 2528 */     ResultSetImpl rs = null;
/*      */     
/* 2530 */     switch (resultSetConcurrency) {
/*      */       case 1007:
/* 2532 */         rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */ 
/*      */         
/* 2535 */         if (isBinaryEncoded) {
/* 2536 */           rs.setBinaryEncoded();
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
/* 2552 */         rs.setResultSetType(resultSetType);
/* 2553 */         rs.setResultSetConcurrency(resultSetConcurrency);
/*      */         
/* 2555 */         return rs;case 1008: rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, true); rs.setResultSetType(resultSetType); rs.setResultSetConcurrency(resultSetConcurrency); return rs;
/*      */     } 
/*      */     return ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */   }
/*      */   
/*      */   private ResultSetImpl buildResultSetWithUpdates(StatementImpl callingStatement, Buffer resultPacket) throws SQLException {
/* 2561 */     long updateCount = -1L;
/* 2562 */     long updateID = -1L;
/* 2563 */     String info = null;
/*      */     
/*      */     try {
/* 2566 */       if (this.useNewUpdateCounts) {
/* 2567 */         updateCount = resultPacket.newReadLength();
/* 2568 */         updateID = resultPacket.newReadLength();
/*      */       } else {
/* 2570 */         updateCount = resultPacket.readLength();
/* 2571 */         updateID = resultPacket.readLength();
/*      */       } 
/*      */       
/* 2574 */       if (this.use41Extensions) {
/*      */         
/* 2576 */         this.serverStatus = resultPacket.readInt();
/*      */         
/* 2578 */         checkTransactionState(this.oldServerStatus);
/*      */         
/* 2580 */         this.warningCount = resultPacket.readInt();
/*      */         
/* 2582 */         if (this.warningCount > 0) {
/* 2583 */           this.hadWarnings = true;
/*      */         }
/*      */         
/* 2586 */         resultPacket.readByte();
/*      */         
/* 2588 */         if (this.profileSql) {
/* 2589 */           this.queryNoIndexUsed = ((this.serverStatus & 0x10) != 0);
/*      */           
/* 2591 */           this.queryBadIndexUsed = ((this.serverStatus & 0x20) != 0);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 2596 */       if (this.connection.isReadInfoMsgEnabled()) {
/* 2597 */         info = resultPacket.readString(this.connection.getErrorMessageEncoding());
/*      */       }
/* 2599 */     } catch (Exception ex) {
/* 2600 */       SQLException sqlEx = SQLError.createSQLException(SQLError.get("S1000"), "S1000", -1);
/*      */       
/* 2602 */       sqlEx.initCause(ex);
/*      */       
/* 2604 */       throw sqlEx;
/*      */     } 
/*      */     
/* 2607 */     ResultSetInternalMethods updateRs = ResultSetImpl.getInstance(updateCount, updateID, this.connection, callingStatement);
/*      */ 
/*      */     
/* 2610 */     if (info != null) {
/* 2611 */       ((ResultSetImpl)updateRs).setServerInfo(info);
/*      */     }
/*      */     
/* 2614 */     return (ResultSetImpl)updateRs;
/*      */   }
/*      */   
/*      */   private void checkForOutstandingStreamingData() throws SQLException {
/* 2618 */     if (this.streamingData != null) {
/* 2619 */       boolean shouldClobber = this.connection.getClobberStreamingResults();
/*      */       
/* 2621 */       if (!shouldClobber) {
/* 2622 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"));
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2630 */       this.streamingData.getOwner().realClose(false);
/*      */ 
/*      */       
/* 2633 */       clearInputStream();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private Buffer compressPacket(Buffer packet, int offset, int packetLen, int headerLength) throws SQLException {
/* 2639 */     packet.writeLongInt(packetLen - headerLength);
/* 2640 */     packet.writeByte((byte)0);
/*      */     
/* 2642 */     int lengthToWrite = 0;
/* 2643 */     int compressedLength = 0;
/* 2644 */     byte[] bytesToCompress = packet.getByteBuffer();
/* 2645 */     byte[] compressedBytes = null;
/* 2646 */     int offsetWrite = 0;
/*      */     
/* 2648 */     if (packetLen < 50) {
/* 2649 */       lengthToWrite = packetLen;
/* 2650 */       compressedBytes = packet.getByteBuffer();
/* 2651 */       compressedLength = 0;
/* 2652 */       offsetWrite = offset;
/*      */     } else {
/* 2654 */       compressedBytes = new byte[bytesToCompress.length * 2];
/*      */       
/* 2656 */       this.deflater.reset();
/* 2657 */       this.deflater.setInput(bytesToCompress, offset, packetLen);
/* 2658 */       this.deflater.finish();
/*      */       
/* 2660 */       int compLen = this.deflater.deflate(compressedBytes);
/*      */       
/* 2662 */       if (compLen > packetLen) {
/* 2663 */         lengthToWrite = packetLen;
/* 2664 */         compressedBytes = packet.getByteBuffer();
/* 2665 */         compressedLength = 0;
/* 2666 */         offsetWrite = offset;
/*      */       } else {
/* 2668 */         lengthToWrite = compLen;
/* 2669 */         headerLength += 3;
/* 2670 */         compressedLength = packetLen;
/*      */       } 
/*      */     } 
/*      */     
/* 2674 */     Buffer compressedPacket = new Buffer(packetLen + headerLength);
/*      */     
/* 2676 */     compressedPacket.setPosition(0);
/* 2677 */     compressedPacket.writeLongInt(lengthToWrite);
/* 2678 */     compressedPacket.writeByte(this.packetSequence);
/* 2679 */     compressedPacket.writeLongInt(compressedLength);
/* 2680 */     compressedPacket.writeBytesNoNull(compressedBytes, offsetWrite, lengthToWrite);
/*      */ 
/*      */     
/* 2683 */     return compressedPacket;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException {
/* 2688 */     if (this.use41Extensions) {
/* 2689 */       rowPacket.readByte();
/*      */       
/* 2691 */       this.warningCount = rowPacket.readInt();
/*      */       
/* 2693 */       if (this.warningCount > 0) {
/* 2694 */         this.hadWarnings = true;
/*      */       }
/*      */       
/* 2697 */       this.oldServerStatus = this.serverStatus;
/* 2698 */       this.serverStatus = rowPacket.readInt();
/* 2699 */       checkTransactionState(this.oldServerStatus);
/*      */       
/* 2701 */       if (this.profileSql) {
/* 2702 */         this.queryNoIndexUsed = ((this.serverStatus & 0x10) != 0);
/*      */         
/* 2704 */         this.queryBadIndexUsed = ((this.serverStatus & 0x20) != 0);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private SocketFactory createSocketFactory() throws SQLException {
/*      */     try {
/* 2712 */       if (this.socketFactoryClassName == null) {
/* 2713 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.75"), "08001");
/*      */       }
/*      */ 
/*      */       
/* 2717 */       return (SocketFactory)Class.forName(this.socketFactoryClassName).newInstance();
/*      */     }
/* 2719 */     catch (Exception ex) {
/* 2720 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.76") + this.socketFactoryClassName + Messages.getString("MysqlIO.77"), "08001");
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2725 */       sqlEx.initCause(ex);
/*      */       
/* 2727 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet) throws SQLException {
/* 2734 */     if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
/* 2735 */       this.packetDebugRingBuffer.removeFirst();
/*      */     }
/*      */     
/* 2738 */     StringBuffer packetDump = null;
/*      */     
/* 2740 */     if (!isPacketBeingSent) {
/* 2741 */       int bytesToDump = Math.min(1024, packet.getBufLength());
/*      */ 
/*      */       
/* 2744 */       Buffer packetToDump = new Buffer(4 + bytesToDump);
/*      */       
/* 2746 */       packetToDump.setPosition(0);
/* 2747 */       packetToDump.writeBytesNoNull(header);
/* 2748 */       packetToDump.writeBytesNoNull(packet.getBytes(0, bytesToDump));
/*      */       
/* 2750 */       String packetPayload = packetToDump.dump(bytesToDump);
/*      */       
/* 2752 */       packetDump = new StringBuffer(96 + packetPayload.length());
/*      */       
/* 2754 */       packetDump.append("Server ");
/*      */       
/* 2756 */       if (isPacketReused) {
/* 2757 */         packetDump.append("(re-used)");
/*      */       } else {
/* 2759 */         packetDump.append("(new)");
/*      */       } 
/*      */       
/* 2762 */       packetDump.append(" ");
/* 2763 */       packetDump.append(packet.toSuperString());
/* 2764 */       packetDump.append(" --------------------> Client\n");
/* 2765 */       packetDump.append("\nPacket payload:\n\n");
/* 2766 */       packetDump.append(packetPayload);
/*      */       
/* 2768 */       if (bytesToDump == 1024) {
/* 2769 */         packetDump.append("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 'Ѐ' + " bytes.\n");
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 2774 */       int bytesToDump = Math.min(1024, sendLength);
/*      */       
/* 2776 */       String packetPayload = packet.dump(bytesToDump);
/*      */       
/* 2778 */       packetDump = new StringBuffer(68 + packetPayload.length());
/*      */       
/* 2780 */       packetDump.append("Client ");
/* 2781 */       packetDump.append(packet.toSuperString());
/* 2782 */       packetDump.append("--------------------> Server\n");
/* 2783 */       packetDump.append("\nPacket payload:\n\n");
/* 2784 */       packetDump.append(packetPayload);
/*      */       
/* 2786 */       if (bytesToDump == 1024) {
/* 2787 */         packetDump.append("\nNote: Packet of " + sendLength + " bytes truncated to " + 'Ѐ' + " bytes.\n");
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2793 */     this.packetDebugRingBuffer.addLast(packetDump);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields) throws SQLException {
/* 2800 */     ArrayList rows = new ArrayList();
/*      */     
/* 2802 */     boolean useBufferRowExplicit = useBufferRowExplicit(fields);
/*      */ 
/*      */     
/* 2805 */     ResultSetRow row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */ 
/*      */     
/* 2808 */     int rowCount = 0;
/*      */     
/* 2810 */     if (row != null) {
/* 2811 */       rows.add(row);
/* 2812 */       rowCount = 1;
/*      */     } 
/*      */     
/* 2815 */     while (row != null) {
/* 2816 */       row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */ 
/*      */       
/* 2819 */       if (row != null && (
/* 2820 */         maxRows == -1 || rowCount < maxRows)) {
/* 2821 */         rows.add(row);
/* 2822 */         rowCount++;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2827 */     RowData rowData = new RowDataStatic(rows);
/*      */     
/* 2829 */     return rowData;
/*      */   }
/*      */   
/*      */   public static boolean useBufferRowExplicit(Field[] fields) {
/* 2833 */     if (fields == null) {
/* 2834 */       return false;
/*      */     }
/*      */     
/* 2837 */     for (int i = 0; i < fields.length; i++) {
/* 2838 */       switch (fields[i].getSQLType()) {
/*      */         case -4:
/*      */         case -1:
/*      */         case 2004:
/*      */         case 2005:
/* 2843 */           return true;
/*      */       } 
/*      */     
/*      */     } 
/* 2847 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void reclaimLargeReusablePacket() {
/* 2854 */     if (this.reusablePacket != null && this.reusablePacket.getCapacity() > 1048576)
/*      */     {
/* 2856 */       this.reusablePacket = new Buffer(1024);
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
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse) throws SQLException {
/* 2871 */     return reuseAndReadPacket(reuse, -1);
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
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse, int existingPacketLength) throws SQLException {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: iconst_0
/*      */     //   2: invokevirtual setWasMultiPacket : (Z)V
/*      */     //   5: iconst_0
/*      */     //   6: istore_3
/*      */     //   7: iload_2
/*      */     //   8: iconst_m1
/*      */     //   9: if_icmpne -> 94
/*      */     //   12: aload_0
/*      */     //   13: aload_0
/*      */     //   14: getfield mysqlInput : Ljava/io/InputStream;
/*      */     //   17: aload_0
/*      */     //   18: getfield packetHeaderBuf : [B
/*      */     //   21: iconst_0
/*      */     //   22: iconst_4
/*      */     //   23: invokespecial readFully : (Ljava/io/InputStream;[BII)I
/*      */     //   26: istore #4
/*      */     //   28: iload #4
/*      */     //   30: iconst_4
/*      */     //   31: if_icmpge -> 52
/*      */     //   34: aload_0
/*      */     //   35: invokevirtual forceClose : ()V
/*      */     //   38: new java/io/IOException
/*      */     //   41: dup
/*      */     //   42: ldc_w 'MysqlIO.43'
/*      */     //   45: invokestatic getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   48: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   51: athrow
/*      */     //   52: aload_0
/*      */     //   53: getfield packetHeaderBuf : [B
/*      */     //   56: iconst_0
/*      */     //   57: baload
/*      */     //   58: sipush #255
/*      */     //   61: iand
/*      */     //   62: aload_0
/*      */     //   63: getfield packetHeaderBuf : [B
/*      */     //   66: iconst_1
/*      */     //   67: baload
/*      */     //   68: sipush #255
/*      */     //   71: iand
/*      */     //   72: bipush #8
/*      */     //   74: ishl
/*      */     //   75: iadd
/*      */     //   76: aload_0
/*      */     //   77: getfield packetHeaderBuf : [B
/*      */     //   80: iconst_2
/*      */     //   81: baload
/*      */     //   82: sipush #255
/*      */     //   85: iand
/*      */     //   86: bipush #16
/*      */     //   88: ishl
/*      */     //   89: iadd
/*      */     //   90: istore_3
/*      */     //   91: goto -> 96
/*      */     //   94: iload_2
/*      */     //   95: istore_3
/*      */     //   96: aload_0
/*      */     //   97: getfield traceProtocol : Z
/*      */     //   100: ifeq -> 174
/*      */     //   103: new java/lang/StringBuffer
/*      */     //   106: dup
/*      */     //   107: invokespecial <init> : ()V
/*      */     //   110: astore #4
/*      */     //   112: aload #4
/*      */     //   114: ldc_w 'MysqlIO.44'
/*      */     //   117: invokestatic getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   120: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   123: pop
/*      */     //   124: aload #4
/*      */     //   126: iload_3
/*      */     //   127: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   130: pop
/*      */     //   131: aload #4
/*      */     //   133: ldc_w 'MysqlIO.45'
/*      */     //   136: invokestatic getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   139: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   142: pop
/*      */     //   143: aload #4
/*      */     //   145: aload_0
/*      */     //   146: getfield packetHeaderBuf : [B
/*      */     //   149: iconst_4
/*      */     //   150: invokestatic dumpAsHex : ([BI)Ljava/lang/String;
/*      */     //   153: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   156: pop
/*      */     //   157: aload_0
/*      */     //   158: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   161: invokevirtual getLog : ()Lcom/mysql/jdbc/log/Log;
/*      */     //   164: aload #4
/*      */     //   166: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   169: invokeinterface logTrace : (Ljava/lang/Object;)V
/*      */     //   174: aload_0
/*      */     //   175: getfield packetHeaderBuf : [B
/*      */     //   178: iconst_3
/*      */     //   179: baload
/*      */     //   180: istore #4
/*      */     //   182: aload_0
/*      */     //   183: getfield packetSequenceReset : Z
/*      */     //   186: ifne -> 212
/*      */     //   189: aload_0
/*      */     //   190: getfield enablePacketDebug : Z
/*      */     //   193: ifeq -> 217
/*      */     //   196: aload_0
/*      */     //   197: getfield checkPacketSequence : Z
/*      */     //   200: ifeq -> 217
/*      */     //   203: aload_0
/*      */     //   204: iload #4
/*      */     //   206: invokespecial checkPacketSequencing : (B)V
/*      */     //   209: goto -> 217
/*      */     //   212: aload_0
/*      */     //   213: iconst_0
/*      */     //   214: putfield packetSequenceReset : Z
/*      */     //   217: aload_0
/*      */     //   218: iload #4
/*      */     //   220: putfield readPacketSequence : B
/*      */     //   223: aload_1
/*      */     //   224: iconst_0
/*      */     //   225: invokevirtual setPosition : (I)V
/*      */     //   228: aload_1
/*      */     //   229: invokevirtual getByteBuffer : ()[B
/*      */     //   232: arraylength
/*      */     //   233: iload_3
/*      */     //   234: if_icmpgt -> 246
/*      */     //   237: aload_1
/*      */     //   238: iload_3
/*      */     //   239: iconst_1
/*      */     //   240: iadd
/*      */     //   241: newarray byte
/*      */     //   243: invokevirtual setByteBuffer : ([B)V
/*      */     //   246: aload_1
/*      */     //   247: iload_3
/*      */     //   248: invokevirtual setBufLength : (I)V
/*      */     //   251: aload_0
/*      */     //   252: aload_0
/*      */     //   253: getfield mysqlInput : Ljava/io/InputStream;
/*      */     //   256: aload_1
/*      */     //   257: invokevirtual getByteBuffer : ()[B
/*      */     //   260: iconst_0
/*      */     //   261: iload_3
/*      */     //   262: invokespecial readFully : (Ljava/io/InputStream;[BII)I
/*      */     //   265: istore #5
/*      */     //   267: iload #5
/*      */     //   269: iload_3
/*      */     //   270: if_icmpeq -> 310
/*      */     //   273: new java/io/IOException
/*      */     //   276: dup
/*      */     //   277: new java/lang/StringBuffer
/*      */     //   280: dup
/*      */     //   281: invokespecial <init> : ()V
/*      */     //   284: ldc 'Short read, expected '
/*      */     //   286: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   289: iload_3
/*      */     //   290: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   293: ldc ' bytes, only read '
/*      */     //   295: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   298: iload #5
/*      */     //   300: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   303: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   306: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   309: athrow
/*      */     //   310: aload_0
/*      */     //   311: getfield traceProtocol : Z
/*      */     //   314: ifeq -> 366
/*      */     //   317: new java/lang/StringBuffer
/*      */     //   320: dup
/*      */     //   321: invokespecial <init> : ()V
/*      */     //   324: astore #6
/*      */     //   326: aload #6
/*      */     //   328: ldc_w 'MysqlIO.46'
/*      */     //   331: invokestatic getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   334: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   337: pop
/*      */     //   338: aload #6
/*      */     //   340: aload_1
/*      */     //   341: iload_3
/*      */     //   342: invokestatic getPacketDumpToLog : (Lcom/mysql/jdbc/Buffer;I)Ljava/lang/String;
/*      */     //   345: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   348: pop
/*      */     //   349: aload_0
/*      */     //   350: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   353: invokevirtual getLog : ()Lcom/mysql/jdbc/log/Log;
/*      */     //   356: aload #6
/*      */     //   358: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   361: invokeinterface logTrace : (Ljava/lang/Object;)V
/*      */     //   366: aload_0
/*      */     //   367: getfield enablePacketDebug : Z
/*      */     //   370: ifeq -> 385
/*      */     //   373: aload_0
/*      */     //   374: iconst_0
/*      */     //   375: iconst_1
/*      */     //   376: iconst_0
/*      */     //   377: aload_0
/*      */     //   378: getfield packetHeaderBuf : [B
/*      */     //   381: aload_1
/*      */     //   382: invokespecial enqueuePacketForDebugging : (ZZI[BLcom/mysql/jdbc/Buffer;)V
/*      */     //   385: iconst_0
/*      */     //   386: istore #6
/*      */     //   388: iload_3
/*      */     //   389: aload_0
/*      */     //   390: getfield maxThreeBytes : I
/*      */     //   393: if_icmpne -> 420
/*      */     //   396: aload_1
/*      */     //   397: aload_0
/*      */     //   398: getfield maxThreeBytes : I
/*      */     //   401: invokevirtual setPosition : (I)V
/*      */     //   404: iload_3
/*      */     //   405: istore #7
/*      */     //   407: iconst_1
/*      */     //   408: istore #6
/*      */     //   410: aload_0
/*      */     //   411: aload_1
/*      */     //   412: iload #4
/*      */     //   414: iload #7
/*      */     //   416: invokespecial readRemainingMultiPackets : (Lcom/mysql/jdbc/Buffer;BI)I
/*      */     //   419: istore_3
/*      */     //   420: iload #6
/*      */     //   422: ifne -> 432
/*      */     //   425: aload_1
/*      */     //   426: invokevirtual getByteBuffer : ()[B
/*      */     //   429: iload_3
/*      */     //   430: iconst_0
/*      */     //   431: bastore
/*      */     //   432: aload_0
/*      */     //   433: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   436: invokevirtual getMaintainTimeStats : ()Z
/*      */     //   439: ifeq -> 449
/*      */     //   442: aload_0
/*      */     //   443: invokestatic currentTimeMillis : ()J
/*      */     //   446: putfield lastPacketReceivedTimeMs : J
/*      */     //   449: aload_1
/*      */     //   450: areturn
/*      */     //   451: astore_3
/*      */     //   452: aload_0
/*      */     //   453: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   456: aload_0
/*      */     //   457: getfield lastPacketSentTimeMs : J
/*      */     //   460: aload_0
/*      */     //   461: getfield lastPacketReceivedTimeMs : J
/*      */     //   464: aload_3
/*      */     //   465: invokestatic createCommunicationsException : (Lcom/mysql/jdbc/ConnectionImpl;JJLjava/lang/Exception;)Ljava/sql/SQLException;
/*      */     //   468: athrow
/*      */     //   469: astore_3
/*      */     //   470: aload_0
/*      */     //   471: invokevirtual clearInputStream : ()V
/*      */     //   474: jsr -> 488
/*      */     //   477: goto -> 507
/*      */     //   480: astore #8
/*      */     //   482: jsr -> 488
/*      */     //   485: aload #8
/*      */     //   487: athrow
/*      */     //   488: astore #9
/*      */     //   490: aload_0
/*      */     //   491: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   494: iconst_0
/*      */     //   495: iconst_0
/*      */     //   496: iconst_1
/*      */     //   497: aload_3
/*      */     //   498: invokevirtual realClose : (ZZZLjava/lang/Throwable;)V
/*      */     //   501: aload_3
/*      */     //   502: athrow
/*      */     //   503: astore #10
/*      */     //   505: aload_3
/*      */     //   506: athrow
/*      */     //   507: goto -> 507
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #2878	-> 0
/*      */     //   #2879	-> 5
/*      */     //   #2881	-> 7
/*      */     //   #2882	-> 12
/*      */     //   #2885	-> 28
/*      */     //   #2886	-> 34
/*      */     //   #2887	-> 38
/*      */     //   #2890	-> 52
/*      */     //   #2894	-> 94
/*      */     //   #2897	-> 96
/*      */     //   #2898	-> 103
/*      */     //   #2900	-> 112
/*      */     //   #2901	-> 124
/*      */     //   #2902	-> 131
/*      */     //   #2903	-> 143
/*      */     //   #2906	-> 157
/*      */     //   #2909	-> 174
/*      */     //   #2911	-> 182
/*      */     //   #2912	-> 189
/*      */     //   #2913	-> 203
/*      */     //   #2916	-> 212
/*      */     //   #2919	-> 217
/*      */     //   #2922	-> 223
/*      */     //   #2930	-> 228
/*      */     //   #2931	-> 237
/*      */     //   #2935	-> 246
/*      */     //   #2938	-> 251
/*      */     //   #2941	-> 267
/*      */     //   #2942	-> 273
/*      */     //   #2946	-> 310
/*      */     //   #2947	-> 317
/*      */     //   #2949	-> 326
/*      */     //   #2950	-> 338
/*      */     //   #2953	-> 349
/*      */     //   #2956	-> 366
/*      */     //   #2957	-> 373
/*      */     //   #2961	-> 385
/*      */     //   #2963	-> 388
/*      */     //   #2964	-> 396
/*      */     //   #2966	-> 404
/*      */     //   #2969	-> 407
/*      */     //   #2971	-> 410
/*      */     //   #2975	-> 420
/*      */     //   #2976	-> 425
/*      */     //   #2979	-> 432
/*      */     //   #2980	-> 442
/*      */     //   #2983	-> 449
/*      */     //   #2984	-> 451
/*      */     //   #2985	-> 452
/*      */     //   #2987	-> 469
/*      */     //   #2990	-> 470
/*      */     //   #2991	-> 474
/*      */     //   #2997	-> 477
/*      */     //   #2992	-> 480
/*      */     //   #2993	-> 490
/*      */     //   #2995	-> 501
/*      */     //   #3000	-> 507
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   28	63	4	lengthRead	I
/*      */     //   112	62	4	traceMessageBuf	Ljava/lang/StringBuffer;
/*      */     //   326	40	6	traceMessageBuf	Ljava/lang/StringBuffer;
/*      */     //   407	13	7	packetEndPoint	I
/*      */     //   7	444	3	packetLength	I
/*      */     //   182	269	4	multiPacketSeq	B
/*      */     //   267	184	5	numBytesRead	I
/*      */     //   388	63	6	isMultiPacket	Z
/*      */     //   452	17	3	ioEx	Ljava/io/IOException;
/*      */     //   470	37	3	oom	Ljava/lang/OutOfMemoryError;
/*      */     //   0	510	0	this	Lcom/mysql/jdbc/MysqlIO;
/*      */     //   0	510	1	reuse	Lcom/mysql/jdbc/Buffer;
/*      */     //   0	510	2	existingPacketLength	I
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	450	451	java/io/IOException
/*      */     //   0	450	469	java/lang/OutOfMemoryError
/*      */     //   470	477	480	finally
/*      */     //   480	485	480	finally
/*      */     //   490	501	503	finally
/*      */     //   503	505	503	finally
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
/*      */   private int readRemainingMultiPackets(Buffer reuse, byte multiPacketSeq, int packetEndPoint) throws IOException, SQLException {
/* 3006 */     int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*      */     
/* 3009 */     if (lengthRead < 4) {
/* 3010 */       forceClose();
/* 3011 */       throw new IOException(Messages.getString("MysqlIO.47"));
/*      */     } 
/*      */     
/* 3014 */     int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/*      */ 
/*      */     
/* 3018 */     Buffer multiPacket = new Buffer(packetLength);
/* 3019 */     boolean firstMultiPkt = true;
/*      */     
/*      */     while (true) {
/* 3022 */       if (!firstMultiPkt) {
/* 3023 */         lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*      */         
/* 3026 */         if (lengthRead < 4) {
/* 3027 */           forceClose();
/* 3028 */           throw new IOException(Messages.getString("MysqlIO.48"));
/*      */         } 
/*      */ 
/*      */         
/* 3032 */         packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       }
/*      */       else {
/*      */         
/* 3036 */         firstMultiPkt = false;
/*      */       } 
/*      */       
/* 3039 */       if (!this.useNewLargePackets && packetLength == 1) {
/* 3040 */         clearInputStream();
/*      */         break;
/*      */       } 
/* 3043 */       if (packetLength < this.maxThreeBytes) {
/* 3044 */         byte b = this.packetHeaderBuf[3];
/*      */         
/* 3046 */         if (b != multiPacketSeq + 1) {
/* 3047 */           throw new IOException(Messages.getString("MysqlIO.49"));
/*      */         }
/*      */ 
/*      */         
/* 3051 */         multiPacketSeq = b;
/*      */ 
/*      */         
/* 3054 */         multiPacket.setPosition(0);
/*      */ 
/*      */         
/* 3057 */         multiPacket.setBufLength(packetLength);
/*      */ 
/*      */         
/* 3060 */         byte[] arrayOfByte = multiPacket.getByteBuffer();
/* 3061 */         int i = packetLength;
/*      */         
/* 3063 */         int j = readFully(this.mysqlInput, arrayOfByte, 0, packetLength);
/*      */ 
/*      */         
/* 3066 */         if (j != i) {
/* 3067 */           throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.50") + i + Messages.getString("MysqlIO.51") + j + "."));
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3077 */         reuse.writeBytesNoNull(arrayOfByte, 0, i);
/*      */         
/* 3079 */         packetEndPoint += i;
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/* 3084 */       byte newPacketSeq = this.packetHeaderBuf[3];
/*      */       
/* 3086 */       if (newPacketSeq != multiPacketSeq + 1) {
/* 3087 */         throw new IOException(Messages.getString("MysqlIO.53"));
/*      */       }
/*      */ 
/*      */       
/* 3091 */       multiPacketSeq = newPacketSeq;
/*      */ 
/*      */       
/* 3094 */       multiPacket.setPosition(0);
/*      */ 
/*      */       
/* 3097 */       multiPacket.setBufLength(packetLength);
/*      */ 
/*      */       
/* 3100 */       byte[] byteBuf = multiPacket.getByteBuffer();
/* 3101 */       int lengthToWrite = packetLength;
/*      */       
/* 3103 */       int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
/*      */ 
/*      */       
/* 3106 */       if (bytesRead != lengthToWrite) {
/* 3107 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.54") + lengthToWrite + Messages.getString("MysqlIO.55") + bytesRead + "."));
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3116 */       reuse.writeBytesNoNull(byteBuf, 0, lengthToWrite);
/*      */       
/* 3118 */       packetEndPoint += lengthToWrite;
/*      */     } 
/*      */     
/* 3121 */     reuse.setPosition(0);
/* 3122 */     reuse.setWasMultiPacket(true);
/* 3123 */     return packetLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkPacketSequencing(byte multiPacketSeq) throws SQLException {
/* 3132 */     if (multiPacketSeq == Byte.MIN_VALUE && this.readPacketSequence != Byte.MAX_VALUE) {
/* 3133 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3139 */     if (this.readPacketSequence == -1 && multiPacketSeq != 0) {
/* 3140 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3146 */     if (multiPacketSeq != Byte.MIN_VALUE && this.readPacketSequence != -1 && multiPacketSeq != this.readPacketSequence + 1)
/*      */     {
/* 3148 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void enableMultiQueries() throws SQLException {
/* 3157 */     Buffer buf = getSharedSendPacket();
/*      */     
/* 3159 */     buf.clear();
/* 3160 */     buf.writeByte((byte)27);
/* 3161 */     buf.writeInt(0);
/* 3162 */     sendCommand(27, null, buf, false, null);
/*      */   }
/*      */   
/*      */   void disableMultiQueries() throws SQLException {
/* 3166 */     Buffer buf = getSharedSendPacket();
/*      */     
/* 3168 */     buf.clear();
/* 3169 */     buf.writeByte((byte)27);
/* 3170 */     buf.writeInt(1);
/* 3171 */     sendCommand(27, null, buf, false, null);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void send(Buffer packet, int packetLen) throws SQLException {
/*      */     try {
/* 3177 */       if (packetLen > this.maxAllowedPacket) {
/* 3178 */         throw new PacketTooBigException(packetLen, this.maxAllowedPacket);
/*      */       }
/*      */       
/* 3181 */       if (this.serverMajorVersion >= 4 && packetLen >= this.maxThreeBytes) {
/*      */         
/* 3183 */         sendSplitPackets(packet);
/*      */       } else {
/* 3185 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */         
/* 3187 */         Buffer packetToSend = packet;
/*      */         
/* 3189 */         packetToSend.setPosition(0);
/*      */         
/* 3191 */         if (this.useCompression) {
/* 3192 */           int originalPacketLen = packetLen;
/*      */           
/* 3194 */           packetToSend = compressPacket(packet, 0, packetLen, 4);
/*      */           
/* 3196 */           packetLen = packetToSend.getPosition();
/*      */           
/* 3198 */           if (this.traceProtocol) {
/* 3199 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */             
/* 3201 */             traceMessageBuf.append(Messages.getString("MysqlIO.57"));
/* 3202 */             traceMessageBuf.append(getPacketDumpToLog(packetToSend, packetLen));
/*      */             
/* 3204 */             traceMessageBuf.append(Messages.getString("MysqlIO.58"));
/* 3205 */             traceMessageBuf.append(getPacketDumpToLog(packet, originalPacketLen));
/*      */ 
/*      */             
/* 3208 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           } 
/*      */         } else {
/* 3211 */           packetToSend.writeLongInt(packetLen - 4);
/* 3212 */           packetToSend.writeByte(this.packetSequence);
/*      */           
/* 3214 */           if (this.traceProtocol) {
/* 3215 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */             
/* 3217 */             traceMessageBuf.append(Messages.getString("MysqlIO.59"));
/* 3218 */             traceMessageBuf.append(packetToSend.dump(packetLen));
/*      */             
/* 3220 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 3225 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */         
/* 3227 */         this.mysqlOutput.flush();
/*      */       } 
/*      */       
/* 3230 */       if (this.enablePacketDebug) {
/* 3231 */         enqueuePacketForDebugging(true, false, packetLen + 5, this.packetHeaderBuf, packet);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3238 */       if (packet == this.sharedSendPacket) {
/* 3239 */         reclaimLargeSharedSendPacket();
/*      */       }
/*      */       
/* 3242 */       if (this.connection.getMaintainTimeStats()) {
/* 3243 */         this.lastPacketSentTimeMs = System.currentTimeMillis();
/*      */       }
/* 3245 */     } catch (IOException ioEx) {
/* 3246 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
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
/*      */   private final ResultSetImpl sendFileToServer(StatementImpl callingStatement, String fileName) throws SQLException {
/* 3264 */     Buffer filePacket = (this.loadFileBufRef == null) ? null : this.loadFileBufRef.get();
/*      */ 
/*      */     
/* 3267 */     int bigPacketLength = Math.min(this.connection.getMaxAllowedPacket() - 12, alignPacketSize(this.connection.getMaxAllowedPacket() - 16, 4096) - 12);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3272 */     int oneMeg = 1048576;
/*      */     
/* 3274 */     int smallerPacketSizeAligned = Math.min(oneMeg - 12, alignPacketSize(oneMeg - 16, 4096) - 12);
/*      */ 
/*      */     
/* 3277 */     int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
/*      */     
/* 3279 */     if (filePacket == null) {
/*      */       try {
/* 3281 */         filePacket = new Buffer(packetLength + 4);
/* 3282 */         this.loadFileBufRef = new SoftReference(filePacket);
/* 3283 */       } catch (OutOfMemoryError oom) {
/* 3284 */         throw SQLError.createSQLException("Could not allocate packet of " + packetLength + " bytes required for LOAD DATA LOCAL INFILE operation." + " Try increasing max heap allocation for JVM or decreasing server variable " + "'max_allowed_packet'", "S1001");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3292 */     filePacket.clear();
/* 3293 */     send(filePacket, 0);
/*      */     
/* 3295 */     byte[] fileBuf = new byte[packetLength];
/*      */     
/* 3297 */     BufferedInputStream fileIn = null;
/*      */     
/*      */     try {
/* 3300 */       if (!this.connection.getAllowLoadLocalInfile()) {
/* 3301 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.LoadDataLocalNotAllowed"), "S1000");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 3306 */       InputStream hookedStream = null;
/*      */       
/* 3308 */       if (callingStatement != null) {
/* 3309 */         hookedStream = callingStatement.getLocalInfileInputStream();
/*      */       }
/*      */       
/* 3312 */       if (hookedStream != null) {
/* 3313 */         fileIn = new BufferedInputStream(hookedStream);
/* 3314 */       } else if (!this.connection.getAllowUrlInLocalInfile()) {
/* 3315 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       
/*      */       }
/* 3318 */       else if (fileName.indexOf(':') != -1) {
/*      */         try {
/* 3320 */           URL urlFromFileName = new URL(fileName);
/* 3321 */           fileIn = new BufferedInputStream(urlFromFileName.openStream());
/* 3322 */         } catch (MalformedURLException badUrlEx) {
/*      */           
/* 3324 */           fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */         } 
/*      */       } else {
/*      */         
/* 3328 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 3333 */       int bytesRead = 0;
/*      */       
/* 3335 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 3336 */         filePacket.clear();
/* 3337 */         filePacket.writeBytesNoNull(fileBuf, 0, bytesRead);
/* 3338 */         send(filePacket, filePacket.getPosition());
/*      */       } 
/* 3340 */     } catch (IOException ioEx) {
/* 3341 */       StringBuffer messageBuf = new StringBuffer(Messages.getString("MysqlIO.60"));
/*      */ 
/*      */       
/* 3344 */       if (!this.connection.getParanoid()) {
/* 3345 */         messageBuf.append("'");
/*      */         
/* 3347 */         if (fileName != null) {
/* 3348 */           messageBuf.append(fileName);
/*      */         }
/*      */         
/* 3351 */         messageBuf.append("'");
/*      */       } 
/*      */       
/* 3354 */       messageBuf.append(Messages.getString("MysqlIO.63"));
/*      */       
/* 3356 */       if (!this.connection.getParanoid()) {
/* 3357 */         messageBuf.append(Messages.getString("MysqlIO.64"));
/* 3358 */         messageBuf.append(Util.stackTraceToString(ioEx));
/*      */       } 
/*      */       
/* 3361 */       throw SQLError.createSQLException(messageBuf.toString(), "S1009");
/*      */     } finally {
/*      */       
/* 3364 */       if (fileIn != null) {
/*      */         try {
/* 3366 */           fileIn.close();
/* 3367 */         } catch (Exception ex) {
/* 3368 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.65"), "S1000");
/*      */           
/* 3370 */           sqlEx.initCause(ex);
/*      */           
/* 3372 */           throw sqlEx;
/*      */         } 
/*      */         
/* 3375 */         fileIn = null;
/*      */       } else {
/*      */         
/* 3378 */         filePacket.clear();
/* 3379 */         send(filePacket, filePacket.getPosition());
/* 3380 */         checkErrorPacket();
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 3385 */     filePacket.clear();
/* 3386 */     send(filePacket, filePacket.getPosition());
/*      */     
/* 3388 */     Buffer resultPacket = checkErrorPacket();
/*      */     
/* 3390 */     return buildResultSetWithUpdates(callingStatement, resultPacket);
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
/*      */   private Buffer checkErrorPacket(int command) throws SQLException {
/* 3405 */     int statusCode = 0;
/* 3406 */     Buffer resultPacket = null;
/* 3407 */     this.serverStatus = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 3414 */       resultPacket = reuseAndReadPacket(this.reusablePacket);
/* 3415 */     } catch (SQLException sqlEx) {
/*      */       
/* 3417 */       throw sqlEx;
/* 3418 */     } catch (Exception fallThru) {
/* 3419 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, fallThru);
/*      */     } 
/*      */ 
/*      */     
/* 3423 */     checkErrorPacket(resultPacket);
/*      */     
/* 3425 */     return resultPacket;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkErrorPacket(Buffer resultPacket) throws SQLException {
/* 3430 */     int statusCode = resultPacket.readByte();
/*      */ 
/*      */     
/* 3433 */     if (statusCode == -1) {
/*      */       
/* 3435 */       int errno = 2000;
/*      */       
/* 3437 */       if (this.protocolVersion > 9) {
/* 3438 */         errno = resultPacket.readInt();
/*      */         
/* 3440 */         String xOpen = null;
/*      */         
/* 3442 */         String str1 = resultPacket.readString(this.connection.getErrorMessageEncoding());
/*      */ 
/*      */         
/* 3445 */         if (str1.charAt(0) == '#') {
/*      */ 
/*      */           
/* 3448 */           if (str1.length() > 6) {
/* 3449 */             xOpen = str1.substring(1, 6);
/* 3450 */             str1 = str1.substring(6);
/*      */             
/* 3452 */             if (xOpen.equals("HY000")) {
/* 3453 */               xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */             }
/*      */           } else {
/*      */             
/* 3457 */             xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           } 
/*      */         } else {
/*      */           
/* 3461 */           xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */         } 
/*      */ 
/*      */         
/* 3465 */         clearInputStream();
/*      */         
/* 3467 */         StringBuffer stringBuffer = new StringBuffer();
/*      */         
/* 3469 */         String xOpenErrorMessage = SQLError.get(xOpen);
/*      */         
/* 3471 */         if (!this.connection.getUseOnlyServerErrorMessages() && 
/* 3472 */           xOpenErrorMessage != null) {
/* 3473 */           stringBuffer.append(xOpenErrorMessage);
/* 3474 */           stringBuffer.append(Messages.getString("MysqlIO.68"));
/*      */         } 
/*      */ 
/*      */         
/* 3478 */         stringBuffer.append(str1);
/*      */         
/* 3480 */         if (!this.connection.getUseOnlyServerErrorMessages() && 
/* 3481 */           xOpenErrorMessage != null) {
/* 3482 */           stringBuffer.append("\"");
/*      */         }
/*      */ 
/*      */         
/* 3486 */         appendInnodbStatusInformation(xOpen, stringBuffer);
/*      */         
/* 3488 */         if (xOpen != null && xOpen.startsWith("22")) {
/* 3489 */           throw new MysqlDataTruncation(stringBuffer.toString(), 0, true, false, 0, 0);
/*      */         }
/* 3491 */         throw SQLError.createSQLException(stringBuffer.toString(), xOpen, errno);
/*      */       } 
/*      */ 
/*      */       
/* 3495 */       String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding());
/*      */       
/* 3497 */       clearInputStream();
/*      */       
/* 3499 */       if (serverErrorMessage.indexOf(Messages.getString("MysqlIO.70")) != -1) {
/* 3500 */         throw SQLError.createSQLException(SQLError.get("S0022") + ", " + serverErrorMessage, "S0022", -1);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3507 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.72"));
/*      */       
/* 3509 */       errorBuf.append(serverErrorMessage);
/* 3510 */       errorBuf.append("\"");
/*      */       
/* 3512 */       throw SQLError.createSQLException(SQLError.get("S1000") + ", " + errorBuf.toString(), "S1000", -1);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void appendInnodbStatusInformation(String xOpen, StringBuffer errorBuf) throws SQLException {
/* 3520 */     if (this.connection.getIncludeInnodbStatusInDeadlockExceptions() && xOpen != null && (xOpen.startsWith("40") || xOpen.startsWith("41")) && this.streamingData == null) {
/*      */ 
/*      */ 
/*      */       
/* 3524 */       ResultSet rs = null;
/*      */       
/*      */       try {
/* 3527 */         rs = sqlQueryDirect(null, "SHOW ENGINE INNODB STATUS", this.connection.getEncoding(), null, -1, 1003, 1007, false, this.connection.getCatalog(), null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3533 */         if (rs.next()) {
/* 3534 */           errorBuf.append("\n\n");
/* 3535 */           errorBuf.append(rs.getString(1));
/*      */         } else {
/* 3537 */           errorBuf.append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
/*      */         }
/*      */       
/* 3540 */       } catch (Exception ex) {
/* 3541 */         errorBuf.append(Messages.getString("MysqlIO.InnoDBStatusFailed"));
/*      */         
/* 3543 */         errorBuf.append("\n\n");
/* 3544 */         errorBuf.append(Util.stackTraceToString(ex));
/*      */       } finally {
/* 3546 */         if (rs != null) {
/* 3547 */           rs.close();
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
/*      */   private final void sendSplitPackets(Buffer packet) throws SQLException {
/*      */     try {
/* 3573 */       Buffer headerPacket = (this.splitBufRef == null) ? null : this.splitBufRef.get();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3581 */       if (headerPacket == null) {
/* 3582 */         headerPacket = new Buffer(this.maxThreeBytes + 4);
/*      */         
/* 3584 */         this.splitBufRef = new SoftReference(headerPacket);
/*      */       } 
/*      */       
/* 3587 */       int len = packet.getPosition();
/* 3588 */       int splitSize = this.maxThreeBytes;
/* 3589 */       int originalPacketPos = 4;
/* 3590 */       byte[] origPacketBytes = packet.getByteBuffer();
/* 3591 */       byte[] headerPacketBytes = headerPacket.getByteBuffer();
/*      */       
/* 3593 */       while (len >= this.maxThreeBytes) {
/* 3594 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */         
/* 3596 */         headerPacket.setPosition(0);
/* 3597 */         headerPacket.writeLongInt(splitSize);
/*      */         
/* 3599 */         headerPacket.writeByte(this.packetSequence);
/* 3600 */         System.arraycopy(origPacketBytes, originalPacketPos, headerPacketBytes, 4, splitSize);
/*      */ 
/*      */         
/* 3603 */         int i = splitSize + 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3609 */         if (!this.useCompression) {
/* 3610 */           this.mysqlOutput.write(headerPacketBytes, 0, splitSize + 4);
/*      */           
/* 3612 */           this.mysqlOutput.flush();
/*      */         }
/*      */         else {
/*      */           
/* 3616 */           headerPacket.setPosition(0);
/* 3617 */           Buffer packetToSend = compressPacket(headerPacket, 4, splitSize, 4);
/*      */           
/* 3619 */           i = packetToSend.getPosition();
/*      */           
/* 3621 */           this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, i);
/*      */           
/* 3623 */           this.mysqlOutput.flush();
/*      */         } 
/*      */         
/* 3626 */         originalPacketPos += splitSize;
/* 3627 */         len -= splitSize;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3633 */       headerPacket.clear();
/* 3634 */       headerPacket.setPosition(0);
/* 3635 */       headerPacket.writeLongInt(len - 4);
/* 3636 */       this.packetSequence = (byte)(this.packetSequence + 1);
/* 3637 */       headerPacket.writeByte(this.packetSequence);
/*      */       
/* 3639 */       if (len != 0) {
/* 3640 */         System.arraycopy(origPacketBytes, originalPacketPos, headerPacketBytes, 4, len - 4);
/*      */       }
/*      */ 
/*      */       
/* 3644 */       int packetLen = len - 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3650 */       if (!this.useCompression) {
/* 3651 */         this.mysqlOutput.write(headerPacket.getByteBuffer(), 0, len);
/* 3652 */         this.mysqlOutput.flush();
/*      */       }
/*      */       else {
/*      */         
/* 3656 */         headerPacket.setPosition(0);
/* 3657 */         Buffer packetToSend = compressPacket(headerPacket, 4, packetLen, 4);
/*      */         
/* 3659 */         packetLen = packetToSend.getPosition();
/*      */         
/* 3661 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */         
/* 3663 */         this.mysqlOutput.flush();
/*      */       } 
/* 3665 */     } catch (IOException ioEx) {
/* 3666 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void reclaimLargeSharedSendPacket() {
/* 3672 */     if (this.sharedSendPacket != null && this.sharedSendPacket.getCapacity() > 1048576)
/*      */     {
/* 3674 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */   
/*      */   boolean hadWarnings() {
/* 3679 */     return this.hadWarnings;
/*      */   }
/*      */   
/*      */   void scanForAndThrowDataTruncation() throws SQLException {
/* 3683 */     if (this.streamingData == null && versionMeetsMinimum(4, 1, 0) && this.connection.getJdbcCompliantTruncation() && this.warningCount > 0)
/*      */     {
/* 3685 */       SQLError.convertShowWarningsToSQLWarnings(this.connection, this.warningCount, true);
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
/*      */   private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams) throws SQLException {
/* 3706 */     if (packet == null) {
/* 3707 */       packet = new Buffer(packLength);
/*      */     }
/*      */     
/* 3710 */     if (writeClientParams) {
/* 3711 */       if (this.use41Extensions) {
/* 3712 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 3713 */           packet.writeLong(this.clientParam);
/* 3714 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3719 */           packet.writeByte((byte)8);
/*      */ 
/*      */           
/* 3722 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 3724 */           packet.writeLong(this.clientParam);
/* 3725 */           packet.writeLong(this.maxThreeBytes);
/*      */         } 
/*      */       } else {
/* 3728 */         packet.writeInt((int)this.clientParam);
/* 3729 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3734 */     packet.writeString(user, "Cp1252", this.connection);
/*      */     
/* 3736 */     if (password.length() != 0) {
/*      */       
/* 3738 */       packet.writeString("xxxxxxxx", "Cp1252", this.connection);
/*      */     } else {
/*      */       
/* 3741 */       packet.writeString("", "Cp1252", this.connection);
/*      */     } 
/*      */     
/* 3744 */     if (this.useConnectWithDb) {
/* 3745 */       packet.writeString(database, "Cp1252", this.connection);
/*      */     }
/*      */     
/* 3748 */     send(packet, packet.getPosition());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3753 */     if (password.length() > 0) {
/* 3754 */       Buffer b = readPacket();
/*      */       
/* 3756 */       b.setPosition(0);
/*      */       
/* 3758 */       byte[] replyAsBytes = b.getByteBuffer();
/*      */       
/* 3760 */       if (replyAsBytes.length == 25 && replyAsBytes[0] != 0)
/*      */       {
/* 3762 */         if (replyAsBytes[0] != 42) {
/*      */           
/*      */           try {
/* 3765 */             byte[] buff = Security.passwordHashStage1(password);
/*      */ 
/*      */             
/* 3768 */             byte[] passwordHash = new byte[buff.length];
/* 3769 */             System.arraycopy(buff, 0, passwordHash, 0, buff.length);
/*      */ 
/*      */             
/* 3772 */             passwordHash = Security.passwordHashStage2(passwordHash, replyAsBytes);
/*      */ 
/*      */             
/* 3775 */             byte[] packetDataAfterSalt = new byte[replyAsBytes.length - 5];
/*      */ 
/*      */             
/* 3778 */             System.arraycopy(replyAsBytes, 4, packetDataAfterSalt, 0, replyAsBytes.length - 5);
/*      */ 
/*      */             
/* 3781 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/*      */             
/* 3784 */             Security.passwordCrypt(packetDataAfterSalt, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/*      */ 
/*      */             
/* 3788 */             Security.passwordCrypt(mysqlScrambleBuff, buff, buff, 20);
/*      */             
/* 3790 */             Buffer packet2 = new Buffer(25);
/* 3791 */             packet2.writeBytesNoNull(buff);
/*      */             
/* 3793 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */             
/* 3795 */             send(packet2, 24);
/* 3796 */           } catch (NoSuchAlgorithmException nse) {
/* 3797 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000");
/*      */           } 
/*      */         } else {
/*      */ 
/*      */           
/*      */           try {
/*      */             
/* 3804 */             byte[] passwordHash = Security.createKeyFromOldPassword(password);
/*      */ 
/*      */             
/* 3807 */             byte[] netReadPos4 = new byte[replyAsBytes.length - 5];
/*      */             
/* 3809 */             System.arraycopy(replyAsBytes, 4, netReadPos4, 0, replyAsBytes.length - 5);
/*      */ 
/*      */             
/* 3812 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/*      */             
/* 3815 */             Security.passwordCrypt(netReadPos4, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/*      */ 
/*      */             
/* 3819 */             String scrambledPassword = Util.scramble(new String(mysqlScrambleBuff), password);
/*      */ 
/*      */             
/* 3822 */             Buffer packet2 = new Buffer(packLength);
/* 3823 */             packet2.writeString(scrambledPassword, "Cp1252", this.connection);
/* 3824 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */             
/* 3826 */             send(packet2, 24);
/* 3827 */           } catch (NoSuchAlgorithmException nse) {
/* 3828 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.93") + Messages.getString("MysqlIO.94"), "S1000");
/*      */           } 
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
/*      */   void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams) throws SQLException {
/* 3870 */     if (packet == null) {
/* 3871 */       packet = new Buffer(packLength);
/*      */     }
/*      */     
/* 3874 */     if (writeClientParams) {
/* 3875 */       if (this.use41Extensions) {
/* 3876 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 3877 */           packet.writeLong(this.clientParam);
/* 3878 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3883 */           packet.writeByte((byte)33);
/*      */ 
/*      */           
/* 3886 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 3888 */           packet.writeLong(this.clientParam);
/* 3889 */           packet.writeLong(this.maxThreeBytes);
/*      */         } 
/*      */       } else {
/* 3892 */         packet.writeInt((int)this.clientParam);
/* 3893 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3898 */     packet.writeString(user, "utf-8", this.connection);
/*      */     
/* 3900 */     if (password.length() != 0) {
/* 3901 */       packet.writeByte((byte)20);
/*      */       
/*      */       try {
/* 3904 */         packet.writeBytesNoNull(Security.scramble411(password, this.seed));
/* 3905 */       } catch (NoSuchAlgorithmException nse) {
/* 3906 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000");
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 3912 */       packet.writeByte((byte)0);
/*      */     } 
/*      */     
/* 3915 */     if (this.useConnectWithDb) {
/* 3916 */       packet.writeString(database, "utf-8", this.connection);
/*      */     }
/*      */     
/* 3919 */     send(packet, packet.getPosition());
/*      */     
/* 3921 */     byte savePacketSequence = this.packetSequence = (byte)(this.packetSequence + 1);
/*      */     
/* 3923 */     Buffer reply = checkErrorPacket();
/*      */     
/* 3925 */     if (reply.isLastDataPacket()) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3930 */       this.packetSequence = savePacketSequence = (byte)(savePacketSequence + 1);
/* 3931 */       packet.clear();
/*      */       
/* 3933 */       String seed323 = this.seed.substring(0, 8);
/* 3934 */       packet.writeString(Util.newCrypt(password, seed323));
/* 3935 */       send(packet, packet.getPosition());
/*      */ 
/*      */       
/* 3938 */       checkErrorPacket();
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
/*      */   private final ResultSetRow unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency) throws SQLException {
/* 3955 */     int numFields = fields.length;
/*      */     
/* 3957 */     byte[][] unpackedRowData = new byte[numFields][];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3964 */     int nullCount = (numFields + 9) / 8;
/*      */     
/* 3966 */     byte[] nullBitMask = new byte[nullCount];
/*      */     
/* 3968 */     for (int i = 0; i < nullCount; i++) {
/* 3969 */       nullBitMask[i] = binaryData.readByte();
/*      */     }
/*      */     
/* 3972 */     int nullMaskPos = 0;
/* 3973 */     int bit = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3980 */     for (int j = 0; j < numFields; j++) {
/* 3981 */       if ((nullBitMask[nullMaskPos] & bit) != 0) {
/* 3982 */         unpackedRowData[j] = null;
/*      */       }
/* 3984 */       else if (resultSetConcurrency != 1008) {
/* 3985 */         extractNativeEncodedColumn(binaryData, fields, j, unpackedRowData);
/*      */       } else {
/*      */         
/* 3988 */         unpackNativeEncodedColumn(binaryData, fields, j, unpackedRowData);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 3993 */       if (((bit <<= 1) & 0xFF) == 0) {
/* 3994 */         bit = 1;
/*      */         
/* 3996 */         nullMaskPos++;
/*      */       } 
/*      */     } 
/*      */     
/* 4000 */     return new ByteArrayRow(unpackedRowData);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException {
/*      */     int length;
/* 4006 */     Field curField = fields[columnIndex];
/*      */     
/* 4008 */     switch (curField.getMysqlType()) {
/*      */       case 6:
/*      */         return;
/*      */ 
/*      */       
/*      */       case 1:
/* 4014 */         (new byte[1])[0] = binaryData.readByte(); unpackedRowData[columnIndex] = new byte[1];
/*      */ 
/*      */ 
/*      */       
/*      */       case 2:
/*      */       case 13:
/* 4020 */         unpackedRowData[columnIndex] = binaryData.getBytes(2);
/*      */ 
/*      */       
/*      */       case 3:
/*      */       case 9:
/* 4025 */         unpackedRowData[columnIndex] = binaryData.getBytes(4);
/*      */ 
/*      */       
/*      */       case 8:
/* 4029 */         unpackedRowData[columnIndex] = binaryData.getBytes(8);
/*      */ 
/*      */       
/*      */       case 4:
/* 4033 */         unpackedRowData[columnIndex] = binaryData.getBytes(4);
/*      */ 
/*      */       
/*      */       case 5:
/* 4037 */         unpackedRowData[columnIndex] = binaryData.getBytes(8);
/*      */ 
/*      */       
/*      */       case 11:
/* 4041 */         length = (int)binaryData.readFieldLength();
/*      */         
/* 4043 */         unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/*      */ 
/*      */       
/*      */       case 10:
/* 4048 */         length = (int)binaryData.readFieldLength();
/*      */         
/* 4050 */         unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/*      */       
/*      */       case 7:
/*      */       case 12:
/* 4055 */         length = (int)binaryData.readFieldLength();
/*      */         
/* 4057 */         unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */       
/*      */       case 0:
/*      */       case 15:
/*      */       case 246:
/*      */       case 249:
/*      */       case 250:
/*      */       case 251:
/*      */       case 252:
/*      */       case 253:
/*      */       case 254:
/*      */       case 255:
/* 4069 */         unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/*      */       
/*      */       case 16:
/* 4073 */         unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */     } 
/*      */ 
/*      */     
/* 4077 */     throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000"); } private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException { byte tinyVal; short shortVal; int intVal;
/*      */     long longVal;
/*      */     float floatVal;
/*      */     double doubleVal;
/*      */     int length, hour, minute, seconds;
/*      */     byte[] timeAsBytes;
/*      */     int year, month, day;
/*      */     byte[] arrayOfByte1;
/*      */     int i, j, nanos, k;
/*      */     byte[] arrayOfByte2, arrayOfByte3;
/*      */     byte b;
/*      */     boolean bool;
/* 4089 */     Field curField = fields[columnIndex];
/*      */     
/* 4091 */     switch (curField.getMysqlType()) {
/*      */       case 6:
/*      */         return;
/*      */ 
/*      */       
/*      */       case 1:
/* 4097 */         tinyVal = binaryData.readByte();
/*      */         
/* 4099 */         if (!curField.isUnsigned()) {
/* 4100 */           unpackedRowData[columnIndex] = String.valueOf(tinyVal).getBytes();
/*      */         } else {
/*      */           
/* 4103 */           short unsignedTinyVal = (short)(tinyVal & 0xFF);
/*      */           
/* 4105 */           unpackedRowData[columnIndex] = String.valueOf(unsignedTinyVal).getBytes();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 2:
/*      */       case 13:
/* 4114 */         shortVal = (short)binaryData.readInt();
/*      */         
/* 4116 */         if (!curField.isUnsigned()) {
/* 4117 */           unpackedRowData[columnIndex] = String.valueOf(shortVal).getBytes();
/*      */         } else {
/*      */           
/* 4120 */           int unsignedShortVal = shortVal & 0xFFFF;
/*      */           
/* 4122 */           unpackedRowData[columnIndex] = String.valueOf(unsignedShortVal).getBytes();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 3:
/*      */       case 9:
/* 4131 */         intVal = (int)binaryData.readLong();
/*      */         
/* 4133 */         if (!curField.isUnsigned()) {
/* 4134 */           unpackedRowData[columnIndex] = String.valueOf(intVal).getBytes();
/*      */         } else {
/*      */           
/* 4137 */           long l = intVal & 0xFFFFFFFFL;
/*      */           
/* 4139 */           unpackedRowData[columnIndex] = String.valueOf(l).getBytes();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 8:
/* 4147 */         longVal = binaryData.readLongLong();
/*      */         
/* 4149 */         if (!curField.isUnsigned()) {
/* 4150 */           unpackedRowData[columnIndex] = String.valueOf(longVal).getBytes();
/*      */         } else {
/*      */           
/* 4153 */           BigInteger asBigInteger = ResultSetImpl.convertLongToUlong(longVal);
/*      */           
/* 4155 */           unpackedRowData[columnIndex] = asBigInteger.toString().getBytes();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 4:
/* 4163 */         floatVal = Float.intBitsToFloat(binaryData.readIntAsLong());
/*      */         
/* 4165 */         unpackedRowData[columnIndex] = String.valueOf(floatVal).getBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 5:
/* 4171 */         doubleVal = Double.longBitsToDouble(binaryData.readLongLong());
/*      */         
/* 4173 */         unpackedRowData[columnIndex] = String.valueOf(doubleVal).getBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 11:
/* 4179 */         length = (int)binaryData.readFieldLength();
/*      */         
/* 4181 */         hour = 0;
/* 4182 */         minute = 0;
/* 4183 */         seconds = 0;
/*      */         
/* 4185 */         if (length != 0) {
/* 4186 */           binaryData.readByte();
/* 4187 */           binaryData.readLong();
/* 4188 */           hour = binaryData.readByte();
/* 4189 */           minute = binaryData.readByte();
/* 4190 */           seconds = binaryData.readByte();
/*      */           
/* 4192 */           if (length > 8) {
/* 4193 */             binaryData.readLong();
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/* 4198 */         timeAsBytes = new byte[8];
/*      */         
/* 4200 */         timeAsBytes[0] = (byte)Character.forDigit(hour / 10, 10);
/* 4201 */         timeAsBytes[1] = (byte)Character.forDigit(hour % 10, 10);
/*      */         
/* 4203 */         timeAsBytes[2] = 58;
/*      */         
/* 4205 */         timeAsBytes[3] = (byte)Character.forDigit(minute / 10, 10);
/*      */         
/* 4207 */         timeAsBytes[4] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/*      */         
/* 4210 */         timeAsBytes[5] = 58;
/*      */         
/* 4212 */         timeAsBytes[6] = (byte)Character.forDigit(seconds / 10, 10);
/*      */         
/* 4214 */         timeAsBytes[7] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/*      */         
/* 4217 */         unpackedRowData[columnIndex] = timeAsBytes;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 10:
/* 4223 */         length = (int)binaryData.readFieldLength();
/*      */         
/* 4225 */         year = 0;
/* 4226 */         month = 0;
/* 4227 */         day = 0;
/*      */         
/* 4229 */         hour = 0;
/* 4230 */         minute = 0;
/* 4231 */         seconds = 0;
/*      */         
/* 4233 */         if (length != 0) {
/* 4234 */           year = binaryData.readInt();
/* 4235 */           month = binaryData.readByte();
/* 4236 */           day = binaryData.readByte();
/*      */         } 
/*      */         
/* 4239 */         if (year == 0 && month == 0 && day == 0)
/* 4240 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           
/* 4242 */           { unpackedRowData[columnIndex] = null; }
/*      */           else
/*      */           
/* 4245 */           { if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */             {
/* 4247 */               throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */             }
/*      */ 
/*      */             
/* 4251 */             year = 1;
/* 4252 */             month = 1;
/* 4253 */             day = 1;
/*      */ 
/*      */ 
/*      */             
/* 4257 */             byte[] dateAsBytes = new byte[10];
/*      */             
/* 4259 */             dateAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/*      */             
/* 4262 */             int after1000 = year % 1000;
/*      */             
/* 4264 */             dateAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/*      */             
/* 4267 */             int after100 = after1000 % 100;
/*      */             
/* 4269 */             dateAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */             
/* 4271 */             dateAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/*      */             
/* 4274 */             dateAsBytes[4] = 45;
/*      */             
/* 4276 */             dateAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */             
/* 4278 */             dateAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/*      */             
/* 4281 */             dateAsBytes[7] = 45;
/*      */             
/* 4283 */             dateAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/* 4284 */             dateAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */             
/* 4286 */             unpackedRowData[columnIndex] = dateAsBytes; }   arrayOfByte1 = new byte[10]; arrayOfByte1[0] = (byte)Character.forDigit(year / 1000, 10); i = year % 1000; arrayOfByte1[1] = (byte)Character.forDigit(i / 100, 10); j = i % 100; arrayOfByte1[2] = (byte)Character.forDigit(j / 10, 10); arrayOfByte1[3] = (byte)Character.forDigit(j % 10, 10); arrayOfByte1[4] = 45; arrayOfByte1[5] = (byte)Character.forDigit(month / 10, 10); arrayOfByte1[6] = (byte)Character.forDigit(month % 10, 10); arrayOfByte1[7] = 45; arrayOfByte1[8] = (byte)Character.forDigit(day / 10, 10); arrayOfByte1[9] = (byte)Character.forDigit(day % 10, 10); unpackedRowData[columnIndex] = arrayOfByte1;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 7:
/*      */       case 12:
/* 4293 */         length = (int)binaryData.readFieldLength();
/*      */         
/* 4295 */         year = 0;
/* 4296 */         month = 0;
/* 4297 */         day = 0;
/*      */         
/* 4299 */         hour = 0;
/* 4300 */         minute = 0;
/* 4301 */         seconds = 0;
/*      */         
/* 4303 */         nanos = 0;
/*      */         
/* 4305 */         if (length != 0) {
/* 4306 */           year = binaryData.readInt();
/* 4307 */           month = binaryData.readByte();
/* 4308 */           day = binaryData.readByte();
/*      */           
/* 4310 */           if (length > 4) {
/* 4311 */             hour = binaryData.readByte();
/* 4312 */             minute = binaryData.readByte();
/* 4313 */             seconds = binaryData.readByte();
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4321 */         if (year == 0 && month == 0 && day == 0)
/* 4322 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           
/* 4324 */           { unpackedRowData[columnIndex] = null; }
/*      */           else
/*      */           
/* 4327 */           { if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */             {
/* 4329 */               throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */             }
/*      */ 
/*      */             
/* 4333 */             year = 1;
/* 4334 */             month = 1;
/* 4335 */             day = 1;
/*      */ 
/*      */ 
/*      */             
/* 4339 */             int stringLength = 19;
/*      */             
/* 4341 */             byte[] nanosAsBytes = Integer.toString(nanos).getBytes();
/*      */             
/* 4343 */             stringLength += 1 + nanosAsBytes.length;
/*      */             
/* 4345 */             byte[] datetimeAsBytes = new byte[stringLength];
/*      */             
/* 4347 */             datetimeAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/*      */             
/* 4350 */             int after1000 = year % 1000;
/*      */             
/* 4352 */             datetimeAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/*      */             
/* 4355 */             int after100 = after1000 % 100;
/*      */             
/* 4357 */             datetimeAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */             
/* 4359 */             datetimeAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/*      */             
/* 4362 */             datetimeAsBytes[4] = 45;
/*      */             
/* 4364 */             datetimeAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */             
/* 4366 */             datetimeAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/*      */             
/* 4369 */             datetimeAsBytes[7] = 45;
/*      */             
/* 4371 */             datetimeAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/*      */             
/* 4373 */             datetimeAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/*      */             
/* 4376 */             datetimeAsBytes[10] = 32;
/*      */             
/* 4378 */             datetimeAsBytes[11] = (byte)Character.forDigit(hour / 10, 10);
/*      */             
/* 4380 */             datetimeAsBytes[12] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/*      */             
/* 4383 */             datetimeAsBytes[13] = 58;
/*      */             
/* 4385 */             datetimeAsBytes[14] = (byte)Character.forDigit(minute / 10, 10);
/*      */             
/* 4387 */             datetimeAsBytes[15] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/*      */             
/* 4390 */             datetimeAsBytes[16] = 58;
/*      */             
/* 4392 */             datetimeAsBytes[17] = (byte)Character.forDigit(seconds / 10, 10);
/*      */             
/* 4394 */             datetimeAsBytes[18] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/*      */             
/* 4397 */             datetimeAsBytes[19] = 46;
/*      */             
/* 4399 */             int nanosOffset = 20;
/*      */             
/* 4401 */             int m = 0; }   k = 19; arrayOfByte2 = Integer.toString(nanos).getBytes(); k += 1 + arrayOfByte2.length; arrayOfByte3 = new byte[k]; arrayOfByte3[0] = (byte)Character.forDigit(year / 1000, 10); i = year % 1000; arrayOfByte3[1] = (byte)Character.forDigit(i / 100, 10); j = i % 100; arrayOfByte3[2] = (byte)Character.forDigit(j / 10, 10); arrayOfByte3[3] = (byte)Character.forDigit(j % 10, 10); arrayOfByte3[4] = 45; arrayOfByte3[5] = (byte)Character.forDigit(month / 10, 10); arrayOfByte3[6] = (byte)Character.forDigit(month % 10, 10); arrayOfByte3[7] = 45; arrayOfByte3[8] = (byte)Character.forDigit(day / 10, 10); arrayOfByte3[9] = (byte)Character.forDigit(day % 10, 10); arrayOfByte3[10] = 32; arrayOfByte3[11] = (byte)Character.forDigit(hour / 10, 10); arrayOfByte3[12] = (byte)Character.forDigit(hour % 10, 10); arrayOfByte3[13] = 58; arrayOfByte3[14] = (byte)Character.forDigit(minute / 10, 10); arrayOfByte3[15] = (byte)Character.forDigit(minute % 10, 10); arrayOfByte3[16] = 58; arrayOfByte3[17] = (byte)Character.forDigit(seconds / 10, 10); arrayOfByte3[18] = (byte)Character.forDigit(seconds % 10, 10); arrayOfByte3[19] = 46; b = 20; bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 0:
/*      */       case 15:
/*      */       case 16:
/*      */       case 246:
/*      */       case 249:
/*      */       case 250:
/*      */       case 251:
/*      */       case 252:
/*      */       case 253:
/*      */       case 254:
/* 4420 */         unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 4425 */     throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000"); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void negotiateSSLConnection(String user, String password, String database, int packLength) throws SQLException {
/* 4448 */     if (!ExportControlled.enabled()) {
/* 4449 */       throw new ConnectionFeatureNotAvailableException(this.connection, this.lastPacketSentTimeMs, null);
/*      */     }
/*      */ 
/*      */     
/* 4453 */     boolean doSecureAuth = false;
/*      */     
/* 4455 */     if ((this.serverCapabilities & 0x8000) != 0) {
/* 4456 */       this.clientParam |= 0x8000L;
/* 4457 */       doSecureAuth = true;
/*      */     } 
/*      */     
/* 4460 */     this.clientParam |= 0x800L;
/*      */     
/* 4462 */     Buffer packet = new Buffer(packLength);
/*      */     
/* 4464 */     if (this.use41Extensions) {
/* 4465 */       packet.writeLong(this.clientParam);
/*      */     } else {
/* 4467 */       packet.writeInt((int)this.clientParam);
/*      */     } 
/*      */     
/* 4470 */     send(packet, packet.getPosition());
/*      */     
/* 4472 */     ExportControlled.transformSocketToSSLSocket(this);
/*      */     
/* 4474 */     packet.clear();
/*      */     
/* 4476 */     if (doSecureAuth) {
/* 4477 */       if (versionMeetsMinimum(4, 1, 1)) {
/* 4478 */         secureAuth411(null, packLength, user, password, database, true);
/*      */       } else {
/* 4480 */         secureAuth411(null, packLength, user, password, database, true);
/*      */       } 
/*      */     } else {
/* 4483 */       if (this.use41Extensions) {
/* 4484 */         packet.writeLong(this.clientParam);
/* 4485 */         packet.writeLong(this.maxThreeBytes);
/*      */       } else {
/* 4487 */         packet.writeInt((int)this.clientParam);
/* 4488 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       } 
/*      */ 
/*      */       
/* 4492 */       packet.writeString(user);
/*      */       
/* 4494 */       if (this.protocolVersion > 9) {
/* 4495 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       } else {
/* 4497 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       } 
/*      */       
/* 4500 */       if ((this.serverCapabilities & 0x8) != 0 && database != null && database.length() > 0)
/*      */       {
/* 4502 */         packet.writeString(database);
/*      */       }
/*      */       
/* 4505 */       send(packet, packet.getPosition());
/*      */     } 
/*      */   }
/*      */   
/*      */   protected int getServerStatus() {
/* 4510 */     return this.serverStatus;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected List fetchRowsViaCursor(List fetchedRows, long statementId, Field[] columnTypes, int fetchSize, boolean useBufferRowExplicit) throws SQLException {
/* 4516 */     if (fetchedRows == null) {
/* 4517 */       fetchedRows = new ArrayList(fetchSize);
/*      */     } else {
/* 4519 */       fetchedRows.clear();
/*      */     } 
/*      */     
/* 4522 */     this.sharedSendPacket.clear();
/*      */     
/* 4524 */     this.sharedSendPacket.writeByte((byte)28);
/* 4525 */     this.sharedSendPacket.writeLong(statementId);
/* 4526 */     this.sharedSendPacket.writeLong(fetchSize);
/*      */     
/* 4528 */     sendCommand(28, null, this.sharedSendPacket, true, null);
/*      */ 
/*      */     
/* 4531 */     ResultSetRow row = null;
/*      */ 
/*      */     
/* 4534 */     while ((row = nextRow(columnTypes, columnTypes.length, true, 1007, false, useBufferRowExplicit, false, null)) != null) {
/* 4535 */       fetchedRows.add(row);
/*      */     }
/*      */     
/* 4538 */     return fetchedRows;
/*      */   }
/*      */   
/*      */   protected long getThreadId() {
/* 4542 */     return this.threadId;
/*      */   }
/*      */   
/*      */   protected boolean useNanosForElapsedTime() {
/* 4546 */     return this.useNanosForElapsedTime;
/*      */   }
/*      */   
/*      */   protected long getSlowQueryThreshold() {
/* 4550 */     return this.slowQueryThreshold;
/*      */   }
/*      */   
/*      */   protected String getQueryTimingUnits() {
/* 4554 */     return this.queryTimingUnits;
/*      */   }
/*      */   
/*      */   protected int getCommandCount() {
/* 4558 */     return this.commandCount;
/*      */   }
/*      */   
/*      */   private void checkTransactionState(int oldStatus) throws SQLException {
/* 4562 */     boolean previouslyInTrans = ((oldStatus & 0x1) != 0);
/* 4563 */     boolean currentlyInTrans = ((this.serverStatus & 0x1) != 0);
/*      */     
/* 4565 */     if (previouslyInTrans && !currentlyInTrans) {
/* 4566 */       this.connection.transactionCompleted();
/* 4567 */     } else if (!previouslyInTrans && currentlyInTrans) {
/* 4568 */       this.connection.transactionBegun();
/*      */     } 
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\MysqlIO.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */