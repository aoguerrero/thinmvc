package com.pagestags.thinmvc.cntr;

import java.nio.charset.StandardCharsets;

import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.HttpUtils;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class JsonController implements BaseController {

	public abstract String execute(String body);

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
		return new Response(HttpResponseStatus.OK, headers,
				execute(new String(body, StandardCharsets.UTF_8)).getBytes());
	}
}
