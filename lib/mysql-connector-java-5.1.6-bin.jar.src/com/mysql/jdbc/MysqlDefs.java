/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ public final class MysqlDefs
/*     */ {
/*     */   static final int COM_BINLOG_DUMP = 18;
/*     */   static final int COM_CHANGE_USER = 17;
/*     */   static final int COM_CLOSE_STATEMENT = 25;
/*     */   static final int COM_CONNECT_OUT = 20;
/*     */   static final int COM_END = 29;
/*     */   static final int COM_EXECUTE = 23;
/*     */   static final int COM_FETCH = 28;
/*     */   static final int COM_LONG_DATA = 24;
/*     */   static final int COM_PREPARE = 22;
/*     */   static final int COM_REGISTER_SLAVE = 21;
/*     */   static final int COM_RESET_STMT = 26;
/*     */   static final int COM_SET_OPTION = 27;
/*     */   static final int COM_TABLE_DUMP = 19;
/*     */   static final int CONNECT = 11;
/*     */   static final int CREATE_DB = 5;
/*     */   static final int DEBUG = 13;
/*     */   static final int DELAYED_INSERT = 16;
/*     */   static final int DROP_DB = 6;
/*     */   static final int FIELD_LIST = 4;
/*     */   static final int FIELD_TYPE_BIT = 16;
/*     */   public static final int FIELD_TYPE_BLOB = 252;
/*     */   static final int FIELD_TYPE_DATE = 10;
/*     */   static final int FIELD_TYPE_DATETIME = 12;
/*     */   static final int FIELD_TYPE_DECIMAL = 0;
/*     */   static final int FIELD_TYPE_DOUBLE = 5;
/*     */   static final int FIELD_TYPE_ENUM = 247;
/*     */   static final int FIELD_TYPE_FLOAT = 4;
/*     */   static final int FIELD_TYPE_GEOMETRY = 255;
/*     */   static final int FIELD_TYPE_INT24 = 9;
/*     */   static final int FIELD_TYPE_LONG = 3;
/*     */   static final int FIELD_TYPE_LONG_BLOB = 251;
/*     */   static final int FIELD_TYPE_LONGLONG = 8;
/*     */   static final int FIELD_TYPE_MEDIUM_BLOB = 250;
/*     */   static final int FIELD_TYPE_NEW_DECIMAL = 246;
/*     */   static final int FIELD_TYPE_NEWDATE = 14;
/*     */   static final int FIELD_TYPE_NULL = 6;
/*     */   static final int FIELD_TYPE_SET = 248;
/*     */   static final int FIELD_TYPE_SHORT = 2;
/*     */   static final int FIELD_TYPE_STRING = 254;
/*     */   static final int FIELD_TYPE_TIME = 11;
/*     */   static final int FIELD_TYPE_TIMESTAMP = 7;
/*     */   static final int FIELD_TYPE_TINY = 1;
/*     */   static final int FIELD_TYPE_TINY_BLOB = 249;
/*     */   static final int FIELD_TYPE_VAR_STRING = 253;
/*     */   static final int FIELD_TYPE_VARCHAR = 15;
/*     */   static final int FIELD_TYPE_YEAR = 13;
/*     */   static final int INIT_DB = 2;
/*     */   static final long LENGTH_BLOB = 65535L;
/*     */   static final long LENGTH_LONGBLOB = 4294967295L;
/*     */   static final long LENGTH_MEDIUMBLOB = 16777215L;
/*     */   static final long LENGTH_TINYBLOB = 255L;
/*     */   static final int MAX_ROWS = 50000000;
/*     */   public static final int NO_CHARSET_INFO = -1;
/*     */   static final byte OPEN_CURSOR_FLAG = 1;
/*     */   static final int PING = 14;
/*     */   static final int PROCESS_INFO = 10;
/*     */   static final int PROCESS_KILL = 12;
/*     */   static final int QUERY = 3;
/*     */   static final int QUIT = 1;
/*     */   static final int RELOAD = 7;
/*     */   static final int SHUTDOWN = 8;
/*     */   static final int SLEEP = 0;
/*     */   static final int STATISTICS = 9;
/*     */   static final int TIME = 15;
/*     */   
/*     */   static int mysqlToJavaType(int mysqlType) {
/* 193 */     switch (mysqlType)
/*     */     { case 0:
/*     */       case 246:
/* 196 */         jdbcType = 3;
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
/* 322 */         return jdbcType;case 1: jdbcType = -6; return jdbcType;case 2: jdbcType = 5; return jdbcType;case 3: jdbcType = 4; return jdbcType;case 4: jdbcType = 7; return jdbcType;case 5: jdbcType = 8; return jdbcType;case 6: jdbcType = 0; return jdbcType;case 7: jdbcType = 93; return jdbcType;case 8: jdbcType = -5; return jdbcType;case 9: jdbcType = 4; return jdbcType;case 10: jdbcType = 91; return jdbcType;case 11: jdbcType = 92; return jdbcType;case 12: jdbcType = 93; return jdbcType;case 13: jdbcType = 91; return jdbcType;case 14: jdbcType = 91; return jdbcType;case 247: jdbcType = 1; return jdbcType;case 248: jdbcType = 1; return jdbcType;case 249: jdbcType = -3; return jdbcType;case 250: jdbcType = -4; return jdbcType;case 251: jdbcType = -4; return jdbcType;case 252: jdbcType = -4; return jdbcType;case 15: case 253: jdbcType = 12; return jdbcType;case 254: jdbcType = 1; return jdbcType;case 255: jdbcType = -2; return jdbcType;case 16: jdbcType = -7; return jdbcType; }  int jdbcType = 12; return jdbcType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int mysqlToJavaType(String mysqlType) {
/* 329 */     if (mysqlType.equalsIgnoreCase("BIT"))
/* 330 */       return mysqlToJavaType(16); 
/* 331 */     if (mysqlType.equalsIgnoreCase("TINYINT"))
/* 332 */       return mysqlToJavaType(1); 
/* 333 */     if (mysqlType.equalsIgnoreCase("SMALLINT"))
/* 334 */       return mysqlToJavaType(2); 
/* 335 */     if (mysqlType.equalsIgnoreCase("MEDIUMINT"))
/* 336 */       return mysqlToJavaType(9); 
/* 337 */     if (mysqlType.equalsIgnoreCase("INT") || mysqlType.equalsIgnoreCase("INTEGER"))
/* 338 */       return mysqlToJavaType(3); 
/* 339 */     if (mysqlType.equalsIgnoreCase("BIGINT"))
/* 340 */       return mysqlToJavaType(8); 
/* 341 */     if (mysqlType.equalsIgnoreCase("INT24"))
/* 342 */       return mysqlToJavaType(9); 
/* 343 */     if (mysqlType.equalsIgnoreCase("REAL"))
/* 344 */       return mysqlToJavaType(5); 
/* 345 */     if (mysqlType.equalsIgnoreCase("FLOAT"))
/* 346 */       return mysqlToJavaType(4); 
/* 347 */     if (mysqlType.equalsIgnoreCase("DECIMAL"))
/* 348 */       return mysqlToJavaType(0); 
/* 349 */     if (mysqlType.equalsIgnoreCase("NUMERIC"))
/* 350 */       return mysqlToJavaType(0); 
/* 351 */     if (mysqlType.equalsIgnoreCase("DOUBLE"))
/* 352 */       return mysqlToJavaType(5); 
/* 353 */     if (mysqlType.equalsIgnoreCase("CHAR"))
/* 354 */       return mysqlToJavaType(254); 
/* 355 */     if (mysqlType.equalsIgnoreCase("VARCHAR"))
/* 356 */       return mysqlToJavaType(253); 
/* 357 */     if (mysqlType.equalsIgnoreCase("DATE"))
/* 358 */       return mysqlToJavaType(10); 
/* 359 */     if (mysqlType.equalsIgnoreCase("TIME"))
/* 360 */       return mysqlToJavaType(11); 
/* 361 */     if (mysqlType.equalsIgnoreCase("YEAR"))
/* 362 */       return mysqlToJavaType(13); 
/* 363 */     if (mysqlType.equalsIgnoreCase("TIMESTAMP"))
/* 364 */       return mysqlToJavaType(7); 
/* 365 */     if (mysqlType.equalsIgnoreCase("DATETIME"))
/* 366 */       return mysqlToJavaType(12); 
/* 367 */     if (mysqlType.equalsIgnoreCase("TINYBLOB"))
/* 368 */       return -2; 
/* 369 */     if (mysqlType.equalsIgnoreCase("BLOB"))
/* 370 */       return -4; 
/* 371 */     if (mysqlType.equalsIgnoreCase("MEDIUMBLOB"))
/* 372 */       return -4; 
/* 373 */     if (mysqlType.equalsIgnoreCase("LONGBLOB"))
/* 374 */       return -4; 
/* 375 */     if (mysqlType.equalsIgnoreCase("TINYTEXT"))
/* 376 */       return 12; 
/* 377 */     if (mysqlType.equalsIgnoreCase("TEXT"))
/* 378 */       return -1; 
/* 379 */     if (mysqlType.equalsIgnoreCase("MEDIUMTEXT"))
/* 380 */       return -1; 
/* 381 */     if (mysqlType.equalsIgnoreCase("LONGTEXT"))
/* 382 */       return -1; 
/* 383 */     if (mysqlType.equalsIgnoreCase("ENUM"))
/* 384 */       return mysqlToJavaType(247); 
/* 385 */     if (mysqlType.equalsIgnoreCase("SET"))
/* 386 */       return mysqlToJavaType(248); 
/* 387 */     if (mysqlType.equalsIgnoreCase("GEOMETRY"))
/* 388 */       return mysqlToJavaType(255); 
/* 389 */     if (mysqlType.equalsIgnoreCase("BINARY"))
/* 390 */       return -2; 
/* 391 */     if (mysqlType.equalsIgnoreCase("VARBINARY"))
/* 392 */       return -3; 
/* 393 */     if (mysqlType.equalsIgnoreCase("BIT")) {
/* 394 */       return mysqlToJavaType(16);
/*     */     }
/*     */ 
/*     */     
/* 398 */     return 1111;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String typeToName(int mysqlType) {
/* 406 */     switch (mysqlType) {
/*     */       case 0:
/* 408 */         return "FIELD_TYPE_DECIMAL";
/*     */       
/*     */       case 1:
/* 411 */         return "FIELD_TYPE_TINY";
/*     */       
/*     */       case 2:
/* 414 */         return "FIELD_TYPE_SHORT";
/*     */       
/*     */       case 3:
/* 417 */         return "FIELD_TYPE_LONG";
/*     */       
/*     */       case 4:
/* 420 */         return "FIELD_TYPE_FLOAT";
/*     */       
/*     */       case 5:
/* 423 */         return "FIELD_TYPE_DOUBLE";
/*     */       
/*     */       case 6:
/* 426 */         return "FIELD_TYPE_NULL";
/*     */       
/*     */       case 7:
/* 429 */         return "FIELD_TYPE_TIMESTAMP";
/*     */       
/*     */       case 8:
/* 432 */         return "FIELD_TYPE_LONGLONG";
/*     */       
/*     */       case 9:
/* 435 */         return "FIELD_TYPE_INT24";
/*     */       
/*     */       case 10:
/* 438 */         return "FIELD_TYPE_DATE";
/*     */       
/*     */       case 11:
/* 441 */         return "FIELD_TYPE_TIME";
/*     */       
/*     */       case 12:
/* 444 */         return "FIELD_TYPE_DATETIME";
/*     */       
/*     */       case 13:
/* 447 */         return "FIELD_TYPE_YEAR";
/*     */       
/*     */       case 14:
/* 450 */         return "FIELD_TYPE_NEWDATE";
/*     */       
/*     */       case 247:
/* 453 */         return "FIELD_TYPE_ENUM";
/*     */       
/*     */       case 248:
/* 456 */         return "FIELD_TYPE_SET";
/*     */       
/*     */       case 249:
/* 459 */         return "FIELD_TYPE_TINY_BLOB";
/*     */       
/*     */       case 250:
/* 462 */         return "FIELD_TYPE_MEDIUM_BLOB";
/*     */       
/*     */       case 251:
/* 465 */         return "FIELD_TYPE_LONG_BLOB";
/*     */       
/*     */       case 252:
/* 468 */         return "FIELD_TYPE_BLOB";
/*     */       
/*     */       case 253:
/* 471 */         return "FIELD_TYPE_VAR_STRING";
/*     */       
/*     */       case 254:
/* 474 */         return "FIELD_TYPE_STRING";
/*     */       
/*     */       case 15:
/* 477 */         return "FIELD_TYPE_VARCHAR";
/*     */       
/*     */       case 255:
/* 480 */         return "FIELD_TYPE_GEOMETRY";
/*     */     } 
/*     */     
/* 483 */     return " Unknown MySQL Type # " + mysqlType;
/*     */   }
/*     */ 
/*     */   
/* 487 */   private static Map mysqlToJdbcTypesMap = new HashMap();
/*     */   
/*     */   static {
/* 490 */     mysqlToJdbcTypesMap.put("BIT", Constants.integerValueOf(mysqlToJavaType(16)));
/*     */ 
/*     */     
/* 493 */     mysqlToJdbcTypesMap.put("TINYINT", Constants.integerValueOf(mysqlToJavaType(1)));
/*     */     
/* 495 */     mysqlToJdbcTypesMap.put("SMALLINT", Constants.integerValueOf(mysqlToJavaType(2)));
/*     */     
/* 497 */     mysqlToJdbcTypesMap.put("MEDIUMINT", Constants.integerValueOf(mysqlToJavaType(9)));
/*     */     
/* 499 */     mysqlToJdbcTypesMap.put("INT", Constants.integerValueOf(mysqlToJavaType(3)));
/*     */     
/* 501 */     mysqlToJdbcTypesMap.put("INTEGER", Constants.integerValueOf(mysqlToJavaType(3)));
/*     */     
/* 503 */     mysqlToJdbcTypesMap.put("BIGINT", Constants.integerValueOf(mysqlToJavaType(8)));
/*     */     
/* 505 */     mysqlToJdbcTypesMap.put("INT24", Constants.integerValueOf(mysqlToJavaType(9)));
/*     */     
/* 507 */     mysqlToJdbcTypesMap.put("REAL", Constants.integerValueOf(mysqlToJavaType(5)));
/*     */     
/* 509 */     mysqlToJdbcTypesMap.put("FLOAT", Constants.integerValueOf(mysqlToJavaType(4)));
/*     */     
/* 511 */     mysqlToJdbcTypesMap.put("DECIMAL", Constants.integerValueOf(mysqlToJavaType(0)));
/*     */     
/* 513 */     mysqlToJdbcTypesMap.put("NUMERIC", Constants.integerValueOf(mysqlToJavaType(0)));
/*     */     
/* 515 */     mysqlToJdbcTypesMap.put("DOUBLE", Constants.integerValueOf(mysqlToJavaType(5)));
/*     */     
/* 517 */     mysqlToJdbcTypesMap.put("CHAR", Constants.integerValueOf(mysqlToJavaType(254)));
/*     */     
/* 519 */     mysqlToJdbcTypesMap.put("VARCHAR", Constants.integerValueOf(mysqlToJavaType(253)));
/*     */     
/* 521 */     mysqlToJdbcTypesMap.put("DATE", Constants.integerValueOf(mysqlToJavaType(10)));
/*     */     
/* 523 */     mysqlToJdbcTypesMap.put("TIME", Constants.integerValueOf(mysqlToJavaType(11)));
/*     */     
/* 525 */     mysqlToJdbcTypesMap.put("YEAR", Constants.integerValueOf(mysqlToJavaType(13)));
/*     */     
/* 527 */     mysqlToJdbcTypesMap.put("TIMESTAMP", Constants.integerValueOf(mysqlToJavaType(7)));
/*     */     
/* 529 */     mysqlToJdbcTypesMap.put("DATETIME", Constants.integerValueOf(mysqlToJavaType(12)));
/*     */     
/* 531 */     mysqlToJdbcTypesMap.put("TINYBLOB", Constants.integerValueOf(-2));
/* 532 */     mysqlToJdbcTypesMap.put("BLOB", Constants.integerValueOf(-4));
/*     */     
/* 534 */     mysqlToJdbcTypesMap.put("MEDIUMBLOB", Constants.integerValueOf(-4));
/*     */     
/* 536 */     mysqlToJdbcTypesMap.put("LONGBLOB", Constants.integerValueOf(-4));
/*     */     
/* 538 */     mysqlToJdbcTypesMap.put("TINYTEXT", Constants.integerValueOf(12));
/*     */     
/* 540 */     mysqlToJdbcTypesMap.put("TEXT", Constants.integerValueOf(-1));
/*     */     
/* 542 */     mysqlToJdbcTypesMap.put("MEDIUMTEXT", Constants.integerValueOf(-1));
/*     */     
/* 544 */     mysqlToJdbcTypesMap.put("LONGTEXT", Constants.integerValueOf(-1));
/*     */     
/* 546 */     mysqlToJdbcTypesMap.put("ENUM", Constants.integerValueOf(mysqlToJavaType(247)));
/*     */     
/* 548 */     mysqlToJdbcTypesMap.put("SET", Constants.integerValueOf(mysqlToJavaType(248)));
/*     */     
/* 550 */     mysqlToJdbcTypesMap.put("GEOMETRY", Constants.integerValueOf(mysqlToJavaType(255)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final void appendJdbcTypeMappingQuery(StringBuffer buf, String mysqlTypeColumnName) {
/* 556 */     buf.append("CASE ");
/* 557 */     Map typesMap = new HashMap();
/* 558 */     typesMap.putAll(mysqlToJdbcTypesMap);
/* 559 */     typesMap.put("BINARY", Constants.integerValueOf(-2));
/* 560 */     typesMap.put("VARBINARY", Constants.integerValueOf(-3));
/*     */     
/* 562 */     Iterator mysqlTypes = typesMap.keySet().iterator();
/*     */     
/* 564 */     while (mysqlTypes.hasNext()) {
/* 565 */       String mysqlTypeName = mysqlTypes.next();
/* 566 */       buf.append(" WHEN ");
/* 567 */       buf.append(mysqlTypeColumnName);
/* 568 */       buf.append("='");
/* 569 */       buf.append(mysqlTypeName);
/* 570 */       buf.append("' THEN ");
/* 571 */       buf.append(typesMap.get(mysqlTypeName));
/*     */       
/* 573 */       if (mysqlTypeName.equalsIgnoreCase("DOUBLE") || mysqlTypeName.equalsIgnoreCase("FLOAT") || mysqlTypeName.equalsIgnoreCase("DECIMAL") || mysqlTypeName.equalsIgnoreCase("NUMERIC")) {
/*     */ 
/*     */ 
/*     */         
/* 577 */         buf.append(" WHEN ");
/* 578 */         buf.append(mysqlTypeColumnName);
/* 579 */         buf.append("='");
/* 580 */         buf.append(mysqlTypeName);
/* 581 */         buf.append(" unsigned' THEN ");
/* 582 */         buf.append(typesMap.get(mysqlTypeName));
/*     */       } 
/*     */     } 
/*     */     
/* 586 */     buf.append(" ELSE ");
/* 587 */     buf.append(1111);
/* 588 */     buf.append(" END ");
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\MysqlDefs.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */