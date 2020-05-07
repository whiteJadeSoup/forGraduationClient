package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.GetCommonParamProto;
import com.test.proto.GetCommonParamProto.GetCommonParamRequest.Builder;
import com.test.proto.GetVoteResultProto;
import com.test.proto.GoVoteProto;
import com.test.proto.MsgInfo.MsgBody;

import io.netty.channel.Channel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.CacheUtil;
import util.MsgUtil;

public class GetVoteResultListener implements ActionListener {
	private Channel channel;
	public static GetVoteResultListener instance = null;
	
	public static GetVoteResultListener getInstance() {
		if (instance == null) {
			instance =  new GetVoteResultListener();
		}
		
		return instance;
	}
	
	private GetVoteResultListener() {
		
	}
	
	public void setProperty(Channel f) {
		this.channel = f;
	}
	

	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		//1 构建消息体 发送
		GetVoteResultProto.GetVoteResultRequest.Builder b = 
				GetVoteResultProto.GetVoteResultRequest.newBuilder();
		b.setSelectionId(CacheUtil.gSelectionId);
		b.setVoteId(CacheUtil.gVoteId);
		
		
		GetVoteResultProto.GetVoteResultRequest request = b.build();
		System.out.println("request selection id： " + request.getSelectionId());
		
		
		
		
		this.channel.writeAndFlush(MsgUtil.build(7,  request.toByteArray()));
		
		
		
//		// 1 发送请求得到投票结果。
//		OkHttpClient client = new OkHttpClient();
//		String url = "http://localhost:6889/getVoteResult";
//		RequestBody body = RequestBody.create(out.toByteArray());
//	    Request httpRequest = new Request.Builder().url(url)
//	    		.get()
//	    		.build();
//	    
//	    
//	    
//	    
//	    
//	    Response response;
//		try {
//			response = client.newCall(httpRequest).execute();
//			String ans =  response.body().string();
//			
//			
//			
//			
//			GetVoteResultProto.GetVoteResultResponse responseProto = 
//					GetVoteResultProto.GetVoteResultResponse.parseFrom(ans.getBytes("iso-8859-1"));
//			
//			int statusCode = responseProto.getStatusCode();
//			if (statusCode == 0) {
//				System.out.println("response selection id: " + responseProto.getSelectionId() + " 投票结果为: " + responseProto.getVoteResult());
//				JOptionPane.showMessageDialog(null, " 投票结果为: " + responseProto.getVoteResult());
//				
//			} else {
//				System.out.print("response selection id: " + responseProto.getSelectionId()  + " 失败！ " + responseProto.getExtra());
//				JOptionPane.showMessageDialog(null, " 失败 "  + responseProto.getExtra());
//				
//			}
//
//			
//			
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
	}
	
	
	
	
	public static void afterGetVoteResultSuccess(MsgBody body) {
		//1 反序列化
		GetVoteResultProto.GetVoteResultResponse responseProto;
		try {
			responseProto = GetVoteResultProto.GetVoteResultResponse.parseFrom(body.getContent().toByteArray());
			
			int statusCode = responseProto.getStatusCode();
			if (statusCode == 0) {
				System.out.println("response selection id: " + responseProto.getSelectionId() + " 投票结果为: " + responseProto.getVoteResult());
				JOptionPane.showMessageDialog(null, " 投票结果为: " + responseProto.getVoteResult());
				
			} else {
				System.out.print("response selection id: " + responseProto.getSelectionId()  + " 失败！ " + responseProto.getExtra());
				JOptionPane.showMessageDialog(null, " 失败 "  + responseProto.getExtra());
				
			}	
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "GetVoteResultProto 反序列化失败！");
		}
	}
	
	
	
	
	
	
	
	
	

}
