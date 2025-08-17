/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerController
/*     */ {
/*     */   public static final String BASEDIR_KEY = "basedir";
/*     */   public static final String DATADIR_KEY = "datadir";
/*     */   public static final String DEFAULTS_FILE_KEY = "defaults-file";
/*     */   public static final String EXECUTABLE_NAME_KEY = "executable";
/*     */   public static final String EXECUTABLE_PATH_KEY = "executablePath";
/*  78 */   private Process serverProcess = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private Properties serverProps = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   private Properties systemProps = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerController(String baseDir) {
/*  99 */     setBaseDir(baseDir);
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
/*     */   public ServerController(String basedir, String datadir) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBaseDir(String baseDir) {
/* 121 */     getServerProps().setProperty("basedir", baseDir);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDataDir(String dataDir) {
/* 131 */     getServerProps().setProperty("datadir", dataDir);
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
/*     */   public Process start() throws IOException {
/* 144 */     if (this.serverProcess != null) {
/* 145 */       throw new IllegalArgumentException("Server already started");
/*     */     }
/* 147 */     this.serverProcess = Runtime.getRuntime().exec(getCommandLine());
/*     */     
/* 149 */     return this.serverProcess;
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
/*     */   public void stop(boolean forceIfNecessary) throws IOException {
/* 163 */     if (this.serverProcess != null) {
/*     */       
/* 165 */       String basedir = getServerProps().getProperty("basedir");
/*     */       
/* 167 */       StringBuffer pathBuf = new StringBuffer(basedir);
/*     */       
/* 169 */       if (!basedir.endsWith(File.separator)) {
/* 170 */         pathBuf.append(File.separator);
/*     */       }
/*     */       
/* 173 */       String defaultsFilePath = getServerProps().getProperty("defaults-file");
/*     */ 
/*     */       
/* 176 */       pathBuf.append("bin");
/* 177 */       pathBuf.append(File.separator);
/* 178 */       pathBuf.append("mysqladmin shutdown");
/*     */       
/* 180 */       System.out.println(pathBuf.toString());
/*     */       
/* 182 */       Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
/*     */       
/* 184 */       int exitStatus = -1;
/*     */       
/*     */       try {
/* 187 */         exitStatus = mysqladmin.waitFor();
/* 188 */       } catch (InterruptedException ie) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 196 */       if (exitStatus != 0 && forceIfNecessary) {
/* 197 */         forceStop();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forceStop() {
/* 206 */     if (this.serverProcess != null) {
/* 207 */       this.serverProcess.destroy();
/* 208 */       this.serverProcess = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Properties getServerProps() {
/* 219 */     if (this.serverProps == null) {
/* 220 */       this.serverProps = new Properties();
/*     */     }
/*     */     
/* 223 */     return this.serverProps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getCommandLine() {
/* 233 */     StringBuffer commandLine = new StringBuffer(getFullExecutablePath());
/* 234 */     commandLine.append(buildOptionalCommandLine());
/*     */     
/* 236 */     return commandLine.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getFullExecutablePath() {
/* 245 */     StringBuffer pathBuf = new StringBuffer();
/*     */     
/* 247 */     String optionalExecutablePath = getServerProps().getProperty("executablePath");
/*     */ 
/*     */     
/* 250 */     if (optionalExecutablePath == null) {
/*     */       
/* 252 */       String basedir = getServerProps().getProperty("basedir");
/* 253 */       pathBuf.append(basedir);
/*     */       
/* 255 */       if (!basedir.endsWith(File.separator)) {
/* 256 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */       
/* 259 */       if (runningOnWindows()) {
/* 260 */         pathBuf.append("bin");
/*     */       } else {
/* 262 */         pathBuf.append("libexec");
/*     */       } 
/*     */       
/* 265 */       pathBuf.append(File.separatorChar);
/*     */     } else {
/* 267 */       pathBuf.append(optionalExecutablePath);
/*     */       
/* 269 */       if (!optionalExecutablePath.endsWith(File.separator)) {
/* 270 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */     } 
/*     */     
/* 274 */     String executableName = getServerProps().getProperty("executable", "mysqld");
/*     */ 
/*     */     
/* 277 */     pathBuf.append(executableName);
/*     */     
/* 279 */     return pathBuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String buildOptionalCommandLine() {
/* 289 */     StringBuffer commandLineBuf = new StringBuffer();
/*     */     
/* 291 */     if (this.serverProps != null) {
/*     */       
/* 293 */       Iterator iter = this.serverProps.keySet().iterator();
/* 294 */       while (iter.hasNext()) {
/* 295 */         String key = iter.next();
/* 296 */         String value = this.serverProps.getProperty(key);
/*     */         
/* 298 */         if (!isNonCommandLineArgument(key)) {
/* 299 */           if (value != null && value.length() > 0) {
/* 300 */             commandLineBuf.append(" \"");
/* 301 */             commandLineBuf.append("--");
/* 302 */             commandLineBuf.append(key);
/* 303 */             commandLineBuf.append("=");
/* 304 */             commandLineBuf.append(value);
/* 305 */             commandLineBuf.append("\""); continue;
/*     */           } 
/* 307 */           commandLineBuf.append(" --");
/* 308 */           commandLineBuf.append(key);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 314 */     return commandLineBuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isNonCommandLineArgument(String propName) {
/* 323 */     return (propName.equals("executable") || propName.equals("executablePath"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized Properties getSystemProperties() {
/* 333 */     if (this.systemProps == null) {
/* 334 */       this.systemProps = System.getProperties();
/*     */     }
/*     */     
/* 337 */     return this.systemProps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean runningOnWindows() {
/* 346 */     return (StringUtils.indexOfIgnoreCase(getSystemProperties().getProperty("os.name"), "WINDOWS") != -1);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdb\\util\ServerController.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */