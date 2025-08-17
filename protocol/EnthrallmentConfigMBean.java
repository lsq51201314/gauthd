package protocol;

public interface EnthrallmentConfigMBean {
  int getPlay100Time();
  
  void setPlay100Time(int paramInt);
  
  int getPlay50Time();
  
  void setPlay50Time(int paramInt);
  
  int getRestTime();
  
  void setRestTime(int paramInt);
  
  int getPlay100Alert();
  
  void setPlay100Alert(int paramInt);
  
  int getPlay50Alert();
  
  void setPlay50Alert(int paramInt);
  
  int getPlay0Alert();
  
  void setPlay0Alert(int paramInt);
  
  int getPrecision();
  
  void setPrecision(int paramInt);
  
  String getRestMode();
  
  void setRestMode(String paramString);
  
  String getMapZoneGame();
  
  void putMapZoneGame(int paramInt1, int paramInt2);
  
  void removeMapZoneGame(int paramInt);
  
  void clearMapZoneGame();
  
  String getConsole();
  
  void setConsole(String paramString);
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\EnthrallmentConfigMBean.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */