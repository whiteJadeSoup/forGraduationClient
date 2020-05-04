package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileUtil {

	
	// 从指定文件中读取数据，  返回byte[]
	public static byte[] ReadFromFile(String file) throws Exception {
		FileInputStream in;
		byte[] generator;
		in = new FileInputStream(file);
		generator = in.readAllBytes();
		in.close();
		
		return generator;
	}
	
	
	
	
	// 把数据写入指定文件
	public static void WriteToFile(String file, byte[] data) throws Exception {
		FileOutputStream fw;
		fw = new FileOutputStream(file);
		fw.write(data);
		fw.close();
	}
	
	
}
