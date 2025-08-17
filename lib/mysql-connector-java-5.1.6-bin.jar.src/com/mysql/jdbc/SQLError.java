/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLDataException;
/*      */ import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
/*      */ import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
/*      */ import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTransientConnectionException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.sql.DataTruncation;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SQLError
/*      */ {
/*      */   static final int ER_WARNING_NOT_COMPLETE_ROLLBACK = 1196;
/*      */   private static Map mysqlToSql99State;
/*      */   private static Map mysqlToSqlState;
/*      */   public static final String SQL_STATE_BASE_TABLE_NOT_FOUND = "S0002";
/*      */   public static final String SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS = "S0001";
/*      */   public static final String SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND = "42S02";
/*      */   public static final String SQL_STATE_COLUMN_ALREADY_EXISTS = "S0021";
/*      */   public static final String SQL_STATE_COLUMN_NOT_FOUND = "S0022";
/*      */   public static final String SQL_STATE_COMMUNICATION_LINK_FAILURE = "08S01";
/*      */   public static final String SQL_STATE_CONNECTION_FAIL_DURING_TX = "08007";
/*      */   public static final String SQL_STATE_CONNECTION_IN_USE = "08002";
/*      */   public static final String SQL_STATE_CONNECTION_NOT_OPEN = "08003";
/*      */   public static final String SQL_STATE_CONNECTION_REJECTED = "08004";
/*      */   public static final String SQL_STATE_DATE_TRUNCATED = "01004";
/*      */   public static final String SQL_STATE_DATETIME_FIELD_OVERFLOW = "22008";
/*      */   public static final String SQL_STATE_DEADLOCK = "41000";
/*      */   public static final String SQL_STATE_DISCONNECT_ERROR = "01002";
/*      */   public static final String SQL_STATE_DIVISION_BY_ZERO = "22012";
/*      */   public static final String SQL_STATE_DRIVER_NOT_CAPABLE = "S1C00";
/*      */   public static final String SQL_STATE_ERROR_IN_ROW = "01S01";
/*      */   public static final String SQL_STATE_GENERAL_ERROR = "S1000";
/*      */   public static final String SQL_STATE_ILLEGAL_ARGUMENT = "S1009";
/*      */   public static final String SQL_STATE_INDEX_ALREADY_EXISTS = "S0011";
/*      */   public static final String SQL_STATE_INDEX_NOT_FOUND = "S0012";
/*      */   public static final String SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST = "21S01";
/*      */   public static final String SQL_STATE_INVALID_AUTH_SPEC = "28000";
/*      */   public static final String SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
/*      */   public static final String SQL_STATE_INVALID_COLUMN_NUMBER = "S1002";
/*      */   public static final String SQL_STATE_INVALID_CONNECTION_ATTRIBUTE = "01S00";
/*      */   public static final String SQL_STATE_MEMORY_ALLOCATION_FAILURE = "S1001";
/*      */   public static final String SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED = "01S04";
/*      */   public static final String SQL_STATE_NO_DEFAULT_FOR_COLUMN = "S0023";
/*      */   public static final String SQL_STATE_NO_ROWS_UPDATED_OR_DELETED = "01S03";
/*      */   public static final String SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE = "22003";
/*      */   public static final String SQL_STATE_PRIVILEGE_NOT_REVOKED = "01006";
/*      */   public static final String SQL_STATE_SYNTAX_ERROR = "42000";
/*      */   public static final String SQL_STATE_TIMEOUT_EXPIRED = "S1T00";
/*      */   public static final String SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN = "08007";
/*      */   public static final String SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE = "08001";
/*      */   public static final String SQL_STATE_WRONG_NO_OF_PARAMETERS = "07001";
/*      */   public static final String SQL_STATE_INVALID_TRANSACTION_TERMINATION = "2D000";
/*      */   
/*      */   static {
/*  152 */     if (Util.isJdbc4()) {
/*      */       try {
/*  154 */         JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = Class.forName("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException").getConstructor(new Class[] { ConnectionImpl.class, long.class, long.class, Exception.class });
/*      */ 
/*      */       
/*      */       }
/*  158 */       catch (SecurityException e) {
/*  159 */         throw new RuntimeException(e);
/*  160 */       } catch (NoSuchMethodException e) {
/*  161 */         throw new RuntimeException(e);
/*  162 */       } catch (ClassNotFoundException e) {
/*  163 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*  166 */       JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = null;
/*      */     } 
/*      */     
/*      */     try {
/*  170 */       THROWABLE_INIT_CAUSE_METHOD = Throwable.class.getMethod("initCause", new Class[] { Throwable.class });
/*  171 */     } catch (Throwable t) {
/*      */       
/*  173 */       THROWABLE_INIT_CAUSE_METHOD = null;
/*      */     } 
/*      */   }
/*  176 */   private static Map sqlStateMessages = new HashMap(); static {
/*  177 */     sqlStateMessages.put("01002", Messages.getString("SQLError.35"));
/*      */     
/*  179 */     sqlStateMessages.put("01004", Messages.getString("SQLError.36"));
/*      */     
/*  181 */     sqlStateMessages.put("01006", Messages.getString("SQLError.37"));
/*      */     
/*  183 */     sqlStateMessages.put("01S00", Messages.getString("SQLError.38"));
/*      */     
/*  185 */     sqlStateMessages.put("01S01", Messages.getString("SQLError.39"));
/*      */     
/*  187 */     sqlStateMessages.put("01S03", Messages.getString("SQLError.40"));
/*      */     
/*  189 */     sqlStateMessages.put("01S04", Messages.getString("SQLError.41"));
/*      */     
/*  191 */     sqlStateMessages.put("07001", Messages.getString("SQLError.42"));
/*      */     
/*  193 */     sqlStateMessages.put("08001", Messages.getString("SQLError.43"));
/*      */     
/*  195 */     sqlStateMessages.put("08002", Messages.getString("SQLError.44"));
/*      */     
/*  197 */     sqlStateMessages.put("08003", Messages.getString("SQLError.45"));
/*      */     
/*  199 */     sqlStateMessages.put("08004", Messages.getString("SQLError.46"));
/*      */     
/*  201 */     sqlStateMessages.put("08007", Messages.getString("SQLError.47"));
/*      */     
/*  203 */     sqlStateMessages.put("08S01", Messages.getString("SQLError.48"));
/*      */     
/*  205 */     sqlStateMessages.put("21S01", Messages.getString("SQLError.49"));
/*      */     
/*  207 */     sqlStateMessages.put("22003", Messages.getString("SQLError.50"));
/*      */     
/*  209 */     sqlStateMessages.put("22008", Messages.getString("SQLError.51"));
/*      */     
/*  211 */     sqlStateMessages.put("22012", Messages.getString("SQLError.52"));
/*      */     
/*  213 */     sqlStateMessages.put("41000", Messages.getString("SQLError.53"));
/*      */     
/*  215 */     sqlStateMessages.put("28000", Messages.getString("SQLError.54"));
/*      */     
/*  217 */     sqlStateMessages.put("42000", Messages.getString("SQLError.55"));
/*      */     
/*  219 */     sqlStateMessages.put("42S02", Messages.getString("SQLError.56"));
/*      */     
/*  221 */     sqlStateMessages.put("S0001", Messages.getString("SQLError.57"));
/*      */     
/*  223 */     sqlStateMessages.put("S0002", Messages.getString("SQLError.58"));
/*      */     
/*  225 */     sqlStateMessages.put("S0011", Messages.getString("SQLError.59"));
/*      */     
/*  227 */     sqlStateMessages.put("S0012", Messages.getString("SQLError.60"));
/*      */     
/*  229 */     sqlStateMessages.put("S0021", Messages.getString("SQLError.61"));
/*      */     
/*  231 */     sqlStateMessages.put("S0022", Messages.getString("SQLError.62"));
/*      */     
/*  233 */     sqlStateMessages.put("S0023", Messages.getString("SQLError.63"));
/*      */     
/*  235 */     sqlStateMessages.put("S1000", Messages.getString("SQLError.64"));
/*      */     
/*  237 */     sqlStateMessages.put("S1001", Messages.getString("SQLError.65"));
/*      */     
/*  239 */     sqlStateMessages.put("S1002", Messages.getString("SQLError.66"));
/*      */     
/*  241 */     sqlStateMessages.put("S1009", Messages.getString("SQLError.67"));
/*      */     
/*  243 */     sqlStateMessages.put("S1C00", Messages.getString("SQLError.68"));
/*      */     
/*  245 */     sqlStateMessages.put("S1T00", Messages.getString("SQLError.69"));
/*      */ 
/*      */     
/*  248 */     mysqlToSqlState = new Hashtable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  259 */     mysqlToSqlState.put(Constants.integerValueOf(1040), "08004");
/*  260 */     mysqlToSqlState.put(Constants.integerValueOf(1042), "08004");
/*  261 */     mysqlToSqlState.put(Constants.integerValueOf(1043), "08004");
/*  262 */     mysqlToSqlState.put(Constants.integerValueOf(1047), "08S01");
/*      */     
/*  264 */     mysqlToSqlState.put(Constants.integerValueOf(1081), "08S01");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  269 */     mysqlToSqlState.put(Constants.integerValueOf(1129), "08004");
/*  270 */     mysqlToSqlState.put(Constants.integerValueOf(1130), "08004");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  277 */     mysqlToSqlState.put(Constants.integerValueOf(1045), "28000");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  294 */     mysqlToSqlState.put(Constants.integerValueOf(1037), "S1001");
/*      */     
/*  296 */     mysqlToSqlState.put(Constants.integerValueOf(1038), "S1001");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  305 */     mysqlToSqlState.put(Constants.integerValueOf(1064), "42000");
/*  306 */     mysqlToSqlState.put(Constants.integerValueOf(1065), "42000");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  333 */     mysqlToSqlState.put(Constants.integerValueOf(1055), "S1009");
/*  334 */     mysqlToSqlState.put(Constants.integerValueOf(1056), "S1009");
/*  335 */     mysqlToSqlState.put(Constants.integerValueOf(1057), "S1009");
/*  336 */     mysqlToSqlState.put(Constants.integerValueOf(1059), "S1009");
/*  337 */     mysqlToSqlState.put(Constants.integerValueOf(1060), "S1009");
/*  338 */     mysqlToSqlState.put(Constants.integerValueOf(1061), "S1009");
/*  339 */     mysqlToSqlState.put(Constants.integerValueOf(1062), "S1009");
/*  340 */     mysqlToSqlState.put(Constants.integerValueOf(1063), "S1009");
/*  341 */     mysqlToSqlState.put(Constants.integerValueOf(1066), "S1009");
/*  342 */     mysqlToSqlState.put(Constants.integerValueOf(1067), "S1009");
/*  343 */     mysqlToSqlState.put(Constants.integerValueOf(1068), "S1009");
/*  344 */     mysqlToSqlState.put(Constants.integerValueOf(1069), "S1009");
/*  345 */     mysqlToSqlState.put(Constants.integerValueOf(1070), "S1009");
/*  346 */     mysqlToSqlState.put(Constants.integerValueOf(1071), "S1009");
/*  347 */     mysqlToSqlState.put(Constants.integerValueOf(1072), "S1009");
/*  348 */     mysqlToSqlState.put(Constants.integerValueOf(1073), "S1009");
/*  349 */     mysqlToSqlState.put(Constants.integerValueOf(1074), "S1009");
/*  350 */     mysqlToSqlState.put(Constants.integerValueOf(1075), "S1009");
/*  351 */     mysqlToSqlState.put(Constants.integerValueOf(1082), "S1009");
/*  352 */     mysqlToSqlState.put(Constants.integerValueOf(1083), "S1009");
/*  353 */     mysqlToSqlState.put(Constants.integerValueOf(1084), "S1009");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  358 */     mysqlToSqlState.put(Constants.integerValueOf(1058), "21S01");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  394 */     mysqlToSqlState.put(Constants.integerValueOf(1051), "42S02");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  399 */     mysqlToSqlState.put(Constants.integerValueOf(1054), "S0022");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  411 */     mysqlToSqlState.put(Constants.integerValueOf(1205), "41000");
/*  412 */     mysqlToSqlState.put(Constants.integerValueOf(1213), "41000");
/*      */     
/*  414 */     mysqlToSql99State = new HashMap();
/*      */     
/*  416 */     mysqlToSql99State.put(Constants.integerValueOf(1205), "41000");
/*  417 */     mysqlToSql99State.put(Constants.integerValueOf(1213), "41000");
/*  418 */     mysqlToSql99State.put(Constants.integerValueOf(1022), "23000");
/*      */     
/*  420 */     mysqlToSql99State.put(Constants.integerValueOf(1037), "HY001");
/*      */     
/*  422 */     mysqlToSql99State.put(Constants.integerValueOf(1038), "HY001");
/*      */     
/*  424 */     mysqlToSql99State.put(Constants.integerValueOf(1040), "08004");
/*      */     
/*  426 */     mysqlToSql99State.put(Constants.integerValueOf(1042), "08S01");
/*      */     
/*  428 */     mysqlToSql99State.put(Constants.integerValueOf(1043), "08S01");
/*      */     
/*  430 */     mysqlToSql99State.put(Constants.integerValueOf(1044), "42000");
/*      */     
/*  432 */     mysqlToSql99State.put(Constants.integerValueOf(1045), "28000");
/*      */     
/*  434 */     mysqlToSql99State.put(Constants.integerValueOf(1050), "42S01");
/*      */     
/*  436 */     mysqlToSql99State.put(Constants.integerValueOf(1051), "42S02");
/*      */     
/*  438 */     mysqlToSql99State.put(Constants.integerValueOf(1052), "23000");
/*      */     
/*  440 */     mysqlToSql99State.put(Constants.integerValueOf(1053), "08S01");
/*      */     
/*  442 */     mysqlToSql99State.put(Constants.integerValueOf(1054), "42S22");
/*      */     
/*  444 */     mysqlToSql99State.put(Constants.integerValueOf(1055), "42000");
/*      */     
/*  446 */     mysqlToSql99State.put(Constants.integerValueOf(1056), "42000");
/*      */     
/*  448 */     mysqlToSql99State.put(Constants.integerValueOf(1057), "42000");
/*      */     
/*  450 */     mysqlToSql99State.put(Constants.integerValueOf(1058), "21S01");
/*      */     
/*  452 */     mysqlToSql99State.put(Constants.integerValueOf(1059), "42000");
/*      */     
/*  454 */     mysqlToSql99State.put(Constants.integerValueOf(1060), "42S21");
/*      */     
/*  456 */     mysqlToSql99State.put(Constants.integerValueOf(1061), "42000");
/*      */     
/*  458 */     mysqlToSql99State.put(Constants.integerValueOf(1062), "23000");
/*      */     
/*  460 */     mysqlToSql99State.put(Constants.integerValueOf(1063), "42000");
/*      */     
/*  462 */     mysqlToSql99State.put(Constants.integerValueOf(1064), "42000");
/*      */     
/*  464 */     mysqlToSql99State.put(Constants.integerValueOf(1065), "42000");
/*      */     
/*  466 */     mysqlToSql99State.put(Constants.integerValueOf(1066), "42000");
/*      */     
/*  468 */     mysqlToSql99State.put(Constants.integerValueOf(1067), "42000");
/*      */     
/*  470 */     mysqlToSql99State.put(Constants.integerValueOf(1068), "42000");
/*      */     
/*  472 */     mysqlToSql99State.put(Constants.integerValueOf(1069), "42000");
/*      */     
/*  474 */     mysqlToSql99State.put(Constants.integerValueOf(1070), "42000");
/*      */     
/*  476 */     mysqlToSql99State.put(Constants.integerValueOf(1071), "42000");
/*      */     
/*  478 */     mysqlToSql99State.put(Constants.integerValueOf(1072), "42000");
/*      */     
/*  480 */     mysqlToSql99State.put(Constants.integerValueOf(1073), "42000");
/*      */     
/*  482 */     mysqlToSql99State.put(Constants.integerValueOf(1074), "42000");
/*      */     
/*  484 */     mysqlToSql99State.put(Constants.integerValueOf(1075), "42000");
/*      */     
/*  486 */     mysqlToSql99State.put(Constants.integerValueOf(1080), "08S01");
/*      */     
/*  488 */     mysqlToSql99State.put(Constants.integerValueOf(1081), "08S01");
/*      */     
/*  490 */     mysqlToSql99State.put(Constants.integerValueOf(1082), "42S12");
/*      */     
/*  492 */     mysqlToSql99State.put(Constants.integerValueOf(1083), "42000");
/*      */     
/*  494 */     mysqlToSql99State.put(Constants.integerValueOf(1084), "42000");
/*      */     
/*  496 */     mysqlToSql99State.put(Constants.integerValueOf(1090), "42000");
/*      */     
/*  498 */     mysqlToSql99State.put(Constants.integerValueOf(1091), "42000");
/*      */     
/*  500 */     mysqlToSql99State.put(Constants.integerValueOf(1101), "42000");
/*      */     
/*  502 */     mysqlToSql99State.put(Constants.integerValueOf(1102), "42000");
/*      */     
/*  504 */     mysqlToSql99State.put(Constants.integerValueOf(1103), "42000");
/*      */     
/*  506 */     mysqlToSql99State.put(Constants.integerValueOf(1104), "42000");
/*      */     
/*  508 */     mysqlToSql99State.put(Constants.integerValueOf(1106), "42000");
/*      */     
/*  510 */     mysqlToSql99State.put(Constants.integerValueOf(1107), "42000");
/*      */     
/*  512 */     mysqlToSql99State.put(Constants.integerValueOf(1109), "42S02");
/*      */     
/*  514 */     mysqlToSql99State.put(Constants.integerValueOf(1110), "42000");
/*      */     
/*  516 */     mysqlToSql99State.put(Constants.integerValueOf(1112), "42000");
/*      */     
/*  518 */     mysqlToSql99State.put(Constants.integerValueOf(1113), "42000");
/*      */     
/*  520 */     mysqlToSql99State.put(Constants.integerValueOf(1115), "42000");
/*      */     
/*  522 */     mysqlToSql99State.put(Constants.integerValueOf(1118), "42000");
/*      */     
/*  524 */     mysqlToSql99State.put(Constants.integerValueOf(1120), "42000");
/*      */     
/*  526 */     mysqlToSql99State.put(Constants.integerValueOf(1121), "42000");
/*      */     
/*  528 */     mysqlToSql99State.put(Constants.integerValueOf(1131), "42000");
/*      */     
/*  530 */     mysqlToSql99State.put(Constants.integerValueOf(1132), "42000");
/*      */     
/*  532 */     mysqlToSql99State.put(Constants.integerValueOf(1133), "42000");
/*      */     
/*  534 */     mysqlToSql99State.put(Constants.integerValueOf(1136), "21S01");
/*      */     
/*  536 */     mysqlToSql99State.put(Constants.integerValueOf(1138), "42000");
/*      */     
/*  538 */     mysqlToSql99State.put(Constants.integerValueOf(1139), "42000");
/*      */     
/*  540 */     mysqlToSql99State.put(Constants.integerValueOf(1140), "42000");
/*      */     
/*  542 */     mysqlToSql99State.put(Constants.integerValueOf(1141), "42000");
/*      */     
/*  544 */     mysqlToSql99State.put(Constants.integerValueOf(1142), "42000");
/*      */     
/*  546 */     mysqlToSql99State.put(Constants.integerValueOf(1143), "42000");
/*      */     
/*  548 */     mysqlToSql99State.put(Constants.integerValueOf(1144), "42000");
/*      */     
/*  550 */     mysqlToSql99State.put(Constants.integerValueOf(1145), "42000");
/*      */     
/*  552 */     mysqlToSql99State.put(Constants.integerValueOf(1146), "42S02");
/*      */     
/*  554 */     mysqlToSql99State.put(Constants.integerValueOf(1147), "42000");
/*      */     
/*  556 */     mysqlToSql99State.put(Constants.integerValueOf(1148), "42000");
/*      */     
/*  558 */     mysqlToSql99State.put(Constants.integerValueOf(1149), "42000");
/*      */     
/*  560 */     mysqlToSql99State.put(Constants.integerValueOf(1152), "08S01");
/*      */     
/*  562 */     mysqlToSql99State.put(Constants.integerValueOf(1153), "08S01");
/*      */     
/*  564 */     mysqlToSql99State.put(Constants.integerValueOf(1154), "08S01");
/*      */     
/*  566 */     mysqlToSql99State.put(Constants.integerValueOf(1155), "08S01");
/*      */     
/*  568 */     mysqlToSql99State.put(Constants.integerValueOf(1156), "08S01");
/*      */     
/*  570 */     mysqlToSql99State.put(Constants.integerValueOf(1157), "08S01");
/*      */     
/*  572 */     mysqlToSql99State.put(Constants.integerValueOf(1158), "08S01");
/*      */     
/*  574 */     mysqlToSql99State.put(Constants.integerValueOf(1159), "08S01");
/*      */     
/*  576 */     mysqlToSql99State.put(Constants.integerValueOf(1160), "08S01");
/*      */     
/*  578 */     mysqlToSql99State.put(Constants.integerValueOf(1161), "08S01");
/*      */     
/*  580 */     mysqlToSql99State.put(Constants.integerValueOf(1162), "42000");
/*      */     
/*  582 */     mysqlToSql99State.put(Constants.integerValueOf(1163), "42000");
/*      */     
/*  584 */     mysqlToSql99State.put(Constants.integerValueOf(1164), "42000");
/*      */ 
/*      */ 
/*      */     
/*  588 */     mysqlToSql99State.put(Constants.integerValueOf(1166), "42000");
/*      */     
/*  590 */     mysqlToSql99State.put(Constants.integerValueOf(1167), "42000");
/*      */     
/*  592 */     mysqlToSql99State.put(Constants.integerValueOf(1169), "23000");
/*      */     
/*  594 */     mysqlToSql99State.put(Constants.integerValueOf(1170), "42000");
/*      */     
/*  596 */     mysqlToSql99State.put(Constants.integerValueOf(1171), "42000");
/*      */     
/*  598 */     mysqlToSql99State.put(Constants.integerValueOf(1172), "42000");
/*      */     
/*  600 */     mysqlToSql99State.put(Constants.integerValueOf(1173), "42000");
/*      */     
/*  602 */     mysqlToSql99State.put(Constants.integerValueOf(1177), "42000");
/*      */     
/*  604 */     mysqlToSql99State.put(Constants.integerValueOf(1178), "42000");
/*      */     
/*  606 */     mysqlToSql99State.put(Constants.integerValueOf(1179), "25000");
/*      */ 
/*      */     
/*  609 */     mysqlToSql99State.put(Constants.integerValueOf(1184), "08S01");
/*      */     
/*  611 */     mysqlToSql99State.put(Constants.integerValueOf(1189), "08S01");
/*      */     
/*  613 */     mysqlToSql99State.put(Constants.integerValueOf(1190), "08S01");
/*      */     
/*  615 */     mysqlToSql99State.put(Constants.integerValueOf(1203), "42000");
/*      */     
/*  617 */     mysqlToSql99State.put(Constants.integerValueOf(1207), "25000");
/*      */     
/*  619 */     mysqlToSql99State.put(Constants.integerValueOf(1211), "42000");
/*      */     
/*  621 */     mysqlToSql99State.put(Constants.integerValueOf(1213), "40001");
/*      */     
/*  623 */     mysqlToSql99State.put(Constants.integerValueOf(1216), "23000");
/*      */     
/*  625 */     mysqlToSql99State.put(Constants.integerValueOf(1217), "23000");
/*      */     
/*  627 */     mysqlToSql99State.put(Constants.integerValueOf(1218), "08S01");
/*      */     
/*  629 */     mysqlToSql99State.put(Constants.integerValueOf(1222), "21000");
/*      */ 
/*      */     
/*  632 */     mysqlToSql99State.put(Constants.integerValueOf(1226), "42000");
/*      */     
/*  634 */     mysqlToSql99State.put(Constants.integerValueOf(1230), "42000");
/*      */     
/*  636 */     mysqlToSql99State.put(Constants.integerValueOf(1231), "42000");
/*      */     
/*  638 */     mysqlToSql99State.put(Constants.integerValueOf(1232), "42000");
/*      */     
/*  640 */     mysqlToSql99State.put(Constants.integerValueOf(1234), "42000");
/*      */     
/*  642 */     mysqlToSql99State.put(Constants.integerValueOf(1235), "42000");
/*      */     
/*  644 */     mysqlToSql99State.put(Constants.integerValueOf(1239), "42000");
/*      */     
/*  646 */     mysqlToSql99State.put(Constants.integerValueOf(1241), "21000");
/*      */     
/*  648 */     mysqlToSql99State.put(Constants.integerValueOf(1242), "21000");
/*      */     
/*  650 */     mysqlToSql99State.put(Constants.integerValueOf(1247), "42S22");
/*      */     
/*  652 */     mysqlToSql99State.put(Constants.integerValueOf(1248), "42000");
/*      */     
/*  654 */     mysqlToSql99State.put(Constants.integerValueOf(1249), "01000");
/*      */     
/*  656 */     mysqlToSql99State.put(Constants.integerValueOf(1250), "42000");
/*      */     
/*  658 */     mysqlToSql99State.put(Constants.integerValueOf(1251), "08004");
/*      */     
/*  660 */     mysqlToSql99State.put(Constants.integerValueOf(1252), "42000");
/*      */     
/*  662 */     mysqlToSql99State.put(Constants.integerValueOf(1253), "42000");
/*      */     
/*  664 */     mysqlToSql99State.put(Constants.integerValueOf(1261), "01000");
/*      */     
/*  666 */     mysqlToSql99State.put(Constants.integerValueOf(1262), "01000");
/*      */     
/*  668 */     mysqlToSql99State.put(Constants.integerValueOf(1263), "01000");
/*      */     
/*  670 */     mysqlToSql99State.put(Constants.integerValueOf(1264), "01000");
/*      */     
/*  672 */     mysqlToSql99State.put(Constants.integerValueOf(1265), "01000");
/*      */     
/*  674 */     mysqlToSql99State.put(Constants.integerValueOf(1280), "42000");
/*      */     
/*  676 */     mysqlToSql99State.put(Constants.integerValueOf(1281), "42000");
/*      */     
/*  678 */     mysqlToSql99State.put(Constants.integerValueOf(1286), "42000");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
/*      */ 
/*      */   
/*      */   private static final int DUE_TO_TIMEOUT_FALSE = 0;
/*      */   
/*      */   private static final int DUE_TO_TIMEOUT_MAYBE = 2;
/*      */   
/*      */   private static final int DUE_TO_TIMEOUT_TRUE = 1;
/*      */   
/*      */   private static final Constructor JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR;
/*      */   
/*      */   private static Method THROWABLE_INIT_CAUSE_METHOD;
/*      */ 
/*      */   
/*      */   static SQLWarning convertShowWarningsToSQLWarnings(Connection connection) throws SQLException {
/*  698 */     return convertShowWarningsToSQLWarnings(connection, 0, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static SQLWarning convertShowWarningsToSQLWarnings(Connection connection, int warningCountIfKnown, boolean forTruncationOnly) throws SQLException {
/*  723 */     Statement stmt = null;
/*  724 */     ResultSet warnRs = null;
/*      */     
/*  726 */     SQLWarning currentWarning = null;
/*      */     
/*      */     try {
/*  729 */       if (warningCountIfKnown < 100) {
/*  730 */         stmt = connection.createStatement();
/*      */         
/*  732 */         if (stmt.getMaxRows() != 0) {
/*  733 */           stmt.setMaxRows(0);
/*      */         }
/*      */       } else {
/*      */         
/*  737 */         stmt = connection.createStatement(1003, 1007);
/*      */ 
/*      */         
/*  740 */         stmt.setFetchSize(-2147483648);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  750 */       warnRs = stmt.executeQuery("SHOW WARNINGS");
/*      */       
/*  752 */       while (warnRs.next()) {
/*  753 */         int code = warnRs.getInt("Code");
/*      */         
/*  755 */         if (forTruncationOnly) {
/*  756 */           if (code == 1265 || code == 1264) {
/*  757 */             DataTruncation newTruncation = new MysqlDataTruncation(warnRs.getString("Message"), 0, false, false, 0, 0);
/*      */ 
/*      */             
/*  760 */             if (currentWarning == null) {
/*  761 */               currentWarning = newTruncation; continue;
/*      */             } 
/*  763 */             currentWarning.setNextWarning(newTruncation);
/*      */           } 
/*      */           continue;
/*      */         } 
/*  767 */         String level = warnRs.getString("Level");
/*  768 */         String message = warnRs.getString("Message");
/*      */         
/*  770 */         SQLWarning newWarning = new SQLWarning(message, mysqlToSqlState(code, connection.getUseSqlStateCodes()), code);
/*      */ 
/*      */ 
/*      */         
/*  774 */         if (currentWarning == null) {
/*  775 */           currentWarning = newWarning; continue;
/*      */         } 
/*  777 */         currentWarning.setNextWarning(newWarning);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  782 */       if (forTruncationOnly && currentWarning != null) {
/*  783 */         throw currentWarning;
/*      */       }
/*      */       
/*  786 */       return currentWarning;
/*      */     } finally {
/*  788 */       SQLException reThrow = null;
/*      */       
/*  790 */       if (warnRs != null) {
/*      */         try {
/*  792 */           warnRs.close();
/*  793 */         } catch (SQLException sqlEx) {
/*  794 */           reThrow = sqlEx;
/*      */         } 
/*      */       }
/*      */       
/*  798 */       if (stmt != null) {
/*      */         try {
/*  800 */           stmt.close();
/*  801 */         } catch (SQLException sqlEx) {
/*      */ 
/*      */ 
/*      */           
/*  805 */           reThrow = sqlEx;
/*      */         } 
/*      */       }
/*      */       
/*  809 */       if (reThrow != null) {
/*  810 */         throw reThrow;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void dumpSqlStatesMappingsAsXml() throws Exception {
/*  816 */     TreeMap allErrorNumbers = new TreeMap();
/*  817 */     Map mysqlErrorNumbersToNames = new HashMap();
/*      */     
/*  819 */     Integer errorNumber = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  825 */     Iterator iterator1 = mysqlToSql99State.keySet().iterator();
/*  826 */     while (iterator1.hasNext()) {
/*  827 */       errorNumber = iterator1.next();
/*  828 */       allErrorNumbers.put(errorNumber, errorNumber);
/*      */     } 
/*      */     
/*  831 */     Iterator mysqlErrorNumbers = mysqlToSqlState.keySet().iterator();
/*  832 */     while (mysqlErrorNumbers.hasNext()) {
/*  833 */       errorNumber = mysqlErrorNumbers.next();
/*  834 */       allErrorNumbers.put(errorNumber, errorNumber);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  840 */     Field[] possibleFields = MysqlErrorNumbers.class.getDeclaredFields();
/*      */ 
/*      */     
/*  843 */     for (int i = 0; i < possibleFields.length; i++) {
/*  844 */       String fieldName = possibleFields[i].getName();
/*      */       
/*  846 */       if (fieldName.startsWith("ER_")) {
/*  847 */         mysqlErrorNumbersToNames.put(possibleFields[i].get(null), fieldName);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  852 */     System.out.println("<ErrorMappings>");
/*      */     
/*  854 */     Iterator allErrorNumbersIter = allErrorNumbers.keySet().iterator();
/*  855 */     while (allErrorNumbersIter.hasNext()) {
/*  856 */       errorNumber = allErrorNumbersIter.next();
/*      */       
/*  858 */       String sql92State = mysqlToSql99(errorNumber.intValue());
/*  859 */       String oldSqlState = mysqlToXOpen(errorNumber.intValue());
/*      */       
/*  861 */       System.out.println("   <ErrorMapping mysqlErrorNumber=\"" + errorNumber + "\" mysqlErrorName=\"" + mysqlErrorNumbersToNames.get(errorNumber) + "\" legacySqlState=\"" + ((oldSqlState == null) ? "" : oldSqlState) + "\" sql92SqlState=\"" + ((sql92State == null) ? "" : sql92State) + "\"/>");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  870 */     System.out.println("</ErrorMappings>");
/*      */   }
/*      */   
/*      */   static String get(String stateCode) {
/*  874 */     return (String)sqlStateMessages.get(stateCode);
/*      */   }
/*      */   
/*      */   private static String mysqlToSql99(int errno) {
/*  878 */     Integer err = Constants.integerValueOf(errno);
/*      */     
/*  880 */     if (mysqlToSql99State.containsKey(err)) {
/*  881 */       return (String)mysqlToSql99State.get(err);
/*      */     }
/*      */     
/*  884 */     return "HY000";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String mysqlToSqlState(int errno, boolean useSql92States) {
/*  896 */     if (useSql92States) {
/*  897 */       return mysqlToSql99(errno);
/*      */     }
/*      */     
/*  900 */     return mysqlToXOpen(errno);
/*      */   }
/*      */   
/*      */   private static String mysqlToXOpen(int errno) {
/*  904 */     Integer err = Constants.integerValueOf(errno);
/*      */     
/*  906 */     if (mysqlToSqlState.containsKey(err)) {
/*  907 */       return (String)mysqlToSqlState.get(err);
/*      */     }
/*      */     
/*  910 */     return "S1000";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState) {
/*  926 */     return createSQLException(message, sqlState, 0);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message) {
/*  930 */     return new SQLException(message);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, Throwable cause) {
/*  934 */     if (THROWABLE_INIT_CAUSE_METHOD == null && 
/*  935 */       cause != null) {
/*  936 */       message = message + " due to " + cause.toString();
/*      */     }
/*      */ 
/*      */     
/*  940 */     SQLException sqlEx = createSQLException(message, sqlState);
/*      */     
/*  942 */     if (cause != null && THROWABLE_INIT_CAUSE_METHOD != null) {
/*      */       try {
/*  944 */         THROWABLE_INIT_CAUSE_METHOD.invoke(sqlEx, new Object[] { cause });
/*  945 */       } catch (Throwable t) {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  951 */     return sqlEx;
/*      */   }
/*      */ 
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode) {
/*  956 */     return createSQLException(message, sqlState, vendorErrorCode, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient) {
/*      */     try {
/*  962 */       if (sqlState != null) {
/*  963 */         if (sqlState.startsWith("08")) {
/*  964 */           if (isTransient) {
/*  965 */             if (!Util.isJdbc4()) {
/*  966 */               return (SQLException)new MySQLTransientConnectionException(message, sqlState, vendorErrorCode);
/*      */             }
/*      */ 
/*      */             
/*  970 */             return (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException", new Class[] { String.class, String.class, int.class }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) });
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  979 */           if (!Util.isJdbc4()) {
/*  980 */             return (SQLException)new MySQLNonTransientConnectionException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */ 
/*      */           
/*  984 */           return (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException", new Class[] { String.class, String.class, int.class }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) });
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  993 */         if (sqlState.startsWith("22")) {
/*  994 */           if (!Util.isJdbc4()) {
/*  995 */             return (SQLException)new MySQLDataException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */ 
/*      */           
/*  999 */           return (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLDataException", new Class[] { String.class, String.class, int.class }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) });
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1008 */         if (sqlState.startsWith("23")) {
/*      */           
/* 1010 */           if (!Util.isJdbc4()) {
/* 1011 */             return (SQLException)new MySQLIntegrityConstraintViolationException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */ 
/*      */           
/* 1015 */           return (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", new Class[] { String.class, String.class, int.class }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) });
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1024 */         if (sqlState.startsWith("42")) {
/* 1025 */           if (!Util.isJdbc4()) {
/* 1026 */             return (SQLException)new MySQLSyntaxErrorException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */ 
/*      */           
/* 1030 */           return (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException", new Class[] { String.class, String.class, int.class }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) });
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1039 */         if (sqlState.startsWith("40")) {
/* 1040 */           if (!Util.isJdbc4()) {
/* 1041 */             return (SQLException)new MySQLTransactionRollbackException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */ 
/*      */           
/* 1045 */           return (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException", new Class[] { String.class, String.class, int.class }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) });
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1055 */       return new SQLException(message, sqlState, vendorErrorCode);
/* 1056 */     } catch (SQLException sqlEx) {
/* 1057 */       return new SQLException("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString(sqlEx), "S1000");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SQLException createCommunicationsException(ConnectionImpl conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException) {
/* 1067 */     SQLException exToReturn = null;
/*      */     
/* 1069 */     if (!Util.isJdbc4()) {
/* 1070 */       exToReturn = new CommunicationsException(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException);
/*      */     } else {
/*      */       
/*      */       try {
/* 1074 */         exToReturn = (SQLException)Util.handleNewInstance(JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR, new Object[] { conn, Constants.longValueOf(lastPacketSentTimeMs), Constants.longValueOf(lastPacketReceivedTimeMs), underlyingException });
/*      */       }
/* 1076 */       catch (SQLException sqlEx) {
/*      */ 
/*      */         
/* 1079 */         return sqlEx;
/*      */       } 
/*      */     } 
/*      */     
/* 1083 */     if (THROWABLE_INIT_CAUSE_METHOD != null && underlyingException != null) {
/*      */       try {
/* 1085 */         THROWABLE_INIT_CAUSE_METHOD.invoke(exToReturn, new Object[] { underlyingException });
/* 1086 */       } catch (Throwable t) {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1092 */     return exToReturn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String createLinkFailureMessageBasedOnHeuristics(ConnectionImpl conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException, boolean streamingResultSetInPlay) {
/* 1112 */     long serverTimeoutSeconds = 0L;
/* 1113 */     boolean isInteractiveClient = false;
/*      */     
/* 1115 */     if (conn != null) {
/* 1116 */       isInteractiveClient = conn.getInteractiveClient();
/*      */       
/* 1118 */       String serverTimeoutSecondsStr = null;
/*      */       
/* 1120 */       if (isInteractiveClient) {
/* 1121 */         serverTimeoutSecondsStr = conn.getServerVariable("interactive_timeout");
/*      */       } else {
/*      */         
/* 1124 */         serverTimeoutSecondsStr = conn.getServerVariable("wait_timeout");
/*      */       } 
/*      */ 
/*      */       
/* 1128 */       if (serverTimeoutSecondsStr != null) {
/*      */         try {
/* 1130 */           serverTimeoutSeconds = Long.parseLong(serverTimeoutSecondsStr);
/*      */         }
/* 1132 */         catch (NumberFormatException nfe) {
/* 1133 */           serverTimeoutSeconds = 0L;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1138 */     StringBuffer exceptionMessageBuf = new StringBuffer();
/*      */     
/* 1140 */     if (lastPacketSentTimeMs == 0L) {
/* 1141 */       lastPacketSentTimeMs = System.currentTimeMillis();
/*      */     }
/*      */     
/* 1144 */     long timeSinceLastPacket = (System.currentTimeMillis() - lastPacketSentTimeMs) / 1000L;
/* 1145 */     long timeSinceLastPacketReceived = (System.currentTimeMillis() - lastPacketReceivedTimeMs) / 1000L;
/*      */     
/* 1147 */     int dueToTimeout = 0;
/*      */     
/* 1149 */     StringBuffer timeoutMessageBuf = null;
/*      */     
/* 1151 */     if (streamingResultSetInPlay) {
/* 1152 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.ClientWasStreaming"));
/*      */     } else {
/*      */       
/* 1155 */       if (serverTimeoutSeconds != 0L) {
/* 1156 */         if (timeSinceLastPacket > serverTimeoutSeconds) {
/* 1157 */           dueToTimeout = 1;
/*      */           
/* 1159 */           timeoutMessageBuf = new StringBuffer();
/*      */           
/* 1161 */           timeoutMessageBuf.append(Messages.getString("CommunicationsException.2"));
/*      */ 
/*      */           
/* 1164 */           if (!isInteractiveClient) {
/* 1165 */             timeoutMessageBuf.append(Messages.getString("CommunicationsException.3"));
/*      */           } else {
/*      */             
/* 1168 */             timeoutMessageBuf.append(Messages.getString("CommunicationsException.4"));
/*      */           }
/*      */         
/*      */         }
/*      */       
/* 1173 */       } else if (timeSinceLastPacket > 28800L) {
/* 1174 */         dueToTimeout = 2;
/*      */         
/* 1176 */         timeoutMessageBuf = new StringBuffer();
/*      */         
/* 1178 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.5"));
/*      */         
/* 1180 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.6"));
/*      */         
/* 1182 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.7"));
/*      */         
/* 1184 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.8"));
/*      */       } 
/*      */ 
/*      */       
/* 1188 */       if (dueToTimeout == 1 || dueToTimeout == 2) {
/*      */ 
/*      */         
/* 1191 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.9_1"));
/*      */         
/* 1193 */         exceptionMessageBuf.append(timeSinceLastPacketReceived);
/* 1194 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.9_2"));
/*      */ 
/*      */         
/* 1197 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.9"));
/*      */         
/* 1199 */         exceptionMessageBuf.append(timeSinceLastPacket);
/* 1200 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.10"));
/*      */ 
/*      */         
/* 1203 */         if (timeoutMessageBuf != null) {
/* 1204 */           exceptionMessageBuf.append(timeoutMessageBuf);
/*      */         }
/*      */         
/* 1207 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.11"));
/*      */         
/* 1209 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.12"));
/*      */         
/* 1211 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.13"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 1220 */       else if (underlyingException instanceof java.net.BindException) {
/* 1221 */         if (conn.getLocalSocketAddress() != null && !Util.interfaceExists(conn.getLocalSocketAddress())) {
/*      */ 
/*      */           
/* 1224 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.19a"));
/*      */         }
/*      */         else {
/*      */           
/* 1228 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.14"));
/*      */           
/* 1230 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.15"));
/*      */           
/* 1232 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.16"));
/*      */           
/* 1234 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.17"));
/*      */           
/* 1236 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.18"));
/*      */           
/* 1238 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.19"));
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1245 */     if (exceptionMessageBuf.length() == 0) {
/*      */       
/* 1247 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.20"));
/*      */ 
/*      */       
/* 1250 */       if (THROWABLE_INIT_CAUSE_METHOD == null && underlyingException != null) {
/*      */         
/* 1252 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.21"));
/*      */         
/* 1254 */         exceptionMessageBuf.append(Util.stackTraceToString(underlyingException));
/*      */       } 
/*      */ 
/*      */       
/* 1258 */       if (conn != null && conn.getMaintainTimeStats() && !conn.getParanoid()) {
/*      */         
/* 1260 */         exceptionMessageBuf.append("\n\nLast packet sent to the server was ");
/*      */         
/* 1262 */         exceptionMessageBuf.append(System.currentTimeMillis() - lastPacketSentTimeMs);
/*      */         
/* 1264 */         exceptionMessageBuf.append(" ms ago.");
/*      */       } 
/*      */     } 
/*      */     
/* 1268 */     return exceptionMessageBuf.toString();
/*      */   }
/*      */   
/*      */   public static SQLException notImplemented() {
/* 1272 */     if (Util.isJdbc4()) {
/*      */       try {
/* 1274 */         return (SQLException)Class.forName("java.sql.SQLFeatureNotSupportedException").newInstance();
/*      */       
/*      */       }
/* 1277 */       catch (Throwable t) {}
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1282 */     return new NotImplemented();
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\SQLError.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */