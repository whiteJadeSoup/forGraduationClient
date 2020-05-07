package client;



import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.LoginInfo;
import com.test.proto.MsgInfo.MsgBody;

import io.netty.channel.Channel;
import util.CacheUtil;
import util.MsgUtil;

import java.awt.event.ActionEvent;


public class LoginListener implements ActionListener {
	
	//账号密码输入
	private javax.swing.JTextField jUserid;
	private javax.swing.JPasswordField jPassword;
	private javax.swing.JFrame login;
	private Channel channel;
	
	public static LoginListener instance = null;
	
	
	public static LoginListener getInstance() {
		if (instance == null) {
			instance =  new LoginListener();
		}
		
		return instance;
	}
	
	private LoginListener() {
		
	}
	
	public void setProperty(Channel f, javax.swing.JFrame login, javax.swing.JTextField jt, javax.swing.JPasswordField jp) {
		this.login = login;
		this.jUserid = jt;
		this.jPassword = jp;
		this.channel = f;
	}
	
	
	
	public LoginListener(Channel f, javax.swing.JFrame login, javax.swing.JTextField jt, javax.swing.JPasswordField jp) {
		this.login = login;
		this.jUserid = jt;
		this.jPassword = jp;
		this.channel = f;
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		// todo 进行登录的逻辑验证。
		// 发送请求。 
		
		//
		String id = this.jUserid.getText();
		if (id.trim().equalsIgnoreCase("")) {
			//没有填id
			JOptionPane.showMessageDialog(null, "请填写id");
			return;
		}
		
		
		//
		int idInt;
		try {
			idInt = Integer.valueOf(id);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "id必须是数字");
			return;
			
		}
		
		
		
		
		String password = this.jPassword.getText();
		if (password.trim().equalsIgnoreCase("")) {
			// 没有写密码
			JOptionPane.showMessageDialog(null, "请填写密码!");
			return;
			
		}
		
		
		
		
		LoginInfo.LoginRequest.Builder b = LoginInfo.LoginRequest.newBuilder();
		b.setVoteId(idInt);
		b.setPassword(password.trim());
		
		LoginInfo.LoginRequest request = b.build();
		this.channel.writeAndFlush(MsgUtil.build(1, request.toByteArray()));
	}
	
	
	
	
	public static void afterLoginSuccess(MsgBody msg) {
		
		//1 反序列化
		LoginInfo.LoginResponse response;
		try {
			response = LoginInfo.LoginResponse.parseFrom(msg.getContent().toByteArray());
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
		
		
		
		
		
		//3 新的界面
		JOptionPane.showMessageDialog(null, "登录成功!");
		CacheUtil.gVoteId = response.getVoteId();
		
		AfterLoginFrame afterLogin = new AfterLoginFrame(LoginListener.getInstance().channel);
		afterLogin.ShowUI();
		
		LoginListener.instance.login.dispose();
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
