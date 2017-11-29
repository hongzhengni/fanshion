package com.nee.ims.uitls;

import java.math.BigDecimal;
import java.util.*;

/**
 * Map 相关的工具类 提供简单的xpath功能遍历json map;
 * 
 * @author 徐明明
 * 
 */
public class MapUtil {

	private static final String[] EMPTY_ARY = {};
	private static final Map<String, String> EMPTY_MAP = new HashMap<String, String>();
	private static final List<String> EMPTY_LIST = new ArrayList<String>();

	private static final String SPIT_CHAR = "/";

	/**
	 * 节点遍历<br>
	 * 相当于dom4j.selectSingleNode(xpath)<br>
	 * 
	 * @param map
	 * @param xpath
	 * @return
	 */
	public static Map<?, ?> singleNode(Map<String, Object> map, String xpath) {
		String[] paths = splitPath(xpath);
		return singleNode(map, paths, 0, paths.length - 1);
	}

	/**
	 * 深度遍历节点
	 * 
	 * @param map
	 * @param paths
	 * @param index
	 * @param endIndex
	 * @return
	 */
	private static Map<?, ?> singleNode(Map<?, ?> map, String[] paths, int index, int endIndex) {
		if (index > endIndex) {
			return map;
		}
		String curPath = paths[index];
		if (StringUtils.isEmpty(curPath)) {
			if (index < endIndex) {
				return singleNode(map, paths, index + 1, endIndex);
			}
			return EMPTY_MAP;
		}
		Object node = map.get(paths[index]);
		if (null == node) {
			return EMPTY_MAP;
		}
		// 继续深入
		if (node instanceof Map) {
			return singleNode((Map<?, ?>) node, paths, index + 1, endIndex);
		} else if (node instanceof List) {
			System.out.println("current list ");
			List<?> nodes = (List<?>) node;
			if (nodes != null) {
				return singleNode((Map<?, ?>) nodes.get(0), paths, index + 1, endIndex);
			}
			// System.out.println("list ies empty");
		}
		return EMPTY_MAP;
	}

	private static String[] splitPath(String xpath) {
		if (StringUtils.isEmpty(xpath)) {
			return EMPTY_ARY;
		}
		return xpath.split(SPIT_CHAR);
	}

	/**
	 * 获取单节点文本 <br>
	 * 相当于 dom4j.selectSingleNode(xpath).getText()<br>
	 * 
	 * @param map
	 * @param xpath
	 * @return
	 */
	public static String singleNodeText(Map<?, ?> map, String xpath) {
		String[] paths = splitPath(xpath);
		if (paths.length > 1) {
			String last = paths[paths.length - 1];
			Map<?, ?> node = singleNode(map, paths, 0, paths.length - 2);
			if (null != node)
				return getMapString(node, last, "");
			return "";
		}
		return getMapString(map, xpath, "");
	}

	private static String getMapString(Map<?, ?> node, String key, String def) {
		if (node.containsKey(key)) {
			return node.get(key).toString();
		}

		return def;
	}

	/**
	 * 获取列表 相当于 dom4j.selectNodes(xpath) <br>
	 * 
	 * @param map
	 * @param xpath
	 * @return
	 */
	public static List<?> selectNodes(Map<?, ?> map, String xpath) {
		String[] paths = splitPath(xpath);
		if (paths.length > 1) {
			Object obj = singleObject(map, paths, 0, paths.length - 1);
			if (obj instanceof List) {
				return (List<?>) obj;
			}
			return EMPTY_LIST;
		}
		Object obj = map.get(xpath);
		if (obj instanceof List) {
			return (List<?>) obj;
		}
		return EMPTY_LIST;
	}

	private static Object singleObject(Map<?, ?> map, String[] paths, int index, int endIndex) {
		if (index > endIndex) {
			return map;
		}
		String curPath = paths[index];
		if (StringUtils.isEmpty(curPath)) {
			if (index < endIndex) {
				return singleObject(map, paths, index + 1, endIndex);
			}
			return EMPTY_MAP;
		}
		Object node = map.get(paths[index]);
		if (null == node) {
			return null;
		}
		// System.out.println("current index: " + index + ":" + node);
		// 继续深入
		if (node instanceof Map) {
			return singleObject((Map<?, ?>) node, paths, index + 1, endIndex);
		} else if (node instanceof List) {
			// System.out.println("current list ");
			List<?> nodes = (List<?>) node;
			if (nodes != null) {
				if (index == endIndex)
					return nodes;
				return singleObject((Map<?, ?>) nodes.get(0), paths, index + 1, endIndex);
			}
			// System.out.println("list ies empty");
		}
		return null;
	}

	public static String toListPath(String xpath) {
		if (xpath != null) {
			return xpath.replaceAll("\\/e\\/", "/");
		}
		return null;
	}

	/**
	 * 任意位置节点 <br>
	 * 任意节点查找以//开头，不支持两层连续列表<br>
	 * <span>真实路径：list/map/AcctNo </span><br>
	 * <span>任意节点 写法：//map/AcctNo </span><br>
	 * <span>任意节点 写法：//AcctNo </span>
	 * 
	 * @param map
	 * @param xpath
	 * @return
	 */
	public static String singleAnsyNodeText(Map<?, ?> map, String xpath) {
		if (xpath == null || !xpath.startsWith("//")) {
			return "";
		}
		xpath = xpath.substring(2);
		Object obj = singleAnsyNode(map, xpath.split("/"), 0, false);
		return obj == null ? null : obj.toString();
	}

	/**
	 * 任意节点查找以//开头，不支持两层连续列表<br>
	 * <span>list/map/AcctNo </span><br>
	 * <span>//map/AcctNo </span>
	 * 
	 * @param map
	 * @param xpaths
	 * @param index
	 * @param needJoin
	 *            是否已开始
	 * @return
	 */
	private static Object singleAnsyNode(Map<?, ?> map, String[] xpaths, int index, boolean needJoin) {
		needJoin = needJoin ? true : map.containsKey(xpaths[index]);
		if (needJoin) {
			Object obj = map.get(xpaths[index]);
			if (obj == null) {
				return null;
			}
			if (index == (xpaths.length - 1)) {
				// 已适配完成
				return obj;
			}
			// System.out.println("-------" + xpaths[index]);
			if (obj instanceof Map) {
				return singleAnsyNode((Map<?, ?>) obj, xpaths, index + 1, true);
			} else if (obj instanceof List) {
				// 进入列表遍历:最多两层连续的列表
				List<?> datas = (List<?>) obj;
				Object next = datas.get(0);
				if (next instanceof Map) {
					return singleAnsyNode((Map<?, ?>) next, xpaths, index + 1, true);
				} else if (next instanceof List) {
					// 进入第二层列表递归
					return null;
				}
			}
		} else {
			Set<?> keys = map.keySet();
			for (Object key : keys) {
				Object item = map.get(key);
				if (item instanceof List) {
					// 进入列表遍历:不支持连续的列表
					List<?> datas = (List<?>) item;
					Object next = datas.get(0);
					if (next instanceof Map) {
						return singleAnsyNode((Map<?, ?>) next, xpaths, 0, false);
					}
				} else if (item instanceof Map) {
					return singleAnsyNode((Map<?, ?>) item, xpaths, 0, false);
				}
			}
		}
		return null;
	}

	public static String getMapValue(Map<?, ?> map, String key) {
		return (null != map && null != map.get(key)) ? map.get(key).toString() : "";
	}

	@SuppressWarnings("unchecked")
	public static <T> T getMapValue(Map<?, ?> map, String key, T def) {
		if (null != map && null != key && map.containsKey(key) && null != map.get(key)) {
			try {
				Object obj = map.get(key);
				if (def instanceof Integer) {
					return (T) Integer.valueOf(obj.toString());
				}
				if (def instanceof Long) {
					return (T) Long.valueOf(obj.toString());
				}
				if (def instanceof Float) {
					return (T) Float.valueOf(obj.toString());
				}
				if (def instanceof Double) {
					return (T) Double.valueOf(obj.toString());
				}
				if (def instanceof BigDecimal) {
					return (T) new BigDecimal(obj.toString());
				}
				if (def instanceof String) {
					return (T) obj.toString();
				}
				if (obj instanceof BigDecimal) {
					return (T) obj.toString();
				}

				return (T) obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return def;
	}
}