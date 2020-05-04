package client;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AfterLoginFrame extends JFrame {
	
	public AfterLoginFrame() {
		setTitle("登录后的用户界面");
		//setSize(340,500);//只对顶级容器有效
		setDefaultCloseOperation(3);//窗体关闭时结束程序
		setLocationRelativeTo(null);//居中
		//setResizable(false);
		setVisible(true);
		
		
		getContentPane().setLayout(new GridLayout(5, 1));
		
		
		//第一个panel
		JPanel mJPanel1 = new JPanel();
		mJPanel1.setLayout(new FlowLayout());
		
		//第一个panel的按钮
		JButton getCommonParam = new JButton("获得公共参数");
		mJPanel1.add(getCommonParam);
		
		getContentPane().add(mJPanel1);
		getCommonParam.addActionListener(new GetCommonParamListener());
		
		
		
		//第二个panel
		JPanel mJPanel2 = new JPanel();
		mJPanel2.setLayout(new FlowLayout());
		
		JButton boradcastBtn = new JButton("公布公钥");
		JLabel voterNumber = new JLabel("投票者id: ");
		JTextField mNameTextField = new JTextField(20);
		mJPanel2.add(boradcastBtn);
		mJPanel2.add(voterNumber);
		mJPanel2.add(mNameTextField);
		
		getContentPane().add(mJPanel2);
		boradcastBtn.addActionListener(new BoardCastPrivateKeyListener(mNameTextField));
		
		
		JPanel mJPanel4 = new JPanel();
		mJPanel4.setLayout(new FlowLayout());
		
		JButton calBtn = new JButton("计算g_{y_{i}}") ;
		mJPanel4.add(calBtn);
		
		
		//@todo 去掉。 
		JTextField voteID = new JTextField(10);
		mJPanel4.add(voteID);
		getContentPane().add(mJPanel4);
		
		calBtn.addActionListener(new CalCommonGyiListener(voteID));
		
		
		
		
		
		
		
		
		JPanel mJPanel3 = new JPanel();
		mJPanel3.setLayout(new FlowLayout());
		
		JButton voteBtn = new JButton("投票");
		JLabel voterNumber2 = new JLabel("投票者id: ");
		JTextField mNameTextField2 = new JTextField(10);
		mJPanel3.add(voteBtn);
		mJPanel3.add(voterNumber2);
		mJPanel3.add(mNameTextField2);
		
		
		
		JLabel voteDecision = new JLabel("投票决定: ");
		JTextField decisionField = new JTextField(5);
		mJPanel3.add(voteDecision);
		mJPanel3.add(decisionField);
		getContentPane().add(mJPanel3);
		
		voteBtn.addActionListener(new GoVoteListener(mNameTextField2,  decisionField));
		
		
		JPanel mJPanel5 = new JPanel();
		mJPanel5.setLayout(new FlowLayout());
		
		JButton getVoteResultBtn = new JButton("计算投票结果") ;
		mJPanel5.add(getVoteResultBtn);
		getContentPane().add(mJPanel5);
		
		getVoteResultBtn.addActionListener(new GetVoteResultListener());
		
		
		this.pack();
		
		
		
		

		
	}
	
}
