package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.GetCommonParamProto;
import com.test.proto.GoVoteProto;
import com.test.proto.GoVoteProto.GoVoteRequest;
import com.test.proto.GoVoteProto.GoVoteRequest.Builder;
import com.test.proto.MsgInfo.MsgBody;

import io.netty.channel.Channel;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.ByteUtil;
import util.CacheUtil;
import util.MsgUtil;

public class GoVoteListener implements ActionListener {
	private JTextField voteBinary;
	
	
	
	private static final String commonGyiFile = "commonGyiFile.data";
	private static final String privateKeyFile = "sk.data";
	private static final String generatorFile = "generatorG1.data";
	private Channel channel;
	public static GoVoteListener instance = null;
	
	public static GoVoteListener getInstance() {
		if (instance == null) {
			instance =  new GoVoteListener();
		}
		
		return instance;
	}
	
	private GoVoteListener() {
		
	}
	
	public void setProperty(Channel f, JTextField _voteBinary) {
		this.channel = f;
		voteBinary = _voteBinary;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		//1 先得到参数
		//1.1  投票者编号  1.2 私钥  1.3 投票决定
		int voteIdInteger = CacheUtil.gVoteId;
		
		
		// 投票决定是否为空
		String vote = voteBinary.getText();
		if (vote.trim().equalsIgnoreCase("")) {
			//return
			JOptionPane.showMessageDialog(null,"先投票！");
			return;
		}
		int voteInteger = Integer.valueOf(vote);
		System.out.println(voteInteger);
		if (voteInteger != 1 && voteInteger != 0) {
			JOptionPane.showMessageDialog(null, "投票必须是1或者0!");
			return;
		}
		
		
		
		//是否已经拉取公共参数了？
		if (CacheUtil.gGeneratorG1 == null || CacheUtil.gSelectionId == "") {
			JOptionPane.showMessageDialog(null, "请先拉取公共参数!");
			return;
		}
		
		
		
		
		
		//2 自己计算。 
		//2.1 计算gid
		//2.2 g_yi
		//2.3 pairing对。
		MessageDigest md;
		Element hash_G_2 = CacheUtil.gPairing.getG2().newRandomElement();
		try {
			// Static getInstance method is called with hashing SHA 
	        md = MessageDigest.getInstance("SHA-256"); 
	        byte[] messageDigest = md.digest(CacheUtil.gSelectionId.getBytes()); 
	        hash_G_2 = CacheUtil.gPairing.getG2().newElement().setFromHash(messageDigest, 0, messageDigest.length);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return;
		};
		
		

		
		// 先得到g_y_i;
		byte[] commonGyiKeyBytes;
		try {
			commonGyiKeyBytes = util.FileUtil.ReadFromFile(commonGyiFile);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		};
		
		
		
		// 先得到私钥。
		byte[] privateKeyBytes;
		try {
			privateKeyBytes = util.FileUtil.ReadFromFile(privateKeyFile);
		} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
		};


		Element privateKey = CacheUtil.gPairing.getZr().newElementFromBytes(privateKeyBytes);
		Element commonGyiKey = CacheUtil.gPairing.getG1().newElementFromBytes(commonGyiKeyBytes);
		
		
		System.out.println("privateKey: " + privateKey + 
				" commonGyiKey: " + commonGyiKey + 
				" genenator: " + CacheUtil.gGeneratorG1);
		

		
		// 2.2 再生成一个零知识证明
		//gid: e(g, hash)
		//h: e(gyi, hash)
		//sk
		Element gid = CacheUtil.gPairing.pairing(CacheUtil.gGeneratorG1, hash_G_2);
		Element h = CacheUtil.gPairing.pairing(commonGyiKey, hash_G_2);
		System.out.println("commonKeys: " + commonGyiKey + " hash_G2: " + hash_G_2);
		System.out.println("voteid: " + voteIdInteger + " gid: " + gid + " h: " + h);
		
		
		GoVoteProto.GoVoteRequest.Builder b = GoVoteProto.GoVoteRequest.newBuilder();
		if (voteInteger == 0) {
			generatorVote0(b, voteIdInteger, md, privateKey, h, gid);
			
		} else {
			generatorVote1(b, voteIdInteger, md, privateKey, h, gid);
			
		}
		
		
		b.setSelectionId(CacheUtil.gSelectionId);
		GoVoteProto.GoVoteRequest request = b.build();
		
		
		//3 把投票信息  化为bytes 发送过去。
		this.channel.writeAndFlush(MsgUtil.build(6,  request.toByteArray()));
		
		
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		try {
//			request.writeTo(out);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return;
//		}
//		
//		
//		
//		
//		
//		OkHttpClient client = new OkHttpClient();
//		String url = "http://localhost:6889/goVote";
//		RequestBody body = RequestBody.create(out.toByteArray());
//	    Request httpRequest = new Request.Builder().url(url)
//	    		.post(body)
//	    		.build();
//
//	    
//	    
//	    //4 打印出结果。
//	    Response response;
//		try {
//			response = client.newCall(httpRequest).execute();
//			
//			String ans = response.body().string();
//			GoVoteProto.GoVoteResponse responseProto = 
//					GoVoteProto.GoVoteResponse.parseFrom(ans.getBytes("iso-8859-1"));
//			
//			int statusCode = responseProto.getStatusCode();
//			if (statusCode == 0) {
//				JOptionPane.showMessageDialog(null,"投票成功");
//				
//			} else if (statusCode == 1){
//				JOptionPane.showMessageDialog(null, "重复投票！ 请拉取最新选举状态!");
//				
//			} else {
//				JOptionPane.showMessageDialog(null, responseProto.getExtra());
//				
//			}
//			
//			
//			
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return;
//		}

	}
	
	
	public static void afterGoVoteSuccess(MsgBody body) {
		try {
			//1 反序列化
			GoVoteProto.GoVoteResponse response = GoVoteProto.GoVoteResponse.parseFrom(body.getContent().toByteArray());
			
			
			//2 提示消息
			int statusCode = response.getStatusCode();
			if (statusCode == 0) {
				JOptionPane.showMessageDialog(null,"投票成功!");
				
			} else if (statusCode == 1){
				JOptionPane.showMessageDialog(null, "重复投票！ 请拉取最新选举状态!");
				
			} else {
				JOptionPane.showMessageDialog(null, "投票失败! " + response.getExtra());
				 
			}
			
			
			
			
			
			
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "GoVoteResponse 反序列化失败！");
		}
		
		
	}
	
	
	
	
	
	
	//假设vote决定是1的话，调用这个
	//@param  sk   私钥
	//@param  h    e(gyi,  hash)
	//@param  gid  e(g, hash)
	//@param voteid  投票者id
	//@param messageDigest md  hash256
	//@return GoVoteRequest
	private void generatorVote1(GoVoteProto.GoVoteRequest.Builder b, int voteid, MessageDigest md,
			Element sk, Element h, Element gid) {
		//0  初始化
		Pairing pairing = CacheUtil.gPairing;
		
		
		
		//1 w r1 d1
		Element w = pairing.getZr().newRandomElement();
		Element r1 = pairing.getZr().newRandomElement();
		Element d1 = pairing.getZr().newRandomElement();
		
		
		
		
		//2 x y
		Element x = gid.getImmutable().powZn(sk);
		Element y = h.getImmutable().powZn(sk).getImmutable().mul(gid);
		
		
		
		
		
		//3 a1 b1
		Element a1Left = gid.getImmutable().powZn(r1);
		Element a1Right = x.getImmutable().powZn(d1);
		Element a1 = a1Left.getImmutable().mul(a1Right);
		
		Element b1L = h.getImmutable().powZn(r1);
		Element b1R = y.getImmutable().powZn(d1);
		Element b1 = b1L.getImmutable().mul(b1R);
		
		
		
		
		//4 a2 b2
		Element a2 = gid.getImmutable().powZn(w);
		Element b2 = h.getImmutable().powZn(w);
		
		
		
		
		//5 c
		byte[] c = md.digest(ByteUtil.byteMergerAll(ByteUtil.toLH(voteid),
				x.toBytes(),
				y.toBytes(),
				a1.toBytes(),
				b1.toBytes(),
				a2.toBytes(),
				b2.toBytes()));
		
		Element ce = pairing.getZr().newElementFromBytes(c);
		
		
		
		
		
		
		//6 d2 r2
		Element d2 = ce.getImmutable().add(d1.getImmutable().negate());
		Element r2R = sk.getImmutable().mul(d2);
		Element r2 = w.getImmutable().add(r2R.getImmutable().negate());
		
		
		
		//打印出来。
		System.out.println("w: " + w + 
				"r1: " + r1 +
				"r2: " + r2 +
				"a1: " + a1 + 
				"b1: " + b1 +
				"a2: " + a2 + 
				"b2: " + b2 + 
				"d1: " + d1 + 
				"d2: " + d2 + 
				"c: " + c);
	
		
		
		//Builder b = GoVoteProto.GoVoteRequest.newBuilder();
		b.setVoteId(voteid);
		b.setW(ByteString.copyFrom(w.toBytes()));
		b.setR1(ByteString.copyFrom(r1.toBytes()));
		b.setR2(ByteString.copyFrom(r2.toBytes()));
		b.setX(ByteString.copyFrom(x.toBytes()));
		b.setY(ByteString.copyFrom(y.toBytes()));
		b.setA1(ByteString.copyFrom(a1.toBytes()));
		b.setB1(ByteString.copyFrom(b1.toBytes()));
		b.setA2(ByteString.copyFrom(a2.toBytes()));
		b.setB2(ByteString.copyFrom(b2.toBytes()));
		//c 自己计算
		b.setD1(ByteString.copyFrom(d1.toBytes()));
		b.setD2(ByteString.copyFrom(d2.toBytes()));
		//return b.build();
	}
	
	
	//@同上， 只不过vote是0
	private void generatorVote0(GoVoteProto.GoVoteRequest.Builder b, int voteid, MessageDigest md,
			Element sk, Element h, Element gid) {
		//0 先初始化
		Pairing pairing = CacheUtil.gPairing;
		
		
		//1 先求 w r2 d2
		Element w = pairing.getZr().newRandomElement();
		Element r2 = pairing.getZr().newRandomElement();
		Element d2 = pairing.getZr().newRandomElement();
		
		
		
		
		//2  求x，y
		Element x = gid.getImmutable().powZn(sk);
		Element y = h.getImmutable().powZn(sk);
		
		
		
		
		
		//3 求a1 b1
		Element a1 = gid.getImmutable().powZn(w);
		Element b1 = h.getImmutable().powZn(w);
		
		
		
		
		
		//4 求a2 b2
		Element a2Left = gid.getImmutable().powZn(r2);
		Element a2Right = x.getImmutable().powZn(d2);
		Element a2 = a2Left.getImmutable().mul(a2Right);
		
		
		Element b2Left = h.getImmutable().powZn(r2);
		Element b2Mid = y.getImmutable().div(gid);
		b2Mid = b2Mid.powZn(d2);
		Element b2 = b2Left.getImmutable().mul(b2Mid);
		
		
		
		
		
		
		//5 求c
		byte[] c = md.digest(ByteUtil.byteMergerAll(ByteUtil.toLH(voteid),
				x.toBytes(),
				y.toBytes(),
				a1.toBytes(),
				b1.toBytes(),
				a2.toBytes(),
				b2.toBytes()));
		Element ce = pairing.getZr().newElementFromBytes(c);
		
		
		
		//6 求d1
		Element d1 = ce.getImmutable().add(d2.getImmutable().negate());
		
		
		
		//7 求r1
		Element r1R = sk.getImmutable().mul(d1);
		Element r1 = w.getImmutable().add(r1R.getImmutable().negate());
		
		
		
		
		//打印出来。
		System.out.println("w: " + w + 
				"r1: " + r1 +
				"r2: " + r2 +
				"a1: " + a1 + 
				"b1: " + b1 +
				"a2: " + a2 + 
				"b2: " + b2 + 
				"d1: " + d1 + 
				"d2: " + d2 + 
				"c: " + c);
		
		//Builder b = GoVoteProto.GoVoteRequest.newBuilder();
		b.setVoteId(voteid);
		b.setW(ByteString.copyFrom(w.toBytes()));
		b.setR1(ByteString.copyFrom(r1.toBytes()));
		b.setR2(ByteString.copyFrom(r2.toBytes()));
		b.setX(ByteString.copyFrom(x.toBytes()));
		b.setY(ByteString.copyFrom(y.toBytes()));
		b.setA1(ByteString.copyFrom(a1.toBytes()));
		b.setB1(ByteString.copyFrom(b1.toBytes()));
		b.setA2(ByteString.copyFrom(a2.toBytes()));
		b.setB2(ByteString.copyFrom(b2.toBytes()));
		//c 自己计算
		b.setD1(ByteString.copyFrom(d1.toBytes()));
		b.setD2(ByteString.copyFrom(d2.toBytes()));
		
		
		//@test 设置错误的d
		//pass.
		//b.setD1(ByteString.copyFrom(pairing.getZr().newRandomElement().toBytes()));
		
		//return b.build();
	}
	
	
	

}
