package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.test.proto.GetCommonParamProto;
import com.test.proto.GetCommonParamProto.GetCommonParamRequest.Builder;
import com.test.proto.GetVoteResultProto;
import com.test.proto.GoVoteProto;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.CacheUtil;

public class GetVoteResultListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		
		GetVoteResultProto.GetVoteResultRequest.Builder b = 
				GetVoteResultProto.GetVoteResultRequest.newBuilder();
		b.setSelectionId(CacheUtil.gSelectionId);
		
		
		GetVoteResultProto.GetVoteResultRequest request = b.build();
		System.out.println("request selection id： " + request.getSelectionId());
		
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			request.writeTo(out);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		
		
		
		// 1 发送请求得到投票结果。
		OkHttpClient client = new OkHttpClient();
		String url = "http://localhost:6889/getVoteResult";
		RequestBody body = RequestBody.create(out.toByteArray());
	    Request httpRequest = new Request.Builder().url(url)
	    		.get()
	    		.build();
	    
	    
	    
	    
	    
	    Response response;
		try {
			response = client.newCall(httpRequest).execute();
			String ans =  response.body().string();
			
			
			
			
			GetVoteResultProto.GetVoteResultResponse responseProto = 
					GetVoteResultProto.GetVoteResultResponse.parseFrom(ans.getBytes("iso-8859-1"));
			
			int statusCode = responseProto.getStatusCode();
			if (statusCode == 0) {
				System.out.println("response selection id: " + responseProto.getSelectionId() + " 投票结果为: " + responseProto.getVoteResult());
				JOptionPane.showMessageDialog(null, " 投票结果为: " + responseProto.getVoteResult());
				
			} else {
				System.out.print("response selection id: " + responseProto.getSelectionId()  + " 失败！ " + responseProto.getExtra());
				JOptionPane.showMessageDialog(null, " 失败 "  + responseProto.getExtra());
				
			}

			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

}
