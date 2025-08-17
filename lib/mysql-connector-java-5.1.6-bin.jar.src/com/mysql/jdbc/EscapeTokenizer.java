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
/*     */ public class EscapeTokenizer
/*     */ {
/*  36 */   private int bracesLevel = 0;
/*     */   
/*     */   private boolean emittingEscapeCode = false;
/*     */   
/*     */   private boolean inComment = false;
/*     */   
/*     */   private boolean inQuotes = false;
/*     */   
/*  44 */   private char lastChar = Character.MIN_VALUE;
/*     */   
/*  46 */   private char lastLastChar = Character.MIN_VALUE;
/*     */   
/*  48 */   private int pos = 0;
/*     */   
/*  50 */   private char quoteChar = Character.MIN_VALUE;
/*     */   
/*     */   private boolean sawVariableUse = false;
/*     */   
/*  54 */   private String source = null;
/*     */   
/*  56 */   private int sourceLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EscapeTokenizer(String s) {
/*  68 */     this.source = s;
/*  69 */     this.sourceLength = s.length();
/*  70 */     this.pos = 0;
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
/*     */   public synchronized boolean hasMoreTokens() {
/*  82 */     return (this.pos < this.sourceLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String nextToken() {
/*  91 */     StringBuffer tokenBuf = new StringBuffer();
/*     */     
/*  93 */     if (this.emittingEscapeCode) {
/*  94 */       tokenBuf.append("{");
/*  95 */       this.emittingEscapeCode = false;
/*     */     } 
/*     */     
/*  98 */     for (; this.pos < this.sourceLength; this.pos++) {
/*  99 */       char c = this.source.charAt(this.pos);
/*     */ 
/*     */ 
/*     */       
/* 103 */       if (!this.inQuotes && c == '@') {
/* 104 */         this.sawVariableUse = true;
/*     */       }
/*     */       
/* 107 */       if (c == '\'' || c == '"')
/* 108 */       { if (this.inQuotes && c == this.quoteChar && 
/* 109 */           this.pos + 1 < this.sourceLength && 
/* 110 */           this.source.charAt(this.pos + 1) == this.quoteChar)
/*     */         
/* 112 */         { tokenBuf.append(this.quoteChar);
/* 113 */           tokenBuf.append(this.quoteChar);
/* 114 */           this.pos++;
/*     */            }
/*     */         
/*     */         else
/*     */         
/* 119 */         { if (this.lastChar != '\\') {
/* 120 */             if (this.inQuotes) {
/* 121 */               if (this.quoteChar == c) {
/* 122 */                 this.inQuotes = false;
/*     */               }
/*     */             } else {
/* 125 */               this.inQuotes = true;
/* 126 */               this.quoteChar = c;
/*     */             } 
/* 128 */           } else if (this.lastLastChar == '\\') {
/* 129 */             if (this.inQuotes) {
/* 130 */               if (this.quoteChar == c) {
/* 131 */                 this.inQuotes = false;
/*     */               }
/*     */             } else {
/* 134 */               this.inQuotes = true;
/* 135 */               this.quoteChar = c;
/*     */             } 
/*     */           } 
/*     */           
/* 139 */           tokenBuf.append(c);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 184 */           this.lastLastChar = this.lastChar;
/* 185 */           this.lastChar = c; }  } else { if (c == '-') { if (this.lastChar == '-' && this.lastLastChar != '\\' && !this.inQuotes) this.inComment = true;  tokenBuf.append(c); } else if (c == '\n' || c == '\r') { this.inComment = false; tokenBuf.append(c); } else if (c == '{') { if (this.inQuotes || this.inComment) { tokenBuf.append(c); } else { this.bracesLevel++; if (this.bracesLevel == 1) { this.pos++; this.emittingEscapeCode = true; return tokenBuf.toString(); }  tokenBuf.append(c); }  } else if (c == '}') { tokenBuf.append(c); if (!this.inQuotes && !this.inComment) { this.lastChar = c; this.bracesLevel--; if (this.bracesLevel == 0) { this.pos++; return tokenBuf.toString(); }  }  } else { tokenBuf.append(c); }  this.lastLastChar = this.lastChar; this.lastChar = c; }
/*     */     
/*     */     } 
/* 188 */     return tokenBuf.toString();
/*     */   }
/*     */   
/*     */   boolean sawVariableUse() {
/* 192 */     return this.sawVariableUse;
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\EscapeTokenizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */