/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
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
/*      */ public class CallableStatement
/*      */   extends PreparedStatement
/*      */   implements CallableStatement
/*      */ {
/*      */   protected static final Constructor JDBC_4_CSTMT_2_ARGS_CTOR;
/*      */   protected static final Constructor JDBC_4_CSTMT_4_ARGS_CTOR;
/*      */   private static final int NOT_OUTPUT_PARAMETER_INDICATOR = -2147483648;
/*      */   private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
/*      */   
/*      */   static {
/*   62 */     if (Util.isJdbc4()) {
/*      */       try {
/*   64 */         JDBC_4_CSTMT_2_ARGS_CTOR = Class.forName("com.mysql.jdbc.JDBC4CallableStatement").getConstructor(new Class[] { ConnectionImpl.class, CallableStatementParamInfo.class });
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*   69 */         JDBC_4_CSTMT_4_ARGS_CTOR = Class.forName("com.mysql.jdbc.JDBC4CallableStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class, boolean.class });
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*   75 */       catch (SecurityException e) {
/*   76 */         throw new RuntimeException(e);
/*   77 */       } catch (NoSuchMethodException e) {
/*   78 */         throw new RuntimeException(e);
/*   79 */       } catch (ClassNotFoundException e) {
/*   80 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/*   83 */       JDBC_4_CSTMT_4_ARGS_CTOR = null;
/*   84 */       JDBC_4_CSTMT_2_ARGS_CTOR = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected class CallableStatementParam
/*      */   {
/*      */     int desiredJdbcType;
/*      */     
/*      */     int index;
/*      */     
/*      */     int inOutModifier;
/*      */     
/*      */     boolean isIn;
/*      */     
/*      */     boolean isOut;
/*      */     
/*      */     int jdbcType;
/*      */     
/*      */     short nullability;
/*      */     
/*      */     String paramName;
/*      */     
/*      */     int precision;
/*      */     int scale;
/*      */     String typeName;
/*      */     private final CallableStatement this$0;
/*      */     
/*      */     CallableStatementParam(CallableStatement this$0, String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier) {
/*  113 */       this.this$0 = this$0;
/*  114 */       this.paramName = name;
/*  115 */       this.isIn = in;
/*  116 */       this.isOut = out;
/*  117 */       this.index = idx;
/*      */       
/*  119 */       this.jdbcType = jdbcType;
/*  120 */       this.typeName = typeName;
/*  121 */       this.precision = precision;
/*  122 */       this.scale = scale;
/*  123 */       this.nullability = nullability;
/*  124 */       this.inOutModifier = inOutModifier;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object clone() throws CloneNotSupportedException {
/*  133 */       return super.clone();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected class CallableStatementParamInfo
/*      */   {
/*      */     String catalogInUse;
/*      */     
/*      */     boolean isFunctionCall;
/*      */     
/*      */     String nativeSql;
/*      */     
/*      */     int numParameters;
/*      */     
/*      */     List parameterList;
/*      */     
/*      */     Map parameterMap;
/*      */     
/*      */     private final CallableStatement this$0;
/*      */ 
/*      */     
/*      */     CallableStatementParamInfo(CallableStatement this$0, CallableStatementParamInfo fullParamInfo) {
/*  157 */       this.this$0 = this$0;
/*  158 */       this.nativeSql = this$0.originalSql;
/*  159 */       this.catalogInUse = this$0.currentCatalog;
/*  160 */       this.isFunctionCall = fullParamInfo.isFunctionCall;
/*  161 */       int[] localParameterMap = this$0.placeholderToParameterIndexMap;
/*  162 */       int parameterMapLength = localParameterMap.length;
/*      */       
/*  164 */       this.parameterList = new ArrayList(fullParamInfo.numParameters);
/*  165 */       this.parameterMap = new HashMap(fullParamInfo.numParameters);
/*      */       
/*  167 */       if (this.isFunctionCall)
/*      */       {
/*  169 */         this.parameterList.add(fullParamInfo.parameterList.get(0));
/*      */       }
/*      */       
/*  172 */       int offset = this.isFunctionCall ? 1 : 0;
/*      */       
/*  174 */       for (int i = 0; i < parameterMapLength; i++) {
/*  175 */         if (localParameterMap[i] != 0) {
/*  176 */           CallableStatement.CallableStatementParam param = fullParamInfo.parameterList.get(localParameterMap[i] + offset);
/*      */           
/*  178 */           this.parameterList.add(param);
/*  179 */           this.parameterMap.put(param.paramName, param);
/*      */         } 
/*      */       } 
/*      */       
/*  183 */       this.numParameters = this.parameterList.size();
/*      */     }
/*      */     
/*      */     CallableStatementParamInfo(CallableStatement this$0, ResultSet paramTypesRs) throws SQLException {
/*  187 */       this.this$0 = this$0;
/*  188 */       boolean hadRows = paramTypesRs.last();
/*      */       
/*  190 */       this.nativeSql = this$0.originalSql;
/*  191 */       this.catalogInUse = this$0.currentCatalog;
/*  192 */       this.isFunctionCall = this$0.callingStoredFunction;
/*      */       
/*  194 */       if (hadRows) {
/*  195 */         this.numParameters = paramTypesRs.getRow();
/*      */         
/*  197 */         this.parameterList = new ArrayList(this.numParameters);
/*  198 */         this.parameterMap = new HashMap(this.numParameters);
/*      */         
/*  200 */         paramTypesRs.beforeFirst();
/*      */         
/*  202 */         addParametersFromDBMD(paramTypesRs);
/*      */       } else {
/*  204 */         this.numParameters = 0;
/*      */       } 
/*      */       
/*  207 */       if (this.isFunctionCall) {
/*  208 */         this.numParameters++;
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void addParametersFromDBMD(ResultSet paramTypesRs) throws SQLException {
/*  214 */       int i = 0;
/*      */       
/*  216 */       while (paramTypesRs.next()) {
/*  217 */         String paramName = paramTypesRs.getString(4);
/*  218 */         int inOutModifier = paramTypesRs.getInt(5);
/*      */         
/*  220 */         boolean isOutParameter = false;
/*  221 */         boolean isInParameter = false;
/*      */         
/*  223 */         if (i == 0 && this.isFunctionCall) {
/*  224 */           isOutParameter = true;
/*  225 */           isInParameter = false;
/*  226 */         } else if (inOutModifier == 2) {
/*  227 */           isOutParameter = true;
/*  228 */           isInParameter = true;
/*  229 */         } else if (inOutModifier == 1) {
/*  230 */           isOutParameter = false;
/*  231 */           isInParameter = true;
/*  232 */         } else if (inOutModifier == 4) {
/*  233 */           isOutParameter = true;
/*  234 */           isInParameter = false;
/*      */         } 
/*      */         
/*  237 */         int jdbcType = paramTypesRs.getInt(6);
/*  238 */         String typeName = paramTypesRs.getString(7);
/*  239 */         int precision = paramTypesRs.getInt(8);
/*  240 */         int scale = paramTypesRs.getInt(10);
/*  241 */         short nullability = paramTypesRs.getShort(12);
/*      */         
/*  243 */         CallableStatement.CallableStatementParam paramInfoToAdd = new CallableStatement.CallableStatementParam(this.this$0, paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  248 */         this.parameterList.add(paramInfoToAdd);
/*  249 */         this.parameterMap.put(paramName, paramInfoToAdd);
/*      */       } 
/*      */     }
/*      */     
/*      */     protected void checkBounds(int paramIndex) throws SQLException {
/*  254 */       int localParamIndex = paramIndex - 1;
/*      */       
/*  256 */       if (paramIndex < 0 || localParamIndex >= this.numParameters) {
/*  257 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.11") + paramIndex + Messages.getString("CallableStatement.12") + this.numParameters + Messages.getString("CallableStatement.13"), "S1009");
/*      */       }
/*      */     }
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
/*      */     protected Object clone() throws CloneNotSupportedException {
/*  271 */       return super.clone();
/*      */     }
/*      */     
/*      */     CallableStatement.CallableStatementParam getParameter(int index) {
/*  275 */       return this.parameterList.get(index);
/*      */     }
/*      */     
/*      */     CallableStatement.CallableStatementParam getParameter(String name) {
/*  279 */       return (CallableStatement.CallableStatementParam)this.parameterMap.get(name);
/*      */     }
/*      */     
/*      */     public String getParameterClassName(int arg0) throws SQLException {
/*  283 */       String mysqlTypeName = getParameterTypeName(arg0);
/*      */       
/*  285 */       boolean isBinaryOrBlob = (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BLOB") != -1 || StringUtils.indexOfIgnoreCase(mysqlTypeName, "BINARY") != -1);
/*      */ 
/*      */       
/*  288 */       boolean isUnsigned = (StringUtils.indexOfIgnoreCase(mysqlTypeName, "UNSIGNED") != -1);
/*      */       
/*  290 */       int mysqlTypeIfKnown = 0;
/*      */       
/*  292 */       if (StringUtils.startsWithIgnoreCase(mysqlTypeName, "MEDIUMINT")) {
/*  293 */         mysqlTypeIfKnown = 9;
/*      */       }
/*      */       
/*  296 */       return ResultSetMetaData.getClassNameForJavaType(getParameterType(arg0), isUnsigned, mysqlTypeIfKnown, isBinaryOrBlob, false);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getParameterCount() throws SQLException {
/*  301 */       if (this.parameterList == null) {
/*  302 */         return 0;
/*      */       }
/*      */       
/*  305 */       return this.parameterList.size();
/*      */     }
/*      */     
/*      */     public int getParameterMode(int arg0) throws SQLException {
/*  309 */       checkBounds(arg0);
/*      */       
/*  311 */       return (getParameter(arg0 - 1)).inOutModifier;
/*      */     }
/*      */     
/*      */     public int getParameterType(int arg0) throws SQLException {
/*  315 */       checkBounds(arg0);
/*      */       
/*  317 */       return (getParameter(arg0 - 1)).jdbcType;
/*      */     }
/*      */     
/*      */     public String getParameterTypeName(int arg0) throws SQLException {
/*  321 */       checkBounds(arg0);
/*      */       
/*  323 */       return (getParameter(arg0 - 1)).typeName;
/*      */     }
/*      */     
/*      */     public int getPrecision(int arg0) throws SQLException {
/*  327 */       checkBounds(arg0);
/*      */       
/*  329 */       return (getParameter(arg0 - 1)).precision;
/*      */     }
/*      */     
/*      */     public int getScale(int arg0) throws SQLException {
/*  333 */       checkBounds(arg0);
/*      */       
/*  335 */       return (getParameter(arg0 - 1)).scale;
/*      */     }
/*      */     
/*      */     public int isNullable(int arg0) throws SQLException {
/*  339 */       checkBounds(arg0);
/*      */       
/*  341 */       return (getParameter(arg0 - 1)).nullability;
/*      */     }
/*      */     
/*      */     public boolean isSigned(int arg0) throws SQLException {
/*  345 */       checkBounds(arg0);
/*      */       
/*  347 */       return false;
/*      */     }
/*      */     
/*      */     Iterator iterator() {
/*  351 */       return this.parameterList.iterator();
/*      */     }
/*      */     
/*      */     int numberOfParameters() {
/*  355 */       return this.numParameters;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected class CallableStatementParamInfoJDBC3
/*      */     extends CallableStatementParamInfo
/*      */     implements ParameterMetaData
/*      */   {
/*      */     private final CallableStatement this$0;
/*      */ 
/*      */ 
/*      */     
/*      */     CallableStatementParamInfoJDBC3(CallableStatement this$0, ResultSet paramTypesRs) throws SQLException {
/*  370 */       super(this$0, paramTypesRs);
/*      */       this.this$0 = this$0;
/*      */     }
/*      */     public CallableStatementParamInfoJDBC3(CallableStatement this$0, CallableStatement.CallableStatementParamInfo paramInfo) {
/*  374 */       super(this$0, paramInfo);
/*      */       this.this$0 = this$0;
/*      */     }
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
/*      */     public boolean isWrapperFor(Class iface) throws SQLException {
/*  393 */       this.this$0.checkClosed();
/*      */ 
/*      */ 
/*      */       
/*  397 */       return iface.isInstance(this);
/*      */     }
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
/*      */     public Object unwrap(Class iface) throws SQLException {
/*      */       try {
/*  418 */         return Util.cast(iface, this);
/*  419 */       } catch (ClassCastException cce) {
/*  420 */         throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009");
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String mangleParameterName(String origParameterName) {
/*  431 */     if (origParameterName == null) {
/*  432 */       return null;
/*      */     }
/*      */     
/*  435 */     int offset = 0;
/*      */     
/*  437 */     if (origParameterName.length() > 0 && origParameterName.charAt(0) == '@')
/*      */     {
/*  439 */       offset = 1;
/*      */     }
/*      */     
/*  442 */     StringBuffer paramNameBuf = new StringBuffer("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
/*      */ 
/*      */     
/*  445 */     paramNameBuf.append("@com_mysql_jdbc_outparam_");
/*  446 */     paramNameBuf.append(origParameterName.substring(offset));
/*      */     
/*  448 */     return paramNameBuf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean callingStoredFunction = false;
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods functionReturnValueResults;
/*      */ 
/*      */   
/*      */   private boolean hasOutputParams = false;
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods outputParameterResults;
/*      */ 
/*      */   
/*      */   protected boolean outputParamWasNull = false;
/*      */ 
/*      */   
/*      */   private int[] parameterIndexToRsIndex;
/*      */ 
/*      */   
/*      */   protected CallableStatementParamInfo paramInfo;
/*      */ 
/*      */   
/*      */   private CallableStatementParam returnValueParam;
/*      */ 
/*      */   
/*      */   private int[] placeholderToParameterIndexMap;
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement(ConnectionImpl conn, CallableStatementParamInfo paramInfo) throws SQLException {
/*  482 */     super(conn, paramInfo.nativeSql, paramInfo.catalogInUse);
/*      */     
/*  484 */     this.paramInfo = paramInfo;
/*  485 */     this.callingStoredFunction = this.paramInfo.isFunctionCall;
/*      */     
/*  487 */     if (this.callingStoredFunction) {
/*  488 */       this.parameterCount++;
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
/*      */   protected static CallableStatement getInstance(ConnectionImpl conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
/*  501 */     if (!Util.isJdbc4()) {
/*  502 */       return new CallableStatement(conn, sql, catalog, isFunctionCall);
/*      */     }
/*      */     
/*  505 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_4_ARGS_CTOR, new Object[] { conn, sql, catalog, Boolean.valueOf(isFunctionCall) });
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
/*      */   protected static CallableStatement getInstance(ConnectionImpl conn, CallableStatementParamInfo paramInfo) throws SQLException {
/*  519 */     if (!Util.isJdbc4()) {
/*  520 */       return new CallableStatement(conn, paramInfo);
/*      */     }
/*      */     
/*  523 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_2_ARGS_CTOR, new Object[] { conn, paramInfo });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void generateParameterMap() throws SQLException {
/*  531 */     if (this.paramInfo == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  539 */     int parameterCountFromMetaData = this.paramInfo.getParameterCount();
/*      */ 
/*      */ 
/*      */     
/*  543 */     if (this.callingStoredFunction) {
/*  544 */       parameterCountFromMetaData--;
/*      */     }
/*      */     
/*  547 */     if (this.paramInfo != null && this.parameterCount != parameterCountFromMetaData) {
/*      */       
/*  549 */       this.placeholderToParameterIndexMap = new int[this.parameterCount];
/*      */       
/*  551 */       int startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase(this.originalSql, "SELECT") : StringUtils.indexOfIgnoreCase(this.originalSql, "CALL");
/*      */ 
/*      */       
/*  554 */       if (startPos != -1) {
/*  555 */         int parenOpenPos = this.originalSql.indexOf('(', startPos + 4);
/*      */         
/*  557 */         if (parenOpenPos != -1) {
/*  558 */           int parenClosePos = StringUtils.indexOfIgnoreCaseRespectQuotes(parenOpenPos, this.originalSql, ")", '\'', true);
/*      */ 
/*      */           
/*  561 */           if (parenClosePos != -1) {
/*  562 */             List parsedParameters = StringUtils.split(this.originalSql.substring(parenOpenPos + 1, parenClosePos), ",", "'\"", "'\"", true);
/*      */             
/*  564 */             int numParsedParameters = parsedParameters.size();
/*      */ 
/*      */ 
/*      */             
/*  568 */             if (numParsedParameters != this.parameterCount);
/*      */ 
/*      */ 
/*      */             
/*  572 */             int placeholderCount = 0;
/*      */             
/*  574 */             for (int i = 0; i < numParsedParameters; i++) {
/*  575 */               if (((String)parsedParameters.get(i)).equals("?")) {
/*  576 */                 this.placeholderToParameterIndexMap[placeholderCount++] = i;
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
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
/*      */ 
/*      */ 
/*      */   
/*      */   public CallableStatement(ConnectionImpl conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
/*  600 */     super(conn, sql, catalog);
/*      */     
/*  602 */     this.callingStoredFunction = isFunctionCall;
/*      */     
/*  604 */     if (!this.callingStoredFunction) {
/*  605 */       if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL")) {
/*      */         
/*  607 */         fakeParameterTypes(false);
/*      */       } else {
/*  609 */         determineParameterTypes();
/*      */       } 
/*      */       
/*  612 */       generateParameterMap();
/*      */     } else {
/*  614 */       determineParameterTypes();
/*  615 */       generateParameterMap();
/*      */       
/*  617 */       this.parameterCount++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addBatch() throws SQLException {
/*  627 */     setOutParams();
/*      */     
/*  629 */     super.addBatch();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private CallableStatementParam checkIsOutputParam(int paramIndex) throws SQLException {
/*  635 */     if (this.callingStoredFunction) {
/*  636 */       if (paramIndex == 1) {
/*      */         
/*  638 */         if (this.returnValueParam == null) {
/*  639 */           this.returnValueParam = new CallableStatementParam(this, "", 0, false, true, 12, "VARCHAR", 0, 0, (short)2, 5);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  645 */         return this.returnValueParam;
/*      */       } 
/*      */ 
/*      */       
/*  649 */       paramIndex--;
/*      */     } 
/*      */     
/*  652 */     checkParameterIndexBounds(paramIndex);
/*      */     
/*  654 */     int localParamIndex = paramIndex - 1;
/*      */     
/*  656 */     if (this.placeholderToParameterIndexMap != null) {
/*  657 */       localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */     }
/*      */     
/*  660 */     CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  666 */     if (this.connection.getNoAccessToProcedureBodies()) {
/*  667 */       paramDescriptor.isOut = true;
/*  668 */       paramDescriptor.isIn = true;
/*  669 */       paramDescriptor.inOutModifier = 2;
/*  670 */     } else if (!paramDescriptor.isOut) {
/*  671 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.9") + paramIndex + Messages.getString("CallableStatement.10"), "S1009");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  677 */     this.hasOutputParams = true;
/*      */     
/*  679 */     return paramDescriptor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkParameterIndexBounds(int paramIndex) throws SQLException {
/*  690 */     this.paramInfo.checkBounds(paramIndex);
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
/*      */   private void checkStreamability() throws SQLException {
/*  702 */     if (this.hasOutputParams && createStreamingResultSet()) {
/*  703 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), "S1C00");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized void clearParameters() throws SQLException {
/*  709 */     super.clearParameters();
/*      */     
/*      */     try {
/*  712 */       if (this.outputParameterResults != null) {
/*  713 */         this.outputParameterResults.close();
/*      */       }
/*      */     } finally {
/*  716 */       this.outputParameterResults = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fakeParameterTypes(boolean isReallyProcedure) throws SQLException {
/*  727 */     Field[] fields = new Field[13];
/*      */     
/*  729 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/*  730 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/*  731 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/*  732 */     fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/*  733 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/*  734 */     fields[5] = new Field("", "DATA_TYPE", 5, 0);
/*  735 */     fields[6] = new Field("", "TYPE_NAME", 1, 0);
/*  736 */     fields[7] = new Field("", "PRECISION", 4, 0);
/*  737 */     fields[8] = new Field("", "LENGTH", 4, 0);
/*  738 */     fields[9] = new Field("", "SCALE", 5, 0);
/*  739 */     fields[10] = new Field("", "RADIX", 5, 0);
/*  740 */     fields[11] = new Field("", "NULLABLE", 5, 0);
/*  741 */     fields[12] = new Field("", "REMARKS", 1, 0);
/*      */     
/*  743 */     String procName = isReallyProcedure ? extractProcedureName() : null;
/*      */     
/*  745 */     byte[] procNameAsBytes = null;
/*      */     
/*      */     try {
/*  748 */       procNameAsBytes = (procName == null) ? null : procName.getBytes("UTF-8");
/*  749 */     } catch (UnsupportedEncodingException ueEx) {
/*  750 */       procNameAsBytes = StringUtils.s2b(procName, this.connection);
/*      */     } 
/*      */     
/*  753 */     ArrayList resultRows = new ArrayList();
/*      */     
/*  755 */     for (int i = 0; i < this.parameterCount; i++) {
/*  756 */       byte[][] row = new byte[13][];
/*  757 */       row[0] = null;
/*  758 */       row[1] = null;
/*  759 */       row[2] = procNameAsBytes;
/*  760 */       row[3] = StringUtils.s2b(String.valueOf(i), this.connection);
/*      */       
/*  762 */       row[4] = StringUtils.s2b(String.valueOf(1), this.connection);
/*      */ 
/*      */ 
/*      */       
/*  766 */       row[5] = StringUtils.s2b(String.valueOf(12), this.connection);
/*      */       
/*  768 */       row[6] = StringUtils.s2b("VARCHAR", this.connection);
/*  769 */       row[7] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  770 */       row[8] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  771 */       row[9] = StringUtils.s2b(Integer.toString(0), this.connection);
/*  772 */       row[10] = StringUtils.s2b(Integer.toString(10), this.connection);
/*      */       
/*  774 */       row[11] = StringUtils.s2b(Integer.toString(2), this.connection);
/*      */ 
/*      */ 
/*      */       
/*  778 */       row[12] = null;
/*      */       
/*  780 */       resultRows.add(new ByteArrayRow(row));
/*      */     } 
/*      */     
/*  783 */     ResultSet paramTypesRs = DatabaseMetaData.buildResultSet(fields, resultRows, this.connection);
/*      */ 
/*      */     
/*  786 */     convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */   }
/*      */   
/*      */   private void determineParameterTypes() throws SQLException {
/*  790 */     if (this.connection.getNoAccessToProcedureBodies()) {
/*  791 */       fakeParameterTypes(true);
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  796 */     ResultSet paramTypesRs = null;
/*      */     
/*      */     try {
/*  799 */       String procName = extractProcedureName();
/*      */       
/*  801 */       DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */       
/*  803 */       boolean useCatalog = false;
/*      */       
/*  805 */       if (procName.indexOf(".") == -1) {
/*  806 */         useCatalog = true;
/*      */       }
/*      */       
/*  809 */       paramTypesRs = dbmd.getProcedureColumns((this.connection.versionMeetsMinimum(5, 0, 2) && useCatalog) ? this.currentCatalog : null, null, procName, "%");
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  814 */       convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */     } finally {
/*  816 */       SQLException sqlExRethrow = null;
/*      */       
/*  818 */       if (paramTypesRs != null) {
/*      */         try {
/*  820 */           paramTypesRs.close();
/*  821 */         } catch (SQLException sqlEx) {
/*  822 */           sqlExRethrow = sqlEx;
/*      */         } 
/*      */         
/*  825 */         paramTypesRs = null;
/*      */       } 
/*      */       
/*  828 */       if (sqlExRethrow != null) {
/*  829 */         throw sqlExRethrow;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException {
/*  835 */     if (!this.connection.isRunningOnJDK13()) {
/*  836 */       this.paramInfo = new CallableStatementParamInfoJDBC3(this, paramTypesRs);
/*      */     } else {
/*      */       
/*  839 */       this.paramInfo = new CallableStatementParamInfo(this, paramTypesRs);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean execute() throws SQLException {
/*  849 */     boolean returnVal = false;
/*      */     
/*  851 */     checkClosed();
/*      */     
/*  853 */     checkStreamability();
/*      */     
/*  855 */     synchronized (this.connection.getMutex()) {
/*  856 */       setInOutParamsOnServer();
/*  857 */       setOutParams();
/*      */       
/*  859 */       returnVal = super.execute();
/*      */       
/*  861 */       if (this.callingStoredFunction) {
/*  862 */         this.functionReturnValueResults = this.results;
/*  863 */         this.functionReturnValueResults.next();
/*  864 */         this.results = null;
/*      */       } 
/*      */       
/*  867 */       retrieveOutParams();
/*      */     } 
/*      */     
/*  870 */     if (!this.callingStoredFunction) {
/*  871 */       return returnVal;
/*      */     }
/*      */ 
/*      */     
/*  875 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet executeQuery() throws SQLException {
/*  884 */     checkClosed();
/*      */     
/*  886 */     checkStreamability();
/*      */     
/*  888 */     ResultSet execResults = null;
/*      */     
/*  890 */     synchronized (this.connection.getMutex()) {
/*  891 */       setInOutParamsOnServer();
/*  892 */       setOutParams();
/*      */       
/*  894 */       execResults = super.executeQuery();
/*      */       
/*  896 */       retrieveOutParams();
/*      */     } 
/*      */     
/*  899 */     return execResults;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int executeUpdate() throws SQLException {
/*  908 */     int returnVal = -1;
/*      */     
/*  910 */     checkClosed();
/*      */     
/*  912 */     checkStreamability();
/*      */     
/*  914 */     if (this.callingStoredFunction) {
/*  915 */       execute();
/*      */       
/*  917 */       return -1;
/*      */     } 
/*      */     
/*  920 */     synchronized (this.connection.getMutex()) {
/*  921 */       setInOutParamsOnServer();
/*  922 */       setOutParams();
/*      */       
/*  924 */       returnVal = super.executeUpdate();
/*      */       
/*  926 */       retrieveOutParams();
/*      */     } 
/*      */     
/*  929 */     return returnVal;
/*      */   }
/*      */   
/*      */   private String extractProcedureName() throws SQLException {
/*  933 */     String sanitizedSql = StringUtils.stripComments(this.originalSql, "`\"'", "`\"'", true, false, true, true);
/*      */ 
/*      */ 
/*      */     
/*  937 */     int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
/*      */     
/*  939 */     int offset = 5;
/*      */     
/*  941 */     if (endCallIndex == -1) {
/*  942 */       endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
/*      */       
/*  944 */       offset = 7;
/*      */     } 
/*      */     
/*  947 */     if (endCallIndex != -1) {
/*  948 */       StringBuffer nameBuf = new StringBuffer();
/*      */       
/*  950 */       String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
/*      */ 
/*      */       
/*  953 */       int statementLength = trimmedStatement.length();
/*      */       
/*  955 */       for (int i = 0; i < statementLength; i++) {
/*  956 */         char c = trimmedStatement.charAt(i);
/*      */         
/*  958 */         if (Character.isWhitespace(c) || c == '(' || c == '?') {
/*      */           break;
/*      */         }
/*  961 */         nameBuf.append(c);
/*      */       } 
/*      */ 
/*      */       
/*  965 */       return nameBuf.toString();
/*      */     } 
/*      */     
/*  968 */     throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), "S1000");
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
/*      */   
/*      */   protected String fixParameterName(String paramNameIn) throws SQLException {
/*  984 */     if (paramNameIn == null || paramNameIn.length() == 0) {
/*  985 */       throw SQLError.createSQLException((Messages.getString("CallableStatement.0") + paramNameIn == null) ? Messages.getString("CallableStatement.15") : Messages.getString("CallableStatement.16"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  990 */     if (this.connection.getNoAccessToProcedureBodies()) {
/*  991 */       throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009");
/*      */     }
/*      */ 
/*      */     
/*  995 */     return mangleParameterName(paramNameIn);
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
/*      */   public synchronized Array getArray(int i) throws SQLException {
/* 1010 */     ResultSetInternalMethods rs = getOutputParameters(i);
/*      */     
/* 1012 */     Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
/*      */     
/* 1014 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1016 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Array getArray(String parameterName) throws SQLException {
/* 1024 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1027 */     Array retValue = rs.getArray(fixParameterName(parameterName));
/*      */     
/* 1029 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1031 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/* 1039 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1041 */     BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1044 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1046 */     return retValue;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
/* 1067 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1069 */     BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
/*      */ 
/*      */     
/* 1072 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1074 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized BigDecimal getBigDecimal(String parameterName) throws SQLException {
/* 1082 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1085 */     BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
/*      */     
/* 1087 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1089 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Blob getBlob(int parameterIndex) throws SQLException {
/* 1096 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1098 */     Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1101 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1103 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Blob getBlob(String parameterName) throws SQLException {
/* 1110 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1113 */     Blob retValue = rs.getBlob(fixParameterName(parameterName));
/*      */     
/* 1115 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1117 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getBoolean(int parameterIndex) throws SQLException {
/* 1125 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1127 */     boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1130 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1132 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getBoolean(String parameterName) throws SQLException {
/* 1140 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1143 */     boolean retValue = rs.getBoolean(fixParameterName(parameterName));
/*      */     
/* 1145 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1147 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized byte getByte(int parameterIndex) throws SQLException {
/* 1154 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1156 */     byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1159 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1161 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized byte getByte(String parameterName) throws SQLException {
/* 1168 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1171 */     byte retValue = rs.getByte(fixParameterName(parameterName));
/*      */     
/* 1173 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1175 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized byte[] getBytes(int parameterIndex) throws SQLException {
/* 1182 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1184 */     byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1187 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1189 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized byte[] getBytes(String parameterName) throws SQLException {
/* 1197 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1200 */     byte[] retValue = rs.getBytes(fixParameterName(parameterName));
/*      */     
/* 1202 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1204 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Clob getClob(int parameterIndex) throws SQLException {
/* 1211 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1213 */     Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1216 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1218 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Clob getClob(String parameterName) throws SQLException {
/* 1225 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1228 */     Clob retValue = rs.getClob(fixParameterName(parameterName));
/*      */     
/* 1230 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1232 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Date getDate(int parameterIndex) throws SQLException {
/* 1239 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1241 */     Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1244 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1246 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Date getDate(int parameterIndex, Calendar cal) throws SQLException {
/* 1254 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1256 */     Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/*      */     
/* 1259 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1261 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Date getDate(String parameterName) throws SQLException {
/* 1268 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1271 */     Date retValue = rs.getDate(fixParameterName(parameterName));
/*      */     
/* 1273 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1275 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Date getDate(String parameterName, Calendar cal) throws SQLException {
/* 1284 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1287 */     Date retValue = rs.getDate(fixParameterName(parameterName), cal);
/*      */     
/* 1289 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1291 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized double getDouble(int parameterIndex) throws SQLException {
/* 1299 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1301 */     double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1304 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1306 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized double getDouble(String parameterName) throws SQLException {
/* 1314 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1317 */     double retValue = rs.getDouble(fixParameterName(parameterName));
/*      */     
/* 1319 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1321 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized float getFloat(int parameterIndex) throws SQLException {
/* 1328 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1330 */     float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1333 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1335 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized float getFloat(String parameterName) throws SQLException {
/* 1343 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1346 */     float retValue = rs.getFloat(fixParameterName(parameterName));
/*      */     
/* 1348 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1350 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getInt(int parameterIndex) throws SQLException {
/* 1357 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1359 */     int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1362 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1364 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getInt(String parameterName) throws SQLException {
/* 1371 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1374 */     int retValue = rs.getInt(fixParameterName(parameterName));
/*      */     
/* 1376 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1378 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized long getLong(int parameterIndex) throws SQLException {
/* 1385 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1387 */     long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1390 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1392 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized long getLong(String parameterName) throws SQLException {
/* 1399 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1402 */     long retValue = rs.getLong(fixParameterName(parameterName));
/*      */     
/* 1404 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1406 */     return retValue;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException {
/* 1411 */     if (this.connection.getNoAccessToProcedureBodies()) {
/* 1412 */       throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 1416 */     if (paramName == null || paramName.length() == 0) {
/* 1417 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), "S1009");
/*      */     }
/*      */ 
/*      */     
/* 1421 */     if (this.paramInfo == null) {
/* 1422 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.3") + paramName + Messages.getString("CallableStatement.4"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1427 */     CallableStatementParam namedParamInfo = this.paramInfo.getParameter(paramName);
/*      */ 
/*      */     
/* 1430 */     if (forOut && !namedParamInfo.isOut) {
/* 1431 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.5") + paramName + Messages.getString("CallableStatement.6"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1438 */     if (this.placeholderToParameterIndexMap == null) {
/* 1439 */       return namedParamInfo.index + 1;
/*      */     }
/*      */     
/* 1442 */     for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 1443 */       if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index) {
/* 1444 */         return i + 1;
/*      */       }
/*      */     } 
/*      */     
/* 1448 */     throw SQLError.createSQLException("Can't find local placeholder mapping for parameter named \"" + paramName + "\".", "S1009");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Object getObject(int parameterIndex) throws SQLException {
/* 1457 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/*      */     
/* 1459 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1461 */     Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredJdbcType);
/*      */ 
/*      */ 
/*      */     
/* 1465 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1467 */     return retVal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Object getObject(int parameterIndex, Map map) throws SQLException {
/* 1475 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1477 */     Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
/*      */ 
/*      */     
/* 1480 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1482 */     return retVal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Object getObject(String parameterName) throws SQLException {
/* 1490 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1493 */     Object retValue = rs.getObject(fixParameterName(parameterName));
/*      */     
/* 1495 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1497 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Object getObject(String parameterName, Map map) throws SQLException {
/* 1506 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1509 */     Object retValue = rs.getObject(fixParameterName(parameterName), map);
/*      */     
/* 1511 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1513 */     return retValue;
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
/*      */   protected ResultSetInternalMethods getOutputParameters(int paramIndex) throws SQLException {
/* 1527 */     this.outputParamWasNull = false;
/*      */     
/* 1529 */     if (paramIndex == 1 && this.callingStoredFunction && this.returnValueParam != null)
/*      */     {
/* 1531 */       return this.functionReturnValueResults;
/*      */     }
/*      */     
/* 1534 */     if (this.outputParameterResults == null) {
/* 1535 */       if (this.paramInfo.numberOfParameters() == 0) {
/* 1536 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), "S1009");
/*      */       }
/*      */ 
/*      */       
/* 1540 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), "S1000");
/*      */     } 
/*      */ 
/*      */     
/* 1544 */     return this.outputParameterResults;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized ParameterMetaData getParameterMetaData() throws SQLException {
/* 1550 */     if (this.placeholderToParameterIndexMap == null) {
/* 1551 */       return (CallableStatementParamInfoJDBC3)this.paramInfo;
/*      */     }
/* 1553 */     return new CallableStatementParamInfoJDBC3(this, this.paramInfo);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Ref getRef(int parameterIndex) throws SQLException {
/* 1561 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1563 */     Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1566 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1568 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Ref getRef(String parameterName) throws SQLException {
/* 1575 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1578 */     Ref retValue = rs.getRef(fixParameterName(parameterName));
/*      */     
/* 1580 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1582 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized short getShort(int parameterIndex) throws SQLException {
/* 1589 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1591 */     short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1594 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1596 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized short getShort(String parameterName) throws SQLException {
/* 1604 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1607 */     short retValue = rs.getShort(fixParameterName(parameterName));
/*      */     
/* 1609 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1611 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getString(int parameterIndex) throws SQLException {
/* 1619 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1621 */     String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1624 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1626 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getString(String parameterName) throws SQLException {
/* 1634 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1637 */     String retValue = rs.getString(fixParameterName(parameterName));
/*      */     
/* 1639 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1641 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Time getTime(int parameterIndex) throws SQLException {
/* 1648 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1650 */     Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1653 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1655 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Time getTime(int parameterIndex, Calendar cal) throws SQLException {
/* 1663 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1665 */     Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/*      */     
/* 1668 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1670 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Time getTime(String parameterName) throws SQLException {
/* 1677 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1680 */     Time retValue = rs.getTime(fixParameterName(parameterName));
/*      */     
/* 1682 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1684 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Time getTime(String parameterName, Calendar cal) throws SQLException {
/* 1693 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1696 */     Time retValue = rs.getTime(fixParameterName(parameterName), cal);
/*      */     
/* 1698 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1700 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Timestamp getTimestamp(int parameterIndex) throws SQLException {
/* 1708 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1710 */     Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1713 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1715 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
/* 1723 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1725 */     Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/*      */     
/* 1728 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1730 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Timestamp getTimestamp(String parameterName) throws SQLException {
/* 1738 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1741 */     Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
/*      */     
/* 1743 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1745 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
/* 1754 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1757 */     Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
/*      */ 
/*      */     
/* 1760 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1762 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized URL getURL(int parameterIndex) throws SQLException {
/* 1769 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */     
/* 1771 */     URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*      */     
/* 1774 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1776 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized URL getURL(String parameterName) throws SQLException {
/* 1783 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/*      */     
/* 1786 */     URL retValue = rs.getURL(fixParameterName(parameterName));
/*      */     
/* 1788 */     this.outputParamWasNull = rs.wasNull();
/*      */     
/* 1790 */     return retValue;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int mapOutputParameterIndexToRsIndex(int paramIndex) throws SQLException {
/* 1796 */     if (this.returnValueParam != null && paramIndex == 1) {
/* 1797 */       return 1;
/*      */     }
/*      */     
/* 1800 */     checkParameterIndexBounds(paramIndex);
/*      */     
/* 1802 */     int localParamIndex = paramIndex - 1;
/*      */     
/* 1804 */     if (this.placeholderToParameterIndexMap != null) {
/* 1805 */       localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */     }
/*      */     
/* 1808 */     int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
/*      */     
/* 1810 */     if (rsIndex == Integer.MIN_VALUE) {
/* 1811 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + paramIndex + Messages.getString("CallableStatement.22"), "S1009");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1817 */     return rsIndex + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
/* 1825 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/* 1826 */     paramDescriptor.desiredJdbcType = sqlType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
/* 1834 */     registerOutParameter(parameterIndex, sqlType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
/* 1843 */     checkIsOutputParam(parameterIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void registerOutParameter(String parameterName, int sqlType) throws SQLException {
/* 1852 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
/* 1861 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
/* 1870 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void retrieveOutParams() throws SQLException {
/* 1881 */     int numParameters = this.paramInfo.numberOfParameters();
/*      */     
/* 1883 */     this.parameterIndexToRsIndex = new int[numParameters];
/*      */     
/* 1885 */     for (int i = 0; i < numParameters; i++) {
/* 1886 */       this.parameterIndexToRsIndex[i] = Integer.MIN_VALUE;
/*      */     }
/*      */     
/* 1889 */     int localParamIndex = 0;
/*      */     
/* 1891 */     if (numParameters > 0) {
/* 1892 */       StringBuffer outParameterQuery = new StringBuffer("SELECT ");
/*      */       
/* 1894 */       boolean firstParam = true;
/* 1895 */       boolean hadOutputParams = false;
/*      */       
/* 1897 */       Iterator paramIter = this.paramInfo.iterator();
/* 1898 */       while (paramIter.hasNext()) {
/* 1899 */         CallableStatementParam retrParamInfo = paramIter.next();
/*      */ 
/*      */         
/* 1902 */         if (retrParamInfo.isOut) {
/* 1903 */           hadOutputParams = true;
/*      */           
/* 1905 */           this.parameterIndexToRsIndex[retrParamInfo.index] = localParamIndex++;
/*      */           
/* 1907 */           String outParameterName = mangleParameterName(retrParamInfo.paramName);
/*      */           
/* 1909 */           if (!firstParam) {
/* 1910 */             outParameterQuery.append(",");
/*      */           } else {
/* 1912 */             firstParam = false;
/*      */           } 
/*      */           
/* 1915 */           if (!outParameterName.startsWith("@")) {
/* 1916 */             outParameterQuery.append('@');
/*      */           }
/*      */           
/* 1919 */           outParameterQuery.append(outParameterName);
/*      */         } 
/*      */       } 
/*      */       
/* 1923 */       if (hadOutputParams) {
/*      */ 
/*      */         
/* 1926 */         Statement outParameterStmt = null;
/* 1927 */         ResultSet outParamRs = null;
/*      */         
/*      */         try {
/* 1930 */           outParameterStmt = this.connection.createStatement();
/* 1931 */           outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
/*      */           
/* 1933 */           this.outputParameterResults = ((ResultSetInternalMethods)outParamRs).copy();
/*      */ 
/*      */           
/* 1936 */           if (!this.outputParameterResults.next()) {
/* 1937 */             this.outputParameterResults.close();
/* 1938 */             this.outputParameterResults = null;
/*      */           } 
/*      */         } finally {
/* 1941 */           if (outParameterStmt != null) {
/* 1942 */             outParameterStmt.close();
/*      */           }
/*      */         } 
/*      */       } else {
/* 1946 */         this.outputParameterResults = null;
/*      */       } 
/*      */     } else {
/* 1949 */       this.outputParameterResults = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
/* 1959 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
/* 1968 */     setBigDecimal(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
/* 1977 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBoolean(String parameterName, boolean x) throws SQLException {
/* 1984 */     setBoolean(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setByte(String parameterName, byte x) throws SQLException {
/* 1991 */     setByte(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBytes(String parameterName, byte[] x) throws SQLException {
/* 1998 */     setBytes(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
/* 2007 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(String parameterName, Date x) throws SQLException {
/* 2015 */     setDate(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
/* 2024 */     setDate(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDouble(String parameterName, double x) throws SQLException {
/* 2031 */     setDouble(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFloat(String parameterName, float x) throws SQLException {
/* 2038 */     setFloat(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setInOutParamsOnServer() throws SQLException {
/* 2045 */     if (this.paramInfo.numParameters > 0) {
/* 2046 */       int parameterIndex = 0;
/*      */       
/* 2048 */       Iterator paramIter = this.paramInfo.iterator();
/* 2049 */       while (paramIter.hasNext()) {
/*      */         
/* 2051 */         CallableStatementParam inParamInfo = paramIter.next();
/*      */ 
/*      */         
/* 2054 */         if (inParamInfo.isOut && inParamInfo.isIn) {
/* 2055 */           String inOutParameterName = mangleParameterName(inParamInfo.paramName);
/* 2056 */           StringBuffer queryBuf = new StringBuffer(4 + inOutParameterName.length() + 1 + 1);
/*      */           
/* 2058 */           queryBuf.append("SET ");
/* 2059 */           queryBuf.append(inOutParameterName);
/* 2060 */           queryBuf.append("=?");
/*      */           
/* 2062 */           PreparedStatement setPstmt = null;
/*      */           
/*      */           try {
/* 2065 */             setPstmt = (PreparedStatement)this.connection.clientPrepareStatement(queryBuf.toString());
/*      */ 
/*      */             
/* 2068 */             byte[] parameterAsBytes = getBytesRepresentation(inParamInfo.index);
/*      */ 
/*      */             
/* 2071 */             if (parameterAsBytes != null) {
/* 2072 */               if (parameterAsBytes.length > 8 && parameterAsBytes[0] == 95 && parameterAsBytes[1] == 98 && parameterAsBytes[2] == 105 && parameterAsBytes[3] == 110 && parameterAsBytes[4] == 97 && parameterAsBytes[5] == 114 && parameterAsBytes[6] == 121 && parameterAsBytes[7] == 39) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2081 */                 setPstmt.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
/*      */               } else {
/*      */                 
/* 2084 */                 int sqlType = inParamInfo.desiredJdbcType;
/*      */                 
/* 2086 */                 switch (sqlType) {
/*      */                   case -7:
/*      */                   case -4:
/*      */                   case -3:
/*      */                   case -2:
/*      */                   case 2000:
/*      */                   case 2004:
/* 2093 */                     setPstmt.setBytes(1, parameterAsBytes);
/*      */                     break;
/*      */ 
/*      */                   
/*      */                   default:
/* 2098 */                     setPstmt.setBytesNoEscape(1, parameterAsBytes); break;
/*      */                 } 
/*      */               } 
/*      */             } else {
/* 2102 */               setPstmt.setNull(1, 0);
/*      */             } 
/*      */             
/* 2105 */             setPstmt.executeUpdate();
/*      */           } finally {
/* 2107 */             if (setPstmt != null) {
/* 2108 */               setPstmt.close();
/*      */             }
/*      */           } 
/*      */         } 
/*      */         
/* 2113 */         parameterIndex++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInt(String parameterName, int x) throws SQLException {
/* 2122 */     setInt(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLong(String parameterName, long x) throws SQLException {
/* 2129 */     setLong(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNull(String parameterName, int sqlType) throws SQLException {
/* 2136 */     setNull(getNamedParamIndex(parameterName, false), sqlType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
/* 2145 */     setNull(getNamedParamIndex(parameterName, false), sqlType, typeName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x) throws SQLException {
/* 2153 */     setObject(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
/* 2162 */     setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setOutParams() throws SQLException {
/* 2174 */     if (this.paramInfo.numParameters > 0) {
/* 2175 */       Iterator paramIter = this.paramInfo.iterator();
/* 2176 */       while (paramIter.hasNext()) {
/* 2177 */         CallableStatementParam outParamInfo = paramIter.next();
/*      */ 
/*      */         
/* 2180 */         if (!this.callingStoredFunction && outParamInfo.isOut) {
/* 2181 */           int outParamIndex; String outParameterName = mangleParameterName(outParamInfo.paramName);
/*      */ 
/*      */ 
/*      */           
/* 2185 */           if (this.placeholderToParameterIndexMap == null) {
/* 2186 */             outParamIndex = outParamInfo.index + 1;
/*      */           } else {
/* 2188 */             outParamIndex = this.placeholderToParameterIndexMap[outParamInfo.index - 1];
/*      */           } 
/*      */           
/* 2191 */           setBytesNoEscapeNoQuotes(outParamIndex, StringUtils.getBytes(outParameterName, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode()));
/*      */         } 
/*      */       } 
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
/*      */   public void setShort(String parameterName, short x) throws SQLException {
/* 2206 */     setShort(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setString(String parameterName, String x) throws SQLException {
/* 2214 */     setString(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTime(String parameterName, Time x) throws SQLException {
/* 2221 */     setTime(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
/* 2230 */     setTime(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
/* 2239 */     setTimestamp(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
/* 2248 */     setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURL(String parameterName, URL val) throws SQLException {
/* 2255 */     setURL(getNamedParamIndex(parameterName, false), val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean wasNull() throws SQLException {
/* 2262 */     return this.outputParamWasNull;
/*      */   }
/*      */   
/*      */   public int[] executeBatch() throws SQLException {
/* 2266 */     if (this.hasOutputParams) {
/* 2267 */       throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", "S1009");
/*      */     }
/*      */ 
/*      */     
/* 2271 */     return super.executeBatch();
/*      */   }
/*      */   
/*      */   protected int getParameterIndexOffset() {
/* 2275 */     if (this.callingStoredFunction) {
/* 2276 */       return -1;
/*      */     }
/*      */     
/* 2279 */     return super.getParameterIndexOffset();
/*      */   }
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/* 2283 */     setAsciiStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
/* 2288 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
/* 2293 */     setBinaryStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
/* 2298 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException {
/* 2303 */     setBlob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
/* 2308 */     setBlob(getNamedParamIndex(parameterName, false), inputStream);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
/* 2313 */     setBlob(getNamedParamIndex(parameterName, false), inputStream, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
/* 2318 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/* 2323 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(String parameterName, Clob x) throws SQLException {
/* 2328 */     setClob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException {
/* 2333 */     setClob(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException {
/* 2338 */     setClob(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
/* 2343 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
/* 2348 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value, length);
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\CallableStatement.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */