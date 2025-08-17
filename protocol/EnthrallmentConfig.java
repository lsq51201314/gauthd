package protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EnthrallmentConfig implements EnthrallmentConfigMBean, Serializable {
  private int precision = 60000;
  
  private int PLAY_100_TIME = 10800000;
  
  private int PLAY_50_TIME = 7200000;
  
  private int REST_TIME = 18000000;
  
  private int PLAY_100_ALERT = 3600000;
  
  private int PLAY_50_ALERT = 1800000;
  
  private int PLAY_0_ALERT = 600000;
  
  private String restmode = "china";
  
  private Map<Integer, Integer> map_zonegame = new HashMap<Integer, Integer>();
  
  public static final int PLAY_100 = 1;
  
  public static final int PLAY_50 = 2;
  
  public static final int PLAY_0 = 3;
  
  void assertPositive(int paramInt) {
    if (paramInt <= 0)
      throw new RuntimeException("Positive"); 
  }
  
  public int getPlay100Time() {
    return this.PLAY_100_TIME;
  }
  
  public void setPlay100Time(int paramInt) {
    assertPositive(paramInt);
    this.PLAY_100_TIME = paramInt;
  }
  
  public int getPlay50Time() {
    return this.PLAY_50_TIME;
  }
  
  public void setPlay50Time(int paramInt) {
    assertPositive(paramInt);
    this.PLAY_50_TIME = paramInt;
  }
  
  public int getRestTime() {
    return this.REST_TIME;
  }
  
  public void setRestTime(int paramInt) {
    assertPositive(paramInt);
    this.REST_TIME = paramInt;
  }
  
  public int getPlay100Alert() {
    return this.PLAY_100_ALERT;
  }
  
  public void setPlay100Alert(int paramInt) {
    assertPositive(paramInt);
    this.PLAY_100_ALERT = paramInt;
  }
  
  public int getPlay50Alert() {
    return this.PLAY_50_ALERT;
  }
  
  public void setPlay50Alert(int paramInt) {
    assertPositive(paramInt);
    this.PLAY_50_ALERT = paramInt;
  }
  
  public int getPlay0Alert() {
    return this.PLAY_0_ALERT;
  }
  
  public void setPlay0Alert(int paramInt) {
    assertPositive(paramInt);
    this.PLAY_0_ALERT = paramInt;
  }
  
  public int getPrecision() {
    return this.precision;
  }
  
  public void setPrecision(int paramInt) {
    assertPositive(paramInt);
    this.precision = paramInt;
  }
  
  public String getRestMode() {
    return this.restmode;
  }
  
  public void setRestMode(String paramString) {
    this.restmode = paramString;
  }
  
  public String getMapZoneGame() {
    return this.map_zonegame.toString();
  }
  
  public void putMapZoneGame(int paramInt1, int paramInt2) {
    this.map_zonegame.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
  }
  
  public void removeMapZoneGame(int paramInt) {
    this.map_zonegame.remove(Integer.valueOf(paramInt));
  }
  
  public void clearMapZoneGame() {
    this.map_zonegame.clear();
  }
  
  public String getConsole() {
    return "";
  }
  
  public void setConsole(String paramString) {
    Enthrallment enthrallment = Enthrallment.getInstance();
    if (paramString.equals("apply")) {
      enthrallment.apply(new EnthrallmentConfig(this));
    } else if (paramString.equals("sync")) {
      enthrallment.getConfig(this);
    } else if (!paramString.equals("save")) {
      throw new RuntimeException("unknown '" + paramString + "'");
    } 
  }
  
  public EnthrallmentConfig() {}
  
  public EnthrallmentConfig(EnthrallmentConfig paramEnthrallmentConfig) {
    assign(paramEnthrallmentConfig);
  }
  
  public void assign(EnthrallmentConfig paramEnthrallmentConfig) {
    this.precision = paramEnthrallmentConfig.precision;
    this.PLAY_100_TIME = paramEnthrallmentConfig.PLAY_100_TIME;
    this.PLAY_50_TIME = paramEnthrallmentConfig.PLAY_50_TIME;
    this.REST_TIME = paramEnthrallmentConfig.REST_TIME;
    this.PLAY_100_ALERT = paramEnthrallmentConfig.PLAY_100_ALERT;
    this.PLAY_50_ALERT = paramEnthrallmentConfig.PLAY_50_ALERT;
    this.PLAY_0_ALERT = paramEnthrallmentConfig.PLAY_0_ALERT;
    this.restmode = paramEnthrallmentConfig.restmode;
    this.map_zonegame.clear();
    this.map_zonegame.putAll(paramEnthrallmentConfig.map_zonegame);
  }
  
  public int getMaxTimeout() {
    int i = 0;
    if (getPlay100Time() > i)
      i = getPlay100Time(); 
    if (getPlay50Time() > i)
      i = getPlay50Time(); 
    if (getRestTime() > i)
      i = getRestTime(); 
    if (getPlay100Alert() > i)
      i = getPlay100Alert(); 
    if (getPlay50Alert() > i)
      i = getPlay50Alert(); 
    if (getPlay0Alert() > i)
      i = getPlay0Alert(); 
    return i;
  }
  
  public int playtime2state(int paramInt, boolean paramBoolean) {
    byte b = 3;
    if (paramInt < this.PLAY_100_TIME) {
      b = 1;
    } else if (paramInt < this.PLAY_50_TIME + this.PLAY_100_TIME) {
      b = 2;
    } 
    return paramBoolean ? -b : b;
  }
  
  public int getStateAlertTime(int paramInt) {
    switch (paramInt) {
      case 1:
        return this.PLAY_100_ALERT;
      case 2:
        return this.PLAY_50_ALERT;
      case 3:
        return this.PLAY_0_ALERT;
    } 
    return -1;
  }
  
  public int getPlayingStateTime(int paramInt) {
    switch (paramInt) {
      case 1:
        return this.PLAY_100_TIME;
      case 2:
        return this.PLAY_50_TIME + this.PLAY_100_TIME;
    } 
    return -1;
  }
  
  public Integer getGameidBy(int paramInt1, int paramInt2) {
    if (null != this.map_zonegame.get(Integer.valueOf(paramInt2)))
      return null; 
    if (paramInt1 < 8)
      paramInt1 = 8; 
    return Integer.valueOf(paramInt1);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\EnthrallmentConfig.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */