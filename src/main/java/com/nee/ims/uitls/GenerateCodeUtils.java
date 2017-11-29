package com.nee.ims.uitls;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Random;

/**
 * 编码生成工具类
 * 
 * @author LiYishi
 */
public class GenerateCodeUtils {

	/**
	 * 纯数字
	 * 
	 * @param length
	 * @return
	 */
	public static String randomNumberCode(int length) {
		if (length < 1) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 6; i++) {
			sb.append(RandomUtils.nextInt(0, 10));
		}
		return sb.toString();
	}

	/**
	 * 时间戳+指定长度随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String timeAndRandomNumberCode(int length) {
		StringBuffer sb = new StringBuffer();
		sb.append(System.currentTimeMillis());

		if (length < 1) {
			return sb.toString();
		}

		for (int i = 0; i < 6; i++) {
			sb.append(RandomUtils.nextInt(0, 10));
		}
		return sb.toString();
	}

	/**
	 * 日期格式+指定长度随机数
	 * 
	 * @param dateFormat
	 * @param length
	 * @return
	 */
	public static String datetimeAndRandomNumberCode(String dateFormat, int length) {
		StringBuffer sb = new StringBuffer();
		sb.append(DateFormatUtils.format(Calendar.getInstance(), StringUtils.isNotBlank(dateFormat) ? dateFormat : "yyyyMMddHHmmss"));

		if (length < 1) {
			return sb.toString();
		}

		for (int i = 0; i < 6; i++) {
			sb.append(RandomUtils.nextInt(0, 10));
		}
		return sb.toString();
	}

	/**
	 * 大小写字母和数字
	 * 
	 * @param length
	 * @return
	 */
	public static String randomLetterAndNumberCode(int length) {
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}

		return sb.toString();
	}
}
