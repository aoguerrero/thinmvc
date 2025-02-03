package com.pagestags.thinmvc.cntr;

import static com.pagestags.thinmvc.ThinmvcParameters.BASE_PATH;

import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.HttpUtils;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class RedirectController implements BaseController {

	protected final String path;
	private HttpRequest request;
	private HttpHeaders responseHeaders;

	protected RedirectController(String path) {
		this.path = path;
	}

	public abstract String execute();

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		this.request = request;
		this.responseHeaders = new DefaultHttpHeaders();
		String id = execute();
		String basePath = BASE_PATH.get();
		if (id != null)
			responseHeaders.add(HttpUtils.LOCATION, basePath + this.path.replace("{id}", id));
		else
			responseHeaders.add(HttpUtils.LOCATION, basePath + this.path);
		return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[] {});
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

	public HttpRequest getRequest() {
		return request;
	}
}
