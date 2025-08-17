package com.goldhuman.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public final class Conf {
  private static Conf instance = new Conf();
  
  private File conffile;
  
  private long mtime;
  
  private HashMap confhash = new HashMap<Object, Object>();
  
  private void parse(BufferedReader paramBufferedReader) throws IOException {
    String str = null;
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    this.confhash.clear();
    while (paramBufferedReader.ready()) {
      String str1 = paramBufferedReader.readLine().trim();
      if (str1.length() == 0)
        continue; 
      char c = str1.charAt(0);
      if (c == '#' || c == ';')
        continue; 
      if (c == '[') {
        str1 = str1.substring(1, str1.indexOf(']')).trim();
        if (str != null) {
          this.confhash.put(str, hashMap);
          hashMap = new HashMap<Object, Object>();
        } 
        str = str1;
        continue;
      } 
      String[] arrayOfString = str1.split("=", 2);
      hashMap.put(arrayOfString[0].trim(), arrayOfString[1].trim());
    } 
    if (str != null)
      this.confhash.put(str, hashMap); 
  }
  
  private void reload() {
    try {
      for (long l = this.conffile.lastModified(); l != this.mtime; l = this.conffile.lastModified()) {
        this.mtime = l;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(this.conffile));
        parse(bufferedReader);
        bufferedReader.close();
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public synchronized String find(String paramString1, String paramString2) {
    HashMap hashMap = (HashMap)this.confhash.get(paramString1);
    if (hashMap != null) {
      String str = (String)hashMap.get(paramString2);
      if (str != null)
        return new String(str); 
    } 
    return new String();
  }
  
  public static synchronized Conf GetInstance(String paramString) {
    File file = new File(paramString);
    if (file.isFile()) {
      instance.conffile = file;
      instance.reload();
    } 
    return instance;
  }
  
  public static synchronized Conf GetInstance(URL paramURL) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramURL.openStream()));
      instance.parse(bufferedReader);
      bufferedReader.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return instance;
  }
  
  public static synchronized Conf GetInstance() {
    return instance;
  }
  
  public static void main(String[] paramArrayOfString) {
    GetInstance(paramArrayOfString[0]);
    System.out.println(GetInstance().find(paramArrayOfString[1], paramArrayOfString[2]));
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Conf.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */