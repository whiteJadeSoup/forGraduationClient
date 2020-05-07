package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.test.proto.MsgInfo.MsgBody;

import client.BoardCastPrivateKeyListener;
import client.CalCommonGyiListener;
import client.GetCommonParamListener;
import client.GetVoteResultListener;
import client.GoVoteListener;
import client.LoginListener;
import client.RegisterListener;
import io.netty.channel.ChannelHandler;


public class ClientHandler extends ChannelInboundHandlerAdapter {
	

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收msg消息{与上一章节相比，此处已经不需要自己进行解码}
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息类型：" + msg.getClass());
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息内容：" + JsonFormat.printToString((MsgBody) msg));
    	
    	MsgBody body = (MsgBody) (msg);
    	
    	
    	switch (body.getCommand()) {
    	case 1:
    		// 登录请求。
    		doLoginResponse(body);
    		break;
    		
    		
    	case 2:
    		// 注册请求
    		doRegisterResponse(body);
    		break;
    	
    	case 3:
    		// 拉取公共参数
    		doGetCommonParamResponse(body);
    		break;
    		
    		
    	case 4:
    		// 传播公约
    		doGetBoardCastResponse(body);
    		break;
    		
    		
    	case 5:
    		// 计算共同公钥。
    		doCalCommonYiResponse(body);
    		break;
    		
    		
    	case 6:
    		//投票
    		doGoVoteResponse(body);
    		break;
    		
    	case 7:
    		// 得到投票结果
    		doGetVoteResultResponse(body);
    		break;
    		
    	default:
    		System.out.println("未知指令!");
    		break;
    		
    	}
    	
    	
    	
    }
    
    private void doGetVoteResultResponse(MsgBody body) {
    	System.out.println("do doGetVoteResultResponse Response");
    	GetVoteResultListener.afterGetVoteResultSuccess(body);//afterGoVoteSuccess(body);//CalCommonGyiListener.afterCalSuccess(body);
    }
    
    
    
    private void doGoVoteResponse(MsgBody body) {
    	System.out.println("do doGoVoteResponse Response");
    	GoVoteListener.afterGoVoteSuccess(body);//CalCommonGyiListener.afterCalSuccess(body);
    }
    
    
    private void doCalCommonYiResponse(MsgBody body) {
    	System.out.println("do doCalCommonYiResponse Response");
    	CalCommonGyiListener.afterCalSuccess(body);
    	
    }
    
    
    
    private void doGetCommonParamResponse(MsgBody body) {
    	System.out.println("do doGetCommonParamResponse Response");
    	GetCommonParamListener.afterGetCommonParamSuccess(body);
    }
    
    
    private void doGetBoardCastResponse(MsgBody body) {
    	System.out.println("do doGetBoardCastResponse Response");
    	BoardCastPrivateKeyListener.afterBoardCastSuccess(body);//.afterGetCommonParamSuccess(body);;
    }
    
    
    
    
    private void doRegisterResponse(MsgBody body) {
    	System.out.println("do doRegisterResponse Response");
    	RegisterListener.afterRegisterSuccess(body);
    }
    
    
    private void doLoginResponse(MsgBody body) {
    	System.out.println("do login Response");
    	LoginListener.afterLoginSuccess(body);
    }
    
    
    
    
    
    
    

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }
	
}
