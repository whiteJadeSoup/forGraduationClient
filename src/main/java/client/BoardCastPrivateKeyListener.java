package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.ByteUtil;
import util.CacheUtil;
import util.FileUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.PublicKeyProto;
import com.test.proto.PublicKeyProto.BoardcastPublicKeyRequest;
import com.test.proto.PublicKeyProto.BoardcastPublicKeyRequest.Builder;
import com.test.proto.PublicKeyProto.BoardcastPublicKeyResponse;


public class BoardCastPrivateKeyListener implements ActionListener {
	private static final String privateKeyFile = "sk.data";
	private static final String generatorFile = "generatorG1.data";
	private JTextField voteId;
	public BoardCastPrivateKeyListener(JTextField _voteId) {
		voteId = _voteId;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		//1 投票者id是否写了?
		//@todo 是否是数字
		String id = voteId.getText();
		if (id.trim().equalsIgnoreCase("")) {
			//return
			JOptionPane.showMessageDialog(null, "先输入id");
			return;
		}
		
		
		
		//是否已经拉取公共参数了？
		if (CacheUtil.gGeneratorG1 == null || CacheUtil.gSelectionId == "") {
			JOptionPane.showMessageDialog(null, "请先拉取公共参数!");
			return;
		}
		
		
		
		
		
		//2 是不是已经有私钥了？
		//是否在当前文件下有了？
		Element privateKey = CacheUtil.gPairing.getZr().newElement();
		File file = new File(privateKeyFile);
		if (file.exists()) {
			//存在了。 读出私钥来
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
				
			}
			
			byte[] privateKeyBytes;
			try {
				privateKeyBytes = in.readAllBytes();
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			
			
			
			privateKey.setFromBytes(privateKeyBytes);
			System.out.println("read sk from file: " + privateKey);
			
		} else {
			//不存在
			//3 没有私钥 就生成一个私钥。 
			// 并且存在当地。
			privateKey = CacheUtil.gPairing.getZr().newRandomElement();
			System.out.println("now, we know privateKey: "  + privateKey);
			
			

			try {
				FileUtil.WriteToFile(privateKeyFile, privateKey.toBytes());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}

		
		
		// 4 生成公钥
		// 先读出公约
		// 不需要了 先去拉取公共参数。
//		FileInputStream in;
//		byte[] generator;
//		
//		
//		try {
//			generator = FileUtil.ReadFromFile(generatorFile);
//		} catch (Exception e3) {
//			// TODO Auto-generated catch block
//			e3.printStackTrace();
//			return;
//		}
//		Element generatorG1 = pairing.getG1().newElementFromBytes(generator);
		Element publicKeyElement = CacheUtil.gGeneratorG1.getImmutable().powZn(privateKey);
		
		System.out.println("voteid: " + Integer.valueOf(id) + 
				" sk: " + privateKey + 
				" generator: " + CacheUtil.gGeneratorG1 + 
				" pk: " + publicKeyElement);
		
		
	
		
		//生成一个对私钥的的零知识证明。
		//g^{q}, r
		
		// 先生成gq;
		Element q = CacheUtil.gPairing.getZr().newRandomElement();
		Element gq = CacheUtil.gGeneratorG1.getImmutable().powZn(q);
		
		// 再生成 r = q - x_{i} z
		//z = hash(g, gq, gxi, i)
		int voteIdInt = Integer.valueOf(id.trim());
		
		
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		} 
        
	     // digest() method called 
	        // to calculate message digest of an input 
	        // and return array of byte 
	    byte[] z = md.digest(ByteUtil.byteMergerAll(CacheUtil.gGeneratorG1.duplicate().toBytes(),
	    		gq.toBytes(),
	    		publicKeyElement.toBytes(),
	    		ByteUtil.toLH(voteIdInt))); 
		
		
	    //再来计算r
	    //r = q - xi * z
	    Element zZrElement = CacheUtil.gPairing.getZr().newElementFromBytes(z);
	    Element rightPart = privateKey.getImmutable().mul(zZrElement);
	    Element r = q.getImmutable().add(rightPart.getImmutable().negate());
		//假设r没有正确形成
	    //pass test!
	    //Element r2 = pairing.getZr().newRandomElement();
		
	    
	    
	    
	    //先自己验证一下试试。 
	    //gq ==?  gr gxi z
	    //验证通过。
	    //todo 删掉。
//	    Element gr = generatorG1.getImmutable().powZn(r);
//	    //Element gxiz = generatorG1.getImmutable().powZn(privateKey.getImmutable().mul(zZrElement));
//	    Element gxiz = publicKeyElement.getImmutable().powZn(zZrElement);
//	    Element newRightPart = gr.getImmutable().mul(gxiz);
//
//	    
//	    if (gq.isEqual(newRightPart)) {
//	    	System.out.println("pass!");
//	    } else {
//	    	System.out.println("error!");
//	    }
//	    

		
		
		//5 发送给服务器。
		Builder b = BoardcastPublicKeyRequest.newBuilder();
		b.setVoteId(voteIdInt);
		b.setPublickey(ByteString.copyFrom(publicKeyElement.toBytes()));
		// gq 没有正确形成
		// pass the test!
		//b.setGq(ByteString.copyFrom(pairing.getG1().newRandomElement().toBytes()));
		b.setGq(ByteString.copyFrom(gq.toBytes()));
		b.setR(ByteString.copyFrom(r.toBytes()));
		BoardcastPublicKeyRequest publicKeyRequest = b.build();
		
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			publicKeyRequest.writeTo(out);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		

		OkHttpClient client = new OkHttpClient();
		String url = "http://localhost:6889/boardcastPrivateKey";
		RequestBody body = RequestBody.create(out.toByteArray());
	    Request request = new Request.Builder().url(url)
	    		.post(body)
	    		.build();

	    
	    
	    //6 打印出结果。
	    Response response;
		try {
			response = client.newCall(request).execute();
			
			String ans =  response.body().string();
			PublicKeyProto.BoardcastPublicKeyResponse responseProto = 
					BoardcastPublicKeyResponse.parseFrom(ans.getBytes("iso-8859-1"));
			
			
			if (responseProto.getStatusCode() == 0) {
				JOptionPane.showMessageDialog(null, "公布公钥成功!");
				
			} else if (responseProto.getStatusCode() == 1){
				JOptionPane.showMessageDialog(null, "已经公开过公钥了!");
				
			} else if (responseProto.getStatusCode() == 3) {
				JOptionPane.showMessageDialog(null, "零知识证明无法通过!");
				
			} else {
				JOptionPane.showMessageDialog(null, "未知错误!");
				
			}
			
			
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
	}

}
