package onl.andres.thinmvc.cntr;

import static onl.andres.thinmvc.ThinmvcParameters.ENABLE_CACHE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.thinmvc.mdl.Response;
import onl.andres.thinmvc.utl.ContentType;
import onl.andres.thinmvc.utl.FileSystemUtils;
import onl.andres.thinmvc.utl.HttpUtils;

public abstract class TemplateController implements BaseController {

	protected final String path;
	protected HttpRequest request;
	private Map<String, byte[]> templatesCache;
	private VelocityEngine velocityEngine;

	public final static String CURRENT_PATH = "current_path";

	protected TemplateController(String path) {
		this.path = path;
		templatesCache = new HashMap<>();

		velocityEngine = new VelocityEngine();
		if (FileSystemUtils.isClasspath(path)) {
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			velocityEngine.init();
		}
	}

	public abstract Map<String, Object> getContext();

	public Response execute(HttpRequest request, byte[] body) {
		this.request = request;
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, ContentType.HTML.getStr());
		headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_NO_STORE);
		Map<String, Object> context = getContext();
		VelocityContext velocityContext = new VelocityContext(context);

		byte[] template = getTemplate(path);
		StringWriter writer = new StringWriter();

		velocityContext.put(CURRENT_PATH, FileSystemUtils.getDirectory(path));

		velocityEngine.evaluate(velocityContext, writer, "", new String(template, StandardCharsets.UTF_8));
		return new Response(HttpResponseStatus.OK, headers, writer.toString().getBytes(StandardCharsets.UTF_8));
	}

	private byte[] getTemplate(String path) {
		if (Boolean.valueOf(ENABLE_CACHE.get())) {
			if (!templatesCache.containsKey(path)) {
				templatesCache.put(path, FileSystemUtils.getContent(path));
			}
			return templatesCache.get(path);
		}
		return FileSystemUtils.getContent(path);
	}
}
