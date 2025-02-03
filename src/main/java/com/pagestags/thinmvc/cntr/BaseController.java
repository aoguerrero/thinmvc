package com.pagestags.thinmvc.cntr;

import com.pagestags.thinmvc.mdl.Response;

import io.netty.handler.codec.http.HttpRequest;

public interface BaseController {

	public Response execute(HttpRequest request, byte[] body);

}
