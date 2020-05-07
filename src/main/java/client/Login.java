package client;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import io.netty.channel.Channel;
import util.CacheUtil;

public class Login {
	
	private Channel thisChannel;
	
	
	
	public static void main(String args[]) throws Exception {
		//初始化
		CacheUtil.init();
		new NettyClient().doConnect("127.0.0.1", 6889);
		
		
	}
	
	
	
	public Login() {
		
	}
	
	public Login(Channel channel) {
		this.thisChannel = channel;
	}
	
	
	
	
	public void showUI() {

		javax.swing.JFrame jf = new javax.swing.JFrame();
		//@todo: 改成中文
		jf.setTitle("Login");
		jf.setSize(340, 300);
		jf.setDefaultCloseOperation(3);
		jf.setLocationRelativeTo(null); //居中
		jf.setResizable(false);
		
		
		
		
		
		java.awt.Dimension labelSize = new java.awt.Dimension(50, 50);//标签的大小
		java.awt.Dimension inputSize = new java.awt.Dimension(250, 30);//输入框的大小		
		java.awt.Dimension buttonSize = new java.awt.Dimension(100, 40);//按钮的大小	
		
		
		//设置布局
		java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.CENTER, 5, 5);
		jf.setLayout(layout);
		
		
		//投票者编号
		JLabel labname = new JLabel();
		labname.setText("编号: ");
		labname.setPreferredSize(labelSize);
		jf.add(labname);
		
		
		
		//输入框
		JTextField textname = new JTextField();
		textname.setPreferredSize(inputSize);
		jf.add(textname);
		
		//
		
		
		//密码标签
		JLabel labpassword = new JLabel();
		labpassword.setText("密码： ");
		labpassword.setPreferredSize(labelSize);
		jf.add(labpassword);
		
		//输入框
		JPasswordField jp = new JPasswordField();
		jp.setPreferredSize(inputSize);
		jf.add(jp);
		
		
		
		//添加一个登录按钮
		javax.swing.JButton loginButton = new javax.swing.JButton();
		loginButton.setText("登录");
		loginButton.setPreferredSize(buttonSize);
		jf.add(loginButton);
		
		
		//添加一个注册按钮
		javax.swing.JButton registerButton = new javax.swing.JButton();
		registerButton.setText("注册");
		registerButton.setPreferredSize(buttonSize);
		jf.add(registerButton);
		
		
		jf.setVisible(true);
		
		
		
		
		
		//登录按钮这个需要注册监听事件
		LoginListener ll = LoginListener.getInstance();
		ll.setProperty(thisChannel, jf, textname, jp);
		
		//LoginListener ll = new LoginListener(thisChannel, jf, textname, jp);
		loginButton.addActionListener(ll);
		
		
		//对注册也是
		RegisterListener registerListener = RegisterListener.getInstance();
		registerListener.setProperty(thisChannel, jp);
		registerButton.addActionListener(registerListener);
		
		
		
	}
	

}
