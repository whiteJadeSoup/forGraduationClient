package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.LoginInfo;
import com.test.proto.MsgInfo.MsgBody;
import com.test.proto.RegisterInfo;

import io.netty.channel.Channel;
import util.CacheUtil;
import util.MsgUtil;

public class RegisterListener implements ActionListener {

	private javax.swing.JPasswordField jPassword;
	private Channel channel;
	
	public static RegisterListener instance = null;
	
	
	public static RegisterListener getInstance() {
		if (instance == null) {
			instance =  new RegisterListener();
		}
		
		return instance;
	}
	
	
	private RegisterListener() {
		
	}
	
	
	
	public void setProperty(Channel f,  javax.swing.JPasswordField jp) {
		this.jPassword = jp;
		this.channel = f;
	}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//1 是否填写了密码?
		// TODO Auto-generated method stub
		String password = this.jPassword.getText();
		if (password.trim().equalsIgnoreCase("")) {
			// 没有写密码
			JOptionPane.showMessageDialog(null, "请填写密码!");
			return;
			
		}
		
		
		
		
		//2 构建包 发送
		RegisterInfo.RegisterRequest.Builder b = RegisterInfo.RegisterRequest.newBuilder();
		b.setPassword(password);
		
		
		RegisterInfo.RegisterRequest request = b.build();
		this.channel.writeAndFlush(MsgUtil.build(2, request.toByteArray()));
		
	}
	
	
	public static void afterRegisterSuccess(MsgBody msg) {
		
		//1 反序列化
		RegisterInfo.RegisterResponse response;
		try {
			response = RegisterInfo.RegisterResponse.parseFrom(msg.getContent().toByteArray());
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "反序列化失败! 异常!");
			return;
		}
		
		
		
		
		//2 打印出状态
		if (response.getStatusCode() == 1) {
			//错误
			JOptionPane.showMessageDialog(null, response.getExtra());
			return;
			
		}
		
		
		
		//3 告知id
		CacheUtil.gVoteId = response.getVoteId();
		JOptionPane.showMessageDialog(null, "您的投票编号为: " + response.getVoteId());
		
	}
	

}
