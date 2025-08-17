/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
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
/*     */ public class SingleByteCharsetConverter
/*     */ {
/*     */   private static final int BYTE_RANGE = 256;
/*  44 */   private static byte[] allBytes = new byte[256];
/*  45 */   private static final Map CONVERTER_MAP = new HashMap();
/*     */   
/*  47 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private static byte[] unknownCharsMap = new byte[65536];
/*     */   static {
/*     */     int i;
/*  55 */     for (i = -128; i <= 127; i++) {
/*  56 */       allBytes[i - -128] = (byte)i;
/*     */     }
/*     */     
/*  59 */     for (i = 0; i < unknownCharsMap.length; i++) {
/*  60 */       unknownCharsMap[i] = 63;
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
/*     */   public static synchronized SingleByteCharsetConverter getInstance(String encodingName, Connection conn) throws UnsupportedEncodingException, SQLException {
/*  81 */     SingleByteCharsetConverter instance = (SingleByteCharsetConverter)CONVERTER_MAP.get(encodingName);
/*     */ 
/*     */     
/*  84 */     if (instance == null) {
/*  85 */       instance = initCharset(encodingName);
/*     */     }
/*     */     
/*  88 */     return instance;
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
/*     */   public static SingleByteCharsetConverter initCharset(String javaEncodingName) throws UnsupportedEncodingException, SQLException {
/* 103 */     if (CharsetMapping.isMultibyteCharset(javaEncodingName)) {
/* 104 */       return null;
/*     */     }
/*     */     
/* 107 */     SingleByteCharsetConverter converter = new SingleByteCharsetConverter(javaEncodingName);
/*     */ 
/*     */     
/* 110 */     CONVERTER_MAP.put(javaEncodingName, converter);
/*     */     
/* 112 */     return converter;
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
/*     */   public static String toStringDefaultEncoding(byte[] buffer, int startPos, int length) {
/* 132 */     return new String(buffer, startPos, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 138 */   private char[] byteToChars = new char[256];
/*     */   
/* 140 */   private byte[] charToByteMap = new byte[65536];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SingleByteCharsetConverter(String encodingName) throws UnsupportedEncodingException {
/* 152 */     String allBytesString = new String(allBytes, 0, 256, encodingName);
/*     */     
/* 154 */     int allBytesLen = allBytesString.length();
/*     */     
/* 156 */     System.arraycopy(unknownCharsMap, 0, this.charToByteMap, 0, this.charToByteMap.length);
/*     */ 
/*     */     
/* 159 */     for (int i = 0; i < 256 && i < allBytesLen; i++) {
/* 160 */       char c = allBytesString.charAt(i);
/* 161 */       this.byteToChars[i] = c;
/* 162 */       this.charToByteMap[c] = allBytes[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public final byte[] toBytes(char[] c) {
/* 167 */     if (c == null) {
/* 168 */       return null;
/*     */     }
/*     */     
/* 171 */     int length = c.length;
/* 172 */     byte[] bytes = new byte[length];
/*     */     
/* 174 */     for (int i = 0; i < length; i++) {
/* 175 */       bytes[i] = this.charToByteMap[c[i]];
/*     */     }
/*     */     
/* 178 */     return bytes;
/*     */   }
/*     */   
/*     */   public final byte[] toBytesWrapped(char[] c, char beginWrap, char endWrap) {
/* 182 */     if (c == null) {
/* 183 */       return null;
/*     */     }
/*     */     
/* 186 */     int length = c.length + 2;
/* 187 */     int charLength = c.length;
/*     */     
/* 189 */     byte[] bytes = new byte[length];
/* 190 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */     
/* 192 */     for (int i = 0; i < charLength; i++) {
/* 193 */       bytes[i + 1] = this.charToByteMap[c[i]];
/*     */     }
/*     */     
/* 196 */     bytes[length - 1] = this.charToByteMap[endWrap];
/*     */     
/* 198 */     return bytes;
/*     */   }
/*     */   
/*     */   public final byte[] toBytes(char[] chars, int offset, int length) {
/* 202 */     if (chars == null) {
/* 203 */       return null;
/*     */     }
/*     */     
/* 206 */     if (length == 0) {
/* 207 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 210 */     byte[] bytes = new byte[length];
/*     */     
/* 212 */     for (int i = 0; i < length; i++) {
/* 213 */       bytes[i] = this.charToByteMap[chars[i + offset]];
/*     */     }
/*     */     
/* 216 */     return bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final byte[] toBytes(String s) {
/* 227 */     if (s == null) {
/* 228 */       return null;
/*     */     }
/*     */     
/* 231 */     int length = s.length();
/* 232 */     byte[] bytes = new byte[length];
/*     */     
/* 234 */     for (int i = 0; i < length; i++) {
/* 235 */       bytes[i] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */     
/* 238 */     return bytes;
/*     */   }
/*     */   
/*     */   public final byte[] toBytesWrapped(String s, char beginWrap, char endWrap) {
/* 242 */     if (s == null) {
/* 243 */       return null;
/*     */     }
/*     */     
/* 246 */     int stringLength = s.length();
/*     */     
/* 248 */     int length = stringLength + 2;
/*     */     
/* 250 */     byte[] bytes = new byte[length];
/*     */     
/* 252 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */     
/* 254 */     for (int i = 0; i < stringLength; i++) {
/* 255 */       bytes[i + 1] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */     
/* 258 */     bytes[length - 1] = this.charToByteMap[endWrap];
/*     */     
/* 260 */     return bytes;
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
/*     */   public final byte[] toBytes(String s, int offset, int length) {
/* 276 */     if (s == null) {
/* 277 */       return null;
/*     */     }
/*     */     
/* 280 */     if (length == 0) {
/* 281 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 284 */     byte[] bytes = new byte[length];
/*     */     
/* 286 */     for (int i = 0; i < length; i++) {
/* 287 */       char c = s.charAt(i + offset);
/* 288 */       bytes[i] = this.charToByteMap[c];
/*     */     } 
/*     */     
/* 291 */     return bytes;
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
/*     */   public final String toString(byte[] buffer) {
/* 303 */     return toString(buffer, 0, buffer.length);
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
/*     */   public final String toString(byte[] buffer, int startPos, int length) {
/* 319 */     char[] charArray = new char[length];
/* 320 */     int readpoint = startPos;
/*     */     
/* 322 */     for (int i = 0; i < length; i++) {
/* 323 */       charArray[i] = this.byteToChars[buffer[readpoint] - -128];
/* 324 */       readpoint++;
/*     */     } 
/*     */     
/* 327 */     return new String(charArray);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\SingleByteCharsetConverter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */