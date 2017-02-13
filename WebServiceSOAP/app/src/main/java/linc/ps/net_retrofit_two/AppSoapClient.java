package linc.ps.net_retrofit_two;

import android.util.Log;

import com.blankj.utilcode.utils.EncodeUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import linc.ps.net_common.ApiNode;
import linc.ps.net_common.BuildConfig;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Retrofit初始化工具,拦截器模式
 */
public class AppSoapClient {
    // 超时时间 默认5秒
    private static final int DEFAULT_TIMEOUT = 5;

    public static Retrofit mRetrofit;
    private ApiSoapStores apiSoapStores;

    private AppSoapClient() {
        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            // 日志信息拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //设置 Debug Log 模式
            builder.addInterceptor(loggingInterceptor);

            //SOAP请求过滤器
            HttpRequestInterceptor httpRequestInterceptor = new HttpRequestInterceptor();
            builder.addInterceptor(httpRequestInterceptor);


            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        apiSoapStores = mRetrofit.create(ApiSoapStores.class);
    }

    /**
     * SOAP请求过滤器
     */
    static class HttpRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //获取原Resuqest
            Request originalRequest = chain.request();
            //解析出namespace
            String url = originalRequest.url().toString();
            String namespace = url.replace(BuildConfig.API_SERVER_URL + ApiSoapStores.URL_HEAD, "");
            RequestBody requestBody = originalRequest.body();
            if(requestBody != null) {
                //构建新的ResuqestBody
                RequestBody newRequestBody = buildRequestBody(namespace, requestBody);
                if(newRequestBody != null) {
                    //构建新的Request
                    Request.Builder builder = originalRequest.newBuilder();
                    Request request = builder
                            .url(url.replace("/" + namespace, ""))
//                            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .addHeader("Accept", "application/json,text/javascript,*/*")
                            .addHeader("Content-type", ApiSoapStores.CONTENT_TYPE)
                            .addHeader("SOAPAction", ApiSoapStores.SOAP_ACTION_HEAD + namespace)
                            .post(newRequestBody)
                            .build();
                    //开始进行网络请求
                    Response originalResponse = chain.proceed(request);
                    if(originalResponse != null) {
                        //获取原ResponseBody
                        ResponseBody responseBody = originalResponse.body();
                        //构建新的ResponseBody
                        ResponseBody newResponseBody = parseResponseBody(namespace, responseBody);
                        if (newResponseBody != null) {
                            Response.Builder responseBuilder = originalResponse.newBuilder();
                            //返回新的Resonse
                            return responseBuilder.body(newResponseBody).build();
                        }
                    }
                    return originalResponse;
                }
            }
            return chain.proceed(originalRequest);
        }

        private RequestBody buildRequestBody(String namespace, RequestBody requestBody) {

            Map<String, String> map = new HashMap<>();

            if (requestBody instanceof FormBody) {
                Log.e("test", "--->有入参");
                FormBody formBody = (FormBody) requestBody;
                for (int i = 0; i < formBody.size(); i++) {
                    String name = formBody.encodedName(i);
                    String value  = EncodeUtils.urlDecode(formBody.encodedValue(i)).replace("&lt;", "<").replace("&gt;", ">").replace("%24", "$");
//                    String value = formBody.encodedValue(i).replace("&lt;", "<").replace("&gt;", ">").replace("%24", "$");//--->转义字符得封装成一个类
                    map.put(name, value);
                }
                String newBody = ApiNode.getParameter(namespace, map);
                Log.e("test", "--->有入参-->"+newBody);
                MediaType mediaType = MediaType.parse(ApiSoapStores.CONTENT_TYPE);
                RequestBody newRequestBody = RequestBody.create(mediaType, newBody);
                Log.e("test", "--->有入参-->"+newRequestBody.toString());
                return newRequestBody;
            }else{
                Log.e("test", "--->无入参");
                String newBody = ApiNode.getParameter(namespace, map);
                MediaType mediaType = MediaType.parse(ApiSoapStores.CONTENT_TYPE);
                RequestBody newRequestBody = RequestBody.create(mediaType, newBody);
                Log.e("test", "--->无入参-->"+newRequestBody.toString());
                return newRequestBody;
            }
        }
        /**
         * 解析Response
         * @param namespace
         * @param responseBody
         * @return
         */
        private ResponseBody parseResponseBody(String namespace, ResponseBody responseBody) {
            try {

                String res = responseBody.string();
                MediaType mediaType = MediaType.parse(ApiSoapStores.CONTENT_TYPE);
                if (res != null && !res.equals("")) {
                    // 字符转义
                    String ostar = "<" + namespace + "Result>";
                    String oend = "</" + namespace + "Result>";
                    if (res.contains(ostar) && res.contains(oend)) {
                        int startIndex = res.indexOf(ostar) + ostar.length();
                        int endIndex = res.lastIndexOf(oend);
                        String ores = res.substring(startIndex, endIndex);
                        return ResponseBody.create(mediaType, ores);
                    }
                }else{
                    return ResponseBody.create(mediaType, res);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final AppSoapClient INSTANCE = new AppSoapClient();
    }

    //获取单例
    public static AppSoapClient getInstance(){
        return AppSoapClient.SingletonHolder.INSTANCE;
    }



    public Observable<ResponseBody> getSupportCity(String byProvinceName){
        return apiSoapStores.getSupportCity(byProvinceName);
    }
}
