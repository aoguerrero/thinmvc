package com.pagestags.thinmvc.cntr;

import static com.pagestags.thinmvc.Constants.BASE_PATH;

import java.util.Map;

import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.HttpUtils;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class FormController implements BaseController {

	protected final String path;
	protected final String basePath;
	private HttpRequest request;
	private HttpHeaders responseHeaders;
	private Map<String, String> formData;
	private byte[] body;

	protected FormController(String path) {
		this.path = path;
		this.basePath = System.getProperty(BASE_PATH);
	}

	public abstract String execute();

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		this.body = body;
		this.request = request;
		this.responseHeaders = new DefaultHttpHeaders();
		if (!isMultipartRequest(request)) {
			this.formData = HttpUtils.bodyToForm(body);
		}
		String id = execute();
		if (id != null)
			responseHeaders.add(HttpUtils.LOCATION, this.path.replace("{id}", id));
		else
			responseHeaders.add(HttpUtils.LOCATION, this.path);

		return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[] {});
	}

	private boolean isMultipartRequest(HttpRequest request) {
		return request.headers() != null && request.headers().get(HttpUtils.CONTENT_TYPE) != null
				&& request.headers().get(HttpUtils.CONTENT_TYPE).startsWith(HttpUtils.FORM_MULTIPART);

	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public byte[] getBody() {
		return body;
	}

	public Map<String, String> getFormData() {
		return formData;
	}

}
