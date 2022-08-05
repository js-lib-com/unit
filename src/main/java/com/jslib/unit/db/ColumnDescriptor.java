package com.jslib.unit.db;

class ColumnDescriptor {
	private final String name;
	private String type;
	private String value;

	public ColumnDescriptor(String name) {
		this.name = name;
	}

	public ColumnDescriptor(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
