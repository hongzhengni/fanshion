package com.nee.ims.uitls;

import java.security.MessageDigest;

/**
 * MD5加密
 * 
 */
public class MD5Encrypt {
	public static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 對字符串進行MD5加密
	 * 
	 * @param s
	 * @return 加密後的字符串
	 */
	public final static String MD5(String s) {
		try {
			if (s != null && !"".equals(s.trim())) {
				byte[] strTemp = s.getBytes("UTF-8");
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				mdTemp.update(strTemp);
				byte[] md = mdTemp.digest();
				int j = md.length;
				char str[] = new char[j * 2];
				int k = 0;
				for (int i = 0; i < j; i++) {
					byte byte0 = md[i];
					str[k++] = hexDigits[byte0 >>> 4 & 0xf];
					str[k++] = hexDigits[byte0 & 0xf];
				}
				return new String(str);
			} else {
				return "";
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 對字节進行MD5加密
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static String getMD5(byte[] source) throws Exception {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(MD5Encrypt.MD5("123456").toUpperCase().length());
		System.out.println(MD5Encrypt.MD5("123456").toUpperCase());
		String ss = "didFinishLaunchingWithOptionsdidFinishLaunchingWithOptionsdidFinishLaunchingWithOptions";
		System.out.println(MD5Encrypt.MD5(ss).toUpperCase());
	}
}
