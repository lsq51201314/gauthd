/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ public class Util
/*     */ {
/*     */   protected static Method systemNanoTimeMethod;
/*     */   private static Method CAST_METHOD;
/*     */   
/*     */   static {
/*     */     try {
/*  54 */       systemNanoTimeMethod = System.class.getMethod("nanoTime", null);
/*  55 */     } catch (SecurityException e) {
/*  56 */       systemNanoTimeMethod = null;
/*  57 */     } catch (NoSuchMethodException e) {
/*  58 */       systemNanoTimeMethod = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean nanoTimeAvailable() {
/*  63 */     return (systemNanoTimeMethod != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
/*     */   
/*     */   static final TimeZone getDefaultTimeZone() {
/*  74 */     return (TimeZone)DEFAULT_TIMEZONE.clone();
/*     */   }
/*     */   class RandStructcture { RandStructcture(Util this$0) {
/*  77 */       this.this$0 = this$0;
/*     */     }
/*     */     
/*     */     long maxValue;
/*     */     double maxValueDbl;
/*     */     long seed1;
/*     */     long seed2;
/*     */     private final Util this$0; }
/*     */ 
/*     */   
/*  87 */   private static Util enclosingInstance = new Util();
/*     */   
/*     */   private static boolean isJdbc4 = false;
/*     */   
/*     */   private static boolean isColdFusion = false;
/*     */   
/*     */   static {
/*     */     try {
/*  95 */       CAST_METHOD = Class.class.getMethod("cast", new Class[] { Object.class });
/*     */     }
/*  97 */     catch (Throwable t) {}
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 102 */       Class.forName("java.sql.NClob");
/* 103 */       isJdbc4 = true;
/* 104 */     } catch (Throwable t) {
/* 105 */       isJdbc4 = false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     String loadedFrom = stackTraceToString(new Throwable());
/*     */     
/* 117 */     if (loadedFrom != null) {
/* 118 */       isColdFusion = (loadedFrom.indexOf("coldfusion") != -1);
/*     */     } else {
/* 120 */       isColdFusion = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJdbc4() {
/* 128 */     return isJdbc4;
/*     */   }
/*     */   
/*     */   public static boolean isColdFusion() {
/* 132 */     return isColdFusion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String newCrypt(String password, String seed) {
/* 140 */     if (password == null || password.length() == 0) {
/* 141 */       return password;
/*     */     }
/*     */     
/* 144 */     long[] pw = newHash(seed);
/* 145 */     long[] msg = newHash(password);
/* 146 */     long max = 1073741823L;
/* 147 */     long seed1 = (pw[0] ^ msg[0]) % max;
/* 148 */     long seed2 = (pw[1] ^ msg[1]) % max;
/* 149 */     char[] chars = new char[seed.length()];
/*     */     int i;
/* 151 */     for (i = 0; i < seed.length(); i++) {
/* 152 */       seed1 = (seed1 * 3L + seed2) % max;
/* 153 */       seed2 = (seed1 + seed2 + 33L) % max;
/* 154 */       double d1 = seed1 / max;
/* 155 */       byte b1 = (byte)(int)Math.floor(d1 * 31.0D + 64.0D);
/* 156 */       chars[i] = (char)b1;
/*     */     } 
/*     */     
/* 159 */     seed1 = (seed1 * 3L + seed2) % max;
/* 160 */     seed2 = (seed1 + seed2 + 33L) % max;
/* 161 */     double d = seed1 / max;
/* 162 */     byte b = (byte)(int)Math.floor(d * 31.0D);
/*     */     
/* 164 */     for (i = 0; i < seed.length(); i++) {
/* 165 */       chars[i] = (char)(chars[i] ^ (char)b);
/*     */     }
/*     */     
/* 168 */     return new String(chars);
/*     */   }
/*     */   
/*     */   static long[] newHash(String password) {
/* 172 */     long nr = 1345345333L;
/* 173 */     long add = 7L;
/* 174 */     long nr2 = 305419889L;
/*     */ 
/*     */     
/* 177 */     for (int i = 0; i < password.length(); i++) {
/* 178 */       if (password.charAt(i) != ' ' && password.charAt(i) != '\t') {
/*     */ 
/*     */ 
/*     */         
/* 182 */         long tmp = (0xFF & password.charAt(i));
/* 183 */         nr ^= ((nr & 0x3FL) + add) * tmp + (nr << 8L);
/* 184 */         nr2 += nr2 << 8L ^ nr;
/* 185 */         add += tmp;
/*     */       } 
/*     */     } 
/* 188 */     long[] result = new long[2];
/* 189 */     result[0] = nr & 0x7FFFFFFFL;
/* 190 */     result[1] = nr2 & 0x7FFFFFFFL;
/*     */     
/* 192 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String oldCrypt(String password, String seed) {
/* 200 */     long max = 33554431L;
/*     */ 
/*     */ 
/*     */     
/* 204 */     if (password == null || password.length() == 0) {
/* 205 */       return password;
/*     */     }
/*     */     
/* 208 */     long hp = oldHash(seed);
/* 209 */     long hm = oldHash(password);
/*     */     
/* 211 */     long nr = hp ^ hm;
/* 212 */     nr %= max;
/* 213 */     long s1 = nr;
/* 214 */     long s2 = nr / 2L;
/*     */     
/* 216 */     char[] chars = new char[seed.length()];
/*     */     
/* 218 */     for (int i = 0; i < seed.length(); i++) {
/* 219 */       s1 = (s1 * 3L + s2) % max;
/* 220 */       s2 = (s1 + s2 + 33L) % max;
/* 221 */       double d = s1 / max;
/* 222 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 223 */       chars[i] = (char)b;
/*     */     } 
/*     */     
/* 226 */     return new String(chars);
/*     */   }
/*     */   
/*     */   static long oldHash(String password) {
/* 230 */     long nr = 1345345333L;
/* 231 */     long nr2 = 7L;
/*     */ 
/*     */     
/* 234 */     for (int i = 0; i < password.length(); i++) {
/* 235 */       if (password.charAt(i) != ' ' && password.charAt(i) != '\t') {
/*     */ 
/*     */ 
/*     */         
/* 239 */         long tmp = password.charAt(i);
/* 240 */         nr ^= ((nr & 0x3FL) + nr2) * tmp + (nr << 8L);
/* 241 */         nr2 += tmp;
/*     */       } 
/*     */     } 
/* 244 */     return nr & 0x7FFFFFFFL;
/*     */   }
/*     */   
/*     */   private static RandStructcture randomInit(long seed1, long seed2) {
/* 248 */     enclosingInstance.getClass(); RandStructcture randStruct = new RandStructcture(enclosingInstance);
/*     */     
/* 250 */     randStruct.maxValue = 1073741823L;
/* 251 */     randStruct.maxValueDbl = randStruct.maxValue;
/* 252 */     randStruct.seed1 = seed1 % randStruct.maxValue;
/* 253 */     randStruct.seed2 = seed2 % randStruct.maxValue;
/*     */     
/* 255 */     return randStruct;
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
/*     */   public static Object readObject(ResultSet resultSet, int index) throws Exception {
/* 273 */     ObjectInputStream objIn = new ObjectInputStream(resultSet.getBinaryStream(index));
/*     */     
/* 275 */     Object obj = objIn.readObject();
/* 276 */     objIn.close();
/*     */     
/* 278 */     return obj;
/*     */   }
/*     */   
/*     */   private static double rnd(RandStructcture randStruct) {
/* 282 */     randStruct.seed1 = (randStruct.seed1 * 3L + randStruct.seed2) % randStruct.maxValue;
/*     */     
/* 284 */     randStruct.seed2 = (randStruct.seed1 + randStruct.seed2 + 33L) % randStruct.maxValue;
/*     */ 
/*     */     
/* 287 */     return randStruct.seed1 / randStruct.maxValueDbl;
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
/*     */   public static String scramble(String message, String password) {
/* 303 */     byte[] to = new byte[8];
/* 304 */     String val = "";
/*     */     
/* 306 */     message = message.substring(0, 8);
/*     */     
/* 308 */     if (password != null && password.length() > 0) {
/* 309 */       long[] hashPass = newHash(password);
/* 310 */       long[] hashMessage = newHash(message);
/*     */       
/* 312 */       RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);
/*     */ 
/*     */       
/* 315 */       int msgPos = 0;
/* 316 */       int msgLength = message.length();
/* 317 */       int toPos = 0;
/*     */       
/* 319 */       while (msgPos++ < msgLength) {
/* 320 */         to[toPos++] = (byte)(int)(Math.floor(rnd(randStruct) * 31.0D) + 64.0D);
/*     */       }
/*     */ 
/*     */       
/* 324 */       byte extra = (byte)(int)Math.floor(rnd(randStruct) * 31.0D);
/*     */       
/* 326 */       for (int i = 0; i < to.length; i++) {
/* 327 */         to[i] = (byte)(to[i] ^ extra);
/*     */       }
/*     */       
/* 330 */       val = new String(to);
/*     */     } 
/*     */     
/* 333 */     return val;
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
/*     */   public static String stackTraceToString(Throwable ex) {
/* 349 */     StringBuffer traceBuf = new StringBuffer();
/* 350 */     traceBuf.append(Messages.getString("Util.1"));
/*     */     
/* 352 */     if (ex != null) {
/* 353 */       traceBuf.append(ex.getClass().getName());
/*     */       
/* 355 */       String message = ex.getMessage();
/*     */       
/* 357 */       if (message != null) {
/* 358 */         traceBuf.append(Messages.getString("Util.2"));
/* 359 */         traceBuf.append(message);
/*     */       } 
/*     */       
/* 362 */       StringWriter out = new StringWriter();
/*     */       
/* 364 */       PrintWriter printOut = new PrintWriter(out);
/*     */       
/* 366 */       ex.printStackTrace(printOut);
/*     */       
/* 368 */       traceBuf.append(Messages.getString("Util.3"));
/* 369 */       traceBuf.append(out.toString());
/*     */     } 
/*     */     
/* 372 */     traceBuf.append(Messages.getString("Util.4"));
/*     */     
/* 374 */     return traceBuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getInstance(String className, Class[] argTypes, Object[] args) throws SQLException {
/*     */     try {
/* 381 */       return handleNewInstance(Class.forName(className).getConstructor(argTypes), args);
/*     */     }
/* 383 */     catch (SecurityException e) {
/* 384 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e);
/*     */     
/*     */     }
/* 387 */     catch (NoSuchMethodException e) {
/* 388 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e);
/*     */     
/*     */     }
/* 391 */     catch (ClassNotFoundException e) {
/* 392 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e);
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
/*     */   public static final Object handleNewInstance(Constructor ctor, Object[] args) throws SQLException {
/*     */     try {
/* 406 */       return ctor.newInstance(args);
/* 407 */     } catch (IllegalArgumentException e) {
/* 408 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e);
/*     */     
/*     */     }
/* 411 */     catch (InstantiationException e) {
/* 412 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e);
/*     */     
/*     */     }
/* 415 */     catch (IllegalAccessException e) {
/* 416 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e);
/*     */     
/*     */     }
/* 419 */     catch (InvocationTargetException e) {
/* 420 */       Throwable target = e.getTargetException();
/*     */       
/* 422 */       if (target instanceof SQLException) {
/* 423 */         throw (SQLException)target;
/*     */       }
/*     */       
/* 426 */       if (target instanceof ExceptionInInitializerError) {
/* 427 */         target = ((ExceptionInInitializerError)target).getException();
/*     */       }
/*     */       
/* 430 */       throw SQLError.createSQLException(target.toString(), "S1000");
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
/*     */   public static boolean interfaceExists(String hostname) {
/*     */     try {
/* 445 */       Class networkInterfaceClass = Class.forName("java.net.NetworkInterface"); return 
/*     */         
/* 447 */         (networkInterfaceClass.getMethod("getByName", null).invoke(networkInterfaceClass, new Object[] { hostname }) != null);
/*     */     }
/* 449 */     catch (Throwable t) {
/* 450 */       return false;
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
/*     */   public static Object cast(Object invokeOn, Object toCast) {
/* 463 */     if (CAST_METHOD != null) {
/*     */       try {
/* 465 */         return CAST_METHOD.invoke(invokeOn, new Object[] { toCast });
/* 466 */       } catch (Throwable t) {
/* 467 */         return null;
/*     */       } 
/*     */     }
/*     */     
/* 471 */     return null;
/*     */   }
/*     */   
/*     */   public static long getCurrentTimeNanosOrMillis() {
/* 475 */     if (systemNanoTimeMethod != null) {
/*     */       try {
/* 477 */         return ((Long)systemNanoTimeMethod.invoke(null, null)).longValue();
/*     */       }
/* 479 */       catch (IllegalArgumentException e) {
/*     */       
/* 481 */       } catch (IllegalAccessException e) {
/*     */       
/* 483 */       } catch (InvocationTargetException e) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 488 */     return System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs) throws SQLException {
/* 493 */     while (rs.next()) {
/* 494 */       mappedValues.put(rs.getObject(1), rs.getObject(2));
/*     */     }
/*     */   }
/*     */   
/*     */   public static Map calculateDifferences(Map map1, Map map2) {
/* 499 */     Map diffMap = new HashMap();
/*     */     
/* 501 */     Iterator map1Entries = map1.entrySet().iterator();
/*     */     
/* 503 */     while (map1Entries.hasNext()) {
/* 504 */       Map.Entry entry = map1Entries.next();
/* 505 */       Object key = entry.getKey();
/*     */       
/* 507 */       Number value1 = null;
/* 508 */       Number value2 = null;
/*     */       
/* 510 */       if (entry.getValue() instanceof Number) {
/*     */         
/* 512 */         value1 = (Number)entry.getValue();
/* 513 */         value2 = (Number)map2.get(key);
/*     */       } else {
/*     */         try {
/* 516 */           value1 = new Double(entry.getValue().toString());
/* 517 */           value2 = new Double(map2.get(key).toString());
/* 518 */         } catch (NumberFormatException nfe) {
/*     */           continue;
/*     */         } 
/*     */       } 
/*     */       
/* 523 */       if (value1.equals(value2)) {
/*     */         continue;
/*     */       }
/*     */       
/* 527 */       if (value1 instanceof Byte) {
/* 528 */         diffMap.put(key, new Byte((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue())));
/*     */         continue;
/*     */       } 
/* 531 */       if (value1 instanceof Short) {
/* 532 */         diffMap.put(key, new Short((short)(((Short)value2).shortValue() - ((Short)value1).shortValue()))); continue;
/*     */       } 
/* 534 */       if (value1 instanceof Integer) {
/* 535 */         diffMap.put(key, new Integer(((Integer)value2).intValue() - ((Integer)value1).intValue()));
/*     */         continue;
/*     */       } 
/* 538 */       if (value1 instanceof Long) {
/* 539 */         diffMap.put(key, new Long(((Long)value2).longValue() - ((Long)value1).longValue()));
/*     */         continue;
/*     */       } 
/* 542 */       if (value1 instanceof Float) {
/* 543 */         diffMap.put(key, new Float(((Float)value2).floatValue() - ((Float)value1).floatValue())); continue;
/*     */       } 
/* 545 */       if (value1 instanceof Double) {
/* 546 */         diffMap.put(key, new Double((((Double)value2).shortValue() - ((Double)value1).shortValue())));
/*     */         continue;
/*     */       } 
/* 549 */       if (value1 instanceof BigDecimal) {
/* 550 */         diffMap.put(key, ((BigDecimal)value2).subtract((BigDecimal)value1)); continue;
/*     */       } 
/* 552 */       if (value1 instanceof BigInteger) {
/* 553 */         diffMap.put(key, ((BigInteger)value2).subtract((BigInteger)value1));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 558 */     return diffMap;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static List loadExtensions(Connection conn, Properties props, String extensionClassNames, String errorMessageKey) throws SQLException {
/* 564 */     List extensionList = new LinkedList();
/*     */     
/* 566 */     List interceptorsToCreate = StringUtils.split(extensionClassNames, ",", true);
/*     */ 
/*     */     
/* 569 */     Iterator iter = interceptorsToCreate.iterator();
/*     */     
/* 571 */     String className = null;
/*     */     
/*     */     try {
/* 574 */       while (iter.hasNext()) {
/* 575 */         className = iter.next().toString();
/* 576 */         Extension extensionInstance = (Extension)Class.forName(className).newInstance();
/*     */         
/* 578 */         extensionInstance.init(conn, props);
/*     */         
/* 580 */         extensionList.add(extensionInstance);
/*     */       } 
/* 582 */     } catch (Throwable t) {
/* 583 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString(errorMessageKey, new Object[] { className }));
/*     */       
/* 585 */       sqlEx.initCause(t);
/*     */       
/* 587 */       throw sqlEx;
/*     */     } 
/*     */     
/* 590 */     return extensionList;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Util.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */