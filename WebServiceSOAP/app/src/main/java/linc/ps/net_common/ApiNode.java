package linc.ps.net_common;

import android.util.Log;
import java.util.Map;
/**
 * Created by Frank on 2016/12/9.
 * 拼接http请求的头文件
 */
public class ApiNode {
    // 转义字符-> <xxx>
    public static String toStart2(String name) {
        return "&lt;" + name + "&gt;";
    }
    // 转义字符-> </xxx>
    public static String toEnd2(String name) {
        return "&lt;/" + name + "&gt;";
    }
    // 正常字符-> <xxx>
    public static String toStart(String name) {
        return "<" + name + ">";
    }
    // 正常字符-> </xxx>
    public static String toEnd(String name) {
        return "</" + name + ">";
    }

    public static String getParameter(String namespace, Map<String, String> map) {
        StringBuffer sbf = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sbf.append(ApiNode.toStart(entry.getKey()));
            sbf.append(entry.getValue());
            sbf.append(ApiNode.toEnd(entry.getKey()));
        }
        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <" + namespace + " xmlns=\"http://WebXml.com.cn/\">" + sbf.toString() +
                "    </" + namespace + ">" +
                "  </soap:Body>" +
                "</soap:Envelope>";
        Log.v("MainActivity", namespace+"请求入参:" + str);
        return str;
    }
}
