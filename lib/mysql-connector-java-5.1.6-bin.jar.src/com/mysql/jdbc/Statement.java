package com.mysql.jdbc;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;

public interface Statement extends Statement {
  void enableStreamingResults() throws SQLException;
  
  void disableStreamingResults() throws SQLException;
  
  void setLocalInfileInputStream(InputStream paramInputStream);
  
  InputStream getLocalInfileInputStream();
  
  void setPingTarget(PingTarget paramPingTarget);
}


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\Statement.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */