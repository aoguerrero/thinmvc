package com.pagestags.thinmvc.cntr;

import static com.pagestags.thinmvc.Constants.BASE_PATH;
import static com.pagestags.thinmvc.Constants.ENABLE_CACHE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.FileSystemUtils;
import com.pagestags.thinmvc.utl.HttpUtils;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class TemplateController implements BaseController {

	protected final String path;
	protected final String basePath;
	protected HttpRequest request;
	protected byte[] body;
	private Map<String, byte[]> templatesCache;
	private final boolean cacheEnable;

	protected TemplateController(String path) {
		this.path = path;
		this.basePath = System.getProperty(BASE_PATH);
		this.templatesCache = new HashMap<>();
		this.cacheEnable = Boolean.valueOf(System.getProperty(ENABLE_CACHE, "false"));
	}

	public abstract Map<String, Object> getContext();

	public Response execute(HttpRequest request, byte[] body) {
		this.request = request;
		this.body = body;
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.getContentType("html"));
		headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_NO_STORE);
		Map<String, Object> context = getContext();
		if (context != null && context.size() > 0) {
			VelocityContext velocityContext = new VelocityContext(context);
			if (cacheEnable && !templatesCache.containsKey(path)) {
				templatesCache.put(path, FileSystemUtils.getContent(path));
			}
			byte[] template = cacheEnable ? templatesCache.get(path) : FileSystemUtils.getContent(path);
			StringWriter writer = new StringWriter();

			VelocityEngine velocityEngine = new VelocityEngine();
			if (FileSystemUtils.isClasspath(path)) {
				velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
				velocityEngine.init();
			}
			velocityContext.put("current_path", FileSystemUtils.getDirectory(path));

			velocityEngine.evaluate(velocityContext, writer, "", new String(template, StandardCharsets.UTF_8));
			return new Response(HttpResponseStatus.OK, headers, writer.toString().getBytes(StandardCharsets.UTF_8));
		} else {
			return new Response(HttpResponseStatus.OK, headers, FileSystemUtils.getContent(path));
		}
	}
}
