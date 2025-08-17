/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParsePosition;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PreparedStatement
/*      */   extends StatementImpl
/*      */   implements PreparedStatement
/*      */ {
/*      */   private static final Constructor JDBC_4_PSTMT_2_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_PSTMT_3_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_PSTMT_4_ARG_CTOR;
/*      */   
/*      */   static {
/*   92 */     if (Util.isJdbc4()) {
/*      */       try {
/*   94 */         JDBC_4_PSTMT_2_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class });
/*      */ 
/*      */ 
/*      */         
/*   98 */         JDBC_4_PSTMT_3_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class });
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  103 */         JDBC_4_PSTMT_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class, ParseInfo.class });
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  108 */       catch (SecurityException e) {
/*  109 */         throw new RuntimeException(e);
/*  110 */       } catch (NoSuchMethodException e) {
/*  111 */         throw new RuntimeException(e);
/*  112 */       } catch (ClassNotFoundException e) {
/*  113 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*  116 */       JDBC_4_PSTMT_2_ARG_CTOR = null;
/*  117 */       JDBC_4_PSTMT_3_ARG_CTOR = null;
/*  118 */       JDBC_4_PSTMT_4_ARG_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   class BatchParams
/*      */   {
/*      */     boolean[] isNull;
/*      */     
/*      */     boolean[] isStream;
/*      */     InputStream[] parameterStreams;
/*      */     byte[][] parameterStrings;
/*      */     int[] streamLengths;
/*      */     private final PreparedStatement this$0;
/*      */     
/*      */     BatchParams(PreparedStatement this$0, byte[][] strings, InputStream[] streams, boolean[] isStreamFlags, int[] lengths, boolean[] isNullFlags) {
/*  134 */       this.this$0 = this$0; this.isNull = null; this.isStream = null;
/*      */       this.parameterStreams = null;
/*      */       this.parameterStrings = (byte[][])null;
/*      */       this.streamLengths = null;
/*  138 */       this.parameterStrings = new byte[strings.length][];
/*  139 */       this.parameterStreams = new InputStream[streams.length];
/*  140 */       this.isStream = new boolean[isStreamFlags.length];
/*  141 */       this.streamLengths = new int[lengths.length];
/*  142 */       this.isNull = new boolean[isNullFlags.length];
/*  143 */       System.arraycopy(strings, 0, this.parameterStrings, 0, strings.length);
/*      */       
/*  145 */       System.arraycopy(streams, 0, this.parameterStreams, 0, streams.length);
/*      */       
/*  147 */       System.arraycopy(isStreamFlags, 0, this.isStream, 0, isStreamFlags.length);
/*      */       
/*  149 */       System.arraycopy(lengths, 0, this.streamLengths, 0, lengths.length);
/*  150 */       System.arraycopy(isNullFlags, 0, this.isNull, 0, isNullFlags.length);
/*      */     }
/*      */   }
/*      */   
/*      */   class EndPoint
/*      */   {
/*      */     int begin;
/*      */     int end;
/*      */     private final PreparedStatement this$0;
/*      */     
/*      */     EndPoint(PreparedStatement this$0, int b, int e) {
/*  161 */       this.this$0 = this$0;
/*  162 */       this.begin = b;
/*  163 */       this.end = e;
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
/*      */   class ParseInfo
/*      */   {
/*      */     char firstStmtChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean foundLimitClause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean foundLoadData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long lastUsed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int statementLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int statementStartPos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     byte[][] staticSql;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final PreparedStatement this$0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ParseInfo(PreparedStatement this$0, String sql, ConnectionImpl conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter) throws SQLException {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: invokespecial <init> : ()V
/*      */       //   4: aload_0
/*      */       //   5: aload_1
/*      */       //   6: putfield this$0 : Lcom/mysql/jdbc/PreparedStatement;
/*      */       //   9: aload_0
/*      */       //   10: iconst_0
/*      */       //   11: putfield firstStmtChar : C
/*      */       //   14: aload_0
/*      */       //   15: iconst_0
/*      */       //   16: putfield foundLimitClause : Z
/*      */       //   19: aload_0
/*      */       //   20: iconst_0
/*      */       //   21: putfield foundLoadData : Z
/*      */       //   24: aload_0
/*      */       //   25: lconst_0
/*      */       //   26: putfield lastUsed : J
/*      */       //   29: aload_0
/*      */       //   30: iconst_0
/*      */       //   31: putfield statementLength : I
/*      */       //   34: aload_0
/*      */       //   35: iconst_0
/*      */       //   36: putfield statementStartPos : I
/*      */       //   39: aload_0
/*      */       //   40: aconst_null
/*      */       //   41: checkcast [[B
/*      */       //   44: putfield staticSql : [[B
/*      */       //   47: aload_2
/*      */       //   48: ifnonnull -> 62
/*      */       //   51: ldc 'PreparedStatement.61'
/*      */       //   53: invokestatic getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */       //   56: ldc 'S1009'
/*      */       //   58: invokestatic createSQLException : (Ljava/lang/String;Ljava/lang/String;)Ljava/sql/SQLException;
/*      */       //   61: athrow
/*      */       //   62: aload_0
/*      */       //   63: invokestatic currentTimeMillis : ()J
/*      */       //   66: putfield lastUsed : J
/*      */       //   69: aload #4
/*      */       //   71: invokeinterface getIdentifierQuoteString : ()Ljava/lang/String;
/*      */       //   76: astore #7
/*      */       //   78: iconst_0
/*      */       //   79: istore #8
/*      */       //   81: aload #7
/*      */       //   83: ifnull -> 112
/*      */       //   86: aload #7
/*      */       //   88: ldc ' '
/*      */       //   90: invokevirtual equals : (Ljava/lang/Object;)Z
/*      */       //   93: ifne -> 112
/*      */       //   96: aload #7
/*      */       //   98: invokevirtual length : ()I
/*      */       //   101: ifle -> 112
/*      */       //   104: aload #7
/*      */       //   106: iconst_0
/*      */       //   107: invokevirtual charAt : (I)C
/*      */       //   110: istore #8
/*      */       //   112: aload_0
/*      */       //   113: aload_2
/*      */       //   114: invokevirtual length : ()I
/*      */       //   117: putfield statementLength : I
/*      */       //   120: new java/util/ArrayList
/*      */       //   123: dup
/*      */       //   124: invokespecial <init> : ()V
/*      */       //   127: astore #9
/*      */       //   129: iconst_0
/*      */       //   130: istore #10
/*      */       //   132: iconst_0
/*      */       //   133: istore #11
/*      */       //   135: iconst_0
/*      */       //   136: istore #12
/*      */       //   138: iconst_0
/*      */       //   139: istore #13
/*      */       //   141: aload_0
/*      */       //   142: getfield statementLength : I
/*      */       //   145: iconst_5
/*      */       //   146: isub
/*      */       //   147: istore #15
/*      */       //   149: aload_0
/*      */       //   150: iconst_0
/*      */       //   151: putfield foundLimitClause : Z
/*      */       //   154: aload_1
/*      */       //   155: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */       //   158: invokevirtual isNoBackslashEscapesSet : ()Z
/*      */       //   161: istore #16
/*      */       //   163: aload_0
/*      */       //   164: aload_1
/*      */       //   165: aload_2
/*      */       //   166: invokevirtual findStartOfStatement : (Ljava/lang/String;)I
/*      */       //   169: putfield statementStartPos : I
/*      */       //   172: aload_0
/*      */       //   173: getfield statementStartPos : I
/*      */       //   176: istore #14
/*      */       //   178: iload #14
/*      */       //   180: aload_0
/*      */       //   181: getfield statementLength : I
/*      */       //   184: if_icmpge -> 803
/*      */       //   187: aload_2
/*      */       //   188: iload #14
/*      */       //   190: invokevirtual charAt : (I)C
/*      */       //   193: istore #17
/*      */       //   195: aload_0
/*      */       //   196: getfield firstStmtChar : C
/*      */       //   199: ifne -> 219
/*      */       //   202: iload #17
/*      */       //   204: invokestatic isLetter : (C)Z
/*      */       //   207: ifeq -> 219
/*      */       //   210: aload_0
/*      */       //   211: iload #17
/*      */       //   213: invokestatic toUpperCase : (C)C
/*      */       //   216: putfield firstStmtChar : C
/*      */       //   219: iload #16
/*      */       //   221: ifne -> 248
/*      */       //   224: iload #17
/*      */       //   226: bipush #92
/*      */       //   228: if_icmpne -> 248
/*      */       //   231: iload #14
/*      */       //   233: aload_0
/*      */       //   234: getfield statementLength : I
/*      */       //   237: iconst_1
/*      */       //   238: isub
/*      */       //   239: if_icmpge -> 248
/*      */       //   242: iinc #14, 1
/*      */       //   245: goto -> 797
/*      */       //   248: iload #10
/*      */       //   250: ifne -> 280
/*      */       //   253: iload #8
/*      */       //   255: ifeq -> 280
/*      */       //   258: iload #17
/*      */       //   260: iload #8
/*      */       //   262: if_icmpne -> 280
/*      */       //   265: iload #12
/*      */       //   267: ifne -> 274
/*      */       //   270: iconst_1
/*      */       //   271: goto -> 275
/*      */       //   274: iconst_0
/*      */       //   275: istore #12
/*      */       //   277: goto -> 628
/*      */       //   280: iload #12
/*      */       //   282: ifne -> 628
/*      */       //   285: iload #10
/*      */       //   287: ifeq -> 398
/*      */       //   290: iload #17
/*      */       //   292: bipush #39
/*      */       //   294: if_icmpeq -> 304
/*      */       //   297: iload #17
/*      */       //   299: bipush #34
/*      */       //   301: if_icmpne -> 359
/*      */       //   304: iload #17
/*      */       //   306: iload #11
/*      */       //   308: if_icmpne -> 359
/*      */       //   311: iload #14
/*      */       //   313: aload_0
/*      */       //   314: getfield statementLength : I
/*      */       //   317: iconst_1
/*      */       //   318: isub
/*      */       //   319: if_icmpge -> 341
/*      */       //   322: aload_2
/*      */       //   323: iload #14
/*      */       //   325: iconst_1
/*      */       //   326: iadd
/*      */       //   327: invokevirtual charAt : (I)C
/*      */       //   330: iload #11
/*      */       //   332: if_icmpne -> 341
/*      */       //   335: iinc #14, 1
/*      */       //   338: goto -> 797
/*      */       //   341: iload #10
/*      */       //   343: ifne -> 350
/*      */       //   346: iconst_1
/*      */       //   347: goto -> 351
/*      */       //   350: iconst_0
/*      */       //   351: istore #10
/*      */       //   353: iconst_0
/*      */       //   354: istore #11
/*      */       //   356: goto -> 628
/*      */       //   359: iload #17
/*      */       //   361: bipush #39
/*      */       //   363: if_icmpeq -> 373
/*      */       //   366: iload #17
/*      */       //   368: bipush #34
/*      */       //   370: if_icmpne -> 628
/*      */       //   373: iload #17
/*      */       //   375: iload #11
/*      */       //   377: if_icmpne -> 628
/*      */       //   380: iload #10
/*      */       //   382: ifne -> 389
/*      */       //   385: iconst_1
/*      */       //   386: goto -> 390
/*      */       //   389: iconst_0
/*      */       //   390: istore #10
/*      */       //   392: iconst_0
/*      */       //   393: istore #11
/*      */       //   395: goto -> 628
/*      */       //   398: iload #17
/*      */       //   400: bipush #35
/*      */       //   402: if_icmpeq -> 436
/*      */       //   405: iload #17
/*      */       //   407: bipush #45
/*      */       //   409: if_icmpne -> 482
/*      */       //   412: iload #14
/*      */       //   414: iconst_1
/*      */       //   415: iadd
/*      */       //   416: aload_0
/*      */       //   417: getfield statementLength : I
/*      */       //   420: if_icmpge -> 482
/*      */       //   423: aload_2
/*      */       //   424: iload #14
/*      */       //   426: iconst_1
/*      */       //   427: iadd
/*      */       //   428: invokevirtual charAt : (I)C
/*      */       //   431: bipush #45
/*      */       //   433: if_icmpne -> 482
/*      */       //   436: aload_0
/*      */       //   437: getfield statementLength : I
/*      */       //   440: iconst_1
/*      */       //   441: isub
/*      */       //   442: istore #18
/*      */       //   444: iload #14
/*      */       //   446: iload #18
/*      */       //   448: if_icmpge -> 797
/*      */       //   451: aload_2
/*      */       //   452: iload #14
/*      */       //   454: invokevirtual charAt : (I)C
/*      */       //   457: istore #17
/*      */       //   459: iload #17
/*      */       //   461: bipush #13
/*      */       //   463: if_icmpeq -> 797
/*      */       //   466: iload #17
/*      */       //   468: bipush #10
/*      */       //   470: if_icmpne -> 476
/*      */       //   473: goto -> 797
/*      */       //   476: iinc #14, 1
/*      */       //   479: goto -> 444
/*      */       //   482: iload #17
/*      */       //   484: bipush #47
/*      */       //   486: if_icmpne -> 607
/*      */       //   489: iload #14
/*      */       //   491: iconst_1
/*      */       //   492: iadd
/*      */       //   493: aload_0
/*      */       //   494: getfield statementLength : I
/*      */       //   497: if_icmpge -> 607
/*      */       //   500: aload_2
/*      */       //   501: iload #14
/*      */       //   503: iconst_1
/*      */       //   504: iadd
/*      */       //   505: invokevirtual charAt : (I)C
/*      */       //   508: istore #18
/*      */       //   510: iload #18
/*      */       //   512: bipush #42
/*      */       //   514: if_icmpne -> 604
/*      */       //   517: iinc #14, 2
/*      */       //   520: iload #14
/*      */       //   522: istore #19
/*      */       //   524: iload #19
/*      */       //   526: aload_0
/*      */       //   527: getfield statementLength : I
/*      */       //   530: if_icmpge -> 604
/*      */       //   533: iinc #14, 1
/*      */       //   536: aload_2
/*      */       //   537: iload #19
/*      */       //   539: invokevirtual charAt : (I)C
/*      */       //   542: istore #18
/*      */       //   544: iload #18
/*      */       //   546: bipush #42
/*      */       //   548: if_icmpne -> 598
/*      */       //   551: iload #19
/*      */       //   553: iconst_1
/*      */       //   554: iadd
/*      */       //   555: aload_0
/*      */       //   556: getfield statementLength : I
/*      */       //   559: if_icmpge -> 598
/*      */       //   562: aload_2
/*      */       //   563: iload #19
/*      */       //   565: iconst_1
/*      */       //   566: iadd
/*      */       //   567: invokevirtual charAt : (I)C
/*      */       //   570: bipush #47
/*      */       //   572: if_icmpne -> 598
/*      */       //   575: iinc #14, 1
/*      */       //   578: iload #14
/*      */       //   580: aload_0
/*      */       //   581: getfield statementLength : I
/*      */       //   584: if_icmpge -> 604
/*      */       //   587: aload_2
/*      */       //   588: iload #14
/*      */       //   590: invokevirtual charAt : (I)C
/*      */       //   593: istore #17
/*      */       //   595: goto -> 604
/*      */       //   598: iinc #19, 1
/*      */       //   601: goto -> 524
/*      */       //   604: goto -> 628
/*      */       //   607: iload #17
/*      */       //   609: bipush #39
/*      */       //   611: if_icmpeq -> 621
/*      */       //   614: iload #17
/*      */       //   616: bipush #34
/*      */       //   618: if_icmpne -> 628
/*      */       //   621: iconst_1
/*      */       //   622: istore #10
/*      */       //   624: iload #17
/*      */       //   626: istore #11
/*      */       //   628: iload #17
/*      */       //   630: bipush #63
/*      */       //   632: if_icmpne -> 670
/*      */       //   635: iload #10
/*      */       //   637: ifne -> 670
/*      */       //   640: iload #12
/*      */       //   642: ifne -> 670
/*      */       //   645: aload #9
/*      */       //   647: iconst_2
/*      */       //   648: newarray int
/*      */       //   650: dup
/*      */       //   651: iconst_0
/*      */       //   652: iload #13
/*      */       //   654: iastore
/*      */       //   655: dup
/*      */       //   656: iconst_1
/*      */       //   657: iload #14
/*      */       //   659: iastore
/*      */       //   660: invokevirtual add : (Ljava/lang/Object;)Z
/*      */       //   663: pop
/*      */       //   664: iload #14
/*      */       //   666: iconst_1
/*      */       //   667: iadd
/*      */       //   668: istore #13
/*      */       //   670: iload #10
/*      */       //   672: ifne -> 797
/*      */       //   675: iload #14
/*      */       //   677: iload #15
/*      */       //   679: if_icmpge -> 797
/*      */       //   682: iload #17
/*      */       //   684: bipush #76
/*      */       //   686: if_icmpeq -> 696
/*      */       //   689: iload #17
/*      */       //   691: bipush #108
/*      */       //   693: if_icmpne -> 797
/*      */       //   696: aload_2
/*      */       //   697: iload #14
/*      */       //   699: iconst_1
/*      */       //   700: iadd
/*      */       //   701: invokevirtual charAt : (I)C
/*      */       //   704: istore #18
/*      */       //   706: iload #18
/*      */       //   708: bipush #73
/*      */       //   710: if_icmpeq -> 720
/*      */       //   713: iload #18
/*      */       //   715: bipush #105
/*      */       //   717: if_icmpne -> 797
/*      */       //   720: aload_2
/*      */       //   721: iload #14
/*      */       //   723: iconst_2
/*      */       //   724: iadd
/*      */       //   725: invokevirtual charAt : (I)C
/*      */       //   728: istore #19
/*      */       //   730: iload #19
/*      */       //   732: bipush #77
/*      */       //   734: if_icmpeq -> 744
/*      */       //   737: iload #19
/*      */       //   739: bipush #109
/*      */       //   741: if_icmpne -> 797
/*      */       //   744: aload_2
/*      */       //   745: iload #14
/*      */       //   747: iconst_3
/*      */       //   748: iadd
/*      */       //   749: invokevirtual charAt : (I)C
/*      */       //   752: istore #20
/*      */       //   754: iload #20
/*      */       //   756: bipush #73
/*      */       //   758: if_icmpeq -> 768
/*      */       //   761: iload #20
/*      */       //   763: bipush #105
/*      */       //   765: if_icmpne -> 797
/*      */       //   768: aload_2
/*      */       //   769: iload #14
/*      */       //   771: iconst_4
/*      */       //   772: iadd
/*      */       //   773: invokevirtual charAt : (I)C
/*      */       //   776: istore #21
/*      */       //   778: iload #21
/*      */       //   780: bipush #84
/*      */       //   782: if_icmpeq -> 792
/*      */       //   785: iload #21
/*      */       //   787: bipush #116
/*      */       //   789: if_icmpne -> 797
/*      */       //   792: aload_0
/*      */       //   793: iconst_1
/*      */       //   794: putfield foundLimitClause : Z
/*      */       //   797: iinc #14, 1
/*      */       //   800: goto -> 178
/*      */       //   803: aload_0
/*      */       //   804: getfield firstStmtChar : C
/*      */       //   807: bipush #76
/*      */       //   809: if_icmpne -> 837
/*      */       //   812: aload_2
/*      */       //   813: ldc 'LOAD DATA'
/*      */       //   815: invokestatic startsWithIgnoreCaseAndWs : (Ljava/lang/String;Ljava/lang/String;)Z
/*      */       //   818: ifeq -> 829
/*      */       //   821: aload_0
/*      */       //   822: iconst_1
/*      */       //   823: putfield foundLoadData : Z
/*      */       //   826: goto -> 842
/*      */       //   829: aload_0
/*      */       //   830: iconst_0
/*      */       //   831: putfield foundLoadData : Z
/*      */       //   834: goto -> 842
/*      */       //   837: aload_0
/*      */       //   838: iconst_0
/*      */       //   839: putfield foundLoadData : Z
/*      */       //   842: aload #9
/*      */       //   844: iconst_2
/*      */       //   845: newarray int
/*      */       //   847: dup
/*      */       //   848: iconst_0
/*      */       //   849: iload #13
/*      */       //   851: iastore
/*      */       //   852: dup
/*      */       //   853: iconst_1
/*      */       //   854: aload_0
/*      */       //   855: getfield statementLength : I
/*      */       //   858: iastore
/*      */       //   859: invokevirtual add : (Ljava/lang/Object;)Z
/*      */       //   862: pop
/*      */       //   863: aload_0
/*      */       //   864: aload #9
/*      */       //   866: invokevirtual size : ()I
/*      */       //   869: anewarray [B
/*      */       //   872: putfield staticSql : [[B
/*      */       //   875: aload_2
/*      */       //   876: invokevirtual toCharArray : ()[C
/*      */       //   879: astore #17
/*      */       //   881: iconst_0
/*      */       //   882: istore #14
/*      */       //   884: iload #14
/*      */       //   886: aload_0
/*      */       //   887: getfield staticSql : [[B
/*      */       //   890: arraylength
/*      */       //   891: if_icmpge -> 1107
/*      */       //   894: aload #9
/*      */       //   896: iload #14
/*      */       //   898: invokevirtual get : (I)Ljava/lang/Object;
/*      */       //   901: checkcast [I
/*      */       //   904: astore #18
/*      */       //   906: aload #18
/*      */       //   908: iconst_1
/*      */       //   909: iaload
/*      */       //   910: istore #19
/*      */       //   912: aload #18
/*      */       //   914: iconst_0
/*      */       //   915: iaload
/*      */       //   916: istore #20
/*      */       //   918: iload #19
/*      */       //   920: iload #20
/*      */       //   922: isub
/*      */       //   923: istore #21
/*      */       //   925: aload_0
/*      */       //   926: getfield foundLoadData : Z
/*      */       //   929: ifeq -> 962
/*      */       //   932: new java/lang/String
/*      */       //   935: dup
/*      */       //   936: aload #17
/*      */       //   938: iload #20
/*      */       //   940: iload #21
/*      */       //   942: invokespecial <init> : ([CII)V
/*      */       //   945: astore #22
/*      */       //   947: aload_0
/*      */       //   948: getfield staticSql : [[B
/*      */       //   951: iload #14
/*      */       //   953: aload #22
/*      */       //   955: invokevirtual getBytes : ()[B
/*      */       //   958: aastore
/*      */       //   959: goto -> 1101
/*      */       //   962: aload #5
/*      */       //   964: ifnonnull -> 1016
/*      */       //   967: iload #21
/*      */       //   969: newarray byte
/*      */       //   971: astore #22
/*      */       //   973: iconst_0
/*      */       //   974: istore #23
/*      */       //   976: iload #23
/*      */       //   978: iload #21
/*      */       //   980: if_icmpge -> 1004
/*      */       //   983: aload #22
/*      */       //   985: iload #23
/*      */       //   987: aload_2
/*      */       //   988: iload #20
/*      */       //   990: iload #23
/*      */       //   992: iadd
/*      */       //   993: invokevirtual charAt : (I)C
/*      */       //   996: i2b
/*      */       //   997: bastore
/*      */       //   998: iinc #23, 1
/*      */       //   1001: goto -> 976
/*      */       //   1004: aload_0
/*      */       //   1005: getfield staticSql : [[B
/*      */       //   1008: iload #14
/*      */       //   1010: aload #22
/*      */       //   1012: aastore
/*      */       //   1013: goto -> 1101
/*      */       //   1016: aload #6
/*      */       //   1018: ifnull -> 1057
/*      */       //   1021: aload_0
/*      */       //   1022: getfield staticSql : [[B
/*      */       //   1025: iload #14
/*      */       //   1027: aload_2
/*      */       //   1028: aload #6
/*      */       //   1030: aload #5
/*      */       //   1032: aload_1
/*      */       //   1033: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */       //   1036: invokevirtual getServerCharacterEncoding : ()Ljava/lang/String;
/*      */       //   1039: iload #20
/*      */       //   1041: iload #21
/*      */       //   1043: aload_1
/*      */       //   1044: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */       //   1047: invokevirtual parserKnowsUnicode : ()Z
/*      */       //   1050: invokestatic getBytes : (Ljava/lang/String;Lcom/mysql/jdbc/SingleByteCharsetConverter;Ljava/lang/String;Ljava/lang/String;IIZ)[B
/*      */       //   1053: aastore
/*      */       //   1054: goto -> 1101
/*      */       //   1057: new java/lang/String
/*      */       //   1060: dup
/*      */       //   1061: aload #17
/*      */       //   1063: iload #20
/*      */       //   1065: iload #21
/*      */       //   1067: invokespecial <init> : ([CII)V
/*      */       //   1070: astore #22
/*      */       //   1072: aload_0
/*      */       //   1073: getfield staticSql : [[B
/*      */       //   1076: iload #14
/*      */       //   1078: aload #22
/*      */       //   1080: aload #5
/*      */       //   1082: aload_1
/*      */       //   1083: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */       //   1086: invokevirtual getServerCharacterEncoding : ()Ljava/lang/String;
/*      */       //   1089: aload_1
/*      */       //   1090: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */       //   1093: invokevirtual parserKnowsUnicode : ()Z
/*      */       //   1096: aload_3
/*      */       //   1097: invokestatic getBytes : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/mysql/jdbc/ConnectionImpl;)[B
/*      */       //   1100: aastore
/*      */       //   1101: iinc #14, 1
/*      */       //   1104: goto -> 884
/*      */       //   1107: goto -> 1151
/*      */       //   1110: astore #7
/*      */       //   1112: new java/sql/SQLException
/*      */       //   1115: dup
/*      */       //   1116: new java/lang/StringBuffer
/*      */       //   1119: dup
/*      */       //   1120: invokespecial <init> : ()V
/*      */       //   1123: ldc 'Parse error for '
/*      */       //   1125: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */       //   1128: aload_2
/*      */       //   1129: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */       //   1132: invokevirtual toString : ()Ljava/lang/String;
/*      */       //   1135: invokespecial <init> : (Ljava/lang/String;)V
/*      */       //   1138: astore #8
/*      */       //   1140: aload #8
/*      */       //   1142: aload #7
/*      */       //   1144: invokevirtual initCause : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*      */       //   1147: pop
/*      */       //   1148: aload #8
/*      */       //   1150: athrow
/*      */       //   1151: return
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #190	-> 0
/*      */       //   #168	-> 9
/*      */       //   #170	-> 14
/*      */       //   #172	-> 19
/*      */       //   #174	-> 24
/*      */       //   #176	-> 29
/*      */       //   #178	-> 34
/*      */       //   #180	-> 39
/*      */       //   #192	-> 47
/*      */       //   #193	-> 51
/*      */       //   #198	-> 62
/*      */       //   #200	-> 69
/*      */       //   #202	-> 78
/*      */       //   #204	-> 81
/*      */       //   #207	-> 104
/*      */       //   #210	-> 112
/*      */       //   #212	-> 120
/*      */       //   #213	-> 129
/*      */       //   #214	-> 132
/*      */       //   #215	-> 135
/*      */       //   #216	-> 138
/*      */       //   #219	-> 141
/*      */       //   #221	-> 149
/*      */       //   #223	-> 154
/*      */       //   #229	-> 163
/*      */       //   #231	-> 172
/*      */       //   #232	-> 187
/*      */       //   #234	-> 195
/*      */       //   #237	-> 210
/*      */       //   #240	-> 219
/*      */       //   #242	-> 242
/*      */       //   #243	-> 245
/*      */       //   #248	-> 248
/*      */       //   #250	-> 265
/*      */       //   #251	-> 280
/*      */       //   #254	-> 285
/*      */       //   #255	-> 290
/*      */       //   #256	-> 311
/*      */       //   #257	-> 335
/*      */       //   #258	-> 338
/*      */       //   #261	-> 341
/*      */       //   #262	-> 353
/*      */       //   #263	-> 359
/*      */       //   #264	-> 380
/*      */       //   #265	-> 392
/*      */       //   #268	-> 398
/*      */       //   #273	-> 436
/*      */       //   #275	-> 444
/*      */       //   #276	-> 451
/*      */       //   #278	-> 459
/*      */       //   #279	-> 473
/*      */       //   #275	-> 476
/*      */       //   #284	-> 482
/*      */       //   #286	-> 500
/*      */       //   #288	-> 510
/*      */       //   #289	-> 517
/*      */       //   #291	-> 520
/*      */       //   #292	-> 533
/*      */       //   #293	-> 536
/*      */       //   #295	-> 544
/*      */       //   #296	-> 562
/*      */       //   #297	-> 575
/*      */       //   #299	-> 578
/*      */       //   #300	-> 587
/*      */       //   #291	-> 598
/*      */       //   #308	-> 607
/*      */       //   #309	-> 621
/*      */       //   #310	-> 624
/*      */       //   #315	-> 628
/*      */       //   #316	-> 645
/*      */       //   #317	-> 664
/*      */       //   #320	-> 670
/*      */       //   #321	-> 682
/*      */       //   #322	-> 696
/*      */       //   #324	-> 706
/*      */       //   #325	-> 720
/*      */       //   #327	-> 730
/*      */       //   #328	-> 744
/*      */       //   #330	-> 754
/*      */       //   #331	-> 768
/*      */       //   #333	-> 778
/*      */       //   #334	-> 792
/*      */       //   #231	-> 797
/*      */       //   #343	-> 803
/*      */       //   #344	-> 812
/*      */       //   #345	-> 821
/*      */       //   #347	-> 829
/*      */       //   #350	-> 837
/*      */       //   #353	-> 842
/*      */       //   #354	-> 863
/*      */       //   #355	-> 875
/*      */       //   #357	-> 881
/*      */       //   #358	-> 894
/*      */       //   #359	-> 906
/*      */       //   #360	-> 912
/*      */       //   #361	-> 918
/*      */       //   #363	-> 925
/*      */       //   #364	-> 932
/*      */       //   #365	-> 947
/*      */       //   #366	-> 962
/*      */       //   #367	-> 967
/*      */       //   #369	-> 973
/*      */       //   #370	-> 983
/*      */       //   #369	-> 998
/*      */       //   #373	-> 1004
/*      */       //   #375	-> 1016
/*      */       //   #376	-> 1021
/*      */       //   #381	-> 1057
/*      */       //   #383	-> 1072
/*      */       //   #357	-> 1101
/*      */       //   #395	-> 1107
/*      */       //   #390	-> 1110
/*      */       //   #391	-> 1112
/*      */       //   #392	-> 1140
/*      */       //   #394	-> 1148
/*      */       //   #396	-> 1151
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   444	38	18	endOfStmt	I
/*      */       //   524	80	19	j	I
/*      */       //   510	94	18	cNext	C
/*      */       //   778	19	21	posT	C
/*      */       //   754	43	20	posI2	C
/*      */       //   730	67	19	posM	C
/*      */       //   706	91	18	posI1	C
/*      */       //   195	602	17	c	C
/*      */       //   947	12	22	temp	Ljava/lang/String;
/*      */       //   976	28	23	j	I
/*      */       //   973	40	22	buf	[B
/*      */       //   1072	29	22	temp	Ljava/lang/String;
/*      */       //   906	195	18	ep	[I
/*      */       //   912	189	19	end	I
/*      */       //   918	183	20	begin	I
/*      */       //   925	176	21	len	I
/*      */       //   78	1029	7	quotedIdentifierString	Ljava/lang/String;
/*      */       //   81	1026	8	quotedIdentifierChar	C
/*      */       //   129	978	9	endpointList	Ljava/util/ArrayList;
/*      */       //   132	975	10	inQuotes	Z
/*      */       //   135	972	11	quoteChar	C
/*      */       //   138	969	12	inQuotedId	Z
/*      */       //   141	966	13	lastParmEnd	I
/*      */       //   178	929	14	i	I
/*      */       //   149	958	15	stopLookingForLimitClause	I
/*      */       //   163	944	16	noBackslashEscapes	Z
/*      */       //   881	226	17	asCharArray	[C
/*      */       //   1140	11	8	sqlEx	Ljava/sql/SQLException;
/*      */       //   1112	39	7	oobEx	Ljava/lang/StringIndexOutOfBoundsException;
/*      */       //   0	1152	0	this	Lcom/mysql/jdbc/PreparedStatement$ParseInfo;
/*      */       //   0	1152	1	this$0	Lcom/mysql/jdbc/PreparedStatement;
/*      */       //   0	1152	2	sql	Ljava/lang/String;
/*      */       //   0	1152	3	conn	Lcom/mysql/jdbc/ConnectionImpl;
/*      */       //   0	1152	4	dbmd	Ljava/sql/DatabaseMetaData;
/*      */       //   0	1152	5	encoding	Ljava/lang/String;
/*      */       //   0	1152	6	converter	Lcom/mysql/jdbc/SingleByteCharsetConverter;
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   47	1107	1110	java/lang/StringIndexOutOfBoundsException
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
/*  399 */   private static final byte[] HEX_DIGITS = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static int readFully(Reader reader, char[] buf, int length) throws IOException {
/*  422 */     int numCharsRead = 0;
/*      */     
/*  424 */     while (numCharsRead < length) {
/*  425 */       int count = reader.read(buf, numCharsRead, length - numCharsRead);
/*      */       
/*  427 */       if (count < 0) {
/*      */         break;
/*      */       }
/*      */       
/*  431 */       numCharsRead += count;
/*      */     } 
/*      */     
/*  434 */     return numCharsRead;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean batchHasPlainStatements = false;
/*      */ 
/*      */ 
/*      */   
/*  445 */   private DatabaseMetaData dbmd = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  451 */   protected char firstCharOfStmt = Character.MIN_VALUE;
/*      */ 
/*      */   
/*      */   protected boolean hasLimitClause = false;
/*      */ 
/*      */   
/*      */   protected boolean isLoadDataQuery = false;
/*      */   
/*  459 */   private boolean[] isNull = null;
/*      */   
/*  461 */   private boolean[] isStream = null;
/*      */   
/*  463 */   protected int numberOfExecutions = 0;
/*      */ 
/*      */   
/*  466 */   protected String originalSql = null;
/*      */ 
/*      */   
/*      */   protected int parameterCount;
/*      */   
/*      */   protected MysqlParameterMetadata parameterMetaData;
/*      */   
/*  473 */   private InputStream[] parameterStreams = null;
/*      */   
/*  475 */   private byte[][] parameterValues = (byte[][])null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  481 */   protected int[] parameterTypes = null;
/*      */   
/*      */   private ParseInfo parseInfo;
/*      */   
/*      */   private ResultSetMetaData pstmtResultMetaData;
/*      */   
/*  487 */   private byte[][] staticSqlStrings = (byte[][])null;
/*      */   
/*  489 */   private byte[] streamConvertBuf = new byte[4096];
/*      */   
/*  491 */   private int[] streamLengths = null;
/*      */   
/*  493 */   private SimpleDateFormat tsdf = null;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean useTrueBoolean = false;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean usingAnsiMode;
/*      */ 
/*      */ 
/*      */   
/*      */   protected String batchedValuesClause;
/*      */ 
/*      */ 
/*      */   
/*      */   private int statementAfterCommentsPos;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasCheckedForRewrite = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean canRewrite = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean doPingInstead;
/*      */ 
/*      */   
/*      */   private SimpleDateFormat ddf;
/*      */ 
/*      */   
/*      */   private SimpleDateFormat tdf;
/*      */ 
/*      */ 
/*      */   
/*      */   protected static PreparedStatement getInstance(ConnectionImpl conn, String catalog) throws SQLException {
/*  532 */     if (!Util.isJdbc4()) {
/*  533 */       return new PreparedStatement(conn, catalog);
/*      */     }
/*      */     
/*  536 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_2_ARG_CTOR, new Object[] { conn, catalog });
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
/*      */   protected static PreparedStatement getInstance(ConnectionImpl conn, String sql, String catalog) throws SQLException {
/*  549 */     if (!Util.isJdbc4()) {
/*  550 */       return new PreparedStatement(conn, sql, catalog);
/*      */     }
/*      */     
/*  553 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_3_ARG_CTOR, new Object[] { conn, sql, catalog });
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
/*      */   protected static PreparedStatement getInstance(ConnectionImpl conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
/*  566 */     if (!Util.isJdbc4()) {
/*  567 */       return new PreparedStatement(conn, sql, catalog, cachedParseInfo);
/*      */     }
/*      */     
/*  570 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_4_ARG_CTOR, new Object[] { conn, sql, catalog, cachedParseInfo });
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
/*      */   public PreparedStatement(ConnectionImpl conn, String catalog) throws SQLException {
/*  588 */     super(conn, catalog);
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
/*      */   public PreparedStatement(ConnectionImpl conn, String sql, String catalog) throws SQLException {
/*  606 */     super(conn, catalog);
/*      */     
/*  608 */     if (sql == null) {
/*  609 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.0"), "S1009");
/*      */     }
/*      */ 
/*      */     
/*  613 */     this.originalSql = sql;
/*      */     
/*  615 */     if (this.originalSql.startsWith("/* ping */")) {
/*  616 */       this.doPingInstead = true;
/*      */     } else {
/*  618 */       this.doPingInstead = false;
/*      */     } 
/*      */     
/*  621 */     this.dbmd = this.connection.getMetaData();
/*      */     
/*  623 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */     
/*  625 */     this.parseInfo = new ParseInfo(this, sql, this.connection, this.dbmd, this.charEncoding, this.charConverter);
/*      */ 
/*      */     
/*  628 */     initializeFromParseInfo();
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
/*      */   public PreparedStatement(ConnectionImpl conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
/*  648 */     super(conn, catalog);
/*      */     
/*  650 */     if (sql == null) {
/*  651 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.1"), "S1009");
/*      */     }
/*      */ 
/*      */     
/*  655 */     this.originalSql = sql;
/*      */     
/*  657 */     this.dbmd = this.connection.getMetaData();
/*      */     
/*  659 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */     
/*  661 */     this.parseInfo = cachedParseInfo;
/*      */     
/*  663 */     this.usingAnsiMode = !this.connection.useAnsiQuotedIdentifiers();
/*      */     
/*  665 */     initializeFromParseInfo();
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
/*      */   public void addBatch() throws SQLException {
/*  677 */     if (this.batchedArgs == null) {
/*  678 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */     
/*  681 */     this.batchedArgs.add(new BatchParams(this, this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addBatch(String sql) throws SQLException {
/*  687 */     this.batchHasPlainStatements = true;
/*      */     
/*  689 */     super.addBatch(sql);
/*      */   }
/*      */   
/*      */   protected String asSql() throws SQLException {
/*  693 */     return asSql(false);
/*      */   }
/*      */   
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/*  697 */     if (this.isClosed) {
/*  698 */       return "statement has been closed, no further internal information available";
/*      */     }
/*      */     
/*  701 */     StringBuffer buf = new StringBuffer();
/*      */     
/*      */     try {
/*  704 */       for (int i = 0; i < this.parameterCount; i++) {
/*  705 */         if (this.charEncoding != null) {
/*  706 */           buf.append(new String(this.staticSqlStrings[i], this.charEncoding));
/*      */         } else {
/*      */           
/*  709 */           buf.append(new String(this.staticSqlStrings[i]));
/*      */         } 
/*      */         
/*  712 */         if (this.parameterValues[i] == null && !this.isStream[i]) {
/*  713 */           if (quoteStreamsAndUnknowns) {
/*  714 */             buf.append("'");
/*      */           }
/*      */           
/*  717 */           buf.append("** NOT SPECIFIED **");
/*      */           
/*  719 */           if (quoteStreamsAndUnknowns) {
/*  720 */             buf.append("'");
/*      */           }
/*  722 */         } else if (this.isStream[i]) {
/*  723 */           if (quoteStreamsAndUnknowns) {
/*  724 */             buf.append("'");
/*      */           }
/*      */           
/*  727 */           buf.append("** STREAM DATA **");
/*      */           
/*  729 */           if (quoteStreamsAndUnknowns) {
/*  730 */             buf.append("'");
/*      */           }
/*      */         }
/*  733 */         else if (this.charConverter != null) {
/*  734 */           buf.append(this.charConverter.toString(this.parameterValues[i]));
/*      */         
/*      */         }
/*  737 */         else if (this.charEncoding != null) {
/*  738 */           buf.append(new String(this.parameterValues[i], this.charEncoding));
/*      */         } else {
/*      */           
/*  741 */           buf.append(StringUtils.toAsciiString(this.parameterValues[i]));
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  748 */       if (this.charEncoding != null) {
/*  749 */         buf.append(new String(this.staticSqlStrings[this.parameterCount], this.charEncoding));
/*      */       }
/*      */       else {
/*      */         
/*  753 */         buf.append(StringUtils.toAsciiString(this.staticSqlStrings[this.parameterCount]));
/*      */       }
/*      */     
/*      */     }
/*  757 */     catch (UnsupportedEncodingException uue) {
/*  758 */       throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  764 */     return buf.toString();
/*      */   }
/*      */   
/*      */   public synchronized void clearBatch() throws SQLException {
/*  768 */     this.batchHasPlainStatements = false;
/*      */     
/*  770 */     super.clearBatch();
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
/*      */   public synchronized void clearParameters() throws SQLException {
/*  784 */     checkClosed();
/*      */     
/*  786 */     for (int i = 0; i < this.parameterValues.length; i++) {
/*  787 */       this.parameterValues[i] = null;
/*  788 */       this.parameterStreams[i] = null;
/*  789 */       this.isStream[i] = false;
/*  790 */       this.isNull[i] = false;
/*  791 */       this.parameterTypes[i] = 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close() throws SQLException {
/*  802 */     realClose(true, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException {
/*  807 */     int lastwritten = 0;
/*      */     
/*  809 */     for (int i = 0; i < size; i++) {
/*  810 */       byte b = buf[i];
/*      */       
/*  812 */       if (b == 0) {
/*      */         
/*  814 */         if (i > lastwritten) {
/*  815 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*      */         
/*  819 */         packet.writeByte((byte)92);
/*  820 */         packet.writeByte((byte)48);
/*  821 */         lastwritten = i + 1;
/*      */       }
/*  823 */       else if (b == 92 || b == 39 || (!this.usingAnsiMode && b == 34)) {
/*      */ 
/*      */         
/*  826 */         if (i > lastwritten) {
/*  827 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  832 */         packet.writeByte((byte)92);
/*  833 */         lastwritten = i;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  839 */     if (lastwritten < size) {
/*  840 */       packet.writeBytesNoNull(buf, lastwritten, size - lastwritten);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size) {
/*  846 */     int lastwritten = 0;
/*      */     
/*  848 */     for (int i = 0; i < size; i++) {
/*  849 */       byte b = buf[i];
/*      */       
/*  851 */       if (b == 0) {
/*      */         
/*  853 */         if (i > lastwritten) {
/*  854 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*      */         
/*  858 */         bytesOut.write(92);
/*  859 */         bytesOut.write(48);
/*  860 */         lastwritten = i + 1;
/*      */       }
/*  862 */       else if (b == 92 || b == 39 || (!this.usingAnsiMode && b == 34)) {
/*      */ 
/*      */         
/*  865 */         if (i > lastwritten) {
/*  866 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*      */         
/*  870 */         bytesOut.write(92);
/*  871 */         lastwritten = i;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  877 */     if (lastwritten < size) {
/*  878 */       bytesOut.write(buf, lastwritten, size - lastwritten);
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
/*      */   public boolean execute() throws SQLException {
/*  894 */     checkClosed();
/*      */     
/*  896 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */     
/*  898 */     if (locallyScopedConn.isReadOnly() && this.firstCharOfStmt != 'S') {
/*  899 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  904 */     ResultSetInternalMethods rs = null;
/*      */     
/*  906 */     CachedResultSetMetaData cachedMetadata = null;
/*      */     
/*  908 */     synchronized (locallyScopedConn.getMutex()) {
/*  909 */       boolean doStreaming = createStreamingResultSet();
/*      */       
/*  911 */       clearWarnings();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  921 */       if (doStreaming && this.connection.getNetTimeoutForStreamingResults() > 0)
/*      */       {
/*  923 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  929 */       this.batchedGeneratedKeys = null;
/*      */       
/*  931 */       Buffer sendPacket = fillSendPacket();
/*      */       
/*  933 */       String oldCatalog = null;
/*      */       
/*  935 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/*  936 */         oldCatalog = locallyScopedConn.getCatalog();
/*  937 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  943 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/*  944 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */       
/*  947 */       Field[] metadataFromCache = null;
/*      */       
/*  949 */       if (cachedMetadata != null) {
/*  950 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */       
/*  953 */       boolean oldInfoMsgState = false;
/*      */       
/*  955 */       if (this.retrieveGeneratedKeys) {
/*  956 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*  957 */         locallyScopedConn.setReadInfoMsgEnabled(true);
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
/*  969 */       if (locallyScopedConn.useMaxRows()) {
/*  970 */         int rowLimit = -1;
/*      */         
/*  972 */         if (this.firstCharOfStmt == 'S') {
/*  973 */           if (this.hasLimitClause) {
/*  974 */             rowLimit = this.maxRows;
/*      */           }
/*  976 */           else if (this.maxRows <= 0) {
/*  977 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */           } else {
/*      */             
/*  980 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  986 */           executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  991 */         rs = executeInternal(rowLimit, sendPacket, doStreaming, (this.firstCharOfStmt == 'S'), metadataFromCache, false);
/*      */       }
/*      */       else {
/*      */         
/*  995 */         rs = executeInternal(-1, sendPacket, doStreaming, (this.firstCharOfStmt == 'S'), metadataFromCache, false);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1000 */       if (cachedMetadata != null) {
/* 1001 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       
/*      */       }
/* 1004 */       else if (rs.reallyResult() && locallyScopedConn.getCacheResultSetMetadata()) {
/* 1005 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, (CachedResultSetMetaData)null, rs);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1010 */       if (this.retrieveGeneratedKeys) {
/* 1011 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 1012 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       } 
/*      */       
/* 1015 */       if (oldCatalog != null) {
/* 1016 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */       
/* 1019 */       if (rs != null) {
/* 1020 */         this.lastInsertId = rs.getUpdateID();
/*      */         
/* 1022 */         this.results = rs;
/*      */       } 
/*      */     } 
/*      */     
/* 1026 */     return (rs != null && rs.reallyResult());
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
/*      */   public int[] executeBatch() throws SQLException {
/* 1044 */     checkClosed();
/*      */     
/* 1046 */     if (this.connection.isReadOnly()) {
/* 1047 */       throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1052 */     synchronized (this.connection.getMutex()) {
/* 1053 */       if (this.batchedArgs == null || this.batchedArgs.size() == 0) {
/* 1054 */         return new int[0];
/*      */       }
/*      */ 
/*      */       
/* 1058 */       int batchTimeout = this.timeoutInMillis;
/* 1059 */       this.timeoutInMillis = 0;
/*      */       
/* 1061 */       resetCancelledState();
/*      */       
/*      */       try {
/* 1064 */         clearWarnings();
/*      */         
/* 1066 */         if (!this.batchHasPlainStatements && this.connection.getRewriteBatchedStatements()) {
/*      */ 
/*      */ 
/*      */           
/* 1070 */           if (canRewriteAsMultivalueInsertStatement()) {
/* 1071 */             return executeBatchedInserts(batchTimeout);
/*      */           }
/*      */           
/* 1074 */           if (this.connection.versionMeetsMinimum(4, 1, 0) && !this.batchHasPlainStatements && this.batchedArgs != null && this.batchedArgs.size() > 3)
/*      */           {
/*      */ 
/*      */             
/* 1078 */             return executePreparedBatchAsMultiStatement(batchTimeout);
/*      */           }
/*      */         } 
/*      */         
/* 1082 */         return executeBatchSerially(batchTimeout);
/*      */       } finally {
/* 1084 */         clearBatch();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public synchronized boolean canRewriteAsMultivalueInsertStatement() {
/* 1090 */     if (!this.hasCheckedForRewrite) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1101 */       this.canRewrite = (StringUtils.startsWithIgnoreCaseAndWs(this.originalSql, "INSERT", this.statementAfterCommentsPos) && StringUtils.indexOfIgnoreCaseRespectMarker(this.statementAfterCommentsPos, this.originalSql, "SELECT", "\"'`", "\"'`", false) == -1 && StringUtils.indexOfIgnoreCaseRespectMarker(this.statementAfterCommentsPos, this.originalSql, "UPDATE", "\"'`", "\"'`", false) == -1);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1106 */       this.hasCheckedForRewrite = true;
/*      */     } 
/*      */     
/* 1109 */     return this.canRewrite;
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
/*      */   protected int[] executePreparedBatchAsMultiStatement(int batchTimeout) throws SQLException {
/* 1123 */     synchronized (this.connection.getMutex()) {
/*      */       
/* 1125 */       if (this.batchedValuesClause == null) {
/* 1126 */         this.batchedValuesClause = this.originalSql + ";";
/*      */       }
/*      */       
/* 1129 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */       
/* 1131 */       boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
/* 1132 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       
/*      */       try {
/* 1135 */         clearWarnings();
/*      */         
/* 1137 */         int numBatchedArgs = this.batchedArgs.size();
/*      */         
/* 1139 */         if (this.retrieveGeneratedKeys) {
/* 1140 */           this.batchedGeneratedKeys = new ArrayList(numBatchedArgs);
/*      */         }
/*      */         
/* 1143 */         int numValuesPerBatch = computeBatchSize(numBatchedArgs);
/*      */         
/* 1145 */         if (numBatchedArgs < numValuesPerBatch) {
/* 1146 */           numValuesPerBatch = numBatchedArgs;
/*      */         }
/*      */         
/* 1149 */         PreparedStatement batchedStatement = null;
/*      */         
/* 1151 */         int batchedParamIndex = 1;
/* 1152 */         int numberToExecuteAsMultiValue = 0;
/* 1153 */         int batchCounter = 0;
/* 1154 */         int updateCountCounter = 0;
/* 1155 */         int[] updateCounts = new int[numBatchedArgs];
/* 1156 */         SQLException sqlEx = null;
/*      */         
/*      */         try {
/* 1159 */           if (!multiQueriesEnabled) {
/* 1160 */             locallyScopedConn.getIO().enableMultiQueries();
/*      */           }
/*      */           
/* 1163 */           if (this.retrieveGeneratedKeys) {
/* 1164 */             batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch), 1);
/*      */           }
/*      */           else {
/*      */             
/* 1168 */             batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch));
/*      */           } 
/*      */ 
/*      */           
/* 1172 */           if (locallyScopedConn.getEnableQueryTimeouts() && batchTimeout != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */             
/* 1175 */             timeoutTask = new StatementImpl.CancelTask(this, (StatementImpl)batchedStatement);
/* 1176 */             ConnectionImpl.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           } 
/*      */ 
/*      */           
/* 1180 */           if (numBatchedArgs < numValuesPerBatch) {
/* 1181 */             numberToExecuteAsMultiValue = numBatchedArgs;
/*      */           } else {
/* 1183 */             numberToExecuteAsMultiValue = numBatchedArgs / numValuesPerBatch;
/*      */           } 
/*      */           
/* 1186 */           int numberArgsToExecute = numberToExecuteAsMultiValue * numValuesPerBatch;
/*      */           
/* 1188 */           for (int i = 0; i < numberArgsToExecute; i++) {
/* 1189 */             if (i != 0 && i % numValuesPerBatch == 0) {
/*      */               try {
/* 1191 */                 batchedStatement.execute();
/* 1192 */               } catch (SQLException ex) {
/* 1193 */                 sqlEx = handleExceptionForBatch(batchCounter, numValuesPerBatch, updateCounts, ex);
/*      */               } 
/*      */ 
/*      */               
/* 1197 */               updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/*      */ 
/*      */               
/* 1201 */               batchedStatement.clearParameters();
/* 1202 */               batchedParamIndex = 1;
/*      */             } 
/*      */             
/* 1205 */             batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1211 */             batchedStatement.execute();
/* 1212 */           } catch (SQLException ex) {
/* 1213 */             sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */           } 
/*      */ 
/*      */           
/* 1217 */           updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/*      */ 
/*      */           
/* 1221 */           batchedStatement.clearParameters();
/*      */           
/* 1223 */           numValuesPerBatch = numBatchedArgs - batchCounter;
/*      */         } finally {
/* 1225 */           if (batchedStatement != null) {
/* 1226 */             batchedStatement.close();
/*      */           }
/*      */         } 
/*      */         
/*      */         try {
/* 1231 */           if (numValuesPerBatch > 0) {
/*      */             
/* 1233 */             if (this.retrieveGeneratedKeys) {
/* 1234 */               batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch), 1);
/*      */             }
/*      */             else {
/*      */               
/* 1238 */               batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch));
/*      */             } 
/*      */ 
/*      */             
/* 1242 */             if (timeoutTask != null) {
/* 1243 */               timeoutTask.toCancel = (StatementImpl)batchedStatement;
/*      */             }
/*      */             
/* 1246 */             batchedParamIndex = 1;
/*      */             
/* 1248 */             while (batchCounter < numBatchedArgs) {
/* 1249 */               batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*      */             try {
/* 1255 */               batchedStatement.execute();
/* 1256 */             } catch (SQLException ex) {
/* 1257 */               sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */             } 
/*      */ 
/*      */             
/* 1261 */             updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/*      */ 
/*      */             
/* 1265 */             batchedStatement.clearParameters();
/*      */           } 
/*      */           
/* 1268 */           if (timeoutTask != null) {
/* 1269 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 1270 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */             
/* 1273 */             timeoutTask.cancel();
/* 1274 */             timeoutTask = null;
/*      */           } 
/*      */           
/* 1277 */           if (sqlEx != null) {
/* 1278 */             throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1283 */           return updateCounts;
/*      */         } finally {
/* 1285 */           if (batchedStatement != null) {
/* 1286 */             batchedStatement.close();
/*      */           }
/*      */         } 
/*      */       } finally {
/* 1290 */         if (timeoutTask != null) {
/* 1291 */           timeoutTask.cancel();
/*      */         }
/*      */         
/* 1294 */         resetCancelledState();
/*      */         
/* 1296 */         if (!multiQueriesEnabled) {
/* 1297 */           locallyScopedConn.getIO().disableMultiQueries();
/*      */         }
/*      */         
/* 1300 */         clearBatch();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private String generateMultiStatementForBatch(int numBatches) {
/* 1306 */     StringBuffer newStatementSql = new StringBuffer((this.originalSql.length() + 1) * numBatches);
/*      */ 
/*      */     
/* 1309 */     newStatementSql.append(this.originalSql);
/*      */     
/* 1311 */     for (int i = 0; i < numBatches - 1; i++) {
/* 1312 */       newStatementSql.append(';');
/* 1313 */       newStatementSql.append(this.originalSql);
/*      */     } 
/*      */     
/* 1316 */     return newStatementSql.toString();
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
/*      */   protected int[] executeBatchedInserts(int batchTimeout) throws SQLException {
/* 1329 */     String valuesClause = extractValuesClause();
/*      */     
/* 1331 */     Connection locallyScopedConn = this.connection;
/*      */     
/* 1333 */     if (valuesClause == null) {
/* 1334 */       return executeBatchSerially(batchTimeout);
/*      */     }
/*      */     
/* 1337 */     int numBatchedArgs = this.batchedArgs.size();
/*      */     
/* 1339 */     if (this.retrieveGeneratedKeys) {
/* 1340 */       this.batchedGeneratedKeys = new ArrayList(numBatchedArgs);
/*      */     }
/*      */     
/* 1343 */     int numValuesPerBatch = computeBatchSize(numBatchedArgs);
/*      */     
/* 1345 */     if (numBatchedArgs < numValuesPerBatch) {
/* 1346 */       numValuesPerBatch = numBatchedArgs;
/*      */     }
/*      */     
/* 1349 */     PreparedStatement batchedStatement = null;
/*      */     
/* 1351 */     int batchedParamIndex = 1;
/* 1352 */     int updateCountRunningTotal = 0;
/* 1353 */     int numberToExecuteAsMultiValue = 0;
/* 1354 */     int batchCounter = 0;
/* 1355 */     StatementImpl.CancelTask timeoutTask = null;
/* 1356 */     SQLException sqlEx = null;
/*      */     
/* 1358 */     int[] updateCounts = new int[numBatchedArgs];
/*      */     
/* 1360 */     for (int i = 0; i < this.batchedArgs.size(); i++) {
/* 1361 */       updateCounts[i] = 1;
/*      */     }
/*      */     
/*      */     try {
/*      */       try {
/* 1366 */         if (this.retrieveGeneratedKeys) {
/* 1367 */           batchedStatement = locallyScopedConn.prepareStatement(generateBatchedInsertSQL(valuesClause, numValuesPerBatch), 1);
/*      */         }
/*      */         else {
/*      */           
/* 1371 */           batchedStatement = locallyScopedConn.prepareStatement(generateBatchedInsertSQL(valuesClause, numValuesPerBatch));
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1376 */         if (this.connection.getEnableQueryTimeouts() && batchTimeout != 0 && this.connection.versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */           
/* 1379 */           timeoutTask = new StatementImpl.CancelTask(this, (StatementImpl)batchedStatement);
/*      */           
/* 1381 */           ConnectionImpl.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */         } 
/*      */ 
/*      */         
/* 1385 */         if (numBatchedArgs < numValuesPerBatch) {
/* 1386 */           numberToExecuteAsMultiValue = numBatchedArgs;
/*      */         } else {
/* 1388 */           numberToExecuteAsMultiValue = numBatchedArgs / numValuesPerBatch;
/*      */         } 
/*      */ 
/*      */         
/* 1392 */         int numberArgsToExecute = numberToExecuteAsMultiValue * numValuesPerBatch;
/*      */ 
/*      */         
/* 1395 */         for (int j = 0; j < numberArgsToExecute; j++) {
/* 1396 */           if (j != 0 && j % numValuesPerBatch == 0) {
/*      */             try {
/* 1398 */               updateCountRunningTotal += batchedStatement.executeUpdate();
/*      */             }
/* 1400 */             catch (SQLException ex) {
/* 1401 */               sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */             } 
/*      */ 
/*      */             
/* 1405 */             getBatchedGeneratedKeys(batchedStatement);
/* 1406 */             batchedStatement.clearParameters();
/* 1407 */             batchedParamIndex = 1;
/*      */           } 
/*      */ 
/*      */           
/* 1411 */           batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1417 */           updateCountRunningTotal += batchedStatement.executeUpdate();
/* 1418 */         } catch (SQLException ex) {
/* 1419 */           sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */         } 
/*      */ 
/*      */         
/* 1423 */         getBatchedGeneratedKeys(batchedStatement);
/*      */         
/* 1425 */         numValuesPerBatch = numBatchedArgs - batchCounter;
/*      */       } finally {
/* 1427 */         if (batchedStatement != null) {
/* 1428 */           batchedStatement.close();
/*      */         }
/*      */       } 
/*      */       
/*      */       try {
/* 1433 */         if (numValuesPerBatch > 0) {
/*      */           
/* 1435 */           if (this.retrieveGeneratedKeys) {
/* 1436 */             batchedStatement = locallyScopedConn.prepareStatement(generateBatchedInsertSQL(valuesClause, numValuesPerBatch), 1);
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 1441 */             batchedStatement = locallyScopedConn.prepareStatement(generateBatchedInsertSQL(valuesClause, numValuesPerBatch));
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1446 */           if (timeoutTask != null) {
/* 1447 */             timeoutTask.toCancel = (StatementImpl)batchedStatement;
/*      */           }
/*      */           
/* 1450 */           batchedParamIndex = 1;
/*      */           
/* 1452 */           while (batchCounter < numBatchedArgs) {
/* 1453 */             batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1459 */             updateCountRunningTotal += batchedStatement.executeUpdate();
/* 1460 */           } catch (SQLException ex) {
/* 1461 */             sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */           } 
/*      */ 
/*      */           
/* 1465 */           getBatchedGeneratedKeys(batchedStatement);
/*      */         } 
/*      */         
/* 1468 */         if (sqlEx != null) {
/* 1469 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1474 */         return updateCounts;
/*      */       } finally {
/* 1476 */         if (batchedStatement != null) {
/* 1477 */           batchedStatement.close();
/*      */         }
/*      */       } 
/*      */     } finally {
/* 1481 */       if (timeoutTask != null) {
/* 1482 */         timeoutTask.cancel();
/*      */       }
/*      */       
/* 1485 */       resetCancelledState();
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
/*      */   protected int computeBatchSize(int numBatchedArgs) {
/* 1497 */     long[] combinedValues = computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
/*      */     
/* 1499 */     long maxSizeOfParameterSet = combinedValues[0];
/* 1500 */     long sizeOfEntireBatch = combinedValues[1];
/*      */     
/* 1502 */     int maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */     
/* 1504 */     if (sizeOfEntireBatch < (maxAllowedPacket - this.originalSql.length())) {
/* 1505 */       return numBatchedArgs;
/*      */     }
/*      */     
/* 1508 */     return (int)Math.max(1L, (maxAllowedPacket - this.originalSql.length()) / maxSizeOfParameterSet);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) {
/* 1516 */     long sizeOfEntireBatch = 0L;
/* 1517 */     long maxSizeOfParameterSet = 0L;
/*      */     
/* 1519 */     for (int i = 0; i < numBatchedArgs; i++) {
/* 1520 */       BatchParams paramArg = this.batchedArgs.get(i);
/*      */ 
/*      */       
/* 1523 */       boolean[] isNullBatch = paramArg.isNull;
/* 1524 */       boolean[] isStreamBatch = paramArg.isStream;
/*      */       
/* 1526 */       long sizeOfParameterSet = 0L;
/*      */       
/* 1528 */       for (int j = 0; j < isNullBatch.length; j++) {
/* 1529 */         if (!isNullBatch[j]) {
/*      */           
/* 1531 */           if (isStreamBatch[j]) {
/* 1532 */             int streamLength = paramArg.streamLengths[j];
/*      */             
/* 1534 */             if (streamLength != -1) {
/* 1535 */               sizeOfParameterSet += (streamLength * 2);
/*      */             } else {
/* 1537 */               int paramLength = (paramArg.parameterStrings[j]).length;
/* 1538 */               sizeOfParameterSet += paramLength;
/*      */             } 
/*      */           } else {
/* 1541 */             sizeOfParameterSet += (paramArg.parameterStrings[j]).length;
/*      */           } 
/*      */         } else {
/* 1544 */           sizeOfParameterSet += 4L;
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
/* 1556 */       if (this.batchedValuesClause != null) {
/* 1557 */         sizeOfParameterSet += (this.batchedValuesClause.length() + 1);
/*      */       }
/*      */       
/* 1560 */       sizeOfEntireBatch += sizeOfParameterSet;
/*      */       
/* 1562 */       if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 1563 */         maxSizeOfParameterSet = sizeOfParameterSet;
/*      */       }
/*      */     } 
/*      */     
/* 1567 */     return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
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
/*      */   protected int[] executeBatchSerially(int batchTimeout) throws SQLException {
/* 1580 */     Connection locallyScopedConn = this.connection;
/*      */     
/* 1582 */     if (locallyScopedConn == null) {
/* 1583 */       checkClosed();
/*      */     }
/*      */     
/* 1586 */     int[] updateCounts = null;
/*      */     
/* 1588 */     if (this.batchedArgs != null) {
/* 1589 */       int nbrCommands = this.batchedArgs.size();
/* 1590 */       updateCounts = new int[nbrCommands];
/*      */       
/* 1592 */       for (int i = 0; i < nbrCommands; i++) {
/* 1593 */         updateCounts[i] = -3;
/*      */       }
/*      */       
/* 1596 */       SQLException sqlEx = null;
/*      */       
/* 1598 */       int commandIndex = 0;
/*      */       
/* 1600 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       
/*      */       try {
/* 1603 */         if (this.connection.getEnableQueryTimeouts() && batchTimeout != 0 && this.connection.versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */           
/* 1606 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1607 */           ConnectionImpl.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */         } 
/*      */ 
/*      */         
/* 1611 */         if (this.retrieveGeneratedKeys) {
/* 1612 */           this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */         }
/*      */         
/* 1615 */         for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 1616 */           Object arg = this.batchedArgs.get(commandIndex);
/*      */           
/* 1618 */           if (arg instanceof String) {
/* 1619 */             updateCounts[commandIndex] = executeUpdate((String)arg);
/*      */           } else {
/* 1621 */             BatchParams paramArg = (BatchParams)arg;
/*      */             
/*      */             try {
/* 1624 */               updateCounts[commandIndex] = executeUpdate(paramArg.parameterStrings, paramArg.parameterStreams, paramArg.isStream, paramArg.streamLengths, paramArg.isNull, true);
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1629 */               if (this.retrieveGeneratedKeys) {
/* 1630 */                 ResultSet rs = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             }
/* 1645 */             catch (SQLException ex) {
/* 1646 */               updateCounts[commandIndex] = -3;
/*      */               
/* 1648 */               if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException)) {
/*      */ 
/*      */                 
/* 1651 */                 sqlEx = ex;
/*      */               } else {
/* 1653 */                 int[] newUpdateCounts = new int[commandIndex];
/* 1654 */                 System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*      */                 
/* 1657 */                 throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1665 */         if (sqlEx != null) {
/* 1666 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */       } finally {
/*      */         
/* 1670 */         if (timeoutTask != null) {
/* 1671 */           timeoutTask.cancel();
/*      */         }
/*      */         
/* 1674 */         resetCancelledState();
/*      */       } 
/*      */     } 
/*      */     
/* 1678 */     return (updateCounts != null) ? updateCounts : new int[0];
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
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch) throws SQLException {
/*      */     try {
/*      */       ResultSetInternalMethods rs;
/* 1709 */       resetCancelledState();
/*      */       
/* 1711 */       ConnectionImpl locallyScopedConnection = this.connection;
/*      */       
/* 1713 */       this.numberOfExecutions++;
/*      */       
/* 1715 */       if (this.doPingInstead) {
/* 1716 */         doPingInstead();
/*      */         
/* 1718 */         return this.results;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1723 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       
/*      */       try {
/* 1726 */         if (locallyScopedConnection.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConnection.versionMeetsMinimum(5, 0, 0)) {
/*      */ 
/*      */           
/* 1729 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1730 */           ConnectionImpl.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         } 
/*      */ 
/*      */         
/* 1734 */         rs = locallyScopedConnection.execSQL(this, (String)null, maxRowsToRetrieve, sendPacket, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, metadataFromCache, isBatch);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1739 */         if (timeoutTask != null) {
/* 1740 */           timeoutTask.cancel();
/*      */           
/* 1742 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1743 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */           
/* 1746 */           timeoutTask = null;
/*      */         } 
/*      */         
/* 1749 */         synchronized (this.cancelTimeoutMutex) {
/* 1750 */           if (this.wasCancelled) {
/* 1751 */             MySQLStatementCancelledException mySQLStatementCancelledException; SQLException cause = null;
/*      */             
/* 1753 */             if (this.wasCancelledByTimeout) {
/* 1754 */               MySQLTimeoutException mySQLTimeoutException = new MySQLTimeoutException();
/*      */             } else {
/* 1756 */               mySQLStatementCancelledException = new MySQLStatementCancelledException();
/*      */             } 
/*      */             
/* 1759 */             resetCancelledState();
/*      */             
/* 1761 */             throw mySQLStatementCancelledException;
/*      */           } 
/*      */         } 
/*      */       } finally {
/* 1765 */         if (timeoutTask != null) {
/* 1766 */           timeoutTask.cancel();
/*      */         }
/*      */       } 
/*      */       
/* 1770 */       return rs;
/* 1771 */     } catch (NullPointerException npe) {
/* 1772 */       checkClosed();
/*      */ 
/*      */ 
/*      */       
/* 1776 */       throw npe;
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
/*      */   public ResultSet executeQuery() throws SQLException {
/* 1790 */     checkClosed();
/*      */     
/* 1792 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */     
/* 1794 */     checkForDml(this.originalSql, this.firstCharOfStmt);
/*      */     
/* 1796 */     CachedResultSetMetaData cachedMetadata = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1802 */     synchronized (locallyScopedConn.getMutex()) {
/* 1803 */       clearWarnings();
/*      */       
/* 1805 */       boolean doStreaming = createStreamingResultSet();
/*      */       
/* 1807 */       this.batchedGeneratedKeys = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1817 */       if (doStreaming && this.connection.getNetTimeoutForStreamingResults() > 0)
/*      */       {
/* 1819 */         locallyScopedConn.execSQL(this, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults(), -1, (Buffer)null, 1003, 1007, false, this.currentCatalog, (Field[])null, false);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1826 */       Buffer sendPacket = fillSendPacket();
/*      */       
/* 1828 */       if (this.results != null && 
/* 1829 */         !this.connection.getHoldResultsOpenOverStatementClose() && 
/* 1830 */         !this.holdResultsOpenOverClose) {
/* 1831 */         this.results.realClose(false);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1836 */       String oldCatalog = null;
/*      */       
/* 1838 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1839 */         oldCatalog = locallyScopedConn.getCatalog();
/* 1840 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1846 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1847 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */       
/* 1850 */       Field[] metadataFromCache = null;
/*      */       
/* 1852 */       if (cachedMetadata != null) {
/* 1853 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */       
/* 1856 */       if (locallyScopedConn.useMaxRows()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1863 */         if (this.hasLimitClause) {
/* 1864 */           this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, metadataFromCache, false);
/*      */         }
/*      */         else {
/*      */           
/* 1868 */           if (this.maxRows <= 0) {
/* 1869 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */           } else {
/*      */             
/* 1872 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
/*      */           } 
/*      */ 
/*      */           
/* 1876 */           this.results = executeInternal(-1, sendPacket, doStreaming, true, metadataFromCache, false);
/*      */ 
/*      */ 
/*      */           
/* 1880 */           if (oldCatalog != null) {
/* 1881 */             this.connection.setCatalog(oldCatalog);
/*      */           }
/*      */         } 
/*      */       } else {
/* 1885 */         this.results = executeInternal(-1, sendPacket, doStreaming, true, metadataFromCache, false);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1890 */       if (oldCatalog != null) {
/* 1891 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */       
/* 1894 */       if (cachedMetadata != null) {
/* 1895 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       
/*      */       }
/* 1898 */       else if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1899 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, (CachedResultSetMetaData)null, this.results);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1905 */     this.lastInsertId = this.results.getUpdateID();
/*      */     
/* 1907 */     return this.results;
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
/*      */   public int executeUpdate() throws SQLException {
/* 1922 */     return executeUpdate(true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int executeUpdate(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch) throws SQLException {
/* 1932 */     if (clearBatchedGeneratedKeysAndWarnings) {
/* 1933 */       clearWarnings();
/* 1934 */       this.batchedGeneratedKeys = null;
/*      */     } 
/*      */     
/* 1937 */     return executeUpdate(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull, isBatch);
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
/*      */   protected int executeUpdate(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull, boolean isReallyBatch) throws SQLException {
/* 1965 */     checkClosed();
/*      */     
/* 1967 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */     
/* 1969 */     if (locallyScopedConn.isReadOnly()) {
/* 1970 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1975 */     if (this.firstCharOfStmt == 'S' && isSelectQuery())
/*      */     {
/* 1977 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), "01S03");
/*      */     }
/*      */ 
/*      */     
/* 1981 */     if (this.results != null && 
/* 1982 */       !locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
/* 1983 */       this.results.realClose(false);
/*      */     }
/*      */ 
/*      */     
/* 1987 */     ResultSetInternalMethods rs = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1992 */     synchronized (locallyScopedConn.getMutex()) {
/* 1993 */       Buffer sendPacket = fillSendPacket(batchedParameterStrings, batchedParameterStreams, batchedIsStream, batchedStreamLengths);
/*      */ 
/*      */ 
/*      */       
/* 1997 */       String oldCatalog = null;
/*      */       
/* 1999 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 2000 */         oldCatalog = locallyScopedConn.getCatalog();
/* 2001 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2007 */       if (locallyScopedConn.useMaxRows()) {
/* 2008 */         executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */       }
/*      */ 
/*      */       
/* 2012 */       boolean oldInfoMsgState = false;
/*      */       
/* 2014 */       if (this.retrieveGeneratedKeys) {
/* 2015 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 2016 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       } 
/*      */       
/* 2019 */       rs = executeInternal(-1, sendPacket, false, false, (Field[])null, isReallyBatch);
/*      */ 
/*      */       
/* 2022 */       if (this.retrieveGeneratedKeys) {
/* 2023 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 2024 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       } 
/*      */       
/* 2027 */       if (oldCatalog != null) {
/* 2028 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */     } 
/*      */     
/* 2032 */     this.results = rs;
/*      */     
/* 2034 */     this.updateCount = rs.getUpdateCount();
/*      */     
/* 2036 */     int truncatedUpdateCount = 0;
/*      */     
/* 2038 */     if (this.updateCount > 2147483647L) {
/* 2039 */       truncatedUpdateCount = Integer.MAX_VALUE;
/*      */     } else {
/* 2041 */       truncatedUpdateCount = (int)this.updateCount;
/*      */     } 
/*      */     
/* 2044 */     this.lastInsertId = rs.getUpdateID();
/*      */     
/* 2046 */     return truncatedUpdateCount;
/*      */   }
/*      */   
/*      */   private String extractValuesClause() throws SQLException {
/* 2050 */     if (this.batchedValuesClause == null) {
/* 2051 */       String quoteCharStr = this.connection.getMetaData().getIdentifierQuoteString();
/*      */ 
/*      */       
/* 2054 */       int indexOfValues = -1;
/*      */       
/* 2056 */       if (quoteCharStr.length() > 0) {
/* 2057 */         indexOfValues = StringUtils.indexOfIgnoreCaseRespectQuotes(this.statementAfterCommentsPos, this.originalSql, "VALUES ", quoteCharStr.charAt(0), false);
/*      */       }
/*      */       else {
/*      */         
/* 2061 */         indexOfValues = StringUtils.indexOfIgnoreCase(this.statementAfterCommentsPos, this.originalSql, "VALUES ");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2066 */       if (indexOfValues == -1) {
/* 2067 */         return null;
/*      */       }
/*      */       
/* 2070 */       int indexOfFirstParen = this.originalSql.indexOf('(', indexOfValues + 7);
/*      */ 
/*      */       
/* 2073 */       if (indexOfFirstParen == -1) {
/* 2074 */         return null;
/*      */       }
/*      */       
/* 2077 */       int indexOfLastParen = this.originalSql.lastIndexOf(')');
/*      */       
/* 2079 */       if (indexOfLastParen == -1) {
/* 2080 */         return null;
/*      */       }
/*      */       
/* 2083 */       this.batchedValuesClause = this.originalSql.substring(indexOfFirstParen, indexOfLastParen + 1);
/*      */     } 
/*      */ 
/*      */     
/* 2087 */     return this.batchedValuesClause;
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
/*      */   protected Buffer fillSendPacket() throws SQLException {
/* 2100 */     return fillSendPacket(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths);
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
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths) throws SQLException {
/* 2124 */     Buffer sendPacket = this.connection.getIO().getSharedSendPacket();
/*      */     
/* 2126 */     sendPacket.clear();
/*      */     
/* 2128 */     sendPacket.writeByte((byte)3);
/*      */     
/* 2130 */     boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2137 */     int ensurePacketSize = 0;
/*      */     
/* 2139 */     String statementComment = this.connection.getStatementComment();
/*      */     
/* 2141 */     byte[] commentAsBytes = null;
/*      */     
/* 2143 */     if (statementComment != null) {
/* 2144 */       if (this.charConverter != null) {
/* 2145 */         commentAsBytes = this.charConverter.toBytes(statementComment);
/*      */       } else {
/* 2147 */         commentAsBytes = StringUtils.getBytes(statementComment, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2153 */       ensurePacketSize += commentAsBytes.length;
/* 2154 */       ensurePacketSize += 6;
/*      */     } 
/*      */     int i;
/* 2157 */     for (i = 0; i < batchedParameterStrings.length; i++) {
/* 2158 */       if (batchedIsStream[i] && useStreamLengths) {
/* 2159 */         ensurePacketSize += batchedStreamLengths[i];
/*      */       }
/*      */     } 
/*      */     
/* 2163 */     if (ensurePacketSize != 0) {
/* 2164 */       sendPacket.ensureCapacity(ensurePacketSize);
/*      */     }
/*      */     
/* 2167 */     if (commentAsBytes != null) {
/* 2168 */       sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2169 */       sendPacket.writeBytesNoNull(commentAsBytes);
/* 2170 */       sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */     } 
/*      */     
/* 2173 */     for (i = 0; i < batchedParameterStrings.length; i++) {
/* 2174 */       if (batchedParameterStrings[i] == null && batchedParameterStreams[i] == null)
/*      */       {
/* 2176 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.40") + (i + 1), "07001");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 2181 */       sendPacket.writeBytesNoNull(this.staticSqlStrings[i]);
/*      */       
/* 2183 */       if (batchedIsStream[i]) {
/* 2184 */         streamToBytes(sendPacket, batchedParameterStreams[i], true, batchedStreamLengths[i], useStreamLengths);
/*      */       } else {
/*      */         
/* 2187 */         sendPacket.writeBytesNoNull(batchedParameterStrings[i]);
/*      */       } 
/*      */     } 
/*      */     
/* 2191 */     sendPacket.writeBytesNoNull(this.staticSqlStrings[batchedParameterStrings.length]);
/*      */ 
/*      */     
/* 2194 */     return sendPacket;
/*      */   }
/*      */   
/*      */   private String generateBatchedInsertSQL(String valuesClause, int numBatches) {
/* 2198 */     StringBuffer newStatementSql = new StringBuffer(this.originalSql.length() + numBatches * (valuesClause.length() + 1));
/*      */ 
/*      */ 
/*      */     
/* 2202 */     newStatementSql.append(this.originalSql);
/*      */     
/* 2204 */     for (int i = 0; i < numBatches - 1; i++) {
/* 2205 */       newStatementSql.append(',');
/* 2206 */       newStatementSql.append(valuesClause);
/*      */     } 
/*      */     
/* 2209 */     return newStatementSql.toString();
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
/*      */   public byte[] getBytesRepresentation(int parameterIndex) throws SQLException {
/* 2225 */     if (this.isStream[parameterIndex]) {
/* 2226 */       return streamToBytes(this.parameterStreams[parameterIndex], false, this.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 2231 */     byte[] parameterVal = this.parameterValues[parameterIndex];
/*      */     
/* 2233 */     if (parameterVal == null) {
/* 2234 */       return null;
/*      */     }
/*      */     
/* 2237 */     if (parameterVal[0] == 39 && parameterVal[parameterVal.length - 1] == 39) {
/*      */       
/* 2239 */       byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2240 */       System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */ 
/*      */       
/* 2243 */       return valNoQuotes;
/*      */     } 
/*      */     
/* 2246 */     return parameterVal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String getDateTimePattern(String dt, boolean toTime) throws Exception {
/* 2256 */     int dtLength = (dt != null) ? dt.length() : 0;
/*      */     
/* 2258 */     if (dtLength >= 8 && dtLength <= 10) {
/* 2259 */       int dashCount = 0;
/* 2260 */       boolean isDateOnly = true;
/*      */       
/* 2262 */       for (int k = 0; k < dtLength; k++) {
/* 2263 */         char c = dt.charAt(k);
/*      */         
/* 2265 */         if (!Character.isDigit(c) && c != '-') {
/* 2266 */           isDateOnly = false;
/*      */           
/*      */           break;
/*      */         } 
/*      */         
/* 2271 */         if (c == '-') {
/* 2272 */           dashCount++;
/*      */         }
/*      */       } 
/*      */       
/* 2276 */       if (isDateOnly && dashCount == 2) {
/* 2277 */         return "yyyy-MM-dd";
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2284 */     boolean colonsOnly = true;
/*      */     
/* 2286 */     for (int i = 0; i < dtLength; i++) {
/* 2287 */       char c = dt.charAt(i);
/*      */       
/* 2289 */       if (!Character.isDigit(c) && c != ':') {
/* 2290 */         colonsOnly = false;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*      */     
/* 2296 */     if (colonsOnly) {
/* 2297 */       return "HH:mm:ss";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2306 */     StringReader reader = new StringReader(dt + " ");
/* 2307 */     ArrayList vec = new ArrayList();
/* 2308 */     ArrayList vecRemovelist = new ArrayList();
/* 2309 */     Object[] nv = new Object[3];
/*      */     
/* 2311 */     nv[0] = Constants.characterValueOf('y');
/* 2312 */     nv[1] = new StringBuffer();
/* 2313 */     nv[2] = Constants.integerValueOf(0);
/* 2314 */     vec.add(nv);
/*      */     
/* 2316 */     if (toTime) {
/* 2317 */       nv = new Object[3];
/* 2318 */       nv[0] = Constants.characterValueOf('h');
/* 2319 */       nv[1] = new StringBuffer();
/* 2320 */       nv[2] = Constants.integerValueOf(0);
/* 2321 */       vec.add(nv);
/*      */     } 
/*      */     int z;
/* 2324 */     while ((z = reader.read()) != -1) {
/* 2325 */       char separator = (char)z;
/* 2326 */       int maxvecs = vec.size();
/*      */       
/* 2328 */       for (int count = 0; count < maxvecs; count++) {
/* 2329 */         Object[] arrayOfObject = vec.get(count);
/* 2330 */         int n = ((Integer)arrayOfObject[2]).intValue();
/* 2331 */         char c = getSuccessor(((Character)arrayOfObject[0]).charValue(), n);
/*      */         
/* 2333 */         if (!Character.isLetterOrDigit(separator)) {
/* 2334 */           if (c == ((Character)arrayOfObject[0]).charValue() && c != 'S') {
/* 2335 */             vecRemovelist.add(arrayOfObject);
/*      */           } else {
/* 2337 */             ((StringBuffer)arrayOfObject[1]).append(separator);
/*      */             
/* 2339 */             if (c == 'X' || c == 'Y') {
/* 2340 */               arrayOfObject[2] = Constants.integerValueOf(4);
/*      */             }
/*      */           } 
/*      */         } else {
/* 2344 */           if (c == 'X') {
/* 2345 */             c = 'y';
/* 2346 */             nv = new Object[3];
/* 2347 */             nv[1] = (new StringBuffer(((StringBuffer)arrayOfObject[1]).toString())).append('M');
/*      */             
/* 2349 */             nv[0] = Constants.characterValueOf('M');
/* 2350 */             nv[2] = Constants.integerValueOf(1);
/* 2351 */             vec.add(nv);
/* 2352 */           } else if (c == 'Y') {
/* 2353 */             c = 'M';
/* 2354 */             nv = new Object[3];
/* 2355 */             nv[1] = (new StringBuffer(((StringBuffer)arrayOfObject[1]).toString())).append('d');
/*      */             
/* 2357 */             nv[0] = Constants.characterValueOf('d');
/* 2358 */             nv[2] = Constants.integerValueOf(1);
/* 2359 */             vec.add(nv);
/*      */           } 
/*      */           
/* 2362 */           ((StringBuffer)arrayOfObject[1]).append(c);
/*      */           
/* 2364 */           if (c == ((Character)arrayOfObject[0]).charValue()) {
/* 2365 */             arrayOfObject[2] = Constants.integerValueOf(n + 1);
/*      */           } else {
/* 2367 */             arrayOfObject[0] = Constants.characterValueOf(c);
/* 2368 */             arrayOfObject[2] = Constants.integerValueOf(1);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 2373 */       int k = vecRemovelist.size();
/*      */       
/* 2375 */       for (int m = 0; m < k; m++) {
/* 2376 */         Object[] arrayOfObject = vecRemovelist.get(m);
/* 2377 */         vec.remove(arrayOfObject);
/*      */       } 
/*      */       
/* 2380 */       vecRemovelist.clear();
/*      */     } 
/*      */     
/* 2383 */     int size = vec.size();
/*      */     int j;
/* 2385 */     for (j = 0; j < size; j++) {
/* 2386 */       Object[] arrayOfObject = vec.get(j);
/* 2387 */       char c = ((Character)arrayOfObject[0]).charValue();
/* 2388 */       int n = ((Integer)arrayOfObject[2]).intValue();
/*      */       
/* 2390 */       boolean bk = (getSuccessor(c, n) != c);
/* 2391 */       boolean atEnd = ((c == 's' || c == 'm' || (c == 'h' && toTime)) && bk);
/* 2392 */       boolean finishesAtDate = (bk && c == 'd' && !toTime);
/* 2393 */       boolean containsEnd = (((StringBuffer)arrayOfObject[1]).toString().indexOf('W') != -1);
/*      */ 
/*      */       
/* 2396 */       if ((!atEnd && !finishesAtDate) || containsEnd) {
/* 2397 */         vecRemovelist.add(arrayOfObject);
/*      */       }
/*      */     } 
/*      */     
/* 2401 */     size = vecRemovelist.size();
/*      */     
/* 2403 */     for (j = 0; j < size; j++) {
/* 2404 */       vec.remove(vecRemovelist.get(j));
/*      */     }
/*      */     
/* 2407 */     vecRemovelist.clear();
/* 2408 */     Object[] v = vec.get(0);
/*      */     
/* 2410 */     StringBuffer format = (StringBuffer)v[1];
/* 2411 */     format.setLength(format.length() - 1);
/*      */     
/* 2413 */     return format.toString();
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
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/* 2439 */     if (!isSelectQuery()) {
/* 2440 */       return null;
/*      */     }
/*      */     
/* 2443 */     PreparedStatement mdStmt = null;
/* 2444 */     ResultSet mdRs = null;
/*      */     
/* 2446 */     if (this.pstmtResultMetaData == null) {
/*      */       try {
/* 2448 */         mdStmt = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog, this.parseInfo);
/*      */ 
/*      */         
/* 2451 */         mdStmt.setMaxRows(0);
/*      */         
/* 2453 */         int paramCount = this.parameterValues.length;
/*      */         
/* 2455 */         for (int i = 1; i <= paramCount; i++) {
/* 2456 */           mdStmt.setString(i, "");
/*      */         }
/*      */         
/* 2459 */         boolean hadResults = mdStmt.execute();
/*      */         
/* 2461 */         if (hadResults) {
/* 2462 */           mdRs = mdStmt.getResultSet();
/*      */           
/* 2464 */           this.pstmtResultMetaData = mdRs.getMetaData();
/*      */         } else {
/* 2466 */           this.pstmtResultMetaData = new ResultSetMetaData(new Field[0], this.connection.getUseOldAliasMetadataBehavior());
/*      */         }
/*      */       
/*      */       } finally {
/*      */         
/* 2471 */         SQLException sqlExRethrow = null;
/*      */         
/* 2473 */         if (mdRs != null) {
/*      */           try {
/* 2475 */             mdRs.close();
/* 2476 */           } catch (SQLException sqlEx) {
/* 2477 */             sqlExRethrow = sqlEx;
/*      */           } 
/*      */           
/* 2480 */           mdRs = null;
/*      */         } 
/*      */         
/* 2483 */         if (mdStmt != null) {
/*      */           try {
/* 2485 */             mdStmt.close();
/* 2486 */           } catch (SQLException sqlEx) {
/* 2487 */             sqlExRethrow = sqlEx;
/*      */           } 
/*      */           
/* 2490 */           mdStmt = null;
/*      */         } 
/*      */         
/* 2493 */         if (sqlExRethrow != null) {
/* 2494 */           throw sqlExRethrow;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 2499 */     return this.pstmtResultMetaData;
/*      */   }
/*      */   
/*      */   protected boolean isSelectQuery() {
/* 2503 */     return StringUtils.startsWithIgnoreCaseAndWs(StringUtils.stripComments(this.originalSql, "'\"", "'\"", true, false, true, true), "SELECT");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException {
/* 2514 */     if (this.parameterMetaData == null) {
/* 2515 */       if (this.connection.getGenerateSimpleParameterMetadata()) {
/* 2516 */         this.parameterMetaData = new MysqlParameterMetadata(this.parameterCount);
/*      */       } else {
/* 2518 */         this.parameterMetaData = new MysqlParameterMetadata(null, this.parameterCount);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 2523 */     return this.parameterMetaData;
/*      */   }
/*      */   
/*      */   ParseInfo getParseInfo() {
/* 2527 */     return this.parseInfo;
/*      */   }
/*      */   
/*      */   private final char getSuccessor(char c, int n) {
/* 2531 */     return (c == 'y' && n == 2) ? 'X' : ((c == 'y' && n < 4) ? 'y' : ((c == 'y') ? 'M' : ((c == 'M' && n == 2) ? 'Y' : ((c == 'M' && n < 3) ? 'M' : ((c == 'M') ? 'd' : ((c == 'd' && n < 2) ? 'd' : ((c == 'd') ? 'H' : ((c == 'H' && n < 2) ? 'H' : ((c == 'H') ? 'm' : ((c == 'm' && n < 2) ? 'm' : ((c == 'm') ? 's' : ((c == 's' && n < 2) ? 's' : 'W'))))))))))));
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
/*      */   private final void hexEscapeBlock(byte[] buf, Buffer packet, int size) throws SQLException {
/* 2557 */     for (int i = 0; i < size; i++) {
/* 2558 */       byte b = buf[i];
/* 2559 */       int lowBits = (b & 0xFF) / 16;
/* 2560 */       int highBits = (b & 0xFF) % 16;
/*      */       
/* 2562 */       packet.writeByte(HEX_DIGITS[lowBits]);
/* 2563 */       packet.writeByte(HEX_DIGITS[highBits]);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initializeFromParseInfo() throws SQLException {
/* 2568 */     this.staticSqlStrings = this.parseInfo.staticSql;
/* 2569 */     this.hasLimitClause = this.parseInfo.foundLimitClause;
/* 2570 */     this.isLoadDataQuery = this.parseInfo.foundLoadData;
/* 2571 */     this.firstCharOfStmt = this.parseInfo.firstStmtChar;
/*      */     
/* 2573 */     this.parameterCount = this.staticSqlStrings.length - 1;
/*      */     
/* 2575 */     this.parameterValues = new byte[this.parameterCount][];
/* 2576 */     this.parameterStreams = new InputStream[this.parameterCount];
/* 2577 */     this.isStream = new boolean[this.parameterCount];
/* 2578 */     this.streamLengths = new int[this.parameterCount];
/* 2579 */     this.isNull = new boolean[this.parameterCount];
/* 2580 */     this.parameterTypes = new int[this.parameterCount];
/*      */     
/* 2582 */     clearParameters();
/*      */     
/* 2584 */     for (int j = 0; j < this.parameterCount; j++) {
/* 2585 */       this.isStream[j] = false;
/*      */     }
/*      */     
/* 2588 */     this.statementAfterCommentsPos = this.parseInfo.statementStartPos;
/*      */   }
/*      */   
/*      */   boolean isNull(int paramIndex) {
/* 2592 */     return this.isNull[paramIndex];
/*      */   }
/*      */   
/*      */   private final int readblock(InputStream i, byte[] b) throws SQLException {
/*      */     try {
/* 2597 */       return i.read(b);
/* 2598 */     } catch (Throwable ex) {
/* 2599 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000");
/*      */       
/* 2601 */       sqlEx.initCause(ex);
/*      */       
/* 2603 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final int readblock(InputStream i, byte[] b, int length) throws SQLException {
/*      */     try {
/* 2610 */       int lengthToRead = length;
/*      */       
/* 2612 */       if (lengthToRead > b.length) {
/* 2613 */         lengthToRead = b.length;
/*      */       }
/*      */       
/* 2616 */       return i.read(b, 0, lengthToRead);
/* 2617 */     } catch (Throwable ex) {
/* 2618 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000");
/*      */       
/* 2620 */       sqlEx.initCause(ex);
/*      */       
/* 2622 */       throw sqlEx;
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
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
/* 2637 */     if (this.useUsageAdvisor && 
/* 2638 */       this.numberOfExecutions <= 1) {
/* 2639 */       String message = Messages.getString("PreparedStatement.43");
/*      */       
/* 2641 */       this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2650 */     super.realClose(calledExplicitly, closeOpenResults);
/*      */     
/* 2652 */     this.dbmd = null;
/* 2653 */     this.originalSql = null;
/* 2654 */     this.staticSqlStrings = (byte[][])null;
/* 2655 */     this.parameterValues = (byte[][])null;
/* 2656 */     this.parameterStreams = null;
/* 2657 */     this.isStream = null;
/* 2658 */     this.streamLengths = null;
/* 2659 */     this.isNull = null;
/* 2660 */     this.streamConvertBuf = null;
/* 2661 */     this.parameterTypes = null;
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
/*      */   public void setArray(int i, Array x) throws SQLException {
/* 2678 */     throw SQLError.notImplemented();
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
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 2705 */     if (x == null) {
/* 2706 */       setNull(parameterIndex, 12);
/*      */     } else {
/* 2708 */       setBinaryStream(parameterIndex, x, length);
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
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/* 2726 */     if (x == null) {
/* 2727 */       setNull(parameterIndex, 3);
/*      */     } else {
/* 2729 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString(x)));
/*      */ 
/*      */       
/* 2732 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 3;
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
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 2758 */     if (x == null) {
/* 2759 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 2761 */       int parameterIndexOffset = getParameterIndexOffset();
/*      */       
/* 2763 */       if (parameterIndex < 1 || parameterIndex > this.staticSqlStrings.length)
/*      */       {
/* 2765 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.2") + parameterIndex + Messages.getString("PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString("PreparedStatement.4"), "S1009");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 2770 */       if (parameterIndexOffset == -1 && parameterIndex == 1) {
/* 2771 */         throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 2776 */       this.parameterStreams[parameterIndex - 1 + parameterIndexOffset] = x;
/* 2777 */       this.isStream[parameterIndex - 1 + parameterIndexOffset] = true;
/* 2778 */       this.streamLengths[parameterIndex - 1 + parameterIndexOffset] = length;
/* 2779 */       this.isNull[parameterIndex - 1 + parameterIndexOffset] = false;
/* 2780 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 2004;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/* 2786 */     setBinaryStream(parameterIndex, inputStream, (int)length);
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
/*      */   public void setBlob(int i, Blob x) throws SQLException {
/* 2801 */     if (x == null) {
/* 2802 */       setNull(i, 2004);
/*      */     } else {
/* 2804 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */       
/* 2806 */       bytesOut.write(39);
/* 2807 */       escapeblockFast(x.getBytes(1L, (int)x.length()), bytesOut, (int)x.length());
/*      */       
/* 2809 */       bytesOut.write(39);
/*      */       
/* 2811 */       setInternal(i, bytesOut.toByteArray());
/*      */       
/* 2813 */       this.parameterTypes[i - 1 + getParameterIndexOffset()] = 2004;
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
/*      */   public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/* 2830 */     if (this.useTrueBoolean) {
/* 2831 */       setInternal(parameterIndex, x ? "1" : "0");
/*      */     } else {
/* 2833 */       setInternal(parameterIndex, x ? "'t'" : "'f'");
/*      */       
/* 2835 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 16;
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
/*      */   public void setByte(int parameterIndex, byte x) throws SQLException {
/* 2852 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 2854 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = -6;
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
/*      */   public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/* 2871 */     setBytes(parameterIndex, x, true, true);
/*      */     
/* 2873 */     if (x != null) {
/* 2874 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = -2;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) throws SQLException {
/* 2881 */     if (x == null) {
/* 2882 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 2884 */       String connectionEncoding = this.connection.getEncoding();
/*      */       
/* 2886 */       if (this.connection.isNoBackslashEscapesSet() || (escapeForMBChars && this.connection.getUseUnicode() && connectionEncoding != null && CharsetMapping.isMultibyteCharset(connectionEncoding))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2894 */         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(x.length * 2 + 3);
/*      */         
/* 2896 */         byteArrayOutputStream.write(120);
/* 2897 */         byteArrayOutputStream.write(39);
/*      */         
/* 2899 */         for (int j = 0; j < x.length; j++) {
/* 2900 */           int lowBits = (x[j] & 0xFF) / 16;
/* 2901 */           int highBits = (x[j] & 0xFF) % 16;
/*      */           
/* 2903 */           byteArrayOutputStream.write(HEX_DIGITS[lowBits]);
/* 2904 */           byteArrayOutputStream.write(HEX_DIGITS[highBits]);
/*      */         } 
/*      */         
/* 2907 */         byteArrayOutputStream.write(39);
/*      */         
/* 2909 */         setInternal(parameterIndex, byteArrayOutputStream.toByteArray());
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 2915 */       int numBytes = x.length;
/*      */       
/* 2917 */       int pad = 2;
/*      */       
/* 2919 */       boolean needsIntroducer = (checkForIntroducer && this.connection.versionMeetsMinimum(4, 1, 0));
/*      */ 
/*      */       
/* 2922 */       if (needsIntroducer) {
/* 2923 */         pad += 7;
/*      */       }
/*      */       
/* 2926 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
/*      */ 
/*      */       
/* 2929 */       if (needsIntroducer) {
/* 2930 */         bOut.write(95);
/* 2931 */         bOut.write(98);
/* 2932 */         bOut.write(105);
/* 2933 */         bOut.write(110);
/* 2934 */         bOut.write(97);
/* 2935 */         bOut.write(114);
/* 2936 */         bOut.write(121);
/*      */       } 
/* 2938 */       bOut.write(39);
/*      */       
/* 2940 */       for (int i = 0; i < numBytes; i++) {
/* 2941 */         byte b = x[i];
/*      */         
/* 2943 */         switch (b) {
/*      */           case 0:
/* 2945 */             bOut.write(92);
/* 2946 */             bOut.write(48);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 10:
/* 2951 */             bOut.write(92);
/* 2952 */             bOut.write(110);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 13:
/* 2957 */             bOut.write(92);
/* 2958 */             bOut.write(114);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 92:
/* 2963 */             bOut.write(92);
/* 2964 */             bOut.write(92);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 39:
/* 2969 */             bOut.write(92);
/* 2970 */             bOut.write(39);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 34:
/* 2975 */             bOut.write(92);
/* 2976 */             bOut.write(34);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 26:
/* 2981 */             bOut.write(92);
/* 2982 */             bOut.write(90);
/*      */             break;
/*      */ 
/*      */           
/*      */           default:
/* 2987 */             bOut.write(b);
/*      */             break;
/*      */         } 
/*      */       } 
/* 2991 */       bOut.write(39);
/*      */       
/* 2993 */       setInternal(parameterIndex, bOut.toByteArray());
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
/*      */   protected void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
/* 3011 */     byte[] parameterWithQuotes = new byte[parameterAsBytes.length + 2];
/* 3012 */     parameterWithQuotes[0] = 39;
/* 3013 */     System.arraycopy(parameterAsBytes, 0, parameterWithQuotes, 1, parameterAsBytes.length);
/*      */     
/* 3015 */     parameterWithQuotes[parameterAsBytes.length + 1] = 39;
/*      */     
/* 3017 */     setInternal(parameterIndex, parameterWithQuotes);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
/* 3022 */     setInternal(parameterIndex, parameterAsBytes);
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
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/*      */     try {
/* 3050 */       if (reader == null) {
/* 3051 */         setNull(parameterIndex, -1);
/*      */       } else {
/* 3053 */         char[] c = null;
/* 3054 */         int len = 0;
/*      */         
/* 3056 */         boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/*      */         
/* 3059 */         String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */         
/* 3061 */         if (useLength && length != -1) {
/* 3062 */           c = new char[length];
/*      */           
/* 3064 */           int numCharsRead = readFully(reader, c, length);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3069 */           if (forcedEncoding == null) {
/* 3070 */             setString(parameterIndex, new String(c, 0, numCharsRead));
/*      */           } else {
/*      */             try {
/* 3073 */               setBytes(parameterIndex, (new String(c, 0, numCharsRead)).getBytes(forcedEncoding));
/*      */             
/*      */             }
/* 3076 */             catch (UnsupportedEncodingException uee) {
/* 3077 */               throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009");
/*      */             } 
/*      */           } 
/*      */         } else {
/*      */           
/* 3082 */           c = new char[4096];
/*      */           
/* 3084 */           StringBuffer buf = new StringBuffer();
/*      */           
/* 3086 */           while ((len = reader.read(c)) != -1) {
/* 3087 */             buf.append(c, 0, len);
/*      */           }
/*      */           
/* 3090 */           if (forcedEncoding == null) {
/* 3091 */             setString(parameterIndex, buf.toString());
/*      */           } else {
/*      */             try {
/* 3094 */               setBytes(parameterIndex, buf.toString().getBytes(forcedEncoding));
/*      */             }
/* 3096 */             catch (UnsupportedEncodingException uee) {
/* 3097 */               throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009");
/*      */             } 
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 3103 */         this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 2005;
/*      */       } 
/* 3105 */     } catch (IOException ioEx) {
/* 3106 */       throw SQLError.createSQLException(ioEx.toString(), "S1000");
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
/*      */   public void setClob(int i, Clob x) throws SQLException {
/* 3123 */     if (x == null) {
/* 3124 */       setNull(i, 2005);
/*      */     } else {
/*      */       
/* 3127 */       String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */       
/* 3129 */       if (forcedEncoding == null) {
/* 3130 */         setString(i, x.getSubString(1L, (int)x.length()));
/*      */       } else {
/*      */         try {
/* 3133 */           setBytes(i, x.getSubString(1L, (int)x.length()).getBytes(forcedEncoding));
/*      */         }
/* 3135 */         catch (UnsupportedEncodingException uee) {
/* 3136 */           throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009");
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 3141 */       this.parameterTypes[i - 1 + getParameterIndexOffset()] = 2005;
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
/*      */   public void setDate(int parameterIndex, Date x) throws SQLException {
/* 3159 */     setDate(parameterIndex, x, (Calendar)null);
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
/* 3178 */     if (x == null) {
/* 3179 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 3181 */       checkClosed();
/*      */       
/* 3183 */       if (!this.useLegacyDatetimeCode) {
/* 3184 */         newSetDateInternal(parameterIndex, x, cal);
/*      */       }
/*      */       else {
/*      */         
/* 3188 */         SimpleDateFormat dateFormatter = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */         
/* 3190 */         setInternal(parameterIndex, dateFormatter.format(x));
/*      */         
/* 3192 */         this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 91;
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
/*      */   public void setDouble(int parameterIndex, double x) throws SQLException {
/* 3211 */     if (!this.connection.getAllowNanAndInf() && (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || Double.isNaN(x)))
/*      */     {
/*      */       
/* 3214 */       throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3220 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */ 
/*      */     
/* 3223 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 8;
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
/*      */   public void setFloat(int parameterIndex, float x) throws SQLException {
/* 3239 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */ 
/*      */     
/* 3242 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 6;
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
/*      */   public void setInt(int parameterIndex, int x) throws SQLException {
/* 3258 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 3260 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 4;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void setInternal(int paramIndex, byte[] val) throws SQLException {
/* 3265 */     if (this.isClosed) {
/* 3266 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.48"), "S1009");
/*      */     }
/*      */ 
/*      */     
/* 3270 */     int parameterIndexOffset = getParameterIndexOffset();
/*      */     
/* 3272 */     checkBounds(paramIndex, parameterIndexOffset);
/*      */     
/* 3274 */     this.isStream[paramIndex - 1 + parameterIndexOffset] = false;
/* 3275 */     this.isNull[paramIndex - 1 + parameterIndexOffset] = false;
/* 3276 */     this.parameterStreams[paramIndex - 1 + parameterIndexOffset] = null;
/* 3277 */     this.parameterValues[paramIndex - 1 + parameterIndexOffset] = val;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException {
/* 3282 */     if (paramIndex < 1) {
/* 3283 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009");
/*      */     }
/*      */ 
/*      */     
/* 3287 */     if (paramIndex > this.parameterCount) {
/* 3288 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + this.parameterValues.length + Messages.getString("PreparedStatement.53"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 3293 */     if (parameterIndexOffset == -1 && paramIndex == 1) {
/* 3294 */       throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void setInternal(int paramIndex, String val) throws SQLException {
/* 3301 */     checkClosed();
/*      */     
/* 3303 */     byte[] parameterAsBytes = null;
/*      */     
/* 3305 */     if (this.charConverter != null) {
/* 3306 */       parameterAsBytes = this.charConverter.toBytes(val);
/*      */     } else {
/* 3308 */       parameterAsBytes = StringUtils.getBytes(val, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3314 */     setInternal(paramIndex, parameterAsBytes);
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
/*      */   public void setLong(int parameterIndex, long x) throws SQLException {
/* 3330 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 3332 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = -5;
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
/*      */   public void setNull(int parameterIndex, int sqlType) throws SQLException {
/* 3352 */     setInternal(parameterIndex, "null");
/* 3353 */     this.isNull[parameterIndex - 1 + getParameterIndexOffset()] = true;
/*      */     
/* 3355 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 0;
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
/*      */   public void setNull(int parameterIndex, int sqlType, String arg) throws SQLException {
/* 3377 */     setNull(parameterIndex, sqlType);
/*      */     
/* 3379 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 0;
/*      */   }
/*      */ 
/*      */   
/*      */   private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
/*      */     Number parameterAsNum;
/* 3385 */     if (parameterObj instanceof Boolean) {
/* 3386 */       parameterAsNum = ((Boolean)parameterObj).booleanValue() ? Constants.integerValueOf(1) : Constants.integerValueOf(0);
/*      */     
/*      */     }
/* 3389 */     else if (parameterObj instanceof String) {
/* 3390 */       boolean parameterAsBoolean; switch (targetSqlType) {
/*      */         case -7:
/* 3392 */           if ("1".equals(parameterObj) || "0".equals(parameterObj)) {
/*      */             
/* 3394 */             Number number = Integer.valueOf((String)parameterObj); break;
/*      */           } 
/* 3396 */           parameterAsBoolean = "true".equalsIgnoreCase((String)parameterObj);
/*      */ 
/*      */           
/* 3399 */           parameterAsNum = parameterAsBoolean ? Constants.integerValueOf(1) : Constants.integerValueOf(0);
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case -6:
/*      */         case 4:
/*      */         case 5:
/* 3408 */           parameterAsNum = Integer.valueOf((String)parameterObj);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case -5:
/* 3414 */           parameterAsNum = Long.valueOf((String)parameterObj);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 7:
/* 3420 */           parameterAsNum = Float.valueOf((String)parameterObj);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 6:
/*      */         case 8:
/* 3427 */           parameterAsNum = Double.valueOf((String)parameterObj);
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         default:
/* 3435 */           parameterAsNum = new BigDecimal((String)parameterObj);
/*      */           break;
/*      */       } 
/*      */     } else {
/* 3439 */       parameterAsNum = (Number)parameterObj;
/*      */     } 
/*      */     
/* 3442 */     switch (targetSqlType) {
/*      */       case -7:
/*      */       case -6:
/*      */       case 4:
/*      */       case 5:
/* 3447 */         setInt(parameterIndex, parameterAsNum.intValue());
/*      */         break;
/*      */ 
/*      */       
/*      */       case -5:
/* 3452 */         setLong(parameterIndex, parameterAsNum.longValue());
/*      */         break;
/*      */ 
/*      */       
/*      */       case 7:
/* 3457 */         setFloat(parameterIndex, parameterAsNum.floatValue());
/*      */         break;
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 8:
/* 3463 */         setDouble(parameterIndex, parameterAsNum.doubleValue());
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 2:
/*      */       case 3:
/* 3470 */         if (parameterAsNum instanceof BigDecimal) {
/* 3471 */           BigDecimal scaledBigDecimal = null;
/*      */           
/*      */           try {
/* 3474 */             scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale);
/*      */           }
/* 3476 */           catch (ArithmeticException ex) {
/*      */             try {
/* 3478 */               scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale, 4);
/*      */             
/*      */             }
/* 3481 */             catch (ArithmeticException arEx) {
/* 3482 */               throw SQLError.createSQLException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'", "S1009");
/*      */             } 
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3491 */           setBigDecimal(parameterIndex, scaledBigDecimal); break;
/* 3492 */         }  if (parameterAsNum instanceof BigInteger) {
/* 3493 */           setBigDecimal(parameterIndex, new BigDecimal((BigInteger)parameterAsNum, scale));
/*      */ 
/*      */           
/*      */           break;
/*      */         } 
/*      */         
/* 3499 */         setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
/*      */         break;
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
/*      */   public void setObject(int parameterIndex, Object parameterObj) throws SQLException {
/* 3521 */     if (parameterObj == null) {
/* 3522 */       setNull(parameterIndex, 1111);
/*      */     }
/* 3524 */     else if (parameterObj instanceof Byte) {
/* 3525 */       setInt(parameterIndex, ((Byte)parameterObj).intValue());
/* 3526 */     } else if (parameterObj instanceof String) {
/* 3527 */       setString(parameterIndex, (String)parameterObj);
/* 3528 */     } else if (parameterObj instanceof BigDecimal) {
/* 3529 */       setBigDecimal(parameterIndex, (BigDecimal)parameterObj);
/* 3530 */     } else if (parameterObj instanceof Short) {
/* 3531 */       setShort(parameterIndex, ((Short)parameterObj).shortValue());
/* 3532 */     } else if (parameterObj instanceof Integer) {
/* 3533 */       setInt(parameterIndex, ((Integer)parameterObj).intValue());
/* 3534 */     } else if (parameterObj instanceof Long) {
/* 3535 */       setLong(parameterIndex, ((Long)parameterObj).longValue());
/* 3536 */     } else if (parameterObj instanceof Float) {
/* 3537 */       setFloat(parameterIndex, ((Float)parameterObj).floatValue());
/* 3538 */     } else if (parameterObj instanceof Double) {
/* 3539 */       setDouble(parameterIndex, ((Double)parameterObj).doubleValue());
/* 3540 */     } else if (parameterObj instanceof byte[]) {
/* 3541 */       setBytes(parameterIndex, (byte[])parameterObj);
/* 3542 */     } else if (parameterObj instanceof Date) {
/* 3543 */       setDate(parameterIndex, (Date)parameterObj);
/* 3544 */     } else if (parameterObj instanceof Time) {
/* 3545 */       setTime(parameterIndex, (Time)parameterObj);
/* 3546 */     } else if (parameterObj instanceof Timestamp) {
/* 3547 */       setTimestamp(parameterIndex, (Timestamp)parameterObj);
/* 3548 */     } else if (parameterObj instanceof Boolean) {
/* 3549 */       setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */     }
/* 3551 */     else if (parameterObj instanceof InputStream) {
/* 3552 */       setBinaryStream(parameterIndex, (InputStream)parameterObj, -1);
/* 3553 */     } else if (parameterObj instanceof Blob) {
/* 3554 */       setBlob(parameterIndex, (Blob)parameterObj);
/* 3555 */     } else if (parameterObj instanceof Clob) {
/* 3556 */       setClob(parameterIndex, (Clob)parameterObj);
/* 3557 */     } else if (this.connection.getTreatUtilDateAsTimestamp() && parameterObj instanceof Date) {
/*      */       
/* 3559 */       setTimestamp(parameterIndex, new Timestamp(((Date)parameterObj).getTime()));
/*      */     }
/* 3561 */     else if (parameterObj instanceof BigInteger) {
/* 3562 */       setString(parameterIndex, parameterObj.toString());
/*      */     } else {
/* 3564 */       setSerializableObject(parameterIndex, parameterObj);
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
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType) throws SQLException {
/* 3585 */     if (!(parameterObj instanceof BigDecimal)) {
/* 3586 */       setObject(parameterIndex, parameterObj, targetSqlType, 0);
/*      */     } else {
/* 3588 */       setObject(parameterIndex, parameterObj, targetSqlType, ((BigDecimal)parameterObj).scale());
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
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
/* 3624 */     if (parameterObj == null) {
/* 3625 */       setNull(parameterIndex, 1111);
/*      */     } else {
/*      */       try {
/* 3628 */         Date parameterAsDate; switch (targetSqlType) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 16:
/* 3648 */             if (parameterObj instanceof Boolean) {
/* 3649 */               setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */             
/*      */             }
/* 3652 */             else if (parameterObj instanceof String) {
/* 3653 */               setBoolean(parameterIndex, ("true".equalsIgnoreCase((String)parameterObj) || !"0".equalsIgnoreCase((String)parameterObj)));
/*      */ 
/*      */             
/*      */             }
/* 3657 */             else if (parameterObj instanceof Number) {
/* 3658 */               int intValue = ((Number)parameterObj).intValue();
/*      */               
/* 3660 */               setBoolean(parameterIndex, (intValue != 0));
/*      */             }
/*      */             else {
/*      */               
/* 3664 */               throw SQLError.createSQLException("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible.", "S1009");
/*      */             } 
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case -7:
/*      */           case -6:
/*      */           case -5:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/* 3680 */             setNumericObject(parameterIndex, parameterObj, targetSqlType, scale);
/*      */             return;
/*      */ 
/*      */           
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/* 3687 */             if (parameterObj instanceof BigDecimal) {
/* 3688 */               setString(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString((BigDecimal)parameterObj)));
/*      */             
/*      */             }
/*      */             else {
/*      */ 
/*      */               
/* 3694 */               setString(parameterIndex, parameterObj.toString());
/*      */             } 
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case 2005:
/* 3701 */             if (parameterObj instanceof Clob) {
/* 3702 */               setClob(parameterIndex, (Clob)parameterObj);
/*      */             } else {
/* 3704 */               setString(parameterIndex, parameterObj.toString());
/*      */             } 
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case 2004:
/* 3714 */             if (parameterObj instanceof byte[]) {
/* 3715 */               setBytes(parameterIndex, (byte[])parameterObj);
/* 3716 */             } else if (parameterObj instanceof Blob) {
/* 3717 */               setBlob(parameterIndex, (Blob)parameterObj);
/*      */             } else {
/* 3719 */               setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode()));
/*      */             } 
/*      */             return;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 91:
/*      */           case 93:
/* 3733 */             if (parameterObj instanceof String) {
/* 3734 */               ParsePosition pp = new ParsePosition(0);
/* 3735 */               DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, false), Locale.US);
/*      */               
/* 3737 */               parameterAsDate = sdf.parse((String)parameterObj, pp);
/*      */             } else {
/* 3739 */               parameterAsDate = (Date)parameterObj;
/*      */             } 
/*      */             
/* 3742 */             switch (targetSqlType) {
/*      */               
/*      */               case 91:
/* 3745 */                 if (parameterAsDate instanceof Date) {
/* 3746 */                   setDate(parameterIndex, (Date)parameterAsDate);
/*      */                   break;
/*      */                 } 
/* 3749 */                 setDate(parameterIndex, new Date(parameterAsDate.getTime()));
/*      */                 break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               case 93:
/* 3757 */                 if (parameterAsDate instanceof Timestamp) {
/* 3758 */                   setTimestamp(parameterIndex, (Timestamp)parameterAsDate);
/*      */                   break;
/*      */                 } 
/* 3761 */                 setTimestamp(parameterIndex, new Timestamp(parameterAsDate.getTime()));
/*      */                 break;
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case 92:
/* 3773 */             if (parameterObj instanceof String) {
/* 3774 */               DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, true), Locale.US);
/*      */               
/* 3776 */               setTime(parameterIndex, new Time(sdf.parse((String)parameterObj).getTime()));
/*      */             }
/* 3778 */             else if (parameterObj instanceof Timestamp) {
/* 3779 */               Timestamp xT = (Timestamp)parameterObj;
/* 3780 */               setTime(parameterIndex, new Time(xT.getTime()));
/*      */             } else {
/* 3782 */               setTime(parameterIndex, (Time)parameterObj);
/*      */             } 
/*      */             return;
/*      */ 
/*      */           
/*      */           case 1111:
/* 3788 */             setSerializableObject(parameterIndex, parameterObj);
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 3793 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.16"), "S1000");
/*      */ 
/*      */       
/*      */       }
/* 3797 */       catch (Exception ex) {
/* 3798 */         if (ex instanceof SQLException) {
/* 3799 */           throw (SQLException)ex;
/*      */         }
/*      */         
/* 3802 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex.getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), "S1000");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3810 */         sqlEx.initCause(ex);
/*      */         
/* 3812 */         throw sqlEx;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int setOneBatchedParameterSet(PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
/* 3820 */     BatchParams paramArg = (BatchParams)paramSet;
/*      */     
/* 3822 */     boolean[] isNullBatch = paramArg.isNull;
/* 3823 */     boolean[] isStreamBatch = paramArg.isStream;
/*      */     
/* 3825 */     for (int j = 0; j < isNullBatch.length; j++) {
/* 3826 */       if (isNullBatch[j]) {
/* 3827 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 3829 */       else if (isStreamBatch[j]) {
/* 3830 */         batchedStatement.setBinaryStream(batchedParamIndex++, paramArg.parameterStreams[j], paramArg.streamLengths[j]);
/*      */       }
/*      */       else {
/*      */         
/* 3834 */         ((PreparedStatement)batchedStatement).setBytesNoEscapeNoQuotes(batchedParamIndex++, paramArg.parameterStrings[j]);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3841 */     return batchedParamIndex;
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
/*      */   public void setRef(int i, Ref x) throws SQLException {
/* 3858 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setResultSetConcurrency(int concurrencyFlag) {
/* 3868 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setResultSetType(int typeFlag) {
/* 3878 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setRetrieveGeneratedKeys(boolean retrieveGeneratedKeys) {
/* 3887 */     this.retrieveGeneratedKeys = retrieveGeneratedKeys;
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
/*      */   private final void setSerializableObject(int parameterIndex, Object parameterObj) throws SQLException {
/*      */     try {
/* 3907 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/* 3908 */       ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
/* 3909 */       objectOut.writeObject(parameterObj);
/* 3910 */       objectOut.flush();
/* 3911 */       objectOut.close();
/* 3912 */       bytesOut.flush();
/* 3913 */       bytesOut.close();
/*      */       
/* 3915 */       byte[] buf = bytesOut.toByteArray();
/* 3916 */       ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
/* 3917 */       setBinaryStream(parameterIndex, bytesIn, buf.length);
/* 3918 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 2000;
/* 3919 */     } catch (Exception ex) {
/* 3920 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.54") + ex.getClass().getName(), "S1009");
/*      */ 
/*      */       
/* 3923 */       sqlEx.initCause(ex);
/*      */       
/* 3925 */       throw sqlEx;
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
/*      */   public void setShort(int parameterIndex, short x) throws SQLException {
/* 3942 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 3944 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 5;
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
/*      */   public void setString(int parameterIndex, String x) throws SQLException {
/* 3962 */     if (x == null) {
/* 3963 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 3965 */       checkClosed();
/*      */       
/* 3967 */       int stringLength = x.length();
/*      */       
/* 3969 */       if (this.connection.isNoBackslashEscapesSet()) {
/*      */ 
/*      */         
/* 3972 */         boolean needsHexEscape = isEscapeNeededForString(x, stringLength);
/*      */ 
/*      */         
/* 3975 */         if (!needsHexEscape) {
/* 3976 */           byte[] arrayOfByte = null;
/*      */           
/* 3978 */           StringBuffer quotedString = new StringBuffer(x.length() + 2);
/* 3979 */           quotedString.append('\'');
/* 3980 */           quotedString.append(x);
/* 3981 */           quotedString.append('\'');
/*      */           
/* 3983 */           if (!this.isLoadDataQuery) {
/* 3984 */             arrayOfByte = StringUtils.getBytes(quotedString.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 3990 */             arrayOfByte = quotedString.toString().getBytes();
/*      */           } 
/*      */           
/* 3993 */           setInternal(parameterIndex, arrayOfByte);
/*      */         } else {
/* 3995 */           byte[] arrayOfByte = null;
/*      */           
/* 3997 */           if (!this.isLoadDataQuery) {
/* 3998 */             arrayOfByte = StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 4004 */             arrayOfByte = x.getBytes();
/*      */           } 
/*      */           
/* 4007 */           setBytes(parameterIndex, arrayOfByte);
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 4013 */       String parameterAsString = x;
/* 4014 */       boolean needsQuoted = true;
/*      */       
/* 4016 */       if (this.isLoadDataQuery || isEscapeNeededForString(x, stringLength)) {
/* 4017 */         needsQuoted = false;
/*      */         
/* 4019 */         StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D));
/*      */         
/* 4021 */         buf.append('\'');
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4030 */         for (int i = 0; i < stringLength; i++) {
/* 4031 */           char c = x.charAt(i);
/*      */           
/* 4033 */           switch (c) {
/*      */             case '\000':
/* 4035 */               buf.append('\\');
/* 4036 */               buf.append('0');
/*      */               break;
/*      */ 
/*      */             
/*      */             case '\n':
/* 4041 */               buf.append('\\');
/* 4042 */               buf.append('n');
/*      */               break;
/*      */ 
/*      */             
/*      */             case '\r':
/* 4047 */               buf.append('\\');
/* 4048 */               buf.append('r');
/*      */               break;
/*      */ 
/*      */             
/*      */             case '\\':
/* 4053 */               buf.append('\\');
/* 4054 */               buf.append('\\');
/*      */               break;
/*      */ 
/*      */             
/*      */             case '\'':
/* 4059 */               buf.append('\\');
/* 4060 */               buf.append('\'');
/*      */               break;
/*      */ 
/*      */             
/*      */             case '"':
/* 4065 */               if (this.usingAnsiMode) {
/* 4066 */                 buf.append('\\');
/*      */               }
/*      */               
/* 4069 */               buf.append('"');
/*      */               break;
/*      */ 
/*      */             
/*      */             case '\032':
/* 4074 */               buf.append('\\');
/* 4075 */               buf.append('Z');
/*      */               break;
/*      */ 
/*      */             
/*      */             default:
/* 4080 */               buf.append(c);
/*      */               break;
/*      */           } 
/*      */         } 
/* 4084 */         buf.append('\'');
/*      */         
/* 4086 */         parameterAsString = buf.toString();
/*      */       } 
/*      */       
/* 4089 */       byte[] parameterAsBytes = null;
/*      */       
/* 4091 */       if (!this.isLoadDataQuery) {
/* 4092 */         if (needsQuoted) {
/* 4093 */           parameterAsBytes = StringUtils.getBytesWrapped(parameterAsString, '\'', '\'', this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 4098 */           parameterAsBytes = StringUtils.getBytes(parameterAsString, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 4105 */         parameterAsBytes = parameterAsString.getBytes();
/*      */       } 
/*      */       
/* 4108 */       setInternal(parameterIndex, parameterAsBytes);
/*      */       
/* 4110 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 12;
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isEscapeNeededForString(String x, int stringLength) {
/* 4115 */     boolean needsHexEscape = false;
/*      */     
/* 4117 */     for (int i = 0; i < stringLength; i++) {
/* 4118 */       char c = x.charAt(i);
/*      */       
/* 4120 */       switch (c) {
/*      */         
/*      */         case '\000':
/* 4123 */           needsHexEscape = true;
/*      */           break;
/*      */         
/*      */         case '\n':
/* 4127 */           needsHexEscape = true;
/*      */           break;
/*      */ 
/*      */         
/*      */         case '\r':
/* 4132 */           needsHexEscape = true;
/*      */           break;
/*      */         
/*      */         case '\\':
/* 4136 */           needsHexEscape = true;
/*      */           break;
/*      */ 
/*      */         
/*      */         case '\'':
/* 4141 */           needsHexEscape = true;
/*      */           break;
/*      */ 
/*      */         
/*      */         case '"':
/* 4146 */           needsHexEscape = true;
/*      */           break;
/*      */ 
/*      */         
/*      */         case '\032':
/* 4151 */           needsHexEscape = true;
/*      */           break;
/*      */       } 
/*      */       
/* 4155 */       if (needsHexEscape) {
/*      */         break;
/*      */       }
/*      */     } 
/* 4159 */     return needsHexEscape;
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
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/* 4178 */     setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
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
/*      */   public void setTime(int parameterIndex, Time x) throws SQLException {
/* 4195 */     setTimeInternal(parameterIndex, x, (Calendar)null, Util.getDefaultTimeZone(), false);
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
/*      */   private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4216 */     if (x == null) {
/* 4217 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 4219 */       checkClosed();
/*      */       
/* 4221 */       if (!this.useLegacyDatetimeCode) {
/* 4222 */         newSetTimeInternal(parameterIndex, x, targetCalendar);
/*      */       } else {
/* 4224 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */         
/* 4226 */         synchronized (sessionCalendar) {
/* 4227 */           x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4234 */         setInternal(parameterIndex, "'" + x.toString() + "'");
/*      */       } 
/*      */       
/* 4237 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 92;
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
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/* 4257 */     setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
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
/* 4274 */     setTimestampInternal(parameterIndex, x, (Calendar)null, Util.getDefaultTimeZone(), false);
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
/*      */   private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4294 */     if (x == null) {
/* 4295 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 4297 */       checkClosed();
/*      */       
/* 4299 */       if (!this.useLegacyDatetimeCode) {
/* 4300 */         newSetTimestampInternal(parameterIndex, x, targetCalendar);
/*      */       } else {
/* 4302 */         String timestampString = null;
/*      */         
/* 4304 */         Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */         
/* 4308 */         synchronized (sessionCalendar) {
/* 4309 */           x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4316 */         if (this.connection.getUseSSPSCompatibleTimezoneShift()) {
/* 4317 */           doSSPSCompatibleTimezoneShift(parameterIndex, x, sessionCalendar);
/*      */         } else {
/* 4319 */           synchronized (this) {
/* 4320 */             if (this.tsdf == null) {
/* 4321 */               this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''", Locale.US);
/*      */             }
/*      */             
/* 4324 */             timestampString = this.tsdf.format(x);
/*      */             
/* 4326 */             setInternal(parameterIndex, timestampString);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 4332 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 93;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized void newSetTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar) throws SQLException {
/* 4338 */     if (this.tsdf == null) {
/* 4339 */       this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''", Locale.US);
/*      */     }
/*      */     
/* 4342 */     String timestampString = null;
/*      */     
/* 4344 */     if (targetCalendar != null) {
/* 4345 */       targetCalendar.setTime(x);
/* 4346 */       this.tsdf.setTimeZone(targetCalendar.getTimeZone());
/*      */       
/* 4348 */       timestampString = this.tsdf.format(x);
/*      */     } else {
/* 4350 */       this.tsdf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4351 */       timestampString = this.tsdf.format(x);
/*      */     } 
/*      */     
/* 4354 */     setInternal(parameterIndex, timestampString);
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized void newSetTimeInternal(int parameterIndex, Time x, Calendar targetCalendar) throws SQLException {
/* 4359 */     if (this.tdf == null) {
/* 4360 */       this.tdf = new SimpleDateFormat("''HH:mm:ss''", Locale.US);
/*      */     }
/*      */ 
/*      */     
/* 4364 */     String timeString = null;
/*      */     
/* 4366 */     if (targetCalendar != null) {
/* 4367 */       targetCalendar.setTime(x);
/* 4368 */       this.tdf.setTimeZone(targetCalendar.getTimeZone());
/*      */       
/* 4370 */       timeString = this.tdf.format(x);
/*      */     } else {
/* 4372 */       this.tdf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4373 */       timeString = this.tdf.format(x);
/*      */     } 
/*      */     
/* 4376 */     setInternal(parameterIndex, timeString);
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized void newSetDateInternal(int parameterIndex, Date x, Calendar targetCalendar) throws SQLException {
/* 4381 */     if (this.ddf == null) {
/* 4382 */       this.ddf = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */     }
/*      */ 
/*      */     
/* 4386 */     String timeString = null;
/*      */     
/* 4388 */     if (targetCalendar != null) {
/* 4389 */       targetCalendar.setTime(x);
/* 4390 */       this.ddf.setTimeZone(targetCalendar.getTimeZone());
/*      */       
/* 4392 */       timeString = this.ddf.format(x);
/*      */     } else {
/* 4394 */       this.ddf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4395 */       timeString = this.ddf.format(x);
/*      */     } 
/*      */     
/* 4398 */     setInternal(parameterIndex, timeString);
/*      */   }
/*      */   
/*      */   private void doSSPSCompatibleTimezoneShift(int parameterIndex, Timestamp x, Calendar sessionCalendar) throws SQLException {
/* 4402 */     Calendar sessionCalendar2 = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4407 */     synchronized (sessionCalendar2) {
/* 4408 */       Date oldTime = sessionCalendar2.getTime();
/*      */       
/*      */       try {
/* 4411 */         sessionCalendar2.setTime(x);
/*      */         
/* 4413 */         int year = sessionCalendar2.get(1);
/* 4414 */         int month = sessionCalendar2.get(2) + 1;
/* 4415 */         int date = sessionCalendar2.get(5);
/*      */         
/* 4417 */         int hour = sessionCalendar2.get(11);
/* 4418 */         int minute = sessionCalendar2.get(12);
/* 4419 */         int seconds = sessionCalendar2.get(13);
/*      */         
/* 4421 */         StringBuffer tsBuf = new StringBuffer();
/*      */         
/* 4423 */         tsBuf.append('\'');
/* 4424 */         tsBuf.append(year);
/*      */         
/* 4426 */         tsBuf.append("-");
/*      */         
/* 4428 */         if (month < 10) {
/* 4429 */           tsBuf.append('0');
/*      */         }
/*      */         
/* 4432 */         tsBuf.append(month);
/*      */         
/* 4434 */         tsBuf.append('-');
/*      */         
/* 4436 */         if (date < 10) {
/* 4437 */           tsBuf.append('0');
/*      */         }
/*      */         
/* 4440 */         tsBuf.append(date);
/*      */         
/* 4442 */         tsBuf.append(' ');
/*      */         
/* 4444 */         if (hour < 10) {
/* 4445 */           tsBuf.append('0');
/*      */         }
/*      */         
/* 4448 */         tsBuf.append(hour);
/*      */         
/* 4450 */         tsBuf.append(':');
/*      */         
/* 4452 */         if (minute < 10) {
/* 4453 */           tsBuf.append('0');
/*      */         }
/*      */         
/* 4456 */         tsBuf.append(minute);
/*      */         
/* 4458 */         tsBuf.append(':');
/*      */         
/* 4460 */         if (seconds < 10) {
/* 4461 */           tsBuf.append('0');
/*      */         }
/*      */         
/* 4464 */         tsBuf.append(seconds);
/*      */         
/* 4466 */         tsBuf.append('\'');
/*      */         
/* 4468 */         setInternal(parameterIndex, tsBuf.toString());
/*      */       } finally {
/*      */         
/* 4471 */         sessionCalendar.setTime(oldTime);
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
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 4502 */     if (x == null) {
/* 4503 */       setNull(parameterIndex, 12);
/*      */     } else {
/* 4505 */       setBinaryStream(parameterIndex, x, length);
/*      */       
/* 4507 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 2005;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURL(int parameterIndex, URL arg) throws SQLException {
/* 4515 */     if (arg != null) {
/* 4516 */       setString(parameterIndex, arg.toString());
/*      */       
/* 4518 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 70;
/*      */     } else {
/* 4520 */       setNull(parameterIndex, 1);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void streamToBytes(Buffer packet, InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException {
/*      */     try {
/* 4528 */       String connectionEncoding = this.connection.getEncoding();
/*      */       
/* 4530 */       boolean hexEscape = false;
/*      */       
/* 4532 */       if (this.connection.isNoBackslashEscapesSet() || (this.connection.getUseUnicode() && connectionEncoding != null && CharsetMapping.isMultibyteCharset(connectionEncoding) && !this.connection.parserKnowsUnicode()))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 4537 */         hexEscape = true;
/*      */       }
/*      */       
/* 4540 */       if (streamLength == -1) {
/* 4541 */         useLength = false;
/*      */       }
/*      */       
/* 4544 */       int bc = -1;
/*      */       
/* 4546 */       if (useLength) {
/* 4547 */         bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */       } else {
/* 4549 */         bc = readblock(in, this.streamConvertBuf);
/*      */       } 
/*      */       
/* 4552 */       int lengthLeftToRead = streamLength - bc;
/*      */       
/* 4554 */       if (hexEscape) {
/* 4555 */         packet.writeStringNoNull("x");
/* 4556 */       } else if (this.connection.getIO().versionMeetsMinimum(4, 1, 0)) {
/* 4557 */         packet.writeStringNoNull("_binary");
/*      */       } 
/*      */       
/* 4560 */       if (escape) {
/* 4561 */         packet.writeByte((byte)39);
/*      */       }
/*      */       
/* 4564 */       while (bc > 0) {
/* 4565 */         if (hexEscape) {
/* 4566 */           hexEscapeBlock(this.streamConvertBuf, packet, bc);
/* 4567 */         } else if (escape) {
/* 4568 */           escapeblockFast(this.streamConvertBuf, packet, bc);
/*      */         } else {
/* 4570 */           packet.writeBytesNoNull(this.streamConvertBuf, 0, bc);
/*      */         } 
/*      */         
/* 4573 */         if (useLength) {
/* 4574 */           bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */           
/* 4576 */           if (bc > 0)
/* 4577 */             lengthLeftToRead -= bc; 
/*      */           continue;
/*      */         } 
/* 4580 */         bc = readblock(in, this.streamConvertBuf);
/*      */       } 
/*      */ 
/*      */       
/* 4584 */       if (escape) {
/* 4585 */         packet.writeByte((byte)39);
/*      */       }
/*      */     } finally {
/* 4588 */       if (this.connection.getAutoClosePStmtStreams()) {
/*      */         try {
/* 4590 */           in.close();
/* 4591 */         } catch (IOException ioEx) {}
/*      */ 
/*      */ 
/*      */         
/* 4595 */         in = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final byte[] streamToBytes(InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException {
/*      */     try {
/* 4603 */       if (streamLength == -1) {
/* 4604 */         useLength = false;
/*      */       }
/*      */       
/* 4607 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */       
/* 4609 */       int bc = -1;
/*      */       
/* 4611 */       if (useLength) {
/* 4612 */         bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */       } else {
/* 4614 */         bc = readblock(in, this.streamConvertBuf);
/*      */       } 
/*      */       
/* 4617 */       int lengthLeftToRead = streamLength - bc;
/*      */       
/* 4619 */       if (escape) {
/* 4620 */         if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 4621 */           bytesOut.write(95);
/* 4622 */           bytesOut.write(98);
/* 4623 */           bytesOut.write(105);
/* 4624 */           bytesOut.write(110);
/* 4625 */           bytesOut.write(97);
/* 4626 */           bytesOut.write(114);
/* 4627 */           bytesOut.write(121);
/*      */         } 
/*      */         
/* 4630 */         bytesOut.write(39);
/*      */       } 
/*      */       
/* 4633 */       while (bc > 0) {
/* 4634 */         if (escape) {
/* 4635 */           escapeblockFast(this.streamConvertBuf, bytesOut, bc);
/*      */         } else {
/* 4637 */           bytesOut.write(this.streamConvertBuf, 0, bc);
/*      */         } 
/*      */         
/* 4640 */         if (useLength) {
/* 4641 */           bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */           
/* 4643 */           if (bc > 0)
/* 4644 */             lengthLeftToRead -= bc; 
/*      */           continue;
/*      */         } 
/* 4647 */         bc = readblock(in, this.streamConvertBuf);
/*      */       } 
/*      */ 
/*      */       
/* 4651 */       if (escape) {
/* 4652 */         bytesOut.write(39);
/*      */       }
/*      */       
/* 4655 */       return bytesOut.toByteArray();
/*      */     } finally {
/* 4657 */       if (this.connection.getAutoClosePStmtStreams()) {
/*      */         try {
/* 4659 */           in.close();
/* 4660 */         } catch (IOException ioEx) {}
/*      */ 
/*      */ 
/*      */         
/* 4664 */         in = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 4675 */     StringBuffer buf = new StringBuffer();
/* 4676 */     buf.append(super.toString());
/* 4677 */     buf.append(": ");
/*      */     
/*      */     try {
/* 4680 */       buf.append(asSql());
/* 4681 */     } catch (SQLException sqlEx) {
/* 4682 */       buf.append("EXCEPTION: " + sqlEx.toString());
/*      */     } 
/*      */     
/* 4685 */     return buf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isClosed() throws SQLException {
/* 4691 */     return this.isClosed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getParameterIndexOffset() {
/* 4702 */     return 0;
/*      */   }
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/* 4706 */     setAsciiStream(parameterIndex, x, -1);
/*      */   }
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 4710 */     setAsciiStream(parameterIndex, x, (int)length);
/* 4711 */     this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 2005;
/*      */   }
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/* 4715 */     setBinaryStream(parameterIndex, x, -1);
/*      */   }
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 4719 */     setBinaryStream(parameterIndex, x, (int)length);
/*      */   }
/*      */   
/*      */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/* 4723 */     setBinaryStream(parameterIndex, inputStream);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/* 4727 */     setCharacterStream(parameterIndex, reader, -1);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/* 4731 */     setCharacterStream(parameterIndex, reader, (int)length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(int parameterIndex, Reader reader) throws SQLException {
/* 4736 */     setCharacterStream(parameterIndex, reader);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/* 4742 */     setCharacterStream(parameterIndex, reader, length);
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/* 4746 */     setNCharacterStream(parameterIndex, value, -1L);
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
/*      */   public void setNString(int parameterIndex, String x) throws SQLException {
/* 4764 */     if (this.charEncoding.equalsIgnoreCase("UTF-8") || this.charEncoding.equalsIgnoreCase("utf8")) {
/*      */       
/* 4766 */       setString(parameterIndex, x);
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 4771 */     if (x == null) {
/* 4772 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 4774 */       int stringLength = x.length();
/*      */ 
/*      */ 
/*      */       
/* 4778 */       StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D + 4.0D));
/* 4779 */       buf.append("_utf8");
/* 4780 */       buf.append('\'');
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4789 */       for (int i = 0; i < stringLength; i++) {
/* 4790 */         char c = x.charAt(i);
/*      */         
/* 4792 */         switch (c) {
/*      */           case '\000':
/* 4794 */             buf.append('\\');
/* 4795 */             buf.append('0');
/*      */             break;
/*      */ 
/*      */           
/*      */           case '\n':
/* 4800 */             buf.append('\\');
/* 4801 */             buf.append('n');
/*      */             break;
/*      */ 
/*      */           
/*      */           case '\r':
/* 4806 */             buf.append('\\');
/* 4807 */             buf.append('r');
/*      */             break;
/*      */ 
/*      */           
/*      */           case '\\':
/* 4812 */             buf.append('\\');
/* 4813 */             buf.append('\\');
/*      */             break;
/*      */ 
/*      */           
/*      */           case '\'':
/* 4818 */             buf.append('\\');
/* 4819 */             buf.append('\'');
/*      */             break;
/*      */ 
/*      */           
/*      */           case '"':
/* 4824 */             if (this.usingAnsiMode) {
/* 4825 */               buf.append('\\');
/*      */             }
/*      */             
/* 4828 */             buf.append('"');
/*      */             break;
/*      */ 
/*      */           
/*      */           case '\032':
/* 4833 */             buf.append('\\');
/* 4834 */             buf.append('Z');
/*      */             break;
/*      */ 
/*      */           
/*      */           default:
/* 4839 */             buf.append(c);
/*      */             break;
/*      */         } 
/*      */       } 
/* 4843 */       buf.append('\'');
/*      */       
/* 4845 */       String parameterAsString = buf.toString();
/*      */       
/* 4847 */       byte[] parameterAsBytes = null;
/*      */       
/* 4849 */       if (!this.isLoadDataQuery) {
/* 4850 */         parameterAsBytes = StringUtils.getBytes(parameterAsString, this.connection.getCharsetConverter("UTF-8"), "UTF-8", this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/* 4856 */         parameterAsBytes = parameterAsString.getBytes();
/*      */       } 
/*      */       
/* 4859 */       setInternal(parameterIndex, parameterAsBytes);
/*      */       
/* 4861 */       this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = -9;
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
/*      */   public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     try {
/* 4890 */       if (reader == null) {
/* 4891 */         setNull(parameterIndex, -1);
/*      */       } else {
/*      */         
/* 4894 */         char[] c = null;
/* 4895 */         int len = 0;
/*      */         
/* 4897 */         boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4902 */         if (useLength && length != -1L) {
/* 4903 */           c = new char[(int)length];
/*      */           
/* 4905 */           int numCharsRead = readFully(reader, c, (int)length);
/*      */ 
/*      */ 
/*      */           
/* 4909 */           setNString(parameterIndex, new String(c, 0, numCharsRead));
/*      */         } else {
/*      */           
/* 4912 */           c = new char[4096];
/*      */           
/* 4914 */           StringBuffer buf = new StringBuffer();
/*      */           
/* 4916 */           while ((len = reader.read(c)) != -1) {
/* 4917 */             buf.append(c, 0, len);
/*      */           }
/*      */           
/* 4920 */           setNString(parameterIndex, buf.toString());
/*      */         } 
/*      */         
/* 4923 */         this.parameterTypes[parameterIndex - 1 + getParameterIndexOffset()] = 2011;
/*      */       } 
/* 4925 */     } catch (IOException ioEx) {
/* 4926 */       throw SQLError.createSQLException(ioEx.toString(), "S1000");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/* 4932 */     setNCharacterStream(parameterIndex, reader);
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
/*      */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/* 4950 */     if (reader == null) {
/* 4951 */       setNull(parameterIndex, -1);
/*      */     } else {
/* 4953 */       setNCharacterStream(parameterIndex, reader, length);
/*      */     } 
/*      */   }
/*      */   
/*      */   public ParameterBindings getParameterBindings() throws SQLException {
/* 4958 */     return new EmulatedPreparedStatementBindings(this);
/*      */   }
/*      */   
/*      */   class EmulatedPreparedStatementBindings implements ParameterBindings { private ResultSetImpl bindingsAsRs;
/*      */     private boolean[] parameterIsNull;
/*      */     private final PreparedStatement this$0;
/*      */     
/*      */     public EmulatedPreparedStatementBindings(PreparedStatement this$0) throws SQLException {
/* 4966 */       this.this$0 = this$0;
/* 4967 */       List rows = new ArrayList();
/* 4968 */       this.parameterIsNull = new boolean[this$0.parameterCount];
/* 4969 */       System.arraycopy(this$0.isNull, 0, this.parameterIsNull, 0, this$0.parameterCount);
/*      */ 
/*      */       
/* 4972 */       byte[][] rowData = new byte[this$0.parameterCount][];
/* 4973 */       Field[] typeMetadata = new Field[this$0.parameterCount];
/*      */       
/* 4975 */       for (int i = 0; i < this$0.parameterCount; i++) {
/* 4976 */         rowData[i] = this$0.getBytesRepresentation(i);
/*      */         
/* 4978 */         int charsetIndex = 0;
/*      */         
/* 4980 */         if (this$0.parameterTypes[i] == -2 || this$0.parameterTypes[i] == 2004) {
/*      */           
/* 4982 */           charsetIndex = 63;
/*      */         } else {
/* 4984 */           String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(this$0.connection.getEncoding(), this$0.connection);
/*      */ 
/*      */           
/* 4987 */           charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(mysqlEncodingName);
/*      */         } 
/*      */ 
/*      */         
/* 4991 */         Field parameterMetadata = new Field(null, "parameter_" + (i + 1), charsetIndex, this$0.parameterTypes[i], (rowData[i]).length);
/*      */ 
/*      */         
/* 4994 */         parameterMetadata.setConnection(this$0.connection);
/* 4995 */         typeMetadata[i] = parameterMetadata;
/*      */       } 
/*      */       
/* 4998 */       rows.add(new ByteArrayRow(rowData));
/*      */       
/* 5000 */       this.bindingsAsRs = new ResultSetImpl(this$0.connection.getCatalog(), typeMetadata, new RowDataStatic(rows), this$0.connection, null);
/*      */       
/* 5002 */       this.bindingsAsRs.next();
/*      */     }
/*      */     
/*      */     public Array getArray(int parameterIndex) throws SQLException {
/* 5006 */       return this.bindingsAsRs.getArray(parameterIndex);
/*      */     }
/*      */ 
/*      */     
/*      */     public InputStream getAsciiStream(int parameterIndex) throws SQLException {
/* 5011 */       return this.bindingsAsRs.getAsciiStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/* 5015 */       return this.bindingsAsRs.getBigDecimal(parameterIndex);
/*      */     }
/*      */ 
/*      */     
/*      */     public InputStream getBinaryStream(int parameterIndex) throws SQLException {
/* 5020 */       return this.bindingsAsRs.getBinaryStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Blob getBlob(int parameterIndex) throws SQLException {
/* 5024 */       return this.bindingsAsRs.getBlob(parameterIndex);
/*      */     }
/*      */     
/*      */     public boolean getBoolean(int parameterIndex) throws SQLException {
/* 5028 */       return this.bindingsAsRs.getBoolean(parameterIndex);
/*      */     }
/*      */     
/*      */     public byte getByte(int parameterIndex) throws SQLException {
/* 5032 */       return this.bindingsAsRs.getByte(parameterIndex);
/*      */     }
/*      */     
/*      */     public byte[] getBytes(int parameterIndex) throws SQLException {
/* 5036 */       return this.bindingsAsRs.getBytes(parameterIndex);
/*      */     }
/*      */ 
/*      */     
/*      */     public Reader getCharacterStream(int parameterIndex) throws SQLException {
/* 5041 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Clob getClob(int parameterIndex) throws SQLException {
/* 5045 */       return this.bindingsAsRs.getClob(parameterIndex);
/*      */     }
/*      */     
/*      */     public Date getDate(int parameterIndex) throws SQLException {
/* 5049 */       return this.bindingsAsRs.getDate(parameterIndex);
/*      */     }
/*      */     
/*      */     public double getDouble(int parameterIndex) throws SQLException {
/* 5053 */       return this.bindingsAsRs.getDouble(parameterIndex);
/*      */     }
/*      */     
/*      */     public float getFloat(int parameterIndex) throws SQLException {
/* 5057 */       return this.bindingsAsRs.getFloat(parameterIndex);
/*      */     }
/*      */     
/*      */     public int getInt(int parameterIndex) throws SQLException {
/* 5061 */       return this.bindingsAsRs.getInt(parameterIndex);
/*      */     }
/*      */     
/*      */     public long getLong(int parameterIndex) throws SQLException {
/* 5065 */       return this.bindingsAsRs.getLong(parameterIndex);
/*      */     }
/*      */ 
/*      */     
/*      */     public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/* 5070 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Reader getNClob(int parameterIndex) throws SQLException {
/* 5074 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Object getObject(int parameterIndex) throws SQLException {
/* 5078 */       this.this$0.checkBounds(parameterIndex, 0);
/*      */       
/* 5080 */       if (this.parameterIsNull[parameterIndex - 1]) {
/* 5081 */         return null;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5088 */       switch (this.this$0.parameterTypes[parameterIndex - 1]) {
/*      */         case -6:
/* 5090 */           return new Byte(getByte(parameterIndex));
/*      */         case 5:
/* 5092 */           return new Short(getShort(parameterIndex));
/*      */         case 4:
/* 5094 */           return new Integer(getInt(parameterIndex));
/*      */         case -5:
/* 5096 */           return new Long(getLong(parameterIndex));
/*      */         case 6:
/* 5098 */           return new Float(getFloat(parameterIndex));
/*      */         case 8:
/* 5100 */           return new Double(getDouble(parameterIndex));
/*      */       } 
/* 5102 */       return this.bindingsAsRs.getObject(parameterIndex);
/*      */     }
/*      */ 
/*      */     
/*      */     public Ref getRef(int parameterIndex) throws SQLException {
/* 5107 */       return this.bindingsAsRs.getRef(parameterIndex);
/*      */     }
/*      */     
/*      */     public short getShort(int parameterIndex) throws SQLException {
/* 5111 */       return this.bindingsAsRs.getShort(parameterIndex);
/*      */     }
/*      */     
/*      */     public String getString(int parameterIndex) throws SQLException {
/* 5115 */       return this.bindingsAsRs.getString(parameterIndex);
/*      */     }
/*      */     
/*      */     public Time getTime(int parameterIndex) throws SQLException {
/* 5119 */       return this.bindingsAsRs.getTime(parameterIndex);
/*      */     }
/*      */     
/*      */     public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/* 5123 */       return this.bindingsAsRs.getTimestamp(parameterIndex);
/*      */     }
/*      */     
/*      */     public URL getURL(int parameterIndex) throws SQLException {
/* 5127 */       return this.bindingsAsRs.getURL(parameterIndex);
/*      */     }
/*      */     
/*      */     public boolean isNull(int parameterIndex) throws SQLException {
/* 5131 */       this.this$0.checkBounds(parameterIndex, 0);
/*      */       
/* 5133 */       return this.parameterIsNull[parameterIndex - 1];
/*      */     } }
/*      */ 
/*      */   
/*      */   public String getPreparedSql() {
/* 5138 */     return this.originalSql;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\PreparedStatement.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */