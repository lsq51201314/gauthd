/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
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
/*      */ public class CharsetMapping
/*      */ {
/*   48 */   private static final Properties CHARSET_CONFIG = new Properties();
/*      */ 
/*      */   
/*      */   public static final String[] INDEX_TO_CHARSET;
/*      */ 
/*      */   
/*      */   public static final String[] INDEX_TO_COLLATION;
/*      */ 
/*      */   
/*      */   private static final Map JAVA_TO_MYSQL_CHARSET_MAP;
/*      */ 
/*      */   
/*      */   private static final Map JAVA_UC_TO_MYSQL_CHARSET_MAP;
/*      */ 
/*      */   
/*      */   private static final Map ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP;
/*      */ 
/*      */   
/*      */   private static final Map MULTIBYTE_CHARSETS;
/*      */ 
/*      */   
/*      */   private static final Map MYSQL_TO_JAVA_CHARSET_MAP;
/*      */   
/*      */   private static final Map MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP;
/*      */   
/*      */   private static final String NOT_USED = "ISO8859_1";
/*      */   
/*      */   public static final Map STATIC_CHARSET_TO_NUM_BYTES_MAP;
/*      */ 
/*      */   
/*      */   static {
/*   79 */     HashMap tempNumBytesMap = new HashMap();
/*      */     
/*   81 */     tempNumBytesMap.put("big5", Constants.integerValueOf(2));
/*   82 */     tempNumBytesMap.put("dec8", Constants.integerValueOf(1));
/*   83 */     tempNumBytesMap.put("cp850", Constants.integerValueOf(1));
/*   84 */     tempNumBytesMap.put("hp8", Constants.integerValueOf(1));
/*   85 */     tempNumBytesMap.put("koi8r", Constants.integerValueOf(1));
/*   86 */     tempNumBytesMap.put("latin1", Constants.integerValueOf(1));
/*   87 */     tempNumBytesMap.put("latin2", Constants.integerValueOf(1));
/*   88 */     tempNumBytesMap.put("swe7", Constants.integerValueOf(1));
/*   89 */     tempNumBytesMap.put("ascii", Constants.integerValueOf(1));
/*   90 */     tempNumBytesMap.put("ujis", Constants.integerValueOf(3));
/*   91 */     tempNumBytesMap.put("sjis", Constants.integerValueOf(2));
/*   92 */     tempNumBytesMap.put("hebrew", Constants.integerValueOf(1));
/*   93 */     tempNumBytesMap.put("tis620", Constants.integerValueOf(1));
/*   94 */     tempNumBytesMap.put("euckr", Constants.integerValueOf(2));
/*   95 */     tempNumBytesMap.put("koi8u", Constants.integerValueOf(1));
/*   96 */     tempNumBytesMap.put("gb2312", Constants.integerValueOf(2));
/*   97 */     tempNumBytesMap.put("greek", Constants.integerValueOf(1));
/*   98 */     tempNumBytesMap.put("cp1250", Constants.integerValueOf(1));
/*   99 */     tempNumBytesMap.put("gbk", Constants.integerValueOf(2));
/*  100 */     tempNumBytesMap.put("latin5", Constants.integerValueOf(1));
/*  101 */     tempNumBytesMap.put("armscii8", Constants.integerValueOf(1));
/*  102 */     tempNumBytesMap.put("utf8", Constants.integerValueOf(3));
/*  103 */     tempNumBytesMap.put("ucs2", Constants.integerValueOf(2));
/*  104 */     tempNumBytesMap.put("cp866", Constants.integerValueOf(1));
/*  105 */     tempNumBytesMap.put("keybcs2", Constants.integerValueOf(1));
/*  106 */     tempNumBytesMap.put("macce", Constants.integerValueOf(1));
/*  107 */     tempNumBytesMap.put("macroman", Constants.integerValueOf(1));
/*  108 */     tempNumBytesMap.put("cp852", Constants.integerValueOf(1));
/*  109 */     tempNumBytesMap.put("latin7", Constants.integerValueOf(1));
/*  110 */     tempNumBytesMap.put("cp1251", Constants.integerValueOf(1));
/*  111 */     tempNumBytesMap.put("cp1256", Constants.integerValueOf(1));
/*  112 */     tempNumBytesMap.put("cp1257", Constants.integerValueOf(1));
/*  113 */     tempNumBytesMap.put("binary", Constants.integerValueOf(1));
/*  114 */     tempNumBytesMap.put("geostd8", Constants.integerValueOf(1));
/*  115 */     tempNumBytesMap.put("cp932", Constants.integerValueOf(2));
/*  116 */     tempNumBytesMap.put("eucjpms", Constants.integerValueOf(3));
/*      */     
/*  118 */     STATIC_CHARSET_TO_NUM_BYTES_MAP = Collections.unmodifiableMap(tempNumBytesMap);
/*      */ 
/*      */     
/*  121 */     CHARSET_CONFIG.setProperty("javaToMysqlMappings", "US-ASCII =\t\t\tusa7,US-ASCII =\t\t\t>4.1.0 ascii,Big5 = \t\t\t\tbig5,GBK = \t\t\t\tgbk,SJIS = \t\t\t\tsjis,EUC_CN = \t\t\tgb2312,EUC_JP = \t\t\tujis,EUC_JP_Solaris = \t>5.0.3 eucjpms,EUC_KR = \t\t\teuc_kr,EUC_KR = \t\t\t>4.1.0 euckr,ISO8859_1 =\t\t\t*latin1,ISO8859_1 =\t\t\tlatin1_de,ISO8859_1 =\t\t\tgerman1,ISO8859_1 =\t\t\tdanish,ISO8859_2 =\t\t\tlatin2,ISO8859_2 =\t\t\tczech,ISO8859_2 =\t\t\thungarian,ISO8859_2  =\t\tcroat,ISO8859_7  =\t\tgreek,ISO8859_7  =\t\tlatin7,ISO8859_8  = \t\thebrew,ISO8859_9  =\t\tlatin5,ISO8859_13 =\t\tlatvian,ISO8859_13 =\t\tlatvian1,ISO8859_13 =\t\testonia,Cp437 =             *>4.1.0 cp850,Cp437 =\t\t\t\tdos,Cp850 =\t\t\t\tcp850,Cp852 = \t\t\tcp852,Cp866 = \t\t\tcp866,KOI8_R = \t\t\tkoi8_ru,KOI8_R = \t\t\t>4.1.0 koi8r,TIS620 = \t\t\ttis620,Cp1250 = \t\t\tcp1250,Cp1250 = \t\t\twin1250,Cp1251 = \t\t\t*>4.1.0 cp1251,Cp1251 = \t\t\twin1251,Cp1251 = \t\t\tcp1251cias,Cp1251 = \t\t\tcp1251csas,Cp1256 = \t\t\tcp1256,Cp1251 = \t\t\twin1251ukr,Cp1252 =             latin1,Cp1257 = \t\t\tcp1257,MacRoman = \t\t\tmacroman,MacCentralEurope = \tmacce,UTF-8 = \t\tutf8,UnicodeBig = \tucs2,US-ASCII =\t\tbinary,Cp943 =        \tsjis,MS932 =\t\t\tsjis,MS932 =        \t>4.1.11 cp932,WINDOWS-31J =\tsjis,WINDOWS-31J = \t>4.1.11 cp932,CP932 =\t\t\tsjis,CP932 =\t\t\t*>4.1.11 cp932,SHIFT_JIS = \tsjis,ASCII =\t\t\tascii,LATIN5 =\t\tlatin5,LATIN7 =\t\tlatin7,HEBREW =\t\thebrew,GREEK =\t\t\tgreek,EUCKR =\t\t\teuckr,GB2312 =\t\tgb2312,LATIN2 =\t\tlatin2,UTF-16 = \t>5.2.0 utf16,UTF-32 = \t>5.2.0 utf32");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  198 */     HashMap javaToMysqlMap = new HashMap();
/*      */     
/*  200 */     populateMapWithKeyValuePairs("javaToMysqlMappings", javaToMysqlMap, true, false);
/*      */     
/*  202 */     JAVA_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(javaToMysqlMap);
/*      */     
/*  204 */     HashMap mysqlToJavaMap = new HashMap();
/*      */     
/*  206 */     Set keySet = JAVA_TO_MYSQL_CHARSET_MAP.keySet();
/*      */     
/*  208 */     Iterator javaCharsets = keySet.iterator();
/*      */     
/*  210 */     while (javaCharsets.hasNext()) {
/*  211 */       Object javaEncodingName = javaCharsets.next();
/*  212 */       List mysqlEncodingList = (List)JAVA_TO_MYSQL_CHARSET_MAP.get(javaEncodingName);
/*      */ 
/*      */       
/*  215 */       Iterator mysqlEncodings = mysqlEncodingList.iterator();
/*      */       
/*  217 */       String mysqlEncodingName = null;
/*      */       
/*  219 */       while (mysqlEncodings.hasNext()) {
/*  220 */         VersionedStringProperty mysqlProp = mysqlEncodings.next();
/*      */         
/*  222 */         mysqlEncodingName = mysqlProp.toString();
/*      */         
/*  224 */         mysqlToJavaMap.put(mysqlEncodingName, javaEncodingName);
/*  225 */         mysqlToJavaMap.put(mysqlEncodingName.toUpperCase(Locale.ENGLISH), javaEncodingName);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  231 */     mysqlToJavaMap.put("cp932", "Windows-31J");
/*  232 */     mysqlToJavaMap.put("CP932", "Windows-31J");
/*      */     
/*  234 */     MYSQL_TO_JAVA_CHARSET_MAP = Collections.unmodifiableMap(mysqlToJavaMap);
/*      */     
/*  236 */     TreeMap ucMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     
/*  238 */     Iterator javaNamesKeys = JAVA_TO_MYSQL_CHARSET_MAP.keySet().iterator();
/*      */     
/*  240 */     while (javaNamesKeys.hasNext()) {
/*  241 */       String key = javaNamesKeys.next();
/*      */       
/*  243 */       ucMap.put(key.toUpperCase(Locale.ENGLISH), JAVA_TO_MYSQL_CHARSET_MAP.get(key));
/*      */     } 
/*      */ 
/*      */     
/*  247 */     JAVA_UC_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(ucMap);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  253 */     HashMap tempMapMulti = new HashMap();
/*      */     
/*  255 */     CHARSET_CONFIG.setProperty("multibyteCharsets", "Big5 = \t\t\tbig5,GBK = \t\t\tgbk,SJIS = \t\t\tsjis,EUC_CN = \t\tgb2312,EUC_JP = \t\tujis,EUC_JP_Solaris = eucjpms,EUC_KR = \t\teuc_kr,EUC_KR = \t\t>4.1.0 euckr,Cp943 =        \tsjis,Cp943 = \t\tcp943,WINDOWS-31J =\tsjis,WINDOWS-31J = \tcp932,CP932 =\t\t\tcp932,MS932 =\t\t\tsjis,MS932 =        \tcp932,SHIFT_JIS = \tsjis,EUCKR =\t\t\teuckr,GB2312 =\t\tgb2312,UTF-8 = \t\tutf8,utf8 =          utf8,UnicodeBig = \tucs2");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  287 */     populateMapWithKeyValuePairs("multibyteCharsets", tempMapMulti, false, true);
/*      */ 
/*      */     
/*  290 */     MULTIBYTE_CHARSETS = Collections.unmodifiableMap(tempMapMulti);
/*      */     
/*  292 */     INDEX_TO_CHARSET = new String[255];
/*      */     
/*      */     try {
/*  295 */       INDEX_TO_CHARSET[1] = getJavaEncodingForMysqlEncoding("big5", null);
/*  296 */       INDEX_TO_CHARSET[2] = getJavaEncodingForMysqlEncoding("czech", null);
/*  297 */       INDEX_TO_CHARSET[3] = "ISO8859_1";
/*  298 */       INDEX_TO_CHARSET[4] = "ISO8859_1";
/*  299 */       INDEX_TO_CHARSET[5] = getJavaEncodingForMysqlEncoding("german1", null);
/*      */       
/*  301 */       INDEX_TO_CHARSET[6] = "ISO8859_1";
/*  302 */       INDEX_TO_CHARSET[7] = getJavaEncodingForMysqlEncoding("koi8_ru", null);
/*      */       
/*  304 */       INDEX_TO_CHARSET[8] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */       
/*  306 */       INDEX_TO_CHARSET[9] = getJavaEncodingForMysqlEncoding("latin2", null);
/*      */       
/*  308 */       INDEX_TO_CHARSET[10] = "ISO8859_1";
/*  309 */       INDEX_TO_CHARSET[11] = getJavaEncodingForMysqlEncoding("usa7", null);
/*  310 */       INDEX_TO_CHARSET[12] = getJavaEncodingForMysqlEncoding("ujis", null);
/*  311 */       INDEX_TO_CHARSET[13] = getJavaEncodingForMysqlEncoding("sjis", null);
/*  312 */       INDEX_TO_CHARSET[14] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */       
/*  314 */       INDEX_TO_CHARSET[15] = getJavaEncodingForMysqlEncoding("danish", null);
/*      */       
/*  316 */       INDEX_TO_CHARSET[16] = getJavaEncodingForMysqlEncoding("hebrew", null);
/*      */ 
/*      */       
/*  319 */       INDEX_TO_CHARSET[17] = "ISO8859_1";
/*      */       
/*  321 */       INDEX_TO_CHARSET[18] = getJavaEncodingForMysqlEncoding("tis620", null);
/*      */       
/*  323 */       INDEX_TO_CHARSET[19] = getJavaEncodingForMysqlEncoding("euc_kr", null);
/*      */       
/*  325 */       INDEX_TO_CHARSET[20] = getJavaEncodingForMysqlEncoding("estonia", null);
/*      */       
/*  327 */       INDEX_TO_CHARSET[21] = getJavaEncodingForMysqlEncoding("hungarian", null);
/*      */       
/*  329 */       INDEX_TO_CHARSET[22] = "KOI8_R";
/*  330 */       INDEX_TO_CHARSET[23] = getJavaEncodingForMysqlEncoding("win1251ukr", null);
/*      */       
/*  332 */       INDEX_TO_CHARSET[24] = getJavaEncodingForMysqlEncoding("gb2312", null);
/*      */       
/*  334 */       INDEX_TO_CHARSET[25] = getJavaEncodingForMysqlEncoding("greek", null);
/*      */       
/*  336 */       INDEX_TO_CHARSET[26] = getJavaEncodingForMysqlEncoding("win1250", null);
/*      */       
/*  338 */       INDEX_TO_CHARSET[27] = getJavaEncodingForMysqlEncoding("croat", null);
/*      */       
/*  340 */       INDEX_TO_CHARSET[28] = getJavaEncodingForMysqlEncoding("gbk", null);
/*  341 */       INDEX_TO_CHARSET[29] = getJavaEncodingForMysqlEncoding("cp1257", null);
/*      */       
/*  343 */       INDEX_TO_CHARSET[30] = getJavaEncodingForMysqlEncoding("latin5", null);
/*      */       
/*  345 */       INDEX_TO_CHARSET[31] = getJavaEncodingForMysqlEncoding("latin1_de", null);
/*      */       
/*  347 */       INDEX_TO_CHARSET[32] = "ISO8859_1";
/*  348 */       INDEX_TO_CHARSET[33] = getJavaEncodingForMysqlEncoding("utf8", null);
/*  349 */       INDEX_TO_CHARSET[34] = "Cp1250";
/*  350 */       INDEX_TO_CHARSET[35] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*  351 */       INDEX_TO_CHARSET[36] = getJavaEncodingForMysqlEncoding("cp866", null);
/*      */       
/*  353 */       INDEX_TO_CHARSET[37] = "Cp895";
/*  354 */       INDEX_TO_CHARSET[38] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */       
/*  356 */       INDEX_TO_CHARSET[39] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */       
/*  358 */       INDEX_TO_CHARSET[40] = "latin2";
/*  359 */       INDEX_TO_CHARSET[41] = getJavaEncodingForMysqlEncoding("latvian", null);
/*      */       
/*  361 */       INDEX_TO_CHARSET[42] = getJavaEncodingForMysqlEncoding("latvian1", null);
/*      */       
/*  363 */       INDEX_TO_CHARSET[43] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */       
/*  365 */       INDEX_TO_CHARSET[44] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */       
/*  367 */       INDEX_TO_CHARSET[45] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */       
/*  369 */       INDEX_TO_CHARSET[46] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */       
/*  371 */       INDEX_TO_CHARSET[47] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */       
/*  373 */       INDEX_TO_CHARSET[48] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */       
/*  375 */       INDEX_TO_CHARSET[49] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */       
/*  377 */       INDEX_TO_CHARSET[50] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */       
/*  379 */       INDEX_TO_CHARSET[51] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */       
/*  381 */       INDEX_TO_CHARSET[52] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */       
/*  383 */       INDEX_TO_CHARSET[53] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */       
/*  385 */       INDEX_TO_CHARSET[54] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */       
/*  387 */       INDEX_TO_CHARSET[55] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */       
/*  389 */       INDEX_TO_CHARSET[56] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */       
/*  391 */       INDEX_TO_CHARSET[57] = getJavaEncodingForMysqlEncoding("cp1256", null);
/*      */ 
/*      */       
/*  394 */       INDEX_TO_CHARSET[58] = "ISO8859_1";
/*  395 */       INDEX_TO_CHARSET[59] = "ISO8859_1";
/*  396 */       INDEX_TO_CHARSET[60] = "ISO8859_1";
/*  397 */       INDEX_TO_CHARSET[61] = "ISO8859_1";
/*  398 */       INDEX_TO_CHARSET[62] = "ISO8859_1";
/*      */       
/*  400 */       INDEX_TO_CHARSET[63] = getJavaEncodingForMysqlEncoding("binary", null);
/*      */       
/*  402 */       INDEX_TO_CHARSET[64] = "ISO8859_2";
/*  403 */       INDEX_TO_CHARSET[65] = getJavaEncodingForMysqlEncoding("ascii", null);
/*      */       
/*  405 */       INDEX_TO_CHARSET[66] = getJavaEncodingForMysqlEncoding("cp1250", null);
/*      */       
/*  407 */       INDEX_TO_CHARSET[67] = getJavaEncodingForMysqlEncoding("cp1256", null);
/*      */       
/*  409 */       INDEX_TO_CHARSET[68] = getJavaEncodingForMysqlEncoding("cp866", null);
/*      */       
/*  411 */       INDEX_TO_CHARSET[69] = "US-ASCII";
/*  412 */       INDEX_TO_CHARSET[70] = getJavaEncodingForMysqlEncoding("greek", null);
/*      */       
/*  414 */       INDEX_TO_CHARSET[71] = getJavaEncodingForMysqlEncoding("hebrew", null);
/*      */       
/*  416 */       INDEX_TO_CHARSET[72] = "US-ASCII";
/*  417 */       INDEX_TO_CHARSET[73] = "Cp895";
/*  418 */       INDEX_TO_CHARSET[74] = getJavaEncodingForMysqlEncoding("koi8r", null);
/*      */       
/*  420 */       INDEX_TO_CHARSET[75] = "KOI8_r";
/*      */       
/*  422 */       INDEX_TO_CHARSET[76] = "ISO8859_1";
/*      */       
/*  424 */       INDEX_TO_CHARSET[77] = getJavaEncodingForMysqlEncoding("latin2", null);
/*      */       
/*  426 */       INDEX_TO_CHARSET[78] = getJavaEncodingForMysqlEncoding("latin5", null);
/*      */       
/*  428 */       INDEX_TO_CHARSET[79] = getJavaEncodingForMysqlEncoding("latin7", null);
/*      */       
/*  430 */       INDEX_TO_CHARSET[80] = getJavaEncodingForMysqlEncoding("cp850", null);
/*      */       
/*  432 */       INDEX_TO_CHARSET[81] = getJavaEncodingForMysqlEncoding("cp852", null);
/*      */       
/*  434 */       INDEX_TO_CHARSET[82] = "ISO8859_1";
/*  435 */       INDEX_TO_CHARSET[83] = getJavaEncodingForMysqlEncoding("utf8", null);
/*  436 */       INDEX_TO_CHARSET[84] = getJavaEncodingForMysqlEncoding("big5", null);
/*  437 */       INDEX_TO_CHARSET[85] = getJavaEncodingForMysqlEncoding("euckr", null);
/*      */       
/*  439 */       INDEX_TO_CHARSET[86] = getJavaEncodingForMysqlEncoding("gb2312", null);
/*      */       
/*  441 */       INDEX_TO_CHARSET[87] = getJavaEncodingForMysqlEncoding("gbk", null);
/*  442 */       INDEX_TO_CHARSET[88] = getJavaEncodingForMysqlEncoding("sjis", null);
/*  443 */       INDEX_TO_CHARSET[89] = getJavaEncodingForMysqlEncoding("tis620", null);
/*      */       
/*  445 */       INDEX_TO_CHARSET[90] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*  446 */       INDEX_TO_CHARSET[91] = getJavaEncodingForMysqlEncoding("ujis", null);
/*  447 */       INDEX_TO_CHARSET[92] = "US-ASCII";
/*  448 */       INDEX_TO_CHARSET[93] = "US-ASCII";
/*  449 */       INDEX_TO_CHARSET[94] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */       
/*  451 */       INDEX_TO_CHARSET[95] = getJavaEncodingForMysqlEncoding("cp932", null);
/*      */       
/*  453 */       INDEX_TO_CHARSET[96] = getJavaEncodingForMysqlEncoding("cp932", null);
/*      */       
/*  455 */       INDEX_TO_CHARSET[97] = getJavaEncodingForMysqlEncoding("eucjpms", null);
/*      */       
/*  457 */       INDEX_TO_CHARSET[98] = getJavaEncodingForMysqlEncoding("eucjpms", null);
/*      */       
/*      */       int j;
/*  460 */       for (j = 99; j < 128; j++) {
/*  461 */         INDEX_TO_CHARSET[j] = "ISO8859_1";
/*      */       }
/*      */       
/*  464 */       INDEX_TO_CHARSET[128] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  466 */       INDEX_TO_CHARSET[129] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  468 */       INDEX_TO_CHARSET[130] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  470 */       INDEX_TO_CHARSET[131] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  472 */       INDEX_TO_CHARSET[132] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  474 */       INDEX_TO_CHARSET[133] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  476 */       INDEX_TO_CHARSET[134] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  478 */       INDEX_TO_CHARSET[135] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  480 */       INDEX_TO_CHARSET[136] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  482 */       INDEX_TO_CHARSET[137] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  484 */       INDEX_TO_CHARSET[138] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  486 */       INDEX_TO_CHARSET[139] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  488 */       INDEX_TO_CHARSET[140] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  490 */       INDEX_TO_CHARSET[141] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  492 */       INDEX_TO_CHARSET[142] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  494 */       INDEX_TO_CHARSET[143] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  496 */       INDEX_TO_CHARSET[144] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  498 */       INDEX_TO_CHARSET[145] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */       
/*  500 */       INDEX_TO_CHARSET[146] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*      */       
/*  503 */       for (j = 147; j < 192; j++) {
/*  504 */         INDEX_TO_CHARSET[j] = "ISO8859_1";
/*      */       }
/*      */       
/*  507 */       INDEX_TO_CHARSET[192] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  509 */       INDEX_TO_CHARSET[193] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  511 */       INDEX_TO_CHARSET[194] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  513 */       INDEX_TO_CHARSET[195] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  515 */       INDEX_TO_CHARSET[196] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  517 */       INDEX_TO_CHARSET[197] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  519 */       INDEX_TO_CHARSET[198] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  521 */       INDEX_TO_CHARSET[199] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  523 */       INDEX_TO_CHARSET[200] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  525 */       INDEX_TO_CHARSET[201] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  527 */       INDEX_TO_CHARSET[202] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  529 */       INDEX_TO_CHARSET[203] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  531 */       INDEX_TO_CHARSET[204] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  533 */       INDEX_TO_CHARSET[205] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  535 */       INDEX_TO_CHARSET[206] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  537 */       INDEX_TO_CHARSET[207] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  539 */       INDEX_TO_CHARSET[208] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  541 */       INDEX_TO_CHARSET[209] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       
/*  543 */       INDEX_TO_CHARSET[210] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*      */       
/*  546 */       INDEX_TO_CHARSET[211] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*      */       
/*  549 */       for (j = 212; j < 224; j++) {
/*  550 */         INDEX_TO_CHARSET[j] = "ISO8859_1";
/*      */       }
/*      */       
/*  553 */       for (j = 224; j <= 243; j++) {
/*  554 */         INDEX_TO_CHARSET[j] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       }
/*      */ 
/*      */       
/*  558 */       for (j = 101; j <= 120; j++) {
/*  559 */         INDEX_TO_CHARSET[j] = getJavaEncodingForMysqlEncoding("utf16", null);
/*      */       }
/*      */ 
/*      */       
/*  563 */       for (j = 160; j <= 179; j++) {
/*  564 */         INDEX_TO_CHARSET[j] = getJavaEncodingForMysqlEncoding("utf32", null);
/*      */       }
/*      */ 
/*      */       
/*  568 */       for (j = 244; j < 254; j++) {
/*  569 */         INDEX_TO_CHARSET[j] = "ISO8859_1";
/*      */       }
/*      */       
/*  572 */       INDEX_TO_CHARSET[254] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  577 */       for (j = 1; j < INDEX_TO_CHARSET.length; j++) {
/*  578 */         if (INDEX_TO_CHARSET[j] == null) {
/*  579 */           throw new RuntimeException("Assertion failure: No mapping from charset index " + j + " to a Java character set");
/*      */         }
/*      */       } 
/*  582 */     } catch (SQLException sqlEx) {}
/*      */ 
/*      */ 
/*      */     
/*  586 */     INDEX_TO_COLLATION = new String[255];
/*      */     
/*  588 */     INDEX_TO_COLLATION[1] = "big5_chinese_ci";
/*  589 */     INDEX_TO_COLLATION[2] = "latin2_czech_cs";
/*  590 */     INDEX_TO_COLLATION[3] = "dec8_swedish_ci";
/*  591 */     INDEX_TO_COLLATION[4] = "cp850_general_ci";
/*  592 */     INDEX_TO_COLLATION[5] = "latin1_german1_ci";
/*  593 */     INDEX_TO_COLLATION[6] = "hp8_english_ci";
/*  594 */     INDEX_TO_COLLATION[7] = "koi8r_general_ci";
/*  595 */     INDEX_TO_COLLATION[8] = "latin1_swedish_ci";
/*  596 */     INDEX_TO_COLLATION[9] = "latin2_general_ci";
/*  597 */     INDEX_TO_COLLATION[10] = "swe7_swedish_ci";
/*  598 */     INDEX_TO_COLLATION[11] = "ascii_general_ci";
/*  599 */     INDEX_TO_COLLATION[12] = "ujis_japanese_ci";
/*  600 */     INDEX_TO_COLLATION[13] = "sjis_japanese_ci";
/*  601 */     INDEX_TO_COLLATION[14] = "cp1251_bulgarian_ci";
/*  602 */     INDEX_TO_COLLATION[15] = "latin1_danish_ci";
/*  603 */     INDEX_TO_COLLATION[16] = "hebrew_general_ci";
/*  604 */     INDEX_TO_COLLATION[18] = "tis620_thai_ci";
/*  605 */     INDEX_TO_COLLATION[19] = "euckr_korean_ci";
/*  606 */     INDEX_TO_COLLATION[20] = "latin7_estonian_cs";
/*  607 */     INDEX_TO_COLLATION[21] = "latin2_hungarian_ci";
/*  608 */     INDEX_TO_COLLATION[22] = "koi8u_general_ci";
/*  609 */     INDEX_TO_COLLATION[23] = "cp1251_ukrainian_ci";
/*  610 */     INDEX_TO_COLLATION[24] = "gb2312_chinese_ci";
/*  611 */     INDEX_TO_COLLATION[25] = "greek_general_ci";
/*  612 */     INDEX_TO_COLLATION[26] = "cp1250_general_ci";
/*  613 */     INDEX_TO_COLLATION[27] = "latin2_croatian_ci";
/*  614 */     INDEX_TO_COLLATION[28] = "gbk_chinese_ci";
/*  615 */     INDEX_TO_COLLATION[29] = "cp1257_lithuanian_ci";
/*  616 */     INDEX_TO_COLLATION[30] = "latin5_turkish_ci";
/*  617 */     INDEX_TO_COLLATION[31] = "latin1_german2_ci";
/*  618 */     INDEX_TO_COLLATION[32] = "armscii8_general_ci";
/*  619 */     INDEX_TO_COLLATION[33] = "utf8_general_ci";
/*  620 */     INDEX_TO_COLLATION[34] = "cp1250_czech_cs";
/*  621 */     INDEX_TO_COLLATION[35] = "ucs2_general_ci";
/*  622 */     INDEX_TO_COLLATION[36] = "cp866_general_ci";
/*  623 */     INDEX_TO_COLLATION[37] = "keybcs2_general_ci";
/*  624 */     INDEX_TO_COLLATION[38] = "macce_general_ci";
/*  625 */     INDEX_TO_COLLATION[39] = "macroman_general_ci";
/*  626 */     INDEX_TO_COLLATION[40] = "cp852_general_ci";
/*  627 */     INDEX_TO_COLLATION[41] = "latin7_general_ci";
/*  628 */     INDEX_TO_COLLATION[42] = "latin7_general_cs";
/*  629 */     INDEX_TO_COLLATION[43] = "macce_bin";
/*  630 */     INDEX_TO_COLLATION[44] = "cp1250_croatian_ci";
/*  631 */     INDEX_TO_COLLATION[47] = "latin1_bin";
/*  632 */     INDEX_TO_COLLATION[48] = "latin1_general_ci";
/*  633 */     INDEX_TO_COLLATION[49] = "latin1_general_cs";
/*  634 */     INDEX_TO_COLLATION[50] = "cp1251_bin";
/*  635 */     INDEX_TO_COLLATION[51] = "cp1251_general_ci";
/*  636 */     INDEX_TO_COLLATION[52] = "cp1251_general_cs";
/*  637 */     INDEX_TO_COLLATION[53] = "macroman_bin";
/*  638 */     INDEX_TO_COLLATION[57] = "cp1256_general_ci";
/*  639 */     INDEX_TO_COLLATION[58] = "cp1257_bin";
/*  640 */     INDEX_TO_COLLATION[59] = "cp1257_general_ci";
/*  641 */     INDEX_TO_COLLATION[63] = "binary";
/*  642 */     INDEX_TO_COLLATION[64] = "armscii8_bin";
/*  643 */     INDEX_TO_COLLATION[65] = "ascii_bin";
/*  644 */     INDEX_TO_COLLATION[66] = "cp1250_bin";
/*  645 */     INDEX_TO_COLLATION[67] = "cp1256_bin";
/*  646 */     INDEX_TO_COLLATION[68] = "cp866_bin";
/*  647 */     INDEX_TO_COLLATION[69] = "dec8_bin";
/*  648 */     INDEX_TO_COLLATION[70] = "greek_bin";
/*  649 */     INDEX_TO_COLLATION[71] = "hebrew_bin";
/*  650 */     INDEX_TO_COLLATION[72] = "hp8_bin";
/*  651 */     INDEX_TO_COLLATION[73] = "keybcs2_bin";
/*  652 */     INDEX_TO_COLLATION[74] = "koi8r_bin";
/*  653 */     INDEX_TO_COLLATION[75] = "koi8u_bin";
/*  654 */     INDEX_TO_COLLATION[77] = "latin2_bin";
/*  655 */     INDEX_TO_COLLATION[78] = "latin5_bin";
/*  656 */     INDEX_TO_COLLATION[79] = "latin7_bin";
/*  657 */     INDEX_TO_COLLATION[80] = "cp850_bin";
/*  658 */     INDEX_TO_COLLATION[81] = "cp852_bin";
/*  659 */     INDEX_TO_COLLATION[82] = "swe7_bin";
/*  660 */     INDEX_TO_COLLATION[83] = "utf8_bin";
/*  661 */     INDEX_TO_COLLATION[84] = "big5_bin";
/*  662 */     INDEX_TO_COLLATION[85] = "euckr_bin";
/*  663 */     INDEX_TO_COLLATION[86] = "gb2312_bin";
/*  664 */     INDEX_TO_COLLATION[87] = "gbk_bin";
/*  665 */     INDEX_TO_COLLATION[88] = "sjis_bin";
/*  666 */     INDEX_TO_COLLATION[89] = "tis620_bin";
/*  667 */     INDEX_TO_COLLATION[90] = "ucs2_bin";
/*  668 */     INDEX_TO_COLLATION[91] = "ujis_bin";
/*  669 */     INDEX_TO_COLLATION[92] = "geostd8_general_ci";
/*  670 */     INDEX_TO_COLLATION[93] = "geostd8_bin";
/*  671 */     INDEX_TO_COLLATION[94] = "latin1_spanish_ci";
/*  672 */     INDEX_TO_COLLATION[95] = "cp932_japanese_ci";
/*  673 */     INDEX_TO_COLLATION[96] = "cp932_bin";
/*  674 */     INDEX_TO_COLLATION[97] = "eucjpms_japanese_ci";
/*  675 */     INDEX_TO_COLLATION[98] = "eucjpms_bin";
/*  676 */     INDEX_TO_COLLATION[99] = "cp1250_polish_ci";
/*  677 */     INDEX_TO_COLLATION[128] = "ucs2_unicode_ci";
/*  678 */     INDEX_TO_COLLATION[129] = "ucs2_icelandic_ci";
/*  679 */     INDEX_TO_COLLATION[130] = "ucs2_latvian_ci";
/*  680 */     INDEX_TO_COLLATION[131] = "ucs2_romanian_ci";
/*  681 */     INDEX_TO_COLLATION[132] = "ucs2_slovenian_ci";
/*  682 */     INDEX_TO_COLLATION[133] = "ucs2_polish_ci";
/*  683 */     INDEX_TO_COLLATION[134] = "ucs2_estonian_ci";
/*  684 */     INDEX_TO_COLLATION[135] = "ucs2_spanish_ci";
/*  685 */     INDEX_TO_COLLATION[136] = "ucs2_swedish_ci";
/*  686 */     INDEX_TO_COLLATION[137] = "ucs2_turkish_ci";
/*  687 */     INDEX_TO_COLLATION[138] = "ucs2_czech_ci";
/*  688 */     INDEX_TO_COLLATION[139] = "ucs2_danish_ci";
/*  689 */     INDEX_TO_COLLATION[140] = "ucs2_lithuanian_ci ";
/*  690 */     INDEX_TO_COLLATION[141] = "ucs2_slovak_ci";
/*  691 */     INDEX_TO_COLLATION[142] = "ucs2_spanish2_ci";
/*  692 */     INDEX_TO_COLLATION[143] = "ucs2_roman_ci";
/*  693 */     INDEX_TO_COLLATION[144] = "ucs2_persian_ci";
/*  694 */     INDEX_TO_COLLATION[145] = "ucs2_esperanto_ci";
/*  695 */     INDEX_TO_COLLATION[146] = "ucs2_hungarian_ci";
/*  696 */     INDEX_TO_COLLATION[192] = "utf8_unicode_ci";
/*  697 */     INDEX_TO_COLLATION[193] = "utf8_icelandic_ci";
/*  698 */     INDEX_TO_COLLATION[194] = "utf8_latvian_ci";
/*  699 */     INDEX_TO_COLLATION[195] = "utf8_romanian_ci";
/*  700 */     INDEX_TO_COLLATION[196] = "utf8_slovenian_ci";
/*  701 */     INDEX_TO_COLLATION[197] = "utf8_polish_ci";
/*  702 */     INDEX_TO_COLLATION[198] = "utf8_estonian_ci";
/*  703 */     INDEX_TO_COLLATION[199] = "utf8_spanish_ci";
/*  704 */     INDEX_TO_COLLATION[200] = "utf8_swedish_ci";
/*  705 */     INDEX_TO_COLLATION[201] = "utf8_turkish_ci";
/*  706 */     INDEX_TO_COLLATION[202] = "utf8_czech_ci";
/*  707 */     INDEX_TO_COLLATION[203] = "utf8_danish_ci";
/*  708 */     INDEX_TO_COLLATION[204] = "utf8_lithuanian_ci ";
/*  709 */     INDEX_TO_COLLATION[205] = "utf8_slovak_ci";
/*  710 */     INDEX_TO_COLLATION[206] = "utf8_spanish2_ci";
/*  711 */     INDEX_TO_COLLATION[207] = "utf8_roman_ci";
/*  712 */     INDEX_TO_COLLATION[208] = "utf8_persian_ci";
/*  713 */     INDEX_TO_COLLATION[209] = "utf8_esperanto_ci";
/*  714 */     INDEX_TO_COLLATION[210] = "utf8_hungarian_ci";
/*      */ 
/*      */ 
/*      */     
/*  718 */     INDEX_TO_COLLATION[33] = "utf8mb3_general_ci";
/*  719 */     INDEX_TO_COLLATION[83] = "utf8mb3_bin";
/*  720 */     INDEX_TO_COLLATION[192] = "utf8mb3_unicode_ci";
/*  721 */     INDEX_TO_COLLATION[193] = "utf8mb3_icelandic_ci";
/*  722 */     INDEX_TO_COLLATION[194] = "utf8mb3_latvian_ci";
/*  723 */     INDEX_TO_COLLATION[195] = "utf8mb3_romanian_ci";
/*  724 */     INDEX_TO_COLLATION[196] = "utf8mb3_slovenian_ci";
/*  725 */     INDEX_TO_COLLATION[197] = "utf8mb3_polish_ci";
/*  726 */     INDEX_TO_COLLATION[198] = "utf8mb3_estonian_ci";
/*  727 */     INDEX_TO_COLLATION[199] = "utf8mb3_spanish_ci";
/*  728 */     INDEX_TO_COLLATION[200] = "utf8mb3_swedish_ci";
/*  729 */     INDEX_TO_COLLATION[201] = "utf8mb3_turkish_ci";
/*  730 */     INDEX_TO_COLLATION[202] = "utf8mb3_czech_ci";
/*  731 */     INDEX_TO_COLLATION[203] = "utf8mb3_danish_ci";
/*  732 */     INDEX_TO_COLLATION[204] = "utf8mb3_lithuanian_ci";
/*  733 */     INDEX_TO_COLLATION[205] = "utf8mb3_slovak_ci";
/*  734 */     INDEX_TO_COLLATION[206] = "utf8mb3_spanish2_ci";
/*  735 */     INDEX_TO_COLLATION[207] = "utf8mb3_roman_ci";
/*  736 */     INDEX_TO_COLLATION[208] = "utf8mb3_persian_ci";
/*  737 */     INDEX_TO_COLLATION[209] = "utf8mb3_esperanto_ci";
/*  738 */     INDEX_TO_COLLATION[210] = "utf8mb3_hungarian_ci";
/*  739 */     INDEX_TO_COLLATION[211] = "utf8mb3_sinhala_ci";
/*  740 */     INDEX_TO_COLLATION[254] = "utf8mb3_general_cs";
/*      */     
/*  742 */     INDEX_TO_COLLATION[45] = "utf8_general_ci";
/*  743 */     INDEX_TO_COLLATION[46] = "utf8_bin";
/*  744 */     INDEX_TO_COLLATION[224] = "utf8_unicode_ci";
/*  745 */     INDEX_TO_COLLATION[225] = "utf8_icelandic_ci";
/*  746 */     INDEX_TO_COLLATION[226] = "utf8_latvian_ci";
/*  747 */     INDEX_TO_COLLATION[227] = "utf8_romanian_ci";
/*  748 */     INDEX_TO_COLLATION[228] = "utf8_slovenian_ci";
/*  749 */     INDEX_TO_COLLATION[229] = "utf8_polish_ci";
/*  750 */     INDEX_TO_COLLATION[230] = "utf8_estonian_ci";
/*  751 */     INDEX_TO_COLLATION[231] = "utf8_spanish_ci";
/*  752 */     INDEX_TO_COLLATION[232] = "utf8_swedish_ci";
/*  753 */     INDEX_TO_COLLATION[233] = "utf8_turkish_ci";
/*  754 */     INDEX_TO_COLLATION[234] = "utf8_czech_ci";
/*  755 */     INDEX_TO_COLLATION[235] = "utf8_danish_ci";
/*  756 */     INDEX_TO_COLLATION[236] = "utf8_lithuanian_ci";
/*  757 */     INDEX_TO_COLLATION[237] = "utf8_slovak_ci";
/*  758 */     INDEX_TO_COLLATION[238] = "utf8_spanish2_ci";
/*  759 */     INDEX_TO_COLLATION[239] = "utf8_roman_ci";
/*  760 */     INDEX_TO_COLLATION[240] = "utf8_persian_ci";
/*  761 */     INDEX_TO_COLLATION[241] = "utf8_esperanto_ci";
/*  762 */     INDEX_TO_COLLATION[242] = "utf8_hungarian_ci";
/*  763 */     INDEX_TO_COLLATION[243] = "utf8_sinhala_ci";
/*      */     
/*  765 */     INDEX_TO_COLLATION[54] = "utf16_general_ci";
/*  766 */     INDEX_TO_COLLATION[55] = "utf16_bin";
/*  767 */     INDEX_TO_COLLATION[101] = "utf16_unicode_ci";
/*  768 */     INDEX_TO_COLLATION[102] = "utf16_icelandic_ci";
/*  769 */     INDEX_TO_COLLATION[103] = "utf16_latvian_ci";
/*  770 */     INDEX_TO_COLLATION[104] = "utf16_romanian_ci";
/*  771 */     INDEX_TO_COLLATION[105] = "utf16_slovenian_ci";
/*  772 */     INDEX_TO_COLLATION[106] = "utf16_polish_ci";
/*  773 */     INDEX_TO_COLLATION[107] = "utf16_estonian_ci";
/*  774 */     INDEX_TO_COLLATION[108] = "utf16_spanish_ci";
/*  775 */     INDEX_TO_COLLATION[109] = "utf16_swedish_ci";
/*  776 */     INDEX_TO_COLLATION[110] = "utf16_turkish_ci";
/*  777 */     INDEX_TO_COLLATION[111] = "utf16_czech_ci";
/*  778 */     INDEX_TO_COLLATION[112] = "utf16_danish_ci";
/*  779 */     INDEX_TO_COLLATION[113] = "utf16_lithuanian_ci";
/*  780 */     INDEX_TO_COLLATION[114] = "utf16_slovak_ci";
/*  781 */     INDEX_TO_COLLATION[115] = "utf16_spanish2_ci";
/*  782 */     INDEX_TO_COLLATION[116] = "utf16_roman_ci";
/*  783 */     INDEX_TO_COLLATION[117] = "utf16_persian_ci";
/*  784 */     INDEX_TO_COLLATION[118] = "utf16_esperanto_ci";
/*  785 */     INDEX_TO_COLLATION[119] = "utf16_hungarian_ci";
/*  786 */     INDEX_TO_COLLATION[120] = "utf16_sinhala_ci";
/*      */     
/*  788 */     INDEX_TO_COLLATION[60] = "utf32_general_ci";
/*  789 */     INDEX_TO_COLLATION[61] = "utf32_bin";
/*  790 */     INDEX_TO_COLLATION[160] = "utf32_unicode_ci";
/*  791 */     INDEX_TO_COLLATION[161] = "utf32_icelandic_ci";
/*  792 */     INDEX_TO_COLLATION[162] = "utf32_latvian_ci";
/*  793 */     INDEX_TO_COLLATION[163] = "utf32_romanian_ci";
/*  794 */     INDEX_TO_COLLATION[164] = "utf32_slovenian_ci";
/*  795 */     INDEX_TO_COLLATION[165] = "utf32_polish_ci";
/*  796 */     INDEX_TO_COLLATION[166] = "utf32_estonian_ci";
/*  797 */     INDEX_TO_COLLATION[167] = "utf32_spanish_ci";
/*  798 */     INDEX_TO_COLLATION[168] = "utf32_swedish_ci";
/*  799 */     INDEX_TO_COLLATION[169] = "utf32_turkish_ci";
/*  800 */     INDEX_TO_COLLATION[170] = "utf32_czech_ci";
/*  801 */     INDEX_TO_COLLATION[171] = "utf32_danish_ci";
/*  802 */     INDEX_TO_COLLATION[172] = "utf32_lithuanian_ci";
/*  803 */     INDEX_TO_COLLATION[173] = "utf32_slovak_ci";
/*  804 */     INDEX_TO_COLLATION[174] = "utf32_spanish2_ci";
/*  805 */     INDEX_TO_COLLATION[175] = "utf32_roman_ci";
/*  806 */     INDEX_TO_COLLATION[176] = "utf32_persian_ci";
/*  807 */     INDEX_TO_COLLATION[177] = "utf32_esperanto_ci";
/*  808 */     INDEX_TO_COLLATION[178] = "utf32_hungarian_ci";
/*  809 */     INDEX_TO_COLLATION[179] = "utf32_sinhala_ci";
/*      */     
/*  811 */     Map indexMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     
/*  813 */     for (int i = 0; i < INDEX_TO_CHARSET.length; i++) {
/*  814 */       String mysqlEncodingName = INDEX_TO_CHARSET[i];
/*      */       
/*  816 */       if (mysqlEncodingName != null) {
/*  817 */         indexMap.put(INDEX_TO_CHARSET[i], Constants.integerValueOf(i));
/*      */       }
/*      */     } 
/*      */     
/*  821 */     MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP = Collections.unmodifiableMap(indexMap);
/*      */     
/*  823 */     Map tempMap = new HashMap();
/*      */     
/*  825 */     tempMap.put("czech", "latin2");
/*  826 */     tempMap.put("danish", "latin1");
/*  827 */     tempMap.put("dutch", "latin1");
/*  828 */     tempMap.put("english", "latin1");
/*  829 */     tempMap.put("estonian", "latin7");
/*  830 */     tempMap.put("french", "latin1");
/*  831 */     tempMap.put("german", "latin1");
/*  832 */     tempMap.put("greek", "greek");
/*  833 */     tempMap.put("hungarian", "latin2");
/*  834 */     tempMap.put("italian", "latin1");
/*  835 */     tempMap.put("japanese", "ujis");
/*  836 */     tempMap.put("japanese-sjis", "sjis");
/*  837 */     tempMap.put("korean", "euckr");
/*  838 */     tempMap.put("norwegian", "latin1");
/*  839 */     tempMap.put("norwegian-ny", "latin1");
/*  840 */     tempMap.put("polish", "latin2");
/*  841 */     tempMap.put("portuguese", "latin1");
/*  842 */     tempMap.put("romanian", "latin2");
/*  843 */     tempMap.put("russian", "koi8r");
/*  844 */     tempMap.put("serbian", "cp1250");
/*  845 */     tempMap.put("slovak", "latin2");
/*  846 */     tempMap.put("spanish", "latin1");
/*  847 */     tempMap.put("swedish", "latin1");
/*  848 */     tempMap.put("ukrainian", "koi8u");
/*      */     
/*  850 */     ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(tempMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String getJavaEncodingForMysqlEncoding(String mysqlEncoding, Connection conn) throws SQLException {
/*  857 */     if (conn != null && conn.versionMeetsMinimum(4, 1, 0) && "latin1".equalsIgnoreCase(mysqlEncoding))
/*      */     {
/*  859 */       return "Cp1252";
/*      */     }
/*      */     
/*  862 */     return (String)MYSQL_TO_JAVA_CHARSET_MAP.get(mysqlEncoding);
/*      */   }
/*      */ 
/*      */   
/*      */   public static final String getMysqlEncodingForJavaEncoding(String javaEncodingUC, Connection conn) throws SQLException {
/*  867 */     List mysqlEncodings = (List)JAVA_UC_TO_MYSQL_CHARSET_MAP.get(javaEncodingUC);
/*      */ 
/*      */ 
/*      */     
/*  871 */     if (mysqlEncodings != null) {
/*  872 */       Iterator iter = mysqlEncodings.iterator();
/*      */       
/*  874 */       VersionedStringProperty versionedProp = null;
/*      */       
/*  876 */       while (iter.hasNext()) {
/*  877 */         VersionedStringProperty propToCheck = iter.next();
/*      */ 
/*      */         
/*  880 */         if (conn == null)
/*      */         {
/*      */           
/*  883 */           return propToCheck.toString();
/*      */         }
/*      */         
/*  886 */         if (versionedProp != null && !versionedProp.preferredValue && 
/*  887 */           versionedProp.majorVersion == propToCheck.majorVersion && versionedProp.minorVersion == propToCheck.minorVersion && versionedProp.subminorVersion == propToCheck.subminorVersion)
/*      */         {
/*      */           
/*  890 */           return versionedProp.toString();
/*      */         }
/*      */ 
/*      */         
/*  894 */         if (propToCheck.isOkayForVersion(conn)) {
/*  895 */           if (propToCheck.preferredValue) {
/*  896 */             return propToCheck.toString();
/*      */           }
/*      */           
/*  899 */           versionedProp = propToCheck;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  905 */       if (versionedProp != null) {
/*  906 */         return versionedProp.toString();
/*      */       }
/*      */     } 
/*      */     
/*  910 */     return null;
/*      */   }
/*      */   
/*      */   static final int getNumberOfCharsetsConfigured() {
/*  914 */     return MYSQL_TO_JAVA_CHARSET_MAP.size() / 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final String getCharacterEncodingForErrorMessages(ConnectionImpl conn) throws SQLException {
/*  930 */     String errorMessageFile = conn.getServerVariable("language");
/*      */     
/*  932 */     if (errorMessageFile == null || errorMessageFile.length() == 0)
/*      */     {
/*  934 */       return "Cp1252";
/*      */     }
/*      */     
/*  937 */     int endWithoutSlash = errorMessageFile.length();
/*      */     
/*  939 */     if (errorMessageFile.endsWith("/") || errorMessageFile.endsWith("\\")) {
/*  940 */       endWithoutSlash--;
/*      */     }
/*      */     
/*  943 */     int lastSlashIndex = errorMessageFile.lastIndexOf('/', endWithoutSlash - 1);
/*      */     
/*  945 */     if (lastSlashIndex == -1) {
/*  946 */       lastSlashIndex = errorMessageFile.lastIndexOf('\\', endWithoutSlash - 1);
/*      */     }
/*      */     
/*  949 */     if (lastSlashIndex == -1) {
/*  950 */       lastSlashIndex = 0;
/*      */     }
/*      */     
/*  953 */     if (lastSlashIndex == endWithoutSlash || endWithoutSlash < lastSlashIndex)
/*      */     {
/*  955 */       return "Cp1252";
/*      */     }
/*      */     
/*  958 */     errorMessageFile = errorMessageFile.substring(lastSlashIndex + 1, endWithoutSlash);
/*      */     
/*  960 */     String errorMessageEncodingMysql = (String)ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP.get(errorMessageFile);
/*      */     
/*  962 */     if (errorMessageEncodingMysql == null)
/*      */     {
/*  964 */       return "Cp1252";
/*      */     }
/*      */     
/*  967 */     String javaEncoding = getJavaEncodingForMysqlEncoding(errorMessageEncodingMysql, conn);
/*      */     
/*  969 */     if (javaEncoding == null)
/*      */     {
/*  971 */       return "Cp1252";
/*      */     }
/*      */     
/*  974 */     return javaEncoding;
/*      */   }
/*      */   
/*      */   static final boolean isAliasForSjis(String encoding) {
/*  978 */     return ("SJIS".equalsIgnoreCase(encoding) || "WINDOWS-31J".equalsIgnoreCase(encoding) || "MS932".equalsIgnoreCase(encoding) || "SHIFT_JIS".equalsIgnoreCase(encoding) || "CP943".equalsIgnoreCase(encoding));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final boolean isMultibyteCharset(String javaEncodingName) {
/*  987 */     String javaEncodingNameUC = javaEncodingName.toUpperCase(Locale.ENGLISH);
/*      */ 
/*      */     
/*  990 */     return MULTIBYTE_CHARSETS.containsKey(javaEncodingNameUC);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void populateMapWithKeyValuePairs(String configKey, Map mapToPopulate, boolean addVersionedProperties, boolean addUppercaseKeys) {
/*  996 */     String javaToMysqlConfig = CHARSET_CONFIG.getProperty(configKey);
/*      */     
/*  998 */     if (javaToMysqlConfig != null) {
/*  999 */       List mappings = StringUtils.split(javaToMysqlConfig, ",", true);
/*      */       
/* 1001 */       if (mappings != null) {
/* 1002 */         Iterator mappingsIter = mappings.iterator();
/*      */         
/* 1004 */         while (mappingsIter.hasNext()) {
/* 1005 */           String aMapping = mappingsIter.next();
/*      */           
/* 1007 */           List parsedPair = StringUtils.split(aMapping, "=", true);
/*      */           
/* 1009 */           if (parsedPair.size() == 2) {
/* 1010 */             String key = parsedPair.get(0).toString();
/* 1011 */             String value = parsedPair.get(1).toString();
/*      */             
/* 1013 */             if (addVersionedProperties) {
/* 1014 */               List versionedProperties = (List)mapToPopulate.get(key);
/*      */ 
/*      */               
/* 1017 */               if (versionedProperties == null) {
/* 1018 */                 versionedProperties = new ArrayList();
/* 1019 */                 mapToPopulate.put(key, versionedProperties);
/*      */               } 
/*      */               
/* 1022 */               VersionedStringProperty verProp = new VersionedStringProperty(value);
/*      */               
/* 1024 */               versionedProperties.add(verProp);
/*      */               
/* 1026 */               if (addUppercaseKeys) {
/* 1027 */                 String keyUc = key.toUpperCase(Locale.ENGLISH);
/*      */                 
/* 1029 */                 versionedProperties = mapToPopulate.get(keyUc);
/*      */ 
/*      */                 
/* 1032 */                 if (versionedProperties == null) {
/* 1033 */                   versionedProperties = new ArrayList();
/* 1034 */                   mapToPopulate.put(keyUc, versionedProperties);
/*      */                 } 
/*      */ 
/*      */                 
/* 1038 */                 versionedProperties.add(verProp);
/*      */               }  continue;
/*      */             } 
/* 1041 */             mapToPopulate.put(key, value);
/*      */             
/* 1043 */             if (addUppercaseKeys) {
/* 1044 */               mapToPopulate.put(key.toUpperCase(Locale.ENGLISH), value);
/*      */             }
/*      */             
/*      */             continue;
/*      */           } 
/* 1049 */           throw new RuntimeException("Syntax error in Charsets.properties resource for token \"" + aMapping + "\".");
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1056 */         throw new RuntimeException("Missing/corrupt entry for \"" + configKey + "\" in Charsets.properties.");
/*      */       } 
/*      */     } else {
/*      */       
/* 1060 */       throw new RuntimeException("Could not find configuration value \"" + configKey + "\" in Charsets.properties resource");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static int getCharsetIndexForMysqlEncodingName(String name) {
/* 1066 */     if (name == null) {
/* 1067 */       return 0;
/*      */     }
/*      */     
/* 1070 */     Integer asInt = (Integer)MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP.get(name);
/*      */     
/* 1072 */     if (asInt == null) {
/* 1073 */       return 0;
/*      */     }
/*      */     
/* 1076 */     return asInt.intValue();
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\CharsetMapping.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */