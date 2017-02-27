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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HUPENG on 2017/2/25.
 */
public class EcardQuery {
    /**
     * 账户流水集合
     * */
    private List<AccountBill>accountBills = null;

    /**
     * 查询回调接口
     * */
    private AccountBillListener accountBillListener;

    /**
     * OkHttp库，主要用来执行网络请求
     * */
    private OkHttpClient client = new OkHttpClient.Builder()
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
     * 执行登录操作,登录操作成功以后进行获取账户名称的回调
     * @param username      登录名
     * @param password      密码
     * */
    private void login(String username, String password){
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
//                Log.i(EcardQuery.class.toString(),"http request fail");
                accountBillListener.done(null,e);
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body =  response.body().string();
//                getLog("40637");
                getAccount();
            }
        });
    }


    /**
     * 得到账户名,然后进行回调
     * */
    private void getAccount(){
        /**
         * 用 Request.Builder 构造一个request对象
         * */
        Request.Builder requestBuilder = new Request.Builder().url("http://ecard.imu.edu.cn/accounttodayTrjn.action");
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();

        /**
         * 执行请求操作
         * */
        Call mCall= client.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                accountBillListener.done(null,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Document document= Jsoup.parse(response.body().string());
                Element element = document.getElementById("account");
                String account = getNumbers(element.select("option").text());
                getLog(account);
            }
        });
    }

    /**
     * 得到字符串中的数字
     * */
    private String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }


    /**
     * 得到当天的消费记录
     * */
    private void getLog(String account ) {

        /**
         * 构造一个表单对象
         * */
        RequestBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("inputObject", "all")
                .add("Submit", "+%C8%B7+%B6%A8+")
                .build();

        /**
         * 构造一个 Request对象
         * */
        Request.Builder requestBuilder = new Request.Builder().url("http://ecard.imu.edu.cn/accounttodatTrjnObject.action");
        requestBuilder.method("POST",formBody);
        Request request = requestBuilder.build();

        /**
         * 执行一个请求操作
         * */
        Call mCall= client.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                accountBillListener.done(null,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document document= Jsoup.parse(response.body().string());
                Element tables = document.getElementById("tables");
                Elements tr=tables.select("tr");
                for (Element element : tr) {
                    String text = element.text();
                    /**
                     * 跳过开始以及结束的非需要的数据
                     * */
                    if (text.contains("交易发生时间") || text.contains("总交易额为")){
                        continue;
                    }
                    String[] tmp = text.split(" ");
                    AccountBill accountBill = new AccountBill();
                    accountBill.time = tmp[0] + " " + tmp[1];
                    accountBill.type = tmp[4];
                    accountBill.shop = tmp[5];
                    accountBill.turnover = tmp[6];
                    accountBill.balance = tmp[7];
                    accountBill.no = Integer.parseInt(tmp[8]);
                    if (accountBills == null){
                        accountBills = new LinkedList<>();
                    }
                    accountBills.add(accountBill);
//                    System.out.println("*****************************************");

                }
                accountBillListener.done(accountBills,null);
            }
        });
    }

    /**
     * 构造内部类
     * */
    public static class AccountBill{
        /**
         * 次数
         * */
        public int no;

        /**
         * 交易时间
         * */
        public String time;

        /**
         * 交易类型
         * */
        public String type;

        /**
         * 商户名称
         * */
        public String shop;

        /**
         * 交易额
         * */
        public String turnover;

        /**
         * 余额
         * */
        public String balance;

    }

    public void getAccountBiils(String username,String password, AccountBillListener accountBillListener){
        this.accountBillListener = accountBillListener;
        login(username, password);
    }
}
