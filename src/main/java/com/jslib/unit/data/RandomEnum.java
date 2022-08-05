package com.jslib.unit.data;

import java.lang.reflect.Type;
import java.util.Random;

public final class RandomEnum implements RandomValue
{
  private static final Random random = new Random();
  private Object[] values;

  public RandomEnum(Type type)
  {
    Class<?> clazz = (Class<?>)type;
    this.values = clazz.getEnumConstants();
  }

  @Override
  public Object value(int maxLength)
  {
    return this.values[random.nextInt(this.values.length)];
  }
}
