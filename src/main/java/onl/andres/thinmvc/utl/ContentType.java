package onl.andres.thinmvc.utl;

public enum ContentType {

	// @formatter:off
	CSS("text/css; charset=utf-8"), 
	HTML("text/html; charset=utf-8"), 
	HTM("text/html; charset=utf-8"), 
	JS("text/javascript; charset=utf-8"),
	TXT("text/plain; charset=utf-8"), 
	GIF("image/gif"), 
	ICO("image/x-icon"), 
	JPEG("image/jpeg"), 
	JPG("image/jpeg"),
	PNG("image/png"), 
	SVG("image/svg+xml"), 
	PDF("application/pdf"), 
	JSON("application/json"), 
	ZIP("application/zip");
	// @formatter:on

	private final String str;

	ContentType(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

}
