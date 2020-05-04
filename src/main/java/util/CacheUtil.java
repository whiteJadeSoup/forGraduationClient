package util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;




public class CacheUtil {
	
	// 生成元
	public static Element gGeneratorG1 = null;
	
	// 选举id
	public static String gSelectionId = "";
	
	
	public static Pairing gPairing = null;
	
	
	public static void init() throws Exception {
		//0 初始化
		gPairing = PairingFactory.getInstance().getPairing("a.properties");//
		PairingFactory.getInstance().setUsePBCWhenPossible(true);
		if (!gPairing.isSymmetric()) {
			throw new RuntimeException("密钥不对称!");
		}
	}
}
