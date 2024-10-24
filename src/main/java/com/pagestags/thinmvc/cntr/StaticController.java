package com.pagestags.thinmvc.cntr;

import static com.pagestags.thinmvc.Constants.ENABLE_CACHE;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.FileSystemUtils;
import com.pagestags.thinmvc.utl.HttpUtils;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StaticController implements BaseController {

	private final String path;

	private Map<String, byte[]> staticCache;

	private final boolean cacheEnable;

	public StaticController(String path) {
		this.path = path;
		this.staticCache = new HashMap<>();
		this.cacheEnable = Boolean.valueOf(System.getProperty(ENABLE_CACHE, "false"));
	}

	public Response execute(HttpRequest request, byte[] body) {
		Matcher matcher = Pattern.compile("([^/]*)$").matcher(request.uri());
		if (matcher.find()) {
			String file = matcher.group(1);
			String resPath = path.replace("{file}", file);
			HttpHeaders headers = new DefaultHttpHeaders();
			headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.getContentType(resPath));
			headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_3_MONTH);
			if (cacheEnable && !staticCache.containsKey(resPath)) {
				staticCache.put(resPath, FileSystemUtils.getContent(resPath));
			}
			return new Response(HttpResponseStatus.OK, headers,
					cacheEnable ? staticCache.get(resPath) : FileSystemUtils.getContent(resPath));
		}
		return null;
	}

}
