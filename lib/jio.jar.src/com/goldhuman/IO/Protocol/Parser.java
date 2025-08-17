package com.goldhuman.IO.Protocol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Parser {
  private static SAXParser sap;
  
  private static final URL config = Parser.class.getResource("/config.xml");
  
  public static final String default_package = "protocol";
  
  private static PrintStream find_file(String paramString) {
    String str = "protocol/" + paramString;
    File file = new File(str + ".java");
    if (file.exists()) {
      if (!(new File(str + ".class")).exists())
        System.err.println("Compile " + str + ".java first..."); 
      return null;
    } 
    System.out.println("Generate " + file.getName());
    try {
      return new PrintStream(new FileOutputStream(file));
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  private static void generate_import(PrintStream paramPrintStream) {
    paramPrintStream.println("package protocol;\n");
    paramPrintStream.println("import com.goldhuman.Common.*;");
    paramPrintStream.println("import com.goldhuman.Common.Marshal.*;");
    paramPrintStream.println("import com.goldhuman.Common.Security.*;");
    paramPrintStream.println("import com.goldhuman.IO.Protocol.*;");
    paramPrintStream.println("");
  }
  
  private static void generate_datamember(PrintStream paramPrintStream, Vector<Variable> paramVector) {
    int i = paramVector.size();
    for (byte b = 0; b < i; b++) {
      Variable variable = paramVector.get(b);
      if (variable.type.endsWith("Vector")) {
        paramPrintStream.println("\tpublic DataVector\t" + variable.name + ";");
      } else {
        paramPrintStream.println("\tpublic " + variable.type + "\t" + variable.name + ";");
      } 
    } 
    paramPrintStream.println("");
  }
  
  private static void generate_marshalable(PrintStream paramPrintStream, Vector<Variable> paramVector) {
    paramPrintStream.println("\tpublic OctetsStream marshal(OctetsStream os)");
    paramPrintStream.println("\t{");
    int i = paramVector.size();
    byte b;
    for (b = 0; b < i; b++) {
      Variable variable = paramVector.get(b);
      paramPrintStream.println("\t\tos.marshal(" + variable.name + ");");
    } 
    paramPrintStream.println("\t\treturn os;\n\t}");
    paramPrintStream.println("");
    paramPrintStream.println("\tpublic OctetsStream unmarshal(OctetsStream os) throws MarshalException");
    paramPrintStream.println("\t{");
    for (b = 0; b < i; b++) {
      Variable variable = paramVector.get(b);
      String str = variable.type;
      if (str.compareTo("int") == 0 || str.compareTo("short") == 0 || str.compareTo("long") == 0 || str.compareTo("byte") == 0 || str.compareTo("float") == 0 || str.compareTo("double") == 0) {
        paramPrintStream.println("\t\t" + variable.name + " = os.unmarshal_" + str + "();");
      } else {
        paramPrintStream.println("\t\tos.unmarshal(" + variable.name + ");");
      } 
    } 
    paramPrintStream.println("\t\treturn os;\n\t}");
    paramPrintStream.println("");
  }
  
  private static void generate_defaultconstructor(PrintStream paramPrintStream, String paramString, Vector<Variable> paramVector) {
    paramPrintStream.println("\tpublic " + paramString + "()");
    paramPrintStream.println("\t{");
    int i = paramVector.size();
    for (byte b = 0; b < i; b++) {
      Variable variable = paramVector.get(b);
      String str1 = variable.type;
      String str2 = variable.name;
      if (str1.compareTo("int") != 0 && str1.compareTo("short") != 0 && str1.compareTo("long") != 0 && str1.compareTo("byte") != 0 && str1.compareTo("float") != 0 && str1.compareTo("double") != 0)
        if (variable.type.endsWith("Vector")) {
          paramPrintStream.println("\t\t" + str2 + " = new DataVector(" + "new " + str1.replaceAll("Vector", "") + "());");
        } else {
          paramPrintStream.println("\t\t" + str2 + " = new " + str1 + "();");
        }  
    } 
    paramPrintStream.println("\t}");
    paramPrintStream.println("");
  }
  
  private static void generate_cloneable(PrintStream paramPrintStream, String paramString, Vector<Variable> paramVector) {
    paramPrintStream.println("\tpublic Object clone()");
    paramPrintStream.println("\t{");
    paramPrintStream.println("\t\ttry");
    paramPrintStream.println("\t\t{");
    paramPrintStream.println("\t\t\t" + paramString + " o = (" + paramString + ")super.clone();");
    int i = paramVector.size();
    for (byte b = 0; b < i; b++) {
      Variable variable = paramVector.get(b);
      String str1 = variable.type;
      String str2 = variable.name;
      if (str1.compareTo("int") != 0 && str1.compareTo("short") != 0 && str1.compareTo("long") != 0 && str1.compareTo("byte") != 0 && str1.compareTo("float") != 0 && str1.compareTo("double") != 0)
        if (variable.type.endsWith("Vector")) {
          paramPrintStream.println("\t\t\to." + str2 + " = (DataVector)" + str2 + ".clone();");
        } else {
          paramPrintStream.println("\t\t\to." + str2 + " = (" + str1 + ")" + str2 + ".clone();");
        }  
    } 
    paramPrintStream.println("\t\t\treturn o;");
    paramPrintStream.println("\t\t}");
    paramPrintStream.println("\t\tcatch (Exception e) { }");
    paramPrintStream.println("\t\treturn null;\n\t}");
    paramPrintStream.println("");
  }
  
  private static void generate_dataaccess(PrintStream paramPrintStream, Vector<Variable> paramVector) {
    int i = paramVector.size();
    for (byte b = 0; b < i; b++) {
      Variable variable = paramVector.get(b);
      String str1 = variable.type;
      String str2 = variable.name;
      paramPrintStream.println("");
      paramPrintStream.println("\tpublic void Set" + str2 + "(" + str1 + " " + str2 + ") { this." + str2 + " = " + str2 + "; }");
      paramPrintStream.println("");
    } 
  }
  
  private static void protocol_generate(String paramString, Vector paramVector) {
    PrintStream printStream = find_file(paramString);
    if (printStream == null)
      return; 
    generate_import(printStream);
    printStream.println("public final class " + paramString + " extends Protocol");
    printStream.println("{");
    generate_datamember(printStream, paramVector);
    generate_defaultconstructor(printStream, paramString, paramVector);
    generate_marshalable(printStream, paramVector);
    generate_cloneable(printStream, paramString, paramVector);
    printStream.println("\tpublic void Process(Manager manager, Session session) throws ProtocolException");
    printStream.println("\t{");
    printStream.println("\t}");
    printStream.println("");
    printStream.println("}");
    printStream.close();
  }
  
  private static void rpcdata_generate(String paramString, Vector paramVector) {
    PrintStream printStream = find_file(paramString);
    if (printStream == null)
      return; 
    generate_import(printStream);
    printStream.println("public final class " + paramString + " extends Rpc.Data");
    printStream.println("{");
    generate_datamember(printStream, paramVector);
    generate_defaultconstructor(printStream, paramString, paramVector);
    generate_marshalable(printStream, paramVector);
    generate_cloneable(printStream, paramString, paramVector);
    printStream.println("}");
    printStream.close();
  }
  
  private static void rpc_generate(String paramString1, String paramString2, String paramString3) {
    PrintStream printStream = find_file(paramString1);
    if (printStream == null)
      return; 
    generate_import(printStream);
    printStream.println("public final class " + paramString1 + " extends Rpc");
    printStream.println("{");
    printStream.println("\tpublic void Server(Data argument, Data result) throws ProtocolException");
    printStream.println("\t{");
    printStream.println("\t\t" + paramString2 + " arg = (" + paramString2 + ")argument;");
    printStream.println("\t\t" + paramString3 + " res = (" + paramString3 + ")result;");
    printStream.println("\t}");
    printStream.println("");
    printStream.println("\tpublic void Client(Data argument, Data result) throws ProtocolException");
    printStream.println("\t{");
    printStream.println("\t\t" + paramString2 + " arg = (" + paramString2 + ")argument;");
    printStream.println("\t\t" + paramString3 + " res = (" + paramString3 + ")result;");
    printStream.println("\t}");
    printStream.println("");
    printStream.println("\tpublic void OnTimeout()");
    printStream.println("\t{");
    printStream.println("\t}");
    printStream.println("");
    printStream.println("}");
    printStream.close();
  }
  
  protected static void ParseProtocol(final Map map) throws Exception {
    sap.parse(config.openStream(), new DefaultHandler() {
          private boolean parsing = false;
          
          private String name;
          
          private String type;
          
          private String class_name;
          
          private String maxsize;
          
          private String priority;
          
          private Vector variables;
          
          public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) {
            try {
              if (param1String3.compareTo("protocol") == 0) {
                this.parsing = true;
                this.variables = new Vector();
                this.name = param1Attributes.getValue("name");
                this.type = param1Attributes.getValue("type");
                this.class_name = param1Attributes.getValue("class");
                this.maxsize = param1Attributes.getValue("maxsize");
                this.priority = param1Attributes.getValue("priority");
                if (this.priority == null)
                  this.priority = param1Attributes.getValue("prior"); 
              } else if (this.parsing && param1String3.compareTo("variable") == 0) {
                String str = param1Attributes.getValue("type");
                this.variables.add(new Parser.Variable(param1Attributes.getValue("name"), str));
              } 
            } catch (Exception exception) {
              exception.printStackTrace();
            } 
          }
          
          public void endElement(String param1String1, String param1String2, String param1String3) {
            if (this.parsing && param1String3.compareTo("protocol") == 0) {
              this.parsing = false;
              try {
                Protocol protocol = (Protocol)Class.forName("protocol." + ((this.class_name == null) ? this.name : this.class_name)).newInstance();
                protocol.type = Integer.parseInt(this.type);
                protocol.size_policy = (this.maxsize == null) ? 0 : Integer.parseInt(this.maxsize);
                protocol.prior_policy = (this.priority == null) ? 0 : Integer.parseInt(this.priority);
                if (map.put(this.name.toUpperCase(), protocol) != null)
                  System.err.println("Duplicate protocol name " + this.name); 
                if (map.put(this.type, protocol) != null)
                  System.err.println("Duplicate protocol type " + this.type); 
              } catch (Exception exception) {
                Parser.protocol_generate(this.name, this.variables);
              } 
              this.name = null;
              this.type = null;
              this.class_name = null;
              this.maxsize = null;
              this.priority = null;
              this.variables = null;
            } 
          }
        });
  }
  
  protected static void ParseState(final Map map) throws Exception {
    final HashMap<Object, Object> typemap = new HashMap<Object, Object>();
    sap.parse(config.openStream(), new DefaultHandler() {
          public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) {
            if (param1String3.compareTo("protocol") != 0 && param1String3.compareTo("rpc") != 0)
              return; 
            try {
              typemap.put(param1Attributes.getValue("name").trim(), param1Attributes.getValue("type").trim());
            } catch (Exception exception) {
              exception.printStackTrace();
            } 
          }
        });
    sap.parse(config.openStream(), new DefaultHandler() {
          private String state_name = null;
          
          private State state = null;
          
          public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) {
            if (param1String3.compareTo("state") == 0)
              try {
                this.state_name = param1Attributes.getValue("name").trim().toUpperCase();
                try {
                  this.state = new State(1000L * Long.parseLong(param1Attributes.getValue("timeout").trim()));
                } catch (Exception exception) {
                  this.state = new State(0L);
                } 
              } catch (Exception exception) {} 
            if (this.state_name != null && param1String3.compareTo("proto") == 0)
              this.state.AddProtocolType((String)typemap.get(param1Attributes.getValue("name"))); 
          }
          
          public void endElement(String param1String1, String param1String2, String param1String3) {
            if (param1String3.compareTo("state") == 0) {
              if (this.state_name != null)
                map.put(this.state_name, this.state); 
              this.state_name = null;
            } 
          }
        });
  }
  
  private static final Map ParseRpcData() throws Exception {
    final HashMap<Object, Object> typemap = new HashMap<Object, Object>();
    sap.parse(config.openStream(), new DefaultHandler() {
          private boolean parsing = false;
          
          private String rpcdataName = null;
          
          private Vector variables;
          
          public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) {
            try {
              if (param1String3.compareTo("rpcdata") == 0) {
                this.parsing = true;
                this.variables = new Vector();
                this.rpcdataName = param1Attributes.getValue("name");
              } else if (this.parsing && param1String3.compareTo("variable") == 0) {
                String str = param1Attributes.getValue("type");
                this.variables.add(new Parser.Variable(param1Attributes.getValue("name"), str));
              } 
            } catch (Exception exception) {
              exception.printStackTrace();
            } 
          }
          
          public void endElement(String param1String1, String param1String2, String param1String3) {
            if (this.parsing && param1String3.compareTo("rpcdata") == 0) {
              this.parsing = false;
              try {
                typemap.put(this.rpcdataName, Class.forName("protocol." + this.rpcdataName).newInstance());
              } catch (Exception exception) {
                Parser.rpcdata_generate(this.rpcdataName, this.variables);
              } 
              this.rpcdataName = null;
              this.variables = null;
            } 
          }
        });
    return hashMap;
  }
  
  protected static void ParseRpc(final Map map) throws Exception {
    final Map typemap = ParseRpcData();
    sap.parse(config.openStream(), new DefaultHandler() {
          public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) {
            if (param1String3.compareTo("rpc") == 0)
              try {
                String str1 = param1Attributes.getValue("name");
                String str2 = param1Attributes.getValue("type");
                String str3 = param1Attributes.getValue("argument");
                String str4 = param1Attributes.getValue("result");
                String str5 = param1Attributes.getValue("maxsize");
                String str6 = param1Attributes.getValue("timeout");
                String str7 = param1Attributes.getValue("priority");
                if (str7 == null)
                  str7 = param1Attributes.getValue("prior"); 
                Rpc rpc = null;
                try {
                  rpc = (Rpc)Class.forName("protocol." + str1).newInstance();
                } catch (Exception exception) {
                  Parser.rpc_generate(str1, str3, str4);
                  return;
                } 
                rpc.argument = (Rpc.Data)typemap.get(str3);
                rpc.result = (Rpc.Data)typemap.get(str4);
                rpc.type = Integer.parseInt(str2);
                rpc.prior_policy = (str7 == null) ? 0 : Integer.parseInt(str7);
                rpc.time_policy = (str6 == null) ? 25000L : (1000L * Long.parseLong(str6));
                rpc.size_policy = (str5 == null) ? 0 : Integer.parseInt(str5);
                if (map.put(str1.toUpperCase(), rpc) != null)
                  System.err.println("Duplicate protocol name " + str1); 
                if (map.put(str2, rpc) != null)
                  System.err.println("Duplicate protocol type " + str2); 
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
          }
        });
  }
  
  static {
    try {
      sap = SAXParserFactory.newInstance().newSAXParser();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private static class Variable {
    public String name;
    
    public String type;
    
    public Variable(String param1String1, String param1String2) {
      this.name = param1String1;
      this.type = param1String2;
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\IO\Protocol\Parser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */