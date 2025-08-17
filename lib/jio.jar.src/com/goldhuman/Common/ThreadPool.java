package com.goldhuman.Common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadPool implements java.lang.Runnable {
  private static SortedMap tasks = new TreeMap<Object, Object>();
  
  private static SortedMap count = new TreeMap<Object, Object>();
  
  private static int task_count = 0;
  
  private static LinkedList remove = new LinkedList();
  
  public Integer priority;
  
  private ThreadPool(Integer paramInteger) {
    this.priority = paramInteger;
    synchronized (count) {
      Integer integer = (Integer)count.get(paramInteger);
      count.put(paramInteger, new Integer((integer == null) ? 1 : (integer.intValue() + 1)));
    } 
  }
  
  private Runnable GetTask(SortedMap paramSortedMap) {
    Iterator<Map.Entry> iterator = paramSortedMap.entrySet().iterator();
    while (iterator.hasNext()) {
      LinkedList<Runnable> linkedList = (LinkedList)((Map.Entry)iterator.next()).getValue();
      if (!linkedList.isEmpty())
        return linkedList.removeLast(); 
    } 
    return null;
  }
  
  public void run() {
    while (true) {
      try {
        Runnable runnable = null;
        synchronized (tasks) {
          while (task_count == 0)
            tasks.wait(); 
          if ((runnable = GetTask(tasks.tailMap(this.priority))) == null)
            runnable = GetTask(tasks); 
          task_count--;
        } 
        runnable.run();
        synchronized (remove) {
          if (!remove.isEmpty() && this.priority.equals(remove.getLast())) {
            remove.removeLast();
            return;
          } 
        } 
      } catch (Exception exception) {}
    } 
  }
  
  public static void AddTask(Runnable paramRunnable) {
    synchronized (tasks) {
      Integer integer = new Integer(paramRunnable.GetPriority());
      LinkedList<Runnable> linkedList = (LinkedList)tasks.get(integer);
      if (linkedList == null)
        tasks.put(integer, linkedList = new LinkedList()); 
      linkedList.addFirst(paramRunnable);
      task_count++;
      tasks.notify();
    } 
  }
  
  public static int TaskCount() {
    return task_count;
  }
  
  public static void AddThread(int paramInt) {
    (new Thread(new ThreadPool(new Integer(paramInt)))).start();
  }
  
  public static int ThreadCount() {
    int i = 0;
    synchronized (count) {
      Iterator<Map.Entry> iterator = count.entrySet().iterator();
      while (iterator.hasNext())
        i += ((Integer)((Map.Entry)iterator.next()).getValue()).intValue(); 
    } 
    return i;
  }
  
  public static int ThreadCount(int paramInt) {
    int i = 0;
    synchronized (count) {
      Integer integer = (Integer)count.get(new Integer(paramInt));
      if (integer != null)
        i = integer.intValue(); 
    } 
    return i;
  }
  
  public static void RemoveThread(int paramInt) {
    Integer integer = new Integer(paramInt);
    synchronized (count) {
      Integer integer1 = (Integer)count.get(integer);
      if (integer1 != null) {
        int i = integer1.intValue() - 1;
        if (i > 0) {
          count.put(integer, new Integer(i));
          synchronized (remove) {
            remove.addFirst(integer);
          } 
        } else {
          count.remove(integer);
        } 
      } 
    } 
  }
  
  static {
    try {
      String str = Conf.GetInstance().find("ThreadPool", "config");
      if (str != null) {
        Matcher matcher = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)").matcher(str);
        while (matcher.find()) {
          int i = Integer.parseInt(matcher.group(1));
          for (int j = Integer.parseInt(matcher.group(2)); j > 0; j--)
            AddThread(i); 
        } 
      } 
    } catch (Exception exception) {}
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\ThreadPool.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */