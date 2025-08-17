/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
/*     */   private static final String URL_PREFIX = "jdbc:mysql://";
/*     */   private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
/*     */   private static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
/*     */   public static final String DBNAME_PROPERTY_KEY = "DBNAME";
/*     */   public static final boolean DEBUG = false;
/*     */   public static final int HOST_NAME_INDEX = 0;
/*     */   public static final String HOST_PROPERTY_KEY = "HOST";
/*     */   public static final String PASSWORD_PROPERTY_KEY = "password";
/*     */   public static final int PORT_NUMBER_INDEX = 1;
/*     */   public static final String PORT_PROPERTY_KEY = "PORT";
/*     */   public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
/*     */   public static final boolean TRACE = false;
/*     */   public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
/*     */   public static final String USER_PROPERTY_KEY = "user";
/*     */   
/*     */   static int getMajorVersionInternal() {
/* 128 */     return safeIntParse("5");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getMinorVersionInternal() {
/* 137 */     return safeIntParse("1");
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
/*     */   protected static String[] parseHostPortPair(String hostPortPair) throws SQLException {
/* 156 */     int portIndex = hostPortPair.indexOf(":");
/*     */     
/* 158 */     String[] splitValues = new String[2];
/*     */     
/* 160 */     String hostname = null;
/*     */     
/* 162 */     if (portIndex != -1) {
/* 163 */       if (portIndex + 1 < hostPortPair.length()) {
/* 164 */         String portAsString = hostPortPair.substring(portIndex + 1);
/* 165 */         hostname = hostPortPair.substring(0, portIndex);
/*     */         
/* 167 */         splitValues[0] = hostname;
/*     */         
/* 169 */         splitValues[1] = portAsString;
/*     */       } else {
/* 171 */         throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.37"), "01S00");
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 176 */       splitValues[0] = hostPortPair;
/* 177 */       splitValues[1] = null;
/*     */     } 
/*     */     
/* 180 */     return splitValues;
/*     */   }
/*     */   
/*     */   private static int safeIntParse(String intAsString) {
/*     */     try {
/* 185 */       return Integer.parseInt(intAsString);
/* 186 */     } catch (NumberFormatException nfe) {
/* 187 */       return 0;
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
/*     */   public boolean acceptsURL(String url) throws SQLException {
/* 217 */     return (parseURL(url, null) != null);
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
/*     */   public Connection connect(String url, Properties info) throws SQLException {
/* 266 */     if (url != null) {
/* 267 */       if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://"))
/* 268 */         return connectLoadBalanced(url, info); 
/* 269 */       if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://"))
/*     */       {
/* 271 */         return connectReplicationConnection(url, info);
/*     */       }
/*     */     } 
/*     */     
/* 275 */     Properties props = null;
/*     */     
/* 277 */     if ((props = parseURL(url, info)) == null) {
/* 278 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 282 */       Connection newConn = ConnectionImpl.getInstance(host(props), port(props), props, database(props), url);
/*     */ 
/*     */       
/* 285 */       return newConn;
/* 286 */     } catch (SQLException sqlEx) {
/*     */ 
/*     */       
/* 289 */       throw sqlEx;
/* 290 */     } catch (Exception ex) {
/* 291 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), "08001");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 297 */       sqlEx.initCause(ex);
/*     */       
/* 299 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Connection connectLoadBalanced(String url, Properties info) throws SQLException {
/* 305 */     Properties parsedProps = parseURL(url, info);
/*     */ 
/*     */     
/* 308 */     parsedProps.remove("roundRobinLoadBalance");
/*     */     
/* 310 */     if (parsedProps == null) {
/* 311 */       return null;
/*     */     }
/*     */     
/* 314 */     String hostValues = parsedProps.getProperty("HOST");
/*     */     
/* 316 */     List hostList = null;
/*     */     
/* 318 */     if (hostValues != null) {
/* 319 */       hostList = StringUtils.split(hostValues, ",", true);
/*     */     }
/*     */     
/* 322 */     if (hostList == null) {
/* 323 */       hostList = new ArrayList();
/* 324 */       hostList.add("localhost:3306");
/*     */     } 
/*     */     
/* 327 */     LoadBalancingConnectionProxy proxyBal = new LoadBalancingConnectionProxy(hostList, parsedProps);
/*     */ 
/*     */     
/* 330 */     return (Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, proxyBal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Connection connectReplicationConnection(String url, Properties info) throws SQLException {
/* 337 */     Properties parsedProps = parseURL(url, info);
/*     */     
/* 339 */     if (parsedProps == null) {
/* 340 */       return null;
/*     */     }
/*     */     
/* 343 */     Properties masterProps = (Properties)parsedProps.clone();
/* 344 */     Properties slavesProps = (Properties)parsedProps.clone();
/*     */ 
/*     */ 
/*     */     
/* 348 */     slavesProps.setProperty("com.mysql.jdbc.ReplicationConnection.isSlave", "true");
/*     */ 
/*     */     
/* 351 */     String hostValues = parsedProps.getProperty("HOST");
/*     */     
/* 353 */     if (hostValues != null) {
/* 354 */       StringTokenizer st = new StringTokenizer(hostValues, ",");
/*     */       
/* 356 */       StringBuffer masterHost = new StringBuffer();
/* 357 */       StringBuffer slaveHosts = new StringBuffer();
/*     */       
/* 359 */       if (st.hasMoreTokens()) {
/* 360 */         String[] hostPortPair = parseHostPortPair(st.nextToken());
/*     */         
/* 362 */         if (hostPortPair[0] != null) {
/* 363 */           masterHost.append(hostPortPair[0]);
/*     */         }
/*     */         
/* 366 */         if (hostPortPair[1] != null) {
/* 367 */           masterHost.append(":");
/* 368 */           masterHost.append(hostPortPair[1]);
/*     */         } 
/*     */       } 
/*     */       
/* 372 */       boolean firstSlaveHost = true;
/*     */       
/* 374 */       while (st.hasMoreTokens()) {
/* 375 */         String[] hostPortPair = parseHostPortPair(st.nextToken());
/*     */         
/* 377 */         if (!firstSlaveHost) {
/* 378 */           slaveHosts.append(",");
/*     */         } else {
/* 380 */           firstSlaveHost = false;
/*     */         } 
/*     */         
/* 383 */         if (hostPortPair[0] != null) {
/* 384 */           slaveHosts.append(hostPortPair[0]);
/*     */         }
/*     */         
/* 387 */         if (hostPortPair[1] != null) {
/* 388 */           slaveHosts.append(":");
/* 389 */           slaveHosts.append(hostPortPair[1]);
/*     */         } 
/*     */       } 
/*     */       
/* 393 */       if (slaveHosts.length() == 0) {
/* 394 */         throw SQLError.createSQLException("Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", "01S00");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 400 */       masterProps.setProperty("HOST", masterHost.toString());
/* 401 */       slavesProps.setProperty("HOST", slaveHosts.toString());
/*     */     } 
/*     */     
/* 404 */     return new ReplicationConnection(masterProps, slavesProps);
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
/*     */   public String database(Properties props) {
/* 416 */     return props.getProperty("DBNAME");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMajorVersion() {
/* 425 */     return getMajorVersionInternal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinorVersion() {
/* 434 */     return getMinorVersionInternal();
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
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
/* 465 */     if (info == null) {
/* 466 */       info = new Properties();
/*     */     }
/*     */     
/* 469 */     if (url != null && url.startsWith("jdbc:mysql://")) {
/* 470 */       info = parseURL(url, info);
/*     */     }
/*     */     
/* 473 */     DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST"));
/*     */     
/* 475 */     hostProp.required = true;
/* 476 */     hostProp.description = Messages.getString("NonRegisteringDriver.3");
/*     */     
/* 478 */     DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", "3306"));
/*     */     
/* 480 */     portProp.required = false;
/* 481 */     portProp.description = Messages.getString("NonRegisteringDriver.7");
/*     */     
/* 483 */     DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME"));
/*     */     
/* 485 */     dbProp.required = false;
/* 486 */     dbProp.description = "Database name";
/*     */     
/* 488 */     DriverPropertyInfo userProp = new DriverPropertyInfo("user", info.getProperty("user"));
/*     */     
/* 490 */     userProp.required = true;
/* 491 */     userProp.description = Messages.getString("NonRegisteringDriver.13");
/*     */     
/* 493 */     DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", info.getProperty("password"));
/*     */ 
/*     */     
/* 496 */     passwordProp.required = true;
/* 497 */     passwordProp.description = Messages.getString("NonRegisteringDriver.16");
/*     */ 
/*     */     
/* 500 */     DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo(info, 5);
/*     */ 
/*     */     
/* 503 */     dpi[0] = hostProp;
/* 504 */     dpi[1] = portProp;
/* 505 */     dpi[2] = dbProp;
/* 506 */     dpi[3] = userProp;
/* 507 */     dpi[4] = passwordProp;
/*     */     
/* 509 */     return dpi;
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
/*     */   public String host(Properties props) {
/* 526 */     return props.getProperty("HOST", "localhost");
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
/*     */   public boolean jdbcCompliant() {
/* 542 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Properties parseURL(String url, Properties defaults) throws SQLException {
/* 547 */     Properties urlProps = (defaults != null) ? new Properties(defaults) : new Properties();
/*     */ 
/*     */     
/* 550 */     if (url == null) {
/* 551 */       return null;
/*     */     }
/*     */     
/* 554 */     if (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql://") && !StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://") && !StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://") && !StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://"))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 561 */       return null;
/*     */     }
/*     */     
/* 564 */     int beginningOfSlashes = url.indexOf("//");
/*     */     
/* 566 */     if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://"))
/*     */     {
/* 568 */       urlProps.setProperty("socketFactory", "com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 577 */     int index = url.indexOf("?");
/*     */     
/* 579 */     if (index != -1) {
/* 580 */       String paramString = url.substring(index + 1, url.length());
/* 581 */       url = url.substring(0, index);
/*     */       
/* 583 */       StringTokenizer queryParams = new StringTokenizer(paramString, "&");
/*     */       
/* 585 */       while (queryParams.hasMoreTokens()) {
/* 586 */         String parameterValuePair = queryParams.nextToken();
/*     */         
/* 588 */         int indexOfEquals = StringUtils.indexOfIgnoreCase(0, parameterValuePair, "=");
/*     */ 
/*     */         
/* 591 */         String parameter = null;
/* 592 */         String value = null;
/*     */         
/* 594 */         if (indexOfEquals != -1) {
/* 595 */           parameter = parameterValuePair.substring(0, indexOfEquals);
/*     */           
/* 597 */           if (indexOfEquals + 1 < parameterValuePair.length()) {
/* 598 */             value = parameterValuePair.substring(indexOfEquals + 1);
/*     */           }
/*     */         } 
/*     */         
/* 602 */         if (value != null && value.length() > 0 && parameter != null && parameter.length() > 0) {
/*     */           
/*     */           try {
/* 605 */             urlProps.put(parameter, URLDecoder.decode(value, "UTF-8"));
/*     */           }
/* 607 */           catch (UnsupportedEncodingException badEncoding) {
/*     */             
/* 609 */             urlProps.put(parameter, URLDecoder.decode(value));
/* 610 */           } catch (NoSuchMethodError nsme) {
/*     */             
/* 612 */             urlProps.put(parameter, URLDecoder.decode(value));
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 618 */     url = url.substring(beginningOfSlashes + 2);
/*     */     
/* 620 */     String hostStuff = null;
/*     */     
/* 622 */     int slashIndex = url.indexOf("/");
/*     */     
/* 624 */     if (slashIndex != -1) {
/* 625 */       hostStuff = url.substring(0, slashIndex);
/*     */       
/* 627 */       if (slashIndex + 1 < url.length()) {
/* 628 */         urlProps.put("DBNAME", url.substring(slashIndex + 1, url.length()));
/*     */       }
/*     */     } else {
/*     */       
/* 632 */       hostStuff = url;
/*     */     } 
/*     */     
/* 635 */     if (hostStuff != null && hostStuff.length() > 0) {
/* 636 */       urlProps.put("HOST", hostStuff);
/*     */     }
/*     */     
/* 639 */     String propertiesTransformClassName = urlProps.getProperty("propertiesTransform");
/*     */ 
/*     */     
/* 642 */     if (propertiesTransformClassName != null) {
/*     */       try {
/* 644 */         ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
/*     */ 
/*     */         
/* 647 */         urlProps = propTransformer.transformProperties(urlProps);
/* 648 */       } catch (InstantiationException e) {
/* 649 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 655 */       catch (IllegalAccessException e) {
/* 656 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 662 */       catch (ClassNotFoundException e) {
/* 663 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 672 */     if (Util.isColdFusion() && urlProps.getProperty("autoConfigureForColdFusion", "true").equalsIgnoreCase("true")) {
/*     */       
/* 674 */       String configs = urlProps.getProperty("useConfigs");
/*     */       
/* 676 */       StringBuffer newConfigs = new StringBuffer();
/*     */       
/* 678 */       if (configs != null) {
/* 679 */         newConfigs.append(configs);
/* 680 */         newConfigs.append(",");
/*     */       } 
/*     */       
/* 683 */       newConfigs.append("coldFusion");
/*     */       
/* 685 */       urlProps.setProperty("useConfigs", newConfigs.toString());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 691 */     String configNames = null;
/*     */     
/* 693 */     if (defaults != null) {
/* 694 */       configNames = defaults.getProperty("useConfigs");
/*     */     }
/*     */     
/* 697 */     if (configNames == null) {
/* 698 */       configNames = urlProps.getProperty("useConfigs");
/*     */     }
/*     */     
/* 701 */     if (configNames != null) {
/* 702 */       List splitNames = StringUtils.split(configNames, ",", true);
/*     */       
/* 704 */       Properties configProps = new Properties();
/*     */       
/* 706 */       Iterator namesIter = splitNames.iterator();
/*     */       
/* 708 */       while (namesIter.hasNext()) {
/* 709 */         String configName = namesIter.next();
/*     */         
/*     */         try {
/* 712 */           InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
/*     */ 
/*     */ 
/*     */           
/* 716 */           if (configAsStream == null) {
/* 717 */             throw SQLError.createSQLException("Can't find configuration template named '" + configName + "'", "01S00");
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 723 */           configProps.load(configAsStream);
/* 724 */         } catch (IOException ioEx) {
/* 725 */           SQLException sqlEx = SQLError.createSQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, "01S00");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 731 */           sqlEx.initCause(ioEx);
/*     */           
/* 733 */           throw sqlEx;
/*     */         } 
/*     */       } 
/*     */       
/* 737 */       Iterator propsIter = urlProps.keySet().iterator();
/*     */       
/* 739 */       while (propsIter.hasNext()) {
/* 740 */         String key = propsIter.next().toString();
/* 741 */         String property = urlProps.getProperty(key);
/* 742 */         configProps.setProperty(key, property);
/*     */       } 
/*     */       
/* 745 */       urlProps = configProps;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 750 */     if (defaults != null) {
/* 751 */       Iterator propsIter = defaults.keySet().iterator();
/*     */       
/* 753 */       while (propsIter.hasNext()) {
/* 754 */         String key = propsIter.next().toString();
/* 755 */         String property = defaults.getProperty(key);
/* 756 */         urlProps.setProperty(key, property);
/*     */       } 
/*     */     } 
/*     */     
/* 760 */     return urlProps;
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
/*     */   public int port(Properties props) {
/* 772 */     return Integer.parseInt(props.getProperty("PORT", "3306"));
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
/*     */   public String property(String name, Properties props) {
/* 786 */     return props.getProperty(name);
/*     */   }
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\NonRegisteringDriver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */