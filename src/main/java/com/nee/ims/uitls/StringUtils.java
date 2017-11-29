package com.nee.ims.uitls;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import sun.misc.BASE64Decoder;

/**
 * 字符串工具类
 * 
 * @author 徐明明
 */
@SuppressWarnings("restriction")
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	private static final char SEPARATOR = '_';
	private static final String CHARSET_NAME = "UTF-8";
	public static final char[] codeSequences = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	public static final char[] charSequences = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z' };

	/**
	 * 转换为字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] getBytes(String str) {
		if (str != null) {
			try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 转换为字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toString(byte[] bytes) {
		try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return EMPTY;
		}
	}

	/**
	 * 是否包含字符串
	 * 
	 * @param str
	 *            验证字符串
	 * @param strs
	 *            字符串组
	 * @return 包含返回true
	 */
	public static boolean inString(String str, String... strs) {
		if (str != null) {
			for (String s : strs) {
				if (str.equals(trim(s))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * 
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html) {
		if (html == null) {
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * 
	 * @param str
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val) {
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val) {
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val) {
		return toLong(val).intValue();
	}


	/**
	 * 驼峰命名法工具
	 * 
	 * @return toCamelCase("hello_world") == "helloWorld"
	 *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 *         toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toCamelCase(String s) {
		if (s == null) {
			return null;
		}

		s = s.toLowerCase();

		StringBuilder sb = new StringBuilder(s.length());
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * 驼峰命名法工具
	 * 
	 * @return toCamelCase("hello_world") == "helloWorld"
	 *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 *         toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toCapitalizeCamelCase(String s) {
		if (s == null) {
			return null;
		}
		s = toCamelCase(s);
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * 驼峰命名法工具
	 * 
	 * @return toCamelCase("hello_world") == "helloWorld"
	 *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 *         toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toUnderScoreCase(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			boolean nextUpperCase = true;

			if (i < (s.length() - 1)) {
				nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
			}

			if ((i > 0) && Character.isUpperCase(c)) {
				if (!upperCase || !nextUpperCase) {
					sb.append(SEPARATOR);
				}
				upperCase = true;
			} else {
				upperCase = false;
			}

			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();
	}

	/**
	 * 如果不为空，则设置值
	 * 
	 * @param target
	 * @param source
	 */
	public static void setValueIfNotBlank(String target, String source) {
		if (isNotBlank(source)) {
			target = source;
		}
	}

	/**
	 * 转换为JS获取对象值，生成三目运算返回结果
	 * 
	 * @param objectString
	 *            对象串 例如：row.user.id
	 *            返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
	 */
	public static String jsGetVal(String objectString) {
		StringBuilder result = new StringBuilder();
		StringBuilder val = new StringBuilder();
		String[] vals = split(objectString, ".");
		for (int i = 0; i < vals.length; i++) {
			val.append("." + vals[i]);
			result.append("!" + (val.substring(1)) + "?'':");
		}
		result.append(val.substring(1));
		return result.toString();
	}

	/**
	 * 
	 * 随机生成数字
	 */
	public static String randomInt(int length) {
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(codeSequences[random.nextInt(10)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	/**
	 * 
	 * 随机生成字母
	 */
	public static String randomString(int length) {
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(charSequences[random.nextInt(26)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * @author linjie
	 * @date 2015年10月9日下午7:02:53
	 * @description
	 * @param imageData
	 * @return
	 * @throws Exception
	 */
	public static byte[] decode(String imageData) throws Exception {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] data = decoder.decodeBuffer(imageData);
		for (int i = 0; i < data.length; ++i) {
			if (data[i] < 0) {
				// 调整异常数据
				data[i] += 256;
			}
		}
		return data;
	}

	/**
	 * @author linjie
	 * @date 2015年11月4日 下午3:30:33
	 * @Description 使用base64加密字符串
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String base64(String str) throws Exception {
		String code = new String(Base64Encoder.encode(str.getBytes()));
		return code;
	}

	/**
	 * URL 新增后缀
	 * 
	 * @param url
	 * @param suffix
	 * @return
	 */
	public static String addUrlSuffix(String url, String suffix) {
		if (StringUtils.isBlank(url) || StringUtils.isBlank(suffix)) {
			return url;
		}

		return url + (url.indexOf("?") < 0 ? "?" : "&") + suffix;
	}

	/**
	 * 如果字符串为null,则返回空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String toEmpty(String str) {
		return null != str ? str : "";
	}

	/**
	 * 比较两个字符串是否相等（如果字符串为null,则返回空字符串
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static boolean equalsEmpty(String arg1, String arg2) {
		return toEmpty(arg1).equals(toEmpty(arg2));
	}

	/**
	 * 判断字符串是否是json格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isJson(String str) {
		return null != str && str.startsWith("{") && str.endsWith("}");
	}


	public static String filterEmoji(String source) {
		if (source != null) {
			Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
					Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				source = emojiMatcher.replaceAll("");
				return source;
			}
			return source;
		}
		return source;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	/**
	 * 获取首字母小写
	 * 
	 * @param str
	 * @return
	 */
	public static String toFirstLower(String str) {
		if (StringUtils.isBlank(str) || str.length() < 1) {
			return str;
		}

		String first = str.substring(0, 1);
		return str.replaceFirst(first, first.toLowerCase());
	}



	/**
	 * 替换markdown语法
	 * 
	 * @param content
	 * @return
	 */
	public static String toMarkDownStr(String content) {
		if (StringUtils.isBlank(content)) {
			return content;
		}

		Pattern p = Pattern.compile("<([^<>]+?)>|\\[[^\\[\\]\\(\\)]+?\\]\\(([^\\[\\]\\(\\)]+?)\\)|\\[([^\\[\\]\\(\\)]+?)\\]\\[[^\\[\\]\\(\\)]+?\\]",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(content);
		while (matcher.find()) {
			int count = matcher.groupCount();
			for (int i = 0; i < count; i++) {
				String text = matcher.group(i + 1);
				if (null == text) {
					continue;
				}

				content = content.replace(matcher.group(), text);
				break;
			}
		}

		return content;
	}

	/**
	 * 替换当前引用markdown对应的客户端数据
	 * 
	 * @param deviceType
	 * @param appVersion
	 * @param content
	 * @return
	 */
	public static String toAppMarkDownClientData(String deviceType, String appVersion, String content) {
		String oldVersion = null;
		if ("ios".equals(deviceType)) {
			oldVersion = "1.9.3.1721";
		} else if ("android".equals(deviceType)) {
			oldVersion = "1.9.2";
		}

		try {
			if (VersionUtils.compareVersion(appVersion, oldVersion) > 0) {
				return content;
			} else {
				return StringUtils.toMarkDownStr(content);
			}
		} catch (Exception e) {
			return content;
		}
	}

	public static String getQuotesStr(String str) {
		if (null == str) {
			return str;
		}

		return "\"" + str + "\"";
	}
}