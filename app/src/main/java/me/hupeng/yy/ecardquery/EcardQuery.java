package me.hupeng.yy.ecardquery;


import android.util.Log;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HUPENG on 2017/2/25.
 */
public class EcardQuery {
    public OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    /**
     * 执行登录操作,登录操作成功以后进行回调
     * @param username      登录名
     * @param password      密码
     * */
    public void login(String username, String password){
        RequestBody formBody = new FormBody.Builder()
                .add("name", username)
                .add("passwd", password)
                .add("loginType", "2")
                .add("rand","6775")
                .add("imageField.x","40")
                .add("imageField.y","9")
                .add("userType","1")
                .build();
        final Request request = new Request.Builder()
                .url("http://ecard.imu.edu.cn/loginstudent.action")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(EcardQuery.class.toString(),"http request fail");
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body =  response.body().string();
                getLog("40637");
            }
        });
    }


    /**
     * 得到账户名,然后进行回调
     * */
    private void getAccount(){
        Request.Builder requestBuilder = new Request.Builder().url("http://ecard.imu.edu.cn/accounttodayTrjn.action");
        requestBuilder.method("GET", null);

    }

    public void getLog(String account ) {


        RequestBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("inputObject", "15")
                .add("Submit", "+%C8%B7+%B6%A8+")
                .build();

        Request.Builder requestBuilder = new Request.Builder().url("http://ecard.imu.edu.cn/accounttodatTrjnObject.action");
        requestBuilder.method("POST",formBody);

        Request request = requestBuilder.build();
        Call mCall= client.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body =  response.body().string();
                Log.i("body/*****", response.body().toString());
                Document document= Jsoup.parse(response.body().string());
                Elements tr=document.select("tr.odd");
                for (Element element : tr) {

                }

            }
        });
    }

    public void getInfo() {
//        OkHttpClient mOkHttpClient = new OkHttpClient();

    }
}
