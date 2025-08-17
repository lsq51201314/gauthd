/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
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
/*     */ public class NamedPipeSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*     */   private static final String NAMED_PIPE_PROP_NAME = "namedPipePath";
/*     */   private Socket namedPipeSocket;
/*     */   
/*     */   class NamedPipeSocket
/*     */     extends Socket
/*     */   {
/*     */     private boolean isClosed;
/*     */     private RandomAccessFile namedPipeFile;
/*     */     private final NamedPipeSocketFactory this$0;
/*     */     
/*     */     NamedPipeSocket(NamedPipeSocketFactory this$0, String filePath) throws IOException {
/*  49 */       this.this$0 = this$0; this.isClosed = false;
/*  50 */       if (filePath == null || filePath.length() == 0) {
/*  51 */         throw new IOException(Messages.getString("NamedPipeSocketFactory.4"));
/*     */       }
/*     */ 
/*     */       
/*  55 */       this.namedPipeFile = new RandomAccessFile(filePath, "rw");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized void close() throws IOException {
/*  62 */       this.namedPipeFile.close();
/*  63 */       this.isClosed = true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public InputStream getInputStream() throws IOException {
/*  70 */       return new NamedPipeSocketFactory.RandomAccessFileInputStream(this.this$0, this.namedPipeFile);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OutputStream getOutputStream() throws IOException {
/*  77 */       return new NamedPipeSocketFactory.RandomAccessFileOutputStream(this.this$0, this.namedPipeFile);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isClosed() {
/*  84 */       return this.isClosed;
/*     */     }
/*     */   }
/*     */   
/*     */   class RandomAccessFileInputStream
/*     */     extends InputStream {
/*     */     RandomAccessFile raFile;
/*     */     private final NamedPipeSocketFactory this$0;
/*     */     
/*     */     RandomAccessFileInputStream(NamedPipeSocketFactory this$0, RandomAccessFile file) {
/*  94 */       this.this$0 = this$0;
/*  95 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int available() throws IOException {
/* 102 */       return -1;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 109 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 116 */       return this.raFile.read();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b) throws IOException {
/* 123 */       return this.raFile.read(b);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 130 */       return this.raFile.read(b, off, len);
/*     */     }
/*     */   }
/*     */   
/*     */   class RandomAccessFileOutputStream
/*     */     extends OutputStream {
/*     */     RandomAccessFile raFile;
/*     */     private final NamedPipeSocketFactory this$0;
/*     */     
/*     */     RandomAccessFileOutputStream(NamedPipeSocketFactory this$0, RandomAccessFile file) {
/* 140 */       this.this$0 = this$0;
/* 141 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 148 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] b) throws IOException {
/* 155 */       this.raFile.write(b);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 162 */       this.raFile.write(b, off, len);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(int b) throws IOException {}
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
/*     */   public Socket afterHandshake() throws SocketException, IOException {
/* 187 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket beforeHandshake() throws SocketException, IOException {
/* 194 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket connect(String host, int portNumber, Properties props) throws SocketException, IOException {
/* 202 */     String namedPipePath = props.getProperty("namedPipePath");
/*     */     
/* 204 */     if (namedPipePath == null) {
/* 205 */       namedPipePath = "\\\\.\\pipe\\MySQL";
/* 206 */     } else if (namedPipePath.length() == 0) {
/* 207 */       throw new SocketException(Messages.getString("NamedPipeSocketFactory.2") + "namedPipePath" + Messages.getString("NamedPipeSocketFactory.3"));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     this.namedPipeSocket = new NamedPipeSocket(this, namedPipePath);
/*     */     
/* 215 */     return this.namedPipeSocket;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\NamedPipeSocketFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */