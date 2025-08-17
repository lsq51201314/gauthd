/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class CompressedInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private byte[] buffer;
/*     */   private Connection connection;
/*     */   private InputStream in;
/*     */   private Inflater inflater;
/*  59 */   private byte[] packetHeaderBuffer = new byte[7];
/*     */ 
/*     */   
/*  62 */   private int pos = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressedInputStream(Connection conn, InputStream streamFromServer) {
/*  73 */     this.connection = conn;
/*  74 */     this.in = streamFromServer;
/*  75 */     this.inflater = new Inflater();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  82 */     if (this.buffer == null) {
/*  83 */       return this.in.available();
/*     */     }
/*     */     
/*  86 */     return this.buffer.length - this.pos + this.in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  93 */     this.in.close();
/*  94 */     this.buffer = null;
/*  95 */     this.inflater = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void getNextPacketFromServer() throws IOException {
/* 106 */     byte[] uncompressedData = null;
/*     */     
/* 108 */     int lengthRead = readFully(this.packetHeaderBuffer, 0, 7);
/*     */     
/* 110 */     if (lengthRead < 7) {
/* 111 */       throw new IOException("Unexpected end of input stream");
/*     */     }
/*     */     
/* 114 */     int compressedPacketLength = (this.packetHeaderBuffer[0] & 0xFF) + ((this.packetHeaderBuffer[1] & 0xFF) << 8) + ((this.packetHeaderBuffer[2] & 0xFF) << 16);
/*     */ 
/*     */ 
/*     */     
/* 118 */     int uncompressedLength = (this.packetHeaderBuffer[4] & 0xFF) + ((this.packetHeaderBuffer[5] & 0xFF) << 8) + ((this.packetHeaderBuffer[6] & 0xFF) << 16);
/*     */ 
/*     */ 
/*     */     
/* 122 */     if (this.connection.getTraceProtocol()) {
/*     */       try {
/* 124 */         this.connection.getLog().logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength);
/*     */ 
/*     */       
/*     */       }
/* 128 */       catch (SQLException sqlEx) {
/* 129 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 134 */     if (uncompressedLength > 0) {
/* 135 */       uncompressedData = new byte[uncompressedLength];
/*     */       
/* 137 */       byte[] compressedBuffer = new byte[compressedPacketLength];
/*     */       
/* 139 */       readFully(compressedBuffer, 0, compressedPacketLength);
/*     */       
/*     */       try {
/* 142 */         this.inflater.reset();
/* 143 */       } catch (NullPointerException npe) {
/* 144 */         this.inflater = new Inflater();
/*     */       } 
/*     */       
/* 147 */       this.inflater.setInput(compressedBuffer);
/*     */       
/*     */       try {
/* 150 */         this.inflater.inflate(uncompressedData);
/* 151 */       } catch (DataFormatException dfe) {
/* 152 */         throw new IOException("Error while uncompressing packet from server.");
/*     */       } 
/*     */ 
/*     */       
/* 156 */       this.inflater.end();
/*     */     } else {
/* 158 */       if (this.connection.getTraceProtocol()) {
/*     */         try {
/* 160 */           this.connection.getLog().logTrace("Packet didn't meet compression threshold, not uncompressing...");
/*     */ 
/*     */         
/*     */         }
/* 164 */         catch (SQLException sqlEx) {
/* 165 */           throw new IOException(sqlEx.toString());
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 174 */       uncompressedData = new byte[compressedPacketLength];
/* 175 */       readFully(uncompressedData, 0, compressedPacketLength);
/*     */     } 
/*     */     
/* 178 */     if (this.connection.getTraceProtocol()) {
/*     */       try {
/* 180 */         this.connection.getLog().logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, compressedPacketLength));
/*     */ 
/*     */       
/*     */       }
/* 184 */       catch (SQLException sqlEx) {
/* 185 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 190 */     if (this.buffer != null && this.pos < this.buffer.length) {
/* 191 */       if (this.connection.getTraceProtocol()) {
/*     */         try {
/* 193 */           this.connection.getLog().logTrace("Combining remaining packet with new: ");
/*     */         }
/* 195 */         catch (SQLException sqlEx) {
/* 196 */           throw new IOException(sqlEx.toString());
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 201 */       int remaining = this.buffer.length - this.pos;
/* 202 */       byte[] newBuffer = new byte[remaining + uncompressedData.length];
/*     */       
/* 204 */       int newIndex = 0;
/*     */       
/* 206 */       for (int i = this.pos; i < this.buffer.length; i++) {
/* 207 */         newBuffer[newIndex++] = this.buffer[i];
/*     */       }
/* 209 */       System.arraycopy(uncompressedData, 0, newBuffer, newIndex, uncompressedData.length);
/*     */ 
/*     */       
/* 212 */       uncompressedData = newBuffer;
/*     */     } 
/*     */     
/* 215 */     this.pos = 0;
/* 216 */     this.buffer = uncompressedData;
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
/*     */   private void getNextPacketIfRequired(int numBytes) throws IOException {
/* 232 */     if (this.buffer == null || this.pos + numBytes > this.buffer.length)
/*     */     {
/* 234 */       getNextPacketFromServer();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*     */     try {
/* 243 */       getNextPacketIfRequired(1);
/* 244 */     } catch (IOException ioEx) {
/* 245 */       return -1;
/*     */     } 
/*     */     
/* 248 */     return this.buffer[this.pos++] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 255 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 262 */     if (b == null)
/* 263 */       throw new NullPointerException(); 
/* 264 */     if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0)
/*     */     {
/* 266 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 269 */     if (len <= 0) {
/* 270 */       return 0;
/*     */     }
/*     */     
/*     */     try {
/* 274 */       getNextPacketIfRequired(len);
/* 275 */     } catch (IOException ioEx) {
/* 276 */       return -1;
/*     */     } 
/*     */     
/* 279 */     System.arraycopy(this.buffer, this.pos, b, off, len);
/* 280 */     this.pos += len;
/*     */     
/* 282 */     return len;
/*     */   }
/*     */   
/*     */   private final int readFully(byte[] b, int off, int len) throws IOException {
/* 286 */     if (len < 0) {
/* 287 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 290 */     int n = 0;
/*     */     
/* 292 */     while (n < len) {
/* 293 */       int count = this.in.read(b, off + n, len - n);
/*     */       
/* 295 */       if (count < 0) {
/* 296 */         throw new EOFException();
/*     */       }
/*     */       
/* 299 */       n += count;
/*     */     } 
/*     */     
/* 302 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 309 */     long count = 0L;
/*     */     long i;
/* 311 */     for (i = 0L; i < n; i++) {
/* 312 */       int bytesRead = read();
/*     */       
/* 314 */       if (bytesRead == -1) {
/*     */         break;
/*     */       }
/*     */       
/* 318 */       count++;
/*     */     } 
/*     */     
/* 321 */     return count;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\CompressedInputStream.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */