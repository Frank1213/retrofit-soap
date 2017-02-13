package linc.ps.net_retrofit_two;
import org.simpleframework.xml.Default;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Frank on 2016/12/9.
 * (请求有用拦截器)
 * 无入参的要去掉@FormUrlEncoded @Nullable  @Default("")
 */
public interface ApiSoapStores {

    /**  head基础参数 **/
    String CONTENT_TYPE = "text/xml; charset=utf-8";
    String SOAP_ACTION_HEAD = "http://WebXml.com.cn/";
    String URL_HEAD = "WeatherWebService.asmx/";

    /**  无参请求 **/
//    @POST("WeatherWebService.asmx/getSupportCity")
//    Observable<ResponseBody> getSupportCity();

    //**  带参请求   **//*
/*    @FormUrlEncoded
    @POST("WeatherWebService.asmx/getSupportCity")
    Observable<ResponseBody> getSupportCity(@Field("byProvinceName") String byProvinceName);*/

    /**  带参请求   **/
    @FormUrlEncoded
//    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8;")
//    @Headers("Content-Type: text/xml; charset=utf-8")
//    @Headers("Content-Type: application/json;charset=UTF-8")
//    @Headers("Accept: application/json; charset=utf-8")
    @POST("WeatherWebService.asmx/getSupportCity")
//    Observable<ResponseBody> getSupportCity(@Field(value = "byProvinceName", encoded=true) String byProvinceName);
    Observable<ResponseBody> getSupportCity(@Field("byProvinceName") String byProvinceName);
}
