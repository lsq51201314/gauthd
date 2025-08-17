/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.regex.PatternSyntaxException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Field
/*      */ {
/*      */   private static final int AUTO_INCREMENT_FLAG = 512;
/*      */   private static final int NO_CHARSET_INFO = -1;
/*      */   private byte[] buffer;
/*   46 */   private int charsetIndex = 0;
/*      */   
/*   48 */   private String charsetName = null;
/*      */   
/*      */   private int colDecimals;
/*      */   
/*      */   private short colFlag;
/*      */   
/*   54 */   private String collationName = null;
/*      */   
/*   56 */   private ConnectionImpl connection = null;
/*      */   
/*   58 */   private String databaseName = null;
/*      */   
/*   60 */   private int databaseNameLength = -1;
/*      */ 
/*      */   
/*   63 */   private int databaseNameStart = -1;
/*      */   
/*   65 */   private int defaultValueLength = -1;
/*      */ 
/*      */   
/*   68 */   private int defaultValueStart = -1;
/*      */   
/*   70 */   private String fullName = null;
/*      */   
/*   72 */   private String fullOriginalName = null;
/*      */   
/*      */   private boolean isImplicitTempTable = false;
/*      */   
/*      */   private long length;
/*      */   
/*   78 */   private int mysqlType = -1;
/*      */   
/*      */   private String name;
/*      */   
/*      */   private int nameLength;
/*      */   
/*      */   private int nameStart;
/*      */   
/*   86 */   private String originalColumnName = null;
/*      */   
/*   88 */   private int originalColumnNameLength = -1;
/*      */ 
/*      */   
/*   91 */   private int originalColumnNameStart = -1;
/*      */   
/*   93 */   private String originalTableName = null;
/*      */   
/*   95 */   private int originalTableNameLength = -1;
/*      */ 
/*      */   
/*   98 */   private int originalTableNameStart = -1;
/*      */   
/*  100 */   private int precisionAdjustFactor = 0;
/*      */   
/*  102 */   private int sqlType = -1;
/*      */ 
/*      */ 
/*      */   
/*      */   private String tableName;
/*      */ 
/*      */   
/*      */   private int tableNameLength;
/*      */ 
/*      */   
/*      */   private int tableNameStart;
/*      */ 
/*      */   
/*      */   private boolean useOldNameMetadata = false;
/*      */ 
/*      */   
/*      */   private boolean isSingleBit;
/*      */ 
/*      */   
/*      */   private int maxBytesPerChar;
/*      */ 
/*      */ 
/*      */   
/*      */   Field(ConnectionImpl conn, byte[] buffer, int databaseNameStart, int databaseNameLength, int tableNameStart, int tableNameLength, int originalTableNameStart, int originalTableNameLength, int nameStart, int nameLength, int originalColumnNameStart, int originalColumnNameLength, long length, int mysqlType, short colFlag, int colDecimals, int defaultValueStart, int defaultValueLength, int charsetIndex) throws SQLException {
/*  126 */     this.connection = conn;
/*  127 */     this.buffer = buffer;
/*  128 */     this.nameStart = nameStart;
/*  129 */     this.nameLength = nameLength;
/*  130 */     this.tableNameStart = tableNameStart;
/*  131 */     this.tableNameLength = tableNameLength;
/*  132 */     this.length = length;
/*  133 */     this.colFlag = colFlag;
/*  134 */     this.colDecimals = colDecimals;
/*  135 */     this.mysqlType = mysqlType;
/*      */ 
/*      */     
/*  138 */     this.databaseNameStart = databaseNameStart;
/*  139 */     this.databaseNameLength = databaseNameLength;
/*      */     
/*  141 */     this.originalTableNameStart = originalTableNameStart;
/*  142 */     this.originalTableNameLength = originalTableNameLength;
/*      */     
/*  144 */     this.originalColumnNameStart = originalColumnNameStart;
/*  145 */     this.originalColumnNameLength = originalColumnNameLength;
/*      */     
/*  147 */     this.defaultValueStart = defaultValueStart;
/*  148 */     this.defaultValueLength = defaultValueLength;
/*      */ 
/*      */ 
/*      */     
/*  152 */     this.charsetIndex = charsetIndex;
/*      */ 
/*      */ 
/*      */     
/*  156 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */     
/*  158 */     checkForImplicitTemporaryTable();
/*      */ 
/*      */     
/*  161 */     if (this.mysqlType == 252) {
/*  162 */       boolean isFromFunction = (this.originalTableNameLength == 0);
/*      */       
/*  164 */       if ((this.connection != null && this.connection.getBlobsAreStrings()) || (this.connection.getFunctionsNeverReturnBlobs() && isFromFunction)) {
/*      */         
/*  166 */         this.sqlType = 12;
/*  167 */         this.mysqlType = 15;
/*  168 */       } else if (this.charsetIndex == 63 || !this.connection.versionMeetsMinimum(4, 1, 0)) {
/*      */         
/*  170 */         if (this.connection.getUseBlobToStoreUTF8OutsideBMP() && shouldSetupForUtf8StringInBlob()) {
/*      */           
/*  172 */           setupForUtf8StringInBlob();
/*      */         } else {
/*  174 */           setBlobTypeBasedOnLength();
/*  175 */           this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */         } 
/*      */       } else {
/*      */         
/*  179 */         this.mysqlType = 253;
/*  180 */         this.sqlType = -1;
/*      */       } 
/*      */     } 
/*      */     
/*  184 */     if (this.sqlType == -6 && this.length == 1L && this.connection.getTinyInt1isBit())
/*      */     {
/*      */       
/*  187 */       if (conn.getTinyInt1isBit()) {
/*  188 */         if (conn.getTransformedBitIsBoolean()) {
/*  189 */           this.sqlType = 16;
/*      */         } else {
/*  191 */           this.sqlType = -7;
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*  197 */     if (!isNativeNumericType() && !isNativeDateTimeType()) {
/*  198 */       this.charsetName = this.connection.getCharsetNameForIndex(this.charsetIndex);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  205 */       boolean isBinary = isBinary();
/*      */       
/*  207 */       if (this.connection.versionMeetsMinimum(4, 1, 0) && this.mysqlType == 253 && isBinary && this.charsetIndex == 63)
/*      */       {
/*      */ 
/*      */         
/*  211 */         if (isOpaqueBinary()) {
/*  212 */           this.sqlType = -3;
/*      */         }
/*      */       }
/*      */       
/*  216 */       if (this.connection.versionMeetsMinimum(4, 1, 0) && this.mysqlType == 254 && isBinary && this.charsetIndex == 63)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  226 */         if (isOpaqueBinary() && !this.connection.getBlobsAreStrings()) {
/*  227 */           this.sqlType = -2;
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  233 */       if (this.mysqlType == 16) {
/*  234 */         this.isSingleBit = (this.length == 0L);
/*      */         
/*  236 */         if (this.connection != null && (this.connection.versionMeetsMinimum(5, 0, 21) || this.connection.versionMeetsMinimum(5, 1, 10)) && this.length == 1L)
/*      */         {
/*  238 */           this.isSingleBit = true;
/*      */         }
/*      */         
/*  241 */         if (this.isSingleBit) {
/*  242 */           this.sqlType = -7;
/*      */         } else {
/*  244 */           this.sqlType = -3;
/*  245 */           this.colFlag = (short)(this.colFlag | 0x80);
/*  246 */           this.colFlag = (short)(this.colFlag | 0x10);
/*  247 */           isBinary = true;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  254 */       if (this.sqlType == -4 && !isBinary) {
/*  255 */         this.sqlType = -1;
/*  256 */       } else if (this.sqlType == -3 && !isBinary) {
/*  257 */         this.sqlType = 12;
/*      */       } 
/*      */     } else {
/*  260 */       this.charsetName = "US-ASCII";
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  266 */     if (!isUnsigned()) {
/*  267 */       switch (this.mysqlType) {
/*      */         case 0:
/*      */         case 246:
/*  270 */           this.precisionAdjustFactor = -1;
/*      */           break;
/*      */         
/*      */         case 4:
/*      */         case 5:
/*  275 */           this.precisionAdjustFactor = 1;
/*      */           break;
/*      */       } 
/*      */     
/*      */     } else {
/*  280 */       switch (this.mysqlType) {
/*      */         case 4:
/*      */         case 5:
/*  283 */           this.precisionAdjustFactor = 1;
/*      */           break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean shouldSetupForUtf8StringInBlob() throws SQLException {
/*  291 */     String includePattern = this.connection.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */     
/*  293 */     String excludePattern = this.connection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */ 
/*      */     
/*  296 */     if (excludePattern != null && !StringUtils.isEmptyOrWhitespaceOnly(excludePattern)) {
/*      */       
/*      */       try {
/*  299 */         if (getOriginalName().matches(excludePattern)) {
/*  300 */           if (includePattern != null && !StringUtils.isEmptyOrWhitespaceOnly(includePattern)) {
/*      */             
/*      */             try {
/*  303 */               if (getOriginalName().matches(includePattern)) {
/*  304 */                 return true;
/*      */               }
/*  306 */             } catch (PatternSyntaxException pse) {
/*  307 */               SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpIncludedColumnNamePattern\"", "S1009");
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  312 */               if (!this.connection.getParanoid()) {
/*  313 */                 sqlEx.initCause(pse);
/*      */               }
/*      */               
/*  316 */               throw sqlEx;
/*      */             } 
/*      */           }
/*      */           
/*  320 */           return false;
/*      */         } 
/*  322 */       } catch (PatternSyntaxException pse) {
/*  323 */         SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpExcludedColumnNamePattern\"", "S1009");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  328 */         if (!this.connection.getParanoid()) {
/*  329 */           sqlEx.initCause(pse);
/*      */         }
/*      */         
/*  332 */         throw sqlEx;
/*      */       } 
/*      */     }
/*      */     
/*  336 */     return true;
/*      */   }
/*      */   
/*      */   private void setupForUtf8StringInBlob() {
/*  340 */     if (this.length == 255L || this.length == 65535L) {
/*  341 */       this.mysqlType = 15;
/*  342 */       this.sqlType = 12;
/*      */     } else {
/*  344 */       this.mysqlType = 253;
/*  345 */       this.sqlType = -1;
/*      */     } 
/*      */     
/*  348 */     this.charsetIndex = 33;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Field(ConnectionImpl conn, byte[] buffer, int nameStart, int nameLength, int tableNameStart, int tableNameLength, int length, int mysqlType, short colFlag, int colDecimals) throws SQLException {
/*  357 */     this(conn, buffer, -1, -1, tableNameStart, tableNameLength, -1, -1, nameStart, nameLength, -1, -1, length, mysqlType, colFlag, colDecimals, -1, -1, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Field(String tableName, String columnName, int jdbcType, int length) {
/*  366 */     this.tableName = tableName;
/*  367 */     this.name = columnName;
/*  368 */     this.length = length;
/*  369 */     this.sqlType = jdbcType;
/*  370 */     this.colFlag = 0;
/*  371 */     this.colDecimals = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Field(String tableName, String columnName, int charsetIndex, int jdbcType, int length) {
/*  392 */     this.tableName = tableName;
/*  393 */     this.name = columnName;
/*  394 */     this.length = length;
/*  395 */     this.sqlType = jdbcType;
/*  396 */     this.colFlag = 0;
/*  397 */     this.colDecimals = 0;
/*  398 */     this.charsetIndex = charsetIndex;
/*      */   }
/*      */   
/*      */   private void checkForImplicitTemporaryTable() {
/*  402 */     this.isImplicitTempTable = (this.tableNameLength > 5 && this.buffer[this.tableNameStart] == 35 && this.buffer[this.tableNameStart + 1] == 115 && this.buffer[this.tableNameStart + 2] == 113 && this.buffer[this.tableNameStart + 3] == 108 && this.buffer[this.tableNameStart + 4] == 95);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCharacterSet() throws SQLException {
/*  416 */     return this.charsetName;
/*      */   }
/*      */   
/*      */   public void setCharacterSet(String javaEncodingName) throws SQLException {
/*  420 */     this.charsetName = javaEncodingName;
/*  421 */     this.charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(javaEncodingName);
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized String getCollation() throws SQLException {
/*  426 */     if (this.collationName == null && 
/*  427 */       this.connection != null && 
/*  428 */       this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  429 */       if (this.connection.getUseDynamicCharsetInfo()) {
/*  430 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*      */         
/*  433 */         String quotedIdStr = dbmd.getIdentifierQuoteString();
/*      */         
/*  435 */         if (" ".equals(quotedIdStr)) {
/*  436 */           quotedIdStr = "";
/*      */         }
/*      */         
/*  439 */         String csCatalogName = getDatabaseName();
/*  440 */         String csTableName = getOriginalTableName();
/*  441 */         String csColumnName = getOriginalName();
/*      */         
/*  443 */         if (csCatalogName != null && csCatalogName.length() != 0 && csTableName != null && csTableName.length() != 0 && csColumnName != null && csColumnName.length() != 0) {
/*      */ 
/*      */ 
/*      */           
/*  447 */           StringBuffer queryBuf = new StringBuffer(csCatalogName.length() + csTableName.length() + 28);
/*      */ 
/*      */           
/*  450 */           queryBuf.append("SHOW FULL COLUMNS FROM ");
/*  451 */           queryBuf.append(quotedIdStr);
/*  452 */           queryBuf.append(csCatalogName);
/*  453 */           queryBuf.append(quotedIdStr);
/*  454 */           queryBuf.append(".");
/*  455 */           queryBuf.append(quotedIdStr);
/*  456 */           queryBuf.append(csTableName);
/*  457 */           queryBuf.append(quotedIdStr);
/*      */           
/*  459 */           Statement collationStmt = null;
/*  460 */           ResultSet collationRs = null;
/*      */           
/*      */           try {
/*  463 */             collationStmt = this.connection.createStatement();
/*      */             
/*  465 */             collationRs = collationStmt.executeQuery(queryBuf.toString());
/*      */ 
/*      */             
/*  468 */             while (collationRs.next()) {
/*  469 */               if (csColumnName.equals(collationRs.getString("Field"))) {
/*      */                 
/*  471 */                 this.collationName = collationRs.getString("Collation");
/*      */ 
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } finally {
/*  478 */             if (collationRs != null) {
/*  479 */               collationRs.close();
/*  480 */               collationRs = null;
/*      */             } 
/*      */             
/*  483 */             if (collationStmt != null) {
/*  484 */               collationStmt.close();
/*  485 */               collationStmt = null;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } else {
/*  490 */         this.collationName = CharsetMapping.INDEX_TO_COLLATION[this.charsetIndex];
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  496 */     return this.collationName;
/*      */   }
/*      */   
/*      */   public String getColumnLabel() throws SQLException {
/*  500 */     return getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDatabaseName() throws SQLException {
/*  509 */     if (this.databaseName == null && this.databaseNameStart != -1 && this.databaseNameLength != -1)
/*      */     {
/*  511 */       this.databaseName = getStringFromBytes(this.databaseNameStart, this.databaseNameLength);
/*      */     }
/*      */ 
/*      */     
/*  515 */     return this.databaseName;
/*      */   }
/*      */   
/*      */   int getDecimals() {
/*  519 */     return this.colDecimals;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFullName() throws SQLException {
/*  528 */     if (this.fullName == null) {
/*  529 */       StringBuffer fullNameBuf = new StringBuffer(getTableName().length() + 1 + getName().length());
/*      */       
/*  531 */       fullNameBuf.append(this.tableName);
/*      */ 
/*      */       
/*  534 */       fullNameBuf.append('.');
/*  535 */       fullNameBuf.append(this.name);
/*  536 */       this.fullName = fullNameBuf.toString();
/*  537 */       fullNameBuf = null;
/*      */     } 
/*      */     
/*  540 */     return this.fullName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFullOriginalName() throws SQLException {
/*  549 */     getOriginalName();
/*      */     
/*  551 */     if (this.originalColumnName == null) {
/*  552 */       return null;
/*      */     }
/*      */     
/*  555 */     if (this.fullName == null) {
/*  556 */       StringBuffer fullOriginalNameBuf = new StringBuffer(getOriginalTableName().length() + 1 + getOriginalName().length());
/*      */ 
/*      */       
/*  559 */       fullOriginalNameBuf.append(this.originalTableName);
/*      */ 
/*      */       
/*  562 */       fullOriginalNameBuf.append('.');
/*  563 */       fullOriginalNameBuf.append(this.originalColumnName);
/*  564 */       this.fullOriginalName = fullOriginalNameBuf.toString();
/*  565 */       fullOriginalNameBuf = null;
/*      */     } 
/*      */     
/*  568 */     return this.fullOriginalName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLength() {
/*  577 */     return this.length;
/*      */   }
/*      */   
/*      */   public synchronized int getMaxBytesPerCharacter() throws SQLException {
/*  581 */     if (this.maxBytesPerChar == 0) {
/*  582 */       this.maxBytesPerChar = this.connection.getMaxBytesPerChar(getCharacterSet());
/*      */     }
/*      */     
/*  585 */     return this.maxBytesPerChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMysqlType() {
/*  594 */     return this.mysqlType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() throws SQLException {
/*  603 */     if (this.name == null) {
/*  604 */       this.name = getStringFromBytes(this.nameStart, this.nameLength);
/*      */     }
/*      */     
/*  607 */     return this.name;
/*      */   }
/*      */   
/*      */   public String getNameNoAliases() throws SQLException {
/*  611 */     if (this.useOldNameMetadata) {
/*  612 */       return getName();
/*      */     }
/*      */     
/*  615 */     if (this.connection != null && this.connection.versionMeetsMinimum(4, 1, 0))
/*      */     {
/*  617 */       return getOriginalName();
/*      */     }
/*      */     
/*  620 */     return getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getOriginalName() throws SQLException {
/*  629 */     if (this.originalColumnName == null && this.originalColumnNameStart != -1 && this.originalColumnNameLength != -1)
/*      */     {
/*      */       
/*  632 */       this.originalColumnName = getStringFromBytes(this.originalColumnNameStart, this.originalColumnNameLength);
/*      */     }
/*      */ 
/*      */     
/*  636 */     return this.originalColumnName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getOriginalTableName() throws SQLException {
/*  645 */     if (this.originalTableName == null && this.originalTableNameStart != -1 && this.originalTableNameLength != -1)
/*      */     {
/*      */       
/*  648 */       this.originalTableName = getStringFromBytes(this.originalTableNameStart, this.originalTableNameLength);
/*      */     }
/*      */ 
/*      */     
/*  652 */     return this.originalTableName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPrecisionAdjustFactor() {
/*  664 */     return this.precisionAdjustFactor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSQLType() {
/*  673 */     return this.sqlType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getStringFromBytes(int stringStart, int stringLength) throws SQLException {
/*  682 */     if (stringStart == -1 || stringLength == -1) {
/*  683 */       return null;
/*      */     }
/*      */     
/*  686 */     String stringVal = null;
/*      */     
/*  688 */     if (this.connection != null) {
/*  689 */       if (this.connection.getUseUnicode()) {
/*  690 */         String encoding = this.connection.getCharacterSetMetadata();
/*      */         
/*  692 */         if (encoding == null) {
/*  693 */           encoding = this.connection.getEncoding();
/*      */         }
/*      */         
/*  696 */         if (encoding != null) {
/*  697 */           SingleByteCharsetConverter converter = null;
/*      */           
/*  699 */           if (this.connection != null) {
/*  700 */             converter = this.connection.getCharsetConverter(encoding);
/*      */           }
/*      */ 
/*      */           
/*  704 */           if (converter != null) {
/*  705 */             stringVal = converter.toString(this.buffer, stringStart, stringLength);
/*      */           }
/*      */           else {
/*      */             
/*  709 */             byte[] stringBytes = new byte[stringLength];
/*      */             
/*  711 */             int endIndex = stringStart + stringLength;
/*  712 */             int pos = 0;
/*      */             
/*  714 */             for (int i = stringStart; i < endIndex; i++) {
/*  715 */               stringBytes[pos++] = this.buffer[i];
/*      */             }
/*      */             
/*      */             try {
/*  719 */               stringVal = new String(stringBytes, encoding);
/*  720 */             } catch (UnsupportedEncodingException ue) {
/*  721 */               throw new RuntimeException(Messages.getString("Field.12") + encoding + Messages.getString("Field.13"));
/*      */             }
/*      */           
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/*  728 */           stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/*  733 */         stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  738 */       stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */     } 
/*      */ 
/*      */     
/*  742 */     return stringVal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTable() throws SQLException {
/*  751 */     return getTableName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTableName() throws SQLException {
/*  760 */     if (this.tableName == null) {
/*  761 */       this.tableName = getStringFromBytes(this.tableNameStart, this.tableNameLength);
/*      */     }
/*      */ 
/*      */     
/*  765 */     return this.tableName;
/*      */   }
/*      */   
/*      */   public String getTableNameNoAliases() throws SQLException {
/*  769 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  770 */       return getOriginalTableName();
/*      */     }
/*      */     
/*  773 */     return getTableName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutoIncrement() {
/*  782 */     return ((this.colFlag & 0x200) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBinary() {
/*  791 */     return ((this.colFlag & 0x80) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBlob() {
/*  800 */     return ((this.colFlag & 0x10) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isImplicitTemporaryTable() {
/*  809 */     return this.isImplicitTempTable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isMultipleKey() {
/*  818 */     return ((this.colFlag & 0x8) > 0);
/*      */   }
/*      */   
/*      */   boolean isNotNull() {
/*  822 */     return ((this.colFlag & 0x1) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isOpaqueBinary() throws SQLException {
/*  832 */     if (this.charsetIndex == 63 && isBinary() && (getMysqlType() == 254 || getMysqlType() == 253)) {
/*      */ 
/*      */ 
/*      */       
/*  836 */       if (this.originalTableNameLength == 0 && this.connection != null && !this.connection.versionMeetsMinimum(5, 0, 25))
/*      */       {
/*  838 */         return false;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  844 */       return !isImplicitTemporaryTable();
/*      */     } 
/*      */     
/*  847 */     return (this.connection.versionMeetsMinimum(4, 1, 0) && "binary".equalsIgnoreCase(getCharacterSet()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPrimaryKey() {
/*  858 */     return ((this.colFlag & 0x2) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isReadOnly() throws SQLException {
/*  868 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  869 */       String orgColumnName = getOriginalName();
/*  870 */       String orgTableName = getOriginalTableName();
/*      */       
/*  872 */       return (orgColumnName == null || orgColumnName.length() <= 0 || orgTableName == null || orgTableName.length() <= 0);
/*      */     } 
/*      */ 
/*      */     
/*  876 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isUniqueKey() {
/*  885 */     return ((this.colFlag & 0x4) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isUnsigned() {
/*  894 */     return ((this.colFlag & 0x20) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isZeroFill() {
/*  903 */     return ((this.colFlag & 0x40) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setBlobTypeBasedOnLength() {
/*  912 */     if (this.length == 255L) {
/*  913 */       this.mysqlType = 249;
/*  914 */     } else if (this.length == 65535L) {
/*  915 */       this.mysqlType = 252;
/*  916 */     } else if (this.length == 16777215L) {
/*  917 */       this.mysqlType = 250;
/*  918 */     } else if (this.length == 4294967295L) {
/*  919 */       this.mysqlType = 251;
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isNativeNumericType() {
/*  924 */     return ((this.mysqlType >= 1 && this.mysqlType <= 5) || this.mysqlType == 8 || this.mysqlType == 13);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isNativeDateTimeType() {
/*  931 */     return (this.mysqlType == 10 || this.mysqlType == 14 || this.mysqlType == 12 || this.mysqlType == 11 || this.mysqlType == 7);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnection(ConnectionImpl conn) {
/*  945 */     this.connection = conn;
/*      */     
/*  947 */     if (this.charsetName == null || this.charsetIndex == 0) {
/*  948 */       this.charsetName = this.connection.getEncoding();
/*      */     }
/*      */   }
/*      */   
/*      */   void setMysqlType(int type) {
/*  953 */     this.mysqlType = type;
/*  954 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */   }
/*      */   
/*      */   protected void setUseOldNameMetadata(boolean useOldNameMetadata) {
/*  958 */     this.useOldNameMetadata = useOldNameMetadata;
/*      */   }
/*      */   
/*      */   public String toString() {
/*      */     try {
/*  963 */       StringBuffer asString = new StringBuffer();
/*  964 */       asString.append(super.toString());
/*  965 */       asString.append("[");
/*  966 */       asString.append("catalog=");
/*  967 */       asString.append(getDatabaseName());
/*  968 */       asString.append(",tableName=");
/*  969 */       asString.append(getTableName());
/*  970 */       asString.append(",originalTableName=");
/*  971 */       asString.append(getOriginalTableName());
/*  972 */       asString.append(",columnName=");
/*  973 */       asString.append(getName());
/*  974 */       asString.append(",originalColumnName=");
/*  975 */       asString.append(getOriginalName());
/*  976 */       asString.append(",mysqlType=");
/*  977 */       asString.append(getMysqlType());
/*  978 */       asString.append("(");
/*  979 */       asString.append(MysqlDefs.typeToName(getMysqlType()));
/*  980 */       asString.append(")");
/*  981 */       asString.append(",flags=");
/*      */       
/*  983 */       if (isAutoIncrement()) {
/*  984 */         asString.append(" AUTO_INCREMENT");
/*      */       }
/*      */       
/*  987 */       if (isPrimaryKey()) {
/*  988 */         asString.append(" PRIMARY_KEY");
/*      */       }
/*      */       
/*  991 */       if (isUniqueKey()) {
/*  992 */         asString.append(" UNIQUE_KEY");
/*      */       }
/*      */       
/*  995 */       if (isBinary()) {
/*  996 */         asString.append(" BINARY");
/*      */       }
/*      */       
/*  999 */       if (isBlob()) {
/* 1000 */         asString.append(" BLOB");
/*      */       }
/*      */       
/* 1003 */       if (isMultipleKey()) {
/* 1004 */         asString.append(" MULTI_KEY");
/*      */       }
/*      */       
/* 1007 */       if (isUnsigned()) {
/* 1008 */         asString.append(" UNSIGNED");
/*      */       }
/*      */       
/* 1011 */       if (isZeroFill()) {
/* 1012 */         asString.append(" ZEROFILL");
/*      */       }
/*      */       
/* 1015 */       asString.append(", charsetIndex=");
/* 1016 */       asString.append(this.charsetIndex);
/* 1017 */       asString.append(", charsetName=");
/* 1018 */       asString.append(this.charsetName);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1027 */       asString.append("]");
/*      */       
/* 1029 */       return asString.toString();
/* 1030 */     } catch (Throwable t) {
/* 1031 */       return super.toString();
/*      */     } 
/*      */   }
/*      */   
/*      */   protected boolean isSingleBit() {
/* 1036 */     return this.isSingleBit;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Field.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */