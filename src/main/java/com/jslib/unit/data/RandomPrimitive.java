package com.jslib.unit.data;

import java.lang.reflect.Type;
import java.util.Random;

import com.jslib.unit.util.Types;

public final class RandomPrimitive implements RandomValue
{
  private static final Random random = new Random();
  private Type type;

  public RandomPrimitive(Type type)
  {
    this.type = type;
  }

  @Override
  public Object value(int maxLength)
  {
    if(Types.equalsAny(this.type, int.class, Integer.class)) return (int)random.nextInt();
    if(Types.equalsAny(this.type, double.class, Double.class)) return (double)random.nextDouble();
    if(Types.equalsAny(this.type, boolean.class, Boolean.class)) return (boolean)random.nextBoolean();
    if(Types.equalsAny(this.type, byte.class, Byte.class)) return (byte)random.nextInt(Byte.MAX_VALUE);
    if(Types.equalsAny(this.type, short.class, Short.class)) return (short)random.nextInt(Short.MAX_VALUE);
    if(Types.equalsAny(this.type, long.class, Long.class)) return (long)random.nextLong();
    if(Types.equalsAny(this.type, float.class, Float.class)) return (float)random.nextFloat();
    if(Types.equalsAny(this.type, char.class, Character.class)) return (char)random.nextInt(Character.MAX_VALUE);
    throw new IllegalStateException("No random generator for " + this.type);
  }
}
