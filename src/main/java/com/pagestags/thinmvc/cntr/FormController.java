package com.pagestags.thinmvc.cntr;

import static com.pagestags.thinmvc.ThinmvcParameters.BASE_PATH;

import java.util.Map;
import java.util.Optional;

import com.pagestags.thinmvc.excp.ServiceException;
import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.HttpUtils;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class FormController implements BaseController {

	protected final String path;
	private HttpRequest request;
	private HttpHeaders responseHeaders;
	private Map<String, String> formData;

	protected FormController(String path) {
		this.path = path;
	}

	public abstract Optional<String> execute();

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		this.request = request;
		this.responseHeaders = new DefaultHttpHeaders();
		this.formData = HttpUtils.bodyToForm(body);
		var opId = execute();
		String basePath = BASE_PATH.get();
		opId.ifPresentOrElse(id -> responseHeaders.add(HttpUtils.LOCATION, basePath + this.path.replace("{id}", id)),
				() -> responseHeaders.add(HttpUtils.LOCATION, basePath + this.path));
		return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[] {});
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public Map<String, String> getFormData() {
		return formData;
	}

}
