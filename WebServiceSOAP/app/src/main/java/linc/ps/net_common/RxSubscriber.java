package linc.ps.net_common;

import rx.Subscriber;

/**
 * Created by Frank on 2016/12/9.
 * 封装Subscriber
 */

public abstract class RxSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

//        if (!NetUtil.checkNet(MyApplication.getInstance())) {
//            _onError("网络不可用!");
//            return;
//        }

        if (e instanceof ApiException) {
            _onError(e.getMessage());
        } else if(true){
            // 更多异常处理
        }else{
            _onError("请求失败，请稍后重试...");
        }
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    public abstract void _onNext(T t);

    public abstract void _onError(String msg);
}
