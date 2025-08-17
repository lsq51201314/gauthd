/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
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
/*     */ public class ResultSetMetaData
/*     */   implements ResultSetMetaData
/*     */ {
/*     */   Field[] fields;
/*     */   
/*     */   private static int clampedGetLength(Field f) {
/*  40 */     long fieldLength = f.getLength();
/*     */     
/*  42 */     if (fieldLength > 2147483647L) {
/*  43 */       fieldLength = 2147483647L;
/*     */     }
/*     */     
/*  46 */     return (int)fieldLength;
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
/*     */   private static final boolean isDecimalType(int type) {
/*  58 */     switch (type) {
/*     */       case -7:
/*     */       case -6:
/*     */       case -5:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/*  69 */         return true;
/*     */     } 
/*     */     
/*  72 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean useOldAliasBehavior = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetMetaData(Field[] fields, boolean useOldAliasBehavior) {
/*  85 */     this.fields = fields;
/*  86 */     this.useOldAliasBehavior = useOldAliasBehavior;
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
/*     */   public String getCatalogName(int column) throws SQLException {
/* 101 */     Field f = getField(column);
/*     */     
/* 103 */     String database = f.getDatabaseName();
/*     */     
/* 105 */     return (database == null) ? "" : database;
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
/*     */   public String getColumnCharacterEncoding(int column) throws SQLException {
/* 122 */     String mysqlName = getColumnCharacterSet(column);
/*     */     
/* 124 */     String javaName = null;
/*     */     
/* 126 */     if (mysqlName != null) {
/* 127 */       javaName = CharsetMapping.getJavaEncodingForMysqlEncoding(mysqlName, null);
/*     */     }
/*     */ 
/*     */     
/* 131 */     return javaName;
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
/*     */   public String getColumnCharacterSet(int column) throws SQLException {
/* 146 */     return getField(column).getCharacterSet();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getColumnClassName(int column) throws SQLException {
/* 172 */     Field f = getField(column);
/*     */     
/* 174 */     return getClassNameForJavaType(f.getSQLType(), f.isUnsigned(), f.getMysqlType(), (f.isBinary() || f.isBlob()), f.isOpaqueBinary());
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
/*     */   public int getColumnCount() throws SQLException {
/* 190 */     return this.fields.length;
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
/*     */   public int getColumnDisplaySize(int column) throws SQLException {
/* 205 */     Field f = getField(column);
/*     */     
/* 207 */     int lengthInBytes = clampedGetLength(f);
/*     */     
/* 209 */     return lengthInBytes / f.getMaxBytesPerCharacter();
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
/*     */   public String getColumnLabel(int column) throws SQLException {
/* 224 */     if (this.useOldAliasBehavior) {
/* 225 */       return getColumnName(column);
/*     */     }
/*     */     
/* 228 */     return getField(column).getColumnLabel();
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
/*     */   public String getColumnName(int column) throws SQLException {
/* 243 */     if (this.useOldAliasBehavior) {
/* 244 */       return getField(column).getName();
/*     */     }
/*     */     
/* 247 */     String name = getField(column).getNameNoAliases();
/*     */     
/* 249 */     if (name != null && name.length() == 0) {
/* 250 */       return getField(column).getName();
/*     */     }
/*     */     
/* 253 */     return name;
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
/*     */   public int getColumnType(int column) throws SQLException {
/* 270 */     return getField(column).getSQLType();
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
/*     */   public String getColumnTypeName(int column) throws SQLException {
/* 285 */     Field field = getField(column);
/*     */     
/* 287 */     int mysqlType = field.getMysqlType();
/* 288 */     int jdbcType = field.getSQLType();
/*     */     
/* 290 */     switch (mysqlType) {
/*     */       case 16:
/* 292 */         return "BIT";
/*     */       case 0:
/*     */       case 246:
/* 295 */         return field.isUnsigned() ? "DECIMAL UNSIGNED" : "DECIMAL";
/*     */       
/*     */       case 1:
/* 298 */         return field.isUnsigned() ? "TINYINT UNSIGNED" : "TINYINT";
/*     */       
/*     */       case 2:
/* 301 */         return field.isUnsigned() ? "SMALLINT UNSIGNED" : "SMALLINT";
/*     */       
/*     */       case 3:
/* 304 */         return field.isUnsigned() ? "INT UNSIGNED" : "INT";
/*     */       
/*     */       case 4:
/* 307 */         return field.isUnsigned() ? "FLOAT UNSIGNED" : "FLOAT";
/*     */       
/*     */       case 5:
/* 310 */         return field.isUnsigned() ? "DOUBLE UNSIGNED" : "DOUBLE";
/*     */       
/*     */       case 6:
/* 313 */         return "NULL";
/*     */       
/*     */       case 7:
/* 316 */         return "TIMESTAMP";
/*     */       
/*     */       case 8:
/* 319 */         return field.isUnsigned() ? "BIGINT UNSIGNED" : "BIGINT";
/*     */       
/*     */       case 9:
/* 322 */         return field.isUnsigned() ? "MEDIUMINT UNSIGNED" : "MEDIUMINT";
/*     */       
/*     */       case 10:
/* 325 */         return "DATE";
/*     */       
/*     */       case 11:
/* 328 */         return "TIME";
/*     */       
/*     */       case 12:
/* 331 */         return "DATETIME";
/*     */       
/*     */       case 249:
/* 334 */         return "TINYBLOB";
/*     */       
/*     */       case 250:
/* 337 */         return "MEDIUMBLOB";
/*     */       
/*     */       case 251:
/* 340 */         return "LONGBLOB";
/*     */       
/*     */       case 252:
/* 343 */         if (getField(column).isBinary()) {
/* 344 */           return "BLOB";
/*     */         }
/*     */         
/* 347 */         return "TEXT";
/*     */       
/*     */       case 15:
/* 350 */         return "VARCHAR";
/*     */       
/*     */       case 253:
/* 353 */         if (jdbcType == -3) {
/* 354 */           return "VARBINARY";
/*     */         }
/*     */         
/* 357 */         return "VARCHAR";
/*     */       
/*     */       case 254:
/* 360 */         if (jdbcType == -2) {
/* 361 */           return "BINARY";
/*     */         }
/*     */         
/* 364 */         return "CHAR";
/*     */       
/*     */       case 247:
/* 367 */         return "ENUM";
/*     */       
/*     */       case 13:
/* 370 */         return "YEAR";
/*     */       
/*     */       case 248:
/* 373 */         return "SET";
/*     */       
/*     */       case 255:
/* 376 */         return "GEOMETRY";
/*     */     } 
/*     */     
/* 379 */     return "UNKNOWN";
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
/*     */   protected Field getField(int columnIndex) throws SQLException {
/* 395 */     if (columnIndex < 1 || columnIndex > this.fields.length) {
/* 396 */       throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), "S1002");
/*     */     }
/*     */ 
/*     */     
/* 400 */     return this.fields[columnIndex - 1];
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
/*     */   public int getPrecision(int column) throws SQLException {
/* 415 */     Field f = getField(column);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 421 */     if (isDecimalType(f.getSQLType())) {
/* 422 */       if (f.getDecimals() > 0) {
/* 423 */         return clampedGetLength(f) - 1 + f.getPrecisionAdjustFactor();
/*     */       }
/*     */       
/* 426 */       return clampedGetLength(f) + f.getPrecisionAdjustFactor();
/*     */     } 
/*     */     
/* 429 */     switch (f.getMysqlType()) {
/*     */       case 249:
/*     */       case 250:
/*     */       case 251:
/*     */       case 252:
/* 434 */         return clampedGetLength(f);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 441 */     return clampedGetLength(f) / f.getMaxBytesPerCharacter();
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
/*     */   public int getScale(int column) throws SQLException {
/* 458 */     Field f = getField(column);
/*     */     
/* 460 */     if (isDecimalType(f.getSQLType())) {
/* 461 */       return f.getDecimals();
/*     */     }
/*     */     
/* 464 */     return 0;
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
/*     */   public String getSchemaName(int column) throws SQLException {
/* 481 */     return "";
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
/*     */   public String getTableName(int column) throws SQLException {
/* 496 */     if (this.useOldAliasBehavior) {
/* 497 */       return getField(column).getTableName();
/*     */     }
/*     */     
/* 500 */     return getField(column).getTableNameNoAliases();
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
/*     */   public boolean isAutoIncrement(int column) throws SQLException {
/* 515 */     Field f = getField(column);
/*     */     
/* 517 */     return f.isAutoIncrement();
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
/*     */   public boolean isCaseSensitive(int column) throws SQLException {
/*     */     String collationName;
/* 532 */     Field field = getField(column);
/*     */     
/* 534 */     int sqlType = field.getSQLType();
/*     */     
/* 536 */     switch (sqlType) {
/*     */       case -7:
/*     */       case -6:
/*     */       case -5:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/*     */       case 91:
/*     */       case 92:
/*     */       case 93:
/* 548 */         return false;
/*     */ 
/*     */       
/*     */       case -1:
/*     */       case 1:
/*     */       case 12:
/* 554 */         if (field.isBinary()) {
/* 555 */           return true;
/*     */         }
/*     */         
/* 558 */         collationName = field.getCollation();
/*     */         
/* 560 */         return (collationName != null && !collationName.endsWith("_ci"));
/*     */     } 
/*     */     
/* 563 */     return true;
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
/*     */   public boolean isCurrency(int column) throws SQLException {
/* 579 */     return false;
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
/*     */   public boolean isDefinitelyWritable(int column) throws SQLException {
/* 594 */     return isWritable(column);
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
/*     */   public int isNullable(int column) throws SQLException {
/* 609 */     if (!getField(column).isNotNull()) {
/* 610 */       return 1;
/*     */     }
/*     */     
/* 613 */     return 0;
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
/*     */   public boolean isReadOnly(int column) throws SQLException {
/* 628 */     return getField(column).isReadOnly();
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
/*     */   public boolean isSearchable(int column) throws SQLException {
/* 647 */     return true;
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
/*     */   public boolean isSigned(int column) throws SQLException {
/* 662 */     Field f = getField(column);
/* 663 */     int sqlType = f.getSQLType();
/*     */     
/* 665 */     switch (sqlType) {
/*     */       case -6:
/*     */       case -5:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/* 675 */         return !f.isUnsigned();
/*     */       
/*     */       case 91:
/*     */       case 92:
/*     */       case 93:
/* 680 */         return false;
/*     */     } 
/*     */     
/* 683 */     return false;
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
/*     */   public boolean isWritable(int column) throws SQLException {
/* 699 */     return !isReadOnly(column);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 708 */     StringBuffer toStringBuf = new StringBuffer();
/* 709 */     toStringBuf.append(super.toString());
/* 710 */     toStringBuf.append(" - Field level information: ");
/*     */     
/* 712 */     for (int i = 0; i < this.fields.length; i++) {
/* 713 */       toStringBuf.append("\n\t");
/* 714 */       toStringBuf.append(this.fields[i].toString());
/*     */     } 
/*     */     
/* 717 */     return toStringBuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String getClassNameForJavaType(int javaType, boolean isUnsigned, int mysqlTypeIfKnown, boolean isBinaryOrBlob, boolean isOpaqueBinary) {
/* 724 */     switch (javaType) {
/*     */       case -7:
/*     */       case 16:
/* 727 */         return "java.lang.Boolean";
/*     */ 
/*     */       
/*     */       case -6:
/* 731 */         if (isUnsigned) {
/* 732 */           return "java.lang.Integer";
/*     */         }
/*     */         
/* 735 */         return "java.lang.Integer";
/*     */ 
/*     */       
/*     */       case 5:
/* 739 */         if (isUnsigned) {
/* 740 */           return "java.lang.Integer";
/*     */         }
/*     */         
/* 743 */         return "java.lang.Integer";
/*     */ 
/*     */       
/*     */       case 4:
/* 747 */         if (!isUnsigned || mysqlTypeIfKnown == 9)
/*     */         {
/* 749 */           return "java.lang.Integer";
/*     */         }
/*     */         
/* 752 */         return "java.lang.Long";
/*     */ 
/*     */       
/*     */       case -5:
/* 756 */         if (!isUnsigned) {
/* 757 */           return "java.lang.Long";
/*     */         }
/*     */         
/* 760 */         return "java.math.BigInteger";
/*     */       
/*     */       case 2:
/*     */       case 3:
/* 764 */         return "java.math.BigDecimal";
/*     */       
/*     */       case 7:
/* 767 */         return "java.lang.Float";
/*     */       
/*     */       case 6:
/*     */       case 8:
/* 771 */         return "java.lang.Double";
/*     */       
/*     */       case -1:
/*     */       case 1:
/*     */       case 12:
/* 776 */         if (!isOpaqueBinary) {
/* 777 */           return "java.lang.String";
/*     */         }
/*     */         
/* 780 */         return "[B";
/*     */ 
/*     */       
/*     */       case -4:
/*     */       case -3:
/*     */       case -2:
/* 786 */         if (mysqlTypeIfKnown == 255)
/* 787 */           return "[B"; 
/* 788 */         if (isBinaryOrBlob) {
/* 789 */           return "[B";
/*     */         }
/* 791 */         return "java.lang.String";
/*     */ 
/*     */       
/*     */       case 91:
/* 795 */         return "java.sql.Date";
/*     */       
/*     */       case 92:
/* 798 */         return "java.sql.Time";
/*     */       
/*     */       case 93:
/* 801 */         return "java.sql.Timestamp";
/*     */     } 
/*     */     
/* 804 */     return "java.lang.Object";
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
/*     */   public boolean isWrapperFor(Class iface) throws SQLException {
/* 826 */     return iface.isInstance(this);
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
/*     */   public Object unwrap(Class iface) throws SQLException {
/*     */     try {
/* 847 */       return Util.cast(iface, this);
/* 848 */     } catch (ClassCastException cce) {
/* 849 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ResultSetMetaData.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */