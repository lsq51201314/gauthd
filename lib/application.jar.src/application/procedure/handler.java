/*     */ package application.procedure;
/*     */ 
/*     */ import application.procedure.parameter.handler;
/*     */ import com.goldhuman.xml.xmlobject;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class handler
/*     */   extends xmlobject
/*     */ {
/*     */   private String sql_clause;
/*     */   private handler[] parameter;
/*     */   private String operate;
/*  27 */   private Set conn_set = new HashSet();
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setattr(Attributes attributes) {
/*  32 */     super.setattr(attributes);
/*  33 */     this.conn_set.addAll(Arrays.asList(attributes.getValue("connection").split("[ \n\t,]+")));
/*  34 */     this.operate = attributes.getValue("operate");
/*  35 */     instance.put(this.name, this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void action() {
/*  40 */     xmlobject[] axmlobject = this.parent.children;
/*  41 */     for (Iterator<String> iterator = this.conn_set.iterator(); iterator.hasNext(); ) {
/*     */       
/*  43 */       String s = iterator.next();
/*  44 */       boolean flag = false;
/*  45 */       if (application.handler.debug)
/*  46 */         System.err.println("Procedure '" + this.name + "' Bind '" + s + "'"); 
/*  47 */       int k = 0;
/*     */ 
/*     */       
/*  50 */       while (k < axmlobject.length) {
/*     */         
/*  52 */         if (axmlobject[k] instanceof application.connection.handler && (axmlobject[k]).name.compareTo(s) == 0) {
/*     */           
/*  54 */           flag = true;
/*     */           break;
/*     */         } 
/*  57 */         k++;
/*     */       } 
/*  59 */       if (!flag) {
/*     */         
/*  61 */         System.err.println("In Procedure '" + this.name + "' Connection '" + s + "' Miss");
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*  66 */     axmlobject = this.children;
/*  67 */     HashSet<String> hashset = new HashSet();
/*  68 */     Vector<xmlobject> vector = new Vector();
/*  69 */     for (int i = 0; i < axmlobject.length; i++) {
/*     */       
/*  71 */       if (axmlobject[i] instanceof handler) {
/*     */         
/*  73 */         axmlobject[i].action();
/*  74 */         if (hashset.add((axmlobject[i]).name)) {
/*  75 */           vector.add(axmlobject[i]);
/*     */         } else {
/*  77 */           System.err.println("In Procedure '" + this.name + "' Duplicate parameter '" + (axmlobject[i]).name + "'");
/*     */         } 
/*     */       } 
/*  80 */     }  this.parameter = new handler[vector.size()];
/*  81 */     for (int j = 0; j < vector.size(); j++) {
/*  82 */       this.parameter[j] = (handler)vector.get(j);
/*     */     }
/*  84 */     if (application.handler.debug)
/*  85 */       System.err.println(this); 
/*  86 */     if (this.operate != null) {
/*     */       
/*  88 */       if (this.operate.compareTo("create") == 0)
/*  89 */         Create(); 
/*  90 */       if (this.operate.compareTo("drop") == 0)
/*  91 */         Drop(); 
/*  92 */       if (this.operate.compareTo("replace") == 0) {
/*     */         
/*  94 */         Drop();
/*  95 */         Create();
/*     */       } 
/*     */     } 
/*     */     
/*  99 */     StringBuffer stringbuffer = new StringBuffer("{call ");
/* 100 */     stringbuffer.append(this.name).append("(");
/* 101 */     for (int l = 0; l < this.parameter.length; l++) {
/* 102 */       stringbuffer.append("?,");
/*     */     }
/* 104 */     stringbuffer.setCharAt(stringbuffer.lastIndexOf(","), ')');
/* 105 */     this.sql_clause = stringbuffer.append("}").toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 110 */     StringBuffer stringbuffer = new StringBuffer("CREATE PROCEDURE ");
/* 111 */     stringbuffer.append(this.name).append("(");
/* 112 */     for (int i = 0; i < this.parameter.length; i++) {
/*     */       
/* 114 */       if ((this.parameter[i]).out) {
/* 115 */         stringbuffer.append("out ");
/*     */       } else {
/* 117 */         stringbuffer.append("in ");
/* 118 */       }  stringbuffer.append((this.parameter[i]).name).append(" ").append((this.parameter[i]).sql_type.toUpperCase());
/* 119 */       stringbuffer.append(", ");
/*     */     } 
/*     */     
/* 122 */     stringbuffer.deleteCharAt(stringbuffer.lastIndexOf(", "));
/* 123 */     stringbuffer.append(")\n");
/* 124 */     stringbuffer.append("BEGIN\n\t").append(this.content.trim()).append("\nEND");
/* 125 */     return stringbuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void Drop() {
/* 131 */     for (Iterator<String> iterator = this.conn_set.iterator(); iterator.hasNext(); application.connection.handler.put(connection)) {
/*     */       
/* 133 */       Connection connection = application.connection.handler.get(iterator.next());
/*     */       
/*     */       try {
/* 136 */         Statement statement = connection.createStatement();
/* 137 */         statement.executeUpdate("DROP PROCEDURE " + this.name);
/* 138 */         statement.close();
/*     */       }
/* 140 */       catch (Exception exception) {
/*     */         
/* 142 */         exception.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void Create() {
/* 151 */     for (Iterator<String> iterator = this.conn_set.iterator(); iterator.hasNext(); application.connection.handler.put(connection)) {
/*     */       
/* 153 */       Connection connection = application.connection.handler.get(iterator.next());
/*     */       
/*     */       try {
/* 156 */         Statement statement = connection.createStatement();
/* 157 */         statement.executeUpdate(toString());
/* 158 */         statement.close();
/*     */       }
/* 160 */       catch (Exception exception) {
/*     */         
/* 162 */         exception.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static handler get(String s) {
/* 170 */     return (handler)instance.get(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int execute(Object[] aobj, String s) throws Exception {
/* 178 */     if (aobj.length != this.parameter.length)
/* 179 */       throw new SQLException("Parameter number error"); 
/* 180 */     if (s != null && !this.conn_set.contains(s))
/* 181 */       throw new SQLException("Connection '" + s + "' NOT Match"); 
/* 182 */     boolean flag = false;
/* 183 */     Connection connection = application.connection.handler.get(s);
/* 184 */     CallableStatement callablestatement = null;
/* 185 */     int i = 0;
/*     */     
/*     */     try {
/* 188 */       System.out.println("Prepare procedure call:" + this.sql_clause);
/* 189 */       callablestatement = connection.prepareCall(this.sql_clause);
/*     */       
/* 191 */       for (int j = 0; j < aobj.length; j++) {
/*     */         
/* 193 */         callablestatement.setObject(j + 1, aobj[j]);
/* 194 */         if ((this.parameter[j]).out)
/* 195 */           callablestatement.registerOutParameter(j + 1, (this.parameter[j]).out_type); 
/*     */       } 
/* 197 */       callablestatement.execute();
/*     */       
/* 199 */       for (int k = 0; k < aobj.length; k++) {
/* 200 */         if ((this.parameter[k]).out) {
/* 201 */           aobj[k] = callablestatement.getObject(k + 1);
/*     */         }
/*     */       } 
/* 204 */     } catch (Exception exception1) {
/*     */       
/* 206 */       throw exception1;
/*     */     } 
/*     */     
/*     */     try {
/* 210 */       if (callablestatement != null) {
/* 211 */         callablestatement.close();
/*     */       }
/* 213 */     } catch (Exception exception) {}
/*     */     
/*     */     try {
/* 216 */       application.connection.handler.put(connection);
/*     */     }
/* 218 */     catch (Exception exception2) {}
/*     */     
/*     */     try {
/* 221 */       if (callablestatement != null) {
/* 222 */         callablestatement.close();
/*     */       }
/* 224 */     } catch (Exception exception3) {}
/*     */     
/*     */     try {
/* 227 */       application.connection.handler.put(connection);
/*     */     }
/* 229 */     catch (Exception exception2) {}
/* 230 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int execute(Object[] aobj) throws Exception {
/* 236 */     return execute(aobj, (String)null);
/*     */   }
/*     */   
/* 239 */   private static Map instance = new HashMap<Object, Object>();
/*     */ }


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\application\procedure\handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */