package com.nee.ims.uitls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * 货比转换
 * 
 * @author xumingming
 *
 */
public class CurrencyUtils {
	private static Logger logger = LoggerFactory.getLogger(CurrencyUtils.class);

	/**
	 * 汇率请求
	 * 
	 * @param httpUrl
	 * @param httpArg
	 * @return
	 */
	public static String request(String httpUrl, String httpArg) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		httpUrl = httpUrl + "?" + httpArg;

		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 填入apikey到HTTP header
			connection.setRequestProperty("apikey", "6b4de21ed7766ca9df7943cc1f3ada52");
			connection.connect();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			logger.error("CurrencyUtils request error...", e);
		}

		return result;
	}

	public static String[] currencys = { "CNY", "USD", "EUR", "GBP", "HKD", "JPY", "KRW", "AUD", "TWD", "CAD", "SGD", "FRF" };

	/**
	 * 转换金额为人民币
	 * 
	 * @param fromAmount
	 * @param fromCurrency
	 * @return
	 */
	public static Integer convertCNYCurrency(Float fromAmount, Integer fromCurrency) {
		Integer amount = null;
		String strResult = null;

		try {
			if (null == fromAmount || null == fromCurrency || fromCurrency > currencys.length) {
				return amount;
			}

			String currency = currencys[fromCurrency - 1];
			if ("CNY".equals(currency)) {
				return amount;
			}
			String httpUrl = "http://apis.baidu.com/apistore/currencyservice/currency";
			String httpArg = "fromCurrency=" + currency + "&toCurrency=CNY&amount=" + fromAmount;

			strResult = request(httpUrl, httpArg);
			if (null == strResult || !(strResult.trim().startsWith("{") && strResult.trim().endsWith("}"))) {
				return amount;
			}
			JsonObject jsonResult = new JsonObject(strResult);
			if (!jsonResult.containsKey("retData")) {
				return amount;
			}
			JsonObject retData = jsonResult.getJsonObject("retData");
			if (null == retData || !retData.containsKey("convertedamount")) {
				return amount;
			}

			Float amountFloat = retData.getFloat("convertedamount");
			if (null != amountFloat) {
				amount = new BigDecimal(amountFloat).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			}
		} catch (Exception e) {
			if (null != strResult) {
				logger.error("CurrencyUtils convertCNYCurrency response: {}", strResult);
			}

			logger.error("CurrencyUtils convertCNYCurrency error...", e);
		}

		return amount;
	}
}
