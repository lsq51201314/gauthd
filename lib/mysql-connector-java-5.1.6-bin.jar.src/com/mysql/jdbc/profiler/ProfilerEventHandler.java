package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Extension;

public interface ProfilerEventHandler extends Extension {
  void consumeEvent(ProfilerEvent paramProfilerEvent);
}


/* Location:              D:\UserData\Desktop\authd\!\lib\mysql-connector-java-5.1.6-bin.jar!\com\mysql\jdbc\profiler\ProfilerEventHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */