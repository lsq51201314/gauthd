/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
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
/*      */ public class DatabaseMetaDataUsingInfoSchema
/*      */   extends DatabaseMetaData
/*      */ {
/*      */   private boolean hasReferentialConstraintsView;
/*      */   
/*      */   protected DatabaseMetaDataUsingInfoSchema(ConnectionImpl connToSet, String databaseToSet) throws SQLException {
/*   42 */     super(connToSet, databaseToSet);
/*      */     
/*   44 */     this.hasReferentialConstraintsView = this.conn.versionMeetsMinimum(5, 1, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultSet executeMetadataQuery(PreparedStatement pStmt) throws SQLException {
/*   50 */     ResultSet rs = pStmt.executeQuery();
/*   51 */     ((ResultSetInternalMethods)rs).setOwningStatement(null);
/*      */     
/*   53 */     return rs;
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
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
/*   94 */     if (columnNamePattern == null) {
/*   95 */       if (this.conn.getNullNamePatternMatchesAll()) {
/*   96 */         columnNamePattern = "%";
/*      */       } else {
/*   98 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  104 */     if (catalog == null && 
/*  105 */       this.conn.getNullCatalogMeansCurrent()) {
/*  106 */       catalog = this.database;
/*      */     }
/*      */ 
/*      */     
/*  110 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME,COLUMN_NAME, NULL AS GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME =? AND COLUMN_NAME LIKE ? ORDER BY COLUMN_NAME, PRIVILEGE_TYPE";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  117 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  120 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  122 */       if (catalog != null) {
/*  123 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  125 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  128 */       pStmt.setString(2, table);
/*  129 */       pStmt.setString(3, columnNamePattern);
/*      */       
/*  131 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  132 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 64), new Field("", "TABLE_SCHEM", 1, 1), new Field("", "TABLE_NAME", 1, 64), new Field("", "COLUMN_NAME", 1, 64), new Field("", "GRANTOR", 1, 77), new Field("", "GRANTEE", 1, 77), new Field("", "PRIVILEGE", 1, 64), new Field("", "IS_GRANTABLE", 1, 3) });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  142 */       return rs;
/*      */     } finally {
/*  144 */       if (pStmt != null) {
/*  145 */         pStmt.close();
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
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableName, String columnNamePattern) throws SQLException {
/*  196 */     if (columnNamePattern == null) {
/*  197 */       if (this.conn.getNullNamePatternMatchesAll()) {
/*  198 */         columnNamePattern = "%";
/*      */       } else {
/*  200 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  206 */     if (catalog == null && 
/*  207 */       this.conn.getNullCatalogMeansCurrent()) {
/*  208 */       catalog = this.database;
/*      */     }
/*      */ 
/*      */     
/*  212 */     StringBuffer sqlBuf = new StringBuffer("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,TABLE_NAME,COLUMN_NAME,");
/*      */ 
/*      */     
/*  215 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */     
/*  217 */     sqlBuf.append(" AS DATA_TYPE, ");
/*      */     
/*  219 */     if (this.conn.getCapitalizeTypeNames()) {
/*  220 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS TYPE_NAME,");
/*      */     } else {
/*  222 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS TYPE_NAME,");
/*      */     } 
/*      */     
/*  225 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, " + MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH," + "NUMERIC_SCALE AS DECIMAL_DIGITS," + "10 AS NUM_PREC_RADIX," + "CASE WHEN IS_NULLABLE='NO' THEN " + Character.MIN_VALUE + " ELSE CASE WHEN IS_NULLABLE='YES' THEN " + '\001' + " ELSE " + '\002' + " END END AS NULLABLE," + "COLUMN_COMMENT AS REMARKS," + "COLUMN_DEFAULT AS COLUMN_DEF," + "0 AS SQL_DATA_TYPE," + "0 AS SQL_DATETIME_SUB," + "CASE WHEN CHARACTER_OCTET_LENGTH > " + 2147483647 + " THEN " + 2147483647 + " ELSE CHARACTER_OCTET_LENGTH END AS CHAR_OCTET_LENGTH," + "ORDINAL_POSITION," + "IS_NULLABLE," + "NULL AS SCOPE_CATALOG," + "NULL AS SCOPE_SCHEMA," + "NULL AS SCOPE_TABLE," + "NULL AS SOURCE_DATA_TYPE," + "IF (EXTRA LIKE '%auto_increment%','YES','NO') AS IS_AUTOINCREMENT " + "FROM INFORMATION_SCHEMA.COLUMNS WHERE " + "TABLE_SCHEMA LIKE ? AND " + "TABLE_NAME LIKE ? AND COLUMN_NAME LIKE ? " + "ORDER BY TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");
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
/*  249 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  252 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/*  254 */       if (catalog != null) {
/*  255 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  257 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  260 */       pStmt.setString(2, tableName);
/*  261 */       pStmt.setString(3, columnNamePattern);
/*      */       
/*  263 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  265 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "DATA_TYPE", 5, 5), new Field("", "TYPE_NAME", 1, 16), new Field("", "COLUMN_SIZE", 4, Integer.toString(2147483647).length()), new Field("", "BUFFER_LENGTH", 4, 10), new Field("", "DECIMAL_DIGITS", 4, 10), new Field("", "NUM_PREC_RADIX", 4, 10), new Field("", "NULLABLE", 4, 10), new Field("", "REMARKS", 1, 0), new Field("", "COLUMN_DEF", 1, 0), new Field("", "SQL_DATA_TYPE", 4, 10), new Field("", "SQL_DATETIME_SUB", 4, 10), new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(2147483647).length()), new Field("", "ORDINAL_POSITION", 4, 10), new Field("", "IS_NULLABLE", 1, 3), new Field("", "SCOPE_CATALOG", 1, 255), new Field("", "SCOPE_SCHEMA", 1, 255), new Field("", "SCOPE_TABLE", 1, 255), new Field("", "SOURCE_DATA_TYPE", 5, 10), new Field("", "IS_AUTOINCREMENT", 1, 3) });
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
/*  291 */       return rs;
/*      */     } finally {
/*  293 */       if (pStmt != null) {
/*  294 */         pStmt.close();
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
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
/*  369 */     if (primaryTable == null) {
/*  370 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/*  374 */     if (primaryCatalog == null && 
/*  375 */       this.conn.getNullCatalogMeansCurrent()) {
/*  376 */       primaryCatalog = this.database;
/*      */     }
/*      */ 
/*      */     
/*  380 */     if (foreignCatalog == null && 
/*  381 */       this.conn.getNullCatalogMeansCurrent()) {
/*  382 */       foreignCatalog = this.database;
/*      */     }
/*      */ 
/*      */     
/*  386 */     Field[] fields = new Field[14];
/*  387 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/*  388 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/*  389 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/*  390 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/*  391 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/*  392 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/*  393 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/*  394 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/*  395 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/*  396 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/*  397 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/*  398 */     fields[11] = new Field("", "FK_NAME", 1, 0);
/*  399 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/*  400 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */     
/*  402 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + '\007' + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "AND A.TABLE_SCHEMA LIKE ? AND A.TABLE_NAME=? " + "ORDER BY " + "A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
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
/*  436 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  439 */       pStmt = prepareMetaDataSafeStatement(sql);
/*  440 */       if (primaryCatalog != null) {
/*  441 */         pStmt.setString(1, primaryCatalog);
/*      */       } else {
/*  443 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  446 */       pStmt.setString(2, primaryTable);
/*      */       
/*  448 */       if (foreignCatalog != null) {
/*  449 */         pStmt.setString(3, foreignCatalog);
/*      */       } else {
/*  451 */         pStmt.setString(3, "%");
/*      */       } 
/*      */       
/*  454 */       pStmt.setString(4, foreignTable);
/*      */       
/*  456 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  457 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "PKTABLE_CAT", 1, 255), new Field("", "PKTABLE_SCHEM", 1, 0), new Field("", "PKTABLE_NAME", 1, 255), new Field("", "PKCOLUMN_NAME", 1, 32), new Field("", "FKTABLE_CAT", 1, 255), new Field("", "FKTABLE_SCHEM", 1, 0), new Field("", "FKTABLE_NAME", 1, 255), new Field("", "FKCOLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 2), new Field("", "UPDATE_RULE", 5, 2), new Field("", "DELETE_RULE", 5, 2), new Field("", "FK_NAME", 1, 0), new Field("", "PK_NAME", 1, 0), new Field("", "DEFERRABILITY", 4, 2) });
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
/*  473 */       return rs;
/*      */     } finally {
/*  475 */       if (pStmt != null) {
/*  476 */         pStmt.close();
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
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
/*  544 */     if (table == null) {
/*  545 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/*  549 */     if (catalog == null && 
/*  550 */       this.conn.getNullCatalogMeansCurrent()) {
/*  551 */       catalog = this.database;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  557 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME,A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + '\007' + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
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
/*  590 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  593 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  595 */       if (catalog != null) {
/*  596 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  598 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  601 */       pStmt.setString(2, table);
/*      */       
/*  603 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  605 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "PKTABLE_CAT", 1, 255), new Field("", "PKTABLE_SCHEM", 1, 0), new Field("", "PKTABLE_NAME", 1, 255), new Field("", "PKCOLUMN_NAME", 1, 32), new Field("", "FKTABLE_CAT", 1, 255), new Field("", "FKTABLE_SCHEM", 1, 0), new Field("", "FKTABLE_NAME", 1, 255), new Field("", "FKCOLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 2), new Field("", "UPDATE_RULE", 5, 2), new Field("", "DELETE_RULE", 5, 2), new Field("", "FK_NAME", 1, 255), new Field("", "PK_NAME", 1, 0), new Field("", "DEFERRABILITY", 4, 2) });
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
/*  621 */       return rs;
/*      */     } finally {
/*  623 */       if (pStmt != null) {
/*  624 */         pStmt.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private String generateOptionalRefContraintsJoin() {
/*  631 */     return this.hasReferentialConstraintsView ? "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND R.TABLE_NAME = B.TABLE_NAME AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA) " : "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String generateDeleteRuleClause() {
/*  639 */     return this.hasReferentialConstraintsView ? ("CASE WHEN R.DELETE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.DELETE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.DELETE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.DELETE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.DELETE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END ") : String.valueOf(1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String generateUpdateRuleClause() {
/*  649 */     return this.hasReferentialConstraintsView ? ("CASE WHEN R.UPDATE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.UPDATE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.UPDATE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.UPDATE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.UPDATE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END ") : String.valueOf(1);
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
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
/*  719 */     if (table == null) {
/*  720 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/*  724 */     if (catalog == null && 
/*  725 */       this.conn.getNullCatalogMeansCurrent()) {
/*  726 */       catalog = this.database;
/*      */     }
/*      */ 
/*      */     
/*  730 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + '\007' + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A " + "JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING " + "(CONSTRAINT_NAME, TABLE_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.TABLE_SCHEMA LIKE ? " + "AND A.TABLE_NAME=? " + "AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL " + "ORDER BY " + "A.REFERENCED_TABLE_SCHEMA, A.REFERENCED_TABLE_NAME, " + "A.ORDINAL_POSITION";
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
/*  767 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  770 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  772 */       if (catalog != null) {
/*  773 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  775 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  778 */       pStmt.setString(2, table);
/*      */       
/*  780 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  782 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "PKTABLE_CAT", 1, 255), new Field("", "PKTABLE_SCHEM", 1, 0), new Field("", "PKTABLE_NAME", 1, 255), new Field("", "PKCOLUMN_NAME", 1, 32), new Field("", "FKTABLE_CAT", 1, 255), new Field("", "FKTABLE_SCHEM", 1, 0), new Field("", "FKTABLE_NAME", 1, 255), new Field("", "FKCOLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 2), new Field("", "UPDATE_RULE", 5, 2), new Field("", "DELETE_RULE", 5, 2), new Field("", "FK_NAME", 1, 255), new Field("", "PK_NAME", 1, 0), new Field("", "DEFERRABILITY", 4, 2) });
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
/*  798 */       return rs;
/*      */     } finally {
/*  800 */       if (pStmt != null) {
/*  801 */         pStmt.close();
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
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
/*  866 */     StringBuffer sqlBuf = new StringBuffer("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,TABLE_NAME,NON_UNIQUE,TABLE_SCHEMA AS INDEX_QUALIFIER,INDEX_NAME,3 AS TYPE,SEQ_IN_INDEX AS ORDINAL_POSITION,COLUMN_NAME,COLLATION AS ASC_OR_DESC,CARDINALITY,NULL AS PAGES,NULL AS FILTER_CONDITION FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ?");
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
/*  877 */     if (unique) {
/*  878 */       sqlBuf.append(" AND NON_UNIQUE=0 ");
/*      */     }
/*      */     
/*  881 */     sqlBuf.append("ORDER BY NON_UNIQUE, INDEX_NAME, SEQ_IN_INDEX");
/*      */     
/*  883 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  886 */       if (catalog == null && 
/*  887 */         this.conn.getNullCatalogMeansCurrent()) {
/*  888 */         catalog = this.database;
/*      */       }
/*      */ 
/*      */       
/*  892 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/*  894 */       if (catalog != null) {
/*  895 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  897 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  900 */       pStmt.setString(2, table);
/*      */       
/*  902 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  904 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "NON_UNIQUE", 1, 4), new Field("", "INDEX_QUALIFIER", 1, 1), new Field("", "INDEX_NAME", 1, 32), new Field("", "TYPE", 1, 32), new Field("", "ORDINAL_POSITION", 5, 5), new Field("", "COLUMN_NAME", 1, 32), new Field("", "ASC_OR_DESC", 1, 1), new Field("", "CARDINALITY", 4, 10), new Field("", "PAGES", 4, 10), new Field("", "FILTER_CONDITION", 1, 32) });
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
/*  919 */       return rs;
/*      */     } finally {
/*  921 */       if (pStmt != null) {
/*  922 */         pStmt.close();
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
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
/*  955 */     if (catalog == null && 
/*  956 */       this.conn.getNullCatalogMeansCurrent()) {
/*  957 */       catalog = this.database;
/*      */     }
/*      */ 
/*      */     
/*  961 */     if (table == null) {
/*  962 */       throw SQLError.createSQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*      */     
/*  966 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, SEQ_IN_INDEX AS KEY_SEQ, 'PRIMARY' AS PK_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND INDEX_NAME='PRIMARY' ORDER BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  971 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/*  974 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  976 */       if (catalog != null) {
/*  977 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  979 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/*  982 */       pStmt.setString(2, table);
/*      */       
/*  984 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  985 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 5), new Field("", "PK_NAME", 1, 32) });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  993 */       return rs;
/*      */     } finally {
/*  995 */       if (pStmt != null) {
/*  996 */         pStmt.close();
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
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
/* 1044 */     if (procedureNamePattern == null || procedureNamePattern.length() == 0)
/*      */     {
/* 1046 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1047 */         procedureNamePattern = "%";
/*      */       } else {
/* 1049 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1055 */     String db = null;
/*      */     
/* 1057 */     if (catalog == null && 
/* 1058 */       this.conn.getNullCatalogMeansCurrent()) {
/* 1059 */       db = this.database;
/*      */     }
/*      */ 
/*      */     
/* 1063 */     String sql = "SELECT ROUTINE_SCHEMA AS PROCEDURE_CAT, NULL AS PROCEDURE_SCHEM, ROUTINE_NAME AS PROCEDURE_NAME, NULL AS RESERVED_1, NULL AS RESERVED_2, NULL AS RESERVED_3, ROUTINE_COMMENT AS REMARKS, CASE WHEN ROUTINE_TYPE = 'PROCEDURE' THEN 1 WHEN ROUTINE_TYPE='FUNCTION' THEN 2 ELSE 0 END AS PROCEDURE_TYPE FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_SCHEMA LIKE ? AND ROUTINE_NAME LIKE ? ORDER BY ROUTINE_SCHEMA, ROUTINE_NAME";
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
/* 1076 */     PreparedStatement pStmt = null;
/*      */     
/*      */     try {
/* 1079 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/* 1081 */       if (db != null) {
/* 1082 */         pStmt.setString(1, db);
/*      */       } else {
/* 1084 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/* 1087 */       pStmt.setString(2, procedureNamePattern);
/*      */       
/* 1089 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1090 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "PROCEDURE_CAT", 1, 0), new Field("", "PROCEDURE_SCHEM", 1, 0), new Field("", "PROCEDURE_NAME", 1, 0), new Field("", "reserved1", 1, 0), new Field("", "reserved2", 1, 0), new Field("", "reserved3", 1, 0), new Field("", "REMARKS", 1, 0), new Field("", "PROCEDURE_TYPE", 5, 0) });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1100 */       return rs;
/*      */     } finally {
/* 1102 */       if (pStmt != null) {
/* 1103 */         pStmt.close();
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
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
/* 1146 */     if (catalog == null && 
/* 1147 */       this.conn.getNullCatalogMeansCurrent()) {
/* 1148 */       catalog = this.database;
/*      */     }
/*      */ 
/*      */     
/* 1152 */     if (tableNamePattern == null) {
/* 1153 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1154 */         tableNamePattern = "%";
/*      */       } else {
/* 1156 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1162 */     PreparedStatement pStmt = null;
/*      */     
/* 1164 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, CASE WHEN TABLE_TYPE='BASE TABLE' THEN 'TABLE' WHEN TABLE_TYPE='TEMPORARY' THEN 'LOCAL_TEMPORARY' ELSE TABLE_TYPE END AS TABLE_TYPE, TABLE_COMMENT AS REMARKS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND TABLE_TYPE IN (?,?,?) ORDER BY TABLE_TYPE, TABLE_SCHEMA, TABLE_NAME";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1172 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/* 1174 */       if (catalog != null) {
/* 1175 */         pStmt.setString(1, catalog);
/*      */       } else {
/* 1177 */         pStmt.setString(1, "%");
/*      */       } 
/*      */       
/* 1180 */       pStmt.setString(2, tableNamePattern);
/*      */ 
/*      */ 
/*      */       
/* 1184 */       if (types == null || types.length == 0) {
/* 1185 */         pStmt.setString(3, "BASE TABLE");
/* 1186 */         pStmt.setString(4, "VIEW");
/* 1187 */         pStmt.setString(5, "TEMPORARY");
/*      */       } else {
/* 1189 */         pStmt.setNull(3, 12);
/* 1190 */         pStmt.setNull(4, 12);
/* 1191 */         pStmt.setNull(5, 12);
/*      */         
/* 1193 */         for (int i = 0; i < types.length; i++) {
/* 1194 */           if ("TABLE".equalsIgnoreCase(types[i])) {
/* 1195 */             pStmt.setString(3, "BASE TABLE");
/*      */           }
/*      */           
/* 1198 */           if ("VIEW".equalsIgnoreCase(types[i])) {
/* 1199 */             pStmt.setString(4, "VIEW");
/*      */           }
/*      */           
/* 1202 */           if ("LOCAL TEMPORARY".equalsIgnoreCase(types[i])) {
/* 1203 */             pStmt.setString(5, "TEMPORARY");
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1208 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/* 1210 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 12, (catalog == null) ? 0 : catalog.length()), new Field("", "TABLE_SCHEM", 12, 0), new Field("", "TABLE_NAME", 12, 255), new Field("", "TABLE_TYPE", 12, 5), new Field("", "REMARKS", 12, 0) });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1218 */       return rs;
/*      */     } finally {
/* 1220 */       if (pStmt != null) {
/* 1221 */         pStmt.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PreparedStatement prepareMetaDataSafeStatement(String sql) throws SQLException {
/* 1230 */     PreparedStatement pStmt = (PreparedStatement)this.conn.clientPrepareStatement(sql);
/*      */     
/* 1232 */     if (pStmt.getMaxRows() != 0) {
/* 1233 */       pStmt.setMaxRows(0);
/*      */     }
/*      */     
/* 1236 */     pStmt.setHoldResultsOpenOverClose(true);
/*      */     
/* 1238 */     return pStmt;
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\DatabaseMetaDataUsingInfoSchema.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */