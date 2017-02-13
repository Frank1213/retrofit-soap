package linc.ps.net_common;

import android.util.Log;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Frank on 2016/12/9.
 * 处理Rx线程
 */

public class ApiSchedulersHelper {
    /**
     * 线程转换->不是什么好写法-->尽量自己写,数据需要做的判断太多了
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> io_main() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * 返回正确数据
     */
    private static Observable<String> returnString(final String t) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 从网络请求里面的那串截取出需要的json数据（旧的方法，只适用于json）
     *
     * @return
     */
    public static Observable.Transformer<ResponseBody, String> gsonResult2() {
        return new Observable.Transformer<ResponseBody, String>() {
            @Override
            public Observable<String> call(Observable<ResponseBody> tObservable) {
                return tObservable.flatMap(
                        new Func1<ResponseBody, Observable<String>>() {
                            @Override
                            public Observable<String> call(ResponseBody response) {
                                try {
                                    if (response == null) {
                                        Log.v("test", "--->请求发生未知错误");
                                        return Observable.error(new ApiException("0001", "response == null"));
                                    }
                                    String res = response.string();// 记得要关闭 ResponseBody
                                    if (res.contains("{") && res.contains("}")) {
                                        int startIndex = res.indexOf("{");
                                        int endtIndex = res.lastIndexOf("}") + 1;
                                        String subStr = res.substring(startIndex, endtIndex);
                                        // 字符转义!!!
                                        subStr = subStr.replace("&lt;", "<").replace("&gt;", ">");
                                        return returnString(subStr);
                                    } else if (res != null && !res.equals("")) {
                                        return returnString(res);
                                    }
                                } catch (Exception e) {
                                    Log.v("test", "--->IOException e: " + e.getMessage());
                                    e.printStackTrace();
                                    return Observable.error(new ApiException("0002", e.getMessage()));
                                }
                                return Observable.empty();
                            }
                        }
                );
            }
        };
    }


    /**
     * 从网络请求里面的那串截取出需要的json数据
     * @return
     */
    public static Observable.Transformer<ResponseBody, String> gsonResult(final String papapa) {
        return new Observable.Transformer<ResponseBody, String>() {
            @Override
            public Observable<String> call(Observable<ResponseBody> tObservable) {
                return tObservable.flatMap(
                        new Func1<ResponseBody, Observable<String>>() {
                            @Override
                            public Observable<String> call(ResponseBody response) {
                                try {
                                    if (response == null) {
                                        Log.v("test", "--->请求发生未知错误");
                                        return Observable.error(new ApiException("0002", "接口调用无返回值"));
                                    }
                                    String res = response.string();// 记得要关闭!!!  ResponseBody .string()会自动关闭  .string()和toString()完全不一样!!!
                                    if (res != null && !res.equals("")) {
                                        // 字符转义&lt;
                                        String subStr = res.replace("&lt;", "<").replace("&gt;", ">");
                                        // success date like this <GetSysDateTimeResult>string</GetSysDateTimeResult>
                                        String ostar = "<" + papapa + "Result>";
                                        String oend = "</" + papapa + "Result>";
                                        if (subStr.contains(ostar) && subStr.contains(oend)) {
                                            int startIndex = subStr.indexOf(ostar) + ostar.length();
                                            int endtIndex = subStr.lastIndexOf(oend);
                                            String ores = subStr.substring(startIndex, endtIndex);
                                            return returnString(ores);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.v("test", "--->IOException e: " + e.getMessage());
                                    e.printStackTrace();
                                    return Observable.error(new ApiException("0003", e.getMessage()));
                                }
//                                return Observable.empty();
                                return Observable.error(new ApiException("0001","无"+papapa+"接口信息,请检查调用的接口是否正确"));
                            }
                        }
                );
            }
        };
    }


    /**
     * 从ResponseBody取出需要的json数据
     * 返回值用拦截器拦截的时候用的到，因为返回的是ResponseBody
     * @return
     */
    public static Observable.Transformer<ResponseBody, String> gsonFromRes() {
        return new Observable.Transformer<ResponseBody, String>() {
            @Override
            public Observable<String> call(Observable<ResponseBody> tObservable) {
                return tObservable.flatMap(
                        new Func1<ResponseBody, Observable<String>>() {
                            @Override
                            public Observable<String> call(ResponseBody response) {
                                try {
                                    if (response == null) {
                                        Log.v("test", "--->请求发生未知错误");
                                        return Observable.error(new ApiException("0002", "接口调用无返回值"));
                                    }
                                    String res = response.string();// 记得要关闭!!!  ResponseBody .string()会自动关闭  .string()和toString()完全不一样!!!
                                    return returnString(res);
                                } catch (Exception e) {
                                    Log.v("test", "--->IOException e: " + e.getMessage());
                                    e.printStackTrace();
                                    return Observable.error(new ApiException("0003", e.getMessage()));
                                }
                            }
                        }
                );
            }
        };
    }
}
