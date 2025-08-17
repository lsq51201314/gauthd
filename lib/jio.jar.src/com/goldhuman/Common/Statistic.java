package com.goldhuman.Common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Statistic {
  public int m_max;
  
  public int m_min;
  
  public int m_cur;
  
  public int m_cnt;
  
  public int m_sum;
  
  private static HashMap m_map = new HashMap<Object, Object>();
  
  private Statistic() {
    reset();
  }
  
  public void reset() {
    this.m_cur = 0;
    this.m_cnt = 0;
    this.m_sum = 0;
    this.m_max = 0;
    this.m_min = 0;
  }
  
  public void update(int paramInt) {
    this.m_cur = paramInt;
    this.m_cnt++;
    this.m_sum += paramInt;
    this.m_max = (paramInt > this.m_max) ? paramInt : this.m_max;
    this.m_min = (0 == this.m_min) ? paramInt : ((paramInt < this.m_min) ? paramInt : this.m_min);
  }
  
  public static Statistic GetInstance(String paramString) {
    synchronized (m_map) {
      Statistic statistic = (Statistic)m_map.get(paramString);
      if (statistic != null)
        return statistic; 
      statistic = new Statistic();
      m_map.put(paramString, statistic);
      return statistic;
    } 
  }
  
  public static boolean enumdefault(String paramString, Statistic paramStatistic) {
    System.out.println(paramString);
    System.out.print(" MAX: " + paramStatistic.m_max);
    System.out.print(" MIN: " + paramStatistic.m_min);
    System.out.print(" CUR: " + paramStatistic.m_cur);
    System.out.print(" CNT: " + paramStatistic.m_cnt);
    System.out.println(" SUM: " + paramStatistic.m_sum);
    return true;
  }
  
  public static void enumerate(StatCallBack paramStatCallBack) {
    synchronized (m_map) {
      for (Map.Entry entry : m_map.entrySet()) {
        String str = (String)entry.getKey();
        Statistic statistic = (Statistic)entry.getValue();
        paramStatCallBack.enumerate(str, statistic);
      } 
    } 
  }
  
  public static synchronized void resetall() {
    synchronized (m_map) {
      Iterator<Map.Entry> iterator = m_map.entrySet().iterator();
      while (iterator.hasNext()) {
        Statistic statistic = (Statistic)((Map.Entry)iterator.next()).getValue();
        statistic.reset();
      } 
    } 
  }
  
  public static interface StatCallBack {
    boolean enumerate(String param1String, Statistic param1Statistic);
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\Statistic.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */