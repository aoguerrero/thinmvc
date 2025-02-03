package com.pagestags.thinmvc.utl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import com.pagestags.thinmvc.excp.ServiceException;

public class HttpUtils {

	private HttpUtils() {
	}

	// @formatter:off
    static final Map<String, String> contentTypes = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("CSS", "text/css; charset=utf-8"),
            new AbstractMap.SimpleEntry<>("HTML", "text/html; charset=utf-8"),
            new AbstractMap.SimpleEntry<>("JS", "text/javascript; charset=utf-8"),
            new AbstractMap.SimpleEntry<>("TXT", "text/plain; charset=utf-8"),
            new AbstractMap.SimpleEntry<>("GIF", "image/gif"), 
            new AbstractMap.SimpleEntry<>("ICO", "image/x-icon"),
            new AbstractMap.SimpleEntry<>("JPEG", "image/jpeg"), 
            new AbstractMap.SimpleEntry<>("JPG", "image/jpeg"),
            new AbstractMap.SimpleEntry<>("PNG", "image/png"), 
            new AbstractMap.SimpleEntry<>("SVG", "image/svg+xml"),
            new AbstractMap.SimpleEntry<>("PDF", "application/pdf"),
            new AbstractMap.SimpleEntry<>("JSON", "application/json"),
            new AbstractMap.SimpleEntry<>("ZIP", "application/zip"));          
            
    // @formatter:on

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String LOCATION = "Location";
	public static final String SET_COOKIE = "Set-Cookie";
	public static final String COOKIE = "Cookie";

	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String CACHE_CONTROL_3_MONTH = "max-age=7776000";
	public static final String CACHE_CONTROL_NO_STORE = "no-store";

	public static final String FORM_MULTIPART = "multipart/form-data";
	public static final String APPLICATION_JSON = "application/json";

	public static final String HTML_CONTENT_TYPE = contentTypes.get("HTML");

	public static Map<String, String> bodyToForm(byte[] body) {
		return bodyToForm(new String(body, StandardCharsets.UTF_8));
	}

	public static Map<String, String> bodyToForm(String body) {
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

		String contentType = contentTypes.get(ext);
		if (contentType != null)
			return contentType;

		throw new ServiceException.BadRequest();
	}
}
