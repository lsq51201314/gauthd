/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Messages
/*     */ {
/*     */   private static final String BUNDLE_NAME = "com.mysql.jdbc.LocalizedErrorMessages";
/*     */   private static final ResourceBundle RESOURCE_BUNDLE;
/*     */   
/*     */   static {
/*  45 */     ResourceBundle temp = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  54 */       temp = ResourceBundle.getBundle("com.mysql.jdbc.LocalizedErrorMessages", Locale.getDefault(), Messages.class.getClassLoader());
/*     */     }
/*  56 */     catch (Throwable t) {
/*     */       try {
/*  58 */         temp = ResourceBundle.getBundle("com.mysql.jdbc.LocalizedErrorMessages");
/*  59 */       } catch (Throwable t2) {
/*  60 */         RuntimeException rt = new RuntimeException("Can't load resource bundle due to underlying exception " + t.toString());
/*     */ 
/*     */         
/*  63 */         rt.initCause(t2);
/*     */         
/*  65 */         throw rt;
/*     */       } 
/*     */     } finally {
/*  68 */       RESOURCE_BUNDLE = temp;
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
/*     */   public static String getString(String key) {
/*  80 */     if (RESOURCE_BUNDLE == null) {
/*  81 */       throw new RuntimeException("Localized messages from resource bundle 'com.mysql.jdbc.LocalizedErrorMessages' not loaded during initialization of driver.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  87 */       if (key == null) {
/*  88 */         throw new IllegalArgumentException("Message key can not be null");
/*     */       }
/*     */ 
/*     */       
/*  92 */       String message = RESOURCE_BUNDLE.getString(key);
/*     */       
/*  94 */       if (message == null) {
/*  95 */         message = "Missing error message for key '" + key + "'";
/*     */       }
/*     */       
/*  98 */       return message;
/*  99 */     } catch (MissingResourceException e) {
/* 100 */       return '!' + key + '!';
/*     */     } 
/*     */   }
/*     */   
/*     */   public static String getString(String key, Object[] args) {
/* 105 */     return MessageFormat.format(getString(key), args);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Messages.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */