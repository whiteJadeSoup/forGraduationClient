package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.test.proto.GetCommonParamProto;
import com.test.proto.MsgInfo.MsgBody;
import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.CalCommonKeyProto;
import com.test.proto.CalCommonKeyProto.CalCommonKeyRequest;

import io.netty.channel.Channel;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.CacheUtil;
import util.MsgUtil;




public class CalCommonGyiListener implements ActionListener {

	private static final String commonGyiFile = "commonGyiFile.data";
	
	private Channel channel;
	public static CalCommonGyiListener instance = null;
	public static CalCommonGyiListener getInstance() {
		if (instance == null) {
			instance =  new CalCommonGyiListener();
		}
		
		return instance;
	}
	
	private CalCommonGyiListener() {
		
	}
	
	public void setProperty(Channel f) {
		this.channel = f;
	}
	
	
	
	
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int voteId = CacheUtil.gVoteId;
		
		
		
		
		//1 是否传播了公钥?
		
		
		
		//2 是否已经拉取公共参数了？
		if (CacheUtil.gGeneratorG1 == null || CacheUtil.gSelectionId == "") {
			JOptionPane.showMessageDialog(null, "请先拉取公共参数!");
			return;
		}
		
		//3 是否本地已经有了文件
		File file = new File(commonGyiFile);
		if (file.exists()) {
			JOptionPane.showMessageDialog(null, "已经拉取了公共公钥了!");
			return;
		}
		
		
		
		
		
		// 4  构建protobuf 并发送
		CalCommonKeyRequest.Builder b = CalCommonKeyRequest.newBuilder();
		b.setVoteId(voteId);
		CalCommonKeyRequest request = b.build();
		
		
		this.channel.writeAndFlush(MsgUtil.build(5, request.toByteArray()));
		
		

		
		
		
		
//		// 3 发送http request
//		OkHttpClient client = new OkHttpClient();
//		String url = "http://localhost:6889/calCommonGyi";
//		RequestBody body = RequestBody.create(out.toByteArray());
//	    Request httpRequest = new Request.Builder().url(url)
//	    		.post(body)
//	    		.build();
//
//	    
//	    
//	    //6 打印出结果。
//	    Response response;
//		try {
//			response = client.newCall(httpRequest).execute();
//			String ans =  response.body().string();
//
//			
//			CalCommonKeyProto.CalCommonKeyResponse responseProto = 
//					CalCommonKeyProto.CalCommonKeyResponse .parseFrom(ans.getBytes("iso-8859-1"));
//			
//			//校对一下ans
//			Element eAns = CacheUtil.gPairing.getG1().newElementFromBytes(responseProto.getCommonKey().toByteArray());
//			System.out.println("voteid: " + voteId +  " commonGyiKey: " + eAns);
//			
//			
//			
//			
//			// 把这个ansBytes保存起来。
//			util.FileUtil.WriteToFile(commonGyiFile, responseProto.getCommonKey().toByteArray());
//			JOptionPane.showMessageDialog(null, "公共公钥已经保存在本地文件了!");
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return;
//		}
//		
	}
	
	
	
	public static void afterCalSuccess(MsgBody body) {
		//1 反序列化
		CalCommonKeyProto.CalCommonKeyResponse response;
		try {
			response = CalCommonKeyProto.CalCommonKeyResponse.parseFrom(body.getContent());
		} catch (InvalidProtocolBufferException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "CalCommonKeyProto 反序列化失败！");
			return;
		}
		
		
		// 2 提示消息 
		if (response.getStatusCode() == 0 || response.getStatusCode() == 1) {
			
			//校对一下ans
			Element eAns = CacheUtil.gPairing.getG1().newElementFromBytes(response.getCommonKey().toByteArray());
			System.out.println("voteid: " + CacheUtil.gVoteId +  " commonGyiKey: " + eAns);
			
			
			
			
			// 把这个ansBytes保存起来。
			try {
				util.FileUtil.WriteToFile(commonGyiFile, response.getCommonKey().toByteArray());
				JOptionPane.showMessageDialog(null, "公共公钥已经保存在本地!");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "CalCommonKeyProto 保存文件失败！");
				
			}
			
			
			
		} else {
			
			System.out.println("error!");
			JOptionPane.showMessageDialog(null,  response.getExtra());
			
			
		}	
		
		
	}
	
	
	
	
	

}
