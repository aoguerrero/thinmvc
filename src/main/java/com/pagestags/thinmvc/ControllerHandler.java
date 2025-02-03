package com.pagestags.thinmvc;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pagestags.thinmvc.cntr.BaseController;
import com.pagestags.thinmvc.excp.ServiceException;
import com.pagestags.thinmvc.mdl.Response;
import com.pagestags.thinmvc.utl.FileSystemUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;

public class ControllerHandler extends SimpleChannelInboundHandler<Object> {

	private static Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

	private Map<String, BaseController> controllers;

	private String basePath;

	public ControllerHandler(String basePath, Map<String, BaseController> controllers) {
		this.basePath = basePath;
		this.controllers = controllers;
	}

	private HttpRequest request;
	private byte[] body = new byte[] {};

	@Override
	protected void channelRead0(ChannelHandlerContext context, Object message) {
		try {
			if (message instanceof HttpRequest) {
				request = (HttpRequest) message;
			}
			if (message instanceof HttpContent) {
				HttpContent httpContent = (HttpContent) message;
				BaseController controller = getController(request.uri());

				ByteBuf byteBuf = httpContent.content();
				byte[] buffer = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(buffer);
				byte[] joined = new byte[body.length + buffer.length];
				System.arraycopy(body, 0, joined, 0, body.length);
				System.arraycopy(buffer, 0, joined, body.length, buffer.length);
				body = joined;

				if (message instanceof LastHttpContent) {
					Response response = controller.execute(request, body);
					writeResponse(context, response.status(), response.headers(), response.body());
				}
			}
		} catch (ServiceException.BadRequest e) {
			writeError(context, BAD_REQUEST);
		} catch (ServiceException.NotFound e) {
			writeError(context, NOT_FOUND);
		} catch (ServiceException.Unauthorized e) {
			writeError(context, UNAUTHORIZED);
		} catch (Exception e) {
			logger.error("Unexpected error", e);
			writeError(context, INTERNAL_SERVER_ERROR);
		}
	}

	private BaseController getController(String uri) {
		if(basePath.equals(uri))
			uri += "/";
		for (Entry<String, BaseController> entry : controllers.entrySet()) {
			if (Pattern.matches(basePath + entry.getKey(), uri)) {
				return entry.getValue();
			}
		}
		throw new ServiceException.NotFound();
	}

	private void writeResponse(ChannelHandlerContext context, HttpResponseStatus status, HttpHeaders headers,
			byte[] body) {
		FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(body));
		httpResponse.headers().add(headers);
		context.write(httpResponse);
		context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	private void writeError(ChannelHandlerContext context, HttpResponseStatus status) {
		try {
			FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status,
					Unpooled.copiedBuffer(FileSystemUtils.getContent("classpath:///error/" + status.code() + ".html")));
			context.write(httpResponse);
			context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		} catch (Exception e) {
			logger.error("Exception loading error page", e);
		}
	}
}