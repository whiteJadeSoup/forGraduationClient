package util;

import com.google.protobuf.ByteString;
import com.test.proto.MsgInfo.MsgBody;

public class MsgUtil {
	
	
	public static MsgBody build(int command, byte[] content) {
		MsgBody.Builder b = MsgBody.newBuilder();
		b.setCommand(command);
		b.setContent(ByteString.copyFrom(content));
		return b.build();
		
	}
	
}
