package protocol;

public interface EnthrallmentMBean {
  int getCountOnlines();
  
  int getCountPlayers();
  
  int getCountTimeouts();
  
  int getCapacityTimeouts();
  
  long getTotalTriggers();
  
  long getTotalLengths();
  
  int getTriggerTimes();
  
  int getMaxTriggers();
  
  int getZeroTriggers();
  
  void setEnable(boolean paramBoolean);
  
  boolean getEnable();
  
  void setAlert(boolean paramBoolean);
  
  boolean getAlert();
  
  void login(int paramInt1, int paramInt2, int paramInt3);
  
  void logout(int paramInt1, int paramInt2);
  
  void remove(int paramInt1, int paramInt2);
  
  String find(int paramInt1, int paramInt2);
  
  void clear(String paramString);
  
  void timeoutTrimToSize();
  
  void dumpTimeout(String paramString);
  
  void dumpPlayers(String paramString);
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\EnthrallmentMBean.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */