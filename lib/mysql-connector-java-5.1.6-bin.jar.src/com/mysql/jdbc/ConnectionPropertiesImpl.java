/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.StandardLogger;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.sql.DriverPropertyInfo;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TreeMap;
/*      */ import javax.naming.RefAddr;
/*      */ import javax.naming.Reference;
/*      */ import javax.naming.StringRefAddr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ConnectionPropertiesImpl
/*      */   implements Serializable, ConnectionProperties
/*      */ {
/*      */   private static final long serialVersionUID = 4257801713007640580L;
/*      */   
/*      */   class BooleanConnectionProperty
/*      */     extends ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 2540132501709159404L;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */     
/*      */     BooleanConnectionProperty(ConnectionPropertiesImpl this$0, String propertyNameToSet, boolean defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*   73 */       super(this$0, propertyNameToSet, Boolean.valueOf(defaultValueToSet), null, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */       this.this$0 = this$0;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String[] getAllowableValues() {
/*   82 */       return new String[] { "true", "false", "yes", "no" };
/*      */     }
/*      */     
/*      */     boolean getValueAsBoolean() {
/*   86 */       return ((Boolean)this.valueAsObject).booleanValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasValueConstraints() {
/*   93 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  100 */       if (extractedValue != null) {
/*  101 */         validateStringValues(extractedValue);
/*      */         
/*  103 */         this.valueAsObject = Boolean.valueOf((extractedValue.equalsIgnoreCase("TRUE") || extractedValue.equalsIgnoreCase("YES")));
/*      */       }
/*      */       else {
/*      */         
/*  107 */         this.valueAsObject = this.defaultValue;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean isRangeBased() {
/*  115 */       return false;
/*      */     }
/*      */     
/*      */     void setValue(boolean valueFlag) {
/*  119 */       this.valueAsObject = Boolean.valueOf(valueFlag);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   abstract class ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     String[] allowableValues;
/*      */     
/*      */     String categoryName;
/*      */     
/*      */     Object defaultValue;
/*      */     
/*      */     int lowerBound;
/*      */     
/*      */     int order;
/*      */     
/*      */     String propertyName;
/*      */     String sinceVersion;
/*      */     int upperBound;
/*      */     Object valueAsObject;
/*      */     boolean required;
/*      */     String description;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */     
/*      */     public ConnectionProperty(ConnectionPropertiesImpl this$0) {
/*  146 */       this.this$0 = this$0;
/*      */     }
/*      */ 
/*      */     
/*      */     ConnectionProperty(ConnectionPropertiesImpl this$0, String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  151 */       this.this$0 = this$0;
/*      */       
/*  153 */       this.description = descriptionToSet;
/*  154 */       this.propertyName = propertyNameToSet;
/*  155 */       this.defaultValue = defaultValueToSet;
/*  156 */       this.valueAsObject = defaultValueToSet;
/*  157 */       this.allowableValues = allowableValuesToSet;
/*  158 */       this.lowerBound = lowerBoundToSet;
/*  159 */       this.upperBound = upperBoundToSet;
/*  160 */       this.required = false;
/*  161 */       this.sinceVersion = sinceVersionToSet;
/*  162 */       this.categoryName = category;
/*  163 */       this.order = orderInCategory;
/*      */     }
/*      */     
/*      */     String[] getAllowableValues() {
/*  167 */       return this.allowableValues;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getCategoryName() {
/*  174 */       return this.categoryName;
/*      */     }
/*      */     
/*      */     Object getDefaultValue() {
/*  178 */       return this.defaultValue;
/*      */     }
/*      */     
/*      */     int getLowerBound() {
/*  182 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getOrder() {
/*  189 */       return this.order;
/*      */     }
/*      */     
/*      */     String getPropertyName() {
/*  193 */       return this.propertyName;
/*      */     }
/*      */     
/*      */     int getUpperBound() {
/*  197 */       return this.upperBound;
/*      */     }
/*      */     
/*      */     Object getValueAsObject() {
/*  201 */       return this.valueAsObject;
/*      */     }
/*      */     
/*      */     abstract boolean hasValueConstraints();
/*      */     
/*      */     void initializeFrom(Properties extractFrom) throws SQLException {
/*  207 */       String extractedValue = extractFrom.getProperty(getPropertyName());
/*  208 */       extractFrom.remove(getPropertyName());
/*  209 */       initializeFrom(extractedValue);
/*      */     }
/*      */     
/*      */     void initializeFrom(Reference ref) throws SQLException {
/*  213 */       RefAddr refAddr = ref.get(getPropertyName());
/*      */       
/*  215 */       if (refAddr != null) {
/*  216 */         String refContentAsString = (String)refAddr.getContent();
/*      */         
/*  218 */         initializeFrom(refContentAsString);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     abstract void initializeFrom(String param1String) throws SQLException;
/*      */ 
/*      */     
/*      */     abstract boolean isRangeBased();
/*      */ 
/*      */     
/*      */     void setCategoryName(String categoryName) {
/*  231 */       this.categoryName = categoryName;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void setOrder(int order) {
/*  239 */       this.order = order;
/*      */     }
/*      */     
/*      */     void setValueAsObject(Object obj) {
/*  243 */       this.valueAsObject = obj;
/*      */     }
/*      */     
/*      */     void storeTo(Reference ref) {
/*  247 */       if (getValueAsObject() != null) {
/*  248 */         ref.add(new StringRefAddr(getPropertyName(), getValueAsObject().toString()));
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     DriverPropertyInfo getAsDriverPropertyInfo() {
/*  254 */       DriverPropertyInfo dpi = new DriverPropertyInfo(this.propertyName, null);
/*  255 */       dpi.choices = getAllowableValues();
/*  256 */       dpi.value = (this.valueAsObject != null) ? this.valueAsObject.toString() : null;
/*  257 */       dpi.required = this.required;
/*  258 */       dpi.description = this.description;
/*      */       
/*  260 */       return dpi;
/*      */     }
/*      */ 
/*      */     
/*      */     void validateStringValues(String valueToValidate) throws SQLException {
/*  265 */       String[] validateAgainst = getAllowableValues();
/*      */       
/*  267 */       if (valueToValidate == null) {
/*      */         return;
/*      */       }
/*      */       
/*  271 */       if (validateAgainst == null || validateAgainst.length == 0) {
/*      */         return;
/*      */       }
/*      */       
/*  275 */       for (int i = 0; i < validateAgainst.length; i++) {
/*  276 */         if (validateAgainst[i] != null && validateAgainst[i].equalsIgnoreCase(valueToValidate)) {
/*      */           return;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/*  282 */       StringBuffer errorMessageBuf = new StringBuffer();
/*      */       
/*  284 */       errorMessageBuf.append("The connection property '");
/*  285 */       errorMessageBuf.append(getPropertyName());
/*  286 */       errorMessageBuf.append("' only accepts values of the form: ");
/*      */       
/*  288 */       if (validateAgainst.length != 0) {
/*  289 */         errorMessageBuf.append("'");
/*  290 */         errorMessageBuf.append(validateAgainst[0]);
/*  291 */         errorMessageBuf.append("'");
/*      */         
/*  293 */         for (int j = 1; j < validateAgainst.length - 1; j++) {
/*  294 */           errorMessageBuf.append(", ");
/*  295 */           errorMessageBuf.append("'");
/*  296 */           errorMessageBuf.append(validateAgainst[j]);
/*  297 */           errorMessageBuf.append("'");
/*      */         } 
/*      */         
/*  300 */         errorMessageBuf.append(" or '");
/*  301 */         errorMessageBuf.append(validateAgainst[validateAgainst.length - 1]);
/*      */         
/*  303 */         errorMessageBuf.append("'");
/*      */       } 
/*      */       
/*  306 */       errorMessageBuf.append(". The value '");
/*  307 */       errorMessageBuf.append(valueToValidate);
/*  308 */       errorMessageBuf.append("' is not in this set.");
/*      */       
/*  310 */       throw SQLError.createSQLException(errorMessageBuf.toString(), "S1009");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   class IntegerConnectionProperty
/*      */     extends ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -3004305481796850832L;
/*      */     int multiplier;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */     
/*      */     public IntegerConnectionProperty(ConnectionPropertiesImpl this$0, String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  324 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */ 
/*      */       
/*      */       ConnectionPropertiesImpl.this = ConnectionPropertiesImpl.this;
/*      */       
/*  329 */       this.multiplier = 1;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     IntegerConnectionProperty(ConnectionPropertiesImpl this$0, String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  335 */       super(propertyNameToSet, new Integer(defaultValueToSet), null, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */       ConnectionPropertiesImpl.this = ConnectionPropertiesImpl.this;
/*      */       this.multiplier = 1;
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
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  353 */       this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String[] getAllowableValues() {
/*  361 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getLowerBound() {
/*  368 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getUpperBound() {
/*  375 */       return this.upperBound;
/*      */     }
/*      */     
/*      */     int getValueAsInt() {
/*  379 */       return ((Integer)this.valueAsObject).intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasValueConstraints() {
/*  386 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  393 */       if (extractedValue != null) {
/*      */         
/*      */         try {
/*  396 */           int intValue = Double.valueOf(extractedValue).intValue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  407 */           this.valueAsObject = new Integer(intValue * this.multiplier);
/*  408 */         } catch (NumberFormatException nfe) {
/*  409 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values. The value '" + extractedValue + "' can not be converted to an integer.", "S1009");
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  417 */         this.valueAsObject = this.defaultValue;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean isRangeBased() {
/*  425 */       return (getUpperBound() != getLowerBound());
/*      */     }
/*      */     
/*      */     void setValue(int valueFlag) {
/*  429 */       this.valueAsObject = new Integer(valueFlag);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public class LongConnectionProperty
/*      */     extends IntegerConnectionProperty
/*      */   {
/*      */     private static final long serialVersionUID = 6068572984340480895L;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */     
/*      */     LongConnectionProperty(ConnectionPropertiesImpl this$0, String propertyNameToSet, long defaultValueToSet, long lowerBoundToSet, long upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  441 */       super(propertyNameToSet, new Long(defaultValueToSet), (String[])null, (int)lowerBoundToSet, (int)upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */       ConnectionPropertiesImpl.this = ConnectionPropertiesImpl.this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  450 */       this(propertyNameToSet, defaultValueToSet, 0L, 0L, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void setValue(long value) {
/*  457 */       this.valueAsObject = new Long(value);
/*      */     }
/*      */     
/*      */     long getValueAsLong() {
/*  461 */       return ((Long)this.valueAsObject).longValue();
/*      */     }
/*      */     
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  465 */       if (extractedValue != null) {
/*      */         
/*      */         try {
/*  468 */           long longValue = Double.valueOf(extractedValue).longValue();
/*      */           
/*  470 */           this.valueAsObject = new Long(longValue);
/*  471 */         } catch (NumberFormatException nfe) {
/*  472 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values. The value '" + extractedValue + "' can not be converted to a long integer.", "S1009");
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  480 */         this.valueAsObject = this.defaultValue;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   class MemorySizeConnectionProperty
/*      */     extends IntegerConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7351065128998572656L;
/*      */     private String valueAsString;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */     
/*      */     MemorySizeConnectionProperty(ConnectionPropertiesImpl this$0, String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  495 */       super(this$0, propertyNameToSet, defaultValueToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */       this.this$0 = this$0;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  502 */       this.valueAsString = extractedValue;
/*      */       
/*  504 */       if (extractedValue != null) {
/*  505 */         if (extractedValue.endsWith("k") || extractedValue.endsWith("K") || extractedValue.endsWith("kb") || extractedValue.endsWith("Kb") || extractedValue.endsWith("kB")) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  510 */           this.multiplier = 1024;
/*  511 */           int indexOfK = StringUtils.indexOfIgnoreCase(extractedValue, "k");
/*      */           
/*  513 */           extractedValue = extractedValue.substring(0, indexOfK);
/*  514 */         } else if (extractedValue.endsWith("m") || extractedValue.endsWith("M") || extractedValue.endsWith("G") || extractedValue.endsWith("mb") || extractedValue.endsWith("Mb") || extractedValue.endsWith("mB")) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  520 */           this.multiplier = 1048576;
/*  521 */           int indexOfM = StringUtils.indexOfIgnoreCase(extractedValue, "m");
/*      */           
/*  523 */           extractedValue = extractedValue.substring(0, indexOfM);
/*  524 */         } else if (extractedValue.endsWith("g") || extractedValue.endsWith("G") || extractedValue.endsWith("gb") || extractedValue.endsWith("Gb") || extractedValue.endsWith("gB")) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  529 */           this.multiplier = 1073741824;
/*  530 */           int indexOfG = StringUtils.indexOfIgnoreCase(extractedValue, "g");
/*      */           
/*  532 */           extractedValue = extractedValue.substring(0, indexOfG);
/*      */         } 
/*      */       }
/*      */       
/*  536 */       super.initializeFrom(extractedValue);
/*      */     }
/*      */     
/*      */     void setValue(String value) throws SQLException {
/*  540 */       initializeFrom(value);
/*      */     }
/*      */     
/*      */     String getValueAsString() {
/*  544 */       return this.valueAsString;
/*      */     }
/*      */   }
/*      */   
/*      */   class StringConnectionProperty
/*      */     extends ConnectionProperty
/*      */     implements Serializable {
/*      */     private static final long serialVersionUID = 5432127962785948272L;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */     
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  555 */       this(propertyNameToSet, defaultValueToSet, (String[])null, descriptionToSet, sinceVersionToSet, category, orderInCategory);
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
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String[] allowableValuesToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
/*  573 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */       ConnectionPropertiesImpl.this = ConnectionPropertiesImpl.this;
/*      */     }
/*      */ 
/*      */     
/*      */     String getValueAsString() {
/*  579 */       return (String)this.valueAsObject;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasValueConstraints() {
/*  586 */       return (this.allowableValues != null && this.allowableValues.length > 0);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  594 */       if (extractedValue != null) {
/*  595 */         validateStringValues(extractedValue);
/*      */         
/*  597 */         this.valueAsObject = extractedValue;
/*      */       } else {
/*  599 */         this.valueAsObject = this.defaultValue;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean isRangeBased() {
/*  607 */       return false;
/*      */     }
/*      */     
/*      */     void setValue(String valueFlag) {
/*  611 */       this.valueAsObject = valueFlag;
/*      */     }
/*      */   }
/*      */   
/*  615 */   private static final String CONNECTION_AND_AUTH_CATEGORY = Messages.getString("ConnectionProperties.categoryConnectionAuthentication");
/*      */   
/*  617 */   private static final String NETWORK_CATEGORY = Messages.getString("ConnectionProperties.categoryNetworking");
/*      */   
/*  619 */   private static final String DEBUGING_PROFILING_CATEGORY = Messages.getString("ConnectionProperties.categoryDebuggingProfiling");
/*      */   
/*  621 */   private static final String HA_CATEGORY = Messages.getString("ConnectionProperties.categorryHA");
/*      */   
/*  623 */   private static final String MISC_CATEGORY = Messages.getString("ConnectionProperties.categoryMisc");
/*      */   
/*  625 */   private static final String PERFORMANCE_CATEGORY = Messages.getString("ConnectionProperties.categoryPerformance");
/*      */   
/*  627 */   private static final String SECURITY_CATEGORY = Messages.getString("ConnectionProperties.categorySecurity");
/*      */   
/*  629 */   private static final String[] PROPERTY_CATEGORIES = new String[] { CONNECTION_AND_AUTH_CATEGORY, NETWORK_CATEGORY, HA_CATEGORY, SECURITY_CATEGORY, PERFORMANCE_CATEGORY, DEBUGING_PROFILING_CATEGORY, MISC_CATEGORY };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  634 */   private static final ArrayList PROPERTY_LIST = new ArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  639 */   private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
/*      */   
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
/*      */   
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
/*      */   
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
/*      */   
/*      */   static {
/*      */     try {
/*  649 */       Field[] declaredFields = ConnectionPropertiesImpl.class.getDeclaredFields();
/*      */ 
/*      */       
/*  652 */       for (int i = 0; i < declaredFields.length; i++) {
/*  653 */         if (ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType()))
/*      */         {
/*  655 */           PROPERTY_LIST.add(declaredFields[i]);
/*      */         }
/*      */       } 
/*  658 */     } catch (Exception ex) {
/*  659 */       RuntimeException rtEx = new RuntimeException();
/*  660 */       rtEx.initCause(ex);
/*      */       
/*  662 */       throw rtEx;
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
/*      */   protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve) throws SQLException {
/*  682 */     return (new ConnectionPropertiesImpl() {  }).exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
/*      */   }
/*      */ 
/*      */   
/*  686 */   private BooleanConnectionProperty allowLoadLocalInfile = new BooleanConnectionProperty(this, "allowLoadLocalInfile", true, Messages.getString("ConnectionProperties.loadDataLocal"), "3.0.3", SECURITY_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  692 */   private BooleanConnectionProperty allowMultiQueries = new BooleanConnectionProperty(this, "allowMultiQueries", false, Messages.getString("ConnectionProperties.allowMultiQueries"), "3.1.1", SECURITY_CATEGORY, 1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  698 */   private BooleanConnectionProperty allowNanAndInf = new BooleanConnectionProperty(this, "allowNanAndInf", false, Messages.getString("ConnectionProperties.allowNANandINF"), "3.1.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  704 */   private BooleanConnectionProperty allowUrlInLocalInfile = new BooleanConnectionProperty(this, "allowUrlInLocalInfile", false, Messages.getString("ConnectionProperties.allowUrlInLoadLocal"), "3.1.4", SECURITY_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  710 */   private BooleanConnectionProperty alwaysSendSetIsolation = new BooleanConnectionProperty(this, "alwaysSendSetIsolation", true, Messages.getString("ConnectionProperties.alwaysSendSetIsolation"), "3.1.7", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  716 */   private BooleanConnectionProperty autoClosePStmtStreams = new BooleanConnectionProperty(this, "autoClosePStmtStreams", false, Messages.getString("ConnectionProperties.autoClosePstmtStreams"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  724 */   private BooleanConnectionProperty autoDeserialize = new BooleanConnectionProperty(this, "autoDeserialize", false, Messages.getString("ConnectionProperties.autoDeserialize"), "3.1.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  730 */   private BooleanConnectionProperty autoGenerateTestcaseScript = new BooleanConnectionProperty(this, "autoGenerateTestcaseScript", false, Messages.getString("ConnectionProperties.autoGenerateTestcaseScript"), "3.1.9", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean autoGenerateTestcaseScriptAsBoolean = false;
/*      */ 
/*      */   
/*  737 */   private BooleanConnectionProperty autoReconnect = new BooleanConnectionProperty(this, "autoReconnect", false, Messages.getString("ConnectionProperties.autoReconnect"), "1.1", HA_CATEGORY, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  743 */   private BooleanConnectionProperty autoReconnectForPools = new BooleanConnectionProperty(this, "autoReconnectForPools", false, Messages.getString("ConnectionProperties.autoReconnectForPools"), "3.1.3", HA_CATEGORY, 1);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean autoReconnectForPoolsAsBoolean = false;
/*      */ 
/*      */ 
/*      */   
/*  751 */   private MemorySizeConnectionProperty blobSendChunkSize = new MemorySizeConnectionProperty(this, "blobSendChunkSize", 1048576, 1, 2147483647, Messages.getString("ConnectionProperties.blobSendChunkSize"), "3.1.9", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  759 */   private BooleanConnectionProperty autoSlowLog = new BooleanConnectionProperty(this, "autoSlowLog", true, Messages.getString("ConnectionProperties.autoSlowLog"), "5.1.4", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  764 */   private BooleanConnectionProperty blobsAreStrings = new BooleanConnectionProperty(this, "blobsAreStrings", false, "Should the driver always treat BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  770 */   private BooleanConnectionProperty functionsNeverReturnBlobs = new BooleanConnectionProperty(this, "functionsNeverReturnBlobs", false, "Should the driver always treat data from functions returning BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  776 */   private BooleanConnectionProperty cacheCallableStatements = new BooleanConnectionProperty(this, "cacheCallableStmts", false, Messages.getString("ConnectionProperties.cacheCallableStatements"), "3.1.2", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  781 */   private BooleanConnectionProperty cachePreparedStatements = new BooleanConnectionProperty(this, "cachePrepStmts", false, Messages.getString("ConnectionProperties.cachePrepStmts"), "3.0.10", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  787 */   private BooleanConnectionProperty cacheResultSetMetadata = new BooleanConnectionProperty(this, "cacheResultSetMetadata", false, Messages.getString("ConnectionProperties.cacheRSMetadata"), "3.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean cacheResultSetMetaDataAsBoolean;
/*      */ 
/*      */ 
/*      */   
/*  795 */   private BooleanConnectionProperty cacheServerConfiguration = new BooleanConnectionProperty(this, "cacheServerConfiguration", false, Messages.getString("ConnectionProperties.cacheServerConfiguration"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  801 */   private IntegerConnectionProperty callableStatementCacheSize = new IntegerConnectionProperty("callableStmtCacheSize", 100, 0, 2147483647, Messages.getString("ConnectionProperties.callableStmtCacheSize"), "3.1.2", PERFORMANCE_CATEGORY, 5);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  809 */   private BooleanConnectionProperty capitalizeTypeNames = new BooleanConnectionProperty(this, "capitalizeTypeNames", true, Messages.getString("ConnectionProperties.capitalizeTypeNames"), "2.0.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  815 */   private StringConnectionProperty characterEncoding = new StringConnectionProperty("characterEncoding", null, Messages.getString("ConnectionProperties.characterEncoding"), "1.1g", MISC_CATEGORY, 5);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  821 */   private String characterEncodingAsString = null;
/*      */   
/*  823 */   private StringConnectionProperty characterSetResults = new StringConnectionProperty("characterSetResults", null, Messages.getString("ConnectionProperties.characterSetResults"), "3.0.13", MISC_CATEGORY, 6);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  828 */   private StringConnectionProperty clientInfoProvider = new StringConnectionProperty("clientInfoProvider", "com.mysql.jdbc.JDBC4CommentClientInfoProvider", Messages.getString("ConnectionProperties.clientInfoProvider"), "5.1.0", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  834 */   private BooleanConnectionProperty clobberStreamingResults = new BooleanConnectionProperty(this, "clobberStreamingResults", false, Messages.getString("ConnectionProperties.clobberStreamingResults"), "3.0.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  840 */   private StringConnectionProperty clobCharacterEncoding = new StringConnectionProperty("clobCharacterEncoding", null, Messages.getString("ConnectionProperties.clobCharacterEncoding"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  846 */   private StringConnectionProperty connectionCollation = new StringConnectionProperty("connectionCollation", null, Messages.getString("ConnectionProperties.connectionCollation"), "3.0.13", MISC_CATEGORY, 7);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  852 */   private StringConnectionProperty connectionLifecycleInterceptors = new StringConnectionProperty("connectionLifecycleInterceptors", null, Messages.getString("ConnectionProperties.connectionLifecycleInterceptors"), "5.1.4", CONNECTION_AND_AUTH_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  858 */   private IntegerConnectionProperty connectTimeout = new IntegerConnectionProperty("connectTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.connectTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 9);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  863 */   private BooleanConnectionProperty continueBatchOnError = new BooleanConnectionProperty(this, "continueBatchOnError", true, Messages.getString("ConnectionProperties.continueBatchOnError"), "3.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  869 */   private BooleanConnectionProperty createDatabaseIfNotExist = new BooleanConnectionProperty(this, "createDatabaseIfNotExist", false, Messages.getString("ConnectionProperties.createDatabaseIfNotExist"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  875 */   private IntegerConnectionProperty defaultFetchSize = new IntegerConnectionProperty("defaultFetchSize", 0, Messages.getString("ConnectionProperties.defaultFetchSize"), "3.1.9", PERFORMANCE_CATEGORY, -2147483648);
/*      */   
/*  877 */   private BooleanConnectionProperty detectServerPreparedStmts = new BooleanConnectionProperty(this, "useServerPrepStmts", false, Messages.getString("ConnectionProperties.useServerPrepStmts"), "3.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  883 */   private BooleanConnectionProperty dontTrackOpenResources = new BooleanConnectionProperty(this, "dontTrackOpenResources", false, Messages.getString("ConnectionProperties.dontTrackOpenResources"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  889 */   private BooleanConnectionProperty dumpQueriesOnException = new BooleanConnectionProperty(this, "dumpQueriesOnException", false, Messages.getString("ConnectionProperties.dumpQueriesOnException"), "3.1.3", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  895 */   private BooleanConnectionProperty dynamicCalendars = new BooleanConnectionProperty(this, "dynamicCalendars", false, Messages.getString("ConnectionProperties.dynamicCalendars"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  901 */   private BooleanConnectionProperty elideSetAutoCommits = new BooleanConnectionProperty(this, "elideSetAutoCommits", false, Messages.getString("ConnectionProperties.eliseSetAutoCommit"), "3.1.3", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  907 */   private BooleanConnectionProperty emptyStringsConvertToZero = new BooleanConnectionProperty(this, "emptyStringsConvertToZero", true, Messages.getString("ConnectionProperties.emptyStringsConvertToZero"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  912 */   private BooleanConnectionProperty emulateLocators = new BooleanConnectionProperty(this, "emulateLocators", false, Messages.getString("ConnectionProperties.emulateLocators"), "3.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/*  916 */   private BooleanConnectionProperty emulateUnsupportedPstmts = new BooleanConnectionProperty(this, "emulateUnsupportedPstmts", true, Messages.getString("ConnectionProperties.emulateUnsupportedPstmts"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  922 */   private BooleanConnectionProperty enablePacketDebug = new BooleanConnectionProperty(this, "enablePacketDebug", false, Messages.getString("ConnectionProperties.enablePacketDebug"), "3.1.3", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  928 */   private BooleanConnectionProperty enableQueryTimeouts = new BooleanConnectionProperty(this, "enableQueryTimeouts", true, Messages.getString("ConnectionProperties.enableQueryTimeouts"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  935 */   private BooleanConnectionProperty explainSlowQueries = new BooleanConnectionProperty(this, "explainSlowQueries", false, Messages.getString("ConnectionProperties.explainSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  942 */   private BooleanConnectionProperty failOverReadOnly = new BooleanConnectionProperty(this, "failOverReadOnly", true, Messages.getString("ConnectionProperties.failoverReadOnly"), "3.0.12", HA_CATEGORY, 2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  948 */   private BooleanConnectionProperty gatherPerformanceMetrics = new BooleanConnectionProperty(this, "gatherPerfMetrics", false, Messages.getString("ConnectionProperties.gatherPerfMetrics"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  954 */   private BooleanConnectionProperty generateSimpleParameterMetadata = new BooleanConnectionProperty(this, "generateSimpleParameterMetadata", false, Messages.getString("ConnectionProperties.generateSimpleParameterMetadata"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */   
/*      */   private boolean highAvailabilityAsBoolean = false;
/*      */   
/*  959 */   private BooleanConnectionProperty holdResultsOpenOverStatementClose = new BooleanConnectionProperty(this, "holdResultsOpenOverStatementClose", false, Messages.getString("ConnectionProperties.holdRSOpenOverStmtClose"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  965 */   private BooleanConnectionProperty includeInnodbStatusInDeadlockExceptions = new BooleanConnectionProperty(this, "includeInnodbStatusInDeadlockExceptions", false, "Include the output of \"SHOW ENGINE INNODB STATUS\" in exception messages when deadlock exceptions are detected?", "5.0.7", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  971 */   private BooleanConnectionProperty ignoreNonTxTables = new BooleanConnectionProperty(this, "ignoreNonTxTables", false, Messages.getString("ConnectionProperties.ignoreNonTxTables"), "3.0.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  977 */   private IntegerConnectionProperty initialTimeout = new IntegerConnectionProperty("initialTimeout", 2, 1, 2147483647, Messages.getString("ConnectionProperties.initialTimeout"), "1.1", HA_CATEGORY, 5);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  982 */   private BooleanConnectionProperty isInteractiveClient = new BooleanConnectionProperty(this, "interactiveClient", false, Messages.getString("ConnectionProperties.interactiveClient"), "3.1.0", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  988 */   private BooleanConnectionProperty jdbcCompliantTruncation = new BooleanConnectionProperty(this, "jdbcCompliantTruncation", true, Messages.getString("ConnectionProperties.jdbcCompliantTruncation"), "3.1.2", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  994 */   private boolean jdbcCompliantTruncationForReads = this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */ 
/*      */   
/*  997 */   protected MemorySizeConnectionProperty largeRowSizeThreshold = new MemorySizeConnectionProperty(this, "largeRowSizeThreshold", 2048, 0, 2147483647, Messages.getString("ConnectionProperties.largeRowSizeThreshold"), "5.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1002 */   private StringConnectionProperty loadBalanceStrategy = new StringConnectionProperty("loadBalanceStrategy", "random", new String[] { "random", "bestResponseTime" }, Messages.getString("ConnectionProperties.loadBalanceStrategy"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1009 */   private StringConnectionProperty localSocketAddress = new StringConnectionProperty("localSocketAddress", null, Messages.getString("ConnectionProperties.localSocketAddress"), "5.0.5", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/* 1013 */   private MemorySizeConnectionProperty locatorFetchBufferSize = new MemorySizeConnectionProperty(this, "locatorFetchBufferSize", 1048576, 0, 2147483647, Messages.getString("ConnectionProperties.locatorFetchBufferSize"), "3.2.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1021 */   private StringConnectionProperty loggerClassName = new StringConnectionProperty("logger", STANDARD_LOGGER_NAME, Messages.getString("ConnectionProperties.logger", new Object[] { Log.class.getName(), STANDARD_LOGGER_NAME }), "3.1.1", DEBUGING_PROFILING_CATEGORY, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1027 */   private BooleanConnectionProperty logSlowQueries = new BooleanConnectionProperty(this, "logSlowQueries", false, Messages.getString("ConnectionProperties.logSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1033 */   private BooleanConnectionProperty logXaCommands = new BooleanConnectionProperty(this, "logXaCommands", false, Messages.getString("ConnectionProperties.logXaCommands"), "5.0.5", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1039 */   private BooleanConnectionProperty maintainTimeStats = new BooleanConnectionProperty(this, "maintainTimeStats", true, Messages.getString("ConnectionProperties.maintainTimeStats"), "3.1.9", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean maintainTimeStatsAsBoolean = true;
/*      */ 
/*      */ 
/*      */   
/* 1047 */   private IntegerConnectionProperty maxQuerySizeToLog = new IntegerConnectionProperty("maxQuerySizeToLog", 2048, 0, 2147483647, Messages.getString("ConnectionProperties.maxQuerySizeToLog"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 4);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1055 */   private IntegerConnectionProperty maxReconnects = new IntegerConnectionProperty("maxReconnects", 3, 1, 2147483647, Messages.getString("ConnectionProperties.maxReconnects"), "1.1", HA_CATEGORY, 4);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1063 */   private IntegerConnectionProperty maxRows = new IntegerConnectionProperty("maxRows", -1, -1, 2147483647, Messages.getString("ConnectionProperties.maxRows"), Messages.getString("ConnectionProperties.allVersions"), MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1068 */   private int maxRowsAsInt = -1;
/*      */   
/* 1070 */   private IntegerConnectionProperty metadataCacheSize = new IntegerConnectionProperty("metadataCacheSize", 50, 1, 2147483647, Messages.getString("ConnectionProperties.metadataCacheSize"), "3.1.1", PERFORMANCE_CATEGORY, 5);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1078 */   private IntegerConnectionProperty netTimeoutForStreamingResults = new IntegerConnectionProperty("netTimeoutForStreamingResults", 600, 0, 2147483647, Messages.getString("ConnectionProperties.netTimeoutForStreamingResults"), "5.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1084 */   private BooleanConnectionProperty noAccessToProcedureBodies = new BooleanConnectionProperty(this, "noAccessToProcedureBodies", false, "When determining procedure parameter types for CallableStatements, and the connected user  can't access procedure bodies through \"SHOW CREATE PROCEDURE\" or select on mysql.proc  should the driver instead create basic metadata (all parameters reported as IN VARCHARs, but allowing registerOutParameter() to be called on them anyway) instead  of throwing an exception?", "5.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1094 */   private BooleanConnectionProperty noDatetimeStringSync = new BooleanConnectionProperty(this, "noDatetimeStringSync", false, Messages.getString("ConnectionProperties.noDatetimeStringSync"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1100 */   private BooleanConnectionProperty noTimezoneConversionForTimeType = new BooleanConnectionProperty(this, "noTimezoneConversionForTimeType", false, Messages.getString("ConnectionProperties.noTzConversionForTimeType"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1106 */   private BooleanConnectionProperty nullCatalogMeansCurrent = new BooleanConnectionProperty(this, "nullCatalogMeansCurrent", true, Messages.getString("ConnectionProperties.nullCatalogMeansCurrent"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1112 */   private BooleanConnectionProperty nullNamePatternMatchesAll = new BooleanConnectionProperty(this, "nullNamePatternMatchesAll", true, Messages.getString("ConnectionProperties.nullNamePatternMatchesAll"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1118 */   private IntegerConnectionProperty packetDebugBufferSize = new IntegerConnectionProperty("packetDebugBufferSize", 20, 0, 2147483647, Messages.getString("ConnectionProperties.packetDebugBufferSize"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 7);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1126 */   private BooleanConnectionProperty padCharsWithSpace = new BooleanConnectionProperty(this, "padCharsWithSpace", false, Messages.getString("ConnectionProperties.padCharsWithSpace"), "5.0.6", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1134 */   private BooleanConnectionProperty paranoid = new BooleanConnectionProperty(this, "paranoid", false, Messages.getString("ConnectionProperties.paranoid"), "3.0.1", SECURITY_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1140 */   private BooleanConnectionProperty pedantic = new BooleanConnectionProperty(this, "pedantic", false, Messages.getString("ConnectionProperties.pedantic"), "3.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/* 1144 */   private BooleanConnectionProperty pinGlobalTxToPhysicalConnection = new BooleanConnectionProperty(this, "pinGlobalTxToPhysicalConnection", false, Messages.getString("ConnectionProperties.pinGlobalTxToPhysicalConnection"), "5.0.1", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/* 1148 */   private BooleanConnectionProperty populateInsertRowWithDefaultValues = new BooleanConnectionProperty(this, "populateInsertRowWithDefaultValues", false, Messages.getString("ConnectionProperties.populateInsertRowWithDefaultValues"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1153 */   private IntegerConnectionProperty preparedStatementCacheSize = new IntegerConnectionProperty("prepStmtCacheSize", 25, 0, 2147483647, Messages.getString("ConnectionProperties.prepStmtCacheSize"), "3.0.10", PERFORMANCE_CATEGORY, 10);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1158 */   private IntegerConnectionProperty preparedStatementCacheSqlLimit = new IntegerConnectionProperty("prepStmtCacheSqlLimit", 256, 1, 2147483647, Messages.getString("ConnectionProperties.prepStmtCacheSqlLimit"), "3.0.10", PERFORMANCE_CATEGORY, 11);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1166 */   private BooleanConnectionProperty processEscapeCodesForPrepStmts = new BooleanConnectionProperty(this, "processEscapeCodesForPrepStmts", true, Messages.getString("ConnectionProperties.processEscapeCodesForPrepStmts"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1173 */   private StringConnectionProperty profilerEventHandler = new StringConnectionProperty("profilerEventHandler", "com.mysql.jdbc.profiler.LoggingProfilerEventHandler", Messages.getString("ConnectionProperties.profilerEventHandler"), "5.1.6", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1179 */   private StringConnectionProperty profileSql = new StringConnectionProperty("profileSql", null, Messages.getString("ConnectionProperties.profileSqlDeprecated"), "2.0.14", DEBUGING_PROFILING_CATEGORY, 3);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1185 */   private BooleanConnectionProperty profileSQL = new BooleanConnectionProperty(this, "profileSQL", false, Messages.getString("ConnectionProperties.profileSQL"), "3.1.0", DEBUGING_PROFILING_CATEGORY, 1);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean profileSQLAsBoolean = false;
/*      */ 
/*      */ 
/*      */   
/* 1193 */   private StringConnectionProperty propertiesTransform = new StringConnectionProperty("propertiesTransform", null, Messages.getString("ConnectionProperties.connectionPropertiesTransform"), "3.1.4", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1199 */   private IntegerConnectionProperty queriesBeforeRetryMaster = new IntegerConnectionProperty("queriesBeforeRetryMaster", 50, 1, 2147483647, Messages.getString("ConnectionProperties.queriesBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 7);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1207 */   private BooleanConnectionProperty reconnectAtTxEnd = new BooleanConnectionProperty(this, "reconnectAtTxEnd", false, Messages.getString("ConnectionProperties.reconnectAtTxEnd"), "3.0.10", HA_CATEGORY, 4);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean reconnectTxAtEndAsBoolean = false;
/*      */ 
/*      */   
/* 1214 */   private BooleanConnectionProperty relaxAutoCommit = new BooleanConnectionProperty(this, "relaxAutoCommit", false, Messages.getString("ConnectionProperties.relaxAutoCommit"), "2.0.13", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1220 */   private IntegerConnectionProperty reportMetricsIntervalMillis = new IntegerConnectionProperty("reportMetricsIntervalMillis", 30000, 0, 2147483647, Messages.getString("ConnectionProperties.reportMetricsIntervalMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 3);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1228 */   private BooleanConnectionProperty requireSSL = new BooleanConnectionProperty(this, "requireSSL", false, Messages.getString("ConnectionProperties.requireSSL"), "3.1.0", SECURITY_CATEGORY, 3);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1233 */   private StringConnectionProperty resourceId = new StringConnectionProperty("resourceId", null, Messages.getString("ConnectionProperties.resourceId"), "5.0.1", HA_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1240 */   private IntegerConnectionProperty resultSetSizeThreshold = new IntegerConnectionProperty("resultSetSizeThreshold", 100, Messages.getString("ConnectionProperties.resultSetSizeThreshold"), "5.0.5", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */   
/* 1243 */   private BooleanConnectionProperty retainStatementAfterResultSetClose = new BooleanConnectionProperty(this, "retainStatementAfterResultSetClose", false, Messages.getString("ConnectionProperties.retainStatementAfterResultSetClose"), "3.1.11", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1249 */   private BooleanConnectionProperty rewriteBatchedStatements = new BooleanConnectionProperty(this, "rewriteBatchedStatements", false, Messages.getString("ConnectionProperties.rewriteBatchedStatements"), "3.1.13", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1255 */   private BooleanConnectionProperty rollbackOnPooledClose = new BooleanConnectionProperty(this, "rollbackOnPooledClose", true, Messages.getString("ConnectionProperties.rollbackOnPooledClose"), "3.0.15", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1261 */   private BooleanConnectionProperty roundRobinLoadBalance = new BooleanConnectionProperty(this, "roundRobinLoadBalance", false, Messages.getString("ConnectionProperties.roundRobinLoadBalance"), "3.1.2", HA_CATEGORY, 5);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1267 */   private BooleanConnectionProperty runningCTS13 = new BooleanConnectionProperty(this, "runningCTS13", false, Messages.getString("ConnectionProperties.runningCTS13"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1273 */   private IntegerConnectionProperty secondsBeforeRetryMaster = new IntegerConnectionProperty("secondsBeforeRetryMaster", 30, 1, 2147483647, Messages.getString("ConnectionProperties.secondsBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 8);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1281 */   private IntegerConnectionProperty selfDestructOnPingSecondsLifetime = new IntegerConnectionProperty("selfDestructOnPingSecondsLifetime", 0, 0, 2147483647, Messages.getString("ConnectionProperties.selfDestructOnPingSecondsLifetime"), "5.1.6", HA_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1289 */   private IntegerConnectionProperty selfDestructOnPingMaxOperations = new IntegerConnectionProperty("selfDestructOnPingMaxOperations", 0, 0, 2147483647, Messages.getString("ConnectionProperties.selfDestructOnPingMaxOperations"), "5.1.6", HA_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1297 */   private StringConnectionProperty serverTimezone = new StringConnectionProperty("serverTimezone", null, Messages.getString("ConnectionProperties.serverTimezone"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1303 */   private StringConnectionProperty sessionVariables = new StringConnectionProperty("sessionVariables", null, Messages.getString("ConnectionProperties.sessionVariables"), "3.1.8", MISC_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1308 */   private IntegerConnectionProperty slowQueryThresholdMillis = new IntegerConnectionProperty("slowQueryThresholdMillis", 2000, 0, 2147483647, Messages.getString("ConnectionProperties.slowQueryThresholdMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 9);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1316 */   private LongConnectionProperty slowQueryThresholdNanos = new LongConnectionProperty("slowQueryThresholdNanos", 0L, Messages.getString("ConnectionProperties.slowQueryThresholdNanos"), "5.0.7", DEBUGING_PROFILING_CATEGORY, 10);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1324 */   private StringConnectionProperty socketFactoryClassName = new StringConnectionProperty("socketFactory", StandardSocketFactory.class.getName(), Messages.getString("ConnectionProperties.socketFactory"), "3.0.3", CONNECTION_AND_AUTH_CATEGORY, 4);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1330 */   private IntegerConnectionProperty socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 10);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1338 */   private StringConnectionProperty statementInterceptors = new StringConnectionProperty("statementInterceptors", null, Messages.getString("ConnectionProperties.statementInterceptors"), "5.1.1", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */   
/* 1341 */   private BooleanConnectionProperty strictFloatingPoint = new BooleanConnectionProperty(this, "strictFloatingPoint", false, Messages.getString("ConnectionProperties.strictFloatingPoint"), "3.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1346 */   private BooleanConnectionProperty strictUpdates = new BooleanConnectionProperty(this, "strictUpdates", true, Messages.getString("ConnectionProperties.strictUpdates"), "3.0.4", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1352 */   private BooleanConnectionProperty overrideSupportsIntegrityEnhancementFacility = new BooleanConnectionProperty(this, "overrideSupportsIntegrityEnhancementFacility", false, Messages.getString("ConnectionProperties.overrideSupportsIEF"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1358 */   private BooleanConnectionProperty tcpNoDelay = new BooleanConnectionProperty(this, "tcpNoDelay", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpNoDelay"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1364 */   private BooleanConnectionProperty tcpKeepAlive = new BooleanConnectionProperty(this, "tcpKeepAlive", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpKeepAlive"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1370 */   private IntegerConnectionProperty tcpRcvBuf = new IntegerConnectionProperty("tcpRcvBuf", Integer.parseInt("0"), 0, 2147483647, Messages.getString("ConnectionProperties.tcpSoRcvBuf"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1377 */   private IntegerConnectionProperty tcpSndBuf = new IntegerConnectionProperty("tcpSndBuf", Integer.parseInt("0"), 0, 2147483647, Messages.getString("ConnectionProperties.tcpSoSndBuf"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1384 */   private IntegerConnectionProperty tcpTrafficClass = new IntegerConnectionProperty("tcpTrafficClass", Integer.parseInt("0"), 0, 255, Messages.getString("ConnectionProperties.tcpTrafficClass"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1391 */   private BooleanConnectionProperty tinyInt1isBit = new BooleanConnectionProperty(this, "tinyInt1isBit", true, Messages.getString("ConnectionProperties.tinyInt1isBit"), "3.0.16", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1397 */   private BooleanConnectionProperty traceProtocol = new BooleanConnectionProperty(this, "traceProtocol", false, Messages.getString("ConnectionProperties.traceProtocol"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1402 */   private BooleanConnectionProperty treatUtilDateAsTimestamp = new BooleanConnectionProperty(this, "treatUtilDateAsTimestamp", true, Messages.getString("ConnectionProperties.treatUtilDateAsTimestamp"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1407 */   private BooleanConnectionProperty transformedBitIsBoolean = new BooleanConnectionProperty(this, "transformedBitIsBoolean", false, Messages.getString("ConnectionProperties.transformedBitIsBoolean"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1413 */   private BooleanConnectionProperty useBlobToStoreUTF8OutsideBMP = new BooleanConnectionProperty(this, "useBlobToStoreUTF8OutsideBMP", false, Messages.getString("ConnectionProperties.useBlobToStoreUTF8OutsideBMP"), "5.1.3", MISC_CATEGORY, 128);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1419 */   private StringConnectionProperty utf8OutsideBmpExcludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpExcludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpExcludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1425 */   private StringConnectionProperty utf8OutsideBmpIncludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpIncludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpIncludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1431 */   private BooleanConnectionProperty useCompression = new BooleanConnectionProperty(this, "useCompression", false, Messages.getString("ConnectionProperties.useCompression"), "3.0.17", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1437 */   private StringConnectionProperty useConfigs = new StringConnectionProperty("useConfigs", null, Messages.getString("ConnectionProperties.useConfigs"), "3.1.5", CONNECTION_AND_AUTH_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1443 */   private BooleanConnectionProperty useCursorFetch = new BooleanConnectionProperty(this, "useCursorFetch", false, Messages.getString("ConnectionProperties.useCursorFetch"), "5.0.0", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1449 */   private BooleanConnectionProperty useDynamicCharsetInfo = new BooleanConnectionProperty(this, "useDynamicCharsetInfo", true, Messages.getString("ConnectionProperties.useDynamicCharsetInfo"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1455 */   private BooleanConnectionProperty useDirectRowUnpack = new BooleanConnectionProperty(this, "useDirectRowUnpack", true, "Use newer result set row unpacking code that skips a copy from network buffers  to a MySQL packet instance and instead reads directly into the result set row data buffers.", "5.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1461 */   private BooleanConnectionProperty useFastIntParsing = new BooleanConnectionProperty(this, "useFastIntParsing", true, Messages.getString("ConnectionProperties.useFastIntParsing"), "3.1.4", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1467 */   private BooleanConnectionProperty useFastDateParsing = new BooleanConnectionProperty(this, "useFastDateParsing", true, Messages.getString("ConnectionProperties.useFastDateParsing"), "5.0.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1473 */   private BooleanConnectionProperty useHostsInPrivileges = new BooleanConnectionProperty(this, "useHostsInPrivileges", true, Messages.getString("ConnectionProperties.useHostsInPrivileges"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1478 */   private BooleanConnectionProperty useInformationSchema = new BooleanConnectionProperty(this, "useInformationSchema", false, Messages.getString("ConnectionProperties.useInformationSchema"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1483 */   private BooleanConnectionProperty useJDBCCompliantTimezoneShift = new BooleanConnectionProperty(this, "useJDBCCompliantTimezoneShift", false, Messages.getString("ConnectionProperties.useJDBCCompliantTimezoneShift"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1490 */   private BooleanConnectionProperty useLocalSessionState = new BooleanConnectionProperty(this, "useLocalSessionState", false, Messages.getString("ConnectionProperties.useLocalSessionState"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1496 */   private BooleanConnectionProperty useLegacyDatetimeCode = new BooleanConnectionProperty(this, "useLegacyDatetimeCode", true, Messages.getString("ConnectionProperties.useLegacyDatetimeCode"), "5.1.6", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1502 */   private BooleanConnectionProperty useNanosForElapsedTime = new BooleanConnectionProperty(this, "useNanosForElapsedTime", false, Messages.getString("ConnectionProperties.useNanosForElapsedTime"), "5.0.7", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1509 */   private BooleanConnectionProperty useOldAliasMetadataBehavior = new BooleanConnectionProperty(this, "useOldAliasMetadataBehavior", false, Messages.getString("ConnectionProperties.useOldAliasMetadataBehavior"), "5.0.4", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1517 */   private BooleanConnectionProperty useOldUTF8Behavior = new BooleanConnectionProperty(this, "useOldUTF8Behavior", false, Messages.getString("ConnectionProperties.useOldUtf8Behavior"), "3.1.6", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean useOldUTF8BehaviorAsBoolean = false;
/*      */ 
/*      */ 
/*      */   
/* 1525 */   private BooleanConnectionProperty useOnlyServerErrorMessages = new BooleanConnectionProperty(this, "useOnlyServerErrorMessages", true, Messages.getString("ConnectionProperties.useOnlyServerErrorMessages"), "3.0.15", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1531 */   private BooleanConnectionProperty useReadAheadInput = new BooleanConnectionProperty(this, "useReadAheadInput", true, Messages.getString("ConnectionProperties.useReadAheadInput"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1537 */   private BooleanConnectionProperty useSqlStateCodes = new BooleanConnectionProperty(this, "useSqlStateCodes", true, Messages.getString("ConnectionProperties.useSqlStateCodes"), "3.1.3", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1543 */   private BooleanConnectionProperty useSSL = new BooleanConnectionProperty(this, "useSSL", false, Messages.getString("ConnectionProperties.useSSL"), "3.0.2", SECURITY_CATEGORY, 2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1549 */   private BooleanConnectionProperty useSSPSCompatibleTimezoneShift = new BooleanConnectionProperty(this, "useSSPSCompatibleTimezoneShift", false, Messages.getString("ConnectionProperties.useSSPSCompatibleTimezoneShift"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1555 */   private BooleanConnectionProperty useStreamLengthsInPrepStmts = new BooleanConnectionProperty(this, "useStreamLengthsInPrepStmts", true, Messages.getString("ConnectionProperties.useStreamLengthsInPrepStmts"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1561 */   private BooleanConnectionProperty useTimezone = new BooleanConnectionProperty(this, "useTimezone", false, Messages.getString("ConnectionProperties.useTimezone"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1567 */   private BooleanConnectionProperty useUltraDevWorkAround = new BooleanConnectionProperty(this, "ultraDevHack", false, Messages.getString("ConnectionProperties.ultraDevHack"), "2.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1573 */   private BooleanConnectionProperty useUnbufferedInput = new BooleanConnectionProperty(this, "useUnbufferedInput", true, Messages.getString("ConnectionProperties.useUnbufferedInput"), "3.0.11", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1578 */   private BooleanConnectionProperty useUnicode = new BooleanConnectionProperty(this, "useUnicode", true, Messages.getString("ConnectionProperties.useUnicode"), "1.1g", MISC_CATEGORY, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean useUnicodeAsBoolean = true;
/*      */ 
/*      */ 
/*      */   
/* 1587 */   private BooleanConnectionProperty useUsageAdvisor = new BooleanConnectionProperty(this, "useUsageAdvisor", false, Messages.getString("ConnectionProperties.useUsageAdvisor"), "3.1.1", DEBUGING_PROFILING_CATEGORY, 10);
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean useUsageAdvisorAsBoolean = false;
/*      */ 
/*      */ 
/*      */   
/* 1595 */   private BooleanConnectionProperty yearIsDateType = new BooleanConnectionProperty(this, "yearIsDateType", true, Messages.getString("ConnectionProperties.yearIsDateType"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1601 */   private StringConnectionProperty zeroDateTimeBehavior = new StringConnectionProperty("zeroDateTimeBehavior", "exception", new String[] { "exception", "round", "convertToNull" }, Messages.getString("ConnectionProperties.zeroDateTimeBehavior", new Object[] { "exception", "round", "convertToNull" }), "3.1.4", MISC_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1611 */   private BooleanConnectionProperty useJvmCharsetConverters = new BooleanConnectionProperty(this, "useJvmCharsetConverters", false, Messages.getString("ConnectionProperties.useJvmCharsetConverters"), "5.0.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*      */   
/* 1614 */   private BooleanConnectionProperty useGmtMillisForDatetimes = new BooleanConnectionProperty(this, "useGmtMillisForDatetimes", false, Messages.getString("ConnectionProperties.useGmtMillisForDatetimes"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */   
/* 1616 */   private BooleanConnectionProperty dumpMetadataOnColumnNotFound = new BooleanConnectionProperty(this, "dumpMetadataOnColumnNotFound", false, Messages.getString("ConnectionProperties.dumpMetadataOnColumnNotFound"), "3.1.13", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*      */ 
/*      */   
/* 1620 */   private StringConnectionProperty clientCertificateKeyStoreUrl = new StringConnectionProperty("clientCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 5);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1625 */   private StringConnectionProperty trustCertificateKeyStoreUrl = new StringConnectionProperty("trustCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 8);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1630 */   private StringConnectionProperty clientCertificateKeyStoreType = new StringConnectionProperty("clientCertificateKeyStoreType", null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 6);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1635 */   private StringConnectionProperty clientCertificateKeyStorePassword = new StringConnectionProperty("clientCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.clientCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 7);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1640 */   private StringConnectionProperty trustCertificateKeyStoreType = new StringConnectionProperty("trustCertificateKeyStoreType", null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 9);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1645 */   private StringConnectionProperty trustCertificateKeyStorePassword = new StringConnectionProperty("trustCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.trustCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 10);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1650 */   private BooleanConnectionProperty verifyServerCertificate = new BooleanConnectionProperty(this, "verifyServerCertificate", true, Messages.getString("ConnectionProperties.verifyServerCertificate"), "5.1.6", SECURITY_CATEGORY, 4);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve) throws SQLException {
/* 1658 */     initializeProperties(info);
/*      */     
/* 1660 */     int numProperties = PROPERTY_LIST.size();
/*      */     
/* 1662 */     int listSize = numProperties + slotsToReserve;
/*      */     
/* 1664 */     DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
/*      */     
/* 1666 */     for (int i = slotsToReserve; i < listSize; i++) {
/* 1667 */       Field propertyField = PROPERTY_LIST.get(i - slotsToReserve);
/*      */ 
/*      */       
/*      */       try {
/* 1671 */         ConnectionProperty propToExpose = (ConnectionProperty)propertyField.get(this);
/*      */ 
/*      */         
/* 1674 */         if (info != null) {
/* 1675 */           propToExpose.initializeFrom(info);
/*      */         }
/*      */ 
/*      */         
/* 1679 */         driverProperties[i] = propToExpose.getAsDriverPropertyInfo();
/* 1680 */       } catch (IllegalAccessException iae) {
/* 1681 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.InternalPropertiesFailure"), "S1000");
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1686 */     return driverProperties;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Properties exposeAsProperties(Properties info) throws SQLException {
/* 1691 */     if (info == null) {
/* 1692 */       info = new Properties();
/*      */     }
/*      */     
/* 1695 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 1697 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 1698 */       Field propertyField = PROPERTY_LIST.get(i);
/*      */ 
/*      */       
/*      */       try {
/* 1702 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/*      */         
/* 1705 */         Object propValue = propToGet.getValueAsObject();
/*      */         
/* 1707 */         if (propValue != null) {
/* 1708 */           info.setProperty(propToGet.getPropertyName(), propValue.toString());
/*      */         }
/*      */       }
/* 1711 */       catch (IllegalAccessException iae) {
/* 1712 */         throw SQLError.createSQLException("Internal properties failure", "S1000");
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1717 */     return info;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String exposeAsXml() throws SQLException {
/* 1724 */     StringBuffer xmlBuf = new StringBuffer();
/* 1725 */     xmlBuf.append("<ConnectionProperties>");
/*      */     
/* 1727 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 1729 */     int numCategories = PROPERTY_CATEGORIES.length;
/*      */     
/* 1731 */     Map propertyListByCategory = new HashMap();
/*      */     
/* 1733 */     for (int i = 0; i < numCategories; i++) {
/* 1734 */       propertyListByCategory.put(PROPERTY_CATEGORIES[i], new Map[] { new TreeMap(), new TreeMap() });
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1744 */     StringConnectionProperty userProp = new StringConnectionProperty("user", null, Messages.getString("ConnectionProperties.Username"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483647);
/*      */ 
/*      */ 
/*      */     
/* 1748 */     StringConnectionProperty passwordProp = new StringConnectionProperty("password", null, Messages.getString("ConnectionProperties.Password"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483646);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1753 */     Map[] connectionSortMaps = (Map[])propertyListByCategory.get(CONNECTION_AND_AUTH_CATEGORY);
/*      */     
/* 1755 */     TreeMap userMap = new TreeMap();
/* 1756 */     userMap.put(userProp.getPropertyName(), userProp);
/*      */     
/* 1758 */     connectionSortMaps[0].put(new Integer(userProp.getOrder()), userMap);
/*      */     
/* 1760 */     TreeMap passwordMap = new TreeMap();
/* 1761 */     passwordMap.put(passwordProp.getPropertyName(), passwordProp);
/*      */     
/* 1763 */     connectionSortMaps[0].put(new Integer(passwordProp.getOrder()), passwordMap);
/*      */ 
/*      */     
/*      */     try {
/* 1767 */       for (int k = 0; k < numPropertiesToSet; k++) {
/* 1768 */         Field propertyField = PROPERTY_LIST.get(k);
/*      */         
/* 1770 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */         
/* 1772 */         Map[] sortMaps = (Map[])propertyListByCategory.get(propToGet.getCategoryName());
/*      */         
/* 1774 */         int orderInCategory = propToGet.getOrder();
/*      */         
/* 1776 */         if (orderInCategory == Integer.MIN_VALUE) {
/* 1777 */           sortMaps[1].put(propToGet.getPropertyName(), propToGet);
/*      */         } else {
/* 1779 */           Integer order = new Integer(orderInCategory);
/*      */           
/* 1781 */           Map orderMap = sortMaps[0].get(order);
/*      */           
/* 1783 */           if (orderMap == null) {
/* 1784 */             orderMap = new TreeMap();
/* 1785 */             sortMaps[0].put(order, orderMap);
/*      */           } 
/*      */           
/* 1788 */           orderMap.put(propToGet.getPropertyName(), propToGet);
/*      */         } 
/*      */       } 
/*      */       
/* 1792 */       for (int j = 0; j < numCategories; j++) {
/* 1793 */         Map[] sortMaps = (Map[])propertyListByCategory.get(PROPERTY_CATEGORIES[j]);
/*      */         
/* 1795 */         Iterator orderedIter = sortMaps[0].values().iterator();
/* 1796 */         Iterator alphaIter = sortMaps[1].values().iterator();
/*      */         
/* 1798 */         xmlBuf.append("\n <PropertyCategory name=\"");
/* 1799 */         xmlBuf.append(PROPERTY_CATEGORIES[j]);
/* 1800 */         xmlBuf.append("\">");
/*      */         
/* 1802 */         while (orderedIter.hasNext()) {
/* 1803 */           Iterator orderedAlphaIter = ((Map)orderedIter.next()).values().iterator();
/*      */           
/* 1805 */           while (orderedAlphaIter.hasNext()) {
/* 1806 */             ConnectionProperty propToGet = orderedAlphaIter.next();
/*      */ 
/*      */             
/* 1809 */             xmlBuf.append("\n  <Property name=\"");
/* 1810 */             xmlBuf.append(propToGet.getPropertyName());
/* 1811 */             xmlBuf.append("\" required=\"");
/* 1812 */             xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */             
/* 1814 */             xmlBuf.append("\" default=\"");
/*      */             
/* 1816 */             if (propToGet.getDefaultValue() != null) {
/* 1817 */               xmlBuf.append(propToGet.getDefaultValue());
/*      */             }
/*      */             
/* 1820 */             xmlBuf.append("\" sortOrder=\"");
/* 1821 */             xmlBuf.append(propToGet.getOrder());
/* 1822 */             xmlBuf.append("\" since=\"");
/* 1823 */             xmlBuf.append(propToGet.sinceVersion);
/* 1824 */             xmlBuf.append("\">\n");
/* 1825 */             xmlBuf.append("    ");
/* 1826 */             xmlBuf.append(propToGet.description);
/* 1827 */             xmlBuf.append("\n  </Property>");
/*      */           } 
/*      */         } 
/*      */         
/* 1831 */         while (alphaIter.hasNext()) {
/* 1832 */           ConnectionProperty propToGet = alphaIter.next();
/*      */ 
/*      */           
/* 1835 */           xmlBuf.append("\n  <Property name=\"");
/* 1836 */           xmlBuf.append(propToGet.getPropertyName());
/* 1837 */           xmlBuf.append("\" required=\"");
/* 1838 */           xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */           
/* 1840 */           xmlBuf.append("\" default=\"");
/*      */           
/* 1842 */           if (propToGet.getDefaultValue() != null) {
/* 1843 */             xmlBuf.append(propToGet.getDefaultValue());
/*      */           }
/*      */           
/* 1846 */           xmlBuf.append("\" sortOrder=\"alpha\" since=\"");
/* 1847 */           xmlBuf.append(propToGet.sinceVersion);
/* 1848 */           xmlBuf.append("\">\n");
/* 1849 */           xmlBuf.append("    ");
/* 1850 */           xmlBuf.append(propToGet.description);
/* 1851 */           xmlBuf.append("\n  </Property>");
/*      */         } 
/*      */         
/* 1854 */         xmlBuf.append("\n </PropertyCategory>");
/*      */       } 
/* 1856 */     } catch (IllegalAccessException iae) {
/* 1857 */       throw SQLError.createSQLException("Internal properties failure", "S1000");
/*      */     } 
/*      */ 
/*      */     
/* 1861 */     xmlBuf.append("\n</ConnectionProperties>");
/*      */     
/* 1863 */     return xmlBuf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAllowLoadLocalInfile() {
/* 1870 */     return this.allowLoadLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAllowMultiQueries() {
/* 1877 */     return this.allowMultiQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAllowNanAndInf() {
/* 1884 */     return this.allowNanAndInf.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAllowUrlInLocalInfile() {
/* 1891 */     return this.allowUrlInLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAlwaysSendSetIsolation() {
/* 1898 */     return this.alwaysSendSetIsolation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoDeserialize() {
/* 1905 */     return this.autoDeserialize.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoGenerateTestcaseScript() {
/* 1912 */     return this.autoGenerateTestcaseScriptAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoReconnectForPools() {
/* 1919 */     return this.autoReconnectForPoolsAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getBlobSendChunkSize() {
/* 1926 */     return this.blobSendChunkSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCacheCallableStatements() {
/* 1933 */     return this.cacheCallableStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCachePreparedStatements() {
/* 1940 */     return ((Boolean)this.cachePreparedStatements.getValueAsObject()).booleanValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCacheResultSetMetadata() {
/* 1948 */     return this.cacheResultSetMetaDataAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCacheServerConfiguration() {
/* 1955 */     return this.cacheServerConfiguration.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCallableStatementCacheSize() {
/* 1962 */     return this.callableStatementCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCapitalizeTypeNames() {
/* 1969 */     return this.capitalizeTypeNames.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCharacterSetResults() {
/* 1976 */     return this.characterSetResults.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getClobberStreamingResults() {
/* 1983 */     return this.clobberStreamingResults.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClobCharacterEncoding() {
/* 1990 */     return this.clobCharacterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getConnectionCollation() {
/* 1997 */     return this.connectionCollation.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getConnectTimeout() {
/* 2004 */     return this.connectTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getContinueBatchOnError() {
/* 2011 */     return this.continueBatchOnError.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCreateDatabaseIfNotExist() {
/* 2018 */     return this.createDatabaseIfNotExist.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDefaultFetchSize() {
/* 2025 */     return this.defaultFetchSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getDontTrackOpenResources() {
/* 2032 */     return this.dontTrackOpenResources.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getDumpQueriesOnException() {
/* 2039 */     return this.dumpQueriesOnException.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getDynamicCalendars() {
/* 2046 */     return this.dynamicCalendars.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getElideSetAutoCommits() {
/* 2053 */     return this.elideSetAutoCommits.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getEmptyStringsConvertToZero() {
/* 2060 */     return this.emptyStringsConvertToZero.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getEmulateLocators() {
/* 2067 */     return this.emulateLocators.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getEmulateUnsupportedPstmts() {
/* 2074 */     return this.emulateUnsupportedPstmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getEnablePacketDebug() {
/* 2081 */     return this.enablePacketDebug.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getEncoding() {
/* 2088 */     return this.characterEncodingAsString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getExplainSlowQueries() {
/* 2095 */     return this.explainSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getFailOverReadOnly() {
/* 2102 */     return this.failOverReadOnly.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getGatherPerformanceMetrics() {
/* 2109 */     return this.gatherPerformanceMetrics.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean getHighAvailability() {
/* 2118 */     return this.highAvailabilityAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getHoldResultsOpenOverStatementClose() {
/* 2125 */     return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getIgnoreNonTxTables() {
/* 2132 */     return this.ignoreNonTxTables.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInitialTimeout() {
/* 2139 */     return this.initialTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getInteractiveClient() {
/* 2146 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getIsInteractiveClient() {
/* 2153 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getJdbcCompliantTruncation() {
/* 2160 */     return this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLocatorFetchBufferSize() {
/* 2167 */     return this.locatorFetchBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLogger() {
/* 2174 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLoggerClassName() {
/* 2181 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getLogSlowQueries() {
/* 2188 */     return this.logSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getMaintainTimeStats() {
/* 2195 */     return this.maintainTimeStatsAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxQuerySizeToLog() {
/* 2202 */     return this.maxQuerySizeToLog.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxReconnects() {
/* 2209 */     return this.maxReconnects.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxRows() {
/* 2216 */     return this.maxRowsAsInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMetadataCacheSize() {
/* 2223 */     return this.metadataCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getNoDatetimeStringSync() {
/* 2230 */     return this.noDatetimeStringSync.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getNullCatalogMeansCurrent() {
/* 2237 */     return this.nullCatalogMeansCurrent.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getNullNamePatternMatchesAll() {
/* 2244 */     return this.nullNamePatternMatchesAll.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPacketDebugBufferSize() {
/* 2251 */     return this.packetDebugBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getParanoid() {
/* 2258 */     return this.paranoid.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getPedantic() {
/* 2265 */     return this.pedantic.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPreparedStatementCacheSize() {
/* 2272 */     return ((Integer)this.preparedStatementCacheSize.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPreparedStatementCacheSqlLimit() {
/* 2280 */     return ((Integer)this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getProfileSql() {
/* 2288 */     return this.profileSQLAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getProfileSQL() {
/* 2295 */     return this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPropertiesTransform() {
/* 2302 */     return this.propertiesTransform.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getQueriesBeforeRetryMaster() {
/* 2309 */     return this.queriesBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getReconnectAtTxEnd() {
/* 2316 */     return this.reconnectTxAtEndAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRelaxAutoCommit() {
/* 2323 */     return this.relaxAutoCommit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getReportMetricsIntervalMillis() {
/* 2330 */     return this.reportMetricsIntervalMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRequireSSL() {
/* 2337 */     return this.requireSSL.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   protected boolean getRetainStatementAfterResultSetClose() {
/* 2341 */     return this.retainStatementAfterResultSetClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRollbackOnPooledClose() {
/* 2348 */     return this.rollbackOnPooledClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRoundRobinLoadBalance() {
/* 2355 */     return this.roundRobinLoadBalance.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRunningCTS13() {
/* 2362 */     return this.runningCTS13.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSecondsBeforeRetryMaster() {
/* 2369 */     return this.secondsBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getServerTimezone() {
/* 2376 */     return this.serverTimezone.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSessionVariables() {
/* 2383 */     return this.sessionVariables.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSlowQueryThresholdMillis() {
/* 2390 */     return this.slowQueryThresholdMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSocketFactoryClassName() {
/* 2397 */     return this.socketFactoryClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSocketTimeout() {
/* 2404 */     return this.socketTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getStrictFloatingPoint() {
/* 2411 */     return this.strictFloatingPoint.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getStrictUpdates() {
/* 2418 */     return this.strictUpdates.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getTinyInt1isBit() {
/* 2425 */     return this.tinyInt1isBit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getTraceProtocol() {
/* 2432 */     return this.traceProtocol.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getTransformedBitIsBoolean() {
/* 2439 */     return this.transformedBitIsBoolean.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseCompression() {
/* 2446 */     return this.useCompression.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseFastIntParsing() {
/* 2453 */     return this.useFastIntParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseHostsInPrivileges() {
/* 2460 */     return this.useHostsInPrivileges.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseInformationSchema() {
/* 2467 */     return this.useInformationSchema.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseLocalSessionState() {
/* 2474 */     return this.useLocalSessionState.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseOldUTF8Behavior() {
/* 2481 */     return this.useOldUTF8BehaviorAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseOnlyServerErrorMessages() {
/* 2488 */     return this.useOnlyServerErrorMessages.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseReadAheadInput() {
/* 2495 */     return this.useReadAheadInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseServerPreparedStmts() {
/* 2502 */     return this.detectServerPreparedStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseSqlStateCodes() {
/* 2509 */     return this.useSqlStateCodes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseSSL() {
/* 2516 */     return this.useSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseStreamLengthsInPrepStmts() {
/* 2523 */     return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseTimezone() {
/* 2530 */     return this.useTimezone.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseUltraDevWorkAround() {
/* 2537 */     return this.useUltraDevWorkAround.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseUnbufferedInput() {
/* 2544 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseUnicode() {
/* 2551 */     return this.useUnicodeAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseUsageAdvisor() {
/* 2558 */     return this.useUsageAdvisorAsBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getYearIsDateType() {
/* 2565 */     return this.yearIsDateType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getZeroDateTimeBehavior() {
/* 2572 */     return this.zeroDateTimeBehavior.getValueAsString();
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
/*      */   protected void initializeFromRef(Reference ref) throws SQLException {
/* 2586 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 2588 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 2589 */       Field propertyField = PROPERTY_LIST.get(i);
/*      */ 
/*      */       
/*      */       try {
/* 2593 */         ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/*      */         
/* 2596 */         if (ref != null) {
/* 2597 */           propToSet.initializeFrom(ref);
/*      */         }
/* 2599 */       } catch (IllegalAccessException iae) {
/* 2600 */         throw SQLError.createSQLException("Internal properties failure", "S1000");
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2605 */     postInitialization();
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
/*      */   protected void initializeProperties(Properties info) throws SQLException {
/* 2618 */     if (info != null) {
/*      */       
/* 2620 */       String profileSqlLc = info.getProperty("profileSql");
/*      */       
/* 2622 */       if (profileSqlLc != null) {
/* 2623 */         info.put("profileSQL", profileSqlLc);
/*      */       }
/*      */       
/* 2626 */       Properties infoCopy = (Properties)info.clone();
/*      */       
/* 2628 */       infoCopy.remove("HOST");
/* 2629 */       infoCopy.remove("user");
/* 2630 */       infoCopy.remove("password");
/* 2631 */       infoCopy.remove("DBNAME");
/* 2632 */       infoCopy.remove("PORT");
/* 2633 */       infoCopy.remove("profileSql");
/*      */       
/* 2635 */       int numPropertiesToSet = PROPERTY_LIST.size();
/*      */       
/* 2637 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 2638 */         Field propertyField = PROPERTY_LIST.get(i);
/*      */ 
/*      */         
/*      */         try {
/* 2642 */           ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/*      */           
/* 2645 */           propToSet.initializeFrom(infoCopy);
/* 2646 */         } catch (IllegalAccessException iae) {
/* 2647 */           throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unableToInitDriverProperties") + iae.toString(), "S1000");
/*      */         } 
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2670 */       postInitialization();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void postInitialization() throws SQLException {
/* 2677 */     if (this.profileSql.getValueAsObject() != null) {
/* 2678 */       this.profileSQL.initializeFrom(this.profileSql.getValueAsObject().toString());
/*      */     }
/*      */ 
/*      */     
/* 2682 */     this.reconnectTxAtEndAsBoolean = ((Boolean)this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
/*      */ 
/*      */ 
/*      */     
/* 2686 */     if (getMaxRows() == 0)
/*      */     {
/*      */       
/* 2689 */       this.maxRows.setValueAsObject(Constants.integerValueOf(-1));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2695 */     String testEncoding = getEncoding();
/*      */     
/* 2697 */     if (testEncoding != null) {
/*      */       
/*      */       try {
/*      */         
/* 2701 */         String testString = "abc";
/* 2702 */         testString.getBytes(testEncoding);
/* 2703 */       } catch (UnsupportedEncodingException UE) {
/* 2704 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unsupportedCharacterEncoding", new Object[] { testEncoding }), "0S100");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2713 */     if (((Boolean)this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
/*      */       
/*      */       try {
/* 2716 */         Class.forName("java.util.LinkedHashMap");
/* 2717 */       } catch (ClassNotFoundException cnfe) {
/* 2718 */         this.cacheResultSetMetadata.setValue(false);
/*      */       } 
/*      */     }
/*      */     
/* 2722 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */     
/* 2724 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/* 2725 */     this.characterEncodingAsString = (String)this.characterEncoding.getValueAsObject();
/*      */     
/* 2727 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/* 2728 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */     
/* 2730 */     this.maxRowsAsInt = ((Integer)this.maxRows.getValueAsObject()).intValue();
/*      */     
/* 2732 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/* 2733 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */     
/* 2735 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */     
/* 2737 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */     
/* 2739 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */     
/* 2741 */     this.jdbcCompliantTruncationForReads = getJdbcCompliantTruncation();
/*      */     
/* 2743 */     if (getUseCursorFetch())
/*      */     {
/*      */       
/* 2746 */       setDetectServerPreparedStmts(true);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowLoadLocalInfile(boolean property) {
/* 2754 */     this.allowLoadLocalInfile.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowMultiQueries(boolean property) {
/* 2761 */     this.allowMultiQueries.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowNanAndInf(boolean flag) {
/* 2768 */     this.allowNanAndInf.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowUrlInLocalInfile(boolean flag) {
/* 2775 */     this.allowUrlInLocalInfile.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAlwaysSendSetIsolation(boolean flag) {
/* 2782 */     this.alwaysSendSetIsolation.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoDeserialize(boolean flag) {
/* 2789 */     this.autoDeserialize.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoGenerateTestcaseScript(boolean flag) {
/* 2796 */     this.autoGenerateTestcaseScript.setValue(flag);
/* 2797 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoReconnect(boolean flag) {
/* 2805 */     this.autoReconnect.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoReconnectForConnectionPools(boolean property) {
/* 2812 */     this.autoReconnectForPools.setValue(property);
/* 2813 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoReconnectForPools(boolean flag) {
/* 2821 */     this.autoReconnectForPools.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBlobSendChunkSize(String value) throws SQLException {
/* 2828 */     this.blobSendChunkSize.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheCallableStatements(boolean flag) {
/* 2835 */     this.cacheCallableStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCachePreparedStatements(boolean flag) {
/* 2842 */     this.cachePreparedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheResultSetMetadata(boolean property) {
/* 2849 */     this.cacheResultSetMetadata.setValue(property);
/* 2850 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheServerConfiguration(boolean flag) {
/* 2858 */     this.cacheServerConfiguration.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCallableStatementCacheSize(int size) {
/* 2865 */     this.callableStatementCacheSize.setValue(size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCapitalizeDBMDTypes(boolean property) {
/* 2872 */     this.capitalizeTypeNames.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCapitalizeTypeNames(boolean flag) {
/* 2879 */     this.capitalizeTypeNames.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCharacterEncoding(String encoding) {
/* 2886 */     this.characterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCharacterSetResults(String characterSet) {
/* 2893 */     this.characterSetResults.setValue(characterSet);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClobberStreamingResults(boolean flag) {
/* 2900 */     this.clobberStreamingResults.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClobCharacterEncoding(String encoding) {
/* 2907 */     this.clobCharacterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnectionCollation(String collation) {
/* 2914 */     this.connectionCollation.setValue(collation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnectTimeout(int timeoutMs) {
/* 2921 */     this.connectTimeout.setValue(timeoutMs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContinueBatchOnError(boolean property) {
/* 2928 */     this.continueBatchOnError.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCreateDatabaseIfNotExist(boolean flag) {
/* 2935 */     this.createDatabaseIfNotExist.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDefaultFetchSize(int n) {
/* 2942 */     this.defaultFetchSize.setValue(n);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDetectServerPreparedStmts(boolean property) {
/* 2949 */     this.detectServerPreparedStmts.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDontTrackOpenResources(boolean flag) {
/* 2956 */     this.dontTrackOpenResources.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDumpQueriesOnException(boolean flag) {
/* 2963 */     this.dumpQueriesOnException.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDynamicCalendars(boolean flag) {
/* 2970 */     this.dynamicCalendars.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setElideSetAutoCommits(boolean flag) {
/* 2977 */     this.elideSetAutoCommits.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEmptyStringsConvertToZero(boolean flag) {
/* 2984 */     this.emptyStringsConvertToZero.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEmulateLocators(boolean property) {
/* 2991 */     this.emulateLocators.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEmulateUnsupportedPstmts(boolean flag) {
/* 2998 */     this.emulateUnsupportedPstmts.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEnablePacketDebug(boolean flag) {
/* 3005 */     this.enablePacketDebug.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEncoding(String property) {
/* 3012 */     this.characterEncoding.setValue(property);
/* 3013 */     this.characterEncodingAsString = this.characterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExplainSlowQueries(boolean flag) {
/* 3021 */     this.explainSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFailOverReadOnly(boolean flag) {
/* 3028 */     this.failOverReadOnly.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGatherPerformanceMetrics(boolean flag) {
/* 3035 */     this.gatherPerformanceMetrics.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setHighAvailability(boolean property) {
/* 3044 */     this.autoReconnect.setValue(property);
/* 3045 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag) {
/* 3052 */     this.holdResultsOpenOverStatementClose.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIgnoreNonTxTables(boolean property) {
/* 3059 */     this.ignoreNonTxTables.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInitialTimeout(int property) {
/* 3066 */     this.initialTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIsInteractiveClient(boolean property) {
/* 3073 */     this.isInteractiveClient.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setJdbcCompliantTruncation(boolean flag) {
/* 3080 */     this.jdbcCompliantTruncation.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocatorFetchBufferSize(String value) throws SQLException {
/* 3087 */     this.locatorFetchBufferSize.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLogger(String property) {
/* 3094 */     this.loggerClassName.setValueAsObject(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLoggerClassName(String className) {
/* 3101 */     this.loggerClassName.setValue(className);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLogSlowQueries(boolean flag) {
/* 3108 */     this.logSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaintainTimeStats(boolean flag) {
/* 3115 */     this.maintainTimeStats.setValue(flag);
/* 3116 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes) {
/* 3124 */     this.maxQuerySizeToLog.setValue(sizeInBytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxReconnects(int property) {
/* 3131 */     this.maxReconnects.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxRows(int property) {
/* 3138 */     this.maxRows.setValue(property);
/* 3139 */     this.maxRowsAsInt = this.maxRows.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMetadataCacheSize(int value) {
/* 3146 */     this.metadataCacheSize.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNoDatetimeStringSync(boolean flag) {
/* 3153 */     this.noDatetimeStringSync.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNullCatalogMeansCurrent(boolean value) {
/* 3160 */     this.nullCatalogMeansCurrent.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNullNamePatternMatchesAll(boolean value) {
/* 3167 */     this.nullNamePatternMatchesAll.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPacketDebugBufferSize(int size) {
/* 3174 */     this.packetDebugBufferSize.setValue(size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setParanoid(boolean property) {
/* 3181 */     this.paranoid.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPedantic(boolean property) {
/* 3188 */     this.pedantic.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPreparedStatementCacheSize(int cacheSize) {
/* 3195 */     this.preparedStatementCacheSize.setValue(cacheSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) {
/* 3202 */     this.preparedStatementCacheSqlLimit.setValue(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProfileSql(boolean property) {
/* 3209 */     this.profileSQL.setValue(property);
/* 3210 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProfileSQL(boolean flag) {
/* 3217 */     this.profileSQL.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPropertiesTransform(String value) {
/* 3224 */     this.propertiesTransform.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setQueriesBeforeRetryMaster(int property) {
/* 3231 */     this.queriesBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setReconnectAtTxEnd(boolean property) {
/* 3238 */     this.reconnectAtTxEnd.setValue(property);
/* 3239 */     this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRelaxAutoCommit(boolean property) {
/* 3247 */     this.relaxAutoCommit.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setReportMetricsIntervalMillis(int millis) {
/* 3254 */     this.reportMetricsIntervalMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRequireSSL(boolean property) {
/* 3261 */     this.requireSSL.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag) {
/* 3268 */     this.retainStatementAfterResultSetClose.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRollbackOnPooledClose(boolean flag) {
/* 3275 */     this.rollbackOnPooledClose.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRoundRobinLoadBalance(boolean flag) {
/* 3282 */     this.roundRobinLoadBalance.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRunningCTS13(boolean flag) {
/* 3289 */     this.runningCTS13.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSecondsBeforeRetryMaster(int property) {
/* 3296 */     this.secondsBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setServerTimezone(String property) {
/* 3303 */     this.serverTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSessionVariables(String variables) {
/* 3310 */     this.sessionVariables.setValue(variables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSlowQueryThresholdMillis(int millis) {
/* 3317 */     this.slowQueryThresholdMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSocketFactoryClassName(String property) {
/* 3324 */     this.socketFactoryClassName.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSocketTimeout(int property) {
/* 3331 */     this.socketTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setStrictFloatingPoint(boolean property) {
/* 3338 */     this.strictFloatingPoint.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setStrictUpdates(boolean property) {
/* 3345 */     this.strictUpdates.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTinyInt1isBit(boolean flag) {
/* 3352 */     this.tinyInt1isBit.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTraceProtocol(boolean flag) {
/* 3359 */     this.traceProtocol.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTransformedBitIsBoolean(boolean flag) {
/* 3366 */     this.transformedBitIsBoolean.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseCompression(boolean property) {
/* 3373 */     this.useCompression.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseFastIntParsing(boolean flag) {
/* 3380 */     this.useFastIntParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseHostsInPrivileges(boolean property) {
/* 3387 */     this.useHostsInPrivileges.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseInformationSchema(boolean flag) {
/* 3394 */     this.useInformationSchema.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseLocalSessionState(boolean flag) {
/* 3401 */     this.useLocalSessionState.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseOldUTF8Behavior(boolean flag) {
/* 3408 */     this.useOldUTF8Behavior.setValue(flag);
/* 3409 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseOnlyServerErrorMessages(boolean flag) {
/* 3417 */     this.useOnlyServerErrorMessages.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseReadAheadInput(boolean flag) {
/* 3424 */     this.useReadAheadInput.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseServerPreparedStmts(boolean flag) {
/* 3431 */     this.detectServerPreparedStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseSqlStateCodes(boolean flag) {
/* 3438 */     this.useSqlStateCodes.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseSSL(boolean property) {
/* 3445 */     this.useSSL.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property) {
/* 3452 */     this.useStreamLengthsInPrepStmts.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseTimezone(boolean property) {
/* 3459 */     this.useTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseUltraDevWorkAround(boolean property) {
/* 3466 */     this.useUltraDevWorkAround.setValue(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseUnbufferedInput(boolean flag) {
/* 3473 */     this.useUnbufferedInput.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseUnicode(boolean flag) {
/* 3480 */     this.useUnicode.setValue(flag);
/* 3481 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
/* 3488 */     this.useUsageAdvisor.setValue(useUsageAdvisorFlag);
/* 3489 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setYearIsDateType(boolean flag) {
/* 3497 */     this.yearIsDateType.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setZeroDateTimeBehavior(String behavior) {
/* 3504 */     this.zeroDateTimeBehavior.setValue(behavior);
/*      */   }
/*      */   
/*      */   protected void storeToRef(Reference ref) throws SQLException {
/* 3508 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 3510 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 3511 */       Field propertyField = PROPERTY_LIST.get(i);
/*      */ 
/*      */       
/*      */       try {
/* 3515 */         ConnectionProperty propToStore = (ConnectionProperty)propertyField.get(this);
/*      */ 
/*      */         
/* 3518 */         if (ref != null) {
/* 3519 */           propToStore.storeTo(ref);
/*      */         }
/* 3521 */       } catch (IllegalAccessException iae) {
/* 3522 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.errorNotExpected"));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean useUnbufferedInput() {
/* 3531 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseCursorFetch() {
/* 3538 */     return this.useCursorFetch.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseCursorFetch(boolean flag) {
/* 3545 */     this.useCursorFetch.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 3552 */     return this.overrideSupportsIntegrityEnhancementFacility.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
/* 3559 */     this.overrideSupportsIntegrityEnhancementFacility.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getNoTimezoneConversionForTimeType() {
/* 3566 */     return this.noTimezoneConversionForTimeType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag) {
/* 3573 */     this.noTimezoneConversionForTimeType.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseJDBCCompliantTimezoneShift() {
/* 3580 */     return this.useJDBCCompliantTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag) {
/* 3587 */     this.useJDBCCompliantTimezoneShift.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoClosePStmtStreams() {
/* 3594 */     return this.autoClosePStmtStreams.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoClosePStmtStreams(boolean flag) {
/* 3601 */     this.autoClosePStmtStreams.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getProcessEscapeCodesForPrepStmts() {
/* 3608 */     return this.processEscapeCodesForPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag) {
/* 3615 */     this.processEscapeCodesForPrepStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseGmtMillisForDatetimes() {
/* 3622 */     return this.useGmtMillisForDatetimes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseGmtMillisForDatetimes(boolean flag) {
/* 3629 */     this.useGmtMillisForDatetimes.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getDumpMetadataOnColumnNotFound() {
/* 3636 */     return this.dumpMetadataOnColumnNotFound.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag) {
/* 3643 */     this.dumpMetadataOnColumnNotFound.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getResourceId() {
/* 3650 */     return this.resourceId.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setResourceId(String resourceId) {
/* 3657 */     this.resourceId.setValue(resourceId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRewriteBatchedStatements() {
/* 3664 */     return this.rewriteBatchedStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRewriteBatchedStatements(boolean flag) {
/* 3671 */     this.rewriteBatchedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getJdbcCompliantTruncationForReads() {
/* 3678 */     return this.jdbcCompliantTruncationForReads;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
/* 3686 */     this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseJvmCharsetConverters() {
/* 3693 */     return this.useJvmCharsetConverters.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseJvmCharsetConverters(boolean flag) {
/* 3700 */     this.useJvmCharsetConverters.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getPinGlobalTxToPhysicalConnection() {
/* 3707 */     return this.pinGlobalTxToPhysicalConnection.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag) {
/* 3714 */     this.pinGlobalTxToPhysicalConnection.setValue(flag);
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
/*      */   public void setGatherPerfMetrics(boolean flag) {
/* 3726 */     setGatherPerformanceMetrics(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getGatherPerfMetrics() {
/* 3733 */     return getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUltraDevHack(boolean flag) {
/* 3740 */     setUseUltraDevWorkAround(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUltraDevHack() {
/* 3747 */     return getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInteractiveClient(boolean property) {
/* 3754 */     setIsInteractiveClient(property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSocketFactory(String name) {
/* 3761 */     setSocketFactoryClassName(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSocketFactory() {
/* 3768 */     return getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseServerPrepStmts(boolean flag) {
/* 3775 */     setUseServerPreparedStmts(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseServerPrepStmts() {
/* 3782 */     return getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheCallableStmts(boolean flag) {
/* 3789 */     setCacheCallableStatements(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCacheCallableStmts() {
/* 3796 */     return getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCachePrepStmts(boolean flag) {
/* 3803 */     setCachePreparedStatements(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCachePrepStmts() {
/* 3810 */     return getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCallableStmtCacheSize(int cacheSize) {
/* 3817 */     setCallableStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCallableStmtCacheSize() {
/* 3824 */     return getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPrepStmtCacheSize(int cacheSize) {
/* 3831 */     setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPrepStmtCacheSize() {
/* 3838 */     return getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit) {
/* 3845 */     setPreparedStatementCacheSqlLimit(sqlLimit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPrepStmtCacheSqlLimit() {
/* 3852 */     return getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getNoAccessToProcedureBodies() {
/* 3859 */     return this.noAccessToProcedureBodies.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNoAccessToProcedureBodies(boolean flag) {
/* 3866 */     this.noAccessToProcedureBodies.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseOldAliasMetadataBehavior() {
/* 3873 */     return this.useOldAliasMetadataBehavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag) {
/* 3880 */     this.useOldAliasMetadataBehavior.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClientCertificateKeyStorePassword() {
/* 3887 */     return this.clientCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClientCertificateKeyStorePassword(String value) {
/* 3895 */     this.clientCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClientCertificateKeyStoreType() {
/* 3902 */     return this.clientCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClientCertificateKeyStoreType(String value) {
/* 3910 */     this.clientCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClientCertificateKeyStoreUrl() {
/* 3917 */     return this.clientCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClientCertificateKeyStoreUrl(String value) {
/* 3925 */     this.clientCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTrustCertificateKeyStorePassword() {
/* 3932 */     return this.trustCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTrustCertificateKeyStorePassword(String value) {
/* 3940 */     this.trustCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTrustCertificateKeyStoreType() {
/* 3947 */     return this.trustCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTrustCertificateKeyStoreType(String value) {
/* 3955 */     this.trustCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTrustCertificateKeyStoreUrl() {
/* 3962 */     return this.trustCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTrustCertificateKeyStoreUrl(String value) {
/* 3970 */     this.trustCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseSSPSCompatibleTimezoneShift() {
/* 3977 */     return this.useSSPSCompatibleTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
/* 3984 */     this.useSSPSCompatibleTimezoneShift.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getTreatUtilDateAsTimestamp() {
/* 3991 */     return this.treatUtilDateAsTimestamp.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag) {
/* 3998 */     this.treatUtilDateAsTimestamp.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseFastDateParsing() {
/* 4005 */     return this.useFastDateParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseFastDateParsing(boolean flag) {
/* 4012 */     this.useFastDateParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLocalSocketAddress() {
/* 4019 */     return this.localSocketAddress.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocalSocketAddress(String address) {
/* 4026 */     this.localSocketAddress.setValue(address);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseConfigs(String configs) {
/* 4033 */     this.useConfigs.setValue(configs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUseConfigs() {
/* 4040 */     return this.useConfigs.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getGenerateSimpleParameterMetadata() {
/* 4048 */     return this.generateSimpleParameterMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag) {
/* 4055 */     this.generateSimpleParameterMetadata.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getLogXaCommands() {
/* 4062 */     return this.logXaCommands.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLogXaCommands(boolean flag) {
/* 4069 */     this.logXaCommands.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getResultSetSizeThreshold() {
/* 4076 */     return this.resultSetSizeThreshold.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setResultSetSizeThreshold(int threshold) {
/* 4083 */     this.resultSetSizeThreshold.setValue(threshold);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNetTimeoutForStreamingResults() {
/* 4090 */     return this.netTimeoutForStreamingResults.getValueAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNetTimeoutForStreamingResults(int value) {
/* 4097 */     this.netTimeoutForStreamingResults.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getEnableQueryTimeouts() {
/* 4104 */     return this.enableQueryTimeouts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEnableQueryTimeouts(boolean flag) {
/* 4111 */     this.enableQueryTimeouts.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getPadCharsWithSpace() {
/* 4118 */     return this.padCharsWithSpace.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPadCharsWithSpace(boolean flag) {
/* 4125 */     this.padCharsWithSpace.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseDynamicCharsetInfo() {
/* 4132 */     return this.useDynamicCharsetInfo.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseDynamicCharsetInfo(boolean flag) {
/* 4139 */     this.useDynamicCharsetInfo.setValue(flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClientInfoProvider() {
/* 4146 */     return this.clientInfoProvider.getValueAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClientInfoProvider(String classname) {
/* 4153 */     this.clientInfoProvider.setValue(classname);
/*      */   }
/*      */   
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 4157 */     return this.populateInsertRowWithDefaultValues.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 4161 */     this.populateInsertRowWithDefaultValues.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceStrategy() {
/* 4165 */     return this.loadBalanceStrategy.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 4169 */     this.loadBalanceStrategy.setValue(strategy);
/*      */   }
/*      */   
/*      */   public boolean getTcpNoDelay() {
/* 4173 */     return this.tcpNoDelay.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setTcpNoDelay(boolean flag) {
/* 4177 */     this.tcpNoDelay.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getTcpKeepAlive() {
/* 4181 */     return this.tcpKeepAlive.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 4185 */     this.tcpKeepAlive.setValue(flag);
/*      */   }
/*      */   
/*      */   public int getTcpRcvBuf() {
/* 4189 */     return this.tcpRcvBuf.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setTcpRcvBuf(int bufSize) {
/* 4193 */     this.tcpRcvBuf.setValue(bufSize);
/*      */   }
/*      */   
/*      */   public int getTcpSndBuf() {
/* 4197 */     return this.tcpSndBuf.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setTcpSndBuf(int bufSize) {
/* 4201 */     this.tcpSndBuf.setValue(bufSize);
/*      */   }
/*      */   
/*      */   public int getTcpTrafficClass() {
/* 4205 */     return this.tcpTrafficClass.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setTcpTrafficClass(int classFlags) {
/* 4209 */     this.tcpTrafficClass.setValue(classFlags);
/*      */   }
/*      */   
/*      */   public boolean getUseNanosForElapsedTime() {
/* 4213 */     return this.useNanosForElapsedTime.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 4217 */     this.useNanosForElapsedTime.setValue(flag);
/*      */   }
/*      */   
/*      */   public long getSlowQueryThresholdNanos() {
/* 4221 */     return this.slowQueryThresholdNanos.getValueAsLong();
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdNanos(long nanos) {
/* 4225 */     this.slowQueryThresholdNanos.setValue(nanos);
/*      */   }
/*      */   
/*      */   public String getStatementInterceptors() {
/* 4229 */     return this.statementInterceptors.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setStatementInterceptors(String value) {
/* 4233 */     this.statementInterceptors.setValue(value);
/*      */   }
/*      */   
/*      */   public boolean getUseDirectRowUnpack() {
/* 4237 */     return this.useDirectRowUnpack.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 4241 */     this.useDirectRowUnpack.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getLargeRowSizeThreshold() {
/* 4245 */     return this.largeRowSizeThreshold.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLargeRowSizeThreshold(String value) {
/*      */     try {
/* 4250 */       this.largeRowSizeThreshold.setValue(value);
/* 4251 */     } catch (SQLException sqlEx) {
/* 4252 */       RuntimeException ex = new RuntimeException(sqlEx.getMessage());
/* 4253 */       ex.initCause(sqlEx);
/*      */       
/* 4255 */       throw ex;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 4260 */     return this.useBlobToStoreUTF8OutsideBMP.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 4264 */     this.useBlobToStoreUTF8OutsideBMP.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 4268 */     return this.utf8OutsideBmpExcludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 4272 */     this.utf8OutsideBmpExcludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */   
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 4276 */     return this.utf8OutsideBmpIncludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 4280 */     this.utf8OutsideBmpIncludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */   
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 4284 */     return this.includeInnodbStatusInDeadlockExceptions.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 4288 */     this.includeInnodbStatusInDeadlockExceptions.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getBlobsAreStrings() {
/* 4292 */     return this.blobsAreStrings.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 4296 */     this.blobsAreStrings.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 4300 */     return this.functionsNeverReturnBlobs.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 4304 */     this.functionsNeverReturnBlobs.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getAutoSlowLog() {
/* 4308 */     return this.autoSlowLog.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAutoSlowLog(boolean flag) {
/* 4312 */     this.autoSlowLog.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getConnectionLifecycleInterceptors() {
/* 4316 */     return this.connectionLifecycleInterceptors.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 4320 */     this.connectionLifecycleInterceptors.setValue(interceptors);
/*      */   }
/*      */   
/*      */   public String getProfilerEventHandler() {
/* 4324 */     return this.profilerEventHandler.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setProfilerEventHandler(String handler) {
/* 4328 */     this.profilerEventHandler.setValue(handler);
/*      */   }
/*      */   
/*      */   public boolean getVerifyServerCertificate() {
/* 4332 */     return this.verifyServerCertificate.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 4336 */     this.verifyServerCertificate.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 4340 */     return this.useLegacyDatetimeCode.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 4344 */     this.useLegacyDatetimeCode.setValue(flag);
/*      */   }
/*      */   
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 4348 */     return this.selfDestructOnPingSecondsLifetime.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) {
/* 4352 */     this.selfDestructOnPingSecondsLifetime.setValue(seconds);
/*      */   }
/*      */   
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 4356 */     return this.selfDestructOnPingMaxOperations.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) {
/* 4360 */     this.selfDestructOnPingMaxOperations.setValue(maxOperations);
/*      */   }
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\ConnectionPropertiesImpl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */