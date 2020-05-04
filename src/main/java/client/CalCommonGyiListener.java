package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.test.proto.GetCommonParamProto;
import com.test.proto.CalCommonKeyProto;
import com.test.proto.CalCommonKeyProto.CalCommonKeyRequest;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.CacheUtil;




public class CalCommonGyiListener implements ActionListener {

	private static final String commonGyiFile = "commonGyiFile.data";
	
	private JTextField voteId;
	public CalCommonGyiListener(JTextField _voteId) {
		voteId = _voteId;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String id = voteId.getText();
		if (id.trim().equalsIgnoreCase("")) {
			//return
			JOptionPane.showMessageDialog(null,"先输入id");
			return;
		}
		int voteId = Integer.valueOf(id);
		
		
		// 是否传播了公钥?
		
		
		
		//是否已经拉取公共参数了？
		if (CacheUtil.gGeneratorG1 == null || CacheUtil.gSelectionId == "") {
			JOptionPane.showMessageDialog(null, "请先拉取公共参数!");
			return;
		}
		
		//是否本地已经有了文件
		File file = new File(commonGyiFile);
		if (file.exists()) {
			JOptionPane.showMessageDialog(null, "已经拉取了公共公钥了!");
			return;
		}
		
		
		
		
		
		// 2 构建protobuf
		CalCommonKeyRequest.Builder b = CalCommonKeyRequest.newBuilder();
		b.setVoteId(voteId);
		CalCommonKeyRequest request = b.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			request.writeTo(out);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
			
		}
		
		
		
		
		// 3 发送http request
		OkHttpClient client = new OkHttpClient();
		String url = "http://localhost:6889/calCommonGyi";
		RequestBody body = RequestBody.create(out.toByteArray());
	    Request httpRequest = new Request.Builder().url(url)
	    		.post(body)
	    		.build();

	    
	    
	    //6 打印出结果。
	    Response response;
		try {
			response = client.newCall(httpRequest).execute();
			String ans =  response.body().string();

			
			CalCommonKeyProto.CalCommonKeyResponse responseProto = 
					CalCommonKeyProto.CalCommonKeyResponse .parseFrom(ans.getBytes("iso-8859-1"));
			
			//校对一下ans
			Element eAns = CacheUtil.gPairing.getG1().newElementFromBytes(responseProto.getCommonKey().toByteArray());
			System.out.println("voteid: " + voteId +  " commonGyiKey: " + eAns);
			
			
			
			
			// 把这个ansBytes保存起来。
			util.FileUtil.WriteToFile(commonGyiFile, responseProto.getCommonKey().toByteArray());
			JOptionPane.showMessageDialog(null, "公共公钥已经保存在本地文件了!");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
	}

}
