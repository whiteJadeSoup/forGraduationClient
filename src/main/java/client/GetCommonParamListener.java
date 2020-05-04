package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.test.proto.GetCommonParamProto;
import com.test.proto.GetCommonParamProto.GetCommonParamRequest.Builder;

import it.unisa.dia.gas.jpbc.Element;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.CacheUtil;

public class GetCommonParamListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		
		//发送信息去
		Builder b = GetCommonParamProto.GetCommonParamRequest.newBuilder();
		GetCommonParamProto.GetCommonParamRequest request = b.build();
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			request.writeTo(out);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		
		
		
		
		OkHttpClient client = new OkHttpClient();
		String url = "http://localhost:6889/getCommonParam";
		RequestBody body = RequestBody.create(out.toByteArray());
	    Request httpRequest = new Request.Builder().url(url)
	    		.post(body)
	    		.build();

	    
	    
	    //4 打印出结果。
	    Response httpResponse;
		try {
			httpResponse = client.newCall(httpRequest).execute();
			String bodyStr = httpResponse.body().string();
			
			GetCommonParamProto.GetCommonParamResponse response = 
					GetCommonParamProto.GetCommonParamResponse.parseFrom(bodyStr.getBytes("iso-8859-1"));
			
			
			
			
			if (response.getStatusCode() != 0) {
				System.out.println("error!");
				JOptionPane.showMessageDialog(null, "拉取公共参数失败！");
				
			} else {
				String resId = new String (response.getSelectionId().toByteArray(), "iso-8859-1");
				Element generator = CacheUtil.gPairing.getG1().newElementFromBytes(response.getGeneratorG1().toByteArray());
				
				
				System.out.println("selectionId: " + resId + " generatorG1: " + generator);
				
				CacheUtil.gSelectionId = resId;
				CacheUtil.gGeneratorG1 = generator;
				JOptionPane.showMessageDialog(null, "拉取公共参数成功！");
			}
			
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
	}

}
