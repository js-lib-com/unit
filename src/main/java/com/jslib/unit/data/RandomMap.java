package com.jslib.unit.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;

import com.jslib.unit.JsUnitException;
import com.jslib.unit.util.Classes;

public final class RandomMap implements RandomValue {
	private static final int MAX_SIZE = 10;
	private static final Random random = new Random();
	private TestData.Context context;
	private Class<? extends Map<?, ?>> rawType;
	private Type keyType;
	private Type valueType;

	@SuppressWarnings("unchecked")
	public RandomMap(TestData.Context context, Type type) {
		this.context = context;
		if (!(type instanceof ParameterizedType))
			throw new IllegalArgumentException("Non-parameterized collections are not supported");
		ParameterizedType ptype = (ParameterizedType) type;
		this.rawType = (Class<? extends Map<?, ?>>) ptype.getRawType();
		Type[] types = ptype.getActualTypeArguments();
		this.keyType = types[0];
		this.valueType = types[1];
	}

	@Override
	public Object value(int maxLength) {
		try {
			int size = random.nextInt(MAX_SIZE);
			Map<Object, Object> map = Classes.newMap(this.rawType);
			while (size-- > 0) {
				Object key = this.context.createObject(this.keyType);
				if (key == null)
					break;
				Object value = this.context.createObject(this.valueType);
				if (value == null)
					break;
				map.put(key, value);
			}
			return map;
		} catch (Exception e) {
			throw new JsUnitException(e);
		}
	}
}
