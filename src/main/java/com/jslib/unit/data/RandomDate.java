package com.jslib.unit.data;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public final class RandomDate implements RandomValue
{
  private static final Random random = new Random();
  private static long MAX_T;
  static {
    Calendar c = Calendar.getInstance();
    c.set(2300, 2, 15, 14, 20, 0);
    MAX_T = c.getTimeInMillis();
  }
  private Type type;

  public RandomDate(Type type)
  {
    this.type = type;
  }

  @Override
  public Object value(int maxLength)
  {
    if(maxLength != 0) throw new IllegalArgumentException("Random date does not support maximum length.");
    long t = random.nextLong() % MAX_T;
    if(this.type.equals(Date.class)) return new Date(t);
    if(this.type.equals(java.sql.Date.class)) return new java.sql.Date(t);
    if(this.type.equals(Time.class)) return new Time(t);
    if(this.type.equals(Timestamp.class)) return new Timestamp(t);
    throw new IllegalStateException("No random generator for " + this.type);
  }
}
