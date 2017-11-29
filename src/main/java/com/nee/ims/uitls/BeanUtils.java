package com.nee.ims.uitls;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

	public static final String RANGE = "Range";

	/**
	 * 把bean转换为HashMap
	 * 
	 * @param objBean
	 *            bean的实例对象
	 * @return 以该对象所有字段为key，对应值为value 的 map
	 */
	@SuppressWarnings("unchecked")
	public static HashMap toHashMap(Object objBean) {
		return toHashMap(objBean,objBean.getClass());
	}

	/**
	 * 把bean转换为HashMap,转换成map的字段为 fieldClass 对应的字段
	 * 
	 * @param objBean
	 *            bean的实例对象
	 * @return 以该对象所有字段为key，对应值为value 的 map
	 */
	@SuppressWarnings("unchecked")
	public static HashMap toHashMap(Object objBean,Class fieldClass) {
		HashMap mapBean = new HashMap();
		if (objBean==null)
			return null;

		Field[] fields = fieldClass.getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.isPublic(field.getModifiers()))
				field.setAccessible(true);
			try {
				mapBean.put(field.getName(), field.get(objBean));
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
		return mapBean;
	}
	
	/**
	 * 把bean对象中所有非null的 String 字段进行解码
	 * 
	 * @param objBean
	 *            bean的实例对象
	 * @param code
	 *            解码需要用的编码
	 */
	public static void decodeStringFields(Object objBean, String code) {
		if (objBean==null)
			return;
		Field[] fields = objBean.getClass().getDeclaredFields();
		String value = null;
		Object objValue = null;
		for (Field field : fields) {
			if (field.getType() != String.class)
				continue;
			try {

				if (!Modifier.isPublic(field.getModifiers()))
					field.setAccessible(true);
				
				objValue = field.get(objBean);
				if (objValue == null)
					continue;
				value = objValue.toString();

				field.set(objBean, java.net.URLDecoder.decode(value, code));
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 把bean对象中所有非null的 String 字段进行 utf8 解码 适用于通过AJAX从前台传给后台的bean对象
	 * 
	 * @param objBean
	 *            bean的实例对象
	 */
	public static void decodeStringFieldsByUtf8(Object objBean) {
		decodeStringFields(objBean, "utf-8");
	}

	/**
	 * 把 map中键值与bean的属性相同（或者大写相同）的部分对应的值赋给bean的相应属性
	 * 支持bean属性中的Timestamp、String、Long(long)、Double(double)、String[]这几种类型的赋值和转换
	 * map中的值如果是String/String[]，程序自动把字符串转到相应的类型 如果类型为 String[] 则取其第一个值进行赋值
	 * 如果相应键值的map的值为null则不处理，如果为空串并且bean中对应属性的类型并非String则不处理
	 * 
	 * @param objBean
	 *            bean的实例对象
	 * @param mapBean
	 *            map数据源
	 */
	@SuppressWarnings("unchecked")
	public static void fromMap(Object objBean, Map mapBean) {
		if (mapBean==null)
			return;
		Field[] fields = objBean.getClass().getDeclaredFields();
		String keyName = "";
		Object objResult = null;
		String result = "";
		Class fieldType = null;
		for (Field field : fields) {
			keyName = field.getName();
			fieldType = field.getType();
			if (!mapBean.containsKey(keyName))
				keyName = field.getName().toUpperCase(); // SQL查询结果返回的都是hashmap的key都是大写
			if (mapBean.containsKey(keyName)) {
				objResult = mapBean.get(keyName);
				if (objResult == null)
					continue;

				// (网页上下文的的context对象使用String[]形式存储，因此要支持String[]形式)
				if (objResult instanceof String[]
						&& ((String[]) objResult).length > 0) {
					result = ((String[]) objResult)[0];
				} else
					result = objResult.toString();

				if (result == null)
					continue;

				if (!Modifier.isPublic(field.getModifiers()))
					field.setAccessible(true);

				try {
					if (fieldType == String.class) {
						field.set(objBean, result);
					}
					if (result.equals(""))
						continue;
					if (fieldType == long.class || fieldType == Long.class) {
						field.set(objBean, Long.parseLong(result));
					} else if (fieldType == double.class
							|| fieldType == Double.class) {
						field.set(objBean, Double.parseDouble(result));
					} else if (fieldType == Timestamp.class) {
						field.set(objBean, Timestamp.valueOf(result));
					}
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 对于传入条件bean中结尾是 Range 的字符串属性，如果其值非空非null 则前后添加%字符 主要用于统一设置模糊查询条件给DAO底层的
	 * LIKE查询调用
	 * 
	 * @param objParamBean
	 *            paramBean的实例对象
	 */
	public static void addParamRangeQuery(Object objParamBean) {
		if (objParamBean==null)
			return;
		Field[] fields = objParamBean.getClass().getDeclaredFields();
		Object objResult = null;
		for (Field field : fields) {
			if (!field.getName().endsWith(RANGE))
				continue;
			if (field.getType() != String.class)
				continue;
			if (!Modifier.isPublic(field.getModifiers()))
				field.setAccessible(true);
			try {
				objResult = field.get(objParamBean);
				if (objResult == null || objResult.toString().equals(""))
					continue;
				field.set(objParamBean, "%" + objResult.toString() + "%");
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
	}
}
