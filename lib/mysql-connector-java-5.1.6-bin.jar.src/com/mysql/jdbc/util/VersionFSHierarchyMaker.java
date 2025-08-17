/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.NonRegisteringDriver;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VersionFSHierarchyMaker
/*     */ {
/*     */   public static void main(String[] args) throws Exception {
/*  42 */     if (args.length < 3) {
/*  43 */       usage();
/*  44 */       System.exit(1);
/*     */     } 
/*     */     
/*  47 */     String jdbcUrl = null;
/*     */     
/*  49 */     String jvmVersion = removeWhitespaceChars(System.getProperty("java.version"));
/*  50 */     String jvmVendor = removeWhitespaceChars(System.getProperty("java.vendor"));
/*  51 */     String osName = removeWhitespaceChars(System.getProperty("os.name"));
/*  52 */     String osArch = removeWhitespaceChars(System.getProperty("os.arch"));
/*  53 */     String osVersion = removeWhitespaceChars(System.getProperty("os.version"));
/*     */     
/*  55 */     jdbcUrl = System.getProperty("com.mysql.jdbc.testsuite.url");
/*     */     
/*  57 */     String mysqlVersion = "not-available";
/*     */     
/*     */     try {
/*  60 */       Connection conn = (new NonRegisteringDriver()).connect(jdbcUrl, null);
/*     */       
/*  62 */       ResultSet rs = conn.createStatement().executeQuery("SELECT VERSION()");
/*  63 */       rs.next();
/*  64 */       mysqlVersion = removeWhitespaceChars(rs.getString(1));
/*  65 */     } catch (Throwable t) {
/*  66 */       mysqlVersion = "no-server-running-on-" + removeWhitespaceChars(jdbcUrl);
/*     */     } 
/*     */     
/*  69 */     String jvmSubdirName = jvmVendor + "-" + jvmVersion;
/*  70 */     String osSubdirName = osName + "-" + osArch + "-" + osVersion;
/*     */     
/*  72 */     File baseDir = new File(args[1]);
/*  73 */     File mysqlVersionDir = new File(baseDir, mysqlVersion);
/*  74 */     File osVersionDir = new File(mysqlVersionDir, osSubdirName);
/*  75 */     File jvmVersionDir = new File(osVersionDir, jvmSubdirName);
/*     */     
/*  77 */     jvmVersionDir.mkdirs();
/*     */ 
/*     */     
/*  80 */     FileOutputStream pathOut = null;
/*     */     
/*     */     try {
/*  83 */       String propsOutputPath = args[2];
/*  84 */       pathOut = new FileOutputStream(propsOutputPath);
/*  85 */       String baseDirStr = baseDir.getAbsolutePath();
/*  86 */       String jvmVersionDirStr = jvmVersionDir.getAbsolutePath();
/*     */       
/*  88 */       if (jvmVersionDirStr.startsWith(baseDirStr)) {
/*  89 */         jvmVersionDirStr = jvmVersionDirStr.substring(baseDirStr.length() + 1);
/*     */       }
/*     */       
/*  92 */       pathOut.write(jvmVersionDirStr.getBytes());
/*     */     } finally {
/*  94 */       if (pathOut != null) {
/*  95 */         pathOut.flush();
/*  96 */         pathOut.close();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static String removeWhitespaceChars(String input) {
/* 102 */     if (input == null) {
/* 103 */       return input;
/*     */     }
/*     */     
/* 106 */     int strLen = input.length();
/*     */     
/* 108 */     StringBuffer output = new StringBuffer(strLen);
/*     */     
/* 110 */     for (int i = 0; i < strLen; i++) {
/* 111 */       char c = input.charAt(i);
/* 112 */       if (!Character.isDigit(c) && !Character.isLetter(c)) {
/* 113 */         if (Character.isWhitespace(c)) {
/* 114 */           output.append("_");
/*     */         } else {
/* 116 */           output.append(".");
/*     */         } 
/*     */       } else {
/* 119 */         output.append(c);
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     return output.toString();
/*     */   }
/*     */   
/*     */   private static void usage() {
/* 127 */     System.err.println("Creates a fs hierarchy representing MySQL version, OS version and JVM version.");
/* 128 */     System.err.println("Stores the full path as 'outputDirectory' property in file 'directoryPropPath'");
/* 129 */     System.err.println();
/* 130 */     System.err.println("Usage: java VersionFSHierarchyMaker unit|compliance baseDirectory directoryPropPath");
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdb\\util\VersionFSHierarchyMaker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */