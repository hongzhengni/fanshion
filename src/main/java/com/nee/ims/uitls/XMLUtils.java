package com.nee.ims.uitls;

import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xml 帮助类
 * 
 * @author xumingming
 *
 */
public class XMLUtils {
	private static Logger logger = LoggerFactory.getLogger(XMLUtils.class);

	/**
	 * 将输入的MAP转化为XML
	 * 
	 * @param map
	 * @return
	 */
	public static String convertToXML(Map<String, String> map) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");

		// 遍历Map中所有键值
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (StringUtils.isBlank(entry.getKey())) {
				continue;
			}

			root.addElement(entry.getKey()).addText(StringUtils.toEmpty(entry.getValue()));
		}

		// 返回创建好的XML字符串
		return document.asXML();
	}

	/**
	 * 将XML转化为Map
	 * 
	 * @param xml
	 * @return
	 */
	public static Map<String, Object> XML2Map(String xml) {
		// 从输入的XML创建Document
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error("XML2Map error...", e);
		}

		// 返回创建好的Map对象
		return XML2Map(document);
	}

	/**
	 * 将XML转化为Map
	 * 
	 * @param doc
	 * @return
	 */
	public static Map<String, Object> XML2Map(Document doc) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null) {
			return map;
		}

		Element root = doc.getRootElement();
		for (Iterator<?> iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			List<?> list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			} else {
				map.put(e.getName(), e.getText());
			}
		}
		return map;
	}

	/**
	 * 将某个Element转化为Map
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> Dom2Map(Element e) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<?> list = e.elements();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = (Element) list.get(i);
				List<Object> mapList = new ArrayList<Object>();

				if (iter.elements().size() > 0) {
					Map<String, Object> m = Dom2Map(iter);
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList<Object>();
							mapList.add(obj);
							mapList.add(m);
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List<Object>) obj;
							mapList.add(m);
						}
						map.put(iter.getName(), mapList);
					} else {
						map.put(iter.getName(), m);
					}
				} else {
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList<Object>();
							mapList.add(obj);
							mapList.add(iter.getText());
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List<Object>) obj;
							mapList.add(iter.getText());
						}
						map.put(iter.getName(), mapList);
					} else {
						map.put(iter.getName(), iter.getText());
					}
				}
			}
		} else {
			map.put(e.getName(), e.getText());
		}

		return map;
	}
}
