package com.pagestags.thinmvc;

public enum ThinmvcParameters {

	PORT("port", "8080"), ENABLE_CACHE("enable_cache", "false"), BASE_PATH("base_path", "");

	private final String name;
	private final String defaultValue;

	private ThinmvcParameters(String name, String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}
	
	public String get() {
		return System.getProperty(name, defaultValue);
	}
}
