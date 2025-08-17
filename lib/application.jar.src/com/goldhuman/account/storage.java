/*      */ package com.goldhuman.account;
/*      */ 
/*      */ import application.procedure.handler;
/*      */ import application.query.handler;
/*      */ import com.goldhuman.xml.parser;
/*      */ import java.io.FileInputStream;
/*      */ import java.security.MessageDigest;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class storage
/*      */ {
/*      */   public static String getNameById(Integer integer) {
/*      */     try {
/*   29 */       Object[] aobj = handler.get("getUsername").select("byId").execute(new Object[] { integer }, "auth0");
/*      */       
/*   31 */       if (null != aobj && aobj.length > 0) {
/*   32 */         return (String)((Object[])aobj[0])[0];
/*      */       }
/*   34 */     } catch (Exception exception) {
/*      */       
/*   36 */       exception.printStackTrace(System.out);
/*      */     } 
/*   38 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Integer getIdByName(String s) {
/*      */     try {
/*   45 */       Object[] aobj = handler.get("getUserid").select("byName").execute(new Object[] { s.toLowerCase() }, "auth0");
/*      */       
/*   47 */       if (null != aobj && aobj.length > 0) {
/*   48 */         return (Integer)((Object[])aobj[0])[0];
/*      */       }
/*   50 */     } catch (Exception exception) {
/*      */       
/*   52 */       exception.printStackTrace(System.out);
/*      */     } 
/*   54 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] getUserInfobyName(String s) {
/*      */     try {
/*   61 */       Object[] aobj = handler.get("getUserInfo").select("byName").execute(new Object[] { s.toLowerCase() }, "auth0");
/*      */       
/*   63 */       if (null != aobj && aobj.length > 0) {
/*   64 */         return (Object[])aobj[0];
/*      */       }
/*   66 */     } catch (Exception exception) {
/*      */       
/*   68 */       exception.printStackTrace(System.out);
/*      */     } 
/*   70 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean addUser(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11, String s12, Integer integer, String s13, String s14, String s15) {
/*      */     try {
/*   80 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*   81 */       messagedigest.update(s.toLowerCase().getBytes());
/*   82 */       messagedigest.update(s1.getBytes());
/*   83 */       MessageDigest messagedigest1 = MessageDigest.getInstance("MD5");
/*   84 */       messagedigest1.update(s.toLowerCase().getBytes());
/*   85 */       messagedigest1.update(s15.getBytes());
/*   86 */       if (s2 == null)
/*   87 */         s2 = ""; 
/*   88 */       if (s3 == null)
/*   89 */         s3 = ""; 
/*   90 */       if (s4 == null)
/*   91 */         s4 = ""; 
/*   92 */       if (s5 == null)
/*   93 */         s5 = ""; 
/*   94 */       if (s6 == null)
/*   95 */         s6 = ""; 
/*   96 */       if (s7 == null)
/*   97 */         s7 = ""; 
/*   98 */       if (s8 == null)
/*   99 */         s8 = ""; 
/*  100 */       if (s9 == null)
/*  101 */         s9 = ""; 
/*  102 */       if (s10 == null)
/*  103 */         s10 = ""; 
/*  104 */       if (s11 == null)
/*  105 */         s11 = ""; 
/*  106 */       if (s12 == null)
/*  107 */         s12 = ""; 
/*  108 */       if (integer == null)
/*  109 */         integer = new Integer(2); 
/*  110 */       if (s13 == null)
/*  111 */         s13 = ""; 
/*  112 */       if (s14 == null)
/*  113 */         s14 = ""; 
/*  114 */       Object[] aobj = { s.toLowerCase(), messagedigest.digest(), s2.getBytes("UTF-16LE"), s3.getBytes("UTF-16LE"), s4.getBytes("UTF-16LE"), s5.getBytes("UTF-16LE"), s6.getBytes("UTF-16LE"), s7.getBytes("UTF-16LE"), s8.getBytes("UTF-16LE"), s9.getBytes("UTF-16LE"), s10.getBytes("UTF-16LE"), s11.getBytes("UTF-16LE"), s12.getBytes("UTF-16LE"), integer, s13, s14.getBytes("UTF-16LE"), messagedigest1.digest() };
/*      */ 
/*      */ 
/*      */       
/*  118 */       return (handler.get("adduser").execute(aobj, "auth0") == 0);
/*      */     }
/*  120 */     catch (Exception exception) {
/*      */       
/*  122 */       exception.printStackTrace(System.out);
/*      */       
/*  124 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean updateUserInfo(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11, Integer integer, String s12, String s13) {
/*      */     try {
/*  133 */       if (s1 == null)
/*  134 */         s1 = ""; 
/*  135 */       if (s2 == null)
/*  136 */         s2 = ""; 
/*  137 */       if (s3 == null)
/*  138 */         s3 = ""; 
/*  139 */       if (s4 == null)
/*  140 */         s4 = ""; 
/*  141 */       if (s5 == null)
/*  142 */         s5 = ""; 
/*  143 */       if (s6 == null)
/*  144 */         s6 = ""; 
/*  145 */       if (s7 == null)
/*  146 */         s7 = ""; 
/*  147 */       if (s8 == null)
/*  148 */         s8 = ""; 
/*  149 */       if (s9 == null)
/*  150 */         s9 = ""; 
/*  151 */       if (s10 == null)
/*  152 */         s10 = ""; 
/*  153 */       if (s11 == null)
/*  154 */         s11 = ""; 
/*  155 */       if (integer == null)
/*  156 */         integer = new Integer(2); 
/*  157 */       if (s12 == null)
/*  158 */         s12 = ""; 
/*  159 */       if (s13 == null)
/*  160 */         s13 = ""; 
/*  161 */       Object[] aobj = { s.toLowerCase(), s1.getBytes("UTF-16LE"), s2.getBytes("UTF-16LE"), s3.getBytes("UTF-16LE"), s4.getBytes("UTF-16LE"), s5.getBytes("UTF-16LE"), s6.getBytes("UTF-16LE"), s7.getBytes("UTF-16LE"), s8.getBytes("UTF-16LE"), s9.getBytes("UTF-16LE"), s10.getBytes("UTF-16LE"), s11.getBytes("UTF-16LE"), integer, s12, s13.getBytes("UTF-16LE") };
/*      */ 
/*      */ 
/*      */       
/*  165 */       return (handler.get("updateUserInfo").execute(aobj, "auth0") == 0);
/*      */     }
/*  167 */     catch (Exception exception) {
/*      */       
/*  169 */       exception.printStackTrace(System.out);
/*      */       
/*  171 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean tryLogin(String s, String s1) {
/*      */     try {
/*  178 */       System.out.println("Starting acquire password");
/*  179 */       Object[] aobj = acquireIdPasswd(s.toLowerCase());
/*  180 */       if (null == aobj) {
/*      */         
/*  182 */         System.out.println("Return value is null!!!");
/*  183 */         return false;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  188 */       byte[] abyte0 = (byte[])aobj[1];
/*  189 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  190 */       messagedigest.update(s.toLowerCase().getBytes());
/*  191 */       messagedigest.update(s1.getBytes());
/*  192 */       byte[] abyte1 = messagedigest.digest();
/*  193 */       System.out.println("Checking Password");
/*  194 */       for (int n = 0; n < abyte1.length; n++)
/*      */       {
/*  196 */         System.out.println("base:" + abyte0[n] + "; code:" + abyte1[n]);
/*      */       }
/*  198 */       int i = 0;
/*  199 */       while (i < abyte0.length) {
/*      */         
/*  201 */         if (abyte0[i] != abyte1[i])
/*  202 */           return false; 
/*  203 */         i++;
/*      */       } 
/*  205 */       System.out.println("");
/*  206 */       return true;
/*      */     }
/*  208 */     catch (Exception exception) {
/*      */       
/*  210 */       exception.printStackTrace(System.out);
/*      */       
/*  212 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean tryLogin2(String s, String s1) {
/*      */     try {
/*  219 */       Object[] aobj = acquireIdPasswd2(s.toLowerCase());
/*  220 */       if (null == aobj) {
/*  221 */         return false;
/*      */       }
/*      */ 
/*      */       
/*  225 */       byte[] abyte0 = (byte[])aobj[1];
/*  226 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  227 */       messagedigest.update(s.toLowerCase().getBytes());
/*  228 */       messagedigest.update(s1.getBytes());
/*  229 */       byte[] abyte1 = messagedigest.digest();
/*  230 */       int i = 0;
/*  231 */       while (i < abyte0.length) {
/*      */         
/*  233 */         if (abyte0[i] != abyte1[i])
/*  234 */           return false; 
/*  235 */         i++;
/*      */       } 
/*  237 */       return true;
/*      */     }
/*  239 */     catch (Exception exception) {
/*      */       
/*  241 */       exception.printStackTrace(System.out);
/*      */       
/*  243 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean changePasswdWithOld(String s, String s1, String s2) {
/*      */     try {
/*  250 */       Object[] aobj = acquireIdPasswd(s.toLowerCase());
/*  251 */       if (null == aobj) {
/*  252 */         return false;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  257 */       byte[] abyte0 = (byte[])aobj[1];
/*  258 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  259 */       messagedigest.update(s.toLowerCase().getBytes());
/*  260 */       messagedigest.update(s2.getBytes());
/*  261 */       byte[] abyte1 = messagedigest.digest();
/*  262 */       int i = 0;
/*  263 */       while (i < abyte0.length) {
/*      */         
/*  265 */         if (abyte0[i] != abyte1[i])
/*  266 */           return false; 
/*  267 */         i++;
/*      */       } 
/*  269 */       messagedigest.reset();
/*  270 */       messagedigest.update(s.toLowerCase().getBytes());
/*  271 */       messagedigest.update(s1.getBytes());
/*  272 */       return (handler.get("changePasswd").execute(new Object[] { s.toLowerCase(), messagedigest.digest() }, "auth0") == 0);
/*      */ 
/*      */     
/*      */     }
/*  276 */     catch (Exception exception) {
/*      */       
/*  278 */       exception.printStackTrace(System.out);
/*      */       
/*  280 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean changePasswdWithOld2(String s, String s1, String s2) {
/*      */     try {
/*  287 */       Object[] aobj = acquireIdPasswd2(s.toLowerCase());
/*  288 */       if (null == aobj) {
/*  289 */         return false;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  294 */       byte[] abyte0 = (byte[])aobj[1];
/*  295 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  296 */       messagedigest.update(s.toLowerCase().getBytes());
/*  297 */       messagedigest.update(s2.getBytes());
/*  298 */       byte[] abyte1 = messagedigest.digest();
/*  299 */       int i = 0;
/*  300 */       while (i < abyte0.length) {
/*      */         
/*  302 */         if (abyte0[i] != abyte1[i])
/*  303 */           return false; 
/*  304 */         i++;
/*      */       } 
/*  306 */       messagedigest.reset();
/*  307 */       messagedigest.update(s.toLowerCase().getBytes());
/*  308 */       messagedigest.update(s1.getBytes());
/*  309 */       return (handler.get("changePasswd").execute(new Object[] { s.toLowerCase(), messagedigest.digest() }, "auth0") == 0);
/*      */ 
/*      */     
/*      */     }
/*  313 */     catch (Exception exception) {
/*      */       
/*  315 */       exception.printStackTrace(System.out);
/*      */       
/*  317 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean changePasswd2WithOld2(String s, String s1, String s2) {
/*      */     try {
/*  324 */       Object[] aobj = acquireIdPasswd2(s.toLowerCase());
/*  325 */       if (null == aobj) {
/*  326 */         return false;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  331 */       byte[] abyte0 = (byte[])aobj[1];
/*  332 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  333 */       messagedigest.update(s.toLowerCase().getBytes());
/*  334 */       messagedigest.update(s2.getBytes());
/*  335 */       byte[] abyte1 = messagedigest.digest();
/*  336 */       int i = 0;
/*  337 */       while (i < abyte0.length) {
/*      */         
/*  339 */         if (abyte0[i] != abyte1[i])
/*  340 */           return false; 
/*  341 */         i++;
/*      */       } 
/*  343 */       messagedigest.reset();
/*  344 */       messagedigest.update(s.toLowerCase().getBytes());
/*  345 */       messagedigest.update(s1.getBytes());
/*  346 */       return (handler.get("changePasswd2").execute(new Object[] { s.toLowerCase(), messagedigest.digest() }, "auth0") == 0);
/*      */ 
/*      */     
/*      */     }
/*  350 */     catch (Exception exception) {
/*      */       
/*  352 */       exception.printStackTrace(System.out);
/*      */       
/*  354 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean changePasswd(String s, String s1) {
/*      */     try {
/*  362 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  363 */       messagedigest.update(s.toLowerCase().getBytes());
/*  364 */       messagedigest.update(s1.getBytes());
/*  365 */       return (handler.get("changePasswd").execute(new Object[] { s.toLowerCase(), messagedigest.digest() }, "auth0") == 0);
/*      */ 
/*      */     
/*      */     }
/*  369 */     catch (Exception exception) {
/*      */       
/*  371 */       exception.printStackTrace(System.out);
/*      */       
/*  373 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean changePasswd2(String s, String s1) {
/*      */     try {
/*  381 */       MessageDigest messagedigest = MessageDigest.getInstance("MD5");
/*  382 */       messagedigest.update(s.toLowerCase().getBytes());
/*  383 */       messagedigest.update(s1.getBytes());
/*  384 */       return (handler.get("changePasswd2").execute(new Object[] { s.toLowerCase(), messagedigest.digest() }, "auth0") == 0);
/*      */ 
/*      */     
/*      */     }
/*  388 */     catch (Exception exception) {
/*      */       
/*  390 */       exception.printStackTrace(System.out);
/*      */       
/*  392 */       return false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public static byte[] StringPassword(String s) {
/*  397 */     byte[] abyte0 = new byte[16];
/*  398 */     for (int i = 1; i <= 16; i++) {
/*  399 */       abyte0[i - 1] = (byte)Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
/*      */     }
/*  401 */     return abyte0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] acquireIdPasswd(String s) {
/*      */     try {
/*  411 */       Integer integer = new Integer(0);
/*  412 */       byte[] abyte0 = null;
/*  413 */       String s1 = "";
/*  414 */       Integer integer1 = new Integer(0);
/*  415 */       Object[] aobj = { s.toLowerCase(), integer, s1 };
/*      */ 
/*      */       
/*  418 */       System.out.println("Sending query to acquire password");
/*  419 */       if (handler.get("acquireuserpasswd").execute(aobj, "auth0") == 0) {
/*      */         
/*  421 */         System.out.println("Received acquired password");
/*  422 */         integer = (Integer)aobj[1];
/*  423 */         s1 = (String)aobj[2];
/*  424 */         if (null != s1)
/*  425 */           abyte0 = StringPassword(s1); 
/*  426 */         if (null == integer || null == abyte0 || integer.intValue() == 0) {
/*      */           
/*  428 */           System.out.println("acquireIdPasswd procedure return null:account=" + s + ",uid=" + integer + ",passwd=" + abyte0);
/*  429 */           return null;
/*      */         } 
/*  431 */         System.out.println("Returning acquired password");
/*  432 */         return new Object[] { integer, abyte0, integer1 };
/*      */       } 
/*      */ 
/*      */       
/*  436 */       System.out.println("acquireIdPasswd procedure failed:account=" + s);
/*  437 */       return null;
/*      */     }
/*  439 */     catch (Exception exception) {
/*      */       
/*  441 */       exception.printStackTrace(System.out);
/*  442 */       System.out.println("acquireIdPasswd exception:account=" + s);
/*      */       
/*  444 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] acquireIdPasswd2(String s) {
/*      */     try {
/*  451 */       Object[] aobj = handler.get("acquireIdPasswd2").select("byName").execute(new Object[] { s.toLowerCase() }, "auth0");
/*      */ 
/*      */       
/*  454 */       if (null != aobj && aobj.length > 0) {
/*  455 */         return (Object[])aobj[0];
/*      */       }
/*  457 */     } catch (Exception exception) {
/*      */       
/*  459 */       exception.printStackTrace(System.out);
/*      */     } 
/*  461 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] getIPLimit(Integer integer) {
/*      */     try {
/*  468 */       Object[] aobj = handler.get("getIPLimit").select("byUid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */       
/*  471 */       if (null != aobj && aobj.length > 0)
/*  472 */         return (Object[])aobj[0]; 
/*  473 */       return null;
/*      */     }
/*  475 */     catch (Exception exception) {
/*      */       
/*  477 */       exception.printStackTrace(System.out);
/*      */       
/*  479 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static int checkIPLimit(Integer integer, Integer integer1) {
/*      */     try {
/*  486 */       Object[] aobj = getIPLimit(integer);
/*  487 */       if (null == aobj)
/*  488 */         return 0; 
/*  489 */       if (null != aobj[8] && ((String)aobj[8]).equals("t"))
/*  490 */         return 1; 
/*  491 */       if (null == aobj[7] || !((String)aobj[7]).equals("t")) {
/*  492 */         return 0;
/*      */       }
/*      */       
/*  495 */       boolean flag = false;
/*  496 */       int i = 1;
/*      */ 
/*      */       
/*  499 */       while (i < 6) {
/*      */         
/*  501 */         if (null != aobj[i] && null != aobj[i + 1]) {
/*      */           
/*  503 */           int j = ((Integer)aobj[i]).intValue();
/*  504 */           byte byte0 = Byte.valueOf((String)aobj[i + 1]).byteValue();
/*  505 */           if (byte0 > 0 && byte0 < 32) {
/*      */             
/*  507 */             flag = true;
/*  508 */             int k = 32 - byte0;
/*  509 */             System.out.println("checkIPLimit: uid=" + integer + ",ip=" + integer1 + ",ipaddr=" + j + ",ipmask=" + byte0);
/*  510 */             if ((integer1.intValue() & -1 << k) == (j & -1 << k))
/*  511 */               return 0; 
/*      */           } 
/*      */         } 
/*  514 */         i += 2;
/*      */       } 
/*  516 */       return flag ? 2 : 0;
/*      */     }
/*  518 */     catch (Exception exception) {
/*      */       
/*  520 */       exception.printStackTrace(System.out);
/*      */       
/*  522 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean setIPLimit(Integer integer, Integer integer1, Byte byte1, Integer integer2, Byte byte2, Integer integer3, Byte byte3) {
/*      */     try {
/*  529 */       Object[] aobj = { integer, integer1, byte1.toString(), integer2, byte2.toString(), integer3, byte3.toString() };
/*      */ 
/*      */       
/*  532 */       return (handler.get("setiplimit").execute(aobj, "auth0") == 0);
/*      */     }
/*  534 */     catch (Exception exception) {
/*      */       
/*  536 */       exception.printStackTrace(System.out);
/*      */       
/*  538 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean enableIPLimit(Integer integer, boolean flag) {
/*      */     try {
/*  546 */       String s = flag ? new String("t") : new String("f");
/*  547 */       Object[] aobj = { integer, s };
/*      */ 
/*      */       
/*  550 */       return (handler.get("enableiplimit").execute(aobj, "auth0") == 0);
/*      */     }
/*  552 */     catch (Exception exception) {
/*      */       
/*  554 */       exception.printStackTrace(System.out);
/*      */       
/*  556 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean getLockStatus(Integer integer) {
/*      */     try {
/*  563 */       Object[] aobj = getIPLimit(integer);
/*  564 */       if (null != aobj && null != aobj[8] && ((String)aobj[8]).equals("t"))
/*  565 */         return true; 
/*  566 */       return false;
/*      */     }
/*  568 */     catch (Exception exception) {
/*      */       
/*  570 */       exception.printStackTrace(System.out);
/*      */       
/*  572 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean lockUser(Integer integer, boolean flag) {
/*      */     try {
/*  580 */       String s = flag ? new String("t") : new String("f");
/*  581 */       Object[] aobj = { integer, s };
/*      */ 
/*      */       
/*  584 */       return (handler.get("lockuser").execute(aobj, "auth0") == 0);
/*      */     }
/*  586 */     catch (Exception exception) {
/*      */       
/*  588 */       exception.printStackTrace(System.out);
/*      */       
/*  590 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] acquirePrivilegeByUidZid(Integer integer, Integer integer1) {
/*      */     try {
/*  597 */       return handler.get("acquirePrivilege").select("byUidZid").execute(new Object[] { integer, integer1 }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  601 */     catch (Exception exception) {
/*      */       
/*  603 */       exception.printStackTrace(System.out);
/*      */       
/*  605 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] acquirePrivilegeByUid(Integer integer) {
/*      */     try {
/*  612 */       return handler.get("acquirePrivilege").select("byUid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  616 */     catch (Exception exception) {
/*      */       
/*  618 */       exception.printStackTrace(System.out);
/*      */       
/*  620 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] acquirePrivilegeByZid(Integer integer) {
/*      */     try {
/*  627 */       return handler.get("acquirePrivilege").select("byZid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  631 */     catch (Exception exception) {
/*      */       
/*  633 */       exception.printStackTrace(System.out);
/*      */       
/*  635 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] acquirePrivilege() {
/*      */     try {
/*  642 */       return handler.get("acquirePrivilege").select("byAll").execute(new Object[0], "auth0");
/*      */     }
/*  644 */     catch (Exception exception) {
/*      */       
/*  646 */       exception.printStackTrace(System.out);
/*      */       
/*  648 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static int acquireUserCreatime(Integer integer) {
/*      */     try {
/*  655 */       Object[] aobj = handler.get("acquireUserCreatime").select("byUid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */       
/*  658 */       if (null != aobj && aobj.length > 0)
/*  659 */         return (int)(((Date)((Object[])aobj[0])[0]).getTime() / 1000L); 
/*  660 */       return 0;
/*      */     }
/*  662 */     catch (Exception exception) {
/*      */       
/*  664 */       exception.printStackTrace(System.out);
/*      */       
/*  666 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void addUserPriv(Integer integer, Integer integer1, Integer integer2) {
/*      */     try {
/*  673 */       Object[] aobj = { integer, integer1, integer2 };
/*      */ 
/*      */       
/*  676 */       handler.get("addUserPriv").execute(aobj, "auth0");
/*      */     }
/*  678 */     catch (Exception exception) {
/*      */       
/*  680 */       exception.printStackTrace(System.out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void delUserPriv(Integer integer, Integer integer1, Integer integer2) {
/*      */     try {
/*  688 */       Object[] aobj = { integer, integer1, integer2, new Integer(0) };
/*      */ 
/*      */       
/*  691 */       handler.get("delUserPriv").execute(aobj, "auth0");
/*      */     }
/*  693 */     catch (Exception exception) {
/*      */       
/*  695 */       exception.printStackTrace(System.out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void replaceUserPriv(Integer integer, Integer integer1, Set set) {
/*      */     try {
/*  703 */       Object[] aobj = { integer, integer1, new Integer(0), new Integer(1) };
/*      */ 
/*      */       
/*  706 */       handler.get("delUserPriv").execute(aobj, "auth0");
/*  707 */       aobj = new Object[] { integer, integer1, null };
/*      */ 
/*      */       
/*  710 */       for (Iterator<Integer> iterator = set.iterator(); iterator.hasNext(); handler.get("addUserPriv").execute(aobj, "auth0")) {
/*  711 */         aobj[2] = iterator.next();
/*      */       }
/*      */     }
/*  714 */     catch (Exception exception) {
/*      */       
/*  716 */       exception.printStackTrace(System.out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void delUserPriv(Integer integer, Integer integer1) {
/*      */     try {
/*  724 */       Object[] aobj = { integer, integer1, new Integer(0), new Integer(1) };
/*      */ 
/*      */       
/*  727 */       handler.get("delUserPriv").execute(aobj, "auth0");
/*      */     }
/*  729 */     catch (Exception exception) {
/*      */       
/*  731 */       exception.printStackTrace(System.out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void delUserPriv(Integer integer) {
/*      */     try {
/*  739 */       Object[] aobj = { integer, new Integer(0), new Integer(0), new Integer(2) };
/*      */ 
/*      */       
/*  742 */       handler.get("delUserPriv").execute(aobj, "auth0");
/*      */     }
/*  744 */     catch (Exception exception) {
/*      */       
/*  746 */       exception.printStackTrace(System.out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] acquireUserPrivilege(Integer integer, Integer integer1) {
/*      */     try {
/*  754 */       return handler.get("acquireUserPrivilege").select("byUidZid").execute(new Object[] { integer, integer1 }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  758 */     catch (Exception exception) {
/*      */       
/*  760 */       exception.printStackTrace(System.out);
/*      */       
/*  762 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUserForbidByName(String s) {
/*      */     try {
/*  769 */       return handler.get("acquireForbid").select("byName").execute(new Object[] { s.toLowerCase() }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  773 */     catch (Exception exception) {
/*      */       
/*  775 */       exception.printStackTrace(System.out);
/*      */       
/*  777 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] acquireForbid(Integer integer) {
/*      */     try {
/*  784 */       return handler.get("acquireForbid").select("byUid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  788 */     catch (Exception exception) {
/*      */       
/*  790 */       exception.printStackTrace(System.out);
/*      */       
/*  792 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean deleteTimeoutForbid(Integer integer) {
/*      */     try {
/*  799 */       Object[] aobj = { integer };
/*      */ 
/*      */       
/*  802 */       return (handler.get("deleteTimeoutForbid").execute(aobj, "auth0") == 0);
/*      */     }
/*  804 */     catch (Exception exception) {
/*      */       
/*  806 */       exception.printStackTrace(System.out);
/*      */       
/*  808 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean addForbid(Integer integer, Integer integer1, Integer integer2, byte[] abyte0, Integer integer3) {
/*      */     try {
/*  815 */       Object[] aobj = { integer, integer1, integer2, abyte0, integer3 };
/*      */ 
/*      */       
/*  818 */       return (handler.get("addForbid").execute(aobj, "auth0") == 0);
/*      */     }
/*  820 */     catch (Exception exception) {
/*      */       
/*  822 */       exception.printStackTrace(System.out);
/*      */       
/*  824 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUserOnlineInfo(Integer integer) {
/*      */     try {
/*  831 */       return handler.get("getUserOnlineInfo").select("byUid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  835 */     catch (Exception exception) {
/*      */       
/*  837 */       exception.printStackTrace(System.out);
/*      */       
/*  839 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void clearOnlineRecords(Integer integer, Integer integer1) {
/*      */     try {
/*  846 */       Object[] aobj = { integer, integer1 };
/*      */ 
/*      */       
/*  849 */       handler.get("clearonlinerecords").execute(aobj, "auth0");
/*      */     }
/*  851 */     catch (Exception exception) {
/*      */       
/*  853 */       exception.printStackTrace(System.out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean recordUserOnline(Object[] aobj, Integer integer, Integer integer1) {
/*      */     try {
/*  861 */       Object[] aobj1 = { integer, integer1, aobj[0], aobj[1], aobj[2] };
/*      */ 
/*      */       
/*  864 */       if (handler.get("recordonline").execute(aobj1, "auth0") == 0)
/*      */       {
/*  866 */         aobj[0] = aobj1[2];
/*  867 */         aobj[1] = aobj1[3];
/*  868 */         aobj[2] = aobj1[4];
/*  869 */         return true;
/*      */       }
/*      */     
/*  872 */     } catch (Exception exception) {
/*      */       
/*  874 */       exception.printStackTrace(System.out);
/*      */     } 
/*  876 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean recordUserOffline(Object[] aobj, Integer integer, Integer integer1) {
/*      */     try {
/*  883 */       Object[] aobj1 = { integer, integer1, aobj[0], aobj[1], aobj[2] };
/*      */ 
/*      */       
/*  886 */       if (handler.get("recordoffline").execute(aobj1, "auth0") == 0)
/*      */       {
/*  888 */         aobj[2] = aobj1[4];
/*  889 */         return true;
/*      */       }
/*      */     
/*  892 */     } catch (Exception exception) {
/*      */       
/*  894 */       exception.printStackTrace(System.out);
/*      */     } 
/*  896 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] getUserPoints(Integer integer) {
/*      */     try {
/*  903 */       return handler.get("getUserPoints").select("byuid").execute(new Object[] { integer }, "auth0");
/*      */ 
/*      */     
/*      */     }
/*  907 */     catch (Exception exception) {
/*      */       
/*  909 */       exception.printStackTrace(System.out);
/*      */       
/*  911 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] acquireRemainTime(Integer integer, Integer integer1) {
/*      */     try {
/*  920 */       Object[] aobj = { integer, integer1, new Integer(0), new Integer(0) };
/*      */ 
/*      */       
/*  923 */       handler.get("remaintime").execute(aobj, "auth0");
/*  924 */       Integer integer2 = new Integer(0);
/*  925 */       if (null != aobj[3] && ((Integer)aobj[3]).intValue() > 0)
/*  926 */         integer2 = (Integer)aobj[3]; 
/*  927 */       return new Object[] { aobj[2], integer2 };
/*      */ 
/*      */     
/*      */     }
/*  931 */     catch (Exception exception) {
/*      */       
/*  933 */       exception.printStackTrace(System.out);
/*      */       
/*  935 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean addUserPoint(Integer integer, Integer integer1, Integer integer2) {
/*      */     try {
/*  942 */       Object[] aobj = { integer, integer1, integer2 };
/*      */ 
/*      */       
/*  945 */       return (handler.get("adduserpoint").execute(aobj, "auth0") == 0);
/*      */     }
/*  947 */     catch (Exception exception) {
/*      */       
/*  949 */       exception.printStackTrace(System.out);
/*      */       
/*  951 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static int useCash(Integer integer, Integer integer1, Integer integer2, Integer integer3, Integer integer4, Integer integer5, Integer integer6) {
/*      */     try {
/*  959 */       Integer integer7 = new Integer(0);
/*  960 */       Object[] aobj = { integer, integer1, integer2, integer3, integer4, integer5, integer6, integer7 };
/*      */ 
/*      */       
/*  963 */       handler.get("usecash").execute(aobj, "auth0");
/*  964 */       return ((Integer)aobj[7]).intValue();
/*      */     }
/*  966 */     catch (Exception exception) {
/*      */       
/*  968 */       exception.printStackTrace(System.out);
/*      */       
/*  970 */       return -1;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashNow(Integer integer) {
/*      */     try {
/*  977 */       return handler.get("getusecashnow").select("bystatus").execute(new Object[] { integer });
/*      */ 
/*      */     
/*      */     }
/*  981 */     catch (Exception exception) {
/*      */       
/*  983 */       exception.printStackTrace(System.out);
/*      */       
/*  985 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashNow(Integer integer, Integer integer1) {
/*      */     try {
/*  992 */       return handler.get("getusecashnow").select("byuserzone").execute(new Object[] { integer, integer1 });
/*      */ 
/*      */     
/*      */     }
/*  996 */     catch (Exception exception) {
/*      */       
/*  998 */       exception.printStackTrace(System.out);
/*      */       
/* 1000 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashNow(Integer integer, Integer integer1, Integer integer2) {
/*      */     try {
/* 1007 */       return handler.get("getusecashnow").select("byuserzonesn").execute(new Object[] { integer, integer1, integer2 });
/*      */ 
/*      */     
/*      */     }
/* 1011 */     catch (Exception exception) {
/*      */       
/* 1013 */       exception.printStackTrace(System.out);
/*      */       
/* 1015 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashNowByUser(Integer integer) {
/*      */     try {
/* 1022 */       return handler.get("getusecashnow").select("byuser").execute(new Object[] { integer });
/*      */ 
/*      */     
/*      */     }
/* 1026 */     catch (Exception exception) {
/*      */       
/* 1028 */       exception.printStackTrace(System.out);
/*      */       
/* 1030 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashLog(Integer integer, Integer integer1) {
/*      */     try {
/* 1037 */       return handler.get("getusecashlog").select("byuserzone").execute(new Object[] { integer, integer1 });
/*      */ 
/*      */     
/*      */     }
/* 1041 */     catch (Exception exception) {
/*      */       
/* 1043 */       exception.printStackTrace(System.out);
/*      */       
/* 1045 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashLog(Integer integer, Integer integer1, Integer integer2) {
/*      */     try {
/* 1052 */       return handler.get("getusecashlog").select("byuserzonesn").execute(new Object[] { integer, integer1, integer2 });
/*      */ 
/*      */     
/*      */     }
/* 1056 */     catch (Exception exception) {
/*      */       
/* 1058 */       exception.printStackTrace(System.out);
/*      */       
/* 1060 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object[] getUseCashLogByUser(Integer integer) {
/*      */     try {
/* 1067 */       return handler.get("getusecashlog").select("byuser").execute(new Object[] { integer });
/*      */ 
/*      */     
/*      */     }
/* 1071 */     catch (Exception exception) {
/*      */       
/* 1073 */       exception.printStackTrace(System.out);
/*      */       
/* 1075 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String toHexString(byte[] abyte0) {
/* 1080 */     StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
/* 1081 */     for (int i = 0; i < abyte0.length; i++) {
/*      */       
/* 1083 */       byte byte0 = abyte0[i];
/* 1084 */       int j = byte0 >> 4 & 0xF;
/* 1085 */       stringbuffer.append((char)((j < 10) ? (48 + j) : (97 + j - 10)));
/* 1086 */       j = byte0 & 0xF;
/* 1087 */       stringbuffer.append((char)((j < 10) ? (48 + j) : (97 + j - 10)));
/*      */     } 
/*      */     
/* 1090 */     return stringbuffer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void main(String[] args) {
/*      */     try {
/* 1097 */       parser.parse(new FileInputStream(args[0]));
/* 1098 */       System.out.println("Done!");
/* 1099 */       Object[] aobj = acquireIdPasswd("zengpan");
/* 1100 */       Integer integer = (Integer)aobj[0];
/* 1101 */       byte[] abyte0 = (byte[])aobj[1];
/* 1102 */       System.out.println("uid=" + integer + ",passwd=" + abyte0);
/* 1103 */       for (int i = 0; i < abyte0.length; i++) {
/* 1104 */         System.out.print(Byte.toString(abyte0[i]));
/*      */       }
/* 1106 */       System.out.println("");
/*      */     }
/* 1108 */     catch (Exception exception) {
/*      */       
/* 1110 */       exception.printStackTrace();
/*      */     } 
/*      */   }
/*      */   
/* 1114 */   private static final DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd");
/* 1115 */   private static final DateFormat datefmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 1116 */   private static final DateFormat datefmt3 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
/*      */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\com\goldhuman\account\storage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */