package onl.andres.thinmvc;

import static onl.andres.thinmvc.ThinmvcParameters.ENABLE_CACHE;
import static onl.andres.thinmvc.ThinmvcParameters.PORT;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import onl.andres.thinmvc.cntr.BaseController;;

public class Application {

	private Application() {
	}

	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void start(Map<String, BaseController> controllers) throws InterruptedException {
		String port = PORT.get();

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
							channelPipeline.addLast(new ControllerHandler(controllers));
						}
					});
			ChannelFuture channelFuture = serverBootstrap.bind(Integer.valueOf(port)).sync();
			logger.info("Accepted JVM parameters: '{}', '{}'", PORT.getName(),
					ENABLE_CACHE.getName());
			logger.info("Application listening on port: {}", port);
			logger.info("Cache enabled: {}", ENABLE_CACHE.get());
			logger.info("Endpoint paths:");
			controllers.keySet().stream().forEach(path -> logger.info(path));

			channelFuture.channel().closeFuture().sync();
		} finally {
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
}
