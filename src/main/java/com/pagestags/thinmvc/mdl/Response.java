package com.pagestags.thinmvc.mdl;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class Response {

	private final HttpResponseStatus status;
	private final HttpHeaders headers;
	private final byte[] body;

	public Response(HttpResponseStatus status, HttpHeaders headers, byte[] body) {
		this.status = status;
		this.headers = headers;
		this.body = body;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}
}
