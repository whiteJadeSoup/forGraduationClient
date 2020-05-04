package util;

import java.util.ArrayList;
import java.util.List;



public class ByteUtil {

	public static byte[] byteMergerAll(byte[]... values) {
	    int length_byte = 0;
	        for (int i = 0; i < values.length; i++) {
	            length_byte += values[i].length;
	        }
	        
	        
	        byte[] all_byte = new byte[length_byte];
	        int countLength = 0;
	        for (int i = 0; i < values.length; i++) {
	            byte[] b = values[i];
	            System.arraycopy(b, 0, all_byte, countLength, b.length);
	            countLength += b.length;
	        }
	        return all_byte;
	}
	
	
	
	public static byte[] toLH(int n) {  
		  byte[] b = new byte[4];  
		  b[0] = (byte) (n & 0xff);  
		  b[1] = (byte) (n >> 8 & 0xff);  
		  b[2] = (byte) (n >> 16 & 0xff);  
		  b[3] = (byte) (n >> 24 & 0xff);  
		  return b;  
		}
	
}
