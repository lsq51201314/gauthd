/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class EscapeProcessor
/*     */ {
/*     */   private static Map JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
/*     */   private static Map JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;
/*     */   
/*     */   static {
/*  51 */     Map tempMap = new HashMap();
/*     */     
/*  53 */     tempMap.put("BIGINT", "0 + ?");
/*  54 */     tempMap.put("BINARY", "BINARY");
/*  55 */     tempMap.put("BIT", "0 + ?");
/*  56 */     tempMap.put("CHAR", "CHAR");
/*  57 */     tempMap.put("DATE", "DATE");
/*  58 */     tempMap.put("DECIMAL", "0.0 + ?");
/*  59 */     tempMap.put("DOUBLE", "0.0 + ?");
/*  60 */     tempMap.put("FLOAT", "0.0 + ?");
/*  61 */     tempMap.put("INTEGER", "0 + ?");
/*  62 */     tempMap.put("LONGVARBINARY", "BINARY");
/*  63 */     tempMap.put("LONGVARCHAR", "CONCAT(?)");
/*  64 */     tempMap.put("REAL", "0.0 + ?");
/*  65 */     tempMap.put("SMALLINT", "CONCAT(?)");
/*  66 */     tempMap.put("TIME", "TIME");
/*  67 */     tempMap.put("TIMESTAMP", "DATETIME");
/*  68 */     tempMap.put("TINYINT", "CONCAT(?)");
/*  69 */     tempMap.put("VARBINARY", "BINARY");
/*  70 */     tempMap.put("VARCHAR", "CONCAT(?)");
/*     */     
/*  72 */     JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
/*     */     
/*  74 */     tempMap = new HashMap(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
/*     */     
/*  76 */     tempMap.put("BINARY", "CONCAT(?)");
/*  77 */     tempMap.put("CHAR", "CONCAT(?)");
/*  78 */     tempMap.remove("DATE");
/*  79 */     tempMap.put("LONGVARBINARY", "CONCAT(?)");
/*  80 */     tempMap.remove("TIME");
/*  81 */     tempMap.remove("TIMESTAMP");
/*  82 */     tempMap.put("VARBINARY", "CONCAT(?)");
/*     */     
/*  84 */     JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap);
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
/*     */   public static final Object escapeSQL(String sql, boolean serverSupportsConvertFn, ConnectionImpl conn) throws SQLException {
/* 105 */     boolean replaceEscapeSequence = false;
/* 106 */     String escapeSequence = null;
/*     */     
/* 108 */     if (sql == null) {
/* 109 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 116 */     int beginBrace = sql.indexOf('{');
/* 117 */     int nextEndBrace = (beginBrace == -1) ? -1 : sql.indexOf('}', beginBrace);
/*     */ 
/*     */     
/* 120 */     if (nextEndBrace == -1) {
/* 121 */       return sql;
/*     */     }
/*     */     
/* 124 */     StringBuffer newSql = new StringBuffer();
/*     */     
/* 126 */     EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
/*     */     
/* 128 */     byte usesVariables = 0;
/* 129 */     boolean callingStoredFunction = false;
/*     */     
/* 131 */     while (escapeTokenizer.hasMoreTokens()) {
/* 132 */       String token = escapeTokenizer.nextToken();
/*     */       
/* 134 */       if (token.length() != 0) {
/* 135 */         if (token.charAt(0) == '{') {
/*     */           
/* 137 */           if (!token.endsWith("}")) {
/* 138 */             throw SQLError.createSQLException("Not a valid escape sequence: " + token);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 143 */           if (token.length() > 2) {
/* 144 */             int nestedBrace = token.indexOf('{', 2);
/*     */             
/* 146 */             if (nestedBrace != -1) {
/* 147 */               StringBuffer buf = new StringBuffer(token.substring(0, 1));
/*     */ 
/*     */               
/* 150 */               Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), serverSupportsConvertFn, conn);
/*     */ 
/*     */ 
/*     */               
/* 154 */               String remaining = null;
/*     */               
/* 156 */               if (remainingResults instanceof String) {
/* 157 */                 remaining = (String)remainingResults;
/*     */               } else {
/* 159 */                 remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
/*     */                 
/* 161 */                 if (usesVariables != 1) {
/* 162 */                   usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
/*     */                 }
/*     */               } 
/*     */               
/* 166 */               buf.append(remaining);
/*     */               
/* 168 */               buf.append('}');
/*     */               
/* 170 */               token = buf.toString();
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 176 */           String collapsedToken = removeWhitespace(token);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 181 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape")) {
/*     */             
/*     */             try {
/* 184 */               StringTokenizer st = new StringTokenizer(token, " '");
/*     */               
/* 186 */               st.nextToken();
/* 187 */               escapeSequence = st.nextToken();
/*     */               
/* 189 */               if (escapeSequence.length() < 3) {
/* 190 */                 newSql.append(token);
/*     */ 
/*     */                 
/*     */                 continue;
/*     */               } 
/*     */               
/* 196 */               escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
/*     */               
/* 198 */               replaceEscapeSequence = true;
/*     */             }
/* 200 */             catch (NoSuchElementException e) {
/* 201 */               newSql.append(token);
/*     */             } 
/*     */             
/*     */             continue;
/*     */           } 
/* 206 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn")) {
/*     */             
/* 208 */             int startPos = token.toLowerCase().indexOf("fn ") + 3;
/* 209 */             int endPos = token.length() - 1;
/*     */             
/* 211 */             String fnToken = token.substring(startPos, endPos);
/*     */ 
/*     */ 
/*     */             
/* 215 */             if (StringUtils.startsWithIgnoreCaseAndWs(fnToken, "convert")) {
/*     */               
/* 217 */               newSql.append(processConvertToken(fnToken, serverSupportsConvertFn));
/*     */               
/*     */               continue;
/*     */             } 
/* 221 */             newSql.append(fnToken); continue;
/*     */           } 
/* 223 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d")) {
/*     */             
/* 225 */             int startPos = token.indexOf('\'') + 1;
/* 226 */             int endPos = token.lastIndexOf('\'');
/*     */             
/* 228 */             if (startPos == -1 || endPos == -1) {
/* 229 */               newSql.append(token);
/*     */ 
/*     */               
/*     */               continue;
/*     */             } 
/*     */             
/* 235 */             String argument = token.substring(startPos, endPos);
/*     */             
/*     */             try {
/* 238 */               StringTokenizer st = new StringTokenizer(argument, " -");
/*     */               
/* 240 */               String year4 = st.nextToken();
/* 241 */               String month2 = st.nextToken();
/* 242 */               String day2 = st.nextToken();
/* 243 */               String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
/*     */               
/* 245 */               newSql.append(dateString);
/* 246 */             } catch (NoSuchElementException e) {
/* 247 */               throw SQLError.createSQLException("Syntax error for DATE escape sequence '" + argument + "'", "42000");
/*     */             } 
/*     */             
/*     */             continue;
/*     */           } 
/* 252 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts")) {
/*     */             
/* 254 */             processTimestampToken(conn, newSql, token); continue;
/* 255 */           }  if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t")) {
/*     */             
/* 257 */             processTimeToken(conn, newSql, token); continue;
/* 258 */           }  if (StringUtils.startsWithIgnoreCase(collapsedToken, "{call") || StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 263 */             int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
/*     */             
/* 265 */             int endPos = token.length() - 1;
/*     */             
/* 267 */             if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
/*     */               
/* 269 */               callingStoredFunction = true;
/* 270 */               newSql.append("SELECT ");
/* 271 */               newSql.append(token.substring(startPos, endPos));
/*     */             } else {
/* 273 */               callingStoredFunction = false;
/* 274 */               newSql.append("CALL ");
/* 275 */               newSql.append(token.substring(startPos, endPos));
/*     */             } 
/*     */             
/* 278 */             for (int i = endPos - 1; i >= startPos; ) {
/* 279 */               char c = token.charAt(i);
/*     */               
/* 281 */               if (Character.isWhitespace(c)) {
/*     */                 i--;
/*     */                 continue;
/*     */               } 
/* 285 */               if (c != ')') {
/* 286 */                 newSql.append("()");
/*     */               }
/*     */             } 
/*     */ 
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 294 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj"))
/*     */           {
/*     */ 
/*     */             
/* 298 */             newSql.append(token); } 
/*     */           continue;
/*     */         } 
/* 301 */         newSql.append(token);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 306 */     String escapedSql = newSql.toString();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 312 */     if (replaceEscapeSequence) {
/* 313 */       String currentSql = escapedSql;
/*     */       
/* 315 */       while (currentSql.indexOf(escapeSequence) != -1) {
/* 316 */         int escapePos = currentSql.indexOf(escapeSequence);
/* 317 */         String lhs = currentSql.substring(0, escapePos);
/* 318 */         String rhs = currentSql.substring(escapePos + 1, currentSql.length());
/*     */         
/* 320 */         currentSql = lhs + "\\" + rhs;
/*     */       } 
/*     */       
/* 323 */       escapedSql = currentSql;
/*     */     } 
/*     */     
/* 326 */     EscapeProcessorResult epr = new EscapeProcessorResult();
/* 327 */     epr.escapedSql = escapedSql;
/* 328 */     epr.callingStoredFunction = callingStoredFunction;
/*     */     
/* 330 */     if (usesVariables != 1) {
/* 331 */       if (escapeTokenizer.sawVariableUse()) {
/* 332 */         epr.usesVariables = 1;
/*     */       } else {
/* 334 */         epr.usesVariables = 0;
/*     */       } 
/*     */     }
/*     */     
/* 338 */     return epr;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void processTimeToken(ConnectionImpl conn, StringBuffer newSql, String token) throws SQLException {
/* 343 */     int startPos = token.indexOf('\'') + 1;
/* 344 */     int endPos = token.lastIndexOf('\'');
/*     */     
/* 346 */     if (startPos == -1 || endPos == -1) {
/* 347 */       newSql.append(token);
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 353 */       String argument = token.substring(startPos, endPos);
/*     */       
/*     */       try {
/* 356 */         StringTokenizer st = new StringTokenizer(argument, " :");
/*     */         
/* 358 */         String hour = st.nextToken();
/* 359 */         String minute = st.nextToken();
/* 360 */         String second = st.nextToken();
/*     */         
/* 362 */         if (!conn.getUseTimezone() || !conn.getUseLegacyDatetimeCode()) {
/*     */           
/* 364 */           String timeString = "'" + hour + ":" + minute + ":" + second + "'";
/*     */           
/* 366 */           newSql.append(timeString);
/*     */         } else {
/* 368 */           Calendar sessionCalendar = null;
/*     */           
/* 370 */           if (conn != null) {
/* 371 */             sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */           } else {
/*     */             
/* 374 */             sessionCalendar = new GregorianCalendar();
/*     */           } 
/*     */           
/*     */           try {
/* 378 */             int hourInt = Integer.parseInt(hour);
/* 379 */             int minuteInt = Integer.parseInt(minute);
/*     */             
/* 381 */             int secondInt = Integer.parseInt(second);
/*     */ 
/*     */             
/* 384 */             synchronized (sessionCalendar) {
/* 385 */               Time toBeAdjusted = TimeUtil.fastTimeCreate(sessionCalendar, hourInt, minuteInt, secondInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 391 */               Time inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, (Calendar)null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 403 */               newSql.append("'");
/* 404 */               newSql.append(inServerTimezone.toString());
/*     */               
/* 406 */               newSql.append("'");
/*     */             }
/*     */           
/* 409 */           } catch (NumberFormatException nfe) {
/* 410 */             throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009");
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 417 */       catch (NoSuchElementException e) {
/* 418 */         throw SQLError.createSQLException("Syntax error for escape sequence '" + argument + "'", "42000");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void processTimestampToken(ConnectionImpl conn, StringBuffer newSql, String token) throws SQLException {
/* 427 */     int startPos = token.indexOf('\'') + 1;
/* 428 */     int endPos = token.lastIndexOf('\'');
/*     */     
/* 430 */     if (startPos == -1 || endPos == -1) {
/* 431 */       newSql.append(token);
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 437 */       String argument = token.substring(startPos, endPos);
/*     */       
/*     */       try {
/* 440 */         if (!conn.getUseLegacyDatetimeCode()) {
/* 441 */           Timestamp ts = Timestamp.valueOf(argument);
/* 442 */           SimpleDateFormat tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''", Locale.US);
/*     */ 
/*     */           
/* 445 */           tsdf.setTimeZone(conn.getServerTimezoneTZ());
/*     */ 
/*     */ 
/*     */           
/* 449 */           newSql.append(tsdf.format(ts));
/*     */         } else {
/* 451 */           StringTokenizer st = new StringTokenizer(argument, " .-:");
/*     */           
/*     */           try {
/* 454 */             String year4 = st.nextToken();
/* 455 */             String month2 = st.nextToken();
/* 456 */             String day2 = st.nextToken();
/* 457 */             String hour = st.nextToken();
/* 458 */             String minute = st.nextToken();
/* 459 */             String second = st.nextToken();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 492 */             if (!conn.getUseTimezone() && !conn.getUseJDBCCompliantTimezoneShift()) {
/*     */ 
/*     */               
/* 495 */               newSql.append("'").append(year4).append("-").append(month2).append("-").append(day2).append(" ").append(hour).append(":").append(minute).append(":").append(second).append("'");
/*     */             } else {
/*     */               Calendar sessionCalendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 505 */               if (conn != null) {
/* 506 */                 sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */               } else {
/*     */                 
/* 509 */                 sessionCalendar = new GregorianCalendar();
/* 510 */                 sessionCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */               } 
/*     */ 
/*     */ 
/*     */               
/*     */               try {
/* 516 */                 int year4Int = Integer.parseInt(year4);
/*     */                 
/* 518 */                 int month2Int = Integer.parseInt(month2);
/*     */                 
/* 520 */                 int day2Int = Integer.parseInt(day2);
/*     */                 
/* 522 */                 int hourInt = Integer.parseInt(hour);
/*     */                 
/* 524 */                 int minuteInt = Integer.parseInt(minute);
/*     */                 
/* 526 */                 int secondInt = Integer.parseInt(second);
/*     */ 
/*     */                 
/* 529 */                 synchronized (sessionCalendar) {
/* 530 */                   boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
/*     */ 
/*     */                   
/* 533 */                   Timestamp toBeAdjusted = TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? Calendar.getInstance(TimeZone.getTimeZone("GMT")) : null, sessionCalendar, year4Int, month2Int, day2Int, hourInt, minuteInt, secondInt, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 549 */                   Timestamp inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, (Calendar)null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 561 */                   newSql.append("'");
/*     */                   
/* 563 */                   String timezoneLiteral = inServerTimezone.toString();
/*     */ 
/*     */                   
/* 566 */                   int indexOfDot = timezoneLiteral.indexOf(".");
/*     */ 
/*     */                   
/* 569 */                   if (indexOfDot != -1) {
/* 570 */                     timezoneLiteral = timezoneLiteral.substring(0, indexOfDot);
/*     */                   }
/*     */ 
/*     */ 
/*     */                   
/* 575 */                   newSql.append(timezoneLiteral);
/*     */                 } 
/*     */ 
/*     */                 
/* 579 */                 newSql.append("'");
/*     */               }
/* 581 */               catch (NumberFormatException nfe) {
/* 582 */                 throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009");
/*     */               
/*     */               }
/*     */ 
/*     */             
/*     */             }
/*     */           
/*     */           }
/* 590 */           catch (NoSuchElementException e) {
/* 591 */             throw SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000");
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 597 */       catch (IllegalArgumentException illegalArgumentException) {
/* 598 */         SQLException sqlEx = SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000");
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 603 */         sqlEx.initCause(illegalArgumentException);
/*     */         
/* 605 */         throw sqlEx;
/*     */       } 
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn) throws SQLException {
/* 650 */     int firstIndexOfParen = functionToken.indexOf("(");
/*     */     
/* 652 */     if (firstIndexOfParen == -1) {
/* 653 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'.", "42000");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 660 */     int tokenLength = functionToken.length();
/*     */     
/* 662 */     int indexOfComma = functionToken.lastIndexOf(",");
/*     */     
/* 664 */     if (indexOfComma == -1) {
/* 665 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'.", "42000");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 672 */     int indexOfCloseParen = functionToken.indexOf(')', indexOfComma);
/*     */     
/* 674 */     if (indexOfCloseParen == -1) {
/* 675 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'.", "42000");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 683 */     String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
/*     */     
/* 685 */     String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
/*     */ 
/*     */     
/* 688 */     String newType = null;
/*     */     
/* 690 */     String trimmedType = type.trim();
/*     */     
/* 692 */     if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
/* 693 */       trimmedType = trimmedType.substring(4, trimmedType.length());
/*     */     }
/*     */     
/* 696 */     if (serverSupportsConvertFn) {
/* 697 */       newType = (String)JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */     } else {
/*     */       
/* 700 */       newType = (String)JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 710 */       if (newType == null) {
/* 711 */         throw SQLError.createSQLException("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens.", "S1000");
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 720 */     if (newType == null) {
/* 721 */       throw SQLError.createSQLException("Unsupported conversion type '" + type.trim() + "' found while processing escape token.", "S1000");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 726 */     int replaceIndex = newType.indexOf("?");
/*     */     
/* 728 */     if (replaceIndex != -1) {
/* 729 */       StringBuffer convertRewrite = new StringBuffer(newType.substring(0, replaceIndex));
/*     */       
/* 731 */       convertRewrite.append(expression);
/* 732 */       convertRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
/*     */ 
/*     */       
/* 735 */       return convertRewrite.toString();
/*     */     } 
/*     */     
/* 738 */     StringBuffer castRewrite = new StringBuffer("CAST(");
/* 739 */     castRewrite.append(expression);
/* 740 */     castRewrite.append(" AS ");
/* 741 */     castRewrite.append(newType);
/* 742 */     castRewrite.append(")");
/*     */     
/* 744 */     return castRewrite.toString();
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
/*     */   private static String removeWhitespace(String toCollapse) {
/* 758 */     if (toCollapse == null) {
/* 759 */       return null;
/*     */     }
/*     */     
/* 762 */     int length = toCollapse.length();
/*     */     
/* 764 */     StringBuffer collapsed = new StringBuffer(length);
/*     */     
/* 766 */     for (int i = 0; i < length; i++) {
/* 767 */       char c = toCollapse.charAt(i);
/*     */       
/* 769 */       if (!Character.isWhitespace(c)) {
/* 770 */         collapsed.append(c);
/*     */       }
/*     */     } 
/*     */     
/* 774 */     return collapsed.toString();
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\EscapeProcessor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */