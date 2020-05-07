package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.GetCommonParamProto;
import com.test.proto.GetCommonParamProto.GetCommonParamRequest.Builder;
import com.test.proto.MsgInfo.MsgBody;

import io.netty.channel.Channel;
import it.unisa.dia.gas.jpbc.Element;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.CacheUtil;
import util.MsgUtil;

public class GetCommonParamListener implements ActionListener {
	
	private Channel channel;
	private JLabel selecionId;
	private JLabel deadLine;
	public static GetCommonParamListener instance = null;
	
	
	public static GetCommonParamListener getInstance() {
		if (instance == null) {
			instance =  new GetCommonParamListener();
		}
		
		return instance;
	}
	
	private GetCommonParamListener() {
		
	}
	
	public void setProperty(Channel f, JLabel _id, JLabel deadLine) {
		this.channel = f;
		this.selecionId = _id;
		this.deadLine = deadLine;
	}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		
		// 1 构建消息体
		Builder b = GetCommonParamProto.GetCommonParamRequest.newBuilder();
		b.setVoteId(CacheUtil.gVoteId);
		
		
		GetCommonParamProto.GetCommonParamRequest request = b.build();
		
		
		
		// 2 发送
		this.channel.writeAndFlush(MsgUtil.build(3, request.toByteArray()));
		
	}
	
	
	
	
	public static void afterGetCommonParamSuccess(MsgBody msg) {
		//1 先反序列化
		GetCommonParamProto.GetCommonParamResponse response;
		try {
			response = GetCommonParamProto.GetCommonParamResponse.parseFrom(msg.getContent());
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "拉取公共参数反序列化失败！！");
			return;
		}
		
		
		
		
		// 2 
		
		if (response.getStatusCode() != 0) {
			System.out.println("GetCommonParamProto error!");
			JOptionPane.showMessageDialog(null, "拉取公共参数失败！");
			
		} else {
			
			
			String resId;
			String deadLine;
			try {
				resId = new String (response.getSelectionId().toByteArray(), "iso-8859-1");
				deadLine = response.getDeadLine();
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "string 反序列化失败！");
				return;
				
			}
			
			Element generator = CacheUtil.gPairing.getG1().newElementFromBytes(response.getGeneratorG1().toByteArray());
			
			
			System.out.println("selectionId: " + resId + " generatorG1: " + generator);
			JOptionPane.showMessageDialog(null, "拉取公共参数成功！");
			
			CacheUtil.gSelectionId = resId;
			CacheUtil.gGeneratorG1 = generator;
			
			
			// 更新label
			GetCommonParamListener.instance.selecionId.setText(resId);
			GetCommonParamListener.instance.selecionId.setToolTipText(resId);
			
			
			
			GetCommonParamListener.instance.deadLine.setText(deadLine);
			GetCommonParamListener.instance.deadLine.setToolTipText(deadLine);
		}	
		
	}
	
	
	

}
