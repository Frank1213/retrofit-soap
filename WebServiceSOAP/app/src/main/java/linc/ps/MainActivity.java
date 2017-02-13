package linc.ps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import linc.ps.net_common.ApiNode;
import linc.ps.net_common.ApiSchedulersHelper;
import linc.ps.net_retrofit_one.AppClient;
import linc.ps.net_common.RxSubscriber;
import linc.ps.net_retrofit_two.AppSoapClient;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_cityname;
    private Button btn_getdate;
    private TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        et_cityname = (EditText) findViewById(R.id.activity_main_et_cityname);
        btn_getdate = (Button) findViewById(R.id.activity_main_btn_getdate);
        et_cityname.setText("福建");
        tv_date = (TextView) findViewById(R.id.activity_main_tv_date);
        btn_getdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_btn_getdate:
                getSupportCityByFirst();
                getSupportCityBySecond();
                getSupportCityByThrid();
                break;
        }
    }

    /**
     * 通过省份获取城市代码,无封装
     */
    public void getSupportCityByFirst() {
        // 拼接入参
        Map map = new HashMap<>();
        map.put("byProvinceName", "福建");
        String result = ApiNode.getParameter("getSupportCity", map);
        // 调用
        AppClient.getInstance().getSupportCity(result)
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.e("test", "---getSupportCityByFirst onCompleted--->");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("test", "---getSupportCityByFirst onError--->");
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            String res = response.string();
                            Log.e("test", "---getSupportCityByFirst onNext str--->"+res);
                        } catch (IOException e) {
                            Log.e("test", "---getSupportCityByFirst onNext str-IOException-->"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 通过省份获取城市代码,截取数据
     */
    public void getSupportCityBySecond() {
        Map map = new HashMap<>();
        map.put("byProvinceName", "福建");
        String result = ApiNode.getParameter("getSupportCity", map);

        AppClient.getInstance().getSupportCity(result)
                .compose(ApiSchedulersHelper.gsonResult("getSupportCity")) // 获取从数据源获取json
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new RxSubscriber<String>() {
                    @Override
                    public void _onNext(String string) {
                        Log.e("test", "---getSupportCityBySecond _onNext str--->"+string);
                        // <string>福州 (58847)</string><string>厦门 (59134)</string><string>龙岩 (58927)</string><string>南平 (58834)</string><string>宁德 (58846)</string><string>莆田 (58946)</string><string>泉州 (59137)</string><string>三明 (58828)</string><string>漳州 (59126)</string>
                        tv_date.setText(string);
                    }

                    @Override
                    public void _onError(String msg) {
                        Log.d("test", "--->getSupportCityBySecond msg:" + msg);
                        tv_date.setText(msg);
                    }
                });
    }

    /**
     * 通过省份获取城市代码,截取数据,中文乱码de解决的方案-->入参赋值之前手动转换一次
     */
    public void getSupportCityByThrid() {
        AppSoapClient.getInstance().getSupportCity(et_cityname.getText().toString())
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.e("test", "---getSupportCityBySecond onCompleted--->");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("test", "---getSupportCityBySecond onError--->"+e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        Log.e("test", "---getSupportCityBySecond onNext--->");
                        try {
                            String res = response.string();
                            Log.e("test", "---getSupportCityBySecond onNext str--->"+res);
                            tv_date.setText(res);
                        } catch (IOException e) {
                            Log.e("test", "---getSupportCityBySecond onNext str-IOException-->"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }


}
