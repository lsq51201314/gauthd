package com.goldhuman.Common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class Cache {
  public static int default_size = 100;
  
  public static int default_timeout = 10;
  
  private Map cache = new HashMap<Object, Object>();
  
  private LRU lru = new LRU();
  
  private int nitem;
  
  private int maxsize = default_size;
  
  private int time_stamp = 0;
  
  private int life_time = default_timeout;
  
  private int[] key_pos;
  
  private static Map all_caches = Collections.synchronizedMap(new HashMap<Object, Object>());
  
  private void add(Item paramItem) {
    paramItem.life_time = this.life_time;
    if (this.cache.size() == this.maxsize)
      remove(this.lru.last()); 
    this.cache.put(paramItem, paramItem);
    this.lru.add(paramItem);
  }
  
  private void remove(Item paramItem) {
    paramItem.revoke();
    this.cache.remove(paramItem);
    this.lru.remove(paramItem);
  }
  
  private boolean contains(Item paramItem) {
    return this.cache.containsKey(paramItem);
  }
  
  private Cache(int paramInt, int[] paramArrayOfint) {
    this.nitem = paramInt;
    this.key_pos = paramArrayOfint;
  }
  
  private Cache(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    this.nitem = paramInt1;
    this.key_pos = paramArrayOfint;
    this.maxsize = paramInt2;
    this.life_time = paramInt3;
  }
  
  public static Cache Create(String paramString, int paramInt, int[] paramArrayOfint) {
    Cache cache = new Cache(paramInt, paramArrayOfint);
    all_caches.put(paramString, cache);
    return cache;
  }
  
  public static Cache Create(String paramString, int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    Cache cache = new Cache(paramInt1, paramArrayOfint, paramInt2, paramInt3);
    all_caches.put(paramString, cache);
    return cache;
  }
  
  public static Cache getInstance(String paramString) {
    return (Cache)all_caches.get(paramString);
  }
  
  public synchronized int size() {
    return this.cache.size();
  }
  
  public synchronized Item find(Item paramItem) {
    Item item = (Item)this.cache.get(paramItem);
    if (item == null)
      return null; 
    this.lru.access(item);
    return (Item)item.clone();
  }
  
  public Item newItem() {
    return new Item(this);
  }
  
  public static void main(String[] paramArrayOfString) {
    Cache cache = Create("c1", 2, new int[] { 0 });
    try {
      cache.newItem().set(0, new Integer(1)).set(1, new String("a")).commit();
      cache.newItem().set(0, new Integer(2)).set(1, new String("b")).commit();
      cache.newItem().set(0, new Integer(3)).set(1, new String("c")).commit();
      Item item1 = cache.find(cache.newItem().set(0, new Integer(1)));
      cache.newItem().set(0, new Integer(4)).set(1, new String("d")).commit();
      Item item2 = cache.find(cache.newItem().set(0, new Integer(3)));
      cache.newItem().set(0, new Integer(5)).set(1, new String("e")).commit();
      System.out.println("Size = " + cache.size());
      Thread.sleep(1000L);
      System.out.println("Size = " + cache.size());
      Thread.sleep(1000L);
      System.out.println("Size = " + cache.size());
      Thread.sleep(1000L);
      System.out.println("Size = " + cache.size());
      Thread.sleep(1000L);
      System.out.println("Size = " + cache.size());
      Thread.sleep(1000L);
      System.out.println("Size = " + cache.size());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  static {
    (new Timer(true)).schedule(new TimerTask() {
          public void run() {
            synchronized (Cache.all_caches) {
              Iterator<Map.Entry> iterator = Cache.all_caches.entrySet().iterator();
              while (iterator.hasNext()) {
                Cache cache = (Cache)((Map.Entry)iterator.next()).getValue();
                synchronized (cache) {
                  Iterator<Map.Entry> iterator1 = cache.cache.entrySet().iterator();
                  while (iterator1.hasNext()) {
                    Cache.Item item = (Cache.Item)((Map.Entry)iterator1.next()).getValue();
                    if (--item.life_time <= 0) {
                      item.revoke();
                      iterator1.remove();
                      cache.lru.remove(item);
                    } 
                  } 
                } 
              } 
            } 
          }
        },  0L, 1000L);
  }
  
  public class Item implements Cloneable {
    private static final int CLEAN = 0;
    
    private static final int DIRTY = 1;
    
    private Item origin;
    
    private int time_stamp;
    
    private int life_time;
    
    private int access_count = 0;
    
    private int status;
    
    private Object[] items;
    
    private Cache owner;
    
    protected Object clone() {
      try {
        Item item = (Item)super.clone();
        item.items = new Object[this.items.length];
        System.arraycopy(this.items, 0, item.items, 0, this.items.length);
        item.origin = this;
        return item;
      } catch (Exception exception) {
        return null;
      } 
    }
    
    private Item(Cache param1Cache1) {
      this.owner = param1Cache1;
      this.items = new Object[param1Cache1.nitem];
      this.status = 1;
      this.time_stamp = param1Cache1.time_stamp++;
    }
    
    private void revoke() {}
    
    public boolean equals(Object param1Object) {
      for (byte b = 0; b < this.owner.key_pos.length; b++) {
        if (!this.items[Cache.this.key_pos[b]].equals(((Item)param1Object).items[Cache.this.key_pos[b]]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int i = 0;
      for (byte b = 0; b < this.owner.key_pos.length; b++)
        i = i + this.items[Cache.this.key_pos[b]].hashCode() * 17 >> 4; 
      return i;
    }
    
    public void commit() throws RuntimeException {
      if (this.status == 0)
        return; 
      synchronized (this.owner) {
        if (this.origin == null) {
          if (this.owner.contains(this))
            throw new RuntimeException("Duplicate Key"); 
        } else {
          if (this.origin.time_stamp != this.time_stamp)
            throw new RuntimeException("TimeStamp Collision"); 
          if (hashCode() != this.origin.hashCode() || !equals(this.origin))
            this.owner.remove(this.origin); 
          this.origin.time_stamp = this.owner.time_stamp++;
          this.origin = null;
        } 
        this.status = 0;
        this.owner.add(this);
      } 
    }
    
    public Item set(int param1Int, Object param1Object) {
      this.items[param1Int] = param1Object;
      this.status = 1;
      return this;
    }
    
    public Object get(int param1Int) {
      return this.items[param1Int];
    }
  }
  
  private class LRU {
    private TreeMap lru = new TreeMap<Object, Object>();
    
    private LRU() {}
    
    public void add(Cache.Item param1Item) {
      Integer integer = new Integer(param1Item.access_count);
      LinkedList<Cache.Item> linkedList = (LinkedList)this.lru.get(integer);
      if (linkedList == null)
        this.lru.put(integer, linkedList = new LinkedList()); 
      linkedList.addLast(param1Item);
    }
    
    public void remove(Cache.Item param1Item) {
      Integer integer = new Integer(param1Item.access_count);
      LinkedList linkedList = (LinkedList)this.lru.get(integer);
      linkedList.remove(param1Item);
      if (linkedList.size() == 0)
        this.lru.remove(integer); 
    }
    
    public Cache.Item last() {
      return ((LinkedList<Cache.Item>)this.lru.get(this.lru.firstKey())).getFirst();
    }
    
    public void access(Cache.Item param1Item) {
      remove(param1Item);
      param1Item.access_count++;
      add(param1Item);
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\com\goldhuman\Common\Cache.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */