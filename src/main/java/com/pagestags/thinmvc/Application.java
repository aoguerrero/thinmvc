package com.pagestags.thinmvc;

import static com.pagestags.thinmvc.Constants.ENABLE_CACHE;
import static com.pagestags.thinmvc.Constants.PORT;
import static com.pagestags.thinmvc.Constants.BASE_PATH;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pagestags.thinmvc.cntr.BaseController;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;;

public class Application {

	private Application() {
	}

	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void start(Map<String, BaseController> controllers) throws InterruptedException {
		String port = System.getProperty(PORT, "8080");
		
		String basePath = System.getProperty(BASE_PATH, "");

		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline channelPipeline = socketChannel.pipeline();
							channelPipeline.addLast(new HttpRequestDecoder());
							channelPipeline.addLast(new HttpResponseEncoder());
							channelPipeline.addLast(new ControllerHandler(basePath, controllers));
						}
					});
			ChannelFuture channelFuture = serverBootstrap.bind(Integer.valueOf(port)).sync();
			logger.info("Accepted JVM parameters: 'base_path', 'enable_cache', 'port'");
			logger.info("Application listening on port: {}", port);
			logger.info("Base path: {}", basePath);
			logger.info("Cache enabled: {}", System.getProperty(ENABLE_CACHE, "false"));
			logger.info("Endpoint paths:");
			for (String path : controllers.keySet()) {
				logger.info(path);
			}

			channelFuture.channel().closeFuture().sync();
		} finally {
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
}
