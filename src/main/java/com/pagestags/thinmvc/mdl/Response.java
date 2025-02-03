package com.pagestags.thinmvc.mdl;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public record Response(HttpResponseStatus status, HttpHeaders headers, byte[] body) {
}
