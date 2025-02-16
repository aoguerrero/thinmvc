package onl.andres.thinmvc.utl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

	private HttpUtils() {
	}

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String LOCATION = "Location";
	public static final String SET_COOKIE = "Set-Cookie";
	public static final String COOKIE = "Cookie";

	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String CACHE_CONTROL_3_MONTH = "max-age=7776000";
	public static final String CACHE_CONTROL_NO_STORE = "no-store";

	public static final String FORM_MULTIPART = "multipart/form-data";
	public static final String APPLICATION_JSON = "application/json";

	public static Map<String, String> bodyToForm(byte[] body) {
		return getParams(new String(body, StandardCharsets.UTF_8));
	}
	
	public static Map<String, String> getUrlParams(final String url) {
		if(!url.contains("?"))
			return new HashMap<>();
		String paramsPart = url.substring(url.lastIndexOf('?')+1, url.length());
		return getParams(paramsPart);
	}

	public static Map<String, String> getParams(final String body) {
		Map<String, String> result = new HashMap<>();
		if (body != null) {
			for (String row : body.split("&")) {
				String[] keyValue = row.split("=");
				result.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
						URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
			}
		}
		return result;
	}

	public static Map<String, String> cookiesToMap(String cookieStr) {
		Map<String, String> result = new HashMap<>();
		if (cookieStr != null) {
			String[] rows = cookieStr.split(";");
			for (String row : rows) {
				String[] keyValue = row.split("=");
				if (keyValue.length > 1)
					result.put(keyValue[0].trim(), keyValue[1].trim());
			}
		}
		return result;
	}

	public static String getContentType(String path) {
		String ext = path.substring(path.lastIndexOf('.') + 1, path.length()).toUpperCase();
		return ContentType.valueOf(ext).getStr();
	}
}
