package com.goldhuman.Common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

public class TaskGraph extends Runnable implements TaskState {
  private HashSet<StatefulRunnable> runners = new HashSet<StatefulRunnable>();
  
  private HashSet<TaskNode> nodes = new HashSet<TaskNode>();
  
  private boolean restart = false;
  
  private boolean stopping = false;
  
  private TaskNode root = null;
  
  TaskContext context = null;
  
  Object locker = new Object();
  
  public void run() {
    if (!IsFinish()) {
      ThreadPool.AddTask(this);
      this.stopping = false;
    } else if (this.restart) {
      Iterator<TaskNode> iterator = this.nodes.iterator();
      while (iterator.hasNext())
        ((TaskNode)iterator.next()).SetInit(); 
      this.root.TurnRunning();
      this.restart = false;
      this.stopping = false;
    } else {
      Iterator<StatefulRunnable> iterator = this.runners.iterator();
      while (iterator.hasNext())
        ((StatefulRunnable)iterator.next()).Destroy(); 
      iterator = (Iterator)this.nodes.iterator();
      while (iterator.hasNext())
        ((TaskNode)iterator.next()).Destroy(); 
    } 
  }
  
  protected TaskGraph(TaskContext paramTaskContext) {
    this.root = null;
    this.context = paramTaskContext;
  }
  
  public boolean IsFinish() {
    synchronized (this.locker) {
      Iterator<TaskNode> iterator = this.nodes.iterator();
      while (iterator.hasNext()) {
        if (!((TaskNode)iterator.next()).IsFinish())
          return false; 
      } 
    } 
    return true;
  }
  
  public boolean IsStopping() {
    return this.stopping;
  }
  
  public void Start(TaskNode paramTaskNode) {
    this.root = paramTaskNode;
    this.root.TurnRunning();
  }
  
  public void Restart(TaskNode paramTaskNode) {
    this.root = paramTaskNode;
    this.restart = true;
    Stop();
  }
  
  public void Stop() {
    synchronized (this.locker) {
      Iterator<TaskNode> iterator = this.nodes.iterator();
      while (iterator.hasNext())
        ((TaskNode)iterator.next()).TurnStopping(); 
      this.stopping = true;
      ThreadPool.AddTask(this);
    } 
  }
  
  public TaskNode CreateNode(StatefulRunnable paramStatefulRunnable) {
    TaskNode taskNode = new TaskNode(paramStatefulRunnable, this);
    paramStatefulRunnable.graph = this;
    this.runners.add(paramStatefulRunnable);
    this.nodes.add(taskNode);
    return taskNode;
  }
  
  public TaskNode CreateStopNode() {
    TaskNode taskNode = new TaskNode(null, this);
    this.nodes.add(taskNode);
    return taskNode;
  }
  
  public TaskContext GetContext() {
    return this.context;
  }
  
  public void RunnableChangeState(TaskNode paramTaskNode) {
    if (paramTaskNode.GetState() == 4)
      Stop(); 
  }
  
  protected class TaskNode extends Runnable implements Observer {
    private HashSet<TaskNode> prev = new HashSet<TaskNode>();
    
    private HashSet<TaskNode> next = new HashSet<TaskNode>();
    
    private StatefulRunnable task = null;
    
    private TaskGraph graph = null;
    
    private int state;
    
    void TurnStopping() {
      if (this.state == 1) {
        this.state = 2;
      } else if (this.state == 0) {
        this.state = 3;
      } 
    }
    
    void SetInit() {
      this.state = 0;
      if (this.task != null)
        this.task.Init(); 
    }
    
    void TurnRunning() {
      if (this.state == 0 || this.state >= 4) {
        ThreadPool.AddTask(this);
        this.state = 1;
      } 
    }
    
    void Destroy() {}
    
    TaskNode(StatefulRunnable param1StatefulRunnable, TaskGraph param1TaskGraph1) {
      this.task = param1StatefulRunnable;
      this.graph = param1TaskGraph1;
      this.state = 0;
      if (this.task != null) {
        this.task.Init();
        this.task.addObserver(this);
      } 
    }
    
    public void update(Observable param1Observable, Object param1Object) {
      synchronized (this.graph.locker) {
        if (this.graph.IsStopping())
          return; 
        this.state = this.task.GetState();
      } 
      this.graph.RunnableChangeState(this);
      if (this.state >= 4) {
        synchronized (this.graph.locker) {
          Iterator<TaskNode> iterator = this.next.iterator();
          while (iterator.hasNext())
            ((TaskNode)iterator.next()).TurnRunning(); 
        } 
      } else if (this.state == 1) {
        ThreadPool.AddTask(this);
      } 
    }
    
    boolean IsFinish() {
      return (this.state >= 4 || this.state == 3);
    }
    
    public int GetState() {
      return this.state;
    }
    
    public void run() {
      if (this.state == 2) {
        this.state = 3;
        return;
      } 
      if (this.state != 1)
        return; 
      synchronized (this.graph.locker) {
        boolean bool = true;
        Iterator<TaskNode> iterator = this.prev.iterator();
        while (iterator.hasNext()) {
          if (!((TaskNode)iterator.next()).IsFinish()) {
            bool = false;
            break;
          } 
        } 
        if (!bool) {
          this.state = 0;
          return;
        } 
      } 
      if (this.task != null) {
        this.task.Run();
        if (this.state == 1 || this.state == 2)
          ThreadPool.AddTask(this); 
      } else {
        this.state = 3;
        this.graph.Stop();
      } 
    }
    
    public void AddChild(TaskNode param1TaskNode) {
      synchronized (this.graph.locker) {
        this.next.add(param1TaskNode);
        param1TaskNode.prev.add(this);
      } 
    }
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\jio.jar!\com\goldhuman\Common\TaskGraph.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */