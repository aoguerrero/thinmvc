package onl.andres.thinmvc.cntr;

import static onl.andres.thinmvc.ThinmvcParameters.ENABLE_CACHE;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.thinmvc.excp.ServiceException;
import onl.andres.thinmvc.mdl.Response;
import onl.andres.thinmvc.utl.FileSystemUtils;
import onl.andres.thinmvc.utl.HttpUtils;

public class StaticController implements BaseController {

	private final String baseDir;

	private Map<String, byte[]> staticCache;

	public StaticController(String baseDir) {
		this.baseDir = baseDir;
		this.staticCache = new HashMap<>();
	}

	public Response execute(HttpRequest request, byte[] body) {
		String uri = request.uri();
		var params = HttpUtils.getUrlParams(uri);

		HttpHeaders headers = new DefaultHttpHeaders();
		String filePath = baseDir;

		if (baseDir.endsWith("/")) {
			String resPath = params.get("path");
			if (resPath == null || resPath.trim().length() == 0 || resPath.contains("..") || resPath.contains(":")
					|| resPath.contains("//") || resPath.contains("\\") || resPath.startsWith("/"))
				throw new ServiceException.BadRequest();

			filePath = baseDir + resPath;
			headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.getContentType(filePath));
		}
		headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_3_MONTH);
		return new Response(HttpResponseStatus.OK, headers, getContent(filePath));
	}

	private byte[] getContent(String filePath) {
		if (Boolean.valueOf(ENABLE_CACHE.get())) {
			if (!staticCache.containsKey(filePath)) {
				staticCache.put(filePath, FileSystemUtils.getContent(filePath));
			}
			return staticCache.get(filePath);
		}
		return FileSystemUtils.getContent(filePath);
	}
}
