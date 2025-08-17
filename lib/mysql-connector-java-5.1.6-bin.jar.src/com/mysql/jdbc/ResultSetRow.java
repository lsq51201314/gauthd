/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.Date;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class ResultSetRow
/*      */ {
/*      */   protected Field[] metadata;
/*      */   
/*      */   public abstract void closeOpenStreams();
/*      */   
/*      */   public abstract InputStream getBinaryInputStream(int paramInt) throws SQLException;
/*      */   
/*      */   public abstract byte[] getColumnValue(int paramInt) throws SQLException;
/*      */   
/*      */   protected final Date getDateFast(int columnIndex, byte[] dateAsBytes, int offset, int length, ConnectionImpl conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
/*   91 */     int year = 0;
/*   92 */     int month = 0;
/*   93 */     int day = 0;
/*      */     
/*      */     try {
/*   96 */       if (dateAsBytes == null) {
/*   97 */         return null;
/*      */       }
/*      */       
/*  100 */       boolean allZeroDate = true;
/*      */       
/*  102 */       boolean onlyTimePresent = false;
/*      */       int i;
/*  104 */       for (i = 0; i < length; i++) {
/*  105 */         if (dateAsBytes[offset + i] == 58) {
/*  106 */           onlyTimePresent = true;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*  111 */       for (i = 0; i < length; i++) {
/*  112 */         byte b = dateAsBytes[offset + i];
/*      */         
/*  114 */         if (b == 32 || b == 45 || b == 47) {
/*  115 */           onlyTimePresent = false;
/*      */         }
/*      */         
/*  118 */         if (b != 48 && b != 32 && b != 58 && b != 45 && b != 47 && b != 46) {
/*      */           
/*  120 */           allZeroDate = false;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */       
/*  126 */       if (!onlyTimePresent && allZeroDate) {
/*      */         
/*  128 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*      */           
/*  131 */           return null; } 
/*  132 */         if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  134 */           throw SQLError.createSQLException("Value '" + new String(dateAsBytes) + "' can not be represented as java.sql.Date", "S1009");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  142 */         return rs.fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       } 
/*  144 */       if (this.metadata[columnIndex].getMysqlType() == 7) {
/*      */         
/*  146 */         switch (length) {
/*      */           case 19:
/*      */           case 21:
/*  149 */             year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */             
/*  151 */             month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*      */             
/*  153 */             day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */ 
/*      */             
/*  156 */             return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */ 
/*      */           
/*      */           case 8:
/*      */           case 14:
/*  161 */             year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */             
/*  163 */             month = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */             
/*  165 */             day = StringUtils.getInt(dateAsBytes, offset + 6, offset + 8);
/*      */ 
/*      */             
/*  168 */             return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */ 
/*      */           
/*      */           case 6:
/*      */           case 10:
/*      */           case 12:
/*  174 */             year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/*  177 */             if (year <= 69) {
/*  178 */               year += 100;
/*      */             }
/*      */             
/*  181 */             month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */             
/*  183 */             day = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */ 
/*      */             
/*  186 */             return rs.fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */ 
/*      */           
/*      */           case 4:
/*  190 */             year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*      */             
/*  193 */             if (year <= 69) {
/*  194 */               year += 100;
/*      */             }
/*      */             
/*  197 */             month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */ 
/*      */             
/*  200 */             return rs.fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */ 
/*      */           
/*      */           case 2:
/*  204 */             year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/*  207 */             if (year <= 69) {
/*  208 */               year += 100;
/*      */             }
/*      */             
/*  211 */             return rs.fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         } 
/*      */ 
/*      */         
/*  215 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { new String(dateAsBytes), Constants.integerValueOf(columnIndex + 1) }), "S1009");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  227 */       if (this.metadata[columnIndex].getMysqlType() == 13) {
/*      */         
/*  229 */         if (length == 2 || length == 1) {
/*  230 */           year = StringUtils.getInt(dateAsBytes, offset, offset + length);
/*      */ 
/*      */           
/*  233 */           if (year <= 69) {
/*  234 */             year += 100;
/*      */           }
/*      */           
/*  237 */           year += 1900;
/*      */         } else {
/*  239 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */         } 
/*      */ 
/*      */         
/*  243 */         return rs.fastDateCreate(targetCalendar, year, 1, 1);
/*  244 */       }  if (this.metadata[columnIndex].getMysqlType() == 11) {
/*  245 */         return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/*  247 */       if (length < 10) {
/*  248 */         if (length == 8) {
/*  249 */           return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  254 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { new String(dateAsBytes), Constants.integerValueOf(columnIndex + 1) }), "S1009");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  267 */       if (length != 18) {
/*  268 */         year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */         
/*  270 */         month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*      */         
/*  272 */         day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  277 */         StringTokenizer st = new StringTokenizer(new String(dateAsBytes, offset, length, "ISO8859_1"), "- ");
/*      */ 
/*      */         
/*  280 */         year = Integer.parseInt(st.nextToken());
/*  281 */         month = Integer.parseInt(st.nextToken());
/*  282 */         day = Integer.parseInt(st.nextToken());
/*      */       } 
/*      */ 
/*      */       
/*  286 */       return rs.fastDateCreate(targetCalendar, year, month, day);
/*  287 */     } catch (SQLException sqlEx) {
/*  288 */       throw sqlEx;
/*  289 */     } catch (Exception e) {
/*  290 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { new String(dateAsBytes), Constants.integerValueOf(columnIndex + 1) }), "S1009");
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  295 */       sqlEx.initCause(e);
/*      */       
/*  297 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Date getDateFast(int paramInt, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl, Calendar paramCalendar) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int getInt(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long getLong(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Date getNativeDate(int columnIndex, byte[] bits, int offset, int length, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*  332 */     int year = 0;
/*  333 */     int month = 0;
/*  334 */     int day = 0;
/*      */     
/*  336 */     if (length != 0) {
/*  337 */       year = bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8;
/*      */       
/*  339 */       month = bits[offset + 2];
/*  340 */       day = bits[offset + 3];
/*      */     } 
/*      */     
/*  343 */     if (year == 0 && month == 0 && day == 0) {
/*  344 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  346 */         return null; } 
/*  347 */       if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  349 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  355 */       year = 1;
/*  356 */       month = 1;
/*  357 */       day = 1;
/*      */     } 
/*      */     
/*  360 */     if (!rs.useLegacyDatetimeCode) {
/*  361 */       return TimeUtil.fastDateCreate(year, month, day, null);
/*      */     }
/*      */     
/*  364 */     return rs.fastDateCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Date getNativeDate(int paramInt, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object getNativeDateTimeValue(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*  376 */     int year = 0;
/*  377 */     int month = 0;
/*  378 */     int day = 0;
/*      */     
/*  380 */     int hour = 0;
/*  381 */     int minute = 0;
/*  382 */     int seconds = 0;
/*      */     
/*  384 */     int nanos = 0;
/*      */     
/*  386 */     if (bits == null)
/*      */     {
/*  388 */       return null;
/*      */     }
/*      */     
/*  391 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */     
/*  395 */     boolean populatedFromDateTimeValue = false;
/*      */     
/*  397 */     switch (mysqlType) {
/*      */       case 7:
/*      */       case 12:
/*  400 */         populatedFromDateTimeValue = true;
/*      */         
/*  402 */         if (length != 0) {
/*  403 */           year = bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8;
/*      */           
/*  405 */           month = bits[offset + 2];
/*  406 */           day = bits[offset + 3];
/*      */           
/*  408 */           if (length > 4) {
/*  409 */             hour = bits[offset + 4];
/*  410 */             minute = bits[offset + 5];
/*  411 */             seconds = bits[offset + 6];
/*      */           } 
/*      */           
/*  414 */           if (length > 7)
/*      */           {
/*  416 */             nanos = (bits[offset + 7] & 0xFF | (bits[offset + 8] & 0xFF) << 8 | (bits[offset + 9] & 0xFF) << 16 | (bits[offset + 10] & 0xFF) << 24) * 1000;
/*      */           }
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 10:
/*  424 */         populatedFromDateTimeValue = true;
/*      */         
/*  426 */         if (bits.length != 0) {
/*  427 */           year = bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8;
/*      */           
/*  429 */           month = bits[offset + 2];
/*  430 */           day = bits[offset + 3];
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 11:
/*  435 */         populatedFromDateTimeValue = true;
/*      */         
/*  437 */         if (bits.length != 0) {
/*      */ 
/*      */           
/*  440 */           hour = bits[offset + 5];
/*  441 */           minute = bits[offset + 6];
/*  442 */           seconds = bits[offset + 7];
/*      */         } 
/*      */         
/*  445 */         year = 1970;
/*  446 */         month = 1;
/*  447 */         day = 1;
/*      */         break;
/*      */       
/*      */       default:
/*  451 */         populatedFromDateTimeValue = false;
/*      */         break;
/*      */     } 
/*  454 */     switch (jdbcType) {
/*      */       case 92:
/*  456 */         if (populatedFromDateTimeValue) {
/*  457 */           if (!rs.useLegacyDatetimeCode) {
/*  458 */             return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar);
/*      */           }
/*      */           
/*  461 */           Time time = TimeUtil.fastTimeCreate(rs.getCalendarInstanceForSessionOrNew(), hour, minute, seconds);
/*      */ 
/*      */ 
/*      */           
/*  465 */           Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */ 
/*      */           
/*  469 */           return adjustedTime;
/*      */         } 
/*      */         
/*  472 */         return rs.getNativeTimeViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */ 
/*      */       
/*      */       case 91:
/*  476 */         if (populatedFromDateTimeValue) {
/*  477 */           if (year == 0 && month == 0 && day == 0) {
/*  478 */             if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */             {
/*      */               
/*  481 */               return null; } 
/*  482 */             if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */             {
/*  484 */               throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  489 */             year = 1;
/*  490 */             month = 1;
/*  491 */             day = 1;
/*      */           } 
/*      */           
/*  494 */           if (!rs.useLegacyDatetimeCode) {
/*  495 */             return TimeUtil.fastDateCreate(year, month, day, targetCalendar);
/*      */           }
/*      */           
/*  498 */           return rs.fastDateCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  504 */         return rs.getNativeDateViaParseConversion(columnIndex + 1);
/*      */       case 93:
/*  506 */         if (populatedFromDateTimeValue) {
/*  507 */           if (year == 0 && month == 0 && day == 0) {
/*  508 */             if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */             {
/*      */               
/*  511 */               return null; } 
/*  512 */             if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */             {
/*  514 */               throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  519 */             year = 1;
/*  520 */             month = 1;
/*  521 */             day = 1;
/*      */           } 
/*      */           
/*  524 */           if (!rs.useLegacyDatetimeCode) {
/*  525 */             return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */           }
/*      */ 
/*      */           
/*  529 */           Timestamp ts = rs.fastTimestampCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day, hour, minute, seconds, nanos);
/*      */ 
/*      */ 
/*      */           
/*  533 */           Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */ 
/*      */           
/*  537 */           return adjustedTs;
/*      */         } 
/*      */         
/*  540 */         return rs.getNativeTimestampViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     } 
/*      */ 
/*      */     
/*  544 */     throw new SQLException("Internal error - conversion method doesn't support this type", "S1000");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Object getNativeDateTimeValue(int paramInt1, Calendar paramCalendar, int paramInt2, int paramInt3, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double getNativeDouble(byte[] bits, int offset) {
/*  556 */     long valueAsLong = (bits[offset + 0] & 0xFF) | (bits[offset + 1] & 0xFF) << 8L | (bits[offset + 2] & 0xFF) << 16L | (bits[offset + 3] & 0xFF) << 24L | (bits[offset + 4] & 0xFF) << 32L | (bits[offset + 5] & 0xFF) << 40L | (bits[offset + 6] & 0xFF) << 48L | (bits[offset + 7] & 0xFF) << 56L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  565 */     return Double.longBitsToDouble(valueAsLong);
/*      */   }
/*      */   
/*      */   public abstract double getNativeDouble(int paramInt) throws SQLException;
/*      */   
/*      */   protected float getNativeFloat(byte[] bits, int offset) {
/*  571 */     int asInt = bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8 | (bits[offset + 2] & 0xFF) << 16 | (bits[offset + 3] & 0xFF) << 24;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  576 */     return Float.intBitsToFloat(asInt);
/*      */   }
/*      */ 
/*      */   
/*      */   public abstract float getNativeFloat(int paramInt) throws SQLException;
/*      */   
/*      */   protected int getNativeInt(byte[] bits, int offset) {
/*  583 */     int valueAsInt = bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8 | (bits[offset + 2] & 0xFF) << 16 | (bits[offset + 3] & 0xFF) << 24;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  588 */     return valueAsInt;
/*      */   }
/*      */   
/*      */   public abstract int getNativeInt(int paramInt) throws SQLException;
/*      */   
/*      */   protected long getNativeLong(byte[] bits, int offset) {
/*  594 */     long valueAsLong = (bits[offset + 0] & 0xFF) | (bits[offset + 1] & 0xFF) << 8L | (bits[offset + 2] & 0xFF) << 16L | (bits[offset + 3] & 0xFF) << 24L | (bits[offset + 4] & 0xFF) << 32L | (bits[offset + 5] & 0xFF) << 40L | (bits[offset + 6] & 0xFF) << 48L | (bits[offset + 7] & 0xFF) << 56L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  603 */     return valueAsLong;
/*      */   }
/*      */   
/*      */   public abstract long getNativeLong(int paramInt) throws SQLException;
/*      */   
/*      */   protected short getNativeShort(byte[] bits, int offset) {
/*  609 */     short asShort = (short)(bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8);
/*      */     
/*  611 */     return asShort;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract short getNativeShort(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */   
/*      */   protected Time getNativeTime(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*  621 */     int hour = 0;
/*  622 */     int minute = 0;
/*  623 */     int seconds = 0;
/*      */     
/*  625 */     if (length != 0) {
/*      */ 
/*      */       
/*  628 */       hour = bits[offset + 5];
/*  629 */       minute = bits[offset + 6];
/*  630 */       seconds = bits[offset + 7];
/*      */     } 
/*      */     
/*  633 */     if (!rs.useLegacyDatetimeCode) {
/*  634 */       return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar);
/*      */     }
/*      */     
/*  637 */     Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */     
/*  639 */     synchronized (sessionCalendar) {
/*  640 */       Time time = TimeUtil.fastTimeCreate(sessionCalendar, hour, minute, seconds);
/*      */ 
/*      */       
/*  643 */       Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */ 
/*      */       
/*  647 */       return adjustedTime;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Time getNativeTime(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */   
/*      */   protected Timestamp getNativeTimestamp(byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*  658 */     int year = 0;
/*  659 */     int month = 0;
/*  660 */     int day = 0;
/*      */     
/*  662 */     int hour = 0;
/*  663 */     int minute = 0;
/*  664 */     int seconds = 0;
/*      */     
/*  666 */     int nanos = 0;
/*      */     
/*  668 */     if (length != 0) {
/*  669 */       year = bits[offset + 0] & 0xFF | (bits[offset + 1] & 0xFF) << 8;
/*  670 */       month = bits[offset + 2];
/*  671 */       day = bits[offset + 3];
/*      */       
/*  673 */       if (length > 4) {
/*  674 */         hour = bits[offset + 4];
/*  675 */         minute = bits[offset + 5];
/*  676 */         seconds = bits[offset + 6];
/*      */       } 
/*      */       
/*  679 */       if (length > 7)
/*      */       {
/*  681 */         nanos = (bits[offset + 7] & 0xFF | (bits[offset + 8] & 0xFF) << 8 | (bits[offset + 9] & 0xFF) << 16 | (bits[offset + 10] & 0xFF) << 24) * 1000;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  687 */     if (year == 0 && month == 0 && day == 0) {
/*  688 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*      */         
/*  691 */         return null; } 
/*  692 */       if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  694 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  700 */       year = 1;
/*  701 */       month = 1;
/*  702 */       day = 1;
/*      */     } 
/*      */     
/*  705 */     if (!rs.useLegacyDatetimeCode) {
/*  706 */       return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */     }
/*      */ 
/*      */     
/*  710 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */     
/*  714 */     synchronized (sessionCalendar) {
/*  715 */       Timestamp ts = rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minute, seconds, nanos);
/*      */ 
/*      */       
/*  718 */       Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */ 
/*      */       
/*  722 */       return adjustedTs;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Timestamp getNativeTimestamp(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Reader getReader(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String getString(int paramInt, String paramString, ConnectionImpl paramConnectionImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getString(String encoding, ConnectionImpl conn, byte[] value, int offset, int length) throws SQLException {
/*  776 */     String stringVal = null;
/*      */     
/*  778 */     if (conn != null && conn.getUseUnicode()) {
/*      */       try {
/*  780 */         if (encoding == null) {
/*  781 */           stringVal = new String(value);
/*      */         } else {
/*  783 */           SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
/*      */ 
/*      */           
/*  786 */           if (converter != null) {
/*  787 */             stringVal = converter.toString(value, offset, length);
/*      */           } else {
/*  789 */             stringVal = new String(value, offset, length, encoding);
/*      */           } 
/*      */         } 
/*  792 */       } catch (UnsupportedEncodingException E) {
/*  793 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Unsupported_character_encoding____101") + encoding + "'.", "0S100");
/*      */       
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  800 */       stringVal = StringUtils.toAsciiString(value, offset, length);
/*      */     } 
/*      */     
/*  803 */     return stringVal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Time getTimeFast(int columnIndex, byte[] timeAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*  811 */     int hr = 0;
/*  812 */     int min = 0;
/*  813 */     int sec = 0;
/*      */ 
/*      */     
/*      */     try {
/*  817 */       if (timeAsBytes == null) {
/*  818 */         return null;
/*      */       }
/*      */       
/*  821 */       boolean allZeroTime = true;
/*  822 */       boolean onlyTimePresent = false;
/*      */       int i;
/*  824 */       for (i = 0; i < length; i++) {
/*  825 */         if (timeAsBytes[offset + i] == 58) {
/*  826 */           onlyTimePresent = true;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*  831 */       for (i = 0; i < length; i++) {
/*  832 */         byte b = timeAsBytes[offset + i];
/*      */         
/*  834 */         if (b == 32 || b == 45 || b == 47) {
/*  835 */           onlyTimePresent = false;
/*      */         }
/*      */         
/*  838 */         if (b != 48 && b != 32 && b != 58 && b != 45 && b != 47 && b != 46) {
/*      */           
/*  840 */           allZeroTime = false;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */       
/*  846 */       if (!onlyTimePresent && allZeroTime) {
/*  847 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  849 */           return null; } 
/*  850 */         if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  852 */           throw SQLError.createSQLException("Value '" + new String(timeAsBytes) + "' can not be represented as java.sql.Time", "S1009");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  860 */         return rs.fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */       } 
/*      */       
/*  863 */       Field timeColField = this.metadata[columnIndex];
/*      */       
/*  865 */       if (timeColField.getMysqlType() == 7) {
/*      */         
/*  867 */         switch (length) {
/*      */           
/*      */           case 19:
/*  870 */             hr = StringUtils.getInt(timeAsBytes, offset + length - 8, offset + length - 6);
/*      */             
/*  872 */             min = StringUtils.getInt(timeAsBytes, offset + length - 5, offset + length - 3);
/*      */             
/*  874 */             sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 12:
/*      */           case 14:
/*  881 */             hr = StringUtils.getInt(timeAsBytes, offset + length - 6, offset + length - 4);
/*      */             
/*  883 */             min = StringUtils.getInt(timeAsBytes, offset + length - 4, offset + length - 2);
/*      */             
/*  885 */             sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 10:
/*  892 */             hr = StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */             
/*  894 */             min = StringUtils.getInt(timeAsBytes, offset + 8, offset + 10);
/*      */             
/*  896 */             sec = 0;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/*  902 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + (columnIndex + 1) + "(" + timeColField + ").", "S1009");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  912 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + timeColField + ").");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  921 */       else if (timeColField.getMysqlType() == 12) {
/*  922 */         hr = StringUtils.getInt(timeAsBytes, offset + 11, offset + 13);
/*  923 */         min = StringUtils.getInt(timeAsBytes, offset + 14, offset + 16);
/*  924 */         sec = StringUtils.getInt(timeAsBytes, offset + 17, offset + 19);
/*      */         
/*  926 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + (columnIndex + 1) + "(" + timeColField + ").");
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  936 */         if (timeColField.getMysqlType() == 10) {
/*  937 */           return rs.fastTimeCreate(null, 0, 0, 0);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  942 */         if (length != 5 && length != 8) {
/*  943 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + new String(timeAsBytes) + Messages.getString("ResultSet.___in_column__268") + (columnIndex + 1), "S1009");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  951 */         hr = StringUtils.getInt(timeAsBytes, offset + 0, offset + 2);
/*  952 */         min = StringUtils.getInt(timeAsBytes, offset + 3, offset + 5);
/*  953 */         sec = (length == 5) ? 0 : StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */       } 
/*      */ 
/*      */       
/*  957 */       Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */       
/*  959 */       if (!rs.useLegacyDatetimeCode) {
/*  960 */         return rs.fastTimeCreate(targetCalendar, hr, min, sec);
/*      */       }
/*      */       
/*  963 */       synchronized (sessionCalendar) {
/*  964 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimeCreate(sessionCalendar, hr, min, sec), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  969 */     catch (Exception ex) {
/*  970 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009");
/*      */       
/*  972 */       sqlEx.initCause(ex);
/*      */       
/*  974 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Time getTimeFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Timestamp getTimestampFast(int columnIndex, byte[] timestampAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*      */     try {
/*  988 */       Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*      */ 
/*      */       
/*  992 */       synchronized (sessionCalendar) {
/*  993 */         boolean hasDash, hasColon; int j; boolean allZeroTimestamp = true;
/*      */         
/*  995 */         boolean onlyTimePresent = false;
/*      */         int i;
/*  997 */         for (i = 0; i < length; i++) {
/*  998 */           if (timestampAsBytes[offset + i] == 58) {
/*  999 */             onlyTimePresent = true;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/* 1004 */         for (i = 0; i < length; i++) {
/* 1005 */           byte b = timestampAsBytes[offset + i];
/*      */           
/* 1007 */           if (b == 32 || b == 45 || b == 47) {
/* 1008 */             onlyTimePresent = false;
/*      */           }
/*      */           
/* 1011 */           if (b != 48 && b != 32 && b != 58 && b != 45 && b != 47 && b != 46) {
/*      */             
/* 1013 */             allZeroTimestamp = false;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */         
/* 1019 */         if (!onlyTimePresent && allZeroTimestamp) {
/*      */           
/* 1021 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*      */             
/* 1024 */             return null; } 
/* 1025 */           if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/* 1027 */             throw SQLError.createSQLException("Value '" + timestampAsBytes + "' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1035 */           if (!rs.useLegacyDatetimeCode) {
/* 1036 */             return TimeUtil.fastTimestampCreate(tz, 1, 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/*      */           
/* 1040 */           return rs.fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */         } 
/* 1042 */         if (this.metadata[columnIndex].getMysqlType() == 13) {
/*      */           
/* 1044 */           if (!rs.useLegacyDatetimeCode) {
/* 1045 */             return TimeUtil.fastTimestampCreate(tz, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1050 */           return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1057 */         if (timestampAsBytes[offset + length - 1] == 46) {
/* 1058 */           length--;
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1063 */         int year = 0;
/* 1064 */         int month = 0;
/* 1065 */         int day = 0;
/* 1066 */         int hour = 0;
/* 1067 */         int minutes = 0;
/* 1068 */         int seconds = 0;
/* 1069 */         int nanos = 0;
/*      */         
/* 1071 */         switch (length) {
/*      */           case 19:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*      */           case 23:
/*      */           case 24:
/*      */           case 25:
/*      */           case 26:
/* 1080 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */             
/* 1082 */             month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/*      */             
/* 1084 */             day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */             
/* 1086 */             hour = StringUtils.getInt(timestampAsBytes, offset + 11, offset + 13);
/*      */             
/* 1088 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 14, offset + 16);
/*      */             
/* 1090 */             seconds = StringUtils.getInt(timestampAsBytes, offset + 17, offset + 19);
/*      */ 
/*      */             
/* 1093 */             nanos = 0;
/*      */             
/* 1095 */             if (length > 19) {
/* 1096 */               int decimalIndex = -1;
/*      */               
/* 1098 */               for (int k = 0; k < length; k++) {
/* 1099 */                 if (timestampAsBytes[offset + k] == 46) {
/* 1100 */                   decimalIndex = k;
/*      */                 }
/*      */               } 
/*      */               
/* 1104 */               if (decimalIndex != -1) {
/* 1105 */                 if (decimalIndex + 2 <= length) {
/* 1106 */                   nanos = StringUtils.getInt(timestampAsBytes, decimalIndex + 1, offset + length);
/*      */                   
/*      */                   break;
/*      */                 } 
/* 1110 */                 throw new IllegalArgumentException();
/*      */               } 
/*      */             } 
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 14:
/* 1124 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */             
/* 1126 */             month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */             
/* 1128 */             day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */             
/* 1130 */             hour = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */             
/* 1132 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */             
/* 1134 */             seconds = StringUtils.getInt(timestampAsBytes, offset + 12, offset + 14);
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 12:
/* 1141 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/* 1144 */             if (year <= 69) {
/* 1145 */               year += 100;
/*      */             }
/*      */             
/* 1148 */             year += 1900;
/*      */             
/* 1150 */             month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */             
/* 1152 */             day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */             
/* 1154 */             hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */             
/* 1156 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */             
/* 1158 */             seconds = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 10:
/* 1165 */             hasDash = false;
/*      */             
/* 1167 */             for (j = 0; j < length; j++) {
/* 1168 */               if (timestampAsBytes[offset + j] == 45) {
/* 1169 */                 hasDash = true;
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/* 1174 */             if (this.metadata[columnIndex].getMysqlType() == 10 || hasDash) {
/*      */               
/* 1176 */               year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */               
/* 1178 */               month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/*      */               
/* 1180 */               day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */               
/* 1182 */               hour = 0;
/* 1183 */               minutes = 0; break;
/*      */             } 
/* 1185 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/* 1188 */             if (year <= 69) {
/* 1189 */               year += 100;
/*      */             }
/*      */             
/* 1192 */             month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */             
/* 1194 */             day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */             
/* 1196 */             hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */             
/* 1198 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/*      */             
/* 1201 */             year += 1900;
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 8:
/* 1208 */             hasColon = false;
/*      */             
/* 1210 */             for (j = 0; j < length; j++) {
/* 1211 */               if (timestampAsBytes[offset + j] == 58) {
/* 1212 */                 hasColon = true;
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/* 1217 */             if (hasColon) {
/* 1218 */               hour = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */               
/* 1220 */               minutes = StringUtils.getInt(timestampAsBytes, offset + 3, offset + 5);
/*      */               
/* 1222 */               seconds = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/*      */               
/* 1225 */               year = 1970;
/* 1226 */               month = 1;
/* 1227 */               day = 1;
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 1232 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */             
/* 1234 */             month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */             
/* 1236 */             day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/*      */             
/* 1239 */             year -= 1900;
/* 1240 */             month--;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 6:
/* 1246 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/* 1249 */             if (year <= 69) {
/* 1250 */               year += 100;
/*      */             }
/*      */             
/* 1253 */             year += 1900;
/*      */             
/* 1255 */             month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */             
/* 1257 */             day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 4:
/* 1264 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/* 1267 */             if (year <= 69) {
/* 1268 */               year += 100;
/*      */             }
/*      */             
/* 1271 */             month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/*      */             
/* 1274 */             day = 1;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 2:
/* 1280 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/*      */             
/* 1283 */             if (year <= 69) {
/* 1284 */               year += 100;
/*      */             }
/*      */             
/* 1287 */             year += 1900;
/* 1288 */             month = 1;
/* 1289 */             day = 1;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/* 1295 */             throw new SQLException("Bad format for Timestamp '" + new String(timestampAsBytes) + "' in column " + (columnIndex + 1) + ".", "S1009");
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1303 */         if (!rs.useLegacyDatetimeCode) {
/* 1304 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1310 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1320 */     catch (Exception e) {
/* 1321 */       SQLException sqlEx = SQLError.createSQLException("Cannot convert value '" + getString(columnIndex, "ISO8859_1", conn) + "' from column " + (columnIndex + 1) + " to TIMESTAMP.", "S1009");
/*      */ 
/*      */ 
/*      */       
/* 1325 */       sqlEx.initCause(e);
/*      */       
/* 1327 */       throw sqlEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Timestamp getTimestampFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isFloatingPointNumber(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isNull(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long length(int paramInt) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void setColumnValue(int paramInt, byte[] paramArrayOfbyte) throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSetRow setMetadata(Field[] f) throws SQLException {
/* 1395 */     this.metadata = f;
/*      */     
/* 1397 */     return this;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ResultSetRow.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */