/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
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
/*      */ public class DatabaseMetaData
/*      */   implements DatabaseMetaData
/*      */ {
/*      */   private static String mysqlKeywordsThatArentSQL92;
/*      */   protected static final int MAX_IDENTIFIER_LENGTH = 64;
/*      */   private static final int DEFERRABILITY = 13;
/*      */   private static final int DELETE_RULE = 10;
/*      */   private static final int FK_NAME = 11;
/*      */   private static final int FKCOLUMN_NAME = 7;
/*      */   private static final int FKTABLE_CAT = 4;
/*      */   private static final int FKTABLE_NAME = 6;
/*      */   private static final int FKTABLE_SCHEM = 5;
/*      */   private static final int KEY_SEQ = 8;
/*      */   private static final int PK_NAME = 12;
/*      */   private static final int PKCOLUMN_NAME = 3;
/*      */   private static final int PKTABLE_CAT = 0;
/*      */   private static final int PKTABLE_NAME = 2;
/*      */   private static final int PKTABLE_SCHEM = 1;
/*      */   private static final String SUPPORTS_FK = "SUPPORTS_FK";
/*      */   
/*      */   protected abstract class IteratorWithCleanup
/*      */   {
/*      */     private final DatabaseMetaData this$0;
/*      */     
/*      */     protected IteratorWithCleanup(DatabaseMetaData this$0) {
/*   66 */       this.this$0 = this$0;
/*      */     }
/*      */     
/*      */     abstract void close() throws SQLException;
/*      */     
/*      */     abstract boolean hasNext() throws SQLException;
/*      */     
/*      */     abstract Object next() throws SQLException;
/*      */   }
/*      */   
/*      */   class LocalAndReferencedColumns
/*      */   {
/*      */     String constraintName;
/*      */     List localColumnsList;
/*      */     String referencedCatalog;
/*      */     List referencedColumnsList;
/*      */     String referencedTable;
/*      */     private final DatabaseMetaData this$0;
/*      */     
/*      */     LocalAndReferencedColumns(DatabaseMetaData this$0, List localColumns, List refColumns, String constName, String refCatalog, String refTable) {
/*   86 */       this.this$0 = this$0;
/*   87 */       this.localColumnsList = localColumns;
/*   88 */       this.referencedColumnsList = refColumns;
/*   89 */       this.constraintName = constName;
/*   90 */       this.referencedTable = refTable;
/*   91 */       this.referencedCatalog = refCatalog;
/*      */     }
/*      */   }
/*      */   
/*      */   protected class ResultSetIterator extends IteratorWithCleanup { int colIndex;
/*      */     ResultSet resultSet;
/*      */     private final DatabaseMetaData this$0;
/*      */     
/*      */     ResultSetIterator(DatabaseMetaData this$0, ResultSet rs, int index) {
/*  100 */       super(this$0); this.this$0 = this$0;
/*  101 */       this.resultSet = rs;
/*  102 */       this.colIndex = index;
/*      */     }
/*      */     
/*      */     void close() throws SQLException {
/*  106 */       this.resultSet.close();
/*      */     }
/*      */     
/*      */     boolean hasNext() throws SQLException {
/*  110 */       return this.resultSet.next();
/*      */     }
/*      */     
/*      */     Object next() throws SQLException {
/*  114 */       return this.resultSet.getObject(this.colIndex);
/*      */     } }
/*      */   
/*      */   protected class SingleStringIterator extends IteratorWithCleanup {
/*      */     boolean onFirst;
/*      */     String value;
/*      */     private final DatabaseMetaData this$0;
/*      */     
/*      */     SingleStringIterator(DatabaseMetaData this$0, String s) {
/*  123 */       super(this$0); this.this$0 = this$0; this.onFirst = true;
/*  124 */       this.value = s;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void close() throws SQLException {}
/*      */ 
/*      */     
/*      */     boolean hasNext() throws SQLException {
/*  133 */       return this.onFirst;
/*      */     }
/*      */     
/*      */     Object next() throws SQLException {
/*  137 */       this.onFirst = false;
/*  138 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   class TypeDescriptor
/*      */   {
/*      */     int bufferLength;
/*      */     
/*      */     int charOctetLength;
/*      */     
/*      */     Integer columnSize;
/*      */     
/*      */     short dataType;
/*      */     
/*      */     Integer decimalDigits;
/*      */     
/*      */     String isNullable;
/*      */     
/*      */     int nullability;
/*      */     
/*      */     int numPrecRadix;
/*      */     
/*      */     String typeName;
/*      */     
/*      */     private final DatabaseMetaData this$0;
/*      */     
/*      */     TypeDescriptor(DatabaseMetaData this$0, String typeInfo, String nullabilityInfo) throws SQLException {
/*  166 */       this.this$0 = this$0; this.numPrecRadix = 10;
/*  167 */       if (typeInfo == null) {
/*  168 */         throw SQLError.createSQLException("NULL typeinfo not supported.", "S1009");
/*      */       }
/*      */ 
/*      */       
/*  172 */       String mysqlType = "";
/*  173 */       String fullMysqlType = null;
/*      */       
/*  175 */       if (typeInfo.indexOf("(") != -1) {
/*  176 */         mysqlType = typeInfo.substring(0, typeInfo.indexOf("("));
/*      */       } else {
/*  178 */         mysqlType = typeInfo;
/*      */       } 
/*      */       
/*  181 */       int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
/*      */ 
/*      */       
/*  184 */       if (indexOfUnsignedInMysqlType != -1) {
/*  185 */         mysqlType = mysqlType.substring(0, indexOfUnsignedInMysqlType - 1);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  192 */       boolean isUnsigned = false;
/*      */       
/*  194 */       if (StringUtils.indexOfIgnoreCase(typeInfo, "unsigned") != -1) {
/*  195 */         fullMysqlType = mysqlType + " unsigned";
/*  196 */         isUnsigned = true;
/*      */       } else {
/*  198 */         fullMysqlType = mysqlType;
/*      */       } 
/*      */       
/*  201 */       if (this$0.conn.getCapitalizeTypeNames()) {
/*  202 */         fullMysqlType = fullMysqlType.toUpperCase(Locale.ENGLISH);
/*      */       }
/*      */       
/*  205 */       this.dataType = (short)MysqlDefs.mysqlToJavaType(mysqlType);
/*      */       
/*  207 */       this.typeName = fullMysqlType;
/*      */ 
/*      */ 
/*      */       
/*  211 */       if (StringUtils.startsWithIgnoreCase(typeInfo, "enum")) {
/*  212 */         String temp = typeInfo.substring(typeInfo.indexOf("("), typeInfo.lastIndexOf(")"));
/*      */         
/*  214 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */         
/*  216 */         int maxLength = 0;
/*      */         
/*  218 */         while (tokenizer.hasMoreTokens()) {
/*  219 */           maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */         }
/*      */ 
/*      */         
/*  223 */         this.columnSize = Constants.integerValueOf(maxLength);
/*  224 */         this.decimalDigits = null;
/*  225 */       } else if (StringUtils.startsWithIgnoreCase(typeInfo, "set")) {
/*  226 */         String temp = typeInfo.substring(typeInfo.indexOf("("), typeInfo.lastIndexOf(")"));
/*      */         
/*  228 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */         
/*  230 */         int maxLength = 0;
/*      */         
/*  232 */         while (tokenizer.hasMoreTokens()) {
/*  233 */           String setMember = tokenizer.nextToken().trim();
/*      */           
/*  235 */           if (setMember.startsWith("'") && setMember.endsWith("'")) {
/*      */             
/*  237 */             maxLength += setMember.length() - 2; continue;
/*      */           } 
/*  239 */           maxLength += setMember.length();
/*      */         } 
/*      */ 
/*      */         
/*  243 */         this.columnSize = Constants.integerValueOf(maxLength);
/*  244 */         this.decimalDigits = null;
/*  245 */       } else if (typeInfo.indexOf(",") != -1) {
/*      */         
/*  247 */         this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
/*      */         
/*  249 */         this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
/*      */       }
/*      */       else {
/*      */         
/*  253 */         this.columnSize = null;
/*  254 */         this.decimalDigits = null;
/*      */ 
/*      */         
/*  257 */         if ((StringUtils.indexOfIgnoreCase(typeInfo, "char") != -1 || StringUtils.indexOfIgnoreCase(typeInfo, "text") != -1 || StringUtils.indexOfIgnoreCase(typeInfo, "blob") != -1 || StringUtils.indexOfIgnoreCase(typeInfo, "binary") != -1 || StringUtils.indexOfIgnoreCase(typeInfo, "bit") != -1) && typeInfo.indexOf("(") != -1) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  264 */           int endParenIndex = typeInfo.indexOf(")");
/*      */           
/*  266 */           if (endParenIndex == -1) {
/*  267 */             endParenIndex = typeInfo.length();
/*      */           }
/*      */           
/*  270 */           this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
/*      */ 
/*      */ 
/*      */           
/*  274 */           if (this$0.conn.getTinyInt1isBit() && this.columnSize.intValue() == 1 && StringUtils.startsWithIgnoreCase(typeInfo, 0, "tinyint"))
/*      */           {
/*      */ 
/*      */             
/*  278 */             if (this$0.conn.getTransformedBitIsBoolean()) {
/*  279 */               this.dataType = 16;
/*  280 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  282 */               this.dataType = -7;
/*  283 */               this.typeName = "BIT";
/*      */             } 
/*      */           }
/*  286 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyint")) {
/*      */           
/*  288 */           if (this$0.conn.getTinyInt1isBit() && typeInfo.indexOf("(1)") != -1) {
/*  289 */             if (this$0.conn.getTransformedBitIsBoolean()) {
/*  290 */               this.dataType = 16;
/*  291 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  293 */               this.dataType = -7;
/*  294 */               this.typeName = "BIT";
/*      */             } 
/*      */           } else {
/*  297 */             this.columnSize = Constants.integerValueOf(3);
/*  298 */             this.decimalDigits = Constants.integerValueOf(0);
/*      */           } 
/*  300 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "smallint")) {
/*      */           
/*  302 */           this.columnSize = Constants.integerValueOf(5);
/*  303 */           this.decimalDigits = Constants.integerValueOf(0);
/*  304 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumint")) {
/*      */           
/*  306 */           this.columnSize = Constants.integerValueOf(isUnsigned ? 8 : 7);
/*  307 */           this.decimalDigits = Constants.integerValueOf(0);
/*  308 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int")) {
/*      */           
/*  310 */           this.columnSize = Constants.integerValueOf(10);
/*  311 */           this.decimalDigits = Constants.integerValueOf(0);
/*  312 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "integer")) {
/*      */           
/*  314 */           this.columnSize = Constants.integerValueOf(10);
/*  315 */           this.decimalDigits = Constants.integerValueOf(0);
/*  316 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "bigint")) {
/*      */           
/*  318 */           this.columnSize = Constants.integerValueOf(isUnsigned ? 20 : 19);
/*  319 */           this.decimalDigits = Constants.integerValueOf(0);
/*  320 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int24")) {
/*      */           
/*  322 */           this.columnSize = Constants.integerValueOf(19);
/*  323 */           this.decimalDigits = Constants.integerValueOf(0);
/*  324 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "real")) {
/*      */           
/*  326 */           this.columnSize = Constants.integerValueOf(12);
/*  327 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "float")) {
/*      */           
/*  329 */           this.columnSize = Constants.integerValueOf(12);
/*  330 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "decimal")) {
/*      */           
/*  332 */           this.columnSize = Constants.integerValueOf(12);
/*  333 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "numeric")) {
/*      */           
/*  335 */           this.columnSize = Constants.integerValueOf(12);
/*  336 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "double")) {
/*      */           
/*  338 */           this.columnSize = Constants.integerValueOf(22);
/*  339 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "char")) {
/*      */           
/*  341 */           this.columnSize = Constants.integerValueOf(1);
/*  342 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "varchar")) {
/*      */           
/*  344 */           this.columnSize = Constants.integerValueOf(255);
/*  345 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "timestamp")) {
/*      */           
/*  347 */           this.columnSize = Constants.integerValueOf(19);
/*  348 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "datetime")) {
/*      */           
/*  350 */           this.columnSize = Constants.integerValueOf(19);
/*  351 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "date")) {
/*      */           
/*  353 */           this.columnSize = Constants.integerValueOf(10);
/*  354 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "time")) {
/*      */           
/*  356 */           this.columnSize = Constants.integerValueOf(8);
/*      */         }
/*  358 */         else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyblob")) {
/*      */           
/*  360 */           this.columnSize = Constants.integerValueOf(255);
/*  361 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "blob")) {
/*      */           
/*  363 */           this.columnSize = Constants.integerValueOf(65535);
/*  364 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumblob")) {
/*      */           
/*  366 */           this.columnSize = Constants.integerValueOf(16777215);
/*  367 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longblob")) {
/*      */           
/*  369 */           this.columnSize = Constants.integerValueOf(2147483647);
/*  370 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinytext")) {
/*      */           
/*  372 */           this.columnSize = Constants.integerValueOf(255);
/*  373 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "text")) {
/*      */           
/*  375 */           this.columnSize = Constants.integerValueOf(65535);
/*  376 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumtext")) {
/*      */           
/*  378 */           this.columnSize = Constants.integerValueOf(16777215);
/*  379 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longtext")) {
/*      */           
/*  381 */           this.columnSize = Constants.integerValueOf(2147483647);
/*  382 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "enum")) {
/*      */           
/*  384 */           this.columnSize = Constants.integerValueOf(255);
/*  385 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "set")) {
/*      */           
/*  387 */           this.columnSize = Constants.integerValueOf(255);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  393 */       this.bufferLength = MysqlIO.getMaxBuf();
/*      */ 
/*      */       
/*  396 */       this.numPrecRadix = 10;
/*      */ 
/*      */       
/*  399 */       if (nullabilityInfo != null) {
/*  400 */         if (nullabilityInfo.equals("YES")) {
/*  401 */           this.nullability = 1;
/*  402 */           this.isNullable = "YES";
/*      */         }
/*      */         else {
/*      */           
/*  406 */           this.nullability = 0;
/*  407 */           this.isNullable = "NO";
/*      */         } 
/*      */       } else {
/*  410 */         this.nullability = 0;
/*  411 */         this.isNullable = "NO";
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
/*      */ 
/*      */   
/*  453 */   private static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
/*      */   
/*      */   private static final int UPDATE_RULE = 9;
/*      */   
/*  457 */   private static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
/*      */   
/*      */   private static final Constructor JDBC_4_DBMD_SHOW_CTOR;
/*      */   private static final Constructor JDBC_4_DBMD_IS_CTOR;
/*      */   protected ConnectionImpl conn;
/*      */   
/*      */   static {
/*  464 */     if (Util.isJdbc4()) {
/*      */       try {
/*  466 */         JDBC_4_DBMD_SHOW_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaData").getConstructor(new Class[] { ConnectionImpl.class, String.class });
/*      */ 
/*      */ 
/*      */         
/*  470 */         JDBC_4_DBMD_IS_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema").getConstructor(new Class[] { ConnectionImpl.class, String.class });
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  475 */       catch (SecurityException e) {
/*  476 */         throw new RuntimeException(e);
/*  477 */       } catch (NoSuchMethodException e) {
/*  478 */         throw new RuntimeException(e);
/*  479 */       } catch (ClassNotFoundException e) {
/*  480 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*  483 */       JDBC_4_DBMD_IS_CTOR = null;
/*  484 */       JDBC_4_DBMD_SHOW_CTOR = null;
/*      */     } 
/*      */ 
/*      */     
/*  488 */     String[] allMySQLKeywords = { "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_ONLY", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  531 */     String[] sql92Keywords = { "ABSOLUTE", "EXEC", "OVERLAPS", "ACTION", "EXECUTE", "PAD", "ADA", "EXISTS", "PARTIAL", "ADD", "EXTERNAL", "PASCAL", "ALL", "EXTRACT", "POSITION", "ALLOCATE", "FALSE", "PRECISION", "ALTER", "FETCH", "PREPARE", "AND", "FIRST", "PRESERVE", "ANY", "FLOAT", "PRIMARY", "ARE", "FOR", "PRIOR", "AS", "FOREIGN", "PRIVILEGES", "ASC", "FORTRAN", "PROCEDURE", "ASSERTION", "FOUND", "PUBLIC", "AT", "FROM", "READ", "AUTHORIZATION", "FULL", "REAL", "AVG", "GET", "REFERENCES", "BEGIN", "GLOBAL", "RELATIVE", "BETWEEN", "GO", "RESTRICT", "BIT", "GOTO", "REVOKE", "BIT_LENGTH", "GRANT", "RIGHT", "BOTH", "GROUP", "ROLLBACK", "BY", "HAVING", "ROWS", "CASCADE", "HOUR", "SCHEMA", "CASCADED", "IDENTITY", "SCROLL", "CASE", "IMMEDIATE", "SECOND", "CAST", "IN", "SECTION", "CATALOG", "INCLUDE", "SELECT", "CHAR", "INDEX", "SESSION", "CHAR_LENGTH", "INDICATOR", "SESSION_USER", "CHARACTER", "INITIALLY", "SET", "CHARACTER_LENGTH", "INNER", "SIZE", "CHECK", "INPUT", "SMALLINT", "CLOSE", "INSENSITIVE", "SOME", "COALESCE", "INSERT", "SPACE", "COLLATE", "INT", "SQL", "COLLATION", "INTEGER", "SQLCA", "COLUMN", "INTERSECT", "SQLCODE", "COMMIT", "INTERVAL", "SQLERROR", "CONNECT", "INTO", "SQLSTATE", "CONNECTION", "IS", "SQLWARNING", "CONSTRAINT", "ISOLATION", "SUBSTRING", "CONSTRAINTS", "JOIN", "SUM", "CONTINUE", "KEY", "SYSTEM_USER", "CONVERT", "LANGUAGE", "TABLE", "CORRESPONDING", "LAST", "TEMPORARY", "COUNT", "LEADING", "THEN", "CREATE", "LEFT", "TIME", "CROSS", "LEVEL", "TIMESTAMP", "CURRENT", "LIKE", "TIMEZONE_HOUR", "CURRENT_DATE", "LOCAL", "TIMEZONE_MINUTE", "CURRENT_TIME", "LOWER", "TO", "CURRENT_TIMESTAMP", "MATCH", "TRAILING", "CURRENT_USER", "MAX", "TRANSACTION", "CURSOR", "MIN", "TRANSLATE", "DATE", "MINUTE", "TRANSLATION", "DAY", "MODULE", "TRIM", "DEALLOCATE", "MONTH", "TRUE", "DEC", "NAMES", "UNION", "DECIMAL", "NATIONAL", "UNIQUE", "DECLARE", "NATURAL", "UNKNOWN", "DEFAULT", "NCHAR", "UPDATE", "DEFERRABLE", "NEXT", "UPPER", "DEFERRED", "NO", "USAGE", "DELETE", "NONE", "USER", "DESC", "NOT", "USING", "DESCRIBE", "NULL", "VALUE", "DESCRIPTOR", "NULLIF", "VALUES", "DIAGNOSTICS", "NUMERIC", "VARCHAR", "DISCONNECT", "OCTET_LENGTH", "VARYING", "DISTINCT", "OF", "VIEW", "DOMAIN", "ON", "WHEN", "DOUBLE", "ONLY", "WHENEVER", "DROP", "OPEN", "WHERE", "ELSE", "OPTION", "WITH", "END", "OR", "WORK", "END-EXEC", "ORDER", "WRITE", "ESCAPE", "OUTER", "YEAR", "EXCEPT", "OUTPUT", "ZONE", "EXCEPTION" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  573 */     TreeMap mySQLKeywordMap = new TreeMap();
/*      */     
/*  575 */     for (int i = 0; i < allMySQLKeywords.length; i++) {
/*  576 */       mySQLKeywordMap.put(allMySQLKeywords[i], null);
/*      */     }
/*      */     
/*  579 */     HashMap sql92KeywordMap = new HashMap(sql92Keywords.length);
/*      */     
/*  581 */     for (int j = 0; j < sql92Keywords.length; j++) {
/*  582 */       sql92KeywordMap.put(sql92Keywords[j], null);
/*      */     }
/*      */     
/*  585 */     Iterator it = sql92KeywordMap.keySet().iterator();
/*      */     
/*  587 */     while (it.hasNext()) {
/*  588 */       mySQLKeywordMap.remove(it.next());
/*      */     }
/*      */     
/*  591 */     StringBuffer keywordBuf = new StringBuffer();
/*      */     
/*  593 */     it = mySQLKeywordMap.keySet().iterator();
/*      */     
/*  595 */     if (it.hasNext()) {
/*  596 */       keywordBuf.append(it.next().toString());
/*      */     }
/*      */     
/*  599 */     while (it.hasNext()) {
/*  600 */       keywordBuf.append(",");
/*  601 */       keywordBuf.append(it.next().toString());
/*      */     } 
/*      */     
/*  604 */     mysqlKeywordsThatArentSQL92 = keywordBuf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  611 */   protected String database = null;
/*      */ 
/*      */   
/*  614 */   protected String quotedId = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static DatabaseMetaData getInstance(ConnectionImpl connToSet, String databaseToSet, boolean checkForInfoSchema) throws SQLException {
/*  622 */     if (!Util.isJdbc4()) {
/*  623 */       if (checkForInfoSchema && connToSet != null && connToSet.getUseInformationSchema() && connToSet.versionMeetsMinimum(5, 0, 7))
/*      */       {
/*      */         
/*  626 */         return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet);
/*      */       }
/*      */ 
/*      */       
/*  630 */       return new DatabaseMetaData(connToSet, databaseToSet);
/*      */     } 
/*      */     
/*  633 */     if (checkForInfoSchema && connToSet != null && connToSet.getUseInformationSchema() && connToSet.versionMeetsMinimum(5, 0, 7))
/*      */     {
/*      */ 
/*      */       
/*  637 */       return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_IS_CTOR, new Object[] { connToSet, databaseToSet });
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  642 */     return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_SHOW_CTOR, new Object[] { connToSet, databaseToSet });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DatabaseMetaData(ConnectionImpl connToSet, String databaseToSet) {
/*  655 */     this.conn = connToSet;
/*  656 */     this.database = databaseToSet;
/*      */     
/*      */     try {
/*  659 */       this.quotedId = this.conn.supportsQuotedIdentifiers() ? getIdentifierQuoteString() : "";
/*      */     }
/*  661 */     catch (SQLException sqlEx) {
/*      */ 
/*      */ 
/*      */       
/*  665 */       AssertionFailedException.shouldNotHappen(sqlEx);
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
/*      */   public boolean allProceduresAreCallable() throws SQLException {
/*  678 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean allTablesAreSelectable() throws SQLException {
/*  689 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private ResultSet buildResultSet(Field[] fields, ArrayList rows) throws SQLException {
/*  694 */     return buildResultSet(fields, rows, this.conn);
/*      */   }
/*      */ 
/*      */   
/*      */   static ResultSet buildResultSet(Field[] fields, ArrayList rows, ConnectionImpl c) throws SQLException {
/*  699 */     int fieldsLength = fields.length;
/*      */     
/*  701 */     for (int i = 0; i < fieldsLength; i++) {
/*  702 */       int jdbcType = fields[i].getSQLType();
/*      */       
/*  704 */       switch (jdbcType) {
/*      */         case -1:
/*      */         case 1:
/*      */         case 12:
/*  708 */           fields[i].setCharacterSet(c.getCharacterSetMetadata());
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  714 */       fields[i].setConnection(c);
/*  715 */       fields[i].setUseOldNameMetadata(true);
/*      */     } 
/*      */     
/*  718 */     return ResultSetImpl.getInstance(c.getCatalog(), fields, new RowDataStatic(rows), c, null, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void convertToJdbcFunctionList(String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, Map procedureRowsOrderedByName, int nameIndex, Field[] fields) throws SQLException {
/*  726 */     while (proceduresRs.next()) {
/*  727 */       boolean shouldAdd = true;
/*      */       
/*  729 */       if (needsClientFiltering) {
/*  730 */         shouldAdd = false;
/*      */         
/*  732 */         String procDb = proceduresRs.getString(1);
/*      */         
/*  734 */         if (db == null && procDb == null) {
/*  735 */           shouldAdd = true;
/*  736 */         } else if (db != null && db.equals(procDb)) {
/*  737 */           shouldAdd = true;
/*      */         } 
/*      */       } 
/*      */       
/*  741 */       if (shouldAdd) {
/*  742 */         String functionName = proceduresRs.getString(nameIndex);
/*      */         
/*  744 */         byte[][] rowData = (byte[][])null;
/*      */         
/*  746 */         if (fields != null && fields.length == 9) {
/*      */           
/*  748 */           rowData = new byte[8][];
/*  749 */           rowData[0] = (catalog == null) ? null : s2b(catalog);
/*  750 */           rowData[1] = null;
/*  751 */           rowData[2] = s2b(functionName);
/*  752 */           rowData[3] = null;
/*  753 */           rowData[4] = null;
/*  754 */           rowData[5] = null;
/*  755 */           rowData[6] = s2b(proceduresRs.getString("comment"));
/*  756 */           rowData[7] = s2b(Integer.toString(2));
/*  757 */           rowData[8] = s2b(functionName);
/*      */         } else {
/*      */           
/*  760 */           rowData = new byte[6][];
/*      */           
/*  762 */           rowData[0] = (catalog == null) ? null : s2b(catalog);
/*  763 */           rowData[1] = null;
/*  764 */           rowData[2] = s2b(functionName);
/*  765 */           rowData[3] = s2b(proceduresRs.getString("comment"));
/*  766 */           rowData[4] = s2b(Integer.toString(getJDBC4FunctionNoTableConstant()));
/*  767 */           rowData[5] = s2b(functionName);
/*      */         } 
/*      */         
/*  770 */         procedureRowsOrderedByName.put(functionName, new ByteArrayRow(rowData));
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected int getJDBC4FunctionNoTableConstant() {
/*  776 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void convertToJdbcProcedureList(boolean fromSelect, String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, Map procedureRowsOrderedByName, int nameIndex) throws SQLException {
/*  782 */     while (proceduresRs.next()) {
/*  783 */       boolean shouldAdd = true;
/*      */       
/*  785 */       if (needsClientFiltering) {
/*  786 */         shouldAdd = false;
/*      */         
/*  788 */         String procDb = proceduresRs.getString(1);
/*      */         
/*  790 */         if (db == null && procDb == null) {
/*  791 */           shouldAdd = true;
/*  792 */         } else if (db != null && db.equals(procDb)) {
/*  793 */           shouldAdd = true;
/*      */         } 
/*      */       } 
/*      */       
/*  797 */       if (shouldAdd) {
/*  798 */         String procedureName = proceduresRs.getString(nameIndex);
/*  799 */         byte[][] rowData = new byte[9][];
/*  800 */         rowData[0] = (catalog == null) ? null : s2b(catalog);
/*  801 */         rowData[1] = null;
/*  802 */         rowData[2] = s2b(procedureName);
/*  803 */         rowData[3] = null;
/*  804 */         rowData[4] = null;
/*  805 */         rowData[5] = null;
/*  806 */         rowData[6] = null;
/*      */         
/*  808 */         boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
/*      */ 
/*      */         
/*  811 */         rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(0));
/*      */ 
/*      */ 
/*      */         
/*  815 */         rowData[8] = s2b(procedureName);
/*      */         
/*  817 */         procedureRowsOrderedByName.put(procedureName, new ByteArrayRow(rowData));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultSetRow convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal) throws SQLException {
/*  828 */     byte[][] row = forGetFunctionColumns ? new byte[17][] : new byte[14][];
/*  829 */     row[0] = null;
/*  830 */     row[1] = null;
/*  831 */     row[2] = procNameAsBytes;
/*  832 */     row[3] = s2b(paramName);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  839 */     if (isInParam && isOutParam) {
/*  840 */       row[4] = s2b(String.valueOf(2));
/*  841 */     } else if (isInParam) {
/*  842 */       row[4] = s2b(String.valueOf(1));
/*  843 */     } else if (isOutParam) {
/*  844 */       row[4] = s2b(String.valueOf(4));
/*  845 */     } else if (isReturnParam) {
/*  846 */       row[4] = s2b(String.valueOf(5));
/*      */     } else {
/*  848 */       row[4] = s2b(String.valueOf(0));
/*      */     } 
/*  850 */     row[5] = s2b(Short.toString(typeDesc.dataType));
/*  851 */     row[6] = s2b(typeDesc.typeName);
/*  852 */     row[7] = (typeDesc.columnSize == null) ? null : s2b(typeDesc.columnSize.toString());
/*  853 */     row[8] = s2b(Integer.toString(typeDesc.bufferLength));
/*  854 */     row[9] = (typeDesc.decimalDigits == null) ? null : s2b(typeDesc.decimalDigits.toString());
/*  855 */     row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */     
/*  857 */     switch (typeDesc.nullability) {
/*      */       case 0:
/*  859 */         row[11] = s2b(String.valueOf(0));
/*      */         break;
/*      */ 
/*      */       
/*      */       case 1:
/*  864 */         row[11] = s2b(String.valueOf(1));
/*      */         break;
/*      */ 
/*      */       
/*      */       case 2:
/*  869 */         row[11] = s2b(String.valueOf(2));
/*      */         break;
/*      */ 
/*      */       
/*      */       default:
/*  874 */         throw SQLError.createSQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)", "S1000");
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  879 */     row[12] = null;
/*      */     
/*  881 */     if (forGetFunctionColumns) {
/*      */       
/*  883 */       row[13] = null;
/*      */ 
/*      */       
/*  886 */       row[14] = s2b(String.valueOf(ordinal));
/*      */ 
/*      */       
/*  889 */       row[15] = Constants.EMPTY_BYTE_ARRAY;
/*      */       
/*  891 */       row[16] = s2b(paramName);
/*      */     } 
/*      */     
/*  894 */     return new ByteArrayRow(row);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
/*  906 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
/*  917 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean deletesAreDetected(int type) throws SQLException {
/*  932 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
/*  945 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List extractForeignKeyForTable(ArrayList rows, ResultSet rs, String catalog) throws SQLException {
/*  963 */     byte[][] row = new byte[3][];
/*  964 */     row[0] = rs.getBytes(1);
/*  965 */     row[1] = s2b("SUPPORTS_FK");
/*      */     
/*  967 */     String createTableString = rs.getString(2);
/*  968 */     StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/*      */     
/*  970 */     StringBuffer commentBuf = new StringBuffer("comment; ");
/*  971 */     boolean firstTime = true;
/*      */     
/*  973 */     String quoteChar = getIdentifierQuoteString();
/*      */     
/*  975 */     if (quoteChar == null) {
/*  976 */       quoteChar = "`";
/*      */     }
/*      */     
/*  979 */     while (lineTokenizer.hasMoreTokens()) {
/*  980 */       String line = lineTokenizer.nextToken().trim();
/*      */       
/*  982 */       String constraintName = null;
/*      */       
/*  984 */       if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
/*  985 */         boolean usingBackTicks = true;
/*  986 */         int beginPos = line.indexOf(quoteChar);
/*      */         
/*  988 */         if (beginPos == -1) {
/*  989 */           beginPos = line.indexOf("\"");
/*  990 */           usingBackTicks = false;
/*      */         } 
/*      */         
/*  993 */         if (beginPos != -1) {
/*  994 */           int endPos = -1;
/*      */           
/*  996 */           if (usingBackTicks) {
/*  997 */             endPos = line.indexOf(quoteChar, beginPos + 1);
/*      */           } else {
/*  999 */             endPos = line.indexOf("\"", beginPos + 1);
/*      */           } 
/*      */           
/* 1002 */           if (endPos != -1) {
/* 1003 */             constraintName = line.substring(beginPos + 1, endPos);
/* 1004 */             line = line.substring(endPos + 1, line.length()).trim();
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1010 */       if (line.startsWith("FOREIGN KEY")) {
/* 1011 */         if (line.endsWith(",")) {
/* 1012 */           line = line.substring(0, line.length() - 1);
/*      */         }
/*      */         
/* 1015 */         char quote = this.quotedId.charAt(0);
/*      */         
/* 1017 */         int indexOfFK = line.indexOf("FOREIGN KEY");
/*      */         
/* 1019 */         String localColumnName = null;
/* 1020 */         String referencedCatalogName = this.quotedId + catalog + this.quotedId;
/* 1021 */         String referencedTableName = null;
/* 1022 */         String referencedColumnName = null;
/*      */ 
/*      */         
/* 1025 */         if (indexOfFK != -1) {
/* 1026 */           int afterFk = indexOfFK + "FOREIGN KEY".length();
/*      */           
/* 1028 */           int indexOfRef = StringUtils.indexOfIgnoreCaseRespectQuotes(afterFk, line, "REFERENCES", quote, true);
/*      */           
/* 1030 */           if (indexOfRef != -1) {
/*      */             
/* 1032 */             int indexOfParenOpen = line.indexOf('(', afterFk);
/* 1033 */             int indexOfParenClose = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfParenOpen, line, ")", quote, true);
/*      */             
/* 1035 */             if (indexOfParenOpen == -1 || indexOfParenClose == -1);
/*      */ 
/*      */ 
/*      */             
/* 1039 */             localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
/*      */             
/* 1041 */             int afterRef = indexOfRef + "REFERENCES".length();
/*      */             
/* 1043 */             int referencedColumnBegin = StringUtils.indexOfIgnoreCaseRespectQuotes(afterRef, line, "(", quote, true);
/*      */             
/* 1045 */             if (referencedColumnBegin != -1) {
/* 1046 */               referencedTableName = line.substring(afterRef, referencedColumnBegin);
/*      */               
/* 1048 */               int referencedColumnEnd = StringUtils.indexOfIgnoreCaseRespectQuotes(referencedColumnBegin + 1, line, ")", quote, true);
/*      */               
/* 1050 */               if (referencedColumnEnd != -1) {
/* 1051 */                 referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd);
/*      */               }
/*      */               
/* 1054 */               int indexOfCatalogSep = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referencedTableName, ".", quote, true);
/*      */               
/* 1056 */               if (indexOfCatalogSep != -1) {
/* 1057 */                 referencedCatalogName = referencedTableName.substring(0, indexOfCatalogSep);
/* 1058 */                 referencedTableName = referencedTableName.substring(indexOfCatalogSep + 1);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1065 */         if (!firstTime) {
/* 1066 */           commentBuf.append("; ");
/*      */         } else {
/* 1068 */           firstTime = false;
/*      */         } 
/*      */         
/* 1071 */         if (constraintName != null) {
/* 1072 */           commentBuf.append(constraintName);
/*      */         } else {
/* 1074 */           commentBuf.append("not_available");
/*      */         } 
/*      */         
/* 1077 */         commentBuf.append("(");
/* 1078 */         commentBuf.append(localColumnName);
/* 1079 */         commentBuf.append(") REFER ");
/* 1080 */         commentBuf.append(referencedCatalogName);
/* 1081 */         commentBuf.append("/");
/* 1082 */         commentBuf.append(referencedTableName);
/* 1083 */         commentBuf.append("(");
/* 1084 */         commentBuf.append(referencedColumnName);
/* 1085 */         commentBuf.append(")");
/*      */         
/* 1087 */         int lastParenIndex = line.lastIndexOf(")");
/*      */         
/* 1089 */         if (lastParenIndex != line.length() - 1) {
/* 1090 */           String cascadeOptions = line.substring(lastParenIndex + 1);
/*      */           
/* 1092 */           commentBuf.append(" ");
/* 1093 */           commentBuf.append(cascadeOptions);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1098 */     row[2] = s2b(commentBuf.toString());
/* 1099 */     rows.add(new ByteArrayRow(row));
/*      */     
/* 1101 */     return rows;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName) throws SQLException {
/* 1122 */     ArrayList tableList = new ArrayList();
/* 1123 */     ResultSet rs = null;
/* 1124 */     Statement stmt = null;
/*      */     
/* 1126 */     if (tableName != null) {
/* 1127 */       tableList.add(tableName);
/*      */     } else {
/*      */       try {
/* 1130 */         rs = getTables(catalog, "", "%", new String[] { "TABLE" });
/*      */         
/* 1132 */         while (rs.next()) {
/* 1133 */           tableList.add(rs.getString("TABLE_NAME"));
/*      */         }
/*      */       } finally {
/* 1136 */         if (rs != null) {
/* 1137 */           rs.close();
/*      */         }
/*      */         
/* 1140 */         rs = null;
/*      */       } 
/*      */     } 
/*      */     
/* 1144 */     ArrayList rows = new ArrayList();
/* 1145 */     Field[] fields = new Field[3];
/* 1146 */     fields[0] = new Field("", "Name", 1, 2147483647);
/* 1147 */     fields[1] = new Field("", "Type", 1, 255);
/* 1148 */     fields[2] = new Field("", "Comment", 1, 2147483647);
/*      */     
/* 1150 */     int numTables = tableList.size();
/* 1151 */     stmt = this.conn.getMetadataSafeStatement();
/*      */     
/* 1153 */     String quoteChar = getIdentifierQuoteString();
/*      */     
/* 1155 */     if (quoteChar == null) {
/* 1156 */       quoteChar = "`";
/*      */     }
/*      */     
/*      */     try {
/* 1160 */       for (int i = 0; i < numTables; i++) {
/* 1161 */         String tableToExtract = tableList.get(i);
/*      */         
/* 1163 */         String query = "SHOW CREATE TABLE " + quoteChar + catalog + quoteChar + "." + quoteChar + tableToExtract + quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1169 */           rs = stmt.executeQuery(query);
/* 1170 */         } catch (SQLException sqlEx) {
/*      */           
/* 1172 */           String sqlState = sqlEx.getSQLState();
/*      */           
/* 1174 */           if (!"42S02".equals(sqlState) && sqlEx.getErrorCode() != 1146)
/*      */           {
/* 1176 */             throw sqlEx;
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1182 */         while (rs.next()) {
/* 1183 */           extractForeignKeyForTable(rows, rs, catalog);
/*      */         }
/*      */       } 
/*      */     } finally {
/* 1187 */       if (rs != null) {
/* 1188 */         rs.close();
/*      */       }
/*      */       
/* 1191 */       rs = null;
/*      */       
/* 1193 */       if (stmt != null) {
/* 1194 */         stmt.close();
/*      */       }
/*      */       
/* 1197 */       stmt = null;
/*      */     } 
/*      */     
/* 1200 */     return buildResultSet(fields, rows);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3) throws SQLException {
/* 1208 */     Field[] fields = new Field[21];
/* 1209 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 1210 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 1211 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 1212 */     fields[3] = new Field("", "ATTR_NAME", 1, 32);
/* 1213 */     fields[4] = new Field("", "DATA_TYPE", 5, 32);
/* 1214 */     fields[5] = new Field("", "ATTR_TYPE_NAME", 1, 32);
/* 1215 */     fields[6] = new Field("", "ATTR_SIZE", 4, 32);
/* 1216 */     fields[7] = new Field("", "DECIMAL_DIGITS", 4, 32);
/* 1217 */     fields[8] = new Field("", "NUM_PREC_RADIX", 4, 32);
/* 1218 */     fields[9] = new Field("", "NULLABLE ", 4, 32);
/* 1219 */     fields[10] = new Field("", "REMARKS", 1, 32);
/* 1220 */     fields[11] = new Field("", "ATTR_DEF", 1, 32);
/* 1221 */     fields[12] = new Field("", "SQL_DATA_TYPE", 4, 32);
/* 1222 */     fields[13] = new Field("", "SQL_DATETIME_SUB", 4, 32);
/* 1223 */     fields[14] = new Field("", "CHAR_OCTET_LENGTH", 4, 32);
/* 1224 */     fields[15] = new Field("", "ORDINAL_POSITION", 4, 32);
/* 1225 */     fields[16] = new Field("", "IS_NULLABLE", 1, 32);
/* 1226 */     fields[17] = new Field("", "SCOPE_CATALOG", 1, 32);
/* 1227 */     fields[18] = new Field("", "SCOPE_SCHEMA", 1, 32);
/* 1228 */     fields[19] = new Field("", "SCOPE_TABLE", 1, 32);
/* 1229 */     fields[20] = new Field("", "SOURCE_DATA_TYPE", 5, 32);
/*      */     
/* 1231 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
/* 1282 */     if (table == null) {
/* 1283 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 1287 */     Field[] fields = new Field[8];
/* 1288 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 1289 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 1290 */     fields[2] = new Field("", "DATA_TYPE", 5, 32);
/* 1291 */     fields[3] = new Field("", "TYPE_NAME", 1, 32);
/* 1292 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 10);
/* 1293 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 1294 */     fields[6] = new Field("", "DECIMAL_DIGITS", 4, 10);
/* 1295 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */     
/* 1297 */     ArrayList rows = new ArrayList();
/* 1298 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */     
/*      */     try {
/* 1302 */       (new IterateBlock(this, getCatalogIterator(catalog), table, stmt, rows) { private final String val$table; private final Statement val$stmt; private final ArrayList val$rows; private final DatabaseMetaData this$0;
/*      */           void forEach(Object catalogStr) throws SQLException {
/* 1304 */             ResultSet results = null;
/*      */             
/*      */             try {
/* 1307 */               StringBuffer queryBuf = new StringBuffer("SHOW COLUMNS FROM ");
/*      */               
/* 1309 */               queryBuf.append(this.this$0.quotedId);
/* 1310 */               queryBuf.append(this.val$table);
/* 1311 */               queryBuf.append(this.this$0.quotedId);
/* 1312 */               queryBuf.append(" FROM ");
/* 1313 */               queryBuf.append(this.this$0.quotedId);
/* 1314 */               queryBuf.append(catalogStr.toString());
/* 1315 */               queryBuf.append(this.this$0.quotedId);
/*      */               
/* 1317 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               
/* 1319 */               while (results.next()) {
/* 1320 */                 String keyType = results.getString("Key");
/*      */                 
/* 1322 */                 if (keyType != null && 
/* 1323 */                   StringUtils.startsWithIgnoreCase(keyType, "PRI"))
/*      */                 {
/* 1325 */                   byte[][] rowVal = new byte[8][];
/* 1326 */                   rowVal[0] = Integer.toString(2).getBytes();
/*      */ 
/*      */ 
/*      */                   
/* 1330 */                   rowVal[1] = results.getBytes("Field");
/*      */                   
/* 1332 */                   String type = results.getString("Type");
/* 1333 */                   int size = MysqlIO.getMaxBuf();
/* 1334 */                   int decimals = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/* 1339 */                   if (type.indexOf("enum") != -1) {
/* 1340 */                     String temp = type.substring(type.indexOf("("), type.indexOf(")"));
/*      */ 
/*      */                     
/* 1343 */                     StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */                     
/* 1345 */                     int maxLength = 0;
/*      */                     
/* 1347 */                     while (tokenizer.hasMoreTokens()) {
/* 1348 */                       maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */                     }
/*      */ 
/*      */ 
/*      */                     
/* 1353 */                     size = maxLength;
/* 1354 */                     decimals = 0;
/* 1355 */                     type = "enum";
/* 1356 */                   } else if (type.indexOf("(") != -1) {
/* 1357 */                     if (type.indexOf(",") != -1) {
/* 1358 */                       size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
/*      */ 
/*      */ 
/*      */                       
/* 1362 */                       decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
/*      */                     
/*      */                     }
/*      */                     else {
/*      */                       
/* 1367 */                       size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
/*      */                     } 
/*      */ 
/*      */ 
/*      */ 
/*      */                     
/* 1373 */                     type = type.substring(0, type.indexOf("("));
/*      */                   } 
/*      */ 
/*      */                   
/* 1377 */                   rowVal[2] = this.this$0.s2b(String.valueOf(MysqlDefs.mysqlToJavaType(type)));
/*      */                   
/* 1379 */                   rowVal[3] = this.this$0.s2b(type);
/* 1380 */                   rowVal[4] = Integer.toString(size + decimals).getBytes();
/*      */                   
/* 1382 */                   rowVal[5] = Integer.toString(size + decimals).getBytes();
/*      */                   
/* 1384 */                   rowVal[6] = Integer.toString(decimals).getBytes();
/*      */                   
/* 1386 */                   rowVal[7] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/* 1391 */                   this.val$rows.add(new ByteArrayRow(rowVal));
/*      */                 }
/*      */               
/*      */               } 
/*      */             } finally {
/*      */               
/* 1397 */               if (results != null) {
/*      */                 try {
/* 1399 */                   results.close();
/* 1400 */                 } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */                 
/* 1404 */                 results = null;
/*      */               } 
/*      */             } 
/*      */           } }
/*      */         ).doForAll();
/*      */     } finally {
/* 1410 */       if (stmt != null) {
/* 1411 */         stmt.close();
/*      */       }
/*      */     } 
/*      */     
/* 1415 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 1417 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List resultRows) throws SQLException {
/* 1455 */     getCallStmtParameterTypes(catalog, procName, parameterNamePattern, resultRows, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List resultRows, boolean forGetFunctionColumns) throws SQLException {
/* 1462 */     Statement paramRetrievalStmt = null;
/* 1463 */     ResultSet paramRetrievalRs = null;
/*      */     
/* 1465 */     if (parameterNamePattern == null) {
/* 1466 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1467 */         parameterNamePattern = "%";
/*      */       } else {
/* 1469 */         throw SQLError.createSQLException("Parameter/Column name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1475 */     byte[] procNameAsBytes = null;
/*      */     
/*      */     try {
/* 1478 */       procNameAsBytes = procName.getBytes("UTF-8");
/* 1479 */     } catch (UnsupportedEncodingException ueEx) {
/* 1480 */       procNameAsBytes = s2b(procName);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1485 */     String quoteChar = getIdentifierQuoteString();
/*      */     
/* 1487 */     String parameterDef = null;
/*      */     
/* 1489 */     boolean isProcedureInAnsiMode = false;
/* 1490 */     String storageDefnDelims = null;
/* 1491 */     String storageDefnClosures = null;
/*      */     
/*      */     try {
/* 1494 */       paramRetrievalStmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 1496 */       if (this.conn.lowerCaseTableNames() && catalog != null && catalog.length() != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1502 */         String oldCatalog = this.conn.getCatalog();
/* 1503 */         ResultSet rs = null;
/*      */         
/*      */         try {
/* 1506 */           this.conn.setCatalog(catalog);
/* 1507 */           rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
/* 1508 */           rs.next();
/*      */           
/* 1510 */           catalog = rs.getString(1);
/*      */         }
/*      */         finally {
/*      */           
/* 1514 */           this.conn.setCatalog(oldCatalog);
/*      */           
/* 1516 */           if (rs != null) {
/* 1517 */             rs.close();
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1522 */       if (paramRetrievalStmt.getMaxRows() != 0) {
/* 1523 */         paramRetrievalStmt.setMaxRows(0);
/*      */       }
/*      */       
/* 1526 */       int dotIndex = -1;
/*      */       
/* 1528 */       if (!" ".equals(quoteChar)) {
/* 1529 */         dotIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procName, ".", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */       }
/*      */       else {
/*      */         
/* 1533 */         dotIndex = procName.indexOf(".");
/*      */       } 
/*      */       
/* 1536 */       String dbName = null;
/*      */       
/* 1538 */       if (dotIndex != -1 && dotIndex + 1 < procName.length()) {
/* 1539 */         dbName = procName.substring(0, dotIndex);
/* 1540 */         procName = procName.substring(dotIndex + 1);
/*      */       } else {
/* 1542 */         dbName = catalog;
/*      */       } 
/*      */       
/* 1545 */       StringBuffer procNameBuf = new StringBuffer();
/*      */       
/* 1547 */       if (dbName != null) {
/* 1548 */         if (!" ".equals(quoteChar) && !dbName.startsWith(quoteChar)) {
/* 1549 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */         
/* 1552 */         procNameBuf.append(dbName);
/*      */         
/* 1554 */         if (!" ".equals(quoteChar) && !dbName.startsWith(quoteChar)) {
/* 1555 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */         
/* 1558 */         procNameBuf.append(".");
/*      */       } 
/*      */       
/* 1561 */       boolean procNameIsNotQuoted = !procName.startsWith(quoteChar);
/*      */       
/* 1563 */       if (!" ".equals(quoteChar) && procNameIsNotQuoted) {
/* 1564 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */       
/* 1567 */       procNameBuf.append(procName);
/*      */       
/* 1569 */       if (!" ".equals(quoteChar) && procNameIsNotQuoted) {
/* 1570 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */       
/* 1573 */       boolean parsingFunction = false;
/*      */       
/*      */       try {
/* 1576 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
/*      */ 
/*      */         
/* 1579 */         parsingFunction = false;
/* 1580 */       } catch (SQLException sqlEx) {
/* 1581 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
/*      */ 
/*      */         
/* 1584 */         parsingFunction = true;
/*      */       } 
/*      */       
/* 1587 */       if (paramRetrievalRs.next()) {
/* 1588 */         String procedureDef = parsingFunction ? paramRetrievalRs.getString("Create Function") : paramRetrievalRs.getString("Create Procedure");
/*      */ 
/*      */ 
/*      */         
/* 1592 */         if (procedureDef == null || procedureDef.length() == 0) {
/* 1593 */           throw SQLError.createSQLException("User does not have access to metadata required to determine stored procedure parameter types. If rights can not be granted, configure connection with \"noAccessToProcedureBodies=true\" to have driver generate parameters that represent INOUT strings irregardless of actual parameter types.", "S1000");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1600 */           String sqlMode = paramRetrievalRs.getString("sql_mode");
/*      */           
/* 1602 */           if (StringUtils.indexOfIgnoreCase(sqlMode, "ANSI") != -1) {
/* 1603 */             isProcedureInAnsiMode = true;
/*      */           }
/* 1605 */         } catch (SQLException sqlEx) {}
/*      */ 
/*      */ 
/*      */         
/* 1609 */         String identifierMarkers = isProcedureInAnsiMode ? "`\"" : "`";
/* 1610 */         String identifierAndStringMarkers = "'" + identifierMarkers;
/* 1611 */         storageDefnDelims = "(" + identifierMarkers;
/* 1612 */         storageDefnClosures = ")" + identifierMarkers;
/*      */ 
/*      */         
/* 1615 */         procedureDef = StringUtils.stripComments(procedureDef, identifierAndStringMarkers, identifierAndStringMarkers, true, false, true, true);
/*      */ 
/*      */         
/* 1618 */         int openParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/*      */ 
/*      */         
/* 1622 */         int endOfParamDeclarationIndex = 0;
/*      */         
/* 1624 */         endOfParamDeclarationIndex = endPositionOfParameterDeclaration(openParenIndex, procedureDef, quoteChar);
/*      */ 
/*      */         
/* 1627 */         if (parsingFunction) {
/*      */ 
/*      */ 
/*      */           
/* 1631 */           int returnsIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, " RETURNS ", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1636 */           int endReturnsDef = findEndOfReturnsClause(procedureDef, quoteChar, returnsIndex);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1641 */           int declarationStart = returnsIndex + "RETURNS ".length();
/*      */           
/* 1643 */           while (declarationStart < procedureDef.length() && 
/* 1644 */             Character.isWhitespace(procedureDef.charAt(declarationStart))) {
/* 1645 */             declarationStart++;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1651 */           String returnsDefn = procedureDef.substring(declarationStart, endReturnsDef).trim();
/* 1652 */           TypeDescriptor returnDescriptor = new TypeDescriptor(this, returnsDefn, null);
/*      */ 
/*      */           
/* 1655 */           resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, "", false, false, true, returnDescriptor, forGetFunctionColumns, 0));
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1660 */         if (openParenIndex == -1 || endOfParamDeclarationIndex == -1)
/*      */         {
/*      */           
/* 1663 */           throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1669 */         parameterDef = procedureDef.substring(openParenIndex + 1, endOfParamDeclarationIndex);
/*      */       } 
/*      */     } finally {
/*      */       
/* 1673 */       SQLException sqlExRethrow = null;
/*      */       
/* 1675 */       if (paramRetrievalRs != null) {
/*      */         try {
/* 1677 */           paramRetrievalRs.close();
/* 1678 */         } catch (SQLException sqlEx) {
/* 1679 */           sqlExRethrow = sqlEx;
/*      */         } 
/*      */         
/* 1682 */         paramRetrievalRs = null;
/*      */       } 
/*      */       
/* 1685 */       if (paramRetrievalStmt != null) {
/*      */         try {
/* 1687 */           paramRetrievalStmt.close();
/* 1688 */         } catch (SQLException sqlEx) {
/* 1689 */           sqlExRethrow = sqlEx;
/*      */         } 
/*      */         
/* 1692 */         paramRetrievalStmt = null;
/*      */       } 
/*      */       
/* 1695 */       if (sqlExRethrow != null) {
/* 1696 */         throw sqlExRethrow;
/*      */       }
/*      */     } 
/*      */     
/* 1700 */     if (parameterDef != null) {
/* 1701 */       int ordinal = 1;
/*      */       
/* 1703 */       List parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
/*      */ 
/*      */       
/* 1706 */       int parseListLen = parseList.size();
/*      */       
/* 1708 */       for (int i = 0; i < parseListLen; i++) {
/* 1709 */         String declaration = parseList.get(i);
/*      */         
/* 1711 */         if (declaration.trim().length() == 0) {
/*      */           break;
/*      */         }
/*      */         
/* 1715 */         StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
/*      */ 
/*      */         
/* 1718 */         String paramName = null;
/* 1719 */         boolean isOutParam = false;
/* 1720 */         boolean isInParam = false;
/*      */         
/* 1722 */         if (declarationTok.hasMoreTokens()) {
/* 1723 */           String possibleParamName = declarationTok.nextToken();
/*      */           
/* 1725 */           if (possibleParamName.equalsIgnoreCase("OUT")) {
/* 1726 */             isOutParam = true;
/*      */             
/* 1728 */             if (declarationTok.hasMoreTokens()) {
/* 1729 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1731 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000");
/*      */             }
/*      */           
/*      */           }
/* 1735 */           else if (possibleParamName.equalsIgnoreCase("INOUT")) {
/* 1736 */             isOutParam = true;
/* 1737 */             isInParam = true;
/*      */             
/* 1739 */             if (declarationTok.hasMoreTokens()) {
/* 1740 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1742 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000");
/*      */             }
/*      */           
/*      */           }
/* 1746 */           else if (possibleParamName.equalsIgnoreCase("IN")) {
/* 1747 */             isOutParam = false;
/* 1748 */             isInParam = true;
/*      */             
/* 1750 */             if (declarationTok.hasMoreTokens()) {
/* 1751 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1753 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000");
/*      */             }
/*      */           
/*      */           } else {
/*      */             
/* 1758 */             isOutParam = false;
/* 1759 */             isInParam = true;
/*      */             
/* 1761 */             paramName = possibleParamName;
/*      */           } 
/*      */           
/* 1764 */           TypeDescriptor typeDesc = null;
/*      */           
/* 1766 */           if (declarationTok.hasMoreTokens()) {
/* 1767 */             StringBuffer typeInfoBuf = new StringBuffer(declarationTok.nextToken());
/*      */ 
/*      */             
/* 1770 */             while (declarationTok.hasMoreTokens()) {
/* 1771 */               typeInfoBuf.append(" ");
/* 1772 */               typeInfoBuf.append(declarationTok.nextToken());
/*      */             } 
/*      */             
/* 1775 */             String typeInfo = typeInfoBuf.toString();
/*      */             
/* 1777 */             typeDesc = new TypeDescriptor(this, typeInfo, null);
/*      */           } else {
/* 1779 */             throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter type)", "S1000");
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1784 */           if ((paramName.startsWith("`") && paramName.endsWith("`")) || (isProcedureInAnsiMode && paramName.startsWith("\"") && paramName.endsWith("\"")))
/*      */           {
/* 1786 */             paramName = paramName.substring(1, paramName.length() - 1);
/*      */           }
/*      */           
/* 1789 */           int wildCompareRes = StringUtils.wildCompare(paramName, parameterNamePattern);
/*      */ 
/*      */           
/* 1792 */           if (wildCompareRes != -1) {
/* 1793 */             ResultSetRow row = convertTypeDescriptorToProcedureRow(procNameAsBytes, paramName, isOutParam, isInParam, false, typeDesc, forGetFunctionColumns, ordinal++);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1798 */             resultRows.add(row);
/*      */           } 
/*      */         } else {
/* 1801 */           throw SQLError.createSQLException("Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')", "S1000");
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
/*      */   private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar) throws SQLException {
/* 1830 */     int currentPos = beginIndex + 1;
/* 1831 */     int parenDepth = 1;
/*      */     
/* 1833 */     while (parenDepth > 0 && currentPos < procedureDef.length()) {
/* 1834 */       int closedParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, procedureDef, ")", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/*      */ 
/*      */       
/* 1838 */       if (closedParenIndex != -1) {
/* 1839 */         int nextOpenParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1844 */         if (nextOpenParenIndex != -1 && nextOpenParenIndex < closedParenIndex) {
/*      */           
/* 1846 */           parenDepth++;
/* 1847 */           currentPos = closedParenIndex + 1;
/*      */           
/*      */           continue;
/*      */         } 
/* 1851 */         parenDepth--;
/* 1852 */         currentPos = closedParenIndex;
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1857 */       throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1864 */     return currentPos;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int findEndOfReturnsClause(String procedureDefn, String quoteChar, int positionOfReturnKeyword) throws SQLException {
/* 1889 */     String[] tokens = { "LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN" };
/*      */ 
/*      */ 
/*      */     
/* 1893 */     int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
/*      */     
/* 1895 */     for (int i = 0; i < tokens.length; i++) {
/* 1896 */       int j = StringUtils.indexOfIgnoreCaseRespectQuotes(startLookingAt, procedureDefn, tokens[i], quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/*      */ 
/*      */       
/* 1900 */       if (j != -1) {
/* 1901 */         return j;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1906 */     int endOfReturn = StringUtils.indexOfIgnoreCaseRespectQuotes(startLookingAt, procedureDefn, ":", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/*      */ 
/*      */     
/* 1910 */     if (endOfReturn != -1)
/*      */     {
/* 1912 */       for (int j = endOfReturn; j > 0; j--) {
/* 1913 */         if (Character.isWhitespace(procedureDefn.charAt(j))) {
/* 1914 */           return j;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1921 */     throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getCascadeDeleteOption(String cascadeOptions) {
/* 1935 */     int onDeletePos = cascadeOptions.indexOf("ON DELETE");
/*      */     
/* 1937 */     if (onDeletePos != -1) {
/* 1938 */       String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
/*      */ 
/*      */       
/* 1941 */       if (deleteOptions.startsWith("ON DELETE CASCADE"))
/* 1942 */         return 0; 
/* 1943 */       if (deleteOptions.startsWith("ON DELETE SET NULL"))
/* 1944 */         return 2; 
/* 1945 */       if (deleteOptions.startsWith("ON DELETE RESTRICT"))
/* 1946 */         return 1; 
/* 1947 */       if (deleteOptions.startsWith("ON DELETE NO ACTION")) {
/* 1948 */         return 3;
/*      */       }
/*      */     } 
/*      */     
/* 1952 */     return 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getCascadeUpdateOption(String cascadeOptions) {
/* 1964 */     int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
/*      */     
/* 1966 */     if (onUpdatePos != -1) {
/* 1967 */       String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
/*      */ 
/*      */       
/* 1970 */       if (updateOptions.startsWith("ON UPDATE CASCADE"))
/* 1971 */         return 0; 
/* 1972 */       if (updateOptions.startsWith("ON UPDATE SET NULL"))
/* 1973 */         return 2; 
/* 1974 */       if (updateOptions.startsWith("ON UPDATE RESTRICT"))
/* 1975 */         return 1; 
/* 1976 */       if (updateOptions.startsWith("ON UPDATE NO ACTION")) {
/* 1977 */         return 3;
/*      */       }
/*      */     } 
/*      */     
/* 1981 */     return 3;
/*      */   }
/*      */ 
/*      */   
/*      */   protected IteratorWithCleanup getCatalogIterator(String catalogSpec) throws SQLException {
/*      */     IteratorWithCleanup allCatalogsIter;
/* 1987 */     if (catalogSpec != null) {
/* 1988 */       if (!catalogSpec.equals("")) {
/* 1989 */         allCatalogsIter = new SingleStringIterator(this, catalogSpec);
/*      */       } else {
/*      */         
/* 1992 */         allCatalogsIter = new SingleStringIterator(this, this.database);
/*      */       } 
/* 1994 */     } else if (this.conn.getNullCatalogMeansCurrent()) {
/* 1995 */       allCatalogsIter = new SingleStringIterator(this, this.database);
/*      */     } else {
/* 1997 */       allCatalogsIter = new ResultSetIterator(this, getCatalogs(), 1);
/*      */     } 
/*      */     
/* 2000 */     return allCatalogsIter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getCatalogs() throws SQLException {
/* 2019 */     ResultSet results = null;
/* 2020 */     Statement stmt = null;
/*      */     
/*      */     try {
/* 2023 */       stmt = this.conn.createStatement();
/* 2024 */       stmt.setEscapeProcessing(false);
/* 2025 */       results = stmt.executeQuery("SHOW DATABASES");
/*      */       
/* 2027 */       ResultSetMetaData resultsMD = results.getMetaData();
/* 2028 */       Field[] fields = new Field[1];
/* 2029 */       fields[0] = new Field("", "TABLE_CAT", 12, resultsMD.getColumnDisplaySize(1));
/*      */ 
/*      */       
/* 2032 */       ArrayList tuples = new ArrayList();
/*      */       
/* 2034 */       while (results.next()) {
/* 2035 */         byte[][] rowVal = new byte[1][];
/* 2036 */         rowVal[0] = results.getBytes(1);
/* 2037 */         tuples.add(new ByteArrayRow(rowVal));
/*      */       } 
/*      */       
/* 2040 */       return buildResultSet(fields, tuples);
/*      */     } finally {
/* 2042 */       if (results != null) {
/*      */         try {
/* 2044 */           results.close();
/* 2045 */         } catch (SQLException sqlEx) {
/* 2046 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         } 
/*      */         
/* 2049 */         results = null;
/*      */       } 
/*      */       
/* 2052 */       if (stmt != null) {
/*      */         try {
/* 2054 */           stmt.close();
/* 2055 */         } catch (SQLException sqlEx) {
/* 2056 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         } 
/*      */         
/* 2059 */         stmt = null;
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
/*      */   public String getCatalogSeparator() throws SQLException {
/* 2072 */     return ".";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCatalogTerm() throws SQLException {
/* 2089 */     return "database";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
/* 2130 */     Field[] fields = new Field[8];
/* 2131 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 2132 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 2133 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 2134 */     fields[3] = new Field("", "COLUMN_NAME", 1, 64);
/* 2135 */     fields[4] = new Field("", "GRANTOR", 1, 77);
/* 2136 */     fields[5] = new Field("", "GRANTEE", 1, 77);
/* 2137 */     fields[6] = new Field("", "PRIVILEGE", 1, 64);
/* 2138 */     fields[7] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */     
/* 2140 */     StringBuffer grantQuery = new StringBuffer("SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv from mysql.columns_priv c, mysql.tables_priv t where c.host = t.host and c.db = t.db and c.table_name = t.table_name ");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2147 */     if (catalog != null && catalog.length() != 0) {
/* 2148 */       grantQuery.append(" AND c.db='");
/* 2149 */       grantQuery.append(catalog);
/* 2150 */       grantQuery.append("' ");
/*      */     } 
/*      */ 
/*      */     
/* 2154 */     grantQuery.append(" AND c.table_name ='");
/* 2155 */     grantQuery.append(table);
/* 2156 */     grantQuery.append("' AND c.column_name like '");
/* 2157 */     grantQuery.append(columnNamePattern);
/* 2158 */     grantQuery.append("'");
/*      */     
/* 2160 */     Statement stmt = null;
/* 2161 */     ResultSet results = null;
/* 2162 */     ArrayList grantRows = new ArrayList();
/*      */     
/*      */     try {
/* 2165 */       stmt = this.conn.createStatement();
/* 2166 */       stmt.setEscapeProcessing(false);
/* 2167 */       results = stmt.executeQuery(grantQuery.toString());
/*      */       
/* 2169 */       while (results.next()) {
/* 2170 */         String host = results.getString(1);
/* 2171 */         String db = results.getString(2);
/* 2172 */         String grantor = results.getString(3);
/* 2173 */         String user = results.getString(4);
/*      */         
/* 2175 */         if (user == null || user.length() == 0) {
/* 2176 */           user = "%";
/*      */         }
/*      */         
/* 2179 */         StringBuffer fullUser = new StringBuffer(user);
/*      */         
/* 2181 */         if (host != null && this.conn.getUseHostsInPrivileges()) {
/* 2182 */           fullUser.append("@");
/* 2183 */           fullUser.append(host);
/*      */         } 
/*      */         
/* 2186 */         String columnName = results.getString(6);
/* 2187 */         String allPrivileges = results.getString(7);
/*      */         
/* 2189 */         if (allPrivileges != null) {
/* 2190 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */           
/* 2192 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */           
/* 2194 */           while (st.hasMoreTokens()) {
/* 2195 */             String privilege = st.nextToken().trim();
/* 2196 */             byte[][] tuple = new byte[8][];
/* 2197 */             tuple[0] = s2b(db);
/* 2198 */             tuple[1] = null;
/* 2199 */             tuple[2] = s2b(table);
/* 2200 */             tuple[3] = s2b(columnName);
/*      */             
/* 2202 */             if (grantor != null) {
/* 2203 */               tuple[4] = s2b(grantor);
/*      */             } else {
/* 2205 */               tuple[4] = null;
/*      */             } 
/*      */             
/* 2208 */             tuple[5] = s2b(fullUser.toString());
/* 2209 */             tuple[6] = s2b(privilege);
/* 2210 */             tuple[7] = null;
/* 2211 */             grantRows.add(new ByteArrayRow(tuple));
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } finally {
/* 2216 */       if (results != null) {
/*      */         try {
/* 2218 */           results.close();
/* 2219 */         } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */         
/* 2223 */         results = null;
/*      */       } 
/*      */       
/* 2226 */       if (stmt != null) {
/*      */         try {
/* 2228 */           stmt.close();
/* 2229 */         } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */         
/* 2233 */         stmt = null;
/*      */       } 
/*      */     } 
/*      */     
/* 2237 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
/* 2301 */     if (columnNamePattern == null) {
/* 2302 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 2303 */         columnNamePattern = "%";
/*      */       } else {
/* 2305 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 2311 */     String colPattern = columnNamePattern;
/*      */     
/* 2313 */     Field[] fields = new Field[23];
/* 2314 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 2315 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 2316 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 2317 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 2318 */     fields[4] = new Field("", "DATA_TYPE", 5, 5);
/* 2319 */     fields[5] = new Field("", "TYPE_NAME", 1, 16);
/* 2320 */     fields[6] = new Field("", "COLUMN_SIZE", 4, Integer.toString(2147483647).length());
/*      */     
/* 2322 */     fields[7] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 2323 */     fields[8] = new Field("", "DECIMAL_DIGITS", 4, 10);
/* 2324 */     fields[9] = new Field("", "NUM_PREC_RADIX", 4, 10);
/* 2325 */     fields[10] = new Field("", "NULLABLE", 4, 10);
/* 2326 */     fields[11] = new Field("", "REMARKS", 1, 0);
/* 2327 */     fields[12] = new Field("", "COLUMN_DEF", 1, 0);
/* 2328 */     fields[13] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 2329 */     fields[14] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 2330 */     fields[15] = new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(2147483647).length());
/*      */     
/* 2332 */     fields[16] = new Field("", "ORDINAL_POSITION", 4, 10);
/* 2333 */     fields[17] = new Field("", "IS_NULLABLE", 1, 3);
/* 2334 */     fields[18] = new Field("", "SCOPE_CATALOG", 1, 255);
/* 2335 */     fields[19] = new Field("", "SCOPE_SCHEMA", 1, 255);
/* 2336 */     fields[20] = new Field("", "SCOPE_TABLE", 1, 255);
/* 2337 */     fields[21] = new Field("", "SOURCE_DATA_TYPE", 5, 10);
/* 2338 */     fields[22] = new Field("", "IS_AUTOINCREMENT", 1, 3);
/*      */     
/* 2340 */     ArrayList rows = new ArrayList();
/* 2341 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */     
/*      */     try {
/* 2345 */       (new IterateBlock(this, getCatalogIterator(catalog), tableNamePattern, schemaPattern, colPattern, stmt, rows) { private final String val$tableNamePattern; private final String val$schemaPattern; private final String val$colPattern; private final Statement val$stmt; private final ArrayList val$rows; private final DatabaseMetaData this$0; void forEach(Object catalogStr) throws SQLException { // Byte code:
/*      */             //   0: new java/util/ArrayList
/*      */             //   3: dup
/*      */             //   4: invokespecial <init> : ()V
/*      */             //   7: astore_2
/*      */             //   8: aload_0
/*      */             //   9: getfield val$tableNamePattern : Ljava/lang/String;
/*      */             //   12: ifnonnull -> 111
/*      */             //   15: aconst_null
/*      */             //   16: astore_3
/*      */             //   17: aload_0
/*      */             //   18: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   21: aload_1
/*      */             //   22: checkcast java/lang/String
/*      */             //   25: aload_0
/*      */             //   26: getfield val$schemaPattern : Ljava/lang/String;
/*      */             //   29: ldc '%'
/*      */             //   31: iconst_0
/*      */             //   32: anewarray java/lang/String
/*      */             //   35: invokevirtual getTables : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */             //   38: astore_3
/*      */             //   39: aload_3
/*      */             //   40: invokeinterface next : ()Z
/*      */             //   45: ifeq -> 68
/*      */             //   48: aload_3
/*      */             //   49: ldc 'TABLE_NAME'
/*      */             //   51: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   56: astore #4
/*      */             //   58: aload_2
/*      */             //   59: aload #4
/*      */             //   61: invokevirtual add : (Ljava/lang/Object;)Z
/*      */             //   64: pop
/*      */             //   65: goto -> 39
/*      */             //   68: jsr -> 82
/*      */             //   71: goto -> 108
/*      */             //   74: astore #5
/*      */             //   76: jsr -> 82
/*      */             //   79: aload #5
/*      */             //   81: athrow
/*      */             //   82: astore #6
/*      */             //   84: aload_3
/*      */             //   85: ifnull -> 106
/*      */             //   88: aload_3
/*      */             //   89: invokeinterface close : ()V
/*      */             //   94: goto -> 104
/*      */             //   97: astore #7
/*      */             //   99: aload #7
/*      */             //   101: invokestatic shouldNotHappen : (Ljava/lang/Exception;)V
/*      */             //   104: aconst_null
/*      */             //   105: astore_3
/*      */             //   106: ret #6
/*      */             //   108: goto -> 206
/*      */             //   111: aconst_null
/*      */             //   112: astore_3
/*      */             //   113: aload_0
/*      */             //   114: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   117: aload_1
/*      */             //   118: checkcast java/lang/String
/*      */             //   121: aload_0
/*      */             //   122: getfield val$schemaPattern : Ljava/lang/String;
/*      */             //   125: aload_0
/*      */             //   126: getfield val$tableNamePattern : Ljava/lang/String;
/*      */             //   129: iconst_0
/*      */             //   130: anewarray java/lang/String
/*      */             //   133: invokevirtual getTables : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */             //   136: astore_3
/*      */             //   137: aload_3
/*      */             //   138: invokeinterface next : ()Z
/*      */             //   143: ifeq -> 166
/*      */             //   146: aload_3
/*      */             //   147: ldc 'TABLE_NAME'
/*      */             //   149: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   154: astore #4
/*      */             //   156: aload_2
/*      */             //   157: aload #4
/*      */             //   159: invokevirtual add : (Ljava/lang/Object;)Z
/*      */             //   162: pop
/*      */             //   163: goto -> 137
/*      */             //   166: jsr -> 180
/*      */             //   169: goto -> 206
/*      */             //   172: astore #8
/*      */             //   174: jsr -> 180
/*      */             //   177: aload #8
/*      */             //   179: athrow
/*      */             //   180: astore #9
/*      */             //   182: aload_3
/*      */             //   183: ifnull -> 204
/*      */             //   186: aload_3
/*      */             //   187: invokeinterface close : ()V
/*      */             //   192: goto -> 202
/*      */             //   195: astore #10
/*      */             //   197: aload #10
/*      */             //   199: invokestatic shouldNotHappen : (Ljava/lang/Exception;)V
/*      */             //   202: aconst_null
/*      */             //   203: astore_3
/*      */             //   204: ret #9
/*      */             //   206: aload_2
/*      */             //   207: invokevirtual iterator : ()Ljava/util/Iterator;
/*      */             //   210: astore_3
/*      */             //   211: aload_3
/*      */             //   212: invokeinterface hasNext : ()Z
/*      */             //   217: ifeq -> 1276
/*      */             //   220: aload_3
/*      */             //   221: invokeinterface next : ()Ljava/lang/Object;
/*      */             //   226: checkcast java/lang/String
/*      */             //   229: astore #4
/*      */             //   231: aconst_null
/*      */             //   232: astore #5
/*      */             //   234: new java/lang/StringBuffer
/*      */             //   237: dup
/*      */             //   238: ldc 'SHOW '
/*      */             //   240: invokespecial <init> : (Ljava/lang/String;)V
/*      */             //   243: astore #6
/*      */             //   245: aload_0
/*      */             //   246: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   249: getfield conn : Lcom/mysql/jdbc/ConnectionImpl;
/*      */             //   252: iconst_4
/*      */             //   253: iconst_1
/*      */             //   254: iconst_0
/*      */             //   255: invokevirtual versionMeetsMinimum : (III)Z
/*      */             //   258: ifeq -> 269
/*      */             //   261: aload #6
/*      */             //   263: ldc 'FULL '
/*      */             //   265: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   268: pop
/*      */             //   269: aload #6
/*      */             //   271: ldc 'COLUMNS FROM '
/*      */             //   273: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   276: pop
/*      */             //   277: aload #6
/*      */             //   279: aload_0
/*      */             //   280: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   283: getfield quotedId : Ljava/lang/String;
/*      */             //   286: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   289: pop
/*      */             //   290: aload #6
/*      */             //   292: aload #4
/*      */             //   294: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   297: pop
/*      */             //   298: aload #6
/*      */             //   300: aload_0
/*      */             //   301: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   304: getfield quotedId : Ljava/lang/String;
/*      */             //   307: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   310: pop
/*      */             //   311: aload #6
/*      */             //   313: ldc ' FROM '
/*      */             //   315: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   318: pop
/*      */             //   319: aload #6
/*      */             //   321: aload_0
/*      */             //   322: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   325: getfield quotedId : Ljava/lang/String;
/*      */             //   328: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   331: pop
/*      */             //   332: aload #6
/*      */             //   334: aload_1
/*      */             //   335: checkcast java/lang/String
/*      */             //   338: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   341: pop
/*      */             //   342: aload #6
/*      */             //   344: aload_0
/*      */             //   345: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   348: getfield quotedId : Ljava/lang/String;
/*      */             //   351: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   354: pop
/*      */             //   355: aload #6
/*      */             //   357: ldc ' LIKE ''
/*      */             //   359: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   362: pop
/*      */             //   363: aload #6
/*      */             //   365: aload_0
/*      */             //   366: getfield val$colPattern : Ljava/lang/String;
/*      */             //   369: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   372: pop
/*      */             //   373: aload #6
/*      */             //   375: ldc '''
/*      */             //   377: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   380: pop
/*      */             //   381: iconst_0
/*      */             //   382: istore #7
/*      */             //   384: aconst_null
/*      */             //   385: astore #8
/*      */             //   387: aload_0
/*      */             //   388: getfield val$colPattern : Ljava/lang/String;
/*      */             //   391: ldc '%'
/*      */             //   393: invokevirtual equals : (Ljava/lang/Object;)Z
/*      */             //   396: ifne -> 593
/*      */             //   399: iconst_1
/*      */             //   400: istore #7
/*      */             //   402: new java/lang/StringBuffer
/*      */             //   405: dup
/*      */             //   406: ldc 'SHOW '
/*      */             //   408: invokespecial <init> : (Ljava/lang/String;)V
/*      */             //   411: astore #9
/*      */             //   413: aload_0
/*      */             //   414: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   417: getfield conn : Lcom/mysql/jdbc/ConnectionImpl;
/*      */             //   420: iconst_4
/*      */             //   421: iconst_1
/*      */             //   422: iconst_0
/*      */             //   423: invokevirtual versionMeetsMinimum : (III)Z
/*      */             //   426: ifeq -> 437
/*      */             //   429: aload #9
/*      */             //   431: ldc 'FULL '
/*      */             //   433: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   436: pop
/*      */             //   437: aload #9
/*      */             //   439: ldc 'COLUMNS FROM '
/*      */             //   441: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   444: pop
/*      */             //   445: aload #9
/*      */             //   447: aload_0
/*      */             //   448: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   451: getfield quotedId : Ljava/lang/String;
/*      */             //   454: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   457: pop
/*      */             //   458: aload #9
/*      */             //   460: aload #4
/*      */             //   462: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   465: pop
/*      */             //   466: aload #9
/*      */             //   468: aload_0
/*      */             //   469: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   472: getfield quotedId : Ljava/lang/String;
/*      */             //   475: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   478: pop
/*      */             //   479: aload #9
/*      */             //   481: ldc ' FROM '
/*      */             //   483: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   486: pop
/*      */             //   487: aload #9
/*      */             //   489: aload_0
/*      */             //   490: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   493: getfield quotedId : Ljava/lang/String;
/*      */             //   496: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   499: pop
/*      */             //   500: aload #9
/*      */             //   502: aload_1
/*      */             //   503: checkcast java/lang/String
/*      */             //   506: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   509: pop
/*      */             //   510: aload #9
/*      */             //   512: aload_0
/*      */             //   513: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   516: getfield quotedId : Ljava/lang/String;
/*      */             //   519: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */             //   522: pop
/*      */             //   523: aload_0
/*      */             //   524: getfield val$stmt : Ljava/sql/Statement;
/*      */             //   527: aload #9
/*      */             //   529: invokevirtual toString : ()Ljava/lang/String;
/*      */             //   532: invokeinterface executeQuery : (Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */             //   537: astore #5
/*      */             //   539: new java/util/HashMap
/*      */             //   542: dup
/*      */             //   543: invokespecial <init> : ()V
/*      */             //   546: astore #8
/*      */             //   548: iconst_1
/*      */             //   549: istore #10
/*      */             //   551: aload #5
/*      */             //   553: invokeinterface next : ()Z
/*      */             //   558: ifeq -> 593
/*      */             //   561: aload #5
/*      */             //   563: ldc 'Field'
/*      */             //   565: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   570: astore #11
/*      */             //   572: aload #8
/*      */             //   574: aload #11
/*      */             //   576: iload #10
/*      */             //   578: iinc #10, 1
/*      */             //   581: invokestatic integerValueOf : (I)Ljava/lang/Integer;
/*      */             //   584: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */             //   589: pop
/*      */             //   590: goto -> 551
/*      */             //   593: aload_0
/*      */             //   594: getfield val$stmt : Ljava/sql/Statement;
/*      */             //   597: aload #6
/*      */             //   599: invokevirtual toString : ()Ljava/lang/String;
/*      */             //   602: invokeinterface executeQuery : (Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */             //   607: astore #5
/*      */             //   609: iconst_1
/*      */             //   610: istore #9
/*      */             //   612: aload #5
/*      */             //   614: invokeinterface next : ()Z
/*      */             //   619: ifeq -> 1235
/*      */             //   622: bipush #23
/*      */             //   624: anewarray [B
/*      */             //   627: astore #10
/*      */             //   629: aload #10
/*      */             //   631: iconst_0
/*      */             //   632: aload_0
/*      */             //   633: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   636: aload_1
/*      */             //   637: checkcast java/lang/String
/*      */             //   640: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   643: aastore
/*      */             //   644: aload #10
/*      */             //   646: iconst_1
/*      */             //   647: aconst_null
/*      */             //   648: aastore
/*      */             //   649: aload #10
/*      */             //   651: iconst_2
/*      */             //   652: aload_0
/*      */             //   653: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   656: aload #4
/*      */             //   658: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   661: aastore
/*      */             //   662: aload #10
/*      */             //   664: iconst_3
/*      */             //   665: aload #5
/*      */             //   667: ldc 'Field'
/*      */             //   669: invokeinterface getBytes : (Ljava/lang/String;)[B
/*      */             //   674: aastore
/*      */             //   675: new com/mysql/jdbc/DatabaseMetaData$TypeDescriptor
/*      */             //   678: dup
/*      */             //   679: aload_0
/*      */             //   680: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   683: aload #5
/*      */             //   685: ldc 'Type'
/*      */             //   687: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   692: aload #5
/*      */             //   694: ldc 'Null'
/*      */             //   696: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   701: invokespecial <init> : (Lcom/mysql/jdbc/DatabaseMetaData;Ljava/lang/String;Ljava/lang/String;)V
/*      */             //   704: astore #11
/*      */             //   706: aload #10
/*      */             //   708: iconst_4
/*      */             //   709: aload #11
/*      */             //   711: getfield dataType : S
/*      */             //   714: invokestatic toString : (S)Ljava/lang/String;
/*      */             //   717: invokevirtual getBytes : ()[B
/*      */             //   720: aastore
/*      */             //   721: aload #10
/*      */             //   723: iconst_5
/*      */             //   724: aload_0
/*      */             //   725: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   728: aload #11
/*      */             //   730: getfield typeName : Ljava/lang/String;
/*      */             //   733: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   736: aastore
/*      */             //   737: aload #10
/*      */             //   739: bipush #6
/*      */             //   741: aload #11
/*      */             //   743: getfield columnSize : Ljava/lang/Integer;
/*      */             //   746: ifnonnull -> 753
/*      */             //   749: aconst_null
/*      */             //   750: goto -> 768
/*      */             //   753: aload_0
/*      */             //   754: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   757: aload #11
/*      */             //   759: getfield columnSize : Ljava/lang/Integer;
/*      */             //   762: invokevirtual toString : ()Ljava/lang/String;
/*      */             //   765: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   768: aastore
/*      */             //   769: aload #10
/*      */             //   771: bipush #7
/*      */             //   773: aload_0
/*      */             //   774: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   777: aload #11
/*      */             //   779: getfield bufferLength : I
/*      */             //   782: invokestatic toString : (I)Ljava/lang/String;
/*      */             //   785: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   788: aastore
/*      */             //   789: aload #10
/*      */             //   791: bipush #8
/*      */             //   793: aload #11
/*      */             //   795: getfield decimalDigits : Ljava/lang/Integer;
/*      */             //   798: ifnonnull -> 805
/*      */             //   801: aconst_null
/*      */             //   802: goto -> 820
/*      */             //   805: aload_0
/*      */             //   806: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   809: aload #11
/*      */             //   811: getfield decimalDigits : Ljava/lang/Integer;
/*      */             //   814: invokevirtual toString : ()Ljava/lang/String;
/*      */             //   817: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   820: aastore
/*      */             //   821: aload #10
/*      */             //   823: bipush #9
/*      */             //   825: aload_0
/*      */             //   826: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   829: aload #11
/*      */             //   831: getfield numPrecRadix : I
/*      */             //   834: invokestatic toString : (I)Ljava/lang/String;
/*      */             //   837: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   840: aastore
/*      */             //   841: aload #10
/*      */             //   843: bipush #10
/*      */             //   845: aload_0
/*      */             //   846: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   849: aload #11
/*      */             //   851: getfield nullability : I
/*      */             //   854: invokestatic toString : (I)Ljava/lang/String;
/*      */             //   857: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   860: aastore
/*      */             //   861: aload_0
/*      */             //   862: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   865: getfield conn : Lcom/mysql/jdbc/ConnectionImpl;
/*      */             //   868: iconst_4
/*      */             //   869: iconst_1
/*      */             //   870: iconst_0
/*      */             //   871: invokevirtual versionMeetsMinimum : (III)Z
/*      */             //   874: ifeq -> 894
/*      */             //   877: aload #10
/*      */             //   879: bipush #11
/*      */             //   881: aload #5
/*      */             //   883: ldc 'Comment'
/*      */             //   885: invokeinterface getBytes : (Ljava/lang/String;)[B
/*      */             //   890: aastore
/*      */             //   891: goto -> 908
/*      */             //   894: aload #10
/*      */             //   896: bipush #11
/*      */             //   898: aload #5
/*      */             //   900: ldc 'Extra'
/*      */             //   902: invokeinterface getBytes : (Ljava/lang/String;)[B
/*      */             //   907: aastore
/*      */             //   908: goto -> 921
/*      */             //   911: astore #12
/*      */             //   913: aload #10
/*      */             //   915: bipush #11
/*      */             //   917: iconst_0
/*      */             //   918: newarray byte
/*      */             //   920: aastore
/*      */             //   921: aload #10
/*      */             //   923: bipush #12
/*      */             //   925: aload #5
/*      */             //   927: ldc 'Default'
/*      */             //   929: invokeinterface getBytes : (Ljava/lang/String;)[B
/*      */             //   934: aastore
/*      */             //   935: aload #10
/*      */             //   937: bipush #13
/*      */             //   939: iconst_1
/*      */             //   940: newarray byte
/*      */             //   942: dup
/*      */             //   943: iconst_0
/*      */             //   944: bipush #48
/*      */             //   946: bastore
/*      */             //   947: aastore
/*      */             //   948: aload #10
/*      */             //   950: bipush #14
/*      */             //   952: iconst_1
/*      */             //   953: newarray byte
/*      */             //   955: dup
/*      */             //   956: iconst_0
/*      */             //   957: bipush #48
/*      */             //   959: bastore
/*      */             //   960: aastore
/*      */             //   961: aload #11
/*      */             //   963: getfield typeName : Ljava/lang/String;
/*      */             //   966: ldc 'CHAR'
/*      */             //   968: invokestatic indexOfIgnoreCase : (Ljava/lang/String;Ljava/lang/String;)I
/*      */             //   971: iconst_m1
/*      */             //   972: if_icmpne -> 1017
/*      */             //   975: aload #11
/*      */             //   977: getfield typeName : Ljava/lang/String;
/*      */             //   980: ldc 'BLOB'
/*      */             //   982: invokestatic indexOfIgnoreCase : (Ljava/lang/String;Ljava/lang/String;)I
/*      */             //   985: iconst_m1
/*      */             //   986: if_icmpne -> 1017
/*      */             //   989: aload #11
/*      */             //   991: getfield typeName : Ljava/lang/String;
/*      */             //   994: ldc 'TEXT'
/*      */             //   996: invokestatic indexOfIgnoreCase : (Ljava/lang/String;Ljava/lang/String;)I
/*      */             //   999: iconst_m1
/*      */             //   1000: if_icmpne -> 1017
/*      */             //   1003: aload #11
/*      */             //   1005: getfield typeName : Ljava/lang/String;
/*      */             //   1008: ldc 'BINARY'
/*      */             //   1010: invokestatic indexOfIgnoreCase : (Ljava/lang/String;Ljava/lang/String;)I
/*      */             //   1013: iconst_m1
/*      */             //   1014: if_icmpeq -> 1030
/*      */             //   1017: aload #10
/*      */             //   1019: bipush #15
/*      */             //   1021: aload #10
/*      */             //   1023: bipush #6
/*      */             //   1025: aaload
/*      */             //   1026: aastore
/*      */             //   1027: goto -> 1036
/*      */             //   1030: aload #10
/*      */             //   1032: bipush #15
/*      */             //   1034: aconst_null
/*      */             //   1035: aastore
/*      */             //   1036: iload #7
/*      */             //   1038: ifne -> 1060
/*      */             //   1041: aload #10
/*      */             //   1043: bipush #16
/*      */             //   1045: iload #9
/*      */             //   1047: iinc #9, 1
/*      */             //   1050: invokestatic toString : (I)Ljava/lang/String;
/*      */             //   1053: invokevirtual getBytes : ()[B
/*      */             //   1056: aastore
/*      */             //   1057: goto -> 1114
/*      */             //   1060: aload #5
/*      */             //   1062: ldc 'Field'
/*      */             //   1064: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   1069: astore #12
/*      */             //   1071: aload #8
/*      */             //   1073: aload #12
/*      */             //   1075: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*      */             //   1080: checkcast java/lang/Integer
/*      */             //   1083: astore #13
/*      */             //   1085: aload #13
/*      */             //   1087: ifnull -> 1106
/*      */             //   1090: aload #10
/*      */             //   1092: bipush #16
/*      */             //   1094: aload #13
/*      */             //   1096: invokevirtual toString : ()Ljava/lang/String;
/*      */             //   1099: invokevirtual getBytes : ()[B
/*      */             //   1102: aastore
/*      */             //   1103: goto -> 1114
/*      */             //   1106: ldc 'Can not find column in full column list to determine true ordinal position.'
/*      */             //   1108: ldc 'S1000'
/*      */             //   1110: invokestatic createSQLException : (Ljava/lang/String;Ljava/lang/String;)Ljava/sql/SQLException;
/*      */             //   1113: athrow
/*      */             //   1114: aload #10
/*      */             //   1116: bipush #17
/*      */             //   1118: aload_0
/*      */             //   1119: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   1122: aload #11
/*      */             //   1124: getfield isNullable : Ljava/lang/String;
/*      */             //   1127: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   1130: aastore
/*      */             //   1131: aload #10
/*      */             //   1133: bipush #18
/*      */             //   1135: aconst_null
/*      */             //   1136: aastore
/*      */             //   1137: aload #10
/*      */             //   1139: bipush #19
/*      */             //   1141: aconst_null
/*      */             //   1142: aastore
/*      */             //   1143: aload #10
/*      */             //   1145: bipush #20
/*      */             //   1147: aconst_null
/*      */             //   1148: aastore
/*      */             //   1149: aload #10
/*      */             //   1151: bipush #21
/*      */             //   1153: aconst_null
/*      */             //   1154: aastore
/*      */             //   1155: aload #10
/*      */             //   1157: bipush #22
/*      */             //   1159: aload_0
/*      */             //   1160: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   1163: ldc ''
/*      */             //   1165: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   1168: aastore
/*      */             //   1169: aload #5
/*      */             //   1171: ldc 'Extra'
/*      */             //   1173: invokeinterface getString : (Ljava/lang/String;)Ljava/lang/String;
/*      */             //   1178: astore #12
/*      */             //   1180: aload #12
/*      */             //   1182: ifnull -> 1215
/*      */             //   1185: aload #10
/*      */             //   1187: bipush #22
/*      */             //   1189: aload_0
/*      */             //   1190: getfield this$0 : Lcom/mysql/jdbc/DatabaseMetaData;
/*      */             //   1193: aload #12
/*      */             //   1195: ldc 'auto_increment'
/*      */             //   1197: invokestatic indexOfIgnoreCase : (Ljava/lang/String;Ljava/lang/String;)I
/*      */             //   1200: iconst_m1
/*      */             //   1201: if_icmpeq -> 1209
/*      */             //   1204: ldc 'YES'
/*      */             //   1206: goto -> 1211
/*      */             //   1209: ldc 'NO'
/*      */             //   1211: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */             //   1214: aastore
/*      */             //   1215: aload_0
/*      */             //   1216: getfield val$rows : Ljava/util/ArrayList;
/*      */             //   1219: new com/mysql/jdbc/ByteArrayRow
/*      */             //   1222: dup
/*      */             //   1223: aload #10
/*      */             //   1225: invokespecial <init> : ([[B)V
/*      */             //   1228: invokevirtual add : (Ljava/lang/Object;)Z
/*      */             //   1231: pop
/*      */             //   1232: goto -> 612
/*      */             //   1235: jsr -> 1249
/*      */             //   1238: goto -> 1273
/*      */             //   1241: astore #14
/*      */             //   1243: jsr -> 1249
/*      */             //   1246: aload #14
/*      */             //   1248: athrow
/*      */             //   1249: astore #15
/*      */             //   1251: aload #5
/*      */             //   1253: ifnull -> 1271
/*      */             //   1256: aload #5
/*      */             //   1258: invokeinterface close : ()V
/*      */             //   1263: goto -> 1268
/*      */             //   1266: astore #16
/*      */             //   1268: aconst_null
/*      */             //   1269: astore #5
/*      */             //   1271: ret #15
/*      */             //   1273: goto -> 211
/*      */             //   1276: return
/*      */             // Line number table:
/*      */             //   Java source line number -> byte code offset
/*      */             //   #2348	-> 0
/*      */             //   #2350	-> 8
/*      */             //   #2352	-> 15
/*      */             //   #2355	-> 17
/*      */             //   #2358	-> 39
/*      */             //   #2359	-> 48
/*      */             //   #2361	-> 58
/*      */             //   #2363	-> 68
/*      */             //   #2374	-> 71
/*      */             //   #2364	-> 74
/*      */             //   #2366	-> 88
/*      */             //   #2370	-> 94
/*      */             //   #2367	-> 97
/*      */             //   #2368	-> 99
/*      */             //   #2372	-> 104
/*      */             //   #2376	-> 111
/*      */             //   #2379	-> 113
/*      */             //   #2382	-> 137
/*      */             //   #2383	-> 146
/*      */             //   #2385	-> 156
/*      */             //   #2387	-> 166
/*      */             //   #2398	-> 169
/*      */             //   #2388	-> 172
/*      */             //   #2390	-> 186
/*      */             //   #2394	-> 192
/*      */             //   #2391	-> 195
/*      */             //   #2392	-> 197
/*      */             //   #2396	-> 202
/*      */             //   #2401	-> 206
/*      */             //   #2403	-> 211
/*      */             //   #2404	-> 220
/*      */             //   #2406	-> 231
/*      */             //   #2409	-> 234
/*      */             //   #2411	-> 245
/*      */             //   #2412	-> 261
/*      */             //   #2415	-> 269
/*      */             //   #2416	-> 277
/*      */             //   #2417	-> 290
/*      */             //   #2418	-> 298
/*      */             //   #2419	-> 311
/*      */             //   #2420	-> 319
/*      */             //   #2421	-> 332
/*      */             //   #2422	-> 342
/*      */             //   #2423	-> 355
/*      */             //   #2424	-> 363
/*      */             //   #2425	-> 373
/*      */             //   #2432	-> 381
/*      */             //   #2433	-> 384
/*      */             //   #2435	-> 387
/*      */             //   #2436	-> 399
/*      */             //   #2438	-> 402
/*      */             //   #2441	-> 413
/*      */             //   #2442	-> 429
/*      */             //   #2445	-> 437
/*      */             //   #2446	-> 445
/*      */             //   #2447	-> 458
/*      */             //   #2448	-> 466
/*      */             //   #2449	-> 479
/*      */             //   #2450	-> 487
/*      */             //   #2451	-> 500
/*      */             //   #2453	-> 510
/*      */             //   #2455	-> 523
/*      */             //   #2458	-> 539
/*      */             //   #2460	-> 548
/*      */             //   #2462	-> 551
/*      */             //   #2463	-> 561
/*      */             //   #2466	-> 572
/*      */             //   #2471	-> 593
/*      */             //   #2473	-> 609
/*      */             //   #2475	-> 612
/*      */             //   #2476	-> 622
/*      */             //   #2477	-> 629
/*      */             //   #2478	-> 644
/*      */             //   #2481	-> 649
/*      */             //   #2482	-> 662
/*      */             //   #2484	-> 675
/*      */             //   #2488	-> 706
/*      */             //   #2492	-> 721
/*      */             //   #2494	-> 737
/*      */             //   #2495	-> 769
/*      */             //   #2496	-> 789
/*      */             //   #2497	-> 821
/*      */             //   #2499	-> 841
/*      */             //   #2510	-> 861
/*      */             //   #2511	-> 877
/*      */             //   #2514	-> 894
/*      */             //   #2518	-> 908
/*      */             //   #2516	-> 911
/*      */             //   #2517	-> 913
/*      */             //   #2521	-> 921
/*      */             //   #2523	-> 935
/*      */             //   #2524	-> 948
/*      */             //   #2526	-> 961
/*      */             //   #2530	-> 1017
/*      */             //   #2532	-> 1030
/*      */             //   #2536	-> 1036
/*      */             //   #2537	-> 1041
/*      */             //   #2540	-> 1060
/*      */             //   #2542	-> 1071
/*      */             //   #2545	-> 1085
/*      */             //   #2546	-> 1090
/*      */             //   #2549	-> 1106
/*      */             //   #2555	-> 1114
/*      */             //   #2558	-> 1131
/*      */             //   #2559	-> 1137
/*      */             //   #2560	-> 1143
/*      */             //   #2561	-> 1149
/*      */             //   #2563	-> 1155
/*      */             //   #2565	-> 1169
/*      */             //   #2567	-> 1180
/*      */             //   #2568	-> 1185
/*      */             //   #2574	-> 1215
/*      */             //   #2576	-> 1235
/*      */             //   #2586	-> 1238
/*      */             //   #2577	-> 1241
/*      */             //   #2579	-> 1256
/*      */             //   #2582	-> 1263
/*      */             //   #2580	-> 1266
/*      */             //   #2584	-> 1268
/*      */             //   #2588	-> 1276
/*      */             // Local variable table:
/*      */             //   start	length	slot	name	descriptor
/*      */             //   58	7	4	tableNameFromList	Ljava/lang/String;
/*      */             //   99	5	7	sqlEx	Ljava/lang/Exception;
/*      */             //   17	91	3	tables	Ljava/sql/ResultSet;
/*      */             //   156	7	4	tableNameFromList	Ljava/lang/String;
/*      */             //   197	5	10	sqlEx	Ljava/sql/SQLException;
/*      */             //   113	93	3	tables	Ljava/sql/ResultSet;
/*      */             //   572	18	11	fullOrdColName	Ljava/lang/String;
/*      */             //   413	180	9	fullColumnQueryBuf	Ljava/lang/StringBuffer;
/*      */             //   551	42	10	fullOrdinalPos	I
/*      */             //   913	8	12	E	Ljava/lang/Exception;
/*      */             //   1071	43	12	origColName	Ljava/lang/String;
/*      */             //   1085	29	13	realOrdinal	Ljava/lang/Integer;
/*      */             //   629	603	10	rowVal	[[B
/*      */             //   706	526	11	typeDesc	Lcom/mysql/jdbc/DatabaseMetaData$TypeDescriptor;
/*      */             //   1180	52	12	extra	Ljava/lang/String;
/*      */             //   245	990	6	queryBuf	Ljava/lang/StringBuffer;
/*      */             //   384	851	7	fixUpOrdinalsRequired	Z
/*      */             //   387	848	8	ordinalFixUpMap	Ljava/util/Map;
/*      */             //   612	623	9	ordPos	I
/*      */             //   1268	0	16	ex	Ljava/lang/Exception;
/*      */             //   231	1042	4	tableName	Ljava/lang/String;
/*      */             //   234	1039	5	results	Ljava/sql/ResultSet;
/*      */             //   0	1277	0	this	Lcom/mysql/jdbc/DatabaseMetaData$2;
/*      */             //   0	1277	1	catalogStr	Ljava/lang/Object;
/*      */             //   8	1269	2	tableNameList	Ljava/util/ArrayList;
/*      */             //   211	1066	3	tableNames	Ljava/util/Iterator;
/*      */             // Exception table:
/*      */             //   from	to	target	type
/*      */             //   17	71	74	finally
/*      */             //   74	79	74	finally
/*      */             //   88	94	97	java/lang/Exception
/*      */             //   113	169	172	finally
/*      */             //   172	177	172	finally
/*      */             //   186	192	195	java/sql/SQLException
/*      */             //   234	1238	1241	finally
/*      */             //   861	908	911	java/lang/Exception
/*      */             //   1241	1246	1241	finally
/* 2345 */             //   1256	1263	1266	java/lang/Exception } }).doForAll();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2591 */       if (stmt != null) {
/* 2592 */         stmt.close();
/*      */       }
/*      */     } 
/*      */     
/* 2596 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 2598 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Connection getConnection() throws SQLException {
/* 2609 */     return this.conn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
/* 2683 */     if (primaryTable == null) {
/* 2684 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 2688 */     Field[] fields = new Field[14];
/* 2689 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2690 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2691 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2692 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2693 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2694 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2695 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2696 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2697 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2698 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2699 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2700 */     fields[11] = new Field("", "FK_NAME", 1, 0);
/* 2701 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2702 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */     
/* 2704 */     ArrayList tuples = new ArrayList();
/*      */     
/* 2706 */     if (this.conn.versionMeetsMinimum(3, 23, 0)) {
/*      */       
/* 2708 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */       
/*      */       try {
/* 2712 */         (new IterateBlock(this, getCatalogIterator(foreignCatalog), stmt, foreignTable, primaryTable, foreignCatalog, foreignSchema, primaryCatalog, primarySchema, tuples) { private final Statement val$stmt; private final String val$foreignTable; private final String val$primaryTable; private final String val$foreignCatalog; private final String val$foreignSchema; private final String val$primaryCatalog; private final String val$primarySchema; private final ArrayList val$tuples; private final DatabaseMetaData this$0;
/*      */             
/*      */             void forEach(Object catalogStr) throws SQLException {
/* 2715 */               ResultSet fkresults = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 2722 */                 if (this.this$0.conn.versionMeetsMinimum(3, 23, 50)) {
/* 2723 */                   fkresults = this.this$0.extractForeignKeyFromCreateTable(catalogStr.toString(), null);
/*      */                 } else {
/*      */                   
/* 2726 */                   StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */                   
/* 2728 */                   queryBuf.append(this.this$0.quotedId);
/* 2729 */                   queryBuf.append(catalogStr.toString());
/* 2730 */                   queryBuf.append(this.this$0.quotedId);
/*      */                   
/* 2732 */                   fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */                 } 
/*      */ 
/*      */                 
/* 2736 */                 String foreignTableWithCase = this.this$0.getTableNameWithCase(this.val$foreignTable);
/* 2737 */                 String primaryTableWithCase = this.this$0.getTableNameWithCase(this.val$primaryTable);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2745 */                 while (fkresults.next()) {
/* 2746 */                   String tableType = fkresults.getString("Type");
/*      */                   
/* 2748 */                   if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
/*      */ 
/*      */ 
/*      */                     
/* 2752 */                     String comment = fkresults.getString("Comment").trim();
/*      */ 
/*      */                     
/* 2755 */                     if (comment != null) {
/* 2756 */                       StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/*      */                       
/* 2759 */                       if (commentTokens.hasMoreTokens()) {
/* 2760 */                         String dummy = commentTokens.nextToken();
/*      */                       }
/*      */ 
/*      */ 
/*      */                       
/* 2765 */                       while (commentTokens.hasMoreTokens()) {
/* 2766 */                         String keys = commentTokens.nextToken();
/*      */                         
/* 2768 */                         DatabaseMetaData.LocalAndReferencedColumns parsedInfo = this.this$0.parseTableStatusIntoLocalAndReferencedColumns(keys);
/*      */                         
/* 2770 */                         int keySeq = 0;
/*      */                         
/* 2772 */                         Iterator referencingColumns = parsedInfo.localColumnsList.iterator();
/*      */                         
/* 2774 */                         Iterator referencedColumns = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/*      */                         
/* 2777 */                         while (referencingColumns.hasNext()) {
/* 2778 */                           String referencingColumn = this.this$0.removeQuotedId(referencingColumns.next().toString());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                           
/* 2784 */                           byte[][] tuple = new byte[14][];
/* 2785 */                           tuple[4] = (this.val$foreignCatalog == null) ? null : this.this$0.s2b(this.val$foreignCatalog);
/*      */                           
/* 2787 */                           tuple[5] = (this.val$foreignSchema == null) ? null : this.this$0.s2b(this.val$foreignSchema);
/*      */                           
/* 2789 */                           String dummy = fkresults.getString("Name");
/*      */ 
/*      */                           
/* 2792 */                           if (dummy.compareTo(foreignTableWithCase) != 0) {
/*      */                             continue;
/*      */                           }
/*      */ 
/*      */                           
/* 2797 */                           tuple[6] = this.this$0.s2b(dummy);
/*      */                           
/* 2799 */                           tuple[7] = this.this$0.s2b(referencingColumn);
/* 2800 */                           tuple[0] = (this.val$primaryCatalog == null) ? null : this.this$0.s2b(this.val$primaryCatalog);
/*      */                           
/* 2802 */                           tuple[1] = (this.val$primarySchema == null) ? null : this.this$0.s2b(this.val$primarySchema);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                           
/* 2808 */                           if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) != 0) {
/*      */                             continue;
/*      */                           }
/*      */ 
/*      */                           
/* 2813 */                           tuple[2] = this.this$0.s2b(parsedInfo.referencedTable);
/* 2814 */                           tuple[3] = this.this$0.s2b(this.this$0.removeQuotedId(referencedColumns.next().toString()));
/*      */                           
/* 2816 */                           tuple[8] = Integer.toString(keySeq).getBytes();
/*      */ 
/*      */                           
/* 2819 */                           int[] actions = this.this$0.getForeignKeyActions(keys);
/*      */                           
/* 2821 */                           tuple[9] = Integer.toString(actions[1]).getBytes();
/*      */                           
/* 2823 */                           tuple[10] = Integer.toString(actions[0]).getBytes();
/*      */                           
/* 2825 */                           tuple[11] = null;
/* 2826 */                           tuple[12] = null;
/* 2827 */                           tuple[13] = Integer.toString(7).getBytes();
/*      */ 
/*      */ 
/*      */                           
/* 2831 */                           this.val$tuples.add(new ByteArrayRow(tuple));
/* 2832 */                           keySeq++;
/*      */                         } 
/*      */                       } 
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */               } finally {
/*      */                 
/* 2840 */                 if (fkresults != null) {
/*      */                   try {
/* 2842 */                     fkresults.close();
/* 2843 */                   } catch (Exception sqlEx) {
/* 2844 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */ 
/*      */                   
/* 2848 */                   fkresults = null;
/*      */                 } 
/*      */               } 
/*      */             } }
/*      */           ).doForAll();
/*      */       } finally {
/* 2854 */         if (stmt != null) {
/* 2855 */           stmt.close();
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 2860 */     ResultSet results = buildResultSet(fields, tuples);
/*      */     
/* 2862 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDatabaseMajorVersion() throws SQLException {
/* 2869 */     return this.conn.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDatabaseMinorVersion() throws SQLException {
/* 2876 */     return this.conn.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDatabaseProductName() throws SQLException {
/* 2887 */     return "MySQL";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDatabaseProductVersion() throws SQLException {
/* 2898 */     return this.conn.getServerVersion();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDefaultTransactionIsolation() throws SQLException {
/* 2911 */     if (this.conn.supportsIsolationLevel()) {
/* 2912 */       return 2;
/*      */     }
/*      */     
/* 2915 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDriverMajorVersion() {
/* 2924 */     return NonRegisteringDriver.getMajorVersionInternal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDriverMinorVersion() {
/* 2933 */     return NonRegisteringDriver.getMinorVersionInternal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDriverName() throws SQLException {
/* 2944 */     return "MySQL-AB JDBC Driver";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDriverVersion() throws SQLException {
/* 2955 */     return "mysql-connector-java-5.1.6 ( Revision: ${svn.Revision} )";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
/* 3019 */     if (table == null) {
/* 3020 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 3024 */     Field[] fields = new Field[14];
/* 3025 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 3026 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 3027 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 3028 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 3029 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 3030 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 3031 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 3032 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 3033 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 3034 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 3035 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 3036 */     fields[11] = new Field("", "FK_NAME", 1, 255);
/* 3037 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 3038 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */     
/* 3040 */     ArrayList rows = new ArrayList();
/*      */     
/* 3042 */     if (this.conn.versionMeetsMinimum(3, 23, 0)) {
/*      */       
/* 3044 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */       
/*      */       try {
/* 3048 */         (new IterateBlock(this, getCatalogIterator(catalog), stmt, table, rows) { private final Statement val$stmt; private final String val$table; private final ArrayList val$rows; private final DatabaseMetaData this$0;
/*      */             void forEach(Object catalogStr) throws SQLException {
/* 3050 */               ResultSet fkresults = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 3057 */                 if (this.this$0.conn.versionMeetsMinimum(3, 23, 50)) {
/*      */ 
/*      */                   
/* 3060 */                   fkresults = this.this$0.extractForeignKeyFromCreateTable(catalogStr.toString(), null);
/*      */                 } else {
/*      */                   
/* 3063 */                   StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */                   
/* 3065 */                   queryBuf.append(this.this$0.quotedId);
/* 3066 */                   queryBuf.append(catalogStr.toString());
/* 3067 */                   queryBuf.append(this.this$0.quotedId);
/*      */                   
/* 3069 */                   fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */                 } 
/*      */ 
/*      */ 
/*      */                 
/* 3074 */                 String tableNameWithCase = this.this$0.getTableNameWithCase(this.val$table);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 3080 */                 while (fkresults.next()) {
/* 3081 */                   String tableType = fkresults.getString("Type");
/*      */                   
/* 3083 */                   if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK")))
/*      */                   {
/*      */ 
/*      */                     
/* 3087 */                     String comment = fkresults.getString("Comment").trim();
/*      */ 
/*      */                     
/* 3090 */                     if (comment != null) {
/* 3091 */                       StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/*      */                       
/* 3094 */                       if (commentTokens.hasMoreTokens()) {
/* 3095 */                         commentTokens.nextToken();
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/* 3100 */                         while (commentTokens.hasMoreTokens()) {
/* 3101 */                           String keys = commentTokens.nextToken();
/*      */                           
/* 3103 */                           this.this$0.getExportKeyResults(catalogStr.toString(), tableNameWithCase, keys, this.val$rows, fkresults.getString("Name"));
/*      */                         }
/*      */                       
/*      */                       }
/*      */                     
/*      */                     }
/*      */                   
/*      */                   }
/*      */                 
/*      */                 }
/*      */               
/*      */               }
/*      */               finally {
/*      */                 
/* 3117 */                 if (fkresults != null) {
/*      */                   try {
/* 3119 */                     fkresults.close();
/* 3120 */                   } catch (SQLException sqlEx) {
/* 3121 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */ 
/*      */                   
/* 3125 */                   fkresults = null;
/*      */                 } 
/*      */               } 
/*      */             } }
/*      */           ).doForAll();
/*      */       } finally {
/* 3131 */         if (stmt != null) {
/* 3132 */           stmt.close();
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 3137 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 3139 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getExportKeyResults(String catalog, String exportingTable, String keysComment, List tuples, String fkTableName) throws SQLException {
/* 3163 */     getResultsImpl(catalog, exportingTable, keysComment, tuples, fkTableName, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getExtraNameCharacters() throws SQLException {
/* 3176 */     return "#@";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] getForeignKeyActions(String commentString) {
/* 3189 */     int[] actions = { 3, 3 };
/*      */ 
/*      */ 
/*      */     
/* 3193 */     int lastParenIndex = commentString.lastIndexOf(")");
/*      */     
/* 3195 */     if (lastParenIndex != commentString.length() - 1) {
/* 3196 */       String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
/*      */ 
/*      */       
/* 3199 */       actions[0] = getCascadeDeleteOption(cascadeOptions);
/* 3200 */       actions[1] = getCascadeUpdateOption(cascadeOptions);
/*      */     } 
/*      */     
/* 3203 */     return actions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getIdentifierQuoteString() throws SQLException {
/* 3216 */     if (this.conn.supportsQuotedIdentifiers()) {
/* 3217 */       if (!this.conn.useAnsiQuotedIdentifiers()) {
/* 3218 */         return "`";
/*      */       }
/*      */       
/* 3221 */       return "\"";
/*      */     } 
/*      */     
/* 3224 */     return " ";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
/* 3288 */     if (table == null) {
/* 3289 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 3293 */     Field[] fields = new Field[14];
/* 3294 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 3295 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 3296 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 3297 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 3298 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 3299 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 3300 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 3301 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 3302 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 3303 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 3304 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 3305 */     fields[11] = new Field("", "FK_NAME", 1, 255);
/* 3306 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 3307 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */     
/* 3309 */     ArrayList rows = new ArrayList();
/*      */     
/* 3311 */     if (this.conn.versionMeetsMinimum(3, 23, 0)) {
/*      */       
/* 3313 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */       
/*      */       try {
/* 3317 */         (new IterateBlock(this, getCatalogIterator(catalog), table, stmt, rows) { private final String val$table; private final Statement val$stmt; private final ArrayList val$rows; private final DatabaseMetaData this$0;
/*      */             void forEach(Object catalogStr) throws SQLException {
/* 3319 */               ResultSet fkresults = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 3326 */                 if (this.this$0.conn.versionMeetsMinimum(3, 23, 50)) {
/*      */ 
/*      */                   
/* 3329 */                   fkresults = this.this$0.extractForeignKeyFromCreateTable(catalogStr.toString(), this.val$table);
/*      */                 } else {
/*      */                   
/* 3332 */                   StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS ");
/*      */                   
/* 3334 */                   queryBuf.append(" FROM ");
/* 3335 */                   queryBuf.append(this.this$0.quotedId);
/* 3336 */                   queryBuf.append(catalogStr.toString());
/* 3337 */                   queryBuf.append(this.this$0.quotedId);
/* 3338 */                   queryBuf.append(" LIKE '");
/* 3339 */                   queryBuf.append(this.val$table);
/* 3340 */                   queryBuf.append("'");
/*      */                   
/* 3342 */                   fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */                 } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 3350 */                 while (fkresults.next()) {
/* 3351 */                   String tableType = fkresults.getString("Type");
/*      */                   
/* 3353 */                   if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
/*      */ 
/*      */ 
/*      */                     
/* 3357 */                     String comment = fkresults.getString("Comment").trim();
/*      */ 
/*      */                     
/* 3360 */                     if (comment != null) {
/* 3361 */                       StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/*      */                       
/* 3364 */                       if (commentTokens.hasMoreTokens()) {
/* 3365 */                         commentTokens.nextToken();
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/* 3370 */                         while (commentTokens.hasMoreTokens()) {
/* 3371 */                           String keys = commentTokens.nextToken();
/*      */                           
/* 3373 */                           this.this$0.getImportKeyResults(catalogStr.toString(), this.val$table, keys, this.val$rows);
/*      */                         }
/*      */                       
/*      */                       } 
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */               } finally {
/*      */                 
/* 3382 */                 if (fkresults != null) {
/*      */                   try {
/* 3384 */                     fkresults.close();
/* 3385 */                   } catch (SQLException sqlEx) {
/* 3386 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */ 
/*      */                   
/* 3390 */                   fkresults = null;
/*      */                 } 
/*      */               } 
/*      */             } }
/*      */           ).doForAll();
/*      */       } finally {
/* 3396 */         if (stmt != null) {
/* 3397 */           stmt.close();
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 3402 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 3404 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getImportKeyResults(String catalog, String importingTable, String keysComment, List tuples) throws SQLException {
/* 3426 */     getResultsImpl(catalog, importingTable, keysComment, tuples, null, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
/* 3497 */     Field[] fields = new Field[13];
/* 3498 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3499 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3500 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3501 */     fields[3] = new Field("", "NON_UNIQUE", 1, 4);
/* 3502 */     fields[4] = new Field("", "INDEX_QUALIFIER", 1, 1);
/* 3503 */     fields[5] = new Field("", "INDEX_NAME", 1, 32);
/* 3504 */     fields[6] = new Field("", "TYPE", 1, 32);
/* 3505 */     fields[7] = new Field("", "ORDINAL_POSITION", 5, 5);
/* 3506 */     fields[8] = new Field("", "COLUMN_NAME", 1, 32);
/* 3507 */     fields[9] = new Field("", "ASC_OR_DESC", 1, 1);
/* 3508 */     fields[10] = new Field("", "CARDINALITY", 4, 10);
/* 3509 */     fields[11] = new Field("", "PAGES", 4, 10);
/* 3510 */     fields[12] = new Field("", "FILTER_CONDITION", 1, 32);
/*      */     
/* 3512 */     ArrayList rows = new ArrayList();
/* 3513 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */     
/*      */     try {
/* 3517 */       (new IterateBlock(this, getCatalogIterator(catalog), table, stmt, unique, rows) { private final String val$table; private final Statement val$stmt; private final boolean val$unique; private final ArrayList val$rows; private final DatabaseMetaData this$0;
/*      */           
/*      */           void forEach(Object catalogStr) throws SQLException {
/* 3520 */             ResultSet results = null;
/*      */             
/*      */             try {
/* 3523 */               StringBuffer queryBuf = new StringBuffer("SHOW INDEX FROM ");
/*      */               
/* 3525 */               queryBuf.append(this.this$0.quotedId);
/* 3526 */               queryBuf.append(this.val$table);
/* 3527 */               queryBuf.append(this.this$0.quotedId);
/* 3528 */               queryBuf.append(" FROM ");
/* 3529 */               queryBuf.append(this.this$0.quotedId);
/* 3530 */               queryBuf.append(catalogStr.toString());
/* 3531 */               queryBuf.append(this.this$0.quotedId);
/*      */               
/*      */               try {
/* 3534 */                 results = this.val$stmt.executeQuery(queryBuf.toString());
/* 3535 */               } catch (SQLException sqlEx) {
/* 3536 */                 int errorCode = sqlEx.getErrorCode();
/*      */ 
/*      */ 
/*      */                 
/* 3540 */                 if (!"42S02".equals(sqlEx.getSQLState()))
/*      */                 {
/*      */                   
/* 3543 */                   if (errorCode != 1146) {
/* 3544 */                     throw sqlEx;
/*      */                   }
/*      */                 }
/*      */               } 
/*      */               
/* 3549 */               while (results != null && results.next()) {
/* 3550 */                 byte[][] row = new byte[14][];
/* 3551 */                 row[0] = (catalogStr.toString() == null) ? new byte[0] : this.this$0.s2b(catalogStr.toString());
/*      */ 
/*      */                 
/* 3554 */                 row[1] = null;
/* 3555 */                 row[2] = results.getBytes("Table");
/*      */                 
/* 3557 */                 boolean indexIsUnique = (results.getInt("Non_unique") == 0);
/*      */ 
/*      */                 
/* 3560 */                 row[3] = !indexIsUnique ? this.this$0.s2b("true") : this.this$0.s2b("false");
/*      */                 
/* 3562 */                 row[4] = new byte[0];
/* 3563 */                 row[5] = results.getBytes("Key_name");
/* 3564 */                 row[6] = Integer.toString(3).getBytes();
/*      */ 
/*      */                 
/* 3567 */                 row[7] = results.getBytes("Seq_in_index");
/* 3568 */                 row[8] = results.getBytes("Column_name");
/* 3569 */                 row[9] = results.getBytes("Collation");
/* 3570 */                 row[10] = results.getBytes("Cardinality");
/* 3571 */                 row[11] = this.this$0.s2b("0");
/* 3572 */                 row[12] = null;
/*      */                 
/* 3574 */                 if (this.val$unique) {
/* 3575 */                   if (indexIsUnique) {
/* 3576 */                     this.val$rows.add(new ByteArrayRow(row));
/*      */                   }
/*      */                   continue;
/*      */                 } 
/* 3580 */                 this.val$rows.add(new ByteArrayRow(row));
/*      */               } 
/*      */             } finally {
/*      */               
/* 3584 */               if (results != null) {
/*      */                 try {
/* 3586 */                   results.close();
/* 3587 */                 } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */                 
/* 3591 */                 results = null;
/*      */               } 
/*      */             } 
/*      */           } }
/*      */         ).doForAll();
/*      */       
/* 3597 */       ResultSet indexInfo = buildResultSet(fields, rows);
/*      */       
/* 3599 */       return indexInfo;
/*      */     } finally {
/* 3601 */       if (stmt != null) {
/* 3602 */         stmt.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getJDBCMajorVersion() throws SQLException {
/* 3611 */     return 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getJDBCMinorVersion() throws SQLException {
/* 3618 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxBinaryLiteralLength() throws SQLException {
/* 3629 */     return 16777208;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCatalogNameLength() throws SQLException {
/* 3640 */     return 32;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCharLiteralLength() throws SQLException {
/* 3651 */     return 16777208;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnNameLength() throws SQLException {
/* 3662 */     return 64;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInGroupBy() throws SQLException {
/* 3673 */     return 64;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInIndex() throws SQLException {
/* 3684 */     return 16;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInOrderBy() throws SQLException {
/* 3695 */     return 64;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInSelect() throws SQLException {
/* 3706 */     return 256;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInTable() throws SQLException {
/* 3717 */     return 512;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxConnections() throws SQLException {
/* 3728 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCursorNameLength() throws SQLException {
/* 3739 */     return 64;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxIndexLength() throws SQLException {
/* 3750 */     return 256;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxProcedureNameLength() throws SQLException {
/* 3761 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxRowSize() throws SQLException {
/* 3772 */     return 2147483639;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxSchemaNameLength() throws SQLException {
/* 3783 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxStatementLength() throws SQLException {
/* 3794 */     return MysqlIO.getMaxBuf() - 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxStatements() throws SQLException {
/* 3805 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxTableNameLength() throws SQLException {
/* 3816 */     return 64;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxTablesInSelect() throws SQLException {
/* 3827 */     return 256;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxUserNameLength() throws SQLException {
/* 3838 */     return 16;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNumericFunctions() throws SQLException {
/* 3849 */     return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
/* 3881 */     Field[] fields = new Field[6];
/* 3882 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3883 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3884 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3885 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 3886 */     fields[4] = new Field("", "KEY_SEQ", 5, 5);
/* 3887 */     fields[5] = new Field("", "PK_NAME", 1, 32);
/*      */     
/* 3889 */     if (table == null) {
/* 3890 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 3894 */     ArrayList rows = new ArrayList();
/* 3895 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */     
/*      */     try {
/* 3899 */       (new IterateBlock(this, getCatalogIterator(catalog), table, stmt, rows) { private final String val$table; private final Statement val$stmt; private final ArrayList val$rows; private final DatabaseMetaData this$0;
/*      */           void forEach(Object catalogStr) throws SQLException {
/* 3901 */             ResultSet rs = null;
/*      */ 
/*      */             
/*      */             try {
/* 3905 */               StringBuffer queryBuf = new StringBuffer("SHOW KEYS FROM ");
/*      */               
/* 3907 */               queryBuf.append(this.this$0.quotedId);
/* 3908 */               queryBuf.append(this.val$table);
/* 3909 */               queryBuf.append(this.this$0.quotedId);
/* 3910 */               queryBuf.append(" FROM ");
/* 3911 */               queryBuf.append(this.this$0.quotedId);
/* 3912 */               queryBuf.append(catalogStr.toString());
/* 3913 */               queryBuf.append(this.this$0.quotedId);
/*      */               
/* 3915 */               rs = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               
/* 3917 */               TreeMap sortMap = new TreeMap();
/*      */               
/* 3919 */               while (rs.next()) {
/* 3920 */                 String keyType = rs.getString("Key_name");
/*      */                 
/* 3922 */                 if (keyType != null && (
/* 3923 */                   keyType.equalsIgnoreCase("PRIMARY") || keyType.equalsIgnoreCase("PRI"))) {
/*      */                   
/* 3925 */                   byte[][] tuple = new byte[6][];
/* 3926 */                   tuple[0] = (catalogStr.toString() == null) ? new byte[0] : this.this$0.s2b(catalogStr.toString());
/*      */                   
/* 3928 */                   tuple[1] = null;
/* 3929 */                   tuple[2] = this.this$0.s2b(this.val$table);
/*      */                   
/* 3931 */                   String columnName = rs.getString("Column_name");
/*      */                   
/* 3933 */                   tuple[3] = this.this$0.s2b(columnName);
/* 3934 */                   tuple[4] = this.this$0.s2b(rs.getString("Seq_in_index"));
/* 3935 */                   tuple[5] = this.this$0.s2b(keyType);
/* 3936 */                   sortMap.put(columnName, tuple);
/*      */                 } 
/*      */               } 
/*      */ 
/*      */ 
/*      */               
/* 3942 */               Iterator sortedIterator = sortMap.values().iterator();
/*      */               
/* 3944 */               while (sortedIterator.hasNext()) {
/* 3945 */                 this.val$rows.add(new ByteArrayRow(sortedIterator.next()));
/*      */               }
/*      */             } finally {
/*      */               
/* 3949 */               if (rs != null) {
/*      */                 try {
/* 3951 */                   rs.close();
/* 3952 */                 } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */                 
/* 3956 */                 rs = null;
/*      */               } 
/*      */             } 
/*      */           } }
/*      */         ).doForAll();
/*      */     } finally {
/* 3962 */       if (stmt != null) {
/* 3963 */         stmt.close();
/*      */       }
/*      */     } 
/*      */     
/* 3967 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 3969 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
/* 4041 */     Field[] fields = new Field[13];
/*      */     
/* 4043 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/* 4044 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/* 4045 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/* 4046 */     fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/* 4047 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/* 4048 */     fields[5] = new Field("", "DATA_TYPE", 5, 0);
/* 4049 */     fields[6] = new Field("", "TYPE_NAME", 1, 0);
/* 4050 */     fields[7] = new Field("", "PRECISION", 4, 0);
/* 4051 */     fields[8] = new Field("", "LENGTH", 4, 0);
/* 4052 */     fields[9] = new Field("", "SCALE", 5, 0);
/* 4053 */     fields[10] = new Field("", "RADIX", 5, 0);
/* 4054 */     fields[11] = new Field("", "NULLABLE", 5, 0);
/* 4055 */     fields[12] = new Field("", "REMARKS", 1, 0);
/*      */     
/* 4057 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
/* 4069 */     List proceduresToExtractList = new ArrayList();
/*      */     
/* 4071 */     if (supportsStoredProcedures()) {
/* 4072 */       if (procedureOrFunctionNamePattern.indexOf("%") == -1 && procedureOrFunctionNamePattern.indexOf("?") == -1) {
/*      */         
/* 4074 */         proceduresToExtractList.add(procedureOrFunctionNamePattern);
/*      */       } else {
/*      */         
/* 4077 */         ResultSet procedureNameRs = null;
/*      */ 
/*      */         
/*      */         try {
/* 4081 */           procedureNameRs = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, procedureOrFunctionNamePattern, returnProcedures, returnFunctions);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 4087 */           while (procedureNameRs.next()) {
/* 4088 */             proceduresToExtractList.add(procedureNameRs.getString(3));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 4096 */           Collections.sort(proceduresToExtractList);
/*      */         } finally {
/* 4098 */           SQLException rethrowSqlEx = null;
/*      */           
/* 4100 */           if (procedureNameRs != null) {
/*      */             try {
/* 4102 */               procedureNameRs.close();
/* 4103 */             } catch (SQLException sqlEx) {
/* 4104 */               rethrowSqlEx = sqlEx;
/*      */             } 
/*      */           }
/*      */           
/* 4108 */           if (rethrowSqlEx != null) {
/* 4109 */             throw rethrowSqlEx;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 4115 */     ArrayList resultRows = new ArrayList();
/*      */     
/* 4117 */     for (Iterator iter = proceduresToExtractList.iterator(); iter.hasNext(); ) {
/* 4118 */       String procName = iter.next();
/*      */       
/* 4120 */       getCallStmtParameterTypes(catalog, procName, columnNamePattern, resultRows, (fields.length == 17));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 4125 */     return buildResultSet(fields, resultRows);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
/* 4171 */     Field[] fields = createFieldMetadataForGetProcedures();
/*      */     
/* 4173 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, procedureNamePattern, true, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private Field[] createFieldMetadataForGetProcedures() {
/* 4178 */     Field[] fields = new Field[9];
/* 4179 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 255);
/* 4180 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 255);
/* 4181 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 255);
/* 4182 */     fields[3] = new Field("", "reserved1", 1, 0);
/* 4183 */     fields[4] = new Field("", "reserved2", 1, 0);
/* 4184 */     fields[5] = new Field("", "reserved3", 1, 0);
/* 4185 */     fields[6] = new Field("", "REMARKS", 1, 255);
/* 4186 */     fields[7] = new Field("", "PROCEDURE_TYPE", 5, 6);
/* 4187 */     fields[8] = new Field("", "SPECIFIC_NAME", 1, 255);
/*      */     
/* 4189 */     return fields;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ResultSet getProceduresAndOrFunctions(Field[] fields, String catalog, String schemaPattern, String procedureNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
/* 4199 */     if (procedureNamePattern == null || procedureNamePattern.length() == 0)
/*      */     {
/* 4201 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 4202 */         procedureNamePattern = "%";
/*      */       } else {
/* 4204 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 4210 */     ArrayList procedureRows = new ArrayList();
/*      */     
/* 4212 */     if (supportsStoredProcedures()) {
/* 4213 */       String procNamePattern = procedureNamePattern;
/*      */       
/* 4215 */       Map procedureRowsOrderedByName = new TreeMap();
/*      */       
/* 4217 */       (new IterateBlock(this, getCatalogIterator(catalog), procNamePattern, returnProcedures, procedureRowsOrderedByName, returnFunctions, fields, procedureRows) { private final String val$procNamePattern; private final boolean val$returnProcedures; private final Map val$procedureRowsOrderedByName; private final boolean val$returnFunctions; private final Field[] val$fields; private final ArrayList val$procedureRows; private final DatabaseMetaData this$0;
/*      */           void forEach(Object catalogStr) throws SQLException {
/* 4219 */             String db = catalogStr.toString();
/*      */             
/* 4221 */             boolean fromSelect = false;
/* 4222 */             ResultSet proceduresRs = null;
/* 4223 */             boolean needsClientFiltering = true;
/* 4224 */             PreparedStatement proceduresStmt = (PreparedStatement)this.this$0.conn.clientPrepareStatement("SELECT name, type, comment FROM mysql.proc WHERE name like ? and db <=> ? ORDER BY name");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             try {
/* 4233 */               boolean hasTypeColumn = false;
/*      */               
/* 4235 */               if (db != null) {
/* 4236 */                 proceduresStmt.setString(2, db);
/*      */               } else {
/* 4238 */                 proceduresStmt.setNull(2, 12);
/*      */               } 
/*      */               
/* 4241 */               int nameIndex = 1;
/*      */               
/* 4243 */               if (proceduresStmt.getMaxRows() != 0) {
/* 4244 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */               
/* 4247 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */               
/*      */               try {
/* 4250 */                 proceduresRs = proceduresStmt.executeQuery();
/* 4251 */                 fromSelect = true;
/* 4252 */                 needsClientFiltering = false;
/* 4253 */                 hasTypeColumn = true;
/* 4254 */               } catch (SQLException sqlEx) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 4261 */                 proceduresStmt.close();
/*      */                 
/* 4263 */                 fromSelect = false;
/*      */                 
/* 4265 */                 if (this.this$0.conn.versionMeetsMinimum(5, 0, 1)) {
/* 4266 */                   nameIndex = 2;
/*      */                 } else {
/* 4268 */                   nameIndex = 1;
/*      */                 } 
/*      */                 
/* 4271 */                 proceduresStmt = (PreparedStatement)this.this$0.conn.clientPrepareStatement("SHOW PROCEDURE STATUS LIKE ?");
/*      */ 
/*      */                 
/* 4274 */                 if (proceduresStmt.getMaxRows() != 0) {
/* 4275 */                   proceduresStmt.setMaxRows(0);
/*      */                 }
/*      */                 
/* 4278 */                 proceduresStmt.setString(1, this.val$procNamePattern);
/*      */                 
/* 4280 */                 proceduresRs = proceduresStmt.executeQuery();
/*      */               } 
/*      */               
/* 4283 */               if (this.val$returnProcedures) {
/* 4284 */                 this.this$0.convertToJdbcProcedureList(fromSelect, db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex);
/*      */               }
/*      */ 
/*      */ 
/*      */               
/* 4289 */               if (!hasTypeColumn) {
/*      */                 
/* 4291 */                 if (proceduresStmt != null) {
/* 4292 */                   proceduresStmt.close();
/*      */                 }
/*      */                 
/* 4295 */                 proceduresStmt = (PreparedStatement)this.this$0.conn.clientPrepareStatement("SHOW FUNCTION STATUS LIKE ?");
/*      */ 
/*      */                 
/* 4298 */                 if (proceduresStmt.getMaxRows() != 0) {
/* 4299 */                   proceduresStmt.setMaxRows(0);
/*      */                 }
/*      */                 
/* 4302 */                 proceduresStmt.setString(1, this.val$procNamePattern);
/*      */                 
/* 4304 */                 proceduresRs = proceduresStmt.executeQuery();
/*      */                 
/* 4306 */                 if (this.val$returnFunctions) {
/* 4307 */                   this.this$0.convertToJdbcFunctionList(db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex, this.val$fields);
/*      */                 }
/*      */               } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 4316 */               Iterator proceduresIter = this.val$procedureRowsOrderedByName.values().iterator();
/*      */ 
/*      */               
/* 4319 */               while (proceduresIter.hasNext()) {
/* 4320 */                 this.val$procedureRows.add(proceduresIter.next());
/*      */               }
/*      */             } finally {
/* 4323 */               SQLException rethrowSqlEx = null;
/*      */               
/* 4325 */               if (proceduresRs != null) {
/*      */                 try {
/* 4327 */                   proceduresRs.close();
/* 4328 */                 } catch (SQLException sqlEx) {
/* 4329 */                   rethrowSqlEx = sqlEx;
/*      */                 } 
/*      */               }
/*      */               
/* 4333 */               if (proceduresStmt != null) {
/*      */                 try {
/* 4335 */                   proceduresStmt.close();
/* 4336 */                 } catch (SQLException sqlEx) {
/* 4337 */                   rethrowSqlEx = sqlEx;
/*      */                 } 
/*      */               }
/*      */               
/* 4341 */               if (rethrowSqlEx != null) {
/* 4342 */                 throw rethrowSqlEx;
/*      */               }
/*      */             } 
/*      */           } }
/*      */         ).doForAll();
/*      */     } 
/*      */     
/* 4349 */     return buildResultSet(fields, procedureRows);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getProcedureTerm() throws SQLException {
/* 4361 */     return "PROCEDURE";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getResultSetHoldability() throws SQLException {
/* 4368 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getResultsImpl(String catalog, String table, String keysComment, List tuples, String fkTableName, boolean isExport) throws SQLException {
/* 4375 */     LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
/*      */     
/* 4377 */     if (isExport && !parsedInfo.referencedTable.equals(table)) {
/*      */       return;
/*      */     }
/*      */     
/* 4381 */     if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size())
/*      */     {
/* 4383 */       throw SQLError.createSQLException("Error parsing foreign keys definition,number of local and referenced columns is not the same.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4389 */     Iterator localColumnNames = parsedInfo.localColumnsList.iterator();
/* 4390 */     Iterator referColumnNames = parsedInfo.referencedColumnsList.iterator();
/*      */     
/* 4392 */     int keySeqIndex = 1;
/*      */     
/* 4394 */     while (localColumnNames.hasNext()) {
/* 4395 */       byte[][] tuple = new byte[14][];
/* 4396 */       String lColumnName = removeQuotedId(localColumnNames.next().toString());
/*      */       
/* 4398 */       String rColumnName = removeQuotedId(referColumnNames.next().toString());
/*      */       
/* 4400 */       tuple[4] = (catalog == null) ? new byte[0] : s2b(catalog);
/*      */       
/* 4402 */       tuple[5] = null;
/* 4403 */       tuple[6] = s2b(isExport ? fkTableName : table);
/* 4404 */       tuple[7] = s2b(lColumnName);
/* 4405 */       tuple[0] = s2b(parsedInfo.referencedCatalog);
/* 4406 */       tuple[1] = null;
/* 4407 */       tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
/*      */       
/* 4409 */       tuple[3] = s2b(rColumnName);
/* 4410 */       tuple[8] = s2b(Integer.toString(keySeqIndex++));
/*      */       
/* 4412 */       int[] actions = getForeignKeyActions(keysComment);
/*      */       
/* 4414 */       tuple[9] = s2b(Integer.toString(actions[1]));
/* 4415 */       tuple[10] = s2b(Integer.toString(actions[0]));
/* 4416 */       tuple[11] = s2b(parsedInfo.constraintName);
/* 4417 */       tuple[12] = null;
/* 4418 */       tuple[13] = s2b(Integer.toString(7));
/*      */       
/* 4420 */       tuples.add(new ByteArrayRow(tuple));
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
/*      */   public ResultSet getSchemas() throws SQLException {
/* 4440 */     Field[] fields = new Field[2];
/* 4441 */     fields[0] = new Field("", "TABLE_SCHEM", 1, 0);
/* 4442 */     fields[1] = new Field("", "TABLE_CATALOG", 1, 0);
/*      */     
/* 4444 */     ArrayList tuples = new ArrayList();
/* 4445 */     ResultSet results = buildResultSet(fields, tuples);
/*      */     
/* 4447 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSchemaTerm() throws SQLException {
/* 4458 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSearchStringEscape() throws SQLException {
/* 4476 */     return "\\";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSQLKeywords() throws SQLException {
/* 4488 */     return mysqlKeywordsThatArentSQL92;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSQLStateType() throws SQLException {
/* 4495 */     if (this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 4496 */       return 2;
/*      */     }
/*      */     
/* 4499 */     if (this.conn.getUseSqlStateCodes()) {
/* 4500 */       return 2;
/*      */     }
/*      */     
/* 4503 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getStringFunctions() throws SQLException {
/* 4514 */     return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSuperTables(String arg0, String arg1, String arg2) throws SQLException {
/* 4528 */     Field[] fields = new Field[4];
/* 4529 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4530 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4531 */     fields[2] = new Field("", "TABLE_NAME", 1, 32);
/* 4532 */     fields[3] = new Field("", "SUPERTABLE_NAME", 1, 32);
/*      */     
/* 4534 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSuperTypes(String arg0, String arg1, String arg2) throws SQLException {
/* 4542 */     Field[] fields = new Field[6];
/* 4543 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4544 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4545 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 4546 */     fields[3] = new Field("", "SUPERTYPE_CAT", 1, 32);
/* 4547 */     fields[4] = new Field("", "SUPERTYPE_SCHEM", 1, 32);
/* 4548 */     fields[5] = new Field("", "SUPERTYPE_NAME", 1, 32);
/*      */     
/* 4550 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSystemFunctions() throws SQLException {
/* 4561 */     return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
/*      */   }
/*      */   
/*      */   private String getTableNameWithCase(String table) {
/* 4565 */     String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
/*      */ 
/*      */     
/* 4568 */     return tableNameWithCase;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
/*      */     // Byte code:
/*      */     //   0: aload_3
/*      */     //   1: ifnonnull -> 29
/*      */     //   4: aload_0
/*      */     //   5: getfield conn : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   8: invokevirtual getNullNamePatternMatchesAll : ()Z
/*      */     //   11: ifeq -> 20
/*      */     //   14: ldc '%'
/*      */     //   16: astore_3
/*      */     //   17: goto -> 29
/*      */     //   20: ldc_w 'Table name pattern can not be NULL or empty.'
/*      */     //   23: ldc 'S1009'
/*      */     //   25: invokestatic createSQLException : (Ljava/lang/String;Ljava/lang/String;)Ljava/sql/SQLException;
/*      */     //   28: athrow
/*      */     //   29: bipush #7
/*      */     //   31: anewarray com/mysql/jdbc/Field
/*      */     //   34: astore #4
/*      */     //   36: aload #4
/*      */     //   38: iconst_0
/*      */     //   39: new com/mysql/jdbc/Field
/*      */     //   42: dup
/*      */     //   43: ldc ''
/*      */     //   45: ldc 'TABLE_CAT'
/*      */     //   47: iconst_1
/*      */     //   48: bipush #64
/*      */     //   50: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   53: aastore
/*      */     //   54: aload #4
/*      */     //   56: iconst_1
/*      */     //   57: new com/mysql/jdbc/Field
/*      */     //   60: dup
/*      */     //   61: ldc ''
/*      */     //   63: ldc 'TABLE_SCHEM'
/*      */     //   65: iconst_1
/*      */     //   66: iconst_1
/*      */     //   67: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   70: aastore
/*      */     //   71: aload #4
/*      */     //   73: iconst_2
/*      */     //   74: new com/mysql/jdbc/Field
/*      */     //   77: dup
/*      */     //   78: ldc ''
/*      */     //   80: ldc 'TABLE_NAME'
/*      */     //   82: iconst_1
/*      */     //   83: bipush #64
/*      */     //   85: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   88: aastore
/*      */     //   89: aload #4
/*      */     //   91: iconst_3
/*      */     //   92: new com/mysql/jdbc/Field
/*      */     //   95: dup
/*      */     //   96: ldc ''
/*      */     //   98: ldc 'GRANTOR'
/*      */     //   100: iconst_1
/*      */     //   101: bipush #77
/*      */     //   103: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   106: aastore
/*      */     //   107: aload #4
/*      */     //   109: iconst_4
/*      */     //   110: new com/mysql/jdbc/Field
/*      */     //   113: dup
/*      */     //   114: ldc ''
/*      */     //   116: ldc_w 'GRANTEE'
/*      */     //   119: iconst_1
/*      */     //   120: bipush #77
/*      */     //   122: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   125: aastore
/*      */     //   126: aload #4
/*      */     //   128: iconst_5
/*      */     //   129: new com/mysql/jdbc/Field
/*      */     //   132: dup
/*      */     //   133: ldc ''
/*      */     //   135: ldc_w 'PRIVILEGE'
/*      */     //   138: iconst_1
/*      */     //   139: bipush #64
/*      */     //   141: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   144: aastore
/*      */     //   145: aload #4
/*      */     //   147: bipush #6
/*      */     //   149: new com/mysql/jdbc/Field
/*      */     //   152: dup
/*      */     //   153: ldc ''
/*      */     //   155: ldc_w 'IS_GRANTABLE'
/*      */     //   158: iconst_1
/*      */     //   159: iconst_3
/*      */     //   160: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;II)V
/*      */     //   163: aastore
/*      */     //   164: new java/lang/StringBuffer
/*      */     //   167: dup
/*      */     //   168: ldc_w 'SELECT host,db,table_name,grantor,user,table_priv from mysql.tables_priv '
/*      */     //   171: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   174: astore #5
/*      */     //   176: aload #5
/*      */     //   178: ldc_w ' WHERE '
/*      */     //   181: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   184: pop
/*      */     //   185: aload_1
/*      */     //   186: ifnull -> 221
/*      */     //   189: aload_1
/*      */     //   190: invokevirtual length : ()I
/*      */     //   193: ifeq -> 221
/*      */     //   196: aload #5
/*      */     //   198: ldc_w ' db=''
/*      */     //   201: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   204: pop
/*      */     //   205: aload #5
/*      */     //   207: aload_1
/*      */     //   208: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   211: pop
/*      */     //   212: aload #5
/*      */     //   214: ldc_w '' AND '
/*      */     //   217: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   220: pop
/*      */     //   221: aload #5
/*      */     //   223: ldc_w 'table_name like ''
/*      */     //   226: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   229: pop
/*      */     //   230: aload #5
/*      */     //   232: aload_3
/*      */     //   233: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   236: pop
/*      */     //   237: aload #5
/*      */     //   239: ldc '''
/*      */     //   241: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   244: pop
/*      */     //   245: aconst_null
/*      */     //   246: astore #6
/*      */     //   248: new java/util/ArrayList
/*      */     //   251: dup
/*      */     //   252: invokespecial <init> : ()V
/*      */     //   255: astore #7
/*      */     //   257: aconst_null
/*      */     //   258: astore #8
/*      */     //   260: aload_0
/*      */     //   261: getfield conn : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   264: invokevirtual createStatement : ()Ljava/sql/Statement;
/*      */     //   267: astore #8
/*      */     //   269: aload #8
/*      */     //   271: iconst_0
/*      */     //   272: invokeinterface setEscapeProcessing : (Z)V
/*      */     //   277: aload #8
/*      */     //   279: aload #5
/*      */     //   281: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   284: invokeinterface executeQuery : (Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */     //   289: astore #6
/*      */     //   291: aload #6
/*      */     //   293: invokeinterface next : ()Z
/*      */     //   298: ifeq -> 636
/*      */     //   301: aload #6
/*      */     //   303: iconst_1
/*      */     //   304: invokeinterface getString : (I)Ljava/lang/String;
/*      */     //   309: astore #9
/*      */     //   311: aload #6
/*      */     //   313: iconst_2
/*      */     //   314: invokeinterface getString : (I)Ljava/lang/String;
/*      */     //   319: astore #10
/*      */     //   321: aload #6
/*      */     //   323: iconst_3
/*      */     //   324: invokeinterface getString : (I)Ljava/lang/String;
/*      */     //   329: astore #11
/*      */     //   331: aload #6
/*      */     //   333: iconst_4
/*      */     //   334: invokeinterface getString : (I)Ljava/lang/String;
/*      */     //   339: astore #12
/*      */     //   341: aload #6
/*      */     //   343: iconst_5
/*      */     //   344: invokeinterface getString : (I)Ljava/lang/String;
/*      */     //   349: astore #13
/*      */     //   351: aload #13
/*      */     //   353: ifnull -> 364
/*      */     //   356: aload #13
/*      */     //   358: invokevirtual length : ()I
/*      */     //   361: ifne -> 368
/*      */     //   364: ldc '%'
/*      */     //   366: astore #13
/*      */     //   368: new java/lang/StringBuffer
/*      */     //   371: dup
/*      */     //   372: aload #13
/*      */     //   374: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   377: astore #14
/*      */     //   379: aload #9
/*      */     //   381: ifnull -> 411
/*      */     //   384: aload_0
/*      */     //   385: getfield conn : Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   388: invokevirtual getUseHostsInPrivileges : ()Z
/*      */     //   391: ifeq -> 411
/*      */     //   394: aload #14
/*      */     //   396: ldc_w '@'
/*      */     //   399: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   402: pop
/*      */     //   403: aload #14
/*      */     //   405: aload #9
/*      */     //   407: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   410: pop
/*      */     //   411: aload #6
/*      */     //   413: bipush #6
/*      */     //   415: invokeinterface getString : (I)Ljava/lang/String;
/*      */     //   420: astore #15
/*      */     //   422: aload #15
/*      */     //   424: ifnull -> 633
/*      */     //   427: aload #15
/*      */     //   429: getstatic java/util/Locale.ENGLISH : Ljava/util/Locale;
/*      */     //   432: invokevirtual toUpperCase : (Ljava/util/Locale;)Ljava/lang/String;
/*      */     //   435: astore #15
/*      */     //   437: new java/util/StringTokenizer
/*      */     //   440: dup
/*      */     //   441: aload #15
/*      */     //   443: ldc ','
/*      */     //   445: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   448: astore #16
/*      */     //   450: aload #16
/*      */     //   452: invokevirtual hasMoreTokens : ()Z
/*      */     //   455: ifeq -> 633
/*      */     //   458: aload #16
/*      */     //   460: invokevirtual nextToken : ()Ljava/lang/String;
/*      */     //   463: invokevirtual trim : ()Ljava/lang/String;
/*      */     //   466: astore #17
/*      */     //   468: aconst_null
/*      */     //   469: astore #18
/*      */     //   471: aload_0
/*      */     //   472: aload_1
/*      */     //   473: aload_2
/*      */     //   474: aload #11
/*      */     //   476: ldc '%'
/*      */     //   478: invokevirtual getColumns : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/ResultSet;
/*      */     //   481: astore #18
/*      */     //   483: aload #18
/*      */     //   485: invokeinterface next : ()Z
/*      */     //   490: ifeq -> 595
/*      */     //   493: bipush #8
/*      */     //   495: anewarray [B
/*      */     //   498: astore #19
/*      */     //   500: aload #19
/*      */     //   502: iconst_0
/*      */     //   503: aload_0
/*      */     //   504: aload #10
/*      */     //   506: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */     //   509: aastore
/*      */     //   510: aload #19
/*      */     //   512: iconst_1
/*      */     //   513: aconst_null
/*      */     //   514: aastore
/*      */     //   515: aload #19
/*      */     //   517: iconst_2
/*      */     //   518: aload_0
/*      */     //   519: aload #11
/*      */     //   521: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */     //   524: aastore
/*      */     //   525: aload #12
/*      */     //   527: ifnull -> 543
/*      */     //   530: aload #19
/*      */     //   532: iconst_3
/*      */     //   533: aload_0
/*      */     //   534: aload #12
/*      */     //   536: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */     //   539: aastore
/*      */     //   540: goto -> 548
/*      */     //   543: aload #19
/*      */     //   545: iconst_3
/*      */     //   546: aconst_null
/*      */     //   547: aastore
/*      */     //   548: aload #19
/*      */     //   550: iconst_4
/*      */     //   551: aload_0
/*      */     //   552: aload #14
/*      */     //   554: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   557: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */     //   560: aastore
/*      */     //   561: aload #19
/*      */     //   563: iconst_5
/*      */     //   564: aload_0
/*      */     //   565: aload #17
/*      */     //   567: invokevirtual s2b : (Ljava/lang/String;)[B
/*      */     //   570: aastore
/*      */     //   571: aload #19
/*      */     //   573: bipush #6
/*      */     //   575: aconst_null
/*      */     //   576: aastore
/*      */     //   577: aload #7
/*      */     //   579: new com/mysql/jdbc/ByteArrayRow
/*      */     //   582: dup
/*      */     //   583: aload #19
/*      */     //   585: invokespecial <init> : ([[B)V
/*      */     //   588: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   591: pop
/*      */     //   592: goto -> 483
/*      */     //   595: jsr -> 609
/*      */     //   598: goto -> 630
/*      */     //   601: astore #20
/*      */     //   603: jsr -> 609
/*      */     //   606: aload #20
/*      */     //   608: athrow
/*      */     //   609: astore #21
/*      */     //   611: aload #18
/*      */     //   613: ifnull -> 628
/*      */     //   616: aload #18
/*      */     //   618: invokeinterface close : ()V
/*      */     //   623: goto -> 628
/*      */     //   626: astore #22
/*      */     //   628: ret #21
/*      */     //   630: goto -> 450
/*      */     //   633: goto -> 291
/*      */     //   636: jsr -> 650
/*      */     //   639: goto -> 694
/*      */     //   642: astore #23
/*      */     //   644: jsr -> 650
/*      */     //   647: aload #23
/*      */     //   649: athrow
/*      */     //   650: astore #24
/*      */     //   652: aload #6
/*      */     //   654: ifnull -> 672
/*      */     //   657: aload #6
/*      */     //   659: invokeinterface close : ()V
/*      */     //   664: goto -> 669
/*      */     //   667: astore #25
/*      */     //   669: aconst_null
/*      */     //   670: astore #6
/*      */     //   672: aload #8
/*      */     //   674: ifnull -> 692
/*      */     //   677: aload #8
/*      */     //   679: invokeinterface close : ()V
/*      */     //   684: goto -> 689
/*      */     //   687: astore #25
/*      */     //   689: aconst_null
/*      */     //   690: astore #8
/*      */     //   692: ret #24
/*      */     //   694: aload_0
/*      */     //   695: aload #4
/*      */     //   697: aload #7
/*      */     //   699: invokespecial buildResultSet : ([Lcom/mysql/jdbc/Field;Ljava/util/ArrayList;)Ljava/sql/ResultSet;
/*      */     //   702: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #4608	-> 0
/*      */     //   #4609	-> 4
/*      */     //   #4610	-> 14
/*      */     //   #4612	-> 20
/*      */     //   #4618	-> 29
/*      */     //   #4619	-> 36
/*      */     //   #4620	-> 54
/*      */     //   #4621	-> 71
/*      */     //   #4622	-> 89
/*      */     //   #4623	-> 107
/*      */     //   #4624	-> 126
/*      */     //   #4625	-> 145
/*      */     //   #4627	-> 164
/*      */     //   #4629	-> 176
/*      */     //   #4631	-> 185
/*      */     //   #4632	-> 196
/*      */     //   #4633	-> 205
/*      */     //   #4634	-> 212
/*      */     //   #4637	-> 221
/*      */     //   #4638	-> 230
/*      */     //   #4639	-> 237
/*      */     //   #4641	-> 245
/*      */     //   #4642	-> 248
/*      */     //   #4643	-> 257
/*      */     //   #4646	-> 260
/*      */     //   #4647	-> 269
/*      */     //   #4649	-> 277
/*      */     //   #4651	-> 291
/*      */     //   #4652	-> 301
/*      */     //   #4653	-> 311
/*      */     //   #4654	-> 321
/*      */     //   #4655	-> 331
/*      */     //   #4656	-> 341
/*      */     //   #4658	-> 351
/*      */     //   #4659	-> 364
/*      */     //   #4662	-> 368
/*      */     //   #4664	-> 379
/*      */     //   #4665	-> 394
/*      */     //   #4666	-> 403
/*      */     //   #4669	-> 411
/*      */     //   #4671	-> 422
/*      */     //   #4672	-> 427
/*      */     //   #4674	-> 437
/*      */     //   #4676	-> 450
/*      */     //   #4677	-> 458
/*      */     //   #4680	-> 468
/*      */     //   #4683	-> 471
/*      */     //   #4686	-> 483
/*      */     //   #4687	-> 493
/*      */     //   #4688	-> 500
/*      */     //   #4689	-> 510
/*      */     //   #4690	-> 515
/*      */     //   #4692	-> 525
/*      */     //   #4693	-> 530
/*      */     //   #4695	-> 543
/*      */     //   #4698	-> 548
/*      */     //   #4699	-> 561
/*      */     //   #4700	-> 571
/*      */     //   #4701	-> 577
/*      */     //   #4703	-> 595
/*      */     //   #4711	-> 598
/*      */     //   #4704	-> 601
/*      */     //   #4706	-> 616
/*      */     //   #4709	-> 623
/*      */     //   #4707	-> 626
/*      */     //   #4709	-> 628
/*      */     //   #4715	-> 636
/*      */     //   #4735	-> 639
/*      */     //   #4716	-> 642
/*      */     //   #4718	-> 657
/*      */     //   #4721	-> 664
/*      */     //   #4719	-> 667
/*      */     //   #4723	-> 669
/*      */     //   #4726	-> 672
/*      */     //   #4728	-> 677
/*      */     //   #4731	-> 684
/*      */     //   #4729	-> 687
/*      */     //   #4733	-> 689
/*      */     //   #4737	-> 694
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   500	92	19	tuple	[[B
/*      */     //   628	0	22	ex	Ljava/lang/Exception;
/*      */     //   468	162	17	privilege	Ljava/lang/String;
/*      */     //   471	159	18	columnResults	Ljava/sql/ResultSet;
/*      */     //   450	183	16	st	Ljava/util/StringTokenizer;
/*      */     //   311	322	9	host	Ljava/lang/String;
/*      */     //   321	312	10	db	Ljava/lang/String;
/*      */     //   331	302	11	table	Ljava/lang/String;
/*      */     //   341	292	12	grantor	Ljava/lang/String;
/*      */     //   351	282	13	user	Ljava/lang/String;
/*      */     //   379	254	14	fullUser	Ljava/lang/StringBuffer;
/*      */     //   422	211	15	allPrivileges	Ljava/lang/String;
/*      */     //   669	0	25	ex	Ljava/lang/Exception;
/*      */     //   689	0	25	ex	Ljava/lang/Exception;
/*      */     //   0	703	0	this	Lcom/mysql/jdbc/DatabaseMetaData;
/*      */     //   0	703	1	catalog	Ljava/lang/String;
/*      */     //   0	703	2	schemaPattern	Ljava/lang/String;
/*      */     //   0	703	3	tableNamePattern	Ljava/lang/String;
/*      */     //   36	667	4	fields	[Lcom/mysql/jdbc/Field;
/*      */     //   176	527	5	grantQuery	Ljava/lang/StringBuffer;
/*      */     //   248	455	6	results	Ljava/sql/ResultSet;
/*      */     //   257	446	7	grantRows	Ljava/util/ArrayList;
/*      */     //   260	443	8	stmt	Ljava/sql/Statement;
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   260	639	642	finally
/*      */     //   471	598	601	finally
/*      */     //   601	606	601	finally
/*      */     //   616	623	626	java/lang/Exception
/*      */     //   642	647	642	finally
/*      */     //   657	664	667	java/lang/Exception
/*      */     //   677	684	687	java/lang/Exception
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
/* 4779 */     if (tableNamePattern == null) {
/* 4780 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 4781 */         tableNamePattern = "%";
/*      */       } else {
/* 4783 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 4789 */     Field[] fields = new Field[5];
/* 4790 */     fields[0] = new Field("", "TABLE_CAT", 12, 255);
/* 4791 */     fields[1] = new Field("", "TABLE_SCHEM", 12, 0);
/* 4792 */     fields[2] = new Field("", "TABLE_NAME", 12, 255);
/* 4793 */     fields[3] = new Field("", "TABLE_TYPE", 12, 5);
/* 4794 */     fields[4] = new Field("", "REMARKS", 12, 0);
/*      */     
/* 4796 */     ArrayList tuples = new ArrayList();
/*      */     
/* 4798 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/* 4800 */     String tableNamePat = tableNamePattern;
/*      */ 
/*      */     
/*      */     try {
/* 4804 */       (new IterateBlock(this, getCatalogIterator(catalog), stmt, tableNamePat, types, tuples) { private final Statement val$stmt; private final String val$tableNamePat; private final String[] val$types; private final ArrayList val$tuples; private final DatabaseMetaData this$0;
/*      */           void forEach(Object catalogStr) throws SQLException {
/* 4806 */             ResultSet results = null;
/*      */ 
/*      */             
/*      */             try {
/* 4810 */               if (!this.this$0.conn.versionMeetsMinimum(5, 0, 2)) {
/*      */                 try {
/* 4812 */                   results = this.val$stmt.executeQuery("SHOW TABLES FROM " + this.this$0.quotedId + catalogStr.toString() + this.this$0.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */ 
/*      */ 
/*      */                 
/*      */                 }
/* 4817 */                 catch (SQLException sqlEx) {
/* 4818 */                   if ("08S01".equals(sqlEx.getSQLState())) {
/* 4819 */                     throw sqlEx;
/*      */                   }
/*      */                   
/*      */                   return;
/*      */                 } 
/*      */               } else {
/*      */                 try {
/* 4826 */                   results = this.val$stmt.executeQuery("SHOW FULL TABLES FROM " + this.this$0.quotedId + catalogStr.toString() + this.this$0.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */ 
/*      */ 
/*      */                 
/*      */                 }
/* 4831 */                 catch (SQLException sqlEx) {
/* 4832 */                   if ("08S01".equals(sqlEx.getSQLState())) {
/* 4833 */                     throw sqlEx;
/*      */                   }
/*      */                   
/*      */                   return;
/*      */                 } 
/*      */               } 
/*      */               
/* 4840 */               boolean shouldReportTables = false;
/* 4841 */               boolean shouldReportViews = false;
/*      */               
/* 4843 */               if (this.val$types == null || this.val$types.length == 0) {
/* 4844 */                 shouldReportTables = true;
/* 4845 */                 shouldReportViews = true;
/*      */               } else {
/* 4847 */                 for (int i = 0; i < this.val$types.length; i++) {
/* 4848 */                   if ("TABLE".equalsIgnoreCase(this.val$types[i])) {
/* 4849 */                     shouldReportTables = true;
/*      */                   }
/*      */                   
/* 4852 */                   if ("VIEW".equalsIgnoreCase(this.val$types[i])) {
/* 4853 */                     shouldReportViews = true;
/*      */                   }
/*      */                 } 
/*      */               } 
/*      */               
/* 4858 */               int typeColumnIndex = 0;
/* 4859 */               boolean hasTableTypes = false;
/*      */               
/* 4861 */               if (this.this$0.conn.versionMeetsMinimum(5, 0, 2)) {
/*      */                 
/*      */                 try {
/*      */ 
/*      */                   
/* 4866 */                   typeColumnIndex = results.findColumn("table_type");
/*      */                   
/* 4868 */                   hasTableTypes = true;
/* 4869 */                 } catch (SQLException sqlEx) {
/*      */ 
/*      */                   
/*      */                   try {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     
/* 4880 */                     typeColumnIndex = results.findColumn("Type");
/*      */                     
/* 4882 */                     hasTableTypes = true;
/* 4883 */                   } catch (SQLException sqlEx2) {
/* 4884 */                     hasTableTypes = false;
/*      */                   } 
/*      */                 } 
/*      */               }
/*      */               
/* 4889 */               TreeMap tablesOrderedByName = null;
/* 4890 */               TreeMap viewsOrderedByName = null;
/*      */               
/* 4892 */               while (results.next()) {
/* 4893 */                 byte[][] row = new byte[5][];
/* 4894 */                 row[0] = (catalogStr.toString() == null) ? null : this.this$0.s2b(catalogStr.toString());
/*      */                 
/* 4896 */                 row[1] = null;
/* 4897 */                 row[2] = results.getBytes(1);
/* 4898 */                 row[4] = new byte[0];
/*      */                 
/* 4900 */                 if (hasTableTypes) {
/* 4901 */                   String tableType = results.getString(typeColumnIndex);
/*      */ 
/*      */                   
/* 4904 */                   if (("table".equalsIgnoreCase(tableType) || "base table".equalsIgnoreCase(tableType)) && shouldReportTables) {
/*      */ 
/*      */                     
/* 4907 */                     row[3] = DatabaseMetaData.TABLE_AS_BYTES;
/*      */                     
/* 4909 */                     if (tablesOrderedByName == null) {
/* 4910 */                       tablesOrderedByName = new TreeMap();
/*      */                     }
/*      */                     
/* 4913 */                     tablesOrderedByName.put(results.getString(1), row); continue;
/*      */                   } 
/* 4915 */                   if ("view".equalsIgnoreCase(tableType) && shouldReportViews) {
/*      */                     
/* 4917 */                     row[3] = DatabaseMetaData.VIEW_AS_BYTES;
/*      */                     
/* 4919 */                     if (viewsOrderedByName == null) {
/* 4920 */                       viewsOrderedByName = new TreeMap();
/*      */                     }
/*      */                     
/* 4923 */                     viewsOrderedByName.put(results.getString(1), row); continue;
/*      */                   } 
/* 4925 */                   if (!hasTableTypes) {
/*      */                     
/* 4927 */                     row[3] = DatabaseMetaData.TABLE_AS_BYTES;
/*      */                     
/* 4929 */                     if (tablesOrderedByName == null) {
/* 4930 */                       tablesOrderedByName = new TreeMap();
/*      */                     }
/*      */                     
/* 4933 */                     tablesOrderedByName.put(results.getString(1), row);
/*      */                   } 
/*      */                   continue;
/*      */                 } 
/* 4937 */                 if (shouldReportTables) {
/*      */                   
/* 4939 */                   row[3] = DatabaseMetaData.TABLE_AS_BYTES;
/*      */                   
/* 4941 */                   if (tablesOrderedByName == null) {
/* 4942 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */                   
/* 4945 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 } 
/*      */               } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 4954 */               if (tablesOrderedByName != null) {
/* 4955 */                 Iterator tablesIter = tablesOrderedByName.values().iterator();
/*      */ 
/*      */                 
/* 4958 */                 while (tablesIter.hasNext()) {
/* 4959 */                   this.val$tuples.add(new ByteArrayRow(tablesIter.next()));
/*      */                 }
/*      */               } 
/*      */               
/* 4963 */               if (viewsOrderedByName != null) {
/* 4964 */                 Iterator viewsIter = viewsOrderedByName.values().iterator();
/*      */ 
/*      */                 
/* 4967 */                 while (viewsIter.hasNext()) {
/* 4968 */                   this.val$tuples.add(new ByteArrayRow(viewsIter.next()));
/*      */                 }
/*      */               } 
/*      */             } finally {
/*      */               
/* 4973 */               if (results != null) {
/*      */                 try {
/* 4975 */                   results.close();
/* 4976 */                 } catch (Exception ex) {}
/*      */ 
/*      */ 
/*      */                 
/* 4980 */                 results = null;
/*      */               } 
/*      */             } 
/*      */           } }
/*      */         ).doForAll();
/*      */     } finally {
/*      */       
/* 4987 */       if (stmt != null) {
/* 4988 */         stmt.close();
/*      */       }
/*      */     } 
/*      */     
/* 4992 */     ResultSet tables = buildResultSet(fields, tuples);
/*      */     
/* 4994 */     return tables;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTableTypes() throws SQLException {
/* 5015 */     ArrayList tuples = new ArrayList();
/* 5016 */     Field[] fields = new Field[1];
/* 5017 */     fields[0] = new Field("", "TABLE_TYPE", 12, 5);
/*      */     
/* 5019 */     byte[][] tableTypeRow = new byte[1][];
/* 5020 */     tableTypeRow[0] = TABLE_AS_BYTES;
/* 5021 */     tuples.add(new ByteArrayRow(tableTypeRow));
/*      */     
/* 5023 */     if (this.conn.versionMeetsMinimum(5, 0, 1)) {
/* 5024 */       byte[][] viewTypeRow = new byte[1][];
/* 5025 */       viewTypeRow[0] = VIEW_AS_BYTES;
/* 5026 */       tuples.add(new ByteArrayRow(viewTypeRow));
/*      */     } 
/*      */     
/* 5029 */     byte[][] tempTypeRow = new byte[1][];
/* 5030 */     tempTypeRow[0] = s2b("LOCAL TEMPORARY");
/* 5031 */     tuples.add(new ByteArrayRow(tempTypeRow));
/*      */     
/* 5033 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTimeDateFunctions() throws SQLException {
/* 5044 */     return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTypeInfo() throws SQLException {
/* 5153 */     Field[] fields = new Field[18];
/* 5154 */     fields[0] = new Field("", "TYPE_NAME", 1, 32);
/* 5155 */     fields[1] = new Field("", "DATA_TYPE", 5, 5);
/* 5156 */     fields[2] = new Field("", "PRECISION", 4, 10);
/* 5157 */     fields[3] = new Field("", "LITERAL_PREFIX", 1, 4);
/* 5158 */     fields[4] = new Field("", "LITERAL_SUFFIX", 1, 4);
/* 5159 */     fields[5] = new Field("", "CREATE_PARAMS", 1, 32);
/* 5160 */     fields[6] = new Field("", "NULLABLE", 5, 5);
/* 5161 */     fields[7] = new Field("", "CASE_SENSITIVE", 1, 3);
/* 5162 */     fields[8] = new Field("", "SEARCHABLE", 5, 3);
/* 5163 */     fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", 1, 3);
/* 5164 */     fields[10] = new Field("", "FIXED_PREC_SCALE", 1, 3);
/* 5165 */     fields[11] = new Field("", "AUTO_INCREMENT", 1, 3);
/* 5166 */     fields[12] = new Field("", "LOCAL_TYPE_NAME", 1, 32);
/* 5167 */     fields[13] = new Field("", "MINIMUM_SCALE", 5, 5);
/* 5168 */     fields[14] = new Field("", "MAXIMUM_SCALE", 5, 5);
/* 5169 */     fields[15] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 5170 */     fields[16] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 5171 */     fields[17] = new Field("", "NUM_PREC_RADIX", 4, 10);
/*      */     
/* 5173 */     byte[][] rowVal = (byte[][])null;
/* 5174 */     ArrayList tuples = new ArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5183 */     rowVal = new byte[18][];
/* 5184 */     rowVal[0] = s2b("BIT");
/* 5185 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/*      */     
/* 5188 */     rowVal[2] = s2b("1");
/* 5189 */     rowVal[3] = s2b("");
/* 5190 */     rowVal[4] = s2b("");
/* 5191 */     rowVal[5] = s2b("");
/* 5192 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5196 */     rowVal[7] = s2b("true");
/* 5197 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5201 */     rowVal[9] = s2b("false");
/* 5202 */     rowVal[10] = s2b("false");
/* 5203 */     rowVal[11] = s2b("false");
/* 5204 */     rowVal[12] = s2b("BIT");
/* 5205 */     rowVal[13] = s2b("0");
/* 5206 */     rowVal[14] = s2b("0");
/* 5207 */     rowVal[15] = s2b("0");
/* 5208 */     rowVal[16] = s2b("0");
/* 5209 */     rowVal[17] = s2b("10");
/* 5210 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5215 */     rowVal = new byte[18][];
/* 5216 */     rowVal[0] = s2b("BOOL");
/* 5217 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/*      */     
/* 5220 */     rowVal[2] = s2b("1");
/* 5221 */     rowVal[3] = s2b("");
/* 5222 */     rowVal[4] = s2b("");
/* 5223 */     rowVal[5] = s2b("");
/* 5224 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5228 */     rowVal[7] = s2b("true");
/* 5229 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5233 */     rowVal[9] = s2b("false");
/* 5234 */     rowVal[10] = s2b("false");
/* 5235 */     rowVal[11] = s2b("false");
/* 5236 */     rowVal[12] = s2b("BOOL");
/* 5237 */     rowVal[13] = s2b("0");
/* 5238 */     rowVal[14] = s2b("0");
/* 5239 */     rowVal[15] = s2b("0");
/* 5240 */     rowVal[16] = s2b("0");
/* 5241 */     rowVal[17] = s2b("10");
/* 5242 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5247 */     rowVal = new byte[18][];
/* 5248 */     rowVal[0] = s2b("TINYINT");
/* 5249 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/*      */     
/* 5252 */     rowVal[2] = s2b("3");
/* 5253 */     rowVal[3] = s2b("");
/* 5254 */     rowVal[4] = s2b("");
/* 5255 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5256 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5260 */     rowVal[7] = s2b("false");
/* 5261 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5265 */     rowVal[9] = s2b("true");
/* 5266 */     rowVal[10] = s2b("false");
/* 5267 */     rowVal[11] = s2b("true");
/* 5268 */     rowVal[12] = s2b("TINYINT");
/* 5269 */     rowVal[13] = s2b("0");
/* 5270 */     rowVal[14] = s2b("0");
/* 5271 */     rowVal[15] = s2b("0");
/* 5272 */     rowVal[16] = s2b("0");
/* 5273 */     rowVal[17] = s2b("10");
/* 5274 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 5276 */     rowVal = new byte[18][];
/* 5277 */     rowVal[0] = s2b("TINYINT UNSIGNED");
/* 5278 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/*      */     
/* 5281 */     rowVal[2] = s2b("3");
/* 5282 */     rowVal[3] = s2b("");
/* 5283 */     rowVal[4] = s2b("");
/* 5284 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5285 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5289 */     rowVal[7] = s2b("false");
/* 5290 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5294 */     rowVal[9] = s2b("true");
/* 5295 */     rowVal[10] = s2b("false");
/* 5296 */     rowVal[11] = s2b("true");
/* 5297 */     rowVal[12] = s2b("TINYINT UNSIGNED");
/* 5298 */     rowVal[13] = s2b("0");
/* 5299 */     rowVal[14] = s2b("0");
/* 5300 */     rowVal[15] = s2b("0");
/* 5301 */     rowVal[16] = s2b("0");
/* 5302 */     rowVal[17] = s2b("10");
/* 5303 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5308 */     rowVal = new byte[18][];
/* 5309 */     rowVal[0] = s2b("BIGINT");
/* 5310 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/*      */     
/* 5313 */     rowVal[2] = s2b("19");
/* 5314 */     rowVal[3] = s2b("");
/* 5315 */     rowVal[4] = s2b("");
/* 5316 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5317 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5321 */     rowVal[7] = s2b("false");
/* 5322 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5326 */     rowVal[9] = s2b("true");
/* 5327 */     rowVal[10] = s2b("false");
/* 5328 */     rowVal[11] = s2b("true");
/* 5329 */     rowVal[12] = s2b("BIGINT");
/* 5330 */     rowVal[13] = s2b("0");
/* 5331 */     rowVal[14] = s2b("0");
/* 5332 */     rowVal[15] = s2b("0");
/* 5333 */     rowVal[16] = s2b("0");
/* 5334 */     rowVal[17] = s2b("10");
/* 5335 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 5337 */     rowVal = new byte[18][];
/* 5338 */     rowVal[0] = s2b("BIGINT UNSIGNED");
/* 5339 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/*      */     
/* 5342 */     rowVal[2] = s2b("20");
/* 5343 */     rowVal[3] = s2b("");
/* 5344 */     rowVal[4] = s2b("");
/* 5345 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5346 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5350 */     rowVal[7] = s2b("false");
/* 5351 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5355 */     rowVal[9] = s2b("true");
/* 5356 */     rowVal[10] = s2b("false");
/* 5357 */     rowVal[11] = s2b("true");
/* 5358 */     rowVal[12] = s2b("BIGINT UNSIGNED");
/* 5359 */     rowVal[13] = s2b("0");
/* 5360 */     rowVal[14] = s2b("0");
/* 5361 */     rowVal[15] = s2b("0");
/* 5362 */     rowVal[16] = s2b("0");
/* 5363 */     rowVal[17] = s2b("10");
/* 5364 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5369 */     rowVal = new byte[18][];
/* 5370 */     rowVal[0] = s2b("LONG VARBINARY");
/* 5371 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/*      */     
/* 5374 */     rowVal[2] = s2b("16777215");
/* 5375 */     rowVal[3] = s2b("'");
/* 5376 */     rowVal[4] = s2b("'");
/* 5377 */     rowVal[5] = s2b("");
/* 5378 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5382 */     rowVal[7] = s2b("true");
/* 5383 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5387 */     rowVal[9] = s2b("false");
/* 5388 */     rowVal[10] = s2b("false");
/* 5389 */     rowVal[11] = s2b("false");
/* 5390 */     rowVal[12] = s2b("LONG VARBINARY");
/* 5391 */     rowVal[13] = s2b("0");
/* 5392 */     rowVal[14] = s2b("0");
/* 5393 */     rowVal[15] = s2b("0");
/* 5394 */     rowVal[16] = s2b("0");
/* 5395 */     rowVal[17] = s2b("10");
/* 5396 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5401 */     rowVal = new byte[18][];
/* 5402 */     rowVal[0] = s2b("MEDIUMBLOB");
/* 5403 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/*      */     
/* 5406 */     rowVal[2] = s2b("16777215");
/* 5407 */     rowVal[3] = s2b("'");
/* 5408 */     rowVal[4] = s2b("'");
/* 5409 */     rowVal[5] = s2b("");
/* 5410 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5414 */     rowVal[7] = s2b("true");
/* 5415 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5419 */     rowVal[9] = s2b("false");
/* 5420 */     rowVal[10] = s2b("false");
/* 5421 */     rowVal[11] = s2b("false");
/* 5422 */     rowVal[12] = s2b("MEDIUMBLOB");
/* 5423 */     rowVal[13] = s2b("0");
/* 5424 */     rowVal[14] = s2b("0");
/* 5425 */     rowVal[15] = s2b("0");
/* 5426 */     rowVal[16] = s2b("0");
/* 5427 */     rowVal[17] = s2b("10");
/* 5428 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5433 */     rowVal = new byte[18][];
/* 5434 */     rowVal[0] = s2b("LONGBLOB");
/* 5435 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/*      */     
/* 5438 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/*      */     
/* 5441 */     rowVal[3] = s2b("'");
/* 5442 */     rowVal[4] = s2b("'");
/* 5443 */     rowVal[5] = s2b("");
/* 5444 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5448 */     rowVal[7] = s2b("true");
/* 5449 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5453 */     rowVal[9] = s2b("false");
/* 5454 */     rowVal[10] = s2b("false");
/* 5455 */     rowVal[11] = s2b("false");
/* 5456 */     rowVal[12] = s2b("LONGBLOB");
/* 5457 */     rowVal[13] = s2b("0");
/* 5458 */     rowVal[14] = s2b("0");
/* 5459 */     rowVal[15] = s2b("0");
/* 5460 */     rowVal[16] = s2b("0");
/* 5461 */     rowVal[17] = s2b("10");
/* 5462 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5467 */     rowVal = new byte[18][];
/* 5468 */     rowVal[0] = s2b("BLOB");
/* 5469 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/*      */     
/* 5472 */     rowVal[2] = s2b("65535");
/* 5473 */     rowVal[3] = s2b("'");
/* 5474 */     rowVal[4] = s2b("'");
/* 5475 */     rowVal[5] = s2b("");
/* 5476 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5480 */     rowVal[7] = s2b("true");
/* 5481 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5485 */     rowVal[9] = s2b("false");
/* 5486 */     rowVal[10] = s2b("false");
/* 5487 */     rowVal[11] = s2b("false");
/* 5488 */     rowVal[12] = s2b("BLOB");
/* 5489 */     rowVal[13] = s2b("0");
/* 5490 */     rowVal[14] = s2b("0");
/* 5491 */     rowVal[15] = s2b("0");
/* 5492 */     rowVal[16] = s2b("0");
/* 5493 */     rowVal[17] = s2b("10");
/* 5494 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5499 */     rowVal = new byte[18][];
/* 5500 */     rowVal[0] = s2b("TINYBLOB");
/* 5501 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/*      */     
/* 5504 */     rowVal[2] = s2b("255");
/* 5505 */     rowVal[3] = s2b("'");
/* 5506 */     rowVal[4] = s2b("'");
/* 5507 */     rowVal[5] = s2b("");
/* 5508 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5512 */     rowVal[7] = s2b("true");
/* 5513 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5517 */     rowVal[9] = s2b("false");
/* 5518 */     rowVal[10] = s2b("false");
/* 5519 */     rowVal[11] = s2b("false");
/* 5520 */     rowVal[12] = s2b("TINYBLOB");
/* 5521 */     rowVal[13] = s2b("0");
/* 5522 */     rowVal[14] = s2b("0");
/* 5523 */     rowVal[15] = s2b("0");
/* 5524 */     rowVal[16] = s2b("0");
/* 5525 */     rowVal[17] = s2b("10");
/* 5526 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5532 */     rowVal = new byte[18][];
/* 5533 */     rowVal[0] = s2b("VARBINARY");
/* 5534 */     rowVal[1] = Integer.toString(-3).getBytes();
/*      */ 
/*      */     
/* 5537 */     rowVal[2] = s2b("255");
/* 5538 */     rowVal[3] = s2b("'");
/* 5539 */     rowVal[4] = s2b("'");
/* 5540 */     rowVal[5] = s2b("(M)");
/* 5541 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5545 */     rowVal[7] = s2b("true");
/* 5546 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5550 */     rowVal[9] = s2b("false");
/* 5551 */     rowVal[10] = s2b("false");
/* 5552 */     rowVal[11] = s2b("false");
/* 5553 */     rowVal[12] = s2b("VARBINARY");
/* 5554 */     rowVal[13] = s2b("0");
/* 5555 */     rowVal[14] = s2b("0");
/* 5556 */     rowVal[15] = s2b("0");
/* 5557 */     rowVal[16] = s2b("0");
/* 5558 */     rowVal[17] = s2b("10");
/* 5559 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5565 */     rowVal = new byte[18][];
/* 5566 */     rowVal[0] = s2b("BINARY");
/* 5567 */     rowVal[1] = Integer.toString(-2).getBytes();
/*      */ 
/*      */     
/* 5570 */     rowVal[2] = s2b("255");
/* 5571 */     rowVal[3] = s2b("'");
/* 5572 */     rowVal[4] = s2b("'");
/* 5573 */     rowVal[5] = s2b("(M)");
/* 5574 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5578 */     rowVal[7] = s2b("true");
/* 5579 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5583 */     rowVal[9] = s2b("false");
/* 5584 */     rowVal[10] = s2b("false");
/* 5585 */     rowVal[11] = s2b("false");
/* 5586 */     rowVal[12] = s2b("BINARY");
/* 5587 */     rowVal[13] = s2b("0");
/* 5588 */     rowVal[14] = s2b("0");
/* 5589 */     rowVal[15] = s2b("0");
/* 5590 */     rowVal[16] = s2b("0");
/* 5591 */     rowVal[17] = s2b("10");
/* 5592 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5597 */     rowVal = new byte[18][];
/* 5598 */     rowVal[0] = s2b("LONG VARCHAR");
/* 5599 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/*      */     
/* 5602 */     rowVal[2] = s2b("16777215");
/* 5603 */     rowVal[3] = s2b("'");
/* 5604 */     rowVal[4] = s2b("'");
/* 5605 */     rowVal[5] = s2b("");
/* 5606 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5610 */     rowVal[7] = s2b("false");
/* 5611 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5615 */     rowVal[9] = s2b("false");
/* 5616 */     rowVal[10] = s2b("false");
/* 5617 */     rowVal[11] = s2b("false");
/* 5618 */     rowVal[12] = s2b("LONG VARCHAR");
/* 5619 */     rowVal[13] = s2b("0");
/* 5620 */     rowVal[14] = s2b("0");
/* 5621 */     rowVal[15] = s2b("0");
/* 5622 */     rowVal[16] = s2b("0");
/* 5623 */     rowVal[17] = s2b("10");
/* 5624 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5629 */     rowVal = new byte[18][];
/* 5630 */     rowVal[0] = s2b("MEDIUMTEXT");
/* 5631 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/*      */     
/* 5634 */     rowVal[2] = s2b("16777215");
/* 5635 */     rowVal[3] = s2b("'");
/* 5636 */     rowVal[4] = s2b("'");
/* 5637 */     rowVal[5] = s2b("");
/* 5638 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5642 */     rowVal[7] = s2b("false");
/* 5643 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5647 */     rowVal[9] = s2b("false");
/* 5648 */     rowVal[10] = s2b("false");
/* 5649 */     rowVal[11] = s2b("false");
/* 5650 */     rowVal[12] = s2b("MEDIUMTEXT");
/* 5651 */     rowVal[13] = s2b("0");
/* 5652 */     rowVal[14] = s2b("0");
/* 5653 */     rowVal[15] = s2b("0");
/* 5654 */     rowVal[16] = s2b("0");
/* 5655 */     rowVal[17] = s2b("10");
/* 5656 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5661 */     rowVal = new byte[18][];
/* 5662 */     rowVal[0] = s2b("LONGTEXT");
/* 5663 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/*      */     
/* 5666 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/*      */     
/* 5669 */     rowVal[3] = s2b("'");
/* 5670 */     rowVal[4] = s2b("'");
/* 5671 */     rowVal[5] = s2b("");
/* 5672 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5676 */     rowVal[7] = s2b("false");
/* 5677 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5681 */     rowVal[9] = s2b("false");
/* 5682 */     rowVal[10] = s2b("false");
/* 5683 */     rowVal[11] = s2b("false");
/* 5684 */     rowVal[12] = s2b("LONGTEXT");
/* 5685 */     rowVal[13] = s2b("0");
/* 5686 */     rowVal[14] = s2b("0");
/* 5687 */     rowVal[15] = s2b("0");
/* 5688 */     rowVal[16] = s2b("0");
/* 5689 */     rowVal[17] = s2b("10");
/* 5690 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5695 */     rowVal = new byte[18][];
/* 5696 */     rowVal[0] = s2b("TEXT");
/* 5697 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/*      */     
/* 5700 */     rowVal[2] = s2b("65535");
/* 5701 */     rowVal[3] = s2b("'");
/* 5702 */     rowVal[4] = s2b("'");
/* 5703 */     rowVal[5] = s2b("");
/* 5704 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5708 */     rowVal[7] = s2b("false");
/* 5709 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5713 */     rowVal[9] = s2b("false");
/* 5714 */     rowVal[10] = s2b("false");
/* 5715 */     rowVal[11] = s2b("false");
/* 5716 */     rowVal[12] = s2b("TEXT");
/* 5717 */     rowVal[13] = s2b("0");
/* 5718 */     rowVal[14] = s2b("0");
/* 5719 */     rowVal[15] = s2b("0");
/* 5720 */     rowVal[16] = s2b("0");
/* 5721 */     rowVal[17] = s2b("10");
/* 5722 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5727 */     rowVal = new byte[18][];
/* 5728 */     rowVal[0] = s2b("TINYTEXT");
/* 5729 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/*      */     
/* 5732 */     rowVal[2] = s2b("255");
/* 5733 */     rowVal[3] = s2b("'");
/* 5734 */     rowVal[4] = s2b("'");
/* 5735 */     rowVal[5] = s2b("");
/* 5736 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5740 */     rowVal[7] = s2b("false");
/* 5741 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5745 */     rowVal[9] = s2b("false");
/* 5746 */     rowVal[10] = s2b("false");
/* 5747 */     rowVal[11] = s2b("false");
/* 5748 */     rowVal[12] = s2b("TINYTEXT");
/* 5749 */     rowVal[13] = s2b("0");
/* 5750 */     rowVal[14] = s2b("0");
/* 5751 */     rowVal[15] = s2b("0");
/* 5752 */     rowVal[16] = s2b("0");
/* 5753 */     rowVal[17] = s2b("10");
/* 5754 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5759 */     rowVal = new byte[18][];
/* 5760 */     rowVal[0] = s2b("CHAR");
/* 5761 */     rowVal[1] = Integer.toString(1).getBytes();
/*      */ 
/*      */     
/* 5764 */     rowVal[2] = s2b("255");
/* 5765 */     rowVal[3] = s2b("'");
/* 5766 */     rowVal[4] = s2b("'");
/* 5767 */     rowVal[5] = s2b("(M)");
/* 5768 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5772 */     rowVal[7] = s2b("false");
/* 5773 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5777 */     rowVal[9] = s2b("false");
/* 5778 */     rowVal[10] = s2b("false");
/* 5779 */     rowVal[11] = s2b("false");
/* 5780 */     rowVal[12] = s2b("CHAR");
/* 5781 */     rowVal[13] = s2b("0");
/* 5782 */     rowVal[14] = s2b("0");
/* 5783 */     rowVal[15] = s2b("0");
/* 5784 */     rowVal[16] = s2b("0");
/* 5785 */     rowVal[17] = s2b("10");
/* 5786 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */     
/* 5790 */     int decimalPrecision = 254;
/*      */     
/* 5792 */     if (this.conn.versionMeetsMinimum(5, 0, 3)) {
/* 5793 */       if (this.conn.versionMeetsMinimum(5, 0, 6)) {
/* 5794 */         decimalPrecision = 65;
/*      */       } else {
/* 5796 */         decimalPrecision = 64;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5804 */     rowVal = new byte[18][];
/* 5805 */     rowVal[0] = s2b("NUMERIC");
/* 5806 */     rowVal[1] = Integer.toString(2).getBytes();
/*      */ 
/*      */     
/* 5809 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 5810 */     rowVal[3] = s2b("");
/* 5811 */     rowVal[4] = s2b("");
/* 5812 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5813 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5817 */     rowVal[7] = s2b("false");
/* 5818 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5822 */     rowVal[9] = s2b("false");
/* 5823 */     rowVal[10] = s2b("false");
/* 5824 */     rowVal[11] = s2b("true");
/* 5825 */     rowVal[12] = s2b("NUMERIC");
/* 5826 */     rowVal[13] = s2b("-308");
/* 5827 */     rowVal[14] = s2b("308");
/* 5828 */     rowVal[15] = s2b("0");
/* 5829 */     rowVal[16] = s2b("0");
/* 5830 */     rowVal[17] = s2b("10");
/* 5831 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5836 */     rowVal = new byte[18][];
/* 5837 */     rowVal[0] = s2b("DECIMAL");
/* 5838 */     rowVal[1] = Integer.toString(3).getBytes();
/*      */ 
/*      */     
/* 5841 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 5842 */     rowVal[3] = s2b("");
/* 5843 */     rowVal[4] = s2b("");
/* 5844 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5845 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5849 */     rowVal[7] = s2b("false");
/* 5850 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5854 */     rowVal[9] = s2b("false");
/* 5855 */     rowVal[10] = s2b("false");
/* 5856 */     rowVal[11] = s2b("true");
/* 5857 */     rowVal[12] = s2b("DECIMAL");
/* 5858 */     rowVal[13] = s2b("-308");
/* 5859 */     rowVal[14] = s2b("308");
/* 5860 */     rowVal[15] = s2b("0");
/* 5861 */     rowVal[16] = s2b("0");
/* 5862 */     rowVal[17] = s2b("10");
/* 5863 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5868 */     rowVal = new byte[18][];
/* 5869 */     rowVal[0] = s2b("INTEGER");
/* 5870 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/*      */     
/* 5873 */     rowVal[2] = s2b("10");
/* 5874 */     rowVal[3] = s2b("");
/* 5875 */     rowVal[4] = s2b("");
/* 5876 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5877 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5881 */     rowVal[7] = s2b("false");
/* 5882 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5886 */     rowVal[9] = s2b("true");
/* 5887 */     rowVal[10] = s2b("false");
/* 5888 */     rowVal[11] = s2b("true");
/* 5889 */     rowVal[12] = s2b("INTEGER");
/* 5890 */     rowVal[13] = s2b("0");
/* 5891 */     rowVal[14] = s2b("0");
/* 5892 */     rowVal[15] = s2b("0");
/* 5893 */     rowVal[16] = s2b("0");
/* 5894 */     rowVal[17] = s2b("10");
/* 5895 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 5897 */     rowVal = new byte[18][];
/* 5898 */     rowVal[0] = s2b("INTEGER UNSIGNED");
/* 5899 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/*      */     
/* 5902 */     rowVal[2] = s2b("10");
/* 5903 */     rowVal[3] = s2b("");
/* 5904 */     rowVal[4] = s2b("");
/* 5905 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5906 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5910 */     rowVal[7] = s2b("false");
/* 5911 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5915 */     rowVal[9] = s2b("true");
/* 5916 */     rowVal[10] = s2b("false");
/* 5917 */     rowVal[11] = s2b("true");
/* 5918 */     rowVal[12] = s2b("INTEGER UNSIGNED");
/* 5919 */     rowVal[13] = s2b("0");
/* 5920 */     rowVal[14] = s2b("0");
/* 5921 */     rowVal[15] = s2b("0");
/* 5922 */     rowVal[16] = s2b("0");
/* 5923 */     rowVal[17] = s2b("10");
/* 5924 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5929 */     rowVal = new byte[18][];
/* 5930 */     rowVal[0] = s2b("INT");
/* 5931 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/*      */     
/* 5934 */     rowVal[2] = s2b("10");
/* 5935 */     rowVal[3] = s2b("");
/* 5936 */     rowVal[4] = s2b("");
/* 5937 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5938 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5942 */     rowVal[7] = s2b("false");
/* 5943 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5947 */     rowVal[9] = s2b("true");
/* 5948 */     rowVal[10] = s2b("false");
/* 5949 */     rowVal[11] = s2b("true");
/* 5950 */     rowVal[12] = s2b("INT");
/* 5951 */     rowVal[13] = s2b("0");
/* 5952 */     rowVal[14] = s2b("0");
/* 5953 */     rowVal[15] = s2b("0");
/* 5954 */     rowVal[16] = s2b("0");
/* 5955 */     rowVal[17] = s2b("10");
/* 5956 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 5958 */     rowVal = new byte[18][];
/* 5959 */     rowVal[0] = s2b("INT UNSIGNED");
/* 5960 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/*      */     
/* 5963 */     rowVal[2] = s2b("10");
/* 5964 */     rowVal[3] = s2b("");
/* 5965 */     rowVal[4] = s2b("");
/* 5966 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5967 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5971 */     rowVal[7] = s2b("false");
/* 5972 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 5976 */     rowVal[9] = s2b("true");
/* 5977 */     rowVal[10] = s2b("false");
/* 5978 */     rowVal[11] = s2b("true");
/* 5979 */     rowVal[12] = s2b("INT UNSIGNED");
/* 5980 */     rowVal[13] = s2b("0");
/* 5981 */     rowVal[14] = s2b("0");
/* 5982 */     rowVal[15] = s2b("0");
/* 5983 */     rowVal[16] = s2b("0");
/* 5984 */     rowVal[17] = s2b("10");
/* 5985 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5990 */     rowVal = new byte[18][];
/* 5991 */     rowVal[0] = s2b("MEDIUMINT");
/* 5992 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/*      */     
/* 5995 */     rowVal[2] = s2b("7");
/* 5996 */     rowVal[3] = s2b("");
/* 5997 */     rowVal[4] = s2b("");
/* 5998 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5999 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6003 */     rowVal[7] = s2b("false");
/* 6004 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6008 */     rowVal[9] = s2b("true");
/* 6009 */     rowVal[10] = s2b("false");
/* 6010 */     rowVal[11] = s2b("true");
/* 6011 */     rowVal[12] = s2b("MEDIUMINT");
/* 6012 */     rowVal[13] = s2b("0");
/* 6013 */     rowVal[14] = s2b("0");
/* 6014 */     rowVal[15] = s2b("0");
/* 6015 */     rowVal[16] = s2b("0");
/* 6016 */     rowVal[17] = s2b("10");
/* 6017 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 6019 */     rowVal = new byte[18][];
/* 6020 */     rowVal[0] = s2b("MEDIUMINT UNSIGNED");
/* 6021 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/*      */     
/* 6024 */     rowVal[2] = s2b("8");
/* 6025 */     rowVal[3] = s2b("");
/* 6026 */     rowVal[4] = s2b("");
/* 6027 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6028 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6032 */     rowVal[7] = s2b("false");
/* 6033 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6037 */     rowVal[9] = s2b("true");
/* 6038 */     rowVal[10] = s2b("false");
/* 6039 */     rowVal[11] = s2b("true");
/* 6040 */     rowVal[12] = s2b("MEDIUMINT UNSIGNED");
/* 6041 */     rowVal[13] = s2b("0");
/* 6042 */     rowVal[14] = s2b("0");
/* 6043 */     rowVal[15] = s2b("0");
/* 6044 */     rowVal[16] = s2b("0");
/* 6045 */     rowVal[17] = s2b("10");
/* 6046 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6051 */     rowVal = new byte[18][];
/* 6052 */     rowVal[0] = s2b("SMALLINT");
/* 6053 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/*      */     
/* 6056 */     rowVal[2] = s2b("5");
/* 6057 */     rowVal[3] = s2b("");
/* 6058 */     rowVal[4] = s2b("");
/* 6059 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6060 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6064 */     rowVal[7] = s2b("false");
/* 6065 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6069 */     rowVal[9] = s2b("true");
/* 6070 */     rowVal[10] = s2b("false");
/* 6071 */     rowVal[11] = s2b("true");
/* 6072 */     rowVal[12] = s2b("SMALLINT");
/* 6073 */     rowVal[13] = s2b("0");
/* 6074 */     rowVal[14] = s2b("0");
/* 6075 */     rowVal[15] = s2b("0");
/* 6076 */     rowVal[16] = s2b("0");
/* 6077 */     rowVal[17] = s2b("10");
/* 6078 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 6080 */     rowVal = new byte[18][];
/* 6081 */     rowVal[0] = s2b("SMALLINT UNSIGNED");
/* 6082 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/*      */     
/* 6085 */     rowVal[2] = s2b("5");
/* 6086 */     rowVal[3] = s2b("");
/* 6087 */     rowVal[4] = s2b("");
/* 6088 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6089 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6093 */     rowVal[7] = s2b("false");
/* 6094 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6098 */     rowVal[9] = s2b("true");
/* 6099 */     rowVal[10] = s2b("false");
/* 6100 */     rowVal[11] = s2b("true");
/* 6101 */     rowVal[12] = s2b("SMALLINT UNSIGNED");
/* 6102 */     rowVal[13] = s2b("0");
/* 6103 */     rowVal[14] = s2b("0");
/* 6104 */     rowVal[15] = s2b("0");
/* 6105 */     rowVal[16] = s2b("0");
/* 6106 */     rowVal[17] = s2b("10");
/* 6107 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6113 */     rowVal = new byte[18][];
/* 6114 */     rowVal[0] = s2b("FLOAT");
/* 6115 */     rowVal[1] = Integer.toString(7).getBytes();
/*      */ 
/*      */     
/* 6118 */     rowVal[2] = s2b("10");
/* 6119 */     rowVal[3] = s2b("");
/* 6120 */     rowVal[4] = s2b("");
/* 6121 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6122 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6126 */     rowVal[7] = s2b("false");
/* 6127 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6131 */     rowVal[9] = s2b("false");
/* 6132 */     rowVal[10] = s2b("false");
/* 6133 */     rowVal[11] = s2b("true");
/* 6134 */     rowVal[12] = s2b("FLOAT");
/* 6135 */     rowVal[13] = s2b("-38");
/* 6136 */     rowVal[14] = s2b("38");
/* 6137 */     rowVal[15] = s2b("0");
/* 6138 */     rowVal[16] = s2b("0");
/* 6139 */     rowVal[17] = s2b("10");
/* 6140 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6145 */     rowVal = new byte[18][];
/* 6146 */     rowVal[0] = s2b("DOUBLE");
/* 6147 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/*      */     
/* 6150 */     rowVal[2] = s2b("17");
/* 6151 */     rowVal[3] = s2b("");
/* 6152 */     rowVal[4] = s2b("");
/* 6153 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6154 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6158 */     rowVal[7] = s2b("false");
/* 6159 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6163 */     rowVal[9] = s2b("false");
/* 6164 */     rowVal[10] = s2b("false");
/* 6165 */     rowVal[11] = s2b("true");
/* 6166 */     rowVal[12] = s2b("DOUBLE");
/* 6167 */     rowVal[13] = s2b("-308");
/* 6168 */     rowVal[14] = s2b("308");
/* 6169 */     rowVal[15] = s2b("0");
/* 6170 */     rowVal[16] = s2b("0");
/* 6171 */     rowVal[17] = s2b("10");
/* 6172 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6177 */     rowVal = new byte[18][];
/* 6178 */     rowVal[0] = s2b("DOUBLE PRECISION");
/* 6179 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/*      */     
/* 6182 */     rowVal[2] = s2b("17");
/* 6183 */     rowVal[3] = s2b("");
/* 6184 */     rowVal[4] = s2b("");
/* 6185 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6186 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6190 */     rowVal[7] = s2b("false");
/* 6191 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6195 */     rowVal[9] = s2b("false");
/* 6196 */     rowVal[10] = s2b("false");
/* 6197 */     rowVal[11] = s2b("true");
/* 6198 */     rowVal[12] = s2b("DOUBLE PRECISION");
/* 6199 */     rowVal[13] = s2b("-308");
/* 6200 */     rowVal[14] = s2b("308");
/* 6201 */     rowVal[15] = s2b("0");
/* 6202 */     rowVal[16] = s2b("0");
/* 6203 */     rowVal[17] = s2b("10");
/* 6204 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6209 */     rowVal = new byte[18][];
/* 6210 */     rowVal[0] = s2b("REAL");
/* 6211 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/*      */     
/* 6214 */     rowVal[2] = s2b("17");
/* 6215 */     rowVal[3] = s2b("");
/* 6216 */     rowVal[4] = s2b("");
/* 6217 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6218 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6222 */     rowVal[7] = s2b("false");
/* 6223 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6227 */     rowVal[9] = s2b("false");
/* 6228 */     rowVal[10] = s2b("false");
/* 6229 */     rowVal[11] = s2b("true");
/* 6230 */     rowVal[12] = s2b("REAL");
/* 6231 */     rowVal[13] = s2b("-308");
/* 6232 */     rowVal[14] = s2b("308");
/* 6233 */     rowVal[15] = s2b("0");
/* 6234 */     rowVal[16] = s2b("0");
/* 6235 */     rowVal[17] = s2b("10");
/* 6236 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6241 */     rowVal = new byte[18][];
/* 6242 */     rowVal[0] = s2b("VARCHAR");
/* 6243 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/*      */     
/* 6246 */     rowVal[2] = s2b("255");
/* 6247 */     rowVal[3] = s2b("'");
/* 6248 */     rowVal[4] = s2b("'");
/* 6249 */     rowVal[5] = s2b("(M)");
/* 6250 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6254 */     rowVal[7] = s2b("false");
/* 6255 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6259 */     rowVal[9] = s2b("false");
/* 6260 */     rowVal[10] = s2b("false");
/* 6261 */     rowVal[11] = s2b("false");
/* 6262 */     rowVal[12] = s2b("VARCHAR");
/* 6263 */     rowVal[13] = s2b("0");
/* 6264 */     rowVal[14] = s2b("0");
/* 6265 */     rowVal[15] = s2b("0");
/* 6266 */     rowVal[16] = s2b("0");
/* 6267 */     rowVal[17] = s2b("10");
/* 6268 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6273 */     rowVal = new byte[18][];
/* 6274 */     rowVal[0] = s2b("ENUM");
/* 6275 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/*      */     
/* 6278 */     rowVal[2] = s2b("65535");
/* 6279 */     rowVal[3] = s2b("'");
/* 6280 */     rowVal[4] = s2b("'");
/* 6281 */     rowVal[5] = s2b("");
/* 6282 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6286 */     rowVal[7] = s2b("false");
/* 6287 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6291 */     rowVal[9] = s2b("false");
/* 6292 */     rowVal[10] = s2b("false");
/* 6293 */     rowVal[11] = s2b("false");
/* 6294 */     rowVal[12] = s2b("ENUM");
/* 6295 */     rowVal[13] = s2b("0");
/* 6296 */     rowVal[14] = s2b("0");
/* 6297 */     rowVal[15] = s2b("0");
/* 6298 */     rowVal[16] = s2b("0");
/* 6299 */     rowVal[17] = s2b("10");
/* 6300 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6305 */     rowVal = new byte[18][];
/* 6306 */     rowVal[0] = s2b("SET");
/* 6307 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/*      */     
/* 6310 */     rowVal[2] = s2b("64");
/* 6311 */     rowVal[3] = s2b("'");
/* 6312 */     rowVal[4] = s2b("'");
/* 6313 */     rowVal[5] = s2b("");
/* 6314 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6318 */     rowVal[7] = s2b("false");
/* 6319 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6323 */     rowVal[9] = s2b("false");
/* 6324 */     rowVal[10] = s2b("false");
/* 6325 */     rowVal[11] = s2b("false");
/* 6326 */     rowVal[12] = s2b("SET");
/* 6327 */     rowVal[13] = s2b("0");
/* 6328 */     rowVal[14] = s2b("0");
/* 6329 */     rowVal[15] = s2b("0");
/* 6330 */     rowVal[16] = s2b("0");
/* 6331 */     rowVal[17] = s2b("10");
/* 6332 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6337 */     rowVal = new byte[18][];
/* 6338 */     rowVal[0] = s2b("DATE");
/* 6339 */     rowVal[1] = Integer.toString(91).getBytes();
/*      */ 
/*      */     
/* 6342 */     rowVal[2] = s2b("0");
/* 6343 */     rowVal[3] = s2b("'");
/* 6344 */     rowVal[4] = s2b("'");
/* 6345 */     rowVal[5] = s2b("");
/* 6346 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6350 */     rowVal[7] = s2b("false");
/* 6351 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6355 */     rowVal[9] = s2b("false");
/* 6356 */     rowVal[10] = s2b("false");
/* 6357 */     rowVal[11] = s2b("false");
/* 6358 */     rowVal[12] = s2b("DATE");
/* 6359 */     rowVal[13] = s2b("0");
/* 6360 */     rowVal[14] = s2b("0");
/* 6361 */     rowVal[15] = s2b("0");
/* 6362 */     rowVal[16] = s2b("0");
/* 6363 */     rowVal[17] = s2b("10");
/* 6364 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6369 */     rowVal = new byte[18][];
/* 6370 */     rowVal[0] = s2b("TIME");
/* 6371 */     rowVal[1] = Integer.toString(92).getBytes();
/*      */ 
/*      */     
/* 6374 */     rowVal[2] = s2b("0");
/* 6375 */     rowVal[3] = s2b("'");
/* 6376 */     rowVal[4] = s2b("'");
/* 6377 */     rowVal[5] = s2b("");
/* 6378 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6382 */     rowVal[7] = s2b("false");
/* 6383 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6387 */     rowVal[9] = s2b("false");
/* 6388 */     rowVal[10] = s2b("false");
/* 6389 */     rowVal[11] = s2b("false");
/* 6390 */     rowVal[12] = s2b("TIME");
/* 6391 */     rowVal[13] = s2b("0");
/* 6392 */     rowVal[14] = s2b("0");
/* 6393 */     rowVal[15] = s2b("0");
/* 6394 */     rowVal[16] = s2b("0");
/* 6395 */     rowVal[17] = s2b("10");
/* 6396 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6401 */     rowVal = new byte[18][];
/* 6402 */     rowVal[0] = s2b("DATETIME");
/* 6403 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/*      */     
/* 6406 */     rowVal[2] = s2b("0");
/* 6407 */     rowVal[3] = s2b("'");
/* 6408 */     rowVal[4] = s2b("'");
/* 6409 */     rowVal[5] = s2b("");
/* 6410 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6414 */     rowVal[7] = s2b("false");
/* 6415 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6419 */     rowVal[9] = s2b("false");
/* 6420 */     rowVal[10] = s2b("false");
/* 6421 */     rowVal[11] = s2b("false");
/* 6422 */     rowVal[12] = s2b("DATETIME");
/* 6423 */     rowVal[13] = s2b("0");
/* 6424 */     rowVal[14] = s2b("0");
/* 6425 */     rowVal[15] = s2b("0");
/* 6426 */     rowVal[16] = s2b("0");
/* 6427 */     rowVal[17] = s2b("10");
/* 6428 */     tuples.add(new ByteArrayRow(rowVal));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6433 */     rowVal = new byte[18][];
/* 6434 */     rowVal[0] = s2b("TIMESTAMP");
/* 6435 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/*      */     
/* 6438 */     rowVal[2] = s2b("0");
/* 6439 */     rowVal[3] = s2b("'");
/* 6440 */     rowVal[4] = s2b("'");
/* 6441 */     rowVal[5] = s2b("[(M)]");
/* 6442 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6446 */     rowVal[7] = s2b("false");
/* 6447 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/*      */ 
/*      */     
/* 6451 */     rowVal[9] = s2b("false");
/* 6452 */     rowVal[10] = s2b("false");
/* 6453 */     rowVal[11] = s2b("false");
/* 6454 */     rowVal[12] = s2b("TIMESTAMP");
/* 6455 */     rowVal[13] = s2b("0");
/* 6456 */     rowVal[14] = s2b("0");
/* 6457 */     rowVal[15] = s2b("0");
/* 6458 */     rowVal[16] = s2b("0");
/* 6459 */     rowVal[17] = s2b("10");
/* 6460 */     tuples.add(new ByteArrayRow(rowVal));
/*      */     
/* 6462 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
/* 6508 */     Field[] fields = new Field[6];
/* 6509 */     fields[0] = new Field("", "TYPE_CAT", 12, 32);
/* 6510 */     fields[1] = new Field("", "TYPE_SCHEM", 12, 32);
/* 6511 */     fields[2] = new Field("", "TYPE_NAME", 12, 32);
/* 6512 */     fields[3] = new Field("", "CLASS_NAME", 12, 32);
/* 6513 */     fields[4] = new Field("", "DATA_TYPE", 12, 32);
/* 6514 */     fields[5] = new Field("", "REMARKS", 12, 32);
/*      */     
/* 6516 */     ArrayList tuples = new ArrayList();
/*      */     
/* 6518 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getURL() throws SQLException {
/* 6529 */     return this.conn.getURL();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUserName() throws SQLException {
/* 6540 */     if (this.conn.getUseHostsInPrivileges()) {
/* 6541 */       Statement stmt = null;
/* 6542 */       ResultSet rs = null;
/*      */       
/*      */       try {
/* 6545 */         stmt = this.conn.createStatement();
/* 6546 */         stmt.setEscapeProcessing(false);
/*      */         
/* 6548 */         rs = stmt.executeQuery("SELECT USER()");
/* 6549 */         rs.next();
/*      */         
/* 6551 */         return rs.getString(1);
/*      */       } finally {
/* 6553 */         if (rs != null) {
/*      */           try {
/* 6555 */             rs.close();
/* 6556 */           } catch (Exception ex) {
/* 6557 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           } 
/*      */           
/* 6560 */           rs = null;
/*      */         } 
/*      */         
/* 6563 */         if (stmt != null) {
/*      */           try {
/* 6565 */             stmt.close();
/* 6566 */           } catch (Exception ex) {
/* 6567 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           } 
/*      */           
/* 6570 */           stmt = null;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 6575 */     return this.conn.getUser();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
/* 6614 */     Field[] fields = new Field[8];
/* 6615 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 6616 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 6617 */     fields[2] = new Field("", "DATA_TYPE", 5, 5);
/* 6618 */     fields[3] = new Field("", "TYPE_NAME", 1, 16);
/* 6619 */     fields[4] = new Field("", "COLUMN_SIZE", 1, 16);
/* 6620 */     fields[5] = new Field("", "BUFFER_LENGTH", 1, 16);
/* 6621 */     fields[6] = new Field("", "DECIMAL_DIGITS", 1, 16);
/* 6622 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */     
/* 6624 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean insertsAreDetected(int type) throws SQLException {
/* 6640 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCatalogAtStart() throws SQLException {
/* 6652 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() throws SQLException {
/* 6663 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean locatorsUpdateCopy() throws SQLException {
/* 6670 */     return !this.conn.getEmulateLocators();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullPlusNonNullIsNull() throws SQLException {
/* 6682 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedAtEnd() throws SQLException {
/* 6693 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedAtStart() throws SQLException {
/* 6704 */     return (this.conn.versionMeetsMinimum(4, 0, 2) && !this.conn.versionMeetsMinimum(4, 0, 11));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedHigh() throws SQLException {
/* 6716 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedLow() throws SQLException {
/* 6727 */     return !nullsAreSortedHigh();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean othersDeletesAreVisible(int type) throws SQLException {
/* 6740 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean othersInsertsAreVisible(int type) throws SQLException {
/* 6753 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean othersUpdatesAreVisible(int type) throws SQLException {
/* 6766 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ownDeletesAreVisible(int type) throws SQLException {
/* 6779 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ownInsertsAreVisible(int type) throws SQLException {
/* 6792 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ownUpdatesAreVisible(int type) throws SQLException {
/* 6805 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment) throws SQLException {
/* 6826 */     String columnsDelimitter = ",";
/*      */     
/* 6828 */     char quoteChar = (this.quotedId.length() == 0) ? Character.MIN_VALUE : this.quotedId.charAt(0);
/*      */ 
/*      */     
/* 6831 */     int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysComment, "(", quoteChar, true);
/*      */ 
/*      */ 
/*      */     
/* 6835 */     if (indexOfOpenParenLocalColumns == -1) {
/* 6836 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of local columns list.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 6841 */     String constraintName = removeQuotedId(keysComment.substring(0, indexOfOpenParenLocalColumns).trim());
/*      */     
/* 6843 */     keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
/*      */ 
/*      */     
/* 6846 */     String keysCommentTrimmed = keysComment.trim();
/*      */     
/* 6848 */     int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/*      */ 
/*      */     
/* 6852 */     if (indexOfCloseParenLocalColumns == -1) {
/* 6853 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of local columns list.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 6858 */     String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
/*      */ 
/*      */     
/* 6861 */     int indexOfRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, "REFER ", this.quotedId.charAt(0), true);
/*      */ 
/*      */     
/* 6864 */     if (indexOfRefer == -1) {
/* 6865 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced tables list.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 6870 */     int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfRefer, keysCommentTrimmed, "(", quoteChar, false);
/*      */ 
/*      */ 
/*      */     
/* 6874 */     if (indexOfOpenParenReferCol == -1) {
/* 6875 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced columns list.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 6880 */     String referCatalogTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
/*      */ 
/*      */     
/* 6883 */     int indexOfSlash = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referCatalogTableString, "/", this.quotedId.charAt(0), false);
/*      */ 
/*      */     
/* 6886 */     if (indexOfSlash == -1) {
/* 6887 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find name of referenced catalog.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 6892 */     String referCatalog = removeQuotedId(referCatalogTableString.substring(0, indexOfSlash));
/*      */     
/* 6894 */     String referTable = removeQuotedId(referCatalogTableString.substring(indexOfSlash + 1).trim());
/*      */ 
/*      */     
/* 6897 */     int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfOpenParenReferCol, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/*      */ 
/*      */     
/* 6901 */     if (indexOfCloseParenRefer == -1) {
/* 6902 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of referenced columns list.", "S1000");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 6907 */     String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
/*      */ 
/*      */     
/* 6910 */     List referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */     
/* 6912 */     List localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/*      */     
/* 6915 */     return new LocalAndReferencedColumns(this, localColumnsList, referColumnsList, constraintName, referCatalog, referTable);
/*      */   }
/*      */ 
/*      */   
/*      */   private String removeQuotedId(String s) {
/* 6920 */     if (s == null) {
/* 6921 */       return null;
/*      */     }
/*      */     
/* 6924 */     if (this.quotedId.equals("")) {
/* 6925 */       return s;
/*      */     }
/*      */     
/* 6928 */     s = s.trim();
/*      */     
/* 6930 */     int frontOffset = 0;
/* 6931 */     int backOffset = s.length();
/* 6932 */     int quoteLength = this.quotedId.length();
/*      */     
/* 6934 */     if (s.startsWith(this.quotedId)) {
/* 6935 */       frontOffset = quoteLength;
/*      */     }
/*      */     
/* 6938 */     if (s.endsWith(this.quotedId)) {
/* 6939 */       backOffset -= quoteLength;
/*      */     }
/*      */     
/* 6942 */     return s.substring(frontOffset, backOffset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] s2b(String s) throws SQLException {
/* 6954 */     if (s == null) {
/* 6955 */       return null;
/*      */     }
/*      */     
/* 6958 */     return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata(), this.conn.getServerCharacterEncoding(), this.conn.parserKnowsUnicode(), this.conn);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesLowerCaseIdentifiers() throws SQLException {
/* 6972 */     return this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
/* 6984 */     return this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesMixedCaseIdentifiers() throws SQLException {
/* 6996 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
/* 7008 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesUpperCaseIdentifiers() throws SQLException {
/* 7020 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
/* 7032 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsAlterTableWithAddColumn() throws SQLException {
/* 7043 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsAlterTableWithDropColumn() throws SQLException {
/* 7054 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsANSI92EntryLevelSQL() throws SQLException {
/* 7066 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsANSI92FullSQL() throws SQLException {
/* 7077 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsANSI92IntermediateSQL() throws SQLException {
/* 7088 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsBatchUpdates() throws SQLException {
/* 7100 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInDataManipulation() throws SQLException {
/* 7112 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
/* 7124 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
/* 7136 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInProcedureCalls() throws SQLException {
/* 7148 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInTableDefinitions() throws SQLException {
/* 7160 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsColumnAliasing() throws SQLException {
/* 7176 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsConvert() throws SQLException {
/* 7187 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsConvert(int fromType, int toType) throws SQLException {
/* 7204 */     switch (fromType) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7215 */         switch (toType) {
/*      */           case -6:
/*      */           case -5:
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*      */           case 12:
/*      */           case 91:
/*      */           case 92:
/*      */           case 93:
/*      */           case 1111:
/* 7235 */             return true;
/*      */         } 
/*      */         
/* 7238 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -7:
/* 7245 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -6:
/*      */       case -5:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/* 7261 */         switch (toType) {
/*      */           case -6:
/*      */           case -5:
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*      */           case 12:
/* 7277 */             return true;
/*      */         } 
/*      */         
/* 7280 */         return false;
/*      */ 
/*      */ 
/*      */       
/*      */       case 0:
/* 7285 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 1111:
/* 7293 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/* 7300 */             return true;
/*      */         } 
/*      */         
/* 7303 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 91:
/* 7309 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/* 7316 */             return true;
/*      */         } 
/*      */         
/* 7319 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 92:
/* 7325 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/* 7332 */             return true;
/*      */         } 
/*      */         
/* 7335 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 93:
/* 7344 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/*      */           case 91:
/*      */           case 92:
/* 7353 */             return true;
/*      */         } 
/*      */         
/* 7356 */         return false;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 7361 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCoreSQLGrammar() throws SQLException {
/* 7373 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCorrelatedSubqueries() throws SQLException {
/* 7385 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
/* 7398 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
/* 7410 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsDifferentTableCorrelationNames() throws SQLException {
/* 7423 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsExpressionsInOrderBy() throws SQLException {
/* 7434 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsExtendedSQLGrammar() throws SQLException {
/* 7445 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsFullOuterJoins() throws SQLException {
/* 7456 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGetGeneratedKeys() {
/* 7465 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGroupBy() throws SQLException {
/* 7476 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGroupByBeyondSelect() throws SQLException {
/* 7488 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGroupByUnrelated() throws SQLException {
/* 7499 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsIntegrityEnhancementFacility() throws SQLException {
/* 7510 */     if (!this.conn.getOverrideSupportsIntegrityEnhancementFacility()) {
/* 7511 */       return false;
/*      */     }
/*      */     
/* 7514 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsLikeEscapeClause() throws SQLException {
/* 7526 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsLimitedOuterJoins() throws SQLException {
/* 7538 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMinimumSQLGrammar() throws SQLException {
/* 7550 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMixedCaseIdentifiers() throws SQLException {
/* 7561 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
/* 7573 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMultipleOpenResults() throws SQLException {
/* 7580 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMultipleResultSets() throws SQLException {
/* 7591 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMultipleTransactions() throws SQLException {
/* 7603 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsNamedParameters() throws SQLException {
/* 7610 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsNonNullableColumns() throws SQLException {
/* 7622 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
/* 7634 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
/* 7646 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
/* 7658 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
/* 7670 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOrderByUnrelated() throws SQLException {
/* 7681 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOuterJoins() throws SQLException {
/* 7692 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsPositionedDelete() throws SQLException {
/* 7703 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsPositionedUpdate() throws SQLException {
/* 7714 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
/* 7732 */     switch (type) {
/*      */       case 1004:
/* 7734 */         if (concurrency == 1007 || concurrency == 1008)
/*      */         {
/* 7736 */           return true;
/*      */         }
/* 7738 */         throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009");
/*      */ 
/*      */ 
/*      */       
/*      */       case 1003:
/* 7743 */         if (concurrency == 1007 || concurrency == 1008)
/*      */         {
/* 7745 */           return true;
/*      */         }
/* 7747 */         throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009");
/*      */ 
/*      */ 
/*      */       
/*      */       case 1005:
/* 7752 */         return false;
/*      */     } 
/* 7754 */     throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsResultSetHoldability(int holdability) throws SQLException {
/* 7766 */     return (holdability == 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsResultSetType(int type) throws SQLException {
/* 7780 */     return (type == 1004);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSavepoints() throws SQLException {
/* 7788 */     return (this.conn.versionMeetsMinimum(4, 0, 14) || this.conn.versionMeetsMinimum(4, 1, 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInDataManipulation() throws SQLException {
/* 7800 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInIndexDefinitions() throws SQLException {
/* 7811 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
/* 7822 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInProcedureCalls() throws SQLException {
/* 7833 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInTableDefinitions() throws SQLException {
/* 7844 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSelectForUpdate() throws SQLException {
/* 7855 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsStatementPooling() throws SQLException {
/* 7862 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsStoredProcedures() throws SQLException {
/* 7874 */     return this.conn.versionMeetsMinimum(5, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInComparisons() throws SQLException {
/* 7886 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInExists() throws SQLException {
/* 7898 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInIns() throws SQLException {
/* 7910 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInQuantifieds() throws SQLException {
/* 7922 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTableCorrelationNames() throws SQLException {
/* 7934 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
/* 7949 */     if (this.conn.supportsIsolationLevel()) {
/* 7950 */       switch (level) {
/*      */         case 1:
/*      */         case 2:
/*      */         case 4:
/*      */         case 8:
/* 7955 */           return true;
/*      */       } 
/*      */       
/* 7958 */       return false;
/*      */     } 
/*      */ 
/*      */     
/* 7962 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTransactions() throws SQLException {
/* 7974 */     return this.conn.supportsTransactions();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsUnion() throws SQLException {
/* 7985 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsUnionAll() throws SQLException {
/* 7996 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean updatesAreDetected(int type) throws SQLException {
/* 8010 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean usesLocalFilePerTable() throws SQLException {
/* 8021 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean usesLocalFiles() throws SQLException {
/* 8032 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
/* 8048 */     Field[] fields = { new Field("", "FUNCTION_CAT", 12, 0), new Field("", "FUNCTION_SCHEM", 12, 0), new Field("", "FUNCTION_NAME", 12, 0), new Field("", "COLUMN_NAME", 12, 0), new Field("", "COLUMN_TYPE", 12, 0), new Field("", "DATA_TYPE", 5, 0), new Field("", "TYPE_NAME", 12, 0), new Field("", "PRECISION", 4, 0), new Field("", "LENGTH", 4, 0), new Field("", "SCALE", 5, 0), new Field("", "RADIX", 5, 0), new Field("", "NULLABLE", 5, 0), new Field("", "REMARKS", 12, 0), new Field("", "CHAR_OCTET_LENGTH", 4, 0), new Field("", "ORDINAL_POSITION", 4, 0), new Field("", "IS_NULLABLE", 12, 3), new Field("", "SPECIFIC_NAME", 12, 0) };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 8067 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean providesQueryObjectGenerator() throws SQLException {
/* 8074 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
/* 8079 */     Field[] fields = { new Field("", "TABLE_SCHEM", 12, 255), new Field("", "TABLE_CATALOG", 12, 255) };
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 8084 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */   
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
/* 8088 */     return true;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\DatabaseMetaData.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */