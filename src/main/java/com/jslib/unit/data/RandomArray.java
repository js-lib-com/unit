package com.jslib.unit.data;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Random;

public final class RandomArray implements RandomValue
{
  private static final int MAX_LENGTH = 10;
  private static final Random random = new Random();
  private TestData.Context context;
  private Class<?> type;

  public RandomArray(TestData.Context context, Type type)
  {
    this.context = context;
    Class<?> clazz = (Class<?>)type;
    this.type = clazz.getComponentType();
  }

  @Override
  public Object value(int maxLength)
  {
    int length = random.nextInt(MAX_LENGTH);
    Object array = Array.newInstance(this.type, length);
    for(int i = 0; i < length; i++) {
      Object o = this.context.createObject(this.type);
      if(o == null) break;
      Array.set(array, i, o);
    }
    return array;
  }
}
