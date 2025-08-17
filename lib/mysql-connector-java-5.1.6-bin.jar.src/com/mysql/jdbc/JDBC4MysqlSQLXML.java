/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLOutputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXResult;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXResult;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4MysqlSQLXML
/*     */   implements SQLXML
/*     */ {
/*     */   private XMLInputFactory inputFactory;
/*     */   private XMLOutputFactory outputFactory;
/*     */   private String stringRep;
/*     */   private ResultSetInternalMethods owningResultSet;
/*     */   private int columnIndexOfXml;
/*     */   private boolean fromResultSet;
/*     */   private boolean isClosed = false;
/*     */   private boolean workingWithResult;
/*     */   private DOMResult asDOMResult;
/*     */   private SAXResult asSAXResult;
/*     */   private SimpleSaxToReader saxToReaderConverter;
/*     */   private StringWriter asStringWriter;
/*     */   private ByteArrayOutputStream asByteArrayOutputStream;
/*     */   
/*     */   protected JDBC4MysqlSQLXML(ResultSetInternalMethods owner, int index) {
/* 101 */     this.owningResultSet = owner;
/* 102 */     this.columnIndexOfXml = index;
/* 103 */     this.fromResultSet = true;
/*     */   }
/*     */   
/*     */   protected JDBC4MysqlSQLXML() {
/* 107 */     this.fromResultSet = false;
/*     */   }
/*     */   
/*     */   public synchronized void free() throws SQLException {
/* 111 */     this.stringRep = null;
/* 112 */     this.asDOMResult = null;
/* 113 */     this.asSAXResult = null;
/* 114 */     this.inputFactory = null;
/* 115 */     this.outputFactory = null;
/* 116 */     this.owningResultSet = null;
/* 117 */     this.workingWithResult = false;
/* 118 */     this.isClosed = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized String getString() throws SQLException {
/* 123 */     checkClosed();
/* 124 */     checkWorkingWithResult();
/*     */     
/* 126 */     if (this.fromResultSet) {
/* 127 */       return this.owningResultSet.getString(this.columnIndexOfXml);
/*     */     }
/*     */     
/* 130 */     return this.stringRep;
/*     */   }
/*     */   
/*     */   private synchronized void checkClosed() throws SQLException {
/* 134 */     if (this.isClosed) {
/* 135 */       throw SQLError.createSQLException("SQLXMLInstance has been free()d");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private synchronized void checkWorkingWithResult() throws SQLException {
/* 141 */     if (this.workingWithResult) {
/* 142 */       throw SQLError.createSQLException("Can't perform requested operation after getResult() has been called to write XML data", "S1009");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setString(String str) throws SQLException {
/* 176 */     checkClosed();
/* 177 */     checkWorkingWithResult();
/*     */     
/* 179 */     this.stringRep = str;
/* 180 */     this.fromResultSet = false;
/*     */   }
/*     */   
/*     */   public synchronized boolean isEmpty() throws SQLException {
/* 184 */     checkClosed();
/* 185 */     checkWorkingWithResult();
/*     */     
/* 187 */     if (!this.fromResultSet) {
/* 188 */       return (this.stringRep == null || this.stringRep.length() == 0);
/*     */     }
/*     */     
/* 191 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized InputStream getBinaryStream() throws SQLException {
/* 195 */     checkClosed();
/* 196 */     checkWorkingWithResult();
/*     */     
/* 198 */     return this.owningResultSet.getBinaryStream(this.columnIndexOfXml);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Reader getCharacterStream() throws SQLException {
/* 227 */     checkClosed();
/* 228 */     checkWorkingWithResult();
/*     */     
/* 230 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Source getSource(Class clazz) throws SQLException {
/* 282 */     checkClosed();
/* 283 */     checkWorkingWithResult();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 289 */     if (clazz == null || clazz.equals(SAXSource.class)) {
/*     */       
/* 291 */       InputSource inputSource = null;
/*     */       
/* 293 */       if (this.fromResultSet) {
/* 294 */         inputSource = new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml));
/*     */       } else {
/*     */         
/* 297 */         inputSource = new InputSource(new StringReader(this.stringRep));
/*     */       } 
/*     */       
/* 300 */       return new SAXSource(inputSource);
/* 301 */     }  if (clazz.equals(DOMSource.class)) {
/*     */       try {
/* 303 */         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
/*     */         
/* 305 */         builderFactory.setNamespaceAware(true);
/* 306 */         DocumentBuilder builder = builderFactory.newDocumentBuilder();
/*     */         
/* 308 */         InputSource inputSource = null;
/*     */         
/* 310 */         if (this.fromResultSet) {
/* 311 */           inputSource = new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml));
/*     */         } else {
/*     */           
/* 314 */           inputSource = new InputSource(new StringReader(this.stringRep));
/*     */         } 
/*     */ 
/*     */         
/* 318 */         return new DOMSource(builder.parse(inputSource));
/* 319 */       } catch (Throwable t) {
/* 320 */         SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009");
/*     */         
/* 322 */         sqlEx.initCause(t);
/*     */         
/* 324 */         throw sqlEx;
/*     */       } 
/*     */     }
/* 327 */     if (clazz.equals(StreamSource.class)) {
/* 328 */       Reader reader = null;
/*     */       
/* 330 */       if (this.fromResultSet) {
/* 331 */         reader = this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */       } else {
/*     */         
/* 334 */         reader = new StringReader(this.stringRep);
/*     */       } 
/*     */       
/* 337 */       return new StreamSource(reader);
/* 338 */     }  if (clazz.equals(StAXSource.class)) {
/*     */       try {
/* 340 */         Reader reader = null;
/*     */         
/* 342 */         if (this.fromResultSet) {
/* 343 */           reader = this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */         } else {
/*     */           
/* 346 */           reader = new StringReader(this.stringRep);
/*     */         } 
/*     */         
/* 349 */         return new StAXSource(this.inputFactory.createXMLStreamReader(reader));
/*     */       }
/* 351 */       catch (XMLStreamException ex) {
/* 352 */         SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009");
/*     */         
/* 354 */         sqlEx.initCause(ex);
/*     */         
/* 356 */         throw sqlEx;
/*     */       } 
/*     */     }
/* 359 */     throw SQLError.createSQLException("XML Source of type \"" + clazz.toString() + "\" Not supported.", "S1009");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized OutputStream setBinaryStream() throws SQLException {
/* 385 */     checkClosed();
/* 386 */     checkWorkingWithResult();
/*     */     
/* 388 */     this.workingWithResult = true;
/*     */     
/* 390 */     return setBinaryStreamInternal();
/*     */   }
/*     */ 
/*     */   
/*     */   private synchronized OutputStream setBinaryStreamInternal() throws SQLException {
/* 395 */     this.asByteArrayOutputStream = new ByteArrayOutputStream();
/*     */     
/* 397 */     return this.asByteArrayOutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Writer setCharacterStream() throws SQLException {
/* 426 */     checkClosed();
/* 427 */     checkWorkingWithResult();
/*     */     
/* 429 */     this.workingWithResult = true;
/*     */     
/* 431 */     return setCharacterStreamInternal();
/*     */   }
/*     */ 
/*     */   
/*     */   private synchronized Writer setCharacterStreamInternal() throws SQLException {
/* 436 */     this.asStringWriter = new StringWriter();
/*     */     
/* 438 */     return this.asStringWriter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Result setResult(Class clazz) throws SQLException {
/* 487 */     checkClosed();
/* 488 */     checkWorkingWithResult();
/*     */     
/* 490 */     this.workingWithResult = true;
/* 491 */     this.asDOMResult = null;
/* 492 */     this.asSAXResult = null;
/* 493 */     this.saxToReaderConverter = null;
/* 494 */     this.stringRep = null;
/* 495 */     this.asStringWriter = null;
/* 496 */     this.asByteArrayOutputStream = null;
/*     */     
/* 498 */     if (clazz == null || clazz.equals(SAXResult.class)) {
/* 499 */       this.saxToReaderConverter = new SimpleSaxToReader();
/*     */       
/* 501 */       this.asSAXResult = new SAXResult(this.saxToReaderConverter);
/*     */       
/* 503 */       return this.asSAXResult;
/* 504 */     }  if (clazz.equals(DOMResult.class)) {
/*     */       
/* 506 */       this.asDOMResult = new DOMResult();
/* 507 */       return this.asDOMResult;
/*     */     } 
/* 509 */     if (clazz.equals(StreamResult.class))
/* 510 */       return new StreamResult(setCharacterStreamInternal()); 
/* 511 */     if (clazz.equals(StAXResult.class)) {
/*     */       try {
/* 513 */         if (this.outputFactory == null) {
/* 514 */           this.outputFactory = XMLOutputFactory.newInstance();
/*     */         }
/*     */         
/* 517 */         return new StAXResult(this.outputFactory.createXMLEventWriter(setCharacterStreamInternal()));
/*     */       }
/* 519 */       catch (XMLStreamException ex) {
/* 520 */         SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009");
/*     */         
/* 522 */         sqlEx.initCause(ex);
/*     */         
/* 524 */         throw sqlEx;
/*     */       } 
/*     */     }
/* 527 */     throw SQLError.createSQLException("XML Result of type \"" + clazz.toString() + "\" Not supported.", "S1009");
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
/*     */   private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out) {
/*     */     try {
/* 542 */       String encoding = "UTF-8";
/*     */       
/*     */       try {
/* 545 */         ByteArrayInputStream bIn = new ByteArrayInputStream(out.toByteArray());
/*     */         
/* 547 */         XMLStreamReader reader = this.inputFactory.createXMLStreamReader(bIn);
/*     */ 
/*     */         
/* 550 */         int eventType = 0;
/*     */         
/* 552 */         while ((eventType = reader.next()) != 8) {
/* 553 */           if (eventType == 7) {
/* 554 */             String possibleEncoding = reader.getEncoding();
/*     */             
/* 556 */             if (possibleEncoding != null) {
/* 557 */               encoding = possibleEncoding;
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 563 */       } catch (Throwable t) {}
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 568 */       return new StringReader(new String(out.toByteArray(), encoding));
/* 569 */     } catch (UnsupportedEncodingException badEnc) {
/* 570 */       throw new RuntimeException(badEnc);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected String readerToString(Reader reader) throws SQLException {
/* 575 */     StringBuffer buf = new StringBuffer();
/*     */     
/* 577 */     int charsRead = 0;
/*     */     
/* 579 */     char[] charBuf = new char[512];
/*     */     
/*     */     try {
/* 582 */       while ((charsRead = reader.read(charBuf)) != -1) {
/* 583 */         buf.append(charBuf, 0, charsRead);
/*     */       }
/* 585 */     } catch (IOException ioEx) {
/* 586 */       SQLException sqlEx = SQLError.createSQLException(ioEx.getMessage(), "S1009");
/*     */       
/* 588 */       sqlEx.initCause(ioEx);
/*     */       
/* 590 */       throw sqlEx;
/*     */     } 
/*     */     
/* 593 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   protected synchronized Reader serializeAsCharacterStream() throws SQLException {
/* 598 */     checkClosed();
/* 599 */     if (this.workingWithResult) {
/*     */       
/* 601 */       if (this.stringRep != null) {
/* 602 */         return new StringReader(this.stringRep);
/*     */       }
/*     */       
/* 605 */       if (this.asDOMResult != null) {
/* 606 */         return new StringReader(domSourceToString());
/*     */       }
/*     */       
/* 609 */       if (this.asStringWriter != null) {
/* 610 */         return new StringReader(this.asStringWriter.toString());
/*     */       }
/*     */       
/* 613 */       if (this.asSAXResult != null) {
/* 614 */         return this.saxToReaderConverter.toReader();
/*     */       }
/*     */       
/* 617 */       if (this.asByteArrayOutputStream != null) {
/* 618 */         return binaryInputStreamStreamToReader(this.asByteArrayOutputStream);
/*     */       }
/*     */     } 
/*     */     
/* 622 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */   }
/*     */   
/*     */   protected String domSourceToString() throws SQLException {
/*     */     try {
/* 627 */       DOMSource source = new DOMSource(this.asDOMResult.getNode());
/* 628 */       Transformer identity = TransformerFactory.newInstance().newTransformer();
/*     */       
/* 630 */       StringWriter stringOut = new StringWriter();
/* 631 */       Result result = new StreamResult(stringOut);
/* 632 */       identity.transform(source, result);
/*     */       
/* 634 */       return stringOut.toString();
/* 635 */     } catch (Throwable t) {
/* 636 */       SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009");
/*     */       
/* 638 */       sqlEx.initCause(t);
/*     */       
/* 640 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected synchronized String serializeAsString() throws SQLException {
/* 645 */     checkClosed();
/* 646 */     if (this.workingWithResult) {
/*     */       
/* 648 */       if (this.stringRep != null) {
/* 649 */         return this.stringRep;
/*     */       }
/*     */       
/* 652 */       if (this.asDOMResult != null) {
/* 653 */         return domSourceToString();
/*     */       }
/*     */       
/* 656 */       if (this.asStringWriter != null) {
/* 657 */         return this.asStringWriter.toString();
/*     */       }
/*     */       
/* 660 */       if (this.asSAXResult != null) {
/* 661 */         return readerToString(this.saxToReaderConverter.toReader());
/*     */       }
/*     */       
/* 664 */       if (this.asByteArrayOutputStream != null) {
/* 665 */         return readerToString(binaryInputStreamStreamToReader(this.asByteArrayOutputStream));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 670 */     return this.owningResultSet.getString(this.columnIndexOfXml);
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
/*     */ 
/*     */ 
/*     */   
/*     */   class SimpleSaxToReader
/*     */     extends DefaultHandler
/*     */   {
/* 695 */     StringBuffer buf = new StringBuffer();
/*     */     
/*     */     public void startDocument() throws SAXException {
/* 698 */       this.buf.append("<?xml version='1.0' encoding='UTF-8'?>");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void endDocument() throws SAXException {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
/* 708 */       this.buf.append("<");
/* 709 */       this.buf.append(qName);
/*     */       
/* 711 */       if (attrs != null) {
/* 712 */         for (int i = 0; i < attrs.getLength(); i++) {
/* 713 */           this.buf.append(" ");
/* 714 */           this.buf.append(attrs.getQName(i)).append("=\"");
/* 715 */           escapeCharsForXml(attrs.getValue(i), true);
/* 716 */           this.buf.append("\"");
/*     */         } 
/*     */       }
/*     */       
/* 720 */       this.buf.append(">");
/*     */     }
/*     */ 
/*     */     
/*     */     public void characters(char[] buf, int offset, int len) throws SAXException {
/* 725 */       if (!this.inCDATA) {
/* 726 */         escapeCharsForXml(buf, offset, len, false);
/*     */       } else {
/* 728 */         this.buf.append(buf, offset, len);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
/* 734 */       characters(ch, start, length);
/*     */     }
/*     */     
/*     */     private boolean inCDATA = false;
/*     */     
/*     */     public void startCDATA() throws SAXException {
/* 740 */       this.buf.append("<![CDATA[");
/* 741 */       this.inCDATA = true;
/*     */     }
/*     */     
/*     */     public void endCDATA() throws SAXException {
/* 745 */       this.inCDATA = false;
/* 746 */       this.buf.append("]]>");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void comment(char[] ch, int start, int length) throws SAXException {
/* 752 */       this.buf.append("<!--");
/* 753 */       for (int i = 0; i < length; i++) {
/* 754 */         this.buf.append(ch[start + i]);
/*     */       }
/* 756 */       this.buf.append("-->");
/*     */     }
/*     */ 
/*     */     
/*     */     Reader toReader() {
/* 761 */       return new StringReader(this.buf.toString());
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(String str, boolean isAttributeData) {
/* 765 */       if (str == null) {
/*     */         return;
/*     */       }
/*     */       
/* 769 */       int strLen = str.length();
/*     */       
/* 771 */       for (int i = 0; i < strLen; i++) {
/* 772 */         escapeCharsForXml(str.charAt(i), isAttributeData);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private void escapeCharsForXml(char[] buf, int offset, int len, boolean isAttributeData) {
/* 779 */       if (buf == null) {
/*     */         return;
/*     */       }
/*     */       
/* 783 */       for (int i = 0; i < len; i++) {
/* 784 */         escapeCharsForXml(buf[offset + i], isAttributeData);
/*     */       }
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(char c, boolean isAttributeData) {
/* 789 */       switch (c) {
/*     */         case '<':
/* 791 */           this.buf.append("&lt;");
/*     */           return;
/*     */         
/*     */         case '>':
/* 795 */           this.buf.append("&gt;");
/*     */           return;
/*     */         
/*     */         case '&':
/* 799 */           this.buf.append("&amp;");
/*     */           return;
/*     */ 
/*     */         
/*     */         case '"':
/* 804 */           if (!isAttributeData) {
/* 805 */             this.buf.append("\"");
/*     */           } else {
/*     */             
/* 808 */             this.buf.append("&quot;");
/*     */           } 
/*     */           return;
/*     */ 
/*     */         
/*     */         case '\r':
/* 814 */           this.buf.append("&#xD;");
/*     */           return;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 820 */       if ((c >= '\001' && c <= '\037' && c != '\t' && c != '\n') || (c >= '' && c <= '') || c == ' ' || (isAttributeData && (c == '\t' || c == '\n'))) {
/*     */ 
/*     */         
/* 823 */         this.buf.append("&#x");
/* 824 */         this.buf.append(Integer.toHexString(c).toUpperCase());
/* 825 */         this.buf.append(";");
/*     */       } else {
/*     */         
/* 828 */         this.buf.append(c);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\JDBC4MysqlSQLXML.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */