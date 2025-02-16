package onl.andres.thinmvc;

public enum ThinmvcParameters {

	PORT("port", "8080"), ENABLE_CACHE("enable_cache", "false");

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
