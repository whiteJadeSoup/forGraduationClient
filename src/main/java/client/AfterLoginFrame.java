package client;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.netty.channel.Channel;
import util.CacheUtil;

public class AfterLoginFrame extends JFrame {
	
	private Channel thisChannel;
	public AfterLoginFrame(Channel f) {
		this.thisChannel = f;
	}
	
	
	public void ShowUI() {
		setTitle("投票者编号: " + CacheUtil.gVoteId);
		//setSize(340,500);//只对顶级容器有效
		setDefaultCloseOperation(3);//窗体关闭时结束程序
		setLocationRelativeTo(null);//居中
		//setResizable(false);
		setVisible(true);
		
		
		getContentPane().setLayout(new GridLayout(5, 1));
		
		
		//第一个panel
		JPanel mJPanel1 = new JPanel();
		mJPanel1.setLayout(new GridLayout(1, 4));
		
		//第一个panel的按钮
		//显示选举
		JLabel selectionIdTips = new JLabel("当前选举id: ");
		JLabel selectionId = new JLabel();
		selectionId.setBounds(60, 10, 50, 20);
		
		
		JLabel deadLineTips = new JLabel("选举截止时间： ");
		JLabel deadLine = new JLabel();
		deadLine.setBounds(60, 10, 40, 20);
		
		
		mJPanel1.add(selectionIdTips);
		mJPanel1.add(selectionId);
		mJPanel1.add(deadLineTips);
		mJPanel1.add(deadLine);
		getContentPane().add(mJPanel1);
		
		
		
		
		
		// 第二个panel
		// 拉取公共参数 、 传播公钥、 公共公钥
		JPanel mJPanel2 = new JPanel();
		mJPanel2.setLayout(new FlowLayout());
		
		JButton getCommonParam = new JButton("获得公共参数");
		JButton boradcastBtn = new JButton("公布公钥");
		JButton calBtn = new JButton("计算公共公钥") ;
		
		mJPanel2.add(getCommonParam);
		mJPanel2.add(boradcastBtn);
		mJPanel2.add(calBtn);
		getContentPane().add(mJPanel2);
		
		
		// 3 拉取公共参数listener
		GetCommonParamListener getCommonParamListener = GetCommonParamListener.getInstance();
		getCommonParamListener.setProperty(this.thisChannel, selectionId, deadLine);
		getCommonParam.addActionListener(getCommonParamListener);
		
		// boardCastPrivateKeyListener 4
		BoardCastPrivateKeyListener boardCastPrivateKeyListener = BoardCastPrivateKeyListener.getInstance();
		boardCastPrivateKeyListener.setProperty(this.thisChannel);
		boradcastBtn.addActionListener(boardCastPrivateKeyListener);
		
		// 5： CalCommonGyiListener
		CalCommonGyiListener calCommonGyiListener = CalCommonGyiListener.getInstance();
		calCommonGyiListener.setProperty(thisChannel);
		calBtn.addActionListener(calCommonGyiListener);
		
		
		
		
		JPanel mJPanel3 = new JPanel();
		mJPanel3.setLayout(new FlowLayout());
		
		JButton voteBtn = new JButton("投票");
		JLabel voteDecision = new JLabel("投票决定: ");
		JTextField decisionField = new JTextField(5);
		
		mJPanel3.add(voteBtn);
		mJPanel3.add(voteDecision);
		mJPanel3.add(decisionField);
		getContentPane().add(mJPanel3);
		
		
		//6 GoVoteListener
		GoVoteListener goVoteListener = GoVoteListener.getInstance();
		goVoteListener.setProperty(thisChannel, decisionField);
		voteBtn.addActionListener(goVoteListener);
		
		
		
		
		
		JPanel mJPanel5 = new JPanel();
		mJPanel5.setLayout(new FlowLayout());
		
		JButton getVoteResultBtn = new JButton("计算投票结果") ;
		mJPanel5.add(getVoteResultBtn);
		getContentPane().add(mJPanel5);
		
		
		
		GetVoteResultListener getVoteResultListener = GetVoteResultListener.getInstance();
		getVoteResultListener.setProperty(thisChannel);
		getVoteResultBtn.addActionListener(getVoteResultListener);
		
		
		this.pack();
		
		
		
		

		
	}
	
}
