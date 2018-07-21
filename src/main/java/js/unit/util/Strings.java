package js.unit.util;

public class Strings {
	/**
	 * Join collection of objects, converted to string, using specified string separator.Concatenates strings from collection
	 * converted to string but take care to avoid null items. Uses given separator between strings. Returns null if given
	 * objects array is null and empty if empty. If separator is null uses space string instead. Null objects or empty strings
	 * from given <code>objects</code> parameter are ignored.
	 * 
	 * @param objects collection of objects to join,
	 * @param separator string used as separator.
	 * @return joined string.
	 */
	public static String join(Iterable<?> objects, String separator) {
		if (objects == null) {
			return null;
		}
		if (separator == null) {
			separator = " ";
		}

		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object object : objects) {
			if (object == null) {
				continue;
			}
			String value = object instanceof String ? (String) object : object.toString();
			if (value.isEmpty()) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				builder.append(separator);
			}
			builder.append(value);
		}
		return builder.toString();
	}
}
