import com.test.proto.ChangeSelectionInfo;
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
import util.MsgUtil;

public class ChangeSeletionId {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChangeSeletionId session = new ChangeSeletionId();
		session.connect("127.0.0.1", 6889);
		
		
	}

	 public void connect(String inetHost, int inetPort) {
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
						//ch.pipeline().addLast(new ClientHandler());
					}
				});

	            
	            ChannelFuture future = b.connect(inetHost, inetPort).addListener(new ChannelFutureListener() {
	                public void operationComplete(ChannelFuture channelFuture) throws Exception {
	                	//直接发送修改selectionid的请求
	                	Channel ctx = channelFuture.channel();
	                	
	                	ChangeSelectionInfo.ChangeSelectionRequest.Builder b = ChangeSelectionInfo.ChangeSelectionRequest.newBuilder();
	                	ChangeSelectionInfo.ChangeSelectionRequest request = b.build();
	                	
	                	ctx.writeAndFlush(MsgUtil.build(8, request.toByteArray()));
	                	ctx.close();
	                	
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
	
	
}
