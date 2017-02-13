package linc.ps.net_retrofit_one;
import java.util.concurrent.TimeUnit;
import linc.ps.net_common.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;

/**
 * Created by Frank on 2016/12/11.
 * Retrofit初始化工具,旧的模式(请求没有用拦截器)
 */
public class AppClient {
    // 超时时间 默认5秒
    private static final int DEFAULT_TIMEOUT = 20;

    public static Retrofit mRetrofit;
    private ApiStores apiStores;

    private AppClient() {
        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.retryOnConnectionFailure(true); //连接失败后是否重新连接
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_SERVER_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        apiStores = mRetrofit.create(ApiStores.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final AppClient INSTANCE = new AppClient();
    }

    //获取单例
    public static AppClient getInstance(){
        return SingletonHolder.INSTANCE;
    }



    public Observable<ResponseBody> getSupportCity(String string) {
        return apiStores.getSupportCity(string);
    }
}
