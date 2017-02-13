package linc.ps.net_retrofit_one;

import okhttp3.ResponseBody;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
/**
 * Created by Frank on 2016/12/9.
 * 网络请求地址,旧的模式(请求没有用拦截器)
 */
public interface ApiStores {
    // 通过省份获取城市代码,头文件在第一篇都有讲到
    @Headers({
            "Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://WebXml.com.cn/getSupportCity"
    })
    // 这里对应 http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?op=getWeatherbyCityName 里面的WeatherWebService
    @POST("WeatherWebService.asmx")
    Observable<ResponseBody> getSupportCity(@retrofit2.http.Body String s);
}
