package com.nee.ims.uitls;

import com.nee.ims.common.constant.ErrorCodeEnum;
import com.nee.ims.common.exception.BusinessException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Author: heikki.
 * @Description:
 * @DATE: 上午1:18 17/10/19.
 */
public class WxSignUtil {

    private static String token = "heikki_nee";
    /**
     * 验证签名
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将 token、timestamp、nonce 三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        System.out.println("sign source : " + content);
        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行 sha1 加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将 sha1 加密后的字符串可与 signature 对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }

    /**
     * 根据微信签名规则，生成签名
     *
     * @param paramMap 参数Map
     * @param wxPayPartnerKey 密钥
     */
    public static String genWeChatSign(Map<String, String> paramMap, String wxPayPartnerKey) {

        String sign = null;
        StringBuffer buff = new StringBuffer();

        // 根据key进行字典序排序
        List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);

        // 有效参数计数
        int validCount = 0;
        for (String key : keyList) {

            String value = paramMap.get(key);

            // 参数的值为空不参与签名
            if (value == null || value.equals("")) {
                continue;
            }
            if (validCount == 0) {
                buff.append(key).append("=").append(value);
            } else {
                buff.append("&").append(key).append("=").append(value);
            }
            validCount++;
        }
        if (!buff.toString().equals("")) {
            buff.append("&key=").append(wxPayPartnerKey);
            sign = MD5Util.MD5Encode(buff.toString(), "utf-8").toUpperCase();
        }
        return sign;
    }

    /**
     * 将map转换为xml字符串
     */
    public static String stringMapToXML(Map<String, String> map) {

        StringBuffer buff = new StringBuffer();
        try {
            buff.append("<xml>");
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    buff.append("<").append(entry.getKey()).append(">");
                    if (entry.getValue() == null) {
                        buff.append("");
                    } else {
                        buff.append("<![CDATA[").append(entry.getValue()).append("]]>");
                    }
                    buff.append("</").append(entry.getKey()).append(">");
                }
            }
            buff.append("</xml>");
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
        }
        return buff.toString();
    }

    /**
     * Https请求（指定方法、指定地址、指定内容），返回结果为字符串
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方法
     * @param content 请求中的内容
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String content) {
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod);
            if (null != content) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(content.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            connection.disconnect();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
        }
    }


    /**
     * 将xml转换为map，只适用于两层的xml内容
     */
    public static Map<String, String> xmlToMap(String xml) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            @SuppressWarnings("unchecked")
            Iterator<Element> it = root.elementIterator();
            while (it.hasNext()) {
                Element el = it.next();
                map.put(el.getName(), el.getText());
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
        }
        return map;
    }

    /**
     * 生成秒级别的时间戳
     *
     * @return
     */
    public static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}

