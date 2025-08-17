/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
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
/*      */ public class UpdatableResultSet
/*      */   extends ResultSetImpl
/*      */ {
/*   46 */   protected static final byte[] STREAM_DATA_MARKER = "** STREAM DATA **".getBytes();
/*      */ 
/*      */   
/*      */   protected SingleByteCharsetConverter charConverter;
/*      */ 
/*      */   
/*      */   private String charEncoding;
/*      */ 
/*      */   
/*      */   private byte[][] defaultColumnValue;
/*      */   
/*   57 */   private PreparedStatement deleter = null;
/*      */   
/*   59 */   private String deleteSQL = null;
/*      */ 
/*      */   
/*      */   private boolean initializedCharConverter = false;
/*      */   
/*   64 */   protected PreparedStatement inserter = null;
/*      */   
/*   66 */   private String insertSQL = null;
/*      */ 
/*      */   
/*      */   private boolean isUpdatable = false;
/*      */ 
/*      */   
/*   72 */   private String notUpdatableReason = null;
/*      */ 
/*      */   
/*   75 */   private List primaryKeyIndicies = null;
/*      */   
/*      */   private String qualifiedAndQuotedTableName;
/*      */   
/*   79 */   private String quotedIdChar = null;
/*      */ 
/*      */   
/*      */   private PreparedStatement refresher;
/*      */   
/*   84 */   private String refreshSQL = null;
/*      */ 
/*      */   
/*      */   private ResultSetRow savedCurrentRow;
/*      */ 
/*      */   
/*   90 */   protected PreparedStatement updater = null;
/*      */ 
/*      */   
/*   93 */   private String updateSQL = null;
/*      */   
/*      */   private boolean populateInserterWithDefaultValues = false;
/*      */   
/*   97 */   private Map databasesUsedToTablesUsed = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected UpdatableResultSet(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt) throws SQLException {
/*  118 */     super(catalog, fields, tuples, conn, creatorStmt);
/*  119 */     checkUpdatability();
/*  120 */     this.populateInserterWithDefaultValues = this.connection.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean absolute(int row) throws SQLException {
/*  163 */     return super.absolute(row);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void afterLast() throws SQLException {
/*  179 */     super.afterLast();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void beforeFirst() throws SQLException {
/*  195 */     super.beforeFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void cancelRowUpdates() throws SQLException {
/*  209 */     checkClosed();
/*      */     
/*  211 */     if (this.doingUpdates) {
/*  212 */       this.doingUpdates = false;
/*  213 */       this.updater.clearParameters();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkRowPos() throws SQLException {
/*  223 */     checkClosed();
/*      */     
/*  225 */     if (!this.onInsertRow) {
/*  226 */       super.checkRowPos();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkUpdatability() throws SQLException {
/*  237 */     if (this.fields == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  247 */     String singleTableName = null;
/*  248 */     String catalogName = null;
/*      */     
/*  250 */     int primaryKeyCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  257 */     if (this.catalog == null || this.catalog.length() == 0) {
/*  258 */       this.catalog = this.fields[0].getDatabaseName();
/*      */       
/*  260 */       if (this.catalog == null || this.catalog.length() == 0) {
/*  261 */         throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), "S1009");
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  267 */     if (this.fields.length > 0) {
/*  268 */       singleTableName = this.fields[0].getOriginalTableName();
/*  269 */       catalogName = this.fields[0].getDatabaseName();
/*      */       
/*  271 */       if (singleTableName == null) {
/*  272 */         singleTableName = this.fields[0].getTableName();
/*  273 */         catalogName = this.catalog;
/*      */       } 
/*      */       
/*  276 */       if (singleTableName != null && singleTableName.length() == 0) {
/*  277 */         this.isUpdatable = false;
/*  278 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*  283 */       if (this.fields[0].isPrimaryKey()) {
/*  284 */         primaryKeyCount++;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  290 */       for (int i = 1; i < this.fields.length; i++) {
/*  291 */         String otherTableName = this.fields[i].getOriginalTableName();
/*  292 */         String otherCatalogName = this.fields[i].getDatabaseName();
/*      */         
/*  294 */         if (otherTableName == null) {
/*  295 */           otherTableName = this.fields[i].getTableName();
/*  296 */           otherCatalogName = this.catalog;
/*      */         } 
/*      */         
/*  299 */         if (otherTableName != null && otherTableName.length() == 0) {
/*  300 */           this.isUpdatable = false;
/*  301 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/*  306 */         if (singleTableName == null || !otherTableName.equals(singleTableName)) {
/*      */           
/*  308 */           this.isUpdatable = false;
/*  309 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
/*      */ 
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/*  315 */         if (catalogName == null || !otherCatalogName.equals(catalogName)) {
/*      */           
/*  317 */           this.isUpdatable = false;
/*  318 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/*  323 */         if (this.fields[i].isPrimaryKey()) {
/*  324 */           primaryKeyCount++;
/*      */         }
/*      */       } 
/*      */       
/*  328 */       if (singleTableName == null || singleTableName.length() == 0) {
/*  329 */         this.isUpdatable = false;
/*  330 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.2");
/*      */         
/*      */         return;
/*      */       } 
/*      */     } else {
/*  335 */       this.isUpdatable = false;
/*  336 */       this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  341 */     if (this.connection.getStrictUpdates()) {
/*  342 */       DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */       
/*  344 */       ResultSet rs = null;
/*  345 */       HashMap primaryKeyNames = new HashMap();
/*      */       
/*      */       try {
/*  348 */         rs = dbmd.getPrimaryKeys(catalogName, null, singleTableName);
/*      */         
/*  350 */         while (rs.next()) {
/*  351 */           String keyName = rs.getString(4);
/*  352 */           keyName = keyName.toUpperCase();
/*  353 */           primaryKeyNames.put(keyName, keyName);
/*      */         } 
/*      */       } finally {
/*  356 */         if (rs != null) {
/*      */           try {
/*  358 */             rs.close();
/*  359 */           } catch (Exception ex) {
/*  360 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           } 
/*      */           
/*  363 */           rs = null;
/*      */         } 
/*      */       } 
/*      */       
/*  367 */       int existingPrimaryKeysCount = primaryKeyNames.size();
/*      */       
/*  369 */       if (existingPrimaryKeysCount == 0) {
/*  370 */         this.isUpdatable = false;
/*  371 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
/*      */ 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */ 
/*      */       
/*  379 */       for (int i = 0; i < this.fields.length; i++) {
/*  380 */         if (this.fields[i].isPrimaryKey()) {
/*  381 */           String columnNameUC = this.fields[i].getName().toUpperCase();
/*      */ 
/*      */           
/*  384 */           if (primaryKeyNames.remove(columnNameUC) == null) {
/*      */             
/*  386 */             String originalName = this.fields[i].getOriginalName();
/*      */             
/*  388 */             if (originalName != null && 
/*  389 */               primaryKeyNames.remove(originalName.toUpperCase()) == null) {
/*      */ 
/*      */               
/*  392 */               this.isUpdatable = false;
/*  393 */               this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[] { originalName });
/*      */ 
/*      */               
/*      */               return;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  403 */       this.isUpdatable = primaryKeyNames.isEmpty();
/*      */       
/*  405 */       if (!this.isUpdatable) {
/*  406 */         if (existingPrimaryKeysCount > 1) {
/*  407 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.7");
/*      */         } else {
/*  409 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  419 */     if (primaryKeyCount == 0) {
/*  420 */       this.isUpdatable = false;
/*  421 */       this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  426 */     this.isUpdatable = true;
/*  427 */     this.notUpdatableReason = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void deleteRow() throws SQLException {
/*  443 */     checkClosed();
/*      */     
/*  445 */     if (!this.isUpdatable) {
/*  446 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/*  449 */     if (this.onInsertRow)
/*  450 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1")); 
/*  451 */     if (this.rowData.size() == 0)
/*  452 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2")); 
/*  453 */     if (isBeforeFirst())
/*  454 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3")); 
/*  455 */     if (isAfterLast()) {
/*  456 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"));
/*      */     }
/*      */     
/*  459 */     if (this.deleter == null) {
/*  460 */       if (this.deleteSQL == null) {
/*  461 */         generateStatements();
/*      */       }
/*      */       
/*  464 */       this.deleter = (PreparedStatement)this.connection.clientPrepareStatement(this.deleteSQL);
/*      */     } 
/*      */ 
/*      */     
/*  468 */     this.deleter.clearParameters();
/*      */     
/*  470 */     String characterEncoding = null;
/*      */     
/*  472 */     if (this.connection.getUseUnicode()) {
/*  473 */       characterEncoding = this.connection.getEncoding();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  480 */       int numKeys = this.primaryKeyIndicies.size();
/*      */       
/*  482 */       if (numKeys == 1) {
/*  483 */         int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */         
/*  485 */         String currentVal = (characterEncoding == null) ? new String(this.thisRow.getColumnValue(index)) : new String(this.thisRow.getColumnValue(index), characterEncoding);
/*      */ 
/*      */         
/*  488 */         this.deleter.setString(1, currentVal);
/*      */       } else {
/*  490 */         for (int i = 0; i < numKeys; i++) {
/*  491 */           int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */           
/*  493 */           String currentVal = (characterEncoding == null) ? new String(this.thisRow.getColumnValue(index)) : new String(this.thisRow.getColumnValue(index), characterEncoding);
/*      */ 
/*      */ 
/*      */           
/*  497 */           this.deleter.setString(i + 1, currentVal);
/*      */         } 
/*      */       } 
/*      */       
/*  501 */       this.deleter.executeUpdate();
/*  502 */       this.rowData.removeRow(this.rowData.getCurrentRowNumber());
/*  503 */     } catch (UnsupportedEncodingException encodingEx) {
/*  504 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.39", new Object[] { this.charEncoding }), "S1009");
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
/*      */   private synchronized void extractDefaultValues() throws SQLException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield connection : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   4: invokevirtual getMetaData : ()Ljava/sql/DatabaseMetaData;
/*      */     //   7: astore_1
/*      */     //   8: aload_0
/*      */     //   9: aload_0
/*      */     //   10: getfield fields : [Lcom/mysql/jdbc/Field;
/*      */     //   13: arraylength
/*      */     //   14: anewarray [B
/*      */     //   17: putfield defaultColumnValue : [[B
/*      */     //   20: aconst_null
/*      */     //   21: astore_2
/*      */     //   22: aload_0
/*      */     //   23: getfield databasesUsedToTablesUsed : Ljava/util/Map;
/*      */     //   26: invokeinterface entrySet : ()Ljava/util/Set;
/*      */     //   31: invokeinterface iterator : ()Ljava/util/Iterator;
/*      */     //   36: astore_3
/*      */     //   37: aload_3
/*      */     //   38: invokeinterface hasNext : ()Z
/*      */     //   43: ifeq -> 259
/*      */     //   46: aload_3
/*      */     //   47: invokeinterface next : ()Ljava/lang/Object;
/*      */     //   52: checkcast java/util/Map$Entry
/*      */     //   55: astore #4
/*      */     //   57: aload #4
/*      */     //   59: invokeinterface getKey : ()Ljava/lang/Object;
/*      */     //   64: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   67: astore #5
/*      */     //   69: aload #4
/*      */     //   71: invokeinterface getValue : ()Ljava/lang/Object;
/*      */     //   76: checkcast java/util/Map
/*      */     //   79: invokeinterface entrySet : ()Ljava/util/Set;
/*      */     //   84: invokeinterface iterator : ()Ljava/util/Iterator;
/*      */     //   89: astore #6
/*      */     //   91: aload #6
/*      */     //   93: invokeinterface hasNext : ()Z
/*      */     //   98: ifeq -> 256
/*      */     //   101: aload #6
/*      */     //   103: invokeinterface next : ()Ljava/lang/Object;
/*      */     //   108: checkcast java/util/Map$Entry
/*      */     //   111: astore #7
/*      */     //   113: aload #7
/*      */     //   115: invokeinterface getKey : ()Ljava/lang/Object;
/*      */     //   120: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   123: astore #8
/*      */     //   125: aload #7
/*      */     //   127: invokeinterface getValue : ()Ljava/lang/Object;
/*      */     //   132: checkcast java/util/Map
/*      */     //   135: astore #9
/*      */     //   137: aload_1
/*      */     //   138: aload_0
/*      */     //   139: getfield catalog : Ljava/lang/String;
/*      */     //   142: aconst_null
/*      */     //   143: aload #8
/*      */     //   145: ldc '%'
/*      */     //   147: invokeinterface getColumns : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */     //   152: astore_2
/*      */     //   153: aload_2
/*      */     //   154: invokeinterface next : ()Z
/*      */     //   159: ifeq -> 223
/*      */     //   162: aload_2
/*      */     //   163: ldc 'COLUMN_NAME'
/*      */     //   165: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   170: astore #10
/*      */     //   172: aload_2
/*      */     //   173: ldc 'COLUMN_DEF'
/*      */     //   175: invokeinterface getBytes : (Ljava/lang/String;)[B
/*      */     //   180: astore #11
/*      */     //   182: aload #9
/*      */     //   184: aload #10
/*      */     //   186: invokeinterface containsKey : (Ljava/lang/Object;)Z
/*      */     //   191: ifeq -> 220
/*      */     //   194: aload #9
/*      */     //   196: aload #10
/*      */     //   198: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   203: checkcast java/lang/Integer
/*      */     //   206: invokevirtual intValue : ()I
/*      */     //   209: istore #12
/*      */     //   211: aload_0
/*      */     //   212: getfield defaultColumnValue : [[B
/*      */     //   215: iload #12
/*      */     //   217: aload #11
/*      */     //   219: aastore
/*      */     //   220: goto -> 153
/*      */     //   223: jsr -> 237
/*      */     //   226: goto -> 253
/*      */     //   229: astore #13
/*      */     //   231: jsr -> 237
/*      */     //   234: aload #13
/*      */     //   236: athrow
/*      */     //   237: astore #14
/*      */     //   239: aload_2
/*      */     //   240: ifnull -> 251
/*      */     //   243: aload_2
/*      */     //   244: invokeinterface close : ()V
/*      */     //   249: aconst_null
/*      */     //   250: astore_2
/*      */     //   251: ret #14
/*      */     //   253: goto -> 91
/*      */     //   256: goto -> 37
/*      */     //   259: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #511	-> 0
/*      */     //   #512	-> 8
/*      */     //   #514	-> 20
/*      */     //   #515	-> 22
/*      */     //   #517	-> 37
/*      */     //   #518	-> 46
/*      */     //   #519	-> 57
/*      */     //   #521	-> 69
/*      */     //   #523	-> 91
/*      */     //   #524	-> 101
/*      */     //   #525	-> 113
/*      */     //   #526	-> 125
/*      */     //   #529	-> 137
/*      */     //   #532	-> 153
/*      */     //   #533	-> 162
/*      */     //   #534	-> 172
/*      */     //   #536	-> 182
/*      */     //   #537	-> 194
/*      */     //   #539	-> 211
/*      */     //   #542	-> 223
/*      */     //   #548	-> 226
/*      */     //   #543	-> 229
/*      */     //   #544	-> 243
/*      */     //   #546	-> 249
/*      */     //   #551	-> 259
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   211	9	12	localColumnIndex	I
/*      */     //   172	48	10	columnName	Ljava/lang/String;
/*      */     //   182	38	11	defaultValue	[B
/*      */     //   113	140	7	tableEntry	Ljava/util/Map$Entry;
/*      */     //   125	128	8	tableName	Ljava/lang/String;
/*      */     //   137	116	9	columnNamesToIndices	Ljava/util/Map;
/*      */     //   57	199	4	dbEntry	Ljava/util/Map$Entry;
/*      */     //   69	187	5	databaseName	Ljava/lang/String;
/*      */     //   91	165	6	referencedTables	Ljava/util/Iterator;
/*      */     //   0	260	0	this	Lcom/mysql/jdbc/UpdatableResultSet;
/*      */     //   8	252	1	dbmd	Ljava/sql/DatabaseMetaData;
/*      */     //   22	238	2	columnsResultSet	Ljava/sql/ResultSet;
/*      */     //   37	223	3	referencedDbs	Ljava/util/Iterator;
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   137	226	229	finally
/*      */     //   229	234	229	finally
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean first() throws SQLException {
/*  567 */     return super.first();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void generateStatements() throws SQLException {
/*  580 */     if (!this.isUpdatable) {
/*  581 */       this.doingUpdates = false;
/*  582 */       this.onInsertRow = false;
/*      */       
/*  584 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     } 
/*      */     
/*  587 */     String quotedId = getQuotedIdChar();
/*      */     
/*  589 */     Map tableNamesSoFar = null;
/*      */     
/*  591 */     if (this.connection.lowerCaseTableNames()) {
/*  592 */       tableNamesSoFar = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  593 */       this.databasesUsedToTablesUsed = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     } else {
/*  595 */       tableNamesSoFar = new TreeMap();
/*  596 */       this.databasesUsedToTablesUsed = new TreeMap();
/*      */     } 
/*      */     
/*  599 */     this.primaryKeyIndicies = new ArrayList();
/*      */     
/*  601 */     StringBuffer fieldValues = new StringBuffer();
/*  602 */     StringBuffer keyValues = new StringBuffer();
/*  603 */     StringBuffer columnNames = new StringBuffer();
/*  604 */     StringBuffer insertPlaceHolders = new StringBuffer();
/*  605 */     StringBuffer allTablesBuf = new StringBuffer();
/*  606 */     Map columnIndicesToTable = new HashMap();
/*      */     
/*  608 */     boolean firstTime = true;
/*  609 */     boolean keysFirstTime = true;
/*      */     
/*  611 */     String equalsStr = this.connection.versionMeetsMinimum(3, 23, 0) ? "<=>" : "=";
/*      */ 
/*      */     
/*  614 */     for (int i = 0; i < this.fields.length; i++) {
/*  615 */       StringBuffer tableNameBuffer = new StringBuffer();
/*  616 */       Map updColumnNameToIndex = null;
/*      */ 
/*      */       
/*  619 */       if (this.fields[i].getOriginalTableName() != null) {
/*      */         
/*  621 */         String str1 = this.fields[i].getDatabaseName();
/*      */         
/*  623 */         if (str1 != null && str1.length() > 0) {
/*  624 */           tableNameBuffer.append(quotedId);
/*  625 */           tableNameBuffer.append(str1);
/*  626 */           tableNameBuffer.append(quotedId);
/*  627 */           tableNameBuffer.append('.');
/*      */         } 
/*      */         
/*  630 */         String tableOnlyName = this.fields[i].getOriginalTableName();
/*      */         
/*  632 */         tableNameBuffer.append(quotedId);
/*  633 */         tableNameBuffer.append(tableOnlyName);
/*  634 */         tableNameBuffer.append(quotedId);
/*      */         
/*  636 */         String fqTableName = tableNameBuffer.toString();
/*      */         
/*  638 */         if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  639 */           if (!tableNamesSoFar.isEmpty()) {
/*  640 */             allTablesBuf.append(',');
/*      */           }
/*      */           
/*  643 */           allTablesBuf.append(fqTableName);
/*  644 */           tableNamesSoFar.put(fqTableName, fqTableName);
/*      */         } 
/*      */         
/*  647 */         columnIndicesToTable.put(new Integer(i), fqTableName);
/*      */         
/*  649 */         updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(str1, tableOnlyName);
/*      */       } else {
/*  651 */         String tableOnlyName = this.fields[i].getTableName();
/*      */         
/*  653 */         if (tableOnlyName != null) {
/*  654 */           tableNameBuffer.append(quotedId);
/*  655 */           tableNameBuffer.append(tableOnlyName);
/*  656 */           tableNameBuffer.append(quotedId);
/*      */           
/*  658 */           String fqTableName = tableNameBuffer.toString();
/*      */           
/*  660 */           if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  661 */             if (!tableNamesSoFar.isEmpty()) {
/*  662 */               allTablesBuf.append(',');
/*      */             }
/*      */             
/*  665 */             allTablesBuf.append(fqTableName);
/*  666 */             tableNamesSoFar.put(fqTableName, fqTableName);
/*      */           } 
/*      */           
/*  669 */           columnIndicesToTable.put(new Integer(i), fqTableName);
/*      */           
/*  671 */           updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(this.catalog, tableOnlyName);
/*      */         } 
/*      */       } 
/*      */       
/*  675 */       String originalColumnName = this.fields[i].getOriginalName();
/*  676 */       String columnName = null;
/*      */       
/*  678 */       if (this.connection.getIO().hasLongColumnInfo() && originalColumnName != null && originalColumnName.length() > 0) {
/*      */ 
/*      */         
/*  681 */         columnName = originalColumnName;
/*      */       } else {
/*  683 */         columnName = this.fields[i].getName();
/*      */       } 
/*      */       
/*  686 */       if (updColumnNameToIndex != null && columnName != null) {
/*  687 */         updColumnNameToIndex.put(columnName, new Integer(i));
/*      */       }
/*      */       
/*  690 */       String originalTableName = this.fields[i].getOriginalTableName();
/*  691 */       String tableName = null;
/*      */       
/*  693 */       if (this.connection.getIO().hasLongColumnInfo() && originalTableName != null && originalTableName.length() > 0) {
/*      */ 
/*      */         
/*  696 */         tableName = originalTableName;
/*      */       } else {
/*  698 */         tableName = this.fields[i].getTableName();
/*      */       } 
/*      */       
/*  701 */       StringBuffer fqcnBuf = new StringBuffer();
/*  702 */       String databaseName = this.fields[i].getDatabaseName();
/*      */       
/*  704 */       if (databaseName != null && databaseName.length() > 0) {
/*  705 */         fqcnBuf.append(quotedId);
/*  706 */         fqcnBuf.append(databaseName);
/*  707 */         fqcnBuf.append(quotedId);
/*  708 */         fqcnBuf.append('.');
/*      */       } 
/*      */       
/*  711 */       fqcnBuf.append(quotedId);
/*  712 */       fqcnBuf.append(tableName);
/*  713 */       fqcnBuf.append(quotedId);
/*  714 */       fqcnBuf.append('.');
/*  715 */       fqcnBuf.append(quotedId);
/*  716 */       fqcnBuf.append(columnName);
/*  717 */       fqcnBuf.append(quotedId);
/*      */       
/*  719 */       String qualifiedColumnName = fqcnBuf.toString();
/*      */       
/*  721 */       if (this.fields[i].isPrimaryKey()) {
/*  722 */         this.primaryKeyIndicies.add(Constants.integerValueOf(i));
/*      */         
/*  724 */         if (!keysFirstTime) {
/*  725 */           keyValues.append(" AND ");
/*      */         } else {
/*  727 */           keysFirstTime = false;
/*      */         } 
/*      */         
/*  730 */         keyValues.append(qualifiedColumnName);
/*  731 */         keyValues.append(equalsStr);
/*  732 */         keyValues.append("?");
/*      */       } 
/*      */       
/*  735 */       if (firstTime) {
/*  736 */         firstTime = false;
/*  737 */         fieldValues.append("SET ");
/*      */       } else {
/*  739 */         fieldValues.append(",");
/*  740 */         columnNames.append(",");
/*  741 */         insertPlaceHolders.append(",");
/*      */       } 
/*      */       
/*  744 */       insertPlaceHolders.append("?");
/*      */       
/*  746 */       columnNames.append(qualifiedColumnName);
/*      */       
/*  748 */       fieldValues.append(qualifiedColumnName);
/*  749 */       fieldValues.append("=?");
/*      */     } 
/*      */     
/*  752 */     this.qualifiedAndQuotedTableName = allTablesBuf.toString();
/*      */     
/*  754 */     this.updateSQL = "UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString();
/*      */ 
/*      */     
/*  757 */     this.insertSQL = "INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")";
/*      */ 
/*      */     
/*  760 */     this.refreshSQL = "SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
/*      */ 
/*      */     
/*  763 */     this.deleteSQL = "DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Map getColumnsToIndexMapForTableAndDB(String databaseName, String tableName) {
/*  770 */     Map tablesUsedToColumnsMap = (Map)this.databasesUsedToTablesUsed.get(databaseName);
/*      */     
/*  772 */     if (tablesUsedToColumnsMap == null) {
/*  773 */       if (this.connection.lowerCaseTableNames()) {
/*  774 */         tablesUsedToColumnsMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */       } else {
/*  776 */         tablesUsedToColumnsMap = new TreeMap();
/*      */       } 
/*      */       
/*  779 */       this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
/*      */     } 
/*      */     
/*  782 */     Map nameToIndex = (Map)tablesUsedToColumnsMap.get(tableName);
/*      */     
/*  784 */     if (nameToIndex == null) {
/*  785 */       nameToIndex = new HashMap();
/*  786 */       tablesUsedToColumnsMap.put(tableName, nameToIndex);
/*      */     } 
/*      */     
/*  789 */     return nameToIndex;
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized SingleByteCharsetConverter getCharConverter() throws SQLException {
/*  794 */     if (!this.initializedCharConverter) {
/*  795 */       this.initializedCharConverter = true;
/*      */       
/*  797 */       if (this.connection.getUseUnicode()) {
/*  798 */         this.charEncoding = this.connection.getEncoding();
/*  799 */         this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  804 */     return this.charConverter;
/*      */   }
/*      */ 
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
/*  817 */     return this.isUpdatable ? 1008 : 1007;
/*      */   }
/*      */   
/*      */   private synchronized String getQuotedIdChar() throws SQLException {
/*  821 */     if (this.quotedIdChar == null) {
/*  822 */       boolean useQuotedIdentifiers = this.connection.supportsQuotedIdentifiers();
/*      */ 
/*      */       
/*  825 */       if (useQuotedIdentifiers) {
/*  826 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*  827 */         this.quotedIdChar = dbmd.getIdentifierQuoteString();
/*      */       } else {
/*  829 */         this.quotedIdChar = "";
/*      */       } 
/*      */     } 
/*      */     
/*  833 */     return this.quotedIdChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void insertRow() throws SQLException {
/*  846 */     checkClosed();
/*      */     
/*  848 */     if (!this.onInsertRow) {
/*  849 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"));
/*      */     }
/*      */     
/*  852 */     this.inserter.executeUpdate();
/*      */     
/*  854 */     long autoIncrementId = this.inserter.getLastInsertID();
/*  855 */     int numFields = this.fields.length;
/*  856 */     byte[][] newRow = new byte[numFields][];
/*      */     
/*  858 */     for (int i = 0; i < numFields; i++) {
/*  859 */       if (this.inserter.isNull(i)) {
/*  860 */         newRow[i] = null;
/*      */       } else {
/*  862 */         newRow[i] = this.inserter.getBytesRepresentation(i);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  869 */       if (this.fields[i].isAutoIncrement() && autoIncrementId > 0L) {
/*  870 */         newRow[i] = String.valueOf(autoIncrementId).getBytes();
/*  871 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
/*      */       } 
/*      */     } 
/*      */     
/*  875 */     ResultSetRow resultSetRow = new ByteArrayRow(newRow);
/*      */     
/*  877 */     refreshRow(this.inserter, resultSetRow);
/*      */     
/*  879 */     this.rowData.addRow(resultSetRow);
/*  880 */     resetInserter();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isAfterLast() throws SQLException {
/*  897 */     return super.isAfterLast();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isBeforeFirst() throws SQLException {
/*  914 */     return super.isBeforeFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isFirst() throws SQLException {
/*  930 */     return super.isFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isLast() throws SQLException {
/*  949 */     return super.isLast();
/*      */   }
/*      */   
/*      */   boolean isUpdatable() {
/*  953 */     return this.isUpdatable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean last() throws SQLException {
/*  970 */     return super.last();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void moveToCurrentRow() throws SQLException {
/*  984 */     checkClosed();
/*      */     
/*  986 */     if (!this.isUpdatable) {
/*  987 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/*  990 */     if (this.onInsertRow) {
/*  991 */       this.onInsertRow = false;
/*  992 */       this.thisRow = this.savedCurrentRow;
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
/*      */   public synchronized void moveToInsertRow() throws SQLException {
/* 1014 */     checkClosed();
/*      */     
/* 1016 */     if (!this.isUpdatable) {
/* 1017 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/* 1020 */     if (this.inserter == null) {
/* 1021 */       if (this.insertSQL == null) {
/* 1022 */         generateStatements();
/*      */       }
/*      */       
/* 1025 */       this.inserter = (PreparedStatement)this.connection.clientPrepareStatement(this.insertSQL);
/*      */       
/* 1027 */       if (this.populateInserterWithDefaultValues) {
/* 1028 */         extractDefaultValues();
/*      */       }
/*      */       
/* 1031 */       resetInserter();
/*      */     } else {
/* 1033 */       resetInserter();
/*      */     } 
/*      */     
/* 1036 */     int numFields = this.fields.length;
/*      */     
/* 1038 */     this.onInsertRow = true;
/* 1039 */     this.doingUpdates = false;
/* 1040 */     this.savedCurrentRow = this.thisRow;
/* 1041 */     byte[][] newRowData = new byte[numFields][];
/* 1042 */     this.thisRow = new ByteArrayRow(newRowData);
/*      */     
/* 1044 */     for (int i = 0; i < numFields; i++) {
/* 1045 */       if (!this.populateInserterWithDefaultValues) {
/* 1046 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, "DEFAULT".getBytes());
/*      */         
/* 1048 */         newRowData = (byte[][])null;
/*      */       }
/* 1050 */       else if (this.defaultColumnValue[i] != null) {
/* 1051 */         Field f = this.fields[i];
/*      */         
/* 1053 */         switch (f.getMysqlType()) {
/*      */           
/*      */           case 7:
/*      */           case 10:
/*      */           case 11:
/*      */           case 12:
/*      */           case 14:
/* 1060 */             if ((this.defaultColumnValue[i]).length > 7 && this.defaultColumnValue[i][0] == 67 && this.defaultColumnValue[i][1] == 85 && this.defaultColumnValue[i][2] == 82 && this.defaultColumnValue[i][3] == 82 && this.defaultColumnValue[i][4] == 69 && this.defaultColumnValue[i][5] == 78 && this.defaultColumnValue[i][6] == 84 && this.defaultColumnValue[i][7] == 95) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1069 */               this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
/*      */               break;
/*      */             } 
/*      */ 
/*      */           
/*      */           default:
/* 1075 */             this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */             break;
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1081 */         byte[] defaultValueCopy = new byte[(this.defaultColumnValue[i]).length];
/* 1082 */         System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
/*      */         
/* 1084 */         newRowData[i] = defaultValueCopy;
/*      */       } else {
/* 1086 */         this.inserter.setNull(i + 1, 0);
/* 1087 */         newRowData[i] = null;
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
/*      */   public synchronized boolean next() throws SQLException {
/* 1113 */     return super.next();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean prev() throws SQLException {
/* 1132 */     return super.prev();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean previous() throws SQLException {
/* 1154 */     return super.previous();
/*      */   }
/*      */ 
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
/* 1167 */     if (this.isClosed) {
/*      */       return;
/*      */     }
/*      */     
/* 1171 */     SQLException sqlEx = null;
/*      */     
/* 1173 */     if (this.useUsageAdvisor && 
/* 1174 */       this.deleter == null && this.inserter == null && this.refresher == null && this.updater == null) {
/*      */       
/* 1176 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */       
/* 1178 */       String message = Messages.getString("UpdatableResultSet.34");
/*      */       
/* 1180 */       this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", (this.owningStatement == null) ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, (this.owningStatement == null) ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
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
/*      */     
/*      */     try {
/* 1194 */       if (this.deleter != null) {
/* 1195 */         this.deleter.close();
/*      */       }
/* 1197 */     } catch (SQLException ex) {
/* 1198 */       sqlEx = ex;
/*      */     } 
/*      */     
/*      */     try {
/* 1202 */       if (this.inserter != null) {
/* 1203 */         this.inserter.close();
/*      */       }
/* 1205 */     } catch (SQLException ex) {
/* 1206 */       sqlEx = ex;
/*      */     } 
/*      */     
/*      */     try {
/* 1210 */       if (this.refresher != null) {
/* 1211 */         this.refresher.close();
/*      */       }
/* 1213 */     } catch (SQLException ex) {
/* 1214 */       sqlEx = ex;
/*      */     } 
/*      */     
/*      */     try {
/* 1218 */       if (this.updater != null) {
/* 1219 */         this.updater.close();
/*      */       }
/* 1221 */     } catch (SQLException ex) {
/* 1222 */       sqlEx = ex;
/*      */     } 
/*      */     
/* 1225 */     super.realClose(calledExplicitly);
/*      */     
/* 1227 */     if (sqlEx != null) {
/* 1228 */       throw sqlEx;
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
/*      */   public synchronized void refreshRow() throws SQLException {
/* 1253 */     checkClosed();
/*      */     
/* 1255 */     if (!this.isUpdatable) {
/* 1256 */       throw new NotUpdatable();
/*      */     }
/*      */     
/* 1259 */     if (this.onInsertRow)
/* 1260 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8")); 
/* 1261 */     if (this.rowData.size() == 0)
/* 1262 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9")); 
/* 1263 */     if (isBeforeFirst())
/* 1264 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10")); 
/* 1265 */     if (isAfterLast()) {
/* 1266 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"));
/*      */     }
/*      */     
/* 1269 */     refreshRow(this.updater, this.thisRow);
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized void refreshRow(PreparedStatement updateInsertStmt, ResultSetRow rowToRefresh) throws SQLException {
/* 1274 */     if (this.refresher == null) {
/* 1275 */       if (this.refreshSQL == null) {
/* 1276 */         generateStatements();
/*      */       }
/*      */       
/* 1279 */       this.refresher = (PreparedStatement)this.connection.clientPrepareStatement(this.refreshSQL);
/*      */     } 
/*      */ 
/*      */     
/* 1283 */     this.refresher.clearParameters();
/*      */     
/* 1285 */     int numKeys = this.primaryKeyIndicies.size();
/*      */     
/* 1287 */     if (numKeys == 1) {
/* 1288 */       byte[] dataFrom = null;
/* 1289 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */       
/* 1291 */       if (!this.doingUpdates && !this.onInsertRow) {
/* 1292 */         dataFrom = rowToRefresh.getColumnValue(index);
/*      */       } else {
/* 1294 */         dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */ 
/*      */         
/* 1297 */         if (updateInsertStmt.isNull(index) || dataFrom.length == 0) {
/* 1298 */           dataFrom = rowToRefresh.getColumnValue(index);
/*      */         } else {
/* 1300 */           dataFrom = stripBinaryPrefix(dataFrom);
/*      */         } 
/*      */       } 
/*      */       
/* 1304 */       this.refresher.setBytesNoEscape(1, dataFrom);
/*      */     } else {
/* 1306 */       for (int i = 0; i < numKeys; i++) {
/* 1307 */         byte[] dataFrom = null;
/* 1308 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/*      */         
/* 1311 */         if (!this.doingUpdates && !this.onInsertRow) {
/* 1312 */           dataFrom = rowToRefresh.getColumnValue(index);
/*      */         } else {
/* 1314 */           dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */ 
/*      */           
/* 1317 */           if (updateInsertStmt.isNull(index) || dataFrom.length == 0) {
/* 1318 */             dataFrom = rowToRefresh.getColumnValue(index);
/*      */           } else {
/* 1320 */             dataFrom = stripBinaryPrefix(dataFrom);
/*      */           } 
/*      */         } 
/*      */         
/* 1324 */         this.refresher.setBytesNoEscape(i + 1, dataFrom);
/*      */       } 
/*      */     } 
/*      */     
/* 1328 */     ResultSet rs = null;
/*      */     
/*      */     try {
/* 1331 */       rs = this.refresher.executeQuery();
/*      */       
/* 1333 */       int numCols = rs.getMetaData().getColumnCount();
/*      */       
/* 1335 */       if (rs.next()) {
/* 1336 */         for (int i = 0; i < numCols; i++) {
/* 1337 */           byte[] val = rs.getBytes(i + 1);
/*      */           
/* 1339 */           if (val == null || rs.wasNull()) {
/* 1340 */             rowToRefresh.setColumnValue(i, null);
/*      */           } else {
/* 1342 */             rowToRefresh.setColumnValue(i, rs.getBytes(i + 1));
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1346 */         throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), "S1000");
/*      */       }
/*      */     
/*      */     } finally {
/*      */       
/* 1351 */       if (rs != null) {
/*      */         try {
/* 1353 */           rs.close();
/* 1354 */         } catch (SQLException ex) {}
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
/*      */   public synchronized boolean relative(int rows) throws SQLException {
/* 1388 */     return super.relative(rows);
/*      */   }
/*      */   
/*      */   private void resetInserter() throws SQLException {
/* 1392 */     this.inserter.clearParameters();
/*      */     
/* 1394 */     for (int i = 0; i < this.fields.length; i++) {
/* 1395 */       this.inserter.setNull(i + 1, 0);
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
/*      */   public synchronized boolean rowDeleted() throws SQLException {
/* 1415 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean rowInserted() throws SQLException {
/* 1433 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean rowUpdated() throws SQLException {
/* 1451 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setResultSetConcurrency(int concurrencyFlag) {
/* 1461 */     super.setResultSetConcurrency(concurrencyFlag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private byte[] stripBinaryPrefix(byte[] dataFrom) {
/* 1475 */     return StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void syncUpdate() throws SQLException {
/* 1486 */     if (this.updater == null) {
/* 1487 */       if (this.updateSQL == null) {
/* 1488 */         generateStatements();
/*      */       }
/*      */       
/* 1491 */       this.updater = (PreparedStatement)this.connection.clientPrepareStatement(this.updateSQL);
/*      */     } 
/*      */ 
/*      */     
/* 1495 */     int numFields = this.fields.length;
/* 1496 */     this.updater.clearParameters();
/*      */     
/* 1498 */     for (int i = 0; i < numFields; i++) {
/* 1499 */       if (this.thisRow.getColumnValue(i) != null) {
/* 1500 */         this.updater.setBytes(i + 1, this.thisRow.getColumnValue(i), this.fields[i].isBinary(), false);
/*      */       } else {
/*      */         
/* 1503 */         this.updater.setNull(i + 1, 0);
/*      */       } 
/*      */     } 
/*      */     
/* 1507 */     int numKeys = this.primaryKeyIndicies.size();
/*      */     
/* 1509 */     if (numKeys == 1) {
/* 1510 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/* 1511 */       byte[] keyData = this.thisRow.getColumnValue(index);
/* 1512 */       this.updater.setBytes(numFields + 1, keyData, false, false);
/*      */     } else {
/* 1514 */       for (int j = 0; j < numKeys; j++) {
/* 1515 */         byte[] currentVal = this.thisRow.getColumnValue(((Integer)this.primaryKeyIndicies.get(j)).intValue());
/*      */ 
/*      */         
/* 1518 */         if (currentVal != null) {
/* 1519 */           this.updater.setBytes(numFields + j + 1, currentVal, false, false);
/*      */         } else {
/*      */           
/* 1522 */           this.updater.setNull(numFields + j + 1, 0);
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
/*      */   public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
/* 1547 */     if (!this.onInsertRow) {
/* 1548 */       if (!this.doingUpdates) {
/* 1549 */         this.doingUpdates = true;
/* 1550 */         syncUpdate();
/*      */       } 
/*      */       
/* 1553 */       this.updater.setAsciiStream(columnIndex, x, length);
/*      */     } else {
/* 1555 */       this.inserter.setAsciiStream(columnIndex, x, length);
/* 1556 */       this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
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
/*      */   public synchronized void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
/* 1579 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
/* 1598 */     if (!this.onInsertRow) {
/* 1599 */       if (!this.doingUpdates) {
/* 1600 */         this.doingUpdates = true;
/* 1601 */         syncUpdate();
/*      */       } 
/*      */       
/* 1604 */       this.updater.setBigDecimal(columnIndex, x);
/*      */     } else {
/* 1606 */       this.inserter.setBigDecimal(columnIndex, x);
/*      */       
/* 1608 */       if (x == null) {
/* 1609 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1611 */         this.thisRow.setColumnValue(columnIndex - 1, x.toString().getBytes());
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
/*      */   public synchronized void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
/* 1632 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
/* 1654 */     if (!this.onInsertRow) {
/* 1655 */       if (!this.doingUpdates) {
/* 1656 */         this.doingUpdates = true;
/* 1657 */         syncUpdate();
/*      */       } 
/*      */       
/* 1660 */       this.updater.setBinaryStream(columnIndex, x, length);
/*      */     } else {
/* 1662 */       this.inserter.setBinaryStream(columnIndex, x, length);
/*      */       
/* 1664 */       if (x == null) {
/* 1665 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1667 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
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
/*      */   public synchronized void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
/* 1691 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateBlob(int columnIndex, Blob blob) throws SQLException {
/* 1699 */     if (!this.onInsertRow) {
/* 1700 */       if (!this.doingUpdates) {
/* 1701 */         this.doingUpdates = true;
/* 1702 */         syncUpdate();
/*      */       } 
/*      */       
/* 1705 */       this.updater.setBlob(columnIndex, blob);
/*      */     } else {
/* 1707 */       this.inserter.setBlob(columnIndex, blob);
/*      */       
/* 1709 */       if (blob == null) {
/* 1710 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1712 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateBlob(String columnName, Blob blob) throws SQLException {
/* 1722 */     updateBlob(findColumn(columnName), blob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateBoolean(int columnIndex, boolean x) throws SQLException {
/* 1741 */     if (!this.onInsertRow) {
/* 1742 */       if (!this.doingUpdates) {
/* 1743 */         this.doingUpdates = true;
/* 1744 */         syncUpdate();
/*      */       } 
/*      */       
/* 1747 */       this.updater.setBoolean(columnIndex, x);
/*      */     } else {
/* 1749 */       this.inserter.setBoolean(columnIndex, x);
/*      */       
/* 1751 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateBoolean(String columnName, boolean x) throws SQLException {
/* 1772 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateByte(int columnIndex, byte x) throws SQLException {
/* 1791 */     if (!this.onInsertRow) {
/* 1792 */       if (!this.doingUpdates) {
/* 1793 */         this.doingUpdates = true;
/* 1794 */         syncUpdate();
/*      */       } 
/*      */       
/* 1797 */       this.updater.setByte(columnIndex, x);
/*      */     } else {
/* 1799 */       this.inserter.setByte(columnIndex, x);
/*      */       
/* 1801 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateByte(String columnName, byte x) throws SQLException {
/* 1822 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateBytes(int columnIndex, byte[] x) throws SQLException {
/* 1841 */     if (!this.onInsertRow) {
/* 1842 */       if (!this.doingUpdates) {
/* 1843 */         this.doingUpdates = true;
/* 1844 */         syncUpdate();
/*      */       } 
/*      */       
/* 1847 */       this.updater.setBytes(columnIndex, x);
/*      */     } else {
/* 1849 */       this.inserter.setBytes(columnIndex, x);
/*      */       
/* 1851 */       this.thisRow.setColumnValue(columnIndex - 1, x);
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
/*      */   public synchronized void updateBytes(String columnName, byte[] x) throws SQLException {
/* 1871 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
/* 1893 */     if (!this.onInsertRow) {
/* 1894 */       if (!this.doingUpdates) {
/* 1895 */         this.doingUpdates = true;
/* 1896 */         syncUpdate();
/*      */       } 
/*      */       
/* 1899 */       this.updater.setCharacterStream(columnIndex, x, length);
/*      */     } else {
/* 1901 */       this.inserter.setCharacterStream(columnIndex, x, length);
/*      */       
/* 1903 */       if (x == null) {
/* 1904 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1906 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
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
/*      */   public synchronized void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
/* 1930 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(int columnIndex, Clob clob) throws SQLException {
/* 1938 */     if (clob == null) {
/* 1939 */       updateNull(columnIndex);
/*      */     } else {
/* 1941 */       updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
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
/*      */   public synchronized void updateDate(int columnIndex, Date x) throws SQLException {
/* 1962 */     if (!this.onInsertRow) {
/* 1963 */       if (!this.doingUpdates) {
/* 1964 */         this.doingUpdates = true;
/* 1965 */         syncUpdate();
/*      */       } 
/*      */       
/* 1968 */       this.updater.setDate(columnIndex, x);
/*      */     } else {
/* 1970 */       this.inserter.setDate(columnIndex, x);
/*      */       
/* 1972 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateDate(String columnName, Date x) throws SQLException {
/* 1993 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateDouble(int columnIndex, double x) throws SQLException {
/* 2012 */     if (!this.onInsertRow) {
/* 2013 */       if (!this.doingUpdates) {
/* 2014 */         this.doingUpdates = true;
/* 2015 */         syncUpdate();
/*      */       } 
/*      */       
/* 2018 */       this.updater.setDouble(columnIndex, x);
/*      */     } else {
/* 2020 */       this.inserter.setDouble(columnIndex, x);
/*      */       
/* 2022 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateDouble(String columnName, double x) throws SQLException {
/* 2043 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateFloat(int columnIndex, float x) throws SQLException {
/* 2062 */     if (!this.onInsertRow) {
/* 2063 */       if (!this.doingUpdates) {
/* 2064 */         this.doingUpdates = true;
/* 2065 */         syncUpdate();
/*      */       } 
/*      */       
/* 2068 */       this.updater.setFloat(columnIndex, x);
/*      */     } else {
/* 2070 */       this.inserter.setFloat(columnIndex, x);
/*      */       
/* 2072 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateFloat(String columnName, float x) throws SQLException {
/* 2093 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateInt(int columnIndex, int x) throws SQLException {
/* 2112 */     if (!this.onInsertRow) {
/* 2113 */       if (!this.doingUpdates) {
/* 2114 */         this.doingUpdates = true;
/* 2115 */         syncUpdate();
/*      */       } 
/*      */       
/* 2118 */       this.updater.setInt(columnIndex, x);
/*      */     } else {
/* 2120 */       this.inserter.setInt(columnIndex, x);
/*      */       
/* 2122 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateInt(String columnName, int x) throws SQLException {
/* 2143 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateLong(int columnIndex, long x) throws SQLException {
/* 2162 */     if (!this.onInsertRow) {
/* 2163 */       if (!this.doingUpdates) {
/* 2164 */         this.doingUpdates = true;
/* 2165 */         syncUpdate();
/*      */       } 
/*      */       
/* 2168 */       this.updater.setLong(columnIndex, x);
/*      */     } else {
/* 2170 */       this.inserter.setLong(columnIndex, x);
/*      */       
/* 2172 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateLong(String columnName, long x) throws SQLException {
/* 2193 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateNull(int columnIndex) throws SQLException {
/* 2209 */     if (!this.onInsertRow) {
/* 2210 */       if (!this.doingUpdates) {
/* 2211 */         this.doingUpdates = true;
/* 2212 */         syncUpdate();
/*      */       } 
/*      */       
/* 2215 */       this.updater.setNull(columnIndex, 0);
/*      */     } else {
/* 2217 */       this.inserter.setNull(columnIndex, 0);
/*      */       
/* 2219 */       this.thisRow.setColumnValue(columnIndex - 1, null);
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
/*      */   public synchronized void updateNull(String columnName) throws SQLException {
/* 2236 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateObject(int columnIndex, Object x) throws SQLException {
/* 2255 */     if (!this.onInsertRow) {
/* 2256 */       if (!this.doingUpdates) {
/* 2257 */         this.doingUpdates = true;
/* 2258 */         syncUpdate();
/*      */       } 
/*      */       
/* 2261 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2263 */       this.inserter.setObject(columnIndex, x);
/*      */       
/* 2265 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateObject(int columnIndex, Object x, int scale) throws SQLException {
/* 2290 */     if (!this.onInsertRow) {
/* 2291 */       if (!this.doingUpdates) {
/* 2292 */         this.doingUpdates = true;
/* 2293 */         syncUpdate();
/*      */       } 
/*      */       
/* 2296 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2298 */       this.inserter.setObject(columnIndex, x);
/*      */       
/* 2300 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateObject(String columnName, Object x) throws SQLException {
/* 2321 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateObject(String columnName, Object x, int scale) throws SQLException {
/* 2344 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateRow() throws SQLException {
/* 2358 */     if (!this.isUpdatable) {
/* 2359 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/* 2362 */     if (this.doingUpdates) {
/* 2363 */       this.updater.executeUpdate();
/* 2364 */       refreshRow();
/* 2365 */       this.doingUpdates = false;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2371 */     syncUpdate();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateShort(int columnIndex, short x) throws SQLException {
/* 2390 */     if (!this.onInsertRow) {
/* 2391 */       if (!this.doingUpdates) {
/* 2392 */         this.doingUpdates = true;
/* 2393 */         syncUpdate();
/*      */       } 
/*      */       
/* 2396 */       this.updater.setShort(columnIndex, x);
/*      */     } else {
/* 2398 */       this.inserter.setShort(columnIndex, x);
/*      */       
/* 2400 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateShort(String columnName, short x) throws SQLException {
/* 2421 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateString(int columnIndex, String x) throws SQLException {
/* 2440 */     checkClosed();
/*      */     
/* 2442 */     if (!this.onInsertRow) {
/* 2443 */       if (!this.doingUpdates) {
/* 2444 */         this.doingUpdates = true;
/* 2445 */         syncUpdate();
/*      */       } 
/*      */       
/* 2448 */       this.updater.setString(columnIndex, x);
/*      */     } else {
/* 2450 */       this.inserter.setString(columnIndex, x);
/*      */       
/* 2452 */       if (x == null) {
/* 2453 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       }
/* 2455 */       else if (getCharConverter() != null) {
/* 2456 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode()));
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 2461 */         this.thisRow.setColumnValue(columnIndex - 1, x.getBytes());
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
/*      */   public synchronized void updateString(String columnName, String x) throws SQLException {
/* 2483 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateTime(int columnIndex, Time x) throws SQLException {
/* 2502 */     if (!this.onInsertRow) {
/* 2503 */       if (!this.doingUpdates) {
/* 2504 */         this.doingUpdates = true;
/* 2505 */         syncUpdate();
/*      */       } 
/*      */       
/* 2508 */       this.updater.setTime(columnIndex, x);
/*      */     } else {
/* 2510 */       this.inserter.setTime(columnIndex, x);
/*      */       
/* 2512 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateTime(String columnName, Time x) throws SQLException {
/* 2533 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
/* 2552 */     if (!this.onInsertRow) {
/* 2553 */       if (!this.doingUpdates) {
/* 2554 */         this.doingUpdates = true;
/* 2555 */         syncUpdate();
/*      */       } 
/*      */       
/* 2558 */       this.updater.setTimestamp(columnIndex, x);
/*      */     } else {
/* 2560 */       this.inserter.setTimestamp(columnIndex, x);
/*      */       
/* 2562 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateTimestamp(String columnName, Timestamp x) throws SQLException {
/* 2583 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\UpdatableResultSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */