package com.jslib.unit;

public class JsUnitException extends RuntimeException {
	/** Java serialization version. */
	private static final long serialVersionUID = -5441581664792325385L;

	public JsUnitException(String message, Object... args) {
		super(String.format(message, args));
	}

	public JsUnitException(Exception e) {
		super(e);
	}
}
