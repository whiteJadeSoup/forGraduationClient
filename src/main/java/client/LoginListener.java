package client;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class LoginListener implements ActionListener {
	
	//账号密码输入
	private javax.swing.JTextField jUserid;
	private javax.swing.JPasswordField jPassword;
	private javax.swing.JFrame login;
	
	public LoginListener(javax.swing.JFrame login, javax.swing.JTextField jt, javax.swing.JPasswordField jp) {
		this.login = login;
		this.jUserid = jt;
		this.jPassword = jp;
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		// todo 进行登录的逻辑验证。
		//
		
		
		new AfterLoginFrame();
		
		login.dispose();
		
	}
	
	
	
	
	
}
