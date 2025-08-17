/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Constants
/*     */ {
/*  38 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   public static final String MILLIS_I18N = Messages.getString("Milliseconds");
/*     */   
/*  45 */   public static final byte[] SLASH_STAR_SPACE_AS_BYTES = new byte[] { 47, 42, 32 };
/*     */ 
/*     */   
/*  48 */   public static final byte[] SPACE_STAR_SLASH_SPACE_AS_BYTES = new byte[] { 32, 42, 47, 32 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   private static final Character[] CHARACTER_CACHE = new Character[128];
/*     */   
/*     */   private static final int BYTE_CACHE_OFFSET = 128;
/*     */   
/*  58 */   private static final Byte[] BYTE_CACHE = new Byte[256];
/*     */   
/*     */   private static final int INTEGER_CACHE_OFFSET = 128;
/*     */   
/*  62 */   private static final Integer[] INTEGER_CACHE = new Integer[256];
/*     */   
/*     */   private static final int SHORT_CACHE_OFFSET = 128;
/*     */   
/*  66 */   private static final Short[] SHORT_CACHE = new Short[256];
/*     */   
/*  68 */   private static final Long[] LONG_CACHE = new Long[256];
/*     */   private static final int LONG_CACHE_OFFSET = 128;
/*     */   
/*     */   static {
/*     */     int i;
/*  73 */     for (i = 0; i < CHARACTER_CACHE.length; i++) {
/*  74 */       CHARACTER_CACHE[i] = new Character((char)i);
/*     */     }
/*     */     
/*  77 */     for (i = 0; i < INTEGER_CACHE.length; i++) {
/*  78 */       INTEGER_CACHE[i] = new Integer(i - 128);
/*     */     }
/*     */     
/*  81 */     for (i = 0; i < SHORT_CACHE.length; i++) {
/*  82 */       SHORT_CACHE[i] = new Short((short)(i - 128));
/*     */     }
/*     */     
/*  85 */     for (i = 0; i < LONG_CACHE.length; i++) {
/*  86 */       LONG_CACHE[i] = new Long((i - 128));
/*     */     }
/*     */     
/*  89 */     for (i = 0; i < BYTE_CACHE.length; i++) {
/*  90 */       BYTE_CACHE[i] = new Byte((byte)(i - 128));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static Character characterValueOf(char c) {
/*  96 */     if (c <= '') {
/*  97 */       return CHARACTER_CACHE[c];
/*     */     }
/*     */     
/* 100 */     return new Character(c);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Byte byteValueOf(byte b) {
/* 106 */     return BYTE_CACHE[b + 128];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Integer integerValueOf(int i) {
/* 112 */     if (i >= -128 && i <= 127) {
/* 113 */       return INTEGER_CACHE[i + 128];
/*     */     }
/*     */     
/* 116 */     return new Integer(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Short shortValueOf(short s) {
/* 123 */     if (s >= -128 && s <= 127) {
/* 124 */       return SHORT_CACHE[s + 128];
/*     */     }
/*     */     
/* 127 */     return new Short(s);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Long longValueOf(long l) {
/* 133 */     if (l >= -128L && l <= 127L) {
/* 134 */       return LONG_CACHE[(int)l + 128];
/*     */     }
/*     */     
/* 137 */     return new Long(l);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Constants.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */