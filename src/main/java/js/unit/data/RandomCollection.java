package js.unit.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;

import js.unit.JsUnitException;
import js.unit.util.Classes;

public final class RandomCollection implements RandomValue {
	private static final int MAX_SIZE = 10;
	private static final Random random = new Random();
	private TestData.Context context;
	private Class<? extends Collection<?>> rawType;
	private Type actualType;

	@SuppressWarnings("unchecked")
	public RandomCollection(TestData.Context context, Type type) {
		this.context = context;
		if (!(type instanceof ParameterizedType))
			throw new IllegalArgumentException("Not parameterized collections are not supported");
		ParameterizedType t = (ParameterizedType) type;
		this.rawType = (Class<? extends Collection<?>>) t.getRawType();
		this.actualType = t.getActualTypeArguments()[0];
	}

	@Override
	public Object value(int maxLength) {
		try {
			int size = random.nextInt(MAX_SIZE);
			Collection<Object> collection = Classes.newCollection(this.rawType);
			while (size-- > 0) {
				Object o = this.context.createObject(this.actualType);
				if (o == null)
					break;
				collection.add(o);
			}
			return collection;
		} catch (Exception e) {
			throw new JsUnitException(e);
		}
	}
}
