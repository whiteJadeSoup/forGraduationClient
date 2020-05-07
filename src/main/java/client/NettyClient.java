package client;

import com.test.proto.MsgInfo.MsgBody;

import handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyClient {
	
	
	private Channel thisChannel;
	
	
	public NettyClient () {
		thisChannel = null;
	}
	
	
	public Channel doConnect(String inetHost, int inetPor) {
		connect(inetHost, inetPor);
		return this.thisChannel;
	}
	
	
    private void connect(String inetHost, int inetPort) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {

					
					
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(MsgBody.getDefaultInstance()));
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
			        // 在管道中添加我们自己的接收数据实现方法
					ch.pipeline().addLast(new ClientHandler());
				}
			});

            
            ChannelFuture future = b.connect(inetHost, inetPort).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        //启动控制台输入
                    	startConsole(channelFuture.channel());
                    }else{
                        System.exit(0);
                    }
                }
            });
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    
    
    private static void startConsole(Channel channel){
        BootStrap scan = new BootStrap(channel);
        Thread thread = new Thread(scan);
        thread.setName("scan-thread");
        thread.start();
    }
    
    
    
    
    
    

}