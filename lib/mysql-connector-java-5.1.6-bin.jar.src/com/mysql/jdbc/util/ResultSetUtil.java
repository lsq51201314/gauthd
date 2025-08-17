/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResultSetUtil
/*    */ {
/*    */   public static StringBuffer appendResultSetSlashGStyle(StringBuffer appendTo, ResultSet rs) throws SQLException {
/* 42 */     ResultSetMetaData rsmd = rs.getMetaData();
/*    */     
/* 44 */     int numFields = rsmd.getColumnCount();
/* 45 */     int maxWidth = 0;
/*    */     
/* 47 */     String[] fieldNames = new String[numFields];
/*    */     
/* 49 */     for (int i = 0; i < numFields; i++) {
/* 50 */       fieldNames[i] = rsmd.getColumnLabel(i + 1);
/*    */       
/* 52 */       if (fieldNames[i].length() > maxWidth) {
/* 53 */         maxWidth = fieldNames[i].length();
/*    */       }
/*    */     } 
/*    */     
/* 57 */     int rowCount = 1;
/*    */     
/* 59 */     while (rs.next()) {
/* 60 */       appendTo.append("*************************** ");
/* 61 */       appendTo.append(rowCount++);
/* 62 */       appendTo.append(". row ***************************\n");
/*    */       
/* 64 */       for (int j = 0; j < numFields; j++) {
/* 65 */         int leftPad = maxWidth - fieldNames[j].length();
/*    */         
/* 67 */         for (int k = 0; k < leftPad; k++) {
/* 68 */           appendTo.append(" ");
/*    */         }
/*    */         
/* 71 */         appendTo.append(fieldNames[j]);
/* 72 */         appendTo.append(": ");
/*    */         
/* 74 */         String stringVal = rs.getString(j + 1);
/*    */         
/* 76 */         if (stringVal != null) {
/* 77 */           appendTo.append(stringVal);
/*    */         } else {
/* 79 */           appendTo.append("NULL");
/*    */         } 
/*    */         
/* 82 */         appendTo.append("\n");
/*    */       } 
/*    */       
/* 85 */       appendTo.append("\n");
/*    */     } 
/*    */     
/* 88 */     return appendTo;
/*    */   }
/*    */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdb\\util\ResultSetUtil.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */